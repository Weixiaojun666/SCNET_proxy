package com.weiservers.scnet.thread;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.ServerThread;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.thread.child.Cache;
import com.weiservers.scnet.thread.child.Receive;
import com.weiservers.scnet.utils.ThreadPool;
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

    private Boolean Aggregation = false;

    public Listening(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            //端口为-1不启用转发
            if (this.server.proxy_port() <= 0) return;
            to_client_socket = new DatagramSocket(this.server.proxy_port());
            motd = new Motd(new DatagramSocket(0), server.name());
            motd.setTime(0);
            Main.serverThreads.add(new ServerThread(this, to_client_socket, motd, server));
            ThreadPool.execute(new Cache(motd));
            if (server.id() == 0 && server.name() == null && server.address() == null && server.port() == 0) {
                Aggregation = true;
            }
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                to_client_socket.receive(packet);
                if (Aggregation) {


                    //ThreadPool.execute(new ReceiveAggregation(packet, to_client_socket, server, motd));
                } else ThreadPool.execute(new Receive(packet, to_client_socket, server, motd));
            }
        } catch (IOException e) {
            if (!isInterrupted()) {
                logger.error("无法继续建立监听在端口{} : ", this.server.proxy_port(), e);
                logger.error("=========================================");
                logger.error("已尝试自动重启");
                if (!to_client_socket.isClosed()) to_client_socket.close();
                if (motd != null) {
                    if (!motd.getThread().isAlive()) motd.getThread().interrupt();
                    if (!motd.getSocket().isClosed()) motd.getSocket().close();
                }
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) {
                }
                ThreadPool.execute(new Listening(this.server));
            }
        }
    }
}
