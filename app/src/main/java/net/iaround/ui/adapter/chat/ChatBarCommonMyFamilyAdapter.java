package net.iaround.ui.adapter.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.text.SpannableString;
import android.text.TextUtils;
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
import net.iaround.conf.Common;
import net.iaround.model.chatbar.ChatBarFamily.FamilyNew;
import net.iaround.model.chatbar.ChatBarFamily.JoinGroupBean;
import net.iaround.model.chatbar.ChatBarFamily.PushGroupBean;
import net.iaround.model.chatbar.ChatBarFamilyType;
import net.iaround.model.chatbar.view.ChatBarFmailyHeadView;
import net.iaround.model.chatbar.view.ChatBarFmailyHeadViewEmpty;
import net.iaround.model.chatbar.view.ChatBarFmailyHeadViewJoin;
import net.iaround.model.chatbar.view.ChatBarFmailyHeadViewPush;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.group.activity.GroupChatTopicActivity;

import java.util.List;


public class ChatBarCommonMyFamilyAdapter extends BaseAdapter {
    private final int CHAT_BAR_FAMILY_EMPTY = 7;
    private final int CHAT_BAR_FAMILY_MY_TEXT = 1;
    private final int CHAT_BAR_FAMILY_MY_FAMILY = 2;
    private final int CHAT_BAR_FAMILY_JOIN_TEXT = 3;
    private final int CHAT_BAR_FAMILY_JOIN_GROUP = 4;
    private final int CHAT_BAR_FAMILY_PUSH_TEXT = 5;
    private final int CHAT_BAR_FAMILY_PUST_GROUP = 6;


    private Context mContext;
    private List<ChatBarFamilyType> mAttentionList;

    public ChatBarCommonMyFamilyAdapter(Context context, List<ChatBarFamilyType> attentionBeanList) {
        this.mContext = context;
        this.mAttentionList = attentionBeanList;
    }

    public int getCount() {
        if (mAttentionList == null || mAttentionList.isEmpty())
            return 0;
        else {

            return mAttentionList.size();
        }
    }

