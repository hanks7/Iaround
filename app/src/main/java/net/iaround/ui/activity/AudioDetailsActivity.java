package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.im.WebSocketManager;
import net.iaround.im.service.WebSocketService;
import net.iaround.mic.AudioChatManager;
import net.iaround.model.audiochat.AudioChatBean;
import net.iaround.model.entity.AudioDetailsBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.tools.im.AudioPlayUtils;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.view.CustomViewPager;
import net.iaround.ui.view.IAViewpagerIndector;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 语音主播详情界面
 */
public class AudioDetailsActivity extends BaseActivity implements HttpCallBack, View.OnClickListener, WebSocketManager.CheckCallStatusListener, AudioPlayUtils.AudioPlayStateCallback {

    private static final String TAG = "AudioDetailsActivity";

    private CustomViewPager mCvpPicture;
    private ImageView mIvAudioDetailsIcon;
    private ImageView mIvSex;
    private TextView mTvAudioDetailsName;
    private TextView mTvAudioDetailsAddress;
    private TextView mTvAudioDetailsPrivateChat;
    private ImageView mIvClose;
    private LinearLayout mLlAudioChat;
    private TextView mTvAudioChat;
    private TextView mTvPrice;
    private TextView mTvInstruction;
    private ImageView mIvPlayAudio;
    private TextView mTvAudioLength;
    private IAViewpagerIndector mViewpagerIndicator;

    private AudioDetailsBean mBean;
    private ArrayList<String> mPhotos;
    private String mVoiceUrl;//音频地址

    private long mUid;//主播uid

    private boolean mIsCurrentStart;//是否是当前界面启动的WebSocket

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //统计登录后在语音主播详情页面停留的用户人数
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEvent(BaseApplication.appContext, "watch_voice_anchor_detail_num", map);

