package cn.zzwei.scnet;

import cn.zzwei.scnet.mapping.ConfigMapping;
import cn.zzwei.scnet.mapping.StateMapping;
import cn.zzwei.scnet.thread.TcpForwardThread;
import cn.zzwei.scnet.thread.UdpForwardThread;
import cn.zzwei.scnet.utils.ConfigUtils;
import cn.zzwei.scnet.utils.ThreadPool;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Main {
    public static final ConcurrentHashMap<String, StateMapping> clientAddressList = new ConcurrentHashMap<>();


    public static void main(String[] args) {
        log.info("Loading ...");

        ConfigUtils.readToml();
        ConfigUtils.readAdvanced();
        try (
                EventLoopGroup TCPacceptEventLoopGroup = new NioEventLoopGroup(ConfigMapping.ioAcceptThreadNumber);
                EventLoopGroup TCPworkEventLoopGroup = new NioEventLoopGroup(ConfigMapping.ioWorkThreadNumber);
                EventLoopGroup UDPacceptEventLoopGroup = new NioEventLoopGroup(ConfigMapping.ioAcceptThreadNumber);
                EventLoopGroup UDPworkEventLoopGroup = new NioEventLoopGroup(ConfigMapping.ioWorkThreadNumber)
        ) {
        ThreadPool.LoadThreadPool();
        ConfigUtils.readForwards().forEach(forwardMapping -> {
            if (forwardMapping.getType().equalsIgnoreCase("tcp")) {
                ThreadPool.execute(new TcpForwardThread(TCPacceptEventLoopGroup, TCPworkEventLoopGroup, forwardMapping));
            }
            if (forwardMapping.getType().equalsIgnoreCase("udp")) {
                ThreadPool.execute(new UdpForwardThread(UDPacceptEventLoopGroup, UDPworkEventLoopGroup, forwardMapping));
            }
        });
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            TCPacceptEventLoopGroup.shutdownGracefully();
            TCPworkEventLoopGroup.shutdownGracefully();
            UDPacceptEventLoopGroup.shutdownGracefully();
            UDPworkEventLoopGroup.shutdownGracefully();
            ThreadPool.shutdown();
        }));
        }
    }
}