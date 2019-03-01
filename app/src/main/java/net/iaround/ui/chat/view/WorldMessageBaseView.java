package net.iaround.ui.chat.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.chat.BaseWorldMessageBean;
import net.iaround.model.chat.WorldMessageGiftContent;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.chat.WorldMessageRecruitContent;
import net.iaround.model.chat.WorldMessageTextContent;
import net.iaround.model.entity.Item;
import net.iaround.model.skill.EventLongPressUser;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.utils.AssetUtils;
import net.iaround.utils.SkillHandleUtils;
import net.iaround.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;


/**
 * 作者：zx on 2017/8/24 20:01
 */
public abstract class WorldMessageBaseView extends RelativeLayout{

    @BindView(R.id.head_icon) HeadPhotoView headIcon;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.role_ll) LinearLayout role_ll;
    @BindView(R.id.ll_wealth_rank) LinearLayout llWealthRank;
    @BindView(R.id.friend_ustitle) UserTitleView friend_ustitle;
    @BindView(R.id.content) RichTextView content;
    @BindView(R.id.iv_skill_gif) ImageView ivSkillGif;
    @BindView(R.id.iv_skill_gif_first) ImageView iv_skill_gif_first;
    @BindView(R.id.iv_skill_bg) ImageView iv_skill_bg;
    @BindView(R.id.btn_go_in) Button btnGoIn;
    protected final int defIcon = R.drawable.default_face_map;
    protected boolean isRecruit = false;
    private String groupId;
    private long uid;
    private long currentUid = 0;
    private String userName = "";
    private float defaultTextSize = ScreenUtils.dp2px(14);

    private boolean isScrolled = false;

    public void setScrolled(boolean scrolled) {
        isScrolled = scrolled;
    }

    public WorldMessageBaseView(Context context) {
        this(context, null);
    }

    public WorldMessageBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WorldMessageBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        uid = Common.getInstance().loginUser.getUid();
        defaultTextSize = getResources().getDimension(R.dimen.fourteen_dp);
    }

    private void init(Context context) {
        ViewGroup.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(lp);
        View view = initView(context);
        addView(view);
    }

    protected abstract View initView(Context context);

    private void setVisiable(int id){
        int gif_Visiable = R.id.iv_skill_gif == id ? VISIBLE : GONE;
        int btn_Visiable = R.id.btn_go_in == id ? VISIBLE : GONE;
        iv_skill_bg.setVisibility(gif_Visiable);
        ivSkillGif.setVisibility(gif_Visiable);
        iv_skill_gif_first.setVisibility(gif_Visiable);
        btnGoIn.setVisibility(btn_Visiable);
    }

    public void showRecord(WorldMessageRecord record) {
        setVisiable(-1);
        if (null == record){
            return;
        }
        isRecruit = false;
        if (30 == record.messageType){//普通文本
            WorldMessageTextContent textContent = record.textContent;
            if (!TextUtils.isEmpty(textContent.content)){
                content.setText(textContent.content);
                content.parseIcon(defaultTextSize);
            }

        }else if (31 == record.messageType){//招募消息
            isRecruit = true;
            WorldMessageRecruitContent recruitContent = record.recruitContent;
            setVisiable(R.id.btn_go_in);
            if (!TextUtils.isEmpty(recruitContent.groupid)){
                groupId = recruitContent.groupid;
            }
            if (!TextUtils.isEmpty(recruitContent.text)){
                content.setText(recruitContent.text + "（" + BaseApplication.appContext.getString(R.string.come_from) + recruitContent.groupname + "）");
                content.parseIcon(defaultTextSize);
            }

        }else if (32 == record.messageType){//礼物消息
            WorldMessageGiftContent giftContent = record.giftContent;
            if (null != giftContent && !TextUtils.isEmpty(giftContent.message)){
                String message = giftContent.message;
                String targetName = "";
                if (uid == record.user.UserID){
                    targetName = giftContent.targetUserName;
                }else {
                    targetName = "<font color='#fc2452'>" + giftContent.targetUserName +"</font>";
                }
                message = message.replace("@", targetName);
                content.setText(message);
                content.parseIconWithColor(defaultTextSize);
            }
        }else if (33 == record.messageType){//技能消息
            SkillAttackResult skillContent = record.skillContent;
            setVisiable(R.id.iv_skill_gif);
            GlideUtil.loadImage(BaseApplication.appContext, skillContent.skillIcon, iv_skill_bg);

            if (!isScrolled){
                SkillHandleUtils.playFrame(BaseApplication.appContext, skillContent.gif, ivSkillGif);
                SkillHandleUtils.stopFrameAnim(iv_skill_gif_first);
            }else {
                SkillHandleUtils.stopFrameAnim(ivSkillGif);
                SkillHandleUtils.setFirstFrameImage(BaseApplication.appContext, skillContent.gif, iv_skill_gif_first);
            }

            String desc;
            if (uid == record.user.UserID){
                desc = AssetUtils.getDesc(1,skillContent);
            }else {
                desc = AssetUtils.getDesc(0, skillContent);
            }
            content.setText(desc);
            content.parseIconWithColor(defaultTextSize);
        }

        if(null!=record.user) {
            currentUid = record.user.UserID;
            userName = record.user.NickName;
            setUserIcon(record.user, headIcon);
            setUserInfo(record.user, record.rankItem);
        }
    }

    /**
     * 设置用户的头像
     * @param user
     * @param iconView
     */
    protected void setUserIcon(BaseWorldMessageBean.UserBean user, HeadPhotoView iconView) {
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
     * @param rankItem
     */
    protected void setUserInfo(BaseWorldMessageBean.UserBean userInfo, Item rankItem) {
        role_ll.setVisibility(GONE);
        SpannableString name = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, userInfo.NickName + "", 0, null);
        tvName.setText(name);
        if (isRecruit){
            llWealthRank.setVisibility(VISIBLE);
        }else {
            llWealthRank.setVisibility(GONE);
        }
        if(null!=rankItem) {
            friend_ustitle.setTitleText(rankItem);
            friend_ustitle.setVisibility(VISIBLE);
        }else{
            friend_ustitle.setVisibility(GONE);
        }
    }

    @OnClick({R.id.btn_go_in, R.id.head_icon})
    public void onViewClicked(View view) {
        switch (view.getId()){
            case R.id.btn_go_in:
                GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                if(old!=null){
                    old.isGroupIn = true;
                    old.finish();
                }
                FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
                flyAudioRoom.init(); //不应该要
                flyAudioRoom.clearAudio();

                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                Intent intent = new Intent(activity, GroupChatTopicActivity.class);
                intent.putExtra("id", groupId + "");
                intent.putExtra("isChat", true);
                activity.startActivity(intent);
                break;
            case R.id.head_icon:
                Activity activity1 = CloseAllActivity.getInstance().getTopActivity();
                if (uid == currentUid){
                    Intent intent1 = new Intent(activity1, UserInfoActivity.class);
                    activity1.startActivity(intent1);
                }else {
                    Intent intent1 = new Intent(activity1, OtherInfoActivity.class);
                    intent1.putExtra("uid", currentUid);
                    activity1.startActivity(intent1);
                }
                break;
        }
    }

    @OnLongClick(R.id.head_icon)
    public boolean onLongClick(){
        if (uid != currentUid){
            EventBus.getDefault().post(new EventLongPressUser("@" + userName + "  "));
        }
        return true;
    }

}
