package com.weiservers.scnet.bean;

import com.weiservers.scnet.bean.record.Setting.Server;

import java.net.DatagramSocket;

public record ServerThread(Thread thread, DatagramSocket datagramSocket, Motd motd, Server server) {

}
