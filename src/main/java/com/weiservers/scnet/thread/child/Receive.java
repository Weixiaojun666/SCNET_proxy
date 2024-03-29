package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Client;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.record.Setting.Server;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import static com.weiservers.scnet.utils.DataConvertUtils.*;

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

    @Override
    public void run() {
        InetAddress ClientAddress = packet.getAddress();
        int ClientPort = packet.getPort();
        try {
            DatagramSocket to_server_socket;
            if (!Main.Clients.containsKey(ClientAddress.getHostAddress() + ":" + ClientPort)) {
                //新连接 判断是否是查询请求
                byte[] ans = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, ans, 0, packet.getLength());
                String string = bytesToHexString(ans);
                if (string.equals("0805000000eb60600400")) {
                    //查询请求交给缓存类处理
                    ThreadPool.execute(new ReceiveCache(ClientAddress, ClientPort, to_client_socket, server, motd));
                } else if (string.startsWith("050b000000")) {
                    //新版本服务器数据包经过压缩 需要先解压缩
                    string = decompress(hexStringToByteArray(string.substring(68)));
                    //客户端连接请求打到服务器
                    to_server_socket = new DatagramSocket(0);
                    Client client = new Client(to_server_socket, to_client_socket, server, ClientAddress, ClientPort);
                    client.setTime(System.currentTimeMillis());
                    Main.Clients.put(ClientAddress.getHostAddress() + ":" + ClientPort, client);
                    ThreadPool.execute(new ReceiveServer(client));
                    ThreadPool.execute(new ReceiveClient(packet, client));
                    logger.info("[新客户端连接]   {}  {}  =>  {}  {} 通过端口{} 连接到[{}]", ClientAddress.getHostAddress(), ClientPort, server.address(), server.port(), to_server_socket.getLocalPort(), server.name());
                    ThreadPool.execute(new Check(client, string.substring(14, 46)));
                    Main.info.getNormal_ip().add(ClientAddress);
                    Main.info.addNormal();
                } else {
                    //其他请求不予建立连接
                    ThreadPool.execute(new Discard(ClientAddress));
                }
            } else {
                Client client = Main.Clients.get(ClientAddress.getHostAddress() + ":" + ClientPort);
                ThreadPool.execute(new ReceiveClient(packet, client));
            }
        } catch (Exception e) {
            logger.error("处理时来自  {}  {} 的数据包发生错误", ClientAddress, ClientPort, e);
        }
    }
}
