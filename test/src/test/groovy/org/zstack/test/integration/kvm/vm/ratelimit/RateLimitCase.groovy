package org.zstack.test.integration.kvm.vm.ratelimit

import org.zstack.test.integration.kvm.Env
import org.zstack.test.integration.kvm.KvmTest
import org.zstack.testlib.EnvSpec
import org.zstack.testlib.SubCase
import org.zstack.sdk.*
import org.zstack.core.ratelimit.RateLimitGlobalConfig
import org.zstack.utils.Utils
import org.zstack.utils.stopwatch.StopWatch

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

        StopWatch sw = Utils.getStopWatch()
        sw.start()
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
        sw.stop()

        while (count.get() < n) {
            TimeUnit.SECONDS.sleep(1)
        }

        def useTime = sw.getLapse() / 1000.0
        def LAST_MSG_QPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)
        def MSG_TOTAL = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_TOTAL.value(Integer.class)
        def TEST_MSG_QPS = (passedCount.intValue() - MSG_TOTAL) / useTime

        assert passedCount.intValue() != n
        assert Math.abs(TEST_MSG_QPS - LAST_MSG_QPS) < 0.2

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
        logger.info(String.format("The test message QPS is $TEST_MSG_QPS", TEST_MSG_QPS))

        RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.updateValue((RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class) * 2))

        passedCount.set(0)
        limitedCount.set(0)
        count.set(0)

        sw.start()
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
        sw.stop()

        while (count.get() < n) {
            TimeUnit.SECONDS.sleep(1)
        }

        def newUseTime = sw.getLapse() / 1000.0
        def NEW_MSG_QPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)
        def NEW_TEST_MSG_QPS = (passedCount.intValue() - MSG_TOTAL) / newUseTime

        assert passedCount.intValue() != n
        assert Math.abs(NEW_TEST_MSG_QPS - NEW_MSG_QPS) < 0.2

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
        logger.info(String.format("The test message QPS is $TEST_MSG_QPS", TEST_MSG_QPS))
    }
}
