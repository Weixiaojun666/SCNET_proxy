package com.weiservers;

import com.weiservers.Console.Console;
import com.weiservers.Core.ThreadPool;
import com.weiservers.Thread.TimeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.weiservers.Core.ThreadPool.LoadThreadPool;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("加载中...");
        LoadThreadPool();
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
    }
}