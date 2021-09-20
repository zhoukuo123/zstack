package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDef;
import org.zstack.core.config.GlobalConfigDefinition;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigDef(type = Double.class, defaultValue = "50.0", description = "API sync call msg total")
    public static GlobalConfig API_SYNC_CALL_MSG_Total = new GlobalConfig(CATEGORY, "apiSyncCallMsgTotal");

    @GlobalConfigDef(type = Double.class, defaultValue = "30.0", description = "API sync call msg rate")
    public static GlobalConfig API_SYNC_CALL_MSG_Rate = new GlobalConfig(CATEGORY, "apiSyncCallMsgRate");

    @GlobalConfigDef(type = Double.class, defaultValue = "30.0", description = "API async call msg total")
    public static GlobalConfig API_ASYNC_CALL_MSG_Total = new GlobalConfig(CATEGORY, "apiAsyncCallMsgTotal");

    @GlobalConfigDef(type = Double.class, defaultValue = "10.0", description = "API async call msg rate")
    public static GlobalConfig API_ASYNC_CALL_MSG_Rate = new GlobalConfig(CATEGORY, "apiAsyncCallMsgRate");
}
