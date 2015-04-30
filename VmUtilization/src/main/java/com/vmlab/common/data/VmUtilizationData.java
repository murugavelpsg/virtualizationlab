package com.vmlab.common.data;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VmUtilizationData {
    private Integer vmId;
    private CpuUtilizationData cpuUtilizationData;
    private MemoryUtilizationData memoryUtilizationData;
    private DiskUtilizationData diskUtilizationData;

    public Integer getVmId() {
        return vmId;
    }

    public void setVmId(Integer vmId) {
        this.vmId = vmId;
    }

    public CpuUtilizationData getCpuUtilizationData() {
        return cpuUtilizationData;
    }

    public void setCpuUtilizationData(CpuUtilizationData cpuUtilizationData) {
        this.cpuUtilizationData = cpuUtilizationData;
    }

    public MemoryUtilizationData getMemoryUtilizationData() {
        return memoryUtilizationData;
    }

    public void setMemoryUtilizationData(MemoryUtilizationData memoryUtilizationData) {
        this.memoryUtilizationData = memoryUtilizationData;
    }

    public DiskUtilizationData getDiskUtilizationData() {
        return diskUtilizationData;
    }

    public void setDiskUtilizationData(DiskUtilizationData diskUtilizationData) {
        this.diskUtilizationData = diskUtilizationData;
    }
}
