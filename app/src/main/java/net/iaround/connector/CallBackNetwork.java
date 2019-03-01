
package net.iaround.connector;


import net.iaround.model.im.TransportMessage;

/**
 * 网络通信回调接口
 * socket方式
 *
 * @author Administrator
 */
public interface CallBackNetwork {
    /**
     * 接收消息的回调方法
     */
    void onReceiveMessage(TransportMessage message);

//	/** 异常信息回调 */
//	public void onErrorMessage( int e );

    /**
     * 协议发送成功还是失败的回调
     */
    void onSendCallBack(int e, long flag);

    /**
     * socket连接成功回调
     */
    void onConnected();
}
