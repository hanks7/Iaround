package net.iaround.ui.seach.news;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.group.bean.GroupHistoryBean;

import java.util.List;

/**
 * 聊吧浏览记录适配器
 * Created by gh on 2017/11/6.
 */

public class SearchChatBarHistoryAdapter extends RecyclerView.Adapter<SearchChatBarHistoryAdapter.MyViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<GroupHistoryBean> mDataList;
    private int mItemLayout;

    private OnItemClickListener mOnItemClickListener = null;

    public SearchChatBarHistoryAdapter(Context context, int itemLayout, List<GroupHistoryBean> datalist) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItemLayout = itemLayout;
        mDataList = datalist;
    }

    @Override
    public SearchChatBarHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(mItemLayout, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(mDataList.get((int)v.getTag()));
                }
            }
        });
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SearchChatBarHistoryAdapter.MyViewHolder holder, int position) {
        GroupHistoryBean bean = mDataList.get(position);

        GlideUtil.loadCircleImage(mContext,bean.groupIcon,holder.mIcon);
        SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, bean.groupName,
                0, null);
        holder.mChatName.setText(spName);


        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        if (mDataList.size() > 7){
            return 8;
        }
        return mDataList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIcon;
        private TextView mChatName;

        public MyViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mChatName = (TextView) itemView.findViewById(R.id.tv_search_chabar_history_chatbar_name);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(GroupHistoryBean bean);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}