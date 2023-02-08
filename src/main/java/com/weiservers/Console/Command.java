package com.weiservers.Console;

import com.weiservers.Base.Client;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Thread.Child.Clean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.weiservers.Cloud.Cloud.*;
import static com.weiservers.Core.Tools.disconnect;
import static com.weiservers.Core.Tools.getDatePoor;

public class Command extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Command.class);
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
                    Console.println("========================帮助列表=========================");
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "list", "查看当前在线用户"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "clean", "立即回收垃圾"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "cache", "立即刷新缓存"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "info", "查看统计信息"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "auth", "查看高级菜单"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "kick user [user]", "踢出此用户"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "kick ip [ip]", "断开此IP的所有连接"));
                    Console.println(String.format("\033[31m%-36s \033[32m%s\033[0m", "stop", "停止"));
                    Console.println("======================================================");
                }
                case "auth" -> {
                    Console.println("========================管理功能=========================");
                    Console.println("\033[31m----------------以下功能需要token授权才可使用----------------\033[0m");
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban ip add [ip] [理由] [时间]", "封禁一个IP"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban area add [ip] [理由] [时间]", "封禁这个ip所在的区域"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban user add [name] [理由] [时间]", "封禁此用户"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban ip remove [ip]", "解除封禁当前ip"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban area remove [ip]", "解除封禁当前ip所在的区域"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "ban user remove [name] ", "解封此用户"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "list area [页数]", "查看区域封禁列表"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "list user [页数]", "查看用户封禁列表"));
                    Console.println(String.format("\033[31m%-36s\033[0m \033[32m%s\033[0m", "list ip [页数]", "查看ip封禁列表"));
                    Console.println("\033[31m----------------更多功能可登录管理平台----------------\033[0m");
                    Console.println("======================================================");
                }
                case "clean" -> {
                    Console.println("========================回收垃圾=========================");
                    Clean clean = new Clean();
                    clean.start();
                    Console.println("======================================================");
                }
                case "cache" -> {
                    Console.println("========================刷新缓存=========================");
                    //目前没做
                    Console.println("======================================================");
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
                        case "area" -> {
                            getBanArea(command3);
                        }
                        case "user" -> {
                            getBanUser(command3);
                        }
                        case "ip" -> {
                            getBanIp(command3);
                        }
                        default -> {
                            Console.println("=====================在线连接列表======================");
                            Console.println(String.format("当前存在%s个连接", Console.Clients.size()));
                            for (Map.Entry<String, Client> client : Console.Clients.entrySet()) {
                                Console.println(String.format("%s  通过 %s 连接到 %s 登录用户名 %s 社区ID %s", client.getKey(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name(), client.getValue().getUsername(), client.getValue().getUserid()));
                            }
                            Console.println("======================================================");
                        }
                    }
                }
                case "info" -> {
                    Console.println("=====================统计信息======================");
                    Console.println(String.format("\033[32m正常运行\033[0m \033[33m%s \033[0m", getDatePoor(Console.info.getTime(), System.currentTimeMillis())));
                    Console.println("      \033[32m放行\033[0m      \033[31m拦截\033[0m");
                    Console.println(String.format("连接   \033[32m%s\033[0m       \033[31m%s\033[0m", Console.info.getNormal(), Console.info.getAbnormal()));
                    Console.println(String.format("IP   \033[32m%s\033[0m       \033[31m%s\033[0m", Console.info.getNormal_ip().size(), Console.info.getAbnormal_ip().size()));
                    Console.println(String.format("\033[32m缓存情况\033[0m  \033[32m%s\033[0m/\033[32m%s\033[0m [\033[32m穿透\033[0m/\033[32m应答\033[0m]", Console.info.getRefresh(), Console.info.getRespond()));
                    Console.println(String.format("\033[33m丢弃无效数据包\033[0m \033[31m%s\033[0m", Console.info.getInvalid()));
                    Console.println("======================================================");
                }
                case "stop" -> {
                    Console.println("========================停止程序=========================");
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
                            Console.println(String.format("========================踢出%s=========================", commands[1]));
                            for (Map.Entry<String, Client> client : Console.Clients.entrySet()) {
                                if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(command3)) {
                                    disconnect(client.getValue());
                                    Console.println(String.format("已踢出 %s", client.getKey()));
                                }
                            }
                            Console.println("======================================================");
                        }
                        default -> Console.println(String.format("\033[31m命令缺少参数：%s\033[0m", command));
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
                        default -> Console.println(String.format("命令错误：%s", command));
                    }

                }
                default -> Console.println(String.format("命令不存在：%s", command));
            }
        } catch (Exception e) {
            Console.println(String.format("执行命令时出错", e));
        }
    }
}