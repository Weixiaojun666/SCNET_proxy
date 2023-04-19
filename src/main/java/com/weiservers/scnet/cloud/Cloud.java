package com.weiservers.scnet.cloud;

import com.weiservers.scnet.Main;
import com.weiservers.scnet.base.Motd;

import static com.weiservers.scnet.utils.HttpClient.HttpClient;

public class Cloud {

    public Cloud() {
    }

    public static void ReloadToken() {
    }

    public static void postmotd(Motd motd) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (motd.getModel().equals("未知")) return;
        String url = "token=" + Main.getSetting().get("token") + "&servername=" + motd.getServername() + "&onlineplayer=" + motd.getOnlineplayer() + "&maxplayer=" + motd.getMaxplayer() + "&model=" + motd.getModel() + "&version=" + motd.getVersion();
        HttpClient("https://api.weiservers.com/scnet/apply/serverInfo?" + url);
    }

    public static void postinvalid(String ip, long start_time, long end_time, int num) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        String url = "token=" + Main.getSetting().get("token") + "&ip=" + ip + "&start_time=" + start_time + "&end_time=" + end_time + "&num=" + num;
        HttpClient("https://api.weiservers.com/scnet/apply/invalid?" + url);
    }

    public static void postlogout(String id) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        String url = "checkid=" + id + "&token=" + Main.getSetting().get("token");
        HttpClient("https://api.weiservers.com/scnet/apply/logout?" + url);
    }

    //check id
    // servername  id
    //String state
    public static void postlogin(String checkid, String state, String reason) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        String url = "checkid=" + checkid + "&state=" + state + "&reason=" + reason + "&token=" + Main.getSetting().get("token");
        HttpClient("https://api.weiservers.com/scnet/apply/login?" + url);
    }
}