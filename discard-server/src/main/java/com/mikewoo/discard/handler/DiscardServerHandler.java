package com.mikewoo.discard.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * DiscardServer Channel Handler, extends {@link ChannelInboundHandlerAdapter}
 *
 * @auther Phantom Gui
 * @date 2018/5/3 17:10
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * socket建立连接时，触发一个inbound事件channelActive时调用
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channel active, client address is " + ctx.channel().remoteAddress().toString());
    }

    /**
     * 当读取到消息时调用
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf m = (ByteBuf) msg;
        System.out.println("read msg: " + new Date((m.readUnsignedInt() - 2208988800L) * 1000L));
        m.release();
    }

    /**
     * exceptionCaught事件处理方法由一个ExceptionEvent异常事件调用，这个异常事件起因于Netty的I/O异常或一个处理器实现的内部异常。
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exception ");
        cause.printStackTrace();
        ctx.close();
    }
}
