package com.mikewoo.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;

/**
 * ByteBuf API 测试类
 *
 * @auther Phantom Gui
 * @date 2018/6/4 14:12
 */
public class ByteBufApiTest {

    private static final Logger LOG = LoggerFactory.getLogger(ByteBufApiTest.class);

    /**
     * 对{@link ByteBuf}进行切片，切片与源buffer共享内部存储数据，对切片内容进行修改，也会修改对应源buffer
     */
    @Test
    public void sliceApiTest() {
        Charset uft8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty ByteBuf slice() Api Test!", uft8);
        ByteBuf slicedBuf = buf.slice(0, 13);
        LOG.info("sliced buf: {}", slicedBuf.toString(uft8));
        buf.setByte(0, (byte)'A');
        Assert.assertEquals(buf.getByte(0), slicedBuf.getByte(0));
    }

    /**
     * 复制{@link ByteBuf}一个副本，ByteBuf副本拥有独立的数据，对其修改不会影响源buffer
     */
    @Test
    public void copyApiTest() {
        Charset uft8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty ByteBuf slice() Api Test!", uft8);
        ByteBuf copyBuf = buf.copy(0, 13);
        LOG.info("copy buf: {}", copyBuf.toString(uft8));
        buf.setByte(0, 'A');
        Assert.assertNotEquals(buf.getByte(0), copyBuf.getByte(0));
    }

    /**
     * {@link ByteBuf} get()/set()操作，从指定位置开始获取/设置值，但不会改变readerIndex/writerIndex索引值
     */
    @Test
    public void getSetApiTest() {
        Charset uft8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty ByteBuf slice() Api Test!", uft8);
        LOG.info("buf: {}, buf[0]: {}", buf.toString(uft8), (char)buf.getByte(0));
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        LOG.info("base readerIndex: {}, base writerIndex: {}", readerIndex, writerIndex);
        buf.setByte(0, 'A');
        LOG.info("buf: {}, buf[0]: {}", buf.toString(uft8), (char)buf.getByte(0));
        LOG.info("current readerIndex: {}, current writerIndex: {}", readerIndex, writerIndex);
        Assert.assertEquals(readerIndex, buf.readerIndex());
        Assert.assertEquals(writerIndex, buf.writerIndex());
    }

    /**
     * {@link ByteBuf} read()/write()操作，从当前readerIndex/writerIndex开始读/写，每次操作会改变readerIndex/writerIndex索引值
     */
    @Test
    public void readWriteApiTest() {
        Charset uft8 = Charset.forName("UTF-8");
        ByteBuf buf = Unpooled.copiedBuffer("Netty ByteBuf slice() Api Test!", uft8);
        LOG.info("buf: {}", buf.toString(uft8));
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        LOG.info("base readerIndex: {}, base writerIndex: {}", readerIndex, writerIndex);
        LOG.info("read buf[0]: {}", (char)buf.readByte());
        buf.writeByte('?');
        LOG.info("buf: {}", buf.toString(uft8));
        LOG.info("current readerIndex: {}, current writerIndex: {}", buf.readerIndex(), buf.writerIndex());
        Assert.assertNotEquals(readerIndex, buf.readerIndex());
        Assert.assertNotEquals(writerIndex, buf.writerIndex());
    }

    /**
     * {@link ByteBuf} 引用计数API
     */
    @Test
    public void referenceCountApiTest() {
        Channel channel = new NioSocketChannel();
        ByteBufAllocator alloc = channel.alloc();
        ByteBuf byteBuf = alloc.directBuffer();
        Assert.assertEquals(1, byteBuf.refCnt());
        byteBuf.release();
        Assert.assertEquals(0, byteBuf.refCnt());
    }
}
