package com.mikewoo.handler.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * <p>byte字节流到int类型转换解码器。数据长度最大不能超过1024字节，否则会抛出{@link TooLongFrameException}异常</p>
 * <p>继承自{@link ByteToMessageDecoder}, 在每次调用decode()方法时，都需要检查输入的ByteBuf是否有足够的数据，具有一定的开销。</p>
 *
 * @auther Phantom Gui
 * @date 2018/6/5 15:34
 */
public class ByteToIntegerDecoder extends ByteToMessageDecoder {

    private static final int MAX_FRAME_LENGTH = 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if (readableBytes > MAX_FRAME_LENGTH) {
            in.skipBytes(readableBytes);
            throw new TooLongFrameException("frameLength " + readableBytes + " must be less than equal to or equal to " + MAX_FRAME_LENGTH);
        }
        // 当有4个字节可读时，调用readInt方法读取int类型数据
        if (readableBytes >= 4) {
            out.add(in.readInt());
        }
    }
}
