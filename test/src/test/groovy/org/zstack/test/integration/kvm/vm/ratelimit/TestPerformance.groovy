package org.zstack.test.integration.kvm.vm.ratelimit


import org.zstack.testlib.EnvSpec
import org.zstack.testlib.VmSpec
import org.zstack.sdk.*


public class TestPerformance implements Runnable {
    EnvSpec env

    public TestPerformance(EnvSpec env) {
        this.env = env
    }

    @Override
    public void run() {

        VmSpec spec = env.specByName("vm")

        VmInstanceInventory inv = stopVmInstance {
            uuid = spec.inventory.uuid
        }

    }
}
