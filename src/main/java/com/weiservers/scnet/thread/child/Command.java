package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.bean.record.Whitelist;
import com.weiservers.scnet.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.weiservers.scnet.config.Configuration.ReadConfig;
import static com.weiservers.scnet.config.Configuration.SaveConfig;
import static com.weiservers.scnet.utils.Command.Cache;
import static com.weiservers.scnet.utils.Command.Clean;
import static com.weiservers.scnet.utils.Command.*;
import static com.weiservers.scnet.utils.DataConvertUtils.getDatePoor;

public class Command extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Command.class);
    private final String command;

    public Command(String command) {
        this.command = command.trim().toLowerCase();
    }

    public void hint() {
        hint(true);
    }

    public void hint_error(String error, String command) {
        logger.info("\u001B[31m执行命令时出错: {}：\u001B[0m\u001B[33m{}\u001B[0m", command, error);
    }

    public void hint(Boolean options) {
        if (options) {
            logger.info("======================================================");
        } else {
            logger.info("------------------------------------------------------");
        }
    }

    public void hint(String title) {
        logger.info("========================{}=========================", title);
    }

    public void hint(String command, String hint) {
        logger.info("\u001B[33m{} \u001B[32m{}\u001B[0m", command, hint);
    }

    @Override
    public void run() {
        try {
            String[] commands = command.split("\\s+");
            String[] commandParams = new String[commands.length];

            System.arraycopy(commands, 0, commandParams, 0, commands.length);

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
                    hint("saveConfig", "立即保存配置");
                    hint("reloadConfig", "重新加载配置");
                    hint(false);
                    hint("reload", "重新加载");
                    hint("stop", "停止");
                    hint();
                }
                case "list" -> {
                    if (commandParams.length == 1) {
                        hint("列表相关");
                        hint("list ban", "查看封禁列表");
                        hint("list ip", "查看封禁的IP列表");
                        hint("list area", "查看封禁地区列表");
                        hint("list whitelist", "查看白名单列表");
                        hint("list servers", "查看服务器缓存信息");
                        hint("list info", "查看服务器统计信息");
                        hint("list online", "查看在线用户列表");
                        hint();
                    } else list(commandParams[1]);
                }
                case "ban" -> {
                    if (commandParams.length == 1) {
                        hint("封禁相关");
                        hint("ban add user <id/name/ip>  [expires] [reason]", "封禁用户");
                        hint("ban add ip  <id/name/ip> [expires] [reason]", "封禁IP");
                        hint("ban add area  <id/name/ip>  [expires] [reason]", "封禁地区");
                        hint("ban remove user <user>", "解封用户");
                        hint("ban remove ip <ip>", "解封IP");
                        hint("ban remove area <暂未实现>", "解封地区");
                        hint();
                    } else switch (commands[1]) {
                        case "add" -> {
                            int expires = 0;
                            String reason = "";
                            if ((commandParams.length >= 3)) expires = Integer.parseInt(commandParams[4]);
                            if (commandParams.length >= 4) reason = commandParams[5];
                            switch (commandParams[2]) {
                                case "" -> {
                                }
                                case "user" -> BanAddUser(commandParams[3], expires, reason);
                                case "ip" -> BanAddIp(commandParams[3], expires, reason);
                                case "area" -> BanAddArea(commandParams[3], expires, reason);
                                default -> hint_error("命令不存在", commandParams[2]);
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
                    if (commandParams.length == 1) {
                        hint("踢人相关");
                        hint("kick user <id/name/ip>", "踢出用户");
                        hint("kick area <id/name/ip> [level]", "踢出地区相同的用户");
                        hint("kick all", "踢出所有用户");
                        hint();
                    } else switch (commandParams[1]) {
                        case "user" -> KickUser(commandParams[3]);
                        case "area" -> KickArea(commandParams[3]);
                        case "all" -> KickAll();
                    }
                }
                case "whitelist" -> {
                    if (commandParams.length == 1) {
                        hint("白名单相关");
                        hint("whitelist add <user/ip>", "添加白名单");
                        hint("whitelist remove <user/ip>", "移除白名单");
                        hint();
                    } else switch (commandParams[1]) {
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
                case "saveconfig" -> {
                    hint("保存配置文件");
                    SaveConfig();
                    hint();
                }
                case "reloadconfig" -> {
                    hint("重新加载配置文件");
                    ReadConfig();
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
            case "ban" -> {

                logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", "序号", "用户", "时长", "理由", "时间");
                int num = 0;
                for (Banned.BannedPlayers bannedUsers : Configuration.getBanned().bannedPlayers()) {
                    logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", ++num, bannedUsers.userid(), bannedUsers.expires(), bannedUsers.reason(), bannedUsers.updated());
                }

            }
            case "ip" -> {
                logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", "序号", "IP", "时长", "理由", "时间");
                int num = 0;
                for (Banned.BannedIps bannedIps : Configuration.getBanned().bannedIps()) {
                    logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", ++num, bannedIps.ip(), bannedIps.expires(), bannedIps.reason(), bannedIps.updated());
                }
            }
            case "area" -> {
                logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", "序号", "地区[info1]    [info2]  [info3] [isp]", "时长", "理由", "时间");
                int num = 0;
                for (Banned.BannedAreas bannedAreas : Configuration.getBanned().bannedAreas()) {
                    logger.info("\u001B[32m {}\u001B[0m \u001B[33m{}    {}  {}  {}\u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", ++num, bannedAreas.info1(), bannedAreas.info2(), bannedAreas.info3(), bannedAreas.isp(), bannedAreas.expires(), bannedAreas.reason(), bannedAreas.updated());
                }

            }
            case "whitelist" -> {
                logger.info("\u001B[32m {}\u001B[0m \u001B[33m{}\u001B[0m", "序号", "用户");
                int num = 0;
                for (Whitelist.White whitelist : Configuration.getWhitelist().whiteList()) {
                    logger.info("\u001B[32m {}\u001B[0m \u001B[33m{}\u001B[0m", ++num, whitelist.userid());
                }

            }
            case "servers" -> {
                logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", "名称", "模式", "在线玩家", "最大玩家", "版本");
                for (ServerThread serverThread : Main.serverThreads) {
                    Motd motd = serverThread.motd();
                    logger.info("\u001B[32m {}\u001B[0m \u001B[33m{} \u001B[0m \u001B[32m {} {}\u001B[0m  \u001B[32m {}\u001B[0m", motd.getServername(), motd.getModel(), motd.getOnlineplayer(), motd.getMaxplayer(), motd.getVersion());
                }
            }
            case "info" -> {
                hint("统计信息");
                logger.info("\u001B[32m正常运行\u001B[0m \u001B[33m{} \u001B[0m", getDatePoor(Main.info.getTime(), System.currentTimeMillis()));
                logger.info("      \033[32m放行\033[0m      \033[31m拦截\033[0m");
                logger.info("连接   \u001B[32m{}\u001B[0m       \u001B[31m{}\u001B[0m", Main.info.getNormal(), Main.info.getAbnormal());
                logger.info("IP   \u001B[32m{}\u001B[0m       \u001B[31m{}\u001B[0m", Main.info.getNormal_ip().size(), Main.info.getAbnormal_ip().size());
                logger.info("\u001B[32m缓存情况\u001B[0m  \u001B[32m{}\u001B[0m/\u001B[32m{}\u001B[0m [\u001B[32m穿透\u001B[0m/\u001B[32m应答\u001B[0m]", Main.info.getRefresh(), Main.info.getRespond());
                logger.info("\u001B[33m丢弃无效数据包\u001B[0m \u001B[31m{}\u001B[0m", Main.info.getInvalid());
                hint();
            }
            case "online" -> {
                hint("在线用户列表");
                logger.info("当前存在{}个连接", Main.Clients.size());
                for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                    logger.info("{} 登录用户名 {} 社区ID {} 通过 {} 连接到 {} ", client.getKey(), client.getValue().getUsername(), client.getValue().getUserid(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name());
                }
                hint();

            }
            default -> hint_error("命令不存在", command);
        }
    }
}