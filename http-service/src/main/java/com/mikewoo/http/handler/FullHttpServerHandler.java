package com.mikewoo.http.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikewoo.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务器ChannelHandler处理器
 *
 * @auther Phantom Gui
 * @date 2018/6/14 20:35
 */
public class FullHttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    private static final Logger LOG = LoggerFactory.getLogger(FullHttpServerHandler.class);

    private static final String RESULT_SUCCESS = "success!";
    private static final String RESULT_UNKOWN = "unknown!";
    private static final String RESULT_FAILED = "failed!";

    private ObjectMapper mapper = new ObjectMapper();

    /**
     * 连接建立
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOG.info("新连接客户端地址:" + ctx.channel().remoteAddress());
        ctx.writeAndFlush("客户端"+ InetAddress.getLocalHost().getHostName() + "成功与服务端建立连接！ ");
        super.channelActive(ctx);
    }

    /**
     * 处理客户端请求
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        LOG.info("request class: {}", msg.getClass().getName());

        try {
            if (!(msg instanceof FullHttpRequest)) {
                LOG.info("未知请求.");
                send(ctx, HttpResponseStatus.BAD_REQUEST, RESULT_UNKOWN);
                return;
            }

            FullHttpRequest request = (FullHttpRequest) msg;

            String uri = request.uri(); // 获取路径

            // 去除浏览器"/favicon.ico"请求的干扰
            if (uri.equals("/favicon.ico")) {
                return;
            }
            HttpMethod httpMethod = request.method(); // 获取请求方法
            String body = getBody(request);

            if (HttpMethod.GET == httpMethod) {
                LOG.info("GET 请求, uri: {}", uri);
                QueryStringDecoder decoderQuery = new QueryStringDecoder(uri);
                Map<String, List<String>> uriAttributes = decoderQuery.parameters();
                for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        LOG.info("params: ({}={})", attr.getKey(), attrVal);
                    }
                }
                send(ctx, HttpResponseStatus.OK, "GET: " + uri + ", status: " + RESULT_SUCCESS);
            } else if (HttpMethod.POST == httpMethod) {
                LOG.info("POST 请求, uri: {}", uri);
                // 解析 application/json
                String contentType = getContentType(request.headers());
                parseRequestContent(request, body, contentType);

                send(ctx, HttpResponseStatus.OK, "POST: " + uri + ", status: " + RESULT_SUCCESS);
            } else if (HttpMethod.PUT == httpMethod) {
                LOG.info("PUT 请求, uri: {}, body: {}", uri, body);
                send(ctx, HttpResponseStatus.OK, "PUT: " + uri + ", status: " + RESULT_SUCCESS);
            } else if (HttpMethod.DELETE == httpMethod) {
                LOG.info("DELETE 请求, uri: {}, body: {}", uri, body);
                send(ctx, HttpResponseStatus.OK, "DELETE: " + uri + ", status: " + RESULT_SUCCESS);
            } else {
                LOG.info("未知请求, uri: {}, body: {}", uri, body);
                send(ctx, HttpResponseStatus.BAD_REQUEST, RESULT_UNKOWN);
            }
        } catch (Exception e) {
            LOG.warn("请求处理失败");
            e.printStackTrace();
        }
    }

    private void parseRequestContent(FullHttpRequest request, String body, String contentType) throws IOException {
        if ("application/json".equals(contentType)) {
            TypeReference<HashMap<String, String>> type = new TypeReference<HashMap<String, String>>() { };
            Map<String, String> params = (Map<String, String>) JsonUtil.json2Map(body, type);
            for (Map.Entry<String, String> map : params.entrySet()) {
                LOG.info("response json param: ({}, {})", map.getKey(), map.getValue());
            }
        }
        // 解析 application/x-www-form-urlencoded
        if ("application/x-www-form-urlencoded".equals(contentType)) {
            Map<String, String> postParams = getPostParams(request);
            for (Map.Entry<String, String> entry : postParams.entrySet()) {
                LOG.info("request param: ({}, {})", entry.getKey(), entry.getValue());
            }
        }

        // 解析 multipart/form-data
        if ("multipart/form-data".equals(contentType)) { // 用于文件上传

        } else {

        }
    }

    private Map<String, String> getPostParams(FullHttpRequest request) throws IOException {
        Map<String, String> requestParams = new HashMap<>();
        HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(request);
        decoder.offer(request);
        List<InterfaceHttpData> bodyHttpDatas = decoder.getBodyHttpDatas();
        for (InterfaceHttpData bodyData : bodyHttpDatas) {
            Attribute data = (Attribute) bodyData;
            requestParams.put(data.getName(), data.getValue());
        }
        return requestParams;
    }

    /**
     * 发送服务端响应数据
     * @param ctx
     * @param status
     * @param content
     */
    private void send(ChannelHandlerContext ctx, HttpResponseStatus status, String content) {
        ByteBuf responseContent = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, responseContent);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN + "; charset=UTF-8");
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, responseContent.readableBytes());
        response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
        // Close the connection as soon as the error message is sent.
        ctx.channel().writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 获取请求body参数
     * @param request
     * @return
     */
    private String getBody(FullHttpRequest request){
        ByteBuf buf = request.content();
        return buf.toString(CharsetUtil.UTF_8);
    }

    private String getContentType(HttpHeaders headers){
        String typeStr = headers.get("Content-Type").toString();
        String[] list = typeStr.split(";");
        return list[0];
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOG.warn("exceptionCaught");
        if (null != cause) cause.printStackTrace();
        String content = "Failure: " + HttpResponseStatus.BAD_REQUEST;
        send(ctx, HttpResponseStatus.BAD_REQUEST, content);
    }
}
