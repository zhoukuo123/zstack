package org.zstack.core.ratelimit;

import org.zstack.header.Component;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.APISyncCallMessage;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.logging.CLoggerImpl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucketFacadeImpl implements TokenBucketFacade, Component {
    private static final CLogger logger = CLoggerImpl.getLogger(TokenBucketFacadeImpl.class);

    private Map<String, TokenBucketVO> tokenBucketMap = new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();

    @Override
    public boolean getToken(APIMessage msg) {
        String apiName = msg.getClass().getSimpleName();
        TokenBucketVO tokenBucketVO = tokenBucketMap.get(apiName);
        if (tokenBucketVO == null) {
            if (msg instanceof APISyncCallMessage) {
                tokenBucketVO = new TokenBucketVO(apiName, RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class) * 1.0);
            } else {
                tokenBucketVO = new TokenBucketVO(apiName, RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class), System.currentTimeMillis(), RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class) * 1.0);
            }
            tokenBucketMap.put(apiName, tokenBucketVO);
        }

        if (msg instanceof APISyncCallMessage) {
            if (!tokenBucketVO.getRate().equals(RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class))) {
                tokenBucketVO.setRate(RateLimitGlobalConfig.API_SYNC_CALL_MSG_QPS.value(Integer.class));
            }
        } else {
            if (!tokenBucketVO.getRate().equals(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class))) {
                tokenBucketVO.setRate(RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class));
            }
        }

        lock.lock();
        try {
            long now = System.currentTimeMillis();
            tokenBucketVO.setNowSize(Math.min(tokenBucketVO.getRate(), tokenBucketVO.getNowSize() + (now - tokenBucketVO.getTime()) * tokenBucketVO.getRate() / 1000.0));
            tokenBucketVO.setTime(now);

            if (tokenBucketVO.getNowSize() < 1) {
                return false;
            } else {
                tokenBucketVO.setNowSize(tokenBucketVO.getNowSize() - 1);
                return true;
            }
        } finally {
            lock.unlock();
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
