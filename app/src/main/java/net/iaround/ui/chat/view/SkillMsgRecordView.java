package net.iaround.ui.chat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.entity.Item;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.utils.AssetUtils;
import net.iaround.utils.SkillHandleUtils;
import net.iaround.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作者：zx on 2017/8/24 20:01
 */
public class SkillMsgRecordView extends RelativeLayout{

    @BindView(R.id.head_icon) HeadPhotoView headIcon;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.friend_ustitle) UserTitleView friend_ustitle;
    @BindView(R.id.ll_wealth_rank) LinearLayout llWealthRank;
    @BindView(R.id.content)
    RichTextView content;
    @BindView(R.id.iv_skill_gif) ImageView ivSkillGif;
    @BindView(R.id.iv_skill_gif_first) ImageView iv_skill_gif_first;
    @BindView(R.id.iv_skill_bg) ImageView iv_skill_bg;
    @BindView(R.id.role_ll) LinearLayout role_ll;
    private long uid;
    private long currentUid = 0;
    protected final int defIcon = R.drawable.default_face_map;
    private float defaultTextSize = ScreenUtils.dp2px(14);

    public SkillMsgRecordView(Context context) {
        super(context);
        initView(context);
        uid = Common.getInstance().loginUser.getUid();
        defaultTextSize = getResources().getDimension(R.dimen.fourteen_dp);
    }

    protected void initView(Context context) {
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        View view = LayoutInflater.from(context).inflate(R.layout.item_skill_msg, null);
        ButterKnife.bind(this, view);
        addView(view);
    }

    private boolean isScrolled = false;

    public void setScrolled(boolean scrolled) {
        isScrolled = scrolled;
    }

    public void showRecord(SkillAttackResult skillContent) {
        if (null == skillContent){
            return;
        }

        String format = AssetUtils.getDesc(0, skillContent);
        content.setText(format);

        if (!TextUtils.isEmpty(skillContent.skillIcon)){
            GlideUtil.loadImage(BaseApplication.appContext, skillContent.skillIcon, iv_skill_bg);
        }
        if (!isScrolled){
            SkillHandleUtils.playFrame(BaseApplication.appContext, skillContent.gif, ivSkillGif);
            SkillHandleUtils.stopFrameAnim(iv_skill_gif_first);
        }else {
            SkillHandleUtils.stopFrameAnim(ivSkillGif);
            SkillHandleUtils.setFirstFrameImage(BaseApplication.appContext, skillContent.gif, iv_skill_gif_first);
        }
        content.parseIconWithColor(defaultTextSize);
        currentUid = skillContent.user.UserID;
        setUserIcon(skillContent.user, headIcon);
        setUserInfo(skillContent.user);
    }
    /**
     * 设置用户的头像
     * @param user
     * @param iconView
     */
    protected void setUserIcon(SkillAttackResult.UserBean user, HeadPhotoView iconView) {
        // 设置: 用户头像
        int vipLevel = user.VipLevel;
        int svip = user.VIP;
        String iconPath = user.ICON;
        iconView.executeChat(defIcon, iconPath, svip, vipLevel,-1);
        iconView.setTag(user);
    }

    /**
     * 设置用户信息
     * @param userInfo
     */
    protected void setUserInfo(SkillAttackResult.UserBean userInfo) {
        Item rankItem = userInfo.rankItem;
        role_ll.setVisibility(GONE);
        llWealthRank.setVisibility(GONE);
        SpannableString name = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, userInfo.NickName + "", 0, null);
        tvName.setText(name);
        if(rankItem!=null) {
            friend_ustitle.setTitleText(rankItem);
            friend_ustitle.setVisibility(VISIBLE);
        }else{
            friend_ustitle.setVisibility(GONE);
        }
    }

    @OnClick(R.id.head_icon)
    public void onHeadClick(){
        Activity activity1 = CloseAllActivity.getInstance().getTopActivity();
        if (uid == currentUid){
            Intent intent1 = new Intent(activity1, UserInfoActivity.class);
            activity1.startActivity(intent1);
        }else {
            Intent intent1 = new Intent(activity1, OtherInfoActivity.class);
            intent1.putExtra("uid", currentUid);
            activity1.startActivity(intent1);
        }
    }

}
