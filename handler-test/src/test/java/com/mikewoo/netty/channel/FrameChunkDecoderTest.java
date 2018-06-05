package com.mikewoo.netty.channel;

import com.mikewoo.netty.channel.handler.FrameChunkDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import org.junit.Assert;
import org.junit.Test;

/**
 * 使用EmbeddedChannel测试ChannelHandler异常处理
 *
 * @auther Phantom Gui
 * @date 2018/6/5 11:29
 */
public class FrameChunkDecoderTest {

    @Test
    public void frameChunkDecoderTest() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        Assert.assertTrue(channel.writeInbound(input.readBytes(2)));

        try {
            Assert.assertFalse(channel.writeInbound(input.readBytes(4)));
            Assert.fail();
        } catch (TooLongFrameException e) {
            // e.printStackTrace();
        }

        Assert.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assert.assertTrue(channel.finish());

        ByteBuf readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(2), readInbound);
        readInbound.release();

        readInbound = channel.readInbound();
        Assert.assertEquals(buf.skipBytes(4).readSlice(3), readInbound);
        readInbound.release();
        buf.release();
    }
}
