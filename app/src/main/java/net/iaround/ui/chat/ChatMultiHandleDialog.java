package net.iaround.ui.chat;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.chat.view.ChatRecordView;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年5月12日 下午7:45:33
 * @Description: 聊天中消息长按弹窗Dialog
 */
public class ChatMultiHandleDialog extends DialogFragment {

	private View baseView;
	private ChatRecordView mRecordView;
	private ArrayList<MultiHandleItem> mMultiHandleList;
	
	private MultiHandleListAdapter mAdapter;
	private ListView lvMultiHandle;
	private MultiHandleClickPerform mClickPerform;

	public ChatMultiHandleDialog(){

	}

	@SuppressLint({"NewApi", "ValidFragment"})
	public ChatMultiHandleDialog(ChatRecordView recordView, ArrayList<MultiHandleItem> multiHandleList, MultiHandleClickPerform clickPerform) {
		mRecordView = recordView;
		mMultiHandleList = multiHandleList;
		mClickPerform = clickPerform;
		mAdapter = new MultiHandleListAdapter();
	}
	
	public void refreshData(ChatRecordView recordView, ArrayList<MultiHandleItem> data){
		mRecordView = recordView;
		mMultiHandleList.clear();
		mMultiHandleList.addAll(data);
		
		mAdapter.notifyDataSetChanged();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
		getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);
		baseView = inflater.inflate(R.layout.l_chat_multi_handle_dialog, container);
		initView(baseView);
		
		return baseView;
	}
	
	private void initView(View v)
	{
		lvMultiHandle = (ListView) v.findViewById(R.id.lvMultiHandle);
		lvMultiHandle.setAdapter(mAdapter);
		
		lvMultiHandle.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
				mClickPerform.onItemClick(mRecordView, position, id);
				dismiss();
			}
		});
	}
	

	public interface MultiHandleClickPerform{
		
		/**
		 * @ChatRecordLongClickListener 
		 * @param recordView 当前点击的消息
		 * @param position 当前点击的位置
		 * @param flag 对应每个操作的flag
		 */
        void onItemClick(ChatRecordView recordView, int position, long flag);
	}
	
	
	private class MultiHandleListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mMultiHandleList == null ? 0 : mMultiHandleList.size();
		}

		@Override
		public Object getItem(int position) {
			return mMultiHandleList == null ? 0 : mMultiHandleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return mMultiHandleList == null ? 0 : mMultiHandleList.get(position).getFlag();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			ViewHolder holder;
			MultiHandleItem item = mMultiHandleList.get(position);
			if(convertView == null){
				holder = new ViewHolder();
				convertView = View.inflate(baseView.getContext(), R.layout.l_multi_handle_item_view, null);
				holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			holder.tvName.setText(item.getName());
			return convertView;
		}
		
		class ViewHolder{
			public TextView tvName;
		}
	}
	
	public static class MultiHandleItem{
		
		private String mName;
		private long mFlag;
		public MultiHandleItem(String itemName, long itemFlag) {
			mName = itemName;
			mFlag = itemFlag;
		}
		
		public String getName() {
			return mName;
		}
		public void setName(String mName) {
			this.mName = mName;
		}
		public long getFlag() {
			return mFlag;
		}
		public void setFlag(long mFlag) {
			this.mFlag = mFlag;
		}
	}
}
