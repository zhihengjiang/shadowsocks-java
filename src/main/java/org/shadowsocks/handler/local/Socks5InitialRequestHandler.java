package org.shadowsocks.handler.local;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.handler.codec.socksx.v5.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Socks5InitialRequestHandler extends SimpleChannelInboundHandler<DefaultSocks5InitialRequest> {
    private static final Logger logger = LoggerFactory.getLogger(Socks5InitialRequestHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5InitialRequest msg) {
        System.out.println(msg.version()+","+ msg.decoderResult());
        if(msg.decoderResult().isFailure()) {
            logger.warn("current protocol is not socks5");
            ctx.fireChannelRead(msg);
        } else {
            if(msg.version().equals(SocksVersion.SOCKS5)) {
                Socks5InitialResponse initialResponse = new DefaultSocks5InitialResponse(Socks5AuthMethod.NO_AUTH);
                ctx.writeAndFlush(initialResponse);
                System.out.println(initialResponse);
            }
        }
//        ctx.pipeline().remove(Socks5InitialRequestDecoder.class);
    }
}
