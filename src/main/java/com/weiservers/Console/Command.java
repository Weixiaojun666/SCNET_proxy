package com.weiservers.Console;

import com.weiservers.Base.Client;
import com.weiservers.Base.Motd;
import com.weiservers.Base.ServerThread;
import com.weiservers.Core.ThreadPool;
import com.weiservers.Main;
import com.weiservers.Thread.Child.Clean;

import java.util.Map;

import static com.weiservers.Core.Tools.*;
import static com.weiservers.Main.*;

public class Command extends Thread {
    private final String command;
    private String result;

    public Command(String command) {
        this.command = command.trim().toLowerCase();
    }


    public String getResult() {
        return result;
    }


    public void run() {
        try {


            String[] commands = command.split("\\s+");

            String command2 = "";
            String command3 = "";
            if (commands.length > 1) {
                command2 = commands[1];
            }
            if (commands.length > 2) {
                command3 = commands[2];
            }
            switch (commands[0]) {
                case "" -> {
                }
                case "help" -> {
                    System.out.println("========================帮助列表=========================");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "list", "查看当前在线用户");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "clean", "立即回收垃圾");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "cache", "立即刷新缓存");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "info", "查看统计信息");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "servers", "查看服务器缓存信息");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "kick user [user]", "踢出此用户");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "kick ip [ip]", "断开此IP的所有连接");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "reload", "重载配置文件");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "stop", "停止");
                    System.out.println("======================================================");
                    result = result + "list 查看当前在线用户\n";
                    result = result + "clean 立即回收垃圾\n";
                    result = result + "cache 立即刷新缓存\n";
                    result = result + "info 查看统计信息\n";
                    result = result + "servers 查看服务器缓存信息\n";
                    result = result + "kick user [user] 踢出此用户\n";
                    result = result + "kick ip [ip] 断开此IP的所有连接\n";
                    result = result + "reload 重载配置文件\n";
                    result = result + "stop 停止\n";
                }
                case "clean" -> {
                    System.out.println("========================回收垃圾=========================");
                    ThreadPool.execute(new Clean());
                    System.out.println("======================================================");
                    result = result + "回收垃圾完成\n";
                }
                case "reload" -> {
                    System.out.println("========================重载配置文件=========================");
                    stopservice();
                    ConfigLoad();
                    ServerLoad(serverlist);
                    System.out.println("======================================================");
                    result = result + "重载配置文件完成\n";
                }
                case "cache" -> {
                    System.out.println("====================刷新缓存[被动]=========================");
                    for (ServerThread serverThread : Main.serverThreads) {
                        ReloadCache(serverThread.getMotd(), serverThread.getServer());
                    }
                    System.out.println("缓存刷新请求已发出");
                    System.out.println("======================================================");
                    result = result + "刷新缓存请求已发出\n";
                }
                case "list" -> {
                    System.out.println("=====================在线连接列表======================");
                    System.out.printf("当前存在%s个连接%n", Main.Clients.size());
                    for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                        //result = result + System.out.format("%s 登录用户名 %s 社区ID %s 通过 %s 连接到 %s %n", client.getKey(), client.getValue().getUsername(), client.getValue().getUserid(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name()) + "\n";
                        System.out.printf("%s 登录用户名 %s 社区ID %s 通过 %s 连接到 %s %n", client.getKey(), client.getValue().getUsername(), client.getValue().getUserid(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name());
                    }
                    System.out.println("======================================================");

                }
                case "info" -> {
                    System.out.println("=====================统计信息======================");
                    System.out.printf("\u001B[32m正常运行\u001B[0m \u001B[33m%s \u001B[0m%n", getDatePoor(Main.info.getTime(), System.currentTimeMillis()));
                    System.out.println("      \033[32m放行\033[0m      \033[31m拦截\033[0m");
                    System.out.printf("连接   \u001B[32m%s\u001B[0m       \u001B[31m%s\u001B[0m%n", Main.info.getNormal(), Main.info.getAbnormal());
                    System.out.printf("IP   \u001B[32m%s\u001B[0m       \u001B[31m%s\u001B[0m%n", Main.info.getNormal_ip().size(), Main.info.getAbnormal_ip().size());
                    System.out.printf("\u001B[32m缓存情况\u001B[0m  \u001B[32m%s\u001B[0m/\u001B[32m%s\u001B[0m [\u001B[32m穿透\u001B[0m/\u001B[32m应答\u001B[0m]%n", Main.info.getRefresh(), Main.info.getRespond());
                    System.out.printf("\u001B[33m丢弃无效数据包\u001B[0m \u001B[31m%s\u001B[0m%n", Main.info.getInvalid());
                    System.out.println("======================================================");

                    result = result + "正常运行:" + getDatePoor(Main.info.getTime(), System.currentTimeMillis()) + "\n";
                    result = result + "放行连接:" + Main.info.getNormal() + "IP" + Main.info.getNormal_ip().size() + "\n";
                    result = result + "拦截连接:" + Main.info.getAbnormal() + "IP" + Main.info.getAbnormal_ip().size() + "\n";
                    result = result + "缓存 穿透:" + Main.info.getRefresh() + "应答" + Main.info.getRespond() + "\n";
                    result = result + "丢弃无效数据包:" + Main.info.getInvalid() + "\n";
                }
                case "servers" -> {
                    System.out.println("=====================服务器信息======================");
                    System.out.printf("\u001B[32m %-18s\u001B[0m \u001B[33m%-8s \u001B[0m \u001B[32m %-6s %-6s\u001B[0m  \u001B[32m %-8s\u001B[0m%n", "名称", "模式", "在线玩家", "最大玩家", "版本");
                    for (ServerThread serverThread : Main.serverThreads) {
                        Motd motd = serverThread.getMotd();
                        //result = result + motd.getServername() + " " + motd.getModel() + " " + motd.getOnlineplayer() + " " + motd.getMaxplayer() + " " + motd.getVersion() + "\n";
                        System.out.printf("\u001B[32m %-18s\u001B[0m \u001B[33m%-8s \u001B[0m \u001B[32m %-6s %-6s\u001B[0m  \u001B[32m %-8s\u001B[0m%n", motd.getServername(), motd.getModel(), motd.getOnlineplayer(), motd.getMaxplayer(), motd.getVersion());
                    }
                    System.out.println("======================================================");
                }
                case "stop" -> {
                    System.out.println("========================停止程序=========================");
                    stopservice();
                    ThreadPool.shutdown();
                    System.exit(0);
                }
                case "kick" -> {
                    switch (command2) {
                        case "user" -> {
                            System.out.printf("========================踢出%s=========================%n", commands[1]);
                            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                                if (client.getValue().getUsername().equals(command3)) {
                                    disconnect(client.getValue());
                                    System.out.printf("已踢出 %s%n", client.getValue().getUsername());
                                    //result = result + client.getValue().getUsername() + "\n";
                                }
                            }
                        }
                        case "ip" -> {
                            System.out.printf("========================踢出%s=========================%n", commands[1]);
                            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                                if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(command3)) {
                                    disconnect(client.getValue());
                                    System.out.printf("已踢出 %s%n", client.getKey());
                                    //result = result + client.getKey() + "\n";
                                }
                            }
                            System.out.println("======================================================");
                        }
                        default -> {
                            System.out.printf("\u001B[31m命令缺少参数：%s\u001B[0m%n", command);
                            result = result + "命令缺少参数\n";
                        }
                    }
                }
                default -> {
                    System.out.printf("\u001B[31m命令不存在：\u001B[0m\u001B[33m%s%n\u001B[0m", command);
                    result = result + "命令不存在\n";
                }
            }
        } catch (Exception e) {
            System.out.printf("执行命令时出错 %s%n", e);
            result = result + "执行命令时出错\n";
        }
    }
}