package com.mikewoo.discard.server;

import com.mikewoo.discard.handler.DiscardServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DiscardServer
 *
 * @auther Phantom Gui
 * @date 2018/5/3 17:53
 */
public class DiscardServer {

    private static final Logger LOG = LoggerFactory.getLogger(DiscardServer.class);

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // 一个多线程EventLoop用于处理I/O操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap(); // netty server 配置辅助类
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // 指定连接的Channel类型
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 每一个新连接的Channel配置辅助类
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new DiscardServerHandler());
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // Connections配置项
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // 每个Connection中Channel的配置项

            LOG.info("discard server started && listen on {}", port);
            ChannelFuture f = b.bind(port).sync(); // 绑定server到指定的port来开启服务，监听等待连接到来


            f.channel().closeFuture().sync(); // 直到server socket 关闭才退出服务
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        } else {
            port = 8080;
        }
        new DiscardServer(port).run();
    }
}
