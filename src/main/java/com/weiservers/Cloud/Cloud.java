package com.weiservers.Cloud;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weiservers.Base.Motd;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.weiservers.Core.HttpClient.HttpClient;

public class Cloud {
    private final static Logger logger = LoggerFactory.getLogger(Cloud.class);

    private static String access_token;

    public Cloud() {
        access_token = (String) Main.getSetting().get("token");
    }

    public static void ReloadToken() {
        access_token = null;
    }

    public static void postmotd(Motd motd) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (access_token == null) return;
        if (motd.getModel().equals("未知")) return;
        String url = "token=" + access_token + "&servername=" + motd.getServername()+ "&onlineplayer=" + motd.getOnlineplayer() + "&maxplayer=" + motd.getMaxplayer() + "&model=" + motd.getModel() + "&version=" + motd.getVersion();
        HttpClient("https://api.weiservers.com/scnet/apply/serverInfo?" + url);
    }

    public static void postinvalid(String ip, long start_time, long end_time, int num) {
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (access_token == null) return;
        String url = "token=" + access_token + "&ip=" + ip + "&start_time=" + start_time + "&end_time=" + end_time + "&num=" + num;
        HttpClient("https://api.weiservers.com/scnet/apply/invalid?" + url);
    }


    public static void postlogout(String id){
        if (!(boolean) Main.getSetting().get("cloud")) return;
        if (access_token == null) return;
        String url =  "checkid=" + id;
        HttpClient("https://api.weiservers.com/scnet/apply/logout?" + url);
    }

    //check id
    // servername  id
    //String state
     public static void postlogin(String checkid, String servername, String state,String reason) {
         if (!(boolean) Main.getSetting().get("cloud")) return;
         if (access_token == null) return;
         String url = "checkid="+checkid+"&servername="+servername+"&state="+state + "&reason" + reason+"$token="+access_token;
        HttpClient("https://api.weiservers.com/scnet/apply/login?" + url);
    }
}