package com.weiservers.scnet.thread;

import com.weiservers.scnet.utils.ThreadPool;
import com.weiservers.scnet.thread.Child.Clean;

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

