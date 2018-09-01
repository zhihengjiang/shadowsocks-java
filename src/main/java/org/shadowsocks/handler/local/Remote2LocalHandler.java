package org.shadowsocks.handler.local;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.shadowsocks.crypto.SSCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Remote2LocalHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Remote2LocalHandler.class);
    private ChannelHandlerContext clientChannelContext;
    private SSCrypto ssCrypto;
    private boolean isProxy = true;

    public Remote2LocalHandler(ChannelHandlerContext clientChannelContext, SSCrypto ssCrypto, boolean isProxy) {
        this.clientChannelContext = clientChannelContext;
        this.ssCrypto = ssCrypto;
        this.isProxy = isProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx2, Object remoteMsg) throws Exception {
        if(isProxy){
            ByteBuf buff = (ByteBuf)remoteMsg;
            byte[] encrypted = ByteBufUtil.getBytes(buff);
            System.out.println(Arrays.toString((ssCrypto.getIV(false))));
            byte[] decrypted = ssCrypto.decrypt(encrypted,encrypted.length);
            System.out.println(Arrays.toString((ssCrypto.getIV(false)))+encrypted.length+","+decrypted.length);
            System.out.println(new String(decrypted,StandardCharsets.UTF_8));
            logger.info("relay response of target server to client");
//        System.out.println("========="+Unpooled.copiedBuffer(decrypted).toString(StandardCharsets.US_ASCII));
            clientChannelContext.writeAndFlush(Unpooled.copiedBuffer(decrypted));
        }
        else {
            clientChannelContext.writeAndFlush(remoteMsg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx2) throws Exception {
        logger.trace("close connection to target server");
        clientChannelContext.channel().close();
    }
}
