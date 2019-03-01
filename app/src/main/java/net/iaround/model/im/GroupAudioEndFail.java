package net.iaround.model.im;
/**
 * @ClassName GroupAudioEndFail.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-29 上午10:27:57
 * @Description: 73024 圈子语音发送结束失败
 */

public class GroupAudioEndFail
{
	/*
	 * 错误码说明
	 * -100  (参数不正确)
	 * -10083031参数不正确
	 * -20083031：服务端错误
	 */
	public int error;
	public long flag;//客户端消息唯一标识
}
