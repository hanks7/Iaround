package net.iaround.model.im;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月27日 下午4:21:11
 * @Description:语音文件基础类
 */
public class AudioBaseBean {

	//语音开始录制的时间戳
	public long flag;
	// 语音文件路径
	public String filePath;
	// 语音长度,单位：秒
	public String audioLength;
	//是否发送语音
	public boolean isSend;
}
