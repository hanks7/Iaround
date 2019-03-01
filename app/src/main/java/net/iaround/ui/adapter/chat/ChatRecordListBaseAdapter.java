package net.iaround.ui.adapter.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年5月10日 下午2:47:00
 * @Description: 私聊,圈聊,举报,聊天室的Adapter基类
 */
public abstract class ChatRecordListBaseAdapter extends BaseAdapter {

	protected ArrayList<ChatRecord> mDataList;
	private ArrayList<ChatRecord> mSeletedList = new ArrayList<ChatRecord>();//选中的ListView
	protected Context mContext;
	protected boolean isShowCheckBox = false;//是否展示
	protected int MAX_SELECTED_COUNT = 10;//不同的页面需要的可选择的数量不同
	private int selectedCount; // 已经选择的记录条数

	public ChatRecordListBaseAdapter(Context context, ArrayList<ChatRecord> dataList) {
		if (context == null){
			mContext = BaseApplication.appContext;
		}else {
			mContext = context;
		}
		mDataList = dataList;
	}
	
	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList == null ? null : mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return ChatRecordViewFactory.TYPE_COUNT;
	}
	
	@Override
	public abstract int getItemViewType(int position);
	
	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);

	/** 多选功能 显示CheckBox */
	public void showCheckBox(boolean isShow){
		isShowCheckBox = isShow;
		this.notifyDataSetChanged();
	}
	protected boolean isMyRecord(ChatRecord record) {
        return record.getSendType() != MessageBelongType.RECEIVE;
	}

	public int getSelectedCount() {
		return selectedCount;
	}

	public void setSelectedCount(int selectedCount) {
		this.selectedCount = selectedCount;
	}

	public ArrayList<ChatRecord> getSeletedList() {
		return mSeletedList;
	}

	public void addToSeletedList(ChatRecord record) {
		this.mSeletedList.add(record);
	}
	
	public void removeFromSeletedList(ChatRecord record) {
		this.mSeletedList.remove(record);
	}
	
	public void clearSeletedList() {
		setSelectedCount(0);
		this.mSeletedList.clear();
	}
	
	public void setMaxSelectSize(int maxCount)
	{
		MAX_SELECTED_COUNT = maxCount;
	}
	
	protected View.OnClickListener checkBoxClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ChatRecord record = (ChatRecord) v.getTag();
			if(record == null)return;
			if(record.isSelect()){
				selectedCount--;
				removeFromSeletedList(record);
				record.setSelect(!record.isSelect());
			}else{
				if(getSelectedCount() < MAX_SELECTED_COUNT){
					selectedCount++;
					addToSeletedList(record);
					record.setSelect(!record.isSelect());
				}else{
					record.setSelect(false);
					String noticeStr = String.format(mContext.getResources().getString(R.string.chat_records_seleted_max_notice), MAX_SELECTED_COUNT);
					CommonFunction.toastMsg(mContext, noticeStr);
				}
			}
			notifyDataSetChanged();
		}
	};
}