    public ChatBarFamilyType getItem(int position) {
        if (mAttentionList != null) {
            return mAttentionList.get(position);
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

    public void updateData(List<ChatBarFamilyType> chatBarAttentionExtends) {
        this.mAttentionList = chatBarAttentionExtends;

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int type = mAttentionList.get(position).getType();
        if (type == CHAT_BAR_FAMILY_EMPTY) {
            return CHAT_BAR_FAMILY_EMPTY;
        } else if (type == CHAT_BAR_FAMILY_MY_TEXT) {
            return CHAT_BAR_FAMILY_MY_TEXT;
        } else if (type == CHAT_BAR_FAMILY_MY_FAMILY) {
            return CHAT_BAR_FAMILY_MY_FAMILY;
        } else if (type == CHAT_BAR_FAMILY_JOIN_TEXT) {
            return CHAT_BAR_FAMILY_JOIN_TEXT;
        } else if (type == CHAT_BAR_FAMILY_JOIN_GROUP) {
            return CHAT_BAR_FAMILY_JOIN_GROUP;
        } else if (type == CHAT_BAR_FAMILY_PUSH_TEXT) {
            return CHAT_BAR_FAMILY_PUSH_TEXT;
        } else if (type == CHAT_BAR_FAMILY_PUST_GROUP) {
            return CHAT_BAR_FAMILY_PUST_GROUP;
        } else {
            return 0;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 9;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        final ChatBarFamilyType item = mAttentionList.get(position);
        switch (itemViewType) {
            case CHAT_BAR_FAMILY_EMPTY:
                convertView = new ChatBarFmailyHeadViewEmpty(mContext);
                break;
            case CHAT_BAR_FAMILY_MY_TEXT:
                if (convertView == null) {
                    convertView = new ChatBarFmailyHeadView(mContext, item);
                } else {
                    ((ChatBarFmailyHeadView) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_FAMILY_MY_FAMILY:
                ChatBarHolder holderFamily = null;
                if (convertView == null ) {
                    holderFamily = new ChatBarHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_bar_popular_new_my, null);
                    holderFamily.leftCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_left_view);
                    holderFamily.leftView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_left_view);
                    holderFamily.ivChatBarIconLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_left);
                    holderFamily.tvHotHourLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_left);
                    holderFamily.tvChatBarNameLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_left);
                    holderFamily.llChatBarHotHourLeft = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_left);
                    holderFamily.tvFamilyPersonNumLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_person_num_left);
                    holderFamily.tvFamilyHotNumLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_hot_num_left);


                    holderFamily.rightCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_right_view);
                    holderFamily.rightView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_right_view);
                    holderFamily.ivChatBarIconRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_right);
                    holderFamily.tvHotHourRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_right);
                    holderFamily.tvChatBarNameRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_right);
                    holderFamily.llChatBarHotHourRight = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_right);
                    holderFamily.tvFamilyPersonNumRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_person_num_right);
                    holderFamily.tvFamilyHotNumRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_hot_num_right);

                    convertView.setTag(holderFamily);
                } else {
                    holderFamily = (ChatBarHolder) convertView.getTag();
                }
                if (holderFamily != null) if (mAttentionList != null) {
                    final ChatBarFamilyType leftItemType = mAttentionList.get(position);
                    if (leftItemType.getType() == 2) {
                        FamilyNew leftItem = (FamilyNew) leftItemType.getObjectLeft();
                        if (leftItem != null) {
                            GlideUtil.loadRoundImage(BaseApplication.appContext, leftItem.getFamilurl(), 4, holderFamily.ivChatBarIconLeft, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            holderFamily.llChatBarHotHourLeft.setVisibility(View.GONE);

                            SpannableString spName;
                            if(!TextUtils.isEmpty(leftItem.getName())){
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getName(),
                                        0, null);
                            }else {
                                spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getGroupid() + "",
                                        0, null);
                            }


                            holderFamily.tvChatBarNameLeft.setText(spName);
                            holderFamily.tvFamilyPersonNumLeft.setText(leftItem.getMembers_count() + "");
                            holderFamily.tvFamilyHotNumLeft.setText(leftItem.getHot() + "");
                            final FamilyNew leftItem1 = leftItem;
                            holderFamily.leftView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                    intent.putExtra("id", leftItem1.getGroupid() + "");
//                                    intent.putExtra("icon", leftItem1.getFamilurl());
//                                    intent.putExtra("name", leftItem1.getName());
//                                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                    intent.putExtra("isChat", true);
//                                    mContext.startActivity(intent);

                                    ToGroupChatTopicActivity(leftItem1.getGroupid() + "",leftItem1.getFamilurl(),leftItem1.getName());
                                }
                            });
                        } else {
                            holderFamily.leftView.setVisibility(View.INVISIBLE);
                            holderFamily.leftCardView.setVisibility(View.INVISIBLE);
                        }


                    } else {
                        holderFamily.leftView.setVisibility(View.INVISIBLE);
                        holderFamily.leftCardView.setVisibility(View.INVISIBLE);
                    }

                    ChatBarFamilyType rightItemType = null;
                    FamilyNew rightItem = null;
                    rightItemType = mAttentionList.get(position);
                    if (rightItemType.getType() == 2) {
                        rightItem = (FamilyNew) rightItemType.getObjectRight();
                        if (rightItem == null) {
                            holderFamily.rightCardView.setVisibility(View.INVISIBLE);
                            holderFamily.rightView.setVisibility(View.INVISIBLE);
                        } else {
                            holderFamily.rightCardView.setVisibility(View.VISIBLE);
                            holderFamily.rightView.setVisibility(View.VISIBLE);
                        }

                        final FamilyNew rightItem1 = rightItem;

                        if (rightItem != null) {
                            GlideUtil.loadRoundImage(BaseApplication.appContext, rightItem.getFamilurl(), 4, holderFamily.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            holderFamily.llChatBarHotHourRight.setVisibility(View.GONE);

                            SpannableString spNameRight;
                            if(!TextUtils.isEmpty(rightItem.getName())){
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getName(),
                                        0, null);
                            }else {
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getGroupid() + "",
                                        0, null);
                            }

                            holderFamily.tvChatBarNameRight.setText(spNameRight);
                            holderFamily.tvFamilyPersonNumRight.setText(rightItem.getMembers_count() + "");
                            holderFamily.tvFamilyHotNumRight.setText(rightItem.getHot() + "");
                        } else {
                            holderFamily.rightView.setVisibility(View.INVISIBLE);
                        }
                        holderFamily.rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                intent.putExtra("id", rightItem1.getGroupid() + "");
