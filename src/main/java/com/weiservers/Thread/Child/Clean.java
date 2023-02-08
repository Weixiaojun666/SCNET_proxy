package com.weiservers.Thread.Child;

import com.weiservers.Base.Client;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.Map;

public class Clean extends Thread {
    private final static Logger logger = LoggerFactory.getLogger(Clean.class);

    public Clean() {
    }

    public void run() {
        try {
            Iterator<Map.Entry<String, Client>> it = Main.Clients.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Client> client = it.next();
                if ((client.getValue().getTime() + (int) Main.getSetting().get("time_out") * 1000L) < System.currentTimeMillis()) {
                    logger.info("[断开连接]   {}", client.getKey());
                    if (client.getValue().getThread() != null) client.getValue().getThread().interrupt();
                    if (client.getValue().getTo_server_socket() != null)
                        client.getValue().getTo_server_socket().close();
                    Cloud.postloguser(client.getValue().getWeiid());
                    it.remove();
                }
            }
        } catch (Exception e) {
            logger.error("断开连接时出现错误：", e);
        }
    }
}

