package org.shadowsocks.handler.local;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;

public class Socks5CmdRequesthanler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) {

    }
}
