package com.weiservers.Thread.Child;

import com.fasterxml.jackson.databind.JsonNode;
import com.weiservers.Console.Command;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.weiservers.Core.HttpClient.HttpClient;

public class Monitor extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Monitor.class);

    public Monitor() {
    }

    public void run() {
        //从服务器拉取指令
        String url = "https://api.weiservers.com/scnet/apply/monitor?token=" + Main.getSetting().get("token");
        JsonNode rootNode = HttpClient(url, false);
        if (rootNode != null) {

            int code = rootNode.at("/code").intValue();
            if (code == 200) {
                //200表示需要下发指令
                String command = rootNode.at("/data/command").toString().replace("\"", "");
                int commandid = rootNode.at("/data/id").intValue();
                logger.info("收到来自Weiservers下发的指令: {}", command);
                Command Runcommand = new Command(command);
                Runcommand.start();
                //返回执行结果
                HttpClient("https://api.weiservers.com/scnet/apply/monitorRE?commandid=" + commandid, false);
                HttpClient("https://api.weiservers.com/scnet/apply/monitorRE?commandid=" + commandid + "&result=" + Runcommand.getResult(), false);
            }
        }
    }
}
