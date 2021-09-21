package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDef;
import org.zstack.core.config.GlobalConfigDefinition;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigDef(type = Double.class, defaultValue = "50.0", description = "API synchronous message call capacity")
    public static GlobalConfig API_SYNC_CALL_MSG_Total = new GlobalConfig(CATEGORY, "apiSyncCallMsgTotal");

    @GlobalConfigDef(type = Double.class, defaultValue = "0.03", description = "The rate of API synchronous message calls, in milliseconds")
    public static GlobalConfig API_SYNC_CALL_MSG_Rate = new GlobalConfig(CATEGORY, "apiSyncCallMsgRate");

    @GlobalConfigDef(type = Double.class, defaultValue = "30.0", description = "API asynchronous message call capacity")
    public static GlobalConfig API_ASYNC_CALL_MSG_Total = new GlobalConfig(CATEGORY, "apiAsyncCallMsgTotal");

    @GlobalConfigDef(type = Double.class, defaultValue = "0.01", description = "The rate of API asynchronous message calls, in milliseconds")
    public static GlobalConfig API_ASYNC_CALL_MSG_Rate = new GlobalConfig(CATEGORY, "apiAsyncCallMsgRate");
}
