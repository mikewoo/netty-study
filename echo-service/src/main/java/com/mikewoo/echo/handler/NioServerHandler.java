package com.mikewoo.echo.handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * NIO Echo Server 服务端处理器
 *
 * @auther Phantom Gui
 * @date 2018/6/1 13:37
 */
public interface NioServerHandler {

    /**
     * 处理{@link SocketChannel} Accept事件
     * @param key
     * @throws IOException
     */
    void handleAccept(SelectionKey key) throws IOException;

    /**
     * 处理{@link SelectionKey} Read事件
     * @param key
     * @throws IOException
     */
    void handleRead(SelectionKey key) throws IOException;

    /**
     * 处理{@link SocketChannel} Write事件
     * @param key
     * @throws IOException
     */
    void handleWrite(SelectionKey key) throws IOException;
}
