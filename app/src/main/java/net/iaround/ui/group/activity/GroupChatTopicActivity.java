package net.iaround.ui.group.activity;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constant;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.conf.MessageType;
import net.iaround.conf.SocketErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.connector.protocol.LuckPanProtocol;
import net.iaround.connector.protocol.SendWorldMessageProtocol;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.data.remote.SkillRemoteDataSource;
import net.iaround.entity.GroupChatInfo;
import net.iaround.entity.GroupManagerListBean;
import net.iaround.entity.PipelineGift;
import net.iaround.entity.type.ChatFromType;
import net.iaround.floatwindow.AudioChatFloatWindowHelper;
import net.iaround.im.WebSocketManager;
import net.iaround.mic.AudioChatManager;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.chat.WorldMessageGiftContent;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.chatbar.ChatBarBackpackBean;
import net.iaround.model.chatbar.ChatBarSendPacketGiftBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.Item;
import net.iaround.model.im.AudioBaseBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.ChatMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.GroupChatMicUserMessage;
import net.iaround.model.im.GroupChatTopicUserMessage;
import net.iaround.model.im.GroupChatUpdateMicMessage;
import net.iaround.model.im.GroupMessageSendFail;
import net.iaround.model.im.GroupMessageSendSuccess;
import net.iaround.model.im.GroupOnlineUser;
import net.iaround.model.im.GroupSimpleUser;
import net.iaround.model.im.GroupTopicSimpleBean;
import net.iaround.model.im.GroupUser;
import net.iaround.model.im.LotteryNumBean;
import net.iaround.model.im.Me;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.skill.SkillBean;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.model.skill.SkillUpdateEntity;
import net.iaround.model.type.FileUploadType;
import net.iaround.service.NetChangeObserver;
import net.iaround.service.NetworkChangedReceiver;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.DownloadNewVersionTask;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.GeneralSubActivity;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.adapter.GroupChatTopicOnLineAdapter;
import net.iaround.ui.chat.ChatPersionPullDownView;
import net.iaround.ui.chat.ChatPersionPullDownView.OnListViewTopListener;
import net.iaround.ui.chat.ChatPersionPullDownView.OnRefreshAdapterDataListener;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.GroupUserIconDialogForOwner;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.comon.ResizeLayout;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.GroupChatEntryFail;
import net.iaround.ui.datamodel.GroupChatEntrySuccess;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupHistoryMessagesBean;
import net.iaround.ui.datamodel.GroupHistoryModel;
import net.iaround.ui.datamodel.GroupMessageSendFailWithMsgId;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.TopicModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.MessageFragment;
import net.iaround.ui.group.GiftQueueHandler;
import net.iaround.ui.group.GroupOut;
import net.iaround.ui.group.adapter.ChatGroupRecordAdapter;
import net.iaround.ui.group.bean.GiftComponentBean;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.group.bean.GroupNoticeListBean;
import net.iaround.ui.group.util.GroupChatTopicUtil;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.skill.skilldetail.SkillDetailActivity;
import net.iaround.ui.skill.skilluse.SkillUseDialogFragment;
import net.iaround.ui.skill.skilluse.SkillUsePresenter;
import net.iaround.ui.view.DragPointView;
import net.iaround.ui.view.LuxuryGiftView;
import net.iaround.ui.view.dialog.CustomChatBarDialog;
import net.iaround.ui.view.dialog.CustomChatbarMemberDialog;
import net.iaround.ui.view.dialog.CustomGiftDialogNew;
import net.iaround.ui.view.dialog.ReceivedSkillDialog;
import net.iaround.ui.view.dialog.ShareChatbarDialog;
import net.iaround.ui.view.dialog.SkillUpdateDailog;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.ui.view.luckpan.LuckPanDialog;
import net.iaround.ui.view.pipeline.OnHeadViewClickListener;
import net.iaround.ui.view.pipeline.OnHeadViewFromWorldClickListener;
import net.iaround.ui.view.pipeline.PipelineGiftView;
import net.iaround.ui.view.pipeline.PipelineWelcomeView;
import net.iaround.ui.view.pipeline.PipelineWorldMessageView;
import net.iaround.ui.view.spacing.SpacesItemDecoration;
import net.iaround.ui.worldmsg.WorldMessageActivity;
import net.iaround.utils.AssetUtils;
import net.iaround.utils.EditTextViewUtil;
import net.iaround.utils.SkillHandleUtils;
import net.iaround.utils.eventbus.SkillOpenEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GroupTopicActivity
 * @Description: 圈聊/圈话题界面
 * @date 2013-12-16 下午2:49:58
 */
public class GroupChatTopicActivity extends SuperChat implements GroupOut, View.OnLayoutChangeListener, EditTextViewUtil.BackListener, OnHeadViewFromWorldClickListener, LuckPanDialog.OnLuckBingoListener, ShareChatbarDialog.ShareSuccessListener, FlyAudioRoom.UpdateSoundWaveListener {
    private static final String TAG = "GroupChatTopicActivity";

    //礼物ui
    private PipelineGiftView pipelineView1;
    private PipelineGiftView pipelineView2;
    private PipelineGiftView pipelineView3;
    private LuxuryGiftView luxuryGiftView;
    //欢迎语
    private PipelineWelcomeView pipelineWelcomeView;
    private String welcomeText;//以吧主身份发送的欢迎语
    //世界消息
    private PipelineWorldMessageView pipelineWorldMessageView;
    private PipelineWorldMessageView pipelineWorldMessageViewNew;
    //聊吧ID
    private String groupId = null;
    /**
     * 加载聊吧名称
     */
    private String chatbarName;
    private int chatbarHot = 0;

    /**
     * 当前点击的用户
     */
    private User user;
    /**
     * 接收礼物的用户
     */
    private User recieveUser;

    /**
     * 发送私聊socket消息时候本地插入一条消息
     */
    private ChatRecord mChatRecord;

    // Flag
    private long mReqSendMsgFlag;// 发送私聊的请求
    //礼物的类
    private Gift mCurrentGift;
    private Gift mTemporyGift;

    private static int from = 0;// 从哪里进入聊天
    private static int mtype = 1;// 聊天类型（0-正常私聊，1-搭讪聊天）
    private ChatBarSendPacketGiftBean pacageGiftBean;
    private int sendNum;
    private String giftUrl;//礼物图标
    private long lastUserGiftId;//用户上次赠送礼物的user_gift_id
    private List<GiftComponentBean> componentBeenList;

    // 礼物动画
    private GiftQueueHandler giftQueueHandler;
    private Button btn_chat_bar_world_message;

    //用户剩余的抽奖次数,每次拉去聊吧信息时候带有
    private int luckpanFreeCount = -1;
    private int luckpanDiamondCount = -1;
    private int[] luckpanAmount = null; //奖品数量 后台配置

    //从用户信息页面跳到聊吧页面的该用户ID
    private String fromUserId = null;

    private String sharePicUrl = null;//聊吧分享出去的图片地址
    private long getSkillUpdateList;//获取推荐技能升级列表

    private int shareFreeLottery = 1; //每次分享成功后获得的免费抽奖机会

    //聊吧最小化需求引入定义
    private boolean mNeedZoomChatbar = true;  //是否需要最小化聊吧 默认需要
    private int fromchatbarwindowzoom = -1; //是否从最小化悬浮窗进来或悬浮窗的聊吧和其他地方跳转来的聊吧ID相同

    //请求抽奖次数的http接口
    private long requestLottteryNumFlag = -1;

    public boolean isGroupIn = false;// 是否再聊吧内

    //技能弹窗
    private SkillUseDialogFragment skillUseDialogFragment;


    static class HideKeyBoardRunnable implements Runnable {
        private WeakReference<GroupChatTopicActivity> mActivity = null;

        public HideKeyBoardRunnable(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void run() {
            if (mActivity != null && mActivity.get() != null) {
                mActivity.get().hideKeyboard();
                mActivity.clear();
            }
        }
    }

    @Override
    public void back(TextView textView) {
        textView.post(new HideKeyBoardRunnable(this));
    }

    public static final int ViewMode_TOPIC = 100;
    public static final int ViewMode_CHAT = 102;


