package com.weiservers.scnet.thread.Child;

import com.weiservers.scnet.base.Invalid;
import com.weiservers.scnet.Main;

import java.net.InetAddress;

public class Discard extends Thread {
    private final InetAddress ClientAddress;

    public Discard(InetAddress ClientAddress) {
        this.ClientAddress = ClientAddress;
    }

    public void run() {
        Main.info.addInvalid();
        if (!Main.Invalids.containsKey(ClientAddress)) {
            Invalid invalid = new Invalid(System.currentTimeMillis());
            Main.Invalids.put(ClientAddress, invalid);
        } else {
            Invalid invalid = Main.Invalids.get(ClientAddress);
            invalid.addNum();
            invalid.setUpdate_time(System.currentTimeMillis());
        }
    }
}
