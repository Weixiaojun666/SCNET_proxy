package com.weiservers.Base;

public record Server(int id, String name, String address, int port, int proxy_port, String state, String create_time,
                     String provider) {

}
