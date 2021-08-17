package org.zstack.test.integration.kvm.vm.ratelimit

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
