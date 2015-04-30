package com.vmlab.vms;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VirtualMachinesInventory {
    private List<VirtualMachineThread> virtualMachineThreads;
    private Integer numberOfVms;

    public VirtualMachinesInventory(Integer numberOfVms) {
        this.numberOfVms = numberOfVms;
        this.virtualMachineThreads = new ArrayList<VirtualMachineThread>(numberOfVms);
    }

    public void initVms() {
        for (int i = 0; i < numberOfVms; i++) {
            VirtualMachineThread virtualMachineThread = new VirtualMachineThread();
            virtualMachineThreads.add(virtualMachineThread);
            virtualMachineThread.start();
        }
    }

    public List<VirtualMachineThread> getVirtualMachineThreads() {
        return virtualMachineThreads;
    }

    public Integer getNumberOfVms() {
        return numberOfVms;
    }
}
