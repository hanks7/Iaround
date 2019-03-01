package net.iaround.im;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * WebSocket接口
 */
public interface IWebSocketListener {

    /**
     * 连接成功
     *
     * @param webSocket
     * @param response
     */
    void connectSuccess(final WebSocket webSocket, Response response);

    /**
     * 处理接收到的message
     *
     * @param webSocket
     * @param text
     */
    void handleReceiveMessage(WebSocket webSocket, String text);

    /**
     * 不再传输传入的消息时调用
     *
     * @param webSocket
     * @param code
     * @param reason
     */
    void onClosing(WebSocket webSocket, int code, String reason);

    /**
     * 正常关闭
     *
     * @param webSocket
     * @param code
     * @param reason
     */
    void onClosed(WebSocket webSocket, int code, String reason);

    /**
     * 从网络读取或写入错误而关闭时调用。发送和传入消息都可能丢失。
     *
     * @param webSocket
     * @param t
     * @param response
     */
    void onFailure(WebSocket webSocket, Throwable t, Response response);

    /**
     * 退出语音聊天
     */
    void closeAudioRoom();

}
