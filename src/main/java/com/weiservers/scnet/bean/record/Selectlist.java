package com.weiservers.scnet.bean.record;

import java.util.List;

public record Selectlist(List<Select> selectlist) {

    public record Select(String token, int serverid, String updated) {

    }


}
