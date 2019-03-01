package net.iaround.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.chatbar.ChatBarAttenttion.AttentionBean;
import net.iaround.model.chatbar.ChatBarEntity;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.datamodel.ResourceBanner;

import java.util.List;

/**
 * Created by gh on 2017/6/13.
 */

public class ChatBarAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ChatBarEntity> mDatas;

    private View footView;

    public ChatBarAdapter(Context mContext, List<ChatBarEntity> mDatas) {
        this.mContext = mContext;
        this.mDatas = mDatas;
    }

    public void updateData(List<ChatBarEntity> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }

    public void setFooterView(View foot) {
        this.footView = foot;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        RecyclerView.ViewHolder holder = null;
        if (ChatBarEntity.RESOURCE_BANNER == viewType) {
            View v = mInflater.inflate(R.layout.chat_bar_fragment_banner_view, parent, false);
            holder = new ResouceViewHolder(v);
        } else if (ChatBarEntity.BANNER_NOTICE == viewType) {
            View v = mInflater.inflate(R.layout.chat_bar_fragment_banner_view, parent, false);
            holder = new BannerViewHolder(v);
        } else {
            View v = mInflater.inflate(R.layout.fragment_chat_bar_family_new, parent, false);
            holder = new BarListHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResouceViewHolder) {
            ResouceViewHolder resouceViewHolder = (ResouceViewHolder) holder;
            ResourceBanner banner = (ResourceBanner)mDatas.get(position).itemBean;
            GlideUtil.loadImage(BaseApplication.appContext,banner.getImageUrl(),resouceViewHolder.resouceImg);
        } else if (holder instanceof BannerViewHolder) {
            ResouceViewHolder resouceViewHolder = (ResouceViewHolder) holder;
            ResourceBanner banner = (ResourceBanner)mDatas.get(position).itemBean;
            GlideUtil.loadImage(BaseApplication.appContext,banner.getImageUrl(),resouceViewHolder.resouceImg);
        } else {
            BarListHolder barListHolder = (BarListHolder) holder;
            AttentionBean bean = (AttentionBean)mDatas.get(position).itemBean;
            GlideUtil.loadRoundImage(BaseApplication.appContext,bean.getUrl(),8,barListHolder.ivChatBarIconLeft);
            barListHolder.tvChatBarNameLeft.setText(bean.getName());
            barListHolder.tvHotHourLeft.setText(""+bean.getHot());
            barListHolder.tvHotFamilyLeft.setText(""+bean.getFamily());
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChatBarEntity entity = mDatas.get(position);
        return entity.itemType;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class BarListHolder extends RecyclerView.ViewHolder {
        ImageView ivChatBarIconLeft;//聊吧图片
        TextView tvHotHourLeft;//小时热度
        TextView tvHotFamilyLeft;//家族热度
        TextView tvChatBarNameLeft;//聊吧名字

        public BarListHolder(View itemView) {
            super(itemView);
            ivChatBarIconLeft = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_left);
            tvHotHourLeft = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_hour_left);
            tvHotFamilyLeft = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_left);
            tvChatBarNameLeft = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_left);
        }
    }

    class ResouceViewHolder extends RecyclerView.ViewHolder {
        ImageView resouceImg;
        Button tv1;

        public ResouceViewHolder(View itemView) {
            super(itemView);
            resouceImg = (ImageView) itemView.findViewById(R.id.chat_ad_im);
        }
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView resouceImg;
        Button tv1;

        public BannerViewHolder(View itemView) {
            super(itemView);
            resouceImg = (ImageView) itemView.findViewById(R.id.chat_ad_im);
        }
    }
}
