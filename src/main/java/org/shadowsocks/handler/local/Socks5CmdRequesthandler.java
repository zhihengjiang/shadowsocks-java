package org.shadowsocks.handler.local;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.socksx.v5.*;
import org.shadowsocks.config.Config;
import org.shadowsocks.crypto.CryptoFactory;
import org.shadowsocks.crypto.SSCrypto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * handle socks5 request
 */
public class Socks5CmdRequesthandler extends SimpleChannelInboundHandler<DefaultSocks5CommandRequest> {
    private static final Logger logger = LoggerFactory.getLogger(Socks5CmdRequesthandler.class);
    private Config config;
    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private SSCrypto ssCrypto ;
    private boolean isProxy = true;

    public Socks5CmdRequesthandler(Config config){
        this.config = config;
        try{
            ssCrypto = CryptoFactory.create(config.getMethod(),config.getPassword());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DefaultSocks5CommandRequest msg) {
        if(msg.type().equals(Socks5CommandType.CONNECT)) {
            logger.trace("connecting remote server");

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(bossGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new Remote2LocalHandler(ctx,ssCrypto,isProxy));
                        }
                    });
            ChannelFuture future;
            if(isProxy){
                future = bootstrap.connect(config.getServerAddress(), config.getServerPort());
            }
            else {
                future = bootstrap.connect(msg.dstAddr(),msg.dstPort());
            }
            future.addListener(new ChannelFutureListener() {

                public void operationComplete(final ChannelFuture future) throws Exception {
                    if(future.isSuccess()) {
                        logger.info("successfully connected remote server");
                        ctx.pipeline().addLast(new Local2RemoteHandler(future,ssCrypto,msg,isProxy));
                        Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.SUCCESS, Socks5AddressType.IPv4);
                        ctx.writeAndFlush(commandResponse);
                    } else {
                        Socks5CommandResponse commandResponse = new DefaultSocks5CommandResponse(Socks5CommandStatus.FAILURE, Socks5AddressType.IPv4);
                        ctx.writeAndFlush(commandResponse);
                    }
                }

            });
        } else {
            ctx.fireChannelRead(msg);
        }
    }


}
