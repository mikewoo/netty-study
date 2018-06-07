package com.mikewoo.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * HTTP服务ChannelInitializer
 *
 * @auther Phantom Gui
 * @date 2018/6/7 10:16
 */
public class HttpChannelInitializer extends ChannelInitializer<Channel> {

    private boolean server;

    public HttpChannelInitializer(boolean server) {
        this.server = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
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
