package com.weiservers.scnet.bean.record;

import java.util.List;

public record Banned(List<BannedAreas> bannedAreas, List<BannedIps> bannedIps, List<BannedPlayers> bannedPlayers) {
    public record BannedAreas(String info1, String info2, String info3, String isp, int expires, String reason,
                              String updated) {
    }

    public record BannedIps(String ip, int expires, String reason, String updated) {
    }

    public record BannedPlayers(int userid, String username, int expires, String reason, String updated) {
    }

}