//                                intent.putExtra("icon", rightItem1.getFamilurl());
//                                intent.putExtra("name", rightItem1.getName());
//                                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                intent.putExtra("isChat", true);

                                ToGroupChatTopicActivity(rightItem1.getGroupid() + "",rightItem1.getFamilurl(),rightItem1.getName());
                            }
                        });
                    }


                }
                break;
            case CHAT_BAR_FAMILY_JOIN_TEXT:
                if (convertView == null) {
                    convertView = new ChatBarFmailyHeadViewJoin(mContext, item);
                } else {
                    ((ChatBarFmailyHeadViewJoin) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_FAMILY_JOIN_GROUP:
                ChatBarHolder holderGroup = null;
                if (convertView == null ) {
                    holderGroup = new ChatBarHolder();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_bar_popular_new, null);
                    holderGroup.leftCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_left_view);
                    holderGroup.leftView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_left_view);
                    holderGroup.ivChatBarIconLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_left);
                    holderGroup.tvHotHourLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_left);
                    holderGroup.tvChatBarNameLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_left);
                    holderGroup.tvHotFamilyLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_left);
                    holderGroup.llChatBarHotHourLeft = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_left);
                    holderGroup.tvFamilyAuditLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_famifly_audit_left);
                    holderGroup.ivChatBarWaveLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_left);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holderGroup.ivChatBarWaveLeft);

                    holderGroup.rightCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_right_view);
                    holderGroup.rightView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_right_view);
                    holderGroup.ivChatBarIconRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_right);
                    holderGroup.ivChatBarWaveRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_right);
                    holderGroup.tvHotHourRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_right);
                    holderGroup.tvChatBarNameRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_right);
                    holderGroup.tvHotFamilyRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_right);
                    holderGroup.llChatBarHotHourRight = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_right);
                    holderGroup.tvFamilyAuditRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_famifly_audit_right);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holderGroup.ivChatBarWaveRight);

                    convertView.setTag(holderGroup);
                } else {
                    holderGroup = (ChatBarHolder) convertView.getTag();
                }
                if (holderGroup != null) if (mAttentionList != null) {
                    final ChatBarFamilyType leftItemType = mAttentionList.get(position);
                    if (leftItemType.getType() == 4) {
                        JoinGroupBean leftItem = (JoinGroupBean) leftItemType.getObjectLeft();

                        GlideUtil.loadRoundImage(BaseApplication.appContext, leftItem.getFamilurl(), 4, holderGroup.ivChatBarIconLeft, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                        holderGroup.llChatBarHotHourLeft.setVisibility(View.GONE);
                        holderGroup.tvHotFamilyLeft.setText(leftItem.getHot() + "");

                        SpannableString spName;
                        if(!TextUtils.isEmpty(leftItem.getName())){
                            spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getName(),
                                    0, null);
                        }else {
                            spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getGroupid() + "",
                                    0, null);
                        }

                        if (leftItem.getVerify() == 2){
                            holderGroup.tvFamilyAuditLeft.setVisibility(View.VISIBLE);
                        }else{
                            holderGroup.tvFamilyAuditLeft.setVisibility(View.GONE);
                        }

                        holderGroup.tvChatBarNameLeft.setText(spName);
                        final JoinGroupBean leftItem1 = leftItem;
                        holderGroup.leftView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (leftItem1.getVerify() == 2)return;
//                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                intent.putExtra("id", leftItem1.getGroupid() + "");
//                                intent.putExtra("icon", leftItem1.getFamilurl());
//                                intent.putExtra("name", leftItem1.getName());
//                                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                intent.putExtra("isChat", true);
//                                mContext.startActivity(intent);
                                ToGroupChatTopicActivity(leftItem1.getGroupid() + "",leftItem1.getFamilurl(),leftItem1.getName());

                            }
                        });

                    } else {
                        holderGroup.leftCardView.setVisibility(View.INVISIBLE);
                        holderGroup.leftView.setVisibility(View.INVISIBLE);
                    }

                    ChatBarFamilyType rightItemType = null;
                    JoinGroupBean rightItem = null;
                    rightItemType = mAttentionList.get(position);
                    if (rightItemType.getType() == 4) {
                        rightItem = (JoinGroupBean) rightItemType.getObjectRight();
                        if (rightItem == null) {
                            holderGroup.rightCardView.setVisibility(View.INVISIBLE);
                            holderGroup.rightView.setVisibility(View.INVISIBLE);
                        } else {
                            holderGroup.rightCardView.setVisibility(View.VISIBLE);
                            holderGroup.rightView.setVisibility(View.VISIBLE);
                        }

                        final JoinGroupBean rightItem1 = rightItem;

                        if (rightItem != null) {
//                            if(!rightItem.getFamilurl().equals(holderGroup.ivChatBarIconRight.getTag(R.id.iv_chat_bar_icon_right))){
//                                GlideUtil.loadRoundImage(BaseApplication.appContext, rightItem.getFamilurl(), 4, holderGroup.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
//                                holderGroup.ivChatBarIconRight.setTag(R.id.iv_chat_bar_icon_right,rightItem.getFamilurl());
//                            }
                            GlideUtil.loadRoundImage(BaseApplication.appContext, rightItem.getFamilurl(), 4, holderGroup.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                            holderGroup.ivChatBarIconRight.setTag(R.id.iv_chat_bar_icon_right,rightItem.getFamilurl());

                            holderGroup.llChatBarHotHourRight.setVisibility(View.GONE);
                            holderGroup.tvHotFamilyRight.setText(rightItem.getHot() + "");

                            SpannableString spNameRight;
                            if(!TextUtils.isEmpty(rightItem.getName())){
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getName(),
                                        0, null);
                            }else {
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getGroupid() + "",
                                        0, null);
                            }

                            if (rightItem.getVerify() == 2){
                                holderGroup.tvFamilyAuditRight.setVisibility(View.VISIBLE);
                            }else{
                                holderGroup.tvFamilyAuditRight.setVisibility(View.GONE);
                            }

                            holderGroup.tvChatBarNameRight.setText(spNameRight);
                        } else {
                            holderGroup.rightCardView.setVisibility(View.INVISIBLE);
                            holderGroup.rightView.setVisibility(View.INVISIBLE);
                        }
                        holderGroup.rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (rightItem1.getVerify() == 2)return;
