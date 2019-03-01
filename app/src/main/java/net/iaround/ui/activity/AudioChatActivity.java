package net.iaround.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.floatwindow.AudioChatFloatWindowHelper;
import net.iaround.mic.AudioChatManager;
import net.iaround.im.WebSocketManager;
import net.iaround.im.service.WebSocketService;
import net.iaround.model.audiochat.AudioChatBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.utils.DeviceUtils;


/**
 * 语音聊天界面
 */
public class AudioChatActivity extends BaseActivity implements View.OnClickListener, WebSocketService.AudioChatServiceListener {

    public static final int STATUS_CALLER = 0;//用户拨打界面
    public static final int STATUS_CALLEE = 1;//主播待接通界面
    public static final int STATUS_CONNECT = 2;//接通后界面
//    public static final int STATUS_CLOSE = 3;//挂断

    private ImageView mIvAudioChatBg;//聊天背景
    private ImageView mIvSmall;//语音聊天最小化界面
    private TextView mTvCurrentIncome;//当前收入
    private TextView mTvTip;//状态提示 接通状态以及通话时长
    private TextView mTvName;
    private ImageView mIvHead;
    private LinearLayout mLlEndTip;//倒计时布局
    private TextView mTvCountDown;
    private LinearLayout mLlWaitForConnectCallee;//被呼叫者等待接通状态
    private ImageView mIvRefuse;//被呼叫者拒绝接听
    private ImageView mIvAnswer;//被呼叫者接听
    private LinearLayout mLlWaitForConnectCaller;//呼叫者等待接通状态
    private ImageView mIvCancel;//呼叫者取消拨打
    private LinearLayout mLlConnected;//接通后的布局
    private ImageView mIvSilence;//静音 关闭自己的麦克风
    private ImageView mIvHangUp;//挂断
    private LinearLayout mLlHangUp;//挂断按钮布局
    private ImageView mIvHandsFree;//免提
    private TextView mTvAddTime;//加时

    private AudioChatBean mAudioChatBean;
    private WebSocketManager mWebSocketManager;
    private AudioChatManager mAudioChatManager;
    private int mStatus;//通话状态

    private String mIcon;//头像
    private String mName;//名称

    private AudioManager mAudioManager;

    private Handler mHandler;

    private boolean isCreateFloat = false;//是否要创建最小化窗口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_chat);
        initView();
        mHandler = new Handler(Looper.getMainLooper());
        Intent data = getIntent();
        mStatus = data.getIntExtra("status", 0);
        mIcon = data.getStringExtra("icon");
        mName = data.getStringExtra("name");

        mWebSocketManager = WebSocketManager.getsInstance();
        mAudioChatManager = AudioChatManager.getsInstance();
        mAudioChatBean = mWebSocketManager.getAudioChatBean();
        initDate();
        switch (mStatus) {
            case STATUS_CALLER:
                showCaller();
                break;
            case STATUS_CALLEE:
                showCallee();
                break;
            case STATUS_CONNECT:
                showConnect();
                break;
        }
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
//        mAudioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);

        Intent intent = new Intent(this, WebSocketService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

        if (CommonFunction.isMobile(this)) {
            //提示当前正在使用移动网络
            CommonFunction.toastMsg(BaseApplication.appContext, mContext.getString(R.string.just_using_mobile_for_video_chat));
        }
        AudioChatFloatWindowHelper.getInstance().destroy();
        AudioChatFloatWindowHelper.getInstance().init(BaseApplication.appContext);
    }

    private void initView() {
        mIvAudioChatBg = findView(R.id.iv_audio_chat_bg);
        mIvSmall = findView(R.id.iv_small);
        mTvCurrentIncome = findView(R.id.tv_current_income);
        mTvTip = findView(R.id.tv_tip);
        mTvName = findView(R.id.tv_name);
        mIvHead = findView(R.id.iv_head);
        mLlEndTip = findView(R.id.ll_end_tip);
        mTvCountDown = findView(R.id.tv_count_down);
        mLlWaitForConnectCallee = findView(R.id.ll_wait_for_connect_callee);
        mIvRefuse = findView(R.id.iv_refuse);
        mIvAnswer = findView(R.id.iv_answer);
        mLlWaitForConnectCaller = findView(R.id.ll_wait_for_connect_caller);
        mIvCancel = findView(R.id.iv_cancel);
        mLlConnected = findView(R.id.ll_connected);
        mIvSilence = findView(R.id.iv_silence);
        mIvHangUp = findView(R.id.iv_hang_up);
        mLlHangUp = findView(R.id.ll_hang_up);
        mIvHandsFree = findView(R.id.iv_hands_free);
        mTvAddTime = findView(R.id.tv_add_time);
        mIvSmall = findView(R.id.iv_small);

        mIvAnswer.setOnClickListener(this);
        mIvCancel.setOnClickListener(this);
        mIvRefuse.setOnClickListener(this);
        mIvHangUp.setOnClickListener(this);
        mTvAddTime.setOnClickListener(this);
        mIvSilence.setOnClickListener(this);
        mIvHandsFree.setOnClickListener(this);
        mIvSmall.setOnClickListener(this);
    }

