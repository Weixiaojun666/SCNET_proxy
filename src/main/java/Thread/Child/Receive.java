package Thread.Child;

import Base.Client;
import Base.Motd;
import Base.Server;
import Thread.Console;
import Thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.weiservers.Tools.bytesToHexString;

public class Receive extends Thread {

    private final static Logger logger = LoggerFactory.getLogger(Receive.class);
    final DatagramPacket packet;
    final DatagramSocket to_client_socket;
    final Server server;
    final Motd motd;

    public Receive(DatagramPacket packet, DatagramSocket to_client_socket, Server server, Motd motd) {
        this.packet = packet;
        this.to_client_socket = to_client_socket;
        this.server = server;
        this.motd = motd;
    }

    public void run() {
        InetAddress ClientAddress = packet.getAddress();
        int ClientPort = packet.getPort();
        try {
            DatagramSocket to_server_socket;

            if (!Console.Clients.containsKey(ClientAddress + ":" + ClientPort)) {
                //新连接 判断是否是查询请求
                byte[] ans = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, ans, 0, packet.getLength());
                String string = bytesToHexString(ans);
                if (string.equals("0804")) {
                    //查询请求交给缓存类处理
                    ThreadPool.execute(new Cache(packet, ClientAddress, ClientPort, to_client_socket, server, motd));
                } else if (string.startsWith("050b000000")) {
                    //客户端连接请求打到服务器
                    to_server_socket = new DatagramSocket(0);
                    Client client = new Client(to_server_socket, to_client_socket, server, ClientAddress, ClientPort);
                    client.setTime(System.currentTimeMillis());
                    Console.Clients.put(ClientAddress + ":" + ClientPort, client);
                    ThreadPool.execute(new ReceiveServer(client));
                    ThreadPool.execute(new ReceiveClient(packet, client));
                    logger.info("{} [新客户端连接]   {}  {}  =>  {}  {} 连接到[{}]{}", "\033[32m", ClientAddress, ClientPort, server.address(), server.port(), server.name(), "\033[0m");
                    ThreadPool.execute(new Check(ClientAddress, client, string.substring(78, 110)));
                    Console.info.getNormal_ip().add(ClientAddress);
                    Console.info.addNormal();
                } else {
                    //其他请求不予建立连接
                    logger.info("{} 来自  {}  {} 的非法数据包已丢弃{}", "\033[33m", ClientAddress, ClientPort, "\033[0m");
                    Console.info.addInvalid();
                }
            } else {
                Client client = Console.Clients.get(ClientAddress + ":" + ClientPort);
                ThreadPool.execute(new ReceiveClient(packet, client));
            }
        } catch (Exception e) {
            logger.error("{} 处理时来自  {}  {} 的数据包发生错误 {}{}", "\033[31m", ClientAddress, ClientPort, e, "\033[0m");
        }
    }
}
