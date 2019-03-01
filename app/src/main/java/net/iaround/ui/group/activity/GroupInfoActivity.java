
package net.iaround.ui.group.activity;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.SendWorldMessageProtocol;
import net.iaround.database.GroupSharePrefrenceUtil;
import net.iaround.entity.type.ChatFromType;
import net.iaround.eventbus.FirstEvent;
import net.iaround.model.chat.WorldMessageRecruitContent;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.ChatbarFansActivity;
import net.iaround.ui.activity.ChatbarInviteActivity;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.GroupInfoScrollView;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.ChatBarFamilyFragment;
import net.iaround.ui.group.GroupScaleType;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.GroupInfoBean;
import net.iaround.ui.group.bean.GroupInfoForRelation;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.group.bean.JoinGroupBean;
import net.iaround.ui.map.MapUtils;
import net.iaround.ui.space.SpaceTextClickListener;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.dialog.RecruitAndCallDialog;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.utils.ImageViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GroupInfoActivity
 * @Description: 圈资料界面
 * @date 2013-12-4 下午4:45:44
 */
public class GroupInfoActivity extends GroupHandleActivity implements OnClickListener {

    // {$ 控件声明

    private GroupInfoScrollView scrollView;

    /**
     * 底部弹出popupmenu
     */
    private CustomContextDialog customContextDialog;
    /**
     * 圈子图片
     */
    private ImageView groupImage;

    private ImageView groupTag;
    /**
     * 加入圈子
     */
    private RelativeLayout rlLayoutBtn;
    private TextView btnJoinGroup;
    private ImageView mIvJoinGroup;
    /**召唤功能**/
    private RelativeLayout rlLayoutBtn_1;
    /**招募广播**/
    private RelativeLayout rlLayoutBtn1;
    /**邀请聊天**/
    private RelativeLayout rlLayoutBtn2;
    /**欢迎语**/
    private TextView tvWelcomeHint;
    /**关注**/
    private LinearLayout rlAttentionLayout;
    /**关注人数**/
    private RelativeLayout llAttentionLayout1;
    private TextView tvAttentionNum,tvAttentionNum1;

    /**所在地**/
    private TextView tvPosition;
    /**邀请新成员*/
    private RelativeLayout rlInviteNewMember;
    private TextView tvInviteNewMember;
    /**
     * 最大显示的成员个数
     */
    private int MAX_SHOW_MEMBER_NUM = 5;

    /**
     * 成员个数
     */
    private TextView memberNumView;
    /**
     * 成员列表layout
     */
    private RelativeLayout memberListView;
//	private LinearLayout memberListView;
    /**
     * 成员列表头像
     */
    private HeadPhotoView[] userIconViewList;
    private HeadPhotoView userIconView0;
    private HeadPhotoView userIconView1;
    private HeadPhotoView userIconView2;
    private HeadPhotoView userIconView3;
    private HeadPhotoView userIconView4;

    /**
     * 管理员
     */
    private ImageView[] adminLogoList;
    private ImageView adminLogoView0;
    private ImageView adminLogoView1;
    private ImageView adminLogoView2;
    private ImageView adminLogoView3;
    private ImageView adminLogoView4;

    /**
     * 分类
     */
    private TextView groupType;
    /**
     * 圈子号
     */
    private TextView groupId;
    /**
     * 圈中心
     */
    private TextView groupCenter;
    /**
     * 查看圈中心位置
     */
    private RelativeLayout btnViewLocation;
    /**
     * 加入条件
     */
    private TextView groupJoinCondition;
    /**
     * 圈主
     */
    private TextView groupOwner;
    /**
     * 查看圈主资料
     */
//	private LinearLayout btnViewOwner;
    private RelativeLayout btnViewOwner;
    /**
     * 圈介绍
     */
    private TextView groupIntroduce;
    /**
     * 圈状态
     */
    private TextView groupStatus;

    /**
     * 创建日期
     */
    private TextView groupCreateDate;
    /**
     * 圈等级
     */
    private TextView groupLevel;
    private RelativeLayout btnViewLevel;

    //标题栏
    /**
     * 返回按钮
     */
    private FrameLayout flLeft;
    private ImageView btnTitleBack;
    /**
     * 更多按钮
     */
    private ImageView btnTitleMore;
    private LinearLayout llRight;
    /**
     * 标题，圈名
     */
    private TextView textTitle;


    /**
     * 加载框
     */
    private Dialog waitDialog;

    /**
     * 操作项
     **/
    /* 圈聚会 */
    private RelativeLayout layoutViewGatherings;
    /* 圈推荐 */
    private RelativeLayout layoutViewRecommend;
    /* 分享圈子 */
    private RelativeLayout layoutViewShare;
    /* 清除聊天记录 */
    private RelativeLayout layoutViewClearChat;
    /* 圈消息设置 */
    private RelativeLayout layoutViewGroupMSGSetting;
    /*圈状态*/
    private RelativeLayout layoutViewGroupState;
    /*圈主帮助*/
    private RelativeLayout layoutViewGroupHelp;

    private TextView groupPostbarselect;
    private ImageView groupTypeIcon;
    private TextView groupMSGsetting;

    /**
     * 是否第一次加载
     */
    private boolean isFirstInit = false;

    /**
     * 圈子id
     */
    public static final String GROUPID = "groupid";
    private String mGroupId;
    /**
     * 游戏事件上报中的查看游戏来源
     */
    private int gameFrom;
    /**
     * 从游戏跳转时的ID
     */
    private long gameID;
    /**
     * 当前登陆者id
     */
    public static final String CREATEID = "createid";
    private long createId;

    /**
     * 创建者id
     */
    private long creatorId;
    /**
     * 是否从搜索进来
     */
    public static final String FROMSEARCH = "fromsearch";
    private boolean mIsFromSearch = false;
    /**
     * 创建者头像
     */
    public static final String CREATEORICONURL = "creatoriconurl";
    private String mCreatorIcon;
    /**
     * 是否加入
     */
    public static final String ISJOIN = "isjoin";
    private boolean mIsJoin = false;

    /**
     * 圈子信息
     */
    private GroupInfoBean mGroupInfoBean;
    private static GroupInfoBean GroupInfo;

    /**
     * 圈成员列表的bean
     */
    private List<GroupSearchUser> mMemberListBean;

    /**
     * 当前用户在圈子的角色，默认为游客
     */
    private int mUserRole = 3;// 3 为游客  2 成员  1 管理员 0 吧主

    /**
     * 修改圈资料后是否需要刷新( 1为需要，0为不需要 )
     */
    private int hasEdit = 0;

    /**
     * 是否进入聊天页面
     */
    private boolean isChat;
    /**
     * 是否进入圈话题页面
     */
    private boolean isViewTopic = false;

    /**
     * 请求圈资料的flag
     */
    private long FLAG_GET_GROUP_INFO;
    /**
     * 關注聊吧flag
     */
    private long FLAG_FOLLOW_CHATBAR;
    /**
     * 取消關注聊吧flag
     */
    private long FLAG_CANCEL_FOLLOW_CHATBAR;

    /**
     * 加入圈子
     */
    private long FLAG_JOIN_GROUP;

    /**
     * 带验证加入圈子
     */
    private long FLAG_JOIN_GROUP_WITH_CHECK;

    /**
     * 解散圈子
     */
    private long FLAG_DISBANDED_GROUP;

    /**
     * 退出聊天室
     */
    private long FLAG_EXIT_GROUP;

    /**
     * 圈管理
     */
    private final int MENU_GROUP_MANAGER = 1;
    /**
     * 清除聊天记录
     */
    private final int MENU_CLEAR_HISTORY = 2;
    /**
     * 解散圈子
     */
    private final int MENU_DISBANDED_GROUP = 3;
    /**
     * 退出圈子
     */
    private final int MENU_EXIT_GROUP = 4;
    /**
     * 举报圈子
     */
    private final int MENU_REPORT_GROUP = 5;
    /**
     * 取消
     */
    private final int MENU_CANCEL = 6;

    /**
     * 圈子转让
     */
    private final int MENU_TRANSFER_GROUP = 8;

    /**
     * 圈成员管理
     */
    private final int REQUEST_CODE_GROUP_MEMBER = 10;

    /**
     * 修改圈资料
     */
    private final int REQUEST_CODE_GROUP_MANAGER = 11;

    /**
     * 成功创建圈子
     */
    private final int REQUEST_CODE_SHARE_CREATE_GROUP = 12;
    /**
     * 圈子升级
     */
    private final int REQUEST_CODE_SHARE_UPGRADE_GROUP = 13;
    /**
     * 进入话题列表
     **/
    private final int REQUEST_CODE_TOPIC_LIST_GROUP = 14;
    /**
     * 圈消息设置
     **/
    private final int REQUEST_CODE_MSG_SETTING_GROUP = 15;

    public final static int REQEST_CODE_MSG_POST_BAR_SELECT = 16;

    /**
     * 发送举报请求码
     */
    public final static int REQUEST_CODE_REPORT = 18;
    /**
     * 发送招募广播的flag
     */
    public long sendRecruitAndCallFlag;
    /**
     * 吧主发送召唤的flag
     */
    public long sendCallbackFlag;

    public static final String GROUP_ROLE = "group_role";

    public static final String CLEARALL = "clearAll";
    private boolean mIsClearAllMessages = false;// 是否清空圈聊消息
    private View divider_introduce,mView;
    private TextView tvGroupInfoHotNum;//圈子热度

    private static final String DYNAMICACTION = "com.byread.dynamic";
    private Dialog dialog;


    SpannableString groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        EventBus.getDefault().register(GroupInfoActivity.this);

