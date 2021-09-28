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
            int n = 2000
            testRebootVmRateLimit(n)
            testRateLimitSwitch(n)
        }
    }

    void testRateLimitSwitch(int n) {
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
        def testPassedCount = calculatePassedCount(useTime)
        def testApiMsgQPS = calculateQPS(passedCount, useTime)
        def apiMsgQPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)

        assert passedCount.intValue() < testPassedCount + 30 && passedCount.intValue() > testPassedCount - 30
        assert Math.abs(testApiMsgQPS - apiMsgQPS) < 0.2

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
        logger.info(String.format("The test message QPS is $testApiMsgQPS", testApiMsgQPS))

        RateLimitGlobalConfig.RATE_LIMIT_SWITCH.updateValue("false")

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

        assert passedCount.intValue() == n

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
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
        def testPassedCount = calculatePassedCount(useTime)
        def testApiMsgQPS = calculateQPS(passedCount, useTime)
        def apiMsgQPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)

        assert passedCount.intValue() < testPassedCount + 30 && passedCount.intValue() > testPassedCount - 30
        assert Math.abs(testApiMsgQPS - apiMsgQPS) < 0.2

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
        logger.info(String.format("The test message QPS is $testApiMsgQPS", testApiMsgQPS))

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
        def newTestPassedCount = calculatePassedCount(newUseTime)
        def newTestApiMsgQPS = calculateQPS(passedCount, newUseTime)
        def newApiMsgQPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)

        assert passedCount.intValue() < newTestPassedCount + 30 && passedCount.intValue() > newTestPassedCount - 30
        assert Math.abs(newTestApiMsgQPS - newApiMsgQPS) < 0.2

        logger.info(String.format("Passed $passedCount APIs", passedCount))
        logger.info(String.format("Limited to $limitedCount APIs", limitedCount))
        logger.info(String.format("The test message QPS is $newTestApiMsgQPS", newTestApiMsgQPS))
    }

    Object calculatePassedCount(Object useTime) {
        def apiMsgQPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)
        def apiMsgCapacity = apiMsgQPS
        def testPassedCount = (apiMsgQPS * useTime < apiMsgCapacity)
                ? apiMsgQPS * useTime
                : apiMsgQPS * useTime + apiMsgCapacity
        return testPassedCount
    }

    Object calculateQPS(AtomicInteger passedCount, Object useTime) {
        def apiMsgQPS = RateLimitGlobalConfig.API_ASYNC_CALL_MSG_QPS.value(Integer.class)
        def apiMsgCapacity = apiMsgQPS
        def testMsgQPS
        if (passedCount.intValue() > apiMsgCapacity) {
            testMsgQPS = (passedCount.intValue() - apiMsgCapacity) / useTime
        } else {
            testMsgQPS = passedCount.intValue() / useTime
        }
        return testMsgQPS
    }
}
