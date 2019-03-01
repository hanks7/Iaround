package net.iaround.ui.view.dialog;

import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.entity.type.ReportTargetType;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.ChatbarClickUser;
import net.iaround.model.im.GroupSimpleUser;
import net.iaround.model.im.GroupUser;
import net.iaround.model.type.ReportType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.SkillRankingFragment;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.skill.skilluse.SkillUseDialogFragment;
import net.iaround.ui.skill.skilluse.SkillUsePresenter;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.LuxuryGiftView;
import net.iaround.ui.view.RatingBarView;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/6/12 18:06
 * Email：15369302822@163.com
 */
public class CustomChatBarDialog extends Dialog {

    /**关闭在线用户列表的标识**/
    private final int CLOST_CHATBAR_MEMBER_DIALOG = 10000;
    /**
     * 用户头像
     */
    private HeadPhotoView userIcon;
    /**
     *  用户头像背景
     */
    private ImageView ivCustomDiaBg1;
    private ImageView ivCustomDiaBg2;
    /**
     * 用户昵称
     */
    private TextView tvName;
    /**
     * 用户性别，年龄，星座
     */
    private RelativeLayout rlAgeSex;
    private ImageView ivSex;
    private TextView tvAge;
    private TextView tvConstellation;
    /**
     * 用户个性签名
     */
    private TextView tvSign;
    /**
     * 送礼按钮
     */
    private LinearLayout llSendGift;
    private ImageView ivSendGift;
    private TextView tvSendGift;
    /**
     * 关注按钮
     */
    private LinearLayout llFollow;
    private TextView tvRelationship;
    private ImageView ivRelationshipStatus;
    /**
     * 上麦/下麦按钮
     */
    private LinearLayout llOnWheat;
    private TextView tvDownText;
    private ImageView ivDownIcon;
    /**
     * 禁言按钮
     */
    private LinearLayout llForbid;
    private TextView tvForbidText;
    private ImageView ivForbidIcon;
    /**
     * 技能按钮
     */
    private LinearLayout llSkill;
    private ImageView ivSkill;
    private TextView tvSkill;
    /**
     * 举报按钮
     */
    private TextView tvReport;
    /**
     * 等级
     */
    private TextView tvLevel;
    /**
     * 富豪等级排名
     * 魅力等级排名
     */
    private TextView tvRegal,tvCharm;

    /**
     * 技能等级左1
     */
    private RelativeLayout rlSkillLevelLeft1;
    private ImageView ivSkillLevelLeft1;
    private ImageView ivSkillAniLeft1;
    private RatingBarView rbvSkillLevelLeft1;
    /**
     * 技能等级左2
     */
    private RelativeLayout rlSkillLevelLeft2;
    private ImageView ivSkillLevelLeft2;
    private ImageView ivSkillAniLeft2;
    private RatingBarView rbvSkillLevelLeft2;
    /**
     * 技能等级左3
     */
    private RelativeLayout rlSkillLevelLeft3;
    private ImageView ivSkillLevelLeft3;
    private ImageView ivSkillAniLeft3;
    private RatingBarView rbvSkillLevelLeft3;
    /**
     * 技能等级右1
     */
    private RelativeLayout rlSkillLevelRight1;
    private ImageView ivSkillLevelRight1;
    private ImageView ivSkillAniRight1;
    private RatingBarView rbvSkillLevelRight1;
    /**
     * 技能等级右2
     */
    private RelativeLayout rlSkillLevelRight2;
    private ImageView ivSkillLevelRight2;
    private ImageView ivSkillAniRight2;
    private RatingBarView rbvSkillLevelRight2;
    /**
     * 技能等级右3
     */
    private RelativeLayout rlSkillLevelRight3;
    private ImageView ivSkillLevelRight3;
    private RatingBarView rbvSkillLevelRight3;

    /**
     * 当前被点击者的身份
     * 0 吧主  1 管理员 2 成员 4 粉丝 3 游客
     */
    private int groupRole;

    private Context mContext;

    private User user;

    /**收礼用户*/
    private User recieveGiftUser;
    private ChatbarClickUser clickUser;

