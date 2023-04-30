package com.weiservers.scnet.bean.record;

import java.util.List;

public record Selectlist(List<Select> selectlist) {

    public record Select(int userid, String username, int serverid, String servername, String updated, String ip) {

    }


}
