package net.iaround.ui.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;

/**
 * @ClassName FollowRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 关注提示
 */

public class ErrorPromptRecordView extends ChatRecordView {
	private TextView mNoticeView;
	private Context mContext;

	public ErrorPromptRecordView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.chat_record_follow, this);
		mNoticeView = (TextView) findViewById(R.id.notice);
		this.mContext = context;
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {

	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		mNoticeView.setText(mContext.getResources().getString(R.string.se_200083010));
	}

	@Override
	public void reset() {
		mNoticeView.setText("");
	}

}