    /**
     * 跳转到聊吧
     */
    public static void ToGroupChatTopicActivity(final Context current, final Intent intent) {
        /**判断是否在语音聊天中
         * 关闭语音聊天悬浮窗, 退出语音通话*/
        if (AudioChatManager.getsInstance().isHasLogin()) {
//            AudioChatFloatWindowHelper.getInstance().destroy();
            DialogUtil.showTowButtonDialog(current, BaseApplication.appContext.getString(R.string.prompt), current.getString(R.string.close_audio_chat_for_chat_bar),
                    BaseApplication.appContext.getString(R.string.cancel), BaseApplication.appContext.getString(R.string.ok),
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            current.startActivity(intent);
                        }
                    });
        } else {
            current.startActivity(intent);
        }

    }

    /**
     * 跳转到聊吧
     */
    public static void ToGroupChatTopicActivityForResult(final Activity current, final Intent intent, final int resultCode) {
        /**判断是否在语音聊天中
         * 关闭语音聊天悬浮窗, 退出语音通话*/
        if (AudioChatManager.getsInstance().isHasLogin()) {
//            AudioChatFloatWindowHelper.getInstance().destroy();
            DialogUtil.showTowButtonDialog(current, BaseApplication.appContext.getString(R.string.prompt), "进入聊吧，语音通话将会结束，呵呵～，\n是否进入",
                    BaseApplication.appContext.getString(R.string.cancel), BaseApplication.appContext.getString(R.string.ok),
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            current.startActivityForResult(intent, resultCode);
                        }
                    });
        } else {
            current.startActivityForResult(intent, resultCode);
        }

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v == mBtnAttention) {//关注

        } else if (v == mBtnLookMore) {//查看更多成员
            initCustomChatbarMemberDialog();
        } else if (v == mBtnOnChat1) {//上麦
            CommonFunction.log(TAG, "to on mic 1");
            if (currentUser == null) {
                return;
            }
            if (currentUser.getGroup_role() == 0 || currentUser.getGroup_role() == 1) {
                CommonFunction.log(TAG, "to on mic 1, send on mic message");
                SocketGroupProtocol.onMic(mActivity, getCurrentGroupId(), Common.getInstance().loginUser.getUid(), 1);
            } else {
                CommonFunction.toastMsg(mContext, getString(R.string.mic_no_permissions));
            }
        } else if (v == mBtnOnChat2) {//上麦
            CommonFunction.log(TAG, "to on mic 2");
            if (currentUser == null) {
                return;
            }
            if (currentUser.getGroup_role() == 0 || currentUser.getGroup_role() == 1) {
                SocketGroupProtocol.onMic(mActivity, getCurrentGroupId(), Common.getInstance().loginUser.getUid(), 2);
            } else {
                CommonFunction.toastMsg(mContext, getString(R.string.mic_no_permissions));
            }
        } else if (v == rlOnChat1 || v == onChatUserIcon1) { // 下麦
            long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());

            //添加非空判断
            if (currentUser == null) {
                return;
            }
            if (micUser1 != null && user != null) {
                user.setUid(micUser1.getUserid());
                user.setIcon(micUser1.getIcon());
                user.setGroupRole(micUser1.getGroup_role());
                user.setAudio(micUser1.getAudio());
                if (currentUser.getUserid() == micUser1.getUserid()) {//我当前在麦上
                    customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                    customChatBarDialog.setUser(user, user.getGroupRole(), getCurrentGroupId());
                    customChatBarDialog.isClickMySelf(true);
                    customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                    customChatBarDialog.show();
                } else {
                    if (currentUser.getUserid() == creatorUid) { // 是群的创建者
                        // 吧主权限
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 0, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    } else if (currentUser.getGroup_role() == 1) { // 为管理员
                        // 管理员的权限
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 1, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    } else {
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 3, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    }
                }
            }
        } else if (v == rlOnChat2 || v == onChatUserIcon2) { // 下麦
            long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());

            //添加非空判断
            if (currentUser == null) {
                return;
            }
            if (micUser2 != null && user != null) {
                user.setUid(micUser2.getUserid());
                user.setIcon(micUser2.getIcon());
                user.setGroupRole(micUser2.getGroup_role());
                user.setAudio(micUser2.getAudio());
                if (currentUser.getUserid() == micUser2.getUserid()) {//我当前在麦上
                    customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                    customChatBarDialog.setUser(user, user.getGroupRole(), getCurrentGroupId());
                    customChatBarDialog.isClickMySelf(true);
                    customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                    customChatBarDialog.show();
                } else {
                    if (currentUser.getUserid() == creatorUid) { // 是群的创建者
                        // 吧主权限
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 0, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    } else if (currentUser.getGroup_role() == 1) { // 为管理员
                        // 管理员的权限
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 1, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    } else {
                        customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                        customChatBarDialog.setUser(user, 3, getCurrentGroupId());
                        customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                        customChatBarDialog.show();
                    }
                }
            }
        } else if (v == mBtnCloseWheat) { //开麦
            isVoice = false;
            mBtnCloseWheat.setVisibility(View.GONE);
            mBtnOpenWheat.setVisibility(View.VISIBLE);
            if (isUserId(micUser2) == currentUser.getUserid() || isUserId(micUser1) == currentUser.getUserid()) {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMicState(true);
                }
            } else {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(true);
                }
            }
        } else if (v == mBtnOpenWheat) {//闭麦
            isVoice = true;
            mBtnCloseWheat.setVisibility(View.VISIBLE);
            mBtnOpenWheat.setVisibility(View.GONE);

            if (isUserId(micUser2) == currentUser.getUserid() || isUserId(micUser1) == currentUser.getUserid()) {
                //登陆用户是麦上用户则是停止录音
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMicState(false);
                }
            } else {
                //登陆用户非麦上用户则是停止播放
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(false);
                }
            }
        } else if (v == mBtnGroupInfo) { //查看聊吧资料
            if (currentUser != null) {
                if (currentUser.getGroup_role() < 3 && currentUser.getGroup_role() >= 0) {
                    groupInfoBtnClick(true);
                } else if (currentUser.getUserid() == Long.valueOf(GroupModel.getInstance().getGroupOwnerId())) {
                    groupInfoBtnClick(true);
                } else {
                    groupInfoBtnClick(false);
                }
            }
        } else if (v.getId() == R.id.iv_left || v.getId() == R.id.fl_left) {
            //这个是顶部小的返回键箭头，但已经被 flBack 覆盖，所以不会执行
            showZoomWindow();
        } else if (v == flBack) { //点击返回按钮附近的区域时
            showZoomWindow();
        } else if (v == flagImageViewBarrage) {//世界消息弹幕开关
            barrageState = !flagImageViewBarrage.isSelected();
            flagImageViewBarrage.setState(barrageState);
            if (flagImageViewBarrage.isSelected()) {
                editContent.setHint(getResources().getString(R.string.send_world_message_consume));
            } else {
                editContent.setHint(getResources().getString(R.string.chat_update_text_of_hint_in_eidtText));

            }
        } else if (btnHistoryMsg == v) {
            Intent intent = new Intent(this, WorldMessageActivity.class);
            intent.putExtra("cuurentInde", 1);
            startActivity(intent);
        } else if (v == mBtnSend) { //发送消息
            String text = editContent.getText().toString();
            if (TextUtils.isEmpty(text) || CommonFunction.isStringAllSpace(text)) {
                return;
            }
            if (barrageState) { //发送世界消息
                if (!TextUtils.isEmpty(groupId)) {
                    String content = text.trim() + getString(R.string.chat_bar_send_world_message_ahead) + chatbarName + getString(R.string.chat_bar_send_world_message_end);
                    sendWorldMessageFlag = SendWorldMessageProtocol.getInstance().getSendWorldMessageData(GroupChatTopicActivity.this, Integer.parseInt(groupId), content, 30, new ChatBarHttpCallback(this));
                }
            } else { //发送聊天消息
                if (!TextUtils.isEmpty(text.trim())) {
                    sendTextFace();
                }
            }
        } else if (v == btNewMessage) { //私聊消息
            ArrayList<NearContact> beans = new ArrayList<NearContact>();
            ArrayList<NearContact> contacts = ChatPersonalModel.getInstance().getNearContact(getActivity(), Common.getInstance().getUid(), 0, Integer.MAX_VALUE);
            for (NearContact contact : contacts) {
                if (contact.getSubGroup() == SubGroupType.NormalChat) {// 属于私聊类型
                    if (contact.getNumber() > 0) {
                        beans.add(contact);
                    }
                }
            }
            if (beans.size() == 1) {
                ChatPersonal.skipToChatPersonal(GroupChatTopicActivity.this, beans.get(0).getUser(), REQUEST_CODE_CHATPERSONAL);
            } else if (beans.size() > 1) {
                Intent messageFra = new Intent(GroupChatTopicActivity.this, GeneralSubActivity.class);
                messageFra.putExtra(GeneralSubActivity.KEY_CLASS_NAME, MessageFragment.class.getName());
                startActivityForResult(messageFra, REQUEST_CODE_CHATPERSONAL);
            }
        } else if (v == btn_chat_bar_world_message) { //世界消息
            if (mGroupInfo == null) return;
            Intent intent = new Intent(this, WorldMessageActivity.class);
            intent.putExtra("groupId", groupId);
            intent.putExtra("groupName", mGroupInfo.getGroupName() == null ? "" : mGroupInfo.getGroupName());
            intent.putExtra("cuurentInde", 1);
            startActivity(intent);
        } else if (v == btnLuckpan) { //抽奖按钮
            if (luckpanFreeCount != -1 && luckpanDiamondCount != -1) {
                LuckPanDialog.showLuckPanDialog(mContext, luckpanFreeCount, luckpanDiamondCount, luckpanAmount, this);
            }
        } else if (v == btShare) {//聊吧分享按钮
            if (!CommonFunction.isEmptyOrNullStr(sharePicUrl)) {
                ShareChatbarDialog.showDialog(mContext, sharePicUrl, shareFreeLottery, this);
            }
        }
    }

    /**
     * 获取用户id
     *
     * @param user
     * @return
     */
    private long isUserId(GroupUser user) {
        if (user == null) return 0;
        return user.getUserid();
    }

    static class FlyAudioRoomPulishCallback implements FlyAudioRoom.IFlyMediaCallback {
        private WeakReference<GroupChatTopicActivity> mActivity;

        public FlyAudioRoomPulishCallback(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void engineSuccess() {
            CommonFunction.log(TAG, "doMicShowPermissions() engineSuccess");
            FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
            if (null != flyAudioRoom) {
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, true);
                SocketGroupProtocol.onMicSuccess(BaseApplication.appContext, flyAudioRoom.getRoomId(), flyAudioRoom.getManageId(), flyAudioRoom.getPosition(), flyAudioRoom.getPushflows());
            }
        }

        @Override
        public void engineError() {
            CommonFunction.log(TAG, "doMicShowPermissions() engineError");
            GroupChatTopicActivity activity = mActivity.get();
            FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
            if (null != activity && null != flyAudioRoom) {
                if (activity.currentUser != null && activity.currentUser.getUserid() == flyAudioRoom.getManageId()) {
                    CommonFunction.toastMsg(activity, activity.getString(R.string.se_10073035));
                }
            }
            if (null != flyAudioRoom) {
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, false);
                SocketGroupProtocol.onMicError(BaseApplication.appContext, flyAudioRoom.getRoomId(), flyAudioRoom.getManageId(), flyAudioRoom.getPosition());
            }
        }

    }

    @Override
    public void doMicShowPermissions() {
        super.doMicShowPermissions();
        CommonFunction.log(TAG, "doMicShowPermissions() into");
        String url = "" + Common.getInstance().loginUser.getUid();
        if (null != flyAudioRoom) {
            flyAudioRoom.setiFlyMediaCallback(new FlyAudioRoomPulishCallback(this));
            flyAudioRoom.startPublish(url, getCurrentGroupId());
        }
    }

    @Override
    public void delOneRecord(ChatRecord record) {
        if (record.getSendType() == MessageBelongType.SEND) {
            GroupChatListModel.getInstance().removeGroupMessageByLoaclId(mContext, record.getLocid());
        } else {
            GroupChatListModel.getInstance().removeGroupMsgByServerId(mContext, getTargetID(), record.getId());
        }

        delRecordByMsgId(mRecordList, record.getId());
        recordBufferList.remove(record);

        // 更新圈子的最后的消息
        String content = GroupChatListModel.getInstance().getGroupLastContent(mContext, getCurrentGroupId());
        String status = GroupChatListModel.getInstance().getGroupLastMessageStatus(mContext, getCurrentGroupId());
        String time = "";
        if (!TextUtils.isEmpty(content))// 如果获取到内容,就更新到最近圈聊消息那里去
        {
            JSONObject json;
            try {
                json = new JSONObject(content);
                time = json.optString("datetime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            GroupModel.getInstance().updateGroupContact(mContext, content, getCurrentGroupId(), time, status);
        } else {
            // 否则删除最近圈聊消息
            String uidStr = String.valueOf(Common.getInstance().loginUser.getUid());
            GroupModel.getInstance().removeGroupAndAllMessage(mContext, uidStr, getCurrentGroupId());
        }

        mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
    }

    /**
     * 初始化在线用户对话框
     */
    private void initCustomChatbarMemberDialog() {
        customCharbarMemberDialog = new CustomChatbarMemberDialog(GroupChatTopicActivity.this, new ChatbarSendPersonalSocketListenerImpl(this));
        customCharbarMemberDialog.setGroupid(getCurrentGroupId());
        customCharbarMemberDialog.show();
    }

    /**
     * 公聊区域用户头像点击响应
     */
    public class UserIconClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            CommonFunction.log(TAG, "UserIconClickListener onClick() into");
            hideKeyboard();
            hideMenu();
            //当前被点击的用户
            user = new User();

            final ChatRecord record = (ChatRecord) v.getTag();
            final long clickUserId = record.getSendType() == MessageBelongType.SEND ? record.getUid() : record.getFuid();

            //被点击者的id
            user.setUid(clickUserId);

            if (clickUserId == UserId) {//点击的是当前登陆者的id
                customChatBarDialog = new CustomChatBarDialog(GroupChatTopicActivity.this, new ChatbarSendPersonalSocketListenerImpl(GroupChatTopicActivity.this));
                customChatBarDialog.setUser(Common.getInstance().loginUser, user.getGroupRole(), getCurrentGroupId());
                customChatBarDialog.setMicNumber(getMicNumber());
                customChatBarDialog.isClickMySelf(true);
                customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                customChatBarDialog.show();
                return;
            }
            //从用户信息页面跳到聊吧，再在聊吧显示用户信息时候，如果用户相同则关闭当前聊吧页面
//            if(null!=fromUserId && fromUserId.equals(String.valueOf(clickUserId))) {
//                Activity activity = CloseAllActivity.getInstance().getSecondActivity();
//                if (activity != null && activity instanceof OtherInfoActivity) {
//                    if (v.getContext() instanceof Activity) {
//                        mNeedZoomChatbar = false;
//                        ((Activity) v.getContext()).finish();
//                        return;
//                    }
//                }
//            }
            //创建者的id
            long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());
            // 成员角色：0：创建者，1：管理员，2：普通成员,3：游客，4：粉丝
            /**
             * 点击者的角色
             */
            int clickRole = 2; // 默认为普通成员
            if (clickUserId == creatorUid) { // 为创建者
                clickRole = 0;
            } else if (GroupModel.getInstance().isManager(String.valueOf(clickUserId))) { // 为管理员
                clickRole = 1;
            } else {
                clickRole = 3;
            }

            user.setUid(clickUserId);
            user.setNoteName(record.getfNoteName(true));
            user.setNickname(record.getNickname());
            user.setIcon(record.getfIconUrl());
            user.setViplevel(record.getfVipLevel());
            user.setGroupRole(clickRole);
            user.setAge(record.getfAge());
            user.setSex(record.getfSex());
            user.setLat(record.getfLat());
            user.setLng(record.getfLng());
            user.setRelationship(record.getRelationship());

            if (currentUser != null) {

                refreshGroupUserToUser(currentUser);
                /**
                 * 判断当前点击者的身份，系统用户，创建者，管理员，圈成员，粉丝，游客
                 */
                if (clickUserId <= Config.SYSTEM_UID) {// 是系统用户（圈超级管理员）
                    Intent intent = new Intent(mContext, OtherInfoActivity.class);
                    intent.putExtra("uid", user.getUid());
                    intent.putExtra("user", user);
                    intent.putExtra("from", ChatFromType.UNKONW);
                    intent.putExtra("fromchatbar", true); //来自聊吧呼起用户信息页面
                    startActivity(intent);
                } else if (UserId == creatorUid) { // 是群的创建者
                    // 吧主权限

                    customChatBarDialog = new CustomChatBarDialog(GroupChatTopicActivity.this, new ChatbarSendPersonalSocketListenerImpl(GroupChatTopicActivity.this));
                    customChatBarDialog.setUser(user, 0, getCurrentGroupId());
                    customChatBarDialog.setRecieveGiftUser(user);
                    customChatBarDialog.setMicNumber(getMicNumber());
                    customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                    customChatBarDialog.show();
                } else if (currentUser.getGroup_role() == 1) { // 为管理员
                    // 管理员的权限
                    customChatBarDialog = new CustomChatBarDialog(GroupChatTopicActivity.this, new ChatbarSendPersonalSocketListenerImpl(GroupChatTopicActivity.this));
                    customChatBarDialog.setUser(user, 1, getCurrentGroupId());
                    customChatBarDialog.setMicNumber(getMicNumber());
                    customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                    customChatBarDialog.show();
                } else {
                    /***
                     * 点击角色是圈成员，无论被点击的是圈主 圈管理员，圈成员都
                     * 打开他人资料
                     */
                    customChatBarDialog = new CustomChatBarDialog(GroupChatTopicActivity.this, new ChatbarSendPersonalSocketListenerImpl(GroupChatTopicActivity.this));
                    customChatBarDialog.setUser(user, 3, getCurrentGroupId());
                    customChatBarDialog.setMicNumber(getMicNumber());
                    customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                    customChatBarDialog.show();
                }

            }
            CommonFunction.log(TAG, "UserIconClickListener onClick() out");
        }
    }

    private int getMicNumber() {
        int micNumber = 0;
        if (micUser1 != null) {
            micNumber = 2;
        } else {
            micNumber = 1;
        }
        return micNumber;
    }

    /**
     * 手势滑动监听
     */
    private void setGestureListener() {
    }

    /**
     * 升为管理员 或 免去管理员
     *
     * @param role
     * @param userId
     * @param nickname
     */
    private void becomeCancleManager(int role, long userId, String nickname) {
        if (1 == role) { // 为管理员，则免去管理员
            displayCancleManagerDialog(userId, nickname);
        } else { // 为普通成员，则升为管理员
            displayBecomeManagerDialog(userId, nickname);
        }
    }

    /**
     * 显示升为管理员的对话框
     */
    private void displayBecomeManagerDialog(final long userId, final String nickname) {
        DialogUtil.showTwoButtonDialog(mContext, getString(R.string.dialog_title),
                getString(R.string.set_manager_info), getString(R.string.cancel),
                getString(R.string.ok), null, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog(true);
                        long flag = GroupHttpProtocol.groupManagerAdd(mContext, getCurrentGroupId(), user.getUid() + "", new ChatBarHttpCallback(GroupChatTopicActivity.this));
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("user_id", userId);
                        map.put("nickname", nickname);
                        if (flag < 0) {
                            showWaitDialog(false);
                        } else {
                            becomeManagerReqMap.put(String.valueOf(flag), map);
                        }
                    }
                });
    }

    /**
     * 显示免去管理员的对话框
     */
    private void displayCancleManagerDialog(final long userId, final String nickname) {

        DialogUtil.showTwoButtonDialog(mContext, getString(R.string.dialog_title), String
                        .format(mActivity.getString(R.string.group_user_list_cancle_manager_msg),
                                user.getNickname()), getString(R.string.cancel),
                getString(R.string.ok), null, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        showWaitDialog(true);
                        long flag = GroupHttpProtocol.groupManagerCancel(mContext, getCurrentGroupId(), user.getUid() + "", new ChatBarHttpCallback(GroupChatTopicActivity.this));
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("user_id", userId);
                        map.put("nickname", nickname);
                        if (-1 == flag) {
                            showWaitDialog(false);
                        } else {
                            cancleManagerReqMap.put(String.valueOf(flag), map);
                        }
                    }
                });
    }

    /**
     * @param isShow
     * @Title: showWaitDialog
     * @Description: 显示加载框
     */
    private void showWaitDialog(boolean isShow) {
        if (mWaitDialog != null) {
            if (isShow) {
                mWaitDialog.show();
            } else {
                mWaitDialog.dismiss();
            }
        }
    }

    /**
     * 头像长按点击事件
     */
    public class UserIconLongClickListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View v) {

            ChatRecord record = (ChatRecord) v.getTag();
            if (record == null)
                return false;
//            handleAtOperation(record);
            GroupChatTopicUtil.getInstance().handleAtOperation(record);
            return true;
        }
    }

    protected void handleAtOperation(ChatRecord record) {
        String noteName = "";
        long userid;
        if (record.getSendType() == MessageBelongType.SEND) {
            //			noteName = "@" + record.getNoteName( true );
            noteName = "@" + record.getNickname();
            userid = record.getUid();
        } else {
            //			noteName = "@" + record.getfNoteName( true );
            noteName = "@" + record.getfNickName();
            userid = record.getFuid();
        }

        final EditText etInput = getEditContent();
        AtSpan spanStr = new AtSpan(noteName, userid);
        int end = spanStr.length();
        spanStr.setSpan(spanStr, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable text = etInput.getText();

        /**
         * 解决重复@某个人的情况
         */
        if (!text.toString().contains(spanStr)) {
            etInput.append(spanStr);
        }
        etInput.append("  ");
        etInput.requestFocus();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonFunction.showInputMethodForQuery(mContext, etInput);
//                setKeyboardStateShow();
                GroupChatTopicUtil.getInstance().setKeyboardStateShow();
            }
        }, 100);
    }

    public String getCurrentGroupId() {
        return groupId;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    // 发消息回调
    @Override
    public void onSendCallBack(int e, long flag) {
        super.onSendCallBack(e, flag);
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putLong("flag", flag);
        b.putInt("e", e);
        msg.what = HandleMsgCode.MSG_SEND_MESSAGE;
        msg.setData(b);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
        showWaitDialog(false);
        if (flag == requestSkillOpenFlag) {
            if (!Constant.isSuccess(result)) {
                return;
            }
            SkillOpenBean skillOpenBean = GsonUtil.getInstance().getServerBean(result, SkillOpenBean.class);
            if (null != skillOpenBean) {
                EventBus.getDefault().post(new SkillOpenEvent(skillOpenBean));
            }
        } else if (flag == requestGroupManagerIdFlag) {
            // 群管理员ID列表
            GroupManagerListBean bean = GsonUtil.getInstance().getServerBean(result, GroupManagerListBean.class);

            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST_SUCC, bean);
            mHandler.sendMessage(msg);
        } else if (becomeManagerReqMap != null && becomeManagerReqMap.containsKey(String.valueOf(flag))) {
            // 升为管理员
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);

            if (bean.isSuccess()) {
                Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_BECOME_MANAGER_SUCC, becomeManagerReqMap.get(String.valueOf(flag)));
                mHandler.sendMessage(msg);
            } else if (bean.error == 6013)// 该圈管理员已经达到上限
            {
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_MANAGER_LIMIT);
            } else if (bean.error == 6012)// 该圈成员已经是圈管理员
            {
                Message msg = new Message();
                msg.what = GroupUserIconDialogForOwner.MSG_BECOME_MANAGER_FAIL;
                msg.obj = result;
                mHandler.sendMessage(msg);
            } else {
                // 失败
                Message msg = new Message();
                msg.what = GroupUserIconDialogForOwner.MSG_BECOME_MANAGER_FAIL;
                msg.obj = result;
                mHandler.sendMessage(msg);

            }

            becomeManagerReqMap.remove(String.valueOf(flag));
        } else if (cancleManagerReqMap != null && cancleManagerReqMap.containsKey(String.valueOf(flag))) {
            // 免去管理员
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);

            if (bean.isSuccess()) {
                Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_CANCLE_MANAGER_SUCC,
                        cancleManagerReqMap.get(String.valueOf(flag)));
                mHandler.sendMessage(msg);
            } else {
                Message msg = new Message();
                msg.what = GroupUserIconDialogForOwner.MSG_CANCLE_MANAGER_FAIL;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
            cancleManagerReqMap.remove(String.valueOf(flag));
        } else if (flag == requestGroupSimpleFlag) {
            // 群资料详情
            GroupTopicSimpleBean bean = GsonUtil.getInstance().getServerBean(result, GroupTopicSimpleBean.class);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_REQ_GROUP_SIMPLE_SUCC, bean);
            mHandler.sendMessage(msg);
        } else if (flag == getOnLineMemberFlag) {
            // 群在线成员ID列表
            GroupOnlineUser bean = GsonUtil.getInstance().getServerBean(result, GroupOnlineUser.class);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_REQ_GROUP_ONLINE_SUCC, bean);
            mHandler.sendMessage(msg);

        } else if (flag == sendWorldMessageFlag) {
            editContent.getText().clear();
            if (result != null) {
                try {
                    // TODO: 2017/8/30 钻石不足处理
                    JSONObject jsonObject = new JSONObject(result);
                    if (result.contains("error")) {
                        if (jsonObject.getInt("error") == 5930) {
                            String note = getResString(R.string.create_chat_bar_have_no_diamond);
                            DialogUtil.showOKCancelDialog(this, getResString(R.string.prompt), note,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(GroupChatTopicActivity.this, MyWalletActivity.class);
                                            mContext.startActivity(intent);
                                        }
                                    });
                        } else {
                            ErrorCode.showError(GroupChatTopicActivity.this, result);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (flag == getSkillUpdateList) {
            // 得到推荐技能升级列表数据
            final SkillUpdateEntity skillUpdateEntity = GsonUtil.getInstance().getServerBean(result, SkillUpdateEntity.class);
            if (null != skillUpdateEntity && skillUpdateEntity.isSuccess()) {
                // 技能升级上一次弹框时间
                SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putLong(Common.getInstance().getUid() + SharedPreferenceUtil.GROUO_CHAT_BAR_SKILL_UPDATE, System.currentTimeMillis() / 1000);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        final Activity activity = CloseAllActivity.getInstance().getTopActivity();
                        if (activity instanceof GroupChatTopicActivity) {
                            DialogUtil.showSkillUpdateDialog(activity, skillUpdateEntity, new SkillUpdateDailog.SkillItemOnClick() {
                                @Override
                                public void onItemClick(SkillBean skillBean) {
                                    switch (skillBean.Status) {
                                        case 0:

                                            break;
                                        case 1://升级
                                            Intent intent = new Intent(activity, SkillDetailActivity.class);
                                            intent.putExtra("skillId", String.valueOf(skillBean.ID));
                                            startActivity(intent);
                                            break;
                                        case 2:
                                            requestSkillOpenFlag = new SkillRemoteDataSource().openSkill(String.valueOf(skillBean.ID), new ChatBarHttpCallback(null));//GroupChatTopicActivity.this
                                            break;
                                    }
                                }
                            });
                        }
//                        SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putLong(Common.getInstance().getUid() + SharedPreferenceUtil.GROUO_CHAT_BAR_SKILL_UPDATE, System.currentTimeMillis() / 1000);

                    }
                }, 1000 * 60);

            }

        } else if (flag == requestLottteryNumFlag) {
            CommonFunction.log(TAG, "get lottery num response:" + result);
            LotteryNumBean bean = GsonUtil.getInstance().getServerBean(result, LotteryNumBean.class);
            if (bean != null && bean.isSuccess()) {
                Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_LOTTERY_NUM_UPDATE, bean);
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        if (flag == requestGroupManagerIdFlag) { // 群管理员ID列表
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST_FAIL);
        } else if (becomeManagerReqMap != null &&
                becomeManagerReqMap.containsKey(String.valueOf(flag))) { // 升为管理员
            becomeManagerReqMap.remove(String.valueOf(flag));
            mHandler.sendEmptyMessage(GroupUserIconDialogForOwner.MSG_BECOME_MANAGER_FAIL);
        } else if (cancleManagerReqMap != null &&
                cancleManagerReqMap.containsKey(String.valueOf(flag))) { // 免去管理员
            cancleManagerReqMap.remove(String.valueOf(flag));
            mHandler.sendEmptyMessage(GroupUserIconDialogForOwner.MSG_CANCLE_MANAGER_FAIL);
        } else if (flag == getUserInfoFlag) {
            mHandler.sendEmptyMessage(GET_USER_ICON_FLAG);
        } else if (flag == getAttentionFlag) {
            mHandler.sendEmptyMessage(GET_ATTENTION_STATUS);
        } else if (flag == requestGroupSimpleFlag) {// 聊吧拉取资料接口
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_REQ_GROUP_SIMPLE_FAIL);
        } else if (flag == requestGroupSimpleFlag) {// 聊吧在线列表接口
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_REQ_GROUP_ONLINE_FAIL);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //backBtnClick();
            if (false == hideChatInputBar()) {
                showZoomWindow();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) { // 按下菜单键
            if (mCurViewMode == ViewMode_CHAT) { // 在聊天视图时，才响应菜单键
                clickMenu(0);
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 接收消息
     */
    @Override
    public void onReceiveMessage(TransportMessage message) {
        super.onReceiveMessage(message);
        if (onLineNumberAdapter != null) {
            onLineNumberAdapter.setMicNumber(getMicNumber());
        }

        switch (message.getMethodId()) {
            case MessageID.GROUP_COME_IN_Y:// 进入群成功
                receiveJoinInSuccess(message);
                break;
            case MessageID.GROUP_COME_IN_N: // 进入群失败
                receiveJoinInFail(message);
                break;
            case MessageID.GROUP_SEND_MESSAGE_Y: // 成功发送群消息
                receiveSendMsgSucc(message);
                break;
            case MessageID.GROUP_SEND_MESSAGE_N: // 发送群消息失败
                receiveSendMsgFail(message);
                break;
            case MessageID.GROUP_PUSH_MESSAGE: // 接收最新的群消息
                receiveGroupNewMsg(message);
                break;
            case MessageID.GROUP_PUSH_KICK: // 接收被踢消息(本人被群主踢)
                receiveKickedMsg(message);
                break;
            case MessageID.GROUP_PUSH_DISSOLVE: // 解散群组
                receiveDismissGroup(message);
                break;
            case MessageID.GROUP_KICK_Y: // 群主踢人成功
                receiveKickUserSucc(message);
                break;
            case MessageID.GROUP_KICK_N: // 群主踢人失败
                receiveKickUserFail(message);
                break;
            case MessageID.SESSION_GROUP_AUDIO_BEGIN_FAIL:
                GroupChatTopicUtil.getInstance().handleAudioSendEndFail(message.getContentBody());
                break;
            case MessageID.SESSION_GROUP_AUDIO_END_SUCCESS:
                GroupChatTopicUtil.getInstance().handleAudioSendEndSuccess(message.getContentBody());
                break;
            case MessageID.SESSION_GROUP_AUDIO_END_FAIL:
                handleAudioSendEndFail(message.getContentBody());
                break;
            case MessageID.GROUP_GET_HISTORY_MESSAGES_SUCCESS:
                //防止两个线程对数据访问
                final String content = message.getContentBody();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        handleGetHistoryRecords(content);
                    }
                });
                break;
            case MessageID.GROUP_GET_HISTORY_MESSAGES_FAIL:
                break;
            case MessageID.SESSION_GROUP_ON_MIC_SEND_SUCCESS:
                receiveOnMicMsgSucc(message);
                break;
            case MessageID.SESSION_GROUP_ON_MIC_SEND_FAIL:
                receiveOnMicFail(message);
                break;
            case MessageID.SESSION_GROUP_OFF_MIC_SEND_SUCCESS:
                receiveOffMicMsgSucc(message);
                break;
            case MessageID.SESSION_GROUP_OFF_MIC_SEND_FAIL:
                receiveOffMicFail(message);
                break;
            case MessageID.SESSION_GROUP_ON_LINE_RESULT:
                receiveOnLineMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_OFF_LINERESULT:
                receiveOffLineMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_ON_MIC_RESULT:
                receiveOnMicMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_OFF_MIC_RESULT:
                receiveOffMicMsgResult(message);
                break;
            case MessageID.SESSION_PUSH_GROUP_NOTICE_MESSAGE:
                receiveManagerMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_PREPAREONMI_RESULT:
                receivePrepareOnMicMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_ON_MIC_ERROR_RESULT:
                // 告知管理员用户上麦失败
                CommonFunction.toastMsg(this, getString(R.string.group_chat_on_mic_error));
                break;
            case MessageID.SESSION_GROUP_PREPARE_OFFMIC_RESULT:
                receivePrepareOffMicMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_ON_MIC_FEEDBACK_SUCCESS_RESULT:
                receiveOffMicFail(message);
                break;
            case MessageID.SESSION_GROUP_ON_MIC_FEEDBACK_ERROR_RESULT:
                receiveOffMicFail(message);
                break;
            case MessageID.SESSION_GROUP_SEND_PACAGE_GIFT_SUCCESS:
                //  TODO:发送背包礼物
                CommonFunction.log(TAG, "收到socket消息,发送背包礼物成功:" + message.getContentBody());
                handleSendPacketGiftFromSocket(message.getContentBody());
                break;
            case MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC:
                //  TODO:发送私聊socket消息成功
                CommonFunction.log(TAG, "收到socket消息,发送私聊成功:" + message.getContentBody());
                handleSendPacketSocketMessage(message.getContentBody(), message.getMethodId());
                break;
            case MessageID.SESSION_GROUP_UPDATE_MIC_RESULT:
                receiveUpdateMicMsgResult(message);
                break;
            case MessageID.SESSION_GROUP_STOP_PUSH_RESULT:
                receiveStopPushResult(message);
                break;
            // TODO: 2017/8/24 接收招募消息并展示
            case MessageID.SESSION_GROUP_WORLD_MESSAGE:
                handleSendWorldMessageFromSocket(message.getContentBody());
                break;

        }
    }

    /* 处理socket消息
     */
    private void handleSendPacketSocketMessage(String contentBody, int messageId) {

    }

    /* 处理socket消息
     */
    private void handleSendPacketGiftFromSocket(String result) {
        CommonFunction.log(TAG, "handleSendPacketGiftFromSocket() into");
        ChatBarSendPacketGiftBean bean = GsonUtil.getInstance().getServerBean(result, ChatBarSendPacketGiftBean.class);

        if (bean != null && mGroupInfo != null) {
            if (bean.getGroup_id() == Long.parseLong(mGroupInfo.getGroupId())) {
                Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_PACAGE_GIFT_SUCCESS_RESULT, bean);
                mHandler.sendMessage(msg);
            }
        }
    }

    /* 处理socket消息
     */
    private void handleSendWorldMessageFromSocket(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }

        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(result);
        com.alibaba.fastjson.JSONObject message = JSON.parseObject(jsonObject.getString("message"));
        String json = JSON.toJSONString(message);
        Item item = RankingTitleUtil.getInstance().getTitleItemFromChatBarWorkd(json);


        WorldMessageRecord record = SkillHandleUtils.parseMsg(result);
        if (null == record) {
            return;
        }
        if (item != null) {
            record.item = item;
        }
        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_WORLD_MESSAGE_SUCCESS_RESULT, record);
        mHandler.sendMessage(msg);

        SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putString(SharedPreferenceUtil.CHAT_BAR_SAVE_WORLD_MESSAGE, result);
    }

    /* 处理socket消息
     */
    private void handleGetHistoryRecords(String json) {
        CommonFunction.log(TAG, "called handleGetHistoryRecords");
        // 服务端数据不足一页,证明服务端的已经没有数据了
        //gh
        GroupHistoryMessagesBean bean = JSON.parseObject(json, GroupHistoryMessagesBean.class);
        ArrayList<GroupChatMessage> list = bean.getMessageList();
        if (list != null && list.size() < LOAD_MORE_NUM) {
            mHistoryRecorder.setServerNoData(true);
        }

        if (list.size() > 0) {
            // 记录当前请求的数据的最大messageid和increaseid
            // 主要是为了,本地没有数据的时候,请求最新的历史消息用的
            long increaseId = list.get(list.size() - 1).incmsgid + 1;
            long messageId = list.get(list.size() - 1).msgid + 1;
            mHistoryRecorder.setMaxRecordIncreaseId(increaseId);
            mHistoryRecorder.setMaxRecordMessageId(messageId);
            mHistoryRecorder.getServerRequestSet().add(messageId);
        }

        // 需要去数据库拿数据
        getHistoryRecords();
    }

    /* UI 线程里处理消息
     * */
    @Override
    public void handleSelfMessage(Message msg) {
        switch (msg.what) {
            case HandleMsgCode.MSG_SEND_MESSAGE: // 当发送消息，返回的发送情况
                handleSendMsgState(msg);
                break;
            case HandleMsgCode.MSG_SEND_ROOM_MSG_SUCC: // 成功发送聊天消息
                handleSendMsgSucc(msg);
                break;
            case HandleMsgCode.MSG_SEND_ROOM_MSG_FAIL: // 失败发送聊天消息
                handleSendMsgFail(msg);
                break;
            case HandleMsgCode.MSG_RECEIVE_ROOM_MESSAGE: // 接收最新的聊天室消息
                handleGroupNewMsg(msg);
                break;
            case HandleMsgCode.MSG_RECEIVE_KICKED_USER: // 接收被踢信息(本人被群主踢)
                handleKickedFromGroup(msg);
                break;
            case HandleMsgCode.MSG_KICK_USER_SUCC: // 群主踢人成功
                handleKickUserSucc(msg);
                break;
            case HandleMsgCode.MSG_KICK_USER_FAIL: // 群主踢人失败
                GroupChatTopicUtil.getInstance().handleKickUserFail(msg);
                break;
            case HandleMsgCode.MSG_INIT_DATA: // 初始化数据
                initData();
                break;
            case HandleMsgCode.MSG_DISMISS_GROUP: // 群组解散消息
                handleGroupDismiss(msg);
                break;
            case HandleMsgCode.MSG_JOIN_IN_FAIL: // 进入群失败
                handleJoinFail(msg);
                break;
            case HandleMsgCode.MSG_JOIN_IN_SUCCESS: // 进入群成功
                handleJoinSuccess(msg);
                break;
            case HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST: // 请求群管理员ID列表
                reqGroupManagerIdList();
                break;
            case HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST_SUCC: // 请求群管理员ID列表成功
                handleGroupManagerIdListSucc(msg);
                break;
            case HandleMsgCode.MSG_REQ_GROUP_SIMPLE_SUCC: // 请求聊吧拉取资料接口成功
                handleGroupSimpleSucc(msg);
                break;
            case HandleMsgCode.MSG_REQ_GROUP_ONLINE_SUCC: // 请求聊吧在线接口成功
                handleGroupOnLineListSuccess(msg);
                break;
            case HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST_FAIL: // 请求群管理员ID列表失败
                handleGroupManagerIdListFail(msg);
                break;
            case HandleMsgCode.MSG_REQ_GROUP_SIMPLE_FAIL: // 请求聊吧拉取资料接口失败
                handleGroupSimpleListFail(msg);
                break;
            case HandleMsgCode.MSG_BECOME_MANAGER_SUCC: // 升为管理员成功
                handleBecomeManagerSucc(msg);
                GroupChatTopicUtil.getInstance().handleBecomeManagerSucc(msg);
                break;
            case GroupUserIconDialogForOwner.MSG_BECOME_MANAGER_FAIL: // 升为管理员失败
                ErrorCode.showError(mActivity, (String) msg.obj);
                break;
            case HandleMsgCode.MSG_CANCLE_MANAGER_SUCC: // 免去管理员成功
                GroupChatTopicUtil.getInstance().handleCancleManagerSucc(msg);
                break;
            case GroupUserIconDialogForOwner.MSG_CANCLE_MANAGER_FAIL: // 免去管理员失败
                ErrorCode.showError(mActivity, (String) msg.obj);
                break;
            case HandleMsgCode.MSG_MANAGER_LIMIT: // 达到管理员上限
                displayManagerLimitDialog();
                break;
            case HandleMsgCode.MSG_GET_HISTORY: // 获取更多历史数据
                getHistoryRecord(msg);
                break;
            case GET_USER_ICON_FLAG://获取用户信息
                ErrorCode.showError(mActivity, (String) msg.obj);
                break;
            case GET_ATTENTION_STATUS://获取关注状态
                ErrorCode.showError(mActivity, (String) msg.obj);
                break;
            case HandleMsgCode.MSG_SEND_ON_MIC_MSG_SUCC://当前用户上麦成功
                handleOnMicSuccess(msg);
                break;
            case HandleMsgCode.MSG_ON_MIC_FAIL://当前用户上麦失败
                handleMicError(msg);
                break;
            case HandleMsgCode.MSG_SEND_OFF_MIC_MSG_SUCC://当前用户下麦成功
                handleOffMicSuccess(msg);
                break;
            case HandleMsgCode.MSG_OFF_MIC_FAIL://当前用户下麦失败
                handleOffMicError(msg);
                break;
            case HandleMsgCode.MSG_ON_LINE_RESULT:// 用户上线
                handleOnLineResult(msg);
                break;
            case HandleMsgCode.MSG_OFF_LINE_RESULT:// 用户下线
                handleOffLineResult(msg);
                break;
            case HandleMsgCode.MSG_ON_MIC_RESULT:// 有用户上麦（不包含自己）
                handleOnMicResult(msg);
                break;
            case HandleMsgCode.MSG_OFF_MIC_RESULT:// 有用户下麦（可能包含自己）
                handleOffMicResult(msg);
                break;
            case HandleMsgCode.MSG_ADMIN_MESSAGE_RESULT:// 管理员消息
                handleManagerResult(msg);
                break;
            case HandleMsgCode.MSG_PREPARE_ON_MIC_MESSAGE_RESULT:// 告知用户准备上麦
                handlePrepareOnMic(msg);
                break;
            case HandleMsgCode.MSG_PREPARE_OFF_MIC_MESSAGE_RESULT:// 告知用户准备下麦
                handlePrepareOffMic(msg);
                break;
            case HandleMsgCode.MSG_SEND_PACAGE_GIFT_SUCCESS_RESULT:
                handlerGiftMessage(msg);
                pacageGiftBean = (ChatBarSendPacketGiftBean) msg.obj;
                if (pacageGiftBean.getGift().getCombo_type() == 0) {
                    mHandler.postDelayed(runnableHandle, 100);
                }
                break;
            case HandleMsgCode.MSG_WHAT_REFRESH_MINE_SOCKET_SEND:
                // 赠送礼物私聊socket结果
                break;
            case HandleMsgCode.MSG_UPDATE_MIC_MESSAGE_RESULT:// 收到更新麦位消息
                handleUpdateMicResult(msg);
                break;
            case HandleMsgCode.MSG_STOP_PUSH_RESULT:// 指定用户断流
                handleStopPushResult(msg);
                break;
            case HandleMsgCode.MSG_ON_LINE_IS_COMEING:// 用户上线,XXX来了
                handleForIsComeing(msg);
                break;
            case HandleMsgCode.MSG_SEND_WORLD_MESSAGE_SUCCESS_RESULT:
                handleShowWorldMessageNew(msg);
                break;
            case HandleMsgCode.MSG_MIC_USER_SPEAKING_STATE_UPDATE:
                handleMicUserSpeakingStateUpdate(msg);
                break;
            case HandleMsgCode.MSG_LOTTERY_NUM_UPDATE: //请求抽奖次数接口成功
                handleLotteryNumUpdate(msg);
                break;
        }
    }

    /* 更新抽奖次数
     * */
    private void handleLotteryNumUpdate(Message msg) {
        LotteryNumBean numBean = (LotteryNumBean) msg.obj;
        if (null != numBean && numBean.num >= 0) {
            if (numBean.num != luckpanFreeCount) {
                luckpanFreeCount = numBean.num;
                if (luckpanFreeCount > 0) {
                    luckPanCountView.setVisibility(View.VISIBLE);
                    luckPanCountView.setText(String.valueOf(luckpanFreeCount));
                } else {
                    luckPanCountView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /* 更新用户是否在讲话
     * */
    private void handleMicUserSpeakingStateUpdate(Message msg) {
        UserSpeakingState state = (UserSpeakingState) msg.obj;
        if (null != state) {
            if (state.user1 != null) {
                if (micUser1 != null && String.valueOf(micUser1.getUserid()).equals(state.user1)) {
                    if (currentUser != null && currentUser.getUserid() == micUser1.getUserid()) {
                        //当前登陆用户正在推流
                        onChatUserMic1.setVisibility(View.VISIBLE);
                    } else {
                        if (state.speaking1 == true) {
                            onChatUserMic1.setVisibility(View.VISIBLE);
                        } else {
                            onChatUserMic1.setVisibility(View.GONE);
                        }
                    }
                } else if (micUser2 != null && String.valueOf(micUser2.getUserid()).equals(state.user1)) {
                    if (currentUser != null && currentUser.getUserid() == micUser2.getUserid()) {
                        //当前登陆用户正在推流
                        onChatUserMic2.setVisibility(View.VISIBLE);
                    } else {
                        if (state.speaking1 == true) {
                            onChatUserMic2.setVisibility(View.VISIBLE);
                        } else {
                            onChatUserMic2.setVisibility(View.GONE);
                        }
                    }
                }

            }
            if (state.user2 != null) {
                if (micUser1 != null && String.valueOf(micUser1.getUserid()).equals(state.user2)) {
                    if (currentUser != null && currentUser.getUserid() == micUser1.getUserid()) {
                        //当前登陆用户正在推流
                        onChatUserMic1.setVisibility(View.VISIBLE);
                    } else {
                        if (state.speaking2 == true) {
                            onChatUserMic1.setVisibility(View.VISIBLE);
                        } else {
                            onChatUserMic1.setVisibility(View.GONE);
                        }
                    }
                } else if (micUser2 != null && String.valueOf(micUser2.getUserid()).equals(state.user2)) {
                    if (currentUser != null && currentUser.getUserid() == micUser2.getUserid()) {
                        //当前登陆用户正在推流
                        onChatUserMic2.setVisibility(View.VISIBLE);
                    } else {
                        if (state.speaking2 == true) {
                            onChatUserMic2.setVisibility(View.VISIBLE);
                        } else {
                            onChatUserMic2.setVisibility(View.GONE);
                        }
                    }
                }
            }
        }
    }


    /* UI 线程里处理世界消息
     * */
    private void handleShowWorldMessageNew(Message msg) {
        WorldMessageRecord contentBean = (WorldMessageRecord) msg.obj;
        if (contentBean != null) {
            handleWorldConverData(contentBean, contentBean.messageType);
        }
    }

    /* 工具函数
     * */
    private String selectMessageContentType(WorldMessageRecord contentBean, int messageType) {
        if (30 == messageType) {
            if (contentBean != null) {
                if (contentBean.textContent != null) {
                    return contentBean.textContent.content;
                }
            } else {
                return " ";
            }

        } else if (31 == messageType) {//招募
            if (contentBean != null) {
                if (contentBean.recruitContent != null) {
                    return contentBean.recruitContent.text + getResources().getString(R.string.chat_bar_send_world_message_ahead) + contentBean.recruitContent.groupname + getResources().getString(R.string.chat_bar_send_world_message_end);
                }
            } else {
                return " ";
            }

        } else if (32 == messageType) {//礼物
            if (contentBean != null) {
                if (contentBean.giftContent != null) {
                    String str = "";
                    String content = contentBean.giftContent.message;
                    String target = contentBean.giftContent.targetUserName;
                    if (content.contains("@")) {
                        str = content.replace("@", target);
                    } else {
                        str = target;
                    }
                    return str;
                }
            } else {
                return "";
            }

        } else if (33 == messageType) {//技能
            if (contentBean != null) {
                if (contentBean.skillContent != null) {
                    //技能内容
                    String format = AssetUtils.getDesc(1, contentBean.skillContent);
                    return format;
                }

            } else {
                return "";
            }


        }
        return null;
    }

    /* 工具函数
     * */
    private void handleWorldConverData(WorldMessageRecord contentBean, int messageType) {
        User user = new User();
        if (contentBean.user != null) {
            String nickName = contentBean.user.NickName;
            user.setNickname(nickName);
            user.setIcon(contentBean.user.ICON);
            user.setViplevel(contentBean.user.VipLevel);
            user.setSVip(contentBean.user.VIP);
            user.setUid(contentBean.user.UserID);
            user.setAge(contentBean.user.Age);
            int currentRole = -1;
            if (currentUser != null) {
                currentRole = currentUser.getGroup_role();
            }


            if (pipelineWorldMessageView.isShow) {
                pipelineWorldMessageView.translationViewRevert(1000);
                pipelineWorldMessageView.setIsShow(false);
                pipelineWorldMessageViewNew.setWorldMessageInfo(GroupChatTopicActivity.this, user, selectMessageContentType(contentBean, messageType), contentBean.item, contentBean.recruit, currentRole, messageType, contentBean, groupId, mGroupInfo == null ? "" : mGroupInfo.getGroupName(), this);
                pipelineWorldMessageViewNew.translationView(1000);
                pipelineWorldMessageViewNew.setIsShow(true);
            } else {
                if (pipelineWorldMessageViewNew.isShow) {
                    pipelineWorldMessageViewNew.translationViewRevert(1000);
                    pipelineWorldMessageViewNew.setIsShow(false);
                    pipelineWorldMessageView.setWorldMessageInfo(GroupChatTopicActivity.this, user, selectMessageContentType(contentBean, messageType), contentBean.item, contentBean.recruit, currentRole, messageType, contentBean, groupId, mGroupInfo == null ? "" : mGroupInfo.getGroupName(), this);
                    pipelineWorldMessageView.translationView(1000);
                    pipelineWorldMessageView.setIsShow(true);

                } else {
                    //设置资料数据 pipelineWorldMessageView.setWorldMessageInfo();
                    pipelineWorldMessageView.setWorldMessageInfo(GroupChatTopicActivity.this, user, selectMessageContentType(contentBean, messageType), contentBean.item, contentBean.recruit, currentRole, messageType, contentBean, groupId, mGroupInfo == null ? "" : mGroupInfo.getGroupName(), this);
                    pipelineWorldMessageView.translationView(1000);
                    pipelineWorldMessageView.setIsShow(true);

                }
            }

        }
    }


    Runnable runnableHandle = new SendGiftDataRunnable(this);

    static class SendGiftDataRunnable implements Runnable {
        private WeakReference<GroupChatTopicActivity> mActivity;

        public SendGiftDataRunnable(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void run() {
            GroupChatTopicActivity activity = mActivity.get();
            if (activity == null) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                if (activity.isDestroyed()) {
                    return;
                }
            }
            activity.handleSendGiftData();
        }
    }


    Runnable runnable = new SendGiftMessageRunnable(this);

    static class SendGiftMessageRunnable implements Runnable {
        private WeakReference<GroupChatTopicActivity> mActivity;

        private SendGiftMessageRunnable(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void run() {
            GroupChatTopicActivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            if (Build.VERSION.SDK_INT >= 17) {
                if (activity.isDestroyed()) {
                    return;
                }
            }
            activity.sendGiftMessage(activity.pacageGiftBean.getReceive_user().getUserid(), activity.sendNum, false);
        }
    }

    private void handleSendGiftData() {
        CommonFunction.log(TAG, "handleSendGiftData() into");
        if (pacageGiftBean != null) {
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
                mCurrentGift.setIconUrl(pacageGiftBean.getGift().getGift_icon() == null ? "" : pacageGiftBean.getGift().getGift_icon());
                if (pacageGiftBean.getGift().getUser_gift_id() == lastUserGiftId) {
                    // gh 对集合修改时需要做一个临时存贮
                    ArrayList<GiftComponentBean> componentListS = new ArrayList();
                    componentListS.addAll(componentBeenList);
                    for (GiftComponentBean componentBean : componentListS) {
                        if (componentBean.getUsergiftId() == lastUserGiftId) {
                            componentBeenList.remove(componentBean);
                            GiftComponentBean giftComponentBean = new GiftComponentBean();
                            giftComponentBean.setUsergiftId(pacageGiftBean.getGift().getUser_gift_id());
                            giftComponentBean.setGift(mCurrentGift);
                            giftComponentBean.setGiftNum(sendNum);
                            componentBeenList.add(giftComponentBean);
                        }

                    }
                    mHandler.removeCallbacks(runnable);
                    mHandler.postDelayed(runnable, 6000);
                } else {
                    GiftComponentBean giftComponentBean = new GiftComponentBean();
                    giftComponentBean.setUsergiftId(pacageGiftBean.getGift().getUser_gift_id());
                    giftComponentBean.setGift(mCurrentGift);
                    giftComponentBean.setGiftNum(sendNum);
                    componentBeenList.add(giftComponentBean);
                    mHandler.postDelayed(runnable, 6000);
                }
                lastUserGiftId = pacageGiftBean.getGift().getUser_gift_id();
            }
        }
    }

    /**
     * 收到礼物消息（用来处理礼物动画） UI 线程
     *
     * @param msg
     */
    private void handlerGiftMessage(Message msg) {
        ChatBarSendPacketGiftBean pacageGiftBean = (ChatBarSendPacketGiftBean) msg.obj;
        CommonFunction.log(TAG, "handlerGiftMessage() 处理发送背包礼物成功响应，将展现动画 bean = " + pacageGiftBean.toString());
        if (giftQueueHandler == null) {
            return;
        }
        giftQueueHandler.addGift(pacageGiftBean);
    }

    @Override
    public void onToolListener(int type) {

        // 是否被禁言
        if (CommonFunction.forbidSay(mActivity)) {
            return;
        }

        // 是否被圈子禁言
        if (GroupChatListModel.getInstance().checkGroupSay(mActivity, getCurrentGroupId(), this)) {
            return;
        }

        super.onToolListener(type);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
//        //聊吧最小化
//        View view = getWindow().getDecorView();
//        WindowManager.LayoutParams lp =  (WindowManager.LayoutParams)view.getLayoutParams();
//        lp.x = 0;
//        lp.y = 0;
//        lp.width = getWindowManager().getDefaultDisplay().getWidth();
//        lp.height = getWindowManager().getDefaultDisplay().getHeight();
//        int statusBarHeight = CommonFunction.getStatusBarHeight(this);
//        lp.height = lp.height - statusBarHeight;
//
//        getWindowManager().updateViewLayout(view, lp);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonFunction.log(TAG, "onCreate() into");
        super.onCreate(savedInstanceState);
        if (isRestart) {
            return;
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_group_chat_topic);

        CommonFunction.log(TAG, "onCreate() register eventbus...");
        EventBus.getDefault().register(GroupChatTopicActivity.this);
        if (isRestart) {
            return;
        }
        //开启广播去监听 网络 改变事件
        NetworkChangedReceiver.addObserver(mNetChangeObserver);

        CommonFunction.log(TAG, "onCreate() initInputBar...");
        initInputBar();
        CommonFunction.log(TAG, "onCreate() initViews...");
        initViews();
        setListeners();

        // 聊吧拉取资料
        reqGroupSimpleList();

        initDataByView();

        setOnLineNumberAdapter();

        // 聊吧初始化
        CommonFunction.log(TAG, "onCreate() init flyAudioRoom...");
        initFlyAudioRoom();

        setGestureListener();

        instant = this;
        CommonFunction.log(TAG, "onCreate() out, chatbar=" + groupId);

        CommonFunction.log(TAG, "getConnectorManage setServiceCallBack");
        getConnectorManage().setServiceCallBack(this);
    }

    private void initFlyAudioRoom() {
        flyAudioRoom = FlyAudioRoom.getInstance();
        if (fromchatbarwindowzoom != 1) { //最小化再进聊吧不需要再初始化
            if (flyAudioRoom != null) {
                flyAudioRoom.init();
                flyAudioRoom.stopPublish();
            }
        }

        //恢复聊吧时候需要把开关麦的图标状态还原
        if (flyAudioRoom != null) {
            //CommonFunction.log(TAG, "onCreate() isPublishing=" + flyAudioRoom.isPublishing() + ", record="+flyAudioRoom.isEnableRecorder() + ", mute="+flyAudioRoom.isEnableMute());
            if (flyAudioRoom.isDestroy() == true) {
                mBtnCloseWheat.setVisibility(View.GONE);
                mBtnOpenWheat.setVisibility(View.VISIBLE);
            } else {
                if (flyAudioRoom.isPublishing() == true) {
                    if (flyAudioRoom.isEnableRecorder() == true) {
                        mBtnCloseWheat.setVisibility(View.GONE);
                        mBtnOpenWheat.setVisibility(View.VISIBLE);
                    } else {
                        mBtnCloseWheat.setVisibility(View.VISIBLE);
                        mBtnOpenWheat.setVisibility(View.GONE);
                    }
                } else {
                    if (flyAudioRoom.isEnableSpeaker() == true) {
                        mBtnCloseWheat.setVisibility(View.GONE);
                        mBtnOpenWheat.setVisibility(View.VISIBLE);
                    } else {
                        mBtnCloseWheat.setVisibility(View.VISIBLE);
                        mBtnOpenWheat.setVisibility(View.GONE);
                    }
                }
                //麦上用户波形
                flyAudioRoom.setUpdateSoundWaveListener(this);
            }
        }
    }

    /**
     * 获取焦点后开始播放
     */
    @Override
    protected void onResume() {
        CommonFunction.log(TAG, "onResume() into, chatbar=" + groupId);
        super.onResume();
        if (isRestart) {
            return;
        }

        //聊吧最小化
//        if(mChatBarWindowShow == 1){
//            mChatBarWindowShow = 0;
//            if(mChatBarWindowAdd == 0) {
//                CommonFunction.log(TAG, "onResume() add ChatBarWindow");
//                mChatBarWindowAdd = 1;
//                mChatBarWindowContentView.setVisibility(View.VISIBLE);
//                mChatBarWindowLayoutParams.flags = mChatBarWindowLayoutParams.flags & ~WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE & ~WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//                mChatBarWindow.getWindowManager().updateViewLayout(mChatBarWindow.getDecorView(), mChatBarWindowLayoutParams);
//            }
//        }
//        setFinishOnTouchOutside(false);

//        if (!CommonFunction.isActive) {
//            CommonFunction.isActive = true;
//            if (mGroupInfo != null) {
//                if (getCurrentGroupId() != null & !TextUtils.isEmpty(getCurrentGroupId())) {
//                    if (micUser1 != null) {
//                        if (micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
//                            SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());// 通知服务器，已进入群组；开始接收群消息
//                        }
//                    }
//
//                    if (micUser2 != null) {
//                        if (micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {
//                            SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());// 通知服务器，已进入群组；开始接收群消息
//                        }
//                    }
//
//                }
//            }
//        }

        GroupModel.getInstance().setInGroupChat(true);

        refershOnlineData();

        if (isRestart) {
            return;
        }

        HandleOutOfGroupEvent();

        if (mPermissionRequestState == 1) { //正在请求权限
            requestMicshowPermissions();
        }

        CommonFunction.log(TAG, "onResume() out");
    }

    @Override
    protected void onPause() {
        CommonFunction.log(TAG, "onPause() into, chatbar=" + groupId);
        super.onPause();
        if (isRestart) {
            return;
        }

        if (isGroupIn) {
            // 游客不保存历史消息
            final String gid = getCurrentGroupId();
            if (mGroupInfo != null && mGroupInfo.getGroupRole() == 3) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String mUidStr = String.valueOf(Common.getInstance().loginUser.getUid());
                        GroupModel.getInstance().removeGroupAndAllMessage(BaseApplication.appContext, mUidStr, gid);
                        EventBus.getDefault().post("refersh_message");
                    }
                }).start();
            }
            if (null != flyAudioRoom) {
                flyAudioRoom.clearAudio();
            }
            //聊吧跳聊吧时第一个activity得先退出聊吧服务器，第二个activity才得以进入聊吧服务器
            SocketGroupProtocol.groupLeave(mActivity, getCurrentGroupId(), UserId); // 离开群组

            // 保存最后的聊天记录作为草稿箱
            if (editContent != null) {
                String content = editContent.getText().toString().trim();
                GroupChatListModel.getInstance().setChatDraft(this, getCurrentGroupId(), content);
                GroupModel.getInstance().resetNewMsgFlag(getCurrentGroupId());
            }

            Common.getInstance().setSkillResult("");
        }
        CommonFunction.log(TAG, "onPause() out");
    }

    /**
     * 刷新在线人数列表数据
     */
    private void refershOnlineData() {
        if (onLineNumberAdapter != null & mLineNumberDatas != null) {
            onLineNumberAdapter.updateData(mLineNumberDatas);
            onLineNumberAdapter.setMicNumber(getMicNumber());
        }
    }

    /**
     * 挂断（被 receiver 调用）
     */
    public void callStateIdle() {
        if (micUser1 != null) {
            if (micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(true);
                }
                SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());// 通知服务器，已进入群组；开始接收群消息

            } else {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(true);
                }
            }
        }

        if (micUser2 != null) {
            if (micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(true);
                }
                SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());// 通知服务器，已进入群组；开始接收群消息
            } else {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(true);
                }
            }
        }
    }

    /**
     * 接听 （被 receiver 调用）
     */
    public void callStateRinging() {
        CommonFunction.log(TAG, "callStateRinging() into");

        if (micUser1 != null) {
            if (micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
                if (flyAudioRoom != null) {
                    stopPush();
                    flyAudioRoom.handleMuteState1(false);
                    SocketGroupProtocol.backChatRoom(GroupChatTopicActivity.this, getCurrentGroupId());
                }
            } else {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(false);
                }
            }
        }

        if (micUser2 != null) {
            if (micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {
                if (flyAudioRoom != null) {
                    stopPush();
                    flyAudioRoom.handleMuteState1(false);
                    SocketGroupProtocol.backChatRoom(GroupChatTopicActivity.this, getCurrentGroupId());
                }
            } else {
                if (flyAudioRoom != null) {
                    flyAudioRoom.handleMuteState1(false);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        CommonFunction.log(TAG, "onStart() into, chatbar=" + groupId);
        super.onStart();
        if (isRestart) {
            return;
        }
    }

    @Override
    protected void onStop() {
        CommonFunction.log(TAG, "onStop() into, chatbar=" + groupId);
        super.onStop();
        if (isRestart) {
            return;
        }

        //播放音乐文件资源施放
        AudioPlayUtil.getInstance().releaseRes();

        hideMenu();

        if (componentBeenList != null) {
            if (!componentBeenList.isEmpty()) {
                if (pacageGiftBean != null) {
                    sendGiftMessage(pacageGiftBean.getReceive_user().getUserid(), sendNum, false);
                }
            }
        }

        CommonFunction.log(TAG, "onStop() out");
    }

    /* 通知聊吧解散 或 被踢出聊吧
     * */
    @Override
    public void HandleOutOfGroupEvent() {
        try {
            if (Common.groupKickDisbandedMap.containsKey(getCurrentGroupId())) {
                String dialogMsg = "";
                int kickID = Common.groupKickDisbandedMap.get(getCurrentGroupId()).getMethodId();
                if (kickID == MessageID.GROUP_PUSH_KICK) {
                    dialogMsg = mContext.getString(R.string.has_kick_from_group);
                } else if (kickID == MessageID.GROUP_PUSH_DISSOLVE) {
                    dialogMsg = mContext.getString(R.string.group_is_disbanded);
                }
                DialogUtil.showOKDialog(mContext, mContext.getString(R.string.dialog_title), dialogMsg,
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                CommonFunction.log(TAG, "HandleOutOfGroupEvent() 被踢出聊吧");
                                String userid = String.valueOf(UserId);
                                String groupid = String.valueOf(getTargetID());
                                GroupModel.getInstance().removeGroupAndAllMessage(mContext, userid, groupid);
                                mNeedZoomChatbar = false; //不需要最小化聊吧
                                finish();
                            }
                        }).setCancelable(false);
                Common.groupKickDisbandedMap.remove(getCurrentGroupId());
                GroupModel.getInstance().isNeedRefreshGroupList = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        CommonFunction.log(TAG, "onDestroy() into, chatbar=" + groupId);
        if (mNetChangeObserver != null) {
            NetworkChangedReceiver.removeRegisterObserver(mNetChangeObserver);
        }
        super.onDestroy();
        if (isRestart) {
            return;
        }

        try {
            if (mHandler != null) {
                CommonFunction.log(TAG, "onDestroy() remove git send runnable");
                mHandler.removeCallbacks(runnable);
                mHandler.removeCallbacks(runnableHandle);
            }
            TopicModel.getInstance().reset(); // 释放话题列表的缓存

            // 游客不保存历史消息
            if (mGroupInfo != null && mGroupInfo.getGroupRole() == 3) {
                final String gid = getCurrentGroupId();
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        String mUidStr = String.valueOf(Common.getInstance().loginUser.getUid());
                        GroupModel.getInstance().removeGroupAndAllMessage(BaseApplication.appContext, mUidStr, gid);
                        EventBus.getDefault().post("refersh_message");
                    }
                }).start();
            }

            if (mNeedZoomChatbar == false || isGroupIn == true) { //不需要最小化聊吧窗口
                CommonFunction.log(TAG, "need to leave chatbar, isGroupIn=" + isGroupIn + ", mNeedZoomChatbar=" + mNeedZoomChatbar);
                Common.getInstance().setmCurrentGroupId(0);
                GroupModel.getInstance().setInGroupChat(false);
                // 当前用户不在麦上
                SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, false);
                //if (!isGroupIn) {
                //离开群组 (在悬浮窗里控制离开聊吧)
                SocketGroupProtocol.groupLeave(mActivity, getCurrentGroupId(), UserId);
                //}
                GroupModel.getInstance().releaseBuffer();// 释放群组的缓存

                if (flyAudioRoom != null) {
                    flyAudioRoom.clearAudio();
                }
            }

            if (flyAudioRoom != null) {
                //麦上用户波形
                flyAudioRoom.setUpdateSoundWaveListener(null);
            }

            if (giftQueueHandler != null) {
                giftQueueHandler.setMainHandle(null);
                giftQueueHandler.setOnHeadViewClickListener(null);
                giftQueueHandler.release();
                giftQueueHandler = null;
            }

            // 保存最后的聊天记录作为草稿箱
            if (editContent != null) {
                String content = editContent.getText().toString().trim();
                GroupChatListModel.getInstance().setChatDraft(this, getCurrentGroupId(), content);
                GroupModel.getInstance().resetNewMsgFlag(getCurrentGroupId());
                editContent.setBackListener(null);
                editContent = null;
            }

            Common.getInstance().setShowDailog(false);
            Common.getInstance().setSkillResult("");

            GroupChatTopicUtil.getInstance().initData(null);
            GroupChatTopicUtil.getInstance().destroyView(); //防止引用 activity context

            //移除缓存中保存的最后一个礼物位置
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_ARRAY_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_ARRAY_POSTION);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_ARRAY_VALUE);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_ARRAY_VALUE);
            SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_ID);


            EventBus.getDefault().unregister(GroupChatTopicActivity.this);

