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

import java.util.concurrent.CountDownLatch


public class TestPerformance implements Runnable {
    EnvSpec env
    CountDownLatch latch

    public TestPerformance(EnvSpec env, CountDownLatch latch) {
        this.env = env
        this.latch = latch
    }

    @Override
    public void run() {
        VmSpec spec = env.specByName("vm")

        VmInstanceInventory inv = rebootVmInstance {
            uuid = spec.inventory.uuid
        }

        latch.countDown()
    }
}
