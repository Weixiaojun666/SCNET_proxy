package com.weiservers.scnet.bean.record;

public record BannedIps(String ip,int userid,String created,String expires,String reason) {
}
