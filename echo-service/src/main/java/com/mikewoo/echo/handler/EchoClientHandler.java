package com.mikewoo.echo.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Echo Service 客户端处理器
 *
 * @auther Phantom Gui
 * @date 2018/5/31 16:19
 */
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger LOG = LoggerFactory.getLogger(EchoClientHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("channel active");
        ctx.writeAndFlush(Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("channel Inactive");
        ctx.close();
    }

    /**
     * 当某个{@link ChannelInboundHandler}的实现重写channelRead()方法时，它将负责显式地释放与池化的ByteBuf实例相关的内存。
     * Netty为此提供了一个实用方法ReferenceCountUtil.release() 但是以这种方式管理资源可能很繁琐。
     * 一个更加简单的方式是使用{@link SimpleChannelInboundHandler}，SimpleChannelInboundHandler会自动释放资源
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        LOG.info("Client recevied: " + msg.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.info("exception happend, {}", cause.toString());
        ctx.close();
    }

}
