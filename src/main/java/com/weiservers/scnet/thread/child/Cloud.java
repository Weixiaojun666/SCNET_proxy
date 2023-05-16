package com.weiservers.scnet.thread.child;

public class Cloud {

    public Cloud() {
    }

    public static void ReloadToken() {
    }

    public static void UploadMOTD() {


    }

    public static void UploadInfo() {

    }

    public static void UploadLogin() {

    }

    public static void UploadSignout() {

    }


//    public static void postmotd(Motd motd) {
//        if (!Configuration.getSetting().cloud().enable()) return;
//        if (motd.getModel().equals("未知")) return;
//        String url = "token=" + Configuration.getSetting().cloud().token() + "&servername=" + motd.getServername() + "&onlineplayer=" + motd.getOnlineplayer() + "&maxplayer=" + motd.getMaxplayer() + "&model=" + motd.getModel() + "&version=" + motd.getVersion();
//        HttpClient("https://api.weiservers.com/scnet/apply/serverInfo?" + url);
//    }
//
//    public static void postinvalid(String ip, long start_time, long end_time, int num) {
//        if (!Configuration.getSetting().cloud().enable()) return;
//        String url = "token=" + Configuration.getSetting().cloud().token() + "&ip=" + ip + "&start_time=" + start_time + "&end_time=" + end_time + "&num=" + num;
//        HttpClient("https://api.weiservers.com/scnet/apply/invalid?" + url);
//    }
//
//    public static void postlogout(String id) {
//        if (!Configuration.getSetting().cloud().enable()) return;
//
//        String url = "checkid=" + id + "&token=" + Configuration.getSetting().cloud().token();
//        HttpClient("https://api.weiservers.com/scnet/apply/logout?" + url);
//    }
//
//    //check id
//    // servername  id
//    //String state
//    public static void postlogin(String checkid, String state, String reason) {
//        if (!Configuration.getSetting().cloud().enable()) return;
//        String url = "checkid=" + checkid + "&state=" + state + "&reason=" + reason + "&token=" + Configuration.getSetting().cloud().token();
//        HttpClient("https://api.weiservers.com/scnet/apply/login?" + url);
//    }
}