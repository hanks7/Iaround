package net.iaround.ui.chat;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.ui.comon.QuickAction;
import net.iaround.ui.comon.QuickActionChatBar;
import net.iaround.ui.comon.QuickActionWidget;

/**
 * 聊天记录选项功能：用于多媒体的聊天记录，如语音、图片、视频、地图、礼物等
 * 
 * @author chenlb
 * 
 */
public class ChatMediaLongClickListener implements OnLongClickListener {
	private Context mContext;
	private View chatView;
	private ChatRecord record;
	private QuickActionChatBar mBar;

	public ChatMediaLongClickListener(Context context, View chatView) {
		mContext = context;
		this.chatView = chatView;
	}

	@Override
	public boolean onLongClick(View v) {
		record = (ChatRecord) v.getTag();
		if (Common.getInstance().loginUser.getUid() == record.getUid()) { // 点击的是用户本人发的记录
			prepareQuickActionBar(false);
		} else {
			prepareQuickActionBar(true);
		}
		View view = chatView.findViewById(R.id.content); // 获取聊天内容的气泡框，弹出的框将相对于此气泡框
		mBar.show(view);
		return false;
	}

	private void prepareQuickActionBar(boolean isOnLeft) {
		mBar = new QuickActionChatBar(mContext, isOnLeft);
		mBar.addQuickAction(new QuickAction(mContext, null,
				R.string.chat_record_fun_del));
		mBar.setOnQuickActionClickListener(mActionListener);
	}

	private QuickActionWidget.OnQuickActionClickListener mActionListener = new QuickActionWidget.OnQuickActionClickListener() {
		public void onQuickActionClicked(QuickActionWidget widget, int position) {
			switch (position) {
			case 0: // 删除
				deleteRecord(record);
				break;
			}
		}
	};

	/**
	 * 删除聊天记录
	 * 
	 * @param record
	 *            要删除的聊天记录
	 */
	private void deleteRecord(ChatRecord record) {
		if (mContext instanceof SuperChat) {
			((SuperChat) mContext).delOneRecord(record);
		}
	}

}
