package net.iaround.model.im;
/**
 * @ClassName GroupMessageSendFail.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-12 下午7:56:39
 * @Description: 发送群组消息失败
 * @相关协议:73008
 */

public class GroupMessageSendFail
{
	public long error;//错误码
	public long groupid;
	public long flag;
	public int ts; //禁言时间
}
