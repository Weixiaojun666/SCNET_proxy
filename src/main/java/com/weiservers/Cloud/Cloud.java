package com.weiservers.Cloud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.Base.BanArea;
import com.weiservers.Base.BanIp;
import com.weiservers.Base.BanUser;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.weiservers.Core.HttpClient.HttpClient;

public class Cloud {
    private final static Logger logger = LoggerFactory.getLogger(Cloud.class);

    private static String access_token;

    public Cloud() {
    }

    public static void getBanArea(String page) {
        if (page.equals("")) page = "1";
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/getArea?token=" + access_token + "&page=" + page);
            if (rootNode != null) {
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
                int count = rootNode.at("/count").asInt();
                ObjectMapper objectMapper = new ObjectMapper();
                List<BanArea> BanArealist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
                });
                System.out.println("======================================================");
                System.out.printf("%-16s %-24s %-24s %-16s %-16s%n", "ID", "信息1", "信息2", "信息3", "运营商");
                for (BanArea banArea : BanArealist) {
                    System.out.printf("%-16s %-24s %-24s %-16s %-16s%n", banArea.id(), banArea.info1(), banArea.info2(), banArea.info3(), banArea.isp());
                }
                System.out.println("======================================================");
                System.out.printf("当前第%s页 共%s页 总计%s条%n", page, (int) Math.ceil(count * 1.0 / 10), count);
            }
        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void getBanUser(String page) {
        if (page.equals("")) page = "1";
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/getUser?token=" + access_token + "&page=" + page);
            if (rootNode != null) {
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
                int count = rootNode.at("/count").asInt();
                ObjectMapper objectMapper = new ObjectMapper();
                List<BanUser> Banuserlist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
                });
                System.out.println("======================================================");
                System.out.printf("%-16s %-24s %-24s%n", "id", "用户名", "社区ID");
                for (BanUser banUser : Banuserlist) {
                    System.out.printf((String.format("%-16s %-24s %-24s", banUser.id(), banUser.username(), banUser.userid())) + "%n");
                }
                System.out.println("======================================================");
                System.out.printf("当前第%s页 共%s页 总计%s条%n", page, (int) Math.ceil(count * 1.0 / 10), count);
            }

        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void getBanIp(String page) {
        if (page.equals("")) page = "1";
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/getIp?token=" + access_token + "&page=" + page);
            if (rootNode != null) {
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
                int count = rootNode.at("/count").asInt();

                ObjectMapper objectMapper = new ObjectMapper();
                List<BanIp> Baniplist = objectMapper.readValue(rootNode.at("/data").toString(), new TypeReference<>() {
                });

                System.out.println("======================================================");
                System.out.printf("%-16s %-24s %n", "id", "ip");
                for (BanIp banIp : Baniplist) {
                    System.out.printf("%-16s %-24s %n", banIp.id(), banIp.ip());
                }
                System.out.println("======================================================");
                System.out.printf("当前第%s页 共%s页 总计%s条%n", page, (int) Math.ceil(count * 1.0 / 10), count);
            } else {

            }
        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void banuser(String username, String reason, String time, String state) {
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state=" + state + "&token=" + access_token + "&username=" + username + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/saveUser?" + url);
            if (rootNode != null)
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
            else
                System.out.print("操作失败");
        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void banip(String ip, String reason, String time, String state) {
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state=" + state + "&token=" + access_token + "&ip=" + ip + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/saveIp?" + url);
            if (rootNode != null)
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
            else
                System.out.print("操作失败");
        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void banarea(String ip, String reason, String time, String state) {
        if (access_token == null) {
            System.out.println("\033[33m 请登录后使用此功能  \033[0m");
            return;
        }
        try {
            String url = "state=" + state + "&token=" + access_token + "&ip=" + ip + "&reason=" + reason + "&time=" + time;
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/ban/saveAreaByIp?" + url);
            if (rootNode != null)
                System.out.printf("操作成功:%s%n", rootNode.at("/msg").toString());
            else
                System.out.print("操作失败");
        } catch (Exception e) {
            System.out.printf("操作失败:%s%n", e);
        }
    }

    public static void postloginvalid(String ip, long start_time, long end_time, int num) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (access_token == null) return;
        String url = "token=" + access_token + "&ip=" + ip + "&start_time=" + start_time + "&end_time=" + end_time + "&num=" + num;
        HttpClient("https://api.weiservers.com/scnet/log/invalid?" + url);
    }

    public static void postloguser(String id) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (access_token == null) return;
        String url = "token=" + access_token + "&id=" + id;
        HttpClient("https://api.weiservers.com/scnet/log/user?" + url);
    }

    public static String postloguser(String userid, String username, String ip, String servername, String reason, String state) {
        if (!(boolean) Main.getSetting().get("cloud")) return "0";
        if (access_token == null) return "0";
        try {
            String url = "token=" + access_token + "&userid=" + userid + "&name=" + username + "&ip=" + ip + "&server=" + servername + "&reason=" + reason + "&state=" + state;
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/log/user?" + url);
            return rootNode.at("/data/WeiId").toString().replace("\"", "");
        } catch (Exception e) {
            logger.error("尝试上报登录记录时出现错误", e);
        }
        return "0";
    }

    public static void Load() {
        try {
            JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/Index/login?token=" + Main.getSetting().get("token"));
            if (rootNode == null) {
                logger.warn("无法登录到WeiServers 功能将受限");
            } else {
                String username = rootNode.at("/data/username").toString();
                access_token = rootNode.at("/data/access_token").toString().replace("\"", "");
                logger.info("已成功登录WeiServers 登录用户名{}", username);
            }
        } catch (Exception e) {
            logger.error("尝试登录WeiServers平台时出现错误", e);
        }
    }
}