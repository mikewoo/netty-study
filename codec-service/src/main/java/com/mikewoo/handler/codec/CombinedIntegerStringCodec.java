package com.mikewoo.handler.codec;

import com.mikewoo.handler.codec.decode.ByteToIntegerDecoder;
import com.mikewoo.handler.codec.encode.IntegerToByteEncoder;
import io.netty.channel.CombinedChannelDuplexHandler;

/**
 * <p>{@link CombinedChannelDuplexHandler}编解码处理器。</p>
 * <p>组合{@link ByteToIntegerDecoder}解码器和{@link IntegerToByteEncoder}编码器</p>
 *
 * @auther Phantom Gui
 * @date 2018/6/5 16:49
 */
public class CombinedIntegerStringCodec extends CombinedChannelDuplexHandler<ByteToIntegerDecoder, IntegerToByteEncoder> {

    public CombinedIntegerStringCodec() {
        super(new ByteToIntegerDecoder(), new IntegerToByteEncoder());
    }
}
