package com.weiservers.scnet;

import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Info;
import com.weiservers.scnet.bean.Invalid;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.Console;
import com.weiservers.scnet.thread.Listening;
import com.weiservers.scnet.thread.ListeningAggregation;
import com.weiservers.scnet.thread.TimeTask;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Main {
    public static final Map<String, Client> Clients = new ConcurrentHashMap<>();
    public static final Map<String, Server> Servers = new ConcurrentHashMap<>();

    public static final Map<InetAddress, Invalid> Invalids = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    public static final List<ServerThread> serverThreads = new ArrayList<>();
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void ServersLoad() {


        List<Server> serverlist = Configuration.getSetting().serverList();


        if (serverlist.isEmpty()) {
            logger.error("没有任何服务器被加载，你应该需要去修改一下config.json");
            System.exit(0);
        }

        logger.info("已加载{}个服务器", serverlist.size());
        logger.info(String.format("%-8s  %-8s %-18s %-12s %-8s %-8s", "序号", "服务器ID", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
        int num = 0;
        for (Server server : serverlist) {
            num++;
            logger.info(String.format("%-12s %-12s %-18s %-18s %-12s %-12s", num, server.id(), server.name(), server.address(), server.port(), server.proxyPort()));
            ThreadPool.execute(new Listening(server));
        }
        logger.info("所有服务器已经加载完成");
        if (Configuration.getSetting().aggregation().port() > 0) {
            logger.info("聚合模式已启动在端口 {} 默认服务器 ID: {}", Configuration.getSetting().aggregation().port(), Configuration.getSetting().aggregation().defaultServer());
            ThreadPool.execute(new ListeningAggregation(Configuration.getSetting().aggregation().port(), Configuration.getSetting().aggregation().defaultServer()));
        }
    }


    public static void main(String[] args) {

        logger.info("Loading...");
        if (Integer.parseInt(System.getProperty("java.version").substring(0, 2)) < 20) {
            logger.error("请使用java20或更新版本的java");
            System.exit(0);
        }
        if (Integer.parseInt(System.getProperty("sun.arch.data.model")) != 64)
            logger.warn("您正在使用32位Java,为了保证性能请使用64位Java");
        ThreadPool.LoadThreadPool();
        Configuration.Load();

        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
        ServersLoad();
    }
}