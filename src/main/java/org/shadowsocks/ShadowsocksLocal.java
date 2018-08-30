package org.shadowsocks;

import com.sun.security.ntlm.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import org.shadowsocks.handler.local.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShadowsocksLocal {

    private static Logger logger = LoggerFactory.getLogger(ShadowSocksServer.class);

    private int localPort;
    private int serverPort;
    private String serverHost;
    public void start() throws InterruptedException{
        EventLoopGroup worker = new NioEventLoopGroup(1);
        EventLoopGroup boss = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            bootstrap.group(worker,boss)
                    .localAddress(localPort)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new Socks5InitialRequestDecoder());
                            ch.pipeline().addLast(new Socks5InitialRequestHandler());
                            ch.pipeline().addLast(new Socks5CommandRequestDecoder());
                            ch.pipeline().addLast(new Socks5CmdRequesthanler());
                        }
                    });
            ChannelFuture futrue = bootstrap.bind().sync();
            logger.info("connected local port:" + localPort);
            futrue.channel().closeFuture().sync();
        }
        finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
