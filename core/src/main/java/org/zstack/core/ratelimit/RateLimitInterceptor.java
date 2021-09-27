package org.zstack.core.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.GlobalApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.List;

public class RateLimitInterceptor implements GlobalApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(RateLimitInterceptor.class);

    @Autowired
    private TokenBucketFacade tbf;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        Boolean isRateLimit = RateLimitGlobalConfig.RATE_LIMIT_SWITCH.value(Boolean.class);
        if (isRateLimit) {
            boolean acquired = tbf.getToken(msg);
            if (!acquired) {
                throw new RuntimeException("Requests exceeded rate limit");
            }
        }
        return msg;
    }

    @Override
    public List<Class> getMessageClassToIntercept() {
        return null;
    }

    @Override
    public InterceptorPosition getPosition() {
        return InterceptorPosition.FRONT;
    }
}
