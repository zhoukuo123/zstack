package org.zstack.core.ratelimit;

/**
 * @author CoderZk
 */
public interface TokenBucketFacade {
    boolean getToken(Class clz);
}
