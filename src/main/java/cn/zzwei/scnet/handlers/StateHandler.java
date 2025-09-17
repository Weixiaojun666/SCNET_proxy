package cn.zzwei.scnet.handlers;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * State Handler for exceptions or other state
 */
public class StateHandler extends ChannelHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(StateHandler.class);

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof IdleStateEvent) {
            LOG.info("channel {} becomes idle, close it", ctx.channel());
            ctx.close();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.fireExceptionCaught(cause);
    }
}
