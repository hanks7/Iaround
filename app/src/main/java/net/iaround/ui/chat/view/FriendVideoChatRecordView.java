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
 * @ClassName FriendTextRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2017-12-21 下午12:42:38
 * @Description: 朋友的一对一视频消息
 */

public class FriendVideoChatRecordView extends FriendBaseRecordView {
	private RichTextView mTextView;
	private int grouprole;

	public FriendVideoChatRecordView(Context context) {
		super(context);

		mTextView = (RichTextView) findViewById(R.id.content);
//		mTextView.setBackgroundResource(contentBackgroundRes);

//		int paddingLeft = CommonFunction.dipToPx(context, 24);
//		int paddingRight = CommonFunction.dipToPx(context, 12);
//		int paddingVer = CommonFunction.dipToPx(context, 9);
//		mTextView.setPadding(paddingLeft, paddingVer, paddingRight, paddingVer);
	}

	public void setGrouprole(int grouprole)
	{
		this.grouprole = grouprole;
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_video_chat_other,
				this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);

		if (!bIsSystemUser(record.getFuid())) {
			mTextView.setOnLongClickListener(mRecordLongClickListener);
		}
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {

		VideoChatBean bean = GsonUtil.getInstance().getServerBean(record.getContent(),VideoChatBean.class);
		String[] states = context.getResources().getStringArray(R.array.video_chat_state);

		switch (bean.getVideoState()) {
			case Iavchat.STATE_CANCEL:
				mTextView.setText(states[1]);
				break;
			case Iavchat.STATE_REJECT:
				mTextView.setText(states[3]);
				break;
			case Iavchat.STATE_TIMEOUT:
				mTextView.setText(states[2]);
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

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		checkbox.setTag(record);
		mTextView.setTag(R.id.im_preview_uri,record);
		setTag(record);
		//设置用户备注昵称
		setUserNotename(context, record);

		//设置用户昵称
		setUserNameDis(context, record,record.getGroupRole());

		// 设置头像点击事件
		setUserIcon(context, record, mIconView);
	}

	@Override
	public void reset() {
		mIconView.getImageView().setImageBitmap(null);
		mTextView.setText("");
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		mTextView.setEnabled(isEnable);
	}


}