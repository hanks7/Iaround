package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.im.STNManager;
import net.iaround.im.proto.Iachat;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.push.IPushMessageHandler;
import net.iaround.im.push.PushMessage;
import net.iaround.model.entity.VideoDetailsBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;

import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.VideoChatModel;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.video.AliVideoViewPlayer;
import net.iaround.ui.view.video.AliVideoViewPlayer.OnInitListener;
import net.iaround.utils.logger.Logger;
import net.iaround.videochat.VideoChatManager;

import java.lang.ref.WeakReference;

/**
 * 主播视频详情
 * Created by Administrator on 2017/12/11.
 */

public class VideoDetailsActivity extends BaseActivity implements View.OnClickListener, HttpCallBack, IPushMessageHandler {
    public static String KEY_VIDEO_UID = "key_video_uid";
    public static String KEY_VIDEO_PATH = "key_video_path";
    public static String KEY_VIDEO_IMG = "key_video_img";
    public static final int REQUEST_CODE_VIDEO_CHAT = 8000; //请求视频会话界面 返回是否关注用户

    private AliVideoViewPlayer videoPlay;
    private ProgressBar playProgree;
    private HeadPhotoView iconHead;
    private TextView idTv;
    private TextView nameTv;
    private TextView addressTv;
    private TextView moodTextTv;
    private TextView chatMintueTv;
    private LinearLayout videoChatLy;
    private ImageView ivCover;

    private long uid;
    private String videoPath;
    private String videoCover;

    private VideoDetailsBean bean = null;//当前主播信息

    private boolean mRequestPermission = false; //是否请求过摄像头权限 为视频会话做准备

    private boolean isRefersh = true;//视频会话状态

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.activity_video_details);

        uid = getIntent().getLongExtra(KEY_VIDEO_UID,0);
        videoPath = getIntent().getStringExtra(KEY_VIDEO_PATH);
        videoCover = getIntent().getStringExtra(KEY_VIDEO_IMG);

        initView();

//        if (videoCover != null && !TextUtils.isEmpty(videoCover)){
//            Glide.with(this).asDrawable().load(videoCover).into(ivCover);
//        }

        if (videoPath != null && !TextUtils.isEmpty(videoPath)){

            videoPlay.play(videoPath);//开始播放视频
        }

