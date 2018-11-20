package me.asu.socket.client;


import me.asu.socket.message.IMessage;
import me.asu.socket.message.IProtoMessage;

/**
 * 通知代理.
 * @author suk
 */
public interface Delegate {
    /**
     * 打开连接通知.
     * @return IMessage
     */
    void onOpen(String host, int port);
    /**
     * 成功发送通知.
     * @return IMessage
     */
    void onSend(IMessage ibox);
    /**
     * 接收数据通知.
     * @return IMessage
     */
    void onRecv(IMessage ibox);
    /**
     * 关闭通知.
     */
    void onClose(String host, int port);
    /**
     * 错误通知.
     * @return IMessage
     */
    void onError(int code, IMessage ibox);
    /**
     * 超时通知.
     */
    void onTimeout(String host, int port);

    /**
     * 创建一个数据对象.
     * @return IMessage
     */
    IProtoMessage createMessage();
}
