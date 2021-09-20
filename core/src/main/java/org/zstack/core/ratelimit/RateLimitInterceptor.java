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
        boolean acquired = tbf.getToken(msg);
        if (!acquired) {
            throw new RuntimeException("Your access is blocked");
        }

        logger.info(String.format("This API [api: %s] passed", msg.getClass().getSimpleName()));

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
