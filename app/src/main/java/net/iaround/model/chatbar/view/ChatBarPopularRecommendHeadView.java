package net.iaround.model.chatbar.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.chatbar.ChatBarAttenttion.AttentionBean;
import net.iaround.model.chatbar.ChatBarPopularType;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.group.activity.GroupChatTopicActivity;

import java.util.ArrayList;
import java.util.List;

import static net.iaround.R.id.iv_chat_bar_icon_five;
import static net.iaround.R.id.iv_chat_bar_icon_four;
import static net.iaround.R.id.iv_chat_bar_icon_one;
import static net.iaround.R.id.iv_chat_bar_icon_six;
import static net.iaround.R.id.iv_chat_bar_icon_three;
import static net.iaround.R.id.iv_chat_bar_icon_two;

/**
 * Created by Ray on 2017/7/28.
 */

public class ChatBarPopularRecommendHeadView extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private ChatBarPopularType mChatBarFamilyType;
    private ImageView ivPopularOne;
    private TextView tvPopularBarNameOne;
    private TextView tvPopularHoyNumOne;
    private ImageView ivPopularTwo;
    private TextView tvPopularBarNameTwo;
    private TextView tvPopularHoyNumTwo;
    private ImageView ivPopularThree;
    private TextView tvPopularBarNameThree;
    private TextView tvPopularHoyNumThree;
    private ImageView ivPopularFour;
    private TextView tvPopularBarNameFour;
    private TextView tvPopularHoyNumFour;
    private ImageView ivPopularFive;
    private TextView tvPopularBarNameFive;
    private TextView tvPopularHoyNumFive;
    private ImageView ivPopularSix;
    private TextView tvPopularBarNameSix;
    private TextView tvPopularHoyNumSix;
    private ImageView ivPopularHotOne;
    private ImageView ivPopularHotTwo;
    private ImageView ivPopularHotThree;
    private ImageView ivPopularHotFour;
    private ImageView ivPopularHotFive;
    private ImageView ivPopularHotSix;


    private List<AttentionBean> mAttentionList;
    private AttentionBean attentionBeanOne;
    private AttentionBean attentionBeanTwo;
    private AttentionBean attentionBeanThree;
    private AttentionBean attentionBeanFour;
    private AttentionBean attentionBeanFive;
    private AttentionBean attentionBeanSix;


    public ChatBarPopularRecommendHeadView(Context context, ChatBarPopularType chatBarFamilyType) {
        super(context);
        this.mContext = context;
        this.mChatBarFamilyType = chatBarFamilyType;
        initView();
        refreshView(chatBarFamilyType);
    }


    private void initView() {
        View itemView = LayoutInflater.from(mContext).inflate(
                R.layout.chat_bar_fragment_popular_recommend_header, this);
        ivPopularOne = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_one);
        tvPopularBarNameOne = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_one);
        tvPopularHoyNumOne = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_one);
        ivPopularHotOne = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_one);

        ivPopularTwo = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_two);
        tvPopularBarNameTwo = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_two);
        tvPopularHoyNumTwo = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_two);
        ivPopularHotTwo = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_two);

        ivPopularThree = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_three);
        tvPopularBarNameThree = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_three);
        tvPopularHoyNumThree = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_three);
        ivPopularHotThree = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_three);

        ivPopularFour = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_four);
        tvPopularBarNameFour = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_four);
        tvPopularHoyNumFour = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_four);
        ivPopularHotFour = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_four);

        ivPopularFive = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_five);
        tvPopularBarNameFive = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_five);
        tvPopularHoyNumFive = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_five);
        ivPopularHotFive = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_five);

        ivPopularSix = (ImageView) itemView.findViewById(R.id.iv_chat_bar_icon_six);
        tvPopularBarNameSix = (TextView) itemView.findViewById(R.id.tv_chat_bar_name_six);
        tvPopularHoyNumSix = (TextView) itemView.findViewById(R.id.tv_chat_bar_hot_family_six);
        ivPopularHotSix = (ImageView) itemView.findViewById(R.id.iv_chat_bar_hot_family_six);

        ivPopularOne.setOnClickListener(this);
        ivPopularTwo.setOnClickListener(this);
        ivPopularThree.setOnClickListener(this);
        ivPopularFour.setOnClickListener(this);
        ivPopularFive.setOnClickListener(this);
        ivPopularSix.setOnClickListener(this);

    }

    public void refreshView(ChatBarPopularType chatBarFamilyType) {
        mAttentionList = new ArrayList<>();
        if (chatBarFamilyType != null) {
            mAttentionList = (List<AttentionBean>) chatBarFamilyType.getObjectLeft();
            if (mAttentionList != null && !mAttentionList.isEmpty()) {
                for (int i = 0; i <= mAttentionList.size() - 1; i++) {
                    switch (i) {
                        case 0:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(0).getUrl(), 4, ivPopularOne, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotOne);

                            SpannableString spNameOne;
                            if (!TextUtils.isEmpty(mAttentionList.get(0).getName())) {
                                spNameOne = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(0).getName(),
                                        0, null);
                            } else {
                                spNameOne = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(0).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameOne.setText("" + spNameOne);
                            tvPopularHoyNumOne.setText("" + mAttentionList.get(0).getHot());
                            attentionBeanOne = mAttentionList.get(0);
//                            Glide.with(mContext).load(mAttentionList.get(0).getUrl()).asBitmap().into(new SimpleTarget<Bitmap>() {
//                                @Override
//                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                                    if(resource!=null){
//                                       Bitmap bitmap = RoundPicture.getRoundedCornerBitmap(resource,90,resource.getWidth(),resource.getHeight());
//                                        ivPopularOne.setImageBitmap(bitmap);
//                                    }
//                                }
//                            });

                            break;
                        case 1:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(1).getUrl(), 4, ivPopularTwo, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotTwo);

                            SpannableString spNameTwo;
                            if (!TextUtils.isEmpty(mAttentionList.get(1).getName())) {
                                spNameTwo = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(1).getName(),
                                        0, null);
                            } else {
                                spNameTwo = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(1).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameTwo.setText("" + spNameTwo);
                            tvPopularHoyNumTwo.setText("" + mAttentionList.get(1).getHot());
                            attentionBeanTwo = mAttentionList.get(1);
                            break;
                        case 2:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(2).getUrl(), 4, ivPopularThree, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotThree);

                            SpannableString spNameThree;
                            if (!TextUtils.isEmpty(mAttentionList.get(2).getName())) {
                                spNameThree = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(2).getName(),
                                        0, null);
                            } else {
                                spNameThree = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(2).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameThree.setText("" + spNameThree);
                            tvPopularHoyNumThree.setText("" + mAttentionList.get(2).getHot());
                            attentionBeanThree = mAttentionList.get(2);
                            break;
                        case 3:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(3).getUrl(), 4, ivPopularFour, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotFour);

                            SpannableString spNameFour;
                            if (!TextUtils.isEmpty(mAttentionList.get(3).getName())) {
                                spNameFour = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(3).getName(),
                                        0, null);
                            } else {
                                spNameFour = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(3).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameFour.setText("" + spNameFour);
                            tvPopularHoyNumFour.setText("" + mAttentionList.get(3).getHot());
                            attentionBeanFour = mAttentionList.get(3);
                            break;
                        case 4:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(4).getUrl(), 4, ivPopularFive, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotFive);

                            SpannableString spNameFive;
                            if (!TextUtils.isEmpty(mAttentionList.get(4).getName())) {
                                spNameFive = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(4).getName(),
                                        0, null);
                            } else {
                                spNameFive = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(4).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameFive.setText("" + spNameFive);
                            tvPopularHoyNumFive.setText("" + mAttentionList.get(4).getHot());
                            attentionBeanFive = mAttentionList.get(4);
                            break;
                        case 5:
                            GlideUtil.loadRoundImage(BaseApplication.appContext, mAttentionList.get(5).getUrl(), 4, ivPopularSix, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);

                            GlideUtil.loadImageLocalGif(BaseApplication.appContext, R.drawable.chat_bar_item_icon_hot, ivPopularHotSix);

                            SpannableString spNameSix;
                            if (!TextUtils.isEmpty(mAttentionList.get(5).getName())) {
                                spNameSix = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(5).getName(),
                                        0, null);
                            } else {
                                spNameSix = FaceManager.getInstance(mContext).parseIconForString(mContext, mAttentionList.get(5).getGroupid() + "",
                                        0, null);
                            }
                            tvPopularBarNameSix.setText("" + spNameSix);
                            tvPopularHoyNumSix.setText("" + mAttentionList.get(5).getHot());
                            attentionBeanSix = mAttentionList.get(5);
                            break;
                        default:
                            break;
                    }


                }


            }
        }

    }

    public ChatBarPopularRecommendHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatBarPopularRecommendHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case iv_chat_bar_icon_one:
                if (attentionBeanOne != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanOne.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanOne.getUrl());
