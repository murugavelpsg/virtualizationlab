package com.vmlab.vmutilizationapp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmlab.common.data.VmUtilizationData;
import com.vmlab.common.global.Constants;
import com.vmlab.vms.VmUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by msivagna on 4/27/2015.
 */
public class VmDataCollectorWorkerThread implements Runnable {
    private VmDataCollectorMaster vmDataCollectorMaster;
    private Gson gson;

    public VmDataCollectorWorkerThread(VmDataCollectorMaster vmDataCollectorMaster) {
        this.vmDataCollectorMaster = vmDataCollectorMaster;
        gson = new GsonBuilder().create();
    }

    @Override
    public void run() {
        Integer vmId = 0;
        try {
            while((vmId = vmDataCollectorMaster.getNextReadyTask()) != -1) {
                collectVmData(vmId);
            }
        } catch (InterruptedException e) {
            System.out.println("Worker thread finishes because of interrupted exception.");
            e.printStackTrace();
        }
    }

    private void collectVmData(Integer vmId) {
        try {
            String response = VmUtils.sendMessageToVm(vmId, Constants.VM_GET_UTILIZATION);
            VmUtilizationData vmUtilizationData = gson.fromJson(response, VmUtilizationData.class);
            synchronized (vmDataCollectorMaster.getVmUtilizationTable()) {
                ArrayList<VmUtilizationData> vmUtilizationDatas =
                        vmDataCollectorMaster.getVmUtilizationTable().get(vmId);
                vmUtilizationDatas.add(vmUtilizationData);
                vmDataCollectorMaster.getVmUtilizationTable().put(vmId, vmUtilizationDatas);
            }
        } catch (IOException e) {
            System.out.println("Cannot communicate with VM = " + vmId);
        }
    }
}
