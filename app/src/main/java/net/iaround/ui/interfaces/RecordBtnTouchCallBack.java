package net.iaround.ui.interfaces;

/**
 * @author tanzy
 * @Description 聊天中录音按钮点击回调
 * @date 15/10/10
 */
public interface RecordBtnTouchCallBack
{
	void onRecordBtnTouchDown();
	void onRecordBtnTouchMove();
	void onRecordBtnTouchUp();
	void onRecordBtnTouchCancel();
}
