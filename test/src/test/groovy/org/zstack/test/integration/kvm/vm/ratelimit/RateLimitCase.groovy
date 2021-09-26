package org.zstack.test.integration.kvm.vm.ratelimit

import org.zstack.test.integration.kvm.Env
import org.zstack.test.integration.kvm.KvmTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.sdk.*
import org.zstack.core.ratelimit.RateLimitGlobalConfig

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class RateLimitCase extends SubCase {
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
            testRebootVmRateLimit(n)
        }
    }

    void testRebootVmRateLimit(int n) {
        VmInstanceInventory vm = env.inventoryByName("vm")
        def count = new AtomicInteger(0)
        def passedCount = new AtomicInteger(0)
        def limitedCount = new AtomicInteger(0)

        for (int i = 0; i < n; i++) {
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

        while (count.get() < n) {
            TimeUnit.SECONDS.sleep(1)
        }

        assert passedCount.intValue() != n

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))

        def lastPassedCount = passedCount.intValue()

        RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.updateValue((RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class) * 2))

        passedCount.set(0)
        limitedCount.set(0)
        count.set(0)

        for (int i = 0; i < n; i++) {
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

        while (count.get() < n) {
            TimeUnit.SECONDS.sleep(1)
        }

        assert passedCount.intValue() != n
        assert (passedCount.intValue() < lastPassedCount * 1.5 + 400) && (passedCount.intValue() > lastPassedCount * 1.5 - 400)

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
    }
}
