package cn.zzwei.scnet.utils;

import cn.zzwei.scnet.mapping.ConfigMapping;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
    private static ThreadPoolExecutor executor = null;

    public static void LoadThreadPool() {
        executor = new ThreadPoolExecutor(ConfigMapping.corePoolSize, Integer.MAX_VALUE, ConfigMapping.keepAliveTime, TimeUnit.SECONDS, new SynchronousQueue<>());
        executor.allowCoreThreadTimeOut(true);
    }

    public static void execute(Runnable task) {
        executor.execute(task);
    }

    public static void shutdown() {
        executor.shutdown();
    }
}