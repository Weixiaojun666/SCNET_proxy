package com.weiservers.Thread.Child;

import com.weiservers.Base.Motd;
import com.weiservers.Core.ThreadPool;
import com.weiservers.Thread.Listening;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramPacket;

public class Cache extends Thread{
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    private final Motd motd;
    public Cache(Motd motd) {
        this.motd=motd;
    }
    public void run() {
        motd.setThread(this);
        try{
            while (!isInterrupted()) {
                byte[] buf = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象
                motd.getSocket().receive(packet);
                byte[] bytes = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, bytes, 0, packet.getLength());
                motd.setMotd(bytes);
                motd.setTime(System.currentTimeMillis());
            }
        }catch (Exception e){
            if(!isInterrupted()) {
                logger.error("尝试刷新缓存时出现错误 在端口{} : {}",motd.getSocket().getPort(),e);
                logger.error("=========================================");
                logger.error("已尝试自动重启");
                motd.getSocket().close();
                ThreadPool.execute(new Cache(motd));
            }
        }
    }
}
