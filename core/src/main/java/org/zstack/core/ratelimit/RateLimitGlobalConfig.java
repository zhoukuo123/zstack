package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDef;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "50", description = "API sync call msg max flow rate")
    public static GlobalConfig API_SYNC_CALL_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSyncCallMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "30", description = "API sync call msg avg flow rate")
    public static GlobalConfig API_SYNC_CALL_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSyncCallMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "30", description = "API async call msg max flow rate")
    public static GlobalConfig API_ASYNC_CALL_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAsyncCallMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    @GlobalConfigDef(type = Integer.class, defaultValue = "10", description = "API async call msg avg flow rate")
    public static GlobalConfig API_ASYNC_CALL_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAsyncCallMsgAvgFlowRate");
}
