package com.weiservers.scnet.bean.record;

import java.util.List;

public record Setting(Base base, Cloud cloud, Aggregation aggregation, List<Server> server_list) {
    public record Base(int connection_limit, int time_out, int cache_time, Boolean whitelist, int area_leave) {

    }

    public record Cloud(Boolean enable, String token, int timeout_retry, Boolean verification) {

    }

    public record Aggregation(Boolean enable, int port, int default_server) {

    }

    public record Server(int id, String name, String address, int port, int proxy_port) {

    }

}
