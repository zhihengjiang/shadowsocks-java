package org.shadowsocks.handler.local;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.socksx.v5.DefaultSocks5CommandRequest;
import org.shadowsocks.crypto.SSCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.IDN;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Local2RemoteHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(Local2RemoteHandler.class);

    private ChannelFuture destChannelFuture;
    private SSCrypto ssCrypto;
    private DefaultSocks5CommandRequest socks5CommandRequest;
    private boolean isProxy = true;
    private boolean addAddress = false;

    public Local2RemoteHandler(ChannelFuture destChannelFuture,SSCrypto ssCrypto, DefaultSocks5CommandRequest msg,
                               boolean isProxy) {
        this.destChannelFuture = destChannelFuture;
        this.ssCrypto = ssCrypto;
        this.socks5CommandRequest = msg;
        this.isProxy = isProxy;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(isProxy){
            ByteBuf buff = (ByteBuf)msg;
            if(!addAddress){
                ByteBuf addressInfo = parseAddress(socks5CommandRequest);
                addressInfo.writeBytes(buff);
                buff = addressInfo;
                addAddress = true;
            }
            byte[] plainTxt = ByteBufUtil.getBytes(buff);
            byte[] encrypt = ssCrypto.encrypt(plainTxt,plainTxt.length);
            logger.info("relay message to remote server from client");
            destChannelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(encrypt));
        }
        else {
            destChannelFuture.channel().writeAndFlush(msg);
        }

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.trace("close connection");
        destChannelFuture.channel().close();
    }

    private ByteBuf parseAddress (DefaultSocks5CommandRequest msg) throws Exception{
        ByteBuf buff = Unpooled.buffer();
        byte addressType = msg.dstAddrType().byteValue();
        int port  = msg.dstPort();
        String host = msg.dstAddr();
        if(addressType == 0x01){
            buff.writeByte(0x01);
            InetAddress address = Inet4Address.getByName(host);
            byte[] addr = address.getAddress();
            buff.writeBytes(addr);
        }
        if(addressType == 0x03){
            buff.writeByte(0x03);
            String address = IDN.toASCII(host);
            byte[] addr = address.getBytes(StandardCharsets.US_ASCII);
            System.out.println(address+","+host+","+addr.length+","+Arrays.toString(addr));
            buff.writeByte(addr.length);
            buff.writeBytes(addr);
        }
        else
            throw new IllegalArgumentException("IP v6 not supported");
        buff.writeShort(port);
        return buff;
    }

}