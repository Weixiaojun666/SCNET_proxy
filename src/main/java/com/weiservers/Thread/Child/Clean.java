package com.weiservers.Thread.Child;

import com.weiservers.Base.Client;
import com.weiservers.Base.Invalid;
import com.weiservers.Cloud.Cloud;
import com.weiservers.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import static com.weiservers.Core.Tools.getDatePoor;

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
            Iterator<Map.Entry<InetAddress, Invalid>> it0 = Main.Invalids.entrySet().iterator();
            while (it0.hasNext()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Map.Entry<InetAddress, Invalid> invalidEntry = it0.next();
                if (invalidEntry.getValue().getUpdate_time() + 8000L < System.currentTimeMillis()) {
                    logger.info("收到来自 {} 的无效包,数量{} 开始时间 {} 持续 {}", invalidEntry.getKey(), invalidEntry.getValue().getNum(), format.format(invalidEntry.getValue().getCreate_time()), getDatePoor(invalidEntry.getValue().getCreate_time(), invalidEntry.getValue().getUpdate_time()));
                    it0.remove();
                }
                if (invalidEntry.getValue().getCreate_time() + 120000L < System.currentTimeMillis()) {
                    logger.warn("收到来自 {} 的无效包,数量{} 开始时间 {} 持续 {}", invalidEntry.getKey(), invalidEntry.getValue().getNum(), format.format(invalidEntry.getValue().getCreate_time()), getDatePoor(invalidEntry.getValue().getCreate_time(), invalidEntry.getValue().getUpdate_time()));
                }
            }
        } catch (Exception e) {
            logger.error("断开连接时出现错误：", e);
        }
    }
}

