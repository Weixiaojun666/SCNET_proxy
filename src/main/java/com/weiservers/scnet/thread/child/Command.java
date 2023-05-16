package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.ServerThread;

import java.util.Map;

import static com.weiservers.scnet.utils.Command.Cache;
import static com.weiservers.scnet.utils.Command.Clean;
import static com.weiservers.scnet.utils.Command.*;
import static com.weiservers.scnet.utils.DataConvertUtils.getDatePoor;

public class Command extends Thread {
    private final String command;


    public Command(String command) {
        this.command = command.trim().toLowerCase();
    }

    public void hint() {
        hint(true);
    }

    public void hint_error(String error, String command) {
        System.out.printf("\u001B[31m执行命令时出错: %s%n：\u001B[0m\u001B[33m%s%n\u001B[0m", error, command);
    }

    public void hint(Boolean options) {
        if (options) {
            System.out.println("======================================================");
        } else {
            System.out.println("------------------------------------------------------");
        }
    }

    public void hint(String title) {
        System.out.printf("========================%s%n=========================", title);
    }

    public void hint(String command, String hint) {
        System.out.printf("\u001B[33m%-36s \u001B[32m%s\u001B[0m%n", command, hint);
    }

    @Override
    public void run() {
        try {
            String[] commands = command.split("\\s+");
            String[] commandParams = new String[6];

            for (int i = 0; i <= commandParams.length && i <= commands.length; i++) {
                commandParams[i] = commands[i];
            }

            switch (commands[0]) {
                case "" -> {
                }
                case "help" -> {
                    hint("帮助列表");
                    //已将除列表类指令外全部拆分出去,为远程调用做准备[列表类指令不允许远程调用]
                    hint("list", "查看各种列表信息");
                    hint("clean", "立即回收垃圾");
                    hint("cache", "立即刷新缓存");
                    hint(false);
                    hint("whitelist", "查看白名单相关指令");
                    hint("kick", "踢出用户");
                    hint("ban", "封禁用户");
                    hint(false);
                    hint("reload", "重新加载");
                    hint("stop", "停止");
                    hint();
                }
                case "list" -> {
                    if (commandParams[1].equals("")) {
                        hint("列表相关");
                        hint("list ban", "查看封禁列表");
                        hint("list ips", "查看封禁的IP列表");
                        hint("list area", "查看封禁地区列表");
                        hint("list whitelist", "查看白名单列表");
                        hint("list servers", "查看服务器缓存信息");
                        hint("list info", "查看服务器统计信息");
                        hint("list online", "查看在线用户列表");
                        hint();
                    } else list(commandParams[1]);
                }
                case "ban" -> {
                    switch (commands[1]) {
                        case "" -> {
                            hint("封禁相关");
                            hint("ban add user <id/name/ip>  [reason] [duration]", "封禁用户");
                            hint("ban add ip  <id/name/ip> [reason] [duration]", "封禁IP");
                            hint("ban add area  <id/name/ip> [level] [reason] [duration]", "封禁地区");
                            hint("ban remove user <user>", "解封用户");
                            hint("ban remove ip <ip>", "解封IP");
                            hint("ban remove area <暂未实现>", "解封地区");
                            hint();
                        }
                        case "add" -> {
                            int expires = 0;
                            if (!commandParams[4].equals("")) expires = Integer.parseInt(commandParams[4]);
                            switch (commandParams[2]) {
                                case "" -> {
                                }
                                case "user" -> BanAddUser(commandParams[3], expires, commandParams[5]);
                                case "ip" -> BanAddIp(commandParams[3], expires, commandParams[5]);
                                case "area" -> BanAddArea(commandParams[3], expires, commandParams[5]);
                                default -> hint_error("命令不存在", command);
                            }

                        }
                        case "remove" -> {
                            switch (commandParams[2]) {
                                case "" -> {
                                }
                                case "user" -> BanRemoveUser(commandParams[3]);
                                case "ip" -> BanRemoveIp(commandParams[3]);
                                case "area" -> System.out.println("暂未实现");
                                default -> hint_error("命令不存在", command);
                            }
                        }
                        default -> hint_error("命令不存在", command);
                    }
                }
                case "kick" -> {

                    switch (commandParams[1]) {
                        case "" -> {
                            hint("踢人相关");
                            hint("kick user <id/name/ip>", "踢出用户");
                            hint("kick area <id/name/ip> [level]", "踢出地区相同的用户");
                            hint("kick all", "踢出所有用户");
                            hint();
                        }
                        case "user" -> KickUser(commandParams[3]);
                        case "area" -> KickArea(commandParams[3]);
                        case "all" -> KickAll();
                    }
                }
                case "whitelist" -> {

                    switch (commandParams[1]) {
                        case "" -> {
                            hint("白名单相关");
                            hint("whitelist add <user/ip>", "添加白名单");
                            hint("whitelist remove <user/ip>", "移除白名单");
                            hint();
                        }
                        case "add" -> WhitelistAdd(commandParams[2]);
                        case "remove" -> WhitelistRemove(commandParams[2]);
                        default -> hint_error("命令不存在", command);
                    }
                }
                case "clean" -> {
                    hint("回收垃圾");
                    Clean();
                    hint();
                }
                case "reload" -> {
                    hint("重载配置文件");
                    Reload();
                    hint();
                }
                case "cache" -> {
                    hint("刷新缓存");
                    Cache();
                    hint();
                }
                case "stop" -> {
                    hint("停止程序");
                    Stop();
                }
                default -> hint_error("命令不存在", command);
            }
        } catch (Exception e) {
            hint_error(e.toString(), command);
        }
    }

