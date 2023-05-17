package com.weiservers.scnet.bean.record;

import java.util.List;

public record Setting(BasicConfig basicConfig, LocalProtection localProtection, CloudProtection cloudProtection,
                      Aggregation aggregation, List<Server> serverList) {
    public record BasicConfig(int maxConnections, int playerTimeout, int cacheTime, int httpTimeout, int httpRetryCount,
                              int regionBlockLevel) {
    }

    public record LocalProtection(boolean denyOverseasLogin, boolean whitelistOnlyLogin) {
    }

    public record CloudProtection(String appKey, String appSecret, boolean loginCheck) {
    }

    public record Aggregation(int port, int defaultServer) {
    }

    public record Server(int id, String name, String address, int port, int proxyPort) {
    }
}
