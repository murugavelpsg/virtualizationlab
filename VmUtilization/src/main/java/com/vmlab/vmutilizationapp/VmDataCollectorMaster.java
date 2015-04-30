package com.vmlab.vmutilizationapp;

import com.vmlab.common.data.VmUtilizationData;
import com.vmlab.common.global.Constants;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by msivagna on 4/27/2015.
 */
public class VmDataCollectorMaster {
    private Map<Integer, ArrayList<VmUtilizationData>> vmUtilizationTable;
    private Queue<Integer> taskQueue;
    private Integer lock = 1;
    private Long programTimeoutSec;
    private Integer numberOfVms;

    public VmDataCollectorMaster(Integer numberOfVms, Long programTimeoutSec) {
        this.taskQueue = new LinkedList<>();
        this.programTimeoutSec = programTimeoutSec;
        this.numberOfVms = numberOfVms;
        initializeVmUtilizationTable();
    }

    private void initializeVmUtilizationTable() {
        this.vmUtilizationTable = new HashMap<>();
        int vmId = 0;
        for (int i = 0; i < numberOfVms; i++) {
            vmId = Constants.BEGINNING_PORT + i;
            vmUtilizationTable.put(vmId, new ArrayList<VmUtilizationData>());
        }
    }

    public Map<Integer, ArrayList<VmUtilizationData>> getVmData() {
        //Create a threadpool and start the worker threads
        ExecutorService executor = Executors.newFixedThreadPool(Constants.VM_DATA_COLLECTOR_THREAD_POOL_SIZE);
        for (int i = 0; i < Constants.VM_DATA_COLLECTOR_THREAD_POOL_SIZE; i++) {
            Runnable workerThread = new VmDataCollectorWorkerThread(this);
            executor.execute(workerThread);
        }
        //Collect VM data
        collectVmData();
        //Shutdown all the VM threads
        shutdownWorkerThreads();
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        return vmUtilizationTable;
    }

    private void collectVmData() {
        Long programTimeOutMsec = programTimeoutSec * 1000L;
        while (programTimeOutMsec > 0) {
            try {
                addVmDataCollectionTasks();
                programTimeOutMsec = programTimeOutMsec - Constants.SLEEP_TIME_OUT_MSEC;
                Thread.sleep(Constants.SLEEP_TIME_OUT_MSEC);
            } catch (InterruptedException e) {
                System.out.println("Caught InterruptedException. Exiting VM data collection before timeout.");
                break;
            }
        }
    }

    private void addVmDataCollectionTasks() {
        for (int i = 0; i < numberOfVms; i++) {
            Integer vmId = Constants.BEGINNING_PORT + i;
            addTaskToQueue(vmId);
        }
    }

    private void shutdownWorkerThreads() {
        for (int i = 0; i < Constants.VM_DATA_COLLECTOR_THREAD_POOL_SIZE; i++) {
            addTaskToQueue(Constants.EXIT_WORKER_THREAD);
        }
    }

    private void addTaskToQueue(Integer vmId) {
        synchronized (lock) {
            taskQueue.add(vmId);
            lock.notifyAll();
        }
    }

    public Integer getNextReadyTask() throws InterruptedException {
        synchronized (lock) {
            do {
                if (!taskQueue.isEmpty()) {
                    return taskQueue.remove();
                }
                lock.wait();
            } while(true);
        }
    }

    public Map<Integer, ArrayList<VmUtilizationData>> getVmUtilizationTable() {
        return vmUtilizationTable;
    }
}
