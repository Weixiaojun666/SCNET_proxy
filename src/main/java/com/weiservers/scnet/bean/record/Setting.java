package com.weiservers.scnet.bean.record;

import java.util.List;

public record Setting(Base base, Cloud cloud, Aggregation aggregation, Server_list server_list) {
    public record Base(int connection_limit, int time_out, int cache_time, Boolean whitelist) {

    }
    public record Cloud(Boolean enable, String token, int timeout_retry, Boolean verification) {

    }
    public record Aggregation(Boolean enable, int port, int default_server) {

    }
    public record Server_list(List<Server> server_list) {

    }

}
