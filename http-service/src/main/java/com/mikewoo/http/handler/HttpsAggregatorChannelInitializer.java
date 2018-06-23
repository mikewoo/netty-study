package com.mikewoo.http.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;

/**
 * 聚合HTTP消息ChannelHandler
 * 通过{@link HttpObjectAggregator}可以把 HttpMessage 和 HttpContent 聚合成一个 FullHttpRequest 或者 FullHttpResponse （取决于是处理请求还是响应），
 * 而且它还可以帮助你在解码时忽略是否为“块”传输方式。
 *
 * @auther Phantom Gui
 * @date 2018/6/14 20:26
 */
public class HttpsAggregatorChannelInitializer extends ChannelInitializer<Channel> {

    private SslContext context;

    private boolean server;

    public HttpsAggregatorChannelInitializer(SslContext context, boolean server) {
        this.context = context;
        this.server = server;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("ssl-handler", new SslHandler(context.newEngine(ch.alloc())));
        if (server) {
            pipeline.addLast("server-codec", new HttpServerCodec());
            pipeline.addLast("http-compressor", new HttpContentCompressor()); // HTTP消息压缩
            /**
             * Netty提供了一个聚合器，可以将多个消息部分合并为 FullHttpRequest或者FullHttpResponse消息。
             * 通过这样的方式，可以总是看到完整的消息内容。
             *
             * 由于消息分段需要被缓冲，直到可以转发一个完整的消息给下一个ChannelInboundHandler。
             * 这个操作有轻微的开销。其所带来的好处便是不用关心消息碎片了
             */
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
