package com.weiservers.scnet.bean;

import lombok.Data;

import java.net.DatagramSocket;
@Data
public class Motd {
    private final DatagramSocket socket;
    private final String servername;
    private long time;

    private byte[] motd;
    private Thread thread;
    private String model;
    private int onlineplayer;
    private int maxplayer;
    private String version;


    public Motd(DatagramSocket socket, String servername) {
        time = 0;
        this.socket = socket;
        this.servername = servername;
    }

    public void setString(String model, int onlineplayer, int maxplayer, String version) {
        this.model = model;
        this.onlineplayer = onlineplayer;
        this.maxplayer = maxplayer;
        this.version = version;
    }

}
