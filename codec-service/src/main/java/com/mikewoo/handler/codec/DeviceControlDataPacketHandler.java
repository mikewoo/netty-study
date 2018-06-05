package com.mikewoo.handler.codec;

import com.mikewoo.protocol.DataPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * <p>设备控制指令解析编解码处理器。</p>设备控制指令解析编解码处理器。
 * <p>指令格式[包头+包体]：[控制指令+数据Body长度][数据Body]</p>
 * <p>控制指令：</p>
 * <p>  0x01: 认证</p>
 * <p>  0x02: 启动</p>
 * <p>  0x03: 停止</p>
 * @auther Phantom Gui
 * @date 2018/6/5 16:19
 */
public class DeviceControlDataPacketHandler extends MessageToMessageCodec<ByteBuf, DataPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DataPacket msg, List<Object> out) throws Exception {
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(msg.getCommand());
        buf.writeInt(msg.getBodyLength());
        buf.readBytes(msg.getBody());
        out.add(buf);

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int readBytes = msg.readableBytes();
        if (readBytes >= DataPacket.HEADER_LENGTH) {
            if (readBytes > DataPacket.MAX_DATAPACKET_LENGTH) {
                msg.skipBytes(readBytes);
                throw new TooLongFrameException("data packet length " + readBytes + " must be less than equal to or equal to " + DataPacket.MAX_DATAPACKET_LENGTH);
            }
            DataPacket dataPacket = new DataPacket();
            dataPacket.setCommand(msg.readByte());
            int bodyLength = msg.readInt();
            dataPacket.setBodyLength(bodyLength);
            if (bodyLength > 0) {
                byte[] body = new byte[bodyLength];
                msg.readBytes(body);
                dataPacket.setBody(body);
            }
        }
    }
}
