package com.mikewoo.http.client;

import com.mikewoo.http.handler.HttpsAggregatorChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.KeyManagerFactory;
import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.KeyStore;

/**
 * Netty实现的HTTP客户端
 *
 * @auther Phantom Gui
 * @date 2018/6/7 11:08
 */
public class HttpsClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpsClient.class);

    public final static int EXIT_NORMAL = 0;
    public final static int EXIT_ABNORMAL = 1;

    private String host;

    private int port;

    public HttpsClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            String keyStorePassword = "gmm123";
            KeyManagerFactory keyManagerFactory = null;
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream("D:\\tmp\\ssl\\client\\client.jks"), keyStorePassword.toCharArray());
            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, keyStorePassword.toCharArray());
            SslContext sslContext = SslContextBuilder.forServer(keyManagerFactory).build();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // .handler(new HttpChannelInitializer(false));
                    .handler(new HttpsAggregatorChannelInitializer(sslContext, false));
            LOG.info("netty http client started && connect to server {} : {}", host, port);
            ChannelFuture f = bootstrap.connect().sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    URI uri = new URI("https://" + host + ":" + port + "");
                    String msg = "netty client is ready...";
                    DefaultFullHttpRequest request = new DefaultFullHttpRequest(
                            HttpVersion.HTTP_1_1, HttpMethod.POST, uri.toASCIIString(),
                            Unpooled.wrappedBuffer(msg.getBytes()));
                    // 构建http请求
                    request.headers().set(HttpHeaderNames.HOST, host);
                    request.headers().set(HttpHeaderNames.CONNECTION,
                            HttpHeaderNames.CONNECTION);
                    request.headers().set(HttpHeaderNames.CONTENT_LENGTH,
                            request.content().readableBytes());

                    // 发送http请求
                    f.channel().write(request);
                    f.channel().flush();
                }
            });
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

        new HttpsClient(host, port).start();

    }
}