        setContentView(R.layout.activity_audio_details);
        mUid = getIntent().getLongExtra("UserId", 0);
        CommonFunction.log(TAG, "mUid: " + mUid);
        mHandler = new Handler(Looper.getMainLooper());
        initView();
        showWaitDialog();
        AnchorsCertificationProtocol.getAnchorAudioDetails(this, mUid, this);
    }

    private void initView() {
        mCvpPicture = findView(R.id.cvp_picture);
        mIvAudioDetailsIcon = findView(R.id.iv_audio_details_icon);
        mIvSex = findView(R.id.iv_sex);
        mTvAudioDetailsName = findView(R.id.tv_audio_details_name);
        mTvAudioDetailsAddress = findView(R.id.tv_audio_details_address);
        mTvAudioDetailsPrivateChat = findView(R.id.tv_audio_details_private_chat);
        mIvClose = findView(R.id.iv_close);
        mLlAudioChat = findView(R.id.ll_audio_chat);
        mTvAudioChat = findView(R.id.tv_audio_chat);
        mTvPrice = findView(R.id.tv_price);
        mTvInstruction = findView(R.id.tv_instruction);
        mIvPlayAudio = findView(R.id.iv_play_audio);
        mTvAudioLength = findView(R.id.tv_audio_length);
        mViewpagerIndicator = findView(R.id.viewpager_indicator);


        mIvPlayAudio.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
        mTvAudioDetailsPrivateChat.setOnClickListener(this);
        mLlAudioChat.setOnClickListener(this);
        mIvAudioDetailsIcon.setOnClickListener(this);

        WebSocketManager.getsInstance().setCheckCallStatusListener(this);

        if (mUid == Common.getInstance().loginUser.getUid()) {
            mLlAudioChat.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
            mTvAudioChat.setTextColor(getResources().getColor(R.color.common_black));
            mTvPrice.setTextColor(getResources().getColor(R.color.common_black));
            mLlAudioChat.setEnabled(false);
            mTvAudioDetailsPrivateChat.setVisibility(View.GONE);
        }

    }


    private void initData(AudioDetailsBean bean) {
        mPhotos = bean.photos;
        FlyPageAdapter adapter = new FlyPageAdapter();
        mCvpPicture.setAdapter(adapter);
        mCvpPicture.addOnPageChangeListener(mOnPageChangeListener);

        mViewpagerIndicator.setCount(mPhotos.size());

        GlideUtil.loadCircleImage(this, bean.icon, mIvAudioDetailsIcon);
        if (!TextUtils.isEmpty(bean.nickName)) {
            SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, bean.nickName, 0, null);
            mTvAudioDetailsName.setText(spName);
        }
        if ("m".equals(bean.sex)) {
            mIvSex.setImageResource(R.drawable.bg_home_sex_man);
        } else {
            mIvSex.setImageResource(R.drawable.bg_home_sex_girl);
        }
        mTvAudioDetailsAddress.setText(bean.addr);
        mTvPrice.setText(bean.price + " " + bean.unit);
        mTvInstruction.setText(bean.description);

        mVoiceUrl = bean.voice;
        CommonFunction.log(TAG, "mVoiceUrl: " + mVoiceUrl);
        AudioPlayUtils.getInstance().playAudio(BaseApplication.appContext, mVoiceUrl,true, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mLlAudioChat.setClickable(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLlAudioChat.setClickable(true);
            }
        },1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_play_audio:
                //关闭聊吧悬浮窗
                ChatBarZoomWindow.getInstance().close();
                AudioPlayUtils.getInstance().playAudio(BaseApplication.appContext, mVoiceUrl,false, this);
                break;
            case R.id.tv_audio_details_private_chat:
                if (mBean != null && mBean.targetUid != Common.getInstance().loginUser.getUid()) {

                    User user = new User();
                    user.setUid(mBean.targetUid);
                    user.setIcon(mBean.icon);
                    user.setNickname(mBean.nickName);
                    ChatPersonal.skipToChatPersonal(this, user, 0);
                }
                break;
            case R.id.iv_close:
                finish();
                break;
            case R.id.ll_audio_chat:
                if (ChatBarZoomWindow.getInstance().isShowing()) {
                    DialogUtil.showTowButtonDialog(this, getString(R.string.prompt), getString(R.string.close_chat_bar_for_audio),
                            getString(R.string.ok), getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    requestMicshowPermissions();
                                }
                            }, null);
                } else {
                    requestMicshowPermissions();
                }
                break;
            case R.id.iv_audio_details_icon:
                if (mUid == Common.getInstance().loginUser.getUid()) {
                    Intent intent = new Intent(mContext, UserInfoActivity.class);
                    intent.putExtra(Constants.UID, mUid);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, OtherInfoActivity.class);
                    intent.putExtra(Constants.UID, mUid);
                    mContext.startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void doMicshowPermissions() {
        super.doMicshowPermissions();
        if (AudioChatManager.getsInstance().isHasLogin()) {
            if (WebSocketManager.getsInstance().getAudioChatBean() != null && WebSocketManager.getsInstance().getAudioChatBean().ReceiveUserID == mUid) {
                //如果正在通话中，并且是跟当前主播通话，则直接进入通话界面
                Intent intent = new Intent(this, AudioChatActivity.class);
                intent.putExtra("status", AudioChatActivity.STATUS_CONNECT);
                if (mBean != null) {
                    intent.putExtra("icon", mBean.icon);
                    intent.putExtra("name", mBean.nickName);
                }
                startActivity(intent);
            } else {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.can_not_make_phone_tip);
            }
        } else {
            //先销毁聊吧悬浮窗和聊吧推拉流
            ChatBarZoomWindow.getInstance().close();
            Intent intent = new Intent(this, AudioChatActivity.class);
            intent.putExtra("status", AudioChatActivity.STATUS_CALLER);
            if (mBean != null) {
                intent.putExtra("icon", mBean.icon);
                intent.putExtra("name", mBean.nickName);
            }
            startActivity(intent);
            if (CommonFunction.isServiceExisted(BaseApplication.appContext, WebSocketService.class.getName())) {
                sendAudioChatMessage();
            } else {
                mIsCurrentStart = true;
                //需要重新设置监听
                WebSocketManager.getsInstance().setCheckCallStatusListener(this);
                WebSocketManager.getsInstance().startWebSocketService(this);
            }
            if (Common.getInstance().loginUser.getVoiceUserType() == 0) {
            }
            mLlAudioChat.setClickable(false);
        }
    }

    /**
     * 弹出是否充值弹窗
     */
    private void gotoStarPay() {
//        SpannableString spName = FaceManager.getInstance(mContext).parseIconForString(mContext, mBean.nickName, 0, null);
        DialogUtil.showTowButtonDialog(this, getString(R.string.star_balance), getString(R.string.pay_star_tip), getString(R.string.recharge_love_recharge), getString(R.string.cancel), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AudioDetailsActivity.this, StarPayActivity.class);
                startActivity(intent);
            }
        }, null);
    }

    /**
     * 发送发起语音聊天消息
     */
    private void sendAudioChatMessage() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("Status", WebSocketManager.TAG_CALL);
        map.put("ReceiveUserID", mUid);
        map.put("SendUserID", Common.getInstance().loginUser.getUid());
        map.put("GameID", Constants.AUDIO_CHAT_GAME_ID);//语音ID 13
        WebSocketManager.getsInstance().sendMessage(map);
    }

    @Override
    public void onOpen() {
        if (mIsCurrentStart) {
            sendAudioChatMessage();
            mIsCurrentStart = false;
        }
    }

    /**
     * 检查是否可以拨打
     */
    @Override
    public void checkStatus(AudioChatBean bean) {
        mLlAudioChat.setClickable(true);
        if (bean != null) {
            if (bean.Code != 200) {
                if (bean.Code == 305) {
                    gotoStarPay();
                } else {
                    CommonFunction.toastMsg(BaseApplication.appContext, bean.Info);
                }
            }
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        mBean = GsonUtil.getInstance().getServerBean(result, AudioDetailsBean.class);
        if (mBean != null && mBean.photos != null && mBean.photos.size() > 0) {
            initData(mBean);
        } else {
            destroyWaitDialog();
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mPhotos != null && mPhotos.size() > 0) {
                position = position % mPhotos.size();
                mViewpagerIndicator.setPosition(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public void getPlayDuration(int duration) {
        destroyWaitDialog();
        CommonFunction.log(TAG, "prepare() mAudioDuration: " + duration);
        mTvAudioLength.setText(String.format("%02d", duration) + "s");
        mIvPlayAudio.setImageResource(R.drawable.btn_play);
    }

    @Override
    public void onPauseStarted() {
        mIvPlayAudio.setImageResource(R.drawable.btn_play);
    }

    @Override
    public void OnPlayingProgress(int mCountTime) {
        if (mCountTime >= 0) {
            mTvAudioLength.setText(String.format("%02d", mCountTime) + "s");
        } else {
            mTvAudioLength.setText("0s");
        }
        mIvPlayAudio.setImageResource(R.drawable.btn_stop);
    }

    @Override
    public void onPlayCompleted(int duration) {
        mTvAudioLength.setText(String.format("%02d", duration) + "s");
        mIvPlayAudio.setImageResource(R.drawable.btn_play);
    }

    @Override
    public void OnPlayError() {

    }

    private class FlyPageAdapter extends PagerAdapter {

        public FlyPageAdapter() {
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(BaseApplication.appContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            if (mPhotos != null && mPhotos.size() > 0) {
                position = position % mPhotos.size();
                GlideUtil.loadImage(BaseApplication.appContext, mPhotos.get(position), imageView);
                container.addView(imageView);
            }
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    protected void onDestroy() {
        AudioPlayUtils.getInstance().releaseRes();
        WebSocketManager.getsInstance().setCheckCallStatusListener(null);
        super.onDestroy();
    }
}
