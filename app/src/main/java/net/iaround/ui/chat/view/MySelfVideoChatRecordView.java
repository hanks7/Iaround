package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.im.proto.Iavchat;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.friend.bean.VideoChatBean;


/**
 * @ClassName MySelfTextRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2017-12-21 下午14:42:38
 * @Description: 我的一对一视频消息
 */
public class MySelfVideoChatRecordView extends MySelfBaseRecordView {
	private RichTextView mTextView;

	public MySelfVideoChatRecordView(Context context) {
		super(context);
		
		mTextView = (RichTextView) findViewById(R.id.content);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_video_chat_mine, this);
	}
	
	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);
		mTextView.setOnLongClickListener(mRecordLongClickListener);
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		// 设置聊天内容

		VideoChatBean bean = GsonUtil.getInstance().getServerBean(record.getContent(),VideoChatBean.class);

		String[] states = context.getResources().getStringArray(R.array.video_chat_state);

		switch (bean.getVideoState()) {
			case Iavchat.STATE_CANCEL:
				mTextView.setText(states[0]);
				break;
			case Iavchat.STATE_REJECT:
				mTextView.setText(states[2]);
				break;
			case Iavchat.STATE_TIMEOUT:
				mTextView.setText(states[4]);
				break;
			case Iavchat.STATE_USER_BUSY:
				mTextView.setText(states[5]);
				break;
			case Iavchat.STATE_CALLER_CLOSE:
			case Iavchat.STATE_CALLEE_CLOSE:
				mTextView.setText(context.getString(R.string.private_video_chat_talk_time) + " " + bean.getTalkTime());
				break;
			default:
				Log.d("getVideoState","getVideoState = " + bean.getVideoState());
				break;
		}

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