    private int type;

    private Handler mHandler = new Handler();

    private String groupid;

    private boolean isMyself;//是否是自己在麦上

    private boolean isClickMySelf = false;//是否点击自己 0 否  1 是
    /**
     * 当前点击用户与我之间的关系
     */
    private int relationShip;

    /**
     * 禁言请求
     */
    private long getForbidFlag;

    /**
     * 解禁请求
     */
    private long getCloseForbidFlag;

    private int micNumber = 0;
    /**
     * 豪华礼物的动画
     */
    private LuxuryGiftView luxuryGiftView;

    public ChatbarSendPersonalSocketListener mSendListener;

    private long getUserInfoFlag = 0; //请求用户信息
    private long reportUserFlag = 0; //举报用户
    private long followUserFlag = 0; //关注用户

    public CustomChatBarDialog(Context context, ChatbarSendPersonalSocketListener listener) {
        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;
        this.mSendListener=listener;
    }

    public CustomChatBarDialog(Context context, int themeResId) {

        super(context, R.style.chat_bar_transparent_dialog);
        this.mContext = context;
        this.type = themeResId;
    }

    protected CustomChatBarDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }
    public void setUser(User user,int groupRole,String groupid)
    {
        this.user = user;
        this.groupRole = groupRole;
        relationShip = user.getRelationship();
        this.groupid = groupid;
    }
    public static void showCustomChatbarDialog(Context mContext,User user,int groupRole,String groupid,boolean isClickMySelf,LuxuryGiftView luxuryGiftView,ChatbarSendPersonalSocketListener listener)
    {
        CustomChatBarDialog customChatBarDialog = new CustomChatBarDialog(mContext,listener);
        customChatBarDialog.setUser(user,groupRole,groupid);
        customChatBarDialog.isClickMySelf(isClickMySelf);
        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
        customChatBarDialog.show();

    }
    public void setRecieveGiftUser(User user)
    {
        this.recieveGiftUser = user;
    }

    public void isMyself(boolean isMyself)
    {
        this.isMyself = isMyself;
    }

    public void isClickMySelf(boolean isClickMySelf)
    {
        this.isClickMySelf = isClickMySelf;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_of_chat_bar);

        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
        //底部弹出样式
        window.setWindowAnimations(R.style.popwin_anim_style);
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        initView();
    }

    private void initView() {

        userIcon = (HeadPhotoView) findViewById(R.id.user_icon);
        ivCustomDiaBg1 = (ImageView) findViewById(R.id.iv_custom_bg1);
        ivCustomDiaBg2 = (ImageView) findViewById(R.id.iv_custom_bg2);
        tvName = (TextView) findViewById(R.id.tvName);
        rlAgeSex = (RelativeLayout) findViewById(R.id.rlAgeSex);
        ivSex = (ImageView) findViewById(R.id.ivSex);
        tvAge = (TextView) findViewById(R.id.tvAge);
        tvConstellation = (TextView) findViewById(R.id.tvConstellation);
        tvSign = (TextView) findViewById(R.id.tvSign);
        llSendGift = (LinearLayout) findViewById(R.id.ll_btn_send_gift);
        ivSendGift = (ImageView) findViewById(R.id.iv_send_gift);
        tvSendGift = (TextView) findViewById(R.id.tv_send_gift);
        llFollow = (LinearLayout) findViewById(R.id.ll_btn_follow);
        tvRelationship = (TextView) findViewById(R.id.tv_relationship);
        ivRelationshipStatus = (ImageView) findViewById(R.id.iv_relationship_status);
        llOnWheat = (LinearLayout) findViewById(R.id.ll_btn_on_wheat);
        tvDownText = (TextView) findViewById(R.id.tv_down_wheat);
        ivDownIcon = (ImageView) findViewById(R.id.iv_wheat_icon);
        llForbid = (LinearLayout) findViewById(R.id.ll_btn_forbid);
        tvForbidText = (TextView) findViewById(R.id.tv_forbid_text);
        ivForbidIcon = (ImageView) findViewById(R.id.iv_forbid_icon);
        llSkill = (LinearLayout) findViewById(R.id.ll_btn_skill);
        tvSkill = (TextView) findViewById(R.id.tv_skill);
        ivSkill = (ImageView) findViewById(R.id.iv_skill);

        tvReport = (TextView) findViewById(R.id.tv_report);
        tvLevel = (TextView) findViewById(R.id.tvLevel);
        tvRegal = (TextView) findViewById(R.id.tv_regal_level);
        tvCharm = (TextView) findViewById(R.id.tv_charm_level);

        rlSkillLevelLeft1 = (RelativeLayout) findViewById(R.id.rl_skill_left_1);
        rlSkillLevelLeft2 = (RelativeLayout) findViewById(R.id.rl_skill_left_2);
        rlSkillLevelLeft3 = (RelativeLayout) findViewById(R.id.rl_skill_left_3);
        rlSkillLevelRight1 = (RelativeLayout) findViewById(R.id.rl_skill_right_1);
        rlSkillLevelRight2 = (RelativeLayout) findViewById(R.id.rl_skill_right_2);

        ivSkillLevelLeft1 = (ImageView) findViewById(R.id.iv_skill_left_1);
        ivSkillLevelLeft2 = (ImageView) findViewById(R.id.iv_skill_left_2);
        ivSkillLevelLeft3 = (ImageView) findViewById(R.id.iv_skill_left_3);
        ivSkillLevelRight1 = (ImageView) findViewById(R.id.iv_skill_right_1);
        ivSkillLevelRight2 = (ImageView) findViewById(R.id.iv_skill_right_2);

        rbvSkillLevelLeft1 = (RatingBarView) findViewById(R.id.rbv_skill_left_1);
        rbvSkillLevelLeft2 = (RatingBarView) findViewById(R.id.rbv_skill_left_2);
        rbvSkillLevelLeft3 = (RatingBarView) findViewById(R.id.rbv_skill_left_3);
        rbvSkillLevelRight1 = (RatingBarView) findViewById(R.id.rbv_skill_right_1);
        rbvSkillLevelRight2 = (RatingBarView) findViewById(R.id.rbv_skill_right_2);

        ivSkillAniLeft1 = (ImageView) findViewById(R.id.iv_skill_ani_left1);
        ivSkillAniLeft2 = (ImageView) findViewById(R.id.iv_skill_ani_left2);
        ivSkillAniLeft3 = (ImageView) findViewById(R.id.iv_skill_ani_left3);
        ivSkillAniRight1 = (ImageView) findViewById(R.id.iv_skill_ani_right1);
        ivSkillAniRight2 = (ImageView) findViewById(R.id.iv_skill_ani_right2);

        /**
         * 获取点击用户信息
         */
        getUserInfoFlag = GroupHttpProtocol.getUserInfo(mContext,groupid, String.valueOf(user.getUid()), new HttpCallBackImpl(this));
    }

    /*处理获得用户信息
    * */
    private void handleGetUserInfo(String result){
        if (result != null)
        {
            clickUser = GsonUtil.getInstance().getServerBean(result,ChatbarClickUser.class);
            if (null!=clickUser && clickUser.isSuccess())
            {
                GroupSimpleUser simpleUser = clickUser.getUser();
                if (simpleUser != null) {
                    GroupUser data = simpleUser.user;
                    if(data!=null) {
                        String horoscope = data.getHoroscope();
                        if (!"null".equals(horoscope)) {
                            user.setHoroscope(Integer.parseInt(horoscope));
                        }
                        user.setUid(data.getUserid());
                        user.setIcon(data.getIcon());
                        user.setNickname(data.getNickname());
                        user.setNoteName(data.getNotes());
                        user.setAge(data.getAge());
                        user.setSex(data.getGender().equals("m") ? 1 : 0);
                        user.setSign(data.getNotes());
                        user.setGroupRole(data.getGroup_role());
                        user.setAudio(data.getAudio());
                        user.setSVip(data.getSvip());
                        user.setViplevel(data.getViplevel());
                        user.setRelationship(data.getRelation());
                        user.setLevel(data.getLevel());
                        isMyself = data.getAudio() == 1;
                        user.setIs_forbid(data.getIs_forbid());
                        user.setExpired_time(data.getExpired_time());
                        //设置用户富豪排行和魅力排行 做等级判断，如果等级大于300，显示暂无排名
                        if (clickUser.top.charmTop == 0) {
                            tvCharm.setText(mContext.getResources().getString(R.string.user_dialog_ranking));
                        } else {
                            tvCharm.setText("No."+clickUser.top.charmTop + "");
                        }
                        if (clickUser.top.pluteTop == 0) {
                            tvRegal.setText(mContext.getResources().getString(R.string.user_dialog_ranking));
                        } else {
                            tvRegal.setText("No."+clickUser.top.pluteTop + "");
                        }
                        /**技能icon**/
                        /**技能等级*/
                        List<ChatbarClickUser.SkillBean> skill = clickUser.skill;
                        int[] icons = SkillRankingFragment.icons;
                        int[] iconsbg = SkillRankingFragment.iconsbg;
                        //技能展示列表需要的数据
                        if(skill == null){
                            return;
                        }
                        if(skill.isEmpty()){
                            return;
                        }

                        List<ChatbarClickUser.SkillBean> converListNew = new ArrayList<>();
                        for(int i=0;i<5;i++){
                            boolean find = false;
                            for(int j=0;j<skill.size();j++){
                                ChatbarClickUser.SkillBean bean = skill.get(j);
                                if(bean.getSkillID()>=1 && bean.getSkillID()<=5 && (bean.getSkillID()-1) == i ){
                                    find = true;
                                }
                            }
                            if(false == find){
                                ChatbarClickUser.SkillBean itemDeafault = new ChatbarClickUser().new SkillBean();
                                itemDeafault.setSkillLevel(0);
                                itemDeafault.setSkillID(i+1);
                                converListNew.add(itemDeafault);
                            }
                        }
                        skill.addAll(converListNew);
                        int skillID = 0;
                        int skillLevel = 0;
                        for (int i = 0; i < skill.size(); i++) {
                            switch (i)
                            {
                                case 0:
                                    skillID = skill.get(0).getSkillID();
                                    skillLevel = skill.get(0).getSkillLevel();
                                    ivSkillLevelLeft1.setImageResource(iconsbg[skillID - 1]);
                                    ivSkillAniLeft1.setImageResource(icons[skillID - 1]);
                                    rbvSkillLevelLeft1.setStarView(skillLevel);
                                    break;
                                case 1:
                                    skillID = skill.get(1).getSkillID();
                                    skillLevel = skill.get(1).getSkillLevel();
                                    ivSkillLevelRight1.setImageResource(iconsbg[skillID - 1]);
                                    ivSkillAniRight1.setImageResource(icons[skillID - 1]);
                                    rbvSkillLevelRight1.setStarView(skillLevel);
                                    break;
                                case 2:
                                    skillID = skill.get(2).getSkillID();
                                    skillLevel = skill.get(2).getSkillLevel();
                                    ivSkillLevelLeft2.setImageResource(iconsbg[skillID - 1]);
                                    ivSkillAniLeft2.setImageResource(icons[skillID - 1]);
                                    rbvSkillLevelLeft2.setStarView(skillLevel);
                                    break;
                                case 3:
                                    skillID = skill.get(3).getSkillID();
                                    skillLevel = skill.get(3).getSkillLevel();
                                    ivSkillLevelRight2.setImageResource(iconsbg[skillID - 1]);
                                    ivSkillAniRight2.setImageResource(icons[skillID - 1]);
                                    rbvSkillLevelRight2.setStarView(skillLevel);
                                    break;
                                case 4:
                                    skillID = skill.get(4).getSkillID();
                                    skillLevel = skill.get(4).getSkillLevel();
                                    ivSkillLevelLeft3.setImageResource(iconsbg[skillID - 1]);
                                    ivSkillAniLeft3.setImageResource(icons[skillID - 1]);
                                    rbvSkillLevelLeft3.setStarView(skillLevel);
                                    break;
                            }
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initData();
                            }
                        }, 200);
                    }
                }
            }else
            {
                ErrorCode.showError(mContext,result);
            }
        }
    }

    //HTTP回调处理
    static class HttpCallBackImpl implements HttpCallBack{
        private WeakReference<CustomChatBarDialog> mDialog;

        public HttpCallBackImpl(CustomChatBarDialog dialog){
            mDialog = new WeakReference<CustomChatBarDialog>(dialog);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            CustomChatBarDialog dialog = mDialog.get();
            if(dialog == null){
                return;
            }
            if(flag == dialog.getUserInfoFlag){
                dialog.handleGetUserInfo(result);
            }else if(dialog.reportUserFlag == flag){
                dialog.handleReportUser(result);
            }else if(dialog.followUserFlag == flag){
                dialog.handleFollowUser(result);
            }else if(dialog.getForbidFlag == flag){
                dialog.handleForbidUser(result);
            }else if(dialog.getCloseForbidFlag == flag){
                dialog.handleUnForbidUser(result);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            CustomChatBarDialog dialog = mDialog.get();
            if(dialog == null){
                return;
            }
            if(dialog.followUserFlag == flag){
                Toast.makeText(BaseApplication.appContext,R.string.chat_bar_pay_attention,Toast.LENGTH_SHORT).show();
            }else{
                ErrorCode.toastError(BaseApplication.appContext,e);
            }
        }
    }

    /***
     * 初始化数据
     *
     */
    public void initData() {
        //设置用户等级
        if (user.getLevel() != -1)
        {
            tvLevel.setVisibility(View.VISIBLE);
            tvLevel.setText("LV."+user.getLevel());
        }

        if (user.getSVip() > 0|| user.getViplevel() > 0)
        {
            ivCustomDiaBg1.setVisibility(View.GONE);
        }else
        {
            ivCustomDiaBg1.setVisibility(View.VISIBLE);
        }

        //点击的是自己
        if (isClickMySelf)
        {
            llSkill.setVisibility(View.VISIBLE);
            ivSkill.setImageResource(R.drawable.chatbar_userinfo_skill_no);
            tvSkill.setText(R.string.chatbar_userinfo_skil);
            //如果当前我自己在线上
            if (isMyself)
            {
                //关注
                tvRelationship.setText(R.string.chat_bar_attention);
                ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention_no);
                //送礼
                tvSendGift.setText(R.string.chat_bar_dialog_send_gift);
                ivSendGift.setImageResource(R.drawable.chatbar_userinfo_send_gift_no);
                //下麦
                llOnWheat.setVisibility(View.VISIBLE);
                ivDownIcon.setImageResource(R.drawable.chatbar_userinfo_down_wheat);
                tvDownText.setText(R.string.chat_bar_dialog_down_wheat);
                //禁言和解禁
                llForbid.setVisibility(View.VISIBLE);
                ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_forbid_no);
                tvForbidText.setText(R.string.chatbar_close_forbid_user_btn);
            }else {//我当前不在麦上
                //关注
                ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention_no);
                tvRelationship.setText(R.string.chat_bar_attention);
                //送礼
                ivSendGift.setImageResource(R.drawable.chatbar_userinfo_send_gift_no);
                tvSendGift.setText(R.string.chat_bar_dialog_send_gift);
                //禁言和解禁
                llForbid.setVisibility(View.VISIBLE);
                ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_forbid_no);
                tvForbidText.setText(R.string.chatbar_close_forbid_user_btn);
                if (user.getGroupRole() == 0 || user.getGroupRole() == 1)
                {
                    llOnWheat.setVisibility(View.VISIBLE);
                    ivDownIcon.setImageResource(R.drawable.chatbar_userinfo_up_wheat);
                    tvDownText.setText(R.string.chat_bar_dialog_on_wheat);
                    llOnWheat.setOnClickListener(onWheatListener);
                }else
                {
                    llOnWheat.setVisibility(View.GONE);
                }
            }
        }else
        {//点击的是其他人
            llSkill.setVisibility(View.VISIBLE);
            ivSkill.setImageResource(R.drawable.chatbar_userinfo_skill);
            tvSkill.setText(R.string.chatbar_userinfo_skil);
            llSkill.setOnClickListener(skillIconListenr);
            //我的当前身份是吧主/管理员
            if (groupRole == 0 || groupRole == 1)
            {
                if (user.getAudio() == 1)
                {
                    llOnWheat.setVisibility(View.VISIBLE);
                    ivDownIcon.setImageResource(R.drawable.chatbar_userinfo_down_wheat);
                    tvDownText.setText(R.string.chat_bar_dialog_down_wheat);
                    ivSendGift.setImageResource(R.drawable.chatbar_userinfo_sendgift);
                    tvSendGift.setText(R.string.chat_bar_dialog_send_gift);
                    /**
                     * 如果当前已经关注不可点击
                     */
                    if (user.getRelationship() == 3 || user.getRelationship() == 1)
                    {
                        tvRelationship.setText(R.string.dynamic_filter_follow);
                        ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention_have);
                    }else
                    {
                        tvRelationship.setText(R.string.chat_bar_attention);
                        ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention);
                        llFollow.setOnClickListener(followListener);
                    }

                    llOnWheat.setOnClickListener(downWheatListener);

                    if (clickUser.getDeny_flag() == 1)
                    {//展示禁言和解禁
                        llForbid.setVisibility(View.VISIBLE);

                        if (user.getIs_forbid() == 1)
                        {//被禁言
                            ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_cancel_forbid);
                            tvForbidText.setText(mContext.getString(R.string.chatbar_close_forbid_user_btn));
                            llForbid.setOnClickListener(closeFrobidListener);
                        }else
                        {//未被禁言
                            ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_forbid);
                            tvForbidText.setText(R.string.chatbar_forbid_user_btn);
                            llForbid.setOnClickListener(forbidListener);
                        }
                    }else
                    {
                        llForbid.setVisibility(View.GONE);
                    }
                } else
                {
                    llOnWheat.setVisibility(View.VISIBLE);
                    ivDownIcon.setImageResource(R.drawable.chatbar_userinfo_up_wheat);
                    tvDownText.setText(R.string.chat_bar_dialog_on_wheat);
                    ivSendGift.setImageResource(R.drawable.chatbar_userinfo_sendgift);
                    tvSendGift.setText(R.string.chat_bar_dialog_send_gift);

                    /**
                     * 如果当前已经关注不可点击
                     */
                    if (user.getRelationship() == 3 || user.getRelationship() == 1)
                    {
                        tvRelationship.setText(R.string.dynamic_filter_follow);
                        ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention_have);
                    }else
                    {
                        ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention);
                        tvRelationship.setText(R.string.chat_bar_attention);
                        llFollow.setOnClickListener(followListener);
                    }
                    llOnWheat.setOnClickListener(onWheatListener);

                    if (clickUser.getDeny_flag() == 1)
                    {//展示禁言和解禁
                        llForbid.setVisibility(View.VISIBLE);

                        if (user.getIs_forbid() == 1)
                        {//被禁言
                            ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_cancel_forbid);
                            tvForbidText.setText(mContext.getString(R.string.chatbar_close_forbid_user_btn));
                            llForbid.setOnClickListener(closeFrobidListener);
                        }else
                        {//未被禁言
                            ivForbidIcon.setImageResource(R.drawable.chatbar_userinfo_forbid);
                            tvForbidText.setText(R.string.chatbar_forbid_user_btn);
                            llForbid.setOnClickListener(forbidListener);
                        }
                    }else
                    {
                        llForbid.setVisibility(View.GONE);
                    }

                }
            }else
            {//普通成员
                ivSendGift.setImageResource(R.drawable.chatbar_userinfo_sendgift);
                tvSendGift.setText(R.string.chat_bar_dialog_send_gift);

                llForbid.setVisibility(View.GONE);
                /**
                 * 如果当前已经关注不可点击
                 */
                if (user.getRelationship() == 3 || user.getRelationship() == 1)
                {
                    ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention_have);
                    tvRelationship.setText(R.string.dynamic_filter_follow);
                }else
                {
                    ivRelationshipStatus.setImageResource(R.drawable.chatbar_userinfo_attention);
                    tvRelationship.setText(R.string.chat_bar_attention);
                    llFollow.setOnClickListener(followListener);
                }
                llOnWheat.setVisibility(View.GONE);
            }

            llSendGift.setOnClickListener(sendGiftListener);
        }

        //头像
        if (user!= null) {
            userIcon.executeChat(R.drawable.iaround_default_img,user.getIcon(),user.getSVip(),user.getViplevel(),-1);
        }

        //昵称/备注
        String nickname = "";
        if (user.getNoteName() != null && !"null".equals(user.getNoteName()) && !"".equals(user.getNoteName()))
        {
            nickname = user.getNoteName();
        } else {
            if (user.getNickname() != null && !"null".equals(user.getNickname()) && !"".equals(user.getNickname())) {
                nickname = user.getNickname();
            } else {
                nickname = String.valueOf(user.getUid());
            }
        }
        tvName.setText(FaceManager.getInstance(mContext).parseIconForString(mContext, nickname, 0, null));

        //性别
        String sex = user.getGender();
        if ("m".equals(sex)) {
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
            ivSex.setImageResource(R.drawable.thread_register_man_select);
        } else {
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
            ivSex.setImageResource(R.drawable.thread_register_woman_select);
        }
        tvAge.setText(user.getAge()+"");

        //星座
        String[] strings = mContext.getResources().getStringArray(R.array.horoscope_date);
        if (user.getHoroscopeIndex() >= 0) {
            tvConstellation.setVisibility(View.VISIBLE);
            tvConstellation.setText(strings[user.getHoroscopeIndex()]);
        }

        //个性签名
        if (user.getSign() != null && !"".equals(user.getSign()) && !"null".equals(user.getSign())) {
            tvSign.setText(FaceManager.getInstance(mContext).parseIconForString(mContext, user.getSign(), 13, null));
        }else
        {
            tvSign.setText(mContext.getResources().getString(R.string.signature_empty_tips));
        }
        initListener();
    }

    private void initListener()
    {
//        userIcon.setOnClickListener(userIconListener);
        userIcon.setOnClickListener(userIconListener);
        if (user.getAudio() == 0)
        {
            llOnWheat.setOnClickListener(onWheatListener);
        }else if (user.getAudio() == 1)
        {
            llOnWheat.setOnClickListener(downWheatListener);
        }


        //举报用户
        tvReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showOKCancelDialog(mContext, mContext.getString(R.string.dialog_title), mContext.getString(R.string.chat_setting_report), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //举报用户
                        reportUserFlag = UserHttpProtocol.systemReport(BaseApplication.appContext, ReportType.INFORMATION_ILLEGAL, ReportTargetType.HUMAN, String.valueOf(user.getUid()), "", new HttpCallBackImpl(CustomChatBarDialog.this));
                    }
                });
            }
        });
    }

    private void handleReportUser(String result){
        if(result == null){
            return;
        }
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (bean != null)
        {
            if (bean.isSuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.report_return_content_photo);
            } else {
                ErrorCode.showError(BaseApplication.appContext, result);
            }
        }
    }
    /**
     * 用户头像点击事件
     */
    private View.OnClickListener userIconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (user.getUid() == Common.getInstance().loginUser.getUid())
            {
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                mContext.startActivity(intent);
                dismiss();
            }else
            {
                Intent intent = new Intent(mContext, OtherInfoActivity.class);
                intent.putExtra("user",user);
                intent.putExtra(Constants.UID, user.getUid());
                intent.putExtra("fromchatbar",true);
                mContext.startActivity(intent);
                dismiss();
            }
        }
    };
    /**
     * 技能点击事件
     */
    private View.OnClickListener skillIconListenr = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Common.getInstance().setShowDailog(true);
            Bundle bundle = new Bundle();
            bundle.putString("groupId", groupid);
            bundle.putString("targetUserId", user.getUid() + "");
            SkillUseDialogFragment skillUseDialogFragment = SkillUseDialogFragment.getInstance(bundle);
            new SkillUsePresenter(skillUseDialogFragment);
            FragmentManager manager = ((GroupChatTopicActivity) mContext).getFragmentManager();
            skillUseDialogFragment.show(manager, "skillUseDialogFragment");
            dismiss();
        }
    };
    /**
     * 赠送礼物的点击事件
     */
    private View.OnClickListener sendGiftListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            EventBus.getDefault().post(CLOST_CHATBAR_MEMBER_DIALOG);
            CustomGiftDialogNew.jumpIntoCustomGiftDia(mContext,user,Long.valueOf(groupid),luxuryGiftView,mSendListener);
            if(luxuryGiftView!=null){
                luxuryGiftView.setHeightPosition(5);
                luxuryGiftView.upDateDefaultMargin(5);
            }
            dismiss();
        }
    };
    /**
     * 关注按钮点击事件
     */
    private View.OnClickListener followListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            followUserFlag = FriendHttpProtocol.userFanLove(mContext, user.getUid(), 3, Long.valueOf(groupid), new HttpCallBackImpl(CustomChatBarDialog.this));
            dismiss();
        }
    };

    private void handleFollowUser(String result){
        if (result != null)
        {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,BaseServerBean.class);
            if (bean != null)
            {
                if (bean.isSuccess())
                {
                    Toast.makeText(mContext, R.string.other_info_follow, Toast.LENGTH_SHORT).show();
                }else
                {
                    ErrorCode.showError(mContext,result);
                }
            }
        }
    }

    /***
     * 上麦按钮点击事件
     */
    private View.OnClickListener onWheatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SocketGroupProtocol.onMic(mContext, groupid, user.getUid(),micNumber);
            dismiss();
        }
    };
    /***
     * 下麦按钮点击事件
     */
    private View.OnClickListener downWheatListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SocketGroupProtocol.offMic(mContext, groupid, user.getUid());
            dismiss();
        }
    };

    /**
     * 禁言事件
     */
    private View.OnClickListener forbidListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getForbidFlag = GroupHttpProtocol.groupManageForbid(mContext,groupid,String.valueOf(user.getUid()),3000000, new HttpCallBackImpl(CustomChatBarDialog.this));
            dismiss();

            if (getForbidFlag == -1)
            {
                CommonFunction.showToast( mContext , mContext.getResources().getString( R.string.operate_fail ) , 0 );
            }

        }
    };

    private void handleForbidUser(String result){
        if (result != null)
        {
            BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result,BaseServerBean.class);
            if (serverBean != null && serverBean.isSuccess())
            {
                Toast.makeText(mContext,mContext.getString(R.string.chatbar_forbid_user), Toast.LENGTH_SHORT).show();
            }else
            {
                ErrorCode.showError(mContext,result);
            }
        }
    }
    /**
     * 解禁事件
     */
    private View.OnClickListener closeFrobidListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            getCloseForbidFlag = GroupHttpProtocol.groupManagerCacelForbid(mContext, groupid, String.valueOf(user.getUid()), new HttpCallBackImpl(CustomChatBarDialog.this));
            dismiss();

            if (getCloseForbidFlag == -1)
            {
                CommonFunction.showToast( mContext , mContext.getResources().getString( R.string.operate_fail ) , 0 );
            }

        }
    };

    private void handleUnForbidUser(String result){
        if (result != null)
        {
            BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result,BaseServerBean.class);
            if (serverBean != null && serverBean.isSuccess())
            {
                Toast.makeText(mContext,mContext.getString(R.string.chatbar_close_forbid_user) , Toast.LENGTH_SHORT).show();
            }else
            {
                ErrorCode.showError(mContext,result);
            }
        }
    }

    /**
     * 设置当前麦位数量
     * @param micNumber
     */
    public void setMicNumber(int micNumber) {
        this.micNumber = micNumber;
    }
    /**
     * 设置豪华礼物的view
     */
    public void setLuxuryGiftView(LuxuryGiftView luxuryView){
        this.luxuryGiftView = luxuryView;
    }

}