        mMemberListBean = new ArrayList<>();

        mGroupId = getIntent().getStringExtra(GROUPID);
        gameFrom = getIntent().getIntExtra("from", -1);
        gameID = getIntent().getLongExtra("gameID", -1);
        createId = getIntent().getLongExtra(CREATEID, 0);
        mCreatorIcon = getIntent().getStringExtra(CREATEORICONURL);
        mIsFromSearch = getIntent().getBooleanExtra(FROMSEARCH, false);
        mIsJoin = getIntent().getBooleanExtra(ISJOIN, false);

        initActionBar();
        initViews();
        setListeners();
        initData();
    }

    /**
     * 初始化标题栏
     */
    private void initActionBar() {
        //初始化标题栏  begin
        this.flLeft = (FrameLayout) findViewById(R.id.fl_left);
        this.btnTitleBack = (ImageView) findViewById(R.id.iv_left);
        this.btnTitleMore = (ImageView) findViewById(R.id.iv_right);
        this.textTitle = (TextView) findViewById(R.id.tv_title);
        llRight = (LinearLayout) findViewById(R.id.fl_right);

        this.btnTitleMore.setVisibility(View.VISIBLE);
        this.btnTitleBack.setVisibility(View.VISIBLE);
        this.textTitle.setVisibility(View.VISIBLE);

        this.textTitle.setText("");
        this.btnTitleBack.setImageResource(R.drawable.title_back);
        this.btnTitleMore.setImageResource(R.drawable.title_more);
        //初始化标题栏 end
    }

    /**
     * @Title: initViews
     * @Description: 初始化控件
     */
    private void initViews() {


        this.scrollView = (GroupInfoScrollView) findViewById(R.id.scroll_view);
        this.groupImage = (ImageView) findViewById(R.id.group_image);
        this.groupTag = (ImageView) findViewById(R.id.imgTag);

        //初始化加入圈子和开始圈聊按钮
        rlLayoutBtn = (RelativeLayout) findViewById(R.id.rl_layout_btn);
        this.btnJoinGroup = (TextView) findViewById(R.id.btn_join_group);
        this.mIvJoinGroup = (ImageView) findViewById(R.id.imag_joinGroup);
        /**新增关注，所在地，招募广播，邀请聊天，召唤功能**/
        rlLayoutBtn1 = (RelativeLayout) findViewById(R.id.rl_layout_btn1);
        rlLayoutBtn_1 = (RelativeLayout) findViewById(R.id.rl_layout_btn_1);
        rlLayoutBtn2 = (RelativeLayout) findViewById(R.id.rl_layout_btn2);
        tvWelcomeHint = (TextView) findViewById(R.id.tv_welcome_hint);
        rlAttentionLayout = (LinearLayout) findViewById(R.id.ll_attention);
        llAttentionLayout1 = (RelativeLayout) findViewById(R.id.ll_attention1);
        tvAttentionNum = (TextView) findViewById(R.id.tv_attention_num);
        tvAttentionNum1 = (TextView) findViewById(R.id.tv_attention_num1);
        tvPosition = (TextView) findViewById(R.id.tv_group_position);

        memberListView = (RelativeLayout) findViewById(R.id.group_member_layout);
        this.memberNumView = (TextView) findViewById(R.id.member_num);
        userIconView0 = (HeadPhotoView) findViewById(R.id.user_icon_0);
        userIconView1 = (HeadPhotoView) findViewById(R.id.user_icon_1);
        userIconView2 = (HeadPhotoView) findViewById(R.id.user_icon_2);
        userIconView3 = (HeadPhotoView) findViewById(R.id.user_icon_3);
        userIconView4 = (HeadPhotoView) findViewById(R.id.user_icon_4);
        userIconViewList = new HeadPhotoView[]
                {userIconView0, userIconView1, userIconView2, userIconView3, userIconView4};
        adminLogoView0 = (ImageView) findViewById(R.id.user_icon_role0);
        adminLogoView1 = (ImageView) findViewById(R.id.user_icon_role1);
        adminLogoView2 = (ImageView) findViewById(R.id.user_icon_role2);
        adminLogoView3 = (ImageView) findViewById(R.id.user_icon_role3);
        adminLogoView4 = (ImageView) findViewById(R.id.user_icon_role4);
        adminLogoList = new ImageView[]
                {adminLogoView0, adminLogoView1, adminLogoView2, adminLogoView3,
                        adminLogoView4};

        groupType = (TextView) findViewById(R.id.group_type);
        groupId = (TextView) findViewById(R.id.group_id);
        groupCenter = (TextView) findViewById(R.id.group_address);
        btnViewLocation = (RelativeLayout) findViewById(R.id.view_group_location);
        groupJoinCondition = (TextView) findViewById(R.id.group_join_condition);
        groupOwner = (TextView) findViewById(R.id.group_owner);
        btnViewOwner = (RelativeLayout) findViewById(R.id.view_group_owner);
        groupIntroduce = (TextView) findViewById(R.id.group_introduce_content);

        groupStatus = (TextView) findViewById(R.id.group_state);//圈子状态

        groupCreateDate = (TextView) findViewById(R.id.group_create_date);
        groupLevel = (TextView) findViewById(R.id.group_level);
        btnViewLevel = (RelativeLayout) findViewById(R.id.view_group_level);

        groupMSGsetting = (TextView) findViewById(R.id.tv_group_inf_msg_set_content); // 圈消息设置

        layoutViewGatherings = (RelativeLayout) findViewById(R.id.view_group_gatherings);//圈聚会 暂时没有使用
        layoutViewRecommend = (RelativeLayout) findViewById(R.id.view_group_recommend);//圈子推荐
        layoutViewShare = (RelativeLayout) findViewById(R.id.view_group_share);//圈子分享  暂时没有使用
        layoutViewClearChat = (RelativeLayout) findViewById(R.id.view_group_clear_chat);//清空聊天记录
        layoutViewGroupMSGSetting = (RelativeLayout) findViewById(R.id.view_group_setting);//圈子设置
        layoutViewGroupState = (RelativeLayout) findViewById(R.id.view_group_state);//圈状态
        layoutViewGroupHelp = (RelativeLayout) findViewById(R.id.view_group_help);//圈主帮助
        mView = findViewById(R.id.last_group_info_divider);//圈等级下的分界线
        divider_introduce = findViewById(R.id.last_group_info_divider);//吧介绍下的分界线


        groupPostbarselect = (TextView) findViewById(R.id.post_bar_select);
        groupTypeIcon = (ImageView) findViewById(R.id.iv_group_type);
        tvGroupInfoHotNum = (TextView) findViewById(R.id.tv_activity_group_info_hot_num);

        rlInviteNewMember = (RelativeLayout) findViewById(R.id.rl_invit_layout);
        tvInviteNewMember = (TextView) findViewById(R.id.member_invite);

        //吧等级和吧状态不可见
        btnViewLevel.setVisibility(View.GONE);
        layoutViewGroupState.setVisibility(View.GONE);

        reSetImageLayoutSize();
    }

    /**
     * grouprole  0 吧主，1管理员 2 成员，3 游客，4 粉丝
     */
    private void initCustomDialog() {

        if (mUserRole >= 0)
        {
            if (mUserRole == 0) {//吧主
                customContextDialog = new CustomContextDialog(this, 2);
            } else if (mUserRole == 1) {//管理员
                customContextDialog = new CustomContextDialog(this, 10);
            } else if (mUserRole == 2) {//成员
                customContextDialog = new CustomContextDialog(this, 3);
            }else if (mUserRole == 4) {//粉丝
                customContextDialog = new CustomContextDialog(this, 30);
            }else//游客
                customContextDialog = new CustomContextDialog(this,31);
            customContextDialog.setListenner(moreOnclickLisener);
            customContextDialog.dismiss();
        }
    }

    private void reSetImageLayoutSize() {
        RelativeLayout layout1 = (RelativeLayout) findViewById(R.id.avatar_layout0);
        RelativeLayout layout2 = (RelativeLayout) findViewById(R.id.avatar_layout1);
        RelativeLayout layout3 = (RelativeLayout) findViewById(R.id.avatar_layout2);
        RelativeLayout layout4 = (RelativeLayout) findViewById(R.id.avatar_layout3);
        RelativeLayout layout5 = (RelativeLayout) findViewById(R.id.avatar_layout4);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) layout1.getLayoutParams();
        int screenWith = CommonFunction.getScreenPixWidth(mContext);
        int imageWidth = (int) (62 / 480.0 * screenWith);
        int margin = (int) (8 / 480.0 * screenWith);
        lp.width = imageWidth;
        lp.height = imageWidth;
        lp.setMargins(margin, margin, margin, margin);
        layout1.setLayoutParams(lp);
        layout2.setLayoutParams(lp);
        layout3.setLayoutParams(lp);
        layout4.setLayoutParams(lp);
        layout5.setLayoutParams(lp);
    }

    /**
     * @Title: setListeners
     * @Description: 设置监听器
     */
    private void setListeners() {
        registerForContextMenu(btnTitleMore);
        flLeft.setOnClickListener(this);
        btnTitleBack.setOnClickListener(this);
        btnTitleMore.setOnClickListener(this);
        memberListView.setOnClickListener(this);
        btnViewLocation.setOnClickListener(this);
        btnViewOwner.setOnClickListener(this);
        btnViewLevel.setOnClickListener(this);

        layoutViewGatherings.setOnClickListener(this); //圈聚会  暂时没有使用
        layoutViewRecommend.setOnClickListener(this);//推荐圈子
        layoutViewClearChat.setOnClickListener(clearHistoryClick);//清空聊天记录
        layoutViewGroupHelp.setOnClickListener(this);//圈主帮助
        layoutViewGroupState.setOnClickListener(this);//圈状态
        layoutViewGroupMSGSetting.setOnClickListener(this);//圈设置

        groupPostbarselect.setOnClickListener(this);
        /**
         * 加入圈子和开始圈聊按钮
         */
        RelativeLayout rlSendTopicLayout = (RelativeLayout) findViewById(R.id.rl_layout_btn);
        rlSendTopicLayout.setOnClickListener(this);
        rlLayoutBtn1.setOnClickListener(this);
        rlLayoutBtn_1.setOnClickListener(this);
        rlLayoutBtn2.setOnClickListener(this);
        rlAttentionLayout.setOnClickListener(this);
        llAttentionLayout1.setOnClickListener(this);

        llRight.setOnClickListener(this);

    }

    /**
     * @Title: initData
     * @Description: 初始化数据
     */
    private void initData() {
        Log.v("group", "isFirstInit---" + isFirstInit);

        String cacheData = SharedPreferenceCache.getInstance(mContext).getString(mGroupId,
                "");
        if (!isFirstInit) {
            isFirstInit = true;

            showWaitDialog(true);
            getChatbarInfo(cacheData);

        }
    }

    /**
     * 编辑完聊吧资料后接收EventBus重新加载数据
     */
    @Subscribe
    public void receiveMesUpdateChatbarInfo(FirstEvent event)
    {
        if (!"".equals(event.getUpdateChatbarInfo()))
        {
            getChatbarInfo(null);
        }
    }

    /**
     * @Title: initGroupInfo
     * @Description: 初始化数据展示
     */
    private void initGroupInfo(int from) {
        // 圈子角色
        mUserRole = mGroupInfoBean.grouprole;
        //圈主或管理员，成员
//游客，粉丝
        mIsJoin = mUserRole >= 0 && mUserRole < 3;
        // 从编辑返回，圈名、圈图、圈介绍都需要审核通过才更新显示
        if (from != 2) {
            // 圈子图片

            if (TextUtils.equals(mGroupInfoBean.icon, "http://f1.iaround.com/upload/images/cb12a78720c8b0aaa59cd75f64f16e4e.png")) {
                groupImage.setImageResource(R.drawable.group_info_bg);
            } else {
                ImageViewUtil.getDefault().loadImageNoCache(mGroupInfoBean.icon, groupImage, 0, 0);
            }
            // 圈子名称
            groupName = FaceManager.getInstance(mContext)
                    .parseIconForStringBaseline(textTitle, mContext, mGroupInfoBean.name,
                            14);
            textTitle.setText(groupName);
            new Handler(getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.initComponent(groupImage);
                }
            }, 100);
            // 圈子介绍
            SpannableString groupDesc = FaceManager.getInstance(mContext)
                    .parseIconForString(groupIntroduce, mContext,
                            mGroupInfoBean.getGroupDesc(mContext), 13);
            groupIntroduce.setText(groupDesc);
        }

        // 圈子id
        String id = getResString(R.string.group_info_item_num) + mGroupInfoBean.id;
        groupId.setText(id);
        groupId.setOnLongClickListener(new SpaceTextClickListener(this, String
                .valueOf(mGroupInfoBean.id), R.id.group_id));

        // 创建者昵称
        SpannableString nickName = FaceManager.getInstance(mContext).parseIconForStringBaseline(
                groupOwner, mContext, mGroupInfoBean.user.getDisplayName(true), 13);
        groupOwner.setText(nickName);

        // 创建日期
        Date createDate = new Date(mGroupInfoBean.datetime);
        groupCreateDate.setText(TimeFormat.timeFormat7(mContext, createDate.getTime()));

        initCustomDialog();

        // 圈子不同角色的权限显示
        initGroupInfAuthorityLimits();

        // 圈子热度标示
        initGroupInfoHotStatus();

        // 圈状态
        initGroupInfoStatus();

        // 圈子分类
        initGroupInfoCategory();

        // 圈子贴吧分类
        initGroupInfoPostbar();

        // 加入条件
        initGroupInforCondition();

        // 圈中心
        initGroupInfCenter();

        // 圈等级
        initGroupInfLevels();

        // 圈消息设置
        initGroupInfMessagesSet();

        GroupInfoForRelation.setGroupInfoForRelation(Long.parseLong(mGroupInfoBean.id),
                mGroupInfoBean.name, mGroupInfoBean.icon);

    }

    /**
     * 获取聊吧资料详情
     */
    private void getChatbarInfo(String cacheData)
    {
        FLAG_GET_GROUP_INFO = GroupHttpProtocol.getChatbarInfo(mContext, mGroupId, this);
        if (FLAG_GET_GROUP_INFO < 0) {
            handleDataFail(104, FLAG_GET_GROUP_INFO);
        } else {
            if (!CommonFunction.isEmptyOrNullStr(cacheData)) {
                showWaitDialog(false);
                handleDataSuccess(cacheData, FLAG_GET_GROUP_INFO);
            }

        }
    }

    /**
     * @Title: initMemberList
     * @Description: 初始化成员列表
     */
    private void initMemberList() {

        if (mGroupInfoBean.group_members == null || mGroupInfoBean.group_members.size() == 0)
            return;
        memberListView.setVisibility(View.VISIBLE);
        // 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
        if (mGroupInfoBean.classify == 1) {
            String str = getString(R.string.group_info_member_count);
            //YC
            String showString = null;
            showString = String.format(str, mGroupInfoBean.group_members.size(), mGroupInfoBean.maxcount);
            memberNumView.setText(showString);
        } else {
            memberNumView.setText("圈成员 " + String.valueOf(mGroupInfoBean.group_members.size()));
        }

        userIconView0.setVisibility(View.GONE);
        userIconView1.setVisibility(View.GONE);
        userIconView2.setVisibility(View.GONE);
        userIconView3.setVisibility(View.GONE);
        userIconView4.setVisibility(View.GONE);


        for (int i = 0; i < mGroupInfoBean.group_members.size(); i++) {
            if (i > MAX_SHOW_MEMBER_NUM - 1) {
                break;
            }
            GroupSearchUser groupUser = mGroupInfoBean.group_members.get(i);
            userIconViewList[i].setVisibility(View.VISIBLE); userIconViewList[i].setOnHeadPhotoViewClick(null);
            userIconViewList[i].executeGreet(ChatFromType.Group, groupUser.user.convertBaseToUser(), null);

            //YC
            if (groupUser.user.group_role == 0 || groupUser.user.group_role == 1) {
                adminLogoList[i].setVisibility(View.VISIBLE);
                if (groupUser.user.group_role == 0) {
                    adminLogoList[i].setImageResource(R.drawable.group_member_role_owner2);
                } else {
                    adminLogoList[i].setImageResource(R.drawable.group_member_role_admin2);
                }
            } else {
                adminLogoList[i].setVisibility(View.GONE);
            }
        }
    }
    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            if (waitDialog != null) {
                waitDialog.dismiss();
                waitDialog = null;
            }
            EventBus.getDefault().unregister(GroupInfoActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onGeneralError(final int e, final long flag) {
        super.onGeneralError(e, flag);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                handleDataFail(e, flag);
            }
        });
    }

    @Override
    public void onGeneralSuccess(final String result, final long flag) {
        super.onGeneralSuccess(result, flag);
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    handleDataSuccess(result, flag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

    }

    /**
     * @param error
     * @Title: handleDataFail
     * @Description: 处理数据请求失败
     */
    private void handleDataFail(int error, long flag) {
        if (flag == FLAG_JOIN_GROUP) {
            // 加入圈子失败
            if (error == 6032 || error == 6007) {
                // HTTP 响应拦截时已经提示失败 toast
//                showWaitDialog(false);
//                if (Common.getInstance().loginUser.isVip()
//                        || Common.getInstance().loginUser.isSVip()) {
//                    DialogUtil.showTowButtonDialog(mContext,
//                            mContext.getString(R.string.group_full),
//                            mContext.getString(R.string.group_full_content),
//                            mContext.getString(R.string.cancel),
//                            mContext.getString(R.string.go_to_del), null,
//                            new OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent();
//                                    intent.setClass(mContext, GroupListToQuit.class);
//                                    mContext.startActivity(intent);//jiqiang
//                                }
//                            });
//                } else {
//                    showWaitDialog(false);
//                    DialogUtil.showTobeVipDialog(mContext, R.string.join_group, R.string.vip_join_group2, DataTag.VIEW_group);
//                }
            } else if (error == 6041) {
                FLAG_JOIN_GROUP_WITH_CHECK = GroupHttpProtocol.submitJoinGroup(mContext, mGroupId, "", this);

            } else if (error == 6006) {
                showWaitDialog(false);
                // 小圈成员数量达到上限时, 弹窗提示
                DialogUtil.showTwoButtonDialog(mContext,
                        getString(R.string.group_cant_add_member_title_without_verify),
                        getString(R.string.group_cant_add_member_msg_without_verify),
                        getString(R.string.cancel),
                        getString(R.string.group_notify_button_text), null,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                if (mMemberListBean != null && mMemberListBean.size() > 0)
                                {
                                    for(int i = 0 ;i < mMemberListBean.size() ;i ++)
                                    {
                                        if (mMemberListBean.get(i).grouprole == 0)
                                        {
                                            GroupUser groupUser = mMemberListBean.get(i).user;
                                            User user = new User();
                                            user.setUid(groupUser.userid);
                                            user.setNickname(groupUser.nickname);
                                            user.setIcon(groupUser.icon);
                                            user.setViplevel(groupUser.viplevel);
                                            user.setSex(groupUser.getSex());
                                            user.setAge(groupUser.age);
                                            user.setLat(groupUser.lat);
                                            user.setLng(groupUser.lng);

                                            ChatPersonal.skipToChatPersonal(mContext, user);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
            } else {
                showWaitDialog(false);
                ErrorCode.toastError(mContext, error);
            }
        } else if (flag == FLAG_DISBANDED_GROUP || flag == FLAG_EXIT_GROUP) {
            showWaitDialog(false);

            // 解散、退出群组失败
            ErrorCode.toastError(mContext, error);
        }else if (flag == sendRecruitAndCallFlag)
        {
            return;
        }
        else {
            showWaitDialog(false);
            ErrorCode.toastError(mContext, error);
        }
    }

    /**
     * @param result
     * @Title: handleGetGroupInfo
     * @Description: 处理获取圈资料数据返回
     */
    private void handleDataSuccess(String result, long flag) {
        showWaitDialog(false);
        if (result == null)
            return;
        if (flag == FLAG_GET_GROUP_INFO) {
            // 获取圈子信息
            mGroupInfoBean = GsonUtil.getInstance().getServerBean(result, GroupInfoBean.class);
            if (mGroupInfoBean != null) {
                if (mGroupInfoBean.isSuccess()) {
                    SharedPreferenceCache.getInstance(mContext)
                            .putString(mGroupId, result);
                    creatorId = mGroupInfoBean.user.userid;
                    mUserRole = mGroupInfoBean.grouprole;
                    setGroupInfoBean(mGroupInfoBean);

                    if (mGroupInfoBean.fans_num > 10000)
                    {
                        tvAttentionNum1.setVisibility(View.VISIBLE);
                        tvAttentionNum.setVisibility(View.GONE);
                        tvAttentionNum1.setText("人数："+mGroupInfoBean.fans_num);
                    }else
                    {
                        tvAttentionNum.setVisibility(View.VISIBLE);
                        tvAttentionNum1.setVisibility(View.GONE);
                        tvAttentionNum.setText("人数："+mGroupInfoBean.fans_num);
                    }

                    if (hasEdit == 1) {
                        initGroupInfo(2);
                    } else {
                        initGroupInfo(1);
                    }
//                    if (mGroupInfoBean.popup == 1) {
//                        // 跳到成功创建圈子页面
//                        SuccessCreateGroupShareActivity.JumpToSuccessCreatGroupActivity(
//                                mActivity, mGroupInfoBean.id,
//                                REQUEST_CODE_SHARE_CREATE_GROUP);
//                    } else {
//                    }
                    // 开始获取圈成员列表
//                        getMemberList();
                    initMemberList();
                } else {
                    handleDataFail(mGroupInfoBean.error, flag);
                    mGroupInfoBean = null;
                }
            } else {
                handleDataFail(104, flag);
            }
        }
        else if (flag == FLAG_JOIN_GROUP) {
            if (gameFrom > 0 && gameID >= 0) {// 上报加入游戏圈子事件
            }
            // 加入圈子
            JoinGroupBean bean = GsonUtil.getInstance().getServerBean(
                    result, JoinGroupBean.class);
            if (bean != null) {
                if (bean.isSuccess()) {
                    /*
                     * //让圈成员数量+1 mMemberListBean.amount ++;
					 * refreshMemberCount();
					 */
                    mUserRole = 2;
                    mIsJoin = true;
                    enterGroupRoom(true, false);
                    try {

                        String groupId = bean.groupid;
                        // 从附近圈子移除，然后添加到我的圈子列表中
                        GroupModel groupModel = GroupModel.getInstance();
                        ArrayList<Group> nearbyGroupList = groupModel
                                .getCacheGroupNearList();
                        Group curGroup = null;
                        Group specialGroup = null;
                        int length = nearbyGroupList.size();
                        for (int i = 0; i < length; i++) {
                            if (groupId
                                    .equals(String.valueOf(nearbyGroupList.get(i).id))) {

                                if (i == length - 1) {
                                    if (nearbyGroupList.get(i - 1).contentType == 1) {
                                        specialGroup = nearbyGroupList.get(i - 1);
                                    }
                                } else {
                                    if (nearbyGroupList.get(i - 1).contentType == 1
                                            && nearbyGroupList.get(i + 1).contentType == 1) {
                                        specialGroup = nearbyGroupList.get(i - 1);
                                    } else {
                                        if (nearbyGroupList.get(i - 1).contentType == 2) {
                                            nearbyGroupList.get(i - 1).isShowDivider = 0;
                                        }
                                    }
                                }

                                curGroup = nearbyGroupList.get(i);

                                break;
                            }
                        }
                        if (curGroup != null) {
                            nearbyGroupList.remove(curGroup);
                            if (specialGroup != null) {
                                nearbyGroupList.remove(specialGroup);
                            }
                        } else {
                            curGroup = new Group();
                            curGroup.address = mGroupInfoBean.address;
                            curGroup.category = mGroupInfoBean.category;
                            curGroup.categoryicon = mGroupInfoBean.categoryicon;
                            curGroup.categoryid = mGroupInfoBean.categoryid;
                            curGroup.content = mGroupInfoBean.content;
                            curGroup.datetime = mGroupInfoBean.datetime;
                            curGroup.flag = mGroupInfoBean.flag;
                            curGroup.gold = mGroupInfoBean.gold;
                            curGroup.grouprole = 2;
                            curGroup.icon = mGroupInfoBean.icon;
                            curGroup.id = mGroupInfoBean.id;
                            curGroup.landmarkid = mGroupInfoBean.landmarkid;
                            curGroup.landmarkname = mGroupInfoBean.landmarkname;
                            curGroup.lat = mGroupInfoBean.lat;
                            curGroup.lng = mGroupInfoBean.lng;
                            curGroup.level = mGroupInfoBean.level;
                            curGroup.name = mGroupInfoBean.name;
                            curGroup.oldmaxrange = mGroupInfoBean.oldmaxrange;
                            curGroup.rang = mGroupInfoBean.rang;
                            curGroup.topic = mGroupInfoBean.topic;
                            curGroup.topiccount = mGroupInfoBean.topiccount;
                            curGroup.user = mGroupInfoBean.user;
                            curGroup.usercount = mGroupInfoBean.usercount;
                        }
                        ArrayList<Group> cacheGroupList = groupModel.getCacheMyGroupList();
                        if (cacheGroupList != null && cacheGroupList.size() > 0) {
                            cacheGroupList.add(0, curGroup);
                            groupModel.cacheMyGroupList(cacheGroupList);
                        } else {
//                            cacheGroupList.add(0, curGroup);
//                            groupModel.cacheMyGroupList(cacheGroupList);
                            GroupModel.getInstance().isNeedRefreshGroupList = true;
                        }
                        mGroupInfoBean.grouprole = 2;
                        mIsJoin = true;
                        // 刷新圈资料
                        initGroupInfo(1);

                        // 进入聊天室

                        initMemberList();
//                        getMemberList();
                        //加入聊吧后发送broadcast刷新聊吧列表我的模块数据
                        Intent intent = new Intent();
                        intent.setAction(DYNAMICACTION);
                        sendBroadcast(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    handleDataFail(bean.error, flag);
                }
            } else {
                handleDataFail(104, flag);
            }
        } else if (flag == FLAG_JOIN_GROUP_WITH_CHECK) {
            if (Constant.isSuccess(result)) {
                CommonFunction.toastMsg(mContext, R.string.apply_join_group_success);
                //发送broadcast刷新聊吧列表我的模块数据
                Intent intent = new Intent();
                intent.setAction(DYNAMICACTION);
                sendBroadcast(intent);
            } else {
                // 加入圈子失败
                JSONObject json;
                try {
                    json = new JSONObject(result);
                    int error = json.optInt("error");
//                    if (error == 6032 || error == 6007) {
//                        showWaitDialog(false);
//                        if (Common.getInstance().loginUser.isVip()
//                                || Common.getInstance().loginUser.isSVip()) {
//                            // VIP提示加入的圈子数量已经达到上限
////							DialogUtil.showOKDialog( mContext , R.string.group_join2 ,
////									R.string.join_group3 , null );
//
//                            DialogUtil.showTowButtonDialog(mContext,
//                                    mContext.getString(R.string.group_full),
//                                    mContext.getString(R.string.group_full_content),
//                                    mContext.getString(R.string.cancel),
//                                    mContext.getString(R.string.go_to_del), null,
//                                    new OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            Intent intent = new Intent();
//                                            intent.setClass(mContext, GroupListToQuit.class);
//                                            mContext.startActivity(intent);//jiqiang
//                                        }
//                                    });
//                        } else {
//                            showWaitDialog(false);
//                            DialogUtil.showTobeVipDialog(mContext, R.string.join_group, R.string.vip_join_group2, DataTag.VIEW_group);
//                        }
//                    } else
                    {
                        ErrorCode.showError(mContext, result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (flag == FLAG_DISBANDED_GROUP || flag == FLAG_EXIT_GROUP) {
            // 解散、退出圈子成功
            GroupModel.getInstance().removeGroupFromCache(mGroupId);
            GroupModel.getInstance().clearByGroupID(Integer.valueOf(mGroupId));

            BaseServerBean bean = GsonUtil.getInstance().getServerBean(
                    result, BaseServerBean.class);
            if (bean != null) {
                if (bean.isSuccess()) {
                    finishActivity();

//					ContactsView.newGroupMenber = true;//jiqiang
                    backToMainActivity();
                    SharedPreferenceCache.getInstance(mContext).putString(mGroupId, "");
                    try {

                        GroupModel.getInstance().dissolveGroup(mContext, mGroupId);
                        GroupModel.getInstance().isNeedRefreshGroupList = true;
                        // 退出或解散圈子，删除掉消息列表记录，并返回到带侧栏的界面
                        String userIdStr = Common.getInstance().loginUser.getUid() + "";
                        GroupModel.getInstance().removeGroupAndAllMessage(mContext,
                                userIdStr, mGroupId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //发送broadcast刷新聊吧列表我的模块数据
                    Intent intent = new Intent();
                    intent.setAction(DYNAMICACTION);
                    sendBroadcast(intent);
                } else {
                    handleDataFail(bean.error, flag);
                }
            } else {
                handleDataFail(104, flag);
            }
        } else if (flag == FLAG_FOLLOW_CHATBAR) {//關注聊吧
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optInt("status") == 200)
                {
                    rlAttentionLayout.setVisibility(View.GONE);
                    llAttentionLayout1.setVisibility(View.VISIBLE);
                    mUserRole = 4;
                    Intent intentFilter = new Intent();
                    intentFilter.setAction(ChatBarFamilyFragment.FOLLOW_ACTION);
                    GroupInfoActivity.this.sendBroadcast(intentFilter);
                    FLAG_GET_GROUP_INFO = GroupHttpProtocol.getChatbarInfo(mContext, mGroupId, this);
                    CommonFunction.toastMsg(GroupInfoActivity.this,R.string.other_info_follow);
                }else
                {
                    ErrorCode.showError(GroupInfoActivity.this,result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (flag == FLAG_CANCEL_FOLLOW_CHATBAR)
        {//取消關注聊吧
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optInt("status") == 200)
                {
                    rlAttentionLayout.setVisibility(View.VISIBLE);
                    llAttentionLayout1.setVisibility(View.GONE);
                    mUserRole = 3;
                    Intent intentFilter = new Intent();
                    intentFilter.setAction(ChatBarFamilyFragment.FOLLOW_ACTION);
                    GroupInfoActivity.this.sendBroadcast(intentFilter);
                    CommonFunction.toastMsg(GroupInfoActivity.this,R.string.other_info_cancle_follow);
                }else
                {
                    ErrorCode.showError(GroupInfoActivity.this,result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (flag == sendRecruitAndCallFlag)
        {//发送招募广播
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("status") == 200)
                {
                    dialog.dismiss();
                    CommonFunction.toastMsg(mContext,R.string.operate_success);
                }else
                {
                    // TODO: 2017/8/26 根据错误码提示信息，钻石不足
                    if (jsonObject.getInt("error") == 5930)
                    {
                        String note = getResString(R.string.create_chat_bar_have_no_diamond);
                        DialogUtil.showOKCancelDialog(this ,getResString(R.string.prompt), note ,R.string.call_back_dia_ok_btn_text,
                                new View.OnClickListener( )
                                {
                                    @Override
                                    public void onClick( View v )
                                    {
                                        Intent intent = new Intent(GroupInfoActivity.this, MyWalletActivity.class);
                                        mContext.startActivity(intent);
                                    }
                                } );
                    }else
                    {
                        ErrorCode.showError(GroupInfoActivity.this,result);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (flag == sendCallbackFlag)
        {//召唤
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getInt("status") == 200)
                {
                    CommonFunction.toastMsg(GroupInfoActivity.this,R.string.operate_success);
                }else
                {
                    ErrorCode.showError(GroupInfoActivity.this,result);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 圈详情中对应的点击事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        final Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.view_group_gatherings:// 圈聚会
                if (mGroupInfoBean == null)
                    return;
                break;
            case R.id.view_group_help://圈主帮助

                intent.setClass(this, WebViewAvtivity.class);
                intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE,
                        this.getString(R.string.setting_about_comment_question));
                intent.putExtra(WebViewAvtivity.WEBVIEW_URL, Constants.GroupHelpUrl);
                startActivity(intent);
                break;
            case R.id.view_group_setting:// 圈消息设置
                if (mGroupInfoBean == null)
                    return;
                GroupMsgSetActivity.launch(this, REQUEST_CODE_MSG_SETTING_GROUP,
                        mGroupInfoBean.id, mGroupInfoBean.name);//jiqiang
                break;
            case R.id.post_bar_select://选择贴吧类型
                if (mGroupInfoBean == null)
                    return;
                break;
            case R.id.ll_invite_user:
            case R.id.member_invite:// 邀请新成员
                if (mGroupInfoBean == null || mMemberListBean == null) {
                    return;
                }
                if (mGroupInfoBean.maxcount - mMemberListBean.size() > 0) {
                    GroupInviteActivity.launch(mContext, mGroupId);
//                     TODO: 2017/8/16 跳转到新的邀请界面
//                    intent.setClass(mContext,ChatbarInviteActivity.class);
//                    intent.putExtra(GROUPID,mGroupId);
//                    startActivity(intent);
                } else {
                    DialogUtil.showOKDialog(mContext, R.string.prompt,
                            R.string.group_cant_add_member_msg_without_verify, null);
                }
                break;
            case R.id.fl_left:
            case R.id.iv_left://标题栏返回按钮点击
                finishActivity();
                break;
            case R.id.fl_right:
            case R.id.iv_right://标题栏更多按钮点击
                if (mGroupInfoBean != null) {
                    initCustomDialog();
                    customContextDialog.show();
                }
                break;
            case R.id.group_member_layout:// 查看圈成员
                if (mGroupInfoBean != null) {
                    Intent i = new Intent(mContext, GroupMemberViewActivity.class);
                    i.putExtra("groupId", mGroupInfoBean.id);
                    i.putExtra("groupRole", mGroupInfoBean.grouprole);
                    i.putExtra("groupName", mGroupInfoBean.name);
                    i.putExtra("groupUser", mGroupInfoBean.user);
                    i.putExtra(CREATEID, createId);
                    startActivityForResult(i, REQUEST_CODE_GROUP_MEMBER);
                }
                break;
            case R.id.view_group_location:// 查看圈中心
                if (mGroupInfoBean == null) {
                    return;
                }
                showGroupCenter();
                break;
            case R.id.view_group_owner:// 查看圈主资料
                if (mGroupInfoBean == null) {
                    return;
                }
                User user = mGroupInfoBean.user.convertBaseToUser();

                Intent intent1 = new Intent(this, OtherInfoActivity.class);
                intent1.putExtra("user", user);
                intent1.putExtra(Constants.UID, user.getUid());
                startActivity(intent1);
                break;
            case R.id.view_group_level:// 查看圈子等级
                if (mGroupInfoBean == null) {
                    return;
                }

                DialogUtil.showOKDialog(mContext,
                        getString(R.string.group_level_introduction_title),
                        getString(R.string.group_level_introduction_msg), null);
                break;
            case R.id.view_group_state://圈子状态
                if (mGroupInfoBean == null)
                    return;
                // 仅圈成员且出现以下异常状态时弹窗提示
                String title = "";
                String msg = "";
                switch (mGroupInfoBean.groupstatus) {
                    case 1: // 1-年费已到期
                        title = getString(R.string.group_expire_title);
                        msg = getString(R.string.group_expire_msg);
                        break;
                    case 2: // 2-已隐藏，圈子从附近圈子里消失
                        title = getString(R.string.group_hide_reason_title);
                        msg = getString(R.string.group_hide_reason_msg);
                        break;
                    case 3: // 3-更新资料审核中
                        title = getString(R.string.group_new_info_exmained_title);
                        msg = getString(R.string.group_new_info_exmained_msg);
                        break;
                    case 4: // 4-已隐藏，圈子仅能通过圈号被搜索到
                        title = getString(R.string.group_search_by_id_title);
                        msg = getString(R.string.group_search_by_id_msg);
                        break;
                }
                if (title != "" && msg != "") {
                    DialogUtil.showOKDialog(mContext, title, msg, null);
                }
                break;
            case R.id.rl_layout_btn:// 加入圈子
                if (mGroupInfoBean == null) {
                    return;
                }
                enterGroupRoom(true, GroupModel.getInstance().isHaveNewMessage(mGroupInfoBean.id));
                break;
            case R.id.rl_layout_btn_1://召唤
                //召唤功能
                DialogUtil.showCallDialog(GroupInfoActivity.this, new RecruitAndCallDialog.SureClickListener() {
                    @Override
                    public void onSure(String content) {
                        sendCallbackFlag = GroupHttpProtocol.chatbarHomecall(GroupInfoActivity.this,mGroupId,content,GroupInfoActivity.this);
                    }
                });
                break;
            case R.id.rl_layout_btn1://招募广播
                //招募广播
                dialog = DialogUtil.showRecruitDialog(GroupInfoActivity.this, new RecruitAndCallDialog.SureClickListener() {
                    @Override
                    public void onSure(String content) {
                        if ("".equals(content) || null == content) {
                            CommonFunction.toastMsg(GroupInfoActivity.this, R.string.e_4001);
                            return;
                        }
                        String str = content;
                        WorldMessageRecruitContent worldMessageGiftContent = new WorldMessageRecruitContent();
                        worldMessageGiftContent.groupname = groupName +"";
                        worldMessageGiftContent.text = content;
                        worldMessageGiftContent.groupid = getGroupId();
                        String message = JSON.toJSONString(worldMessageGiftContent);
                        sendRecruitAndCallFlag = SendWorldMessageProtocol.getInstance().getSendWorldMessageData(GroupInfoActivity.this,Integer.valueOf(mGroupId),
                                content,31,GroupInfoActivity.this);
                    }
                });

                break;
            case R.id.rl_layout_btn2://邀请聊天
                if (mGroupInfoBean == null ) {
                    return;
                }
//                if (mGroupInfoBean.maxcount - mMemberListBean.amount > 0) {
                if (mGroupInfoBean.maxcount - mMemberListBean.size() > 0) {
                    intent.setClass(mContext,ChatbarInviteActivity.class);
                    intent.putExtra(GROUPID,mGroupId);
                    startActivity(intent);
                } else {
                    DialogUtil.showOKDialog(mContext, R.string.prompt,
                            R.string.group_cant_add_member_msg_without_verify, null);
                }
                break;
            case R.id.ll_attention://关注
                //关注聊吧
                FLAG_FOLLOW_CHATBAR = GroupHttpProtocol.followChatbar(GroupInfoActivity.this,mGroupId,this);
                break;
            case R.id.ll_attention1://查看粉丝人数
                //查看粉丝
                intent.setClass(GroupInfoActivity.this, ChatbarFansActivity.class);
                intent.putExtra("group_id",mGroupId);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     *
     * 加入圈子更多菜单的点击事件
     */
    private OnClickListener moreOnclickLisener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((int) (v.getTag())) {
                case 0:
                    if (creatorId == Common.getInstance().loginUser.getUid() || mUserRole == 1) {//如果创建者Id是== 登陆者id
                        Intent managerIntent = new Intent(mContext, GroupManagerActivity.class);
                        managerIntent.putExtra("welcome",tvWelcomeHint.getText().toString());
                        managerIntent.putExtra("groupInfo", mGroupInfoBean);
                        startActivityForResult(managerIntent, REQUEST_CODE_GROUP_MANAGER);

                    } else {
                        // TODO: 2017/4/15 跳转到举报界面
                        Intent intent = new Intent(mActivity, SpaceReport.class);
                        intent.putExtra("targetId", mGroupId);
                        intent.putExtra("targetType", 5);
                        startActivityForResult(intent, REQUEST_CODE_REPORT);
                        customContextDialog.dismiss();
                    }
                    break;
                case 1:
                    if (creatorId == Common.getInstance().loginUser.getUid()) {
                        //转让聊吧
                        Intent i = new Intent(mContext, ChatbarTransferActivity.class);
                        i.putExtra("groupId", mGroupInfoBean.id);
                        startActivityForResult(i, REQUEST_CODE_GROUP_MEMBER);
                    } else if (mUserRole == 1) {
                        // TODO: 2017/4/15 跳转到举报界面
                        Intent intent = new Intent(mActivity, SpaceReport.class);
                        intent.putExtra("targetId", mGroupId);
                        intent.putExtra("targetType", 5);
                        startActivityForResult(intent, REQUEST_CODE_REPORT);
                        customContextDialog.dismiss();
                    } else if (mUserRole == 2){// TODO: 2017/4/15 退出圈子
                        DialogUtil.showOKCancelDialog(mContext, R.string.chat_room_info_quit,
                                R.string.room_info_quit_info, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        showWaitDialog(true);
                                        FLAG_EXIT_GROUP = GroupHttpProtocol.groupDelUser(mContext,
                                                mGroupId, PhoneInfoUtil.getInstance(mContext)
                                                        .loginCode(mContext), GroupInfoActivity.this);
                                        if (FLAG_EXIT_GROUP < 0) {
                                            showWaitDialog(false);
                                            handleDataFail(104, FLAG_EXIT_GROUP);
                                        }

                                        // 记录用户加入的圈子，不管是否退出成功，用于退出圈子再次进入圈聊之后不给出提示
                                        GroupSharePrefrenceUtil util = new GroupSharePrefrenceUtil();
                                        util.putBoolean(mContext, GroupSharePrefrenceUtil.FIRST_ENTER_GROUP, mGroupId,
                                                false);
                                    }
                                });
                    }else if (mUserRole == 4)
                    {
                        // TODO: 2017/8/15 取消关注
                        FLAG_CANCEL_FOLLOW_CHATBAR = GroupHttpProtocol.cancelFollowChatbar(GroupInfoActivity.this,mGroupId,GroupInfoActivity.this);
                    }
                    break;
                case 2:
                    if (creatorId == Common.getInstance().loginUser.getUid()) {
                        //解散聊吧
                        DialogUtil.showOKCancelDialog(mContext,
                                getResString(R.string.chat_room_info_dismiss),
                                getResString(R.string.room_info_dismiss_info),
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        showWaitDialog(true);
                                        FLAG_DISBANDED_GROUP = GroupHttpProtocol.groupDel(mContext,
                                                mGroupId, GroupInfoActivity.this);
                                        if (FLAG_DISBANDED_GROUP < 0) {
                                            showWaitDialog(false);
                                            handleDataFail(104, FLAG_DISBANDED_GROUP);
                                        }
                                    }
                                });
                    } else if (mUserRole == 1){
                        // TODO: 2017/4/15 退出圈子
                        DialogUtil.showOKCancelDialog(mContext, R.string.chat_room_info_quit,
                                R.string.room_info_quit_info, new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        showWaitDialog(true);
                                        FLAG_EXIT_GROUP = GroupHttpProtocol.groupDelUser(mContext,
                                                mGroupId, PhoneInfoUtil.getInstance(mContext)
                                                        .loginCode(mContext), GroupInfoActivity.this);
                                        if (FLAG_EXIT_GROUP < 0) {
                                            showWaitDialog(false);
                                            handleDataFail(104, FLAG_EXIT_GROUP);
                                        }
                                    }
                                });
                    }
                    break;
            }
            customContextDialog.dismiss();
        }
    };
    private void finishActivity() {
        Intent data = new Intent();
        data.putExtra(GROUPID, mGroupId);
        data.putExtra(CLEARALL, mIsClearAllMessages);
        data.putExtra(GROUP_ROLE,mUserRole);
        setResult(RESULT_OK, data);
        finish();
    }
    /**
     * 清除圈聊天记录
     */
    OnClickListener clearHistoryClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            DialogUtil.showOKCancelDialog(mContext, R.string.dialog_title,
                    R.string.comfirm_to_clear_group_chat_history, new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            showWaitDialog(true);
                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    String mUidStr = String.valueOf(Common.getInstance().loginUser
                                            .getUid());
                                    GroupModel.getInstance().removeGroupAndAllMessage(
                                            mContext, mUidStr, mGroupId);
                                    mHandle.sendEmptyMessage(MENU_CLEAR_HISTORY);
                                }
                            }).start();
                        }
                    });
        }
    };

    /**
     * 查看圈中心
     */
    private void showGroupCenter() {
        String address = CommonFunction.showAddress(mGroupInfoBean.landmarkname);
        if (CommonFunction.isEmptyOrNullStr(address)) {
            address = mGroupInfoBean.landmarkname;
        }
        String title = getString(R.string.group_info_item_core);
        CommonFunction.log("group", "mGroupInfoBean.landmarklat***"
                + mGroupInfoBean.landmarklat);
        CommonFunction.log("group", "mGroupInfoBean.landmarklng***"
                + mGroupInfoBean.landmarklng);
        MapUtils.showOnePositionMap(mContext, MapUtils.LOAD_TYPE_POS_MAP,
                mGroupInfoBean.landmarklat, mGroupInfoBean.landmarklng, address, title);
    }

    /**
     * @Title: enterGroupRoom
     * @Description: 进入聊天室
     */
    private void enterGroupRoom(boolean isChat, boolean isHaveNewMsg) {

        if ((mUserRole >= 0 && mUserRole < 3) || (!mIsJoin && mGroupInfoBean.config != null && mGroupInfoBean.config.viewtopic == 1 && isViewTopic)) {
            this.isChat = isChat;
            isViewTopic = false;
            Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
            // 开始圈聊
            intent.putExtra("id", mGroupId);
            intent.putExtra("icon", mGroupInfoBean.icon);
            intent.putExtra("name", mGroupInfoBean.name);
            intent.putExtra("userid", mGroupInfoBean.user.userid);
            intent.putExtra("usericon", mGroupInfoBean.user.icon);
            intent.putExtra("grouprole", mUserRole);
            // 判断是否有新圈消息
            intent.putExtra("has_new_message", isHaveNewMsg);
            intent.putExtra("isChat", isChat);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (isChat) {
//                startActivity(intent);
                GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
            } else {
                GroupChatTopicActivity.ToGroupChatTopicActivityForResult(this, intent, REQUEST_CODE_TOPIC_LIST_GROUP);
//                startActivityForResult(intent, REQUEST_CODE_TOPIC_LIST_GROUP);
            }
        } else {
            // 加入圈子
            joinGroup();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGroupInfoBean != null) {
            if (mGroupInfoBean.groupstatus == 2
                    && (mGroupInfoBean.grouprole == 0 || mGroupInfoBean.grouprole == 1)
                    && isChat) {
                // 连续3天内，圈主或管理员没在圈子发言
                isFirstInit = false;
                initData();
                isChat = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String cacheData = SharedPreferenceCache.getInstance(mContext).getString(mGroupId,
                "");
        if (requestCode == REQUEST_CODE_GROUP_MEMBER && resultCode == RESULT_OK) {
            getChatbarInfo(cacheData);
        } else if (requestCode == REQUEST_CODE_GROUP_MANAGER && resultCode == RESULT_OK) {
            GroupInfoBean bean = data.getParcelableExtra("groupInfo");
            if (bean != null) {
                mGroupInfoBean = bean;
                // 圈名称、圈图、圈介绍需审核
                if (data.getExtras().containsKey("hasEdit")
                        && data.getIntExtra("hasEdit", 0) == 1) {
                    hasEdit = data.getIntExtra("hasEdit", 0);
                }
                isFirstInit = false;
                initData();
            }
        } else if (requestCode == REQUEST_CODE_SHARE_CREATE_GROUP && resultCode == RESULT_OK) {
            //YC
            getChatbarInfo(cacheData);
        } else if (requestCode == REQUEST_CODE_SHARE_UPGRADE_GROUP && resultCode == RESULT_OK) {
            //YC
            getChatbarInfo(cacheData);
        } else if (requestCode == REQUEST_CODE_MSG_SETTING_GROUP && resultCode == RESULT_OK) {
            int type = GroupAffairModel.getInstance().getGroupMsgStatus(
                    Long.parseLong(mGroupInfoBean.id));
            String setContent = getResources().getStringArray(R.array.group_msgsetting_s)[type];
            groupMSGsetting.setText(setContent);
            String setContentTips = getResources().getStringArray(
                    R.array.group_msgsetting_tips)[type];
        } else if (requestCode == REQEST_CODE_MSG_POST_BAR_SELECT && resultCode == RESULT_OK) {
            // 刷新选择贴吧分类

            groupPostbarselect.setText(data.getExtras().getString("postbarname"));
            groupPostbarselect.setClickable(false);
            groupPostbarselect.setCompoundDrawables(null, null, null, null);

            Random rnd = new Random();
            int rand = rnd.nextInt(6);
            int[] drawableId =
                    {R.drawable.postbar_sel_0, R.drawable.postbar_sel_1,
                            R.drawable.postbar_sel_2, R.drawable.postbar_sel_3,
                            R.drawable.postbar_sel_4, R.drawable.postbar_sel_5};

            groupPostbarselect.setBackgroundResource(drawableId[rand]);

        } else if (requestCode == REQUEST_CODE_TOPIC_LIST_GROUP)// && resultCode
        // == RESULT_OK)
        {
            FLAG_GET_GROUP_INFO = GroupHttpProtocol.groupDetail(mContext, mGroupId, this);
            if (FLAG_GET_GROUP_INFO < 0) {
                handleDataFail(104, FLAG_GET_GROUP_INFO);
            }

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        if (mUserRole >= 0) {
            if (mUserRole == 0 || mUserRole == 1) {
                // 圈主或管理员
                menu.add(0, MENU_GROUP_MANAGER, 1, getString(R.string.menu_group_manager));
            } else if (mUserRole == 2) {
                // 普通用户
                menu.add(0, MENU_REPORT_GROUP, 1, getString(R.string.menu_report_group));
            }
            menu.add(0, MENU_CLEAR_HISTORY, 2, getString(R.string.menu_clear_history));
            // 圈主
            if (mUserRole == 0) {
                menu.add(0, MENU_DISBANDED_GROUP, 3,
                        getString(R.string.menu_disbanded_group));
            } else {
                menu.add(0, MENU_EXIT_GROUP, 3, getString(R.string.menu_exit_group));
            }
            menu.add(0, MENU_CANCEL, 4, getString(R.string.cancel));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_GROUP_MANAGER:
                // 跳转到圈管理
//				Intent managerIntent = new Intent( mContext , GroupManagerActivity.class );
//				managerIntent.putExtra( "groupInfo" , mGroupInfoBean );
//				startActivityForResult( managerIntent , REQUEST_CODE_GROUP_MANAGER );//jiqiang
                break;

            case MENU_TRANSFER_GROUP:
                // 跳转到圈成员，圈子转让
                if (mGroupInfoBean != null) {
//					Intent i = new Intent( mContext , GroupMemberViewActivity.class );
//					i.putExtra( "groupId" , mGroupInfoBean.id );
//					i.putExtra( "groupRole" , mGroupInfoBean.grouprole );
//					i.putExtra( "groupName" , mGroupInfoBean.name );
//					i.putExtra( "groupTransfer" , true );
//					startActivityForResult( i , REQUEST_CODE_GROUP_MEMBER );//jiqiang
                }

                break;

            case MENU_REPORT_GROUP:
                // 举报圈子
//				Intent intent = new Intent( mActivity , SpaceReport.class );
//				intent.putExtra( "targetId" , mGroupId );
//				intent.putExtra( "targetType" , 5 );
//				startActivity( intent );//jiqiang
                break;
            case MENU_CLEAR_HISTORY:
                // 清除历史消息
                showWaitDialog(true);
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String mUidStr = String.valueOf(Common.getInstance().loginUser
                                .getUid());
                        GroupModel.getInstance().removeGroupAndAllMessage(mContext,
                                mUidStr, mGroupId);
                        mHandle.sendEmptyMessage(MENU_CLEAR_HISTORY);
                    }
                }).start();
                break;
            case MENU_DISBANDED_GROUP:
                // 解散圈子
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.chat_room_info_dismiss)
                        .setMessage(R.string.room_info_dismiss_info)
                        .setCancelable(true)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        showWaitDialog(true);
                                        FLAG_DISBANDED_GROUP = GroupHttpProtocol.groupDel(
                                                mContext, mGroupId, GroupInfoActivity.this);
                                        if (FLAG_DISBANDED_GROUP < 0) {
                                            showWaitDialog(false);
                                            handleDataFail(104, FLAG_DISBANDED_GROUP);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();

                break;
            case MENU_EXIT_GROUP:
                // 退出圈子
                DialogUtil.showOKCancelDialog(mContext, R.string.chat_room_info_quit,
                        R.string.room_info_quit_info, new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                showWaitDialog(true);
                                FLAG_EXIT_GROUP = GroupHttpProtocol.groupDelUser(mContext,
                                        mGroupId, PhoneInfoUtil.getInstance(mContext)
                                                .loginCode(mContext),
                                        GroupInfoActivity.this);
                                if (FLAG_EXIT_GROUP < 0) {
                                    showWaitDialog(false);
                                    handleDataFail(104, FLAG_EXIT_GROUP);
                                }
                            }
                        });
                break;
            case MENU_CANCEL:

                break;
        }
        return true;
    }

    /**
     * Handle消息处理
     */
    Handler mHandle = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MENU_CLEAR_HISTORY:
                    showWaitDialog(false);
                    CommonFunction.toastMsg(mContext, R.string.delete_succuss);
                    mIsClearAllMessages = true;
                    break;
                default:
                    break;
            }
        }

    };

    /**
     * @Title: joinGroup
     * @Description: 加入圈子
     */
    private void joinGroup() {
        if (CommonFunction.isEmptyOrNullStr(Common.getInstance().loginUser.getIcon())) {
            DialogUtil.showOKDialog(mContext, getString(R.string.dialog_title), getString(R.string.icon_group_tip1), null);
        } else {
            // 记录用户加入的圈子，不管是否加入成功，用于加入成功并进入圈聊之后给出提示
            GroupSharePrefrenceUtil util = new GroupSharePrefrenceUtil();
            util.putBoolean(mContext, GroupSharePrefrenceUtil.FIRST_ENTER_GROUP, mGroupId, true);
            showWaitDialog(true);
            if (mGroupInfoBean.config != null && mGroupInfoBean.config.joincheck == 0) {
                FLAG_JOIN_GROUP = GroupHttpProtocol.joinChatbar(mContext,mGroupId,PhoneInfoUtil.getInstance(mContext).loginCode(mContext),this);
                if (FLAG_JOIN_GROUP < 0) {
                    handleDataFail(104, FLAG_JOIN_GROUP);
                }
            }
            else {
                FLAG_JOIN_GROUP_WITH_CHECK = GroupHttpProtocol.submitJoinGroup(mContext, mGroupId, "", this);
                if ( FLAG_JOIN_GROUP_WITH_CHECK < 0 ) {
                    showWaitDialog( false );
                    handleDataFail( 104 , FLAG_JOIN_GROUP );
                }

            }
        }
    }

    /**
     * @param isShow
     * @Title: showWaitDialog
     * @Description: 显示加载对话框
     */
    private void showWaitDialog(boolean isShow) {
        if (isShow) {
            if (waitDialog == null) {
                waitDialog = DialogUtil.getProgressDialog(mContext, "",
                        getString(R.string.please_wait), new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                            }
                        });
            }
            if (!waitDialog.isShowing()) {
                waitDialog.show();
            }
        } else {
            if (waitDialog != null) {
                waitDialog.hide();
            }
        }
    }

    @Override
    protected String getGroupId() {
        return mGroupId;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//		if ( tPage != null )
//		{
//			tPage.onSaveInstanceState( outState );
//		}
    }

    public static void setGroupInfoBean(GroupInfoBean mGroupInfoBean) {

        GroupInfo = mGroupInfoBean;

    }

    public static GroupInfoBean getGroupInfoBean() {

        return GroupInfo;
    }

    // ///////////////////////////////////////////////////////////////////////////
    /*
     * 圈子信息各个部分分开初始化
	 */
    // //////////////////////////////////////////////////////////////////////

    // 初始化圈状态
    private void initGroupInfoStatus() {
        if (mUserRole >= 0 && mUserRole < 3) {
            if (mGroupInfoBean.groupstatus > 0) {
                layoutViewGroupState.setVisibility(View.GONE);
                switch (mGroupInfoBean.groupstatus) {
                    case 1: // 1-年费已到期
                        groupStatus.setText(R.string.group_status_expired);
                        break;
                    case 2: // 2-已隐藏，圈子从附近圈子里消失
                        groupStatus.setText(R.string.group_status_hiden);
                        break;
                    case 3: // 3-更新资料审核中
                        groupStatus.setText(R.string.group_status_examining);
                        break;
                    case 4: // 4-已隐藏，圈子仅能通过圈号被搜索到
                        groupStatus.setText(R.string.group_status_hiden);
                        break;
                }
            } else {
                layoutViewGroupState.setVisibility(View.GONE);
            }
        } else {
            layoutViewGroupState.setVisibility(View.GONE);
        }

    }

    // 圈成员和非圈成员显示权限
    private void initGroupInfAuthorityLimits() {
        //热度均可见
        tvGroupInfoHotNum.setText(mGroupInfoBean.activesocre+"");

        String lLocation = "";
        if (mGroupInfoBean.landmarkname == null){
            lLocation = getString(R.string.address_x);
        }else{
            if (CommonFunction.isEmptyOrNullStr(mGroupInfoBean.landmarkname)){
                lLocation = getString(R.string.address_x);
            }else {
                lLocation = mGroupInfoBean.landmarkname;
            }
        }
        tvPosition.setText(lLocation);
        //更多功能键均可见
        btnTitleMore.setVisibility(View.VISIBLE);
        //欢迎语均可见
        String welcome = "";
        if (null != mGroupInfoBean.welcome && mGroupInfoBean.welcome.contains("|"))
        {
            String[] problems = mGroupInfoBean.welcome.split("\\|");
            if (problems.length >= 3) {

                int languageIndex = CommonFunction.getLanguageIndex(GroupInfoActivity.this);
                if (languageIndex == 0) {
                    // 英文
                    welcome = problems[0];
                } else if (languageIndex == 1) {
                    // 简体
                    welcome = problems[1];
                } else {
                    // 繁体
                    welcome = problems[2];
                }

            }
        }else
        {
            if (null == mGroupInfoBean.welcome){
                welcome = "";
            }else{
                welcome = mGroupInfoBean.welcome;
            }
        }
        SpannableString spContent = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, welcome, 0, null);
        tvWelcomeHint.setText(spContent);
