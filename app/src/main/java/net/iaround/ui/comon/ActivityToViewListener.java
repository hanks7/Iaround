
package net.iaround.ui.comon;


import net.iaround.model.im.TransportMessage;

/**
 * activity接收到数据之后，给当前的view分发。
 * 
 * @author Administrator
 * 
 */
public interface ActivityToViewListener
{
	// socket方式
    void onReceiveMessageListener(TransportMessage message);
	
	void onErrorMessageListener(int e);
	
	void onSendMessageListener(int e, long flag);
	
}
