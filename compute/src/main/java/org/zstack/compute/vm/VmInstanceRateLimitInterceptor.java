package org.zstack.compute.vm;

import org.zstack.core.ratelimit.RateLimitGlobalConfig;
import org.zstack.core.ratelimit.TokenBucket;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.utils.Utils;
import org.zstack.utils.logging.CLogger;

import java.util.HashMap;
import java.util.Map;

public class VmInstanceRateLimitInterceptor implements ApiMessageInterceptor {
    private static final CLogger logger = Utils.getLogger(VmInstanceRateLimitInterceptor.class);

    private Map<String, TokenBucket> tokenBucketMap = new HashMap<>();

    {
        tokenBucketMap.put("APICreateVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeMsg", new TokenBucket(10, 10, "apiCreateVmInstanceFromVolumeMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeSnapshotMsg", new TokenBucket(10, 10, "apiCreateVmInstanceFromVolumeSnapshotMsg").build());
        tokenBucketMap.put("APICreateVmInstanceFromVolumeSnapshotGroupMsg", new TokenBucket(10, 10, "apiCreateVmInstanceFromVolumeSnapshotGroupMsg").build());
        tokenBucketMap.put("APIStopVmInstanceMsg", new TokenBucket(RateLimitGlobalConfig.API_STOP_VM_INSTANCE_MSG_MAX_FLOW_RATE.value(Integer.class), RateLimitGlobalConfig.API_STOP_VM_INSTANCE_MSG_AVG_FLOW_RATE.value(Integer.class), "apiStopVmInstanceMsg").build());
//        tokenBucketMap.put("APIRebootVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDestroyVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIStartVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIMigrateVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIQueryVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIQueryVmNicMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIQueryVmPriorityConfigMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIAttachL3NetworkToVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIChangeVmNicNetworkMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetCandidateL3NetworksForChangeVmNicNetworkMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APICreateVmNicMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIAttachVmNicToVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmNicMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmNicAttachedNetworkServiceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmMigrationCandidateHostsMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmAttachableDataVolumeMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIUpdateVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIChangeInstanceOfferingMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDetachL3NetworkFromVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmAttachableL3NetworkMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIAttachIsoToVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDetachIsoFromVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIRecoverVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIExpungeVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmBootOrderMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmClockTrackMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmBootOrderMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmConsolePasswordMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmConsolePasswordMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmConsolePasswordMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmConsoleAddressMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmHostnameMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmHostnameMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmBootModeMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmBootModeMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmStaticIpMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmStaticIpMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmHostnameMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmStartingCandidateClustersHostsMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmCapabilitiesMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmSshKeyMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmSshKeyMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmSshKeyMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetCandidateZonesClustersHostsForCreatingVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetCandidatePrimaryStoragesForCreatingVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetInterdependentL3NetworksImagesMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetCandidateVmForAttachingIsoMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetCandidateIsoForAttachingVmMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIPauseVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIResumeVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIReimageVmInstanceMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIQueryVmCdRomMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APICreateVmCdRomMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIDeleteVmCdRomMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIUpdateVmCdRomMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmInstanceDefaultCdRomMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIUpdateVmPriorityMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIUpdatePriorityConfigMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIUpdateVmNicDriverMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmSoundTypeMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APISetVmQxlMemoryMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetSpiceCertificatesMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmDeviceAddressMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
//        tokenBucketMap.put("APIGetVmsCapabilitiesMsg", new TokenBucket(10, 10, "apiCreateVmInstanceMsg").build());
    }

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        boolean result = tokenBucketMap.get(msg.getClass().getSimpleName()).getTokens();

        if (!result) {
            throw new RuntimeException("Your access is blocked");
        }

        return msg;
    }
}
