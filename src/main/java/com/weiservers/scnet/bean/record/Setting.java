package com.weiservers.scnet.bean.record;

import java.util.List;

public record Setting(Base base, Cloud cloud, Aggregation aggregation, List<Server> server_list) {
    public record Base(int connection_limit, int time_out, int cache_time, boolean whitelist, int area_leave,boolean AllowForeign) {

    }

    public record Cloud(boolean enable, String token, int retryNum,int timeOut, boolean verification) {

    }

    public record Aggregation(boolean enable, int port, int default_server) {

    }

    public record Server(int id, String name, String address, int port, int proxy_port) {

    }

}
