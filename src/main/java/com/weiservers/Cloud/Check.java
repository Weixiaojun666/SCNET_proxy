package com.weiservers.Cloud;

import com.fasterxml.jackson.databind.JsonNode;
import com.weiservers.Base.Client;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.weiservers.Core.HttpClient.HttpClient;
import static com.weiservers.Core.Tools.disconnect;

public class Check extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Check.class);
    private static Client client;
    private static String token;

    private static boolean whitename = false;
    private static String reason = "";

    private static String IP_info = "";

    private static String userstate = "";

    public Check(Client client, String token) {
        Check.client = client;
        Check.token = token.substring(6, 8) + token.substring(4, 6) + token.substring(2, 4) + token.substring(0, 2) + token.substring(10, 12) + token.substring(8, 10) + token.substring(14, 16) + token.substring(12, 14) + token.substring(16);
    }

    public static boolean checklimit() {
        try {
            int num = 0;
            int numuser = 0;
            for (Map.Entry<String, Client> client0 : Main.Clients.entrySet()) {
                if (client0.getKey().substring(0, client0.getKey().indexOf(":")).equals(client.getAddress().toString())) {
                    num++;
                }
                if (client0.getValue().getUserid().equals(client.getUserid())) {
                    numuser++;
                }
            }
            if (numuser > (int) Main.getSetting().get("connection_limit")) {
                logger.warn("{}用户名 {} ID{} 存在一号多登行为，此次连接将被拒绝", client.getAddress(), client.getUsername(), client.getUserid());
                reason = "存在一号多登行为";
                disconnect(client);
                Main.info.addAbnormal();
                return false;
            }
            if (num > (int) Main.getSetting().get("connection_limit")) {
                logger.warn("{}的连接数达到{} 超过上限，此次连接将被拒绝 使用用户名{} ID{}", client.getAddress(), num, client.getUsername(), client.getUserid());
                reason = "连接数超过" + Main.getSetting().get("connection_limit");
                disconnect(client);
                Main.info.addAbnormal();
                return false;
            }
        } catch (Exception e) {
            logger.info("来自{}的连接,检查失败,此次请求检查已临时停止,此次将被默认允许{}", client.getAddress(), e);
        }
        return true;
    }

    public boolean checkuser() {
        //社区验证
        JsonNode rootNode = HttpClient("https://m.schub.top/com/checkuser?token=" + token, false);
        if (rootNode == null) {
            logger.error("{}社区认证失败 无法连接到社区 将默认放行", client.getAddress());
            return true;
        }
        String code = rootNode.at("/code").toString();
        if (!code.equals("200")) {
            reason = "未通过社区验证";
            logger.warn("{}社区验证失败", client.getAddress());
            return false;
        }
        client.setUserid(rootNode.at("/data/id").toString().replace("\"", ""));
        client.setUsername(rootNode.at("/data/nickname").toString().replace("\"", ""));
        return true;
    }

    public boolean checkip() {
        JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/index/checkip?ip=" + client.getAddress().toString().substring(1), false);
        if (rootNode == null) {
            logger.error("{}检查失败 无法连接到WeiServers 将默认放行", client.getAddress());
            return true;
        }
        String code = rootNode.at("/code").toString();
        String info1 = rootNode.at("/data/info1").toString();
        String info2 = rootNode.at("/data/info2").toString();
        String info3 = rootNode.at("/data/info3").toString();
        String isp = rootNode.at("/data/isp").toString();
        switch (code) {
            case "200", "950", "954", "951", "952", "953" -> {
                IP_info = info1 + info2 + info3 + isp;
                //logger.warn("{}来自 {} {} {} 运营商{} 已被封禁 封禁代码{}", client.getAddress(), info1, info2, info3, isp, code);
                reason = "在区域封禁范围内";
                return false;
            }
            default -> logger.error("获取{}详细信息时发生错误：接口返回值不是预期 将默认放行", client.getAddress());
        }
        return true;
    }

    public boolean checkstate() {
        JsonNode rootNode = HttpClient("https://api.weiservers.com/scnet/index/checkuser?userid=" + client.getUserid(), false);
        if (rootNode == null) {
            logger.error("{}检查失败 无法连接到WeiServers 将默认放行", client.getUsername());
            return true;
        }
        String code = rootNode.at("/code").toString();
        switch (code) {
            case "200" -> {
                userstate = "普通用户";
                return true;
            }
            case "220" -> {
                userstate = "白名单用户";
                whitename = true;
                return true;
            }
            case "403" -> {
                userstate = "黑名单用户";
                reason = "黑名单用户";
                //logger.warn("{}  {} {} 为黑名单用户", client.getAddress(), client.getUserid(), client.getUsername());
                return false;
            }
        }
        return false;
    }

    public void run() {
        boolean flag = false;
        if (checkuser() && checkstate() && checklimit() && (checkip() || whitename)) {
            if (!whitename) {
                reason = "正常登录";
            }
            if (checkip()) {
                flag = true;
            } else if (whitename) {
                reason = "使用白名单跳过区域封禁";
                flag = true;
            }
        }
        String state = "登录成功";
        if (!flag) {
            state = "登录失败";
            disconnect(client);
            Main.info.getAbnormal_ip().add(client.getAddress());
            Main.info.addAbnormal();
        } else {
            if (!whitename) reason = "正常登录";
            logger.info("{} 玩家 {} ID{} 状态{} 来自{} {}", client.getAddress(), client.getUsername(), client.getUserid(), userstate, IP_info, reason, state);
        }
        client.setWeiid(Cloud.postloguser(client.getUserid(), client.getUsername(), client.getAddress().toString(), client.getServer().name(), reason, state));
    }
}