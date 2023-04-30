package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.utils.ConfigLoad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.weiservers.scnet.utils.Tools.ReloadCache;

public class ReceiveCache extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(ReceiveCache.class);
    final Motd motd;

    final InetAddress clientAddress;
    final int clientPort;
    final DatagramSocket toClientSocket;
    final Server server;

    public ReceiveCache(InetAddress clientAddress, int clientPort, DatagramSocket toClientSocket, Server server, Motd motd) {
        this.clientAddress = clientAddress;
        this.clientPort = clientPort;
        this.toClientSocket = toClientSocket;
        this.server = server;
        this.motd = motd;
    }

    public void run() {
        try {

            if ((motd.getTime() + ConfigLoad.getSetting().base().cache_time() * 1000L) < System.currentTimeMillis()) {
                //刷新缓存
                ReloadCache(motd, server);
            }
            //直接回复
            Main.info.addRespond();
            byte[] bytes = motd.getMotd();
            if (bytes == null) {
                ReloadCache(motd, server);
            } else {
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, clientAddress, clientPort);
                toClientSocket.send(datagramPacket);
            }
        } catch (Exception e) {
            logger.error("获取服务器信息时出现错误：{}", server.name(), e);
        }
    }
}
