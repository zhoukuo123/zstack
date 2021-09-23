package org.zstack.test.integration.kvm.vm.ratelimit

import org.springframework.http.HttpEntity
import org.zstack.compute.vm.VmSystemTags
import org.zstack.core.cloudbus.CloudBusGlobalConfig
import org.zstack.core.db.Q
import org.zstack.header.image.ImageConstant
import org.zstack.header.vm.VmCreationStrategy
import org.zstack.header.vm.VmInstanceEO
import org.zstack.header.vm.VmInstanceEO_
import org.zstack.header.vm.VmInstanceState
import org.zstack.header.vm.VmInstanceVO
import org.zstack.header.volume.VolumeEO
import org.zstack.header.volume.VolumeVO_
import org.zstack.kvm.KVMAgentCommands
import org.zstack.kvm.KVMConstant
import org.zstack.network.l3.NetworkGlobalProperty
import org.zstack.sdk.*
import org.zstack.test.integration.kvm.Env
import org.zstack.test.integration.kvm.KvmTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.testlib.VmSpec
import org.zstack.utils.gson.JSONObjectUtil

import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class APICreateVmInstanceMsgCase extends SubCase {
    EnvSpec env

    @Override
    void setup() {
        useSpring(KvmTest.springSpec)
        spring {
            include("ratelimit.xml")
        }
    }

    @Override
    void environment() {
        CloudBusGlobalConfig.STATISTICS_ON.updateValue(true)

        env = Env.oneVmBasicEnv()
    }

    @Override
    void test() {
        env.create {
            int n = 50;
            def l3 = env.inventoryByName("l3") as L3NetworkInventory
            StopWatch sw = Utils.getStopWatch()
            try {
                sw.start()
                testRebootVMConcurrently(n, l3)
            } finally {
                sw.stop()
                logger.info(String.format("XXX: Rebooting %d VMs costs %d seconds", n, sw.getLapse(TimeUnit.SECONDS)))
            }
        }
    }

    void testRebootVMConcurrently(int numberOfVM, String l3Uuid) {
        def instanceOffering = env.inventoryByName("instanceOffering") as InstanceOfferingInventory
        def image = env.inventoryByName("image") as ImageInventory

        def existingVM = Q.New(VmInstanceVO.class).eq(VmInstanceVO_.state, VmInstanceState.Running).count()
        def count = new AtomicInteger(0)

        logger.info("XXX: rebooting $numberOfVM VMs ...")
        for (int i = 0; i < numberOfVM; i++) {
            new RebootVmInstanceAction(
                    name: "test-vm",
                    instanceOfferingUuid: instanceOffering.uuid,
                    imageUuid: image.uuid,
                    l3NetworkUuids: [l3.uuid],
                    systemTags: ["createWithoutCdRom::true"],
                    sessionId: adminSession(),
            ).call(new Completion<RebootVmInstanceAction.Result>() {
                @Override
                void complete(RebootVmInstanceAction.Result ret) {
                    count.incrementAndGet()
                    if (ret.error == null) {
                        def uuid = ret.value.inventory.uuid
                        assert Q.New(VmInstanceVO.class).eq(VmInstanceVO_.uuid, uuid).eq(VmInstanceVO_.state, VmInstanceState.Running).isExists()
                    }
                }
            })
        }

        while (count.get() < numberOfVM) {
            TimeUnit.SECONDS.sleep(1)
        }

        logger.info("XXX: rebooted $numberOfVM VMs ...")
        assert Q.New(VmInstanceVO.class).eq(VmInstanceVO_.type, "UserVm").eq(VmInstanceVO_.state, VmInstanceState.Running).count() == existingVM + numberOfVM
    }

    @Override
    void clean() {
        env.delete()
    }
}
