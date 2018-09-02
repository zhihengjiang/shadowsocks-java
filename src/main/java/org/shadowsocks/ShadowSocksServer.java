package org.shadowsocks;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.shadowsocks.config.Config;
import org.shadowsocks.crypto.CryptoFactory;
import org.shadowsocks.handler.server.AddressHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ShadowSocksServer {

    private static Logger logger = LoggerFactory.getLogger(ShadowSocksServer.class);
    Config config;
    public ShadowSocksServer(Config config){
        this.config = config;
    }

    public void start() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup(2);
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(config.getServerPort())
                    .option(ChannelOption.SO_TIMEOUT, config.getTimeout())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new AddressHandler(CryptoFactory.create(config.getMethod(),
                                    config.getPassword())));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind().sync();
            logger.info("started and listen on " + channelFuture.channel().localAddress());
            channelFuture.channel().closeFuture().sync();

        } finally {
            group.shutdownGracefully();
        }

    }
}
