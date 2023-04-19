package com.weiservers.scnet.console;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Server;
import com.weiservers.scnet.thread.Listening;
import com.weiservers.scnet.utils.ConfigLoad;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;


public class Console extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Console.class);
    public void run() {
        while (!isInterrupted()) {
            Scanner scanner = new Scanner(System.in);
            ThreadPool.execute(new Command(scanner.nextLine()));
        }
    }
    public static void ServerLoad() {
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
}
