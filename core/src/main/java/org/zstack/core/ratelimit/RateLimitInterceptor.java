package org.zstack.core.ratelimit;

import org.springframework.beans.factory.annotation.Autowired;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

public class RateLimitInterceptor implements ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(RateLimitInterceptor.class);

    @Autowired
    private TokenBucketFacade tbf;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {

        boolean acquired = tbf.getToken(msg.getClass().getSimpleName());
        if (!acquired) {
            throw new RuntimeException("Your access is blocked");
        }

        logger.debug(String.format("This API [api: %s] passed", msg.getClass().getSimpleName()));

        return msg;
    }
}
