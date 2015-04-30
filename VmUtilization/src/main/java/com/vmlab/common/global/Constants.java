package com.vmlab.common.global;

/**
 * Created by msivagna on 4/26/2015.
 */
public interface Constants {
    public static final Integer BEGINNING_PORT = 2600;
    public static final String LOCAL_HOST = "localhost";
    public static final int VM_DATA_COLLECTOR_THREAD_POOL_SIZE = 5;
    public static final Long SLEEP_TIME_OUT_MSEC = 1000L;
    public static final Integer EXIT_WORKER_THREAD = -1;
    public static final Integer MAX_THRESHOLD = 50;
    public static final Integer MIN_THRESHOLD = 10;

    //VM Operations
    public static final String VM_SHUTDOWN = "SHUTDOWN_VM";
    public static final String VM_GET_UTILIZATION = "GET_VM_UTILIZATION";

    //VM Response
    public static final String VM_SHUTDOWN_RESPONSE = "SHUTTING_DOWN_VM";
    public static final String INVALID_REQUEST = "INVALID_REQUEST";
}
