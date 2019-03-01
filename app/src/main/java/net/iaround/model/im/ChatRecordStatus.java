package net.iaround.model.im;
/**
 * @ClassName ChatRecordStatus.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-6-5 上午11:01:38
 * @Description: 消息状态类
 * 	关于消息状态的解释:<br>
 * 	对于一条消息，可以分为：发送与接收；私聊和圈聊。<br>
 * 	发送-私聊：发送中、已达、已读、失败<br>
 * 	发送-圈聊：发送中、已达、失败<br>
 * 	接收-私聊：已达、已读<br>
 * 	接收-圈聊：已达、已读<br>
 * 	状态标识：0,无状态,收到别人的消息1为发送中，2为已达， 3为已读， 4为失败（备注，因为接收的消息已读没有作用，所以统一把接收到的消息状态设置为已达）
*/


public class ChatRecordStatus
{
	/** 无状态 */
	public final static	int NONE = 0;
	/** 发送中 */
    public final static	int SENDING = 1;
    /** 已达 */
    public final static	int ARRIVED = 2;
    /** 已读 */
    public final static	int READ = 3;
    /** 失败 */
    public final static	int FAIL = 4;
    
    public static boolean isSending( int status )
    {
    	return SENDING == status;
    }
    
    public static boolean isArrived( int status )
    {
    	return ARRIVED == status;
    }
    
    public static boolean isRead( int status )
    {
    	return READ == status;
    }
    
    public static boolean isFail( int status )
    {
    	return FAIL == status;
    }
}
