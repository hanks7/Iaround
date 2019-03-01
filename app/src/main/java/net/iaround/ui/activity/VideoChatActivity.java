package net.iaround.ui.activity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.request.target.Target;
import com.google.protobuf.nano.MessageNano;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.SendWorldMessageProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.im.STNManager;
import net.iaround.im.proto.Iachat;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.push.IPushMessageHandler;
import net.iaround.im.push.PushMessage;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.mic.FlyVideoRoom;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.Me;
import net.iaround.model.chatbar.ChatBarBackpackBean;
import net.iaround.model.chatbar.ChatBarSendPacketGiftBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.VideoChatModel;
import net.iaround.ui.group.GiftQueueHandler;
import net.iaround.ui.group.activity.SpaceReport;
import net.iaround.ui.group.bean.GiftComponentBean;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.interfaces.VideoChatPersonalSocketListenter;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.LuxuryGiftView;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.ui.view.pipeline.PipelineGiftView;
import net.iaround.utils.logger.Logger;
import net.iaround.videochat.VideoChatManager;
import net.iaround.videochat.task.BlurVideoTaskWrapper;
import net.iaround.videochat.task.CancelVideoTaskWrapper;
import net.iaround.videochat.task.CloseVideoTaskWrapper;
import net.iaround.videochat.task.ConfirmVideoTaskWrapper;
import net.iaround.videochat.task.InviteVideoTaskWrapper;
import net.iaround.videochat.task.LoginTaskWrapper;
import net.iaround.videochat.task.LogoutTaskWrapper;
import net.iaround.videochat.task.PayTaskWrapper;
import net.iaround.videochat.task.RejectVideoTaskWrapper;
import net.iaround.videochat.task.StartVideoTaskWrapper;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.dialog.CustomGiftDialog;

/**
 * 视频一对一Activity
 *
 * Created by liangyuanhuan on 01/12/2017.
 */


public class VideoChatActivity extends BaseActivity implements ITaskEndListener, IPushMessageHandler {
    private static final String TAG = "VideoChatActivity";
    private static final int PAY_VIDEO_TIMER_INTERNAL = 60000; //定时扣费上报的间隔 10秒
    private static final int PAY_VIDEO_TIMER_INTERNAL_MAX_DELAY = 5000; //检测是否收到扣费通知的最大延迟间隔 5秒
    private static final int SHOW_COUNTDOWN_MIN_SECONDS = 180; //倒计时显示最少时间 180秒
    public static final int LOVE_PAY_REQUEST_CODE = 8000; //跳转去爱心支付时候的请求码
    private static final int NO_RESPONSE_TIMER_INTERNAL = 1000; //无应答定时器间隔 1秒
    private static final int NO_RESPONSE_NOTIFY_SECONDS = 30000; //无应答提示 20秒
    private static final int NO_RESPONSE_CLOSE_VIDEO_CHAT_SECONDS = 60000; //无应答结束视频会话 60秒
    private static final int SHOW_CLOSE_VIEW_SECONDS = 60000; //主播显示关闭视频按钮 60秒
    private static final int RECEIVE_START_VIDEO_SECONDS = 15000; //同意视频会话后多长时间收到推拉流消息 15秒
    private static final int START_VIDEO_PULL_RESULT_SECONDS = 15000; //收到推拉流消息后多长时间能收到即构SDK推拉流的回调 15秒

    private Handler mHandler = new UIMainHandler(this);

    //视频相关
    private FlyVideoRoom mFlyVideoRoom=null; //推拉流封装
    private PowerManager.WakeLock mWakeLock; //屏幕锁

    //视频view
    private RelativeLayout mVideoHolder; //大小视频 holder
    private SurfaceView mSmallVideo; //小视频 view
    private SurfaceView mBigVideo; //大视频 view
    private View mBlurView = null; //模糊自己的遮挡物
    private View mBlurUserView = null; //模糊对方的遮挡物

    //登陆用户发起呼叫但未接通前的UI
    private RelativeLayout mSendCallOtherRL; //被叫接通前的头像和状态布局
    private HeadPhotoView mSendCallOtherIconHV; //被叫接通前的头像
    private TextView mSendCallOtherNameTV; //被叫接通前的名字
    private TextView mSendCallOtherStateTV; //被叫接通前的状态
    private RelativeLayout mSendCallToolRL; //主叫未接通前的工具条布局
    private ImageView mSendCallToolBeautyIV; //主叫未接通前的工具条美颜按钮
    private TextView mSendCallToolBeautyTV; //主叫未接通前的工具条美颜开关文字
    private ImageView mSendCallToolHangupIV; //主叫未接通前的工具条挂断
    private ImageView mSendCallToolBlurIV; //主叫未接通前的工具条模糊按钮
    private TextView mSendCallToolBlurTV; //主叫未接通前的工具条模糊文字
    private boolean mSendCallToolHangupIVClick = false;  //邀请会话时是否点击了挂断功能按钮


    //登陆用户收到呼叫时的UI
    private ImageView mReceiveCallOtherBackground;  //来电前来电方的背景图
    private LinearLayout mReceiveCallOtherLL;  //来电前来电方的信息布局
    private HeadPhotoView mReceiveCallOtherIcon;  //来电前来电方的头像
    private TextView mReceiveCallOtherName;  //来电前来电方的名字
    private LinearLayout mReceiveCallToolLL;  //来电前工具条
    private ImageView mReceiveCallHangup;  //来电前挂断按钮
    private ImageView mReceiveCallAnswer;  //来电前接听按钮
    private boolean mReceiveCallAnswerClick = false;  //是否点击了来电前接听按钮

    //呼叫中的UI
    private RelativeLayout mCallingOtherRL;  //对方用户信息布局
    private HeadPhotoView mCallingOtherHead; //对方用户头像
    private TextView mCallingOtherName; //对方用户名字
    private TextView mCallingOtherAddress; //对方用户地址
    private TextView mCallingOtherFollow; //对方用户关注
    private ImageView mCallingReportUserIV; //举报用户
    private ImageView mCallingCloseVideoIV; //关闭视频
    private LinearLayout mCallingToolLL; //主叫和被叫接通后的工具条
    private LinearLayout mCallingToolLeftTimeLL; //倒计时提示
    private ProgressBar mCallingToolLeftTimePB; //倒计时进度条
    private TextView mCallingToolLeftTimeSeconds; //倒计时提示
    private LinearLayout mCallingToolBeautyLL; //美颜按钮布局
    private ImageView mCallingToolBeautyIV; //美颜按钮
    private ImageView mCallingToolCameraIV; //美颜按钮
    private LinearLayout mCallingToolBlurLL; //模糊按钮布局
    private ImageView mCallingToolBlurIV; //模糊按钮
    private LinearLayout mCallingToolAddTimeLL; //加时按钮布局
    private ImageView mCallingToolAddTimeIV; //加时按钮
    private LinearLayout mCallingToolPresentLL; //送礼按钮布局
    private ImageView mCallingToolPresentIV; //送礼按钮
    private boolean mCallingOtherFollowClick = false;  //是否点击了关注按钮
    private RelativeLayout mCallingChatTimeRL;  //通话时长布局
    private TextView mCallingChatTimeTV;      //通话时长
    private RelativeLayout mCallingLoveRL;  //爱心数量布局
    private TextView mCallingLoveTV;      //爱心数量

    //礼物ui
    private PipelineGiftView pipelineView1;
    private PipelineGiftView pipelineView2;
    private PipelineGiftView pipelineView3;
    private LuxuryGiftView luxuryGiftView;

    // 礼物动画
    private GiftQueueHandler giftQueueHandler;

    private boolean mBeautyEnable = true; //美颜功能默认开
    private boolean mBlurEnable = false; //模糊功能默认关
    private boolean mBluring = false; //正在点击模糊功能按钮

    //定时扣费上报结果
    private Timer mPayTimer = null; //定时器
    private PayTimerTask mPayTimerTask = null; //主叫使用
    private CheckPayTimerTask mCheckPayTimerTask = null; //被叫使用

    //收到定时扣费通知的时间
    private volatile long mPushPayVideoTime = -1;

    //倒计时
    private Timer mCountdownTimer = null; //倒计时定时器
    private CountdownTimerTask mCountdownTimerTask = null; //倒计时定时器任务
    private int mCountdownLeftTime = -1; //剩余时间,每秒减一 单位秒
    private int mCountdownLeftTimeFinal = -1; //剩余时间 单位秒
    private Animation mButtonShakeAnimation = null;

    //来电后是否需要恢复推流
    private boolean mNeedResumePublishing = false;

    //对方无应答提示
    private Timer mNoResponseTimer = null; //对方无应答20秒提示及60秒定时器
    private NoResponseTimerTask mNoResponseTimerTask = null;
    private int mNoResponseTimeCount = 0;  //每次10秒累加

    private String mResourceStringVideoEndLeftTime = null; //资源字符串
    private long mLastNetworkQualityNotifyTime = 0; //提示网络质量不好的时间戳

    private boolean mInviteSend = false; //是否已经发送过 invite消息

    private Timer mVideoChatTimer = null; //会话期间的全局定时器（会话时长）
    private VideoChatTimerTask mVideoChatTimerTask = null;
    private AtomicInteger mShowCloseVideoViewTimeCount = new AtomicInteger(-1); //被叫60秒显示关闭视频按钮
    private AtomicInteger mStartVideoTimeCount = new AtomicInteger(-1); //被叫同意视频会话后10秒内要收到开始推拉流消息
    private AtomicInteger mPullResultTimeCount = new AtomicInteger(-1); //收到推拉流消息后10秒内能收到即构SDK推拉流的回调

    private CustomGiftDialog mCustomGiftDialog = null; //送礼弹窗

    private boolean mRefreshChatTime = false; //是否开始刷新视频会话时间
    private int mVideoChatTotalTimeCount = 0;  //会话时长，单位秒
    private int mVideoChatTotalLoveCount = 0;  //爱心总数

    private View.OnClickListener mOnClickSmallVideoView = null; //点击小头像事件
    private boolean mCurrentUserInSmallVideoView = true; //当前用户在小视图

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.i(TAG, "onCreate() into");

        setContentView(R.layout.activity_video_chat);

        initView();

        initListener();

        initVideo();

        initCall();

        updateAccostRelation();

        Logger.i(TAG, "onCreate() out");
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        acquireWakeLock();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onStop() {
        super.onStop();
        releaseWakeLock();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Logger.i(TAG, "onDestroy() into");

        try {
            destroyVideo();

            destroyCall();

            if (mHandler != null) {
                Logger.d(TAG, "onDestroy() remove git send runnable");
                mHandler.removeCallbacks(sendGiftRun);
                mHandler.removeCallbacks(runnableHandle);
            }
            if (giftQueueHandler != null) {
                giftQueueHandler.setMainHandle(null);
                giftQueueHandler.setOnHeadViewClickListener(null);
                giftQueueHandler.release();
                giftQueueHandler = null;
            }

            //移除缓存中保存的最后一个礼物位置
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialog.LAST_LOVE_GIFT_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialog.LAST_LOVE_GIFT_ARRAY_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialog.LAST_LOVE_GIFT_ARRAY_VALUE);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialog.LIST_STORE_CACHE);

            mCallingToolAddTimeIV.clearAnimation();

