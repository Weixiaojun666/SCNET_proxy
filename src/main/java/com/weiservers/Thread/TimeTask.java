package com.weiservers.Thread;

import com.weiservers.Core.ThreadPool;
import com.weiservers.Main;
import com.weiservers.Thread.Child.Clean;
import com.weiservers.Thread.Child.Monitor;

public class TimeTask extends Thread {
    public void run() {
        long time = System.currentTimeMillis();
        while (!isInterrupted()) {
            if ((time + 3000L) < System.currentTimeMillis()) {
                //回收垃圾
                ThreadPool.execute(new Clean());

                //轮询服务器指令
                if ((boolean) Main.getSetting().get("Monitor")) ThreadPool.execute(new Monitor());
                time = System.currentTimeMillis();
            }
        }
    }
}