//        requestData(true);

        //为视频会话通讯做准备
        initCall();

        videoPlay.setOnInitListener(new OnInitListener() {
            @Override
            public void onInit() {
                ivCover.setVisibility(View.GONE);
                findViewById(R.id.rl_pay_layout).setOnClickListener(VideoDetailsActivity.this);
            }
        });

    }

    private void initView(){

        videoPlay = (AliVideoViewPlayer) findViewById(R.id.video_details_video_view);

        iconHead = (HeadPhotoView)findViewById(R.id.video_details_icon);
        idTv = (TextView)findViewById(R.id.tv_video_details_id);
        nameTv = (TextView)findViewById(R.id.tv_video_details_name);
        addressTv = (TextView)findViewById(R.id.tv_video_details_address);
        moodTextTv = (TextView)findViewById(R.id.tv_video_details_moodtext);
        chatMintueTv = (TextView)findViewById(R.id.tv_video_details_private_chat_mintue);
        videoChatLy = (LinearLayout)findViewById(R.id.ly_video_details_video_chat);
        ivCover = (ImageView)findViewById(R.id.iv_video_details_cover1);

        findViewById(R.id.rl_video_details_private_chat).setOnClickListener(this);
        findViewById(R.id.tv_video_details_private_chat).setOnClickListener(this);
        findViewById(R.id.iv_video__details_close).setOnClickListener(this);

        playProgree = (ProgressBar)findViewById(R.id.video_details_progress);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
//            case R.id.rl_video_details_private_chat:
            case R.id.tv_video_details_private_chat:

                User tempuser = getCurrentUser();
                if (tempuser.getUid() == Common.getInstance().loginUser.getUid())return;
                //从聊吧呼起个人资料页面
                ChatPersonal.skipToChatPersonal(getActivity(), tempuser, 201, false, ChatFromType.UNKONW, false,false);

                break;
            case R.id.iv_video__details_close:
                finish();
                break;
            case R.id.ly_video_details_video_chat:
                if (bean == null || bean.info==null){
                    return;
                }
                if (getCurrentUser().getUid() == Common.getInstance().loginUser.getUid())return;

                if (!CommonFunction.isNetworkConnected(VideoDetailsActivity.this)){
                    CommonFunction.toastMsg(VideoDetailsActivity.this,R.string.network_req_failed);
                    return;
                }

                if (bean.info.uid == 0)return;

                isRefersh = false;
                {
                    //如果当前用户在聊吧里,先关闭聊吧窗口
                    GroupChatTopicActivity chatbar = (GroupChatTopicActivity)CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                    if(chatbar!=null){
                        Logger.i("VideoChat", "关闭聊吧窗口");
                        chatbar.isGroupIn = true;
                        CloseAllActivity.getInstance().closeTarget(GroupChatTopicActivity.class);
                    }

                    //对方用户信息
                    VideoChatManager.VideoChatUser other = new VideoChatManager.VideoChatUser();
                    other.follow = bean.info.follow;
                    other.anchor_follow = bean.info.anchor_follow;
                    other.svip = bean.info.sVip;
                    other.vip = bean.info.viplevel;
                    other.uid = bean.info.uid;
                    other.city = bean.info.landmarkname;
                    other.icon = bean.info.icon;
                    other.name = bean.info.nickName;
                    VideoChatManager.getInstance().setOther(other);

                    //当前用户信息
                    VideoChatManager.VideoChatUser current = new VideoChatManager.VideoChatUser();
                    current.uid = Common.getInstance().loginUser.getUid();
                    current.name = Common.getInstance().loginUser.getNickname();
                    current.icon = Common.getInstance().loginUser.getIcon();
                    VideoChatManager.getInstance().setCurrent(current);

                    //会话类型
                    VideoChatManager.getInstance().setType(VideoChatManager.VIDEO_CHAT_CALL_TYPE_SEND_CALL);

                    Logger.i("VideoChat", "呼起视频呼叫界面, 主叫用户=" + current.uid + ", 被叫用户=" + other.uid);
                    Intent intent = new Intent(VideoDetailsActivity.this, VideoChatActivity.class);
                    startActivityForResult(intent,REQUEST_CODE_VIDEO_CHAT);
                }
                break;
            case R.id.rl_pay_layout:
                    if (videoPlay != null)
                        videoPlay.playOrPause();
                break;
        }
    }

    @NonNull
    private User getCurrentUser() {
        User tempuser = new User();
        tempuser.setUid(uid);
        if(bean!=null && bean.info!=null) {
            tempuser.setNoteName(bean.info.notes);//YC
            tempuser.setNickname(bean.info.nickName);
            tempuser.setRelationship(bean.info.relation);
            tempuser.setSign(bean.info.moodText);
            tempuser.setLat((int) bean.info.lat);
            tempuser.setLng((int) bean.info.lng);
            tempuser.setViplevel(bean.info.viplevel);
            tempuser.setSVip(bean.info.sVip);
            tempuser.setIcon(bean.info.icon);
            tempuser.setAge(bean.info.age);
        }
        return tempuser;
    }

    static class HttpCallbackImpl implements HttpCallBack{
        private WeakReference<VideoDetailsActivity> mActivity;
        public HttpCallbackImpl(VideoDetailsActivity activity){
            mActivity = new WeakReference<VideoDetailsActivity>(activity);
        }
        @Override
        public void onGeneralSuccess(String result, long flag) {
            VideoDetailsActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralSuccess(result, flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            VideoDetailsActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralError(e, flag);
        }
    }

    // 请求主播详情数据
    private void requestData(boolean isProgress){
        AnchorsCertificationProtocol.getAnchorVideoDetails(this, uid, new HttpCallbackImpl(this));
        if (isProgress) {
            showWaitDialog();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (videoPath != null && videoPlay != null) {
            videoPlay.onPause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mRequestPermission==false){
            mRequestPermission = true;
            requestLiveShowPermission();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (videoPlay != null) { //if (videoPath != null && videoPlay != null) {
            videoPlay.onDestroy();
            videoPlay.setOnInitListener(null);
            videoPlay = null;
        }
        destroyWaitDialog();

        destroyCall();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_VIDEO_CHAT){
            if(resultCode == Activity.RESULT_OK){
                //如果在视频会话界面关注了用户，通过 activity result 通知关注结果
                if(bean!=null && bean.info!=null){
                    bean.info.follow = 1;
                }
                isRefersh = true;
            }
            //requestData(false);
            refershView(bean);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        bean = GsonUtil.getInstance().getServerBean(result,VideoDetailsBean.class);
        if(bean!=null) {
            if (bean.isSuccess()) {
                refershView(bean);
            } else {
                ErrorCode.toastError(mContext, bean.error);
            }
        }else{
            CommonFunction.toastMsg(this,getString(R.string.network_error));
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        ErrorCode.toastError(mContext,e);
    }

    /**
     * 刷新View
     * @param bean
     */
    private void refershView(final VideoDetailsBean bean){
        if (bean == null || bean.info==null){
            return;
        }

        videoChatLy.setOnClickListener(this);
        if (bean.info.uid == Common.getInstance().loginUser.getUid() || bean.info.status == 3){
            videoChatLy.setBackgroundResource(R.drawable.video_details_private_chat_round_normal);
            videoChatLy.setOnClickListener(null);
        }

        idTv.setText(getString(R.string.userinfo_id)+":"+bean.info.uid);
        String name = bean.info.nickName;


        SpannableString spName;
        if (TextUtils.isEmpty(name) || name == null) {
            if (!"".equals(bean.info.notes) && bean.info.notes != null) {
                spName = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, bean.info.notes,
                        0, null);
            } else {
                if (!TextUtils.isEmpty(bean.info.nickName)) {
                    spName = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext, bean.info.nickName,
                            0, null);
                } else {
                    spName = FaceManager.getInstance(BaseApplication.appContext).parseIconForString(BaseApplication.appContext
                            , String.valueOf(bean.info.uid), 0, null);
                }
            }

            nameTv.setText(spName);
        } else if (!TextUtils.isEmpty(name) || name != null) {
            spName = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, name, 0, null);
            nameTv.setText(spName);
        }

        String address = "";
        if (bean.info.landmarkname == null)
            address = getResString(R.string.address_x);
        else
            if (TextUtils.isEmpty(bean.info.landmarkname))
                address = getResString(R.string.address_x);
            else
                address = bean.info.landmarkname;
        addressTv.setText(address);
        if (bean.info.moodText != null && !TextUtils.isEmpty(bean.info.moodText)){
            moodTextTv.setVisibility(View.VISIBLE);
            moodTextTv.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, bean.info.moodText, 0, null));
        }
        chatMintueTv.setText(String.format(getResString(R.string.video_details_video_chat_minute),bean.info.love));

        User user = getCurrentUser();
        iconHead.execute(user);

