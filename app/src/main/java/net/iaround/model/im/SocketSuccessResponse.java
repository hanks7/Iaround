package net.iaround.model.im;
/**
 * @ClassName {SocketSuccessResponse}
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-10 下午6:31:52
 * @Description: Socket成功响应,用于私聊发送成功回应
 * @相关协议：82010,
 */

public class SocketSuccessResponse
{
	public long flag;//消息标记
	public long msgid;//成功返回的消息id
	public int distance;//距离（单位：m)
}
