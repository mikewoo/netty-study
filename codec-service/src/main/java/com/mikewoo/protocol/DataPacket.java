package com.mikewoo.protocol;

/**
 * 设备控制协议格式
 *
 * @auther Phantom Gui
 * @date 2018/6/5 16:27
 */
public class DataPacket {

    public static final int MAX_DATAPACKET_LENGTH = 1024;

    public static final int HEADER_LENGTH = 5;

    public enum CommandType {
        LOGIN(1, "认证"), START(2, "启动"), STOP(3, "停止");
        private Integer command;
        private String desc;

        CommandType(int command, String desc) {
            this.command = command;
            this.desc = desc;
        }
    }

    private byte command;

    private int bodyLength;

    private byte[] body;

    public byte getCommand() {
        return command;
    }

    public void setCommand(byte command) {
        this.command = command;
    }

    public int getBodyLength() {
        return bodyLength;
    }

    public void setBodyLength(int bodyLength) {
        this.bodyLength = bodyLength;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
