package com.weiservers.Cloud;

import com.weiservers.Base.Client;
import com.weiservers.Console.Console;
import com.fasterxml.jackson.databind.JsonNode;
import com.weiservers.GetConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;

import static com.weiservers.Core.Tools.HttpClientW;
import static com.weiservers.Core.Tools.disconnect;

public class Check extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Check.class);
    private static Client client;
    private static InetAddress address;
    private static String token;

    private static boolean whitename = false;
    private static String reason = "";

    public Check(InetAddress clientAddress, Client client, String token) {
        address = clientAddress;
        Check.client = client;
        Check.token = token.substring(6, 8) + token.substring(4, 6) + token.substring(2, 4) + token.substring(0, 2) + token.substring(10, 12) + token.substring(8, 10) + token.substring(14, 16) + token.substring(12, 14) + token.substring(16);
    }

    public static boolean checklimit() {
        int num = 0;
        int numuser = 0;
        for (Map.Entry<String, Client> client0 : Console.Clients.entrySet()) {
            if (client0.getKey().substring(0, client0.getKey().indexOf(":")).equals(address.toString())) {
                num++;
            }
            if (client0.getValue().getUserid().equals(client.getUserid())) {
                numuser++;
            }
        }
        if (numuser > (int) GetConfig.getSetting().get("connection_limit")) {
            logger.warn("来自{}的连接 用户名 {} 存在一号多登行为，此次连接将被拒绝", address, client.getUsername());
            reason = "存在一号多登行为";
            disconnect(client);
            Console.info.addAbnormal();
            return false;
        }
        if (num > (int) GetConfig.getSetting().get("connection_limit")) {
            logger.warn("来自{}的连接数达到{} 超过上限，此次连接将被拒绝", address, num);
            reason = "连接数超过" + GetConfig.getSetting().get("connection_limit");
            disconnect(client);
            Console.info.addAbnormal();
            return false;
        }
        return true;
    }

    public boolean checkuser() {
        JsonNode rootNode = HttpClientW("https://m.schub.top/com/checkuser?token=" + token);
        if (rootNode == null) {
            logger.error("{}社区认证失败 无法连接到社区 将默认放行", address);
            return true;
        }
        String code = rootNode.at("/code").toString();
        client.setUserid(rootNode.at("/data/id").toString().replace("\"", ""));
        client.setUsername(rootNode.at("/data/nickname").toString().replace("\"", ""));
        if (code.equals("200")) {
            logger.info("{} 登陆ID{} 用户名{}", address, client.getUserid(), client.getUsername());
            return true;
        }
        reason = "未通过社区验证";
        logger.warn("{}社区验证失败", address);
        return false;
    }

    public boolean checkip() {
        JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/index/checkip?ip=" + address.toString().substring(1));
        if (rootNode == null) {
            logger.error("{}检查失败 无法连接到WeiServers 将默认放行", address);
            return true;
        }
        String code = rootNode.at("/code").toString();
        String info1 = rootNode.at("/data/info1").toString();
        String info2 = rootNode.at("/data/info2").toString();
        String info3 = rootNode.at("/data/info3").toString();
        String isp = rootNode.at("/data/isp").toString();
        switch (code) {
            case "950", "954", "951", "952", "953" -> {
                logger.warn("{}来自 {} {} {} 运营商{} 已被封禁 封禁代码{}", address, info1, info2, info3, isp, code);
                reason = "在区域封禁范围内";
                return false;
            }
            case "200" -> logger.info("{}来自 {} {} {} 运营商{}", address, info1, info2, info3, isp);
            default -> logger.error("获取{}详细信息时发生错误：接口返回值不是预期 将默认放行", address);
        }
        return true;
    }

    public boolean checkuser0() {
        JsonNode rootNode = HttpClientW("https://api.weiservers.com/scnet/index/checkuser?userid=" + client.getUserid());
        if (rootNode == null) {
            logger.error("{}检查失败 无法连接到WeiServers 将默认放行", client.getUsername());
            return true;
        }
        String code = rootNode.at("/code").toString();
        switch (code) {
            case "0" -> {
                return true;
            }
            case "200" -> {
                whitename = true;
                return true;
            }
            case "300" -> {
                reason = "黑名单用户";
                logger.warn("{}  {} {} 为黑名单用户", address, client.getUserid(), client.getUsername());
                return false;
            }
        }
        return false;
    }

    public void run() {
        boolean flag = false;
        if (checkuser() && checkuser0() && checklimit()) {
            if (checkip()) {
                flag = true;
            } else if (whitename) {
                reason = "使用白名单跳过区域封禁";
                logger.info("{} 玩家 {} 使用白名单跳过了区域封禁", address, client.getUsername());
                flag = true;
            }
        }
        String state= "登录成功";
        if (!flag) {
            state = "登录失败";
            disconnect(client);
            Console.info.getAbnormal_ip().add(address);
            Console.info.addAbnormal();
        } else {
            if(!whitename)reason = "正常登录";
        }
        client.setWeiid(Cloud.postloguser(client.getUserid(), client.getUsername(), address.toString(), client.getServer().name(), reason, state));
    }
}