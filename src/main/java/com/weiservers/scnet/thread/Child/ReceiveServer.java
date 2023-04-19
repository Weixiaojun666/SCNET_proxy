package com.weiservers.scnet.thread.Child;

import com.weiservers.scnet.base.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;


public class ReceiveServer extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ReceiveServer.class);
    final Client client;


    public ReceiveServer(Client client) {
        this.client = client;
    }

    public void run() {
        this.client.setThread(this);
        try {
            while (!this.isInterrupted()) {
                this.client.setTime(System.currentTimeMillis());
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                this.client.getTo_server_socket().receive(packet);
                byte[] ans = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, ans, 0, packet.getLength());
                DatagramPacket datagramPacket = new DatagramPacket(ans, packet.getLength(), client.getAddress(), client.getPort());
                client.getTo_client_socket().send(datagramPacket);
            }
        } catch (IOException ignored) {
        }
    }
}