//                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                intent.putExtra("id", rightItem1.getGroupid() + "");
//                                intent.putExtra("icon", rightItem1.getFamilurl());
//                                intent.putExtra("name", rightItem1.getName());
//                                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                intent.putExtra("isChat", true);
//                                mContext.startActivity(intent);
                                ToGroupChatTopicActivity(rightItem1.getGroupid() + "",rightItem1.getFamilurl(),rightItem1.getName());
                            }
                        });
                    } else {
                        holderGroup.rightCardView.setVisibility(View.INVISIBLE);
                        holderGroup.rightView.setVisibility(View.INVISIBLE);
                    }


                }
                break;
            case CHAT_BAR_FAMILY_PUSH_TEXT:
                if (convertView == null) {
                    convertView = new ChatBarFmailyHeadViewPush(mContext, item);
                } else {
                    ((ChatBarFmailyHeadViewPush) convertView).refreshView(item);
                }
                break;
            case CHAT_BAR_FAMILY_PUST_GROUP:
                ChatBarHolder holderPush = null;
                if (convertView == null) {
                    holderPush = new ChatBarHolder();

                    convertView = LayoutInflater.from(mContext).inflate(R.layout.fragment_chat_bar_popular_new, null);
                    convertView.setBackgroundResource(R.color.chat_bar_family_push);
                    holderPush.leftCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_left_view);
                    holderPush.leftView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_left_view);
                    holderPush.ivChatBarIconLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_left);
                    holderPush.tvHotHourLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_left);
                    holderPush.tvChatBarNameLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_left);
                    holderPush.tvHotFamilyLeft = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_left);
                    holderPush.llChatBarHotHourLeft = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_left);
                    holderPush.ivChatBarWaveLeft = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_left);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holderPush.ivChatBarWaveLeft);

                    holderPush.rightCardView = (CardView) convertView.findViewById(R.id.cv_chat_bar_right_view);
                    holderPush.rightView = (RelativeLayout) convertView.findViewById(R.id.rl_chat_bar_right_view);
                    holderPush.ivChatBarIconRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_icon_right);
                    holderPush.tvHotHourRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_hour_right);
                    holderPush.tvChatBarNameRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_name_right);
                    holderPush.tvHotFamilyRight = (TextView) convertView.findViewById(R.id.tv_chat_bar_hot_family_right);
                    holderPush.llChatBarHotHourRight = (LinearLayout) convertView.findViewById(R.id.ll_chat_bar_hot_hour_right);
                    holderPush.ivChatBarWaveRight = (ImageView) convertView.findViewById(R.id.iv_chat_bar_wave_right);
                    GlideUtil.loadImageLocalGif(mContext,R.drawable.chat_bar_item_icon_hot,holderPush.ivChatBarWaveRight);

                    convertView.setTag(holderPush);
                } else {
                    holderPush = (ChatBarHolder) convertView.getTag();
                }
                if (holderPush != null) if (mAttentionList != null) {
                    final ChatBarFamilyType leftItemType = mAttentionList.get(position);
                    if (leftItemType.getType() == 6) {
                        PushGroupBean leftItem = (PushGroupBean) leftItemType.getObjectLeft();
                        GlideUtil.loadRoundImage(BaseApplication.appContext, leftItem.getUrl(), 4, holderPush.ivChatBarIconLeft, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                        holderPush.llChatBarHotHourLeft.setVisibility(View.GONE);
                        holderPush.tvHotFamilyLeft.setText(leftItem.getHot() + "");

                        SpannableString spName;
                        if(!TextUtils.isEmpty(leftItem.getName())){
                            spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getName(),
                                    0, null);
                        }else {
                            spName = FaceManager.getInstance(mContext).parseIconForString(mContext, leftItem.getGroupid() + "",
                                    0, null);
                        }
                        holderPush.tvChatBarNameLeft.setText(spName);
                        final PushGroupBean leftItem1 = leftItem;
                        holderPush.leftView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                intent.putExtra("id", leftItem1.getGroupid() + "");
//                                intent.putExtra("icon", leftItem1.getUrl());
//                                intent.putExtra("name", leftItem1.getName());
//                                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                intent.putExtra("isChat", true);
//                                mContext.startActivity(intent);
                                ToGroupChatTopicActivity(leftItem1.getGroupid() + "",leftItem1.getUrl(),leftItem1.getName());

                            }
                        });
                    } else {
                        holderPush.leftCardView.setVisibility(View.INVISIBLE);
                        holderPush.leftView.setVisibility(View.INVISIBLE);
                    }


                    ChatBarFamilyType rightItemType = null;
                    PushGroupBean rightItem = null;
                    rightItemType = mAttentionList.get(position);
                    if (rightItemType.getType() == 6) {
                        rightItem = (PushGroupBean) rightItemType.getObjectRight();
                        if (rightItem == null) {
                            holderPush.rightCardView.setVisibility(View.INVISIBLE);
                            holderPush.rightView.setVisibility(View.INVISIBLE);
                        } else {
                            holderPush.rightCardView.setVisibility(View.VISIBLE);
                            holderPush.rightView.setVisibility(View.VISIBLE);
                        }


                        final PushGroupBean rightItem1 = rightItem;

                        if (rightItem != null) {
//                            if(!rightItem.getUrl().equals(holderPush.ivChatBarIconRight.getTag(R.id.iv_chat_bar_icon_right))){
//                                GlideUtil.loadRoundImage(BaseApplication.appContext, rightItem.getUrl(), 4, holderPush.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
//                                holderPush.ivChatBarIconRight.setTag(R.id.iv_chat_bar_icon_right,rightItem.getUrl());
//                            }
                            GlideUtil.loadRoundImage(BaseApplication.appContext, rightItem.getUrl(), 4, holderPush.ivChatBarIconRight, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                            holderPush.ivChatBarIconRight.setTag(R.id.iv_chat_bar_icon_right,rightItem.getUrl());

                            holderPush.llChatBarHotHourRight.setVisibility(View.GONE);
                            holderPush.tvHotFamilyRight.setText(rightItem.getHot() + "");

                            SpannableString spNameRight;
                            if(!TextUtils.isEmpty(rightItem.getName())){
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getName(),
                                        0, null);
                            }else {
                                spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, rightItem.getGroupid() + "",
                                        0, null);
                            }
                            holderPush.tvChatBarNameRight.setText(spNameRight);
                        } else {
                            holderPush.rightCardView.setVisibility(View.INVISIBLE);
                            holderPush.rightView.setVisibility(View.INVISIBLE);
                        }

                        holderPush.rightView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                                intent.putExtra("id", rightItem1.getGroupid() + "");
