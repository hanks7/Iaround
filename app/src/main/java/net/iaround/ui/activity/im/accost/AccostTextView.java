package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.text.SpannableString;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-文本
 */

public class AccostTextView extends AccostRecordView {

	private TextView tvTime;
	private TextView tvContent;

	public AccostTextView(Context context) {
		super(context);
		createView(R.layout.accost_record_text);
	}

	@Override
	public void build(ChatRecord record) {
		tvContent = (TextView) findViewById(R.id.tvContent);

		SpannableString spSign = FaceManager.getInstance(mContext)
				.parseIconForString(tvContent, mContext, record.getContent(),
						16);

		tvContent.setText(spSign);

		tvTime = (TextView) findViewById(R.id.tvTime);
		String timeStr = TimeFormat.timeFormat4(mContext, record.getDatetime());
		tvTime.setText(timeStr);

	}

}
