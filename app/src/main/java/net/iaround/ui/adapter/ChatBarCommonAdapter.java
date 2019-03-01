package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.chatbar.ChatBarAttenttion.AttentionBean;
import net.iaround.model.chatbar.ChatBarPopularType;
import net.iaround.model.chatbar.view.ChatBarPopularHeadBannerView;
import net.iaround.model.chatbar.view.ChatBarPopularHeadView;
import net.iaround.model.chatbar.view.ChatBarPopularRecommendHeadView;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.OrderToSetActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;

import java.util.List;


public class ChatBarCommonAdapter extends BaseAdapter {

    private static final int CHAT_BAR_POPULAR_RECOMMOND_TITLE = 1;

    private static final int CHAT_BAR_POPULAR_RECOMMOND_CONTENT = 2;

    private static final int CHAT_BAR_POPULAR_RECOMMOND_OTHER_TITLE = 3;

    private static final int CHAT_BAR_POPULAR_RECOMMOND_OTHER_CONTENT = 4;

    private static final int CHAT_BAR_POPULAR_RECOMMOND_BANNER = 5;

    private Context mContext;

    private List<ChatBarPopularType> mAttentionTypeList;

    public ChatBarCommonAdapter() {
        super();
    }

    public ChatBarCommonAdapter(Context context, List<ChatBarPopularType> attentionBeanList) {
        this.mContext = context;
        this.mAttentionTypeList = attentionBeanList;
    }

    public int getCount() {
        //TODO:改版热门聊吧逻辑
        if (mAttentionTypeList == null || mAttentionTypeList.isEmpty())
            return 0;
        else {
            return mAttentionTypeList.size();
        }
    }

