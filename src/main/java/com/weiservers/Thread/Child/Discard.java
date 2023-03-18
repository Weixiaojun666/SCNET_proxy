package com.weiservers.Thread.Child;

import com.weiservers.Base.Invalid;
import com.weiservers.Main;

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
