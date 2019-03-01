package net.iaround.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.RecordAccostGameBean;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.adapter.chat.ChatRecordListBaseAdapter;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年4月29日 下午2:13:23
 * @Description: 聊天举报列表的适配器
 */
public class ChatReportRecordAdapter extends ChatRecordListBaseAdapter {

	public ChatReportRecordAdapter(Context context, ArrayList<ChatRecord> mDataList) {
		super(context, mDataList);
		MAX_SELECTED_COUNT = 10;
	}

	@Override
	public int getItemViewType(int position) {
		ChatRecord record = ((ChatRecord) getItem(position));
		int type = -1;
		String recordTypeStr = record.getType();

		if (SuperChat.TIME_LINE_TYPE.equals(recordTypeStr)) {
			type = ChatRecordViewFactory.TIME_LINE;
		} else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_QUE) {
			type = ChatRecordViewFactory.ACCOST_GAME_QUE;
		} else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_NOTICE) {
			type = ChatRecordViewFactory.ACCOST_NOTICE;
		} else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.FRIEND_CHCAT_VIDEO) {
			type = ChatRecordViewFactory.FRIEND_CHCAT_VIDEO;
		} else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_ANS) {
			String content = record.getContent();
			RecordAccostGameBean bean = GsonUtil.getInstance().getServerBean(
					content, RecordAccostGameBean.class);
			if (bean.bIsTextAnswer()) {
				type = ChatRecordViewFactory.ACCOST_GAME_ANS_TEXT;
			} else {
				type = ChatRecordViewFactory.ACCOST_GAME_ANS_IMAGE;
			}

			if (!isMyRecord(record)) {
				type += ChatRecordViewFactory.TYPE_OFFSET;
			}
		} else {
			type = Integer.valueOf(recordTypeStr);

			if (!isMyRecord(record)) {
				type += ChatRecordViewFactory.TYPE_OFFSET;
			}
		}
		return type;
	}


	public View getView(final int position, View convertView, ViewGroup parent) {
		// 获取聊天记录
		ChatRecordView view = null;
		int ViewType = getItemViewType(position);
		ChatRecord record = (ChatRecord) getItem(position);

		// 获取聊天记录的View
		if (convertView == null) {
			view = ChatRecordViewFactory.createChatRecordView(mContext, ViewType);
			view.initClickListener(null, null, null, null, null, checkBoxClickListener,null,null,null);
			view.initRecord(mContext, record);
		} else {
			view = (ChatRecordView) convertView;
		}

		view.reset();
		// 设置私聊的聊天记录的内容
		view.showRecord(mContext, record);
		view.showCheckBox(isShowCheckBox);

		return view;
	}
}
