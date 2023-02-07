package Thread.Child;

import Base.Client;
import Thread.Console;
import com.weiservers.Cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.weiservers.Cloud.*;
import static com.weiservers.Tools.disconnect;
import static com.weiservers.Tools.getDatePoor;

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
                    logger.info("========================帮助列表=========================");
                    logger.info(String.format("%-36s %s", "list", "查看当前在线用户"));
                    logger.info(String.format("%-36s %s", "clean", "立即回收垃圾"));
                    logger.info(String.format("%-36s %s", "cache", "立即刷新缓存"));
                    logger.info(String.format("%-36s %s", "info", "查看统计信息"));
                    logger.info(String.format("%-36s %s", "auth", "查看高级菜单"));
                    logger.info(String.format("%-36s %s", "kick user [user]", "踢出此用户"));
                    logger.info(String.format("%-36s %s", "kick ip [ip]", "断开此IP的所有连接"));
                    logger.info(String.format("%-36s %s", "stop", "停止"));
                    logger.info("======================================================");
                }
                case "auth" -> {
                    logger.info("========================管理功能=========================");
                    logger.info("\033[31m----------------以下功能需要token授权才可使用----------------\033[0m");
                    logger.info(String.format("%-36s %s", "ban ip add [ip] [理由] [时间]", "封禁一个IP"));
                    logger.info(String.format("%-36s %s", "ban area add [ip] [理由] [时间]", "封禁这个ip所在的区域"));
                    logger.info(String.format("%-36s %s", "ban user add [name] [理由] [时间]", "封禁此用户"));
                    logger.info(String.format("%-36s %s", "ban ip remove [ip]", "解除封禁当前ip"));
                    logger.info(String.format("%-36s %s", "ban area remove [ip]", "解除封禁当前ip所在的区域"));
                    logger.info(String.format("%-36s %s", "ban user remove [name] ", "解封此用户"));
                    logger.info(String.format("%-36s %s", "list area [页数]", "查看区域封禁列表"));
                    logger.info(String.format("%-36s %s", "list user [页数]", "查看用户封禁列表"));
                    logger.info(String.format("%-36s %s", "list ip [页数]", "查看ip封禁列表"));
                    logger.info("\033[31m----------------更多功能可登录管理平台----------------\033[0m");
                    logger.info("======================================================");
                }
                case "clean" -> {
                    logger.info("========================回收垃圾=========================");
                    Clean clean = new Clean();
                    clean.start();
                    logger.info("======================================================");
                }
                case "cache" -> {
                    logger.info("========================刷新缓存=========================");
                    //目前没做
                    logger.info("======================================================");
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
                            logger.info("=====================在线连接列表======================");
                            logger.info("当前存在{}个连接", Console.Clients.size());
                            for (Map.Entry<String, Client> client : Console.Clients.entrySet()) {
                                logger.info("{}  通过 {} 连接到 {} 登录用户名 {} 社区ID {}", client.getKey(), client.getValue().getTo_server_socket().getLocalPort(), client.getValue().getServer().name(), client.getValue().getUsername(), client.getValue().getUserid());
                            }
                            logger.info("======================================================");
                        }
                    }
                }
                case "info" -> {
                    logger.info("=====================统计信息======================");
                    logger.info("\033[32m正常运行\033[0m \033[33m{} \033[0m", getDatePoor(Console.info.getTime(), System.currentTimeMillis()));
                    logger.info("      \033[32m放行\033[0m      \033[31m拦截\033[0m");
                    logger.info("连接   \033[32m{}\033[0m       \033[31m{}\033[0m", Console.info.getNormal(), Console.info.getAbnormal());
                    logger.info("IP   \033[32m{}\033[0m       \033[31m{}\033[0m", Console.info.getNormal_ip().size(), Console.info.getAbnormal_ip().size());
                    logger.info("\033[32m缓存情况\033[0m  \033[32m{}\033[0m/\033[32m{}\033[0m [\033[32m穿透\033[0m/\033[32m应答\033[0m]", Console.info.getRefresh(), Console.info.getRespond());
                    logger.info("\033[33m丢弃无效数据包\033[0m \033[31m{}\033[0m", Console.info.getInvalid());
                    logger.info("======================================================");
                }
                case "stop" -> {
                    logger.info("========================停止程序=========================");
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
                            logger.info("========================踢出{}=========================", commands[1]);
                            for (Map.Entry<String, Client> client : Console.Clients.entrySet()) {
                                if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(command3)) {
                                    disconnect(client.getValue());
                                    logger.info("已踢出 {}", client.getKey());
                                }
                            }
                            logger.info("======================================================");
                        }
                        default -> logger.info("\033[31m命令缺少参数：{}\033[0m", command);
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
                        default -> logger.warn("命令错误：{}", command);
                    }

                }
                default -> logger.warn("命令不存在：{}", command);
            }
        } catch (Exception e) {
            logger.error("执行命令时出错", e);
        }
    }
}