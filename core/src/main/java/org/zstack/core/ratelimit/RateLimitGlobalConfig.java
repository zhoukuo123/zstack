package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDef;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "50", description = "The maximum number of requests per second allowed for the operation of querying resource information ")
    public static GlobalConfig API_SYNC_CALL_MSG_TOTAL = new GlobalConfig(CATEGORY, "apiSyncCallMsgTokenBucketCapacity");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "30", description = "The average number of requests per second allowed for the operation of querying resource information")
    public static GlobalConfig API_SYNC_CALL_MSG_QPS = new GlobalConfig(CATEGORY, "apiSyncCallMsgQPS");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "30", description = "The maximum number of requests per second allowed for creating, modifying, and deleting resource operations")
    public static GlobalConfig API_ASYNC_CALL_MSG_TOTAL = new GlobalConfig(CATEGORY, "apiAsyncCallMsgTokenBucketCapacity");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "3", description = "The average number of requests per second allowed for creating, modifying, and deleting resource operations")
    public static GlobalConfig API_ASYNC_CALL_MSG_QPS = new GlobalConfig(CATEGORY, "apiAsyncCallMsgQPS");
}
