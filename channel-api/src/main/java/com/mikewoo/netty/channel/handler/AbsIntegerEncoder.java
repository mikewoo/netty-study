package com.mikewoo.netty.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 整数取绝对值编码器（input: -1， -2， -3.. output: 1， 2， 3...）
 *
 * @auther Phantom Gui
 * @date 2018/6/5 10:40
 */
public class AbsIntegerEncoder extends MessageToMessageEncoder<ByteBuf> {
    private static final Logger LOG = LoggerFactory.getLogger(AbsIntegerEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            int absInteger = Math.abs(in.readInt());
            LOG.info("read absInteger: {}", absInteger);
            out.add(absInteger);
        }
    }
}
