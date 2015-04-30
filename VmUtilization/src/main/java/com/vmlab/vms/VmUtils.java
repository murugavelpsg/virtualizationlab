package com.vmlab.vms;

import com.vmlab.common.global.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by msivagna on 4/26/2015.
 */
public class VmUtils {
    public static AtomicInteger portAddress = new AtomicInteger(Constants.BEGINNING_PORT);

    public static String sendMessageToVm(int vmPort, String message) throws IOException {
        // Setup networking
        Socket socket = new Socket(Constants.LOCAL_HOST, vmPort);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        out.println(message);
        return in.readLine();
    }
}
