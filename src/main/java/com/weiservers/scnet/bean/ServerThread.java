package com.weiservers.scnet.bean;

import com.weiservers.scnet.bean.record.Setting.Server;
import lombok.Getter;

import java.net.DatagramSocket;

@Getter
public record ServerThread(Thread thread, DatagramSocket datagramSocket, Motd motd, Server server) {

}
