package Thread.Child;

import Base.Client;
import Thread.Console;
import com.weiservers.Cloud;
import com.weiservers.GetConfig;
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
            Iterator<Map.Entry<String, Client>> it = Console.Clients.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Client> client = it.next();
                if ((client.getValue().getTime() + (int) GetConfig.getSetting().get("time_out") * 1000L) < System.currentTimeMillis()) {
                    logger.info("{} [断开连接]   {} {}", "\033[33m", client.getKey(), "\033[0m");
                    if (client.getValue().getThread() != null) client.getValue().getThread().interrupt();
                    if (client.getValue().getTo_server_socket() != null)
                        client.getValue().getTo_server_socket().close();
                    Cloud.postloguser(client.getValue().getWeiid());
                    it.remove();
                }
            }
        } catch (Exception e) {
            logger.error("{}断开连接时出现错误：{} {}", "\033[31m", e, "\033[0m");
        }
    }
}

