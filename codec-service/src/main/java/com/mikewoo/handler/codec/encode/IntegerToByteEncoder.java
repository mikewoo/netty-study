package com.mikewoo.handler.codec.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * <p>{@link Integer}类型到字节流编码器。</p>
 *
 * @auther Phantom Gui
 * @date 2018/6/5 16:09
 */
public class IntegerToByteEncoder extends MessageToByteEncoder<Integer> {


    @Override
    protected void encode(ChannelHandlerContext ctx, Integer msg, ByteBuf out) throws Exception {
        out.writeInt(msg);
    }
}
