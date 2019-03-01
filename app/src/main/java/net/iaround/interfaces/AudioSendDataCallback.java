package net.iaround.interfaces;
/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月27日 下午1:48:44
 * @Description: 语音数据发送的回调接口
 */
public interface AudioSendDataCallback {

	/** 读取需要发送数据的开始 */
    void DataSendStart(long flag);
	
	/** 读取需要发送数据的过程 */
    void DataSendProgress(long flag, int rank, byte[] data, int dataLength);
	
	/** 读取需要发送数据的结束 */
    void DataSendFinish(long flag, int rank);
	
	/** 读取需要发送的数据的异常 */
    void DataSendError(long flag);
}
