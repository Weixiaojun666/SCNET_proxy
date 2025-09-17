package cn.zzwei.scnet.thread;

import cn.zzwei.scnet.handlers.UdpForwardHandler;
import cn.zzwei.scnet.mapping.ForwardMapping;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UdpForwardThread extends Thread {

    final EventLoopGroup acceptEventLoopGroup;
    final EventLoopGroup workEventLoopGroup;
    private final ForwardMapping forwardMapping;

    public UdpForwardThread(EventLoopGroup acceptEventLoopGroup, EventLoopGroup workEventLoopGroup, ForwardMapping forwardMapping) {
        this.acceptEventLoopGroup = acceptEventLoopGroup;
        this.workEventLoopGroup = workEventLoopGroup;
        this.forwardMapping = forwardMapping;
    }

    @Override
    public void run() {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(acceptEventLoopGroup);
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.handler(new ChannelInitializer<NioDatagramChannel>() {
                @Override
                protected void initChannel(NioDatagramChannel ch) {
                    ch.pipeline().addLast(new UdpForwardHandler(forwardMapping));
                }
            });
            ChannelFuture channelFuture;
            if (forwardMapping.getLocalAddress().equalsIgnoreCase("localhost")) {
                channelFuture = bootstrap.bind(forwardMapping.getLocalPort());
            } else {
                channelFuture = bootstrap.bind(forwardMapping.getLocalAddress(), forwardMapping.getLocalPort());
            }
            channelFuture.addListener(
                    (ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            return;
                        }
                        log.info(String.valueOf(forwardMapping));
                    }
            ).sync().channel().closeFuture().sync().channel();


        } catch (Exception e) {
            log.error("failed {}", forwardMapping, e);
        }
    }
}