    public void list(String list) {
        switch (list) {
//            case "ban" -> {
//
//
//            }
//            case "ip" -> {
//
//            }
//            case "area" -> {
//
//            }
//            case "whitelist" -> {
//
//            }
            case "servers" -> {
                System.out.printf("\u001B[32m %-18s\u001B[0m \u001B[33m%-8s \u001B[0m \u001B[32m %-6s %-6s\u001B[0m  \u001B[32m %-8s\u001B[0m%n", "名称", "模式", "在线玩家", "最大玩家", "版本");
                for (ServerThread serverThread : Main.serverThreads) {
                    Motd motd = serverThread.motd();
                    System.out.printf("\u001B[32m %-18s\u001B[0m \u001B[33m%-8s \u001B[0m \u001B[32m %-6s %-6s\u001B[0m  \u001B[32m %-8s\u001B[0m%n", motd.getServername(), motd.getModel(), motd.getOnlineplayer(), motd.getMaxplayer(), motd.getVersion());
                }
            }
            case "info" -> {
                hint("统计信息");
                System.out.printf("\u001B[32m正常运行\u001B[0m \u001B[33m%s \u001B[0m%n", getDatePoor(Main.info.getTime(), System.currentTimeMillis()));
                System.out.println("      \033[32m放行\033[0m      \033[31m拦截\033[0m");
                System.out.printf("连接   \u001B[32m%s\u001B[0m       \u001B[31m%s\u001B[0m%n", Main.info.getNormal(), Main.info.getAbnormal());
                System.out.printf("IP   \u001B[32m%s\u001B[0m       \u001B[31m%s\u001B[0m%n", Main.info.getNormal_ip().size(), Main.info.getAbnormal_ip().size());
                System.out.printf("\u001B[32m缓存情况\u001B[0m  \u001B[32m%s\u001B[0m/\u001B[32m%s\u001B[0m [\u001B[32m穿透\u001B[0m/\u001B[32m应答\u001B[0m]%n", Main.info.getRefresh(), Main.info.getRespond());
                System.out.printf("\u001B[33m丢弃无效数据包\u001B[0m \u001B[31m%s\u001B[0m%n", Main.info.getInvalid());
                hint();
            }
            case "online" -> {
                hint("在线用户列表");
                System.out.printf("当前存在%s个连接%n", Main.Clients.size());
                for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                    System.out.printf("%s 登录用户名 %s 社区ID %s 通过 %s 连接到 %s %n", client.getKey(), client.getValue().getUsername(), client.getValue().getUserid(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name());
                }
                hint();

            }
            default -> hint_error("命令不存在", command);
        }
    }
}