package org.zstack.core.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQL;
import org.zstack.header.Component;
import org.zstack.header.identity.Action;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.utils.BeanUtils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.logging.CLoggerImpl;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author CoderZk
 */
public class TokenBucketFacadeImpl implements TokenBucketFacade, Component {
    private static final CLogger logger = CLoggerImpl.getLogger(TokenBucketFacadeImpl.class);
    private ReentrantLock lock = new ReentrantLock(true);

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public boolean start() {
        buildTokenBucket();
        return true;
    }

    private void buildTokenBucket() {
        BeanUtils.reflections.getTypesAnnotatedWith(Action.class).forEach(clz -> {
            Object obj = null;
            try {
                obj = clz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
            TokenBucket tokenBucket;
            if (obj instanceof APISyncCallMessage) {
                tokenBucket = new TokenBucket(RateLimitGlobalConfig.API_SYNC_CALL_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SYNC_CALL_MSG_AVG_FLOW_RATE.value(Integer.class), clz.getSimpleName());
            } else {
                tokenBucket = new TokenBucket(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_AVG_FLOW_RATE.value(Integer.class), clz.getSimpleName());
            }

            tokenBucketStart(tokenBucket);
        });
    }

    private void tokenBucketStart(TokenBucket tokenBucket) {
        APIRateLimitVO apirl = new APIRateLimitVO();
        apirl.setApi(tokenBucket.getApiName());
        apirl.setToken(tokenBucket.getMaxFlowRate());
        dbf.persist(apirl);

        TokenBucket.TokenProducer tokenProducer = tokenBucket.new TokenProducer(tokenBucket.getAvgFlowRate(), tokenBucket);
        tokenBucket.getScheduledExecutorService().scheduleAtFixedRate(tokenProducer, 0, 1, TimeUnit.SECONDS);
    }

    public static void addTokens(Integer tokenNum, TokenBucket tokenBucket) {
        for (int i = 0; i < tokenNum; i++) {
            SQL.New("update APIRateLimitVO set token = token + 1 where api = :apiName and token < :maxFlowRate")
                    .param("apiName", tokenBucket.getApiName())
                    .param("maxFlowRate", tokenBucket.getMaxFlowRate()).execute();
        }
    }

    @Override
    public boolean getToken(String apiName) {
        int result = SQL.New("update APIRateLimitVO set token = token - 1 where api = :apiName and token > 0").param("apiName", apiName).execute();

        if (result != 1) {
            return false;
        }

        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
