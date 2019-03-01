package net.iaround.ui.group.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;


import net.iaround.conf.Config;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.GroupUser;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.adapter.chat.ChatRecordListBaseAdapter;
import net.iaround.ui.chat.ChatRecordLongClickListener;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.GroupModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ChatGroupRecordAdapter
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-15 下午4:57:36
 * @Description: 用于圈聊的消息界面
 */

public class ChatGroupRecordAdapter extends ChatRecordListBaseAdapter {

	// 私聊中消息的各种点击事件
	private View.OnClickListener mUserIconClickListener;// 头像点击的监听器
	private View.OnLongClickListener mUserIconLongClickListener;// 头像长按的监听器
	private View.OnClickListener mResendClickListener;// 发送失败,重发点击监听器
	private View.OnLongClickListener mRecordLongClickListener;// 消息体长按的监听器

	private List<GroupUser> groupUsers;
	private GroupUser currentUser;

	public ChatGroupRecordAdapter(Context context, ArrayList<ChatRecord> dataList,GroupUser currentUser) {
		super(context, dataList);
		this.currentUser = currentUser;
	}

	public void updateCurrentUser(GroupUser currentUser)
	{
		this.currentUser = currentUser;
		notifyDataSetChanged();
	}
	public void initClickListeners(View.OnClickListener userIconClickListener,
			View.OnLongClickListener userIconLongClickListener,
			View.OnClickListener resendClickListener) {
		
		mUserIconClickListener = userIconClickListener;
		mUserIconLongClickListener = userIconLongClickListener;
		mResendClickListener = resendClickListener;
	}

	@Override
	public int getItemViewType(int position) {
		ChatRecord record = ((ChatRecord) getItem(position));

		if ( SuperChat.TIME_LINE_TYPE.equals( record.getType( ) ) )
		{
			return 0;
		}
		else if ( !isMyRecord( record ) && record.getFuid( ) == Config.CUSTOM_SERVICE_UID )
		{
			return ChatRecordViewFactory.FORBID;
		}
		else if ( !isMyRecord( record ) )
		{
			return Integer.valueOf( record.getType( ) ) + ChatRecordViewFactory.TYPE_OFFSET;
		}
		else
		{
			return Integer.valueOf( record.getType( ) );
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// 获取聊天记录
		ChatRecord record = (ChatRecord) getItem(position);
		ChatRecordView view = null;

		// 获取聊天记录的View

		if (convertView == null) {
			view = ChatRecordViewFactory.createChatRecordView(mContext, getItemViewType(position));
			mRecordLongClickListener = new ChatRecordLongClickListener(mContext, view);
			view.initClickListener(mUserIconClickListener, mUserIconLongClickListener, null, mRecordLongClickListener, mResendClickListener, checkBoxClickListener,null,null,null);
			view.initRecord(mContext, record);
		} else {
			view = (ChatRecordView) convertView;
			view.initRecord(mContext, record);
		}

		view.reset();
		view.showRecord(mContext, record);
		view.showCheckBox(isShowCheckBox);

		return view;
	}
}
