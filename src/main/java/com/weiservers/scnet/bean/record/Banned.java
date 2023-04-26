package com.weiservers.scnet.bean.record;

public record Banned(BannedAreas bannedAreas, BannedIps bannedIps, BannedPlayers bannedPlayers) {
    public record BannedAreas(int userid, String ip, String info1, String info2, String info3, String isp,
                              String created, String exoires, String reason) {
    }

    public record BannedIps(String ip, int userid, String created, String expires, String reason) {
    }

    public record BannedPlayers(int userid, String username, int serverid, String servername, String updated) {
    }

}
