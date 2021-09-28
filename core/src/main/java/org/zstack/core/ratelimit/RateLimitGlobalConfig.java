package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDef;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigValidation(validValues = {"true", "false"})
    @GlobalConfigDef(type = Boolean.class, defaultValue = "true", description = "rate limit switch")
    public static GlobalConfig RATE_LIMIT_SWITCH = new GlobalConfig(CATEGORY, "rateLimitSwitch");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "600", description = "The number of requests per second allowed for the operation of querying resource information")
    public static GlobalConfig API_SYNC_CALL_MSG_QPS = new GlobalConfig(CATEGORY, "apiSyncCallMsgQPS");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "300", description = "The number of requests per second allowed for creating, modifying, and deleting resource operations")
    public static GlobalConfig API_ASYNC_CALL_MSG_QPS = new GlobalConfig(CATEGORY, "apiAsyncCallMsgQPS");
}
