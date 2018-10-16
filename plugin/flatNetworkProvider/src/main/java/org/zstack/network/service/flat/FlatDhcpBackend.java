package org.zstack.network.service.flat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.zstack.compute.vm.VmSystemTags;
import org.zstack.core.cloudbus.CloudBus;
import org.zstack.core.cloudbus.CloudBusCallBack;
import org.zstack.core.cloudbus.MessageSafe;
import org.zstack.core.componentloader.PluginRegistry;
import org.zstack.core.db.DatabaseFacade;
import org.zstack.core.db.GLock;
import org.zstack.core.db.Q;
import org.zstack.core.db.SimpleQuery;
import org.zstack.core.defer.Defer;
import org.zstack.core.defer.Deferred;
import org.zstack.core.errorcode.ErrorFacade;
import org.zstack.core.notification.N;
import org.zstack.core.thread.SyncTask;
import org.zstack.core.thread.ThreadFacade;
import org.zstack.core.timeout.ApiTimeoutManager;
import org.zstack.core.workflow.FlowChainBuilder;
import org.zstack.core.workflow.ShareFlow;
import org.zstack.header.AbstractService;
import org.zstack.header.core.*;
import org.zstack.header.core.workflow.*;
import org.zstack.header.errorcode.ErrorCode;
import org.zstack.header.errorcode.OperationFailureException;
import org.zstack.header.exception.CloudRuntimeException;
import org.zstack.header.host.HostConstant;
import org.zstack.header.host.HostErrors;
import org.zstack.header.message.APIMessage;
import org.zstack.header.message.Message;
import org.zstack.header.message.MessageReply;
import org.zstack.header.network.l2.L2NetworkVO;
import org.zstack.header.network.l3.*;
import org.zstack.header.network.service.*;
import org.zstack.header.vm.*;
import org.zstack.header.vm.VmAbnormalLifeCycleStruct.VmAbnormalLifeCycleOperation;
import org.zstack.kvm.*;
import org.zstack.kvm.KvmCommandSender.SteppingSendCallback;
import org.zstack.network.service.*;
import org.zstack.tag.SystemTagCreator;
import org.zstack.utils.CollectionUtils;
import org.zstack.utils.DebugUtils;
import org.zstack.utils.TagUtils;
import org.zstack.utils.Utils;
import org.zstack.utils.function.Function;
import org.zstack.utils.gson.JSONObjectUtil;
import org.zstack.utils.logging.CLogger;
import org.zstack.utils.network.IPv6Constants;
import org.zstack.utils.network.IPv6NetworkUtils;
import org.zstack.utils.network.NetworkUtils;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.zstack.core.Platform.argerr;
import static org.zstack.core.Platform.operr;
import static org.zstack.utils.CollectionDSL.*;

/**
 * Created by frank on 9/15/2015.
 */
