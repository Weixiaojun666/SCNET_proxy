package com.weiservers.Thread;

import com.weiservers.Core.ThreadPool;
import com.weiservers.Thread.Child.Clean;

public class TimeTask extends Thread {
    public void run() {
        long time = System.currentTimeMillis();
        while (!isInterrupted()) {
            if ((time + 3000L) < System.currentTimeMillis()) {
                //回收垃圾
                ThreadPool.execute(new Clean());
                time = System.currentTimeMillis();
            }
        }
    }
}

