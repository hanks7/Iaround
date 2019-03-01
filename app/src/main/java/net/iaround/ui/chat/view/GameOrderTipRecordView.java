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
 * @ClassName GameOrderTipRecordView.java
 * @Description: 订单提示
 */

public class GameOrderTipRecordView extends ChatRecordView {
	private TextView mTimeView, mDistanceView;

	public GameOrderTipRecordView(Context context) {
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

		mTimeView.setText(record.getContent());

//		if (context instanceof ChatPersonal) {
//			int distance = record.getDistance();
//			mDistanceView.setText(CommonFunction.covertSelfDistance(distance));
//		}

	}

	@Override
	public void reset() {
		mTimeView.setText("");
		mDistanceView.setText("");
	}

}
