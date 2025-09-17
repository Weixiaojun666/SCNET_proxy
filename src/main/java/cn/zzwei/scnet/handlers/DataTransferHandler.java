package cn.zzwei.scnet.handlers;

import cn.zzwei.scnet.utils.ChannelUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * data transfer between channels
 */
@Slf4j
public final class DataTransferHandler extends ChannelHandlerAdapter {
    private final Channel relayChannel;

    public DataTransferHandler(Channel relayChannel) {
        this.relayChannel = relayChannel;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        tryToReadIfNeeded(ctx);
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (relayChannel.isActive()) {
            relayChannel.writeAndFlush(msg).addListener((ChannelFutureListener) channelFuture -> {
                if (channelFuture.isSuccess()) {
                    tryToReadIfNeeded(ctx);
                } else {
                    ChannelUtils.closeOnFlush(relayChannel);
                }
            });
        } else {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channel {} closed", ctx.channel());
        ChannelUtils.closeOnFlush(relayChannel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("data transfer handler occurs error, channel: {}", ctx.channel(), cause);
        ctx.close();
    }

    private void tryToReadIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }
}
