package com.mikewoo.netty.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * 消息长度限制解码器，当读取的消息字节数超过系统设置的消息长度上限，丢弃数据，并抛出TooLongFrameException异常
 *
 * @auther Phantom Gui
 * @date 2018/6/5 11:22
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {

    private final int maxFrameLength;

    public FrameChunkDecoder(int maxFrameLength) {
        if (maxFrameLength <= 0) {
            throw new IllegalArgumentException(
                    "maxFrameLength must be a positive integer: " + maxFrameLength
            );
        }
        this.maxFrameLength = maxFrameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();

        if (readableBytes > maxFrameLength) {
            in.clear();
            throw new TooLongFrameException("frameLength " + readableBytes + " must be less than equal to or equal to " + maxFrameLength);
        }

        ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }
}
