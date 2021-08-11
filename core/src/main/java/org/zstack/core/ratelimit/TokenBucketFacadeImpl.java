package org.zstack.core.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQL;
import org.zstack.header.Component;
import org.zstack.header.identity.Action;
import org.zstack.utils.BeanUtils;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.logging.CLoggerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author CoderZk
 */
public class TokenBucketFacadeImpl implements TokenBucketFacade, Component {
    private static final CLogger logger = CLoggerImpl.getLogger(TokenBucketFacadeImpl.class);

    private Map<Class, TokenBucket> tokenBucketMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock(true);

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public boolean start() {
        buildTokenBucket();
        return true;
    }

    private void buildTokenBucket() {
        BeanUtils.reflections.getTypesAnnotatedWith(Action.class).forEach(clz-> {
            TokenBucket tokenBucket = tokenBucketMap.put(clz, new TokenBucket(clz));
            tokenBucketStart(tokenBucket);
        });
    }

    private void tokenBucketStart(TokenBucket tokenBucket) {
        if (tokenBucket.getMaxFlowRate() != 0) {
            APIRateLimitVO apirl = new APIRateLimitVO();
            apirl.setApi(tokenBucket.getApiName());
            apirl.setToken(tokenBucket.getMaxFlowRate());
            dbf.persist(apirl);
        }

        TokenBucket.TokenProducer tokenProducer = tokenBucket.new TokenProducer(tokenBucket.getAvgFlowRate(), tokenBucket);
        tokenBucket.getScheduledExecutorService().scheduleAtFixedRate(tokenProducer, 0, 1, TimeUnit.SECONDS);
    }

    public static void addTokens(Integer tokenNum, TokenBucket tokenBucket) {
        for (int i = 0; i < tokenNum; i++) {
            Integer token = SQL.New("select token from APIRateLimitVO where api = :apiName", Integer.class).param("apiName", tokenBucket.getApiName()).find();
            if (token < tokenBucket.getMaxFlowRate()) {
                SQL.New("update APIRateLimitVO apirl set apirl.token = apirl.token + 1 where apirl.api = :apiName").param("apiName", tokenBucket.getApiName()).execute();
            } else {
                break;
            }
        }
    }

    @Override
    public boolean getToken(Class clz) {
        TokenBucket tokenBucket = tokenBucketMap.get(clz);

        if (tokenBucket == null) {
            return false;
        }

        lock.lock();
        try {
            Integer token = SQL.New("select token from APIRateLimitVO where api = :apiName", Integer.class).param("apiName", tokenBucket.getApiName()).find();

            if (token == 0) {
                return false;
            }

            SQL.New("update APIRateLimitVO apirl set apirl.token = apirl.token - 1 where apirl.api = :apiName").param("apiName", tokenBucket.getApiName()).execute();

            return true;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean stop() {
        return true;
    }
}
