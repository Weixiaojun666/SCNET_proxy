package com.weiservers.Console;

import com.weiservers.Base.Client;
import com.weiservers.Base.Info;
import com.weiservers.Base.Server;
import com.weiservers.Cloud.Cloud;
import com.weiservers.GetConfig;
import com.weiservers.Thread.Listening;
import com.weiservers.Core.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;


public class Console extends Thread {
    public static final Map<String, Client> Clients = new ConcurrentHashMap<>();
    public final static Info info = new Info(System.currentTimeMillis());
    private final static Logger logger = LoggerFactory.getLogger(Console.class);


    public static void ServerList() {
        logger.info("已加载{}个服务器", GetConfig.getServerlist().size());
        logger.info("======================================================");
        logger.info(String.format("%-16s %-24s %-24s %-16s %-16s","序号", "服务器名称", "服务器地址", "服务器端口", "转发端口"));
        int num = 0;
        for (Server server : GetConfig.getServerlist()) {
            num++;
            logger.info(String.format("%-16s %-24s %-24s %-16s %-16s",num + "", server.name(), server.address(), server.port() + "", server.proxy_port() + ""));
            ThreadPool.execute(new Listening(server));
        }
        logger.info("======================================================");
        logger.info("所有服务器监听均已启动");
    }
    public static void println(String str){
        System.out.println(str);
    }

    public void run() {
        //读取配置玩家
        GetConfig.Read();
        Cloud.Load();
        ServerList();
        //等待执行命令
        while (!isInterrupted()) {
            Scanner scanner = new Scanner(System.in);
            ThreadPool.execute(new Command(scanner.nextLine()));
        }
    }
}
