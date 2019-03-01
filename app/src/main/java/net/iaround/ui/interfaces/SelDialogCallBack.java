package net.iaround.ui.interfaces;
/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月1日 下午6:11:11
 * @Description: 通用选择框的回调
 */
public interface SelDialogCallBack {

	/** 取消选择回调 */
    void onCancelSelect();
	
	/** 选择完成回调 */
    void onSelectFinish(String contentJson);
}