    private void initDate() {
        if (mAudioChatBean != null) {
            if (mWebSocketManager.isSendUser()) {
                GlideUtil.loadCircleImage(BaseApplication.appContext, mAudioChatBean.ReceiveUserICON, mIvHead);
                GlideUtil.loadImage(BaseApplication.appContext, mAudioChatBean.ReceiveUserICON, mIvAudioChatBg);
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, mAudioChatBean.ReceiveUserNickName, 0, null);
                mTvName.setText(spName);
            } else {
                GlideUtil.loadCircleImage(BaseApplication.appContext, mAudioChatBean.SendUserICON, mIvHead);
                GlideUtil.loadImage(BaseApplication.appContext, mAudioChatBean.SendUserICON, mIvAudioChatBg);
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, mAudioChatBean.SendUserNickName, 0, null);
                mTvName.setText(spName);
            }
        } else {
            if (!TextUtils.isEmpty(mIcon)) {
                GlideUtil.loadCircleImage(BaseApplication.appContext, mIcon, mIvHead);
                GlideUtil.loadImage(BaseApplication.appContext, mIcon, mIvAudioChatBg);
                SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, mName, 0, null);
                mTvName.setText(spName);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestMicshowPermissions();
        if (isCreateFloat) {
            if (Build.VERSION.SDK_INT > 24 || (DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >= 23)) {
                if (AudioChatFloatWindowHelper.getInstance().checkPermission()) {
                    AudioChatFloatWindowHelper.getInstance().createWindow();
                }
            }
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_answer:
                WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_ANCHOR_AGREE);
                showConnect();
                break;
            case R.id.iv_cancel://呼叫者取消
                if (mWebSocketManager.getStatusConnect() == WebSocketManager.STATUS_CONNECT) {
                    if (!TextUtils.isEmpty(mAudioChatManager.getRoomId())) {
                        mWebSocketManager.sendMessage(WebSocketManager.TAG_CANCEL_CALL);
                    }
                }
                mIvCancel.setClickable(false);
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_other_cancel);
                mWebSocketManager.logoutAudioRoom(false);
//                finish();
                break;
            case R.id.iv_refuse://主播拒绝
                WebSocketManager.getsInstance().sendMessage(WebSocketManager.TAG_ANCHOR_REFUSE);
//                if (mAudioChatManager.isHasLogin() && mWebSocketManager.getAudioChatBean() != null && WebSocketManager.getsInstance().isSendUser()) {
//                    mAudioChatManager.clearAudio();
//                }
//                finish();
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.video_chat_reject);
                mWebSocketManager.logoutAudioRoom(false);
                break;
            case R.id.iv_hang_up://挂断
                mIvHangUp.setClickable(false);
                mWebSocketManager.logoutAudioRoom(true);
