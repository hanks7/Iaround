package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constant;
import net.iaround.conf.Constants;
import net.iaround.conf.DataTag;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.RelationHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.contract.OtherInfoContract;
import net.iaround.contract.UserInfoContract;
import net.iaround.entity.type.ChatFromType;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.UserInfoEntity;
import net.iaround.model.entity.UserInfoEntity.Data.SkillBean;
import net.iaround.model.entity.UserInfoGameInfoBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.model.type.ReportType;
import net.iaround.presenter.UserInfoPresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.adapter.OtherInfoGameListAdapter;
import net.iaround.ui.adapter.OtherInfoVPAdapter;
import net.iaround.ui.adapter.UserInfoUserActionAdapter;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.Photos;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.interfaces.OnUpdateNickListener;
import net.iaround.ui.map.MapUtils;
import net.iaround.ui.skill.skilluse.SkillUseDialogFragment;
import net.iaround.ui.skill.skilluse.SkillUsePresenter;
import net.iaround.ui.space.bean.InvisibilitySettingOption;
import net.iaround.ui.space.bean.UserRelationLink;
import net.iaround.ui.store.StoreReceiveGiftActivity;
import net.iaround.ui.view.ExpandLayout;
import net.iaround.ui.view.InterceptScrollContainerView;
import net.iaround.ui.view.RatingBarView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.dialog.DynamicDeatailsSettingDialog;
import net.iaround.ui.view.dialog.FriendRemarkDialog;
import net.iaround.ui.view.dialog.NotLoginDialog;
import net.iaround.ui.view.encounter.UnderlinePageIndicator;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.ui.view.user.FlowLayout;
import net.iaround.ui.view.user.YusScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static net.iaround.R.id.expires;
import static net.iaround.R.id.iv_other_ranking;
import static net.iaround.R.string.complete;
import static net.iaround.ui.fragment.SkillRankingFragment.icons;
import static net.iaround.ui.fragment.SkillRankingFragment.iconsbg;


public class OtherInfoActivity extends BaseActivity implements OtherInfoContract.View, View.OnClickListener, YusScrollView.ScrollViewListener, HttpCallBack {

    /**
     * 标识会员状态，控制点击会员跳转
     */
    private int vipStatus = 0;
    private ArrayList<String> gifts;
    private UserInfoEntity.Data.Secret secret;
    private UserInfoContract.Persenter presenter;
    private RelativeLayout rlBaseInfoNickename;
    private long uid;
    private ViewPager vpHeader;
    private TextView tvNickname;
    private TextView tvTimeAndDistance;
    private RelativeLayout rlAgeSex;
    private ImageView ivSex;
    private TextView tvSexAndAge;
    private TextView tvHoroscope;
    private TextView tvSignature;
    private TextView tvActionNum;
    private MyGridView mgvGameList;
    private ImageView ivUserActionEmptyIcon;
    private TextView tvUserActionEmptyTips;
    private GridView gvUserAction;
    private ImageView ivUserVipIcon;
    private TextView tvVipStatus;
    private TextView tvUserLevel;
    private LinearLayout llUserVip;
    private LinearLayout llUserLevelInfo;
    private LinearLayout llUserLevel;
    private ImageView ivSendGift;
    private InterceptScrollContainerView llUserAction;
    private GridView gvUserGift;
    private ImageView ivUserGift;
    private TextView tvUserGiftEmptyTips;
    private LinearLayout llUserId;
    private LinearLayout llLoveStatus;
    private LinearLayout llUserHeight;
    private LinearLayout llUserWeight;
    private LinearLayout llUserJob;
    private LinearLayout llUserHomeTown;
    private TextView tvUserId;
    private TextView tvLoveStatus;
    private TextView tvUserHeiht;
    private TextView tvUserWeight;
    private TextView tvUserJob;
    private TextView tvUserHometown;
    private TextView tvUserBirthday;
    private TextView tvPhoneAuthenStatus;
    private LinearLayout llUserLastLocal;
    private TextView tvUserLastLocal;
    private LinearLayout llUserPhone;
    private TextView tvUserPhone;
    private InterceptScrollContainerView llUserGift;
    private LinearLayout llUserGiftInter;
    private LinearLayout llUserSecret;
    private FlowLayout flowLayoutHobby;
    private LinearLayout llUserHobbys;
    private YusScrollView svContainer;

    private ImageView ivLeft;
    private ImageView ivRight;
    private FrameLayout flLeft;

    private TextView tvOtherInfoPicCount;
    private ImageView ivChat;
    private ImageView ivLike;
    private TextView tvChatAttention;
    private TextView tvTitleNickname;
    private TextView tvExpireLevel;
    private ImageView ivUserinfoEnter;

    private ImageView ivLoginGiftOne;
    private ImageView ivLoginGiftTwo;
    private ImageView ivLoginGiftThree;
    private ImageView ivSkillShowMore;
    private LinearLayout llSkillShowMore;//技能列表展示更多
    private ExpandLayout mExpandLayout;//技能列表展示更多的控件

    //标题栏
//    private LinearLayout llHeader;
    private RelativeLayout mRlHeader;

    private UnderlinePageIndicator mIndicator;// ViewPage横向滑动进度条

    private TextView tvPicMore;

    private TextView tvMakeOrder;//下单按钮

    private DynamicDeatailsSettingDialog settingDialog;// 设置的Dialog
    private CustomContextDialog reportDialog;// 举报的Dialog
    private FriendRemarkDialog remarkDialog;
    /**
     * 加载数据Loading
     */
    private Dialog mProgressDialog;

    //抓包数据
    /**
     * 服务器当前时间
     */
    public long servertime = 0;
//    /**
//     * key
//     */
//    public String key;
    /**
     * 用户ID
     */
    public BaseUserInfo user;
    LoginResponseBean loginResponseBean;
    public int total;//总共项
    public String token;

    public String firstlogin;

    public String isbind;
    public String accesstoken;//微博用户身份
    public String openid;//微博用户ID
    /**
     * V5.0
     * 5.5以及以后此字段不再添加位数，由于客户端的历史原因，添加位数会导致低版本部分开关异常
     */
    public String newonoffs;//开关
    /**
     * 新增开关字段 5.5以及以后的版本可在此字段添加位数作为开关
     * 第一位为客户端从大众点评获取圈中心信息以及发布动态选择地址（1：开 0：关）
     */
    public String switchs;//
    public Me me;
    public static long NODE_ID = 0;// 中间节点id（圈子为圈子id，游戏为游戏id）
    public static final int DEFAULT_TYPE = 0;
    private int from;
    private User tempuser = new User();

    /**
     * 隐私设置
     */
    public int INVISIBLE_TO_OTHERS = 0;//已对TA隐身
    public int DYNAMIC_REJECT_HIM = 0;//不让他看我的动态
    public int DYNAMIC_NO_SEE_HIM = 0;//不看TA的动态
    public int BLACKLIST_PERSON = 0;//设置黑名单
    public int REPORT_PERSON = 0;//举报

    private long GET_INVISIBILITY_SETTING_FLAG;//请求关于隐私设置的标识
    private long UPDATE_INVISIBILITY_SETTING_FLAG;
    private long UPDATE_INVISIBILITY_SETTING_REJECT_FLAG;
    private long UPDATE_BLACK_LIST_FLAG;
    private long REPORT_USER_FLAG;

    private InvisibilitySettingOption option;
    private long devilId;
    private Bundle locationBundle = new Bundle();
    private boolean isAttention;

    /* 头像最后一页判断*/
    private boolean isDragPage = false;
    private boolean canJumpPage = true;

    private long GET_USER_RELATION_LINK_FLAG;
    private int relationShip;
    private static final int EDIT_SPACE_MODIFY_PHOTO = 1013;
    private long lat;
    private long lng;
    private String address;

    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private SpannableString spNickname;
    private ArrayList<String> picList;
    private boolean isPicValue;
    private LinearLayout llOtherInfoGift;

    private boolean isHeaderViewPage = true;
    private boolean isHasMorePic;
    private TextView tvSkillRanking;//nickName后面的称号
    private UserTitleView userTitleView;
    private RelativeLayout rlChatAttention;//关注
    private RelativeLayout rlChatChat;//聊天
    private RelativeLayout rlChatGift;//送礼
    private RelativeLayout rlChatSkill;//技能
    private ImageView ivSkillLeftMain;
    private RatingBarView rbvSkillLeft;
    private ImageView ivSkillRightMain;
    private RatingBarView rbvSkillRight;
    private ImageView ivSkillStatus;//技能状态展示
    private ImageView ivChatBarStatus;//所在聊吧状态展示
    private TextView tvSkillStatus;

    private TextView tvCharBarName;//他人资料页正在玩的聊吧名字
    private RelativeLayout rlSkillFirstLeft;//技能 左边
    private RelativeLayout rlSkillFirstRight;//技能 右边
    private RelativeLayout rlSkillChatBarStatus;//当前所在哪个聊吧

