package com.weiservers.scnet.bean;

import com.weiservers.scnet.bean.record.Setting.Server;
import lombok.Data;

import java.net.DatagramSocket;
import java.net.InetAddress;

@Data
public class Client {

    private final DatagramSocket to_server_socket;
    private final DatagramSocket to_client_socket;
    private final Server server;
    private final InetAddress address;
    private final int port;
    private long time;
    private int userid;
    private String username;
    private Thread thread;

    private String info1;
    private String info2;
    private String info3;
    private String isp;

    private int Checkid;


    public Client(DatagramSocket to_server_socket, DatagramSocket to_client_socket, Server server, InetAddress address, int port) {
        this.to_server_socket = to_server_socket;
        this.to_client_socket = to_client_socket;
        this.server = server;
        this.address = address;
        this.port = port;
        this.userid = 0;
    }

    public boolean equalArea(Client client, int level) {
        int count = 0;
        if (this.info1.equals(client.info1)) count++;
        if (level >= 2 && this.info2.equals(client.info2)) count++;
        if (level >= 3 && this.info3.equals(client.info3)) count++;
        if (level >= 4 && this.isp.equals(client.isp)) count++;
        return (count == level);
    }
}
