package com.mikewoo.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;

/**
 * 聚合HTTP消息ChannelHandler
 * 通过{@link HttpObjectAggregator}可以把 HttpMessage 和 HttpContent 聚合成一个 FullHttpRequest 或者 FullHttpResponse （取决于是处理请求还是响应），
 * 而且它还可以帮助你在解码时忽略是否为“块”传输方式。
 *
 * @auther Phantom Gui
 * @date 2018/6/14 20:26
 */
public class HttpAggregatorChannelInitializer extends ChannelInitializer<Channel> {


    private boolean server;

    public HttpAggregatorChannelInitializer(boolean server) {
        this.server = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (server) {
            pipeline.addLast("server-codec", new HttpServerCodec());
            pipeline.addLast("http-compressor", new HttpContentCompressor()); // HTTP消息压缩
            pipeline.addLast("http-aggregator", new HttpObjectAggregator(512 * 1024)); // 消息大小为512KB,聚合
            pipeline.addLast("http-server-handler", new FullHttpServerHandler());
        } else {
            pipeline.addLast("client-codec", new HttpClientCodec());
            pipeline.addLast("http-decompressor", new HttpContentDecompressor());
            pipeline.addLast("http-aggregator", new HttpObjectAggregator(512 * 1024)); // 消息大小为512KB,聚合
            pipeline.addLast("http-client-handler", new FullHttpClientHandler());
        }

    }
}
