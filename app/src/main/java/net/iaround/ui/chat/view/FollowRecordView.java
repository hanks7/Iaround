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

public class FollowRecordView extends ChatRecordView {
	private TextView mNoticeView;

	public FollowRecordView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.chat_record_follow, this);
		mNoticeView = (TextView) findViewById(R.id.notice);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {

	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		mNoticeView.setText(FaceManager.getInstance(context)
				.parseIconForString(mNoticeView, context, record.getContent(),
						textSize_Notice));
	}

	@Override
	public void reset() {
	}

}
