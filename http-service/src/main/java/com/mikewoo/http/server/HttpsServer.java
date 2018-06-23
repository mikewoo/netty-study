package com.mikewoo.http.server;

import com.mikewoo.http.handler.HttpsAggregatorChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.security.KeyStore;

/**
 * Netty实现的HTTP服务器
 *
 * @auther Phantom Gui
 * @date 2018/6/7 10:12
 */
public class HttpsServer {

    private static final Logger LOG = LoggerFactory.getLogger(HttpsServer.class);

    public final static int EXIT_NORMAL = 0;
    public final static int EXIT_ABNORMAL = 1;

    private int port;

    public HttpsServer(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            String keyStorePassword = "gmm123";
            KeyManagerFactory keyManagerFactory = null;
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("D:\\tmp\\ssl\\server\\server.jks"), keyStorePassword.toCharArray());
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore,keyStorePassword.toCharArray());
            SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpsAggregatorChannelInitializer(sslContext, true))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            LOG.info("netty http server started && listen on {}", port);
            ChannelFuture f = bootstrap.bind(port).sync(); // 绑定server到指定的port来开启服务，监听等待连接到来


            f.channel().closeFuture().sync(); // 直到server socket 关闭才退出服务
        } catch (Exception e) {
            LOG.info("exception happened, {}", e);
            System.exit(EXIT_ABNORMAL);
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }

        System.exit(EXIT_NORMAL);
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length > 1) {
            port = Integer.parseInt(args[0]);
        }
        new HttpsServer(port).start();
    }
}