//            if (setThisCallbackAction) {
//            }
            // activity 销毁的时候应该设置为空 不然  ConnectorManage 单例会一直拿着当前的回调
            CommonFunction.log("GroupChatTopicActivity", "getConnectorManage setCallBackAction null");
            getConnectorManage().setServiceCallBack(null);
            mWaitDialog.dismiss();
            mWaitDialog.setOnCancelListener(null);
            mWaitDialog.setOnDismissListener(null);
            mWaitDialog = null;

            pullDownView.setOnRefreshAdapterDataListener(null);
            pullDownView.setOnListViewTopListener(null);
            pullDownView = null;
            if (skillUseDialogFragment != null && skillUseDialogFragment.isActive()) {
                skillUseDialogFragment.dismiss();
            }
            if (receivedSkillDialog != null && receivedSkillDialog.isShowing()) {
                receivedSkillDialog.dismiss();
            }

        } catch (Exception e) {
            CommonFunction.log(TAG, "onDestroy() error happen");
            e.printStackTrace();
        }

        CommonFunction.log(TAG, "onDestroy() out");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CommonFunction.log(TAG, "onActivityResult() into, requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (isRestart) {
            return;
        }

        afterBack();

        // SSO 授权回调
        if (ShareChatbarDialog.REQUEST_CODE_WEIBO_AUTH == requestCode ||
                ShareChatbarDialog.REQUEST_CODE_QQ_AUTH == requestCode ||
                ShareChatbarDialog.REQUEST_CODE_QQ_SHARE == requestCode ||
                ShareChatbarDialog.REQUEST_CODE_QZONE_SHARE == requestCode) {
            ShareChatbarDialog.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode == Activity.RESULT_OK) { // 返回确认
            if (REQUEST_CODE_GROUPINFO == requestCode) {
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    boolean isClearAllMessages = false;
                    String group = null;
                    if (bundle != null) {
                        isClearAllMessages = bundle.getBoolean(GroupInfoActivity.CLEARALL, false);
                        group = bundle.getString(GroupInfoActivity.GROUPID);
                        if (bundle.containsKey(GroupInfoActivity.GROUP_ROLE)) {
                            int grouprole = bundle.getInt(GroupInfoActivity.GROUP_ROLE);
                            if (mGroupInfo != null) {
                                mGroupInfo.setGroupRole(grouprole);
                            }
                        }
                    }

                    if (isClearAllMessages == true && group != null && group.equals(getCurrentGroupId())) {
                        mRecordList.clear();
                        recordBufferList.clear();
                        unsendRecordList.clear();
                        systemRecordList.clear();
                        mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                    }
                }
            }
        }

        if (REQUEST_CODE_CHATPERSONAL == requestCode) {
            refershPrivateData();
        }

        /***
         * 发送礼物
         */
        if (resultCode == Activity.RESULT_OK) {
            if (HandleMsgCode.MSG_SEND_GIFT_REQUEST == requestCode) {// 发送礼物
                mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
            }
        }

        //任务页面返回
        if (requestCode == ChatRequestCode.CHAT_TASK_REQUEST_CODE) {
            requestLottteryNumFlag = LuckPanProtocol.getUserFreeLotteryInfo(this, LuckPanProtocol.sLuckPanLottreyNumUrl, new ChatBarHttpCallback(this));
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        CommonFunction.log(TAG, "onNewIntent() into, current chatbar=" + groupId);
        if (isRestart) {
            return;
        }
        //聊吧最小化请求
//        mChatBarWindowShow = intent.getIntExtra("showchatbarwindow",-1);
//        String group = intent.getStringExtra("id");
//        if(group!=null && groupId!=null && false==group.equals(groupId)){
//            CommonFunction.log(TAG, "onNewIntent() charbar not the same");
//            hideZoomWindow();
//            CommonFunction.log(TAG, "onNewIntent() start activity for new charbar=" + group);
//            startActivity(intent);
//        } else if(group!=null && groupId!=null && true==group.equals(groupId)){
//            ChatBarZoomWindow.getInstance().hide();
//            if(mChatBarWindowShow == 0){
//
//                mChatBarWindowShow = 1;
//            }
//        }

        //第三方分享页面跳回本页面
        ShareChatbarDialog.onNewIntent(intent);
    }

    @Override
    protected User getUser() {
        return null;
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.KEY_SHARE_MARK, true);
    }

    /**
     * 点击菜单
     */
    protected void clickMenu(int type) {
        // 判断是否陪圈子禁言
        if (GroupChatListModel.getInstance().checkGroupSay(this, String.valueOf(getTargetID()), this)) {
            return;
        }

        super.clickMenu(type);
    }

    /**
     * 屏幕大小发生改变
     *
     * @param msg
     */
    protected void handleScreenResize(Message msg) {
        if (isMenuOpen()) { // 菜单处于显示状态，则隐藏菜单
            hideMenu();
        }
        mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
    }

    public long getTargetID() {
        return groupId == null ? 0 : Long.valueOf(groupId);
    }

    private void initViews() {

        ResizeLayout layout = (ResizeLayout) findViewById(R.id.root_layout);
        layout.setOnResizeListener(this);

        //聊天记录容器
        mViewContainer = (FrameLayout) findViewById(R.id.view_content);

        //聊天展示列表
        chatRecordListView = (ListView) mChatView.findViewById(R.id.chatRecordList);


        if (mIsChat) {
            mChatView.setVisibility(View.VISIBLE);
            mCurViewMode = ViewMode_CHAT;
        } else {
            mChatView.setVisibility(View.GONE);
            mCurViewMode = ViewMode_TOPIC;

        }
        mViewContainer.removeAllViews();
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mViewContainer.addView(mChatView, lp);


        pullDownView = (ChatPersionPullDownView) findViewById(R.id.chatting_pull_down_view);
        pullDownView.setTopViewInitialize(true);
        pullDownView.setIsCloseTopAllowRefersh(false);
        pullDownView.setHasbottomViewWithoutscroll(false);
        pullDownView.setOnRefreshAdapterDataListener(mOnRefreshAdapterDataListener);
        pullDownView.setOnListViewTopListener(mOnListViewTopListener);

        //是拉取历史记录的分割线
        headView = View.inflate(mContext, R.layout.chat_personal_record_head, null);
        View footerView = new View(mContext);
        footerView.setPadding(0, 2, 0, 2);
        chatRecordListView.addHeaderView(headView);
        chatRecordListView.addFooterView(footerView);

        mWaitDialog = DialogUtil.getProgressDialog(mContext, "", getString(R.string.please_wait), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                CommonFunction.log(TAG, "initViews() 取消等待");
                mNeedZoomChatbar = false;
                finish();
            }
        });

        /**圈子信息*/
        mRlGroupInfo = (RelativeLayout) findViewById(R.id.rl_group_info);
        mIvGroupIcon = (ImageView) findViewById(R.id.iv_group_info);
        mTvGroupName = (TextView) findViewById(R.id.tv_chatbar_name);
        mTvGroupMember = (TextView) findViewById(R.id.tv_chatbar_member);
        mBtnGroupInfo = (Button) findViewById(R.id.btn_look_chatbar_info);
        /**关注按钮*/
        mBtnAttention = (TextView) findViewById(R.id.tv_pay_attention);
        /**上麦下麦按钮*/
        mBtnOnChat1 = (Button) findViewById(R.id.btn_chat_on1);
        rlOnChat1 = (RelativeLayout) findViewById(R.id.rl_on_chat1);
        onChatUserIcon1 = (ImageView) findViewById(R.id.on_chat_usericon1);
        onChatUserMic1 = (ImageView) findViewById(R.id.on_chat_user_mic1);
        mBtnOnChat2 = (Button) findViewById(R.id.btn_chat_on2);
        rlOnChat2 = (RelativeLayout) findViewById(R.id.rl_on_chat2);
        onChatUserIcon2 = (ImageView) findViewById(R.id.on_chat_usericon2);
        onChatUserMic2 = (ImageView) findViewById(R.id.on_chat_user_mic2);
        /**闭麦按钮*/
        mBtnCloseWheat = (Button) findViewById(R.id.btn_close_wheat);
        mBtnOpenWheat = (Button) findViewById(R.id.btn_open_wheat);
        /**标题栏标题*/
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        /**标题返回键范围*/
        flBack = (FrameLayout) findViewById(R.id.fl_back);
        /**查看更多成员按钮*/
        mBtnLookMore = (Button) findViewById(R.id.btn_look_more);
        mOnLineNumber = (RecyclerView) findViewById(R.id.recy_group_head_online_number);
        /**聊吧面板顶部背景图*/
        ivChatbarbg = (ImageView) findViewById(R.id.iv_chatbar_bg);

        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight / 3;
        GroupChatTopicUtil.getInstance().initView(GroupChatTopicActivity.this, mHandler, backManager, mHistoryRecorder, editContent, mIsChat);

        // 默认隐藏
        findViewById(R.id.ly_chat_on2).setVisibility(View.GONE);

        // TODO: 2017/8/24 暂时充当历史消息入口
        btnHistoryMsg = (Button) findViewById(R.id.btn_history_msg);
        btnHistoryMsg.setOnClickListener(this);

        //聊吧窗口最小化功能初始化
