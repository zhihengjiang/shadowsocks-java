package org.shadowsocks;

import com.sun.security.ntlm.Server;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.socksx.v5.Socks5CommandRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5InitialRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5PasswordAuthRequestDecoder;
import io.netty.handler.codec.socksx.v5.Socks5ServerEncoder;
import org.shadowsocks.config.Config;
import org.shadowsocks.handler.local.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShadowsocksLocal {

    private static Logger logger = LoggerFactory.getLogger(ShadowSocksServer.class);
    private Config config;
    public ShadowsocksLocal(Config config){
        this.config = config;
    }
    public void start() throws InterruptedException{
        EventLoopGroup worker = new NioEventLoopGroup(1);
        EventLoopGroup boss = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        try{
            bootstrap.group(worker,boss)
                    .localAddress(config.getLocalPort())
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(Socks5ServerEncoder.DEFAULT);
                            ch.pipeline().addLast(new Socks5InitialRequestDecoder());
                            ch.pipeline().addLast(new Socks5InitialRequestHandler());
                            ch.pipeline().addLast(new Socks5CommandRequestDecoder());
                            ch.pipeline().addLast(new Socks5CmdRequesthandler(config));
                        }
                    });
            ChannelFuture futrue = bootstrap.bind().sync();
            logger.info("connected local port:" + config.getLocalPort());
            futrue.channel().closeFuture().sync();
        }
        finally {
            worker.shutdownGracefully();
            boss.shutdownGracefully();
        }
    }
}
