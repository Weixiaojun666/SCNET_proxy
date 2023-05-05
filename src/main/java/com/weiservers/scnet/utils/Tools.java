package com.weiservers.scnet.utils;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Invalid;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.cloud.Cloud;
import com.weiservers.scnet.thread.child.Clean;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static ch.qos.logback.core.encoder.ByteArrayUtil.hexStringToByteArray;
import static com.weiservers.scnet.utils.DataConvertUtils.bytesToHexString;
import static com.weiservers.scnet.utils.DataConvertUtils.hexStringToString;


public class Tools {
    public static String GetTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
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
        }
    }


    public static void stopservice() {
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

    public static void ReloadCache(Motd motd, Server server) {
        try {
            Main.info.addRefresh();
            byte[] ans = hexStringToByteArray("0804");
            DatagramPacket motd_packet = new DatagramPacket(ans, ans.length, InetAddress.getByName(server.address()), server.port());
            motd.getSocket().send(motd_packet);
        } catch (Exception e) {
        }
    }


    public static String getDatePoor(Long startTime, Long endTime) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endTime - startTime;
        long day = diff / nd;
        long hour = diff % nd / nh;
        long min = diff % nd % nh / nm;
        long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟" + sec + "秒";
    }


}
