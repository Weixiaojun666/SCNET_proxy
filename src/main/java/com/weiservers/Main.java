package com.weiservers;

import Thread.Console;
import Thread.ThreadPool;
import Thread.TimeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Thread.ThreadPool.LoadThreadPool;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("加载中...");
        LoadThreadPool();
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
    }
}