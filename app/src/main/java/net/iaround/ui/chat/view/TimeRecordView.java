package net.iaround.ui.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatPersonal;

/**
 * @ClassName TimeRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 时间间隔
 */

public class TimeRecordView extends ChatRecordView {
	private TextView mTimeView, mDistanceView;

	public TimeRecordView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.chat_record_time, this);
		mTimeView = (TextView) findViewById(R.id.time);
		mDistanceView = (TextView) findViewById(R.id.distance);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		String time = TimeFormat.timeFormat5(context, record.getDatetime());
		mTimeView.setText(time);

		if (context instanceof ChatPersonal) {
			int distance = record.getDistance();
			mDistanceView.setText(CommonFunction.covertSelfDistance(distance));
		}

	}

	@Override
	public void reset() {
		mTimeView.setText("");
		mDistanceView.setText("");
	}

}
