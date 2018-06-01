package com.mikewoo.echo.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 未使用Netty的阻塞模式EchoServer
 *
 * @auther Phantom Gui
 * @date 2018/6/1 11:04
 */
public class EchoOioServer {

    private static final Logger LOG = LoggerFactory.getLogger(EchoOioServer.class);

    private int port;

    public EchoOioServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) {
        int port = 8080;
        if (args.length == 1) {
            port = Integer.parseInt(args[0]);
        }
        new EchoOioServer(port).start();
    }

    public void start() {
        LOG.info("server started && listen on {}", port);
        try {
            final ServerSocket socket = new ServerSocket(port); // 将服务器绑定到指定端口
            for (; ; ) {
                final Socket client = socket.accept(); // 接收客户端连接
                LOG.info(" Accected from: {}", client);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out = null;
                        InputStream in = null;
                        InputStreamReader inReader = null;
                        BufferedReader bReader = null;
                        PrintWriter printWriter = null;
                        try {
                            in = client.getInputStream();
                            inReader = new InputStreamReader(in, "UTF-8");
                            bReader = new BufferedReader(inReader);
                            String info = null;
                            while ((info = bReader.readLine()) != null) {//循环读取客户端的信息
                                LOG.info("Server recevied data： {}", info);
                            }
                            client.shutdownInput();
                            out = client.getOutputStream();
                            printWriter = new PrintWriter(out);
                            printWriter.write(info);
                            printWriter.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (printWriter != null) {
                                    printWriter.close();
                                }

                                if (out != null) {
                                    out.close();
                                }

                                if (bReader != null) {
                                    bReader.close();
                                }

                                if (inReader != null) {
                                    inReader.close();
                                }

                                if (in != null) {
                                    in.close();
                                }

                                if (client != null) {
                                    client.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
