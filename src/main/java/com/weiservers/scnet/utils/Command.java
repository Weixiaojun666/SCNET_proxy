package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.bean.record.Whitelist;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.child.Clean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.weiservers.scnet.utils.Tools.*;

public class Command {
    private static final String USERID_REGEX = "^[0-9]+$";

    public static void Clean() {
        ThreadPool.execute(new Clean());
        System.gc();
    }

    public static void Reload() {
        stopservice();
//                    //  Main.ConfigLoad();
//                    // Main.ServerLoad(Main.serverlist);
    }


    public static void Cache() {
        for (ServerThread serverThread : Main.serverThreads) {
            ReloadCache(serverThread.motd(), serverThread.server());
        }
    }

    public static void Stop() {
        stopservice();
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
                System.out.println("未找到符合条件的用户,请检查用户名/用户ID/ip地址是否正确,并确保在线");
            }
        }
        return clients;
    }

    public static void KickUser(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;

        for (Client client : clients) {
            disconnect(client);
            System.out.printf("已踢出 %s ID: %s %n", client.getUsername(), client.getUserid());
        }
    }

    public static void KickArea(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client0 : clients) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client0.equalArea(client.getValue(), Configuration.getSetting().base().area_leave())) {
                    disconnect(client.getValue());
                    System.out.printf("已踢出 %s ID: %s %n", client.getValue().getUsername(), client.getValue().getUserid());
                }
            }
        }
    }

    public static void KickAll() {
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            disconnect(client.getValue());
            System.out.printf("已踢出 %s ID: %s %n", client.getValue().getUsername(), client.getValue().getUserid());
        }
    }

    public static void BanAddUser(String str, int expires, String reason) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client : clients) {
            disconnect(client);
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getUserid(), client.getUsername(), expires, reason, GetTime()));
            System.out.printf("[user]已踢出并拉黑 %s ID: %s %n", client.getUsername(), client.getUserid());
        }
    }

    public static void BanAddIp(String str, int expires, String reason) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client : clients) {
            disconnect(client);
            Configuration.getBanned().bannedIps().add(new Banned.BannedIps(client.getAddress().toString(), expires, reason, GetTime()));
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getUserid(), client.getUsername(), expires, "IP:" + client.getAddress().toString(), GetTime()));
            System.out.printf("[user]已踢出并拉黑 %s ID: %s %n", client.getUsername(), client.getUserid());
            System.out.printf("[ip]已添加 %s 到黑名单%n", client.getAddress().toString());
        }
    }

    public static void BanAddArea(String ip, int expires, String reason) {
        List<Client> clients = FoundClient(ip);
        if (clients.size() == 0) return;
        for (Client client0 : clients) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client0.equalArea(client.getValue(), Configuration.getSetting().base().area_leave())) {
                    disconnect(client.getValue());
                    Configuration.getBanned().bannedAreas().add(new Banned.BannedAreas(client.getValue().getInfo1(), client.getValue().getInfo2(), client.getValue().getInfo3(), client.getValue().getIsp(), expires, reason, GetTime()));
                    Configuration.getBanned().bannedIps().add(new Banned.BannedIps(client.getValue().getAddress().toString(), expires, reason, GetTime()));
                    Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getValue().getUserid(), client.getValue().getUsername(), expires, "IP:" + client.getValue().getAddress().toString(), GetTime()));
                    System.out.printf("[user]已踢出并拉黑 %s ID: %s %n", client.getValue().getUsername(), client.getValue().getUserid());
                    System.out.printf("[ip]已添加 %s 到黑名单%n", client.getValue().getAddress().toString());
                    System.out.printf("[area]已添加 %s %s %s %s 到黑名单%n", client.getValue().getInfo1(), client.getValue().getInfo2(), client.getValue().getInfo3(), client.getValue().getIsp());
                }
            }
        }
    }


    public static void BanRemoveUser(String user) {
        boolean isfound = false;
        for (Banned.BannedPlayers bannedPlayers : Configuration.getBanned().bannedPlayers()) {
            if (bannedPlayers.username().equals(user)) {
                Configuration.getBanned().bannedPlayers().remove(bannedPlayers);
                System.out.printf("已移除 %s 的黑名单%n", user);
                isfound = true;
            }
        }
        if (!isfound) System.out.println("未找到该IP,请检查IP是否正确");


    }

    public static void BanRemoveIp(String ip) {
        boolean isbanned = false;
        for (Banned.BannedIps bannedIps : Configuration.getBanned().bannedIps()) {
            if (bannedIps.ip().equals(ip)) {
                Configuration.getBanned().bannedIps().remove(bannedIps);
                System.out.printf("已移除 %s 的黑名单%n", ip);
                isbanned = true;
            }
        }
        if (!isbanned) System.out.println("未找到该IP,请检查IP是否正确");
    }

    public static void WhitelistAdd(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client : clients) {
            Configuration.getWhitelist().whiteList().add(new Whitelist.White(client.getUserid(), client.getUsername(), GetTime()));
            System.out.printf("已添加 %s 到白名单%n", str);
        }
    }

    public static void WhitelistRemove(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) return;
        for (Client client : clients) {
            Configuration.getWhitelist().whiteList().remove(new Whitelist.White(client.getUserid(), client.getUsername(), GetTime()));
            System.out.printf("已移除 %s 的白名单%n", str);
        }
    }
}
