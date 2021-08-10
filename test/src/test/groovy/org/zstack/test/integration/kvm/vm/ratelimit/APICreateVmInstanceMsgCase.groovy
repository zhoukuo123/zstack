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


class APICreateVmInstanceMsgCase extends SubCase {
    EnvSpec env

    def DOC = """
test a VM's start/stop/reboot/destroy/recover operations 
"""

    @Override
    void setup() {
        useSpring(KvmTest.springSpec)
    }

    @Override
    void environment() {
        CloudBusGlobalConfig.STATISTICS_ON.updateValue(true)

        env = Env.oneVmBasicEnv()
    }

    @Override
    void test() {
        env.create {
            testStopVm()
//            testStartVm()
        }
    }

    void testStartVm() {
        VmSpec spec = env.specByName("vm")

        KVMAgentCommands.StartVmCmd cmd = null

        env.afterSimulator(KVMConstant.KVM_START_VM_PATH) { rsp, HttpEntity<String> e ->
            cmd = JSONObjectUtil.toObject(e.body, KVMAgentCommands.StartVmCmd.class)
            return rsp
        }

        VmInstanceInventory inv = startVmInstance {
            uuid = spec.inventory.uuid
        }

        assert cmd != null
        assert cmd.vmInstanceUuid == spec.inventory.uuid
        assert inv.state == VmInstanceState.Running.toString()
        assert cmd.chassisAssetTag == "www.zstack.io"

        VmInstanceVO vmvo = dbFindByUuid(cmd.vmInstanceUuid, VmInstanceVO.class)
        assert vmvo.state == VmInstanceState.Running
        assert cmd.vmInternalId == vmvo.internalId
        assert cmd.vmName == vmvo.name
        assert cmd.memory == vmvo.memorySize
        assert cmd.cpuNum == vmvo.cpuNum

        String tag = VmSystemTags.VM_SYSTEM_SERIAL_NUMBER.getTag(spec.inventory.uuid)
        assert tag != null


        //TODO: test socketNum, cpuOnSocket
        assert cmd.rootVolume.installPath == vmvo.rootVolume.installPath
        assert cmd.rootVolume.useVirtio
        vmvo.vmNics.each { nic ->
            KVMAgentCommands.NicTO to = cmd.nics.find { nic.mac == it.mac }
            assert to != null: "unable to find the nic[mac:${nic.mac}]"
            assert to.deviceId == nic.deviceId
            assert to.useVirtio
            assert to.nicInternalName == nic.internalName
        }
    }

    void testStopVm() {
        VmSpec spec = env.specByName("vm")

        KVMAgentCommands.StopVmCmd cmd = null

        env.afterSimulator(KVMConstant.KVM_STOP_VM_PATH) { rsp, HttpEntity<String> e ->
            cmd = JSONObjectUtil.toObject(e.body, KVMAgentCommands.StopVmCmd.class)
            return rsp
        }

        VmInstanceInventory inv = stopVmInstance {
            uuid = spec.inventory.uuid
        }

        assert inv.state == VmInstanceState.Stopped.toString()

        assert cmd != null
        assert cmd.uuid == spec.inventory.uuid

        def vmvo = dbFindByUuid(cmd.uuid, VmInstanceVO.class)
        assert vmvo.state == VmInstanceState.Stopped
    }

    @Override
    void clean() {
//        env.delete()
    }

}
