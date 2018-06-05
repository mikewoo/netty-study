package com.mikewoo.echo.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * {@link NioServerHandler} 默认实现
 *
 * @auther Phantom Gui
 * @date 2018/6/1 14:15
 */
public class SimpleNioServerHandler implements NioServerHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleNioServerHandler.class);

    private int bufferSize;

    public SimpleNioServerHandler(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void handleAccept(SelectionKey key) throws IOException {
        SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept();
        clientChannel.configureBlocking(false);
        // 将状态由OP_ACCEPT改为OP_READ。如果不执行下面的语句，则会一直是accept状态（初始时设置为了accept），无法进入另外两个逻辑
        clientChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocate(bufferSize));
    }

    @Override
    public void handleRead(SelectionKey key) throws IOException {
        // 获得与客户端通信的信道
        SocketChannel clientChannel = (SocketChannel) key.channel();

        // 得到并清空缓冲区
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();

        // 读取信息获得读取的字节数
        long bytesRead = clientChannel.read(buffer);

        if (bytesRead == -1) {
            // 没有读取到内容的情况
            clientChannel.close();
        } else {
            // 将缓冲区准备为数据传出状态
            buffer.flip();

            // 将字节转化为的字符串
            String receivedString = Charset.forName("UTF-8").newDecoder().decode(buffer).toString();

            // 输出接收到的信息
            LOG.info("Server recevie from: {}, message is: {}", clientChannel.socket().getRemoteSocketAddress(), receivedString);


            buffer = ByteBuffer.wrap(receivedString.getBytes("UTF-8"));
            clientChannel.write(buffer);

            // 设置为下一次读取或是写入做准备
            key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        }

    }

    @Override
    public void handleWrite(SelectionKey key) throws IOException {
        // 不做任何处理
    }
}
