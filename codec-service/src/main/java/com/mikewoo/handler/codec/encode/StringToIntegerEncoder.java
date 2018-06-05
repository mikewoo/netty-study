package com.mikewoo.handler.codec.encode;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * <p>{@link String}类型到{@link Integer}类型编码器</p>
 *
 * @auther Phantom Gui
 * @date 2018/6/5 16:13
 */
public class StringToIntegerEncoder extends MessageToMessageEncoder<String> {

    @Override
    protected void encode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
        out.add(Integer.parseInt(msg));
    }
}
