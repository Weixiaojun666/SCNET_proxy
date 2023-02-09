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
                    logger.info("[断开连接]   {} {}", client.getKey(), client.getValue().getUsername());
                    if (!client.getValue().getThread().isAlive()) client.getValue().getThread().interrupt();
                    if (!client.getValue().getTo_server_socket().isClosed())
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
                    Cloud.postloginvalid(String.valueOf(invalidEntry.getKey()), invalidEntry.getValue().getCreate_time(), invalidEntry.getValue().getUpdate_time(), invalidEntry.getValue().getNum());
                }
                if (invalidEntry.getValue().getCheck_time() + 60000L < System.currentTimeMillis()) {
                    invalidEntry.getValue().setCheck_time(System.currentTimeMillis());
                    logger.warn("收到来自 {} 的无效包,数量{} 开始时间 {} 持续 {}", invalidEntry.getKey(), invalidEntry.getValue().getNum(), format.format(invalidEntry.getValue().getCreate_time()), getDatePoor(invalidEntry.getValue().getCreate_time(), invalidEntry.getValue().getUpdate_time()));
                    Cloud.postloginvalid(String.valueOf(invalidEntry.getKey()), invalidEntry.getValue().getCreate_time(), 0L, invalidEntry.getValue().getNum());
                }
            }
        } catch (Exception e) {
            logger.error("回收垃圾时出现错误：", e);
        }
    }
}
