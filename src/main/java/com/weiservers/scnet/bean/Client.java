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
    private String Checkid;
    private Boolean Aggregation;

    public Client(DatagramSocket to_server_socket, DatagramSocket to_client_socket, Server server, InetAddress address, int port) {
        this.to_server_socket = to_server_socket;
        this.to_client_socket = to_client_socket;
        this.server = server;
        this.address = address;
        this.port = port;
        this.userid = 0;
    }
}
