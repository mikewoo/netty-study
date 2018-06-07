package com.mikewoo.http.handler;

import com.mikewoo.util.ByteBufToBytes;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty 实现的HTTP客户端ChannelHandler处理器
 *
 * @auther Phantom Gui
 * @date 2018/6/7 11:19
 */
public class HttpClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(HttpClientHandler.class);

    private ByteBufToBytes reader;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpResponse) {
            HttpResponse response = (HttpResponse) msg;
            LOG.info("CONTENT_TYPE: {}", response.headers().get(HttpHeaderNames.CONTENT_TYPE));
            LOG.info("HttpResponseStatus: {}", response.status().code());
            if (HttpUtil.isContentLengthSet(response)) {
                reader = new ByteBufToBytes((int) HttpUtil.getContentLength(response));
            }
        }
        if (msg instanceof HttpContent) {
            HttpContent httpContent = (HttpContent) msg;
            ByteBuf content = httpContent.content();
            if (reader != null) {
                reader.reading(content);
                content.release();
                if (reader.isFinish()) {
                    String resultStr = new String(reader.readFull());
                    LOG.info("Server response content: " + resultStr);
                    ctx.close();
                }
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("exceptionCaught");
        if(null != cause) cause.printStackTrace();
    }
}
