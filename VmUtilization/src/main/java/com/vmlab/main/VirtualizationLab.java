package com.vmlab.main;

import com.vmlab.common.data.VmUtilizationData;
import com.vmlab.common.exceptions.InvalidInputException;
import com.vmlab.common.global.Constants;
import com.vmlab.vmutilizationapp.VmUtilizationApp;
import com.vmlab.vms.VirtualMachineThread;
import com.vmlab.vms.VirtualMachinesInventory;
import com.vmlab.vms.VmUtils;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.util.List;

/**
 * <h1> VirtualizationLab </h1>
 * <p> Virtualization lab creates the VMs, collects and prints the underutilized VMs
 * and gracefully shuts down all the VMs created </p>
 */
public class VirtualizationLab {
    public VirtualizationLab() {
    }

    /**
     *
     * @param numberOfVms Number of VMs
     * @param programTimeout Amount of time (In seconds) you want the program to run
     * @param cpuUtilizationThreshold CPU Utilization Threshold between 0.0 and 1.0
     * @param memoryUtilizationThreshold Memory Utilization Threshold between 0.0 and 1.0
     * @param diskUtilizationThreshold Disk Utilization Threshold between 0.0 and 1.0
     * @return Returns list of under-utilized VMs as list of VmUtilizationData
     */
    public List<VmUtilizationData> runLab(Integer numberOfVms, Long programTimeout, Double cpuUtilizationThreshold,
                                          Double memoryUtilizationThreshold, Double diskUtilizationThreshold) throws InvalidInputException {
        validateInputData(cpuUtilizationThreshold, memoryUtilizationThreshold, diskUtilizationThreshold);
        System.out.println("Finding under-utilized VMs... Please wait " + programTimeout + " seconds...");
        VirtualMachinesInventory virtualMachinesInventory = initializeVms(numberOfVms);
        VmUtilizationApp vmUtilizationApp = new VmUtilizationApp(numberOfVms, programTimeout, cpuUtilizationThreshold,
                memoryUtilizationThreshold, diskUtilizationThreshold);
        List<VmUtilizationData> underUtilizedVms = vmUtilizationApp.getUnderUtilizedVms();
        printUnderUtilizedVms(underUtilizedVms);
        shutDownVms(virtualMachinesInventory);
        System.out.println("Program exited successfully");
        return underUtilizedVms;
    }

    private void validateInputData(Double cpuUtilizationThreshold, Double memoryUtilizationThreshold,
                                   Double diskUtilizationThreshold) throws InvalidInputException {
        if (!isValidateThreshold(cpuUtilizationThreshold)) {
            throw new InvalidInputException("Invalid CPU Threshold. Please provide a value between 0.0 and 1.0");
        }
        if (!isValidateThreshold(memoryUtilizationThreshold)) {
            throw new InvalidInputException("Invalid Memory Threshold. Please provide a value between 0.0 and 1.0");
        }
        if (!isValidateThreshold(diskUtilizationThreshold)) {
            throw new InvalidInputException("Invalid Disk Threshold. Please provide a value between 0.0 and 1.0");
        }
    }

    private boolean isValidateThreshold(Double threshold) {
        if (threshold < 0.0 || threshold > 1.0) {
            return false;
        }
        return true;
    }

    private void printUnderUtilizedVms(List<VmUtilizationData> underUtilizedVms) {
        System.out.println("Under Utilized Vms are: ");
        if (underUtilizedVms.size() > 0) {
            for (VmUtilizationData underUtilizedVm : underUtilizedVms) {
                System.out.println("=======================================================");
                System.out.println("VM_ID: " + underUtilizedVm.getVmId());
                System.out.println("CPU Utilization = " + Double.toString(
                        underUtilizedVm.getCpuUtilizationData().getCpuUtilization()));
                System.out.println("Memory Utilization = " + Double.toString(
                        underUtilizedVm.getMemoryUtilizationData().getMemoryUtilization()));
                System.out.println("Disk Utilization = " + Double.toString(
                        underUtilizedVm.getDiskUtilizationData().getDiskUtilization()));
            }
        } else {
            System.out.println("No Under-utilized VMs in the cluster");
        }
    }

    private VirtualMachinesInventory initializeVms(Integer numberOfVms) {
        //Initialize VMs
        VirtualMachinesInventory virtualMachinesInventory = new VirtualMachinesInventory(numberOfVms);
        virtualMachinesInventory.initVms();
        return virtualMachinesInventory;
    }

    private static void shutDownVms(VirtualMachinesInventory virtualMachinesInventory) {
        //Send shutdown signal to the VMThreads
        for (int i = 0; i < virtualMachinesInventory.getNumberOfVms(); i++) {
            int vmPortId = i + Constants.BEGINNING_PORT;
            try {
                String response = VmUtils.sendMessageToVm(vmPortId, Constants.VM_SHUTDOWN);
            } catch (IOException e) {
                System.out.println("Failed to shutdown VM at port " + vmPortId);
            }
        }
        //Wait for the VMThreads to finish
        List<VirtualMachineThread> virtualMachineThreads = virtualMachinesInventory.getVirtualMachineThreads();
        for (VirtualMachineThread virtualMachineThread : virtualMachineThreads) {
            try {
                virtualMachineThread.join();
            } catch (InterruptedException e) {
                System.out.println("Caught Interrupted while waiting for VM threads to finish");
            }
        }
    }
}
