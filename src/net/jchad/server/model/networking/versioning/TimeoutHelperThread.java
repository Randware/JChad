package net.jchad.server.model.networking.versioning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimeoutHelperThread implements Runnable{

    private static final ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
    private long timout;
    private final Thread threadToInterrupt;


    private TimeoutHelperThread(long milliSec, Thread threadToInterrupt) {
        this.timout = milliSec;
        this.threadToInterrupt = threadToInterrupt;
        executorService.submit(this);
    }

    protected static TimeoutHelperThread getTimeoutHelperThread(long milliSeconds, Thread thread) throws IllegalArgumentException{
        if (milliSeconds < 0) {throw new IllegalArgumentException("The duration of the Timeout has to be a positive value");}
        if (thread == null) {throw new IllegalArgumentException("The thread to interrupt can not be null");}
        TimeoutHelperThread newTimeoutHelperThread = new TimeoutHelperThread(milliSeconds,thread);
        return newTimeoutHelperThread;
    }

    @Override
    public void run() {

    }
}
