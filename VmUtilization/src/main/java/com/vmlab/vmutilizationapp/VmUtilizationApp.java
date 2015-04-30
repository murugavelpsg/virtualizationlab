package com.vmlab.vmutilizationapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmlab.common.data.CpuUtilizationData;
import com.vmlab.common.data.DiskUtilizationData;
import com.vmlab.common.data.MemoryUtilizationData;
import com.vmlab.common.data.VmUtilizationData;

import java.util.*;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VmUtilizationApp {
    private Integer numberOfVms;
    private Long programTimeoutSec;
    private Double cpuUtilizationThreshold;
    private Double memoryUtilizationThreshold;
    private Double diskUtilizationThreshold;
    Gson gson;

    public VmUtilizationApp(Integer numberOfVms, Long programTimeoutSec, Double cpuUtilizationThreshold,
        Double memoryUtilizationThreshold, Double diskUtilizationThreshold) {
        this.numberOfVms = numberOfVms;
        this.programTimeoutSec = programTimeoutSec;
        this.cpuUtilizationThreshold = cpuUtilizationThreshold;
        this.memoryUtilizationThreshold = memoryUtilizationThreshold;
        this.diskUtilizationThreshold = diskUtilizationThreshold;
        gson = new GsonBuilder().create();
    }

    public List<VmUtilizationData> getUnderUtilizedVms() {
        // Collect VM Data every minute
        VmDataCollectorMaster vmDataCollectorMaster = new VmDataCollectorMaster(numberOfVms, programTimeoutSec);
        Map<Integer, ArrayList<VmUtilizationData>> vmUtilizationMap = vmDataCollectorMaster.getVmData();
        return findUnderUtilizedVms(vmUtilizationMap);
    }

    private List<VmUtilizationData> findUnderUtilizedVms(Map<Integer, ArrayList<VmUtilizationData>> vmUtilizationMap) {
        List<VmUtilizationData> underUtilizedVms = new ArrayList<>();
        Iterator it = vmUtilizationMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            Integer vmId = (Integer) pair.getKey();
            List<VmUtilizationData> vmUtilizationDatas = (List<VmUtilizationData>) pair.getValue();
            VmUtilizationData netVmUtilization = calculateNetVmUtilization(vmId, vmUtilizationDatas);
            if (isUnderUtilizedVm(netVmUtilization)) {
                underUtilizedVms.add(netVmUtilization);
            }
            it.remove();
        }
        return underUtilizedVms;
    }

    private boolean isUnderUtilizedVm(VmUtilizationData netVmUtilization) {
        if (netVmUtilization.getCpuUtilizationData().getCpuUtilization() < cpuUtilizationThreshold &&
                netVmUtilization.getDiskUtilizationData().getDiskUtilization() < diskUtilizationThreshold &&
                netVmUtilization.getMemoryUtilizationData().getMemoryUtilization() < memoryUtilizationThreshold) {
            return true;
        }
        return false;
    }

    private VmUtilizationData calculateNetVmUtilization(Integer vmId, List<VmUtilizationData> vmUtilizationDatas) {
        Double totalCpuUtilization = 0.0;
        Double totalDiskUtilization = 0.0;
        Double totalMemoryUtilization = 0.0;
        Double totalSamples = new Double(vmUtilizationDatas.size());
        for (VmUtilizationData vmUtilizationData : vmUtilizationDatas) {
            totalCpuUtilization = totalCpuUtilization
                    + vmUtilizationData.getCpuUtilizationData().getCpuUtilization();
            totalDiskUtilization = totalDiskUtilization
                    + vmUtilizationData.getDiskUtilizationData().getDiskUtilization();
            totalMemoryUtilization = totalMemoryUtilization
                    + vmUtilizationData.getMemoryUtilizationData().getMemoryUtilization();
        }
        VmUtilizationData netVmUtilizationData = new VmUtilizationData();
        netVmUtilizationData.setVmId(vmId);
        CpuUtilizationData netCpuUtilizationData = new CpuUtilizationData();
        netCpuUtilizationData.setCpuUtilization(totalCpuUtilization/totalSamples);
        DiskUtilizationData netDiskUtilizationData = new DiskUtilizationData();
        netDiskUtilizationData.setDiskUtilization(totalDiskUtilization/totalSamples);
        MemoryUtilizationData netMemoryUtilizationData = new MemoryUtilizationData();
        netMemoryUtilizationData.setMemoryUtilization(totalMemoryUtilization/totalSamples);
        netVmUtilizationData.setCpuUtilizationData(netCpuUtilizationData);
        netVmUtilizationData.setDiskUtilizationData(netDiskUtilizationData);
        netVmUtilizationData.setMemoryUtilizationData(netMemoryUtilizationData);
        return netVmUtilizationData;
    }

    public Integer getNumberOfVms() {
        return numberOfVms;
    }

    public void setNumberOfVms(Integer numberOfVms) {
        this.numberOfVms = numberOfVms;
    }

    public Long getProgramTimeoutSec() {
        return programTimeoutSec;
    }

    public void setProgramTimeoutSec(Long programTimeoutSec) {
        this.programTimeoutSec = programTimeoutSec;
    }

    public Double getCpuUtilizationThreshold() {
        return cpuUtilizationThreshold;
    }

    public void setCpuUtilizationThreshold(Double cpuUtilizationThreshold) {
        this.cpuUtilizationThreshold = cpuUtilizationThreshold;
    }

    public Double getMemoryUtilizationThreshold() {
        return memoryUtilizationThreshold;
    }

    public void setMemoryUtilizationThreshold(Double memoryUtilizationThreshold) {
        this.memoryUtilizationThreshold = memoryUtilizationThreshold;
    }

    public Double getDiskUtilizationThreshold() {
        return diskUtilizationThreshold;
    }

    public void setDiskUtilizationThreshold(Double diskUtilizationThreshold) {
        this.diskUtilizationThreshold = diskUtilizationThreshold;
    }
}
