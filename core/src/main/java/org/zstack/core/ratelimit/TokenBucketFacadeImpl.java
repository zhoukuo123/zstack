package org.zstack.core.ratelimit;

import org.zstack.core.db.GLock;
import org.zstack.header.Component;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.logging.CLoggerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CoderZk
 */
public class TokenBucketFacadeImpl implements TokenBucketFacade, Component {
    private static final CLogger logger = CLoggerImpl.getLogger(TokenBucketFacadeImpl.class);

    private Map<String, TokenBucketVO> tokenBucketMap = new ConcurrentHashMap<>();

    @Override
    public boolean getToken(APIMessage msg) {
        String apiName = msg.getClass().getSimpleName();
        TokenBucketVO tokenBucketVO = tokenBucketMap.get(apiName);
        if (tokenBucketVO == null) {
            if (msg instanceof APISyncCallMessage) {
                tokenBucketVO = new TokenBucketVO(apiName, RateLimitGlobalConfig.API_SYNC_CALL_MSG_TOTAL.value(Integer.class), RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_SYNC_CALL_MSG_TOTAL.value(Integer.class) * 1.0);
            } else {
                tokenBucketVO = new TokenBucketVO(apiName, RateLimitGlobalConfig.API_ASYNC_CALL_MSG_TOTAL.value(Integer.class), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_TOTAL.value(Integer.class) * 1.0);
            }
            tokenBucketMap.put(apiName, tokenBucketVO);
        }

        if (msg instanceof APISyncCallMessage) {
            if (!tokenBucketVO.getTotal().equals(RateLimitGlobalConfig.API_SYNC_CALL_MSG_TOTAL.value(Integer.class))) {
                tokenBucketVO.setTotal(RateLimitGlobalConfig.API_SYNC_CALL_MSG_TOTAL.value(Integer.class));
            }
            if (!tokenBucketVO.getRate().equals(RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class))) {
                tokenBucketVO.setRate(RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class));
            }
        } else {
            if (!tokenBucketVO.getTotal().equals(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_TOTAL.value(Integer.class))) {
                tokenBucketVO.setTotal(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_TOTAL.value(Integer.class));
            }
            if (!tokenBucketVO.getRate().equals(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class))) {
                tokenBucketVO.setRate(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class));
            }
        }

        long now = System.currentTimeMillis();
        tokenBucketVO.setNowSize(Math.min(tokenBucketVO.getTotal(), tokenBucketVO.getNowSize() + (now - tokenBucketVO.getTime()) * tokenBucketVO.getRate() / 1000.0));
        tokenBucketVO.setTime(now);

        if (tokenBucketVO.getNowSize() < 1) {
            return false;
        } else {
            tokenBucketVO.setNowSize(tokenBucketVO.getNowSize() - 1);
            return true;
        }
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
