package com.weiservers.scnet.base;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {

    private final DatagramSocket to_server_socket;

    private final DatagramSocket to_client_socket;
    private final Server server;
    private final InetAddress address;
    private final int port;
    private long time;
    private String userid;
    private String username;
    private Thread thread;

    private String Checkid;


    public Client(DatagramSocket to_server_socket, DatagramSocket to_client_socket, Server server, InetAddress address, int port) {
        this.to_server_socket = to_server_socket;
        this.to_client_socket = to_client_socket;
        this.server = server;
        this.address = address;
        this.port = port;
        this.userid = "0";
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public DatagramSocket getTo_server_socket() {
        return to_server_socket;
    }

    public DatagramSocket getTo_client_socket() {
        return to_client_socket;
    }

    public Server getServer() {
        return server;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCheckid() {
        return Checkid;
    }

    public void setCheckid(String Checkid) {
        this.Checkid = Checkid;
    }

}
