package cn.zzwei.scnet.handlers;

import cn.zzwei.scnet.mapping.ForwardMapping;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;

@Slf4j
public class UdpForwardHandler extends ChannelHandlerAdapter {
    private final BiMap<InetSocketAddress, Channel> clientChannelMap = HashBiMap.create();
    private final ForwardMapping forwardMapping;
    final EventLoopGroup workEventLoopGroup = new NioEventLoopGroup();
    private Channel localChannel;
    private InetSocketAddress ClientAddress;

    public UdpForwardHandler(ForwardMapping forwardMapping) {
        this.forwardMapping = forwardMapping;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        InetSocketAddress remoteAddress = new InetSocketAddress(forwardMapping.getRemoteAddress(), forwardMapping.getRemotePort());
        DatagramPacket packet = (DatagramPacket) msg;
        ClientAddress = packet.sender();
        Channel remoteChannel;

        if (clientChannelMap.containsKey(ClientAddress)) {
            remoteChannel = clientChannelMap.get(ClientAddress);
        } else {
            log.info("新链接建立{}", ClientAddress.getHostString());
            localChannel = ctx.channel();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workEventLoopGroup);
            bootstrap.channel(NioDatagramChannel.class);
            bootstrap.handler(new ChannelHandlerAdapter() {
                public void channelRead(ChannelHandlerContext ctx, Object msg) {
                    DatagramPacket packet = (DatagramPacket) msg;
                    InetSocketAddress ClientAddress = clientChannelMap.inverse().get(ctx.channel());
                    packet = new DatagramPacket(packet.content().retain(), ClientAddress);
                    localChannel.writeAndFlush(packet);
                }
            });
            remoteChannel = bootstrap.bind(0).sync().channel();
            remoteChannel.connect(remoteAddress).sync().channel();
            clientChannelMap.put(ClientAddress, remoteChannel);
        }
        packet = new DatagramPacket(packet.content().retain(), remoteAddress);
        if (remoteChannel != null) {
            remoteChannel.writeAndFlush(packet);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (clientChannelMap.containsKey(ClientAddress)) {
            Objects.requireNonNull(Objects.requireNonNull(clientChannelMap.get(ClientAddress))).close();
            clientChannelMap.remove(ClientAddress);
        }
    }
}
