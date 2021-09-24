package org.zstack.test.integration.kvm.vm.ratelimit

import org.zstack.test.integration.kvm.Env
import org.zstack.test.integration.kvm.KvmTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.sdk.*

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class APIRebootVmMsgRateLimitCase extends SubCase {
    EnvSpec env

    @Override
    void clean() {
        env.delete()
    }

    @Override
    void setup() {
        useSpring(KvmTest.springSpec)
        spring {
            include("ratelimit.xml")
        }
    }

    @Override
    void environment() {
        env = Env.oneVmBasicEnv()
    }

    @Override
    void test() {
        env.create {
            int n = 10000
            testRebootVMRateLimit(n)
        }
    }

    void testRebootVMRateLimit(int numberOfVM) {
        VmInstanceInventory vm = env.inventoryByName("vm")
        def count = new AtomicInteger(0)
        def passedCount = new AtomicInteger(0)
        def limitedCount = new AtomicInteger(0)

        for (int i = 0; i < numberOfVM; i++) {
            new RebootVmInstanceAction(
                    uuid: vm.uuid,
                    sessionId: adminSession(),
            ).call(new Completion<RebootVmInstanceAction.Result>() {
                @Override
                void complete(RebootVmInstanceAction.Result ret) {
                    count.incrementAndGet()
                    if (ret.error == null) {
                        passedCount.incrementAndGet()
                    } else {
                        limitedCount.incrementAndGet()
                    }
                }
            })
        }

        while (count.get() < numberOfVM) {
            TimeUnit.SECONDS.sleep(1)
        }

        logger.info(String.format("passed $passedCount API", passedCount))
        logger.info(String.format("limited $limitedCount API", limitedCount))
    }
}
