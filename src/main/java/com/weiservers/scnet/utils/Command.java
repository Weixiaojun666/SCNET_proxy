package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Banned;
import com.weiservers.scnet.thread.child.Clean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.weiservers.scnet.utils.Tools.*;

public class Command {

    public static void Wclean() {
        ThreadPool.execute(new Clean());
        System.gc();
    }

    public static void Wreload() {
        stopservice();
//                    //  Main.ConfigLoad();
//                    // Main.ServerLoad(Main.serverlist);
    }

    public static void Wcache() {
        for (ServerThread serverThread : Main.serverThreads) {
            ReloadCache(serverThread.getMotd(), serverThread.getServer());
        }
    }

    public static void Wkick_user(String user) {
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            if (client.getValue().getUsername().equals(user)) {
                disconnect(client.getValue());
                System.out.printf("已踢出 %s%n", client.getValue().getUsername());
            }
        }
    }

    public static void Wkick_ip(String ip) {
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(ip)) {
                disconnect(client.getValue());
                System.out.printf("已踢出 %s%n", client.getKey());
            }
        }
    }
    public static void Wstop(){
        stopservice();
//                    ThreadPool.shutdown();
//                    System.exit(0);
    }
    public static void Wbanadduser(String user,int expires,String reason){

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date date = new Date(System.currentTimeMillis());
        Boolean isbanned = false;
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            if (client.getValue().getUsername().equals(user)) {
                disconnect(client.getValue());
                ConfigLoad.getBanned().bannedPlayers().add( new Banned.BannedPlayers(client.getValue().getUserid(),client.getValue().getUsername(),expires,reason,formatter.format(date)));
                System.out.printf("已踢出 %s ID: %s %n", client.getValue().getUsername(),client.getValue().getUserid());
                System.out.printf("已添加 %s ID: %s 到黑名单%n",user,client.getValue().getUserid());
                isbanned=true;
            }
        }
        if (!isbanned) System.out.println("未找到该用户,请检查用户名是否正确");
    }
    public static void Wbanaddip(String ip ,int expires,String reason){

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
        Date date = new Date(System.currentTimeMillis());
        Boolean isbanned = false;
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            if (client.getKey().substring(1, client.getKey().indexOf(":")).equals(ip)) {
                disconnect(client.getValue());
              //  ConfigLoad.getBanned().bannedIps().add( new Banned.BannedIps(client.getKey(),expires,reason,formatter.format(date)));
                System.out.printf("已踢出 %s %n", client.getKey());
                System.out.printf("已添加 %s 到黑名单%n",ip);
                isbanned=true;
            }
        }
        if (!isbanned) System.out.println("未找到该IP,请检查IP是否正确");

    }


}