    private boolean fromChatBar = false; //用户是否从聊吧页面跳转到本页面
    private String gotoChatbarId = null; //是否需要显示的聊吧页面ID（从附近页面有聊吧的用户进个人页面时需要）

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunction.log("OtherInfoActivity", "onCreate() into");
        setContentView(R.layout.activity_other_info);
        presenter = new UserInfoPresenter(this);
        presenter.onActivityCreate();
        initViews();
        initActionBar();
        initParams();
        initDatas();
        initListeners();

        //如果是从附近页面进入个人资料页 产品要求再跳去聊吧页面
        if (false == TextUtils.isEmpty(gotoChatbarId)) {
            GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
            if (old != null) {
                old.isGroupIn = true;
                old.finish();
            }
            Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
            intent.putExtra("id", gotoChatbarId);
            intent.putExtra("isChat", true);
            intent.putExtra("fromUserId", String.valueOf(uid));
            startActivity(intent);
        }
        CommonFunction.log("OtherInfoActivity", "onCreate() out");
    }

    private void initViews() {

        svContainer = findView(R.id.sv_container);
        tvOtherInfoPicCount = findView(R.id.tv_other_info_pic_count);
        vpHeader = findView(R.id.vp_header);
        ivChat = findView(R.id.iv_chat_chat);
        ivLike = findView(R.id.iv_chat_attention);
        tvChatAttention = findView(R.id.tv_chat_attention);
        rlBaseInfoNickename = findView(R.id.rl_base_info_nickname);
        rlBaseInfoNickename.setVisibility(View.VISIBLE);
        tvNickname = findView(R.id.tv_nickname);
        tvTimeAndDistance = findView(R.id.tv_time_and_distance);
        rlAgeSex = findView(R.id.rlAgeSex);
        ivSex = findView(R.id.ivSex);
        tvSexAndAge = findView(R.id.tv_age);
        tvHoroscope = findView(R.id.tv_horoscope);
        tvSignature = findView(R.id.tv_signature);
        tvActionNum = findView(R.id.tv_action_num);
        mgvGameList = findView(R.id.mgv_game_list);
        ivUserActionEmptyIcon = findView(R.id.iv_dynamic);
        tvUserActionEmptyTips = findView(R.id.tv_user_action_empty_tips);
        llUserAction = (InterceptScrollContainerView) findViewById(R.id.ll_user_action);
        gvUserAction = findView(R.id.gv_user_action);
        ivUserVipIcon = findView(R.id.iv_user_vip_icon);
        llUserVip = findView(R.id.ll_user_vip);
        tvVipStatus = findView(R.id.tv_vip_status);
        llUserLevelInfo = findView(R.id.ll_user_level_info);
        llUserLevel = findView(R.id.ll_user_level);
        tvUserLevel = findView(R.id.tv_user_level);
        llUserGift = (InterceptScrollContainerView) findViewById(R.id.ll_user_gift);
        llUserGiftInter = (LinearLayout) findViewById(R.id.ll_user_gift_intenter);
        ivSendGift = findView(R.id.iv_send_gift);
        ivUserGift = findView(R.id.iv_user_gift);
        gvUserGift = findView(R.id.gv_user_gift);
        gvUserGift.setFocusable(false);
        tvUserGiftEmptyTips = findView(R.id.tv_user_gift_empty_tips);
        llUserHobbys = findView(R.id.ll_user_hobbys);
        flowLayoutHobby = findView(R.id.flowlayout_hobby);
        llUserId = findView(R.id.ll_user_id);
        tvUserId = findView(R.id.tv_user_id);
        llLoveStatus = findView(R.id.ll_love_status);
        llLoveStatus = findView(R.id.ll_love_status);
        tvLoveStatus = findView(R.id.tv_love_status);
        llUserHeight = findView(R.id.ll_user_height);
        tvUserHeiht = findView(R.id.tv_user_height);
        llUserWeight = findView(R.id.ll_user_weight);
        tvUserWeight = findView(R.id.tv_user_weight);
        llUserJob = findView(R.id.ll_user_job);
        tvUserJob = findView(R.id.tv_user_job);
        tvUserBirthday = findView(R.id.tv_user_birthday);
        llUserHomeTown = findView(R.id.ll_user_himetown);
        tvUserHometown = findView(R.id.tv_user_hometown);
        llUserSecret = findView(R.id.ll_user_secret);
        tvPhoneAuthenStatus = findView(R.id.tv_phone_authen_status);
        llUserLastLocal = findView(R.id.ll_user_last_local);
        tvUserLastLocal = findView(R.id.tv_user_last_local);
        llUserPhone = findView(R.id.ll_user_phone);
        tvUserPhone = findView(R.id.tv_user_phone);
        tvPicMore = findView(R.id.tv_other_info_more_pic);
        tvMakeOrder = findView(R.id.tv_make_order);

        settingDialog = new DynamicDeatailsSettingDialog(this, 0, 0, 0, 0, 0);
        reportDialog = new CustomContextDialog(this, 1);
        reportDialog.setListenner(reportClick);
        remarkDialog = new FriendRemarkDialog(this, new OnUpdateNickListener() {
            @Override
            public void update(String noteName) {
                if ("".equals(noteName) || noteName == null) {
                    tvNickname.setText(presenter.getData().getNickname());
                    tvTitleNickname.setText(presenter.getData().getNickname());
                    presenter.getData().setNotes("");
                } else {
                    tvNickname.setText(noteName);
                    tvTitleNickname.setText(noteName);//YC  更改标题
                    presenter.getData().setNotes(noteName);
                }

            }
        });
        mIndicator = (UnderlinePageIndicator) findViewById(R.id.magic_indicator);
        mIndicator.setSelectedColor(getResources().getColor(R.color.take_pic_btn_normal));
        tvUserLevel = (TextView) findViewById(R.id.tv_user_level);
        tvExpireLevel = (TextView) findViewById(R.id.tv_user_expire_level);
        ivUserinfoEnter = (ImageView) findViewById(R.id.iv_user_info_enter);

        tvSkillRanking = (TextView) findViewById(iv_other_ranking);
        userTitleView = (UserTitleView) findViewById(R.id.other_info_user_title);
        rlChatAttention = (RelativeLayout) findViewById(R.id.rl_chat_attention);
        rlChatChat = (RelativeLayout) findViewById(R.id.rl_chat_chat);
        rlChatGift = (RelativeLayout) findViewById(R.id.rl_chat_gift);
        rlChatSkill = (RelativeLayout) findViewById(R.id.rl_chat_skill);

        ivLoginGiftOne = (ImageView) findViewById(R.id.iv_login_user_gift_one);
        ivLoginGiftTwo = (ImageView) findViewById(R.id.iv_login_user_gift_two);
        ivLoginGiftThree = (ImageView) findViewById(R.id.iv_login_user_gift_three);
        llOtherInfoGift = (LinearLayout) findViewById(R.id.ll_other_info_gift);
        ivSkillShowMore = (ImageView) findViewById(R.id.iv_center);
        llSkillShowMore = (LinearLayout) findViewById(R.id.ll_skill_center);
        mExpandLayout = (ExpandLayout) findViewById(R.id.el_skill_show);
        mExpandLayout.initExpand(false);

        ivSkillLeftMain = (ImageView) findViewById(R.id.iv_skill_left_main);
        rbvSkillLeft = (RatingBarView) findViewById(R.id.rbv_skill_left_first);

        ivSkillRightMain = (ImageView) findViewById(R.id.iv_skill_right_main);
        rbvSkillRight = (RatingBarView) findViewById(R.id.rbv_skill_right_first);
        rbvSkillLeft.setStarView(28);
        rbvSkillRight.setStarView(39);

        tvCharBarName = (TextView) findViewById(R.id.tv_show_chat_bar_name);
        ivSkillStatus = (ImageView) findViewById(R.id.iv_show_status);
        ivChatBarStatus = (ImageView) findViewById(R.id.iv_show_chat_bar);
        tvSkillStatus = (TextView) findViewById(R.id.tv_show_status);

        rlSkillFirstLeft = (RelativeLayout) findViewById(R.id.rl_skill_left_main);
        rlSkillFirstRight = (RelativeLayout) findViewById(R.id.rl_skill_right_main);
        rlSkillChatBarStatus = (RelativeLayout) findViewById(R.id.rl_other_info_show_chat_bar);

    }

    private void initActionBar() {
        mRlHeader = findView(R.id.rl_actionbar);
        ivLeft = findView(R.id.iv_left);
        flLeft = findView(R.id.fl_left);
        tvTitleNickname = findView(R.id.tv_title);
        ivRight = findView(R.id.iv_right);

        tvTitleNickname.setVisibility(View.INVISIBLE);
        ivLeft.setVisibility(View.VISIBLE);
        ivRight.setVisibility(View.VISIBLE);

        mRlHeader.setBackgroundColor(getResources().getColor(R.color.transparent));
        ivLeft.setImageResource(R.drawable.other_info_back);
        ivRight.setImageResource(R.drawable.new_other_info_more);

    }

    private void initDatas() {
        uid = getIntent().getLongExtra(Constants.UID, -1);
        presenter.initOtherInfo(OtherInfoActivity.this, uid, 0);
        reqMoreData();
        if (tempuser == null) {
            tempuser = new User();
        }
        tempuser.setUid(uid);

    }

    private void reqRelationLink() {
        GET_USER_RELATION_LINK_FLAG = RelationHttpProtocol.getUserRelationLink(OtherInfoActivity.this, uid, new HttpCallBackImpl(this));
    }

    private void initListeners() {
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        svContainer.setScrollViewListener(this);
        ivChat.setOnClickListener(this);
        ivLike.setOnClickListener(this);
        llUserAction.setOnClickListener(this);
        llUserLevel.setOnClickListener(this);
        llUserLevelInfo.setOnClickListener(this);
        ivSendGift.setOnClickListener(this);
        llUserSecret.setOnClickListener(this);
        llUserLastLocal.setOnClickListener(this);
        tvUserGiftEmptyTips.setOnClickListener(this);
        ivSkillShowMore.setOnClickListener(this);
        llSkillShowMore.setOnClickListener(this);
        rlChatAttention.setOnClickListener(this);
        rlChatChat.setOnClickListener(this);
        rlChatGift.setOnClickListener(this);
        rlChatSkill.setOnClickListener(this);
        tvCharBarName.setOnClickListener(this);
    }

    @Override
    public void setHeadPics(ArrayList<String> picsThum, ArrayList<String> pics, ArrayList<Photos.PhotosBean> headPhontos) {
        if (tempuser == null)
            tempuser = new User();
        // 避免重复执行 头部闪动
        if (!isHeaderViewPage) return;
        tempuser.setNickname(presenter.getData().getNickname());
        tempuser.setNoteName(presenter.getData().getNotes());
        tempuser.setRelationship(presenter.getData().getRelation());
        picList = new ArrayList();

        String url = "";
        if (headPhontos != null && headPhontos.size() > 0) {
            for (Photos.PhotosBean photosBean : headPhontos) {
                picList.add(photosBean.getImage());
            }
            if (pics != null && pics.size() > 0) {
                String iconUrl = pics.get(0);
                if (iconUrl != null && !"".equals(iconUrl)) {
                    if (iconUrl.contains(".jpg")) {
                        url = iconUrl.replace("_s.jpg", ".jpg");
                    } else if (iconUrl.contains(".png")) {
                        url = iconUrl.replace("_s.png", ".png");
                    }
                }
            }
            if (url.contains(".jpg") || url.contains(".png")) {
                picList.add(0, url);
                isPicValue = true;
            }
            if (isPicValue) {
                tvOtherInfoPicCount.setText(picsThum.size() + 1 + "");
            } else {
                if (picsThum.size() > 0) {
                    tvOtherInfoPicCount.setText(picsThum.size() + "");
                }

            }
            ArrayList<String> pathList = new ArrayList<>();
            if (picList.size() > 8) {
                for (int i = 0; i < 9; i++) {
                    pathList.add(picList.get(i));
                }
                picList = pathList;
                isHasMorePic = true;
            }
            ArrayList<String> picThumNew = new ArrayList<>();
            if (picsThum.size() > 8) {
                for (int i = 0; i < 8; i++) {
                    picThumNew.add(picsThum.get(i));
                }
                picsThum = picThumNew;
            }

            if (picList.size() <= 1) {
                mIndicator.setVisibility(View.GONE);
            } else {
                mIndicator.setVisibility(View.VISIBLE);
            }
            vpHeader.setAdapter(new OtherInfoVPAdapter(this, picList, picList));
        } else {

            if (pics.size() <= 1) {
                mIndicator.setVisibility(View.GONE);
            } else {
                mIndicator.setVisibility(View.VISIBLE);
            }
            tvOtherInfoPicCount.setText(pics.size() + "");
            vpHeader.setAdapter(new OtherInfoVPAdapter(this, pics, pics));
        }


        mIndicator.setViewPager(vpHeader);
        mIndicator.setFades(false);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 在屏幕滚动过程中不断被调用
             * @param position
             * @param positionOffset   是当前页面滑动比例，如果页面向右翻动，这个值不断变大，最后在趋近1的情况后突变为0。如果页面向左翻动，这个值不断变小，最后变为0
             * @param positionOffsetPixels   是当前页面滑动像素，变化情况和positionOffset一致
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 7 && isDragPage && positionOffsetPixels == 0 && isHasMorePic) {   //当前页是第8页，并且是拖动状态，并且像素偏移量为0   picList.size() > 8
                    if (canJumpPage) {
                        canJumpPage = false;

                        long currentTime = Calendar.getInstance().getTimeInMillis();
                        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                            lastClickTime = currentTime;
                            //最后一页滑动
                            if (relationShip == 1 || Common.getInstance().loginUser.isVip()) {
                                //查看更过相册图片
                                Intent i = new Intent(OtherInfoActivity.this, SpaceTopic.class);
                                i.putExtra("uid", tempuser.getUid());
                                i.putExtra("nickName", tempuser.getNickname());
                                i.putExtra("notename", tempuser.getNoteName(false));
                                i.putExtra("type", 1);
                                // startActivity( i );
                                startActivityForResult(i, EDIT_SPACE_MODIFY_PHOTO);
                            } else {
                                DialogUtil
                                        .showTobeVipDialog(mContext, R.string.recent_visitor, R.string.svip_can_see_more_pic
                                                , new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        Intent intent = new Intent(OtherInfoActivity.this, UserVipOpenActivity.class);
                                                        startActivity(intent);
                                                        lastClickTime = 0;
                                                        canJumpPage = true;
                                                    }
                                                }, new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        lastClickTime = 0;
                                                        canJumpPage = true;
                                                    }
                                                });
                            }
                        }

                    }
                } else {
                    canJumpPage = true;
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 7 && isHasMorePic) {//picList.size() > 8
                    tvPicMore.setVisibility(View.VISIBLE);
                } else {
                    tvPicMore.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isDragPage = state == 1;
            }
        });

    }

    @Override
    public void setRelation(int relation) {
        /**
         * relation
         * 0、没有关系
         * 1、好友
         * 2、陌生人
         * 4.粉丝
         */
        relationShip = relation;
        if (relation == 2) {
            ivLike.setSelected(false);
            tvChatAttention.setText(getString(R.string.following));
        } else if (relation == 3 || relation == 1) {//relation == 3
            ivLike.setSelected(true);
            tvChatAttention.setText(getString(R.string.dynamic_filter_follow));
        }
    }

    @Override
    public void setNickname(String nickname, String skillTitle) {
        spNickname = FaceManager.getInstance(this).parseIconForString(this, nickname, 0, null);
        tvNickname.setText(spNickname);
        tvTitleNickname.setText(spNickname);
        if (presenter.getData() != null && presenter.getData().getNotes() != null) {
            if (presenter.getData().getNotes() != null || !presenter.getData().getNotes().contains("null")
                    || !TextUtils.isEmpty(presenter.getData().getNotes())) {
                remarkDialog.setNickname(presenter.getData().getNotes());
            } else {
                remarkDialog.setNickname("");
            }
        }

    }


    @Override
    public void setTimeAndDistance(long time, double distance) {
        String dis;
        if (distance > 1000) {
            dis = String.format("%.1f", (distance) / 1000) + "km";
        } else {
            dis = String.format("%.1f", distance) + "m";
        }
        if (distance == -1.0 || "null".equals(dis)) {
            dis = "0" + "m";
            tvTimeAndDistance.setText(TimeFormat.timeFormat4(BaseApplication.appContext, time) + "·" + dis);
        } else {
            tvTimeAndDistance.setText(TimeFormat.timeFormat4(BaseApplication.appContext, time) + "·" + dis);
        }

    }

    @Override
    public void setUserType(int userType, int love) {
        //主播入口是否显示
        if (userType == 1) {
            findViewById(R.id.rl_other_info_video).setVisibility(View.VISIBLE);
            findViewById(R.id.rl_other_info_video).setOnClickListener(this);
        } else {
            findViewById(R.id.rl_other_info_video).setVisibility(View.GONE);
        }

    }

    @Override
    public void setAgeAndSex(String age, String sex) {
        if ("m".equals(sex)) {
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
            ivSex.setImageResource(R.drawable.thread_register_man_select);
        } else {
            rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
            ivSex.setImageResource(R.drawable.thread_register_woman_select);
        }

        try {
            tvSexAndAge.setText(age + "");
        } catch (Exception e) {
            tvSexAndAge.setText("0");
            e.printStackTrace();
        }
        tvUserBirthday.setText(age + "");
    }

    @Override
    public void setHoroscope(int horoscope) {
        tvHoroscope.setText(getResStringArr(R.array.horoscope_date)[horoscope]);
        CommonFunction.changeColor(tvHoroscope, 0xFFFFCD6C);
    }

    @Override
    public void setSignature(String signature) {
        if (TextUtils.isEmpty(signature)) {
            tvSignature.setText(getString(R.string.signature_empty_tips));
        } else {
            SpannableString spSignature = FaceManager.getInstance(this).parseIconForString(this, signature, 0, null);
            tvSignature.setText(spSignature);
        }
    }

    @Override
    public void setAction(int count, ArrayList<String> actionPics) {
        String actionNum = getString(R.string.userinfo_action) + "  <font color= '#ff0000'>" + count + "</font>";
        tvActionNum.setText(Html.fromHtml(actionNum));
        if (count == 0 && actionPics.size() == 0) {
            tvActionNum.setText(getString(R.string.userinfo_action));
            llUserAction.setVisibility(View.VISIBLE);
            ivUserActionEmptyIcon.setVisibility(View.VISIBLE);
            tvUserActionEmptyTips.setVisibility(View.GONE);
            gvUserAction.setVisibility(View.GONE);
            llUserAction.setOnClickListener(null);
            findViewById(R.id.iv_dynamic_next).setVisibility(View.GONE);
        } else if (count >= 0 && actionPics.size() > 0) {
            if (count == 0) {
                tvActionNum.setText(getString(R.string.userinfo_action));
            }
            if (actionPics.size() > 0) {
                ivUserActionEmptyIcon.setVisibility(View.GONE);
            }
            gvUserAction.setVisibility(View.VISIBLE);
            llUserAction.setVisibility(View.VISIBLE);
            llUserAction.setOnClickListener(this);
            findViewById(R.id.iv_dynamic_next).setVisibility(View.VISIBLE);
            gvUserAction.setAdapter(new UserInfoUserActionAdapter(actionPics));
            gvUserAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PersonalDynamicActivity.skipToPersonalDynamicActivity(OtherInfoActivity.this, OtherInfoActivity.this, uid, 0);
                }
            });
        } else {
            llUserAction.setVisibility(View.VISIBLE);
            ivUserActionEmptyIcon.setVisibility(View.VISIBLE);
            tvUserActionEmptyTips.setVisibility(View.GONE);
            llUserAction.setOnClickListener(null);
            findViewById(R.id.iv_dynamic_next).setVisibility(View.GONE);
            gvUserAction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    PersonalDynamicActivity.skipToPersonalDynamicActivity(OtherInfoActivity.this, OtherInfoActivity.this, uid, 0);
                }
            });
        }
    }

    @Override
    public void setAccount(int vip, int level, long vipexpire, int charmnum, int userlevel) {
        vipStatus = vip;
        ivUserinfoEnter.setVisibility(View.GONE);
        if (vip == Constants.USER_VIP) {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip);
            tvVipStatus.setText(getResources().getString(R.string.userinfo_open_vip_other_vip_is));
        } else if (vip == Constants.USER_SVIP) {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip);
            tvVipStatus.setText(getString(R.string.userinfo_forever_vip));
        } else {
            ivUserVipIcon.setImageResource(R.drawable.user_info_vip_drak);
            tvVipStatus.setText(getResources().getString(R.string.userinfo_open_vip_other_vip_no));
        }
        tvUserLevel.setText("Lv." + userlevel + getResources().getString(R.string.charm_lv));

        // 魅力图形
        int[] charismaSymbole = CommonFunction.getCharismaSymbol(charmnum);
        if (charismaSymbole.length >= 6) {
            int rank = charismaSymbole[5];
            int index = (rank - 1) / 20;
            index = index > 4 ? 4 : index;
            tvExpireLevel.setText("Lv." + rank + getResources().getString(R.string.charm_lv));
        } else {
            tvExpireLevel.setText("Lv." + charmnum + getResources().getString(R.string.charm_lv));
        }
    }


    @Override
    public void setGift(ArrayList<String> gifts) {
        this.gifts = gifts;
        if (gifts == null || gifts.size() == 0) {
            llUserGiftInter.setVisibility(View.GONE);
            llOtherInfoGift.setVisibility(View.GONE);
            tvUserGiftEmptyTips.setVisibility(View.VISIBLE);
            tvUserGiftEmptyTips.setText(getString(R.string.otherinfo_gift_empty_tips));
            gvUserGift.setVisibility(View.GONE);
            ivUserGift.setVisibility(View.GONE);

        } else {
            tvUserGiftEmptyTips.setVisibility(View.GONE);
            llOtherInfoGift.setOnClickListener(this);//YC 只有在有礼物的时候才可以点击
            llUserGiftInter.setOnClickListener(this);
            ivUserGift.setOnClickListener(this);
            llUserGift.setOnClickListener(this);
            updateLoginGift(gifts);
        }
    }

    public void updateLoginGift(ArrayList<String> gifts) {
        for (int i = 0; i < gifts.size(); i++) {
            switch (i) {
                case 0:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftOne, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                case 1:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftTwo, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;
                case 2:
                    GlideUtil.loadCircleImage(BaseApplication.appContext, gifts.get(i), ivLoginGiftThree, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public void setHobby(ArrayList<String> commonHobby, ArrayList<String> specialHobby) {
        flowLayoutHobby.removeAllViews();
        flowLayoutHobby.setMaxLine(3, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(OtherInfoActivity.this, "跳转至爱好详情界面", Toast.LENGTH_SHORT).show();
            }
        });
        if (commonHobby.size() <= 0 & specialHobby.size() <= 0) {
            llUserHobbys.setVisibility(View.GONE);
            return;
        } else {
            llUserHobbys.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setAboutMe(ArrayList<UserInfoEntity.Data.AboutMe> aboutMe) {//ArrayList<OtherInfoEntity.AboutMe>
        for (int i = 0; i < aboutMe.size(); i++) {
            int uname = Integer.parseInt(aboutMe.get(i).getUname());
            switch (uname) {
                case 1:
                    llUserId.setVisibility(View.VISIBLE);
                    tvUserId.setText(aboutMe.get(i).getUvalue());
                    break;
                case 2:
                    int love = Integer.parseInt(aboutMe.get(i).getUvalue());
                    String[] loveArr = BaseApplication.appContext.getResources().getStringArray(R.array.love_status_data);

                    if (love >= 1 && love <= 3) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[love - 1]);
                    } else if (love == 10) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[3]);
                    } else if (love == 11) {
                        llLoveStatus.setVisibility(View.VISIBLE);
                        tvLoveStatus.setText(loveArr[4]);
                    } else {

                    }
                    break;
                case 3:
                    llUserHeight.setVisibility(View.VISIBLE);

                    tvUserHeiht.setText(aboutMe.get(i).getUvalue() + "cm");
                    break;
                case 4:
                    llUserWeight.setVisibility(View.VISIBLE);
                    tvUserWeight.setText(aboutMe.get(i).getUvalue() + "kg");
                    break;
                case 6:
                    int occupation = Integer.parseInt(aboutMe.get(i).getUvalue());
                    if (occupation > 0) {
                        llUserJob.setVisibility(View.VISIBLE);
                        tvUserJob.setText(getResStringArr(R.array.job)[occupation - 1]);
                    }
                    break;
                case 7:
                    llUserHomeTown.setVisibility(View.VISIBLE);
                    if (aboutMe.get(i).getUvalue() != null) {

                        String home = aboutMe.get(i).getUvalue().replace(":", ",");
                        String[] hometownEntity = home.split(",");

                        if (hometownEntity.length > 5) {
                            tvUserHometown.setText(hometownEntity[5] + " " + hometownEntity[3]);
                        } else if (hometownEntity.length >= 2) {
                            tvUserHometown.setText(hometownEntity[1] + "");


                        } else {
                            llUserHomeTown.setVisibility(View.GONE);
                        }
                    }

                    break;
            }
        }
    }

    // hometown
    private String setHometownView(String hometown) {
        if (!CommonFunction.isEmptyOrNullStr(hometown.trim())) {
            String[] hometowns = hometown.split(",");
            if (hometowns.length == 3) {
                String newHomeTown = hometowns[2] + " " + hometowns[1];
                newHomeTown.replaceAll("[0-9:]", "");
            } else {
                hometown.replaceAll("[0-9:]", "");
            }
        }
        return hometown;
    }

    @Override
    public void setSecret(UserInfoEntity.Data.Secret secret) {
        this.secret = secret;
    }

    @Override
    public void setAuthenInfo(int phoneStatus, int picStatus) {
        switch (phoneStatus) {
            case Constants.PHONE_AUTHEN_SUC:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_suc));
                break;
            case Constants.PHONE_AUTHEN_NO:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_no));
                break;
            default:
                tvPhoneAuthenStatus.setText(getString(R.string.authen_status_no));
        }
    }

    @Override
    public void setLocation(Boolean isShow, UserInfoEntity.Data.Location location) {
        if (isShow && location != null && location.getAddress() != null && !"".equals(location.getAddress()) && !"null".equals(location.getAddress())) {
            llUserLastLocal.setVisibility(View.VISIBLE);
            if (location != null) {
                tvUserLastLocal.setText(location.getAddress());
                String addressnew;
                if (location.getAddress().contains("+")) {
                    address = location.getAddress().replace("+", "");
                    if (address.contains(",")) {
                        addressnew = address.replace(",", " ");
                        tvUserLastLocal.setText(addressnew);
                    } else {
                        tvUserLastLocal.setText(address);
                    }

                } else if (location.getAddress().contains(",")) {
                    {
                        address = location.getAddress().replace(",", " ");
                        tvUserLastLocal.setText(address);
                    }
                } else {

                }
                lat = location.getLat();
                lng = location.getLng();
                address = location.getAddress();
            }
        } else {
            llUserLastLocal.setVisibility(View.GONE);
        }
    }

    @Override
    public void setPhone(Boolean isShow, String phone) {
        if (isShow && phone != null && !"".equals(phone)) {
            llUserPhone.setVisibility(View.VISIBLE);
            tvUserPhone.setText(phone);
        } else {
            llUserPhone.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideAllViews() {
        svContainer.setVisibility(View.GONE);
    }

    @Override
    public void showDialog(Context context) {
        showWaitDialog(context);
    }

    @Override
    public void hideDialog() {
        hideWaitDialog();
    }

    @Override
    public void saveIsChat(int ischat) {
        if (tempuser != null) {
            tempuser.setRelationship(ischat);
        }
    }

    @Override
    public void setLastLocal(UserInfoEntity.Data.Location location) {
        if (location != null && location.getAddress() != null && !"".equals(location.getAddress()) && !"null".equals(location.getAddress())) {
            String address;
            String addressnew;
            String addressNext;
            locationBundle.putDouble(Constants.LATITUDE_KEY, location.getLat() * 1.0 / 1e6);
            locationBundle.putDouble(Constants.LONGITUDE_KEY, 116.337323 * 1.0 / 1e6);
            locationBundle.putString(Constants.ADDRESS_KEY, location.getAddress());

            if (location.getAddress().contains("+")) {
                address = location.getAddress().replace("+", "");
                if (address.contains(",")) {
                    addressnew = address.replace(",", " ");
                    tvUserLastLocal.setText(addressnew);
                } else {
                    tvUserLastLocal.setText(address);
                }

            } else if (location.getAddress().contains(",")) {
                {
                    address = location.getAddress().replace(",", " ");
                    tvUserLastLocal.setText(address);
                }
            } else {

            }

        } else {
            llUserLastLocal.setVisibility(View.GONE);
        }
    }


    @Override
    public void setSkillStatus(UserInfoEntity.Data.AffectBean affect, final UserInfoEntity.Data.PlayGroupBean playGroup) {
        //处理技能状态
        if (affect != null) {
            if (affect.getDeny() == 0) {//0代表未被提出
                ivSkillStatus.setImageResource(R.drawable.other_user_info_skill_status_normal_bg);
                tvSkillStatus.setText(getResources().getString(R.string.chat_bar_show_status_normal));
            } else {
                ivSkillStatus.setImageResource(R.drawable.other_user_info_skill_status_error_bg_new);
                String propExpend = getResources().getString(R.string.other_info_skill_status_error) + "  " + getResources().getString(R.string.other_info_skill_status_error_info_ahead) + "<font color='" + getResources().getColor(R.color.login_btn) + "'>" + getResources().getString(R.string.other_info_skill_title_wuyingjiao) + "</font>" + getResources().getString(R.string.other_info_skill_status_error_wuyingjiao_info_end);
                tvSkillStatus.setText(Html.fromHtml(propExpend));
            }

            if (affect.getGag() == 0) {//代表未禁言
                ivSkillStatus.setImageResource(R.drawable.other_user_info_skill_status_normal_bg);
                tvSkillStatus.setText(getResources().getString(R.string.chat_bar_show_status_normal));
            } else {
                ivSkillStatus.setImageResource(R.drawable.other_user_info_skill_status_error_bg);
                String propExpend = getResources().getString(R.string.other_info_skill_status_error) + "  " + getResources().getString(R.string.other_info_skill_status_error_info_ahead) + "<font color='" + getResources().getColor(R.color.login_btn) + "'>" + getResources().getString(R.string.other_info_skill_title_jinguzhou) + "</font>" + getResources().getString(R.string.other_info_skill_status_error_jinguzhou_info_end);
                tvSkillStatus.setText(Html.fromHtml(propExpend));
            }
        } else {
            ivSkillStatus.setImageResource(R.drawable.other_user_info_skill_status_normal_bg);
            tvSkillStatus.setText(getResources().getString(R.string.chat_bar_show_status_normal));

        }
        //如果用户状态显示他所在聊吧,跳去此用户所在的聊吧
        if (playGroup != null) {
            if (!TextUtils.isEmpty(playGroup.getChatGroupName())) {
                rlSkillChatBarStatus.setVisibility(View.VISIBLE);
                String name = FaceManager.getInstance(OtherInfoActivity.this).parseIconForString(OtherInfoActivity.this, playGroup.getChatGroupName(), 0, null).toString();
                tvCharBarName.setText("  " + name + "  ");
                tvCharBarName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupChatTopicActivity activityOldGroupChat = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                        if (activityOldGroupChat != null) {
                            activityOldGroupChat.isGroupIn = true;
                            activityOldGroupChat.finish();
                        }
                        //CloseAllActivity.getInstance().closeTarget(GroupChatTopicActivity.class);

                        FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
                        flyAudioRoom.clearAudio();

                        Activity activity = CloseAllActivity.getInstance().getTopActivity();
                        Intent intent = new Intent(activity, GroupChatTopicActivity.class);
                        intent.putExtra("id", playGroup.getChatGroupID() + "");
                        intent.putExtra("isChat", true);
                        intent.putExtra("fromUserId", String.valueOf(uid));
                        activity.startActivity(intent);
                    }
                });
            }

        } else {
            rlSkillChatBarStatus.setVisibility(View.GONE);

        }
    }

    @Override
    public void setSkillList(List<SkillBean> skill) {
        //技能展示列表需要的数据
        if (skill == null) {
            return;
        }
        if (skill.isEmpty()) {
            return;
        }
        List<Integer> skillIds = new ArrayList<Integer>();
        List<Integer> levels = new ArrayList<Integer>();
        for (int i = 0; i < skill.size(); i++) {
            int id = skill.get(i).getSkillID();
            if (id >= 1 && id <= 5) {
                skillIds.add(id);
                levels.add(skill.get(i).getSkillLevel());
            }
        }
        for (int i = 0; i < 5; i++) {
            boolean find = false;
            for (int j = 0; j < skillIds.size(); j++) {
                int id = skillIds.get(j);
                if ((id - 1) == i) {
                    find = true;
                }
            }
            if (false == find) {
                skillIds.add(i + 1);
                levels.add(0);
            }

        }
        if (skillIds != null && skillIds.size() > 0) {
            //前面2个技能
            if (skillIds.size() >= 1) {
                rlSkillFirstLeft.setBackgroundResource(iconsbg[skillIds.get(0) - 1]);
                rbvSkillLeft.setStarView(levels.get(0));
                ivSkillLeftMain.setImageResource(icons[skillIds.get(0) - 1]);
            }
            if (skillIds.size() >= 2) {
                rlSkillFirstRight.setBackgroundResource(iconsbg[skillIds.get(1) - 1]);
                rbvSkillRight.setStarView(levels.get(1));
                ivSkillRightMain.setImageResource(icons[skillIds.get(1) - 1]);
            }

            //后面3个技能
            int levelThird = 0;
            int levelFourth = 0;
            int levelFifth = 0;

            int levetThirdSkillID = -1;
            int levelFourthSkillId = -1;
            int levelFifthSkillId = -1;

            for (int j = 2; j < skillIds.size(); j++) {
                switch (j) {
                    case 2:
                        levelThird = levels.get(2);
                        levetThirdSkillID = skillIds.get(2);
                        break;
                    case 3:
                        levelFourth = levels.get(3);
                        levelFourthSkillId = skillIds.get(3);
                        break;
                    case 4:
                        levelFifth = levels.get(4);
                        levelFifthSkillId = skillIds.get(4);
                        break;
                }
            }
            mExpandLayout.initShowData(levelThird, levetThirdSkillID - 1, levelFourth, levelFourthSkillId - 1, levelFifth, levelFifthSkillId - 1);
        }
    }

    @Override
    public void setRankingTitle(Item item) {
        if (item != null) {
            userTitleView.setTitleText(item);
        }
    }

    @Override
    public void setVoiceUserType(int voiceUserType) {
        if (voiceUserType == 1) {
            //如果是语音主播
            tvMakeOrder.setText(R.string.audio_chat);
            if (!tvMakeOrder.isShown()) {
                tvMakeOrder.setVisibility(View.VISIBLE);
                tvMakeOrder.setOnClickListener(this);
            }
        } else {
            tvMakeOrder.setVisibility(View.GONE);
        }
    }

    //设置已认证游戏列表
    @Override
    public void setGameList(ArrayList<UserInfoGameInfoBean> beans) {
        if (beans != null && beans.size() > 0) {
            mgvGameList.setVisibility(View.VISIBLE);
            tvMakeOrder.setVisibility(View.VISIBLE);
            tvMakeOrder.setOnClickListener(this);

            mgvGameList.setAdapter(new OtherInfoGameListAdapter(this, beans));
            mgvGameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    UserInfoGameInfoBean bean = (UserInfoGameInfoBean) parent.getAdapter().getItem(position);
                    if (bean != null) {
                        // 新版语聊gameID 为13
                        if (Constants.AUDIO_CHAT_GAME_ID == bean.game_id) {
                            Intent intentAudio = new Intent(OtherInfoActivity.this, AudioDetailsActivity.class);
                            intentAudio.putExtra("UserId", bean.uid);
                            startActivity(intentAudio);
                        } else {
                            InnerJump.JumpGamerDetail(mContext, bean.game_id, bean.uid);
                        }
                    }
                }
            });
        }
    }

    public static void startAction(Context context, long uid) {
        Intent intent = new Intent(context, OtherInfoActivity.class);
        intent.putExtra(Constants.UID, uid);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
            case R.id.fl_left:
                onBackFinsh();
                break;
            case R.id.iv_right:
                moreSetting();
                break;
            case R.id.ll_user_action://动态
                if (presenter.getData() != null) {
                    if (tempuser == null) {
                        tempuser = new User();
                    }
                    tempuser.setIcon(presenter.getData().getHeadPic().get(0));
                    tempuser.setAge(presenter.getData().getAge());
                    tempuser.setNickname(presenter.getData().getNickname());
                    tempuser.setLastLoginTime(presenter.getData().getLogouttime());
                    tempuser.setSVip(presenter.getData().getSvip());
                    tempuser.setDistance((int) presenter.getData().getDistance());
                    tempuser.setUid(uid);
                    if ("m".equals(presenter.getData().getGender())) {
                        tempuser.setSex(1);
                    } else {
                        tempuser.setSex(2);
                    }
                    PersonalDynamicActivity.skipToPersonalDynamicActivity(this, this, tempuser, 0);
                }
                break;
            case R.id.iv_chat_chat://聊天
            case R.id.rl_chat_chat://聊天
                if (presenter.getData() != null) {
                    if (tempuser == null) {
                        tempuser = new User();
                    }
                    String name = presenter.getData().getNickname();
                    tempuser.setUid(uid);
                    tempuser.setNoteName(presenter.getData().getNotes());//YC
                    tempuser.setNickname(name);
                    tempuser.setRelationship(presenter.getData().getRelation());
                    tempuser.setSign(presenter.getData().getMoodtext());
                    tempuser.setLat((int) presenter.getData().getLocation().getLat());
                    tempuser.setLng((int) presenter.getData().getLocation().getLng());
                    if (presenter.getData().getGroups() != null) {
                        ArrayList<net.iaround.ui.datamodel.Group> gs = new ArrayList<net.iaround.ui.datamodel.Group>();
                        for (int i = 0; i < presenter.getData().getGroups().size(); i++) {
                            UserInfoEntity.Data.GroupBean gb = presenter.getData().getGroups().get(0);
                            net.iaround.ui.datamodel.Group group = new net.iaround.ui.datamodel.Group();
                            group.id = String.valueOf(gb.getId());
                            group.icon = gb.getIcon();
                            group.name = gb.getName();
                            gs.add(group);
                        }
                        tempuser.setGroups(gs);
                    }
                    if (presenter.getData().getPlayGroup() != null) {
                        net.iaround.ui.datamodel.Group group = new net.iaround.ui.datamodel.Group();
                        UserInfoEntity.Data.PlayGroupBean pg = presenter.getData().getPlayGroup();
                        group.id = String.valueOf(pg.getChatGroupID());
                        group.icon = pg.getChatGroupIcon();
                        group.name = pg.getChatGroupName();
                        tempuser.setPlayGroup(group);
                    }
                    if (presenter.getData().getHeadPic() != null && presenter.getData().getHeadPic().size() > 0) {
                        tempuser.setIcon(presenter.getData().getHeadPic().get(0));
                    }

                    //从聊吧呼起个人资料页面
                    ChatPersonal.skipToChatPersonal(getActivity(), tempuser, 201, false, ChatFromType.UNKONW, false, fromChatBar);
                }
                break;
            case R.id.iv_chat_attention:
            case R.id.rl_chat_attention:

                if (presenter.getData() != null) {
                    if (tempuser == null)
                        tempuser = new User();
                    String name = "";
                    if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNotes())) {
                        name = presenter.getData().getNotes();
                    } else if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNickname())) {
                        name = presenter.getData().getNickname();
                    } else {
                        name = String.valueOf(uid);
                    }
                    tempuser.setUid(uid);
                    tempuser.setNickname(name);
                    tempuser.setRelationship(presenter.getData().getRelation());

                    if (ivLike.isSelected()) {
                        //cancle
                        presenter.updateRelation(OtherInfoActivity.this, 1, tempuser, Constants.SECRET_SET_HADLE_TYPE_DELETE);
                    } else {
                        //add
                        presenter.updateRelation(OtherInfoActivity.this, 2, tempuser, Constants.SECRET_SET_HADLE_TYPE_ADD);
                    }

                }
                break;
            case R.id.ll_user_vip:
                Intent intent = new Intent(OtherInfoActivity.this, UserVipOpenActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_user_last_local:
                MapUtils.showOnePositionMap(OtherInfoActivity.this, MapUtils.LOAD_TYPE_NEAR_FRIEND, (int) lat, (int) lng, address, "");
                break;
            case R.id.ll_user_level:
            case R.id.ll_user_level_info:
                String url = Config.getlevelDescUrl(CommonFunction.getLang(OtherInfoActivity.this));
                Intent i = new Intent(OtherInfoActivity.this, WebViewAvtivity.class);
                i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
                startActivity(i);
                break;
            case R.id.iv_user_gift:
            case R.id.ll_user_gift:
            case R.id.ll_user_gift_intenter:
            case R.id.ll_other_info_gift:
                //新加跳转到收到礼物页面
                if (gifts != null) {
                    if (tempuser != null) {
                        tempuser.setUid(uid);
                        StoreReceiveGiftActivity.launchMineGiftToLook(mContext, tempuser);
                    }
                }
                break;
            case R.id.iv_send_gift:
            case R.id.rl_chat_gift:
            case R.id.tv_user_gift_empty_tips:
                if (presenter.getData() != null) {
                    if (tempuser == null)
                        tempuser = new User();
                    String name = "";
                    if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNotes())) {
                        name = presenter.getData().getNotes();
                    } else if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNickname())) {
                        name = presenter.getData().getNickname();
                    } else {
                        name = String.valueOf(uid);
                    }
                    tempuser.setUid(uid);
                    tempuser.setNickname(name);
                    tempuser.setRelationship(presenter.getData().getRelation());

                    //是否从聊吧呼起个人资料页面
                    ChatPersonal.skipToChatPersonal(getActivity(), tempuser, 201, false, ChatFromType.OTHERGIVEGIFT, false, fromChatBar);
                }

                break;
            case R.id.ll_user_secret:
                if (presenter.getData() != null) {
                    if (tempuser == null)
                        tempuser = new User();
                    String name = "";
                    if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNotes())) {
                        name = presenter.getData().getNotes();
                    } else if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNickname())) {
                        name = presenter.getData().getNickname();
                    } else {
                        name = String.valueOf(uid);
                    }
                    tempuser.setUid(uid);
                    tempuser.setNickname(name);
                    tempuser.setRelationship(presenter.getData().getRelation());
                    if (tempuser.isMyFriend() || Common.getInstance().loginUser.isVip() || Common.getInstance().loginUser.isSVip()) {
                        Intent secretIntent = new Intent(this, SecretActivity.class);
                        secretIntent.putExtra(Constants.SECRET, secret);
                        secretIntent.putExtra(SecretActivity.KEY_IS_SECRET, false);
                        startActivity(secretIntent);
                    } else {
                        DialogUtil.showTobeVipDialog(this,
                                R.string.tips_vip,
                                R.string.content_open_vip, DataTag.VIEW_near_filtrate);

                    }
                }


                break;
            case R.id.iv_center:
            case R.id.ll_skill_center:
                mExpandLayout.toggleExpand(ivSkillShowMore);
                break;
            case R.id.tv_show_chat_bar_name:
                break;
            case R.id.rl_chat_skill:
                //打开技能使用面板
                Bundle bundle = new Bundle();
                bundle.putString("targetUserId", uid + "");
                SkillUseDialogFragment skillUseDialogFragment = SkillUseDialogFragment.getInstance(bundle);
                new SkillUsePresenter(skillUseDialogFragment);
                FragmentManager manager = ((OtherInfoActivity) mContext).getFragmentManager();
                skillUseDialogFragment.show(manager, "skillUseDialogFragment");

                break;

            case R.id.rl_other_info_video:

                Intent intentVideo = new Intent(OtherInfoActivity.this, VideoDetailsActivity.class);
                intentVideo.putExtra(VideoDetailsActivity.KEY_VIDEO_UID, uid);
                startActivity(intentVideo);
                break;
            case R.id.tv_make_order:
                if (tvMakeOrder.getText().toString().equals(getString(R.string.audio_chat))) {
                    Intent intentAudio = new Intent(this, AudioDetailsActivity.class);
                    intentAudio.putExtra("UserId", uid);
                    startActivity(intentAudio);
                } else {
                    InnerJump.JumpMakeGameOrder(mContext, uid);
                }
                break;

        }
    }

    private void moreSetting() {
        if (!NotLoginDialog.getInstance(getActivity()).isLogin()) {
            return;
        }

        settingDialog.show();
        settingDialog.init(DynamicDeatailsSettingDialog.OTHERINFO_FOCUS);
        settingDialog.setBlackList(BLACKLIST_PERSON);
        settingDialog.setMyDynamic(DYNAMIC_REJECT_HIM);
        settingDialog.setSeeHimDynamic(DYNAMIC_NO_SEE_HIM);
        settingDialog.setItemOnclick(new DynamicDeatailsSettingDialog.ItemOnclick() {
            @Override
            public void itemOnclick(View view) {
                if (view.getTag().equals("other_notice")) {
                    // 不看ta动态
                    if (DYNAMIC_NO_SEE_HIM == 0) {
                        //不看他的动态
                        UPDATE_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, uid, 3, "n", new HttpCallBackImpl(OtherInfoActivity.this));
                    } else {
                        //可以看他动态
                        UPDATE_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, uid, 3, "y", new HttpCallBackImpl(OtherInfoActivity.this));
                    }
                } else if (view.getTag().equals("mine_notice")) {
                    //不让让看我动态
                    if (DYNAMIC_REJECT_HIM == 0) {
                        //不让他我我动态
                        UPDATE_INVISIBILITY_SETTING_REJECT_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, uid, 2, "n", new HttpCallBackImpl(OtherInfoActivity.this));
                    } else {
                        //让他看我的动态
                        UPDATE_INVISIBILITY_SETTING_REJECT_FLAG = BusinessHttpProtocol.updateInvisibilitySettingInfo(mContext, uid, 2, "y", new HttpCallBackImpl(OtherInfoActivity.this));
                    }

                } else if (view.getTag().equals("report_user")) {
                    DialogUtil.showOKCancelDialog(OtherInfoActivity.this, getString(R.string.dialog_title), getString(R.string.chat_setting_report), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            presenter.report(uid, 0);
                        }
                    });
                } else if (view.getTag().equals("other_blacklist")) {
                    //黑名单
                    if (BLACKLIST_PERSON == 0) {
                        //添加黑名单
                        addBlackList();
                    } else {
                        //移除黑名单
                        deleteBlackList();
                    }
                } else if (view.getTag().equals("other_remarks")) {
                    if (tempuser == null)
                        tempuser = new User();
                    if (presenter.getData() != null) {
                        tempuser.setRelationship(presenter.getData().getRelation());
                    }
                    if (tempuser.getRelationship() == User.RELATION_FOLLOWING || tempuser.getRelationship() == User.RELATION_FRIEND) {
                        remarkDialog.setTargetuserid(uid);
                        remarkDialog.show();
                    } else {
                        Toast.makeText(OtherInfoActivity.this, getResString(R.string.other_info_no_friend_remarks), Toast.LENGTH_SHORT).show();
                    }
                }//jiqiang
            }
        });

    }

    @Override
    public void onScrollChanged(YusScrollView scrollView, int x, int y, int oldx, int oldy) {
        int llHeaderHeight = mRlHeader.getHeight();
        int vpHeaderHeight = vpHeader.getHeight();
        if (y <= 0) {
            mRlHeader.setBackgroundColor(Color.TRANSPARENT);
        } else if (y < vpHeaderHeight - llHeaderHeight) {
            float scale = (float) y / (float) vpHeaderHeight;
            float alpha = 255 * scale;
            //先设置一个背景，然后在让背景乘以透明度
            mRlHeader.setBackgroundColor(getResources().getColor(R.color.common_white));
            ivLeft.setImageResource(R.drawable.other_info_back);
            tvTitleNickname.setVisibility(View.INVISIBLE);
            ivRight.setImageResource(R.drawable.new_other_info_more);
            mRlHeader.getBackground().setAlpha((int) alpha);
        } else if (y >= vpHeaderHeight - llHeaderHeight) {
            ivLeft.setImageResource(R.drawable.title_back);
            tvTitleNickname.setVisibility(View.VISIBLE);
            ivRight.setImageResource(R.drawable.new_dynamic_details_more);
            mRlHeader.setBackgroundColor(getResources().getColor(R.color.common_white));
        }
    }

    private View.OnClickListener reportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int type = ReportType.OTHER;
            switch ((int) view.getTag()) {
                case 0:// 色情
                    type = ReportType.SEX;
                    break;
                case 1:// 广告
                    type = ReportType.ADVERTISEMENT;
                    break;
                case 2:// 骚扰
                    type = ReportType.DISTURB;
                    break;
                case 3:// 欺诈
                    type = ReportType.REACTIONARY;
                    break;
            }
            reportDialog.hide();
            presenter.report(uid, type);

        }
    };

    public void readJson() {
        /*获取到assets文件下的TExt.json文件的数据，并以输出流形式返回。*/
        InputStream is = OtherInfoActivity.this.getClass().getClassLoader().getResourceAsStream("assets/" + "friendone.json");
        InputStreamReader streamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(streamReader);
        String line;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = reader.readLine()) != null) {
                // stringBuilder.append(line);
                stringBuilder.append(line);
            }
            reader.close();
            reader.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Toast.makeText(MainFragmentActivity.this, stringBuilder.toString(), Toast.LENGTH_LONG).show();
        Log.e("-------json", stringBuilder.toString());
        me = loginData(OtherInfoActivity.this, stringBuilder);
    }

    public void loginSuccess(Context context) {
        me = new Me();
        if (servertime == 0) {
            servertime = System.currentTimeMillis();
        }
        Common.getInstance().serverToClientTime = servertime - System.currentTimeMillis();
//        ConnectorManage.getInstance(context).setKey(key);
        SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(context);

//        setUrl( );  //登录的时候所有的URL 赋值

        if (user != null) {
            me.setUid(user.userid);
            me.setNickname(user.nickname);
            me.setIcon(user.icon);
            me.setViplevel(user.viplevel);
            me.setSVip(user.svip);
            String gender = user.gender;
            me.setSex("m".equals(gender) ? 1 : ("f".equals(gender) ? 2 : 0));
            me.setAge(user.age);

            shareDate.putString(SharedPreferenceUtil.USERID, String.valueOf(me.getUid()));
        }

//        me.setEmail(email);
//
//        me.setWithWho(withwho);
//
//        Common.getInstance().isShowGameCenter = opengame == 0 ? false
//                : true;

        // me.setCompleteRate( ( Integer ) resMap.get( "complrate" ) );
        // me.setBasicRate( ( Integer ) resMap.get( "basicrate" ) );
        // me.setSecretRate( ( Integer ) resMap.get( "secretrate" ) );
        // me.setWeiboRate( ( Integer ) resMap.get( "weiborate" ) );
        me.setInfoTotal(total);

        me.setInfoComplete(complete);

        // 是否首次登录
        me.setFirstLogin("y".equals(firstlogin));
        // 是否绑定帐号
        me.setBind("y".equals(isbind));
        // 登录toket,用于推送
        shareDate.putString(SharedPreferenceUtil.TOKEN, token);

        // 第三方信息（用户身份token、用户ID、token过期时间）
        if (!TextUtils.isEmpty(accesstoken)) {
            me.setAccesstoken(accesstoken);
            me.setOpenid(openid);
            me.setExpires(expires);
        }

//        UmengUtils.enable = openstatics == 1;//jiqiang

        // 登录时候判断是否显示手机验证，针对4.2国家政策不稳定原因
        shareDate.putString(SharedPreferenceUtil.NEWONOFFS, String.valueOf(newonoffs));

        //		shareDate.putInt( SharedPreferenceUtil.ONOFFS, String.valueOf(onoffs ));

        shareDate.putString(SharedPreferenceUtil.SWITCH, String.valueOf(switchs));


    }

    public Me loginData(Context context, StringBuilder jsonString) {
        user = GsonUtil.getInstance()
                .getServerBean(jsonString.toString(), BaseUserInfo.class);

        loginSuccess(context);

        return me;
    }

    public void initParams() {
        tempuser = (User) getIntent().getExtras().getSerializable("user");
        if (tempuser != null)
            uid = tempuser.getUid();
        uid = getIntent().getLongExtra("uid", 0);
        fromChatBar = getIntent().getBooleanExtra("fromchatbar", false);
        gotoChatbarId = getIntent().getStringExtra("chatbarid");
    }

    static class HttpCallBackImpl implements HttpCallBack {
        private WeakReference<OtherInfoActivity> mActivity;

        public HttpCallBackImpl(OtherInfoActivity activity) {
            mActivity = new WeakReference<OtherInfoActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            OtherInfoActivity activity = mActivity.get();
            if (activity != null) {
                activity.onGeneralSuccess(result, flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            OtherInfoActivity activity = mActivity.get();
            if (activity != null) {
                activity.onGeneralError(e, flag);
            }
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (Constant.isSuccess(result)) {
            if (GET_INVISIBILITY_SETTING_FLAG == flag) {
                option = GsonUtil.getInstance().getServerBean(result, InvisibilitySettingOption.class);
                if (option != null) {
                    refreshMoreView();
                } else {
                    ErrorCode.showError(OtherInfoActivity.this, result);
                }
            } else if (UPDATE_BLACK_LIST_FLAG == flag) {//blacklist

                JSONObject json = null;
                try {
                    json = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                int error = json.optInt("error");
                int status = json.optInt("status");
                long devil = json.optLong("devilId");
                String problem = json.optString("errordesc");
                if (status == 200) {
                    if (BLACKLIST_PERSON == 0) {
                        devilId = devil;
                        BLACKLIST_PERSON = 1;
                        Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_add_black_success), Toast.LENGTH_LONG).show();
                    } else {
                        devilId = 0;
                        BLACKLIST_PERSON = 0;
                        Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_cancle_black_failure), Toast.LENGTH_LONG).show();
                    }
                    settingDialog.setBlackList(BLACKLIST_PERSON);

                } else {
                    ErrorCode.showError(OtherInfoActivity.this, result);

                }
            } else if (UPDATE_INVISIBILITY_SETTING_FLAG == flag) {//seehim
                BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                if (bean.isSuccess()) {
                    if (DYNAMIC_NO_SEE_HIM == 0) {
                        DYNAMIC_NO_SEE_HIM = 1;
                    } else {
                        DYNAMIC_NO_SEE_HIM = 0;
                    }
                    Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_modify_action_success), Toast.LENGTH_LONG).show();
                    settingDialog.setSeeHimDynamic(DYNAMIC_NO_SEE_HIM);
                } else {
                    ErrorCode.showError(OtherInfoActivity.this, result);
                    Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_modify_action_failure), Toast.LENGTH_LONG).show();
                }

            } else if (UPDATE_INVISIBILITY_SETTING_REJECT_FLAG == flag) {//reject him
                BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                if (bean.isSuccess()) {
                    if (DYNAMIC_REJECT_HIM == 0) {
                        DYNAMIC_REJECT_HIM = 1;
                    } else {
                        DYNAMIC_REJECT_HIM = 0;
                    }
                    Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_modify_action_success), Toast.LENGTH_LONG).show();
                    settingDialog.setMyDynamic(DYNAMIC_REJECT_HIM);
                } else {
                    ErrorCode.showError(OtherInfoActivity.this, result);
                    Toast.makeText(OtherInfoActivity.this, getResources().getString(R.string.other_info_modify_action_failure), Toast.LENGTH_LONG).show();
                }
            } else if (GET_USER_RELATION_LINK_FLAG == flag) {

                UserRelationLink link = GsonUtil.getInstance()
                        .getServerBean(result, UserRelationLink.class);
                tempuser.setRelationLink(link);
            } else {

            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    public void reqMoreData() {
        GET_INVISIBILITY_SETTING_FLAG = BusinessHttpProtocol.getInvisibilitySettingInfo(mContext, uid, new HttpCallBackImpl(this));
    }

    /**
     * 更新点击更多对话框的显示参数
     */
    public void refreshMoreView() {
        if (option.showmydynamic != null && option.showmydynamic != "") {
            //y看到我的动态n看不到
            if ("y".equals(option.showmydynamic)) {
                DYNAMIC_REJECT_HIM = 0;
            } else {
                DYNAMIC_REJECT_HIM = 1;
            }

        }
        if (option.showdynamic != null && option.showdynamic != "") {
            //y看他（她）的动态，n不看他（她）的动态
            if ("y".equals(option.showdynamic)) {
                DYNAMIC_NO_SEE_HIM = 0;
            } else {
                DYNAMIC_NO_SEE_HIM = 1;
            }
        }
        if (option.blacklisted != null && option.blacklisted != "") {
            //y已列入黑名单，n未列入黑名单
            devilId = option.devilId;
            if ("n".equals(option.blacklisted)) {
                BLACKLIST_PERSON = 0;
            } else {
                BLACKLIST_PERSON = 1;
            }
        }
    }

    // 拉黑
    private void addBlackList() {
        String msg = String.format(getString(R.string.add_black_list_comf),
                tempuser == null ? "" : tempuser.getNickname());
        DialogUtil.showOKCancelDialog(this, getString(R.string.add_black), msg,
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            UPDATE_BLACK_LIST_FLAG = UserHttpProtocol.userDevilAdd(mContext, uid, new HttpCallBackImpl(OtherInfoActivity.this));
                        } catch (Throwable e) {

                            e.printStackTrace();
                            Toast.makeText(OtherInfoActivity.this, R.string.network_req_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void deleteBlackList() {
        UPDATE_BLACK_LIST_FLAG = UserHttpProtocol.userDevilDel(mContext, devilId, new HttpCallBackImpl(this));
    }

    private void showProgressDialogue() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(this, R.string.prompt,
                    R.string.please_wait, null);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialogue() {
        if (mProgressDialog != null
                && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onActivityDestroy();
        presenter = null;
        BLACKLIST_PERSON = 0;
        DYNAMIC_REJECT_HIM = 0;
        DYNAMIC_NO_SEE_HIM = 0;
        REPORT_PERSON = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {

            canJumpPage = true;
            lastClickTime = 0;
            switch (requestCode) {
                case EDIT_SPACE_MODIFY_PHOTO:
                    //updatePhoto( );//jiqiang
                    break;
                case SuperChat.HandleMsgCode.MSG_SEND_GIFT_REQUEST:
                    //ChatPersonal.skipToChatPersonal(getActivity(), tempuser, 201);
                    //是否从聊吧呼起个人资料页面
                    ChatPersonal.skipToChatPersonal(getActivity(), tempuser, 201, false, ChatFromType.UNKONW, false, fromChatBar);
                    break;

            }
        } else {
            if (requestCode == 201) {
                isHeaderViewPage = false;
                initDatas();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            onBackFinsh();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 回调信息
     */
    private void onBackFinsh() {
        CommonFunction.log("OtherInfoActivity", "onBackFinsh() into");
        boolean chatSet = getIntent().getBooleanExtra("chat_set", false);
        if (chatSet) {
            if (presenter.getData() != null) {
                if (tempuser == null)
                    tempuser = new User();
                String name = "";
                if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNotes())) {
                    name = presenter.getData().getNotes();
                } else if (!CommonFunction.isEmptyOrNullStr(presenter.getData().getNickname())) {
                    name = presenter.getData().getNickname();
                } else {
                    name = String.valueOf(uid);
                }
                tempuser.setUid(uid);
                tempuser.setNickname(name);
                tempuser.setRelationship(presenter.getData().getRelation());
            }
            Intent data = new Intent();
            data.putExtra("user", tempuser);
            setResult(Activity.RESULT_OK, data);
        }
        finish();
        CommonFunction.log("OtherInfoActivity", "onBackFinsh() out");
    }
}
