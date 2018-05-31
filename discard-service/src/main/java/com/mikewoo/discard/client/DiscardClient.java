package com.mikewoo.discard.client;

import com.mikewoo.discard.handler.DiscardClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DiscardClient Server
 *
 * @auther Phantom Gui
 * @date 2018/5/3 18:07
 */
public class DiscardClient {

    private static final Logger LOG = LoggerFactory.getLogger(DiscardClient.class);

    public static void main(String[] args) throws Exception {
        String host = "127.0.0.1";
        int port = 8080;
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap(); // netty client 配置辅助类
            b.group(workerGroup); // 添加EventLoopGroup
            b.channel(NioSocketChannel.class); // 设置通道类型
            b.option(ChannelOption.SO_KEEPALIVE, true); // Connections配置项
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new DiscardClientHandler());
                }
            });

            LOG.info("discard client started && connect to server {} : {}", host, port);
            ChannelFuture f = b.connect(host, port).sync(); // 启动客户端，连接对应的服务端


            f.channel().closeFuture().sync(); // 等待连接关闭，退出客户端
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
