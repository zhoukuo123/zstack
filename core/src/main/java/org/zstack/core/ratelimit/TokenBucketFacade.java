package org.zstack.core.ratelimit;

import org.zstack.header.message.APIMessage;

/**
 * @author CoderZk
 */
public interface TokenBucketFacade {
    boolean getToken(APIMessage msg);
}
