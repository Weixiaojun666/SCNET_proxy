package com.weiservers.scnet.bean.record;

import java.util.List;

public record SelectList(List<Select> selectList) {

    public record Select(String token, int serverId, String updated) {

    }


}
