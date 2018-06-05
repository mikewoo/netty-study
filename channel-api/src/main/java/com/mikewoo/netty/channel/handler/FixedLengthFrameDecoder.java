package com.mikewoo.netty.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 固定长度解码器
 *
 * @auther Phantom Gui
 * @date 2018/6/5 10:06
 */
public class FixedLengthFrameDecoder extends ByteToMessageDecoder {

    private static final Logger LOG = LoggerFactory.getLogger(FixedLengthFrameDecoder.class);

    private final int frameLength;

    public FixedLengthFrameDecoder(int frameLength) {
        if (frameLength <= 0) {
            throw new IllegalArgumentException(
                    "frameLength must be a positive integer: " + frameLength
            );
        }
        this.frameLength = frameLength;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= frameLength) {
            ByteBuf buf = in.readBytes(frameLength);
            out.add(buf);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < frameLength; i++) {
                stringBuilder.append(buf.getByte(i));
            }
            LOG.info("read inBound frame: {}", stringBuilder);
        }
    }
}