//        mChatBarWindow = getWindow();
//        mChatBarWindowLayoutParams = mChatBarWindow.getAttributes();
//        mChatBarWindowContentView = ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0);
//
        //关闭历史悬浮窗
        hideZoomWindow();

        /**关闭语音聊天*/
        WebSocketManager.getsInstance().logoutAudioRoom(true);

    }

    /**
     * 在线人数适配器
     */
    private void setOnLineNumberAdapter() {
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mOnLineNumber.setLayoutManager(linearLayoutManager);
        mOnLineNumber.addItemDecoration(new SpacesItemDecoration(5));
        if (mLineNumberDatas == null)
            mLineNumberDatas = new ArrayList<>();
        //设置适配器
        onLineNumberAdapter = new GroupChatTopicOnLineAdapter(this, mLineNumberDatas, getCurrentGroupId(), new ChatbarSendPersonalSocketListenerImpl(this));
        mOnLineNumber.setAdapter(onLineNumberAdapter);
    }

    /**
     * 初始化输入框
     */
    protected void initInputBar() {

        mChatView = (FrameLayout) View.inflate(mContext, R.layout.group_chat_view, null);
        //群聊底部输入栏
        chatInputBarLayout = (LinearLayout) mChatView.findViewById(R.id.chat_input_layout);
        chatAudioLayout = (RelativeLayout) mChatView.findViewById(R.id.chat_audio_layout);
        fontNum = (TextView) mChatView.findViewById(R.id.fontNum);
        unseeNum = (TextView) mChatView.findViewById(R.id.unseeNum);
        unseeNum.setOnClickListener(this);
        llMoreHandle = (LinearLayout) mChatView.findViewById(R.id.llMoreHandle);
        lyQuickSend = (LinearLayout) mChatView.findViewById(R.id.ll_quick_sendmsg_layout);
        btnQuickSend1 = (TextView) mChatView.findViewById(R.id.btn_send_1);
        btnQuickSend2 = (TextView) mChatView.findViewById(R.id.btn_send_2);
        btn_chat_bar_world_message = (Button) mChatView.findViewById(R.id.btn_chat_bar_world_message);
        btn_chat_bar_world_message.setOnClickListener(this);
        btnQuickSend1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                keyboardState = SuperChat.KEYBOARD_SHOW;
                //保证软件盘不隐藏
                sendQuickText("1");
            }
        });
        btnQuickSend2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //保证软键盘不隐藏
                keyboardState = SuperChat.KEYBOARD_SHOW;
                sendQuickText("2");
            }
        });

        //礼物区域
        pipelineView1 = (PipelineGiftView) mChatView.findViewById(R.id.pipelineView1);
        pipelineView2 = (PipelineGiftView) mChatView.findViewById(R.id.pipelineView2);
        pipelineView3 = (PipelineGiftView) mChatView.findViewById(R.id.pipelineView3);
        pipelineWelcomeView = (PipelineWelcomeView) mChatView.findViewById(R.id.pipelineView4);
        pipelineWorldMessageView = (PipelineWorldMessageView) findViewById(R.id.pipe_world_message);
        pipelineWorldMessageViewNew = (PipelineWorldMessageView) findViewById(R.id.pipe_world_message_new);
        luxuryGiftView = (LuxuryGiftView) mChatView.findViewById(R.id.luxuryGiftView);

        if (giftQueueHandler == null) {
            giftQueueHandler = GiftQueueHandler.getInstance();
        }
        giftQueueHandler.initView(pipelineView1, pipelineView2, pipelineView3, luxuryGiftView);
        giftQueueHandler.setMainHandle(mHandler);
        super.initInputBar();

        // 聊吧内图标默认显示
        LyMore.setVisibility(View.VISIBLE);

        dragPointView.setDragListencer(new DragPointView.OnDragListencer() {
            @Override
            public void onDragOut() {
                ArrayList<NearContact> contacts = ChatPersonalModel.getInstance().getNearContact(getActivity(), Common.getInstance().getUid(), 0, Integer.MAX_VALUE);
                for (NearContact contact : contacts) {
                    if (contact.getSubGroup() == SubGroupType.NormalChat) {// 属于私聊类型
                        rlNewMessage.setVisibility(View.GONE);
                        try {
                            ChatPersonalModel.getInstance().readAllPersonalMsg(mContext, Common.getInstance().loginUser.getUid(), contact.getUser().getUid());
                            ChatPersonalModel.getInstance().clearNoneReadCount(mContext, String.valueOf(Common.getInstance().loginUser.getUid()), String.valueOf(contact.getUser().getUid()));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        editContent.setBackListener(this);

        long task = SharedPreferenceUtil.getInstance(this).getLong(Common.getInstance().getUid() + SharedPreferenceUtil.GROUO_CHAT_BAR_TASK_READ_STATE);
        if (TimeFormat.getYearMonthDayDate() - task <= 1) {
            viewTask.setVisibility(View.GONE);
        } else {
            viewTask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void saveRecordToBuffer(ChatRecord record) {
        // 将聊天消息添加到聊天记录缓存和显示的聊天记录列表
        GroupChatListModel.getInstance().addRecord(mRecordList, -1, record);
        GroupChatListModel.getInstance().addRecord(recordBufferList, -1, record);
    }

    public void initTargetInfomation() {
        Intent intent = getIntent();

        if (intent == null) {
            return;
        }
        groupId = intent.getStringExtra("id");
        mIsChat = intent.getBooleanExtra("isChat", true);
        fromUserId = intent.getStringExtra("fromUserId");
        fromchatbarwindowzoom = intent.getIntExtra("fromchatbarwindowzoom", -1);

        Common.getInstance().setmCurrentGroupId(groupId == null ? 0 : Long.valueOf(TextUtils.isEmpty(groupId) ? "0" : groupId));
        GroupModel.getInstance().setGroupId(getCurrentGroupId()); // 记录当前群的ID
        CommonFunction.log(TAG, "initTargetInfomation() chatbar=" + groupId + ", mIsChat=" + mIsChat);
    }

    /**
     * 暂停播放
     */
    public void stopPlay() {
        if (flyAudioRoom != null) {
            String url = getCurrentGroupId();
            flyAudioRoom.stopPlay(url);
        }
    }


    /**
     * 开始上麦
     */
    private void startPush() {
        CommonFunction.log("socket", "startPush() into");
        requestMicshowPermissions();
    }

    /**
     * 开始下麦
     */
    private void stopPush() {
        if (flyAudioRoom != null) {
            flyAudioRoom.stopPublish();
        }
    }

    /**
     * 账号在其他设备上登陆时
     */
    public void errorStop() {
        mNeedZoomChatbar = false;
        if (flyAudioRoom != null) {
            flyAudioRoom.clearAudio();
        }
    }

    /**
     * 初始化监听
     */
    private void setListeners() {
        mBtnLookMore.setOnClickListener(this);
        mIvGroupIcon.setOnClickListener(this);
        mRlGroupInfo.setOnClickListener(this);
        mBtnAttention.setOnClickListener(this);
//        mLlOnChat.setOnClickListener(this);
        mBtnOnChat1.setOnClickListener(this);
        mBtnOnChat2.setOnClickListener(this);
        rlOnChat2.setOnClickListener(this);
        rlOnChat1.setOnClickListener(this);
        onChatUserIcon1.setOnClickListener(this);
        onChatUserIcon2.setOnClickListener(this);

        mBtnCloseWheat.setOnClickListener(this);
        mBtnOpenWheat.setOnClickListener(this);
        mBtnGroupInfo.setOnClickListener(this);
        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        flBack.setOnClickListener(this);

        //添加layout大小发生改变监听器
        mChatView.addOnLayoutChangeListener(this);

    }

    private OnListViewTopListener mOnListViewTopListener = new OnListViewTopListener() {

        @Override
        public boolean getIsListViewToTop() {
            View topListView = chatRecordListView.getChildAt(chatRecordListView.getFirstVisiblePosition());
            return !((topListView == null) || (topListView.getTop() != 0));
        }
    };

    /**
     * 加载更多数据，历史数据
     */
    private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener = new OnRefreshAdapterDataListener() {

        @Override
        public void refreshData() {
            dataHandler.post(new Runnable() {

                @Override
                public void run() {
                    getHistoryRecords();
                }
            });
        }
    };

    /**
     * @Title: initDataByView
     * @Description: 初始化数据显示
     */
    private void initDataByView() {
        if (mCurViewMode == ViewMode_CHAT) {
            if (!isInitChatData) {
                isInitChatData = true;
                mHandler.sendEmptyMessageDelayed(HandleMsgCode.MSG_INIT_DATA, 100);
            }
        }
    }

    /**
     * @Title: initData
     * @Description: 加载数据
     */
    private void initData() {
        componentBeenList = new ArrayList<>();
        becomeManagerReqMap = new HashMap<String, HashMap<String, Object>>();
        cancleManagerReqMap = new HashMap<String, HashMap<String, Object>>();

//        // 通知服务器，已进入群组；开始接收群消息
        SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());

        mHandler.sendEmptyMessageDelayed(HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST, 100); // 请求群管理员ID列表

        GroupChatListModel.getInstance().getMaxIncreaseId(mContext, getTargetID(), mHistoryRecorder);

        updateMessageStatus();//将超时的发送中的消息做失败处理
        getUnSendSuccessMessage();
        getSystemMessage();

        if (adapter == null) {
            reloadRoomRecords();
        }

        mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);

        // 读取草稿箱的内容，看是否未发送的历史记录
        String content = GroupChatListModel.getInstance().getChatDraft(this, getCurrentGroupId());
        if (!CommonFunction.isEmptyOrNullStr(content)) {
            editContent.setText(content);
            editContent.setSelection(content.length());
            FaceManager.getInstance(mActivity).parseIconForEditText(mActivity, editContent);
            CommonFunction.MoveCursorToLast(editContent);
        }

        refershPrivateData();

        final String wordMessage = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_SAVE_WORLD_MESSAGE);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleSendWorldMessageFromSocket(wordMessage);
            }
        }, 2000);

    }

    private HistoryRecorder mHistoryRecorder = new HistoryRecorder();

    /**
     * 记录每一次获取数据的内容,先按自增id排序,当自增id为0的时候,按消息的时间戳排序,确保取出来的数据是够一页的
     */
    public static class HistoryRecorder {

        private boolean mLocalNoData = false;// 当最旧的自增id已经为0了,且取出来的数据已经小于一页的数据时候,就认为没有数据了
        private boolean mServerNoData = false;// 当服务端返回的数据少于一页的数据时候,就认为服务端已经没有数据了

        private long maxRecordMessageId;// 单次获取最新数据的Messageid，获取到数据之后设置为该数据中最小的Messageid
        private long maxRecordIncreaseId;// 单次获取最新数据的自增id，获取到数据之后设置为该数据中最小的自增id
        private long maxRecordSendTime;// 单次获取最新数据的timestamp，获取到数据之后设置为数据中最小的timestamp

        // 记录请求服务端的MessageId,以防止同一段数据连续请求(当服务端下发的数据不连续的时候)
        private HashSet<Long> serverRequestSet = new HashSet<Long>();

        private ArrayList<ChatRecord> currentBuffer = new ArrayList<ChatRecord>();// 当次取出数据的buffer

        public boolean isLocalNoData() {
            return mLocalNoData;
        }

        public void setLocalNoData(boolean mNoData) {
            this.mLocalNoData = mNoData;
        }

        public long getMaxRecordIncreaseId() {
            return maxRecordIncreaseId;
        }

        public void setMaxRecordIncreaseId(long maxRecordIncreaseId) {
            this.maxRecordIncreaseId = maxRecordIncreaseId;
        }

        public long getMaxRecordSendTime() {
            return maxRecordSendTime;
        }

        public void setMaxRecordSendTime(long maxRecordSendTime) {
            this.maxRecordSendTime = maxRecordSendTime;
        }

        public ArrayList<ChatRecord> getCurrentBuffer() {
            return currentBuffer;
        }

        public void setCurrentBuffer(ArrayList<ChatRecord> currentBuffer) {
            this.currentBuffer = currentBuffer;
        }

        public boolean isServerNoData() {
            return mServerNoData;
        }

        public void setServerNoData(boolean mServerNoData) {
            this.mServerNoData = mServerNoData;
        }

        public long getMaxRecordMessageId() {
            return maxRecordMessageId;
        }

        public void setMaxRecordMessageId(long maxRecordMessageId) {
            this.maxRecordMessageId = maxRecordMessageId;
        }

        public HashSet<Long> getServerRequestSet() {
            return serverRequestSet;
        }

        public void setServerRequestSet(HashSet<Long> serverRequestSet) {
            this.serverRequestSet = serverRequestSet;
        }
    }

    private ArrayList<ChatRecord> recordBufferList = new ArrayList<ChatRecord>();// 要展示的消息列表
    private ArrayList<ChatRecord> unsendRecordList = new ArrayList<ChatRecord>();// 未发送的消息列表
    private ArrayList<ChatRecord> systemRecordList = new ArrayList<ChatRecord>();// 系统的消息列表(messsageid为0)

    private void getHistoryRecords() {
        boolean isGetDataSuccess = true;

        // 判断是否请求过该段数据的原因是:存在客户端和服务端都没有数据,但是因为上次请求过这一段数据,所以,应该再去数据库拿出这一段数据
        // !mHistoryRecorder.getServerRequestSet().contains(mHistoryRecorder.getMaxRecordMessageId())
        // 这个判断条件的原因
        if (mHistoryRecorder.isServerNoData() && mHistoryRecorder.isLocalNoData() && !mHistoryRecorder.getServerRequestSet().contains(mHistoryRecorder.getMaxRecordMessageId())) {
            isGetDataSuccess = true;
        } else {
            isGetDataSuccess = GroupChatListModel.getInstance().getRecordDatas(mContext, getTargetID(), LOAD_MORE_NUM, mHistoryRecorder);
        }

        if (isGetDataSuccess) {

            ArrayList<ChatRecord> tmpList = mHistoryRecorder.getCurrentBuffer();
            if (mHistoryRecorder.isLocalNoData()) {
                if (tmpList.size() == 0) {
                    if (mHistoryRecorder.isServerNoData()) {
                        Message msg = new Message();
                        msg.what = HandleMsgCode.MSG_GET_HISTORY;
                        msg.arg1 = NO_DATA;
                        mHandler.sendMessage(msg);
                    } else {
                        long requestMessageId = mHistoryRecorder.getMaxRecordMessageId();
                        CommonFunction.log(TAG, "getHistoryRecords() requestMessageId == " + requestMessageId);
                        mHistoryRecorder.getServerRequestSet().add(requestMessageId);
                        getHistoryFromServer(requestMessageId);
                    }

                } else {
                    recordBufferList.addAll(tmpList);
                    CommonFunction.log(TAG, "getHistoryRecords() refresh data up");
                    mHistoryRecorder.getCurrentBuffer().clear();
                    Message msg = new Message();
                    msg.what = HandleMsgCode.MSG_GET_HISTORY;
                    msg.arg1 = REFRESH_DATA;
                    mHandler.sendMessage(msg);
                }
            } else {
                if (tmpList.size() >= LOAD_MORE_NUM) {

                    recordBufferList.addAll(tmpList);
                    CommonFunction.log(TAG, "getHistoryRecords() refresh data down");
                    mHistoryRecorder.getCurrentBuffer().clear();
                    Message msg = new Message();
                    msg.what = HandleMsgCode.MSG_GET_HISTORY;
                    msg.arg1 = REFRESH_DATA;
                    mHandler.sendMessage(msg);
                } else {
                    // 取满一页的数据
                    getHistoryRecords();
                }
            }
        } else {

            if (mHistoryRecorder.isServerNoData()) {
                Message msg = new Message();
                msg.what = HandleMsgCode.MSG_GET_HISTORY;
                msg.arg1 = NO_DATA;
                mHandler.sendMessage(msg);
            } else {
                long requestMessageId = mHistoryRecorder.getMaxRecordMessageId();
                if (mHistoryRecorder.getServerRequestSet().contains(requestMessageId)) {
                    getHistoryRecords();
                } else {
                    mHistoryRecorder.getServerRequestSet().add(requestMessageId);
                    getHistoryFromServer(requestMessageId);
                }
            }
        }

    }

    private void getUnSendSuccessMessage() {
        unsendRecordList = GroupChatListModel.getInstance().getUnsendSuccessRecords(mContext, getTargetID());
    }

    private void getSystemMessage() {
        systemRecordList = GroupChatListModel.getInstance().getSystemRecords(mContext, getTargetID());
    }

    private void putUnsendToDisplayList(long mLastDataTime) {
        Iterator<ChatRecord> it = unsendRecordList.iterator();
        while (it.hasNext()) {
            ChatRecord item = it.next();
            CommonFunction.log(TAG, "putUnsendToDisplayList() time : lastDataTime == " + item.getDatetime() + " : " + mLastDataTime);
            if (item.getDatetime() >= mLastDataTime) {
                it.remove();
                recordBufferList.add(item);
            }
        }
    }

    private void putSystemMessageToDisplayList(long mLastDataTime) {
        Iterator<ChatRecord> it = systemRecordList.iterator();
        while (it.hasNext()) {
            ChatRecord item = it.next();
            if (item.getDatetime() >= mLastDataTime) {
                it.remove();
                recordBufferList.add(item);
            }
        }
    }

    private void getHistoryFromServer(long maxMsgId) {
        SocketGroupProtocol.groupGetHistoryMessages(mContext, getTargetID(), maxMsgId, LOAD_MORE_NUM);
    }

    private void sortListByDataTime(ArrayList<ChatRecord> data) {
        Collections.sort(data, new Comparator<ChatRecord>() {

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public int compare(ChatRecord lhs, ChatRecord rhs) {
                return Long.compare(lhs.getDatetime(), rhs.getDatetime());
            }
        });
    }

    /**
     * 重新加载聊天记录
     */
    private void reloadRoomRecords() {
        if (currentUser != null) {
            adapter = new ChatGroupRecordAdapter(mContext, mRecordList, currentUser);
            ((ChatGroupRecordAdapter) adapter).initClickListeners(new UserIconClickListener(), new UserIconLongClickListener(), new SendFailClickListener());
            adapter.setMaxSelectSize(MAX_SELECT_COUNT);
        }
        chatRecordListView.setAdapter(adapter);
        chatRecordListView.setOnScrollListener(onScrollListener);
    }

    /**
     * 从其他页面返回到圈子后的操作: <br/>
     * <br/>
     * 判读本人是否被踢；重新从缓存加载聊天记录；标识已进入聊天室；重置未读房间消息
     */
    private void afterBack() {
        // 被踢出聊吧或者聊吧解散不需最小化
        if (GroupModel.getInstance().isKicked() || GroupModel.getInstance().isDismissGroup()) { // 本人已经被踢
            CommonFunction.log(TAG, "afterBack() 被踢出聊吧或聊吧已经解散");
            mNeedZoomChatbar = false;
            finish();
            return;
        }

        updateList();
        showListBottom();

        //GroupModel.getInstance().setInGroupChat(true); // 设置：进入聊天室
    }

    /**
     * 显示管理员人数限制的对话框
     */
    private void displayManagerLimitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle(R.string.group_user_list_become_manager_title)
                .setMessage(R.string.group_user_list_manager_limit).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * 进入群成功
     *
     * @param message
     */
    private void receiveJoinInSuccess(TransportMessage message) {
        String json = message.getContentBody();
        CommonFunction.log(TAG, "receiveJoinInSuccess() join success***" + json);
        GroupChatEntrySuccess bean = GsonUtil.getInstance().getServerBean(json, GroupChatEntrySuccess.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_JOIN_IN_SUCCESS, bean);
        mHandler.sendMessage(msg);
    }

    /**
     * 进入群失败
     *
     * @param message
     */
    private void receiveJoinInFail(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatEntryFail bean = GsonUtil.getInstance()
                .getServerBean(json, GroupChatEntryFail.class);

        // 在吧内被别人使用了无影脚
        if (bean.error == -20073004) {

        }

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_JOIN_IN_FAIL, bean);
        mHandler.sendMessage(msg);
    }

    /**
     * 成功发送群消息
     *
     * @param message
     */
    private void receiveSendMsgSucc(TransportMessage message) {
        String json = message.getContentBody();
        GroupMessageSendSuccess bean = GsonUtil.getInstance()
                .getServerBean(json, GroupMessageSendSuccess.class);
        Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(json);
        if (item != null) {
            bean.item = item;
            if (!item.toString().equals(Common.getInstance().loginUser.getItem())) {
                Common.getInstance().loginUser.setItem(item);
            }
        }

        SharedPreferenceUtil.getInstance(this).putInt("cat", bean.cat);
        SharedPreferenceUtil.getInstance(this).putInt("top", bean.top);
        SharedPreferenceUtil.getInstance(this).putInt("type", bean.type);

        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_ROOM_MSG_SUCC, bean);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * 发送群消息：失败
     */
    private void receiveSendMsgFail(TransportMessage message) {
        String json = message.getContentBody();
        GroupMessageSendFail bean = GsonUtil.getInstance()
                .getServerBean(json, GroupMessageSendFail.class);

        GroupMessageSendFailWithMsgId failBean = new GroupMessageSendFailWithMsgId();
        failBean.failBean = bean;
        failBean.msgId = message.getId();

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_ROOM_MSG_FAIL, failBean);
        mHandler.sendMessage(msg);
    }

    /**
     * 接收最新的群消息
     *
     * @param message
     */
    private void receiveGroupNewMsg(TransportMessage message) {
        try {
            String groupId = GroupModel.getInstance().parseGroupId(message);
            if (groupId.equals(getCurrentGroupId())) { // 判断属于本群的消息
                ChatRecord record = GroupChatListModel.getInstance().parseGroupNewMessage(message);
                Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_RECEIVE_ROOM_MESSAGE, record);
                mHandler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收被踢消息(本人被群主踢)
     *
     * @param message
     */
    private void receiveKickedMsg(TransportMessage message) {
        try {
            String groupId = GroupModel.getInstance().parseGroupId(message);
            if (groupId.equals(getCurrentGroupId())) { // 判断属于本群的消息
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_RECEIVE_KICKED_USER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解散群组
     *
     * @param message
     */
    private void receiveDismissGroup(TransportMessage message) {
        try {
            String groupId = GroupModel.getInstance().parseGroupId(message);
            if (groupId.equals(getCurrentGroupId())) { // 判断属于本群的消息
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_DISMISS_GROUP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群主踢人成功
     *
     * @param message
     */
    private void receiveKickUserSucc(TransportMessage message) {
        try {
            String groupId = GroupModel.getInstance().parseGroupId(message);
            if (groupId.equals(getCurrentGroupId())) { // 判断属于本群的消息
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_KICK_USER_SUCC);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 群主踢人失败
     *
     * @param message
     */
    private void receiveKickUserFail(TransportMessage message) {
        CommonFunction.log("VIP踢人失败", "VIP踢人失败");
        try {
            Map<String, Object> map = getKickUserFail(message);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_KICK_USER_FAIL, map);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取VIP踢人失败消息
     *
     * @param message
     * @return
     * @throws JSONException
     */
    private Map<String, Object> getKickUserFail(TransportMessage message) throws JSONException {
        CommonFunction.log("VIP踢人失败消息", message.getContentBody());
        Map<String, Object> map = new HashMap<String, Object>();

        JSONObject kickJson = new JSONObject(message.getContentBody());
        if (kickJson != null) {
            map.put("error", kickJson.optInt("error")); // 错误类型
            map.put("groupid", kickJson.optInt("groupid")); // 圈子id类型
            map.put("userid", kickJson.optInt("userid")); // 用户id类型
        }

        return map;
    }

    /**
     * 当Socket发送消息，返回的发送情况
     *
     * @param msg
     */
    private void handleSendMsgState(Message msg) {
        long flag = msg.getData().getLong("flag");
        int e = msg.getData().getInt("e");
        // 找出当前发送的聊天消息
        int position = findCurSendMsg(mRecordList, flag);
        if (position < 0) {
            return;
        }

        ChatRecord record = mRecordList.get(position);
        if (e == 0) {
            Toast.makeText(mContext, R.string.send_err, Toast.LENGTH_SHORT).show();
            record.setStatus(ChatRecordStatus.FAIL);
        } else {
            record.setStatus(ChatRecordStatus.ARRIVED); // 标识为已送达
        }
        setRecordRole(record);
        updateList();
        autoShowListBottom();

    }

    /**
     * 成功发送聊天消息
     *
     * @param msg
     */
    private void handleSendMsgSucc(Message msg) {
        // 发送成功修改缓存的状态,不保存服务器的消息id
        GroupMessageSendSuccess bean = (GroupMessageSendSuccess) msg.obj;
        long flag = bean.flag;
        // long serverID = bean.msgid;
        // 找出当前发送的聊天消息；设置聊天记录的id；保存到数据库
        int position = findCurSendMsg(mRecordList, flag);
        if (position != -1) {
            ChatRecord record = mRecordList.get(position);
            record.setStatus(ChatRecordStatus.ARRIVED);
            record.setItem(bean.item);

            /**
             *  YC
             */
            record.setmCat(bean.cat);
            record.setmDataType(bean.type);
            record.setmTop(bean.top);

            if (bean.top != 0 || record.getmTop() != bean.top) {
                SharedPreferenceUtil.getInstance(this).putInt("cat", bean.cat);
                SharedPreferenceUtil.getInstance(this).putInt("top", bean.top);
                SharedPreferenceUtil.getInstance(this).putInt("type", bean.type);
            }
            setRecordRole(record);
            updateList();
            autoShowListBottom();

            //更新聊吧消息里的用户称号
            if (bean.item != null) {
                GroupChatListModel.getInstance().modifyGroupMessageUserTitle(this, record.getLocid(), bean.item.key, bean.item.value);
            }
        }
    }

    private void setRecordRole(ChatRecord record) {
        /**
         * 判断好友身份
         */
        record.setGroupRole(-1);
        record.setMgroupRole(-1);
        if (GroupModel.getInstance().getGroupOwnerId() != null && GroupModel.getInstance().getGroupOwnerId().equals(String.valueOf(record.getFuid()))) {
            record.setGroupRole(0);
        } else if (GroupModel.getInstance().isManager(String.valueOf(record.getFuid()))) {
            record.setGroupRole(1);
        } else {
            record.setGroupRole(2);
        }
        if (currentUser != null) {
            if (record.getUid() == currentUser.getUserid()) {
                /**
                 * 判断自己
                 */
                if (currentUser.getGroup_role() == 1) {
                    record.setMgroupRole(1);
                } else if (currentUser.getGroup_role() == 0) {
                    record.setMgroupRole(0);
                } else {
                    record.setMgroupRole(2);
                }
                if (record.getmTop() == 0) {
                    record.setmCat(SharedPreferenceUtil.getInstance(mContext).getInt("cat"));
                    record.setmTop(SharedPreferenceUtil.getInstance(mContext).getInt("top"));
                    record.setmDataType(SharedPreferenceUtil.getInstance(mContext).getInt("type"));
                }
            }
        }
    }

    /**
     * 发送私聊消息失败
     */
    private void handleSendMsgFail(Message msg) {
        GroupMessageSendFailWithMsgId bean = (GroupMessageSendFailWithMsgId) msg.obj;

        long flag = bean.failBean.flag;
        // long msgId = bean.msgId;

        //gh 技能使用紧箍咒时，禁言业务
        if (bean.failBean.error == -60073008) {
            int time = 0;
            if (bean.failBean.ts > 60) {
                time = new BigDecimal(bean.failBean.ts / 60).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
            } else {
                time = 1;
            }
            String desc = String.format(getString(R.string.error_group_skill_attact_gag), String.valueOf(time));
            CommonFunction.toastMsg(this, desc);
        }

        // 找出当前发送的聊天消息；设置聊天记录的id；保存到数据库
        int position = findCurSendMsg(mRecordList, flag);
        if (position != -1) {
            ChatRecord record = mRecordList.get(position);
            // 设置聊天记录的id
            record.setStatus(ChatRecordStatus.FAIL);
            setRecordRole(record);
            updateList();
            autoShowListBottom();
        }
    }

    /**
     * 接收最新的聊天室消息
     *
     * @param msg
     */
    private void handleGroupNewMsg(Message msg) {
        ChatRecord record = (ChatRecord) msg.obj;
        // 过滤非文本的消息
        if (record.getType() == null) return;
        //gh 文本类型
        if (record.getType().equals("1") | record.getType().equals("13") || record.getType().equals("9") || record.getType().equals("10")) {
            long messageId = record.getId();
            if (messageId <= 0 || !mMessageIdSet.contains(messageId)) {
                setRecordRole(record);
                GroupChatListModel.getInstance().addRecord(mRecordList, -1, record);
                recordBufferList.add(record);
            }
            updateList();
            checkForUnSee();
            autoShowListBottom();
        }
    }

    /**
     * 接收被踢信息(本人被群主踢)
     *
     * @param msg
     */
    private void handleKickedFromGroup(Message msg) {
        CommonFunction.log(TAG, "handleKickedFromGroup() 被踢出聊吧");
        mNeedZoomChatbar = false;
        finish();
    }

    /**
     * 群主踢人成功
     *
     * @param msg
     */
    private void handleKickUserSucc(Message msg) {
        CommonFunction.toastMsg(mContext, R.string.operate_success);
    }

    /**
     * 群组解散消息
     *
     * @param msg
     */
    private void handleGroupDismiss(Message msg) {
        CommonFunction.log(TAG, "handleGroupDismiss() 聊吧已经解散");
        mNeedZoomChatbar = false;
        finish();
    }

    /**
     * 进入群成功
     *
     * @param msg
     */
    private void handleJoinSuccess(Message msg) {
        Log.d(TAG, "handleJoinSuccess() into");

        GroupChatEntrySuccess bean = (GroupChatEntrySuccess) msg.obj;
        //gh 技能使用弹框
        if (null != bean.deny && !TextUtils.isEmpty(bean.deny)) {
            SkillAttackResult result = GsonUtil.getInstance().getServerBean(bean.deny, SkillAttackResult.class);
            // 无影脚时，展时技能攻击弹框，且需要缓存
            if (result.skillId == 4) {
                Common.getInstance().setSkillResult(bean.deny);
                handleSkillAttackResult(result);
                Log.d(TAG, "handleJoinSuccess() skill kick out");
                return;
            } else {
                Common.getInstance().setSkillResult("");
            }
        } else {
            Common.getInstance().setSkillResult("");
        }

        // 重新进入房间
        if (flyAudioRoom != null) {
            if (fromchatbarwindowzoom != 1) {
                flyAudioRoom.setRoomId(getCurrentGroupId());
            }
        }

        long loginUserId = Common.getInstance().loginUser.getUid();

        if (bean.micuserid != null) {
            micUser1 = bean.micuserid;
            Log.d(TAG, "login user=" + loginUserId + ",mic1=" + bean.micuserid.getUserid());
            if (bean.micuserid.getUserid() == loginUserId) {
                if (flyAudioRoom != null) {
                    if (fromchatbarwindowzoom == 1) {
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, true);
                        SocketGroupProtocol.onMicSuccess(BaseApplication.appContext, flyAudioRoom.getRoomId(), flyAudioRoom.getManageId(), flyAudioRoom.getPosition(), flyAudioRoom.getPushflows());
                    } else {
                        startPush();
                    }
                }
            }
        } else {
            micUser1 = null;
            Log.d(TAG, "handleJoinSuccess() Mic 1 GONE");
            rlOnChat1.setVisibility(View.GONE);
            mBtnOnChat1.setVisibility(View.VISIBLE);
        }


        if (bean.micuserid2 != null) {
            micUser2 = bean.micuserid2;
            Log.d(TAG, "login user=" + loginUserId + ",mic2=" + bean.micuserid2.getUserid());
            if (bean.micuserid2.getUserid() == loginUserId) {
                if (flyAudioRoom != null) {
                    if (fromchatbarwindowzoom == 1) {
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(Common.getInstance().loginUser.getUid() + "/" + SharedPreferenceUtil.SYSTEM_MIC, true);
                        SocketGroupProtocol.onMicSuccess(BaseApplication.appContext, flyAudioRoom.getRoomId(), flyAudioRoom.getManageId(), flyAudioRoom.getPosition(), flyAudioRoom.getPushflows());
                    } else {
                        startPush();
                    }
                }
            }
        } else {
            micUser2 = null;
            rlOnChat2.setVisibility(View.GONE);
            mBtnOnChat2.setVisibility(View.VISIBLE);
        }

//        if ((micUser1 == null && micUser2 == null)) {
//            Log.d(TAG, "no user on mic...");
//            stopPush();
//        }

        if ((micUser1 == null || micUser1.getUserid() != Common.getInstance().loginUser.getUid()) && (micUser2 == null || micUser2.getUserid() != Common.getInstance().loginUser.getUid())) {
            Log.d(TAG, "自己不在麦上，停止推流");
            stopPush();
        }


        if (fromchatbarwindowzoom == 1) {
            if (null != flyAudioRoom) {
                String user1 = micUser1 == null ? null : String.valueOf(micUser1.getUserid());
                String user2 = micUser2 == null ? null : String.valueOf(micUser2.getUserid());
                flyAudioRoom.updateSoundWaveUser(user1, user2);
            }
        }

        refershMic();


        if (onLineNumberAdapter != null) {
            onLineNumberAdapter.setMicNumber(getMicNumber());
        }

        /*****如果 自己在麦位1或者2上则不作处理，否则停止推流***/
        if (micUser1 != null) {
            if (micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
                return;
            }
        }
        if (micUser2 != null) {
            if (micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {
                return;
            }
        }
        /*****如果 自己在麦位1或者2上则不作处理，否则停止推流***/
        if (flyAudioRoom != null) {
            CommonFunction.log(TAG, "停止推流");
            stopPush();
        }
        Log.d(TAG, "handleJoinSuccess() out");
    }

    /**
     * 进入群失败
     *
     * @param msg
     */
    private void handleJoinFail(Message msg) {
        GroupChatEntryFail bean = (GroupChatEntryFail) msg.obj;
        int error = (int) bean.error;
        String title = getResString(R.string.dialog_title);
        String tips = getResString(R.string.you_are_not_group_member);

        if (error == SocketErrorCode.se_40073004) {
            tips = getResString(R.string.se_40073004);
        } else if (error == SocketErrorCode.se_10073044) {
            tips = getResString(R.string.se_10073044);
            CommonFunction.toastMsg(this, tips);
        } else if (error == SocketErrorCode.se_20073044) {
            tips = getResString(R.string.se_20073044);
            CommonFunction.toastMsg(this, tips);
        }

        if (error == SocketErrorCode.se_30073004 || error == SocketErrorCode.se_40073004) {
            CustomDialog dialog = new CustomDialog(mContext, title, tips, getResString(R.string.ok), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommonFunction.log(TAG, "handleJoinFail() 进群失败");
                    String userid = String.valueOf(UserId);
                    GroupModel.getInstance().removeGroupAndAllMessage(mContext, userid, getCurrentGroupId());
                    mNeedZoomChatbar = false;
                    finish();
                }
            });
            dialog.setCancelable(false);
            dialog.show();
            return;
        }
    }

    /**
     * 请求群管理员ID列表
     */
    private void reqGroupManagerIdList() {
        requestGroupManagerIdFlag = GroupHttpProtocol.groupManageList(mActivity, getCurrentGroupId(), new ChatBarHttpCallback(this));
        if (-1 == requestGroupManagerIdFlag) {
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_REQ_GROUP_MANAGER_LIST_FAIL);
        }
    }

    /**
     * 请求聊吧资料
     */
    private void reqGroupSimpleList() {
        requestGroupSimpleFlag = GroupHttpProtocol.getGroupTopicSimple(mActivity, getCurrentGroupId(), new ChatBarHttpCallback(this));
        if (-1 == requestGroupSimpleFlag) {
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_REQ_GROUP_SIMPLE_FAIL);
        }
    }


    /**
     * 请求群管理员ID列表成功 （UI线程）
     */
    private void handleGroupManagerIdListSucc(Message msg) {
        GroupManagerListBean bean = (GroupManagerListBean) msg.obj;
        GroupModel.getInstance().parseGroupManagerList(bean);
        //获取管理员列表之后再去绘制聊天记录
        reloadRoomRecords();
        getHistoryRecords();
    }

    static class HeadViewClickListener implements OnHeadViewClickListener {
        private WeakReference<GroupChatTopicActivity> mActivity;

        public HeadViewClickListener(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void headViewClick(PipelineGift gift) {
            GroupChatTopicActivity activity = mActivity.get();
            if (null == activity) return;
            // 创建者的id
            long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());

            //添加非空判断
            if (activity.currentUser == null || gift == null) {
                return;
            }
            //添加user空判断
            if (activity.user == null) {
                activity.user = new User();
            }
            activity.user.setUid(gift.getSendId());
            if (activity.currentUser.getUserid() == gift.sendId) {//点击的是自己
                activity.user.setIcon(activity.currentUser.getIcon());
                activity.user.setGroupRole(activity.currentUser.getGroup_role());
                activity.user.setAudio(activity.currentUser.getAudio());
                activity.customChatBarDialog = new CustomChatBarDialog(activity.mContext, new ChatbarSendPersonalSocketListenerImpl(activity));
                activity.customChatBarDialog.setUser(activity.user, activity.user.getGroupRole(), activity.getCurrentGroupId());
                activity.customChatBarDialog.isClickMySelf(true);
                activity.customChatBarDialog.setLuxuryGiftView(activity.luxuryGiftView);
                activity.customChatBarDialog.show();
            } else {
                if (activity.currentUser.getUserid() == creatorUid) { // 是群的创建者
                    // 吧主权限
                    activity.customChatBarDialog = new CustomChatBarDialog(activity.mContext, new ChatbarSendPersonalSocketListenerImpl(activity));
                    activity.customChatBarDialog.setUser(activity.user, 0, activity.getCurrentGroupId());
                    activity.customChatBarDialog.setLuxuryGiftView(activity.luxuryGiftView);
                    activity.customChatBarDialog.show();
                } else if (activity.currentUser.getGroup_role() == 1) { // 为管理员
                    // 管理员的权限
                    activity.customChatBarDialog = new CustomChatBarDialog(activity.mContext, new ChatbarSendPersonalSocketListenerImpl(activity));
                    activity.customChatBarDialog.setUser(activity.user, 1, activity.getCurrentGroupId());
                    activity.customChatBarDialog.setLuxuryGiftView(activity.luxuryGiftView);
                    activity.customChatBarDialog.show();
                } else {
                    activity.customChatBarDialog = new CustomChatBarDialog(activity.mContext, new ChatbarSendPersonalSocketListenerImpl(activity));
                    activity.customChatBarDialog.setUser(activity.user, 3, activity.getCurrentGroupId());
                    activity.customChatBarDialog.setLuxuryGiftView(activity.luxuryGiftView);
                    activity.customChatBarDialog.show();
                }
            }
        }
    }

    /**
     * 请求聊吧拉取资料接口成功
     */
    private void handleGroupSimpleSucc(Message msg) {
        GroupTopicSimpleBean bean = (GroupTopicSimpleBean) msg.obj;
        if (bean != null) {
            sharePicUrl = bean.getSharepic();
            welcomeText = CommonFunction.getLangText(GroupChatTopicActivity.this, bean.getWelcome());
            mGroupInfo = new GroupInfo();
            mGroupInfo.setGroupId(String.valueOf(bean.getGroup_id()));
            mGroupInfo.setGroupIcon(bean.getGroup_icon());
            mGroupInfo.setGroupName(FaceManager.getInstance(this).parseIconForString(this, bean.getGroup_name(), 13, null).toString());
            mGroupInfo.setLotterySwitch(bean.getLotterySwitch());
            mGroupInfo.setShareSwitch(bean.getShareSwitch());
            if (bean != null && bean.getOwn_user() != null) {
                mGroupInfo.setCreatorId(bean.getOwn_user().user.getUserid());
                mGroupInfo.setCreatorIcon(bean.getOwn_user().user.getIcon());
            }
            // 圈子角色，默认普通会员
            if (bean.getUser() != null) {
                mGroupInfo.setGroupRole(bean.getUser().user.getGroup_role());
            }

            GroupModel.getInstance().setGroupId(getCurrentGroupId()); // 记录当前群的ID
            GroupChatTopicUtil.getInstance().initData(mGroupInfo);

            /**
             * 首次进入聊吧，弹出文明聊天提示语
             */
            GroupChatTopicUtil.getInstance().handleFirstEnter(this);

            /**
             * 加载聊吧名称
             */
            chatbarName = mGroupInfo.getGroupName();
            chatbarHot = bean.getChatbar_hot();
            if (chatbarName != null || !chatbarName.equals("")) {
                mTvTitle.setText(chatbarName);
            }

            if (bean.isSuccess()) {
                if (bean.getGroup_icon() != null) {

                    //弹出提示升级的对话框
                    if (bean.getUpdate_flag() == 1) {
                        if (Config.isShowGoogleApp) return;//Google商店不弹更新包
                        DialogUtil.showOKCancelDialogFlatUpdate(mContext, mContext.getString(R.string.dialog_title), mContext.getString(R.string.group_chat_flat_update_title_xontent), new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // 下载
                                DownloadNewVersionTask.getInstance(GroupChatTopicActivity.this, true).reset();
                                DownloadNewVersionTask.getInstance(GroupChatTopicActivity.this, true).execute(Constants.CHAT_BAR_UPDATE_DIALOB_URL);

                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CommonFunction.log(TAG, "handleGroupSimpleSucc() 提示升级被取消");
                                mNeedZoomChatbar = false;
                                finish();
                            }
                        });
                    }
                    /**
                     * 加载聊吧顶部背景图
                     */
                    GlideUtil.loadImage(BaseApplication.appContext, bean.getGroup_icon(), ivChatbarbg,
                            R.drawable.iaround_default_img, R.drawable.iaround_default_img);
                } else {
                    ivChatbarbg.setImageResource(R.drawable.chat_bar_title_default_bg);
                }
                /**
                 * 加载聊吧名称
                 */
                SpannableString spNameRight = FaceManager.getInstance(mContext).parseIconForString(mContext, bean.getGroup_name(),
                        0, null);
                mTvTitle.setText(spNameRight);

                //处理当前用户对象为空时，重新拉取数据
                if (bean.getUser() == null) {
                    reqGroupSimpleList();
                    return;
                }
                //当前用户
                currentUser = bean.getUser() != null ? bean.getUser().user : null;

                if (bean.getOwn_user() != null) {
                    //吧主
                    groupOwnerUser = bean.getOwn_user() != null ? bean.getOwn_user().user : null;
                }

                //设置当前登陆者的圈子角色
                Common.getInstance().loginUser.setGroupRole(currentUser.getGroup_role());

                if (mLineNumberDatas.size() > 0) {
                    mLineNumberDatas.clear();
                }

                onLineNumber = bean.getOnline_amount();

                if (bean.online_list != null) {
                    // 坑啊 在线列表要做一层转化
                    for (int i = 0; i < bean.online_list.size(); i++) {
                        GroupSimpleUser groupSimple = bean.online_list.get(i);
                        mLineNumberDatas.add(groupSimple.user);
                    }
                }
                onLineNumberAdapter.updateData(mLineNumberDatas);
                onLineNumberAdapter.updateCurrentUser(currentUser);

                // 控制二麦的展示与隐藏
                if (bean.getConcat_audio() == 1) {
                    findViewById(R.id.ly_chat_on2).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.ly_chat_on2).setVisibility(View.GONE);
                }

                if (flyAudioRoom != null) {
                    flyAudioRoom.setIsFlag(bean.getConcat_audio());
                }
                //抽奖
                if (bean.getLotteryCount() != null) {
                    luckpanFreeCount = bean.getLotteryCount().getFree();
                    //更新图标
                    if (luckpanFreeCount > 0) {
                        luckPanCountView.setVisibility(View.VISIBLE);
                        luckPanCountView.setText(String.valueOf(luckpanFreeCount));
                    } else {
                        luckPanCountView.setVisibility(View.INVISIBLE);
                    }
                    luckpanDiamondCount = bean.getLotteryCount().getDiamond();
                }
                boolean luckPanSwitch = bean.getLotterySwitch();
                if (luckPanSwitch == false) {
                    luckPanContainer.setVisibility(View.GONE);
                } else {
                    luckPanContainer.setVisibility(View.VISIBLE);
                }
                boolean shareSwitch = bean.getShareSwitch();
                if (false == shareSwitch) {
                    btShare.setVisibility(View.GONE);
                } else {
                    btShare.setVisibility(View.VISIBLE);
                }
                int[] amount = bean.getLotteryAmount();
                if (null != amount && amount.length >= 10) {
                    luckpanAmount = amount;
                }
                int freeLottery = bean.getShareFreeLottery();
                if (freeLottery > 0) {
                    shareFreeLottery = freeLottery;
                }

                // 通知服务器，已进入群组；开始接收群消息
                //SocketGroupProtocol.groupComeIn(mActivity, getCurrentGroupId());

            }

            if (giftQueueHandler != null) {
                giftQueueHandler.setOnHeadViewClickListener(new HeadViewClickListener(this));
            }

            //重新绘制聊天信息
            reloadRoomRecords();
            if (currentUser != null) {
                ((ChatGroupRecordAdapter) adapter).updateCurrentUser(currentUser);
            }

            long skillUpdate = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(Common.getInstance().getUid() + SharedPreferenceUtil.GROUO_CHAT_BAR_SKILL_UPDATE);
            if (bean.getNoticeTime() > 0 && System.currentTimeMillis() / 1000 - skillUpdate > bean.getNoticeTime()) {
                requestSkillUpdateList();
            }

            if (bean.getGroup_id() > 0) {
                GroupHistoryModel.getInstance().insertOrder(BaseApplication.appContext, bean.getGroup_id(), bean.getGroup_name(), bean.getGroup_icon(), TimeFormat.getCurrentTimeMillis());
            }
        }
    }

    /**
     * 请求在线列表成功
     *
     * @param msg
     */
    private void handleGroupOnLineListSuccess(Message msg) {
        GroupOnlineUser bean = (GroupOnlineUser) msg.obj;

        if (null != bean && bean.isSuccess()) {
            if (mLineNumberDatas.size() > 0) {
                mLineNumberDatas.clear();
            }

            if (bean.getList() != null) {
                // 坑啊 在线列表要做一层转化
                for (int i = 0; i < bean.getList().size(); i++) {
                    GroupSimpleUser groupSimple = bean.getList().get(i);
                    mLineNumberDatas.add(groupSimple.user);
                }
            }
            onLineNumberAdapter.updateData(mLineNumberDatas);
            onLineNumberAdapter.updateCurrentUser(currentUser);
        }

    }

    /**
     * 请求群管理员ID列表失败
     *
     * @param msg
     */
    private void handleGroupManagerIdListFail(Message msg) {

    }

    /**
     * 请求聊吧拉取资料接口失败
     *
     * @param msg
     */
    private void handleGroupSimpleListFail(Message msg) {

    }

    /**
     * 升为管理员成功
     *
     * @param msg
     */
    @SuppressWarnings("unchecked")
    private void handleBecomeManagerSucc(Message msg) {
        try {
            HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
            String userId = String.valueOf(map.get("user_id"));
            String nickname = String.valueOf(map.get("nickname"));

            GroupModel.getInstance().addToManagerIdList(userId);
            String des = String
                    .format(mActivity.getString(R.string.group_user_list_become_manager_succ_tip),
                            nickname);
            CommonFunction.showToast(mActivity.getApplicationContext(), des, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理录制语音结束的操作
     *
     * @param bean
     */
    protected void handleRecordFinish(final AudioBaseBean bean) {
        // 在这里应该做一个判断,是否发送出去,如果不是发送出去,应该删除消息
        backManager.callSendDataThreadCodeEnd(bean.flag);
        int position = findCurSendMsg(mRecordList, bean.flag);

        if (position >= 0) {
            ChatRecord record = mRecordList.get(position);
            if (bean.isSend) {
                record.setContent(bean.audioLength);
                GroupChatListModel.getInstance()
                        .modifyGroupMessageContent(mContext, record.getLocid(),
                                record.getContent());
            } else {

                delOneRecord(record);

                dataHandler.post(new Runnable() {

                    @Override
                    public void run() {
                        long localId = GroupChatListModel.getInstance()
                                .getRecordLocalId(bean.flag);
                        backManager.callSendDataThreadCodeEnd(bean.flag);
                        GroupChatListModel.getInstance()
                                .removeGroupMessageByLoaclId(mContext, localId);
                        GroupChatListModel.getInstance().removeRecordLocalId(bean.flag);
                    }
                });
            }
        }
        mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
    }

    private final int REFRESH_DATA = 1;
    private final int NO_DATA = 2;

    /**
     * 获取最近消息
     */
    private void getHistoryRecord(Message msg) {
        if (adapter == null) {
            reloadRoomRecords();
        }
        if (msg.arg1 == REFRESH_DATA) { // 有数据
            int beforeSize = 0;
            if (mRecordList != null) {
                beforeSize = mRecordList.size();
            }

            mRecordList.clear();

            //获取当前buffer中发出时间最小的时间
            long minTime = 0;
            if (recordBufferList.size() > 0)
                for (int i = 0; i < recordBufferList.size(); i++) {
                    if (minTime == 0 || (recordBufferList.get(i).getDatetime() > 0 &&
                            recordBufferList.get(i).getDatetime() < minTime))
                        minTime = recordBufferList.get(i).getDatetime();
                }

            putUnsendToDisplayList(minTime);
            putSystemMessageToDisplayList(minTime);

            mMessageIdSet.clear();
            for (ChatRecord item : recordBufferList) {
                long messageId = item.getId();
                if (messageId <= 0 || !mMessageIdSet.contains(messageId)) {
                    setRecordRole(item);
                    mRecordList.add(item);
                    mMessageIdSet.add(messageId);
                }
            }

            sortListByDataTime(mRecordList);
            GroupChatListModel.getInstance().addTimerRecord(mRecordList);
            updateList();

            int afterSize = 0;
            if (mRecordList != null) {
                afterSize = mRecordList.size();
            }

            final int offset = Math.abs(afterSize - beforeSize);
            CommonFunction.log(TAG, "offset : afterSize : beforeSize : getbottom== " + offset + " : " + afterSize + " : " + beforeSize + " : " + chatRecordListView.getBottom());
            if (beforeSize == 0)
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
            else
                chatRecordListView.setSelection(offset);

            //以吧主身份欢迎自己
            handleToInsertChatRecordByOwner();
        } else {
            CommonFunction.toastMsg(mActivity, R.string.chat_no_history);
        }
    }


    /**
     * 自己上麦成功
     *
     * @param msg
     */
    private void handleOnMicSuccess(Message msg) {
        CommonFunction.log(TAG, "handleOnMicSuccess() into");
        GroupChatMicUserMessage bean = (GroupChatMicUserMessage) msg.obj;

        if (bean != null && currentUser != null && bean.userid == currentUser.getUserid()) {
            CommonFunction.log(TAG, "handleOnMicSuccess() user is the same");
            // 操作者上麦成功
            if (flyAudioRoom != null) {
                flyAudioRoom.setManageId(bean.managerid);
                flyAudioRoom.setPosition(bean.slot);
            }
            startPush();
        } else {
            //CommonFunction.log(TAG, "handleOnMicSuccess() user not the same, bean.userid=" + bean.userid + ", currentUser.getUserid()="+currentUser.getUserid());
        }
        CommonFunction.log(TAG, "handleOnMicSuccess() out");
    }

    /**
     * 自己下麦成功
     *
     * @param msg
     */
    private void handleOffMicSuccess(Message msg) {
        CommonFunction.log(TAG, "handleOffMicSuccess() into");

        GroupChatMicUserMessage bean = (GroupChatMicUserMessage) msg.obj;

        if (bean != null && currentUser != null && bean.userid == currentUser.getUserid()) {
            SocketGroupProtocol.offMicSuccess(this, getCurrentGroupId(), bean.managerid);
        }
    }

    /**
     * 上麦操作失败
     *
     * @param msg
     */
    private void handleMicError(Message msg) {
        CommonFunction.log(TAG, "handleMicError() into");

        Map<String, Object> map = (Map<String, Object>) msg.obj;
        int error = (Integer) map.get("error");
        long userId = (Integer) map.get("userid");
        int groupid = (Integer) map.get("groupid");
        if (userId == Common.getInstance().loginUser.getUid()) {
            if (flyAudioRoom != null) {
                stopPush();
                stopPlay();
            }

        }
        String errorStr = SocketErrorCode.getCoreMessage(mContext, error);
        CommonFunction.toastMsg(mContext, errorStr);
    }

    /**
     * 下麦操作失败
     *
     * @param msg
     */
    private void handleOffMicError(Message msg) {
        CommonFunction.log(TAG, "handleOffMicError() into");

        Map<String, Object> map = (Map<String, Object>) msg.obj;
        int error = (Integer) map.get("error");
        long userId = (Integer) map.get("userid");
        int groupid = (Integer) map.get("groupid");
        if (getCurrentGroupId().equals("" + groupid)) {
            if (error == -10073037) {
                if (flyAudioRoom != null) {
                    stopPush();
                }
            }
        }

        String errorStr = SocketErrorCode.getCoreMessage(mContext, error);
        CommonFunction.toastMsg(mContext, errorStr);

    }

    /**
     * 指定用户停止推流
     *
     * @param msg
     */
    private void handleStopPushResult(Message msg) {
        CommonFunction.log(TAG, "handleStopPushResult() into");

        Map<String, Object> map = (Map<String, Object>) msg.obj;
        int groupid = (Integer) map.get("groupid");
        if (getCurrentGroupId().equals("" + groupid)) {
            stopPush();
        }
    }


    /**
     * 上线消息
     *
     * @param msg
     */
    private void handleOnLineResult(Message msg) {
        CommonFunction.log(TAG, "handleOnLineResult() into");

        GroupChatTopicUserMessage bean = (GroupChatTopicUserMessage) msg.obj;
        for (int i = 0; i < mLineNumberDatas.size(); i++) {
            GroupUser groupUser = mLineNumberDatas.get(i);
            if (groupUser.getUserid() == bean.user.getUserid()) {
                mLineNumberDatas.remove(i);
            }
        }
        mLineNumberDatas.add(0, bean.user);
        onLineNumberAdapter.updateData(mLineNumberDatas);
        changeOnLineNmuber();
    }

    /**
     * 上线消息,xxx来了
     *
     * @param msg
     */
    private void handleForIsComeing(Message msg) {
        final GroupChatTopicUserMessage bean = (GroupChatTopicUserMessage) msg.obj;
        if (bean != null) {
            //CommonFunction.log("GroupChatTopicActivity","handleForIsComeing() into, user=" + bean.user.getUserid());
            GroupUser groupUser = bean.user;
            User user = converOnlineGroupUserToUser(groupUser);
            pipelineWelcomeView.handleWelcomeUser(user, groupId);
        }
    }

    /**
     * 技能消息,被别人使用了某个技能
     *
     * @param bean
     */
    public void handleSkillAttackResult(final SkillAttackResult bean) {
        if (bean != null) {
            // 自己的使用技能-不弹
            if (bean.user.UserID == Common.getInstance().loginUser.getUid()) {
                return;
            }
            if (bean.isHit == 0) return;

            if (Common.getInstance().isShowDailog()) return;

            if (bean.skillId == 2) return;

            if (receivedSkillDialog == null) {
                receivedSkillDialog = new ReceivedSkillDialog(this, bean, sureClickListener);
            } else {
                receivedSkillDialog.refershView(this, bean, sureClickListener);
            }

            receivedSkillDialog.show();

        }
    }

    private ReceivedSkillDialog.SureClickListener sureClickListener = new ReceivedSkillDialog.SureClickListener() {
        @Override
        public void onSure() {
            if (receivedSkillDialog.getSkillId() == 4) {
                backBtnClick();
            }
            Common.getInstance().setSkillResult("");
        }

        @Override
        public void onCancel() {
            Common.getInstance().setShowDailog(true);
            Bundle bundle = new Bundle();
            bundle.putString("targetUserId", receivedSkillDialog.getTargetUserId() + "");
            skillUseDialogFragment = SkillUseDialogFragment.getInstance(bundle);
            new SkillUsePresenter(skillUseDialogFragment);
            FragmentManager manager = ((GroupChatTopicActivity) mContext).getFragmentManager();
            skillUseDialogFragment.show(manager, "skillUseDialogFragment");
        }
    };

    /**
     * 下线消息
     *
     * @param msg
     */
    private void handleOffLineResult(Message msg) {
        CommonFunction.log(TAG, "handleOffLineResult() into");

        GroupChatTopicUserMessage bean = (GroupChatTopicUserMessage) msg.obj;

        if (currentUser != null && null != bean && bean.user != null) {
            if (currentUser.getUserid() == bean.user.getUserid()) {
                stopPush();
            }
        }

        if (micUser1 != null && null != bean && bean.user != null) {
            if (bean.user.getUserid() == micUser1.getUserid()) {
                CommonFunction.log(TAG, "handleOffLineResult() Mic 1 GONE");
                rlOnChat1.setVisibility(View.GONE);
                mBtnOnChat1.setVisibility(View.VISIBLE);
                micUser1 = null;
            }
        }

        if (micUser2 != null && null != bean && bean.user != null) {
            if (bean.user.getUserid() == micUser2.getUserid()) {
                rlOnChat2.setVisibility(View.GONE);
                mBtnOnChat2.setVisibility(View.VISIBLE);
                micUser2 = null;
            }
        }
        if (onLineNumberAdapter != null) {
            onLineNumberAdapter.setMicNumber(getMicNumber());
        }
        if (null != bean) {
            for (int i = 0; i < mLineNumberDatas.size(); i++) {
                GroupUser groupUser = mLineNumberDatas.get(i);
                if (groupUser.getUserid() == bean.user.getUserid()) {
                    mLineNumberDatas.remove(i);
                }

            }
        }
        onLineNumberAdapter.updateData(mLineNumberDatas);

        changeOnLineNmuber();
    }

    /**
     * 检查在线人数不足5人时
     */
    private void changeOnLineNmuber() {
        if (onLineNumber > mLineNumberDatas.size() & mLineNumberDatas.size() <= 15) {
            loadOnLine();
        }
    }

    /* 获取在线用户
     * */
    private void loadOnLine() {
        getOnLineMemberFlag = GroupHttpProtocol.getMemberOnLine(mContext, getCurrentGroupId(), 15, 1, new ChatBarHttpCallback(this));
    }

    /**
     * 上麦消息
     *
     * @param msg
     */
    private void handleOnMicResult(Message msg) {
        Log.d(TAG, "handleOnMicResult() into");

        GroupChatTopicUserMessage bean = (GroupChatTopicUserMessage) msg.obj;

        if (currentUser != null && bean != null && bean.micUser != null) {
            // 管理员指定你上麦
            // TODO  & -> && BY LYH
            if (flyAudioRoom.getManageId() != 0 & bean.micUser.getUserid() == currentUser.getUserid() & bean.micUser.getUserid() != flyAudioRoom.getManageId()) {
                CommonFunction.toastMsg(this, getString(R.string.group_chat_manage_on_mic_success));
            }
        }

        if (bean != null && bean.micUser != null) {
            if (bean.slot == 1) {
                micUser1 = bean.micUser;
            }
            if (bean.slot == 2) {
                micUser2 = bean.micUser;
            }
            if (customChatBarDialog != null) {
                customChatBarDialog.setMicNumber(getMicNumber());
            }
            if (onLineNumberAdapter != null) {
                onLineNumberAdapter.setMicNumber(getMicNumber());
            }
        }

        refershMic();

    }

    /**
     * 下麦消息
     *
     * @param msg
     */
    private void handleOffMicResult(Message msg) {
        CommonFunction.log(TAG, "handleOffMicResult() into");
        // 管理员指定你下麦
        GroupChatTopicUserMessage bean = (GroupChatTopicUserMessage) msg.obj;
        if (currentUser != null && bean != null && bean.micUser != null) {
            if (flyAudioRoom.getManageId() != 0 && bean.micUser.getUserid() == currentUser.getUserid() && bean.micUser.getUserid() != flyAudioRoom.getManageId()) {
                CommonFunction.toastMsg(this, getString(R.string.group_chat_manage_off_mic_success));
            }
            if (currentUser.getUserid() == bean.micUser.getUserid()) {
                stopPush();
            }
        }

        if (bean.micUser != null) {
            if (bean.slot == 1) {
                if (micUser1 != null && bean.micUser.getUserid() == micUser1.getUserid()) {
                    CommonFunction.log(TAG, "handleOffMicResult() Mic 1 GONE");
                    rlOnChat1.setVisibility(View.GONE);
                    mBtnOnChat1.setVisibility(View.VISIBLE);
                    micUser1 = null;
                }
            }
            if (bean.slot == 2) {
                if (micUser2 != null && bean.micUser.getUserid() == micUser2.getUserid()) {
                    rlOnChat2.setVisibility(View.GONE);
                    mBtnOnChat2.setVisibility(View.VISIBLE);
                    micUser2 = null;
                }
            }
            if (customChatBarDialog != null) {
                customChatBarDialog.setMicNumber(getMicNumber());
            }

            if (onLineNumberAdapter != null) {
                onLineNumberAdapter.setMicNumber(getMicNumber());
            }
        }

    }

    /**
     * 管理员下发消息
     *
     * @param msg
     */
    private void handleManagerResult(Message msg) {
        GroupNoticeListBean bean = (GroupNoticeListBean) msg.obj;
        for (GroupNoticeBean groupNoticeBean : bean.messages) {
            if (groupNoticeBean.targetuser.userid == currentUser.getUserid()) {
                if (groupNoticeBean.type == 6) {//设为管理员
                    currentUser.setGroup_role(1);
                } else if (groupNoticeBean.type == 7) {//取消管理员
                    currentUser.setGroup_role(2);
                }
                //YC 实时更新在线用户登录者的身份
                onLineNumberAdapter.updateCurrentUser(currentUser);
                //实时更新当前登陆者的身份
                Common.getInstance().loginUser.setGroupRole(1);
                if (currentUser != null && adapter != null) {
                    ((ChatGroupRecordAdapter) adapter).updateCurrentUser(currentUser);
                }
            }
        }
    }

    /**
     * 更新麦位下发消息
     *
     * @param msg
     */
    private void handleUpdateMicResult(Message msg) {
        Log.d(TAG, "handleUpdateMicResult() into");
        GroupChatUpdateMicMessage bean = (GroupChatUpdateMicMessage) msg.obj;
        // 从1麦到2麦
        if (null != bean && bean.micUser != null) {
            if (bean.src == 1 && bean.dest == 2) {
                if (micUser1 != null) {
                    if (micUser1.getUserid() == bean.micUser.getUserid()) {
                        micUser1 = null;
                    }
                    micUser2 = bean.micUser;
                }
            } else {
                if (micUser2 != null) {
                    if (micUser2.getUserid() == bean.micUser.getUserid()) {
                        micUser2 = null;
                    }
                    micUser1 = bean.micUser;
                }
            }
        }

        refershMic();
    }

    /**
     * 更新麦位
     */
    private void refershMic() {
        if (micUser1 != null) {
            Log.d(TAG, "refershMic() Mic 1 VISIBLE");
            rlOnChat1.setVisibility(View.VISIBLE);
            mBtnOnChat1.setVisibility(View.GONE);
            onChatUserMic1.setVisibility(View.VISIBLE);
            if (user == null) {
                user = new User();
            }
            user.setUid(micUser1.getUserid());
            user.setIcon(micUser1.getIcon());
            user.setViplevel(micUser1.getViplevel());
            user.setSVip(micUser1.getVip());
            user.setAge(micUser1.getAge());
            user.setGroupRole(micUser1.getGroup_role());
            user.setNickname(micUser1.getNickname());
            user.setNoteName(micUser1.getNotes());
            if (null != BaseApplication.appContext) {
                Log.d(TAG, "refershMic() loading Mic 1 icon");
                GlideUtil.loadCircleImage(BaseApplication.appContext, user.getIcon(), onChatUserIcon1);
            }
        } else {
            Log.d(TAG, "refershMic() Mic 1 GONE");
            rlOnChat1.setVisibility(View.GONE);
            mBtnOnChat1.setVisibility(View.VISIBLE);
            onChatUserMic1.setVisibility(View.GONE);
        }

        if (micUser2 != null) {
            Log.d(TAG, "refershMic() Mic 2 VISIBLE");
            rlOnChat2.setVisibility(View.VISIBLE);
            mBtnOnChat2.setVisibility(View.GONE);
            onChatUserMic2.setVisibility(View.VISIBLE);
            if (user == null) {
                user = new User();
            }
            user.setUid(micUser2.getUserid());
            user.setIcon(micUser2.getIcon());
            user.setViplevel(micUser2.getViplevel());
            user.setSVip(micUser2.getVip());
            user.setAge(micUser2.getAge());
            user.setGroupRole(micUser2.getGroup_role());
            user.setNickname(micUser2.getNickname());
            user.setNoteName(micUser2.getNotes());
            if (null != BaseApplication.appContext) {
                Log.d(TAG, "refershMic() loading Mic 2 icon");
                GlideUtil.loadCircleImage(BaseApplication.appContext, user.getIcon(), onChatUserIcon2);
            }
        } else {
            Log.d(TAG, "refershMic() Mic 2 GONE");
            rlOnChat2.setVisibility(View.GONE);
            mBtnOnChat2.setVisibility(View.VISIBLE);
            onChatUserMic1.setVisibility(View.GONE);
        }
    }

    private void handlePrepareOnMic(Message msg) {
        CommonFunction.log(TAG, "handlePrepareOnMic() into");

        GroupChatMicUserMessage onBean = (GroupChatMicUserMessage) msg.obj;
        if (flyAudioRoom != null) {
            startPush();
            flyAudioRoom.setManageId(onBean.managerid);
            flyAudioRoom.setPosition(onBean.slot);
        }
    }

    private void handlePrepareOffMic(Message msg) {
        CommonFunction.log(TAG, "handlePrepareOffMic() into");

        GroupChatMicUserMessage offBean = (GroupChatMicUserMessage) msg.obj;
        stopPush();
        SocketGroupProtocol.offMicSuccess(this, getCurrentGroupId(), offBean.managerid);
    }

    /**
     * 更新私聊-未读消息
     */
    public void refershPrivateData() {
        String uid = String.valueOf(Common.getInstance().loginUser.getUid());
        final int noRead = ChatPersonalModel.getInstance().countNoRead(getActivity(), uid);
        if (noRead > 0) {
            rlNewMessage.setVisibility(View.VISIBLE);
            dragPointView.setVisibility(View.VISIBLE);
            if (noRead <= 99) {
                dragPointView.setText(String.valueOf(noRead));
            } else {
                dragPointView.setText("99+");
            }
        } else {
            rlNewMessage.setVisibility(View.GONE);
            dragPointView.setVisibility(View.GONE);
        }

    }

    /***
     * 转换成User对象
     */
    private void refreshGroupUserToUser(GroupUser groupUser) {
        recieveUser = new User();
        recieveUser.setUid(groupUser.getUserid());
        recieveUser.setNickname(groupUser.getNickname());
        recieveUser.setIcon(groupUser.getIcon());
        recieveUser.setViplevel(groupUser.getViplevel());
        recieveUser.setAge(groupUser.getAge());
        recieveUser.setSVip(groupUser.getSvip());
        recieveUser.setSex("m".equals(groupUser.getGender()) ? 1 : 2);
        recieveUser.setGroupRole(groupUser.getGroup_role());
        recieveUser.setDistance(groupUser.getDistance());
    }

    private HashSet<Long> mMessageIdSet = new HashSet<Long>();

    private void groupInfoBtnClick(boolean isJoin) {
        if (mGroupInfo == null)
            return;
        if (mContext == null)
            mContext = BaseApplication.appContext;
        Intent intent = new Intent(mContext, GroupInfoActivity.class);
        intent.putExtra(GroupInfoActivity.GROUPID, getCurrentGroupId());
        intent.putExtra(GroupInfoActivity.CREATEID, mGroupInfo.getCreatorId());
        intent.putExtra(GroupInfoActivity.CREATEORICONURL, mGroupInfo.getCreatorIcon());
        intent.putExtra(GroupInfoActivity.FROMSEARCH, false);
        intent.putExtra(GroupInfoActivity.ISJOIN, isJoin);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, REQUEST_CODE_GROUPINFO);
    }

    protected void soundTextSwitcherClick() {
        if (!GroupChatListModel.getInstance()
                .checkGroupSay(mActivity, getCurrentGroupId(), this)) { // 是否被圈子禁言
            hideKeyboard();
            updateSwitcherBtnState();
        }
    }

    @Override
    protected void toolBtnClick() {
        if (!CommonFunction.forbidSay(mActivity)) { // 是否被禁言
            clickMenu(2);
        }

    }

    @Override
    protected void faceBtnClick() {
        if (!CommonFunction.forbidSay(mActivity)) { // 是否被禁言
            clickMenu(1);
        }

    }

    static class ChatbarSendPersonalSocketListenerImpl implements ChatbarSendPersonalSocketListener {
        private WeakReference<GroupChatTopicActivity> mActivity;

        public ChatbarSendPersonalSocketListenerImpl(GroupChatTopicActivity activity) {
            mActivity = new WeakReference<GroupChatTopicActivity>(activity);
        }

        @Override
        public void update(boolean mTimerIsEnd, ChatBarBackpackBean.ListBean selectGiftBean) {
            GroupChatTopicActivity activity = mActivity.get();
            if (activity != null) {
                if (Build.VERSION.SDK_INT >= 17) {
                    if (activity.isDestroyed()) {
                        return;
                    }
                }
                activity.mHandler.postDelayed(activity.runnableHandle, 100);
            }
        }
    }

    /***
     * 礼物按钮点击事件
     */
    @Override
    protected void giftBtnClick() {
        CommonFunction.log(TAG, "giftBtnClick() into");
        user = new User();
        if (micUser1 == null || micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
            if (micUser2 == null || micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {//2麦用户为空，给吧主送礼
                if (groupOwnerUser != null && groupOwnerUser.getUserid() != Common.getInstance().loginUser.getUid()) {
                    refreshGroupUserToUser(groupOwnerUser);
                    if (recieveUser != null) {
                        CustomGiftDialogNew.jumpIntoCustomGiftDia(GroupChatTopicActivity.this, recieveUser, Long.valueOf(getCurrentGroupId()), luxuryGiftView, new ChatbarSendPersonalSocketListenerImpl(this));
                    }
                } else {
//                    Toast.makeText(GroupChatTopicActivity.this,getResString(R.string.chatbar_gift_send_gift_to_self_error), Toast.LENGTH_SHORT).show();
                    if (groupOwnerUser != null)
                        refreshGroupUserToUser(groupOwnerUser);
                    if (recieveUser != null) {
                        CustomGiftDialogNew.jumpIntoCustomGiftDia(GroupChatTopicActivity.this, recieveUser, Long.valueOf(getCurrentGroupId()), luxuryGiftView, new ChatbarSendPersonalSocketListenerImpl(this));
                    }
                }
                luxuryGiftView.setHeightPosition(5);

            } else {//给2麦送礼
                refreshGroupUserToUser(micUser2);
                if (recieveUser != null) {
                    CustomGiftDialogNew.jumpIntoCustomGiftDia(GroupChatTopicActivity.this, recieveUser, Long.valueOf(getCurrentGroupId()), luxuryGiftView, new ChatbarSendPersonalSocketListenerImpl(this));
                    luxuryGiftView.setHeightPosition(5);
                }
            }
        } else {
            //获取1麦上用户
            refreshGroupUserToUser(micUser1);
            if (recieveUser != null) {
                CustomGiftDialogNew.jumpIntoCustomGiftDia(GroupChatTopicActivity.this, recieveUser, Long.valueOf(getCurrentGroupId()), luxuryGiftView, new ChatbarSendPersonalSocketListenerImpl(this));
            }
        }
    }

    /***
     * 是否显示更过功能键
     */
    @Override
    protected int showMoreMenuBtn() {
        super.showMoreMenuBtn();
        return 0;

    }

    /**
     * 成功上麦消息
     *
     * @param message
     */
    private void receiveOnMicMsgSucc(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_ON_MIC_MSG_SUCC, bean);
        mHandler.sendMessage(msg);

    }

    /**
     * 上麦失败
     *
     * @param message
     */
    private void receiveOnMicFail(TransportMessage message) {
        CommonFunction.log(TAG, "receiveOnMicFail() into");
        stopPush();
        try {
            Map<String, Object> map = getKickUserFail(message);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_ON_MIC_FAIL, map);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 成功下麦消息
     *
     * @param message
     */
    private void receiveOffMicMsgSucc(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);

        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_OFF_MIC_MSG_SUCC, bean);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * 下麦失败
     *
     * @param message
     */
    private void receiveOffMicFail(TransportMessage message) {
//        if (rtmpRecorder.isRecorderRunning())
//            rtmpRecorder.stop();
        try {
            Map<String, Object> map = getKickUserFail(message);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_OFF_MIC_FAIL, map);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 聊吧用户上线
     *
     * @param message
     */
    private void receiveOnLineMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatTopicUserMessage bean = GsonUtil.getInstance()
                .getServerBean(json, GroupChatTopicUserMessage.class);
        if (bean == null) {
            return;
        }
        String groupIdStr = String.valueOf(bean.groupid);
        //CommonFunction.log(TAG,bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_ON_LINE_RESULT, bean);
            mHandler.sendMessage(msg);
            Message msgIsComing = mHandler.obtainMessage(HandleMsgCode.MSG_ON_LINE_IS_COMEING, bean);
            mHandler.sendMessage(msgIsComing);
        }
    }

    /**
     * 聊吧用户下线
     *
     * @param message
     */
    private void receiveOffLineMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatTopicUserMessage bean = GsonUtil.getInstance()
                .getServerBean(json, GroupChatTopicUserMessage.class);

        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_OFF_LINE_RESULT, bean);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * 聊吧用户上麦
     *
     * @param message
     */
    private void receiveOnMicMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatTopicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatTopicUserMessage.class);

        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_ON_MIC_RESULT, bean);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * 聊吧用户下麦
     *
     * @param message
     */
    private void receiveOffMicMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatTopicUserMessage bean = GsonUtil.getInstance()
                .getServerBean(json, GroupChatTopicUserMessage.class);

        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_OFF_MIC_RESULT, bean);
            mHandler.sendMessage(msg);
        }

    }


    /**
     * 聊吧管理员消息
     *
     * @param message
     */
    private void receiveManagerMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupNoticeListBean bean = GsonUtil.getInstance().getServerBean(json, GroupNoticeListBean.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_ADMIN_MESSAGE_RESULT, bean);
        mHandler.sendMessage(msg);

    }

    /**
     * 告知用户准备上麦
     *
     * @param message
     */
    private void receivePrepareOnMicMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);
        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_PREPARE_ON_MIC_MESSAGE_RESULT, bean);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 告知用户准备下麦
     *
     * @param message
     */
    private void receivePrepareOffMicMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatMicUserMessage bean = GsonUtil.getInstance().getServerBean(json, GroupChatMicUserMessage.class);
        String groupIdStr = String.valueOf(bean.groupid);

        if (groupIdStr.equals(getCurrentGroupId())) {
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_PREPARE_OFF_MIC_MESSAGE_RESULT, bean);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 告知用户准备下麦
     *
     * @param message
     */
    private void receiveUpdateMicMsgResult(TransportMessage message) {
        String json = message.getContentBody();
        GroupChatUpdateMicMessage bean = GsonUtil.getInstance()
                .getServerBean(json, GroupChatUpdateMicMessage.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_UPDATE_MIC_MESSAGE_RESULT, bean);
        mHandler.sendMessage(msg);

    }

    /**
     * 指定用户断流
     *
     * @param message
     */
    private void receiveStopPushResult(TransportMessage message) {
        try {
            Map<String, Object> map = getKickUserFail(message);
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_STOP_PUSH_RESULT, map);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleAudioSendEndFail(String result) {
        try {
            JSONObject json = new JSONObject(result);
            long flag = json.optLong("flag");
            backManager.RemoveSendThread(flag);

            Message msg = new Message();
            Bundle b = new Bundle();
            b.putLong("flag", flag);
            b.putInt("e", 0);
            msg.what = HandleMsgCode.MSG_SEND_MESSAGE;
            msg.setData(b);
            mHandler.sendMessage(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendQuickText(String msg) {
        sendMsg(getCurrentFlag(), MessageType.TEXT, "", "", msg);
    }

    @Override
    protected ChatMessageBean composeChatMessageBean(ChatRecord record) {

        GroupChatInfo chatInfo = new GroupChatInfo();
        chatInfo.setGroupId(getTargetID());
        chatInfo.setGroupIcon(getGroupInfo().getGroupIcon());
        chatInfo.setGroupName(getGroupInfo().getGroupName());

        EditText etInput = getEditContent();
        String mReply = "";
        if (etInput != null) {
            int end = etInput.getText().toString().length();
            AtSpan[] spanStrs = etInput.getText().getSpans(0, end, AtSpan.class);
            for (AtSpan spanItem : spanStrs) {
                mReply += spanItem.getUserid() + ",";
            }

            if (mReply.endsWith(",")) {
                mReply = mReply.substring(0, mReply.length() - 1);
            }
        }
        chatInfo.setReply(mReply);

        ChatMessageBean messageBean = new ChatMessageBean();
        messageBean.chatRecord = record;
        messageBean.chatType = ChatMessageBean.GROUP_CHAT;
        messageBean.targetInfo = chatInfo;

        if (record.getType() == String.valueOf(ChatRecordViewFactory.IMAGE)) {
            messageBean.resourceType = FileUploadType.PIC_GROUP_CHAT;
        } else if (record.getType() == String.valueOf(ChatRecordViewFactory.VIDEO)) {
            messageBean.resourceType = FileUploadType.VIDEO_GROUP_CHAT;
        }

        return messageBean;
    }

    public static class GroupInfo {
        private String groupId;
        private String groupIcon;
        private String groupName;
        private long creatorId;
        private String creatorIcon;
        private int groupRole;
        private boolean lotterySwitch;
        private boolean shareSwitch;

        //YC
        /**
         * 当前聊吧人数
         */
        private long chabarNum;

        public long getGroupMember() {
            return chabarNum;
        }

        public void setGroupMember(long chabarNum) {
            this.chabarNum = chabarNum;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getGroupIcon() {
            return groupIcon;
        }

        public void setGroupIcon(String groupIcon) {
            this.groupIcon = groupIcon;
        }

        public long getCreatorId() {
            return creatorId;
        }

        public void setCreatorId(long creatorId) {
            this.creatorId = creatorId;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getCreatorIcon() {
            return creatorIcon;
        }

        public void setCreatorIcon(String creatorIcon) {
            this.creatorIcon = creatorIcon;
        }

        public int getGroupRole() {
            return groupRole;
        }

        public void setGroupRole(int groupRole) {
            this.groupRole = groupRole;
        }

        public boolean isLotterySwitch() {
            return lotterySwitch;
        }

        public void setLotterySwitch(boolean lotterySwitch) {
            this.lotterySwitch = lotterySwitch;
        }

        public boolean isShareSwitch() {
            return shareSwitch;
        }

        public void setShareSwitch(boolean shareSwitch) {
            this.shareSwitch = shareSwitch;
        }
    }

    /**
     * 更新发送中消息的状态，超时的认为是失败处理
     */
    private void updateMessageStatus() {
        long nowTime = System.currentTimeMillis() + Common.getInstance().serverToClientTime;
        long dateTime = nowTime - TIME_OUT_LIMIT;
        String mUid = String.valueOf(UserId);
        String gourpId = String.valueOf(getTargetID());
        GroupChatListModel.getInstance().updateMessageSendingStatus(mContext, mUid, gourpId, dateTime);
    }

    public GroupInfo getGroupInfo() {
        return mGroupInfo == null ? new GroupInfo() : mGroupInfo;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
            //监听到软键盘弹起

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    lyQuickSend.setVisibility(View.VISIBLE);
                    giftMenuBtn.setVisibility(View.GONE);
                    faceMenuBtn.setVisibility(View.VISIBLE);
                    rlNewMessage.setVisibility(View.GONE);
                    btn_chat_bar_world_message.setVisibility(View.GONE);
                    btTask.setVisibility(View.GONE);
                    btnLuckpan.setVisibility(View.GONE);
                    btShare.setVisibility(View.GONE);

                    String content = editContent.getText().toString().trim();
                    if (content.length() > 0) {
                        mBtnSend.setVisibility(View.VISIBLE);
                    }

                }
            });


        } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
            //监听到软件盘关闭

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    hideKeyboard();
                    lyQuickSend.setVisibility(View.GONE);
                    giftMenuBtn.setVisibility(View.VISIBLE);
                    faceMenuBtn.setVisibility(View.GONE);
                    btn_chat_bar_world_message.setVisibility(View.VISIBLE);
                    btTask.setVisibility(View.VISIBLE);
                    btnLuckpan.setVisibility(View.VISIBLE);
                    mBtnSend.setVisibility(View.GONE);
//                    btShare.setVisibility(View.VISIBLE);
                    refershPrivateData();

                    if (mGroupInfo != null) {
                        boolean shareSwitch = mGroupInfo.isShareSwitch();
                        if (false == shareSwitch) {
                            btShare.setVisibility(View.GONE);
                        } else {
                            btShare.setVisibility(View.VISIBLE);
                        }
                    }


                }
            });

        }
    }

    /**
     * View的容器
     */
    private FrameLayout mViewContainer;

    /**
     * 圈聊界面
     */
    private FrameLayout mChatView;
    /**
     * 聊天记录的列表视图
     */
    private View headView;
    private ChatPersionPullDownView pullDownView;
    /** View end **/

    /** 协议请求flag **/
    /**
     * 请求群管理员ID列表的Flag
     */
    private long requestGroupManagerIdFlag;

    /**
     * 请求聊吧拉取资料接口的Flag
     */
    private long requestGroupSimpleFlag;

    /**
     * 是否已初始化聊天数据
     */
    private boolean isInitChatData = false;
    /**
     * 当前的视图模式
     */
    private int mCurViewMode;

    private GroupInfo mGroupInfo;

    /**
     * 是否圈聊（默认true）
     */
    private boolean mIsChat = true;

    /**
     * 加载更多聊天记录的数目
     */
    private static final int LOAD_MORE_NUM = 15;

    private Dialog mWaitDialog;

    private static final int REQUEST_CODE_GROUPINFO = 0x100;// 跳转到圈资料的请求码
    private static final int REQUEST_CODE_CHATPERSONAL = 0x200;// 跳转到圈资料的请求码

    private HashMap<String, HashMap<String, Object>> becomeManagerReqMap;
    private HashMap<String, HashMap<String, Object>> cancleManagerReqMap;

    /**
     * 聊吧面板头布局
     *
     */
    /**
     * 圈子头像和昵称，在线人数
     */
    private RelativeLayout mRlGroupInfo;
    private ImageView mIvGroupIcon;
    private TextView mTvGroupName;
    private TextView mTvGroupMember;
    private Button mBtnGroupInfo;

    /**
     * 关注按钮
     */
    private TextView mBtnAttention;

    private Button mBtnOnChat1;
    private ImageView onChatUserIcon1; //用户头像
    private RelativeLayout rlOnChat1; //用户头像小喇叭容器
    private ImageView onChatUserMic1; //小喇叭

    private Button mBtnOnChat2;
    private ImageView onChatUserIcon2;
    private RelativeLayout rlOnChat2;
    private ImageView onChatUserMic2; //小喇叭
    /**
     * 闭麦/开麦按钮
     */
    private Button mBtnCloseWheat;
    private Button mBtnOpenWheat;
    /**
     * 查看更多成员列表
     */
    private Button mBtnLookMore;

    /**
     * 在线人数列表展示
     */
    private RecyclerView mOnLineNumber;
    /**
     * 标题栏
     */
    private TextView mTvTitle;
    /**
     * 返回键范围
     */
    private FrameLayout flBack;

    /**
     * 在线人数适配器
     */
    private GroupChatTopicOnLineAdapter onLineNumberAdapter;

    private List<GroupUser> mLineNumberDatas;

    /**
     * 关注状态
     */
    private boolean attentionFlag = false;

    /**
     * 获取用户信息的flag
     */
    private long getUserInfoFlag;
    private static final int GET_USER_ICON_FLAG = 10001;

    /**
     * 关注聊吧的flag
     */
    private long getAttentionFlag;
    private static final int GET_ATTENTION_STATUS = 10002;

    /**
     * 头像弹窗
     */
    private CustomChatBarDialog customChatBarDialog;

    /**
     * 查看更多成员对话框
     */
    private CustomChatbarMemberDialog customCharbarMemberDialog;
    /**
     * 聊吧面板顶部背景图
     */
    private ImageView ivChatbarbg = null;

    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    /**
     * 麦上用户
     */
    private GroupUser micUser1;

    /**
     * 麦上用户
     */
    private GroupUser micUser2;
    /**
     * 当前用户
     */
    private GroupUser currentUser = new GroupUser();
    /**
     * 聊吧吧主
     */
    private GroupUser groupOwnerUser = new GroupUser();

    /**
     * 在线总人数
     */
    private long onLineNumber;

    /**
     * 获取在线用户的flag
     */
    private long getOnLineMemberFlag;

    /**
     * 初始化
     */
    public GroupChatTopicActivity instant;

    /**
     * 声音状态
     */
    private boolean isVoice;

    /**
     * 发送世界消息的Flag
     */
    private long sendWorldMessageFlag;

    /***
     * 暂时的历史消息的入口
     */
    private Button btnHistoryMsg;

    /**
     * 技能使用弹框
     */
    private ReceivedSkillDialog receivedSkillDialog;
    private long requestSkillOpenFlag = -1;

    /**
     * 当发送礼物消息成功之后，发送一条私聊消息
     */
    private void sendGiftMessage(int fUid, int giftNum, boolean isShow) {
        CommonFunction.log(TAG, "sendGiftMessage() 发送礼物消息成功之后，发送私聊消息 into ");
        if (lastUserGiftId != pacageGiftBean.getGift().getUser_gift_id()) {
            lastUserGiftId = 0;
        }
        User mUser = null;
        if (mCurrentGift != null && isShow) {
            giftUrl = mCurrentGift.getIconUrl();
            mUser = new User();
            mUser.setUid(fUid);
            mUser.setIcon(pacageGiftBean.getReceive_user().getIcon());
            mUser.setNickname(pacageGiftBean.getReceive_user().getNickname());
            mUser.setViplevel(pacageGiftBean.getReceive_user().getViplevel());
            mUser.setSVip(pacageGiftBean.getReceive_user().getVip());
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            map.put("giftname", mCurrentGift.getName(BaseApplication.appContext));
            map.put("charmnum", String.valueOf(mCurrentGift.getCharisma() * giftNum));
            String price = "";
            price = String.valueOf(pacageGiftBean.getGift().getGift_price() * giftNum);
            map.put("price", price);
            map.put("currencytype", String.valueOf(mCurrentGift.getCurrencytype()));
            map.put("giftnum", giftNum);
            map.put("exp", mCurrentGift.expvalue * giftNum);
            map.put("isFromChatRoom", 1 + "");
            map.put("gift_desc", mCurrentGift.getGiftdesc(BaseApplication.appContext));
            String content = JsonUtil.mapToJsonString(map);
            mReqSendMsgFlag = System.currentTimeMillis();
            CommonFunction.log(TAG, "call SocketSessionProtocol.sessionPrivateMsg()");
            long flag = SocketSessionProtocol.sessionPrivateMsg(BaseApplication.appContext, mReqSendMsgFlag, fUid, mtype, String.valueOf(ChatRecordViewFactory.GIFE_REMIND), giftUrl, from, content);

            if (flag == -1) {
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
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

            if (mCurrentGift == null) {
                mCurrentGift = new Gift();
            }
            mChatRecord.setAttachment(mCurrentGift.getIconUrl() == null ? "" : mCurrentGift.getIconUrl());
            mChatRecord.setContent(content);
            mChatRecord.setUpload(false);
            mChatRecord.setfLat(mUser.getLat());
            mChatRecord.setfLng(mUser.getLng());
            GeoData geo = LocationUtil.getCurrentGeo(BaseApplication.appContext);
            if (user != null)// 防止用户对象
                mChatRecord.setDistance(LocationUtil.calculateDistance(user.getLng(), user.getLat(), geo.getLng(), geo.getLat()));

        } else {
            if (componentBeenList.isEmpty()) {
                return;
            }
            for (int i = 0; i < componentBeenList.size(); i++) {
                mTemporyGift = componentBeenList.get(i).getGift();
                int num = componentBeenList.get(i).getGiftNum();
                giftNum = num;
                if (mTemporyGift != null) {
                    giftUrl = mTemporyGift.getIconUrl();
                    mUser = new User();
                    mUser.setUid(fUid);
                    mUser.setIcon(pacageGiftBean.getReceive_user().getIcon());
                    mUser.setNickname(pacageGiftBean.getReceive_user().getNickname());
                    mUser.setViplevel(pacageGiftBean.getReceive_user().getViplevel());
                    mUser.setSVip(pacageGiftBean.getReceive_user().getVip());
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("giftname", mTemporyGift.getName(GroupChatTopicActivity.this));

                    map.put("charmnum", String.valueOf(mTemporyGift.getCharisma() * giftNum));
                    String price = "";

                    price = String.valueOf(pacageGiftBean.getGift().getGift_price() * giftNum);
                    map.put("price", price);
                    map.put("currencytype", String.valueOf(mTemporyGift.getCurrencytype()));
                    map.put("giftnum", giftNum);
                    map.put("exp", mTemporyGift.expvalue * giftNum);
                    map.put("isFromChatRoom", 1 + "");
                    map.put("gift_desc", mTemporyGift.getGiftdesc(GroupChatTopicActivity.this));
                    String content = JsonUtil.mapToJsonString(map);
                    mReqSendMsgFlag = System.currentTimeMillis();
                    CommonFunction.log(TAG, "call SocketSessionProtocol.sessionPrivateMsg()");
                    long flag = SocketSessionProtocol.sessionPrivateMsg(this, mReqSendMsgFlag, fUid, mtype, String.valueOf(ChatRecordViewFactory.GIFE_REMIND), giftUrl, from, content);

                    //发送世界消息 有条件
                    if (!TextUtils.isEmpty(price)) {
                        int priceNum = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(SharedPreferenceUtil.GIFT_DIAMOND_MIN_NUM);
                        // 礼物消息是金币类型的不发世界消息
                        if (pacageGiftBean.getGift().getGift_currencytype() != 1 & Integer.parseInt(price) > priceNum) {
                            String str = getString(R.string.chat_bar_send_gift_world_message) + "@" + giftNum + mTemporyGift.getGiftdesc(GroupChatTopicActivity.this) + mTemporyGift.getName(GroupChatTopicActivity.this) + getString(R.string.chat_bar_send_gift_consume_much);
                            WorldMessageGiftContent worldMessageGiftContent = new WorldMessageGiftContent();
                            worldMessageGiftContent.message = str;
                            worldMessageGiftContent.targetUserName = mUser.getNickname();
                            String message = JSON.toJSONString(worldMessageGiftContent);
                            SendWorldMessageProtocol.getInstance().getSendWorldMessageData(BaseApplication.appContext, Integer.parseInt(groupId), message, 32, new ChatBarHttpCallback(this));
                        }
                    }

                    if (flag == -1) {
                        mHandler.sendEmptyMessage(HandleMsgCode.MSG_WHAT_REFRESH_MINE_SOCKET_SEND);
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
                    GeoData geo = LocationUtil.getCurrentGeo(BaseApplication.appContext);
                    if (user != null)// 防止用户对象
                        mChatRecord.setDistance(LocationUtil.calculateDistance(user.getLng(), user.getLat(), geo.getLng(), geo.getLat()));
                }
                int subGroup;
                if (mtype == 0) {
                    subGroup = SubGroupType.NormalChat;
                } else {
                    subGroup = SubGroupType.SendAccost;
                }
                ChatPersonalModel.getInstance().insertOneRecord(mActivity, mUser, mChatRecord, subGroup, from);
                mTemporyGift = null;
            }
            componentBeenList.clear();
        }
    }

    public void saveGroupId(String groupId, String userId, long enterTime, int saveType) {
        if (saveType == 1) {
            String groupIdStrLast = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_GROUP_ID);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(groupIdStrLast);
            stringBuilder.append("iaround" + groupId);
            stringBuilder.append("#" + enterTime + "#" + userId + "&");
            String groupIdStr = stringBuilder.toString();
            SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putString(SharedPreferenceUtil.CHAT_BAR_WELCOME_GROUP_ID, groupIdStr);
        } else if (saveType == 2) {
            String groupIdStrLast = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(groupIdStrLast);
            stringBuilder.append("iaround" + groupId);
            stringBuilder.append("#" + enterTime + "#" + userId + "&");
            String groupIdStr = stringBuilder.toString();
            SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER, groupIdStr);
        }

    }

    /**
     * @param groupId
     * @param userId
     * @param saveType 1,提示语 2，以吧主身份发送
     * @return
     */
    public long saveGrouidToSharePrerence(String groupId, String userId, int saveType) {
        long time = System.currentTimeMillis();//系统的当前时间
        if (saveType == 1) {
            String groupIdStr = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_GROUP_ID);
            if (groupIdStr.contains(groupId)) {
                long groupIdTime = getCorrespondTime(groupId, userId, groupIdStr, saveType);
                return groupIdTime;
            } else {
                saveGroupId(groupId, userId, time, saveType);
                return 0;
            }
        } else {
            String groupIdStr = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER);
            if (groupIdStr.contains(groupId)) {
                long groupIdTime = getCorrespondTime(groupId, userId, groupIdStr, saveType);
                return groupIdTime;
            } else {
                saveGroupId(groupId, userId, time, saveType);
                return 0;
            }
        }

    }

    /**
     * 传入groupId，获得time时间戳
     */
    private long getCorrespondTime(String groupId, String userId, String groupIdStr, int saveType) {

        String[] values = groupIdStr.split("&");//id#128973179#12355&id#7861826387 获取一组对应的grouid跟时间戳
        long currentTime = System.currentTimeMillis();

        if (saveType == 1) {
            for (int i = 0; i < values.length; i++) {
                String[] split = values[i].split("#");
                if (split != null && split.length > 2) {
                    String groupid = split[0];
                    String time = split[1];
                    String userid = split[2];
                    if (currentTime - Long.parseLong(time) > 60 * 1000) {
                        handleToDeleteData(groupid, Long.parseLong(userid), Long.parseLong(time), currentTime, saveType);
                    }
                    if (("iaround" + groupId).equals(groupid) && userid.equals(userId)) {

                        return Long.parseLong(time);
                    }
                }
            }
            return currentTime;
        } else if (saveType == 2) {
            for (int i = 0; i < values.length; i++) {
                String[] split = values[i].split("#");
                if (split != null && split.length > 2) {
                    String groupid = split[0];
                    String time = split[1];
                    String userid = split[2];
                    if (currentTime - Long.parseLong(time) > 60 * 60 * 1000) {
                        handleToDeleteData(groupid, Long.parseLong(userid), Long.parseLong(time), currentTime, saveType);
                    }
                    if (("iaround" + groupId).equals(groupid) && userid.equals(userId)) {

                        return Long.parseLong(time);
                    }
                }
            }
        }


        return currentTime;
    }

    public void handleToInsertChatRecordByOwner() {
        long currentTime = System.currentTimeMillis();
        long time = saveGrouidToSharePrerence(groupId, Common.getInstance().loginUser.getUid() + "", 2);
        if (currentTime - time > 60 * 60 * 1000) {
            if (groupOwnerUser == null) return;
            if (currentUser.getUserid() != groupOwnerUser.getUserid()) {
                String userNickName = Common.getInstance().loginUser.getNickname();
                if (TextUtils.isEmpty(userNickName)) {
                    userNickName = Common.getInstance().loginUser.getUid() + "";
                }
                if (CommonFunction.isEmptyOrNullStr(welcomeText)) return;
                //gh 进入聊吧欢迎语（名字后加空格）
                ChatRecord record = composeRecordForSend(getCurrentFlag(), groupOwnerUser, MessageType.TEXT, "", "", "@" + userNickName + "  " + welcomeText);
                ChatMessageBean chatMessageBean = composeChatMessageBean(record);
                backManager.saveToDatabaseWelcome(chatMessageBean);
                saveRecordToBuffer(record);
                showListBottom();
            }

        } else {
            String groupIdStr = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER);
            //保存最新的groupId 跟时间戳
            if (!groupIdStr.contains(groupId)) {
                saveGroupId(groupId, Common.getInstance().loginUser.getUid() + "", currentTime, 2);
            }
        }

    }

    private void handleToDeleteData(String groupId, long userId, long time, long currentTime, int saveType) {
        if (saveType == 1) {
            String groupIdStr = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_GROUP_ID);
            String currentGroupStr = groupIdStr.replace(groupId + "#" + time + "#" + userId + "&", "");
            SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putString(SharedPreferenceUtil.CHAT_BAR_WELCOME_GROUP_ID, currentGroupStr);
            saveGroupId(groupId, userId + "", currentTime, 1);
        } else {
            String groupIdStr = SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).getString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER);
            String currentGroupStr = groupIdStr.replace(groupId + "#" + time + "#" + userId + "&", "");
            SharedPreferenceUtil.getInstance(GroupChatTopicActivity.this).putString(SharedPreferenceUtil.CHAT_BAR_WELCOME_BY_OWNER, currentGroupStr);
            saveGroupId(groupId, userId + "", currentTime, 2);

        }
    }

    private User converOnlineGroupUserToUser(GroupUser groupUser) {
        if (groupUser != null) {
            User user = new User();
            user.setUid(groupUser.getUserid());
            user.setIcon(groupUser.getIcon());
            user.setViplevel(groupUser.getViplevel());
            user.setSVip(groupUser.getSvip());
            user.setAge(groupUser.getAge());
            user.setNickname(groupUser.getNickname());
            user.setGroupRole(groupUser.getGroup_role());
            user.setSex(convertGenderToSex(groupUser.getGender()));
            return user;
        }
        return null;
    }

    private int convertGenderToSex(String gender) {
        if ("m".equals(gender)) {
            return 1;

        } else if ("f".equals(gender)) {
            return 2;

        } else {
            return 1;
        }

    }

    @Override
    public void headViewWorldMessageClick(User user) {
        // 创建者的id
        long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());

        //添加非空判断
        if (user == null) {
            return;
        }
        if (user.getUid() == Common.getInstance().loginUser.getUid()) {//点击的是自己
            user.setIcon(user.getIcon());
            user.setGroupRole(user.getGroupRole());
            user.setAudio(user.getAudio());
            customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
            customChatBarDialog.setUser(user, user.getGroupRole(), getCurrentGroupId());
            customChatBarDialog.isClickMySelf(true);
            customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
            customChatBarDialog.show();
        } else {
            if (user.getUid() == creatorUid) { // 是群的创建者
                // 吧主权限
                customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                customChatBarDialog.setUser(user, 0, getCurrentGroupId());
                customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                customChatBarDialog.show();
            } else if (user.getGroupRole() == 1) { // 为管理员
                // 管理员的权限
                customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                customChatBarDialog.setUser(user, 1, getCurrentGroupId());
                customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                customChatBarDialog.show();
            } else {
                customChatBarDialog = new CustomChatBarDialog(mContext, new ChatbarSendPersonalSocketListenerImpl(this));
                customChatBarDialog.setUser(user, 3, getCurrentGroupId());
                customChatBarDialog.setLuxuryGiftView(luxuryGiftView);
                customChatBarDialog.show();
            }
        }

    }

    @Subscribe
    public void onEvent(String event) {
        if (event.equals("update_skill_dialog")) {
            String json = Common.getInstance().getSkillResult();
            if (null != json && !TextUtils.isEmpty(json)) {
                handleSkillAttackResult(GsonUtil.getInstance().getServerBean(json, SkillAttackResult.class));
            }
        }
    }

    /* 抽奖回调接口
     * */
    public void onLuckBingo(int propType) {
        if (propType == 1) {
            //免费抽奖中奖
            if (luckpanFreeCount > 0) {
                luckpanFreeCount--;
            }
            //更新图标
            if (luckPanContainer.getVisibility() == View.VISIBLE) {
                if (luckpanFreeCount > 0) {
                    luckPanCountView.setText(String.valueOf(luckpanFreeCount));
                    luckPanCountView.setVisibility(View.VISIBLE);
                } else {
                    luckPanCountView.setVisibility(View.INVISIBLE);
                }
            }
        } else if (propType == 2) {
            //钻石抽奖中奖
            if (luckpanDiamondCount > 0) {
                luckpanDiamondCount--;
            }
        }
    }

    /* 分享成功后获得免费抽奖次数回调接口
     * */
    @Override
    public void onShareSuccess(int free) {
        //免费抽奖中奖
        if (free > 0) {
            luckpanFreeCount += free;
            //更新图标
            if (luckPanContainer.getVisibility() == View.VISIBLE) {
                if (luckpanFreeCount > 0) {
                    luckPanCountView.setText(String.valueOf(luckpanFreeCount));
                    luckPanCountView.setVisibility(View.VISIBLE);
                } else {
                    luckPanCountView.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 推荐技能升级
     */
    private void requestSkillUpdateList() {
        getSkillUpdateList = SkillRemoteDataSource.getSkillUpdate(this);
    }

    static class ChatBarHttpCallback implements HttpCallBack {
        private WeakReference<HttpCallBack> mCallback = null;

        public ChatBarHttpCallback(HttpCallBack callBack) {
            this.mCallback = new WeakReference<HttpCallBack>(callBack);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (mCallback.get() != null) {
                mCallback.get().onGeneralSuccess(result, flag);
                mCallback.clear();
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            if (mCallback.get() != null) {
                mCallback.get().onGeneralError(e, flag);
                mCallback.clear();
            }
        }
    }

    /* 聊吧最小化功能
     * 显示最小化窗口
     * */
    private void showZoomWindow() {
        CommonFunction.log(TAG, "showZoomWindow() into");
        //隐藏本页面
//        if(mChatBarWindowAdd != 0) {
//            CommonFunction.log(TAG, "showZoomWindow() remove activity window");
//            mChatBarWindowAdd = 0;
//            mChatBarWindowContentView.setVisibility(View.GONE);
//            mChatBarWindowLayoutParams.flags = mChatBarWindowLayoutParams.flags | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
//            mChatBarWindow.getWindowManager().updateViewLayout(mChatBarWindow.getDecorView(),mChatBarWindowLayoutParams);
//        }

        if (mNeedZoomChatbar == false) {
            if (flyAudioRoom != null) {
                flyAudioRoom.clearAudio();
            }
            CommonFunction.log(TAG, "showZoomWindow() 不需要最小化聊吧");
            finish();
            return;
        }

        finish();

        //弹出聊吧最小化悬浮窗
        if (groupId != null && ivChatbarbg != null) {
            CommonFunction.log(TAG, "showZoomWindow() to show zoom window");
            ChatBarZoomWindow zoomWindow = ChatBarZoomWindow.getInstance();
            Bitmap groupBitmap = null;
            if (null != ivChatbarbg) {
                Drawable bd = ivChatbarbg.getDrawable();
                if (bd != null) {
                    groupBitmap = ((BitmapDrawable) (bd)).getBitmap();
                } else {
                    groupBitmap = ((BitmapDrawable) (getResources().getDrawable(R.drawable.iaround_default_img))).getBitmap();
                }
            } else {
                groupBitmap = ((BitmapDrawable) (getResources().getDrawable(R.drawable.iaround_default_img))).getBitmap();
            }
            zoomWindow.init();
            String hot = String.valueOf(chatbarHot);
            zoomWindow.show(groupId, groupBitmap, chatbarName, hot);
        }
    }

    /* 聊吧最小化功能
     * 隐藏最小化窗口
     * */
    private void hideZoomWindow() {
        CommonFunction.log(TAG, "hideZoomWindow() into");
        boolean theSameChatbar = ChatBarZoomWindow.getInstance().dismiss(groupId);
        if (true == theSameChatbar) { //聊吧ID不变
            if (1 != fromchatbarwindowzoom) {
                CommonFunction.log(TAG, "hideZoomWindow() fromchatbarwindowzoom=1");
                fromchatbarwindowzoom = 1;
            }
        }
    }

    static class UserSpeakingState {
        public String user1;
        public boolean speaking1;
        public String user2;
        public boolean speaking2;
    }

    /**
     * 更新麦上用户波形
     * 用户说话中
     */
    @Override
    public void onUpdateSoundWave(String user1, boolean speaking1, String user2, boolean speaking2) {
        UserSpeakingState state = new UserSpeakingState();
        state.user1 = user1;
        state.speaking1 = speaking1;
        state.user2 = user2;
        state.speaking2 = speaking2;
        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_MIC_USER_SPEAKING_STATE_UPDATE, state);
        mHandler.sendMessage(msg);
    }

    /**
     * 网络观察者
     */
    protected final NetChangeObserver mNetChangeObserver = new NetChangeObserver() {
        @Override
        public void onNetConnected(CommonFunction.NetType type) {
            if (type == CommonFunction.NetType.WIFI) {
                CommonFunction.log(TAG, "wifi网络已连接");
            } else if (type == CommonFunction.NetType.TYPE_MOBILE) {
                CommonFunction.log(TAG, "移动网络已连接");
            }
//            callStateIdle();
        }

        @Override
        public void onNetDisConnect() {
            CommonFunction.log(TAG, "网络已经断开");
//            if (micUser1 != null) {
//                if (micUser1.getUserid() == Common.getInstance().loginUser.getUid()) {
//                    if (flyAudioRoom != null) {
//                        stopPush();
//                    }
//                }
//            }
//
//            if (micUser2 != null) {
//                if (micUser2.getUserid() == Common.getInstance().loginUser.getUid()) {
//                    if (flyAudioRoom != null) {
//                        stopPush();
//                    }
//                }
//            }
        }
    };
}
