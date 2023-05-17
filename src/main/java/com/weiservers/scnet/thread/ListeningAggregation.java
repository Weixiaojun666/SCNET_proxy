package com.weiservers.scnet.thread;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.bean.record.SelectList;
import com.weiservers.scnet.bean.record.Setting;
import com.weiservers.scnet.config.Configuration;
import com.weiservers.scnet.thread.child.Cache;
import com.weiservers.scnet.thread.child.Receive;
import com.weiservers.scnet.thread.child.ReceiveCache;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Objects;
import java.util.zip.DataFormatException;

import static com.weiservers.scnet.utils.DataConvertUtils.*;

public class ListeningAggregation extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Listening.class);
    private final int port;
    private final int default_id;
    private Motd motd;
    private DatagramSocket to_client_socket;
    private Setting.Server default_server;


    public ListeningAggregation(int port, int default_id) {
        this.port = port;
        this.default_id = default_id;
        for (Setting.Server server0 : Configuration.getSetting().serverList()) {
            if (server0.id() == default_id) default_server = server0;

        }
    }

    @Override
    public void run() {
        try {
            if (port <= 0) return;
            to_client_socket = new DatagramSocket(this.port);
            motd = new Motd(new DatagramSocket(0), "Aggregation");
            motd.setTime(0);
            //Main.serverThreads.add(new ServerThread(this, to_client_socket, motd, server));
            ThreadPool.execute(new Cache(motd));
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                to_client_socket.receive(packet);
                byte[] ans = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, ans, 0, packet.getLength());
                String string = bytesToHexString(ans);

                InetAddress ClientAddress = packet.getAddress();
                int ClientPort = packet.getPort();

                if (!Main.Servers.containsKey(ClientAddress.getHostAddress() + ":" + ClientPort)) {
                    //新连接 从文件中读取选择 若无则进入默认服务器
                    if (string.equals("0805000000eb60600400")) {
                        //查询直接交给查询类处理
                        ThreadPool.execute(new ReceiveCache(ClientAddress, ClientPort, to_client_socket, default_server, motd));
                    } else if (string.startsWith("050b000000")) {
                        string = decompress(hexStringToByteArray(string.substring(68)));
                        int serverId = default_id;
                        String token = MD5(string.substring(14, 46));
                        for (SelectList.Select select : Configuration.getSelectlist().selectList()) {
                            if (Objects.equals(token, select.token())) {
                                serverId = select.serverId();
                            }
                        }
                        Setting.Server server = null;
                        for (Setting.Server server0 : Configuration.getSetting().serverList()) {
                            if (server0.id() == serverId) server = server0;
                        }
                        if (default_server == null) {
                            logger.error("默认ID:{}服务器未找到,检查配置是否正确?", serverId);
                            return;
                        }
                        if (server == null) {
                            logger.warn("未找到ID为{}的服务器,已重置为默认服务器ID:{}", serverId, default_id);
                            serverId = default_id;
                            server = default_server;
                        }
                        Main.Servers.put(ClientAddress.getHostAddress() + ":" + ClientPort, server);
                        Configuration.getSelectlist().selectList().add(new SelectList.Select(token, serverId, GetTime()));
                        ThreadPool.execute(new Receive(packet, to_client_socket, server, motd));
                    }
                } else {
                    Setting.Server server = Main.Servers.get(ClientAddress.getHostAddress() + ":" + ClientPort);
                    ThreadPool.execute(new Receive(packet, to_client_socket, server, motd));
                }
            }
        } catch (IOException e) {
            if (!isInterrupted()) {
                logger.error("无法继续建立监听在端口{} : ", this.port, e);
                logger.error("=========================================");
                logger.error("已尝试自动重启");
                if (!to_client_socket.isClosed()) to_client_socket.close();
                if (motd != null) motd.close();
                try {
                    sleep(1000);
                } catch (InterruptedException ignored) {
                }
                ThreadPool.execute(new ListeningAggregation(port, default_id));
            }
        } catch (DataFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
