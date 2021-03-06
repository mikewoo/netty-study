package com.mikewoo.echo.client;

import com.mikewoo.echo.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Echo Service Netty客户端引导程序
 *
 * @auther Phantom Gui
 * @date 2018/5/31 16:26
 */
public class EchoClient {

    private static final Logger LOG = LoggerFactory.getLogger(EchoClient.class);

    public final static int EXIT_NORMAL = 0;
    public final static int EXIT_ABNORMAL = 1;

    private String host;

    private int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            LOG.info("echo client started && connect to server {} : {}", host, port);
            ChannelFuture f = bootstrap.connect().sync();
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            LOG.info("exception happened, {}", e);
            System.exit(EXIT_ABNORMAL);
        } finally {
            group.shutdownGracefully();
        }
        System.exit(EXIT_NORMAL);
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 8080;
        if (args.length == 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        }

        new EchoClient(host, port).start();

        // 并发测试
        // connectTest(host, port);
    }

    private static void connectTest(String host, int port) {
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LOG.info("discard client started && connect to server {} : {}", host, port);
                    new EchoClient(host, port).start();
                }
            }).start();
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