//        tvWelcomeHint.setTextColor(getResColor(R.color.login_btn));
        //招募广播均可见
        rlLayoutBtn1.setVisibility(View.VISIBLE);
        //邀请聊天均可见
        rlLayoutBtn2.setVisibility(View.VISIBLE);
        mIsJoin = mGroupInfoBean.grouprole == 0 || mGroupInfoBean.grouprole == 1 || mGroupInfoBean.grouprole == 2;
        if (!mIsJoin) {

            if (mGroupInfoBean.grouprole == 3)
            {
                //游客，关注按钮可见
                rlAttentionLayout.setVisibility(View.VISIBLE);
                //游客，粉丝数不可见
                llAttentionLayout1.setVisibility(View.GONE);
            }else if (mGroupInfoBean.grouprole == 4)
            {
                //粉丝，关注按钮不可见
                rlAttentionLayout.setVisibility(View.GONE);
                //粉丝，粉丝数可见
                llAttentionLayout1.setVisibility(View.VISIBLE);
            }
            //非吧主，召唤功能不可见
            rlLayoutBtn_1.setVisibility(View.GONE);
            //非圈成员，加入圈子可见
            rlLayoutBtn.setVisibility(View.VISIBLE);
            // 非圈成员,吧成员可见，邀请新成员不可见，不可点击
            rlInviteNewMember.setOnClickListener(null);
            rlInviteNewMember.setOnClickListener(null);
            rlInviteNewMember.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_invite_user).setVisibility(View.INVISIBLE);

            // 非圈成员，操作项不可见
            findViewById(R.id.handler_items).setVisibility(View.GONE);
            //吧介绍分割线不可见
            divider_introduce.setVisibility(View.GONE);
        } else {
            if (mGroupInfoBean.grouprole == 0)
            {
                rlLayoutBtn_1.setVisibility(View.VISIBLE);
                rlLayoutBtn.setVisibility(View.GONE);
            }else if (mGroupInfoBean.grouprole == 1 || mGroupInfoBean.grouprole == 2)
            {
                rlLayoutBtn_1.setVisibility(View.GONE);
                rlLayoutBtn.setVisibility(View.VISIBLE);
                btnJoinGroup.setText(R.string.chat_group);
            }
            //圈成员，关注按钮不可见
            rlAttentionLayout.setVisibility(View.GONE);
            //圈成员，粉丝数可见
            llAttentionLayout1.setVisibility(View.VISIBLE);
            // 圈成员  圈成员及邀请按钮可见
            rlInviteNewMember.setVisibility(View.VISIBLE);
            findViewById(R.id.ll_invite_user).setVisibility(View.VISIBLE);
            tvInviteNewMember.setOnClickListener(this);
            rlInviteNewMember.setOnClickListener(this);
            /***邀请好友*/
            findViewById(R.id.ll_invite_user).setOnClickListener(this);

            //圈成员，清除聊天记录可见
            layoutViewClearChat.setVisibility(View.VISIBLE);
            //圈成员，圈主帮助可见
            layoutViewGroupHelp.setVisibility(View.VISIBLE);
            //圈成员，圈消息设置可见
            layoutViewGroupMSGSetting.setVisibility(View.VISIBLE);
            mView.setVisibility(View.VISIBLE);
            // 非圈成员，操作项不可见
            findViewById(R.id.handler_items).setVisibility(View.VISIBLE);
            //吧介绍分割线可见
            divider_introduce.setVisibility(View.VISIBLE);
        }

    }
    // 圈子热度标示
    private void initGroupInfoHotStatus() {
        groupTag.setVisibility(View.INVISIBLE);//于超

        switch (mGroupInfoBean.flag) {
            case 0: // 无标识
                groupTag.setVisibility(View.INVISIBLE);
                break;
            case 1: // 最新
                groupTag.setImageResource(R.drawable.group_new);
                break;
            case 2: // 热门
                groupTag.setImageResource(R.drawable.group_hot);
                break;
        }
    }

    // 圈子分类
    private void initGroupInfoCategory() {
        try {
            int langIndex = CommonFunction.getLanguageIndex(mContext);
            String typeArray[] = mGroupInfoBean.category.split("\\|");
            groupType.setText(typeArray[langIndex]);


            GlideUtil.loadImage(BaseApplication.appContext, mGroupInfoBean.categoryicon, groupTypeIcon);
        } catch (Exception e) {
            CommonFunction.log("group", "set type error, type : " + mGroupInfoBean.category);
            e.printStackTrace();
        }
    }

    private void initGroupInfoPostbar() {
        if (mGroupInfoBean.postbar != null
                && !TextUtils.isEmpty(mGroupInfoBean.postbar.postbarname)) {
            /**
             * 是否显示圈类型和选择贴吧分类 暂时没有使用没有修改
             * 修改者:yuchao
             */
//			findViewById( R.id.view_post_bar ).setVisibility( View.VISIBLE );
            findViewById(R.id.view_post_bar).setVisibility(View.GONE);

            groupPostbarselect.setText(mGroupInfoBean.postbar.postbarname);
            groupPostbarselect.setClickable(false);
            groupPostbarselect.setCompoundDrawables(null, null, null, null);
            groupPostbarselect.setTextColor(Color.WHITE);

            Random rnd = new Random();
            int rand = rnd.nextInt(6);
            int[] drawableId =
                    {R.drawable.postbar_sel_0, R.drawable.postbar_sel_1,
                            R.drawable.postbar_sel_2, R.drawable.postbar_sel_3,
                            R.drawable.postbar_sel_4, R.drawable.postbar_sel_5};

            groupPostbarselect.setBackgroundResource(drawableId[rand]);
        }

        if (mUserRole != 0) {
            if (mGroupInfoBean.postbar == null
                    || TextUtils.isEmpty(mGroupInfoBean.postbar.postbarname)) {
                groupPostbarselect.setVisibility(View.GONE);
            }
        }

    }

    // 加入条件
    private void initGroupInforCondition() {
        // 圈加入范围
        groupJoinCondition.setText(mGroupInfoBean.rang + "km");
    }

    // 圈等级
    private void initGroupInfLevels() {
        String levelContent = "";
        if (mGroupInfoBean.classify == GroupScaleType.SMALL_GROUP_TYPE) {
            levelContent = String.format(getString(R.string.less_than_101_members_group),
                    mGroupInfoBean.level, mGroupInfoBean.maxcount);
        } else if (mGroupInfoBean.classify == GroupScaleType.BIG_GROUP_TYPE) {
            levelContent = "LV. " + mGroupInfoBean.level + "  ";
            levelContent += getString(R.string.ten_thousand_members_group);
        } else if (mGroupInfoBean.classify == GroupScaleType.HUNDRED_THOUSAND_GROUP_TYPE) {
            levelContent = "LV. " + mGroupInfoBean.level + "  ";
            levelContent += getString(R.string.hundred_thousand_members_group);
        }
        groupLevel.setText(levelContent);

    }

    // 圈消息设置
    private void initGroupInfMessagesSet() {

        int type = GroupAffairModel.getInstance().getGroupMsgStatus(
                Long.parseLong(mGroupInfoBean.id));
        String setContent = getResources().getStringArray(R.array.group_msgsetting_s)[type];
        groupMSGsetting.setText(setContent);

    }

    // 圈中心
    private void initGroupInfCenter() {
        if (CommonFunction.isEmptyOrNullStr(mGroupInfoBean.landmarkname)) {
            findViewById(R.id.view_group_location).setVisibility(View.GONE);
            findViewById(R.id.group_location_line).setVisibility(View.GONE);
        } else {
            findViewById(R.id.view_group_location).setVisibility(View.VISIBLE);
            findViewById(R.id.group_location_line).setVisibility(View.VISIBLE);
            groupCenter.setText(mGroupInfoBean.landmarkname);
        }

    }

    /**
     * 获取聊吧获得麦克风权限后执行操作
     */
    public void doMicShowPermissions()
    {
        super.doMicShowPermissions();
        ChatBarZoomWindow.getInstance().onRequestMicPermissionSuccess();
    }
}
