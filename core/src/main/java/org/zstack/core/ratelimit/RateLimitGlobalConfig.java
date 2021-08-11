package org.zstack.core.ratelimit;

import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigDefinition;
import org.zstack.core.config.GlobalConfigValidation;

@GlobalConfigDefinition
public class RateLimitGlobalConfig {
    public static final String CATEGORY = "ratelimit";

//    vmInstance api
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_GROUP_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotGroupMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_GROUP_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmInstanceFromVolumeSnapshotGroupMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_STOP_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStopVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_STOP_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStopVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_REBOOT_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRebootVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_REBOOT_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRebootVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DESTROY_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDestroyVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DESTROY_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDestroyVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_START_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStartVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_START_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiStartVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_MIGRATE_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiMigrateVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_MIGRATE_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiMigrateVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_NIC_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmNicMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_NIC_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmNicMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_PRIORITY_CONFIG_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmPriorityConfigMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_PRIORITY_CONFIG_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmPriorityConfigMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_L3_NETWORK_TO_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachL3NetworkToVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_L3_NETWORK_TO_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachL3NetworkToVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_VM_NIC_NETWORK_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeVmNicNetworkMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_VM_NIC_NETWORK_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeVmNicNetworkMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_L3_NETWORKS_FOR_CHANGE_VM_NIC_NETWORK_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateL3NetworksForChangeVmNicNetworkMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_L3_NETWORKS_FOR_CHANGE_VM_NIC_NETWORK_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateL3NetworksForChangeVmNicNetworkMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_NIC_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmNicMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_NIC_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmNicMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_VM_NIC_TO_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachVmNicToVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_VM_NIC_TO_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachVmNicToVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_NIC_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmNicMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_NIC_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmNicMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_NIC_ATTACHED_NETWORK_SERVICE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmNicAttachedNetworkServiceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_NIC_ATTACHED_NETWORK_SERVICE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmNicAttachedNetworkServiceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_MIGRATION_CANDIDATE_HOSTS_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmMigrationCandidateHostsMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_MIGRATION_CANDIDATE_HOSTS_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmMigrationCandidateHostsMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_ATTACHABLE_DATA_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmAttachableDataVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_ATTACHABLE_DATA_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmAttachableDataVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_INSTANCE_OFFERING_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeInstanceOfferingMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_INSTANCE_OFFERING_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeInstanceOfferingMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_L3_NETWORK_FROM_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachL3NetworkFromVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_L3_NETWORK_FROM_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachL3NetworkFromVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_ATTACHABLE_L3_NETWORK_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmAttachableL3NetworkMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_ATTACHABLE_L3_NETWORK_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmAttachableL3NetworkMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_ISO_TO_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachIsoToVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_ISO_TO_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachIsoToVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_ISO_FROM_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachIsoFromVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_ISO_FROM_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachIsoFromVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RECOVER_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRecoverVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RECOVER_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRecoverVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_EXPUNGE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiExpungeVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_EXPUNGE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiExpungeVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_ORDER_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootOrderMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_ORDER_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootOrderMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_CLOCK_TRACK_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmClockTrackMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_CLOCK_TRACK_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmClockTrackMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_BOOT_ORDER_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmBootOrderMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_BOOT_ORDER_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmBootOrderMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmConsolePasswordMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmConsolePasswordMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmConsolePasswordMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmConsolePasswordMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmConsolePasswordMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmConsolePasswordMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CONSOLE_ADDRESS_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmConsoleAddressMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CONSOLE_ADDRESS_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmConsoleAddressMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_HOSTNAME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmHostnameMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_HOSTNAME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmHostnameMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_HOSTNAME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmHostnameMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_HOSTNAME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmHostnameMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_MODE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootModeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_BOOT_MODE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmBootModeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_BOOT_MODE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmBootModeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_BOOT_MODE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmBootModeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_STATIC_IP_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmStaticIpMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_STATIC_IP_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmStaticIpMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_STATIC_IP_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmStaticIpMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_STATIC_IP_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmStaticIpMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_HOSTNAME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmHostnameMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_HOSTNAME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmHostnameMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_STARTING_CANDIDATE_CLUSTERS_HOSTS_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmStartingCandidateClustersHostsMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_STARTING_CANDIDATE_CLUSTERS_HOSTS_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmStartingCandidateClustersHostsMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CAPABILITIES_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmCapabilitiesMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_CAPABILITIES_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmCapabilitiesMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_SSH_KEY_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmSshKeyMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_SSH_KEY_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmSshKeyMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_SSH_KEY_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmSshKeyMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_SSH_KEY_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmSshKeyMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_SSH_KEY_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmSshKeyMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_SSH_KEY_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmSshKeyMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_ZONES_CLUSTERS_HOSTS_FOR_CREATING_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateZonesClustersHostsForCreatingVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_ZONES_CLUSTERS_HOSTS_FOR_CREATING_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateZonesClustersHostsForCreatingVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_PRIMARY_STORAGES_FOR_CREATING_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidatePrimaryStoragesForCreatingVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_PRIMARY_STORAGES_FOR_CREATING_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidatePrimaryStoragesForCreatingVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_INTERDEPENDENT_L3_NETWORKS_IMAGES_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetInterdependentL3NetworksImagesMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_INTERDEPENDENT_L3_NETWORKS_IMAGES_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetInterdependentL3NetworksImagesMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_VM_FOR_ATTACHING_ISO_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateVmForAttachingIsoMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_VM_FOR_ATTACHING_ISO_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateVmForAttachingIsoMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_ISO_FOR_ATTACHING_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateIsoForAttachingVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_CANDIDATE_ISO_FOR_ATTACHING_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetCandidateIsoForAttachingVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_PAUSE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiPauseVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_PAUSE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiPauseVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RESUME_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiResumeVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RESUME_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiResumeVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_REIMAGE_VM_INSTANCE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiReimageVmInstanceMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_REIMAGE_VM_INSTANCE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiReimageVmInstanceMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_L3_NETWORK_TO_VM_NIC_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachL3NetworkToVmNicMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_L3_NETWORK_TO_VM_NIC_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachL3NetworkToVmNicMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_CD_ROM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmCdRomMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VM_CD_ROM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVmCdRomMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_CD_ROM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmCdRomMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VM_CD_ROM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVmCdRomMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_CD_ROM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmCdRomMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_VM_CD_ROM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteVmCdRomMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_CD_ROM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmCdRomMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_CD_ROM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmCdRomMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_INSTANCE_DEFAULT_CD_ROM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmInstanceDefaultCdRomMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_INSTANCE_DEFAULT_CD_ROM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmInstanceDefaultCdRomMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_PRIORITY_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmPriorityMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_PRIORITY_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmPriorityMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_PRIORITY_CONFIG_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdatePriorityConfigMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_PRIORITY_CONFIG_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdatePriorityConfigMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_NIC_DRIVER_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmNicDriverMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VM_NIC_DRIVER_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVmNicDriverMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_SOUND_TYPE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmSoundTypeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_SOUND_TYPE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmSoundTypeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_QXL_MEMORY_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmQxlMemoryMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SET_VM_QXL_MEMORY_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSetVmQxlMemoryMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_SPICE_CERTIFICATES_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetSpiceCertificatesMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_SPICE_CERTIFICATES_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetSpiceCertificatesMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_DEVICE_ADDRESS_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmDeviceAddressMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VM_DEVICE_ADDRESS_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmDeviceAddressMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VMS_CAPABILITIES_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmsCapabilitiesMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VMS_CAPABILITIES_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVmsCapabilitiesMsgAvgFlowRate");

//    volume api
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_DATA_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteDataVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DELETE_DATA_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDeleteDataVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_QUERY_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiQueryVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_VOLUME_STATE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeVolumeStateMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CHANGE_VOLUME_STATE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiChangeVolumeStateMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VOLUME_SNAPSHOT_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVolumeSnapshotMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VOLUME_SNAPSHOT_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVolumeSnapshotMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VOLUME_SNAPSHOT_GROUP_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVolumeSnapshotGroupMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_VOLUME_SNAPSHOT_GROUP_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateVolumeSnapshotGroupMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_FROM_VOLUME_SNAPSHOT_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeFromVolumeSnapshotMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_FROM_VOLUME_SNAPSHOT_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeFromVolumeSnapshotMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_DATA_VOLUME_TO_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachDataVolumeToVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_ATTACH_DATA_VOLUME_TO_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiAttachDataVolumeToVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_DATA_VOLUME_FROM_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachDataVolumeFromVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_DETACH_DATA_VOLUME_FROM_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiDetachDataVolumeFromVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_DATA_VOLUME_ATTACHABLE_VM_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetDataVolumeAttachableVmMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_DATA_VOLUME_ATTACHABLE_VM_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetDataVolumeAttachableVmMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_FROM_VOLUME_TEMPLATE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeFromVolumeTemplateMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_CREATE_DATA_VOLUME_FROM_VOLUME_TEMPLATE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiCreateDataVolumeFromVolumeTemplateMsgAvgFlowRate");
    
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VOLUME_FORMAT_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVolumeFormatMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VOLUME_FORMAT_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVolumeFormatMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_UPDATE_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiUpdateVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RECOVER_DATA_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRecoverDataVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_RECOVER_DATA_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiRecoverDataVolumeMsgAvgFlowRate");
    
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_EXPUNGE_DATA_VOLUME_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiExpungeDataVolumeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_EXPUNGE_DATA_VOLUME_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiExpungeDataVolumeMsgAvgFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SYNC_VOLUME_SIZE_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSyncVolumeSizeMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_SYNC_VOLUME_SIZE_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiSyncVolumeSizeMsgAvgFlowRate");
    
    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VOLUME_CAPABILITIES_MSG_MAX_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVolumeCapabilitiesMsgMaxFlowRate");

    @GlobalConfigValidation(numberGreaterThan = 0)
    public static GlobalConfig API_GET_VOLUME_CAPABILITIES_MSG_AVG_FLOW_RATE = new GlobalConfig(CATEGORY, "apiGetVolumeCapabilitiesMsgAvgFlowRate");

//    volumeSnapshot api

}