//        if(videoCover == null)
//             Glide.with(this).asDrawable().load(bean.pic).into(ivCover);

         if(isRefersh && videoPath == null){
             videoPlay.play(bean.info.video);//开始播放视频
             videoPath = bean.info.video;
         }

    }



    /* 初始化呼叫
    * */
    private void initCall(){
        //如果是普通用户需要启动通讯服务
        if(VideoChatManager.getInstance().loginUserIsAnchor()==false) {
            Logger.i("VideoChat", "启动通讯服务");

            //启动IM服务
            STNManager.start(Config.IM_HOST_NAME, Config.IM_HOST_PORT, false, false);

            //处理推送消息监听器
            STNManager.addPushMessageHandler(Iachat.CMD_ID_PUSH_FINISH_VIDEO, this); //会话记录
        }
    }


    /* 销毁呼叫
     * */
    private void destroyCall(){
        if(VideoChatManager.getInstance().loginUserIsAnchor()==false) {
            Logger.i("VideoChat", "停止通讯服务");

            //移除推送消息监听器
            STNManager.removePushMessageHandler(Iachat.CMD_ID_PUSH_FINISH_VIDEO, this); //会话记录

            //停止IM服务
            STNManager.stop();
        }
    }

    /* 处理推送消息
    * */
    @Override
    public boolean handleReceiveMessage(PushMessage pushMessage) {
        Logger.i("VideoChat", "handleReceiveMessage() into, cmdid="+pushMessage.cmdId);
        if(pushMessage.cmdId == Iachat.CMD_ID_PUSH_FINISH_VIDEO){
            //视频会话记录
            try {
                Logger.i("VideoChat", "收到视频会话记录推送消息");
                //登陆视频服务
                final Iavchat.PushVideoFinish msg = Iavchat.PushVideoFinish.parseFrom(pushMessage.buffer);
                if(null!=msg) {
                    Logger.i("VideoChat", "收到视频会话记录推送消息 from=" + msg.from + ", fromName=" + msg.fromName + ", fromIcon="+msg.fromIcon + ", fromVip=" + msg.fromVip + ", fromSvip="+msg.fromSvip + ", to=" + msg.to + ", toName=" + msg.toName + ", toIcon="+msg.toIcon + ", toVip=" + msg.toVip + ", toSvip="+msg.toSvip  +", seconds="+msg.seconds + ", state="+msg.state + ", roomid=" + msg.roomid+", ended =" + msg.ended+", fanchor =" + msg.fromAnchor+", toanchor =" + msg.toAnchor);
                    if(msg.from==Common.getInstance().loginUser.getUid()) {
                        //注意除重 roomid 唯一
                        VideoChatModel.getInstance().insertData(this, (int) msg.to, msg.toName, msg.toName, msg.toIcon, msg.toVip, msg.toSvip, 0, 0, msg.seconds, msg.state, (int) msg.from, msg.ended,msg.toAnchor);
                    }else if(msg.to==Common.getInstance().loginUser.getUid()){
                        VideoChatModel.getInstance().insertData(this, (int) msg.from, msg.fromName, msg.fromName, msg.fromIcon, msg.fromVip, msg.fromSvip, 0, 0, msg.seconds, msg.state, (int) msg.from, msg.ended,msg.fromAnchor);
                    }else{
                        Logger.w("VideoChat", "收到的视频会话记录推送消息和当前用户无关");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Logger.e("VideoChat", "处理视频会话记录推送消息异常");
            }
        }
        return false;
    }


    @Override
    public void doLiveShowPerssiomison() {
        super.doLiveShowPerssiomison();
        requestData(true);
    }
}
