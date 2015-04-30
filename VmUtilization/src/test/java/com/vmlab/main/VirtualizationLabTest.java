package com.vmlab.main;

import com.vmlab.common.data.VmUtilizationData;
import com.vmlab.common.exceptions.InvalidInputException;

import java.util.List;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VirtualizationLabTest {
    public static void main(String[] args) {
        try {
            List<VmUtilizationData> underUtilizedVms = runVirtualizationLabTest();
        } catch (InvalidInputException e) {
            e.printStackTrace();
        }
    }

    private static List<VmUtilizationData> runVirtualizationLabTest() throws InvalidInputException {
        Integer numberOfVms = new Integer(30);
        Long programTimeout = new Long(10);
        Double cpuUtilizationThreshold = new Double(0.35);
        Double memoryUtilizationThreshold = new Double(0.35);
        Double diskUtilizationThreshold = new Double(0.35);
        VirtualizationLab virtualizationLab = new VirtualizationLab();
        List<VmUtilizationData> underUtilizedVms = virtualizationLab.runLab(numberOfVms, programTimeout, cpuUtilizationThreshold,
                memoryUtilizationThreshold, diskUtilizationThreshold);
        return underUtilizedVms;
    }
}
