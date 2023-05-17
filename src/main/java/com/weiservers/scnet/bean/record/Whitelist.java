package com.weiservers.scnet.bean.record;

import java.util.List;

public record Whitelist(List<White> whiteList) {
    public record White(int userid) {

    }
}
