package com.weiservers.scnet.thread.child;

import com.weiservers.scnet.bean.Motd;
import com.weiservers.scnet.utils.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

import static com.weiservers.scnet.utils.Command.getServerInfo;

public class Cache extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    private final Motd motd;

    public Cache(Motd motd) {
        this.motd = motd;
    }

    @Override
    public void run() {
        motd.setThread(this);
        try {
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                motd.getSocket().receive(packet);
                byte[] bytes = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, bytes, 0, packet.getLength());
                motd.setMotd(bytes);
                motd.setTime(System.currentTimeMillis());
                getServerInfo(motd);
                //postmotd(motd);
            }
        } catch (Exception e) {
            if (!isInterrupted()) {
                logger.error("尝试刷新缓存时出现错误 在端口{} : ", motd.getSocket().getPort(), e);
                logger.error("=========================================");
                logger.error("已尝试自动重启");
                if (!motd.getSocket().isClosed()) motd.getSocket().close();
                ThreadPool.execute(new Cache(motd));
            }
        }
    }
}
