package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.bean.Invalid;

import java.net.InetAddress;

public class Discard extends Thread {
    private final InetAddress ClientAddress;

    public Discard(InetAddress ClientAddress) {
        this.ClientAddress = ClientAddress;
    }

    @Override
    public void run() {
        Main.info.addInvalid();
        if (!Main.Invalids.containsKey(ClientAddress)) {
            Invalid invalid = new Invalid();
            Main.Invalids.put(ClientAddress, invalid);
        } else {
            Invalid invalid = Main.Invalids.get(ClientAddress);
            invalid.addNum();
            invalid.setUpdate_time(System.currentTimeMillis());
        }
    }
}