//                    intent.putExtra("name", attentionBeanOne.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanOne.getGroupid() + "", attentionBeanOne.getUrl(), attentionBeanOne.getName());

                }
                break;
            case iv_chat_bar_icon_two:
                if (attentionBeanTwo != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanTwo.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanTwo.getUrl());
//                    intent.putExtra("name", attentionBeanTwo.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanTwo.getGroupid() + "", attentionBeanTwo.getUrl(), attentionBeanTwo.getName());

                }

                break;
            case iv_chat_bar_icon_three:
                if (attentionBeanThree != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanThree.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanThree.getUrl());
//                    intent.putExtra("name", attentionBeanThree.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanThree.getGroupid() + "", attentionBeanThree.getUrl(), attentionBeanThree.getName());

                }

                break;
            case iv_chat_bar_icon_four:
                if (attentionBeanFour != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanFour.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanFour.getUrl());
//                    intent.putExtra("name", attentionBeanFour.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanFour.getGroupid() + "", attentionBeanFour.getUrl(), attentionBeanFour.getName());

                }

                break;
            case iv_chat_bar_icon_five:
                if (attentionBeanFive != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanFive.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanFive.getUrl());
//                    intent.putExtra("name", attentionBeanFive.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanFive.getGroupid() + "", attentionBeanFive.getUrl(), attentionBeanFive.getName());

                }

                break;
            case iv_chat_bar_icon_six:
                if (attentionBeanSix != null) {
//                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                    intent.putExtra("id", attentionBeanSix.getGroupid() + "");
//                    intent.putExtra("icon", attentionBeanSix.getUrl());
//                    intent.putExtra("name", attentionBeanSix.getName());
//                    intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                    intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                    intent.putExtra("isChat", true);
//                    mContext.startActivity(intent);
                    ToGroupChatTopicActivity(attentionBeanSix.getGroupid() + "", attentionBeanSix.getUrl(), attentionBeanSix.getName());
                }

                break;
        }
    }

    private void ToGroupChatTopicActivity(String id, String icon, String name) {
        Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("icon", icon);
        intent.putExtra("name", name);
        intent.putExtra("userid", Common.getInstance().loginUser.getUid());
        intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
        intent.putExtra("isChat", true);
        GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
    }
}
