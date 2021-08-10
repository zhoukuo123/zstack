package org.zstack.compute.vm;

import org.zstack.core.ratelimit.RateLimitGlobalConfig;
import org.zstack.core.ratelimit.TokenBucket;
import org.zstack.header.Component;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.HashMap;
import java.util.Map;

public class VmInstanceRateLimitInterceptor implements ApiMessageInterceptor, Component {
    private static final CLogger logger = Utils.getLogger(VmInstanceRateLimitInterceptor.class);

    private Map<String, TokenBucket> tokenBucketMap = new HashMap<>();

    @Override
    public boolean start() {
        tokenBucketMap.put("APICreateVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmInstanceMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmInstanceFromVolumeMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeSnapshotMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmInstanceFromVolumeSnapshotMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeSnapshotGroupMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_GROUP_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_INSTANCE_FROM_VOLUME_SNAPSHOT_GROUP_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmInstanceFromVolumeSnapshotGroupMsg").build());
        tokenBucketMap.put("APIStopVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_STOP_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_STOP_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIStopVmInstanceMsg").build());
        tokenBucketMap.put("APIRebootVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_REBOOT_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_REBOOT_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIRebootVmInstanceMsg").build());
        tokenBucketMap.put("APIDestroyVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_DESTROY_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DESTROY_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDestroyVmInstanceMsg").build());
        tokenBucketMap.put("APIStartVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_START_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_START_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIStartVmInstanceMsg").build());
        tokenBucketMap.put("APIMigrateVmMsg", new TokenBucket(RateLimitGlobalConfig.API_MIGRATE_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_MIGRATE_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIMigrateVmMsg").build());
        tokenBucketMap.put("APIQueryVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_QUERY_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_QUERY_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIQueryVmInstanceMsg").build());
        tokenBucketMap.put("APIQueryVmNicMsg", new TokenBucket(RateLimitGlobalConfig.API_QUERY_VM_NIC_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_QUERY_VM_NIC_MSG_AVG_FLOW_RATE.value(Integer.class), "APIQueryVmNicMsg").build());
        tokenBucketMap.put("APIQueryVmPriorityConfigMsg", new TokenBucket(RateLimitGlobalConfig.API_QUERY_VM_PRIORITY_CONFIG_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_QUERY_VM_PRIORITY_CONFIG_MSG_AVG_FLOW_RATE.value(Integer.class), "APIQueryVmPriorityConfigMsg").build());
        tokenBucketMap.put("APIAttachL3NetworkToVmMsg", new TokenBucket(RateLimitGlobalConfig.API_ATTACH_L3_NETWORK_TO_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_ATTACH_L3_NETWORK_TO_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIAttachL3NetworkToVmMsg").build());
        tokenBucketMap.put("APIChangeVmNicNetworkMsg", new TokenBucket(RateLimitGlobalConfig.API_CHANGE_VM_NIC_NETWORK_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CHANGE_VM_NIC_NETWORK_MSG_AVG_FLOW_RATE.value(Integer.class), "APIChangeVmNicNetworkMsg").build());
        tokenBucketMap.put("APIGetCandidateL3NetworksForChangeVmNicNetworkMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_CANDIDATE_L3_NETWORKS_FOR_CHANGE_VM_NIC_NETWORK_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_CANDIDATE_L3_NETWORKS_FOR_CHANGE_VM_NIC_NETWORK_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetCandidateL3NetworksForChangeVmNicNetworkMsg").build());
        tokenBucketMap.put("APICreateVmNicMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_NIC_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_NIC_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmNicMsg").build());
        tokenBucketMap.put("APIAttachVmNicToVmMsg", new TokenBucket(RateLimitGlobalConfig.API_ATTACH_VM_NIC_TO_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_ATTACH_VM_NIC_TO_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIAttachVmNicToVmMsg").build());
        tokenBucketMap.put("APIDeleteVmNicMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_NIC_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_NIC_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmNicMsg").build());
        tokenBucketMap.put("APIGetVmNicAttachedNetworkServiceMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_NIC_ATTACHED_NETWORK_SERVICE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_NIC_ATTACHED_NETWORK_SERVICE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmNicAttachedNetworkServiceMsg").build());
        tokenBucketMap.put("APIGetVmMigrationCandidateHostsMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_MIGRATION_CANDIDATE_HOSTS_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_MIGRATION_CANDIDATE_HOSTS_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmMigrationCandidateHostsMsg").build());
        tokenBucketMap.put("APIGetVmAttachableDataVolumeMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_ATTACHABLE_DATA_VOLUME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_ATTACHABLE_DATA_VOLUME_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmAttachableDataVolumeMsg").build());
        tokenBucketMap.put("APIUpdateVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_UPDATE_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_UPDATE_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIUpdateVmInstanceMsg").build());
        tokenBucketMap.put("APIChangeInstanceOfferingMsg", new TokenBucket(RateLimitGlobalConfig.API_CHANGE_INSTANCE_OFFERING_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CHANGE_INSTANCE_OFFERING_MSG_AVG_FLOW_RATE.value(Integer.class), "APIChangeInstanceOfferingMsg").build());
        tokenBucketMap.put("APIDetachL3NetworkFromVmMsg", new TokenBucket(RateLimitGlobalConfig.API_DETACH_L3_NETWORK_FROM_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DETACH_L3_NETWORK_FROM_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDetachL3NetworkFromVmMsg").build());
        tokenBucketMap.put("APIGetVmAttachableL3NetworkMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_ATTACHABLE_L3_NETWORK_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_ATTACHABLE_L3_NETWORK_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmAttachableL3NetworkMsg").build());
        tokenBucketMap.put("APIAttachIsoToVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_ATTACH_ISO_TO_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_ATTACH_ISO_TO_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIAttachIsoToVmInstanceMsg").build());
        tokenBucketMap.put("APIDetachIsoFromVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_DETACH_ISO_FROM_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DETACH_ISO_FROM_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDetachIsoFromVmInstanceMsg").build());
        tokenBucketMap.put("APIRecoverVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_RECOVER_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_RECOVER_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIRecoverVmInstanceMsg").build());
        tokenBucketMap.put("APIExpungeVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_EXPUNGE_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_EXPUNGE_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIExpungeVmInstanceMsg").build());
        tokenBucketMap.put("APISetVmBootOrderMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_BOOT_ORDER_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_BOOT_ORDER_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmBootOrderMsg").build());
        tokenBucketMap.put("APISetVmClockTrackMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_CLOCK_TRACK_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_CLOCK_TRACK_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmClockTrackMsg").build());
        tokenBucketMap.put("APIGetVmBootOrderMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_BOOT_ORDER_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_BOOT_ORDER_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmBootOrderMsg").build());
        tokenBucketMap.put("APISetVmBootVolumeMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_BOOT_VOLUME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_BOOT_VOLUME_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmBootOrderMsg").build());
        tokenBucketMap.put("APIGetVmConsolePasswordMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmConsolePasswordMsg").build());
        tokenBucketMap.put("APIDeleteVmConsolePasswordMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmConsolePasswordMsg").build());
        tokenBucketMap.put("APISetVmConsolePasswordMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_CONSOLE_PASSWORD_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_CONSOLE_PASSWORD_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmConsolePasswordMsg").build());
        tokenBucketMap.put("APIGetVmConsoleAddressMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_CONSOLE_ADDRESS_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_CONSOLE_ADDRESS_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmConsoleAddressMsg").build());
        tokenBucketMap.put("APIDeleteVmHostnameMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_HOSTNAME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_HOSTNAME_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmHostnameMsg").build());
        tokenBucketMap.put("APISetVmHostnameMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_HOSTNAME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_HOSTNAME_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmHostnameMsg").build());
        tokenBucketMap.put("APISetVmBootModeMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_BOOT_MODE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_BOOT_MODE_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmBootModeMsg").build());
        tokenBucketMap.put("APIDeleteVmBootModeMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_BOOT_MODE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_BOOT_MODE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmBootModeMsg").build());
        tokenBucketMap.put("APISetVmStaticIpMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_STATIC_IP_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_STATIC_IP_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmStaticIpMsg").build());
        tokenBucketMap.put("APIDeleteVmStaticIpMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_STATIC_IP_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_STATIC_IP_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmStaticIpMsg").build());
        tokenBucketMap.put("APIGetVmHostnameMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_HOSTNAME_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_HOSTNAME_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmHostnameMsg").build());
        tokenBucketMap.put("APIGetVmStartingCandidateClustersHostsMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_STARTING_CANDIDATE_CLUSTERS_HOSTS_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_STARTING_CANDIDATE_CLUSTERS_HOSTS_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmStartingCandidateClustersHostsMsg").build());
        tokenBucketMap.put("APIGetVmCapabilitiesMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_CAPABILITIES_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_CAPABILITIES_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmCapabilitiesMsg").build());
        tokenBucketMap.put("APISetVmSshKeyMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_SSH_KEY_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_SSH_KEY_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmSshKeyMsg").build());
        tokenBucketMap.put("APIGetVmSshKeyMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_SSH_KEY_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_SSH_KEY_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmSshKeyMsg").build());
        tokenBucketMap.put("APIDeleteVmSshKeyMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_SSH_KEY_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_SSH_KEY_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmSshKeyMsg").build());
        tokenBucketMap.put("APIGetCandidateZonesClustersHostsForCreatingVmMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_CANDIDATE_ZONES_CLUSTERS_HOSTS_FOR_CREATING_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_CANDIDATE_ZONES_CLUSTERS_HOSTS_FOR_CREATING_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetCandidateZonesClustersHostsForCreatingVmMsg").build());
        tokenBucketMap.put("APIGetCandidatePrimaryStoragesForCreatingVmMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_CANDIDATE_PRIMARY_STORAGES_FOR_CREATING_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_CANDIDATE_PRIMARY_STORAGES_FOR_CREATING_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetCandidatePrimaryStoragesForCreatingVmMsg").build());
        tokenBucketMap.put("APIGetInterdependentL3NetworksImagesMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_INTERDEPENDENT_L3_NETWORKS_IMAGES_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_INTERDEPENDENT_L3_NETWORKS_IMAGES_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetInterdependentL3NetworksImagesMsg").build());
        tokenBucketMap.put("APIGetCandidateVmForAttachingIsoMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_CANDIDATE_VM_FOR_ATTACHING_ISO_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_CANDIDATE_VM_FOR_ATTACHING_ISO_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetCandidateVmForAttachingIsoMsg").build());
        tokenBucketMap.put("APIGetCandidateIsoForAttachingVmMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_CANDIDATE_ISO_FOR_ATTACHING_VM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_CANDIDATE_ISO_FOR_ATTACHING_VM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetCandidateIsoForAttachingVmMsg").build());
        tokenBucketMap.put("APIPauseVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_PAUSE_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_PAUSE_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIPauseVmInstanceMsg").build());
        tokenBucketMap.put("APIResumeVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_RESUME_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_RESUME_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIResumeVmInstanceMsg").build());
        tokenBucketMap.put("APIReimageVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_REIMAGE_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_REIMAGE_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "APIReimageVmInstanceMsg").build());
        tokenBucketMap.put("APIAttachL3NetworkToVmNicMsg", new TokenBucket(RateLimitGlobalConfig.API_ATTACH_L3_NETWORK_TO_VM_NIC_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_ATTACH_L3_NETWORK_TO_VM_NIC_MSG_AVG_FLOW_RATE.value(Integer.class), "APIReimageVmInstanceMsg").build());
        tokenBucketMap.put("APIQueryVmCdRomMsg", new TokenBucket(RateLimitGlobalConfig.API_QUERY_VM_CD_ROM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_QUERY_VM_CD_ROM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIQueryVmCdRomMsg").build());
        tokenBucketMap.put("APICreateVmCdRomMsg", new TokenBucket(RateLimitGlobalConfig.API_CREATE_VM_CD_ROM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_CREATE_VM_CD_ROM_MSG_AVG_FLOW_RATE.value(Integer.class), "APICreateVmCdRomMsg").build());
        tokenBucketMap.put("APIDeleteVmCdRomMsg", new TokenBucket(RateLimitGlobalConfig.API_DELETE_VM_CD_ROM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_DELETE_VM_CD_ROM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIDeleteVmCdRomMsg").build());
        tokenBucketMap.put("APIUpdateVmCdRomMsg", new TokenBucket(RateLimitGlobalConfig.API_UPDATE_VM_CD_ROM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_UPDATE_VM_CD_ROM_MSG_AVG_FLOW_RATE.value(Integer.class), "APIUpdateVmCdRomMsg").build());
        tokenBucketMap.put("APISetVmInstanceDefaultCdRomMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_INSTANCE_DEFAULT_CD_ROM_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_INSTANCE_DEFAULT_CD_ROM_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmInstanceDefaultCdRomMsg").build());
        tokenBucketMap.put("APIUpdateVmPriorityMsg", new TokenBucket(RateLimitGlobalConfig.API_UPDATE_VM_PRIORITY_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_UPDATE_VM_PRIORITY_MSG_AVG_FLOW_RATE.value(Integer.class), "APIUpdateVmPriorityMsg").build());
        tokenBucketMap.put("APIUpdatePriorityConfigMsg", new TokenBucket(RateLimitGlobalConfig.API_UPDATE_PRIORITY_CONFIG_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_UPDATE_PRIORITY_CONFIG_MSG_AVG_FLOW_RATE.value(Integer.class), "APIUpdatePriorityConfigMsg").build());
        tokenBucketMap.put("APIUpdateVmNicDriverMsg", new TokenBucket(RateLimitGlobalConfig.API_UPDATE_VM_NIC_DRIVER_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_UPDATE_VM_NIC_DRIVER_MSG_AVG_FLOW_RATE.value(Integer.class), "APIUpdateVmNicDriverMsg").build());
        tokenBucketMap.put("APISetVmSoundTypeMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_SOUND_TYPE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_SOUND_TYPE_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmSoundTypeMsg").build());
        tokenBucketMap.put("APISetVmQxlMemoryMsg", new TokenBucket(RateLimitGlobalConfig.API_SET_VM_QXL_MEMORY_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_SET_VM_QXL_MEMORY_MSG_AVG_FLOW_RATE.value(Integer.class), "APISetVmQxlMemoryMsg").build());
        tokenBucketMap.put("APIGetSpiceCertificatesMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_SPICE_CERTIFICATES_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_SPICE_CERTIFICATES_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetSpiceCertificatesMsg").build());
        tokenBucketMap.put("APIGetVmDeviceAddressMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VM_DEVICE_ADDRESS_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VM_DEVICE_ADDRESS_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmDeviceAddressMsg").build());
        tokenBucketMap.put("APIGetVmsCapabilitiesMsg", new TokenBucket(RateLimitGlobalConfig.API_GET_VMS_CAPABILITIES_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_GET_VMS_CAPABILITIES_MSG_AVG_FLOW_RATE.value(Integer.class), "APIGetVmsCapabilitiesMsg").build());
        return true;
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        TokenBucket tokenBucket = tokenBucketMap.get(msg.getClass().getSimpleName());
        if (tokenBucket == null) {
            throw new RuntimeException("This api is not tokenBucket");
        }

        boolean result = tokenBucket.getTokens();

        if (!result) {
            throw new RuntimeException("Your access is blocked");
        }

        return msg;
    }

    @Override
    public boolean stop() {
        return true;
    }
}
