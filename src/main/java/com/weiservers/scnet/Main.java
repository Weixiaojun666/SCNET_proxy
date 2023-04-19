package com.weiservers.scnet;

import com.weiservers.scnet.bean.*;
import com.weiservers.scnet.config.SpringConfig;
import com.weiservers.scnet.console.Console;
import com.weiservers.scnet.thread.Listening;
import com.weiservers.scnet.thread.TimeTask;
import com.weiservers.scnet.utils.ConfigLoad;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static com.weiservers.scnet.console.Console.ServerLoad;

public class Main {
    public static final Map<String, Client> Clients = new ConcurrentHashMap<>();

    public static final Map<InetAddress, Invalid> Invalids = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static List<ServerThread> serverThreads = new ArrayList<>();


    public static void main(String[] args) {
        logger.info("加载中...");
        if (Integer.parseInt(System.getProperty("java.version")) <20) {
            logger.error("请使用java20以上版本运行");
            System.exit(0);
        }
        if (Integer.parseInt(System.getProperty("sun.arch.data.model")) !=64) logger.warn("您正在使用32位Java！为保证性能请改用64位java");
        ApplicationContext ioc = new AnnotationConfigApplicationContext(SpringConfig.class);
        ThreadPool.LoadThreadPool();
        //ConfigLoad();
        ServerLoad();
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
    }

}