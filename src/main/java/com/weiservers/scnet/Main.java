package com.weiservers.scnet;

import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Info;
import com.weiservers.scnet.bean.Invalid;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.Console;
import com.weiservers.scnet.thread.Listening;
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
    public static final Map<InetAddress, Invalid> Invalids = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    public static List<ServerThread> serverThreads = new ArrayList<>();

    public static void ServersLoad() {


        List<Server> serverlist = Configuration.getSetting().server_list();


        if (serverlist.size() == 0)
            logger.warn("未加载任何服务器，您可能需要在config.json中添加服务器");
        else {
            logger.info("已加载{}个服务器", serverlist.size());
            logger.info("======================================================");
            logger.info(String.format("%-8s  %-8s %-18s %-12s %-8s %-8s", "序号", "服务器ID", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
            int num = 0;
            for (Server server : serverlist) {
                num++;
                logger.info(String.format("%-12s %-12s %-18s %-18s %-12s %-12s", num, server.id(), server.name(), server.address(), server.port(), server.proxy_port()));
                ThreadPool.execute(new Listening(server));
            }
            logger.info("======================================================");
            logger.info("所有服务器监听均已启动");
            logger.info("======================================================");
            if (Configuration.getSetting().aggregation().enable()) {
                logger.info("已启用聚合模式 聚合模式端口:{}  默认进入服务器ID:{}", Configuration.getSetting().aggregation().port(), Configuration.getSetting().aggregation().default_server());
                logger.info("======================================================");

                Server server = new Server(0, null, null, 0, Configuration.getSetting().aggregation().port());
                ThreadPool.execute(new Listening(server));
            }
        }

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
        Configuration.Load();

        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
        ServersLoad();
    }
}