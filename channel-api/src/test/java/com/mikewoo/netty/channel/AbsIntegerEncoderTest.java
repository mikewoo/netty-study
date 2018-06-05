package com.mikewoo.netty.channel;

import com.mikewoo.netty.channel.handler.AbsIntegerEncoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 * 使用EmbeddedChannel测试ChannelHandler处理出站消息
 *
 * @auther Phantom Gui
 * @date 2018/6/5 10:45
 */
public class AbsIntegerEncoderTest {

    @Test
    public void absIntegerEncoderTest() {
        ByteBuf buf = Unpooled.buffer();
        for (int i = 1; i < 10; i++) {
            buf.writeInt(i * -1);
        }

        ByteBuf output = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());

        Assert.assertTrue(channel.writeOutbound(output.retain()));
        Assert.assertTrue(channel.finish());

        for (int i = 1; i < 10; i++) {
            Assert.assertEquals(i, (int)channel.readOutbound());
        }

        Assert.assertNull(channel.readOutbound());

        buf.release();
    }
}
