package com.weiservers.Core;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static ThreadPoolExecutor executor = null;

    public static void LoadThreadPool() {
        executor = new ThreadPoolExecutor(16, 16000, 30L, TimeUnit.SECONDS, new SynchronousQueue<>());

        executor.allowCoreThreadTimeOut(true);
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
