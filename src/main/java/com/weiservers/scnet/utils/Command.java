package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Invalid;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.bean.record.Setting;
import com.weiservers.scnet.bean.record.Whitelist;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.child.Clean;
import com.weiservers.scnet.thread.child.Cloud;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static com.weiservers.scnet.utils.DataConvertUtils.*;

public class Command {
    private static final String USERID_REGEX = "^[0-9]+$";
    private final static Logger logger = LoggerFactory.getLogger(Command.class);

    public static void Clean() {
        ThreadPool.execute(new Clean());
        System.gc();
    }

    public static void Reload() {
        Configuration.SaveConfig();
        stopService();
        Main.Clients.clear();
        Main.Servers.clear();
        Main.Invalids.clear();
        Main.serverThreads.clear();
        Configuration.ReadConfig();
        Main.ServersLoad();
    }


    public static void Cache() {
        for (ServerThread serverThread : Main.serverThreads) {
            ReloadCache(serverThread.motd(), serverThread.server());
        }
    }

    public static void Stop() {
        stopService();
        Main.Clients.clear();
        Main.Servers.clear();
        Main.Invalids.clear();
        Main.serverThreads.clear();
        ThreadPool.shutdown();
        System.exit(0);
    }


    public static List<Client> FoundClient(String input) {
        List<Client> clients = new LinkedList<>();
        try {
            InetAddress address = InetAddress.getByName(input);
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client.getValue().getAddress().equals(address)) {
                    clients.add(client.getValue());
                }
            }
            return clients;
        } catch (UnknownHostException ignored) {
        }
        if (input.matches(USERID_REGEX)) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client.getValue().getUserid() == Integer.parseInt(input)) {
                    clients.add(client.getValue());
                }
            }
        } else {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client.getValue().getUsername().equals(input)) {
                    clients.add(client.getValue());
                }
            }
            if (clients.size() == 0) {
                logger.warn("未找到符合条件的用户,请检查用户名/用户ID/ip地址是否正确,并确保在线");
            }
        }
        return clients;
    }

    public static void KickUser(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;

        for (Client client : clients) {
            disconnect(client);
            logger.info("已踢出 {} ID: {} ", client.getUsername(), client.getUserid());
        }
    }

    public static void KickArea(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client0 : clients) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client0.equalArea(client.getValue(), Configuration.getSetting().basicConfig().regionBlockLevel())) {
                    disconnect(client.getValue());
                    logger.info("已踢出 {} ID: {} ", client.getValue().getUsername(), client.getValue().getUserid());
                }
            }
        }
    }

    public static void KickAll() {
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            disconnect(client.getValue());
            logger.info("已踢出 {} ID: {} ", client.getValue().getUsername(), client.getValue().getUserid());
        }
    }

    public static void BanAddUser(String str, int expires, String reason) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) {
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(Integer.parseInt(str), expires, reason, GetTime()));
            logger.info("[user]在玩家未在线情况下拉黑 ID: {} ", Integer.parseInt(str));
        }
        for (Client client : clients) {
            disconnect(client);
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getUserid(), expires, reason, GetTime()));
            logger.info("[user]已踢出并拉黑 {} ID: {} ", client.getUsername(), client.getUserid());
        }
    }

    public static void BanAddIp(String str, int expires, String reason) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client : clients) {
            disconnect(client);
            Configuration.getBanned().bannedIps().add(new Banned.BannedIps(client.getAddress().toString(), expires, reason, GetTime()));
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getUserid(), expires, "IP:" + client.getAddress().toString(), GetTime()));
            logger.info("[user]已踢出并拉黑 {} ID: {} ", client.getUsername(), client.getUserid());
            logger.info("[ip]已添加 {} 到黑名单", client.getAddress().toString());
        }
    }

    public static void BanAddArea(String ip, int expires, String reason) {
        List<Client> clients = FoundClient(ip);
        if (clients.size() == 0) return;
        for (Client client0 : clients) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client0.equalArea(client.getValue(), Configuration.getSetting().basicConfig().regionBlockLevel())) {
                    disconnect(client.getValue());
                    Configuration.getBanned().bannedAreas().add(new Banned.BannedAreas(client.getValue().getInfo1(), client.getValue().getInfo2(), client.getValue().getInfo3(), client.getValue().getIsp(), expires, reason, GetTime()));
                    Configuration.getBanned().bannedIps().add(new Banned.BannedIps(client.getValue().getAddress().toString(), expires, reason, GetTime()));
                    Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getValue().getUserid(), expires, "IP:" + client.getValue().getAddress().toString(), GetTime()));
                    logger.info("[user]已踢出并拉黑 {} ID: {} ", client.getValue().getUsername(), client.getValue().getUserid());
                    logger.info("[ip]已添加 {} 到黑名单", client.getValue().getAddress().toString());
                    logger.info("[area]已添加 {} {} {} {} 到黑名单", client.getValue().getInfo1(), client.getValue().getInfo2(), client.getValue().getInfo3(), client.getValue().getIsp());
                }
            }
        }
    }


    public static void BanRemoveUser(String user) {
        boolean isFound = false;
        for (Banned.BannedPlayers bannedPlayers : Configuration.getBanned().bannedPlayers()) {
            if (bannedPlayers.userid() == Integer.parseInt(user)) {
                Configuration.getBanned().bannedPlayers().remove(bannedPlayers);
                logger.info("已移除 {} 的黑名单", user);
                isFound = true;
                break;
            }
        }
        if (!isFound) logger.info("未找到该用户,请检查用户ID是否正确");
    }


    public static void BanRemoveIp(String ip) {
        boolean isBanned = false;
        for (Banned.BannedIps bannedIps : Configuration.getBanned().bannedIps()) {
            if (bannedIps.ip().equals(ip)) {
                Configuration.getBanned().bannedIps().remove(bannedIps);
                logger.info("已移除 {} 的黑名单", ip);
                isBanned = true;
                break;
            }
        }
        if (!isBanned) logger.info("未找到该IP,请检查IP是否正确");
    }

    public static void WhitelistAdd(String str) {
        Configuration.getWhitelist().whiteList().add(new Whitelist.White((Integer.parseInt(str))));
        logger.info("已添加 {} 到白名单", str);
    }

    public static void WhitelistRemove(String str) {
        Configuration.getWhitelist().whiteList().remove(new Whitelist.White(Integer.parseInt(str)));
        logger.info("已移除 {} 的白名单", str);
    }

    public static void getServerInfo(Motd motd) {
        try {
            String info = bytesToHexString(motd.getMotd());
            int length = info.length();
            String model;
            switch (info.substring(length - 14, length - 12)) {
                case "00" -> model = "创造";
                case "01" -> model = "无害";
                case "02" -> model = "挑战";
                case "03" -> model = "残酷";
                case "04" -> model = "冒险";
                default -> model = "未知";
            }
            int maxplayer = Integer.parseInt((info.substring(length - 16, length - 14) + info.substring(length - 18, length - 16)), 16);
            int onlineplayer = Integer.parseInt((info.substring(length - 20, length - 18) + info.substring(length - 22, length - 20)), 16);
            String version = hexStringToString(info.substring(12, length - 22));
            motd.setString(model, onlineplayer, maxplayer, version);
        } catch (Exception e) {
            logger.error("解析服务器信息失败");
        }
    }


    public static void stopService() {
        for (ServerThread serverThread : Main.serverThreads) {
            if (!serverThread.thread().isAlive()) serverThread.thread().interrupt();
            if (!serverThread.motd().getThread().isAlive()) serverThread.motd().getThread().interrupt();
            if (!serverThread.motd().getSocket().isClosed()) serverThread.motd().getSocket().close();
            if (!serverThread.datagramSocket().isClosed()) serverThread.datagramSocket().close();
        }
        Main.serverThreads.clear();
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            disconnect(client.getValue());
        }
        for (Map.Entry<InetAddress, Invalid> Invalids : Main.Invalids.entrySet()) {
            Invalids.getValue().setCheck_time(0);
        }
        ThreadPool.execute(new Clean());
        Cloud.ReloadToken();
    }


    public static void disconnect(Client client) {
        if (client.getThread() != null) client.getThread().interrupt();
        if (client.getTo_server_socket() != null) client.getTo_server_socket().close();
        client.setTime(0);
    }

    public static void ReloadCache(Motd motd, Setting.Server server) {
        try {
            Main.info.addRefresh();
            byte[] ans = hexStringToByteArray("0804");
            DatagramPacket motd_packet = new DatagramPacket(ans, ans.length, InetAddress.getByName(server.address()), server.port());
            motd.getSocket().send(motd_packet);
        } catch (Exception e) {
            logger.warn("刷新服务器信息失败");
        }
    }
}
