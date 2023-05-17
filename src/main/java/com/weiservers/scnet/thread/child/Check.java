package com.weiservers.scnet.thread.child;

import com.fasterxml.jackson.databind.JsonNode;
import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.config.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static com.weiservers.scnet.utils.Command.disconnect;
import static com.weiservers.scnet.utils.HttpUtils.HttpGet;

public class Check extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Check.class);
    private static final String userstate = "";
    private static final boolean whitename = false;
    private static Client client;
    private static String token;
    private static String reason = "";
    private static String IP_info = "";

    public Check(Client client, String token) {
        Check.client = client;
        Check.token = token.substring(6, 8) + token.substring(4, 6) + token.substring(2, 4) + token.substring(0, 2) + token.substring(10, 12) + token.substring(8, 10) + token.substring(14, 16) + token.substring(12, 14) + token.substring(16);
    }

    public boolean CheckWeiServer() {
        String url = "https://api.weiservers.com/scnet/apply/check?token=" + "&userid=" + client.getUserid() + "&username=" + client.getUsername() + "&servername=" + client.getServer().name() + "&ip=" + client.getAddress().toString().substring(1);
        JsonNode rootNode = HttpGet(url);
        if (rootNode == null) {
            logger.error("{} 检查失败 无法连接到WeiServers 将默认放行", client.getUsername());
            return true;
        }
        int code = rootNode.at("/code").intValue();
        client.setCheckId(UUID.fromString(rootNode.at("/data/check").toString().replace("\"", "")));
        reason = rootNode.at("/msg").toString().replace("\"", "");
        return code == 200;
    }

    public boolean CheckLocal() {
        if (Configuration.getSetting().basicConfig().maxConnections() <= 0) return true;
        try {
            int num = 0;
            //判断相同IP的有没有重复建立链接
            for (Map.Entry<String, Client> client0 : Main.Clients.entrySet()) {
                if (client0.getValue().getAddress().toString().equals(client.getAddress().toString())) {
                    num++;
                    //同ip 同id的 连接到同服务器的 为断线重连 不阻止
                    if (client0.getValue().getUserid() == (client.getUserid()))
                        if (client0.getValue().getServer().name().equals(client.getServer().name()))
                            num--;
                }
            }
            if (num > Configuration.getSetting().basicConfig().maxConnections()) {
                reason = "连接数超过" + Configuration.getSetting().basicConfig().maxConnections();
                disconnect(client);
                return false;
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    public boolean CheckUser() {
        //连接社区获取玩家信息
        JsonNode rootNodeUser = HttpGet("https://m.schub.top/com/checkuser?token=" + token);
        if (rootNodeUser == null) {
            reason = "社区检查失败";
            logger.error("{} 社区检查失败", client.getAddress().toString());
            return false;
        }
        client.setUserid(Integer.parseInt(rootNodeUser.at("/data/id").toString().replace("\"", "")));
        client.setUsername(rootNodeUser.at("/data/nickname").toString().replace("\"", ""));
        return true;
    }

    public boolean CheckIp() {
        //连接到公开IP地址API获取IP信息[不再从WeiServer拉取]  接口由VORE-API(https://api.vore.top/)免费提供
        JsonNode rootNodeIp = HttpGet("https://api.vore.top/api/IPdata?ip=" + client.getAddress().toString().substring(1));
        if (rootNodeIp != null) {
            String info1 = rootNodeIp.at("/ipdata/info1").toString().replace("\"", "");
            String info2 = rootNodeIp.at("/ipdata/info2").toString().replace("\"", "");
            String info3 = rootNodeIp.at("/ipdata/info3").toString().replace("\"", "");
            String isp = rootNodeIp.at("/ipdata/isp").toString().replace("\"", "");

            IP_info = info1;
            if (!info2.equals("")) IP_info = IP_info + " " + info2;
            if (!info3.equals("")) IP_info = IP_info + " " + info3;
            if (!isp.equals("")) IP_info = IP_info + " 运营商:" + isp;

            if (!Configuration.getSetting().localProtection().denyOverseasLogin())
                return info1.endsWith("省") || info1.endsWith("市");

        } else logger.warn("{} 拉取IP地址信息失败！不影响WeiServer的区域封禁", client.getAddress().toString());
        return true;
    }

    public boolean check() {
        reason = "正常登录";
        //连接数超限直接断开链接 [防小号]
        if (!CheckLocal()) return false;
        //社区检查不通过直接断开链接 [防未注册用户]
        if (!CheckUser()) return false;
        //IP检查不通过直接断开链接 [阻止海外用户登录]
        if (!CheckIp()) return false;
        //询问WeiServers平台 [绑定QQ 区域封禁 用户黑名单]
        boolean CheckWeiServer = CheckWeiServer();

        //只允许本地白名单

        //使用WeiServer的白名单


        //开启只限白名单进入
        // if ((boolean) Main.getSetting().get("whitelist") && (!whitename)) {
        //    reason = "服务器只允许本地白名单进入";
        //    return false;
        // }
//        if ((boolean) Main.getSetting().get("verification")) {
//            //连接WeiServers 询问是否可放行

//                //如果判断失败,就读取白名单判断是否在白名单内
//                reason = rootNode.at("/msg").toString().replace("\"", "");
//                if (whitename) {
//                    reason = "使用本地白名单跳过检查";
//                    return true;
//                }
//                return false;
//            }
//
//        }

        return true;
    }

    @Override
    public void run() {
        String state = "登录成功";
        if (!check()) {
            state = "登录失败";
            disconnect(client);
            Main.info.getAbnormal_ip().add(client.getAddress());
            Main.info.addAbnormal();
            logger.warn("{} 玩家 [{}] ID [{}] {} 来自 {} {} {}", client.getAddress(), client.getUsername(), client.getUserid(), userstate, IP_info, reason, state);
        } else
            logger.info("{} 玩家 [{}] ID [{}] {} 来自 {} {} {}", client.getAddress(), client.getUsername(), client.getUserid(), userstate, IP_info, reason, state);


        //Cloud.postlogin(String.valueOf(client.getCheckid()), state, reason);
    }
}