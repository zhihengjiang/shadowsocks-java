package org.shadowsocks.handler.local;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.v5.DefaultSocks5InitialRequest;

public class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
    @Override
    public void channelRead0(ChannelHandlerContext context,DefaultSocks5InitialRequest request){

    }
}
