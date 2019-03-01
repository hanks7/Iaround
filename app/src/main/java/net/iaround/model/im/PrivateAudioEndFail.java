package net.iaround.model.im;
/**
 * @ClassName PrivateAudioEndFail.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-27 下午9:44:06
 * @Description: 83054私聊语音发送结束失败, 83010私聊发送失败
 */

public class PrivateAudioEndFail
{
	/*
	 * 错误码说明:
	 * -10083031：参数不正确
	 * -20083031：服务端错误
	 */
	public int error;//错误码
	public long flag;//客户端消息唯一标识
}
