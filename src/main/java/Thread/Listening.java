package Thread;

import Base.Motd;
import Base.Server;
import Thread.Child.Receive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Listening extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Listening.class);
    private final Server server;
    DatagramSocket to_client_socket = null;

    public Listening(Server server) {
        this.server = server;
    }

    public void run() {
        try {
            this.to_client_socket = new DatagramSocket(this.server.proxy_port());
            Motd motd = new Motd(new DatagramSocket(0));
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                to_client_socket.receive(packet);
                ThreadPool.execute(new Receive(packet, to_client_socket, server, motd));
            }
        } catch (IOException e) {
            logger.error("{}无法继续建立监听在端口{} : {} {}", "\033[31m", this.server.proxy_port(), e, "\033[0m");
            logger.error("=========================================");
            logger.error("已尝试自动重启");
            ThreadPool.execute(new Listening(this.server));
        }
    }
}
