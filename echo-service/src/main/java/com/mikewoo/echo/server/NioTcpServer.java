package com.mikewoo.echo.server;

import com.mikewoo.echo.handler.NioServerHandler;
import com.mikewoo.echo.handler.SimpleNioServerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 非阻塞版本TcpServer
 *
 * @auther Phantom Gui
 * @date 2018/6/1 13:43
 */
public class NioTcpServer {

    public final static int EXIT_NORMAL = 0;
    public final static int EXIT_ABNORMAL = 1;
    private static final Logger LOG = LoggerFactory.getLogger(NioTcpServer.class);
    // 缓冲区大小
    private static final int BUF_SIZE = 1024;
    // 超时时间，单位毫秒
    private static final int TIMEOUT = 3000;
    /**
     * singleton instance
     */
    private static NioTcpServer instance = new NioTcpServer();
    /**
     * initialized
     */
    private boolean initialized = false;
    /**
     * NioServer 网络事件处理器
     */
    private NioServerHandler serverHandler;
    /**
     * 监听端口
     */
    private int port;
    /**
     * 选择器
     */
    private Selector selector;

    private NioTcpServer() {

    }

    /**
     * get singleton instance
     *
     * @return
     */
    private static NioTcpServer getInstance() {
        return instance;
    }

    private void init(int port) {
        if (initialized) {
            return;
        }
        try {
            // 打开监听信道
            ServerSocketChannel listenerChannel = ServerSocketChannel.open();

            // 与本地端口绑定
            setPort(port);
            listenerChannel.socket().bind(new InetSocketAddress(port));

            // 设置为非阻塞模式
            listenerChannel.configureBlocking(false);

            // 创建选择器
            selector = Selector.open();

            // 将选择器绑定到监听信道,只有非阻塞信道才可以注册选择器.并在注册过程中指出该信道可以进行Accept操作
            //一个server socket channel准备好接收新进入的连接称为“接收就绪”
            listenerChannel.register(selector, SelectionKey.OP_ACCEPT);

            // 创建网络事件处理器
            serverHandler = new SimpleNioServerHandler(BUF_SIZE);
        } catch (Exception e) {
            LOG.info("init failed", e);
            System.exit(EXIT_ABNORMAL);
        }
        initialized = true;
        LOG.info("init NioServer success");
    }

    private void start() {
        LOG.info("NioServer started && listen on {}", port);
        while (true) {
            try {
                // 等待连接就绪(或超时)
                int selectKeys = selector.select(TIMEOUT);
                if (selectKeys == 0) {
                    //刚启动时keys为0,等待连接
                    continue;
                }
            } catch (IOException e) {
                break;
            }

            // 获取已准备就绪连接的I/O操作SelectionKey集合
            Set<SelectionKey> keys = selector.selectedKeys();
            // 取得SelectionKey集合迭代器
            Iterator<SelectionKey> iterator = keys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                try {
                    if (key.isAcceptable()) {
                        LOG.info("server channel acceptable");

                        serverHandler.handleAccept(key);
                    }

                    if (key.isReadable()) {
                        // 从客户端读取数据
                        LOG.info("server channel readable");
                        serverHandler.handleRead(key);
                    }

                    if (key.isValid() && key.isWritable()) {
                        //客户端连接一次后，N次连续进入该方法
                        // LOG.info("server channel writable");//连续输出
                        serverHandler.handleWrite(key);
                    }
                } catch (IOException ex) {
                    // 出现I/O异常（如客户端断开连接）时移除处理过的键
                    try {
                        key.cancel();
                        key.channel().close();
                        iterator.remove();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }

                // 移除处理过的键
                iterator.remove();
            }
        }
    }

    private void run(int port) {
        init(port);
        start();
        System.exit(EXIT_NORMAL);
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        NioTcpServer.getInstance().run(port);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
