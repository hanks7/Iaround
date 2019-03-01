package net.iaround.model.im;
/**
 * @ClassName PrivateAudioEndSuccess.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-27 下午9:42:37
 * @Description: 82054私聊语音发送结束成功
 */

public class PrivateAudioEndSuccess
{
	public long msgid;//消息ID
	public long flag;//客户端消息唯一标识
	public String attachment;//附件URL
	public int distance;//距离（单位：m） 
}
