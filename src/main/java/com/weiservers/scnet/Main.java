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
            logger.error("No server loaded, you may need to add server in config.json");
            System.exit(0);
        }

        logger.info("{} servers loaded", serverlist.size());
        logger.info(String.format("%-8s  %-8s %-18s %-12s %-8s %-8s", "序号", "服务器ID", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
        int num = 0;
        for (Server server : serverlist) {
            num++;
            logger.info(String.format("%-12s %-12s %-18s %-18s %-12s %-12s", num, server.id(), server.name(), server.address(), server.port(), server.proxyPort()));
            ThreadPool.execute(new Listening(server));
        }
        logger.info("All server listening is started");
        if (Configuration.getSetting().aggregation().port() > 0) {
            logger.info("Aggregation mode enabled Aggregation mode port: {} Default access server ID: {}", Configuration.getSetting().aggregation().port(), Configuration.getSetting().aggregation().defaultServer());
            ThreadPool.execute(new ListeningAggregation(Configuration.getSetting().aggregation().port(), Configuration.getSetting().aggregation().defaultServer()));
        }
    }


    public static void main(String[] args) {

        logger.info("Loading...");
        if (Integer.parseInt(System.getProperty("java.version").substring(0, 2)) < 20) {
            logger.error("Please use java20 or newer version to run");
            System.exit(0);
        }
        if (Integer.parseInt(System.getProperty("sun.arch.data.model")) != 64)
            logger.warn("You are using 32-bit Java! To ensure performance, please switch to 64-bit java");
        ThreadPool.LoadThreadPool();
        Configuration.Load();

        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
        ServersLoad();
    }
}