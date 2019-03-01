package net.iaround.model.im;


/**
 * @Description 聊吧发送语音结束协议的成功返回（92006）
 * @author tanzy
 * @date 15/10/9
 */
public class ChatBarAudioEndSuccess
{
	public long msgid;//消息ID
	public long flag;//客户端消息唯一标识
	public String attachment;//附件URL
}