public class FlatDhcpBackend extends AbstractService implements NetworkServiceDhcpBackend, KVMHostConnectExtensionPoint,
        L3NetworkDeleteExtensionPoint, VmInstanceMigrateExtensionPoint, VmAbnormalLifeCycleExtensionPoint, IpRangeDeletionExtensionPoint,
        BeforeStartNewCreatedVmExtensionPoint {
    private static final CLogger logger = Utils.getLogger(FlatDhcpBackend.class);

    @Autowired
    private CloudBus bus;
    @Autowired
    private ErrorFacade errf;
    @Autowired
    private DatabaseFacade dbf;
    @Autowired
    private ThreadFacade thdf;
    @Autowired
    private ApiTimeoutManager timeoutMgr;
    @Autowired
    private PluginRegistry pluginRgty;

    public static final String APPLY_DHCP_PATH = "/flatnetworkprovider/dhcp/apply";
    public static final String PREPARE_DHCP_PATH = "/flatnetworkprovider/dhcp/prepare";
    public static final String RELEASE_DHCP_PATH = "/flatnetworkprovider/dhcp/release";
    public static final String DHCP_CONNECT_PATH = "/flatnetworkprovider/dhcp/connect";
    public static final String RESET_DEFAULT_GATEWAY_PATH = "/flatnetworkprovider/dhcp/resetDefaultGateway";
    public static final String DHCP_DELETE_NAMESPACE_PATH = "/flatnetworkprovider/dhcp/deletenamespace";

    private Map<String, UsedIpInventory> l3NetworkDhcpServerIp = new ConcurrentHashMap<String, UsedIpInventory>();

    public static String makeNamespaceName(String brName, String l3Uuid) {
        return String.format("%s_%s", brName, l3Uuid);
    }

    @Transactional(readOnly = true)
    private List<DhcpInfo> getDhcpInfoForConnectedKvmHost(KVMHostConnectedContext context) {
        String sql = "select vm.uuid, vm.defaultL3NetworkUuid from VmInstanceVO vm where vm.hostUuid = :huuid and vm.state in (:states) and vm.type = :vtype";
        TypedQuery<Tuple> q = dbf.getEntityManager().createQuery(sql, Tuple.class);
        q.setParameter("huuid", context.getInventory().getUuid());
        q.setParameter("states", list(VmInstanceState.Running, VmInstanceState.Unknown));
        q.setParameter("vtype", VmInstanceConstant.USER_VM_TYPE);
        List<Tuple> ts = q.getResultList();
        if (ts.isEmpty()) {
            return null;
        }

        Map<String, String> vmDefaultL3 = new HashMap<String, String>();
        for (Tuple t : ts) {
            vmDefaultL3.put(t.get(0, String.class), t.get(1, String.class));
        }

        sql = "select nic from VmNicVO nic, L3NetworkVO l3, NetworkServiceL3NetworkRefVO ref, NetworkServiceProviderVO provider, UsedIpVO ip" +
                " where nic.uuid = ip.vmNicUuid and ip.l3NetworkUuid = l3.uuid" +
                " and ref.l3NetworkUuid = l3.uuid and ref.networkServiceProviderUuid = provider.uuid " +
                " and ref.networkServiceType = :dhcpType " +
                " and provider.type = :ptype and nic.vmInstanceUuid in (:vmUuids) group by nic.uuid";

        TypedQuery<VmNicVO> nq = dbf.getEntityManager().createQuery(sql, VmNicVO.class);
        nq.setParameter("ptype", FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE_STRING);
        nq.setParameter("dhcpType", NetworkServiceType.DHCP.toString());
        nq.setParameter("vmUuids", vmDefaultL3.keySet());
        List<VmNicVO> nics = nq.getResultList();
        if (nics.isEmpty()) {
            return null;
        }

        List<String> l3Uuids = new ArrayList<>();
        for (VmNicVO nic : nics) {
            for (UsedIpVO ip : nic.getUsedIps()) {
                l3Uuids.add(ip.getL3NetworkUuid());
            }
        }
        l3Uuids = l3Uuids.stream().distinct().collect(Collectors.toList());

        sql = "select t.tag, l3.uuid from SystemTagVO t, L3NetworkVO l3 where t.resourceType = :ttype and t.tag like :tag" +
                " and t.resourceUuid = l3.l2NetworkUuid and l3.uuid in (:l3Uuids)";
        TypedQuery<Tuple> tq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tq.setParameter("tag", TagUtils.tagPatternToSqlPattern(KVMSystemTags.L2_BRIDGE_NAME.getTagFormat()));
        tq.setParameter("l3Uuids", l3Uuids);
        tq.setParameter("ttype", L2NetworkVO.class.getSimpleName());
        ts = tq.getResultList();

        Map<String, String> bridgeNames = new HashMap<String, String>();
        for (Tuple t : ts) {
            bridgeNames.put(t.get(1, String.class), t.get(0, String.class));
        }

        sql = "select t.tag, vm.uuid from SystemTagVO t, VmInstanceVO vm where t.resourceType = :ttype" +
                " and t.tag like :tag and t.resourceUuid = vm.uuid and vm.uuid in (:vmUuids)";
        tq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tq.setParameter("tag", TagUtils.tagPatternToSqlPattern(VmSystemTags.HOSTNAME.getTagFormat()));
        tq.setParameter("ttype", VmInstanceVO.class.getSimpleName());
        tq.setParameter("vmUuids", vmDefaultL3.keySet());
        ts = tq.getResultList();
        Map<String, String> hostnames = new HashMap<String, String>();
        for (Tuple t : ts) {
            hostnames.put(t.get(1, String.class), VmSystemTags.HOSTNAME.getTokenByTag(t.get(0, String.class), VmSystemTags.HOSTNAME_TOKEN));
        }

        sql = "select l3 from L3NetworkVO l3 where l3.uuid in (:l3Uuids)";
        TypedQuery<L3NetworkVO> l3q = dbf.getEntityManager().createQuery(sql, L3NetworkVO.class);
        l3q.setParameter("l3Uuids", l3Uuids);
        List<L3NetworkVO> l3s = l3q.getResultList();
        Map<String, L3NetworkVO> l3Map = new HashMap<String, L3NetworkVO>();
        for (L3NetworkVO l3 : l3s) {
            l3Map.put(l3.getUuid(), l3);
        }

        List<DhcpInfo> dhcpInfoList = new ArrayList<DhcpInfo>();
        for (VmNicVO nic : nics) {
            for (UsedIpVO ip : nic.getUsedIps()) {

                DhcpInfo info = new DhcpInfo();
                info.bridgeName = KVMSystemTags.L2_BRIDGE_NAME.getTokenByTag(bridgeNames.get(nic.getL3NetworkUuid()), KVMSystemTags.L2_BRIDGE_NAME_TOKEN);
                info.namespaceName = makeNamespaceName(
                        info.bridgeName,
                        ip.getL3NetworkUuid()
                );
                DebugUtils.Assert(info.bridgeName != null, "bridge name cannot be null");
                info.mtu = new MtuGetter().getMtu(ip.getL3NetworkUuid());
                info.mac = nic.getMac();
                info.netmask = ip.getNetmask();
                info.isDefaultL3Network = ip.getL3NetworkUuid().equals(vmDefaultL3.get(nic.getVmInstanceUuid()));
                info.ip = ip.getIp();
                info.gateway = ip.getGateway();
                info.ipVersion = ip.getIpVersion();

                L3NetworkVO l3 = l3Map.get(ip.getL3NetworkUuid());
                info.dnsDomain = l3.getDnsDomain();
                info.dns = getL3NetworkDns(ip.getL3NetworkUuid());
                info.firstIp = NetworkUtils.getSmallestIp(l3.getIpRanges().stream().map(r -> r.getStartIp()).collect(Collectors.toList()));
                info.endIp = NetworkUtils.getBiggesttIp(l3.getIpRanges().stream().map(r -> r.getEndIp()).collect(Collectors.toList()));
                info.prefixLength = l3.getIpRanges().stream().findAny().get().getPrefixLen();

                if (info.isDefaultL3Network) {
                    info.hostname = hostnames.get(nic.getVmInstanceUuid());
                    if (info.hostname == null && ip.getIp() != null) {
                        if (ip.getIpVersion() == IPv6Constants.IPv4) {
                            info.hostname = ip.getIp().replaceAll("\\.", "-");
                        } else {
                            info.hostname = IPv6NetworkUtils.ipv6AddessToHostname(ip.getIp());
                        }
                    }

                    if (info.dnsDomain != null) {
                        info.hostname = String.format("%s.%s", info.hostname, info.dnsDomain);
                    }
                }

                info.l3NetworkUuid = l3.getUuid();
                info.hostRoutes = getL3NetworkHostRoute(ip.getL3NetworkUuid());

                dhcpInfoList.add(info);
            }
        }

        return dhcpInfoList;
    }

    @Override
    @MessageSafe
    public void handleMessage(Message msg) {
        if (msg instanceof APIMessage) {
            handleApiMessage((APIMessage) msg);
        } else {
            handleLocalMessage(msg);
        }

    }

    private void handleApiMessage(APIMessage msg) {
        if (msg instanceof APIGetL3NetworkDhcpIpAddressMsg) {
            handle((APIGetL3NetworkDhcpIpAddressMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handleLocalMessage(Message msg) {
        if (msg instanceof FlatDhcpAcquireDhcpServerIpMsg) {
            handle((FlatDhcpAcquireDhcpServerIpMsg) msg);
        } else if (msg instanceof L3NetworkUpdateDhcpMsg) {
            handle((L3NetworkUpdateDhcpMsg) msg);
        } else {
            bus.dealWithUnknownMessage(msg);
        }
    }

    private void handle(APIGetL3NetworkDhcpIpAddressMsg msg) {
        APIGetL3NetworkDhcpIpAddressReply reply = new APIGetL3NetworkDhcpIpAddressReply();

        if (msg.getL3NetworkUuid() == null) {
            reply.setError(argerr("l3 network uuid cannot be null"));
            bus.reply(msg, reply);
            return;
        }

        UsedIpInventory ip = l3NetworkDhcpServerIp.get(msg.getL3NetworkUuid());
        if (ip != null) {
            reply.setIp(ip.getIp());
            bus.reply(msg, reply);
            return;
        }

        String tag = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTag(msg.getL3NetworkUuid());
        if (tag != null) {
            Map<String, String> tokens = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTokensByTag(tag);
            String ipUuid = tokens.get(FlatNetworkSystemTags.L3_NETWORK_DHCP_IP_UUID_TOKEN);
            UsedIpVO vo = dbf.findByUuid(ipUuid, UsedIpVO.class);
            if (vo == null) {
                throw new CloudRuntimeException(String.format("cannot find used ip [uuid:%s]", ipUuid));
            }

            ip = UsedIpInventory.valueOf(vo);
            l3NetworkDhcpServerIp.put(msg.getL3NetworkUuid(), ip);
            reply.setIp(ip.getIp());
            bus.reply(msg, reply);
            logger.debug(String.format("APIGetL3NetworkDhcpIpAddressMsg[ip:%s, uuid:%s] for l3 network[uuid:%s]",
                    ip.getIp(), ip.getUuid(), ip.getL3NetworkUuid()));
            return;
        }

        reply.setError(operr("Cannot find DhcpIp for l3 network[uuid:%s]", msg.getL3NetworkUuid()));
        bus.reply(msg, reply);
    }

    @Deferred
    public UsedIpInventory allocateDhcpIp(String l3Uuid) {
        L3NetworkVO l3 = Q.New(L3NetworkVO.class).eq(L3NetworkVO_.uuid, l3Uuid).find();
        UsedIpInventory ip = l3NetworkDhcpServerIp.get(l3Uuid);
        if (ip != null) {
            return ip;
        }

        if (!isProvidedbyMe(L3NetworkInventory.valueOf(l3))) {
            return null;
        }

        // TODO: static allocate the IP to avoid the lock
        GLock lock = new GLock(String.format("l3-%s-allocate-dhcp-ip", l3Uuid), TimeUnit.MINUTES.toSeconds(30));
        lock.lock();
        Defer.defer(lock::unlock);

        String tag = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTag(l3Uuid);
        if (tag != null) {
            Map<String, String> tokens = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTokensByTag(tag);
            String ipUuid = tokens.get(FlatNetworkSystemTags.L3_NETWORK_DHCP_IP_UUID_TOKEN);
            UsedIpVO vo = dbf.findByUuid(ipUuid, UsedIpVO.class);
            if (vo == null) {
                throw new CloudRuntimeException(String.format("cannot find used ip [uuid:%s]", ipUuid));
            }

            ip = UsedIpInventory.valueOf(vo);
            l3NetworkDhcpServerIp.put(l3Uuid, ip);
            return ip;
        }

        AllocateIpMsg amsg = new AllocateIpMsg();
        amsg.setL3NetworkUuid(l3Uuid);
        bus.makeTargetServiceIdByResourceUuid(amsg, L3NetworkConstant.SERVICE_ID, l3Uuid);
        MessageReply reply = bus.call(amsg);
        if (!reply.isSuccess()) {
            throw new OperationFailureException(reply.getError());
        }

        AllocateIpReply r = reply.castReply();
        ip = r.getIpInventory();

        SystemTagCreator creator = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.newSystemTagCreator(l3Uuid);
        creator.inherent = true;
        creator.setTagByTokens(
                map(
                        e(FlatNetworkSystemTags.L3_NETWORK_DHCP_IP_TOKEN, IPv6NetworkUtils.ipv6AddessToTagValue(ip.getIp())),
                        e(FlatNetworkSystemTags.L3_NETWORK_DHCP_IP_UUID_TOKEN, ip.getUuid())
                )
        );
        creator.create();

        l3NetworkDhcpServerIp.put(l3Uuid, ip);
        logger.debug(String.format("allocate DHCP server IP[ip:%s, uuid:%s] for l3 network[uuid:%s]", ip.getIp(), ip.getUuid(), ip.getL3NetworkUuid()));
        for (DhcpServerExtensionPoint exp : pluginRgty.getExtensionList(DhcpServerExtensionPoint.class)) {
            exp.afterAllocateDhcpServerIP(l3Uuid, ip);
        }
        return ip;
    }

    private void handle(final FlatDhcpAcquireDhcpServerIpMsg msg) {
        thdf.syncSubmit(new SyncTask<Void>() {
            @Override
            public Void call() throws Exception {
                dealMessage(msg);
                return null;
            }

            @MessageSafe
            private void dealMessage(FlatDhcpAcquireDhcpServerIpMsg msg) {
                FlatDhcpAcquireDhcpServerIpReply reply = new FlatDhcpAcquireDhcpServerIpReply();
                UsedIpInventory ip = allocateDhcpIp(msg.getL3NetworkUuid());
                if (ip != null) {
                    reply.setIp(ip.getIp());
                    reply.setNetmask(ip.getNetmask());
                    reply.setUsedIpUuid(ip.getUuid());
                    IpRangeVO ipr = dbf.findByUuid(ip.getIpRangeUuid(), IpRangeVO.class);
                    reply.setIpr(IpRangeInventory.valueOf(ipr));
                    reply.setUsedIp(ip);
                }
                bus.reply(msg, reply);
            }

            @Override
            public String getName() {
                return getSyncSignature();
            }

            @Override
            public String getSyncSignature() {
                return String.format("flat-dhcp-get-dhcp-ip-for-l3-network-%s", msg.getL3NetworkUuid());
            }

            @Override
            public int getSyncLevel() {
                return 1;
            }
        });
    }

    @Override
    public String getId() {
        return bus.makeLocalServiceId(FlatNetworkServiceConstant.SERVICE_ID);
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String preDeleteL3Network(L3NetworkInventory inventory) throws L3NetworkException {
        return null;
    }

    @Override
    public void beforeDeleteL3Network(L3NetworkInventory inventory) {
    }

    private boolean isProvidedbyMe(L3NetworkInventory l3) {
        String providerType = new NetworkProviderFinder().getNetworkProviderTypeByNetworkServiceType(l3.getUuid(), NetworkServiceType.DHCP.toString());
        return FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE_STRING.equals(providerType);
    }

    @Override
    public void afterDeleteL3Network(L3NetworkInventory inventory) {
        if (!isProvidedbyMe(inventory)) {
            return;
        }

        UsedIpInventory dhchip = getDHCPServerIP(inventory.getUuid());
        if (dhchip != null) {
            deleteDhcpServerIp(dhchip);
            logger.debug(String.format("delete DHCP IP[%s] of the flat network[uuid:%s] as the L3 network is deleted",
                    dhchip.getIp(), dhchip.getL3NetworkUuid()));
        }

        deleteNameSpace(inventory);
    }

    private void deleteNameSpace(L3NetworkInventory inventory) {
        List<String> huuids = new Callable<List<String>>() {
            @Override
            @Transactional(readOnly = true)
            public List<String> call() {
                String sql = "select host.uuid from HostVO host, L2NetworkVO l2, L2NetworkClusterRefVO ref where l2.uuid = ref.l2NetworkUuid" +
                        " and ref.clusterUuid = host.clusterUuid and l2.uuid = :uuid";
                TypedQuery<String> q = dbf.getEntityManager().createQuery(sql, String.class);
                q.setParameter("uuid", inventory.getL2NetworkUuid());
                return q.getResultList();
            }
        }.call();

        if (huuids.isEmpty()) {
            return;
        }

        String brName = new BridgeNameFinder().findByL3Uuid(inventory.getUuid());
        DeleteNamespaceCmd cmd = new DeleteNamespaceCmd();
        cmd.bridgeName = brName;
        cmd.namespaceName = makeNamespaceName(brName, inventory.getUuid());

        new KvmCommandSender(huuids).send(cmd, DHCP_DELETE_NAMESPACE_PATH, wrapper -> {
            DeleteNamespaceRsp rsp = wrapper.getResponse(DeleteNamespaceRsp.class);
            return rsp.isSuccess() ? null : operr("operation error, because:%s", rsp.getError());
        }, new SteppingSendCallback<KvmResponseWrapper>() {
            @Override
            public void success(KvmResponseWrapper w) {
                logger.debug(String.format("successfully deleted namespace for L3 network[uuid:%s, name:%s] on the " +
                        "KVM host[uuid:%s]", inventory.getUuid(), inventory.getName(), getHostUuid()));
            }

            @Override
            public void fail(ErrorCode errorCode) {
                if (!errorCode.isError(HostErrors.OPERATION_FAILURE_GC_ELIGIBLE)) {
                    return;
                }

                FlatDHCPDeleteNamespaceGC gc = new FlatDHCPDeleteNamespaceGC();
                gc.hostUuid = getHostUuid();
                gc.command = cmd;
                gc.NAME = String.format("gc-namespace-on-host-%s", getHostUuid());
                gc.submit();
            }
        });
    }

    private List<DhcpInfo> getVmDhcpInfo(VmInstanceInventory vm) {
        return getVmDhcpInfo(vm, null);
    }

    @Transactional(readOnly = true)
    private List<DhcpInfo> getVmDhcpInfo(VmInstanceInventory vm, String l3Uuid) {
        String sql = "select nic from VmNicVO nic, L3NetworkVO l3, NetworkServiceL3NetworkRefVO ref, NetworkServiceProviderVO provider, UsedIpVO ip" +
                " where nic.uuid = ip.vmNicUuid and ip.l3NetworkUuid = l3.uuid" +
                " and ref.l3NetworkUuid = l3.uuid and ref.networkServiceProviderUuid = provider.uuid " +
                " and ref.networkServiceType = :dhcpType " +
                " and provider.type = :ptype and nic.vmInstanceUuid = :vmUuid group by nic.uuid";

        TypedQuery<VmNicVO> nq = dbf.getEntityManager().createQuery(sql, VmNicVO.class);
        nq.setParameter("dhcpType", NetworkServiceType.DHCP.toString());
        nq.setParameter("ptype", FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE_STRING);
        nq.setParameter("vmUuid", vm.getUuid());
        List<VmNicVO> nics = nq.getResultList();

        if (l3Uuid != null) {
            nics = nics.stream().filter(nic -> nic.getUsedIps().stream().map(ip -> ip.getL3NetworkUuid()).collect(Collectors.toList()).contains(l3Uuid)).collect(Collectors.toList());
        }

        if (nics.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> l3Uuids = new ArrayList<>();
        for (VmNicVO nic : nics) {
            for (UsedIpVO ip : nic.getUsedIps()) {
                l3Uuids.add(ip.getL3NetworkUuid());
            }
        }
        l3Uuids = l3Uuids.stream().distinct().collect(Collectors.toList());

        sql = "select t.tag, l3.uuid from SystemTagVO t, L3NetworkVO l3 where t.resourceType = :ttype and t.tag like :tag" +
                " and t.resourceUuid = l3.l2NetworkUuid and l3.uuid in (:l3Uuids)";
        TypedQuery<Tuple> tq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tq.setParameter("tag", TagUtils.tagPatternToSqlPattern(KVMSystemTags.L2_BRIDGE_NAME.getTagFormat()));
        tq.setParameter("l3Uuids", l3Uuids);
        tq.setParameter("ttype", L2NetworkVO.class.getSimpleName());
        List<Tuple> ts = tq.getResultList();

        Map<String, String> bridgeNames = new HashMap<String, String>();
        for (Tuple t : ts) {
            bridgeNames.put(t.get(1, String.class), t.get(0, String.class));
        }

        sql = "select t.tag, vm.uuid from SystemTagVO t, VmInstanceVO vm where t.resourceType = :ttype" +
                " and t.tag like :tag and t.resourceUuid = vm.uuid and vm.uuid = :vmUuid";
        tq = dbf.getEntityManager().createQuery(sql, Tuple.class);
        tq.setParameter("tag", TagUtils.tagPatternToSqlPattern(VmSystemTags.HOSTNAME.getTagFormat()));
        tq.setParameter("ttype", VmInstanceVO.class.getSimpleName());
        tq.setParameter("vmUuid", vm.getUuid());
        Map<String, String> hostnames = new HashMap<String, String>();
        for (Tuple t : ts) {
            hostnames.put(t.get(1, String.class), VmSystemTags.HOSTNAME.getTokenByTag(t.get(0, String.class), VmSystemTags.HOSTNAME_TOKEN));
        }

        sql = "select l3 from L3NetworkVO l3 where l3.uuid in (:l3Uuids)";
        TypedQuery<L3NetworkVO> l3q = dbf.getEntityManager().createQuery(sql, L3NetworkVO.class);
        l3q.setParameter("l3Uuids", l3Uuids);
        List<L3NetworkVO> l3s = l3q.getResultList();
        Map<String, L3NetworkVO> l3Map = new HashMap<String, L3NetworkVO>();
        for (L3NetworkVO l3 : l3s) {
            l3Map.put(l3.getUuid(), l3);
        }

        List<DhcpInfo> dhcpInfoList = new ArrayList<DhcpInfo>();
        for (VmNicVO nic : nics) {
            for (UsedIpVO ip : nic.getUsedIps()) {
                DhcpInfo info = new DhcpInfo();
                info.bridgeName = KVMSystemTags.L2_BRIDGE_NAME.getTokenByTag(bridgeNames.get(ip.getL3NetworkUuid()), KVMSystemTags.L2_BRIDGE_NAME_TOKEN);
                info.namespaceName = makeNamespaceName(
                        info.bridgeName,
                        ip.getL3NetworkUuid()
                );
                DebugUtils.Assert(info.bridgeName != null, "bridge name cannot be null");
                info.mac = nic.getMac();
                info.netmask = ip.getNetmask();
                info.isDefaultL3Network = ip.getL3NetworkUuid().equals(vm.getDefaultL3NetworkUuid());
                info.ip = ip.getIp();
                info.ipVersion = ip.getIpVersion();
                info.gateway = ip.getGateway();

                L3NetworkVO l3 = l3Map.get(ip.getL3NetworkUuid());
                info.dnsDomain = l3.getDnsDomain();
                info.dns = getL3NetworkDns(ip.getL3NetworkUuid());
                info.firstIp = NetworkUtils.getSmallestIp(l3.getIpRanges().stream().map(r -> r.getStartIp()).collect(Collectors.toList()));
                info.endIp = NetworkUtils.getBiggesttIp(l3.getIpRanges().stream().map(r -> r.getEndIp()).collect(Collectors.toList()));
                info.prefixLength = l3.getIpRanges().stream().findAny().get().getPrefixLen();

                if (info.isDefaultL3Network) {
                    info.hostname = hostnames.get(nic.getVmInstanceUuid());
                    if (info.hostname == null && ip.getIp() != null) {
                        if (ip.getIpVersion() == IPv6Constants.IPv4) {
                            info.hostname = ip.getIp().replaceAll("\\.", "-");
                        } else {
                            info.hostname = IPv6NetworkUtils.ipv6AddessToHostname(ip.getIp());
                        }
                    }

                    if (info.dnsDomain != null) {
                        info.hostname = String.format("%s.%s", info.hostname, info.dnsDomain);
                    }
                }

                info.l3NetworkUuid = l3.getUuid();
                info.hostRoutes = getL3NetworkHostRoute(ip.getL3NetworkUuid());

                dhcpInfoList.add(info);
            }
        }

        return dhcpInfoList;
    }

    @Override
    public void preMigrateVm(VmInstanceInventory inv, String destHostUuid) {
        List<DhcpInfo> info = getVmDhcpInfo(inv);
        if (info == null || info.isEmpty()) {
            return;
        }

        FutureCompletion completion = new FutureCompletion(null);
        applyDhcpToHosts(info, destHostUuid, false, completion);
        completion.await(TimeUnit.MINUTES.toMillis(30));
        if (!completion.isSuccess()) {
            throw new OperationFailureException(operr("cannot configure DHCP for vm[uuid:%s] on the destination host[uuid:%s]",
                            inv.getUuid(), destHostUuid).causedBy(completion.getErrorCode()));
        }
    }

    @Override
    public void beforeMigrateVm(VmInstanceInventory inv, String destHostUuid) {
    }

    @Override
    public void afterMigrateVm(VmInstanceInventory inv, String srcHostUuid) {
        List<DhcpInfo> info = getVmDhcpInfo(inv);
        if (info == null || info.isEmpty()) {
            return;
        }

        releaseDhcpService(info, inv.getUuid(), srcHostUuid, new NoErrorCompletion() {
            @Override
            public void done() {
                // ignore
            }
        });
    }

    @Override
    public void failedToMigrateVm(VmInstanceInventory inv, String destHostUuid, ErrorCode reason) {
        List<DhcpInfo> info = getVmDhcpInfo(inv);
        if (info == null || info.isEmpty()) {
            return;
        }

        releaseDhcpService(info, inv.getUuid(), destHostUuid, new NoErrorCompletion() {
            @Override
            public void done() {
                // ignore
            }
        });
    }

    @Override
    public Flow createVmAbnormalLifeCycleHandlingFlow(final VmAbnormalLifeCycleStruct struct) {
        return new Flow() {
            String __name__ = "flat-network-configure-dhcp";
            VmAbnormalLifeCycleOperation operation = struct.getOperation();
            VmInstanceInventory vm = struct.getVmInstance();
            List<DhcpInfo> info = getVmDhcpInfo(vm);

            String applyHostUuidForRollback;
            String releaseHostUuidForRollback;

            @Override
            public void run(FlowTrigger trigger, Map data) {
                if (info == null || info.isEmpty()) {
                    trigger.next();
                    return;
                }

                if (operation == VmAbnormalLifeCycleOperation.VmRunningOnTheHost) {
                    vmRunningOnTheHost(trigger);
                } else if (operation == VmAbnormalLifeCycleOperation.VmStoppedOnTheSameHost) {
                    vmStoppedOnTheSameHost(trigger);
                } else if (operation == VmAbnormalLifeCycleOperation.VmRunningFromUnknownStateHostChanged) {
                    vmRunningFromUnknownStateHostChanged(trigger);
                } else if (operation == VmAbnormalLifeCycleOperation.VmRunningFromUnknownStateHostNotChanged) {
                    vmRunningFromUnknownStateHostNotChanged(trigger);
                } else if (operation == VmAbnormalLifeCycleOperation.VmMigrateToAnotherHost) {
                    vmMigrateToAnotherHost(trigger);
                } else if (operation == VmAbnormalLifeCycleOperation.VmRunningFromIntermediateState) {
                    vmRunningFromIntermediateState(trigger);
                } else {
                    trigger.next();
                }
            }

            private void vmRunningFromIntermediateState(final FlowTrigger trigger) {
                applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(trigger) {
                    @Override
                    public void success() {
                        releaseHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            private void vmMigrateToAnotherHost(final FlowTrigger trigger) {
                releaseDhcpService(info, vm.getUuid(), struct.getOriginalHostUuid(), new NopeNoErrorCompletion());
                applyHostUuidForRollback = struct.getOriginalHostUuid();

                applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(trigger) {
                    @Override
                    public void success() {
                        releaseHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            private void vmRunningFromUnknownStateHostNotChanged(final FlowTrigger trigger) {
                applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(trigger) {
                    @Override
                    public void success() {
                        releaseHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            private void vmRunningFromUnknownStateHostChanged(final FlowTrigger trigger) {
                releaseDhcpService(info, vm.getUuid(), struct.getOriginalHostUuid(), new NopeNoErrorCompletion());
                applyHostUuidForRollback = struct.getCurrentHostUuid();
                applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(trigger) {
                    @Override
                    public void success() {
                        releaseHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            private void vmStoppedOnTheSameHost(final FlowTrigger trigger) {
                releaseDhcpService(info, vm.getUuid(), struct.getCurrentHostUuid(), new NoErrorCompletion(trigger) {
                    @Override
                    public void done() {
                        applyHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }
                });
            }

            private void vmRunningOnTheHost(final FlowTrigger trigger) {
                applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(trigger) {
                    @Override
                    public void success() {
                        releaseHostUuidForRollback = struct.getCurrentHostUuid();
                        trigger.next();
                    }

                    @Override
                    public void fail(ErrorCode errorCode) {
                        trigger.fail(errorCode);
                    }
                });
            }

            @Override
            public void rollback(FlowRollback trigger, Map data) {
                if (info == null) {
                    trigger.rollback();
                    return;
                }

                if (releaseHostUuidForRollback != null) {
                    releaseDhcpService(info, vm.getUuid(), struct.getOriginalHostUuid(), new NopeNoErrorCompletion());
                }
                if (applyHostUuidForRollback != null) {
                    applyDhcpToHosts(info, struct.getCurrentHostUuid(), false, new Completion(null) {
                        @Override
                        public void success() {
                            //ignore
                        }

                        @Override
                        public void fail(ErrorCode errorCode) {
                            N.New(VmInstanceVO.class, vm.getUuid()).warn_("failed to re-apply DHCP configuration of" +
                                    " the vm[uuid:%s] to the host[uuid:%s], %s. You may need to reboot the VM to" +
                                    " make the DHCP works",  vm.getUuid(), applyHostUuidForRollback, errorCode);
                        }
                    });
                }

                trigger.rollback();
            }
        };
    }

    @Override
    public void preDeleteIpRange(IpRangeInventory ipRange) {

    }

    @Override
    public void beforeDeleteIpRange(IpRangeInventory ipRange) {

    }

    private void deleteDhcpServerIp(UsedIpInventory ip) {
        l3NetworkDhcpServerIp.remove(ip.getL3NetworkUuid());
        FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.deleteInherentTag(ip.getL3NetworkUuid());
        dbf.removeByPrimaryKey(ip.getUuid(), UsedIpVO.class);
        for (DhcpServerExtensionPoint exp : pluginRgty.getExtensionList(DhcpServerExtensionPoint.class)) {
            exp.afterRemoveDhcpServerIP(ip.getL3NetworkUuid(), ip);
        }
    }

    private UsedIpInventory getDHCPServerIP(String l3Uuid) {
        UsedIpInventory dhcpIp = l3NetworkDhcpServerIp.get(l3Uuid);
        if (dhcpIp != null) {
            return dhcpIp;
        }

        String tag = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTag(l3Uuid);
        if (tag != null) {
            Map<String, String> tokens = FlatNetworkSystemTags.L3_NETWORK_DHCP_IP.getTokensByTag(tag);
            String ipUuid = tokens.get(FlatNetworkSystemTags.L3_NETWORK_DHCP_IP_UUID_TOKEN);
            UsedIpVO vo = dbf.findByUuid(ipUuid, UsedIpVO.class);
            if (vo != null) {
                return UsedIpInventory.valueOf(vo);
            }
        }

        return null;
    }

    @Override
    public void afterDeleteIpRange(IpRangeInventory ipRange) {
        UsedIpInventory dhcpIp = getDHCPServerIP(ipRange.getL3NetworkUuid());

        if (dhcpIp != null && NetworkUtils.isInRange(dhcpIp.getIp(), ipRange.getStartIp(), ipRange.getEndIp())) {
            deleteDhcpServerIp(dhcpIp);
            logger.debug(String.format("delete DHCP IP[%s] of the flat network[uuid:%s] as the IP range[uuid:%s] is deleted",
                    dhcpIp.getIp(), ipRange.getL3NetworkUuid(), ipRange.getUuid()));
        }
    }

    @Override
    public void failedToDeleteIpRange(IpRangeInventory ipRange, ErrorCode errorCode) {

    }

    @Override
    public Flow createKvmHostConnectingFlow(final KVMHostConnectedContext context) {
        return new NoRollbackFlow() {
            String __name__ = "prepare-flat-dhcp";

            @Override
            public void run(final FlowTrigger trigger, Map data) {
                final List<DhcpInfo> dhcpInfoList = getDhcpInfoForConnectedKvmHost(context);
                if (dhcpInfoList == null) {
                    trigger.next();
                    return;
                }

                // to flush ebtables
                ConnectCmd cmd = new ConnectCmd();
                KVMHostAsyncHttpCallMsg msg = new KVMHostAsyncHttpCallMsg();
                msg.setHostUuid(context.getInventory().getUuid());
                msg.setCommand(cmd);
                msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
                msg.setNoStatusCheck(true);
                msg.setPath(DHCP_CONNECT_PATH);
                bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, context.getInventory().getUuid());
                bus.send(msg, new CloudBusCallBack(trigger) {
                    @Override
                    public void run(MessageReply reply) {
                        if (!reply.isSuccess()) {
                            trigger.fail(reply.getError());
                        } else {
                            applyDhcpToHosts(dhcpInfoList, context.getInventory().getUuid(), true, new Completion(trigger) {
                                @Override
                                public void success() {
                                    trigger.next();
                                }

                                @Override
                                public void fail(ErrorCode errorCode) {
                                    trigger.fail(errorCode);
                                }
                            });
                        }
                    }
                });
            }
        };
    }

    @Override
    public void beforeStartNewCreatedVm(VmInstanceSpec spec) {
        String providerUuid = new NetworkServiceProviderLookup().lookupUuidByType(FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE_STRING);

        // make sure the Flat DHCP acquired DHCP server IP before starting VMs,
        // otherwise it may not be able to get IP when lots of VMs start concurrently
        // because the logic of VM acquiring IP is ahead flat DHCP acquiring IP
        for (L3NetworkInventory l3 : spec.getL3Networks()) {
            if (l3.getIpVersion() != IPv6Constants.IPv4) {
                continue;
            }

            List<String> serviceTypes = l3.getNetworkServiceTypesFromProvider(providerUuid);
            if (serviceTypes.contains(NetworkServiceType.DHCP.toString())) {
                allocateDhcpIp(l3.getUuid());
            }
        }
    }

    public static class HostRouteInfo {
        public String prefix;
        public String nexthop;
    }

    public static class DhcpInfo {
        public int ipVersion;
        public String raMode;
        public String ip;
        public String mac;
        public String netmask;
        public String firstIp;
        public String endIp;
        public Integer prefixLength;
        public String gateway;
        public String hostname;
        public boolean isDefaultL3Network;
        public String dnsDomain;
        public List<String> dns;
        public String bridgeName;
        public String namespaceName;
        public String l3NetworkUuid;
        public Integer mtu;
        public List<HostRouteInfo> hostRoutes;
    }

    public static class ApplyDhcpCmd extends KVMAgentCommands.AgentCommand {
        public List<DhcpInfo> dhcp;
        public boolean rebuild;
        public String l3NetworkUuid;
    }

    public static class ApplyDhcpRsp extends KVMAgentCommands.AgentResponse {
    }

    public static class ReleaseDhcpCmd extends KVMAgentCommands.AgentCommand {
        public List<DhcpInfo> dhcp;
    }

    public static class ReleaseDhcpRsp extends KVMAgentCommands.AgentResponse {
    }

    public static class PrepareDhcpCmd extends KVMAgentCommands.AgentCommand {
        public String bridgeName;
        public String dhcpServerIp;
        public String dhcpNetmask;
        public String namespaceName;
        public Integer prefixLen;
        public Integer ipVersion;
    }

    public static class PrepareDhcpRsp extends KVMAgentCommands.AgentResponse {
    }

    public static class ConnectCmd extends KVMAgentCommands.AgentCommand {
    }

    public static class ConnectRsp extends KVMAgentCommands.AgentResponse {
    }

    public static class ResetDefaultGatewayCmd extends KVMAgentCommands.AgentCommand {
        public String bridgeNameOfGatewayToRemove;
        public String namespaceNameOfGatewayToRemove;
        public String gatewayToRemove;
        public String macOfGatewayToRemove;
        public String gatewayToAdd;
        public String macOfGatewayToAdd;
        public String bridgeNameOfGatewayToAdd;
        public String namespaceNameOfGatewayToAdd;
    }

    public static class ResetDefaultGatewayRsp extends KVMAgentCommands.AgentResponse {
    }

    public static class DeleteNamespaceCmd extends KVMAgentCommands.AgentCommand {
        public String bridgeName;
        public String namespaceName;
    }

    public static class DeleteNamespaceRsp extends KVMAgentCommands.AgentResponse {
    }

    public NetworkServiceProviderType getProviderType() {
        return FlatNetworkServiceConstant.FLAT_NETWORK_SERVICE_TYPE;
    }

    private List<String> getL3NetworkDns(String l3NetworkUuid){
        List<String> dns = Q.New(L3NetworkDnsVO.class).eq(L3NetworkDnsVO_.l3NetworkUuid, l3NetworkUuid)
                .select(L3NetworkDnsVO_.dns).orderBy(L3NetworkDnsVO_.id, SimpleQuery.Od.ASC).listValues();
        if (dns == null) {
            dns = new ArrayList<String>();
        }

        L3NetworkVO l3VO = Q.New(L3NetworkVO.class).eq(L3NetworkVO_.uuid, l3NetworkUuid).find();
        if (FlatNetwordProviderGlobalConfig.ALLOW_DEFAULT_DNS.value(Boolean.class) && l3VO.getIpVersion() == IPv6Constants.IPv4) {
            UsedIpInventory dhcpIp = getDHCPServerIP(l3NetworkUuid);
            if (dhcpIp != null) {
                dns.add(dhcpIp.getIp());
            }
        }

        return dns;
    }

    private List<HostRouteInfo> getL3NetworkHostRoute(String l3NetworkUuid){
        List<L3NetworkHostRouteVO> vos = Q.New(L3NetworkHostRouteVO.class).eq(L3NetworkHostRouteVO_.l3NetworkUuid, l3NetworkUuid).list();
        if (vos == null || vos.isEmpty()) {
            return new ArrayList<>();
        }

        List<HostRouteInfo> res = new ArrayList<>();
        for (L3NetworkHostRouteVO vo : vos) {
            HostRouteInfo info = new HostRouteInfo();
            info.prefix = vo.getPrefix();
            info.nexthop = vo.getNexthop();
            res.add(info);
        }

        return res;
    }

    private List<DhcpInfo> toDhcpInfo(List<DhcpStruct> structs) {
        final Map<String, String> l3Bridges = new HashMap<String, String>();
        for (DhcpStruct s : structs) {
            if (!l3Bridges.containsKey(s.getL3Network().getUuid())) {
                l3Bridges.put(s.getL3Network().getUuid(),
                        KVMSystemTags.L2_BRIDGE_NAME.getTokenByResourceUuid(s.getL3Network().getL2NetworkUuid(), KVMSystemTags.L2_BRIDGE_NAME_TOKEN));
            }
        }

        return CollectionUtils.transformToList(structs, new Function<DhcpInfo, DhcpStruct>() {
            @Override
            public DhcpInfo call(DhcpStruct arg) {
                if (arg.getIp() == null) {
                    return null;
                }

                if ((arg.getIpVersion() == IPv6Constants.IPv6) && (IPv6Constants.SLAAC.equals(arg.getRaMode()))) {
                    return null;
                }

                DhcpInfo info = new DhcpInfo();
                info.ipVersion = arg.getIpVersion();
                info.raMode = arg.getRaMode();
                info.dnsDomain = arg.getDnsDomain();
                info.gateway = arg.getGateway();
                info.hostname = arg.getHostname();
                info.isDefaultL3Network = arg.isDefaultL3Network();

                if (info.isDefaultL3Network) {
                    if (info.hostname == null && arg.getIp() != null) {
                        if (info.ipVersion == IPv6Constants.IPv4) {
                            info.hostname = arg.getIp().replaceAll("\\.", "-");
                        } else {
                            info.hostname = IPv6NetworkUtils.ipv6AddessToHostname(arg.getIp());
                        }
                    }

                    if (info.dnsDomain != null) {
                        info.hostname = String.format("%s.%s", info.hostname, info.dnsDomain);
                    }
                }

                info.ip = arg.getIp();
                info.netmask = arg.getNetmask();
                info.firstIp = arg.getFirstIp();
                info.endIp = arg.getEndIP();
                info.prefixLength = arg.getPrefixLength();
                info.mac = arg.getMac();
                info.dns = getL3NetworkDns(arg.getL3Network().getUuid());
                info.l3NetworkUuid = arg.getL3Network().getUuid();
                info.bridgeName = l3Bridges.get(arg.getL3Network().getUuid());
                info.namespaceName = makeNamespaceName(info.bridgeName, arg.getL3Network().getUuid());
                info.mtu = arg.getMtu();
                info.hostRoutes = getL3NetworkHostRoute(arg.getL3Network().getUuid());
                return info;
            }
        });
    }

    private void applyDhcpToHosts(List<DhcpInfo> dhcpInfo, final String hostUuid, final boolean rebuild, final Completion completion) {
        final Map<String, List<DhcpInfo>> l3DhcpMap = new HashMap<String, List<DhcpInfo>>();
        for (DhcpInfo d : dhcpInfo) {
            List<DhcpInfo> lst = l3DhcpMap.get(d.l3NetworkUuid);
            if (lst == null) {
                lst = new ArrayList<DhcpInfo>();
                l3DhcpMap.put(d.l3NetworkUuid, lst);
            }

            lst.add(d);
        }

        final Iterator<Map.Entry<String, List<DhcpInfo>>> it = l3DhcpMap.entrySet().iterator();
        class DhcpApply {
            void apply() {
                if (!it.hasNext()) {
                    completion.success();
                    return;
                }

                Map.Entry<String, List<DhcpInfo>> e = it.next();
                final String l3Uuid = e.getKey();
                final List<DhcpInfo> info = e.getValue();

                DebugUtils.Assert(!info.isEmpty(), "how can info be empty???");

                FlowChain chain = FlowChainBuilder.newShareFlowChain();
                chain.setName(String.format("flat-dhcp-provider-apply-dhcp-to-l3-network-%s", l3Uuid));
                chain.then(new ShareFlow() {
                    String dhcpServerIp;
                    String dhcpNetmask;
                    Integer prefixLen;

                    @Override
                    public void setup() {
                        flow(new NoRollbackFlow() {
                            String __name__ = "get-dhcp-server-ip";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                FlatDhcpAcquireDhcpServerIpMsg msg = new FlatDhcpAcquireDhcpServerIpMsg();
                                msg.setL3NetworkUuid(l3Uuid);
                                bus.makeTargetServiceIdByResourceUuid(msg, FlatNetworkServiceConstant.SERVICE_ID, l3Uuid);
                                bus.send(msg, new CloudBusCallBack(trigger) {
                                    @Override
                                    public void run(MessageReply reply) {
                                        if (!reply.isSuccess()) {
                                            trigger.fail(reply.getError());
                                        } else {
                                            FlatDhcpAcquireDhcpServerIpReply r = reply.castReply();
                                            dhcpServerIp = r.getIp();
                                            dhcpNetmask = r.getNetmask();
                                            prefixLen = r.getIpr().getPrefixLen();
                                            trigger.next();
                                        }
                                    }
                                });
                            }
                        });

                        flow(new NoRollbackFlow() {
                            String __name__ = "prepare-distributed-dhcp-server-on-host";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                DhcpInfo i = info.get(0);

                                PrepareDhcpCmd cmd = new PrepareDhcpCmd();
                                cmd.bridgeName = i.bridgeName;
                                cmd.namespaceName = i.namespaceName;
                                cmd.dhcpServerIp = dhcpServerIp;
                                cmd.dhcpNetmask = dhcpNetmask;
                                cmd.prefixLen = prefixLen;
                                cmd.ipVersion = i.ipVersion;

                                KVMHostAsyncHttpCallMsg msg = new KVMHostAsyncHttpCallMsg();
                                msg.setHostUuid(hostUuid);
                                msg.setNoStatusCheck(true);
                                msg.setCommand(cmd);
                                msg.setPath(PREPARE_DHCP_PATH);
                                msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
                                bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
                                bus.send(msg, new CloudBusCallBack(trigger) {
                                    @Override
                                    public void run(MessageReply reply) {
                                        if (!reply.isSuccess()) {
                                            trigger.fail(reply.getError());
                                            return;
                                        }

                                        KVMHostAsyncHttpCallReply ar = reply.castReply();
                                        PrepareDhcpRsp rsp = ar.toResponse(PrepareDhcpRsp.class);
                                        if (!rsp.isSuccess()) {
                                            trigger.fail(operr("operation error, because:%s", rsp.getError()));
                                            return;
                                        }

                                        trigger.next();
                                    }
                                });
                            }
                        });

                        flow(new NoRollbackFlow() {
                            String __name__ = "apply-dhcp";

                            @Override
                            public void run(final FlowTrigger trigger, Map data) {
                                ApplyDhcpCmd cmd = new ApplyDhcpCmd();
                                cmd.dhcp = info;
                                cmd.rebuild = rebuild;
                                cmd.l3NetworkUuid = l3Uuid;

                                KVMHostAsyncHttpCallMsg msg = new KVMHostAsyncHttpCallMsg();
                                msg.setCommand(cmd);
                                msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
                                msg.setHostUuid(hostUuid);
                                msg.setPath(APPLY_DHCP_PATH);
                                msg.setNoStatusCheck(true);
                                bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
                                bus.send(msg, new CloudBusCallBack(trigger) {
                                    @Override
                                    public void run(MessageReply reply) {
                                        if (!reply.isSuccess()) {
                                            trigger.fail(reply.getError());
                                            return;
                                        }

                                        KVMHostAsyncHttpCallReply r = reply.castReply();
                                        ApplyDhcpRsp rsp = r.toResponse(ApplyDhcpRsp.class);
                                        if (!rsp.isSuccess()) {
                                            trigger.fail(operr("operation error, because:%s", rsp.getError()));
                                            return;
                                        }

                                        trigger.next();
                                    }
                                });
                            }
                        });

                        done(new FlowDoneHandler(completion) {
                            @Override
                            public void handle(Map data) {
                                apply();
                            }
                        });

                        error(new FlowErrorHandler(completion) {
                            @Override
                            public void handle(ErrorCode errCode, Map data) {
                                completion.fail(errCode);
                            }
                        });
                    }
                }).start();
            }
        }

        new DhcpApply().apply();
    }

    @Override
    public void applyDhcpService(List<DhcpStruct> dhcpStructList, VmInstanceSpec spec, final Completion completion) {
        if (dhcpStructList.isEmpty()) {
            completion.success();
            return;
        }

        applyDhcpToHosts(toDhcpInfo(dhcpStructList), spec.getDestHost().getUuid(), false, completion);
    }

    private void releaseDhcpService(List<DhcpInfo> info, final String vmUuid, final String hostUuid, final NoErrorCompletion completion) {
        final ReleaseDhcpCmd cmd = new ReleaseDhcpCmd();
        cmd.dhcp = info;

        KVMHostAsyncHttpCallMsg msg = new KVMHostAsyncHttpCallMsg();
        msg.setCommand(cmd);
        msg.setCommandTimeout(timeoutMgr.getTimeout(cmd.getClass(), "5m"));
        msg.setHostUuid(hostUuid);
        msg.setPath(RELEASE_DHCP_PATH);
        bus.makeTargetServiceIdByResourceUuid(msg, HostConstant.SERVICE_ID, hostUuid);
        bus.send(msg, new CloudBusCallBack(completion) {
            @Override
            public void run(MessageReply reply) {
                if (!reply.isSuccess()) {
                    //TODO: Add GC and notification
                    logger.warn(String.format("failed to release dhcp%s for vm[uuid: %s] on the kvm host[uuid:%s]; %s",
                            cmd.dhcp, vmUuid, hostUuid, reply.getError()));
                    completion.done();
                    return;
                }

                KVMHostAsyncHttpCallReply r = reply.castReply();
                ReleaseDhcpRsp rsp = r.toResponse(ReleaseDhcpRsp.class);
                if (!rsp.isSuccess()) {
                    //TODO Add GC and notification
                    logger.warn(String.format("failed to release dhcp%s for vm[uuid: %s] on the kvm host[uuid:%s]; %s",
                            cmd.dhcp, vmUuid, hostUuid, rsp.getError()));
                    completion.done();
                    return;
                }

                completion.done();
            }
        });
    }

    @Override
    public void releaseDhcpService(List<DhcpStruct> dhcpStructsList, final VmInstanceSpec spec, final NoErrorCompletion completion) {
        if (dhcpStructsList.isEmpty()) {
            completion.done();
            return;
        }

        releaseDhcpService(toDhcpInfo(dhcpStructsList), spec.getVmInventory().getUuid(), spec.getDestHost().getUuid(), completion);
    }

    @Override
    public void vmDefaultL3NetworkChanged(VmInstanceInventory vm, String previousL3, String nowL3, final Completion completion) {
        DebugUtils.Assert(previousL3 != null || nowL3 != null, "why I get two NULL L3 networks!!!!");

        if (!VmInstanceState.Running.toString().equals(vm.getState())) {
            return;
        }

        VmNicInventory pnic = null;
        VmNicInventory nnic = null;

        for (VmNicInventory nic : vm.getVmNics()) {
            if (nic.getL3NetworkUuid().equals(previousL3)) {
                pnic = nic;
            } else if (nic.getL3NetworkUuid().equals(nowL3)) {
                nnic = nic;
            }
        }

        ResetDefaultGatewayCmd cmd = new ResetDefaultGatewayCmd();
        if (pnic != null) {
            cmd.gatewayToRemove = pnic.getGateway();
            cmd.macOfGatewayToRemove = pnic.getMac();
            cmd.bridgeNameOfGatewayToRemove = new BridgeNameFinder().findByL3Uuid(previousL3);
            cmd.namespaceNameOfGatewayToRemove = makeNamespaceName(cmd.bridgeNameOfGatewayToRemove, previousL3);
        }
        if (nnic != null) {
            cmd.gatewayToAdd = nnic.getGateway();
            cmd.macOfGatewayToAdd = nnic.getMac();
            cmd.bridgeNameOfGatewayToAdd = new BridgeNameFinder().findByL3Uuid(nowL3);
            cmd.namespaceNameOfGatewayToAdd = makeNamespaceName(cmd.bridgeNameOfGatewayToAdd, nowL3);
        }

        KvmCommandSender sender = new KvmCommandSender(vm.getHostUuid());
        sender.send(cmd, RESET_DEFAULT_GATEWAY_PATH, wrapper -> {
            ResetDefaultGatewayRsp rsp = wrapper.getResponse(ResetDefaultGatewayRsp.class);
            return rsp.isSuccess() ? null : operr("operation error, because:%s", rsp.getError());
        }, new ReturnValueCompletion<KvmResponseWrapper>(completion) {
            @Override
            public void success(KvmResponseWrapper returnValue) {
                completion.success();
            }

            @Override
            public void fail(ErrorCode errorCode) {
                completion.fail(errorCode);
            }
        });
    }

    private void applyDhcpToHosts(Iterator<Map.Entry<String, List<DhcpInfo>>> it, Completion completion) {
        if (!it.hasNext()) {
            completion.success();
            return;
        }

        Map.Entry<String, List<DhcpInfo>> e = it.next();
        final String hostUuid = e.getKey();
        final List<DhcpInfo> infos = e.getValue();
        if (infos == null || infos.isEmpty()) {
            applyDhcpToHosts(it, completion);
            return;
        }

        applyDhcpToHosts(infos, hostUuid, false, new Completion(completion) {
            @Override
            public void success() {
                applyDhcpToHosts(it, completion);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                applyDhcpToHosts(it, completion);
            }
        });
    }

    private void handle(L3NetworkUpdateDhcpMsg msg) {
        L3NetworkUpdateDhcpReply reply = new L3NetworkUpdateDhcpReply();

        Map<String, List<DhcpInfo>> l3DhcpMap = new HashMap<String, List<DhcpInfo>>();

        List<String> vmUuids = Q.New(VmNicVO.class).eq(VmNicVO_.l3NetworkUuid, msg.getL3NetworkUuid()).select(VmNicVO_.vmInstanceUuid)
                .groupBy(VmNicVO_.vmInstanceUuid).listValues();

        for (String uuid: vmUuids) {
            VmInstanceInventory vm = VmInstanceInventory.valueOf(dbf.findByUuid(uuid, VmInstanceVO.class));
            if (!vm.getState().equals(VmInstanceState.Running.toString()) || vm.getHostUuid() == null) {
                continue;
            }

            String hostUuid = vm.getHostUuid();
            List<DhcpInfo> hostInfo = l3DhcpMap.computeIfAbsent(hostUuid, k -> new ArrayList<>());
            hostInfo.addAll(getVmDhcpInfo(vm, msg.getL3NetworkUuid()));
        }

        applyDhcpToHosts(l3DhcpMap.entrySet().iterator(), new Completion(msg) {
            @Override
            public void success() {
                bus.reply(msg, reply);
            }

            @Override
            public void fail(ErrorCode errorCode) {
                reply.setError(errorCode);
                bus.reply(msg, reply);
            }
        });
    }
}
