package com.weiservers.scnet;

import com.weiservers.scnet.bean.*;
import com.weiservers.scnet.bean.record.Server;
import com.weiservers.scnet.config.SpringConfig;
import com.weiservers.scnet.thread.Console;
import com.weiservers.scnet.thread.Listening;
import com.weiservers.scnet.thread.TimeTask;
import com.weiservers.scnet.utils.ConfigLoad;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    public static final Map<String, Client> Clients = new ConcurrentHashMap<>();

    public static final Map<InetAddress, Invalid> Invalids = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static List<ServerThread> serverThreads = new ArrayList<>();

    public static ApplicationContext ioc= new AnnotationConfigApplicationContext(SpringConfig.class);
    public static void ServersLoad() {
        List<Server> serverlist = ConfigLoad.getServerlist();
        logger.info("已加载{}个服务器", serverlist.size());
        logger.info("======================================================");
        logger.info(String.format("%-8s %-18s %-12s %-8s %-8s", "序号", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
        int num = 0;
        for (Server server : serverlist) {
            num++;
            logger.info(String.format("%-12s %-18s %-18s %-12s %-12s", num, server.name(), server.address(), server.port(), server.proxy_port()));
            ThreadPool.execute(new Listening(server));
        }
        logger.info("======================================================");
        logger.info("所有服务器监听均已启动");
    }

    public static void main(String[] args) {
        logger.info("加载中...");
        if (Integer.parseInt(System.getProperty("java.version")) < 20) {
            logger.error("请使用java20以上版本运行");
            System.exit(0);
        }
        if (Integer.parseInt(System.getProperty("sun.arch.data.model")) != 64)
            logger.warn("您正在使用32位Java！为保证性能请改用64位java");
        ThreadPool.LoadThreadPool();
        ConfigLoad.Load();
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
        ServersLoad();
    }




}