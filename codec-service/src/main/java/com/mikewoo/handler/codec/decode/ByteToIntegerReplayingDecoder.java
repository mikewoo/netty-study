package com.mikewoo.handler.codec.decode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * <p>byte字节流到int类型转换解码器。</p>
 * {@link ReplayingDecoder}继承自{@link ByteToMessageDecoder}，
 * 与ByteToMessageDecoder不同的是，不需要在每次调用decode()方法时都去检查ByteBuf数据是否足够。
 * 这段逻辑由ReplayingDecoder内部自定义的ByteBuf实现类，ReplayingDecoderByteBuf去完成。
 * <p><b>ReplayingDecoder效率低于ByteToMessageDecoder</b></p>
 * @auther Phantom Gui
 * @date 2018/6/5 15:44
 */
public class ByteToIntegerReplayingDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        out.add(in.readInt());
    }
}
