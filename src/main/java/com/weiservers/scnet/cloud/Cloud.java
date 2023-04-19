package com.weiservers.scnet.cloud;

import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.utils.ConfigLoad;

import static com.weiservers.scnet.utils.HttpClient.HttpClient;

public class Cloud {

    public Cloud() {
    }

    public static void ReloadToken() {
    }

    public static void postmotd(Motd motd) {
        if (!(boolean) ConfigLoad.getSetting().get("cloud")) return;
        if (motd.getModel().equals("未知")) return;
        String url = "token=" + ConfigLoad.getSetting().get("token") + "&servername=" + motd.getServername() + "&onlineplayer=" + motd.getOnlineplayer() + "&maxplayer=" + motd.getMaxplayer() + "&model=" + motd.getModel() + "&version=" + motd.getVersion();
        HttpClient("https://api.weiservers.com/scnet/apply/serverInfo?" + url);
    }

    public static void postinvalid(String ip, long start_time, long end_time, int num) {
        if (!(boolean) ConfigLoad.getSetting().get("cloud")) return;
        String url = "token=" + ConfigLoad.getSetting().get("token") + "&ip=" + ip + "&start_time=" + start_time + "&end_time=" + end_time + "&num=" + num;
        HttpClient("https://api.weiservers.com/scnet/apply/invalid?" + url);
    }

    public static void postlogout(String id) {
        if (!(boolean) ConfigLoad.getSetting().get("cloud")) return;
        String url = "checkid=" + id + "&token=" + ConfigLoad.getSetting().get("token");
        HttpClient("https://api.weiservers.com/scnet/apply/logout?" + url);
    }

    //check id
    // servername  id
    //String state
    public static void postlogin(String checkid, String state, String reason) {
        if (!(boolean) ConfigLoad.getSetting().get("cloud")) return;
        String url = "checkid=" + checkid + "&state=" + state + "&reason=" + reason + "&token=" + ConfigLoad.getSetting().get("token");
        HttpClient("https://api.weiservers.com/scnet/apply/login?" + url);
    }
}