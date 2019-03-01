package net.iaround.ui.store;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.chatbar.ChatBarBackpackBean;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.NetImageView;

import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/7/29 12:07
 * Email：15369302822@163.com
 */
public class StoreMineGiftAdapter extends RecyclerView.Adapter<StoreMineGiftAdapter.MyGiftViewHolder> {

    private List<ChatBarBackpackBean.ListBean> giftBagBeans;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private int haveGiftNum;
    private GiftNumCallback giftNumCallback;
    private int allGiftNum;

    public StoreMineGiftAdapter(Context context,List<ChatBarBackpackBean.ListBean> giftBagBeans,GiftNumCallback giftNumCallback)
    {
        this.mContext = context;
        this.giftBagBeans = giftBagBeans;
        this.giftNumCallback = giftNumCallback;
        layoutInflater = LayoutInflater.from(mContext);
    }

    public void refreshGiftData(List<ChatBarBackpackBean.ListBean> giftBagBeans)
    {
        this.giftBagBeans = giftBagBeans;
        setGiftNum(giftBagBeans);
        notifyDataSetChanged();
    }

    public class MyGiftViewHolder extends RecyclerView.ViewHolder
    {
//        public ImageView ivGiftIcon;
//        public TextView tvGiftName,tvGiftPrice;
//        public RelativeLayout rlGiftLayout;
//        public View selectGiftBg,selectGiftColor;
//        public TextView tvHaveNum;//礼物剩余数量

        public NetImageView icon;
        public TextView name;
        public TextView price;
        public TextView experience;
        public TextView charm;
        public RelativeLayout giftFlagLy;
        public TextView tvGiftNum;
        public MyGiftViewHolder(View convertView) {
            super(convertView);
//            ivGiftIcon = (ImageView) convertView.findViewById(R.id.iv_gift_icon);
//            tvGiftName = (TextView) convertView.findViewById(R.id.tv_gift_name);
//            tvGiftPrice = (TextView) convertView.findViewById(R.id.tv_gift_price);
//            rlGiftLayout = (RelativeLayout) convertView.findViewById(R.id.rl_gift_layout);
//            selectGiftBg = convertView.findViewById(R.id.chatbar_select_gift_bg);
//            selectGiftColor = convertView.findViewById(R.id.chatbar_select_gift_color);
//            tvHaveNum = (TextView) convertView.findViewById(R.id.tv_have_num);

            icon = (NetImageView) convertView.findViewById(R.id.gift_icon);
            name = (TextView) convertView.findViewById(R.id.gift_name);
            price = (TextView) convertView.findViewById(R.id.gift_price);
            charm = (TextView) convertView.findViewById(R.id.gift_charm);
            experience = (TextView) convertView.findViewById(R.id.gift_experience);
            giftFlagLy = (RelativeLayout) convertView
                    .findViewById(R.id.gift_flag);
            tvGiftNum = (TextView) convertView.findViewById(R.id.tv_have_num);
        }
    }

    @Override
    public MyGiftViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = layoutInflater.inflate(R.layout.gift_detail_layout,null);
        View view = layoutInflater.inflate(R.layout.store_gift_classify_item,null);
        return new MyGiftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyGiftViewHolder holder, int position) {
        if (giftBagBeans != null && giftBagBeans.size() > 0)
        {
            ChatBarBackpackBean.ListBean gift = giftBagBeans.get(position);
            holder.icon.executeFadeInRound(PicIndex.DEFAULT_GIFT, gift.getGift_icon());
            gift.setGiftNameArray(gift.getGift_name());
            String giftName = gift.getGiftNameArray(mContext);
            holder.name.setText(giftName);

            if (gift.getGift_num() > 0)
            {
                holder.tvGiftNum.setVisibility(View.VISIBLE);
                if (gift.getGift_num() < 10)
                {
                    holder.tvGiftNum.setBackgroundResource(R.drawable.store_gift_num1);
                }else
                {
                    holder.tvGiftNum.setBackgroundResource(R.drawable.store_gift_num);
                }
                holder.tvGiftNum.setText(""+gift.getGift_num());
            }else
            {
                holder.tvGiftNum.setVisibility(View.GONE);
            }
            if (gift.getGift_gold() > 0) {
                holder.price.setText(mContext.getResources().getString(R.string.gold_balance)+gift.getGift_gold());
            }else if (gift.getGift_diamond() > 0)
            {
                holder.price.setText(mContext.getResources().getString(R.string.diamond_balance)+gift.getGift_diamond());
            }
            holder.charm.setText(mContext.getString(R.string.charisma_title_new) + gift.getGift_charm_num());
            holder.experience.setText(String.format(mContext.getString(R.string.chat_gift_exp), gift.getGift_exp()));
            holder.giftFlagLy.setVisibility(View.GONE);
//            GlideUtil.loadImage(mContext, giftBagBeans.get(position).getGift_icon(), holder.ivGiftIcon);
//            giftBagBeans.get(position).setGiftNameArray(giftBagBeans.get(position).getGift_name());
//            String giftName = giftBagBeans.get(position).getGiftNameArray(mContext);
//            holder.tvGiftName.setText(giftName);
//
//            if (giftBagBeans.get(position).getGift_gold() == 0)
//            {
//                holder.tvGiftPrice.setText(giftBagBeans.get(position).getGift_diamond() + " " + mContext.getResources().getString(R.string.user_wallet_diamond));
//            } else
//            {
//                holder.tvGiftPrice.setText(giftBagBeans.get(position).getGift_gold() + " " + mContext.getResources().getString(R.string.pay_gold));
//            }
//            holder.tvHaveNum.setVisibility(View.VISIBLE);
//            if (giftBagBeans.get(position).getGift_num() <= 0) {
//                holder.tvHaveNum.setVisibility(View.GONE);
//            } else {
//                haveGiftNum = giftBagBeans.get(position).getGift_num();
//                if (giftBagBeans.get(position).getGift_num() < 10) {
//                    holder.tvHaveNum.setBackgroundResource(R.drawable.chatbar_gift_have_num_bg);
//                } else {
//                    holder.tvHaveNum.setBackgroundResource(R.drawable.chatbar_gift_hava_num_bg1);
//                }
//                holder.tvHaveNum.setText(giftBagBeans.get(position).getGift_num() + "");
//            }
        }
    }

    @Override
    public int getItemCount() {
        return giftBagBeans != null ? giftBagBeans.size() : 0;
    }

    interface GiftNumCallback
    {
        void getGiftNum(int allGiftNum);
    }

    private void setGiftNum(List<ChatBarBackpackBean.ListBean> giftBagBeans)
    {
        if (giftBagBeans != null && giftBagBeans.size() > 0)
        {
            for (int i = 0; i < giftBagBeans.size(); i++) {
                allGiftNum += giftBagBeans.get(i).getGift_num();
            }
            giftNumCallback.getGiftNum(allGiftNum);
        }
    }
}
