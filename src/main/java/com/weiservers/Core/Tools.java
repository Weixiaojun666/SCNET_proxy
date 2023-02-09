package com.weiservers.Core;

import com.weiservers.Base.*;
import com.weiservers.Main;
import com.weiservers.Thread.Child.Clean;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Map;

import static com.weiservers.Main.serverThreads;

public class Tools {

    public static void stopservice() {
        for (ServerThread serverThread : Main.serverThreads) {
            serverThread.getThread().interrupt();
            serverThread.getMotd().getThread().interrupt();
            serverThread.getMotd().getSocket().close();
            serverThread.getDatagramSocket().close();
        }
        serverThreads.clear();
        for (Map.Entry<String, Client> client : Main.Clients.entrySet()) {
            disconnect(client.getValue());
        }
        for (Map.Entry<InetAddress, Invalid> Invalids : Main.Invalids.entrySet()) {
            Invalids.getValue().setCheck_time(0);
        }
        ThreadPool.execute(new Clean());
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
