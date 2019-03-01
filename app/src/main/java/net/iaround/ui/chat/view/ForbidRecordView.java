package net.iaround.ui.chat.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;


public class ForbidRecordView extends ChatRecordView {
	private TextView forbidTipView;

	public ForbidRecordView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(
				R.layout.chat_room_record_list_forbid_say_item, this);
		forbidTipView = (TextView) findViewById(R.id.forbidTip);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		forbidTipView.setText(FaceManager.getInstance(context)
				.parseIconForString(forbidTipView, context,
						record.getContent(), textSize_Notice));
	}

	@Override
	public void reset() {

	}

}
