package net.iaround.ui.chatbar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.chatbar.ChatBarBackpackBean.ListBean.GiftComboBean;

import java.util.List;

/**
 * Created by Ray on 2017/7/12.
 */

public class ChatBarGiftPopWindowAdapter extends BaseAdapter {
    private Context mContext;
    private List<GiftComboBean> mList;

    public ChatBarGiftPopWindowAdapter(Context context, List<GiftComboBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatBarGiftPopWindowHolder holder = null;
        if (convertView == null || holder == null) {
            holder = new ChatBarGiftPopWindowHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_group_chat_bar_gift_popwindow_listview, null);
            holder.tvGiftArrayNum = (TextView) convertView.findViewById(R.id.tv_gift_array_num);
            holder.tvGiftArrayDes = (TextView) convertView.findViewById(R.id.tv_gift_array_des);
            holder.viewGiftArrayLine=convertView.findViewById(R.id.view_gift_array_line);
            convertView.setTag(holder);
        } else {
            holder = (ChatBarGiftPopWindowHolder) convertView.getTag();
        }
        if (mList != null) {
            if (!mList.isEmpty()) {
                GiftComboBean giftComboBean = mList.get(position);
                holder.tvGiftArrayNum.setText(giftComboBean.getCombo_value() + "");
                holder.tvGiftArrayDes.setText(giftComboBean.getCombo_name());
                if(position==mList.size()-1){
                    holder.viewGiftArrayLine.setVisibility(View.INVISIBLE);
                }

            }
        }

        return convertView;
    }

    private class ChatBarGiftPopWindowHolder {
        TextView tvGiftArrayNum;
        TextView tvGiftArrayDes;
        View viewGiftArrayLine;
    }
}