    public ChatBarPopularType getItem(int position) {
        //TODO:改版热门聊吧逻辑
        if (mAttentionTypeList != null) {
            return mAttentionTypeList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void updateData(List<ChatBarPopularType> chatBarAttentionExtends) {
        this.mAttentionTypeList = chatBarAttentionExtends;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mAttentionTypeList.get(position).getType();
        if (type == CHAT_BAR_POPULAR_RECOMMOND_TITLE) {
            return CHAT_BAR_POPULAR_RECOMMOND_TITLE;
        }else if(type == CHAT_BAR_POPULAR_RECOMMOND_CONTENT){
            return CHAT_BAR_POPULAR_RECOMMOND_CONTENT;
        }else if(type == CHAT_BAR_POPULAR_RECOMMOND_OTHER_TITLE){
                return CHAT_BAR_POPULAR_RECOMMOND_OTHER_TITLE;
        }else if(type == CHAT_BAR_POPULAR_RECOMMOND_OTHER_CONTENT){
            return CHAT_BAR_POPULAR_RECOMMOND_OTHER_CONTENT;
        }else if(type == CHAT_BAR_POPULAR_RECOMMOND_BANNER){
            return CHAT_BAR_POPULAR_RECOMMOND_BANNER;
        }else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        final ChatBarPopularType item = mAttentionTypeList.get(position);
        switch (itemViewType) {
            case CHAT_BAR_POPULAR_RECOMMOND_BANNER:
                if (convertView == null) {
                    convertView =  new ChatBarPopularHeadBannerView(mContext, item);
                } else {
                    ((ChatBarPopularHeadBannerView) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_POPULAR_RECOMMOND_TITLE:
                if (convertView == null) {
                    convertView = new ChatBarPopularHeadView(mContext, item);
                } else {
                    ((ChatBarPopularHeadView) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_POPULAR_RECOMMOND_CONTENT:
                if (convertView == null) {
                    convertView = new ChatBarPopularRecommendHeadView(mContext, item);
                } else {
                    ((ChatBarPopularRecommendHeadView) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_POPULAR_RECOMMOND_OTHER_TITLE:
                if (convertView == null) {
                    convertView = new ChatBarPopularHeadView(mContext, item);
                } else {
                    ((ChatBarPopularHeadView) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_POPULAR_RECOMMOND_OTHER_CONTENT:
                ChatBarHolder holder = null;
                if (convertView == null) {
                    holder = new ChatBarHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_bar_popular_new, null);
                    holder.leftCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_left_view);
                    holder.leftView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_left_view);
                    holder.ivChatBarIconLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_left);
                    holder.tvHotHourLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_left);
                    holder.tvChatBarNameLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_left);
                    holder.tvHotFamilyLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_left);
                    holder.llChatBarHotHourLeft = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_left);
                    holder.ivChatBarWaveLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_left);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holder.ivChatBarWaveLeft);

                    holder.rightCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_right_view);
                    holder.rightView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_right_view);
                    holder.ivChatBarIconRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_right);
                    holder.tvHotHourRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_right);
                    holder.tvChatBarNameRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_right);
                    holder.tvHotFamilyRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_right);
                    holder.llChatBarHotHourRight = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_right);
                    holder.ivChatBarWaveRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_right);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holder.ivChatBarWaveRight);
                    convertView.setTag(holder);
                } else {
                    holder = (ChatBarHolder) convertView.getTag();
                }
                if (holder != null) {
                    if (mAttentionTypeList != null) {
                        final ChatBarPopularType chatBarPopularTypeLeft = mAttentionTypeList.get(position );
                        if (chatBarPopularTypeLeft.getType() == 4) {
                            AttentionBean leftItem = (AttentionBean) chatBarPopularTypeLeft.getObjectLeft();
                            GlideUtil.loadRoundImage(BaseApplication.appContext, leftItem.getUrl(), 4, holder.ivChatBarIconLeft, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            holder.llChatBarHotHourLeft.setVisibility(View.GONE);
                            holder.tvHotFamilyLeft.setText(leftItem.getHot() + "");

                            SpannableString spName;
                            if(!TextUtils.isEmpty(leftItem.getName())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getName(),
                                        0, null);
                            }else {
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getGroupid() + "",
                                        0, null);
                            }

                            holder.tvChatBarNameLeft.setText(spName);
                            final AttentionBean leftItem1 = leftItem;
                            holder.leftView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                                intent.putExtra("id", leftItem1.getGroupid() + "");
                                intent.putExtra("isChat", true);
                                GroupChatTopicActivity.ToGroupChatTopicActivity(mContext,intent);
//                                mContext.startActivity(intent);
                                }
                            });
                        } else {
                            holder.leftCardView.setVisibility(View.INVISIBLE);
                            holder.leftView.setVisibility(View.INVISIBLE);
                        }

                        ChatBarPopularType chatBarPopularTypeRight = null;
                        AttentionBean rightItem = null;
                        chatBarPopularTypeRight = mAttentionTypeList.get(position);
                        if (chatBarPopularTypeRight.getType() == 4) {
                            rightItem = (AttentionBean) chatBarPopularTypeRight.getObjectRight();
                            if (rightItem == null) {
                                holder.rightCardView.setVisibility(View.INVISIBLE);
                                holder.rightView.setVisibility(View.INVISIBLE);
                            } else {
                                holder.rightCardView.setVisibility(View.VISIBLE);
                                holder.rightView.setVisibility(View.VISIBLE);
                            }


                            final AttentionBean rightItem1 = rightItem;

                            if (rightItem != null) {

                                if(!rightItem.getUrl().equals(holder.ivChatBarIconRight.getTag(R.id.iv_chat_bar_icon_right))){
                                    GlideUtil.loadImage(BaseApplication.appContext, rightItem.getUrl(), holder.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                                    holder.ivChatBarIconRight.setTag(R.id.iv_chat_bar_icon_right,rightItem.getUrl());
                                }

                                holder.llChatBarHotHourRight.setVisibility(View.GONE);
                                holder.tvHotFamilyRight.setText(rightItem.getHot() + "");

                                SpannableString spNameRight;
                                if(!TextUtils.isEmpty(rightItem.getName())){
                                    spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getName(),
                                            0, null);
                                }else {
                                    spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getGroupid() + "",
                                            0, null);
                                }

                                holder.tvChatBarNameRight.setText(spNameRight);
                            } else {
                                holder.rightCardView.setVisibility(View.INVISIBLE);
                                holder.rightView.setVisibility(View.INVISIBLE);
                            }

                            holder.rightView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                                intent.putExtra("id", rightItem1.getGroupid() + "");
                                intent.putExtra("isChat", true);
                                GroupChatTopicActivity.ToGroupChatTopicActivity(mContext,intent);
//                                    mContext.startActivity(intent);
                                }
                            });
                        } else {
                            holder.rightCardView.setVisibility(View.INVISIBLE);
                            holder.rightView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                break;
        }
        return convertView;
    }

    private class ChatBarHolder {
        CardView leftCardView;
        RelativeLayout leftView;
        ImageView ivChatBarIconLeft;//聊吧图片
        ImageView ivChatBarWaveLeft;//动态图标
        TextView tvHotHourLeft;//小时热度
        TextView tvHotFamilyLeft;//家族热度
        TextView tvChatBarNameLeft;//聊吧名字
        LinearLayout llChatBarHotHourLeft;

        CardView rightCardView;
        RelativeLayout rightView;
        ImageView ivChatBarIconRight;//聊吧图片
        ImageView ivChatBarWaveRight;//动态图标
        TextView tvHotHourRight;//小时热度
        TextView tvHotFamilyRight;//家族热度
        TextView tvChatBarNameRight;//聊吧名字
        LinearLayout llChatBarHotHourRight;
    }
}
