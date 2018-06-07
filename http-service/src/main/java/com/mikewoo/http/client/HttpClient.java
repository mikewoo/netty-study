package com.mikewoo.http.client;

import com.mikewoo.http.handler.HttpChannelInitializer;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.URI;

/**
 * Netty实现的HTTP客户端
 *
 * @auther Phantom Gui
 * @date 2018/6/7 11:08
 */
public class HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClient.class);

    public final static int EXIT_NORMAL = 0;
    public final static int EXIT_ABNORMAL = 1;

    private String host;

    private int port;

    public HttpClient(String host, int port) {
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
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new HttpChannelInitializer(false));
            LOG.info("netty http client started && connect to server {} : {}", host, port);
            ChannelFuture f = bootstrap.connect().sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    URI uri = new URI("http://" + host + ":" + port + "");
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

        new HttpClient(host, port).start();

    }
}
