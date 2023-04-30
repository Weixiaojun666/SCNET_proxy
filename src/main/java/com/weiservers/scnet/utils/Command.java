package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.child.Clean;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            ReloadCache(serverThread.getMotd(), serverThread.getServer());
        }
    }

    public static void Stop() {
        stopservice();
//                    ThreadPool.shutdown();
//                    System.exit(0);
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
            return clients;
        } else {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client.getValue().getUsername().equals(input)) {
                    clients.add(client.getValue());
                }
            }

            return clients;
        }
    }

    public static void KickUser(String str) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) {
            System.out.println("未找到该用户,请检查用户名是否正确");
            return;
        }
        for (Client client : clients) {
            disconnect(client);
            System.out.printf("已踢出 %s ID: %s %n", client.getUsername(), client.getUserid());
        }
    }

    public static void KickArea(String str, int leave) {
        List<Client> clients = FoundClient(str);
        if (clients.size() == 0) {
            System.out.println("未找到该用户,请检查用户名是否正确");
            return;
        }
        for (Client client0 : clients) {
            for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
                if (client0.equalArea(client.getValue(), leave)) {
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
        if (clients.size() == 0) {
            System.out.println("未找到该用户,请检查用户名是否正确");
            return;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date date = new Date(System.currentTimeMillis());
        for (Client client : clients) {
            disconnect(client);
            Configuration.getBanned().bannedPlayers().add(new Banned.BannedPlayers(client.getUserid(), client.getUsername(), expires, reason, formatter.format(date)));
            System.out.printf("已踢出 %s ID: %s %n", client.getUsername(), client.getUserid());
            System.out.printf("已添加 %s ID: %s 到黑名单%n", client.getUsername(), client.getUserid());
        }
    }

    public static void BanAddIp(String ip, int expires, String reason) {

//        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
//        Date date = new Date(System.currentTimeMillis());
//        Boolean isbanned = false;
//        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
//            if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(ip)) {
//                disconnect(client.getValue());
//              //  ConfigLoad.getBanned().bannedIps().add( new Banned.BannedIps(client.getKey(),expires,reason,formatter.format(date)));
//                System.out.printf("已踢出 %s %n", client.getKey());
//                System.out.printf("已添加 %s 到黑名单%n",ip);
//                isbanned=true;
//            }
//        }
//        if (!isbanned) System.out.println("未找到该IP,请检查IP是否正确");

    }

    public static void BanAddArea(String ip, int expires, String reason) {

//        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
//        Date date = new Date(System.currentTimeMillis());
//        Boolean isbanned = false;
//        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
//            if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(ip)) {
//                disconnect(client.getValue());
//                //  ConfigLoad.getBanned().bannedIps().add( new Banned.BannedIps(client.getKey(),expires,reason,formatter.format(date)));
//                System.out.printf("已踢出 %s %n", client.getKey());
//                System.out.printf("已添加 %s 到黑名单%n",ip);
//                isbanned=true;
//            }
//        }
//        if (!isbanned) System.out.println("未找到该IP,请检查IP是否正确");

    }

    public static void BanRemoveUser(String user) {
//        Boolean isbanned = false;
//        for (Banned.BannedPlayers bannedPlayers : Configuration.getBanned().bannedPlayers()) {
//            if (bannedPlayers.getUsername().equals(user)) {
//                Configuration.getBanned().bannedPlayers().remove(bannedPlayers);
//                System.out.printf("已移除 %s 的黑名单%n",user);
//                isbanned=true;
//            }
//        }
//        if (!isbanned) System.out.println("未找到该用户,请检查用户名是否正确");
    }

    public static void BanRemoveIp(String ip) {
//        Boolean isbanned = false;
//        for (Banned.BannedIps bannedIps : Configuration.getBanned().bannedIps()) {
//            if (bannedIps.getIp.equals(ip)) {
//                Configuration.getBanned().bannedIps().remove(bannedIps);
//                System.out.printf("已移除 %s 的黑名单%n",ip);
//                isbanned=true;
//            }
//        }
//        if (!isbanned) System.out.println("未找到该IP,请检查IP是否正确");
    }

    public static void BanRemoveArea(String area) {
//        Boolean isbanned = false;
//        for (Banned.BannedAreas bannedAreas : Configuration.getBanned().bannedAreas()) {
//            if (bannedAreas.getArea().equals(area)) {
//                Configuration.getBanned().bannedAreas().remove(bannedAreas);
//                System.out.printf("已移除 %s 的黑名单%n",area);
//                isbanned=true;
//            }
//        }
//        if (!isbanned) System.out.println("未找到该地区,请检查地区是否正确");
    }

    public enum StringType {USERNAME, USERID, IPV4, IPV6}


}
