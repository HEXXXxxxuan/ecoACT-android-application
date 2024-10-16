package com.go4.utils.design_pattern;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * This class provides a thread-safe singleton instance of an {@link ExecutorService}.
 * It is used to ensure that only one instance of the {@code ExecutorService} is created and shared across the
 * entire application
 * Use core calculator to allocate core to enhance performance and reduce the reaction time
 * @author u7902000 Gea Linggar, u8006862 Hexuan(Shawn)
 */
public class ExecutorServiceSingleton {
    private static ExecutorService instance;

    private ExecutorServiceSingleton() {
    }

    public static ExecutorService getInstance() {
        synchronized (ExecutorServiceSingleton.class) {
            if (instance == null || ((ThreadPoolExecutor) instance).isShutdown() || ((ThreadPoolExecutor) instance).isTerminated()) {
                int numCores = Runtime.getRuntime().availableProcessors();
                int numThreads = numCores * 2; //calculate the runtime core
                instance = Executors.newFixedThreadPool(numThreads);

            }


            return instance;
        }
    }

    public static void shutdown() {
        if (instance != null && !instance.isShutdown()) {
            instance.shutdown();
        }
    }
}
