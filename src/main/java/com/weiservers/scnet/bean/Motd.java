package com.weiservers.scnet.bean;

import java.net.DatagramSocket;

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

    public byte[] getMotd() {
        return motd;
    }

    public void setMotd(byte[] motd) {
        this.motd = motd;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public Thread getThread() {
        return thread;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public int getMaxplayer() {
        return maxplayer;
    }

    public int getOnlineplayer() {
        return onlineplayer;
    }

    public String getModel() {
        return model;
    }

    public String getVersion() {
        return version;
    }

    public String getServername() {
        return servername;
    }

}
