package com.weiservers.scnet.base;

import java.net.DatagramSocket;

public class ServerThread {

    private final Thread thread;
    private final DatagramSocket datagramSocket;
    private final Motd motd;
    private final Server server;

    public ServerThread(Thread thread, DatagramSocket datagramSocket, Motd motd, Server server) {
        this.thread = thread;
        this.datagramSocket = datagramSocket;
        this.motd = motd;
        this.server = server;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public Thread getThread() {
        return thread;
    }

    public Motd getMotd() {
        return motd;
    }

    public Server getServer() {
        return server;
    }
}
