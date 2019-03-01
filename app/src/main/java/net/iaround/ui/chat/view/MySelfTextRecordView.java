package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.ui.comon.RichTextView;


/**
 * @ClassName MySelfTextRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 我的文本消息
 */
public class MySelfTextRecordView extends MySelfBaseRecordView {
	private RichTextView mTextView;

	public MySelfTextRecordView(Context context) {
		super(context);
		
		mTextView = (RichTextView) findViewById(R.id.content);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_text_mine, this);
	}
	
	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);
		mTextView.setOnLongClickListener(mRecordLongClickListener);
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		// 设置聊天内容
		mTextView.setText(record.getContent());
		mTextView.parseIcon();

		checkbox.setChecked(record.isSelect());
		mTextView.setTag(R.id.im_preview_uri,record);

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		checkbox.setTag(record);
		setTag(record);

		// 设置头像
		setUserIcon(context, record, mIconView);

		// 设置消息状态
		updateStatus(context, record);

		//设置用户昵称
		setUserNameDis(context, record,record.getMgroupRole());
	}

	@Override
	public void reset() {
		mTextView.setText("");
		mStatusView.setText("");
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		mTextView.setEnabled(isEnable);
	}

}
