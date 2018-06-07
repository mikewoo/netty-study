package com.mikewoo.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * ByteBuf到byte[]转换工具类
 *
 * @auther Phantom Gui
 * @date 2018/6/7 11:23
 */
public class ByteBufToBytes {

    private ByteBuf buf;

    private boolean finish = true;

    public ByteBufToBytes(int length) {
        buf = Unpooled.buffer(length);
    }
    public void reading(ByteBuf datas) {
        datas.readBytes(buf, datas.readableBytes());
        if (this.buf.writableBytes() != 0) {
            finish = false;
        } else {
            finish = true;
        }
    }

    public byte[] readFull() {
        if (finish) {
            byte[] contentByte = new byte[this.buf.readableBytes()];
            this.buf.readBytes(contentByte);
            this.buf.release();
            return contentByte;
        } else {
            return null;
        }
    }
    public byte[] read(ByteBuf datas) {
        byte[] bytes = new byte[datas.readableBytes()];
        datas.readBytes(bytes);
        return bytes;
    }

    public boolean isFinish() {
        return finish;
    }
}
