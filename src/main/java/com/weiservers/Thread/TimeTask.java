package com.weiservers.Thread;

import com.weiservers.Base.Client;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Console.Console;
import com.weiservers.Core.ThreadPool;
import com.weiservers.GetConfig;
import com.weiservers.Thread.Child.Clean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

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

