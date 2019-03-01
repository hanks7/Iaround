package net.iaround.ui.interfaces;
/**
 * @ClassName AudioRecordCallBack.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-28 上午10:24:47
 * @Description: 录音过程中的接口回调
 */

public interface AudioRecordCallBack
{
	//录音开始
    void AudioRecordStart(long flag, String filePath);
	//录音音量反馈
    void AudioVolumeFeedback(double volume);
	//录音异常的回调(权限受限..)
    void AudioRecordError(long flag);
	//录音结束
    void AudioRecordEnd(long flag, String filePath);
}
