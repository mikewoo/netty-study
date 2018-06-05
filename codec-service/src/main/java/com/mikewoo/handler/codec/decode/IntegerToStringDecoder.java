package com.mikewoo.handler.codec.decode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * <p>{@link Integer}类型到{@link String}类型转换解码器。</p>
 * <p>继承自{@link MessageToMessageDecoder}, decode()方法会将Integer类型参数转换为String类型</p>
 * @auther Phantom Gui
 * @date 2018/6/5 15:58
 */
public class IntegerToStringDecoder extends MessageToMessageDecoder<Integer> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(String.valueOf(msg));
    }
}
