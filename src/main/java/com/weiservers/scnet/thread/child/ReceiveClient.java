package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.bean.Client;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class ReceiveClient extends Thread {
    final DatagramPacket packet;
    final Client client;

    public ReceiveClient(DatagramPacket packet, Client client) {
        this.packet = packet;
        this.client = client;
    }

    @Override
    public void run() {
        try {
            byte[] ans = new byte[this.packet.getLength()];
            System.arraycopy(this.packet.getData(), 0, ans, 0, this.packet.getLength());
            DatagramPacket packet_to_server = new DatagramPacket(ans, this.packet.getLength(), InetAddress.getByName(client.getServer().address()), client.getServer().port());
            client.getTo_server_socket().send(packet_to_server);
        } catch (Exception ignored) {
        }
    }
}
