package com.weiservers.Thread;

import com.weiservers.Base.Motd;
import com.weiservers.Base.Server;
import com.weiservers.Base.ServerThread;
import com.weiservers.Core.ThreadPool;
import com.weiservers.Main;
import com.weiservers.Thread.Child.Cache;
import com.weiservers.Thread.Child.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Listening extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Listening.class);
    private final Server server;
    private Motd motd;
    private DatagramSocket to_client_socket;

    public Listening(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            to_client_socket = new DatagramSocket(this.server.proxy_port());
            motd = new Motd(new DatagramSocket(0), server.name(), server.id());
            motd.setTime(0);
            Main.serverThreads.add(new ServerThread(this, to_client_socket, motd, server));
            ThreadPool.execute(new Cache(motd));
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                to_client_socket.receive(packet);
                ThreadPool.execute(new Receive(packet, to_client_socket, server, motd));
            }
        } catch (IOException e) {
            if (!isInterrupted()) {
                logger.error("无法继续建立监听在端口{} : {}", this.server.proxy_port(), e);
                logger.error("=========================================");
                logger.error("已尝试自动重启");
                if (!to_client_socket.isClosed()) to_client_socket.close();
                if (motd != null) {
                    if (!motd.getThread().isAlive()) motd.getThread().interrupt();
                    if (!motd.getSocket().isClosed()) motd.getSocket().close();
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ex) {
                }
                ThreadPool.execute(new Listening(this.server));
            }
        }
    }
}
