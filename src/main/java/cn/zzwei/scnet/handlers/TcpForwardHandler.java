package cn.zzwei.scnet.handlers;

import cn.zzwei.scnet.mapping.ConfigMapping;
import cn.zzwei.scnet.mapping.ForwardMapping;
import cn.zzwei.scnet.utils.ChannelUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpForwardHandler extends ChannelHandlerAdapter {


    private final ForwardMapping forwardMapping;
    private Channel remoteChannel;

    public TcpForwardHandler(ForwardMapping forwardMapping) {
        this.forwardMapping = forwardMapping;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // create connect to remote address
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(ctx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.AUTO_READ, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, ConfigMapping.connectTimeout)
                .option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new IdleStateHandler(0, 0, ConfigMapping.timeout, TimeUnit.MILLISECONDS));
                pipeline.addLast(new StateHandler());
                pipeline.addLast(new DataTransferHandler(ctx.channel()));
                if (ConfigMapping.openLoggingHandler) {
                    pipeline.addFirst(Handlers.LOGGING_HANDLER);
                }
            }
        });
        bootstrap.connect(forwardMapping.getRemoteAddress(), forwardMapping.getRemotePort()).addListener((ChannelFutureListener) channelFuture -> {
            if (channelFuture.isSuccess()) {
                log.debug("connect success to remote address {}", forwardMapping);
                remoteChannel = channelFuture.channel();
                tryToReadIfNeeded(ctx);
            } else {
                log.warn("connect fail to remote address {}", forwardMapping, channelFuture.cause());
                ctx.close();
            }
        });
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        ChannelUtils.closeOnFlush(remoteChannel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // act as a data transfer handler
        if (remoteChannel.isActive()) {
            remoteChannel.writeAndFlush(msg).addListener((ChannelFutureListener) channelFuture -> {
                if (!channelFuture.isSuccess()) {
                    log.warn("write data to remote address occurs error {}", forwardMapping, channelFuture.cause());
                    ChannelUtils.closeOnFlush(remoteChannel);
                    ctx.close();
                } else {
                    tryToReadIfNeeded(ctx);
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("request handler occurs error", cause);
        ctx.close();
        ChannelUtils.closeOnFlush(remoteChannel);
    }

    private void tryToReadIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }
}
