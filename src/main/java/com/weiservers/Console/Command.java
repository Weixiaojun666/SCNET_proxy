package com.weiservers.Console;

import com.weiservers.Base.Client;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Main;
import com.weiservers.Thread.Child.Clean;

import java.util.Map;

import static com.weiservers.Cloud.Cloud.*;
import static com.weiservers.Core.Tools.disconnect;
import static com.weiservers.Core.Tools.getDatePoor;

public class Command extends Thread {
    private final String command;

    public Command(String command) {
        this.command = command.trim().toLowerCase();
    }

    public void run() {
        try {
            String[] commands = command.split("\\s+");

            switch (commands[0]) {
                case "" -> {
                }
                case "help" -> {
                    System.out.println("========================帮助列表=========================");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "list", "查看当前在线用户");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "clean", "立即回收垃圾");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "cache", "立即刷新缓存");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "info", "查看统计信息");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "auth", "查看高级菜单");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "kick user [user]", "踢出此用户");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "kick ip [ip]", "断开此IP的所有连接");
                    System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", "stop", "停止");
                    System.out.println("======================================================");
                }
                case "auth" -> {
                    System.out.println("========================管理功能=========================");
                    System.out.println("\033[31m----------------以下功能需要token授权才可使用----------------\033[0m");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban ip add [ip] [理由] [时间]", "封禁一个IP");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban area add [ip] [理由] [时间]", "封禁这个ip所在的区域");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban user add [name] [理由] [时间]", "封禁此用户");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban ip remove [ip]", "解除封禁当前ip");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban area remove [ip]", "解除封禁当前ip所在的区域");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "ban user remove [name] ", "解封此用户");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "list area [页数]", "查看区域封禁列表");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "list user [页数]", "查看用户封禁列表");
                    System.out.printf("\u001B[33m%-36s\u001B[0m \u001B[32m%s\u001B[0m%n", "list ip [页数]", "查看ip封禁列表");
                    System.out.println("\033[31m----------------更多功能可登录管理平台----------------\033[0m");
                    System.out.println("======================================================");
                }
                case "clean" -> {
                    System.out.println("========================回收垃圾=========================");
                    Clean clean = new Clean();
                    clean.start();
                    System.out.println("======================================================");
                }
                case "cache" -> {
                    System.out.println("========================刷新缓存=========================");
                    //目前没做
                    System.out.println("======================================================");
                }
                case "list" -> {
                    String command2 = "";
                    int command3 = 1;
                    if (commands.length > 1) {
                        command2 = commands[1];
                    }
                    if (commands.length > 2) {
                        command3 = Integer.parseInt(commands[2]);
                    }
                    switch (command2) {
                        case "area" -> getBanArea(command3);
                        case "user" -> getBanUser(command3);
                        case "ip" -> getBanIp(command3);
                        default -> {
                            System.out.println("=====================在线连接列表======================");
                            System.out.printf("当前存在%s个连接%n", Main.Clients.size());
                            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                                System.out.printf("%s  通过 %s 连接到 %s 登录用户名 %s 社区ID %s%n", client.getKey(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name(), client.getValue().getUsername(), client.getValue().getUserid());
                            }
                            System.out.println("======================================================");
                        }
                    }
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
                }
                case "stop" -> {
                    System.out.println("========================停止程序=========================");
                    System.exit(0);
                }
                case "kick" -> {
                    String command2 = "";
                    String command3 = "";
                    if (commands.length > 1) {
                        command2 = commands[1];
                    }
                    if (commands.length > 2) {
                        command3 = commands[2];
                    }
                    switch (command2) {
                        case "user" -> {

                        }
                        case "ip" -> {
                            System.out.printf("========================踢出%s=========================%n", commands[1]);
                            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                                if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(command3)) {
                                    disconnect(client.getValue());
                                    System.out.printf("已踢出 %s%n", client.getKey());
                                }
                            }
                            System.out.println("======================================================");
                        }
                        default -> System.out.printf("\u001B[31m命令缺少参数：%s\u001B[0m%n", command);
                    }
                }
                case "ban" -> {
                    String command4 = "未填写";
                    String command5 = "36500";
                    if (commands.length > 4) {
                        command4 = commands[4];
                    }
                    if (commands.length > 5) {
                        command5 = commands[5];
                    }
                    switch (commands[1]) {
                        case "ip" -> {
                            if (commands[2].equals("add")) Cloud.banip(commands[3], command4, command5, "1");
                            if (commands[2].equals("remove")) Cloud.banip(commands[3], "", "", "0");
                        }
                        case "area" -> {
                            if (commands[2].equals("add")) Cloud.banarea(commands[3], command4, command5, "1");
                            if (commands[2].equals("remove")) Cloud.banarea(commands[3], "", "", "0");
                        }
                        case "user" -> {
                            String username = commands[3];
                            if (commands[2].equals("add")) Cloud.banuser(username, command4, command5, "1");
                            if (commands[2].equals("remove")) Cloud.banuser(username, "", "", "0");
                        }
                        default -> System.out.printf("\u001B[31m命令错误：\u001B[0m\u001B[33m%s%n\u001B[0m", command);
                    }

                }
                default -> System.out.printf("\u001B[31m命令不存在：\u001B[0m\u001B[33m%s%n\u001B[0m", command);
            }
        } catch (Exception e) {
            System.out.printf("执行命令时出错 %s%n", e);
        }
    }
}