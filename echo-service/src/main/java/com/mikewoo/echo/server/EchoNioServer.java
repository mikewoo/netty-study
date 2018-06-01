package com.mikewoo.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 未使用Netty的非阻塞模式EchoServer
 *
 * @auther Phantom Gui
 * @date 2018/6/1 11:39
 */
public class EchoNioServer {

    private static final Logger LOG = LoggerFactory.getLogger(EchoNioServer.class);

    private int port;

    public EchoNioServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        LOG.info("server started && listen on {}", port);
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket serverSocket = serverChannel.socket();
        InetSocketAddress address = new InetSocketAddress(port);
        serverSocket.bind(address);
        Selector selector = Selector.open();
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            try {
                selector.select();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeys.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey selectionKey = keyIterator.next();
                keyIterator.remove();
                try {
                    if (selectionKey.isAcceptable()) {
                        ServerSocketChannel server = (ServerSocketChannel) selectionKey.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel channel = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        channel.read(buffer);
                        byte[] data = buffer.array();
                        String message = new String(data, Charset.forName("UTF-8"));
                        LOG.info("Recevie message: {}", message);

                        selectionKey.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                        ByteBuffer outbuffer = ByteBuffer.wrap(message.getBytes("UTF-8"));
                        channel.write(outbuffer);
                    } else if (selectionKey.isWritable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
                        while (buffer.hasRemaining()) {
                            if (client.write(buffer) == 0) {
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    selectionKey.cancel();
                    selectionKey.channel().close();
                }
            }
        }

    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        new EchoNioServer(port).start();
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