//                                intent.putExtra("icon", rightItem1.getUrl());
//                                intent.putExtra("name", rightItem1.getName());
//                                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                                intent.putExtra("isChat", true);
//                                mContext.startActivity(intent);
                                ToGroupChatTopicActivity(rightItem1.getGroupid() + "",rightItem1.getUrl(),rightItem1.getName());
                            }
                        });

                    } else {
                        holderPush.rightCardView.setVisibility(View.INVISIBLE);
                        holderPush.rightView.setVisibility(View.INVISIBLE);
                    }


                }
                break;

        }

        return convertView;
    }

    private void ToGroupChatTopicActivity(String id, String icon, String name){
        Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("icon", icon);
        intent.putExtra("name", name);
        intent.putExtra("userid", Common.getInstance().loginUser.getUid());
        intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
        intent.putExtra("isChat", true);
        GroupChatTopicActivity.ToGroupChatTopicActivity(mContext,intent);
//        mContext.startActivity(intent);
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

        TextView tvFamilyPersonNumLeft;
        TextView tvFamilyPersonNumRight;
        TextView tvFamilyHotNumLeft;
        TextView tvFamilyHotNumRight;
        TextView tvFamilyAuditLeft;//聊吧审核状态
        TextView tvFamilyAuditRight;//聊吧审核状态
    }
}
