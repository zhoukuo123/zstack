package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DESTROY_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DESTROY_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_STOP_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStopVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_STOP_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStopVmInstanceMsgAvgFlowRate");

//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbMaxFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");
//
//    @GlobalConfigValidation(numberGreaterThan = 0)
//    public static GlobalConfig apiCreateVmInstanceFromVolumeMsgTbAvgFlowRate = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");


}
