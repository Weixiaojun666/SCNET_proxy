package com.weiservers.Base;

import java.net.DatagramSocket;

public class Motd {


    private final DatagramSocket socket;
    private long time;
    private byte[] motd;

    public Motd(DatagramSocket socket) {
        time = 0;
        this.socket = socket;
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
}
