package com.vmlab.vms;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmlab.common.data.CpuUtilizationData;
import com.vmlab.common.data.DiskUtilizationData;
import com.vmlab.common.data.MemoryUtilizationData;
import com.vmlab.common.data.VmUtilizationData;
import com.vmlab.common.global.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VirtualMachineThread extends Thread {
    private volatile boolean isShutDownVM;
    private Integer portAddress;

    public VirtualMachineThread() {
        this.isShutDownVM = false;
        this.portAddress = VmUtils.portAddress.getAndIncrement();
    }

    @Override
    public void run() {
        try {
            runServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runServer() throws IOException {
        //Create a server socket
        ServerSocket listener = new ServerSocket(portAddress);
        try {
            while (!isShutDownVM) {
                //Block on accept
                Socket socket = listener.accept();
                handleIncomingRequest(socket);
            }
        } finally {
            listener.close();
        }
    }

    private void handleIncomingRequest(Socket socket) {
        try {
            String response = null;
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            //If the incoming request is
            //  - GET_UTILIZATION = then send random values
            //  - SHUTDOWN = the shutdown the VM
            String command = in.readLine();
            if (command.equals(Constants.VM_GET_UTILIZATION)) {
                //Generate random values for VmUtilization
                VmUtilizationData vmUtilizationData = generateRandomVmUtilizationData();
                //Serialize the object using gson and send it to the client
                Gson gson = new GsonBuilder().create();
                response = gson.toJson(vmUtilizationData);
            } else if (command.equals(Constants.VM_SHUTDOWN)) {
                response = Constants.VM_SHUTDOWN_RESPONSE;
                isShutDownVM = true;
            } else {
                //Incorrect command. As the user to try again
                //Send an error code
                response = Constants.INVALID_REQUEST;
            }
            //Write the response back to the user
            out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    private VmUtilizationData generateRandomVmUtilizationData() {
        VmUtilizationData vmUtilizationData = new VmUtilizationData();
        vmUtilizationData.setVmId(portAddress);
        CpuUtilizationData cpuUtilizationData = new CpuUtilizationData();
        DiskUtilizationData diskUtilizationData = new DiskUtilizationData();
        MemoryUtilizationData memoryUtilizationData = new MemoryUtilizationData();
        cpuUtilizationData.setCpuUtilization(getRandomUtilization());
        diskUtilizationData.setDiskUtilization(getRandomUtilization());
        memoryUtilizationData.setMemoryUtilization(getRandomUtilization());
        vmUtilizationData.setCpuUtilizationData(cpuUtilizationData);
        vmUtilizationData.setDiskUtilizationData(diskUtilizationData);
        vmUtilizationData.setMemoryUtilizationData(memoryUtilizationData);
        return vmUtilizationData;
    }

    private Double getRandomUtilization() {
        Random rand = new Random();
        Double randomNumber = new Double(rand.nextInt((Constants.MAX_THRESHOLD - Constants.MIN_THRESHOLD) + 1)
                + Constants.MIN_THRESHOLD);
        return (randomNumber / 100.0);
    }
}
