package com.weiservers.scnet.bean.record;

import java.util.List;

public record Banned(List<BannedAreas> bannedAreas, List<BannedIps> bannedIps, List<BannedPlayers> bannedPlayers) {
    public record BannedAreas(int userid, String ip, String info1, String info2, String info3, String isp,
                              String created, int exoires, String reason) {
    }

    public record BannedIps(String ip, int userid, String created, int expires, String reason) {
    }

    public record BannedPlayers(int userid, String username, int expires , String reason,String updated) {
    }

}
