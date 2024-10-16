package com.go4.utils.design_pattern;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class provides a thread-safe singleton instance of an {@link ExecutorService}.
 * It is used to ensure that only one instance of the {@code ExecutorService} is created and shared across the
 * entire application
 * @author u7902000 Gea Linggar
 */
public class ExecutorServiceSingleton {
    private static ExecutorService instance;

    private ExecutorServiceSingleton() {
    }

    public static ExecutorService getInstance() {
        if (instance == null) {
            synchronized (ExecutorServiceSingleton.class) {
                if (instance == null) {
                    instance = Executors.newFixedThreadPool(6);
                }
            }
        } else {
            Log.d("Executor Singleton", "Instance has already created");
        }
        return instance;
    }

    public static void shutdown() {
        if (instance != null && !instance.isShutdown()) {
            instance.shutdown();
        }
    }
}
