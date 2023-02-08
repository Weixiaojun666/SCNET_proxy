package com.weiservers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.Base.Client;
import com.weiservers.Base.Info;
import com.weiservers.Base.Server;
import com.weiservers.Base.ServerThread;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Console.Console;
import com.weiservers.Core.ThreadPool;
import com.weiservers.Thread.Listening;
import com.weiservers.Thread.TimeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.weiservers.Core.ThreadPool.LoadThreadPool;


public class Main {
    public static final Map<String, Client> Clients = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    private final static Logger logger = LoggerFactory.getLogger(Main.class);
    private static List<Server> serverlist;
    public static List<ServerThread> serverThreads =new ArrayList<>();
    private static Map<String, Object> setting;

    public static void ConfigLoad() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("./setting.json"));
            setting = objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
            serverlist = objectMapper.readValue(rootNode.get("server_list").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            logger.error("读取配置文件失败", e);
            System.exit(0);
        }
    }

    public static void ServerLoad() {
        logger.info("已加载{}个服务器", serverlist.size());
        logger.info("======================================================");
        logger.info(String.format("%-8s %-18s %-12s %-8s %-8s", "序号", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
        int num = 0;
        for (Server server : serverlist) {
            num++;
            logger.info(String.format("%-12s %-18s %-18s %-12s %-12s", num + "", server.name(), server.address(), server.port() + "", server.proxy_port() + ""));
            ThreadPool.execute(new Listening(server));
        }
        logger.info("======================================================");
        logger.info("所有服务器监听均已启动");
    }

    public static List<Server> getServerlist() {
        return serverlist;
    }

    public static Map<String, Object> getSetting() {
        return setting;
    }


    public static void main(String[] args) {
        logger.info("加载中...");
        LoadThreadPool();
        ConfigLoad();
        Cloud.Load();
        ServerLoad();
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
    }
}