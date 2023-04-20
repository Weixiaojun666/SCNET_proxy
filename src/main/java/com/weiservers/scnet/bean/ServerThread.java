package com.weiservers.scnet.bean;

import com.weiservers.scnet.bean.record.Server;
import lombok.Getter;

import java.net.DatagramSocket;
@Getter
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

}
