package org.shadowsocks.handler.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.shadowsocks.crypto.SSCrypto;

import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicReference;

public class ClientDataHandler extends ChannelInboundHandlerAdapter {


    private static Logger logger = LoggerFactory.getLogger(ClientDataHandler.class);
    private final SSCrypto ssCrypto;
    private final AtomicReference<Channel> remoteChannel = new AtomicReference<>();
    private final ByteBuf clientCache;

    public ClientDataHandler(String host, int port, ChannelHandlerContext clientCtx, ByteBuf clientCache, SSCrypto ssCrypto) {
        this.ssCrypto = ssCrypto;
        this.clientCache = clientCache;
        init(host, port, clientCtx, clientCache, ssCrypto);
    }

    private void init(String host, int port, final ChannelHandlerContext clientCtx, final ByteBuf byteBuffer, final SSCrypto ssCrypto) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientCtx.channel().eventLoop())
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5 * 1000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new RemoteDataHandler(clientCtx, ssCrypto, byteBuffer));
                    }
                });
        try {
            ChannelFuture channelFuture = bootstrap.connect(InetAddress.getByName(host), port);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        logger.info("successfully to connect to {}:{}", host, port);
                        remoteChannel.set(future.channel());
                    } else {
                        logger.info("error to connect to {}:{}", host, port);
                        clientCtx.close();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            clientCtx.close();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buff = (ByteBuf) msg;
        if (buff.readableBytes() <= 0) {
            return;
        }
        byte[] bytes = ByteBufUtil.getBytes(buff);
        byte[] decrypt = ssCrypto.decrypt(bytes, bytes.length);
        if(remoteChannel.get() == null) {
            clientCache.writeBytes(decrypt);
        } else {
            remoteChannel.get().writeAndFlush(Unpooled.copiedBuffer(decrypt));
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        if(remoteChannel.get() != null){
            remoteChannel.get().close();
        }
    }

    public static class RemoteDataHandler extends SimpleChannelInboundHandler<ByteBuf> {

        private final ChannelHandlerContext clientCtx;
        private final SSCrypto ssCrypto;
        private final ByteBuf byteBuffer;

        public RemoteDataHandler(ChannelHandlerContext clientCtx, SSCrypto ssCrypto, ByteBuf byteBuffer) {
            this.clientCtx = clientCtx;
            this.ssCrypto = ssCrypto;
            this.byteBuffer = byteBuffer;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            ctx.writeAndFlush(byteBuffer);
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            byte[] bytes = ByteBufUtil.getBytes(msg);
            try {
                byte[] encrypt = ssCrypto.encrypt(bytes, bytes.length);
                clientCtx.writeAndFlush(Unpooled.copiedBuffer(encrypt));
            } catch (Exception e) {
                ctx.close();
                clientCtx.close();
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.close();
            clientCtx.close();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ctx.close();
            clientCtx.close();
        }
    }
}
