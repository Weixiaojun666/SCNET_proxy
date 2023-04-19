package com.weiservers.scnet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.base.*;
import com.weiservers.scnet.console.Console;
import com.weiservers.scnet.utils.ThreadPool;
import com.weiservers.scnet.thread.Listening;
import com.weiservers.scnet.thread.TimeTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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
    public static List<Server> serverlist;
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

    public static void ServerLoad(List<Server> serverlist) {
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

    public static Map<String, Object> getSetting() {
        return setting;
    }


    public static void main(String[] args) {
        logger.info("加载中...");
        ThreadPool.LoadThreadPool();
        ConfigLoad();
        ServerLoad(serverlist);
        ThreadPool.execute(new Console());
        ThreadPool.execute(new TimeTask());
    }
}