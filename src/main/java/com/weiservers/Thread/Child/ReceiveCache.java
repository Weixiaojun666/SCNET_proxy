package com.weiservers.Thread.Child;

import com.weiservers.Base.Motd;
import com.weiservers.Base.Server;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ReceiveCache extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(ReceiveCache.class);
    final Motd motd;
    final DatagramPacket packet;
    final InetAddress clientAddress;
    final int clientPort;
    final DatagramSocket toClientSocket;
    final Server server;

    public ReceiveCache(DatagramPacket packet, InetAddress clientAddress, int clientPort, DatagramSocket toClientSocket, Server server, Motd motd) {
        this.packet = packet;
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.toClientSocket = toClientSocket;
        this.server = server;
        this.motd = motd;
    }

    public void run() {
        try {
            if ((motd.getTime() + (int) Main.getSetting().get("cache_time") * 1000L) < System.currentTimeMillis()) {
                //刷新缓存
                Main.info.addRefresh();
                byte[] ans = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, ans, 0, packet.getLength());
                DatagramPacket motd_packet = new DatagramPacket(ans, packet.getLength(), InetAddress.getByName(server.address()), server.port());
                motd.getSocket().send(motd_packet);
                motd.getSocket().receive(packet);
                byte[] bytes = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, bytes, 0, packet.getLength());
                motd.setMotd(bytes);
                motd.setTime(System.currentTimeMillis());
            }
            Main.info.addRespond();
            //直接回复
            byte[] bytes = motd.getMotd();
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, clientAddress, clientPort);
            toClientSocket.send(datagramPacket);
        } catch (Exception e) {
            logger.error("获取服务器信息时出现错误：{} {}", server.name(), e);
        }
    }
}
