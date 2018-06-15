package com.mikewoo.http.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Netty 实现的HTTP客户端ChannelHandler处理器
 *
 * @auther Phantom Gui
 * @date 2018/6/15 14:11
 */
public class FullHttpClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger LOG = LoggerFactory.getLogger(FullHttpClientHandler.class);


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOG.info("request class: {}", msg.getClass().getName());

        try {
            if (!(msg instanceof FullHttpResponse)) {
                LOG.info("msg: {}", String.valueOf(msg));
            } else {
                FullHttpResponse response = (FullHttpResponse) msg;
                HttpResponseStatus status = response.status();
                LOG.info("response status: {}", status.code());
                List<Map.Entry<String, String>> entryList = response.headers().entries();
                for (Map.Entry<String, String> map : entryList) {
                    LOG.info("response header: ({}, {})", map.getKey(), map.getValue());
                }
                String body = getBody(response);
                LOG.info("response body: {}", body);
                ctx.close();
            }
        } catch (Exception e) {
            LOG.warn("处理响应失败!");
            e.printStackTrace();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("exceptionCaught");
        if(null != cause) cause.printStackTrace();
    }
    /**
     * 获取响应body参数
     * @param response
     * @return
     */
    private String getBody(FullHttpResponse response){
        ByteBuf buf = response.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

}
