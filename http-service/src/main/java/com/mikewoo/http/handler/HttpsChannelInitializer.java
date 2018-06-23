package com.mikewoo.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * HTTP服务ChannelInitializer
 *
 * @auther Phantom Gui
 * @date 2018/6/7 10:16
 */
public class HttpsChannelInitializer extends ChannelInitializer<Channel> {

    private SslContext context;

    private boolean server;

    public HttpsChannelInitializer(SslContext context, boolean server) {
        this.context = context;
        this.server = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast("ssl-handler", new SslHandler(context.newEngine(ch.alloc())));
        if (server) { // 是服务器
            pipeline.addLast("http-decoder", new HttpRequestDecoder());
            pipeline.addLast("http-encoder", new HttpResponseEncoder());
            pipeline.addLast("htt-server-handler", new HttpServerHandler());
        } else { // 是客户端
            pipeline.addLast("http_decoder", new HttpResponseDecoder());
            pipeline.addLast("http-encoder", new HttpRequestEncoder());
            pipeline.addLast("http-client-handler", new HttpClientHandler());
        }
    }


}