            if(mCustomGiftDialog!=null){
                mCustomGiftDialog.dismiss();
                mCustomGiftDialog = null;
            }
        }catch (Exception ex){
            Logger.e(TAG, "onDestroy() error happen");
            ex.printStackTrace();
        }
        Logger.i(TAG, "onDestroy() out");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleOnKeycodeBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Logger.i(TAG, "onActivityResult() into, requestCode=" + requestCode + ", resultCode="+resultCode);

        if(requestCode == LOVE_PAY_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            //支付成功则取消倒计时显示
            hideCountdown();
        }
    }

    /*用户点了回退键
        * */
    private void handleOnKeycodeBack(){

    }

    private void initView() {

        //大小视频布局
        mVideoHolder = findView(R.id.rl_video_chat_holder);
        //大视频 view
        mBigVideo = new SurfaceView(this);
        RelativeLayout.LayoutParams bigLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        mVideoHolder.addView(mBigVideo, bigLp);
        mBigVideo.setVisibility(View.GONE);
        //对方的蒙皮
        if(VideoChatManager.getInstance().getType()==VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL){
            mBlurUserView = new View(this);
            mBlurUserView.setBackgroundResource(R.drawable.video_chat_blur);
            mBlurUserView.setAlpha(0.98f);
            RelativeLayout.LayoutParams fullLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            mVideoHolder.addView(mBlurUserView,fullLp);
            mBlurUserView.setVisibility(View.GONE);
        }
        //小视频 view
        mSmallVideo = new SurfaceView(this);
        RelativeLayout.LayoutParams smallLp = null;
        if (VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
            smallLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            mVideoHolder.addView(mSmallVideo, smallLp);
            mSmallVideo.setZOrderMediaOverlay(true);
            mSmallVideo.setVisibility(View.VISIBLE);

            //加上自己的模糊
            mBlurView = new View(this);
            mBlurView.setBackgroundResource(R.drawable.video_chat_blur);
            mBlurView.setAlpha(0.98f);
            RelativeLayout.LayoutParams fullLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
            mVideoHolder.addView(mBlurView, fullLp);
            mBlurView.setVisibility(View.GONE);

        }else {
            smallLp = new RelativeLayout.LayoutParams(CommonFunction.dipToPx(this, 88), CommonFunction.dipToPx(this, 133));
            smallLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
            smallLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mVideoHolder.addView(mSmallVideo, smallLp);
            mSmallVideo.setZOrderMediaOverlay(true);
            mSmallVideo.setVisibility(View.GONE);
        }

        //登陆用户发起呼叫时的UI
        mSendCallOtherRL = findView(R.id.rl_vc_called_user_state); //被叫接通前的头像和状态布局
        mSendCallOtherIconHV = findView(R.id.hv_vc_called_user_state_icon); //被叫接通前的头像
        mSendCallOtherNameTV = findView(R.id.tv_vc_called_user_state_name); //被叫接通前的名字
        mSendCallOtherStateTV = findView(R.id.tv_vc_called_user_state_state); //被叫接通前的状态
        mSendCallToolRL = findView(R.id.rl_vc_before_call_tool); //主叫未接通前的工具条布局
        mSendCallToolBeautyIV = findView(R.id.iv_vc_before_call_tool_beauty); //主叫未接通前的工具条美颜
        mSendCallToolBeautyTV = findView(R.id.tv_vc_before_call_tool_beauty); //主叫未接通前的工具条美颜
        mSendCallToolHangupIV = findView(R.id.iv_vc_before_call_tool_hang); //主叫未接通前的工具条挂断
        mSendCallToolBlurIV = findView(R.id.iv_vc_before_call_tool_blur); //主叫未接通前的工具条模糊
        mSendCallToolBlurTV = findView(R.id.tv_vc_before_call_tool_blur); //主叫未接通前的工具条模糊

        //登陆用户收到呼叫时的UI
        mReceiveCallOtherBackground = findView(R.id.iv_vc_before_call_caller_bg);
        mReceiveCallOtherIcon =  findView(R.id.hv_vc_before_call_caller_icon);
        mReceiveCallOtherName = findView(R.id.tv_vc_before_call_caller_name);
        mReceiveCallHangup = findView(R.id.iv_vc_before_call_callee_tool_hang);
        mReceiveCallAnswer = findView(R.id.iv_vc_before_call_callee_tool_answer);
        mReceiveCallOtherLL = findView(R.id.ll_vc_before_call_caller);
        mReceiveCallToolLL = findView(R.id.rl_vc_before_call_callee_tool);

        //呼叫中的UI
        mCallingOtherRL = findView(R.id.rl_vc_called_user_detail);  //对方用户信息布局
        mCallingOtherHead = findView(R.id.hv_vc_called_user_icon); //对方用户头像
        mCallingOtherName = findView(R.id.tv_vc_called_user_name); //对方用户名字
        mCallingOtherAddress = findView(R.id.tv_vc_called_user_address); //对方用户地址
        mCallingOtherFollow = findView(R.id.tv_vc_called_user_attend); //对方用户关注
        mCallingReportUserIV = findView(R.id.iv_video_chat_report); //举报用户
        mCallingCloseVideoIV = findView(R.id.iv_video_chat_close); //关闭视频
        mCallingToolLL = findView(R.id.lv_vc_calling_tool); //主叫和被叫接通后的工具条
        mCallingToolLeftTimeLL = findView(R.id.ll_vc_calling_tool_left_time); //倒计时提示
        mCallingToolLeftTimePB = findView(R.id.pb_vc_calling_tool_progress); //倒计时进度条
        mCallingToolLeftTimeSeconds = findView(R.id.tv_vc_calling_tool_time_left_seconds); //倒计时提示
        mCallingToolBeautyLL = findView(R.id.ll_vc_calling_tool_beauty);
        mCallingToolBeautyIV = findView(R.id.iv_vc_calling_tool_beauty); //美颜按钮
        mCallingToolCameraIV = findView(R.id.iv_vc_calling_tool_camera); //摄像头按钮
        mCallingToolBlurLL = findView(R.id.ll_vc_calling_tool_blur);
        mCallingToolBlurIV = findView(R.id.iv_vc_calling_tool_blur); //模糊按钮
        mCallingToolAddTimeLL = findView(R.id.ll_vc_calling_tool_add_time);
        mCallingToolAddTimeIV = findView(R.id.iv_video_chat_add_time); //加时按钮
        mCallingToolPresentLL = findView(R.id.ll_vc_calling_tool_present);
        mCallingToolPresentIV = findView(R.id.iv_vc_calling_tool_present); //送礼按钮
        mCallingChatTimeRL = findView(R.id.rl_vc_chat_time);  //通话时长布局
        mCallingChatTimeTV = findView(R.id.rl_vc_chat_time_value);  //通话时长
        mCallingLoveRL = findView(R.id.rl_vc_chat_love);  //爱心布局
        mCallingLoveTV = findView(R.id.rl_vc_chat_love_value);  //爱心

        //礼物区域
        pipelineView1 = findView(R.id.pipelineView1);
        pipelineView2 = findView(R.id.pipelineView2);
        pipelineView3 = findView(R.id.pipelineView3);
        luxuryGiftView = findView(R.id.luxuryGiftView);

        //引起内存泄漏
        if (giftQueueHandler == null){
            giftQueueHandler = GiftQueueHandler.getInstance();
        }
        giftQueueHandler.initView(pipelineView1,pipelineView2,pipelineView3,luxuryGiftView);
        giftQueueHandler.setMainHandle(mHandler);
    }

    private void initListener(){
        //登陆用户发起呼叫时:挂断
        mSendCallToolHangupIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangupOnSendCall();
            }
        });

        //登陆用户发起呼叫时:美颜
        mSendCallToolBeautyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beautyOnSendCall();
            }
        });

        //登陆用户发起呼叫时:模糊
        mSendCallToolBlurIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurOnSendCall();
            }
        });


        //登陆用户收到呼叫:挂断
        mReceiveCallHangup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hangupOnReceiveCall();
            }
        });

        //登陆用户收到呼叫:接听
        mReceiveCallAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerOnReceiveCall();
            }
        });

        //通话中的模糊处理
        mCallingToolBlurIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blurOnCallingCall();
            }
        });

        //通话中的美颜处理
        mCallingToolBeautyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beautyOnCallingCall();
            }
        });

        //通话中的摄像头处理
        mCallingToolCameraIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraOnCallingCall();
            }
        });

        //通话中的加时处理
        mCallingToolAddTimeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addtimeOnCallingCall();
            }
        });

        //通话中的送礼处理
        mCallingToolPresentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presentOnCallingCall();
            }
        });

        //通话中的举报
        mCallingReportUserIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportUserOnCallingCall();
            }
        });

        //通话中的关闭
        mCallingCloseVideoIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeOnCallingCall();
            }
        });

        //通话中的关注对方
        mCallingOtherFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser();
            }
        });

        //大小头像切换
        mOnClickSmallVideoView = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                changeSmallBigVideoView();
            }
        };
    }

    /* 初始化呼叫
    * */
    private void initCall(){
        if( VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL == VideoChatManager.getInstance().getType() ){
            Logger.i(TAG,"initCall() call type is RECEIVE_CALL");
            //接受到视频会话
            if(VideoChatManager.getInstance().loginUserIsAnchor()==true){
                Logger.i(TAG,"initCall() 登陆用户是主播");
                //处理推送消息
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_START_VIDEO, this); //推拉流开始
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_CLOSE_VIDEO, this); //关闭视频
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_PAY_VIDEO, this); //扣费
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_BLUR_VIDEO, this); //模糊
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_MESSAGE, this); //各种消息（包括礼物）
            }
            //显示UI
            if( CommonFunction.isMobile(this) ) {
                //吐司提示
                CommonFunction.toastMsg(BaseApplication.appContext,mContext.getString(R.string.just_using_mobile_for_video_chat));
            }
            showReceivingUI();

            //播放声音
            CommonFunction.playVideoChatVoice(this);

            //开启视频会话全局定时器
            startVideoChatTimer();

        }else if( VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL == VideoChatManager.getInstance().getType() ){
            Logger.i(TAG,"initCall() call type is SEND_CALL");
            //发起视频会话
            //如果是普通用户需要启动通讯服务
            if(VideoChatManager.getInstance().loginUserIsAnchor()==false){
                //启动IM服务
                //STNManager.start(BaseApplication.appContext, "192.168.100.210" , 8081);
                //处理推送消息
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_START_VIDEO, this); //推拉流开始
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_CLOSE_VIDEO, this); //关闭视频
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_PAY_VIDEO, this); //扣费
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_BLUR_VIDEO, this); //模糊
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_MESSAGE, this); //各种消息（包括礼物）
            }else{
                //处理推送消息
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_START_VIDEO, this); //推拉流开始
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_CLOSE_VIDEO, this); //关闭视频
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_PAY_VIDEO, this); //扣费
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_BLUR_VIDEO, this); //模糊
                STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_MESSAGE, this); //各种消息（包括礼物）
            }

            //显示发送中的UI
            showSendingUI();

            if( CommonFunction.isMobile(this) ){
                //移动网络下提示
                CustomDialog dialog = new CustomDialog( mContext , mContext.getString(R.string.prompt) , mContext.getString(R.string.using_mobile_for_video_chat) ,
                        mContext.getString(R.string.cancel),
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        },  mContext.getString(R.string.continue_) ,
                        new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                if(VideoChatManager.getInstance().loginUserIsAnchor()==true){
                                    sendInvite();
                                }else{
                                    //登陆视频服务
                                    long user = Common.getInstance().loginUser.getUid();
                                    Logger.i(TAG, "initCall() 发送登陆任务, user=" + user);
                                    LoginTaskWrapper task = new LoginTaskWrapper(VideoChatActivity.this);
                                    task.getProperties().putLong("uid", user);
                                    task.getProperties().putInt("authType",Iachat.AUTH_TOKEN);
                                    STNManager.startTask(task);
                                }
                            }
                        } );
                dialog.setCanceledOnTouchOutside(false);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener(){
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
                            return true;
                        }
                        return false;
                    }
                });
                dialog.show();
            }else{
                if(VideoChatManager.getInstance().loginUserIsAnchor()==true) {
                    //主播可以马上发送邀请 普通用户需要等待登陆成功后才能发送邀请
                    sendInvite();
                }else{
                    //主播可以马上发送邀请 普通用户需要等待登陆成功后才能发送邀请
                    //登陆视频服务
                    long user = Common.getInstance().loginUser.getUid();
                    Logger.i(TAG, "initCall() 发送登陆任务, user=" + user);
                    LoginTaskWrapper task = new LoginTaskWrapper(this);
                    task.getProperties().putLong("uid", user);
                    task.getProperties().putInt("authType",Iachat.AUTH_TOKEN);
                    STNManager.startTask(task);
                }
            }

            //开启视频会话全局定时器
            startVideoChatTimer();
        }else{
            Logger.e(TAG, "initCall() 会话类型不明确");
            finish();
        }
    }

    /* 收到来电消息的时候，显示来电中UI
    * */
    private void showReceivingUI(){
        showReceiveCallUI(true);
        VideoChatManager.VideoChatUser other = VideoChatManager.getInstance().getOther();
        if(other!=null) {
            mReceiveCallOtherIcon.executeWithCallback(other.icon,0, 0, -1, new ImageLoadListener(this));
            mReceiveCallOtherName.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, other.name , 0, null));
            mCallingOtherAddress.setText(other.city);
            mCallingOtherName.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, other.name , 0, null) );
            mCallingOtherHead.executeWithCallback(other.icon,other.svip,other.vip,-1,null);
            if(other.follow != 0){
                mCallingOtherFollow.setVisibility(View.GONE);
            }
        }
    }

    /* 显示发送中的UI
    */
    private void showSendingUI(){
        VideoChatManager.VideoChatUser other = VideoChatManager.getInstance().getOther();
        //显示发送会话界面
        showSendCallUI(true);
        if(other!=null) {
            mSendCallOtherIconHV.executeWithCallback(other.icon,other.svip, other.vip, -1, null);
            mSendCallOtherNameTV.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, other.name, 0, null) );
            mCallingOtherAddress.setText(other.city);
            mCallingOtherName.setText(FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, other.name , 0, null) );
            mCallingOtherHead.executeWithCallback(other.icon,other.svip,other.vip,-1,null);
            if(other.follow != 0){
                mCallingOtherFollow.setVisibility(View.GONE);
            }
        }
        //播放声音
        CommonFunction.playVideoChatVoice(this);
    }

    /* 发起视频邀请任务
     */
    private void sendInvite(){
        if( CommonFunction.activityIsDestroyed(this) ){
            Logger.i(TAG, "sendInvite() 将不会发送视频邀请任务 activity已经销毁");
            return ;
        }
        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_CANCEL_INVITE || VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED ){
            Logger.i(TAG, "sendInvite() 将不会发送视频邀请任务，会话状态 state="+VideoChatManager.getInstance().getState());
            return ;
        }
        if(mInviteSend == true){
            return;
        }
        mInviteSend = true;
        VideoChatManager.VideoChatUser other = VideoChatManager.getInstance().getOther();
        VideoChatManager.VideoChatUser current = VideoChatManager.getInstance().getCurrent();
        //发送视频会话邀请
        if(current!=null && other!=null) {
            Logger.i(TAG, "sendInvite() 发送视频邀请任务, from="+current.uid + ", to=" + other.uid);
            VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_SEND_INVITE);

            InviteVideoTaskWrapper task = new InviteVideoTaskWrapper(this);
            task.getProperties().putLong("from", current.uid);
            task.getProperties().putLong("to", other.uid);
            task.getProperties().putInt("follow", other.anchor_follow);
            STNManager.startTask(task);
        }
    }

    /* 销毁呼叫
   * */
    private void destroyCall(){
        if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL){
            //停止无应答定时器
            stopNoResponseTimer();
            //停止扣费上报定时器
            stopPayTimer();
        }else{
            //停止扣费消息检查定时器
            stopPushPayTimer();
        }

        //停止视频会话全局定时器
        stopVideoChatTimer();

        //倒计时定时器取消且隐藏倒计时(主叫和被叫都有)
        hideCountdown();

        //如果是普通用户需要关闭通讯服务
        if(VideoChatManager.getInstance().loginUserIsAnchor()==false){
            //登出视频服务
            Logger.i(TAG, "destroyCall() 发送登出任务");
            LogoutTaskWrapper task = new LogoutTaskWrapper(this);
            task.getProperties().putLong("uid", VideoChatManager.getInstance().getRoom());
            STNManager.startTask(task);

            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_START_VIDEO, this); //推拉流开始
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_CLOSE_VIDEO, this); //关闭视频
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_PAY_VIDEO, this); //扣费
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_BLUR_VIDEO, this); //模糊
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_MESSAGE, this); //各种消息（包括礼物）
            //停止IM服务
            //STNManager.stop(BaseApplication.appContext);
        }else{
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_START_VIDEO, this); //推拉流开始
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_CLOSE_VIDEO, this); //关闭视频
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_PAY_VIDEO, this); //扣费
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_BLUR_VIDEO, this); //模糊
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_MESSAGE, this); //各种消息（包括礼物）
        }

        //还原通话状态
        VideoChatManager.getInstance().reset();

        //停止播放声音
        CommonFunction.stopVideoChatVoice();
    }

    /* 初始化第三方推拉流
   * */
    private void initVideo(){
        //先销毁聊吧悬浮窗和聊吧推拉流
        ChatBarZoomWindow.getInstance().close();

        //初始化视频会话
        mFlyVideoRoom = FlyVideoRoom.getInstance();
        if(mFlyVideoRoom!=null){
            mFlyVideoRoom.init();
            mFlyVideoRoom.setSmallView(mSmallVideo);
            mFlyVideoRoom.setBigView(mBigVideo);
            mBigVideo.setVisibility(View.GONE);

            if( VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL == VideoChatManager.getInstance().getType() ){
                mSmallVideo.setVisibility(View.VISIBLE);
                mFlyVideoRoom.startPreview();
            }else{
                mSmallVideo.setVisibility(View.GONE);
            }
        }
    }

    /* 销毁第三方推拉流
      * */
    private void destroyVideo(){
        if(mFlyVideoRoom!=null){
            mFlyVideoRoom.stopVideo();
            mFlyVideoRoom.clear();
            mFlyVideoRoom = null;
        }
    }


    //登陆用户发起呼叫时:挂断
    private void hangupOnSendCall(){
        Logger.i(TAG, "hangupOnSendCall() 主动取消视频会话");
        if(mSendCallToolHangupIVClick==true){
            return;
        }
        mSendCallToolHangupIVClick = true;

        //停止无应答定时器
        stopNoResponseTimer();

        //停止播放声音
        CommonFunction.stopVideoChatVoice();

        //停止预览摄像头
        if(mFlyVideoRoom!=null) {
            mFlyVideoRoom.stopPreview();
        }

        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.CANCEL_CALL;
        mHandler.sendMessageDelayed(message,1000);
    }

    /*取消当前会话（延迟执行）
    * */
    private void cancelCallDelay(){
        //延迟一秒再关闭退出
        if( VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_IDLE || VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED ){
            //设置状态为取消状态
            VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CANCEL_INVITE);

            finish();
        }else{
            //设置状态为取消状态
            VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CANCEL_INVITE);

            Logger.i(TAG, "cancelCallDelay() 发出取消视频会话任务");
            CancelVideoTaskWrapper task = new CancelVideoTaskWrapper(this);
            task.getProperties().putLong("roomid", VideoChatManager.getInstance().getRoom());
            task.getProperties().putInt("flag", 0);
            STNManager.startTask(task);

            finish();
        }

        CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_other_cancel));
    }

    //登陆用户发起呼叫时:美颜
    private void beautyOnSendCall(){
        mBeautyEnable = !mBeautyEnable;
        if(mBeautyEnable==true){
            mCallingToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_open);
            mSendCallToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_open);
            mSendCallToolBeautyTV.setText(R.string.video_chat_beauty_enable);
        }else{
            mCallingToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_close);
            mSendCallToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_close);
            mSendCallToolBeautyTV.setText(R.string.video_chat_beauty_disable);
        }
        if(mFlyVideoRoom!=null){
            mFlyVideoRoom.setEnableBeauty(mBeautyEnable);
        }
    }

    //登陆用户发起呼叫时:模糊
    private void blurOnSendCall(){
        mBlurEnable = !mBlurEnable;

        Logger.i(TAG, "blurOnSendCall() 模糊开关 mBlurEnable=" +mBlurEnable);
        if(mBlurEnable==true){
            mCallingToolBlurIV.setImageResource(R.drawable.video_chat_blur_open);
            mSendCallToolBlurIV.setImageResource(R.drawable.video_chat_blur_open);
            mSendCallToolBlurTV.setText(R.string.video_chat_blur_enable);
            if(mBlurView!=null) {
                mBlurView.setVisibility(View.VISIBLE);
            }
        }else{
            mCallingToolBlurIV.setImageResource(R.drawable.video_chat_blur_close);
            mSendCallToolBlurIV.setImageResource(R.drawable.video_chat_blur_close);
            mSendCallToolBlurTV.setText(R.string.video_chat_blur_disable);
            if(mBlurView!=null) {
                mBlurView.setVisibility(View.GONE);
            }
        }
    }

    //登陆用户收到呼叫:拒绝来电
    private void hangupOnReceiveCall(){
        Logger.i(TAG, "hangupOnReceiveCall() 拒绝来电");

        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_REJECT_INVITE);
        long room = VideoChatManager.getInstance().getRoom();

        Logger.i(TAG, "hangupOnReceiveCall() 发送拒绝邀请任务, room=" + room);
        RejectVideoTaskWrapper task = new RejectVideoTaskWrapper(this);
        task.getProperties().putLong("roomid", room );
        STNManager.startTask(task);

        //停止播放声音
        CommonFunction.stopVideoChatVoice();

        //结束视频会话(不管是否发送成功)
        finish();

        CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_reject));
    }

    //登陆用户收到呼叫:接听
    private void answerOnReceiveCall(){
        Logger.i(TAG, "answerOnReceiveCall() 接听电话");
        if(mReceiveCallAnswerClick == true) {
            return;
        }
        mReceiveCallAnswerClick = true;

        //同意视频会话后10秒内需要收到开始推拉流消息
        refreshStartVideo(1);

        long room = VideoChatManager.getInstance().getRoom();
        Logger.i(TAG, "answerOnReceiveCall() 发送同意会话任务, room=" + room);
        ConfirmVideoTaskWrapper task = new ConfirmVideoTaskWrapper(this);
        task.getProperties().putLong("roomid", room);
        STNManager.startTask(task);

        //停止播放声音
        CommonFunction.stopVideoChatVoice();
    }

    //通话中的美颜处理
    private void beautyOnCallingCall(){
        mBeautyEnable = !mBeautyEnable;
        if(mBeautyEnable==true){
            mCallingToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_open);
            mSendCallToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_open);
        }else{
            mCallingToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_close);
            mSendCallToolBeautyIV.setImageResource(R.drawable.video_chat_beauty_close);
        }
        if(mFlyVideoRoom!=null){
            mFlyVideoRoom.setEnableBeauty(mBeautyEnable);
        }
    }

    //通话中的摄像头处理
    private void cameraOnCallingCall(){
        if(mFlyVideoRoom!=null){
            mFlyVideoRoom.cameraSwitch();
        }
    }

    //通话中的模糊处理
    private void blurOnCallingCall(){
        if(mBluring==true){
            return;
        }
        mBluring = true;

        boolean blur = !mBlurEnable;
        Logger.i(TAG, "blurOnCallingCall() 发送模糊开关任务, mBlurEnable="+mBlurEnable + ", blur="+blur);
        BlurVideoTaskWrapper blurTask = new BlurVideoTaskWrapper(this);
        blurTask.getProperties().putLong("open", blur == true ? 1 : 0);
        blurTask.getProperties().putLong("roomid", VideoChatManager.getInstance().getRoom());
        STNManager.startTask(blurTask);
    }

    //通话中的加时处理
    private void addtimeOnCallingCall(){
        Intent intent = new Intent(mContext,LovePayActivity.class);
        startActivityForResult(intent,LOVE_PAY_REQUEST_CODE);
    }

    //通话中的送礼处理
    private void presentOnCallingCall(){
        Logger.i(TAG,"presentOnCallingCall() into");
        User tempuser = new User();
        tempuser.setUid(VideoChatManager.getInstance().getOther().uid);
        tempuser.setLat(0);
        tempuser.setLng(0);
        mCustomGiftDialog = CustomGiftDialog.jumpIntoCustomGiftDia(VideoChatActivity.this,tempuser,0,luxuryGiftView,new ChatbarSendPersonalSocketListenerImpl(this),new VideoChatPersonalSocketListenterImp(this));
    }

    //通话中的举报用户
    private void reportUserOnCallingCall(){
        Intent intent = new Intent(this, SpaceReport.class);
        intent.putExtra("targetId",""+VideoChatManager.getInstance().getOther().uid);
        intent.putExtra("targetType",8);
        intent.putExtra("fromType",1);
        startActivity(intent);
    }

    //通话中的关闭视频
    private void closeOnCallingCall(){
        long room = VideoChatManager.getInstance().getRoom();
        Logger.i(TAG, "closeOnCallingCall() into, 发出结束视频会话任务, room=" + room);
        CloseVideoTaskWrapper task = new CloseVideoTaskWrapper(this);
        task.getProperties().putLong("roomid", room);
        task.getProperties().putInt("close_state", Iavchat.CLOSE_STATE_CLOSE_NORMAL);
        STNManager.startTask(task);

        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED);

        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.FINISH_VIDEO;
        mHandler.sendMessage(message);

        CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_end));
    }

    /*同意摄像头录音权限后回调这里
    * */
    public void doLiveShowPerssiomison(){
        Logger.i(TAG, "doLiveShowPerssiomison() into");
        if(mFlyVideoRoom!=null){
            VideoChatManager.VideoChatUser other = VideoChatManager.getInstance().getOther();
            VideoChatManager.VideoChatUser current = VideoChatManager.getInstance().getCurrent();
            if(other!=null && current!=null) {
                Logger.i(TAG, "doLiveShowPerssiomison() to startVideo");
                //当前用户的视频视图变小
                if (VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
                    if(mSmallVideo!=null) {
                        RelativeLayout.LayoutParams smallLp = new RelativeLayout.LayoutParams(CommonFunction.dipToPx(this, 88), CommonFunction.dipToPx(this, 133));
                        smallLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
                        smallLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        mSmallVideo.setLayoutParams(smallLp);
                        mSmallVideo.requestLayout();
                    }
                    if(mBlurView!=null) {
                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(CommonFunction.dipToPx(this, 88), CommonFunction.dipToPx(this, 133));
                        lp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
                        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        mBlurView.setLayoutParams(lp);
                        mBlurView.requestLayout();
                    }
                }
                showCallingUI(true);
                showSendCallUI(false);
                showReceiveCallUI(false);
                mFlyVideoRoom.setFlyPullCallback(new PullSuccessListener(this));
                mFlyVideoRoom.setEnableBeauty(mBeautyEnable);
                mFlyVideoRoom.startVideo(String.valueOf(VideoChatManager.getInstance().getRoom()), String.valueOf(current.uid), String.valueOf(other.uid), VideoChatManager.getInstance().loginUserIsAnchor());
            }else{
                Logger.e(TAG, "doLiveShowPerssiomison() user null");
            }
        }
    }


    //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行
    private void acquireWakeLock()
    {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager)this.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "PostLocationService");
            if (null != mWakeLock) {
                mWakeLock.acquire();
            }
        }
    }

    //释放设备电源锁
    private void releaseWakeLock()
    {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
        if(componentBeenList!=null) {
            if (!componentBeenList.isEmpty()) {
                if(pacageGiftBean!=null&&pacageGiftBean.getReceive_user()!=null) {
                    sendGiftMessage(pacageGiftBean.getReceive_user().getUserid(), sendNum, false);
                }
            }
        }
    }

    /*显示背景图接口
    * */
    static class ImageLoadListener implements GlideUtil.IOnLoadBitmap{
        private WeakReference<VideoChatActivity> mActivity = null;

        public ImageLoadListener(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void onLoadBitmapSussess(Bitmap bitmap) {
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                activity.showOtherBackground(bitmap);
            }
        }
    }

    /*显示背景图方法
   * */
    private void showOtherBackground(Bitmap bitmap){
        if( bitmap!=null ){
            Bitmap newBitmap = CommonFunction.fastBlur(this,bitmap, 25);
            mReceiveCallOtherBackground.setImageBitmap(newBitmap);
        }
    }


    /* 任务响应
    * */
    @Override
    public void onTaskEnd(NanoMarsTaskWrapper taskWrapper, int errType, int errCode, MessageNano request, MessageNano response) {
        if(Build.VERSION.SDK_INT>=17) {
            if (this.isDestroyed()) {
                Logger.i(TAG, "onTaskEnd() activity destroy");
                return;
            }
        }
        if(taskWrapper instanceof LoginTaskWrapper){
            Iachat.LoginRsp rsp = (Iachat.LoginRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                if(rsp.errCode==0) {
                    Logger.i(TAG, "onTaskEnd() 登陆任务响应成功");
                    //普通用户等登陆成功再发邀请
                    sendInvite();
                }else{
                    Logger.i(TAG, "onTaskEnd() 登陆任务响应业务失败");
                }
            }else{
                Logger.i(TAG, "onTaskEnd() 登陆任务网络错误");
                CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.network_error));
                finish();
            }
        } else if(taskWrapper instanceof LogoutTaskWrapper){
            Iachat.LogoutRsp rsp = (Iachat.LogoutRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                Logger.i(TAG, "onTaskEnd() 登出任务响应成功");
            }else{
                Logger.i(TAG, "onTaskEnd() 登出任务网络错误");
            }
        } else if(taskWrapper instanceof InviteVideoTaskWrapper){
            //邀请会话响应
            Iavchat.InviteVideoChatRsp rsp = (Iavchat.InviteVideoChatRsp)response;
            if(errType==0 && errCode==0 && rsp!=null) {
                if(rsp.errCode == Iachat.ERR_SUCCESS) {
                    Logger.i(TAG, "onTaskEnd() 邀请会话任务响应成功，返回房间号:" + rsp.roomid);
                    //设置房间号
                    VideoChatManager.getInstance().setRoom(rsp.roomid);
                    //开启无应答定时器
                    startNoResponseTimer();
                }else{
                    Logger.i(TAG, "onTaskEnd() 邀请会话任务响应业务错误");
                    //处理各种异常
                    handleTaskEndError(rsp.errCode, rsp.errMsg);
                }
            }else{
                //发生错误
                Logger.i(TAG, "onTaskEnd() 邀请会话任务网络错误");
                CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.network_error));

                //发送结束视频的消息
                //sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
                finishVideo(false);
            }

        }else if(taskWrapper instanceof ConfirmVideoTaskWrapper){
            //同意会话回调
            Iavchat.ConfirmVideoChatRsp rsp = (Iavchat.ConfirmVideoChatRsp)response;
            if(errType==0 && errCode==0 && rsp!=null && rsp.roomid==VideoChatManager.getInstance().getRoom()){
                if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE) {
                    if (rsp.errCode == Iachat.ERR_SUCCESS) {
                        Logger.i(TAG, "onTaskEnd() 同意会话任务响应正确");
                        //状态机
                        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CONFORM_INVITE);

                        showReceiveCallUI(false);
                        showCallingUI(true);
                    } else {
                        Logger.i(TAG, "onTaskEnd() 同意会话任务响应返回业务错误");
                        //处理各种异常
                        handleTaskEndError(rsp.errCode, rsp.errMsg);
                    }
                }else{
                    Logger.w(TAG, "onTaskEnd() 同意会话任务状态机不正确，state="+VideoChatManager.getInstance().getState());
                }
            }else{
                //发生错误
                Logger.i(TAG, "onTaskEnd() 同意会话任务网络错误");
                CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.network_error));

                //发送结束视频的消息
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
            }
            mReceiveCallAnswerClick = false;
        }else if(taskWrapper instanceof RejectVideoTaskWrapper){
            //拒绝会话回调
            Iavchat.RejectVideoChatRsp rsp = (Iavchat.RejectVideoChatRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                Logger.i(TAG, "onTaskEnd() 拒绝会话任务响应成功");
            }else{
                Logger.i(TAG, "onTaskEnd() 拒绝会话任务网络错误");

                //发送结束视频的消息
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
            }
        }else if(taskWrapper instanceof CancelVideoTaskWrapper){
            //拒绝会话回调
            Iavchat.CancelVideoChatRsp rsp = (Iavchat.CancelVideoChatRsp)response;
            mSendCallToolHangupIVClick = false;
            if(errType==0 && errCode==0 && rsp!=null){
                Logger.i(TAG, "onTaskEnd() 取消会话任务响应成功");
            }else {
                Logger.i(TAG, "onTaskEnd() 取消会话任务网络错误");
                //发送结束视频的消息
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
            }
        }else if(taskWrapper instanceof StartVideoTaskWrapper){
            Iavchat.StartVideoChatRsp rsp = (Iavchat.StartVideoChatRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                if(rsp.errCode == Iachat.ERR_SUCCESS) {
                    Logger.i(TAG, "onTaskEnd() 推拉流结果上报任务响应成功");
                    if (VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
                        //主动发起视频会话方才会定时发送扣费上报任务
                        Logger.i(TAG, "onTaskEnd() 开启扣费上报定时器");
                        startPayTimer();
                    } else {
                        //被动接收视频会话方会开启扣费检查定时器
                        if (mPushPayVideoTime == -1) {
                            mPushPayVideoTime = System.currentTimeMillis();
                            Logger.i(TAG, "onTaskEnd() 开启扣费检查定时器");
                            startPushPayTimer(PAY_VIDEO_TIMER_INTERNAL/4);
                        }
                    }
                }else {
                    Logger.i(TAG, "onTaskEnd() 推拉流结果上报任务响应业务失败");
                    //处理各种异常
                    handleTaskEndError(rsp.errCode, rsp.errMsg);
                }
            }else{
                Logger.i(TAG, "onTaskEnd() 推拉流结果上报任务网络错误");
                CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.network_error));

                //发送结束视频的消息
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
            }
        } else if(taskWrapper instanceof BlurVideoTaskWrapper){
            //模糊开关任务回调
            Iavchat.BluerRsp rsp = (Iavchat.BluerRsp)response;
            if(errType==0 && errCode==0 && rsp!=null && rsp.errCode==0){
                Logger.i(TAG, "onTaskEnd() 模糊视频开关任务发送成功");
                mBlurEnable = !mBlurEnable;
                if(mBlurEnable==true){
                    mCallingToolBlurIV.setImageResource(R.drawable.video_chat_blur_open);
                    mSendCallToolBlurIV.setImageResource(R.drawable.video_chat_blur_open);
                    if(mBlurView!=null){
                        mBlurView.setVisibility(View.VISIBLE);
                    }
                }else{
                    mCallingToolBlurIV.setImageResource(R.drawable.video_chat_blur_close);
                    mSendCallToolBlurIV.setImageResource(R.drawable.video_chat_blur_close);
                    if(mBlurView!=null) {
                        mBlurView.setVisibility(View.GONE);
                    }
                }
            }else{
                Logger.i(TAG, "onTaskEnd() 模糊视频开关任务发送失败");
            }
            mBluring = false;
        }else if(taskWrapper instanceof PayTaskWrapper){
            //模糊开关任务回调
            Iavchat.PayRsp rsp = (Iavchat.PayRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                Logger.i(TAG, "onTaskEnd() 扣费上报任务发送成功,剩余时间="+rsp.seconds);
                if(rsp.errCode==Iachat.ERR_SUCCESS) {
                    if (rsp.seconds <= SHOW_COUNTDOWN_MIN_SECONDS) {
                        //如果小于指定秒数，则显示倒计时
                        showCountdown(rsp.seconds);
                    } else {
                        //如果已经显示倒计时 取消显示倒计时
                        hideCountdown();
                    }
                    if(mRefreshChatTime == false) {
                        Logger.i(TAG, "onTaskEnd() 即将开始刷新视频会话时间");
                        mRefreshChatTime = true;
                    }
                }else {
                    //处理各种异常
                    handleTaskEndError(rsp.errCode, rsp.errMsg);
                }
            }else{
                Logger.i(TAG, "onTaskEnd() 扣费上报任务网络错误");
                CommonFunction.toastMsg(BaseApplication.appContext,mContext.getString(R.string.current_network_not_good_connection_break));
                //发送结束视频的消息
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_SEND_TASK_FAIL);
            }
        }else if(taskWrapper instanceof CloseVideoTaskWrapper){
            //主动结束视频会话任务回调
            Iavchat.CloseVideoChatRsp rsp = (Iavchat.CloseVideoChatRsp)response;
            if(errType==0 && errCode==0 && rsp!=null){
                Logger.i(TAG, "onTaskEnd() 视频结束任务发送成功");
            }else{
                Logger.i(TAG, "onTaskEnd() 视频结束任务网络错误");
            }
        }else{
            Logger.i(TAG, "onTaskEnd() 未处理任务类型:"+taskWrapper.getClass().getName());
        }
    }

    /* 处理推送消息
    * */
    @Override
    public boolean handleReceiveMessage(PushMessage pushMessage) {
        if(Build.VERSION.SDK_INT>=17) {
            if (this.isDestroyed()) {
                Logger.i(TAG, "handleReceiveMessage() destroy cmdid=" + pushMessage.cmdId);
                return false;
            }
        }
        if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_START_VIDEO){
            //开始视频推拉流通知（被叫同意了视频会话）
            return handleReceiveStartVideoMessage(pushMessage);
        }else if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_PAY_VIDEO){
            //支付扣费通知
            return handleReceivePayVideoMessage(pushMessage);
        }else if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_CLOSE_VIDEO){
            //视频关闭通知
            return handleReceiveCloseVideoMessage(pushMessage);
        }else if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_BLUR_VIDEO){
            //模糊通知
            return handleReceiveBlurVideoMessage(pushMessage);
        }else if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_MESSAGE){
            //各种消息（礼物消息）
            return handleReceiveSendMsgMessage(pushMessage);
        }else{
            Logger.w(TAG, "未处理推送消息: cmdid="+pushMessage.cmdId);
        }

        return false;
    }


    /*处理收到的开始视频推送消息
    * */
    private boolean handleReceiveStartVideoMessage(PushMessage pushMessage){
        try {
            Logger.i(TAG, "收到开始视频推拉流推送消息");
            //登陆视频服务
            final Iavchat.PushStartVideo msg = Iavchat.PushStartVideo.parseFrom(pushMessage.buffer);
            if(null!=msg) {
                if(VideoChatManager.getInstance().getState()!=VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED &&
                        VideoChatManager.getInstance().getState()!=VideoChatManager.VIDEO_CHAT_CALL_STATE_IDLE &&
                        (VideoChatManager.getInstance().getState()==VideoChatManager.VIDEO_CHAT_CALL_STATE_SEND_INVITE || VideoChatManager.getInstance().getState()==VideoChatManager.VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE || VideoChatManager.getInstance().getState()==VideoChatManager.VIDEO_CHAT_CALL_STATE_CONFORM_INVITE) ) {
                    if(msg.roomid == VideoChatManager.getInstance().getRoom()) {
                        if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
                            //停止无应答定时器
                            stopNoResponseTimer();

                            //停止播放声音
                            CommonFunction.stopVideoChatVoice();

                            //是否是能模糊效果(主动发起视频会话的用户才会在通知开始推拉流之时发送模糊开关)
                            long room = VideoChatManager.getInstance().getRoom();
                            Logger.i(TAG, "发送模糊开关任务, roomid=" + room + ", mBlurEnable="+mBlurEnable);
                            BlurVideoTaskWrapper blurTask = new BlurVideoTaskWrapper(null);
                            blurTask.getProperties().putLong("open", mBlurEnable == true ? 1 : 0);
                            blurTask.getProperties().putLong("roomid", room);
                            STNManager.startTask(blurTask);
                        }else{

                            //停止未收到推拉流消息的计时
                            refreshStartVideo(-1);

                            //开始即构推拉流回调计时
                            refreshPullResult(1);
                        }

                        Logger.i(TAG, "即将开始视频推拉流");
                        //开始视频推拉流
                        Message message = mHandler.obtainMessage();
                        message.what = UIMainHandler.START_PUSH;
                        mHandler.sendMessage(message);
                    }else{
                        Logger.i(TAG, "房间号不匹配 roomid[" + msg.roomid+":"+VideoChatManager.getInstance().getRoom()+"]");
                    }
                }else {
                    Logger.i(TAG, "状态机不对 state=" + VideoChatManager.getInstance().getState());
                }
            }else{
                Logger.i(TAG, "收到开始视频推拉流推送消息错误格式");
                sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_PUSH_MSG_FORMAT_ERROR);
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG, "处理开始视频推拉流推送消息异常");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_APP_ERROR);
        }
        return false;
    }

    /*处理收到的结束视频推送消息
    * */
    private boolean handleReceiveCloseVideoMessage(PushMessage pushMessage){
        try {
            Logger.i(TAG, "收到结束视频推送消息");
            if (VideoChatManager.getInstance().getState() != VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED && VideoChatManager.getInstance().getState() != VideoChatManager.VIDEO_CHAT_CALL_STATE_IDLE) {
                final Iavchat.PushCloseVideo msg = Iavchat.PushCloseVideo.parseFrom(pushMessage.buffer);
                if(msg!=null) {
                    if(msg.roomid == VideoChatManager.getInstance().getRoom()) {
                        //停止无应答定时器
                        stopNoResponseTimer();

                        //提示关闭视频会话原因
                        Logger.i(TAG, "视频推送消息里的结束状态 state="+msg.state);
                        if(msg.state == Iavchat.STATE_CANCEL){
                            CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.video_chat_other_cancel));
                        }else if(msg.state == Iavchat.STATE_REJECT){
                            CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.video_chat_other_reject));
                        }else if(msg.state == Iavchat.STATE_USER_BUSY){
                            CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.video_chat_invite_user_busy));
                        }else{
                            CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.other_hangup_video_chat_end));
                        }
                        //结束视频会话
                        finishVideo(false);
                    }else{
                        Logger.w(TAG, "房间号不匹配 roomid[" + msg.roomid+":"+VideoChatManager.getInstance().getRoom()+"]");
                    }
                }
            } else {
                Logger.w(TAG, "状态机不对 video chat state=" + VideoChatManager.getInstance().getState());
            }

        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG, "处理结束视频推送消息异常");

            finishVideo(false);
        }
        return false;
    }

    /*处理收到的扣费通知推送消息
   * */
    private boolean handleReceivePayVideoMessage(PushMessage pushMessage){
        try {
            Logger.i(TAG, "收到扣费通知推送消息");
            Iavchat.PushPayVideo msg = Iavchat.PushPayVideo.parseFrom(pushMessage.buffer);
            if(null!=msg){
                if(msg.roomid == VideoChatManager.getInstance().getRoom()){
                    Logger.i(TAG, "扣费通知推送消息剩余秒数 seconds="+msg.seconds);
                    Message message = mHandler.obtainMessage();
                    message.arg1 = msg.seconds; //对方的剩余秒数
                    message.arg2 = (int)msg.love;
                    message.what = UIMainHandler.RESET_CHECK_PAY_TIMER;
                    mHandler.sendMessage(message);
                }else{
                    Logger.i(TAG, "房间号不匹配 roomid[" + msg.roomid+":"+VideoChatManager.getInstance().getRoom()+"]");
                }
            }else{
                Logger.w(TAG,"解析扣费通知推送消息异常");
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG,"处理扣费通知推送消息异常");
        }
        return false;
    }

    /*处理收到的模糊视频推送消息
    * */
    private boolean handleReceiveBlurVideoMessage(PushMessage pushMessage){
        try {
            Logger.i(TAG, "收到视频模糊推送消息");
            if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL){
                final Iavchat.PushBlurVideo msg = Iavchat.PushBlurVideo.parseFrom(pushMessage.buffer);
                if(null!=msg) {
                    if(msg.roomid == VideoChatManager.getInstance().getRoom()){
                        blurUserOnCallingCall((int)msg.open);
                    }else{
                        Logger.i(TAG, "房间号不匹配 roomid[" + msg.roomid+":"+VideoChatManager.getInstance().getRoom()+"]");
                    }
                }
            }else{
                Logger.i(TAG, "发起视频会话方不用处理视频模糊推送消息");
            }
        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG, "处理视频会话记录推送消息异常");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_APP_ERROR);
        }
        return false;
    }

    /*处理收到的文本消息推送消息
    * */
    private boolean handleReceiveSendMsgMessage(PushMessage pushMessage){
        try {
            Logger.i(TAG, "收到各种消息推送消息");
            final Iachat.PushMsg msg = Iachat.PushMsg.parseFrom(pushMessage.buffer);
            if(null!=msg) {
                if(msg.to == VideoChatManager.getInstance().getCurrent().uid){
                    if(msg.msgType == Iachat.MSG_GIFT){
                        //礼物消息
                        Logger.i(TAG, "收到礼物消息推送消息");
                        receivePresentOnCallingCall(msg.text);
                    }else if(msg.msgType == Iachat.MSG_WARN_ANCHOR){
                        //警告主播消息
                        Logger.i(TAG, "收到警告主播推送消息:text=",msg.text);
                        Message message = mHandler.obtainMessage();
                        message.obj = msg.text;
                        message.what = UIMainHandler.WARNING_ANCHOR;
                        mHandler.sendMessage(message);

                    }else{
                        Logger.w(TAG, "收到其他消息推送消息");
                    }
                }else{
                    Logger.e(TAG, "消息接收方非当前用户 to=" + msg.to+", current="+VideoChatManager.getInstance().getCurrent().uid);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            Logger.e(TAG, "处理视频会话记录推送消息异常");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_APP_ERROR);
        }
        return false;
    }

    private void showSendCallUI(boolean show){
        if(show==false) {
            mSendCallOtherRL.setVisibility(View.GONE);
            mSendCallToolRL.setVisibility(View.GONE);
        }else{
            mSendCallOtherRL.setVisibility(View.VISIBLE);
            mSendCallToolRL.setVisibility(View.VISIBLE);
        }
    }

    private void showReceiveCallUI(boolean show){
        if(show==false) {
            mReceiveCallOtherBackground.setVisibility(View.GONE);
            mReceiveCallOtherLL.setVisibility(View.GONE);
            mReceiveCallToolLL.setVisibility(View.GONE);
        }else{
            mReceiveCallOtherBackground.setVisibility(View.VISIBLE);
            mReceiveCallOtherLL.setVisibility(View.VISIBLE);
            mReceiveCallToolLL.setVisibility(View.VISIBLE);
        }
    }

    private void showCallingUI(boolean show){
        if(show==false) {
            mCallingOtherRL.setVisibility(View.GONE);
            mCallingToolLL.setVisibility(View.GONE);
            mBigVideo.setVisibility(View.GONE);
            mSmallVideo.setVisibility(View.GONE);
            mCallingReportUserIV.setVisibility(View.GONE);
            mCallingCloseVideoIV.setVisibility(View.GONE);
            mCallingLoveRL.setVisibility(View.GONE);
            mCallingChatTimeRL.setVisibility(View.GONE);
        }else{
            mCallingOtherRL.setVisibility(View.VISIBLE);
            mCallingReportUserIV.setVisibility(View.VISIBLE);
            mCallingToolLL.setVisibility(View.VISIBLE);
            mBigVideo.setVisibility(View.VISIBLE);
            mSmallVideo.setVisibility(View.VISIBLE);
            mSmallVideo.setOnClickListener(mOnClickSmallVideoView); //点击小头像进行大小视频图像切换
            mCallingChatTimeRL.setVisibility(View.VISIBLE);
            //只有视频发起方才有美颜和倒计时
            if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL){
                mCallingToolBlurLL.setVisibility(View.GONE); //模糊按钮
                mCallingToolAddTimeLL.setVisibility(View.GONE); //加时按钮
                //mCallingToolPresentLL.setVisibility(View.GONE); //送礼按钮
                mCallingReportUserIV.setVisibility(View.GONE); //举报按钮
                mCallingCloseVideoIV.setVisibility(View.GONE);//60秒后才出现关闭按钮
                mCallingLoveRL.setVisibility(View.VISIBLE);
            }else{
                mCallingCloseVideoIV.setVisibility(View.VISIBLE);
                mCallingLoveRL.setVisibility(View.GONE);
            }
        }
    }

    /*发送结束任务消息给服务器并关闭当前 activity
    * int reson 关闭视频会话的原因，见 iavchat.proto 里 CloseState 定义宏
    * */
    private void sendCloseTaskAndFinish(int reason){
        Logger.d(TAG, "sendCloseTaskAndFinish() into");

        //发送关闭视频任务
        long room = VideoChatManager.getInstance().getRoom();
        Logger.i(TAG, "sendCloseTaskAndFinish() 发送关闭视频会话任务, roomid="+room);
        CloseVideoTaskWrapper task = new CloseVideoTaskWrapper(this);
        task.getProperties().putLong("roomid", room);
        task.getProperties().putInt("close_state",reason);
        STNManager.startTask(task);

        //改变视频会话状态
        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED);

        //关闭当前 activity
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.FINISH_VIDEO;
        mHandler.sendMessage(message);
    }

    /*退出视频通话且关闭当前 activity
   * */
    private void finishVideo(boolean delay){
        Logger.d(TAG, "finishVideo() into");
        //改变视频会话状态
        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED);

        //关闭当前 activity
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.FINISH_VIDEO;
        if(delay) {
            mHandler.sendMessageDelayed(message, 3000);
        }else{
            mHandler.sendMessage(message);
        }
    }

    /*监听即构推拉流SDK拉流成功的回调
    * */
    static class PullSuccessListener implements FlyVideoRoom.IFlyPullCallback{
        private WeakReference<VideoChatActivity> mActivity = null;

        public PullSuccessListener(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void pullResult(int result) {
            Logger.i(TAG,"pullResult() result=" + result);
            VideoChatActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity) == false){
                activity.handlePullResult(result);
            }
        }
    }

    /*处理即构SDK拉流成功
    * */
    private void handlePullResult(int result){
        Logger.i(TAG, "handlePullResult() into result="+result);

        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_CLOSED || VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_CANCEL_INVITE){
            Logger.i(TAG, "handlePullResult() 视频已经关闭或已取消，将不会发送推拉流结果上报任务");
            return;
        }

        if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL) {
            //停止推拉流回调计时间
            refreshPullResult(-1);
        }

        if(VideoChatManager.getInstance().getState() != VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO) {
            //发送开始推拉流任务
            long room = VideoChatManager.getInstance().getRoom();
            Logger.i(TAG, "handlePullResult() 发送推拉流结果任务, roomid=" + room + ", ready=" + result);
            StartVideoTaskWrapper task = new StartVideoTaskWrapper(this);
            task.getProperties().putLong("roomid", room);
            task.getProperties().putInt("ready", result);
            STNManager.startTask(task);
        }

        //改变视频会话状态
        VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO);

        if(result==0){
            //拉流失败
            Logger.i(TAG, "handlePullResult() 推拉流失败");
            CommonFunction.toastMsg(this,getString(R.string.network_not_good));
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_VIDEO_ERROR);
        }else if(result==-1){
            //拉流成功后，视频过程中断流
            Logger.i(TAG, "handlePullResult() 视频过程中拉流断流");
            long current = System.currentTimeMillis();
            if(mLastNetworkQualityNotifyTime == 0){
                mLastNetworkQualityNotifyTime = current;
                CommonFunction.toastMsg(this,getString(R.string.network_not_good));
            }else if( (current - mLastNetworkQualityNotifyTime)>=10000 ) {
                mLastNetworkQualityNotifyTime = current;
                CommonFunction.toastMsg(this,getString(R.string.network_not_good));
            }
            //sendCloseTaskAndFinish();
        }else{
            //拉流成功
            Logger.i(TAG, "handlePullResult() 推拉流成功");
            showSendCallUI(false);
            showReceiveCallUI(false);
            showCallingUI(true);
            if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL){
                //接收会话方60秒延迟显示关闭视频会话按钮
                refreshCloseView(1);
            }
        }
    }

    /* 处理发送任务时响应的各种错误
    * */
    private void handleTaskEndError(int taskErrCode, String taskErrMSg){
        Logger.i(TAG, "handleTaskEndError() 处理任务响应错误,taskErrCode=" + taskErrCode + ", taskErrMSg="+taskErrMSg);
        switch (taskErrCode){
            case Iachat.ERR_PARAMS_ERROR: //任务参数错误
                {
                    Logger.i(TAG, "任务参数错误，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_param_error));
                    //关闭视频会话
                    sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_PARAMS_ERROR);
                }
                break;
            case Iachat.ERR_LOGIN_FAIL: //用户还没登陆通讯服务器
                {
                    long user = Common.getInstance().loginUser.getUid();
                    Logger.i(TAG, "用户未登陆,即将发送登陆任务, user=" + user);
                    LoginTaskWrapper task = new LoginTaskWrapper(null);
                    task.getProperties().putLong("uid", user);
                    task.getProperties().putInt("authType",Iachat.AUTH_TOKEN);
                    STNManager.startTask(task);

                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_login_fail));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_INVITE_VIDEO_FAIL: //邀请视频失败（邀请视频会话任务返回）
                {
                    Logger.i(TAG, "邀请视频失败，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_invite_fail));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_USER_BUSY: //用户忙（邀请视频会话任务返回）
                {
                    Logger.i(TAG, "用户忙，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_invite_user_busy));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_NO_RESPONSE: //无响应
                {
                    Logger.i(TAG, "无响应，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_other_no_response));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_TIMEOUT: //呼叫超时
                {
                    Logger.i(TAG, "呼叫超时，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_other_no_response));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_VIDEO_CHAT_CLOSED: //视频已经结束
                {
                    Logger.i(TAG, "视频已经结束，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_end));
                    finishVideo(true);
                }
                break;
            case Iachat.ERR_BALANCE_NOT_ENOUGH: //账户爱心不足（邀请视频会话任务返回）
                {
                    Logger.i(TAG, "账户爱心不足，即将提示用户充值");
                    showMoneyNotEnough();
                }
                break;
            case Iachat.ERR_PAYMENT_FAIL: //扣费上报时返回不能扣费（定时扣费任务返回）
                {
                    Logger.i(TAG, "扣费失败，即将结束本次视频会话");
                    CommonFunction.toastMsg(BaseApplication.appContext, BaseApplication.appContext.getString(R.string.current_network_not_good_connection_break));
                    //关闭视频会话
                    sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_PAYMENT_FAIL);
                }
                break;
            default:
                break;
        }
    }

    /*提示余额不足
    * */
    private void showMoneyNotEnough(){
        if( CommonFunction.activityIsDestroyed(this)){
            return;
        }
        //隐藏倒计时
        hideCountdown();

        VideoChatManager.VideoChatUser other = VideoChatManager.getInstance().getOther();
        if(other == null) return;
        String fn = String.format(mContext.getString(R.string.video_chat_need_love_need_pay), other.name);
        String notify = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, fn, 0, null).toString();
        CustomDialog dialog = new CustomDialog( mContext , mContext.getString(R.string.rechaarge_love_title) , notify ,
                mContext.getString(R.string.cancel),
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },  mContext.getString(R.string.recharge_love_recharge) ,
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        //跳去充值页面
                        Intent intent = new Intent(mContext,LovePayActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } );
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
                    return true;
                }
                return false;
            }
        });
        dialog.show();
    }

    //通话中的收到模糊开关消息时，主播将用户模糊
    private void blurUserOnCallingCall(final int blur){
        Logger.d(TAG, "blurUserOnCallingCall() 模糊开关 blur=" +blur);
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.BLUR_OTHER_USER;
        message.arg1 = blur;
        mHandler.sendMessage(message);
    }

    private void showWarnAnchor(String text){
        CommonFunction.toastMsgLong(BaseApplication.appContext,text);
    }

    static class UIMainHandler extends  Handler{
        public static final int START_PUSH = 1001; //开始推拉流
        public static final int FINISH_VIDEO = 1002; //结束视频会话
        public static final int BLUR_OTHER_USER = 1003; //模糊对方
        public static final int RESET_CHECK_PAY_TIMER = 1004; //重启定时器
        public static final int REFRESH_COUNTDOWN = 1005; //刷新倒计时
        public static final int RECEIVE_PRESENT = 1006; //收到礼物消息
        public static final int PRESENT_SHOW_COUNT_DOWN = 1007; //送礼显示倒计时
        public static final int CANCEL_CALL = 1008; //取消会话（呼叫未接通前挂断）
        public static final int SHOW_CLOSE_VIEW = 1009; //显示关闭视频会话按钮
        public static final int REFRESH_CHAT_TIME_VIEW = 1010; //刷新显示会话时长
        public static final int WARNING_ANCHOR = 1011;
        public static final int PAUSE_PUBLISHING = 2001; //停止推流
        public static final int RESUME_PUBLISHING = 2002; //恢复推流

        private WeakReference<VideoChatActivity> mActivity;

        public UIMainHandler(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case START_PUSH: //开始推拉流
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (CommonFunction.activityIsDestroyed(activity)) {
                            return;
                        }
                        if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL){
                            //如果是开始推拉流推送消息比同意视频会话任务确认逻辑上先到
                            if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_RECEIVE_INVITE) {
                                //状态机设置为已确认会话邀请
                                VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CONFORM_INVITE);

                                activity.showReceiveCallUI(false);
                                activity.showCallingUI(true);
                            }
                        }
                        //申请摄像头权限
                        activity.requestLiveShowPermission();
                    }
                    break;
                case FINISH_VIDEO: //结束视频会话
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.finish();
                        }
                    }
                    break;
                case BLUR_OTHER_USER: //模糊对方用户
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            int blur = msg.arg1;
                            if (blur == 1) {
                                if (activity.mBlurUserView != null) {
                                    activity.mBlurUserView.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (activity.mBlurUserView != null) {
                                    activity.mBlurUserView.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    break;
                case RESET_CHECK_PAY_TIMER:
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            int seconds = msg.arg1;
                            if(seconds <= SHOW_COUNTDOWN_MIN_SECONDS){
                                //如果小于指定秒数，则显示倒计时
                                activity.showCountdown(seconds);
                            }else{
                                //如果已经显示倒计时 取消显示倒计时
                                activity.hideCountdown();
                            }

                            activity.stopPushPayTimer();
                            activity.mPushPayVideoTime = System.currentTimeMillis();
                            activity.startPushPayTimer(PAY_VIDEO_TIMER_INTERNAL+PAY_VIDEO_TIMER_INTERNAL_MAX_DELAY);

                            if(activity.mRefreshChatTime == false) {
                                Logger.i(TAG, "即将开始刷新视频会话时间");
                                activity.mRefreshChatTime = true;
                            }
                            if(msg.arg2>0) {
                                activity.mVideoChatTotalLoveCount += msg.arg2;
                                activity.mCallingLoveTV.setText(String.valueOf(activity.mVideoChatTotalLoveCount));
                            }else {
                                Logger.e(TAG, "支付通知推送消息爱心数异常 love=" + msg.arg2);
                            }
                        }
                    }
                    break;
                case PRESENT_SHOW_COUNT_DOWN:
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            int seconds = msg.arg1;
                            if(seconds <= (SHOW_COUNTDOWN_MIN_SECONDS-60) ){
                                //如果小于指定秒数，则显示倒计时
                                activity.refreshCountdown(seconds);
                            }else{
                                //如果已经显示倒计时 取消显示倒计时
                                activity.hideCountdown();
                            }
                        }
                    }
                    break;
                case REFRESH_COUNTDOWN:
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity) && activity.mCountdownTimer!=null) {
                            activity.mCountdownLeftTime--;
                            if(activity.mCountdownLeftTime<=0){
                                Logger.i(TAG, "剩余时间为0,结束当前视频会话");
                                activity.hideCountdown();

                                if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
                                    CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.love_not_enough_video_chat_end));
                                    //发出视频会话端（观众）主动发关闭视频消息
                                    activity.sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_LEFT_TIME_FIRE);
                                }
                                return;
                            }
                            String left = activity.secondsToString(activity.mCountdownLeftTime);
                            activity.mCallingToolLeftTimeSeconds.setText(left);
                            float progress = ((float) activity.mCountdownLeftTime/(float)activity.mCountdownLeftTimeFinal)*100;
                            activity.mCallingToolLeftTimePB.setProgress((int)progress);
                        }
                    }
                    break;
                case PAUSE_PUBLISHING:
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            if (activity.mFlyVideoRoom != null) {
                                activity.mFlyVideoRoom.enableMic(false);
                                activity.mFlyVideoRoom.enableSpeaker(false);
//                                if (activity.mFlyVideoRoom.isPublishing()) {
//                                    activity.mNeedResumePublishing = true;
//                                    activity.mFlyVideoRoom.stopPublish();
//                                }
                            }
                        }
                    }
                    break;
                case RESUME_PUBLISHING:
                    {
                        VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            if (activity.mFlyVideoRoom != null) {
                                activity.mFlyVideoRoom.enableMic(true);
                                activity.mFlyVideoRoom.enableSpeaker(true);
//                                if (activity.mNeedResumePublishing == true) {
//                                    activity.mNeedResumePublishing = false;
//                                    activity.mFlyVideoRoom.resumePublish();
//                                }
                            }
                        }
                    }
                    break;
                case RECEIVE_PRESENT:
                    {
                        final VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.pacageGiftBean = GsonUtil.getInstance().getServerBean((String)msg.obj, ChatBarSendPacketGiftBean.class);
                            if(activity.pacageGiftBean!=null&&activity.pacageGiftBean.getGift()!=null&&activity.pacageGiftBean.getReceive_user()!=null&&activity.pacageGiftBean.getSend_user()!=null) {
                                Logger.d(TAG, "git bean:" + activity.pacageGiftBean.toString());
                                //礼物动画
                                activity.handlerGiftMessage(activity.pacageGiftBean);
                                //礼物动画
                                ChatBarSendPacketGiftBean.GiftBean gift = activity.pacageGiftBean.getGift();
                                if (null!=gift && gift.getCombo_type() == 0) {
                                    //去掉100毫秒
                                    Logger.d(TAG, "to send private message");
                                    activity.handleSendGiftData();
                                }
                                //礼物爱心
                                if (null!=gift){
                                    activity.handleGiftLove();
                                }
                            }
                        }
                    }
                    break;
                case CANCEL_CALL:
                    {
                        final VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.cancelCallDelay();
                        }
                    }
                    break;
                case SHOW_CLOSE_VIEW:
                    {
                        final VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.showCloseView();
                        }
                    }
                    break;
                case REFRESH_CHAT_TIME_VIEW:
                    {
                        final VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.showChatTimeView();
                        }
                    }
                    break;
                case WARNING_ANCHOR:
                    {
                        final VideoChatActivity activity = mActivity.get();
                        if (false==CommonFunction.activityIsDestroyed(activity)) {
                            activity.showWarnAnchor((String)msg.obj);
                        }
                    }
                    break;
                default:
                    break;

            }
        }
    }

    /*扣费定时器
   * */
    static class PayTimerTask extends TimerTask {
        private WeakReference<VideoChatActivity> mActivity = null;

        public PayTimerTask(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            Logger.i(TAG,"PayTimerTask() time bingo");
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                activity.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoChatActivity activity = mActivity.get();
                        if(activity!=null) {
                            if(Build.VERSION.SDK_INT>=17) {
                                if (activity.isDestroyed()) {
                                    Logger.i(TAG, "activity destroy");
                                    return;
                                }
                            }
                            activity.doPayVideo();
                        }
                    }
                });
            }
        }
    }

    /*检查扣费定时器
   * */
    static class CheckPayTimerTask extends TimerTask {
        private WeakReference<VideoChatActivity> mActivity = null;

        public CheckPayTimerTask(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            Logger.d(TAG,"CheckPayTimerTask() time bingo");
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                activity.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoChatActivity activity = mActivity.get();
                        if(activity!=null) {
                            if(Build.VERSION.SDK_INT>=17) {
                                if (activity.isDestroyed()) {
                                    Logger.d(TAG, "activity destroy");
                                    return;
                                }
                            }
                            activity.doCheckPayVideo();
                        }
                    }
                });
            }
        }
    }

    /* 　开启主叫上报扣费定时器
    * */
    private void startPayTimer(){
        Logger.i(TAG, "startPayTimer() into");
        if(mPayTimer!=null){
            mPayTimer.cancel();
            mPayTimer = null;
        }
        mPayTimer = new Timer();
        mPayTimerTask = new PayTimerTask(this);
        mPayTimer.schedule(mPayTimerTask, 0, PAY_VIDEO_TIMER_INTERNAL);
    }

    /* 　停止主叫上报扣费定时器
   * */
    private void stopPayTimer(){
        Logger.i(TAG, "stopPayTimer() into");
        if(mPayTimer!=null){
            mPayTimer.cancel();
            mPayTimerTask = null;
            mPayTimer = null;
        }
    }

    /* 　被叫定时检查扣费推送消息
    * */
    private void startPushPayTimer(final int internal){
        Logger.i(TAG, "startPushPayTimer() into, internal=" + internal);
        if(mPayTimer!=null){
            mPayTimer.cancel();
            mPayTimer = null;
        }
        mPayTimer = new Timer();
        mCheckPayTimerTask = new CheckPayTimerTask(this);
        mPayTimer.schedule(mCheckPayTimerTask, internal);
    }

    private void stopPushPayTimer(){
        Logger.i(TAG, "stopPushPayTimer() into");
        if(mPayTimer!=null){
            mPayTimer.cancel();
            mCheckPayTimerTask = null;
            mPayTimer = null;
        }
    }

    private void doPayVideo(){
        Logger.d(TAG,"doPayVideo() into 定时扣费上报");
        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO) {
            if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL) {
                //只有发送视频会话邀请才会定时发送扣费上报
                long room = VideoChatManager.getInstance().getRoom();
                Logger.i(TAG,"doPayVideo() 发送扣费任务, room=" + room);
                PayTaskWrapper task = new PayTaskWrapper(this);
                task.getProperties().putLong("roomid", room);
                STNManager.startTask(task);
            }
        }
    }

    private void doCheckPayVideo(){
        Logger.d(TAG,"doCheckPayVideo() into 定时扣费检查");
        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO) {
            if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL) {
                boolean checkFail = false;
                //接到视频会话才检查扣费通知
                if (-1 == mPushPayVideoTime) {
                    Logger.i(TAG,"未收到扣费通知，即将停止视频会话");
                    checkFail = true;

                } else {
                    long current = System.currentTimeMillis();
                    if ((current - mPushPayVideoTime) >= PAY_VIDEO_TIMER_INTERNAL_MAX_DELAY) {
                        Logger.i(TAG,"未收到扣费通知，即将停止视频会话");
                        checkFail = true;
                    }
                }
                if(checkFail==true){
                    CommonFunction.toastMsg(BaseApplication.appContext,mContext.getString(R.string.other_network_not_good_connection_break));
                    sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_NO_PAY_NOTIFY);
                }
            }
        }
    }

    /*倒计时任务
    * */
    static class CountdownTimerTask extends TimerTask {
        private WeakReference<VideoChatActivity> mActivity = null;

        public CountdownTimerTask(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            Logger.d(TAG,"CountdownTimerTask() time bingo");
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                activity.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoChatActivity activity = mActivity.get();
                        if(activity!=null) {
                            if(Build.VERSION.SDK_INT>=17) {
                                if (activity.isDestroyed()) {
                                    return;
                                }
                            }
                            activity.doCountdown();
                        }
                    }
                });
            }
        }
    }

    /*倒计时显示
    * force 1 强制改变倒计时时间
    * */
    private void showCountdown(int seconds){
        Logger.d(TAG, "showCountdown() into, seconds=" + seconds);
        if(mCountdownTimer!=null) {
            if(seconds>0 && mCountdownLeftTime>0 && (mCountdownLeftTime-seconds)>10 ) {
                mCountdownLeftTime = seconds;
                Logger.d(TAG, "showCountdown() mCountdownLeftTime=" + mCountdownLeftTime);
                if (mButtonShakeAnimation == null) {
                    mButtonShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.button_shake);
                }
                mCallingToolAddTimeIV.startAnimation(mButtonShakeAnimation);
            }
            return;
        }
        mCountdownLeftTime = seconds;
        mCountdownLeftTimeFinal = seconds;
        mCountdownTimer = new Timer();
        mCountdownTimerTask = new CountdownTimerTask(this);
        mCountdownTimer.schedule(mCountdownTimerTask, 0, 1000);

        mCallingToolLeftTimeSeconds.setText(secondsToString(mCountdownLeftTime));
        mCallingToolLeftTimePB.setProgress(100);
        mCallingToolLeftTimeLL.setVisibility(View.VISIBLE);
        mCallingToolLeftTimePB.setVisibility(View.VISIBLE);

        if(mButtonShakeAnimation == null) {
            mButtonShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.button_shake);
        }
        mCallingToolAddTimeIV.startAnimation(mButtonShakeAnimation);
    }

    /*更新倒计时显示（送礼时候剩余时间改变）
   *  seconds 送礼接口计算的剩余的时间 （需要加上已经被扣除的60秒）
   * */
    private void refreshCountdown(int seconds){
        Logger.d(TAG, "refreshCountdown() into, seconds="+seconds);
        if(mCountdownTimer==null){
            //倒计时定时器还没起来
            if(seconds >= 0){

                //默认已扣费的1分钟还剩余30秒
                mCountdownLeftTime = seconds+30;
                mCountdownLeftTimeFinal = seconds+30;
                Logger.i(TAG, "refreshCountdown() create mCountdownTimer, mCountdownLeftTime=" + mCountdownLeftTime);

                mCountdownTimer = new Timer();
                mCountdownTimerTask = new CountdownTimerTask(this);
                mCountdownTimer.schedule(mCountdownTimerTask, 0, 1000);

                mCallingToolLeftTimeSeconds.setText(secondsToString(mCountdownLeftTime));
                mCallingToolLeftTimePB.setProgress(100);
                mCallingToolLeftTimeLL.setVisibility(View.VISIBLE);
                mCallingToolLeftTimePB.setVisibility(View.VISIBLE);

                if(mButtonShakeAnimation == null) {
                    mButtonShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.button_shake);
                }
                mCallingToolAddTimeIV.startAnimation(mButtonShakeAnimation);
            }
        } else{
            //倒计时定时器已经起来
            if (seconds >= 0 && mCountdownLeftTime >= 0) {
                int mod = mCountdownLeftTime % 60;
                mCountdownLeftTime = seconds + mod;
                Logger.i(TAG, "refreshCountdown() mCountdownLeftTime=" + mCountdownLeftTime);
                if (mCountdownLeftTime == 0) {
                    hideCountdown();
                    sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_LEFT_TIME_FIRE);
                } else {
                    if (mButtonShakeAnimation == null) {
                        mButtonShakeAnimation = AnimationUtils.loadAnimation(this, R.anim.button_shake);
                    }
                    mCallingToolAddTimeIV.startAnimation(mButtonShakeAnimation);
                }
            }
        }
    }

    /*倒计时隐藏
    * */
    private void hideCountdown(){
        if(mCountdownTimer!=null) {
            mCountdownTimer.cancel();
            mCountdownTimerTask = null;
            mCountdownTimer = null;
            mCountdownLeftTime = 0;
            mCountdownLeftTimeFinal = 0;
            mCallingToolLeftTimeLL.setVisibility(View.GONE);
            mCallingToolLeftTimePB.setVisibility(View.GONE);
        }
    }

    /*倒计时定时减秒数
    * */
    private void doCountdown(){
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.REFRESH_COUNTDOWN;
        mHandler.sendMessage(message);
    }

    /*倒计时时间秒数转为字符串
    * */
    private String secondsToString(int seconds){
        if(mResourceStringVideoEndLeftTime == null){
            mResourceStringVideoEndLeftTime = getResString(R.string.video_chat_end_left_min);
        }
        if(mResourceStringVideoEndLeftTime!=null) {
            int min = seconds / 60;
            int sec = seconds % 60;
            return String.format(mResourceStringVideoEndLeftTime, min, sec);
        }
        return "";
    }

    /* 手机来电状态：空闲
    * */
    public void onCallStateIdle(){
        Logger.i(TAG,"onCallStateOffHook() into");
        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO) {
            Logger.i(TAG, "onCallStateOffHook() 视频会话恢复推流");
            Message message = mHandler.obtainMessage();
            message.what = UIMainHandler.RESUME_PUBLISHING;
            mHandler.sendMessage(message);
        }
    }

    /* 手机来电状态：响铃
    * */
    public void onCallStateRinging(){
        Logger.i(TAG,"onCallStateRinging() into");
        if(VideoChatManager.getInstance().getState() == VideoChatManager.VIDEO_CHAT_CALL_STATE_START_VIDEO) {
            Logger.i(TAG, "onCallStateOffHook() 视频会话暂停推流");
            Message message = mHandler.obtainMessage();
            message.what = UIMainHandler.PAUSE_PUBLISHING;
            mHandler.sendMessage(message);
        }
    }

    /* 手机来电状态：摘机
    * */
    public void onCallStateOffHook(){
        Logger.i(TAG,"onCallStateOffHook() 即将结束视频会话");
        sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_HANG_UP);
    }

    /*对方无应答定时器
    * */
    static class NoResponseTimerTask extends TimerTask {
        private WeakReference<VideoChatActivity> mActivity = null;

        public NoResponseTimerTask(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            Logger.d(TAG,"NoResponseTimerTask() time bingo");
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                activity.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        VideoChatActivity activity = mActivity.get();
                        if(activity!=null) {
                            if(Build.VERSION.SDK_INT>=17) {
                                if (activity.isDestroyed()) {
                                    return;
                                }
                            }
                            activity.doNoResponse();
                        }
                    }
                });
            }
        }
    }

    /* 处理发起的视频会话无应答
    * */
    private void doNoResponse(){
        if(mNoResponseTimer!=null) {
            mNoResponseTimeCount++;
            int mod = mNoResponseTimeCount%4;
            if(mod == 1 || mod == 2 || mod == 3){
                String state = mSendCallOtherStateTV.getText().toString();
                mSendCallOtherStateTV.setText(state + ".");
            }else if(mod == 0){
                String state = mSendCallOtherStateTV.getText().toString();
                mSendCallOtherStateTV.setText(state.substring(0,state.length()-3));
            }
            if(mNoResponseTimeCount*NO_RESPONSE_TIMER_INTERNAL == NO_RESPONSE_NOTIFY_SECONDS){
                Logger.i(TAG,"doNoResponse() 提示手机不在身边");
                //提示手机不在身边
                CommonFunction.toastMsgLong(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_noresponse_notify));
            }else if(mNoResponseTimeCount*NO_RESPONSE_TIMER_INTERNAL == NO_RESPONSE_CLOSE_VIDEO_CHAT_SECONDS){
                Logger.i(TAG,"doNoResponse() 提示对方无应答");
                //提示对方无应答
                CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_noresponse_close));

                //sendCloseTaskAndFinish()
                //取消视频会话
                long room = VideoChatManager.getInstance().getRoom();
                Logger.i(TAG,"doNoResponse() 发送取消会话任务, roomid=" + room);
                CancelVideoTaskWrapper task = new CancelVideoTaskWrapper(this);
                task.getProperties().putLong("roomid", room);
                task.getProperties().putInt("flag", 1);
                STNManager.startTask(task);

                //改变视频会话状态
                VideoChatManager.getInstance().setState(VideoChatManager.VIDEO_CHAT_CALL_STATE_CANCEL_INVITE);

                //关闭当前 activity
                Message message = mHandler.obtainMessage();
                message.what = UIMainHandler.FINISH_VIDEO;
                mHandler.sendMessage(message);
            }
        }

    }

    /* 开启无应答定时器
    * */
    private void startNoResponseTimer(){
        Logger.i(TAG, "startNoResponseTimer() into");
        if(mNoResponseTimer!=null){
            mNoResponseTimer.cancel();
            mNoResponseTimer = null;
        }
        mNoResponseTimer = new Timer();
        mNoResponseTimerTask = new NoResponseTimerTask(this);
        mNoResponseTimer.schedule(mNoResponseTimerTask, 0, NO_RESPONSE_TIMER_INTERNAL);
    }

    /* 关闭无应答定时器
    * */
    private void stopNoResponseTimer(){
        Logger.i(TAG, "stopNoResponseTimer() into");
        if(mNoResponseTimer!=null){
            mNoResponseTimer.cancel();
            mNoResponseTimerTask = null;
            mNoResponseTimer = null;
        }
    }


    /*视频会话定时器任务
    * */
    static class VideoChatTimerTask extends TimerTask {
        private WeakReference<VideoChatActivity> mActivity = null;

        public VideoChatTimerTask(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            //Logger.i(TAG,"VideoChatTimerTask() time bingo");
            VideoChatActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            //处理定时器刷新
            activity.refreshVideoChatTimer();
        }
    }

    /* 开启视频会话定时器
   * */
    private void startVideoChatTimer(){
        Logger.i(TAG, "startVideoChatTimer() into");
        if(mVideoChatTimer!=null){
            mVideoChatTimer.cancel();
            mVideoChatTimer = null;
        }
        mVideoChatTimer = new Timer();
        mVideoChatTimerTask = new VideoChatTimerTask(this);
        mVideoChatTimer.schedule(mVideoChatTimerTask,0, 1000);
    }

    /* 关闭显示关闭按钮的定时器
    * */
    private void stopVideoChatTimer(){
        Logger.i(TAG, "stopVideoChatTimer() into");
        if(mVideoChatTimer!=null){
            mVideoChatTimer.cancel();
            mVideoChatTimerTask.cancel();
            mVideoChatTimerTask = null;
            mVideoChatTimer = null;
        }
    }

    /* 刷新显示关闭按钮时间
    * start 1 开始计时; -1 停止计时; 0 正常计时
    * */
    private void refreshCloseView(int start){
        if(start == 1){
            Logger.i(TAG, "refreshCloseView() 显示关闭视频按钮开始计时");
            mShowCloseVideoViewTimeCount.getAndSet(0);
            return;
        }else if(start == -1){
            Logger.i(TAG, "refreshCloseView() 显示关闭视频按钮停止计时");
            mShowCloseVideoViewTimeCount.getAndSet(-1);
            return;
        }
        if(mShowCloseVideoViewTimeCount.get() == -1){
            return;
        }
        int timeCount = mShowCloseVideoViewTimeCount.getAndIncrement();
        if(timeCount == -1){
            Logger.i(TAG, "refreshCloseView() 显示关闭视频按钮已停止计时");
            return;
        }
        timeCount++;
        if(timeCount*1000>=SHOW_CLOSE_VIEW_SECONDS){
            mShowCloseVideoViewTimeCount.getAndSet(-1);
            Logger.i(TAG, "refreshCloseView() 即将显示关闭视频按钮");
            Message message = mHandler.obtainMessage();
            message.what = UIMainHandler.SHOW_CLOSE_VIEW;
            mHandler.sendMessage(message);
        }
    }

    /*显示关闭按钮
    * */
    private void showCloseView(){
        mCallingCloseVideoIV.setVisibility(View.VISIBLE);
    }

    /* 刷新未收到推拉流消息时间
     * start 1 开始计时; -1 停止计时; 0 正常计时
     * */
    private void refreshStartVideo(int start){
        if(start == 1){
            //开始计时
            Logger.i(TAG, "refreshStartVideo() 是否收到开始推拉流消息开始计时");
            mStartVideoTimeCount.getAndSet(0);
            return;
        }else if(start == -1){
            //停止计时
            Logger.i(TAG, "refreshStartVideo() 是否收到开始推拉流消息停止计时");
            mStartVideoTimeCount.getAndSet(-1);
            return;
        }
        if(mStartVideoTimeCount.get() == -1){
            return;
        }
        int timeCount = mStartVideoTimeCount.getAndIncrement();
        if(timeCount == -1){
            Logger.i(TAG, "refreshStartVideo() 是否收到开始推拉流消息已停止计时");
            return;
        }
        timeCount++;
        if(timeCount*1000>=RECEIVE_START_VIDEO_SECONDS){
            mStartVideoTimeCount.getAndSet(-1);
            Logger.i(TAG, "refreshStartVideo() 未收到开始推拉流消息,即将结束视频会话");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_NO_START_VIDEO);
        }
    }


    /* 刷新未收到即构推拉流回调时间
    * start 1 开始计时; -1 停止计时; 0 正常计时
    * */
    private void refreshPullResult(int start){
        if(start == 1){
            //开始计时
            Logger.i(TAG, "refreshPullResult() 是否收到即构推拉流回调开始计时");
            mPullResultTimeCount.getAndSet(0);
            return;
        }else if(start == -1){
            //停止计时
            Logger.i(TAG, "refreshPullResult() 是否收到即构推拉流回调停止计时");
            mPullResultTimeCount.getAndSet(-1);
            return;
        }
        if(mPullResultTimeCount.get() == -1){
            return;
        }
        int timeCount = mPullResultTimeCount.getAndIncrement();
        if(timeCount == -1){
            Logger.i(TAG, "refreshPullResult() 是否收到即构推拉流回调已停止计时");
            return;
        }
        timeCount++;
        if(timeCount*1000>=START_VIDEO_PULL_RESULT_SECONDS){
            mPullResultTimeCount.getAndSet(-1);
            Logger.i(TAG, "refreshPullResult() 未收到即构推拉流回调,即将结束视频会话");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_VIDEO_ERROR);
        }
    }

    /* 刷新显示会话时间
    * start 1 开始计时; -1 停止计时; 0 正常计时
    * */
    private void refreshChatTimeView(){
        if(mRefreshChatTime == false){
           return;
        }
        mVideoChatTotalTimeCount++;
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.REFRESH_CHAT_TIME_VIEW;
        mHandler.sendMessage(message);
    }

    private void showChatTimeView(){
        int hour = 0;
        int min = 0;
        int second = 0;
        min = mVideoChatTotalTimeCount/60;
        second = mVideoChatTotalTimeCount%60;
        if(min>=60){
            hour = min/60;
            min = min%60;
        }
        String timeStr = null;
        if(hour>0){
            timeStr = String.format("%02d:%02d:%02d",hour,min,second);
        }else{
            timeStr = String.format("%02d:%02d",min,second);
        }
        mCallingChatTimeTV.setText(timeStr);
    }

    /*全局定时器每秒刷新 定时器线程
    * */
    private void refreshVideoChatTimer(){

        if(VideoChatManager.getInstance().getType() == VideoChatManager.VIDEO_CHAT_CALL_TYPE_RECEIVE_CALL) {
            //被叫60秒显示倒计时
            refreshCloseView(0);

            //被叫确定同意视频会话后，10秒未收到开始推拉流消息时关闭会话
            refreshStartVideo(0);

            //被叫收到开始推拉流消息后，使用即构SDK进行推拉流，如果10秒内未收到推拉流结果则关闭会话
            refreshPullResult(0);
        }

        //主叫和被叫显示会话时长
        refreshChatTimeView();
    }

    /*关注用户
    * */
    private void followUser(){
        if(mCallingOtherFollowClick==true){
            return;
        }
        mCallingOtherFollowClick = true;
        VideoChatManager.VideoChatUser user = VideoChatManager.getInstance().getOther();
        if(user!=null) {
            FriendHttpProtocol.userFanLove(this, user.uid, 3, 0, new VideoChatHttpCallBack(this));
        }
    }

    /*关注成功的结果
    * success 关注成功和失败
    * */
    private void onFollowResult(boolean success){
        if(success==true){
            mCallingOtherFollow.setVisibility(View.GONE);
            setResult(Activity.RESULT_OK); //通知 VideoDetailActivity 关注用户成功
        }
        mCallingOtherFollowClick = false;
    }

    /*处理申请摄像头和录音权限时用户拒绝行为
    * */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean refuse = false;
        if(PermissionUtils.CODE_MULTI_LIVESHOW == requestCode){
            if(grantResults!=null){
                for(int i=0;i<grantResults.length;i++){
                    if(grantResults[i]== PackageManager.PERMISSION_DENIED){
                        refuse = true;
                    }
                }
            }
        }
        if(refuse == true){
            //用户拒绝了权限
            Logger.i(TAG, "用户拒绝了摄像头或录音权限申请");
            sendCloseTaskAndFinish(Iavchat.CLOSE_STATE_REFUSE_PERMISSION);
            CommonFunction.toastMsg(BaseApplication.appContext,BaseApplication.appContext.getString(R.string.video_chat_reject));
        }else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /* 大小头像切换
    * */
    private void changeSmallBigVideoView(){
        if(mVideoHolder==null || mSmallVideo==null || mBigVideo==null)
            return;
        RelativeLayout.LayoutParams smallLp = new RelativeLayout.LayoutParams(mSmallVideo.getLayoutParams());
        RelativeLayout.LayoutParams bigLp = new RelativeLayout.LayoutParams(mBigVideo.getLayoutParams());

        if(mCurrentUserInSmallVideoView == true){
            RelativeLayout.LayoutParams blurLp = null;
            if(mBlurView!=null){
                blurLp = new RelativeLayout.LayoutParams(mBigVideo.getLayoutParams());
            }else if(mBlurUserView!=null){
                blurLp = new RelativeLayout.LayoutParams(mSmallVideo.getLayoutParams());
                blurLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
                blurLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }

            mVideoHolder.removeAllViews();

            mVideoHolder.addView(mSmallVideo, bigLp);
            mSmallVideo.setZOrderMediaOverlay(false);

            if(mBlurView!=null){
                mVideoHolder.addView(mBlurView, blurLp);
            }

            smallLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
            smallLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mVideoHolder.addView(mBigVideo, smallLp);
            mBigVideo.setZOrderMediaOverlay(true);

            if(mBlurUserView!=null){
                mVideoHolder.addView(mBlurUserView, blurLp);
            }

            mSmallVideo.setOnClickListener(null);
            mBigVideo.setOnClickListener(mOnClickSmallVideoView);
            mCurrentUserInSmallVideoView = false;
        }else{
            RelativeLayout.LayoutParams blurLp = null;
            if(mBlurView!=null){
                blurLp = new RelativeLayout.LayoutParams(mBigVideo.getLayoutParams());
                blurLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
                blurLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            }else if(mBlurUserView!=null){
                blurLp = new RelativeLayout.LayoutParams(mSmallVideo.getLayoutParams());
            }

            mVideoHolder.removeAllViews();

            mVideoHolder.addView(mBigVideo, smallLp);
            mBigVideo.setZOrderMediaOverlay(false);

            if(mBlurUserView!=null){
                mVideoHolder.addView(mBlurUserView, blurLp);
            }

            bigLp.setMargins(0, CommonFunction.dipToPx(this, 80), 0, 0);
            bigLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            mVideoHolder.addView(mSmallVideo, bigLp);
            mSmallVideo.setZOrderMediaOverlay(true);

            if(mBlurView!=null){
                mVideoHolder.addView(mBlurView, blurLp);
            }

            mBigVideo.setOnClickListener(null);
            mSmallVideo.setOnClickListener(mOnClickSmallVideoView);
            mCurrentUserInSmallVideoView = true;
        }
    }

    ///////礼物相关//////

    static class VideoChatHttpCallBack implements HttpCallBack{
        private WeakReference<VideoChatActivity> mActivity;

        public VideoChatHttpCallBack(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17) {
                    if (activity.isDestroyed()) {
                        Logger.d(TAG, "onGeneralSuccess() activity destroy");
                        return;
                    }
                }
                BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
                if (serverBean.isSuccess()) {
                    activity.onFollowResult(true);
                }else{
                    activity.onFollowResult(false);
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17) {
                    if (activity.isDestroyed()) {
                        Logger.d(TAG, "onGeneralError() activity destroy");
                        return;
                    }
                }
                activity.onFollowResult(false);
            }
        }
    }

    /*收到礼物推送消息
    * */
    private void receivePresentOnCallingCall(String json){
        Message message = mHandler.obtainMessage();
        message.what = UIMainHandler.RECEIVE_PRESENT;
        message.obj = json;
        mHandler.sendMessage(message);
    }

    /**
     * 收到礼物消息（用来处理礼物动画） UI 线程
     * @param bean
     */
    private void handlerGiftMessage(ChatBarSendPacketGiftBean bean){
        Logger.d(TAG,"handlerGiftMessage() 收到礼物消息，将展现动画 bean = "+bean.toString());
        if (giftQueueHandler == null) {
            return;
        }
        if(bean.getGift()==null){
            Logger.e(TAG,"handlerGiftMessage() 礼物消息格式错误，礼物为空");
            return;
        }
        giftQueueHandler.addGift(bean);
    }

    static class ChatbarSendPersonalSocketListenerImpl implements ChatbarSendPersonalSocketListener{
        private WeakReference<VideoChatActivity> mActivity;

        public ChatbarSendPersonalSocketListenerImpl(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }
        @Override
        public void update(boolean mTimerIsEnd, ChatBarBackpackBean.ListBean selectGiftBean) {
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17) {
                    if (activity.isDestroyed()) {
                        return;
                    }
                }
                activity.mHandler.postDelayed(activity.runnableHandle,100);
            }
        }
    }


    /**
     * 每次送礼剩余爱心回调
     */
    static class VideoChatPersonalSocketListenterImp implements VideoChatPersonalSocketListenter{
        private WeakReference<VideoChatActivity> mActivity;

        public VideoChatPersonalSocketListenterImp(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void updateHadTime(long user_had_time) {
            //执行爱心不足时，倒计时
            VideoChatActivity activity = mActivity.get();
            if(activity!=null){
                if(Build.VERSION.SDK_INT>=17) {
                    if (activity.isDestroyed()) {
                        return;
                    }
                }
                if(VideoChatManager.getInstance().getType()==VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL && user_had_time<=SHOW_COUNTDOWN_MIN_SECONDS) {
                    Message message = activity.mHandler.obtainMessage();
                    message.arg1 = (int)user_had_time;
                    message.what = UIMainHandler.PRESENT_SHOW_COUNT_DOWN;
                    activity.mHandler.sendMessage(message);
                    Logger.i(TAG, "updateHadTime() left time="+message.arg1);
                }
            }

        }

    }

    Runnable runnableHandle = new SendGiftDataRunnable(this);
    static class SendGiftDataRunnable implements Runnable{
        private WeakReference<VideoChatActivity> mActivity;
        public SendGiftDataRunnable(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }
        @Override
        public void run() {
            VideoChatActivity activity = mActivity.get();
            if(activity==null){
                return;
            }
            if(Build.VERSION.SDK_INT>=17) {
                if (activity.isDestroyed()) {
                    return;
                }
            }
            activity.handleSendGiftData();
        }
    }

    private ChatBarSendPacketGiftBean pacageGiftBean;
    //礼物的类
    private Gift mCurrentGift;
//    private Gift mTemporyGift;
    private int sendNum;
    private long lastUserGiftId;//用户上次赠送礼物的user_gift_id
    private List<GiftComponentBean> componentBeenList = new ArrayList<>();
    private String giftUrl;//礼物图标
//    private User currentUser;
    private long mReqSendMsgFlag;// 发送私聊的请求
    /**
     * 发送私聊socket消息时候本地插入一条消息
     */
//    private ChatRecord mChatRecord;


    private void handleSendGiftData() {
        Logger.d(TAG, "handleSendGiftData() into");
        if (pacageGiftBean!=null && pacageGiftBean.getSend_user()!=null && pacageGiftBean.getReceive_user()!=null) {
            //TODO:1）接收dialog回传回来的倒计时已经结束的标识，2）并且只接收自己发送的礼物的socket（senderid跟user_gift_id）消息并确定连送关系，
            //TODO: 3）防止用户异常退出，需要将组装数据进行保存，组装数据进行发送私聊socket消息4）onstop()中检查保存的数据，发送socket消息
            if (pacageGiftBean.getSend_user().getUserid() == Common.getInstance().loginUser.getUid()) {
                mCurrentGift = new Gift();
                sendNum = pacageGiftBean.getGift().getCombo_value() * pacageGiftBean.getGift().getCombo_num();
                mCurrentGift.setName(pacageGiftBean.getGift().getGift_name());//礼物名称
                mCurrentGift.setGiftdesc(pacageGiftBean.getGift().getGift_desc());//礼物的单位
                mCurrentGift.setCharisma(pacageGiftBean.getGift().getGift_charm_num());
                mCurrentGift.setCurrencytype(pacageGiftBean.getGift().getGift_currencytype());
                mCurrentGift.setPrice(pacageGiftBean.getGift().getGift_price());
                mCurrentGift.setExperience(pacageGiftBean.getGift().getGift_exp());//经验值
                mCurrentGift.setIconUrl(pacageGiftBean.getGift().getGift_icon()==null?"":pacageGiftBean.getGift().getGift_icon());
                if(pacageGiftBean.getGift().getUser_gift_id()==lastUserGiftId){
                    // gh 对集合修改时需要做一个临时存贮
                    ArrayList<GiftComponentBean> componentListS = new ArrayList();
                    componentListS.addAll(componentBeenList);
                    for(GiftComponentBean componentBean:componentListS){
                        if(componentBean.getUsergiftId() == lastUserGiftId){
                            componentBeenList.remove(componentBean);
                            GiftComponentBean giftComponentBean = new GiftComponentBean();
                            giftComponentBean.setUsergiftId(pacageGiftBean.getGift().getUser_gift_id());
                            giftComponentBean.setGift(mCurrentGift);
                            giftComponentBean.setGiftNum(sendNum);
                            componentBeenList.add(giftComponentBean);
                        }

                    }
                    mHandler.removeCallbacks(sendGiftRun);
                    mHandler.postDelayed(sendGiftRun,6000);

                }else {
                    GiftComponentBean giftComponentBean = new GiftComponentBean();
                    giftComponentBean.setUsergiftId(pacageGiftBean.getGift().getUser_gift_id());
                    giftComponentBean.setGift(mCurrentGift);
                    giftComponentBean.setGiftNum(sendNum);
                    componentBeenList.add(giftComponentBean);
                    mHandler.postDelayed(sendGiftRun,6000);
                }
                lastUserGiftId = pacageGiftBean.getGift().getUser_gift_id();
            }
            if(pacageGiftBean.getReceive_user().getUserid() == Common.getInstance().loginUser.getUid()){
                Logger.i(TAG, "礼物消息增加爱心数:"+pacageGiftBean.getGift().getGift_price());
                if( pacageGiftBean.getGift().getGift_price() >0 ){
                     mVideoChatTotalLoveCount += pacageGiftBean.getGift().getGift_price();
                     mCallingLoveTV.setText(String.valueOf(mVideoChatTotalLoveCount));
                }
            }
        }else{
            Logger.e(TAG, "礼物消息为空或者接受者发送者为空");
        }
    }

    private Runnable sendGiftRun = new SendGiftMessageRunnable(this);

    static class SendGiftMessageRunnable implements Runnable{
        private WeakReference<VideoChatActivity> mActivity;
        private SendGiftMessageRunnable(VideoChatActivity activity){
            mActivity = new WeakReference<VideoChatActivity>(activity);
        }

        @Override
        public void run() {
            VideoChatActivity activity = mActivity.get();
            if(null==activity){
                return;
            }
            if(Build.VERSION.SDK_INT>=17) {
                if (activity.isDestroyed()) {
                    return;
                }
            }
            activity.sendGiftMessage(activity.pacageGiftBean.getReceive_user().getUserid(),activity.sendNum,false);
        }
    }

    /**
     * 当发送礼物消息成功之后，发送一条私聊消息
     */
    private void sendGiftMessage(int fUid,int giftNum,boolean isShow) {
        Logger.d(TAG, "sendGiftMessage() 发送礼物消息成功之后，发送私聊消息 into ");
        if(lastUserGiftId!=pacageGiftBean.getGift().getUser_gift_id()){
            lastUserGiftId = 0;
        }
        User mUser = null;
        ChatRecord mChatRecord = null;
        if (mCurrentGift != null&&isShow) {
            giftUrl = mCurrentGift.getIconUrl();
            mUser=new User();
            mUser.setUid(fUid);
            mUser.setIcon(pacageGiftBean.getReceive_user().getIcon());
            mUser.setNickname(pacageGiftBean.getReceive_user().getNickname());
            mUser.setViplevel(pacageGiftBean.getReceive_user().getViplevel());
            mUser.setSVip(pacageGiftBean.getReceive_user().getVip());
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("giftname", mCurrentGift.getName(VideoChatActivity.this));
            map.put("charmnum", String.valueOf(mCurrentGift.getCharisma()*giftNum));
            String price = "";
            price=String.valueOf(pacageGiftBean.getGift().getGift_price()*giftNum);
            map.put("price", price);
            map.put("currencytype", String.valueOf(mCurrentGift.getCurrencytype()));
            map.put("giftnum", giftNum);
            map.put("exp", mCurrentGift.expvalue*giftNum);
            map.put("isFromChatRoom",2+"");
            map.put("gift_desc",mCurrentGift.getGiftdesc(VideoChatActivity.this));
            String content = JsonUtil.mapToJsonString(map);
            mReqSendMsgFlag = System.currentTimeMillis();
            Logger.i(TAG, "call SocketSessionProtocol.sessionPrivateMsg()");
            long flag = SocketSessionProtocol.sessionPrivateMsg(BaseApplication.appContext, mReqSendMsgFlag,
                    fUid, 0, String.valueOf(ChatRecordViewFactory.GIFE_REMIND),
                    giftUrl, 0, content);//mCurrentGift.getIconUrl()

            if (flag == -1) {
                mHandler.sendEmptyMessage(SuperChat.HandleMsgCode.MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
                return;
            }

            Me me = Common.getInstance().loginUser;
            mChatRecord = new ChatRecord();
            mChatRecord.setId(-1); // 消息id
            mChatRecord.setUid(me.getUid());
            mChatRecord.setNickname(me.getNickname());
            mChatRecord.setIcon(me.getIcon());
            mChatRecord.setVip(me.getViplevel());
            mChatRecord.setDatetime(mReqSendMsgFlag);
            mChatRecord.setType(Integer.toString(ChatRecordViewFactory.GIFE_REMIND));
            mChatRecord.setStatus(ChatRecordStatus.SENDING); // 发送中

            if(mCurrentGift==null){
                mCurrentGift=new Gift();
            }
            mChatRecord.setAttachment(mCurrentGift.getIconUrl() == null ? "" : mCurrentGift.getIconUrl());
            mChatRecord.setContent(content);
            mChatRecord.setUpload(false);
            mChatRecord.setfLat(mUser.getLat());
            mChatRecord.setfLng(mUser.getLng());
            GeoData geo = LocationUtil.getCurrentGeo(mContext);
            if (VideoChatManager.getInstance().getCurrent() != null)// 防止用户对象
                mChatRecord.setDistance( LocationUtil.calculateDistance(VideoChatManager.getInstance().getCurrent().lng, VideoChatManager.getInstance().getCurrent().lat, geo.getLng(), geo.getLat()));

        }else {
            if(componentBeenList.isEmpty()){
                return;
            }
            Gift mTemporyGift = null;
            for(int i=0;i<componentBeenList.size();i++){
                mTemporyGift = componentBeenList.get(i).getGift();
                int num = componentBeenList.get(i).getGiftNum();
                giftNum = num;
                if (mTemporyGift!=null) {
                    giftUrl = mTemporyGift.getIconUrl();
                    mUser = new User();
                    mUser.setUid(fUid);
                    mUser.setIcon(pacageGiftBean.getReceive_user().getIcon());
                    mUser.setNickname(pacageGiftBean.getReceive_user().getNickname());
                    mUser.setViplevel(pacageGiftBean.getReceive_user().getViplevel());
                    mUser.setSVip(pacageGiftBean.getReceive_user().getVip());
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("giftname", mTemporyGift.getName(VideoChatActivity.this));

                    map.put("charmnum", String.valueOf(mTemporyGift.getCharisma() * giftNum));
                    String price = "";

                    price = String.valueOf(pacageGiftBean.getGift().getGift_price() * giftNum);
                    map.put("price", price);
                    map.put("currencytype", String.valueOf(mTemporyGift.getCurrencytype()));
                    map.put("giftnum", giftNum);
                    map.put("exp", mTemporyGift.expvalue * giftNum);
                    map.put("isFromChatRoom", 2 + "");
                    map.put("gift_desc", mTemporyGift.getGiftdesc(VideoChatActivity.this));
                    String content = JsonUtil.mapToJsonString(map);
                    mReqSendMsgFlag = System.currentTimeMillis();
                    Logger.i(TAG, "call SocketSessionProtocol.sessionPrivateMsg()");
                    long flag = SocketSessionProtocol.sessionPrivateMsg(BaseApplication.appContext, mReqSendMsgFlag,
                            fUid, 0, String.valueOf(ChatRecordViewFactory.GIFE_REMIND),
                            giftUrl, 0, content);//mCurrentGift.getIconUrl()

                    //发送世界消息 有条件
//                    if(!TextUtils.isEmpty(price)){
//                        int priceNum = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getInt(SharedPreferenceUtil.GIFT_DIAMOND_MIN_NUM);
//                        // 礼物消息是金币类型的不发世界消息
//                        if(pacageGiftBean.getGift().getGift_currencytype() != 1 & Integer.parseInt(price) > priceNum){
//                            String str = getString(R.string.chat_bar_send_gift_world_message)+"@"+giftNum+mTemporyGift.getGiftdesc(GroupChatTopicActivity.this)+mTemporyGift.getName(GroupChatTopicActivity.this)+getString(R.string.chat_bar_send_gift_consume_much);
//                            WorldMessageGiftContent worldMessageGiftContent = new WorldMessageGiftContent();
//                            worldMessageGiftContent.message = str;
//                            worldMessageGiftContent.targetUserName = mUser.getNickname();
//                            String message = JSON.toJSONString(worldMessageGiftContent);
//                            SendWorldMessageProtocol.getInstance().getSendWorldMessageData(mContext,Integer.parseInt(groupId),message,32,new GroupChatTopicActivity.ChatBarHttpCallback(this));
//
//                        }
//                    }

                    if (flag == -1) {
                        //发送礼物私聊消息结果
                        mHandler.sendEmptyMessage(SuperChat.HandleMsgCode.MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
                        return;
                    }

                    Me me = Common.getInstance().loginUser;
                    mChatRecord = new ChatRecord();
                    mChatRecord.setId(-1); // 消息id
                    mChatRecord.setUid(me.getUid());
                    mChatRecord.setNickname(me.getNickname());
                    mChatRecord.setIcon(me.getIcon());
                    mChatRecord.setVip(me.getViplevel());
                    mChatRecord.setDatetime(mReqSendMsgFlag);
                    mChatRecord.setType(Integer.toString(ChatRecordViewFactory.GIFE_REMIND));
                    mChatRecord.setStatus(ChatRecordStatus.ARRIVED); // 发送中

                    if (mTemporyGift == null) {
                        mTemporyGift = new Gift();
                    }
                    mChatRecord.setAttachment(mTemporyGift.getIconUrl() == null ? "" : mTemporyGift.getIconUrl());
                    mChatRecord.setContent(content);
                    mChatRecord.setUpload(false);
                    mChatRecord.setfLat(mUser.getLat());
                    mChatRecord.setfLng(mUser.getLng());
                    GeoData geo = LocationUtil.getCurrentGeo(mContext);
                    if (VideoChatManager.getInstance().getCurrent() != null)// 防止用户对象
                        mChatRecord.setDistance(LocationUtil.calculateDistance(VideoChatManager.getInstance().getCurrent().lng, VideoChatManager.getInstance().getCurrent().lat, geo.getLng(), geo.getLat()));
                }
//                int subGroup;
//                if (mtype == 0) {
//                    subGroup = SubGroupType.NormalChat;
//                } else {
//                    subGroup = SubGroupType.SendAccost;
//                }
                ChatPersonalModel.getInstance().insertOneRecord(this, mUser,
                        mChatRecord, SubGroupType.NormalChat, 0);
//                mTemporyGift = null;


            }
            componentBeenList.clear();
        }
    }

    /**
     * 修改两个人的关系
     */
    private void updateAccostRelation(){
        if(VideoChatManager.getInstance().getOther()!=null) {
            ChatPersonalModel.getInstance().putAccostRelation(this, VideoChatManager.getInstance().getOther().uid, 1);
        }
    }

    private void handleGiftLove() {
        Logger.d(TAG, "handleGiftLove() into");
        if (pacageGiftBean!=null && pacageGiftBean.getSend_user()!=null && pacageGiftBean.getReceive_user()!=null) {
            if(pacageGiftBean.getReceive_user().getUserid() == Common.getInstance().loginUser.getUid()){
                Logger.i(TAG, "礼物消息增加爱心: 价格:"+pacageGiftBean.getGift().getGift_price() + ", 数量:" + pacageGiftBean.getGift().getCombo_value());
                mVideoChatTotalLoveCount += pacageGiftBean.getGift().getGift_price() * pacageGiftBean.getGift().getCombo_value();
                mCallingLoveTV.setText(String.valueOf(mVideoChatTotalLoveCount));
            }
        }else{
            Logger.e(TAG, "礼物消息为空或者接受者发送者为空");
        }
    }
}
