package com.mikewoo.netty.channel;

import com.mikewoo.netty.channel.handler.FixedLengthFrameDecoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * 使用EmbeddedChannel测试ChannelHandler处理入站消息
 *
 * @auther Phantom Gui
 * @date 2018/6/5 10:13
 */
public class FixedLengthFrameDecoderTest {

    private static final int FRAME_LENGTH = 3;

    /**
     * 测试入站消息
     */
    @Test
    public void fixedLengthFrameDecoderInboundTest() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(FRAME_LENGTH));

        // 将入站消息写入EmbeddedChannel,只有经过readInbound能读出数据才返回true。
        Assert.assertTrue(channel.writeInbound(input.retain()));

        // 将EmbeddedChannel标记为完成
        Assert.assertTrue(channel.finish());

        // 从EmbeddedChannel中读取数据，读取到的数据都经过了真个ChannelPipeline处理
        ByteBuf readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        Assert.assertNull(channel.readInbound());
        buf.release();
    }

    @Test
    public void fixedLengthFrameDecoderInboundTest02() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 0; i < 9; i++) {
            buf.writeByte(i);
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(FRAME_LENGTH));

        // 将入站消息写入EmbeddedChannel,只有经过readInbound能读出数据才返回true。
        // 由于不满足FixedLengthFrameDecoder中frameLength长度要求，所以无法读出，此时就返回false。
        Assert.assertFalse(channel.writeInbound(input.readBytes(1)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(8)));

        // 将EmbeddedChannel标记为完成
        Assert.assertTrue(channel.finish());

        // 从EmbeddedChannel中读取数据，读取到的数据都经过了真个ChannelPipeline处理
        ByteBuf readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        readInbound = channel.readInbound();
        Assert.assertEquals(buf.readSlice(3), readInbound);
        readInbound.release();

        Assert.assertNull(channel.readInbound());
        buf.release();
    }
}
