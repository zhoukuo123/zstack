package org.zstack.core.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.SQL;
import org.zstack.header.Component;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.logging.CLoggerImpl;

/**
 * @author CoderZk
 */
public class TokenBucketFacadeImpl implements TokenBucketFacade, Component {
    private static final CLogger logger = CLoggerImpl.getLogger(TokenBucketFacadeImpl.class);

    @Autowired
    private DatabaseFacade dbf;

    @Override
    public boolean getToken(APIMessage msg) {
        String apiName = msg.getClass().getSimpleName();
        TokenBucket tokenBucket = SQL.New("select tb from TokenBucket tb where apiName = :apiName", TokenBucket.class).param("apiName", apiName).find();
        if (tokenBucket == null) {
            if (msg instanceof APISyncCallMessage) {
                tokenBucket = new TokenBucket(apiName, RateLimitGlobalConfig.API_SYNC_CALL_MSG_Total.value(Double.class), RateLimitGlobalConfig.API_SYNC_CALL_MSG_Rate.value(Double.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_SYNC_CALL_MSG_Total.value(Double.class));
            } else {
                tokenBucket = new TokenBucket(apiName, RateLimitGlobalConfig.API_ASYNC_CALL_MSG_Total.value(Double.class), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_Rate.value(Double.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_Total.value(Double.class));
            }
            dbf.persist(tokenBucket);
        }

        long now = System.currentTimeMillis();
        tokenBucket.setNowSize(Math.min(tokenBucket.getTotal(), tokenBucket.getNowSize() + (now - tokenBucket.getTime()) * tokenBucket.getRate()));
        tokenBucket.setTime(now);
        dbf.update(tokenBucket);

        int result = SQL.New("update TokenBucket tb set tb.nowSize = tb.nowSize - 1 where tb.apiName = :apiName and tb.nowSize >= 1").param("apiName", apiName).execute();

        if (result != 1) {
            return false;
        }
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
