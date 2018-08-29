package org.shadowsocks.handler.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.shadowsocks.crypto.SSCrypto;

import java.net.InetAddress;

public class AddressHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(AddressHandler.class);
    private final static int ADDR_TYPE_IPV4 = 1;
    private final static int ADDR_TYPE_HOST = 3;

    private final ByteBuf dataQueue = Unpooled.buffer();
    private final SSCrypto ssCrypto;


    public AddressHandler(SSCrypto ssCrypto) {
        this.ssCrypto = ssCrypto;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("connected with {}", ctx.channel());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("disconnected with {}", ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buff = (ByteBuf) msg;

        if (buff.readableBytes() <= 0) {
            return;
        }
        byte [] array = ByteBufUtil.getBytes(buff);
        byte[] decrypted = ssCrypto.decrypt(array, array.length);
        dataQueue.writeBytes(decrypted);
        if (dataQueue.readableBytes() < 2) {
            return;
        }
        String host = null;
        int port = 0;
        int addressType = dataQueue.getUnsignedByte(0);
        if (addressType == ADDR_TYPE_IPV4) {
            if (dataQueue.readableBytes() < 7) {
                return;
            }
            // addrType(1) + ipv4(4) + port(2)
            dataQueue.readUnsignedByte();
            byte[] ipBytes = new byte[4];
            dataQueue.readBytes(ipBytes);
            host = InetAddress.getByAddress(ipBytes).toString().substring(1);            
            port = dataQueue.readShort();
        } else if (addressType == ADDR_TYPE_HOST) {
            int hostLength = dataQueue.getUnsignedByte(1);
            if (dataQueue.readableBytes() < hostLength + 4) {
                return;
            }
            dataQueue.readUnsignedByte();
            dataQueue.readUnsignedByte();
            byte[] hostBytes = new byte[hostLength];
            dataQueue.readBytes(hostBytes);
            host = new String(hostBytes);
            port = dataQueue.readShort();
        } else {
            throw new IllegalStateException("unknown address type: " + addressType);
        }
        ctx.channel().pipeline().addLast(new ClientDataHandler(host, port, ctx, dataQueue, ssCrypto));
        ctx.channel().pipeline().remove(this);
    }

}