//                finish();
                break;
            case R.id.tv_add_time://加时
                Intent intent = new Intent(this, StarPayActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_silence://关闭/打开自己麦克，静音
                if (mAudioChatManager.isEnableMic()) {
                    mAudioChatManager.enableMic(false);
                    mIvSilence.setSelected(true);
                } else {
                    mAudioChatManager.enableMic(true);
                    mIvSilence.setSelected(false);
                }
                break;
            case R.id.iv_hands_free://关闭/打开扬声器
//                if(mAudioChatManager.isSpeakerOn){
//                    mAudioChatManager.builtinSpeakerOn(false);
//                    mIvHandsFree.setSelected(false);
//                }else {
//                    mIvHandsFree.setSelected(true);
//                    mAudioChatManager.builtinSpeakerOn(true);
//                }
                CommonFunction.log("AudioChatActivity", "是否开扬声器" + mAudioManager.isSpeakerphoneOn());
                if (mAudioChatManager.isSpeakerOn()) {
//                    mAudioManager.setSpeakerphoneOn(false);
                    mIvHandsFree.setSelected(false);
                    mAudioChatManager.builtinSpeakerOn(false);
                } else {
                    mAudioChatManager.builtinSpeakerOn(true);
//                    mAudioManager.setSpeakerphoneOn(true);
                    mIvHandsFree.setSelected(true);
                }
                break;
            case R.id.iv_small:
//                finish();
                if (Build.VERSION.SDK_INT > 24 || (DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >= 23)) {
                    if (!AudioChatFloatWindowHelper.getInstance().checkPermission()) {
                        AudioChatFloatWindowHelper.getInstance().showOpenPermissionDialog();
                        isCreateFloat = true;
                        return;
                    }
                }
                AudioChatFloatWindowHelper.getInstance().createWindow();
                break;

        }
    }

    /**
     * 显示拨打布局
     */
    private void showCaller() {
        mLlWaitForConnectCaller.setVisibility(View.VISIBLE);
        mTvTip.setText(getString(R.string.video_chat_waiting_conform));
        //播放拨打提示音乐
        CommonFunction.playAudioChatVoice(this);
        mIvCancel.setClickable(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //3s 后才能点击取消拨打
                mIvCancel.setClickable(true);
            }
        }, 3000);
    }

    /**
     * 显示被呼叫界面
     */
    private void showCallee() {
        mLlWaitForConnectCallee.setVisibility(View.VISIBLE);
        mTvTip.setText(R.string.audio_chat_invite);
        //播放拨打提示音乐
        CommonFunction.playAudioChatVoice(this);
    }

    /**
     * 显示接通画面
     */
    private void showConnect() {
        CommonFunction.stopAudioChatVoice();
        mLlWaitForConnectCallee.setVisibility(View.GONE);
        mLlWaitForConnectCaller.setVisibility(View.GONE);
        mLlConnected.setVisibility(View.VISIBLE);
        mIvSmall.setVisibility(View.VISIBLE);
        if (mAudioChatManager.isSpeakerOn()) {
            mIvHandsFree.setSelected(true);
        } else {
            mIvHandsFree.setSelected(false);
        }
        if (mAudioChatManager.isEnableMic()) {
            mIvSilence.setSelected(false);
        } else {
            mIvSilence.setSelected(true);
        }

        if (mWebSocketManager.getAudioChatBean() != null && mWebSocketManager.isSendUser()) {
            mLlHangUp.setVisibility(View.VISIBLE);
        } else {
            mLlHangUp.setVisibility(View.INVISIBLE);
        }

        mTvTip.setText("00:00");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            WebSocketService webSocketService = ((WebSocketService.MyBinder) service).getService();
            webSocketService.setAudioChatServiceListener(AudioChatActivity.this);
            if (mStatus == STATUS_CONNECT) {//如果是接通状态
                mTvTip.setText(webSocketService.getCurrentAudioChatTime());
                if (webSocketService.isShowCountdown()) {
                    mLlEndTip.setVisibility(View.VISIBLE);
                    if (mWebSocketManager.getAudioChatBean() != null && mWebSocketManager.isSendUser()) {
                        mTvAddTime.setVisibility(View.VISIBLE);
                    }
                    mTvCountDown.setText(webSocketService.getCurrentCountdownTime());
                } else {
                    mTvAddTime.setVisibility(View.GONE);
                    mLlEndTip.setVisibility(View.INVISIBLE);
                }
                if (mWebSocketManager.getAudioChatBean() != null && !mWebSocketManager.isSendUser()) {
                    mTvCurrentIncome.setVisibility(View.VISIBLE);
                    mTvCurrentIncome.setText(String.format(getString(R.string.current_income), webSocketService.getCurrentPrice()));
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void updateAnswerState(int state) {
        switch (state) {
//            case STATUS_CLOSE:
//                mWebSocketManager.logoutAudioRoom(true);
//                break;
            case STATUS_CONNECT:
                showConnect();
                break;
        }
    }

    @Override
    public void updateCurrentIncome(String price) {
        mTvCurrentIncome.setVisibility(View.VISIBLE);
        mTvCurrentIncome.setText(String.format(getString(R.string.current_income), price));
    }

    @Override
    public void updateCallDuration(String time, int second) {
        if (mWebSocketManager.getAudioChatBean() != null && !mWebSocketManager.isSendUser()) {
            if (second >= 60) {
                mLlHangUp.setVisibility(View.VISIBLE);
            } else {
                mLlHangUp.setVisibility(View.INVISIBLE);
            }
        }
        mTvTip.setText(time);
    }

    @Override
    public void updateCountDown(String time) {
        if (TextUtils.isEmpty(time)) {
            mLlEndTip.setVisibility(View.INVISIBLE);
            mTvAddTime.setVisibility(View.GONE);
        } else {
            mLlEndTip.setVisibility(View.VISIBLE);
            mTvCountDown.setText(time);
            if (mWebSocketManager.getAudioChatBean() != null && mWebSocketManager.isSendUser()) {
                mTvAddTime.setVisibility(View.VISIBLE);
            } else {
                mTvAddTime.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void updateIcon(AudioChatBean bean) {
        mAudioChatBean = bean;
        initDate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        CommonFunction.stopAudioChatVoice();
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
