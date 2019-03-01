package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.google.protobuf.nano.MessageNano;
import com.peng.one.push.OnePush;
import com.zego.zegoliveroom.ZegoLiveRoom;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.mic.AudioChatManager;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectGroup;
import net.iaround.connector.ConnectLogin;
import net.iaround.connector.ConnectSession;
import net.iaround.connector.FilterUtil;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.SignClickHttpProtocol;
import net.iaround.contract.MainContract;
import net.iaround.data.jpush.JPushUser;
import net.iaround.im.STNManager;
import net.iaround.im.WebSocketManager;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.push.ICacheMessageHandler;
import net.iaround.im.push.IPushMessageHandler;
import net.iaround.im.push.PushMessage;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.model.database.FriendModel;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.MenuBadge;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.im.ChatGameActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.LoginTimer;
import net.iaround.ui.comon.LoginTimer.LoginCallback;
import net.iaround.ui.comon.MenuBadgeHandle;
import net.iaround.ui.datamodel.BaseUserInfo;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.FaceCenterModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.VideoChatModel;
import net.iaround.ui.dynamic.DynamicMessagesActivity;
import net.iaround.ui.fragment.ChatBarFragment;
import net.iaround.ui.fragment.HomeContainerFragment;
import net.iaround.ui.fragment.MessageFragmentIm;
import net.iaround.ui.fragment.UserFragment;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.DragPointView;
import net.iaround.ui.view.main.GradientIconTextView;
import net.iaround.ui.view.main.TabViewPager;
import net.iaround.utils.UploadZipFileUtils;
import net.iaround.utils.logger.Logger;
import net.iaround.videochat.VideoChatManager;
import net.iaround.videochat.task.CloseVideoTaskWrapper;
import net.iaround.videochat.task.LoginTaskWrapper;
import net.iaround.videochat.task.LogoutTaskWrapper;
import net.iaround.videochat.task.RejectVideoTaskWrapper;
import net.iaround.im.proto.Iachat;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class MainFragmentActivity extends BaseFragmentActivity implements MainContract.View, View.OnClickListener, IPushMessageHandler, ICacheMessageHandler, ITaskEndListener {

    public static final int PAGE_SELECT = 0; // 当页面切换到当前页面时
    public static final int PAGE_REFRESH = 1; // 当有新的消息推送时或需要刷新页面时
    public static final int PAGE_CLICK = 2; // 当点击当前的按钮事件时
    public static final int PAGE_ = 3; // 其他事件定义


    private int FRESH_ALL_COUNT = 11;
    private int LONGIN_FAIL = 4;
    private int LONGIN_TIME_OUT = 9;
    private int NET_CONNECT_FAIL = 5;
    private int NET_CONNECT_SUCCESS = 6;
    private int LONGIN_CONNECTING = 7;
    private int LONGIN_SUCCESS = 8;
    private int AUTO_UPLOAD_LOG = 13; //上传日志

    /**
     * new标示
     */
    public final static int BADGE_NEW = 1;
    /**
     * 原点标示
     */
    public final static int BADGE_ROUND = 2;
    /**
     * 数字表示
     */
    public final static int BADGE_NUMBER = 3;
    /**
     * 不显示
     */
    public final static int BADGE_GONE = 4;


    private ImageView tabNear;
    //    private ImageView tabDynamic;
    private ImageView tabLive;
    private ImageView tabMsg;
    private ImageView tabFind;
    private ImageView tabChatBar;

    private MenuBadge menuMessages;

    private GradientIconTextView[] mTab;

    private TabViewPager mViewPager;
    private MainFragmentAdapter adapter;
    /**
     * 首页
     */
    private HomeContainerFragment tab1Fragement;
//    /**
//     * 排行榜 移到聊吧模块
//     */
//    private RankingFragment tab2Fragement;
    /**
     * 消息
     */
    private MessageFragmentIm tab3Fragement;
    /**
     * 个人
     */
    private UserFragment tab4Fragement;
    /**
     * 聊吧
     */
    private ChatBarFragment tab5Fragment;

    /**
     * 用户ID
     */
    public BaseUserInfo user;
    public int total;//总共项
    public String token;
    public String accesstoken;//微博用户身份
    public String openid;//微博用户ID

    public static MainFragmentActivity sInstance;

    private boolean isLogout = false;
    private Boolean isShowDialog = false;
    private LoginTimer loginTimer;

    // 消息
    public static final int REQ_CODE_MEG_CHAT_PERSONAL = 0XF303;
    private static final int SHOW_WEB_VIEW_AD = 6;

    //第二次点击的时间
    long lastPressTime = 0;

    // 语言是否修改
    public static boolean bIsLocaleChange = false;
    private int startUpCount;

    private int mUpdateDeviceTokenCount = 0; //上传设备 token的次数

    private boolean anchorVersion = false;

    private boolean mVideoChatInit = false;

    public RelativeLayout mChatBarLy;//聊吧点击按钮

    @Override
    public void onCreate(Bundle savedInstanceState) {
        CommonFunction.log("MainFragmentActivity", "onCreate() into");
        super.onCreate(savedInstanceState);
        if (mIsDestroy == true) {
            CommonFunction.log("MainFragmentActivity", "onCreate() activity is destroy");
            return;
        }
        setContentView(R.layout.activity_main_fragment);
        tabNear = (ImageView) findViewById(R.id.near_tab_icon);
        tabLive = (ImageView) findViewById(R.id.live_main_tab_icon);
//        tabDynamic = (ImageView) findViewById(R.id.dynamic_tab_icon);
        tabMsg = (ImageView) findViewById(R.id.msg_tab_icon);
        tabFind = (ImageView) findViewById(R.id.find_tab_img);
        tabChatBar = (ImageView) findViewById(R.id.chat_bar_tab_icon);
        mViewPager = (TabViewPager) findViewById(R.id.main_pager);
        menuMessages = new MenuBadge((DragPointView) findViewById(R.id.badge2), MenuBadge.ItemType.Message, (ImageView) findViewById(R.id.iv_badge2));

        if (VideoChatManager.getInstance().loginUserIsAnchor()) {
            int av = Config.getAnchorVersionOpen();
            anchorVersion = av == 1 ? true : false;
        }
        if (anchorVersion == true) { //主播版本
            mTab = new GradientIconTextView[2];
            mTab[0] = (GradientIconTextView) findViewById(R.id.messages_tab_text);
            mTab[1] = (GradientIconTextView) findViewById(R.id.find_tab_text);
            findViewById(R.id.rl_home_page).setVisibility(View.GONE);
//            findViewById(R.id.dynamicCenter_ly).setVisibility(View.GONE);
            findViewById(R.id.chat_bar_ly).setVisibility(View.GONE);
        } else {
            mTab = new GradientIconTextView[4];
            mTab[0] = (GradientIconTextView) findViewById(R.id.near_tab_text);
            mTab[1] = (GradientIconTextView) findViewById(R.id.chat_bar_tab_text);
//            mTab[2] = (GradientIconTextView) findViewById(R.id.dynamic_tab_text);
            mTab[2] = (GradientIconTextView) findViewById(R.id.messages_tab_text);
            mTab[3] = (GradientIconTextView) findViewById(R.id.find_tab_text);
        }

        findViewById(R.id.rl_home_page).setOnClickListener(this);
//        findViewById(R.id.dynamicCenter_ly).setOnClickListener(this);
        findViewById(R.id.live_main_ly).setOnClickListener(this);
        findViewById(R.id.messages_ly).setOnClickListener(this);
        findViewById(R.id.find_ly).setOnClickListener(this);

        mChatBarLy = (RelativeLayout) findViewById(R.id.chat_bar_ly);
        mChatBarLy.setOnClickListener(this);

        String openurl = SharedPreferenceUtil.getInstance(this).getString(SharedPreferenceUtil.START_PAGE_AD_OPENURL);
        boolean isClassExist = CloseAllActivity.getInstance().isActivityExist(WebViewAvtivity.class);
        if (!TextUtils.isEmpty(openurl) && !isClassExist) {
            Intent i = new Intent(this, WebViewAvtivity.class);
            i.putExtra(WebViewAvtivity.WEBVIEW_URL, openurl);
            i.putExtra(WebViewAvtivity.IS_SHOW_CLOSE, true);
            startActivityForResult(i, SHOW_WEB_VIEW_AD);
        } else {
            if (sInstance == null) {
                doAfterWebViewShow();
            }
        }

        sInstance = this;

        // 初始化UI
        loginTimer = LoginTimer.getInstance(BaseApplication.appContext);

        loginTimer.setLoginCallback(callback);

        initFragment();

        boolean isLogin = Common.getInstance().isUserLogin;
        CommonFunction.log("MainFragmentActivity", "isLogin = " + isLogin);
        // 获取广告中心和游戏平台是否可用标志
        if (isLogin) {
            initModule();
        }

        ZegoLiveRoom.setUser(String.valueOf(Common.getInstance().loginUser.getUid()), String.valueOf(Common.getInstance().loginUser.getNoteName(true)));

        // 设置推送tag
        if (!TextUtils.isEmpty(Common.getInstance().getUid()) && !Common.getInstance().getUid().equals("0")) {
            String tage;
            if (Config.DEBUG) {
                tage = "test" + Common.getInstance().getUid();
            } else {
                tage = "p" + Common.getInstance().getUid();
            }

            OnePush.bindAlias(tage);
        }

        //一对一视频禁止开启
//        initVideoChat();
        //开启webWebSocket
        if (Common.getInstance().loginUser.getVoiceUserType() == 1) {
            WebSocketManager.getsInstance().startWebSocketService(this);
        }

        //TODO
        FilterUtil.updateDeviceToken(this, httpCallBack);

        uploadLog(); //上传日志

        CommonFunction.log("MainFragmentActivity", "onCreate() out");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsDestroy == true) {
            CommonFunction.log("MainFragmentActivity", "onResume() activity is destroy");
            return;
        }
        CommonFunction.log("location receiver", "onResume");
        resetOtherTabs();
        freshAllCount();
        statisticsCount();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 初始化五个Fragment
     */
    private void initFragment() {
        adapter = new MainFragmentAdapter(getSupportFragmentManager(), anchorVersion);
        mViewPager.setAdapter(adapter);
        if (anchorVersion == true) {
            mViewPager.setOffscreenPageLimit(1);
        } else {
            mViewPager.setOffscreenPageLimit(4);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //实时刷新消息列表
                if (anchorVersion == true) {
                    if (position == 0) {
                        EventBus.getDefault().post("main_message");
                    }
                } else {
                    if (position == 3) {
                        EventBus.getDefault().post("main_message");
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (anchorVersion == true) {
            findViewById(R.id.find_ly).performClick();
        } else {
            switch (Common.getInstance().getDefaultTab()) {
                case 1:
                    findViewById(R.id.rl_home_page).performClick();
                    break;
                case 2:
                    findViewById(R.id.chat_bar_ly).performClick();
                    break;
                case 3:
                    findViewById(R.id.messages_ly).performClick();
                    break;
//                case 4:
//                    findViewById(R.id.dynamicCenter_ly).performClick();
//                    break;
                case 4:
                    findViewById(R.id.find_ly).performClick();
                    break;
            }
        }
    }

    /**
     * 选择当前的view
     *
     * @param view
     */
    @Override
    public void onClick(View view) {
        resetOtherTabs();
        int id = view.getId();
        if (id == R.id.rl_home_page) {//首页
            tabNear.setImageResource(R.drawable.z_main_near_sel);
//            tabDynamic.setImageResource(R.drawable.z_main_rank);
            tabMsg.setImageResource(R.drawable.z_main_msg);
            tabFind.setImageResource(R.drawable.z_main_find);
            tabLive.setImageResource(R.drawable.z_main_live);

            tabChatBar.setImageResource(R.drawable.z_main_chat_bar);
            setSelected(0);
            mViewPager.setCurrentItem(0, false);
//            if(tab1Fragement!=null) {
//                PagerSelectNear pagerSelectNear = tab1Fragement;
//                pagerSelectNear.onNearSelected();
//            }
        } else if (id == R.id.chat_bar_ly) {//聊吧
            tabNear.setImageResource(R.drawable.z_main_near);
//            tabDynamic.setImageResource(R.drawable.z_main_rank);
            tabMsg.setImageResource(R.drawable.z_main_msg);
            tabFind.setImageResource(R.drawable.z_main_find);
            tabLive.setImageResource(R.drawable.z_main_live);
            tabChatBar.setImageResource(R.drawable.z_main_chat_bar_sel);

            setSelected(1);
            mViewPager.setCurrentItem(1, false);
            if (tab5Fragment != null) {
                PagerSelectChat pagerSelectChat = tab5Fragment;
                pagerSelectChat.onChatSelected();
            }
        } else if (id == R.id.messages_ly) {//消息
            tabNear.setImageResource(R.drawable.z_main_near);
//            tabDynamic.setImageResource(R.drawable.z_main_rank);
            tabMsg.setImageResource(R.drawable.z_main_msg_sel);
            tabFind.setImageResource(R.drawable.z_main_find);
            tabLive.setImageResource(R.drawable.z_main_live);
            tabChatBar.setImageResource(R.drawable.z_main_chat_bar);
            if (anchorVersion == true) {
                setSelected(0);
                mViewPager.setCurrentItem(0, false);
                if (isFastPressTwice()) {
                    setSelected(0);
                    mViewPager.setCurrentItem(0, false);
                    if (isFastPressTwice()) {
                        if (tab3Fragement == null) {
                            tab3Fragement = new MessageFragmentIm();
                            tab3Fragement.isFastPressTwice(true);
                        } else {
                            tab3Fragement.isFastPressTwice(true);
                        }
                    }
                }
            } else {
                setSelected(2);
                mViewPager.setCurrentItem(2, false);
                if (isFastPressTwice()) {
                    setSelected(2);
                    mViewPager.setCurrentItem(2, false);
                    if (isFastPressTwice()) {
                        if (tab3Fragement == null) {
                            tab3Fragement = new MessageFragmentIm();
                            tab3Fragement.isFastPressTwice(true);
                        } else {
                            tab3Fragement.isFastPressTwice(true);
                        }
                    }
                }
            }
            //统计
            Statistics.onPageClick(Statistics.PAGE_MESSAGE);
        } else if (id == R.id.find_ly) {//个人
            tabNear.setImageResource(R.drawable.z_main_near);
//            tabDynamic.setImageResource(R.drawable.z_main_rank);
            tabMsg.setImageResource(R.drawable.z_main_msg);
            tabFind.setImageResource(R.drawable.z_main_find_sel);
            tabLive.setImageResource(R.drawable.z_main_live);
            tabChatBar.setImageResource(R.drawable.z_main_chat_bar);
            if (anchorVersion == true) {
                setSelected(1);
                mViewPager.setCurrentItem(1, false);
            } else {
                setSelected(3);
                mViewPager.setCurrentItem(3, false);
            }
            //统计
            Statistics.onPageClick(Statistics.PAGE_USER);
        }
//        else if (id == R.id.dynamicCenter_ly) {//排行榜
//
//            tabNear.setImageResource(R.drawable.z_main_near);
//            tabDynamic.setImageResource(R.drawable.z_main_rank_sel);
//            tabMsg.setImageResource(R.drawable.z_main_msg);
//            tabFind.setImageResource(R.drawable.z_main_find);
//            tabLive.setImageResource(R.drawable.z_main_live);
//            tabChatBar.setImageResource(R.drawable.z_main_chat_bar);
//            setSelected(2);
//            mViewPager.setCurrentItem(2, false);
//            if(tab2Fragement!=null) {
//                PagerSelectRanking pagerSelectRanking = tab2Fragement;
//                pagerSelectRanking.onRankingSelected();
//            }
//        }
    }


    /***
     * 判断是否是快速点击两次
     * @return
     */
    private boolean isFastPressTwice() {
        long time = System.currentTimeMillis();

        if (time - lastPressTime < 200) {
            return true;
        }
        lastPressTime = time;
        return false;
    }

    /**
     * 重置其他的Tab
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTab.length; i++) {
            mTab[i].setIconAlpha(0);
        }
    }

    public void setSelected(int position) {
        for (int i = 0; i < mTab.length; i++) {
            if (position == i) {
                mTab[i].setIconAlpha(1.0f);
            } else {
                mTab[i].setIconAlpha(0.0f);
            }
        }
    }

    @Override
    protected void onDestroy() {
        CommonFunction.log("MainFragmentActivity", "onDestroy() into");
        super.onDestroy();
        if (mIsDestroy == true) {
            CommonFunction.log("MainFragmentActivity", "mIsDestroy == true");
            return;
        }
        if (bIsLocaleChange) {
            Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            bIsLocaleChange = false;
        }
        // 清理好友缓存
        FriendModel.getInstance(mActivity).clearFriends();
        // 关闭地理位置监听器
        LocationUtil.stop(this);
        // 如果不是通过sso方式进入了遇见并finsh掉了本activity，则清理当前登录用户，并结束整个应用
        User chatData = (User) sInstance.getIntent().getSerializableExtra("ChatData");
        long uid = sInstance.getIntent().getLongExtra("SpaceData_uid", -1);
        if (uid < 0 && chatData == null) {
            if (Common.getInstance().loginUser != null) {
                Common.getInstance().loginUser.clear();
            }

            // 如果不是注销操作，则关闭所有activity，并结束进程
            if (!isLogout) {
                CloseAllActivity.getInstance().close();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        }
        CloseAllActivity.getInstance().close();
        loginTimer.reset();

        destroyVideoChat();
        destroyAudioChat();

        CommonFunction.log("MainFragmentActivity", "onDestroy() out");
    }

    public interface PagerSelectNear {
        void onNearSelected();
    }

    public interface PagerSelectNear1 {
        void onNearSelected(int pageMode);
    }

    public interface PagerSelectDynamic {
        void onDynamicSelected(int pageMode);
    }

    public interface PagerSelectMsg {
        void onMsgSelected(int pageMode);
    }

    public interface PagerSelectUser {
        void onMsgSelected(int pageMode);
    }

    public interface PagerSelectChat {
        void onChatSelected();
    }

    public interface PagerSelectRanking {
        void onRankingSelected();
    }

    // 刷新所有消息显示数量
    public void freshAllCount() {
        mMainHandler.sendEmptyMessage(FRESH_ALL_COUNT);

    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

            if (msg.what == FRESH_ALL_COUNT) {
                MenuBadge messagesBadge = MenuBadgeHandle.getInstance(BaseApplication.appContext).getMessagesMenuBadge(menuMessages);
                setBadgeView(messagesBadge.badgeView, messagesBadge.badgeType, messagesBadge.badgeNumber, messagesBadge.ivView);

                if (tab3Fragement instanceof PagerSelectMsg) {
                    tab3Fragement.onMsgSelected(PAGE_REFRESH);
                }
//                if (tab1Fragement instanceof NearsFragment) {
//                    tab1Fragement.onNearSelected(PAGE_REFRESH);
//                }
            } else if (msg.what == LONGIN_FAIL) {
                if (!isShowDialog) {
                    isShowDialog = true;
                    DialogUtil.showOKDialog(MainFragmentActivity.this, getString(R.string.prompt),
                            getResources().getString(R.string.e_4208), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ConnectLogin.getInstance(BaseApplication.appContext).logoutCacel(BaseApplication.appContext);
                                }
                            });
                }
            } else if (msg.what == LONGIN_TIME_OUT) {
                setMessageNetworkMode(StartModel.getInstance().getLoginConnnetStatus());
            } else if (msg.what == NET_CONNECT_SUCCESS) {
                setMessageNetworkMode(MessageFragmentIm.LOGIN_NET_WORK_STATUS_SUCCESS);
                ResendSendLikeOrReview();
            } else if (msg.what == LONGIN_CONNECTING) {
                setMessageNetworkMode(MessageFragmentIm.LOGIN_NET_WORK_CONNECTING);
            } else if (msg.what == LONGIN_SUCCESS) {
                setMessageNetworkMode(MessageFragmentIm.LOGIN_NET_WORK_STATUS_SUCCESS);
                Intent intent = new Intent();
                intent.setAction("net.iaround.ui.fragment");
                intent.putExtra("login", "LONGIN_SUCCESS");
                MainFragmentActivity.this.sendBroadcast(intent);
            } else if (msg.what == AUTO_UPLOAD_LOG) {
                String url = Config.loginUrl + "/rpc/upload";
                String token = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.IAROUND_SESSIONKEY);
                ;
                String user = String.valueOf(Common.getInstance().loginUser.getUid());
                CommonFunction.log("uploadlog", "upload log now");
                UploadZipFileUtils.upload(new UploadLogListener(), user, url, token);
            }
        }
    };

    /**
     * @param textView 显示的项
     * @param type     类型
     * @param count
     * @Title: setBadgeView
     * @Description: 显示底栏的标识
     */
    private void setBadgeView(final DragPointView textView, int type, final int count, ImageView imageView) {

        if (count <= 0) {
            textView.setVisibility(View.GONE);
        } else {
            textView.setDragListencer(new DragPointView.OnDragListencer() {
                @Override
                public void onDragOut() {
                    if (tab3Fragement != null)
                        tab3Fragement.markAllRead();
                }
            });

            switch (type) {
                case BADGE_NEW:
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.z_main_bt_flag);
                    break;
                case BADGE_ROUND:
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    break;
                case BADGE_NUMBER:
                    textView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    if (count <= 99) {
                        textView.setText(String.valueOf(count));
                    } else {
                        textView.setText("99+");
                    }
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    textView.setTextSize(11);
                    textView.setBackgroundColor(getResources().getColor(R.color.login_btn));
                    break;
                case BADGE_GONE:
                    textView.setBackgroundResource(0);
                    textView.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    break;
            }

            if (count <= 99) {
                textView.setText(String.valueOf(count));
            } else {
                textView.setText("99+");
            }

        }
    }

    /**
     * 设置登出标识
     **/
    public static void setLogout() {
        if (sInstance != null) {
            sInstance.isLogout = true;
        }
    }

    public void loginFail() {
        mMainHandler.sendEmptyMessage(LONGIN_FAIL);
    }

    public static boolean getIsLogot() {
        if (sInstance != null) {
            return sInstance.isLogout;
        }

        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            {
                // 返回系统桌面,遇见进入后台
                CommonFunction.log("demo", "返回桌面!!!");
                Intent mainInt = new Intent(Intent.ACTION_MAIN);
                mainInt.addCategory(Intent.CATEGORY_HOME);
                startActivity(mainInt);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void freshSpaceVipFlag() {
        if (tab4Fragement instanceof UserFragment) {
            tab4Fragement.onMsgSelected(PAGE_REFRESH);
        }
    }

    /**
     * 初始化模块
     */
    private void initModule() {
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取用户拥有的表情
                FaceCenterModel.getInstance(BaseApplication.appContext).getOwnFaceData(BaseApplication.appContext, null);

                // 获取隐私
                BusinessHttpProtocol.userPrivacyGet(BaseApplication.appContext, null);

                // 获取圈消息接收状态
                GroupHttpProtocol.getGroupMsgReceiveStatus(mContext, null);
                // 获取用户基本资料
                try {
                    SpaceModelNew.getInstance(BaseApplication.appContext).basicInforReq(MainFragmentActivity.this, Common.getInstance().loginUser.getUid(), 0, null);
                } catch (Throwable e) {
                }
            }
        }, 1000 * 10);

        new LocationUtil(mActivity).startListener(null, 1);

        // 开启Session登录
        ConnectSession.getInstance(BaseApplication.appContext).loginSession(null, true);
        ConnectGroup.getInstance(BaseApplication.appContext).loginGroup(null, true);
        ChatPersonalModel.getInstance().resendSendingMessage(BaseApplication.appContext);

        // 获取未读私聊消息数
        final ChatPersonalModel cpModel = ChatPersonalModel.getInstance();
        final String uid = String.valueOf(Common.getInstance().loginUser.getUid());
        Common.getInstance().setNoReadMsgCount(cpModel.countNoRead(mActivity, uid));

        // 未读好友邀请通知
        MessageModel.getInstance().getNoticeCount("" + Common.getInstance().loginUser.getUid(), mActivity);
        if (Common.getInstance().getNoReadMsgCount() > 0 || Common.getInstance().getNoticeCount() > 0) {
            freshAllCount();
        }

        // 15s后启动版本检查
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Config.SHOW_UPDATE_INSTALL) {
                    // 检查版本
                    LoginHttpProtocol.checkVersion(mActivity, 1, null);
                }
            }
        }, 1000 * 15);

        //gh 2s后启动获取广告
        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 获取广告
                LoginHttpProtocol.mainAdSource(mActivity, null);
            }
        }, 1000 * 2);


        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    ResendSendLikeOrReview();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000 * 5);

        mMainHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String extra = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.NOTIFICATION_MESSAGE_EXTAR);
                // 推送启动
                if (extra != null && !TextUtils.isEmpty(extra)) {
                    startActive(extra);
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.NOTIFICATION_MESSAGE_EXTAR, "");
                }
            }
        }, 200);

    }

    //处于前台时并且网络连接正常时，重发未发送的点赞
    private void ResendSendLikeOrReview() {
        if (CommonFunction.uiRunning && StartModel.getInstance().getLoginConnnetStatus() == 1) {
            try {
                DynamicModel.getInstent().ResendSendLike(this);
            } catch (Exception e) {
                CommonFunction.log("", "--->网络正常重发未发送的点赞  err==" + e);
            }
        }
    }

    private void setMessageNetworkMode(int mode) {
        if (MessageFragmentIm.instant != null) {
            MessageFragmentIm.instant.changeToNetworkMode(mode);
        }
    }

    private LoginCallback callback = new LoginCallback() {

        @Override
        public void netConnectSuccess() {
            mMainHandler.sendEmptyMessage(NET_CONNECT_SUCCESS);
            StartModel.getInstance().setLoginConnnetStatus(1);
        }

        @Override
        public void netConnectFail() {
            mMainHandler.sendEmptyMessage(NET_CONNECT_FAIL);
        }

        @Override
        public void autoLoginSuccess() {
            StartModel.getInstance().setLoginConnnetStatus(1);
            mMainHandler.sendEmptyMessage(LONGIN_SUCCESS);
        }

        @Override
        public void autoLoginFail(int type) {
            if (type == 0) {
                mMainHandler.sendEmptyMessage(LONGIN_TIME_OUT);
            } else {
                CommonFunction.log("MainFragmentActivity", "autoLoginFail =======LONGIN_FAIL");
                mMainHandler.sendEmptyMessage(LONGIN_FAIL);
            }
        }

        @Override
        public void autoLoginConnecting() {
            mMainHandler.sendEmptyMessage(LONGIN_CONNECTING);
        }
    };

    class MainFragmentAdapter extends FragmentPagerAdapter {
        private boolean mAnchorVersion = false;

        public MainFragmentAdapter(FragmentManager fm, boolean anchorVersion) {
            super(fm);
            mAnchorVersion = anchorVersion;
        }

        @Override
        public Fragment getItem(int position) {
            CommonFunction.log("MainFragmentAdapter", "getItem() position=" + position);
            if (mAnchorVersion == true) {
                if (position == 0) {
                    if (tab3Fragement == null) {
                        tab3Fragement = new MessageFragmentIm();
                    }
                    return tab3Fragement;
                } else if (position == 1) {
                    if (tab4Fragement == null) {
                        tab4Fragement = new UserFragment();
                    }
                    return tab4Fragement;
                } else {
                    return null;
                }
            } else {
                if (position == 0) {
                    if (tab1Fragement == null) {
                        tab1Fragement = new HomeContainerFragment();
                    }
                    return tab1Fragement;
                } else if (position == 1) {
                    if (tab5Fragment == null) {
                        tab5Fragment = new ChatBarFragment();
                    }
                    return tab5Fragment;
                } else if (position == 2) {
                    if (tab3Fragement == null) {
                        tab3Fragement = new MessageFragmentIm();
                    }
                    return tab3Fragement;
                } else {
                    if (tab4Fragement == null) {
                        tab4Fragement = new UserFragment();
                    }
                    return tab4Fragement;
                }
//                else if (position == 2) {
//                    if (tab2Fragement == null) {
//                        tab2Fragement = new RankingFragment();
//                    }
//                    return tab2Fragement;
//                }
            }
        }

        @Override
        public int getCount() {
            if (mAnchorVersion == true) {
                return 2;
            }
            return 4;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (SHOW_WEB_VIEW_AD == requestCode) {
            doAfterWebViewShow();
        }
    }

    private void doAfterWebViewShow() {
        //获取缓存中的圈消息设置
        GroupAffairModel.getInstance().getStatusFromFile();
        //获取缓存中未发送成功的点赞数据
        DynamicModel.getInstent().updataCacheData(mContext);

    }

    private void statisticsCount() {
        boolean splashIsShow = SharedPreferenceCache.getInstance(BaseApplication.appContext).getBoolean("splashIsShow");
        if (splashIsShow) {
            SharedPreferenceCache.getInstance(BaseApplication.appContext).putBoolean("splashIsShow", false);
        } else {
            startUpCount = SharedPreferenceCache.getInstance(BaseApplication.appContext).getInt("startUpCount", 0);
            startUpCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(BaseApplication.appContext, "7", "7", "" + startUpCount, "0", null);
            SharedPreferenceCache.getInstance(BaseApplication.appContext).putInt("startUpCount", startUpCount);

        }

    }

    /**
     * 启动这个Activity
     *
     * @param extras
     */
    private void startActive(String extras) {
        try {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(extras);
            if (null == jsonObject) {
                return;
            }
            String notificationType = jsonObject.getString("notificationType"); //技能10   搭讪11   私聊4  动态点赞5   动态评论6
            int type = 0;
            if (notificationType != null && !TextUtils.isEmpty(notificationType)) {
                type = Integer.valueOf(notificationType);
            }


            if (jsonObject.containsKey("receiveUid")) {
                long receiveUId = jsonObject.getLong("receiveUid");
                if (receiveUId != Common.getInstance().loginUser.getUid()) {
                    type = 0;
                }
            }

            switch (type) {
                case 11:
                    Intent chatGameIntent = new Intent(this, ChatGameActivity.class);
                    chatGameIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(chatGameIntent);
                    break;
                case 4:
                case 10:
                    String json = jsonObject.getString("senderUser");
                    String ralationship = jsonObject.getString("ralationship");

                    JPushUser senderUser = GsonUtil.getInstance().getServerBean(json, JPushUser.class);

                    // 是否从聊吧呼起个人资料页面
                    User tempuser = new User();
                    tempuser.setUid(senderUser.getUserid());
                    tempuser.setSVip(senderUser.getVip());
                    tempuser.setIcon(senderUser.getIcon());
                    tempuser.setViplevel(senderUser.getViplevel());
                    tempuser.setAge(senderUser.getAge());
                    tempuser.setSex(senderUser.getGender().equals("m") ? 1 : 0);
                    tempuser.setBirth(senderUser.getBirthday());
                    tempuser.setLat(senderUser.getLat());
                    tempuser.setLng(senderUser.getLng());
                    tempuser.setNoteName(senderUser.getNotes());
                    tempuser.setNickname(senderUser.getNickname());
                    if (ralationship != null && !TextUtils.isEmpty(ralationship)) {
                        tempuser.setRelationship(Integer.valueOf(ralationship));
                    }

                    ChatPersonal.skipToChatPersonalReceiver(this, tempuser);
                    break;
                case 5:
                case 6:
                    // 动态消息
                    Intent intent = new Intent(this, DynamicMessagesActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    break;
                case 1:
                    String systemUrl = jsonObject.getString("systemMessage");

                    if (systemUrl != null && systemUrl.length() > 0) {
                        WebViewAvtivity.launchVerifyCode(MainFragmentActivity.this, systemUrl);
                    }

                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /*处理缓存消息
     * */

    @Override
    public ArrayList<PushMessage> handleCacheMessage(ArrayList<PushMessage> pushMessages) {
        Logger.i("VideoChat", "处理缓存的推送消息");
        if (pushMessages == null) {
            return null;
        }
        ArrayList<PushMessage> result = new ArrayList<>(0);

        HashMap<Long, Boolean> cancelRooms = new HashMap<Long, Boolean>(); //不用处理的消息，用户已经挂掉邀请
        HashMap<Long, ArrayList<Integer>> rooms = new HashMap<Long, ArrayList<Integer>>(); //房间号
        for (int i = 0; i < pushMessages.size(); i++) {
            PushMessage msg = pushMessages.get(i);
            if (msg.cmdId == Iachat.CMD_ID_PUSH_INVITE_VIDEO) {
                try {
                    final Iavchat.PushInviteVideo invite = Iavchat.PushInviteVideo.parseFrom(msg.buffer);
                    if (null != invite) {
                        cancelRooms.put(invite.roomid, false);
                        ArrayList<Integer> room = rooms.get(invite.roomid);
                        if (room == null) {
                            room = new ArrayList<Integer>(0);
                            rooms.put(invite.roomid, room);
                        }
                        room.add(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("VideoChat", "处理缓存的视频邀请推送消息异常：" + e.toString());
                }
            } else if (msg.cmdId == Iachat.CMD_ID_PUSH_START_VIDEO) {
                try {
                    final Iavchat.PushStartVideo start = Iavchat.PushStartVideo.parseFrom(msg.buffer);
                    if (null != start) {
                        ArrayList<Integer> room = rooms.get(start.roomid);
                        if (room == null) {
                            room = new ArrayList<Integer>(0);
                            rooms.put(start.roomid, room);
                        }
                        room.add(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("VideoChat", "处理缓存的开始推拉流推送消息异常：" + e.toString());
                }
            } else if (msg.cmdId == Iachat.CMD_ID_PUSH_BLUR_VIDEO) {
                try {
                    final Iavchat.PushBlurVideo blur = Iavchat.PushBlurVideo.parseFrom(msg.buffer);
                    if (null != blur) {
                        ArrayList<Integer> room = rooms.get(blur.roomid);
                        if (room == null) {
                            room = new ArrayList<Integer>(0);
                            rooms.put(blur.roomid, room);
                        }
                        room.add(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("VideoChat", "处理缓存的模糊推送消息异常：" + e.toString());
                }

            } else if (msg.cmdId == Iachat.CMD_ID_PUSH_PAY_VIDEO) {
                try {
                    final Iavchat.PushPayVideo pay = Iavchat.PushPayVideo.parseFrom(msg.buffer);
                    if (null != pay) {
                        ArrayList<Integer> room = rooms.get(pay.roomid);
                        if (room == null) {
                            room = new ArrayList<Integer>(0);
                            rooms.put(pay.roomid, room);
                        }
                        room.add(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("VideoChat", "处理缓存的支付通知推送消息异常：" + e.toString());
                }
            } else if (msg.cmdId == Iachat.CMD_ID_PUSH_CLOSE_VIDEO) {
                try {
                    final Iavchat.PushCloseVideo close = Iavchat.PushCloseVideo.parseFrom(msg.buffer);
                    if (null != close) {
                        cancelRooms.put(close.roomid, true);
                        ArrayList<Integer> room = rooms.get(close.roomid);
                        if (room == null) {
                            room = new ArrayList<Integer>(0);
                            rooms.put(close.roomid, room);
                        }
                        room.add(i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.e("VideoChat", "处理缓存的结束视频推送消息异常：" + e.toString());
                }
            } else {
                Logger.i("VideoChat", "处理其他缓存的推送消息，cmdid=" + msg.cmdId);
            }
        }

        int count = 0;
        //遍历已经被取消的会话，只留存未取消的会话消息
        Iterator iterator = cancelRooms.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Long roomid = (Long) entry.getKey();
            Boolean cancel = (Boolean) entry.getValue();
            if (cancel == true) {
                count++;
            } else {
                ArrayList<Integer> msgIndex = rooms.get(roomid);
                for (int i = 0; i < msgIndex.size(); i++) {
                    Integer pos = msgIndex.get(i);
                    result.add(pushMessages.get(pos));
                }
            }
        }

        if (count > 0) {
            final int fcount = count;
            mMainHandler.post(new Runnable() {
                @Override
                public void run() {
                    CommonFunction.toastMsgLong(BaseApplication.appContext, "由于您未及时应答，已经有" + fcount + "个呼叫被用户取消");
                }
            });
        }
        Logger.i("VideoChat", "剩余的未处理的缓存推送消息数：" + result.size());
        return result;
    }

    /* 处理推送消息
     * */
    @Override
    public boolean handleReceiveMessage(PushMessage pushMessage) {
        if (pushMessage.cmdId == STNManager.PUSH_COMMAND_LONG_LINK_CONNECTED) {
            try {
                Logger.i("VideoChat", "收到网络连接推送消息, 发送登陆任务");
                //登陆视频服务
                LoginTaskWrapper task = new LoginTaskWrapper(this);
                task.getProperties().putLong("uid", Common.getInstance().loginUser.getUid());
                task.getProperties().putInt("authType", Iachat.AUTH_TOKEN);
                STNManager.startTask(task);

            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("VideoChat", "处理网络连接推送消息异常");
            }
        } else if (pushMessage.cmdId == Iachat.CMD_ID_PUSH_LOGIN_VIDEO) {
            try {
                Logger.i("VideoChat", "收到重登陆推送消息");
                //登陆视频服务
                long user = Common.getInstance().loginUser.getUid();
                Logger.i("VideoChat", "发送登陆任务, user=" + user);
                LoginTaskWrapper task = new LoginTaskWrapper(this);
                task.getProperties().putLong("uid", user);
                task.getProperties().putInt("authType", Iachat.AUTH_TOKEN);
                STNManager.startTask(task);

            } catch (Exception e) {
                e.printStackTrace();
                CommonFunction.log("VideoChat", "处理重登陆推送消息异常");
            }
        } else if (pushMessage.cmdId == Iachat.CMD_ID_PUSH_INVITE_VIDEO) {
            //视频会话邀请
            Logger.i("VideoChat", "收到视频会话邀请推送消息");
            try {
                final Iavchat.PushInviteVideo msg = Iavchat.PushInviteVideo.parseFrom(pushMessage.buffer);
                if (null != msg) {
                    if (VideoChatManager.getInstance().isIdle()) {
                        Logger.i("VideoChat", "空闲状态");
                        if (msg.to == Common.getInstance().loginUser.getUid()) {
                            //如果主播在聊吧里,先关闭聊吧窗口
                            GroupChatTopicActivity chatbar = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                            if (chatbar != null) {
                                Logger.i("VideoChat", "关闭聊吧窗口");
                                chatbar.isGroupIn = true;
                                CloseAllActivity.getInstance().closeTarget(GroupChatTopicActivity.class);
                            }

                            //对方用户信息
                            VideoChatManager.VideoChatUser other = new VideoChatManager.VideoChatUser();
                            other.follow = msg.follow;
                            other.svip = msg.svip;
                            other.vip = msg.vip;
                            other.uid = msg.from;
                            other.city = msg.city;
                            other.icon = msg.icon;
                            other.name = msg.name;
                            VideoChatManager.getInstance().setOther(other);

                            //当前用户信息
                            VideoChatManager.VideoChatUser current = new VideoChatManager.VideoChatUser();
                            current.uid = Common.getInstance().loginUser.getUid();
                            current.name = Common.getInstance().loginUser.getNickname();
                            current.icon = Common.getInstance().loginUser.getIcon();
                            VideoChatManager.getInstance().setCurrent(current);

                            //房间号
                            VideoChatManager.getInstance().setRoom(msg.roomid);
                            //会话类型
                            VideoChatManager.getInstance().setType(VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL);
                            //会话状态
                            VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE);

                            Logger.i("VideoChat", "即将呼起视频会话界面, 主叫=" + other.uid + ", 被叫=" + current.uid + ", 房间号=" + msg.roomid);
                            //跳去视频会话页面
                            mMainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(BaseApplication.appContext, VideoChatActivity.class);
                                    Activity activity = CloseAllActivity.getInstance().getTopActivity();
                                    if (activity != null) {
                                        activity.startActivity(intent);
                                    } else {
                                        VideoChatManager.getInstance().reset();
                                    }
                                }
                            });
                        } else {
                            Logger.i("VideoChat", "被叫号码不是当前用户, msg.to=" + msg.to + ", login user=" + Common.getInstance().loginUser.getUid());
                            RejectVideoTaskWrapper task = new RejectVideoTaskWrapper(null);
                            task.getProperties().putLong("roomid", msg.roomid);
                            STNManager.startTask(task);
                        }
                    } else {
                        Logger.i("VideoChat", "非空闲状态");
                        if (msg.to == Common.getInstance().loginUser.getUid()) {
                            //非空闲状态正在会话
                            if (VideoChatManager.getInstance().getRoom() != msg.roomid) {
                                if (msg.from == VideoChatManager.getInstance().getOther().uid) {
                                    //如果上一个用户连续发起2次视频邀请消息
                                    Logger.i("VideoChat", "连续收到两次视频邀请，当前房间号:" + VideoChatManager.getInstance().getRoom() + ", 新房间号:" + msg.roomid);
                                    if (VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE) {
                                        Logger.i("VideoChat", "被叫未接听,重置房间号");
                                        VideoChatManager.getInstance().setRoom(msg.roomid);
                                    } else {
                                        CloseVideoTaskWrapper task = new CloseVideoTaskWrapper(null);
                                        task.getProperties().putLong("roomid", msg.roomid);
                                        task.getProperties().putInt("close_state", Iavchat.CLOSE_STATE_USER_BUSY);
                                        STNManager.startTask(task);
                                    }
                                } else {
                                    Logger.i("VideoChat", "当前用户正在通话，拒绝被叫号码：" + msg.from + ", 房间号:" + msg.roomid);
                                    CloseVideoTaskWrapper task = new CloseVideoTaskWrapper(null);
                                    task.getProperties().putLong("roomid", msg.roomid);
                                    task.getProperties().putInt("close_state", Iavchat.CLOSE_STATE_USER_BUSY);
                                    STNManager.startTask(task);
                                }
                            } else {
                                Logger.i("VideoChat", "收到相同的视频会话邀请(相同房间号) from:" + msg.from + ", room:" + msg.roomid);
                            }
                        } else {
                            Logger.i("VideoChat", "被叫号码不是当前用户, msg.to=" + msg.to + ", login user=" + Common.getInstance().loginUser.getUid());
                            RejectVideoTaskWrapper task = new RejectVideoTaskWrapper(null);
                            task.getProperties().putLong("roomid", msg.roomid);
                            STNManager.startTask(task);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("VideoChat", "处理视频会话邀请推送消息异常");
            }
        } else if (pushMessage.cmdId == Iachat.CMD_ID_PUSH_FINISH_VIDEO) {
            //视频会话历史记录
            Logger.i("VideoChat", "收到视频会话历史记录推送消息");
            try {
                final Iavchat.PushVideoFinish msg = Iavchat.PushVideoFinish.parseFrom(pushMessage.buffer);
                if (null != msg) {
                    //注意除重 roomid 唯一
                    Logger.i("VideoChat", "收到视频会话记录推送消息 from=" + msg.from + ", fromName=" + msg.fromName + ", fromIcon=" + msg.fromIcon + ", fromVip=" + msg.fromVip + ", fromSvip=" + msg.fromSvip + ", to=" + msg.to + ", toName=" + msg.toName + ", toIcon=" + msg.toIcon + ", toVip=" + msg.toVip + ", toSvip=" + msg.toSvip + ", seconds=" + msg.seconds + ", state=" + msg.state + ", roomid=" + msg.roomid + ", ended=" + msg.ended + ", fanchor =" + msg.fromAnchor + ", toanchor =" + msg.toAnchor);
                    if (msg.from == Common.getInstance().loginUser.getUid()) {
                        VideoChatModel.getInstance().insertData(this, (int) msg.to, msg.toName, msg.toName, msg.toIcon, msg.toVip, msg.toSvip, 0, 0, msg.seconds, msg.state, (int) msg.from, msg.ended, msg.toAnchor);
                    } else if (msg.to == Common.getInstance().loginUser.getUid()) {
                        VideoChatModel.getInstance().insertData(this, (int) msg.from, msg.fromName, msg.fromName, msg.fromIcon, msg.fromVip, msg.fromSvip, 0, 0, msg.seconds, msg.state, (int) msg.from, msg.ended, msg.fromAnchor);
                    } else {
                        Logger.w("VideoChat", "收到视频会话历史记录推送消息和当前用户无关");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("VideoChat", "处理视频会话历史记录推送消息异常");
            }
        } else {
            Logger.w("VideoChat", "收到其他推送消息：cmdid=" + pushMessage.cmdId);
        }

        return false;
    }

    /*处理任务响应
     * */

    @Override
    public void onTaskEnd(NanoMarsTaskWrapper taskWrapper, int errType, int errCode, MessageNano request, MessageNano response) {
        if (taskWrapper instanceof LoginTaskWrapper) {
            Iachat.LoginRsp rsp = (Iachat.LoginRsp) response;
            if (errType == 0 && errCode == 0 && rsp != null) {
                if (rsp.errCode == 0) {
                    Logger.i("VideoChat", "登陆任务响应成功");
                } else {
                    Logger.i("VideoChat", "登陆任务响应业务失败");
                }
            } else {
                Logger.w("VideoChat", "登陆任务网络错误");
            }
        } else if (taskWrapper instanceof LogoutTaskWrapper) {
            Iachat.LogoutRsp rsp = (Iachat.LogoutRsp) response;
            if (errType == 0 && errCode == 0 && rsp != null) {
                if (rsp.errCode == 0) {
                    Logger.i("VideoChat", "登出任务响应成功");
                } else {
                    Logger.i("VideoChat", "登出任务响应业务失败");
                }
            } else {
                Logger.w("VideoChat", "登出任务网络错误");
            }
        }
    }

    private void initVideoChat() {
        Logger.i("VideoChat", "initVideoChat() into");

        //主播开启并视频服务，主播登出的时候停止服务
        if (VideoChatManager.getInstance().loginUserIsAnchor() == true) { //当前用户是主播
            //启动IM服务
            Logger.i("VideoChat", "启动通讯服务");
            STNManager.start(Config.IM_HOST_NAME, Config.IM_HOST_PORT, true, true);

            //缓存消息处理器
            STNManager.addCacheMessageHandler(this);

            //推送消息处理器
            //连接状态消息
            //STNManager.addPushMessageHandler(STNManager.PUSH_COMMAND_LONG_LINK_CONNECTED, this);
            //重登陆消息
            STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_LOGIN_VIDEO, this);
            //视频会话邀请
            STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_INVITE_VIDEO, this);
            //视频会话历史消息
            STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_FINISH_VIDEO, this);

            //登陆视频服务
            long user = Common.getInstance().loginUser.getUid();
            Logger.i("VideoChat", "发送登陆任务, user=" + user);
            LoginTaskWrapper task = new LoginTaskWrapper(this);
            task.getProperties().putLong("uid", user);
            task.getProperties().putInt("authType", Iachat.AUTH_TOKEN);
            STNManager.startTask(task);

            mVideoChatInit = true;
        }
    }

    private void destroyVideoChat() {
        Logger.i("VideoChat", "destroyVideoChat() into");

        if (VideoChatManager.getInstance().loginUserIsAnchor() || mVideoChatInit) { //如果是主播
            //注销推送消息监听
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_INVITE_VIDEO, this);
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_LOGIN_VIDEO, this);
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_FINISH_VIDEO, this);
            //STNManager.removePushMessageHandler(STNManager.PUSH_COMMAND_LONG_LINK_CONNECTED, this);

            //注销缓存消息监听
            STNManager.removeCacheMessageHandler(this);

            //登出视频服务
            long user = Common.getInstance().loginUser.getUid();
            Logger.i("VideoChat", "发送登出任务, user=" + user);
            LogoutTaskWrapper task = new LogoutTaskWrapper(this);
            task.getProperties().putLong("uid", user);
            STNManager.startTask(task);

            //停止通讯服务
            Logger.i("VideoChat", "停止通讯服务");
            STNManager.stop();
        }
    }

    private void destroyAudioChat() {
        if (Common.getInstance().loginUser.getVoiceUserType() == 1 || AudioChatManager.getsInstance().isHasLogin()) {
            WebSocketManager.getsInstance().stopWebSocketService(this);
        }
    }

    /**
     * 更新设备回调
     */
    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
            if (!bean.isSuccess() && mContext != null) {
                mUpdateDeviceTokenCount++;
                if (mUpdateDeviceTokenCount <= 3) {
                    FilterUtil.updateDeviceToken(mContext, httpCallBack);
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            if (mContext != null) {
                mUpdateDeviceTokenCount++;
                if (mUpdateDeviceTokenCount <= 3) {
                    FilterUtil.updateDeviceToken(mContext, httpCallBack);
                }
            }
        }
    };

    /*上传日志到服务器
     * 上传条件：该用户的日志上传开关打开，且24小时内只传一次
     * */
    private void uploadLog() {
        if (Common.getInstance().getDebugSwitch() == 1) {
            String key = SharedPreferenceUtil.UPLOAD_LOG_LAST_TIME + Common.getInstance().loginUser.getUid();
            long lastTime = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(key, -1L);
            long currentTime = System.currentTimeMillis();
            CommonFunction.log("uploadlog", "lastTime=" + lastTime + ", currentTime=" + currentTime);
            if (lastTime == -1L || (currentTime - lastTime) >= 24 * 60 * 60 * 1000) {
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(key, currentTime);
                CommonFunction.log("uploadlog", "bingo time to upload log");
                Message message = mMainHandler.obtainMessage(AUTO_UPLOAD_LOG);
                mMainHandler.sendMessageDelayed(message, 30 * 1000);
            }
        }
    }

    static class UploadLogListener implements UploadZipFileUtils.IUploadListener {
        @Override
        public void onUploadResult(int state) {
            CommonFunction.log("uploadlog", "upload log state=" + state);
        }
    }
}



