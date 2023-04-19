package com.weiservers.scnet.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.*;
import com.weiservers.scnet.cloud.Cloud;
import com.weiservers.scnet.thread.Child.Clean;

import java.io.File;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class Tools {
    public static List<Whitelist> readWhitelist() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(new File("Config/whitelist.json"));
            List<Whitelist> whitelists = objectMapper.readValue(rootNode.toString(), new TypeReference<>() {
            });
            return whitelists;
        } catch (Exception e) {

        }
        return null;
    }

    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, StandardCharsets.UTF_8);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
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
            if (!serverThread.getThread().isAlive()) serverThread.getThread().interrupt();
            if (!serverThread.getMotd().getThread().isAlive()) serverThread.getMotd().getThread().interrupt();
            if (!serverThread.getMotd().getSocket().isClosed()) serverThread.getMotd().getSocket().close();
            if (!serverThread.getDatagramSocket().isClosed()) serverThread.getDatagramSocket().close();
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
            byte[] ans = Tools.hexStringToByteArray("0804");
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

    public static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hexString) {
        hexString = hexString.replaceAll(" ", "");
        int len = hexString.length();
        byte[] bytes = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return bytes;
    }
}
