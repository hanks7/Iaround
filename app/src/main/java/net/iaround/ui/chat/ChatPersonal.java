package net.iaround.ui.chat;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.conf.MessageType;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.connector.protocol.GameHttpProtocol;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.entity.PrivateChatInfo;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.chat.GameOrderMessageBean;
import net.iaround.model.chat.ManagerOrderResultBean;
import net.iaround.model.database.FriendModel;
import net.iaround.model.im.AccostRelationshipBean;
import net.iaround.model.im.AudioBaseBean;
import net.iaround.model.entity.ObtainInfoBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.ChatMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.DuibaBackBean;
import net.iaround.model.im.MaxReadIDBean;
import net.iaround.model.im.Me;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.RecentPlay;
import net.iaround.model.im.SocketFailWithFlagResponse;
import net.iaround.model.im.SocketSuccessResponse;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserBasic;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.type.ChatMessageType;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.ui.activity.AudioDetailsActivity;
import net.iaround.ui.activity.ChatSettingActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.activity.VideoDetailsActivity;
import net.iaround.ui.adapter.chat.ChatPersonalRecordAdapter;
import net.iaround.ui.chat.ChatPersionPullDownView.OnListViewTopListener;
import net.iaround.ui.chat.ChatPersionPullDownView.OnRefreshAdapterDataListener;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.PopMenu;
import net.iaround.ui.comon.PopMenu.PopMenuItemBean;
import net.iaround.ui.comon.ResizeLayout;
import net.iaround.ui.comon.RichTextView;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.Group;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.face.SendFaceToFriends;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.interfaces.PersonalSendGiftPopListenerDismiss;
import net.iaround.ui.interfaces.PersonalSendGiftPopListenerShow;
import net.iaround.ui.interfaces.SendPersonalSocketListener;
import net.iaround.ui.view.dialog.CustomGiftDialogNew;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.ui.view.popupwin.RefundPopupWindow;
import net.iaround.utils.ScreenUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


/**
 * <b>私聊界面</b><br/>
 * <br/>
 * 发送消息和接收消息
 *
 * @see SuperChat
 */
public class ChatPersonal extends SuperChat {
    private static final String TAG = "ChatPersonal";
    public static final int REQUEST_SPACEOTHER_JUMP = 1010;
    public static final int RESULT_ADDBLACK = 1009;

    public static final int RESULT_CLEAR_LIST = 1013;
    public static final int REQUEST_CHAT_SETTING = 1014;
    public static final int REQUEST_VIP = 1015;

    public long giftFlag;//发送完礼物后发送socket消息的flag
    public FrameLayout flSendGiftDialog;
    private int mPermissionRequestState = 0;

    /*View*/
    private ImageView mIvLeft, mIvRight;
    private FrameLayout flLeft;
    private TextView mTvTitle;
    private ResizeLayout rootLayout;
    private View headView;
    private RelativeLayout recentGame;
    private ChatPersionPullDownView pullDownView;
    private PopMenu llPageFilter;
    private Animation animDismiss;
    private Animation animShow;
    private LinearLayout chat_input_layout;

    // 阻断联系
    private LinearLayout blockChatLayout;
    private ImageView ivChatGift;
    private ImageView ivChatAttention;
    private TextView tvChatAttention;

    //订单状态头布局
    private TextView tvOrderStartService;//开始服务
    private TextView tvGameName;
    private TextView tvApplyRefund;//申请退款
    private TextView tvOrderComplete;//完成订单
    private ImageView ivOrderUnconfirmed;
    private View viewNotService;
    private ImageView ivNotService;
    private TextView tvNotService;
    private View viewInTheService;
    private ImageView ivInTheService;
    private TextView tvInTheService;
    private View viewComplete;
    private ImageView ivComplete;
    private TextView tvComplete;
    private View mOrderHint;
    private View mGamerHint;
    private TextView userGameName;
    private TextView tvLevel;
    private TextView chatUserGamePrice;
    private TextView tvMakeOrder;
    private TextView userGameHintClose;
    private RefundPopupWindow mWindow;
    private View mChatBarHint;//下单头布局

    private boolean isAnchor = false;//是否是主播 为了显示不同头布局
    private ObtainInfoBean.OrderInfo mOrderInfo;
    private ObtainInfoBean.GameInfo mGameInfo;


    /**
     * View end
     **/

    private User fUser;

    /**
     * 协议请求flag
     **/
    private long BACKLIST_FLAG = 100; // 列入黑名单的flag
    private long RECENT_GAME_FLAG = 101;// 最近玩过的游戏
    private long UPDATE_NOTE_FLAG = 102;// 上传新长备注
    private long GET_USER_INFO_FLAG = 103;// 上传新长备注
    private long GET_DUIBA_URL_FLAG = 104;// 获取兑吧最新URL

    private long AGREE_ORDER_FLAG;// 同意订单
    private long REFUSE_ORDER_FLAG;// 拒绝订单
    private long SEND_SERVIC_MSG_FLAG;// 发送服务邀请
    private long AGREE_SERVER_MSG_FLAG;// 同意主播服务
    private long FINISH_ORDER_FLAG;// 完成服务
    private long REFUND_CHECK_FLAG;// 用户申请退款
    private long AGREE_REFUND_FLAG;// 主播同意退款
    private long REFUSE_REFUND_FLAG;// 主播拒绝退款

    private long mGameOrderId;//订单ID

    private boolean isBackstage = false;// 是否处于后台

    private boolean firstEnterLongNote = true;// 是否为此次私聊的第一次进入长备注

    private static boolean isNewFollowFriend = false;// 是否第一次关注

    private static boolean isNeedRequestUserInfo = false;// 是否需要请求用户个人信息

    private static int ischat = 0;// 类型（int） 是否交流（0-否，1-有）

    private static int from;// 从哪里进入聊天 @ChatFromType

    private boolean fromChatBar = false; //是否从聊吧跳转到私聊页面
    private boolean needLoadUserGroupInfo = true; //是否需要加载用户所在的聊吧信息
    private long loadUserGroupRequestFlag = -1;

    private boolean quietMode = false;// 是否为悄悄查看状态

    private Dialog mProgressDialog;

    /**
     * 备注名字
     */
    private String notesName;

    private long UN_FOLLOW_USER_FLAG = 0; //取消关注请求标记
    private long FOLLOW_USER_FLAG = 0; //关注请求标记

    /**
     * 跳转到私聊界面
     *
     * @param mContext
     * @param user
     * @param requestCode >0 使用startActivityForResult
     * @param quietSee    是否为悄悄查看地进入
     * @param fromChatbar 是否是从聊吧页面呼起个人资料页再进私聊页面 （不敢用from因为是2级跳转）
     */
    public static void skipToChatPersonal(Activity mContext, User user, int requestCode,
                                          boolean isNewFollow, int from, boolean quietSee, boolean fromChatbar) {
        Intent intent = new Intent(mContext, ChatPersonal.class);
        intent.putExtra("fuid", user.getUid());
        intent.putExtra("fnickname", user.getNickname());
        intent.putExtra("fnotename", user.getNoteName());
        intent.putExtra("ficon", user.getIcon());
        intent.putExtra("fvip", user.getViplevel());
        intent.putExtra("fsvip", user.getSVip());
        intent.putExtra("flevel", user.getLevel());
        intent.putExtra("sex", user.getSexIndex());
        intent.putExtra("age", user.getAge());
        intent.putExtra("lat", user.getLat());
        intent.putExtra("lng", user.getLng());
        intent.putExtra("relation", user.getRelationship());
        intent.putExtra("signture", user.getSign());
        intent.putExtra("isnewfollow", isNewFollow);
        intent.putExtra("from", from);
        intent.putExtra("quietSee", quietSee);
        intent.putExtra("user", user);
        intent.putExtra("fromchatbar", fromChatbar);
        intent.putExtra("groups", user.getGroups());
        intent.putExtra("playgroup", user.getPlayGroup());

        if (requestCode > 0) {
            mContext.startActivityForResult(intent, requestCode);
        } else {
            mContext.startActivity(intent);
        }
        isNewFollowFriend = isNewFollow;
    }

    /**
     * 跳转到私聊界面
     *
     * @param mContext
     * @param user
     * @param quietSee    是否为悄悄查看地进入
     * @param fromChatbar 是否是从聊吧页面呼起个人资料页再进私聊页面 （不敢用from因为是2级跳转）
     */
    public static void skipToChatPersonal(Context mContext, User user, boolean isNewFollow, int from, boolean quietSee, boolean fromChatbar) {
        Intent intent = new Intent(mContext, ChatPersonal.class);
        intent.putExtra("fuid", user.getUid());
        intent.putExtra("fnickname", user.getNickname());
        intent.putExtra("fnotename", user.getNoteName());
        intent.putExtra("ficon", user.getIcon());
        intent.putExtra("fvip", user.getViplevel());
        intent.putExtra("fsvip", user.getSVip());
        intent.putExtra("flevel", user.getLevel());
        intent.putExtra("sex", user.getSexIndex());
        intent.putExtra("age", user.getAge());
        intent.putExtra("lat", user.getLat());
        intent.putExtra("lng", user.getLng());
        intent.putExtra("relation", user.getRelationship());
        intent.putExtra("signture", user.getSign());
        intent.putExtra("isnewfollow", isNewFollow);
        intent.putExtra("from", from);
        intent.putExtra("quietSee", quietSee);
        intent.putExtra("user", user);
        intent.putExtra("fromchatbar", fromChatbar);
        intent.putExtra("groups", user.getGroups());
        intent.putExtra("playgroup", user.getPlayGroup());
        mContext.startActivity(intent);
        isNewFollowFriend = isNewFollow;
    }


    public static void skipToChatPersonal(Activity mContext, User user, int requestCode,
                                          boolean isNewFollow, int from) {
        skipToChatPersonal(mContext, user, requestCode, isNewFollow, from, false, false);
    }

    /**
     * 跳转到私聊界面
     */
    public static void skipToChatPersonal(Activity mContext, User user, int requestCode) {
        skipToChatPersonal(mContext, user, requestCode, false, ChatFromType.UNKONW, false, false);
    }

    /**
     * 带悄悄查看参数跳转到私聊界面
     */
    public static void skipToChatPersonal(Context context, User user, boolean quietSee) {
        skipToChatPersonal((Activity) context, user, 0, false, ChatFromType.UNKONW, quietSee, false);
    }

    public static void skipToChatPersonal(Context mContext, User user) {
        skipToChatPersonal((Activity) mContext, user, ChatFromType.UNKONW);
    }

    public static void skipToChatPersonalReceiver(Context mContext, User user) {
        skipToChatPersonal(mContext, user, false, ChatFromType.UNKONW, false, false);
    }


    /**
     * 外部跳转,只通过用户id来请求用户的信息
     *
     * @param mContext
     * @param userId
     */
    public static void skipToChatPersonal(Context mContext, long userId) {
        Intent intent = new Intent(mContext, ChatPersonal.class);
        intent.putExtra("fuid", userId);
        mContext.startActivity(intent);

        isNeedRequestUserInfo = true;
    }

    /**
     * 获取当前聊天用户的id
     *
     * @return
     */
    public long getTargetID() {
        if (isNeedRequestUserInfo) {
            return getIntent().getLongExtra("fuid", 0);
        } else {
            return fUser != null ? fUser.getUid() : 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (v == flLeft || v == mIvLeft) {
            backBtnClick();
        } else if (v == mIvRight) {//私聊中弹窗，点击跳转到聊天设置界面
            llPageFilter.hide();
            Intent intent = new Intent();
            intent.putExtra("user", fUser);
            intent.setClass(this, ChatSettingActivity.class);
            startActivityForResult(intent, REQUEST_CHAT_SETTING);

        } else if (v.getId() == R.id.chat_infomation_tv) {
            // Ta的资料
            friendInfoBtnClick();
        } else if (v.getId() == R.id.chat_report_tv) {
            // 举报
            showTipDialog(R.id.chat_report_tv);
        } else if (v.getId() == R.id.chat_black_tv) {
            // 拉黑
            showTipDialog(R.id.chat_black_tv);
        } else if (v.getId() == R.id.chat_delete_tv) {
            // 清除历史记录
            showTipDialog(R.id.chat_delete_tv);
        } else if (v.getId() == R.id.tv_chat_personal_vip_privilege) {
            //充值VIP
            Intent vipIntent = new Intent(ChatPersonal.this, UserVIPActivity.class);
            startActivityForResult(vipIntent, REQUEST_VIP);
        } else if (v.getId() == R.id.rl_chat_gift) {
            //送礼
            CustomGiftDialogNew.jumpIntoCustomGiftDia(mContext, fUser, 1, mPersonalListener, mSendGiftPop, mSendGiftPopShow);
            hideMenu();

        } else if (v.getId() == R.id.rl_chat_attention) {
            //关注请求
            if (ivChatAttention.isSelected()) {
                //cancle
                updateRelation(ChatPersonal.this, 1, fUser, Constants.SECRET_SET_HADLE_TYPE_DELETE);
            } else {
                //add
                updateRelation(ChatPersonal.this, 2, fUser, Constants.SECRET_SET_HADLE_TYPE_ADD);
            }
        } else if (v.getId() == R.id.tv_order_start_service) {//开始服务
            SEND_SERVIC_MSG_FLAG = GameChatHttpProtocol.anchorSendServerMsg(mContext, mGameOrderId, this);
        } else if (v.getId() == R.id.tv_apply_refund) {//申请退款
            mWindow.showPopup();
            mWindow.setRefundReasonListener(new RefundPopupWindow.RefundReasonListener() {
                @Override
                public void onRefundReason(String reason, String otherReason) {
                    mWindow.dismissWindow();
                    showProgressDialog();
                    REFUND_CHECK_FLAG = GameChatHttpProtocol.refundCheck(mContext, mGameOrderId, reason, otherReason, ChatPersonal.this);

                }
            });
        } else if (v.getId() == R.id.tv_order_complete) {//完成
            DialogUtil.showOKCancelDialog(mContext, R.string.prompt, R.string.finish_order_tip, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showProgressDialog();
                    FINISH_ORDER_FLAG = GameChatHttpProtocol.finishServerMsg(mContext, mGameOrderId, ChatPersonal.this);

                }
            });

        } else {
            super.onClick(v);
        }
    }

    @Override
    public void delOneRecord(ChatRecord record) {
        String mUID = String.valueOf(UserId);
        String fUID = String.valueOf(getTargetID());
        String recordId = String.valueOf(record.getId());

        if (record.getSendType() == MessageBelongType.SEND) {
            ChatPersonalModel.getInstance().deleteRecordByLocalId(mContext, record.getLocid());
        } else {
            ChatPersonalModel.getInstance().deleteRecordByServerId(mContext, mUID, fUID, recordId);
        }

        delRecordByMsgId(mRecordList, record.getId());

        ChatPersonalModel.getInstance().updateLastMessage(mContext, fUser);
        updateList();
    }

    /**
     * 订单同意点击事件
     */
    public class OrderAgreeClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ChatRecord record = (ChatRecord) v.getTag();
            if (record == null || TextUtils.isEmpty(record.getAttachment())) {
                return;
            }
            GameOrderMessageBean bean = GsonUtil.getInstance().getServerBean(record.getAttachment(), GameOrderMessageBean.class);
            if (bean != null) {
                showProgressDialog();
                bean.orderStatus = 1;
                String json = GsonUtil.getInstance().getStringFromJsonObject(bean);
                record.setAttachment(json);
                if (bean.step == 1) {//第一步主播点击同意
                    AGREE_ORDER_FLAG = GameChatHttpProtocol.manageOrderResult(mContext, 1, bean.orderInfoId, record.getId(), ChatPersonal.this);
                } else if (bean.step == 3) {//第3步 用户点击同意服务开始
                    AGREE_SERVER_MSG_FLAG = GameChatHttpProtocol.userAgreeServerMsg(mContext, 1, bean.orderInfoId, record.getId(), ChatPersonal.this);
                } else if (bean.step == 5) {//第5步 主播点击同意退款
                    AGREE_REFUND_FLAG = GameChatHttpProtocol.agreeRefund(mContext, 1, bean.orderInfoId, record.getId(), ChatPersonal.this);
                }
            }

        }
    }

    /**
     * 订单拒绝点击事件
     */
    public class OrderRefuseClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            ChatRecord record = (ChatRecord) v.getTag();
            if (record == null || TextUtils.isEmpty(record.getAttachment())) {
                return;
            }
            GameOrderMessageBean bean = GsonUtil.getInstance().getServerBean(record.getAttachment(), GameOrderMessageBean.class);
            if (bean != null) {
                showProgressDialog();
                bean.orderStatus = 2;
                String json = GsonUtil.getInstance().getStringFromJsonObject(bean);
                record.setAttachment(json);
                if (bean.step == 1) {//第1步主播点击拒绝
                    REFUSE_ORDER_FLAG = GameChatHttpProtocol.manageOrderResult(mContext, 2, bean.orderInfoId, record.getId(), ChatPersonal.this);
                } else if (bean.step == 5) {//第5步 主播点击拒绝退款
                    REFUSE_REFUND_FLAG = GameChatHttpProtocol.agreeRefund(mContext, 2, bean.orderInfoId, record.getId(), ChatPersonal.this);
                }
            }
        }
    }

    /**
     * 礼物兑换提示框的点击相应
     *
     * @author zhengst
     */
    public class lCreditsClickListener implements OnClickListener {
        @Override
        public void onClick(View arg0) {
            GET_DUIBA_URL_FLAG = GoldHttpProtocol.autoLoginDuiba(mContext, "", new ChatPersonalHttpCallBack(ChatPersonal.this));
        }
    }

    /**
     * 用户头像点击响应
     *
     * @author chenlb
     */
    public class UserIconClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            User user = new User();
            hideKeyboard();
            hideMenu();

            ChatRecord record = (ChatRecord) v.getTag();//YC
            long userId = 0;
            if (record == null)
                return;
            if (record.getType() == String.valueOf(15)) {//技能消息过滤
                String content = record.getContent();
                if (content != null)
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String result = jsonObject.getString("content");
                        if (result != null) {
                            SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(result, SkillAttackResult.class);
                            if (record.getSendType() == MessageBelongType.SEND) {
                                userId = Common.getInstance().loginUser.getUid();
                            } else if (record.getSendType() == MessageBelongType.RECEIVE) {
                                userId = skillAttackResult.user.UserID;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            } else {
                userId = record.getSendType() == MessageBelongType.RECEIVE ? record.getFuid() : record.getUid();
            }

            user.setUid(userId);
            if (UserId == userId) {
                user.setIcon(record.getIcon());
                Intent intent = new Intent(mContext, UserInfoActivity.class);
                startActivity(intent);
                return;
            }
            user.setIcon(record.getfIconUrl());

            Activity activity = CloseAllActivity.getInstance().getSecondActivity();
            if (activity != null && activity instanceof OtherInfoActivity) {

                if (v.getContext() instanceof Activity) {
                    ((Activity) v.getContext()).finish();
                    return;
                }
            }
            Intent intent = new Intent(mContext, OtherInfoActivity.class);
            intent.putExtra("uid", userId);
            intent.putExtra("user", user);
            intent.putExtra("from", from);
            startActivity(intent);
        }
    }

    // 发消息回调
    @Override
    public void onSendCallBack(int e, long flag) {
        super.onSendCallBack(e, flag);

        Bundle b = new Bundle();
        b.putLong("flag", flag);
        b.putInt("e", e);

        Message msg = new Message();
        msg.what = HandleMsgCode.MSG_SEND_MESSAGE;
        msg.setData(b);

        if (mHandler != null) {
            mHandler.sendMessage(msg);
        }
    }


    @Override
    public void onReceiveMessage(TransportMessage message) {
        switch (message.getMethodId()) {
            case MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC: // 发送私聊消息：成功
                receiveSendMsgSucc(message);
                break;
            case MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL: // 发送私聊消息：失败
                receiveSendMsgFail(message);
                break;
            case MessageID.SESSION_PUSH_CHAT_PERSION_READED: // 接收对方已读私聊消息(最大的消息id)
                receiveFriendMaxReadId(message);
                break;
            case MessageID.SESSION_PRIVATE_CHAT: // 接收私聊信息
                receiveFriendMsg(message);
                break;
            case MessageID.SESSION_PRIVATE_AUDIO_BEGIN_FAIL:
                receiveAudioSendBeginFail(message);
                break;
            case MessageID.SESSION_PRIVATE_AUDIO_END_SUCCESS:
                receiveSendMsgSucc(message);
                handleAudioSendEndBack(message.getContentBody());
                break;
            case MessageID.SESSION_PRIVATE_AUDIO_END_FAIL:
                receiveSendMsgFail(message);
                handleAudioSendEndBack(message.getContentBody());
                break;
            case MessageID.SESSION_GET_ACCOST_RELATION_SUCCESS:
                TransportMessage msg = message;
                handleAccostRelation(msg);
                mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                break;
            case MessageID.SESSION_PUSH_ACCOST_RELATION:
                // 已经变成正常私聊关系
                ischat = 1;
                break;
            case MessageID.SESSION_PRIVATE_CHAT_RELATION:
                // 关系变更,变更为陌生人
                if (Common.getInstance().loginUser.getSVip() > 0) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(message.getContentBody());
                    long uid = jsonObject.getLong("userid");
                    int blockStaus = jsonObject.getInt("blockStaus");
                    fUser.setBlockStaus(blockStaus);
                    refershBlockView();
                    if (uid == fUser.getUid() && blockStaus == 0) {
                        ischat = 0;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onConnected() {
        super.onConnected();
        Log.d("Other", "Socked成功了");
    }

    // 处理下发的消息
    private void handleAccostRelation(TransportMessage msg) {
        AccostRelationshipBean bean = GsonUtil.getInstance().getServerBean(msg.getContentBody(), AccostRelationshipBean.class);

        ischat = bean.ischat;
        // 把关系插入到sharedPreference中
        ChatPersonalModel.getInstance().putAccostRelation(mContext, fUser.getUid(), bean.ischat);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backBtnClick();
            return true;
        } else if (KeyEvent.KEYCODE_MENU == keyCode) { // 按下菜单键
            clickMenu(0);
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void handleSelfMessage(Message msg) {

        switch (msg.what) {
            case HandleMsgCode.MSG_SEND_PRIVATE_CHAT_SUCC: // 成功发送私聊消息
                handleSendMsgSucc(msg);
                break;
            case HandleMsgCode.MSG_SEND_PRIVATE_CHAT_FAIL: // 发送私聊消息失败
                handleSendMsgFail(msg);
                break;
            case HandleMsgCode.MSG_AUDIO_SEND_BEGIN_FAIL:// 发送语音开头失败
                handleSendAudioMsgBeginFail(msg);
                break;
            case HandleMsgCode.MSG_SESSION_PRIVATE_CHAT: // 接收私聊信息
                handleFriendMsg(msg);
                break;
            case HandleMsgCode.MSG_SEND_MESSAGE: // 当发送消息，返回的发送情况
                handleSendMsgState(msg);
                break;
            case HandleMsgCode.MSG_GET_MAX_READED_RECORED_ID: // 接收已读私聊消息(最大的消息id)
                handleMaxReadId(msg);
                break;
            case HandleMsgCode.MSG_ADD_TO_BACKLIST_SUCC: // 成功列入黑名单
                handleAddToBlackListSucc(msg);
                break;
            case HandleMsgCode.MSG_GET_HISTORY: // 获取更多历史数据
                getHistoryRecord(msg);
                break;
            case HandleMsgCode.MSG_RECENT_SUCCES: // 最近玩过的游戏
                displayRecentGame(String.valueOf(msg.obj));
                break;
            case HandleMsgCode.MSG_GET_DUIBA_SUCCESS:// 获取兑吧url
                handleDuibaBack(String.valueOf(msg.obj));
                break;
            case HandleMsgCode.MSG_SEND_DELEGATION_SUCCESS:// 发送委托主持人或辅导员成功
                handleDelegationBack(String.valueOf(msg.obj));
                break;
        }
    }

    public SendPersonalSocketListener mPersonalListener = new SendPersonalSocketListener() {
        @Override
        public void update(long messageId, ChatRecord chatRecord) {
            getHistoryRecord();
            mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
        }

        @Override
        public void showStatus() {
            refershBlockView();
        }
    };

    @Override
    public void onToolListener(int type) {

        if (CommonFunction.forbidSay(mActivity)) {
            return;
        }

        if (type == ChatMenuTool.CHAT_TOOL_SENDGIFT) {//发送礼物
            CustomGiftDialogNew.jumpIntoCustomGiftDia(mContext, fUser, 1, mPersonalListener, mSendGiftPop, mSendGiftPopShow);
            hideMenu();
        } else {
            super.onToolListener(type);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonFunction.log(TAG, "onCreate() into");
        super.onCreate(savedInstanceState);
        if (isRestart) {
            return;
        }

        EventBus.getDefault().register(ChatPersonal.this);
        setContentView(R.layout.chat_personal);

        flSendGiftDialog = (FrameLayout) findViewById(R.id.fl_add_send_gift_dialog);
        initInputBar();
        initComponent();

        if (isNeedRequestUserInfo) {
            requestUserInfo();
            setViewOnClickEnable(false);
        } else {
            initChatView();
        }
        getConnectorManage().setCallBackAction(this);
        CommonFunction.log(TAG, "onCreate() out");
    }

    private void setViewOnClickEnable(boolean value) {

        if (faceMenuBtn != null) {
            faceMenuBtn.setEnabled(value);
        }

        if (toolMenuBtn != null) {
            toolMenuBtn.setEnabled(value);
        }

        if (mSoundTextSwitch != null) {
            mSoundTextSwitch.setEnabled(value);
        }

        if (mBtnSend != null) {
            mBtnSend.setEnabled(value);
        }
        if (mIvRight != null) {
            mIvRight.setEnabled(value);
        }
    }

    private void requestUserInfo() {
        try {
            GET_USER_INFO_FLAG = UserHttpProtocol.userInfoBasic(this, getTargetID(), 0, new ChatPersonalHttpCallBack(this));

        } catch (Throwable e) {
            e.printStackTrace();
            userInfoGetFail();
        }
    }

    private PersonalSendGiftPopListenerDismiss mSendGiftPop = new PersonalSendGiftPopListenerDismiss() {
        @Override
        public void updateHeightDismiss() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    flSendGiftDialog.setVisibility(View.GONE);
                }
            }, 200);
        }
    };
    private PersonalSendGiftPopListenerShow mSendGiftPopShow = new PersonalSendGiftPopListenerShow() {
        @Override
        public void updateHeightShow() {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    flSendGiftDialog.setVisibility(View.VISIBLE);
                    mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                    mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
                }
            }, 200);
        }
    };

    private void initChatView() {
        if (from != ChatFromType.OTHERGIVEGIFT) {
            popWarningDialog();
        } else {
            CustomGiftDialogNew.jumpIntoCustomGiftDia(mContext, fUser, 1, mPersonalListener, mSendGiftPop, mSendGiftPopShow);
        }


        // 是否需要请求关系
        if (getTargetID() <= Config.SYSTEM_UID)// 如果聊天的对象是系统的id,那么就表示聊过天的
        {
            ChatPersonalModel.getInstance().putAccostRelation(mContext, getTargetID(), 1);
        }
        ischat = ChatPersonalModel.getInstance().getAccostRelation(mContext, UserId, getTargetID());

        if (isNewFollowFriend) {
            insertFollowMsg();
            isNewFollowFriend = false;
        }

        initData();
        handleSpecialFriendUI();

        if (ischat <= 0) {
            // 处于搭讪关系
            // 是否处于需要搭讪的关系，是否数据库已经有题目了
            getAccostRelationShip();
        }

        if (SendFaceToFriends.isFromSendFriends == true) {
            clickMenu(1);
        }

        showUserGroupHint();
    }

    /* 显示用户所加入的或所在的聊吧或者是否为陪玩游戏主播
     * */
    private void showUserGroupHint() {
        CommonFunction.log("ChatPersonal", "showUserGroupHint() into");

        if (needLoadUserGroupInfo) {
            needLoadUserGroupInfo = false;
            //需要加载用户所在聊吧/陪玩订单信息
            loadUserGroupRequestFlag = UserHttpProtocol.obtainInfo(this, fUser.getUid(), new ChatPersonalHttpCallBack(this));
            CommonFunction.log("ChatPersonal", "showUserGroupHint() loadUserGroupRequestFlag = " + loadUserGroupRequestFlag);
            return;
        }
        if (null == fUser) {
            return;
        }

        if (mOrderInfo != null && mOrderInfo.step <= 5 && mOrderInfo.step > 0) {

            if (mOrderInfo.anchorId == Common.getInstance().loginUser.getUid()) {
                isAnchor = true;
            }

            // 订单状态流程
            updateOrderStatusView(mOrderInfo.step);
            mOrderHint.setVisibility(View.VISIBLE);
            if (mGamerHint != null) {
                mGamerHint.setVisibility(View.GONE);
            }
            if (mChatBarHint != null) {
                mChatBarHint.setVisibility(View.GONE);
            }
            return;
        }

        if (mGameInfo != null && mGameInfo.gameId > 0 && mGameInfo.anchorID != Common.getInstance().loginUser.getUid()) {
            //不显示游戏陪玩相关信息, 只显示语音聊天,0-不显示陪玩游戏，只显示语音，1-都显示
            int isShowWhat = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(SharedPreferenceUtil.ACCOMPANY_IS_SHOW);
            if ((isShowWhat == 0 && mGameInfo.gameId == Constants.AUDIO_CHAT_GAME_ID) || isShowWhat == 1) {
                //显示下单陪玩按钮
                if (mGamerHint == null) {
                    mGamerHint = findViewById(R.id.chat_user_gamer_hint_root);
                    userGameName = (TextView) findViewById(R.id.user_game_name);
                    tvLevel = (TextView) findViewById(R.id.tv_level);
                    chatUserGamePrice = (TextView) findViewById(R.id.chat_user_game_price);
                    tvMakeOrder = (TextView) findViewById(R.id.tv_make_order);
                    userGameHintClose = (TextView) findViewById(R.id.user_game_hint_close);
                }

                mGamerHint.setVisibility(View.VISIBLE);

                if (mGameInfo.gameId == Constants.AUDIO_CHAT_GAME_ID) {
                    userGameName.setText(R.string.audio_chat_authenticated_user);
                    tvMakeOrder.setText(R.string.start_audio_chat);
                    tvLevel.setVisibility(View.GONE);
                    chatUserGamePrice.setText(R.string.can_ivite_audio_chat);
                    tvMakeOrder.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ChatPersonal.this, AudioDetailsActivity.class);
                            intent.putExtra("UserId", mGameInfo.anchorID);
                            startActivity(intent);
                        }
                    });
                } else {
                    userGameName.setText(mGameInfo.gameName);
                    if (!TextUtils.isEmpty(mGameInfo.gameRank)) {
                        tvLevel.setVisibility(View.VISIBLE);
                        tvLevel.setText(mGameInfo.gameRank);
                        GradientDrawable grad = (GradientDrawable) tvLevel.getBackground();
                        grad.setColor(CommonFunction.getRankColor(mGameInfo.gameLevel));
                    } else {
                        tvLevel.setVisibility(View.GONE);
                    }
                    chatUserGamePrice.setText(mGameInfo.price + " " + mGameInfo.unit);
                    userGameHintClose.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mGamerHint.setVisibility(View.GONE);
                        }
                    });
                    tvMakeOrder.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InnerJump.JumpMakeGameOrder(mContext, mGameInfo.anchorID);
                        }
                    });
                }
                return;
            }
        }


        Group group = null;
        if (fUser.getPlayGroup() != null && fUser.getPlayGroup().name != null) {
            group = fUser.getPlayGroup();
        } else if (fUser.getGroups() != null && fUser.getGroups().size() > 0) {
            group = fUser.getGroups().get(0);
        }
        mChatBarHint = findViewById(R.id.chat_user_group_hint_root);
        if (null != group && !TextUtils.isEmpty(group.name) && fUser.getUserType() != 1) {
            Button join = (Button) mChatBarHint.findViewById(R.id.user_group_hint_join);
            final String gid = group.id;
            join.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                    if (old != null) {
                        old.isGroupIn = true;
                        old.finish();
                    }
                    Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                    intent.putExtra("id", gid + "");
                    intent.putExtra("isChat", true);
                    GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
//                    startActivity(intent);
                }
            });
            Button close = (Button) mChatBarHint.findViewById(R.id.user_group_hint_close);
            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mChatBarHint.setVisibility(View.GONE);
                }
            });
            RichTextView textView = (RichTextView) mChatBarHint.findViewById(R.id.user_group_hint_text);
            String content = getResString(R.string.show_chat_bar_one) + " " +
                    "<font color=\"#fc2452\">" + group.name + "</font>" + " " +
                    getResString(R.string.show_chat_bar_two);
            textView.setText(content);
            textView.parseIconWithColor(ScreenUtils.dp2px(14));
            if (fUser.getSexIndex() == 1) { //他
                textView.append("\n" + String.format(getResString(R.string.hit_and_paly_with_ta_in_chatbar), getResString(R.string.user_sex_him)));
            } else {//她
                textView.append("\n" + String.format(getResString(R.string.hit_and_paly_with_ta_in_chatbar), getResString(R.string.user_sex_her)));
            }
            mChatBarHint.setVisibility(View.VISIBLE);
        } else {
            mChatBarHint.setVisibility(View.GONE);
        }

        final View videoHint = findViewById(R.id.chat_user_video_hint_root);
        if (Config.getVideoChatOpen() == 1 && fUser.getUserType() == 1) {
            TextView join = (TextView) videoHint.findViewById(R.id.user_group_hint_video);
            final long gid = fUser.getUid();
            join.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(mContext, VideoDetailsActivity.class);
                    intent.putExtra(VideoDetailsActivity.KEY_VIDEO_UID, gid);
                    startActivity(intent);

                }
            });

            TextView textView = (TextView) videoHint.findViewById(R.id.user_video_hint_text);
            if (fUser.getSexIndex() == 1) { //他
                textView.setText(String.format(getResString(R.string.chat_user_video_authentication), getResString(R.string.user_sex_him)));
            } else {//她
                textView.setText(String.format(getResString(R.string.chat_user_video_authentication), getResString(R.string.user_sex_her)));
            }

            TextView invitationView = (TextView) videoHint.findViewById(R.id.chat_user_video_invitation);
            if (fUser.getSexIndex() == 1) { //他
                invitationView.setText(String.format(getResString(R.string.chat_user_video_invitation), getResString(R.string.user_sex_him)));
            } else {//她
                invitationView.setText(String.format(getResString(R.string.chat_user_video_invitation), getResString(R.string.user_sex_her)));
            }

            Button close = (Button) videoHint.findViewById(R.id.user_video_hint_close);
            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    videoHint.setVisibility(View.GONE);
                }
            });

            videoHint.setVisibility(View.VISIBLE);
        } else {
            videoHint.setVisibility(View.GONE);
        }

        refershBlockView();
    }

    /**
     * 更新顶部订单状态布局
     *
     * @param step 订单流程步骤
     */
    private void updateOrderStatusView(int step) {
        CommonFunction.log(TAG, "step==" + step);
        if (mOrderHint == null) {
            mOrderHint = findViewById(R.id.chat_user_game_order_hint_root);
            tvOrderStartService = (TextView) findViewById(R.id.tv_order_start_service);
            tvGameName = (TextView) findViewById(R.id.tv_game_name);
            tvApplyRefund = (TextView) findViewById(R.id.tv_apply_refund);
            tvOrderComplete = (TextView) findViewById(R.id.tv_order_complete);
            ivOrderUnconfirmed = (ImageView) findViewById(R.id.iv_order_unconfirmed);
            viewNotService = findViewById(R.id.view_not_service);
            ivNotService = (ImageView) findViewById(R.id.iv_not_service);
            tvNotService = (TextView) findViewById(R.id.tv_not_service);
            viewInTheService = findViewById(R.id.view_in_the_service);
            ivInTheService = (ImageView) findViewById(R.id.iv_in_the_service);
            tvInTheService = (TextView) findViewById(R.id.tv_in_the_service);
            viewComplete = findViewById(R.id.view_complete);
            ivComplete = (ImageView) findViewById(R.id.iv_complete);
            tvComplete = (TextView) findViewById(R.id.tv_complete);
            mWindow = new RefundPopupWindow(mContext);

            if (mOrderInfo != null && !TextUtils.isEmpty(mOrderInfo.gameName)) {
                tvGameName.setText(mOrderInfo.gameName);
            }
            tvOrderStartService.setOnClickListener(this);
            tvApplyRefund.setOnClickListener(this);
            tvOrderComplete.setOnClickListener(this);
        }
        switch (step) {
            case 1://未开始订单
                tvOrderStartService.setVisibility(View.GONE);
                tvOrderComplete.setVisibility(View.GONE);
                tvApplyRefund.setVisibility(View.GONE);
                ivOrderUnconfirmed.setImageResource(R.drawable.order_status_true);
                viewNotService.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivNotService.setImageResource(R.drawable.order_status_false);
                viewInTheService.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivInTheService.setImageResource(R.drawable.order_status_false);
                viewComplete.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivComplete.setImageResource(R.drawable.order_status_false);
                break;
            case 2://待服务状态 主播点击消息中的同意/拒绝
            case 3://待服务状态 主播点击头布局的开始服务
                if (isAnchor) {
                    tvOrderStartService.setVisibility(View.VISIBLE);
                    tvOrderComplete.setVisibility(View.GONE);
                    tvApplyRefund.setVisibility(View.GONE);
                }
                viewNotService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivNotService.setImageResource(R.drawable.order_status_true);
                viewInTheService.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivInTheService.setImageResource(R.drawable.order_status_false);
                viewComplete.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivComplete.setImageResource(R.drawable.order_status_false);
                tvNotService.setTextColor(getResources().getColor(R.color.common_black));
                break;
            case 4://服务中状态 用户点击消息中的同意服务之后
                if (!isAnchor) {
                    tvOrderStartService.setVisibility(View.GONE);
                    tvOrderComplete.setVisibility(View.VISIBLE);
                    tvApplyRefund.setVisibility(View.VISIBLE);
                } else {
                    tvOrderStartService.setVisibility(View.GONE);
                    tvOrderComplete.setVisibility(View.GONE);
                    tvApplyRefund.setVisibility(View.GONE);
                }
                tvNotService.setTextColor(getResources().getColor(R.color.common_black));
                tvInTheService.setTextColor(getResources().getColor(R.color.common_black));
                viewNotService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivNotService.setImageResource(R.drawable.order_status_true);
                viewInTheService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivInTheService.setImageResource(R.drawable.order_status_true);
                viewComplete.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivComplete.setImageResource(R.drawable.order_status_false);
                break;
            case 5://用户点击头布局的申请退款之后返回第5步状态
                if (!isAnchor) {
                    tvOrderStartService.setVisibility(View.GONE);
                    tvOrderComplete.setVisibility(View.GONE);
                    tvApplyRefund.setVisibility(View.GONE);
                }
                tvNotService.setTextColor(getResources().getColor(R.color.common_black));
                tvInTheService.setTextColor(getResources().getColor(R.color.common_black));
                viewNotService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivNotService.setImageResource(R.drawable.order_status_true);
                viewInTheService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivInTheService.setImageResource(R.drawable.order_status_true);
                viewComplete.setBackgroundColor(getResources().getColor(R.color.c_999999));
                ivComplete.setImageResource(R.drawable.order_status_false);
                break;
            case 6://主播点击消息中的同意/拒绝之后返回第6步状态
            case 7://完成
                tvNotService.setTextColor(getResources().getColor(R.color.common_black));
                tvInTheService.setTextColor(getResources().getColor(R.color.common_black));
                tvInTheService.setTextColor(getResources().getColor(R.color.common_black));
                viewNotService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivNotService.setImageResource(R.drawable.order_status_true);
                viewInTheService.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivInTheService.setImageResource(R.drawable.order_status_true);
                viewComplete.setBackgroundColor(getResources().getColor(R.color.common_iaround_red));
                ivComplete.setImageResource(R.drawable.order_status_true);
                mOrderHint.setVisibility(View.GONE);
                if (mOrderInfo != null) {
                    mOrderInfo.step = step;
                }
                needLoadUserGroupInfo = true;
                showUserGroupHint();
                break;
        }
    }

    private void userInfoGetFail() {
        CommonFunction.showToast(this, getResString(R.string.chat_user_info_get_fail), 0);
        finish();
    }

    @Override
    protected void onResume() {
        CommonFunction.log(TAG, "onResume() into");
        super.onResume();
        if (isRestart) {
            return;
        }
        isBackstage = false;
        updateChatRecordState(mRecordList);
        ChatPersonalModel.getInstance().setChatTargetId(getTargetID());
        ChatPersonalModel.getInstance().setIsInPersonalChat(true);
        ChatPersonalModel.getInstance().setQuietMode(quietMode);
        CommonFunction.log(TAG, "onResume() out");
    }

    @Override
    protected void onPause() {
        CommonFunction.log(TAG, "onPause() into");
        super.onPause();
        if (isRestart) {
            return;
        }
        if (!quietMode) {// 当不是悄悄查看时退出才清空未读数
            try {
                ChatPersonalModel.getInstance().clearNoneReadCount(mContext, String.valueOf(UserId), String.valueOf(getTargetID()));
                ChatPersonalModel.getInstance().readAllPersonalMsg(mContext, UserId, getTargetID());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        CommonFunction.log(TAG, "onPause() out");
    }

    @Override
    protected void onStop() {
        CommonFunction.log(TAG, "onStop() into");
        super.onStop();
        if (isRestart) {
            return;
        }

        hideMenu();
        ChatPersonalModel.getInstance().setIsInPersonalChat(false);
        ChatPersonalModel.getInstance().setQuietMode(false);
        isBackstage = true;

        AudioPlayUtil.getInstance().releaseRes();
        CommonFunction.log(TAG, "onStop() out");
    }

    @Override
    protected void onDestroy() {
        CommonFunction.log(TAG, "onDestroy() into");
        super.onDestroy();
        if (isRestart) {
            return;
        }

        EventBus.getDefault().unregister(ChatPersonal.this);
        // 保存最后的聊天记录作为草稿箱
        String content = editContent.getText().toString().trim();
        if (fUser != null) {
            ChatPersonalModel.getInstance().setChatDraft(mContext, getTargetID(), content);
        }
        editContent.addTextChangedListener(null);
        editContent.setOnTouchListener(null);
        editContent = null;
        CommonFunction.log(TAG, "onDestroy() out");
    }

    /**
     * 点击菜单
     *
     * @param type 0 没选中 1表情 2菜单
     */
    protected void clickMenu(int type) {
        super.clickMenu(type);
        if (llPageFilter.isShown()) {
            llPageFilter.hide();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isRestart) {
            return;
        }
        if (Activity.RESULT_OK == resultCode) { // 返回确认
            if (HandleMsgCode.MSG_SEND_GIFT_REQUEST == requestCode) {// 发送礼物
                int type = data.getIntExtra("type", 0);
                if (type == 2) {// 钻石礼物
                    ischat = 1;
                }
                getHistoryRecord();
                mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
            } else if (requestCode == REQUEST_CHAT_SETTING) {
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    if (bundle.getInt("addblack") == RESULT_ADDBLACK) {
                        finish();
                    } else if (bundle.getInt("clearList") == RESULT_CLEAR_LIST) {
                        mRecordList.clear();
                        updateList();
                    } else {
                        String remarks = data.getStringExtra(Constants.EDIT_RETURN_INFO);

                        if (remarks == null || TextUtils.isEmpty(remarks)) {
                            fUser.setNoteName("");
                            mTvTitle.setText(FaceManager.getInstance(this).parseIconForString(mTvTitle, this, fUser.getNoteName(true), 16));
                        } else {
                            //防止未修改备注，标题显示为空
                            notesName = remarks;
                            fUser.setNoteName(notesName);
                            mTvTitle.setText(FaceManager.getInstance(this).parseIconForString(mTvTitle, this, notesName, 16));
                        }
                    }
                }
            }
        } else {
            if (requestCode == REQUEST_VIP) {
                if (Common.getInstance().loginUser.getSVip() > 0) {
                    fUser.setBlockStaus(1);
                    refershBlockView();
                }
            }
        }
    }

    /**
     * 将当前点击的User对象回传到公共类
     *
     * @return
     */
    @Override
    protected User getUser() {
        return fUser;
    }

    /**
     * 屏幕大小发生改变
     */
    protected void handleScreenResize(Message msg) {

        if (isMenuOpen()) { // 菜单处于显示状态，则隐藏菜单
            hideMenu();
        }
        if (llPageFilter.isShown()) {
            llPageFilter.hide();
        }
        mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
    }

    @Override
    protected void giftBtnClick() {

    }

    protected ChatRecord composeRecordForSend(long flag, int type, String attachment, String field1, String msg) {

        ChatRecord record = super.composeRecordForSend(flag, type, attachment, "", msg);
        CommonFunction.log(TAG, "chat composeRecordForSend ==========" + fUser.getSVip());
        // 私聊中需要在recrod中添加对方的个人信息
        record.setFuid(fUser.getUid());
        record.setfNickName(fUser.getNickname());
        record.setfNoteName(fUser.getNickname());
        record.setfVipLevel(fUser.getViplevel());
        record.setfSVip(fUser.getSVip());
        record.setfIconUrl(fUser.getIcon());
        record.setfSex(fUser.getSexIndex());
        record.setfAge(fUser.getAge());
        record.setfLat(fUser.getLat());
        record.setfLng(fUser.getLng());
        return record;
    }

    /**
     * 获取用户搭讪关系
     */
    private void getAccostRelationShip() {
        SocketSessionProtocol.seesionSendAccostRelation(mContext, getTargetID());
    }

    private void initComponent() {
        //设置标题
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        mIvLeft = (ImageView) findViewById(R.id.iv_left);
        mIvLeft.setImageResource(R.drawable.title_back);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        recentGame = (RelativeLayout) findViewById(R.id.recentGame);
        rootLayout = (ResizeLayout) findViewById(R.id.rootLayout);
        rootLayout.setOnResizeListener(this);

        chatRecordListView = (ListView) findViewById(R.id.chatRecordList);
        chatRecordListView.setCacheColorHint(0);
        chatRecordListView.setScrollingCacheEnabled(false);

        pullDownView = (ChatPersionPullDownView) findViewById(R.id.chatting_pull_down_view);
        pullDownView.setTopViewInitialize(true);
        pullDownView.setIsCloseTopAllowRefersh(false);
        pullDownView.setHasbottomViewWithoutscroll(false);
        pullDownView.setOnRefreshAdapterDataListener(mOnRefreshAdapterDataListener);
        pullDownView.setOnListViewTopListener(mOnListViewTopListener);

        headView = View.inflate(BaseApplication.appContext, R.layout.chat_personal_record_head, null);
        mIvRight.setImageResource(R.drawable.btn_person);
        mIvRight.setVisibility(View.VISIBLE);
        chat_input_layout = (LinearLayout) findViewById(R.id.chat_input_layout);
        rootLayout.measure(0, 0);
        chat_input_layout.measure(0, 0);

        // 阻断聊天布局
        blockChatLayout = (LinearLayout) findViewById(R.id.rl_block_chat);
        ivChatGift = (ImageView) findViewById(R.id.iv_chat_gift);
        ivChatAttention = (ImageView) findViewById(R.id.iv_chat_attention);
        tvChatAttention = (TextView) findViewById(R.id.tv_chat_attention);

        findViewById(R.id.tv_chat_personal_vip_privilege).setOnClickListener(this);
        findViewById(R.id.rl_chat_gift).setOnClickListener(this);
        findViewById(R.id.rl_chat_attention).setOnClickListener(this);

        mIvLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);

        llPageFilter = (PopMenu) findViewById(R.id.llPageFilter);
        llPageFilter.bringToFront();
        initFilterPage();

        animDismiss = AnimationUtils.loadAnimation(mActivity, R.anim.user_nearby_filter_dismiss);
        animShow = AnimationUtils.loadAnimation(mActivity, R.anim.user_nearby_filter_show);

        if (getTargetID() == Config.CUSTOM_SERVICE_UID || getTargetID() == 998) {
            mIvRight.setVisibility(View.GONE);
        }

    }

    private void initFilterPage() {
        ArrayList<PopMenuItemBean> list = new ArrayList<PopMenuItemBean>();
        list.add(llPageFilter.initItemBean(R.string.chat_personal_others_info,
                R.drawable.iaround_chat_more_friendinfo, new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        friendInfoBtnClick();// Ta的资料
                    }
                }));
        list.add(llPageFilter
                .initItemBean(R.string.chat_personal_black, R.drawable.iaround_chat_more_addblack,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                showTipDialog(R.id.chat_black_tv);// 拉黑
                            }
                        }));
        list.add(llPageFilter
                .initItemBean(R.string.chat_personal_black_report, R.drawable.iaround_chat_more_report,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                showTipDialog(R.id.chat_report_tv);// 拉黑并举报
                            }
                        }));
        list.add(llPageFilter
                .initItemBean(R.string.clean_chat_history, R.drawable.iaround_chat_more_recyle,
                        new OnClickListener() {

                            @Override
                            public void onClick(View arg0) {
                                showTipDialog(R.id.chat_delete_tv);// 清除历史记录
                            }
                        }));
        list.add(llPageFilter.initItemBean(R.string.long_note, R.drawable.iaround_chat_more_note,
                new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        Intent intent = new Intent();
                        intent.setClass(mContext, EditLongNoteActivity.class);
                        intent.putExtra("userid", fUser.getUid());
                        intent.putExtra("firstin", firstEnterLongNote);
                        firstEnterLongNote = false;

                        startActivityForResult(intent, EditLongNoteActivity.EDIT_LONG_NOTE);
                    }
                }));
        llPageFilter.init(list);
    }

    protected void initInputBar() {

        chatInputBarLayout = (LinearLayout) findViewById(R.id.chat_input_layout);
        chatAudioLayout = (RelativeLayout) findViewById(R.id.chat_audio_layout);
        fontNum = (TextView) findViewById(R.id.fontNum);
        unseeNum = (TextView) findViewById(R.id.unseeNum);
        unseeNum.setOnClickListener(this);
        llMoreHandle = (LinearLayout) findViewById(R.id.llMoreHandle);

        super.initInputBar();

    }

    @Override
    protected void saveRecordToBuffer(ChatRecord record) {
        addRecord(record);
    }

    protected void saveRecordToDatabase(ChatRecord record) {
        CommonFunction.log("ChatPersonal", "saveRecordToDatabase ==========" + fUser.getSVip());
        User user = new User();
        user.setUid(fUser.getUid());
        user.setNickname(fUser.getNickname());
        user.setNoteName(fUser.getNoteName(true));
        user.setIcon(fUser.getIcon());
        user.setViplevel(fUser.getViplevel());
        user.setSVip(fUser.getSVip());
        user.setSex(fUser.getSexIndex());
        user.setAge(fUser.getAge());
        user.setLat(fUser.getLat());
        user.setLng(fUser.getLng());

        int subgroup;
        if (ischat == 0) {
            subgroup = SubGroupType.SendAccost;
        } else {
            subgroup = SubGroupType.NormalChat;
        }
        long localId = ChatPersonalModel.getInstance()
                .insertOneRecord(mContext, user, record, subgroup, from);
        record.setLocid(localId);

        ChatPersonalModel.getInstance().saveRecordLocalId(record.getDatetime(), localId);
    }

    private void initData() {
        mTvTitle.setText(FaceManager.getInstance(mActivity).parseIconForString(mTvTitle, mActivity, fUser.getNoteName(true), 16));
        getHistoryRecord();

        int count = mRecordList.size();
        if (count > 0) {// 将最后一条作为聊天记录的头像
            String tmpIcon = mRecordList.get(count - 1).getfIconUrl();
            if (!CommonFunction.isEmptyOrNullStr(tmpIcon)) {
                fUser.setIcon(tmpIcon);
            }
        } else {//当没有聊天记录的时候不显示顶部的“下拉更多历史消息”
            headView.findViewById(R.id.txtLine).setVisibility(View.INVISIBLE);
        }

        // 读取草稿箱的内容，看是否未发送的历史记录
        String content = ChatPersonalModel.getInstance().getChatDraft(mContext, getTargetID());
        if (!CommonFunction.isEmptyOrNullStr(content)) {
            editContent.setText(content);
            FaceManager.getInstance(mActivity).parseIconForEditText(mActivity, editContent);
            CommonFunction.MoveCursorToLast(editContent);
        }

        updateChatRecordState(mRecordList);

        View footerView = new View(mContext);
        footerView.setPadding(0, 2, 0, 2);
        chatRecordListView.addHeaderView(headView);
        chatRecordListView.addFooterView(footerView);

        //YC 如果后期崩溃在做修改
        adapter = new ChatPersonalRecordAdapter(mContext, mRecordList);
//        adapter = new ChatPersonalRecordAdapter(ChatPersonal.this, mRecordList);
        adapter.setMaxSelectSize(MAX_SELECT_COUNT);
        ((ChatPersonalRecordAdapter) adapter).initClickListeners(new UserIconClickListener(), new SendFailClickListener(),
                new lCreditsClickListener(), new OrderAgreeClickListener(), new OrderRefuseClickListener());
        chatRecordListView.setAdapter(adapter);
        chatRecordListView.setOnScrollListener(onScrollListener);

        updateList();

        if (Common.getInstance().isShowGameCenter) {
            // 对方的最近玩过的游戏
            checkRecentGame();
        }

    }

    /**
     * 向当前聊天记录添加一条记录
     *
     * @param record
     */
    private synchronized void addRecord(ChatRecord record) {
        if (TextUtils.isEmpty(record.getType()) || TIME_LINE_TYPE.equals(record.getType())) {
            return;
        }
        //私聊不显示用户等级
        if (record.getSendType() == net.iaround.model.im.type.MessageBelongType.SEND) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }
        //CommonFunction.log("ChatPersonal", record.getId() + "addRecord ===" + record.getfNickName() + ";vip:" + record.getfSVip());
        String followType = String.valueOf(ChatRecordViewFactory.FOLLOW);
        String gameQuetionType = String.valueOf(ChatRecordViewFactory.ACCOST_GAME_QUE);

        if (record.getType().equals(gameQuetionType)) {
            String content = String.format(getResString(R.string.accost_notice), fUser.getNickname());

            ChatRecord accostNotice = new ChatRecord();
            accostNotice.setType(String.valueOf(ChatRecordViewFactory.ACCOST_NOTICE));
            accostNotice.setContent(content);
            accostNotice.setDatetime(record.getDatetime());
            accostNotice.setDistance(getReocordDistance(record));

            mRecordList.add(accostNotice);
        }

        // 关注类型的消息上面不需要加时间
        if (!record.getType().equals(followType)) {
            // 加入时间线
            int size = mRecordList.size();
            if (size > 0 && !mRecordList.get(size - 1).getType().equals(followType)) {
                ChatRecord lastRecord = mRecordList.get(size - 1);
                if (lastRecord != null) {
                    long interval = record.getDatetime() - lastRecord.getDatetime();
                    if (interval > (3 * 60 * 1000)) {
                        ChatRecord timeRecord = new ChatRecord();
                        timeRecord.setType(TIME_LINE_TYPE);
                        timeRecord.setDatetime(record.getDatetime());
                        timeRecord.setDistance(getReocordDistance(record));

                        mRecordList.add(timeRecord);
                    }
                }
            } else {
                ChatRecord timeRecord = new ChatRecord();
                timeRecord.setType(TIME_LINE_TYPE);
                timeRecord.setDatetime(record.getDatetime());
                timeRecord.setDistance(getReocordDistance(record));
                mRecordList.add(timeRecord);
            }
        }
        mRecordList.add(record);
        addCreditsTip(record);
    }

    private int getReocordDistance(ChatRecord record) {
        if (record.getUid() == fUser.getUid()) {
            return record.getDistance();
        }

        if (record.getStatus() == ChatRecordStatus.FAIL ||
                record.getStatus() == ChatRecordStatus.SENDING) {
            return ChatPersonalModel.UNKNOWN_DISTANCE;
        } else {
            return record.getDistance();
        }
    }

    /**
     * <b>更新聊天记录的状态</b> <br/>
     * 1.告知服务器本人已读对方消息的最大ID<br/>
     * 2.从服务器获取对方已读本人消息的最大ID
     *
     * @param list 用户本人的未读聊天记录数组
     */
    private void updateChatRecordState(ArrayList<ChatRecord> list) {
        long maxRecieveMsgId = -1;
        String mUId = String.valueOf(UserId);
        String fUID = String.valueOf(getTargetID());

        if (!list.isEmpty()) {
            // 获取本人接收对方消息的最大ID
            maxRecieveMsgId = ChatPersonalModel.getInstance().getFriendMaxId(this, mUId, fUID);

            if (maxRecieveMsgId >= 0) {
                if (!quietMode) {// 如果不是悄悄查看模式则告诉服务端，本人已读对方消息的最大记录ID
                    SocketSessionProtocol.sessionSendMyReadedMaxId(mActivity, getTargetID(), String.valueOf(maxRecieveMsgId));
                }
            }
        }

        if (ChatPersonalModel.getInstance().searchNoReaded(mActivity, mUId, fUID)) {
            // 存在对方未读消息，则请求服务端，获取对方已读最大消息ID
            SocketSessionProtocol.sessionGetFriendReadedMaxId(mActivity, getTargetID());
        }
    }

    /**
     * @param maxMsgId 最大的记录id
     * @Note 服务端在标记全部已读后，推送下来的id不是对应的消息的最大id，而是服务器当前累加的最大id
     * @Description 根据最大id，把所有我发出去且小于最大id的消息的状态改为已读
     */
    private void changeRecordAsReaded(long maxMsgId) {
        if (!mRecordList.isEmpty()) {
            final long FRIEND_UID = fUser.getUid();

            int currentCursor = mRecordList.size() - 1;

            while (currentCursor > 0) {
                ChatRecord record = mRecordList.get(currentCursor);
                long recordId = record.getId();
                int status = record.getStatus();
                if (record.getUid() != FRIEND_UID && recordId <= maxMsgId) {
                    if (ChatRecordStatus.isArrived(status)) {
                        record.setStatus(ChatRecordStatus.READ);
                    }
                }
                currentCursor--;
            }
            updateList();
        }
    }

    /**
     * 获取最近消息
     */
    private void getHistoryRecord() {
        // 获取最近聊天记录
        mRecordList.clear();
        ArrayList<ChatRecord> historyRecords = ChatPersonalModel.getInstance().getChatRecord(mContext, String.valueOf(UserId), String.valueOf(getTargetID()), 0, 20);
        if (historyRecords != null && !historyRecords.isEmpty()) {
            for (ChatRecord record : historyRecords) {
                if (record.getStatus() == 1) {
                    record.setStatus(4);
                    CommonFunction.log("ChatPersonal", "拿到历史记录 发送中的状态改成失败！========");
                }

                addRecord(record);
            }
        }
    }

    // 显示提示，1拉黑，2拉黑并举报，3清除记录
    private void showTipDialog(final int type) {
        int tips = 0;

        if (type == R.id.chat_report_tv) // 举报
        {
            tips = R.string.sure_black_and_report;

        } else if (type == R.id.chat_black_tv) // 拉黑
        {
            tips = R.string.sure_black;

        } else if (type == R.id.chat_delete_tv)// 清除记录
        {
            tips = R.string.chat_personal_record_del_all_hint;
        }

        if (llPageFilter.isShown()) {
            llPageFilter.hide();
        }
        DialogUtil.showOKCancelDialog(mContext, R.string.prompt, tips, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (type) {
                    case R.id.chat_black_tv: // 拉黑
                        black(getTargetID());
                        break;
                    case R.id.chat_report_tv: // 举报
                        blackReport(getTargetID());
                        break;
                    case R.id.chat_delete_tv: // 清除历史记录
                        clearHistory(getTargetID());
                        break;
                }
            }

        });
    }

    // 清空聊天记录
    private void clearHistory(long fuid) {
        // 删除本地私信消息
        ChatPersonalModel.getInstance().deleteRecord(mActivity, String.valueOf(UserId), String.valueOf(fuid));
        mRecordList.clear();
        DatabaseFactory.getNearContactWorker(mContext).deleteRecord(String.valueOf(UserId), String.valueOf(fuid));
        updateList();
    }

    // 拉黑
    private void black(long fuid) {
        BACKLIST_FLAG = UserHttpProtocol.userDevilAdd(mActivity, fuid, new ChatPersonalHttpCallBack(this));
    }

    // 举报并拉黑

    /**
     * Modifier：kevinsu Reason：因为这样如果不想举报了，已经被拉黑，BugFreeID：4741 Date：2014.3.14
     *
     * @param fuid 对方id
     */
    private void blackReport(long fuid) {

    }

    /** Socket 接收处理-Begin **/

    /**
     * 发送私聊消息：成功
     */
    private void receiveSendMsgSucc(TransportMessage message) {
        String json = message.getContentBody();
        SocketSuccessResponse response = GsonUtil.getInstance()
                .getServerBean(json, SocketSuccessResponse.class);
        if (response.flag == giftFlag) {

        } else {

            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_PRIVATE_CHAT_SUCC, response);
            mHandler.sendMessage(msg);
        }
    }

    /**
     * 发送私聊消息：失败
     */
    private void receiveSendMsgFail(TransportMessage message) {
        String json = message.getContentBody();
        SocketFailWithFlagResponse response = GsonUtil.getInstance().getServerBean(json, SocketFailWithFlagResponse.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SEND_PRIVATE_CHAT_FAIL, response);
        mHandler.sendMessage(msg);
    }

    /**
     * 发送语音的开头消息：失败
     */
    private void receiveAudioSendBeginFail(TransportMessage message) {
        String json = message.getContentBody();
        SocketFailWithFlagResponse response = GsonUtil.getInstance()
                .getServerBean(json, SocketFailWithFlagResponse.class);

        Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_AUDIO_SEND_BEGIN_FAIL, response);
        // mHandler.sendMessage( msg );
        mHandler.sendMessageDelayed(msg, 2000);
    }

    /**
     * 接收对方已读私聊消息(最大的消息id)
     */
    private void receiveFriendMaxReadId(TransportMessage message) {
        String json = message.getContentBody();
        MaxReadIDBean bean = GsonUtil.getInstance().getServerBean(json, MaxReadIDBean.class);

        if (bean.readuserid == getTargetID()) {
            Message msg = mHandler
                    .obtainMessage(HandleMsgCode.MSG_GET_MAX_READED_RECORED_ID, bean);
            mHandler.sendMessage(msg);
        }

    }

    /**
     * 接收私聊信息
     */
    private void receiveFriendMsg(TransportMessage message) {
        String json = message.getContentBody();
        PrivateChatMessage chatMessage = JSON.parseObject(json, PrivateChatMessage.class);
        ChatRecord chatRecord;
        if (chatMessage.type == ChatRecordViewFactory.GAME_ORDER_TIP) {
            String result = chatMessage.attachment;
            GameOrderMessageBean orderMessageBean = GsonUtil.getInstance().getServerBean(result, GameOrderMessageBean.class);
            if (orderMessageBean.anchor_id == Common.getInstance().loginUser.getUid()) {
                isAnchor = true;
            }
            if (orderMessageBean.step == 1) {
                needLoadUserGroupInfo = true;
                showUserGroupHint();
            }
            //2018/8/24 如果发送者是当前登陆者需要插入到自己发送的消息记录
            if (orderMessageBean != null && orderMessageBean.senderId == Common.getInstance().loginUser.getUid()) {

                //设置聊天内容
                chatRecord = new ChatRecord();
                Me me = Common.getInstance().loginUser;
                chatRecord.setId(-1); // 消息id
                chatRecord.setUid(me.getUid());
                chatRecord.setNickname(me.getNickname());
                chatRecord.setIcon(me.getIcon());
                chatRecord.setVip(me.getViplevel());
                chatRecord.setSendType(MessageBelongType.SEND);
                chatRecord.setDatetime(System.currentTimeMillis());
                chatRecord.setType(Integer.toString(ChatRecordViewFactory.GAME_ORDER_TIP));
                chatRecord.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
                chatRecord.setContent((String) chatMessage.content);
                chatRecord.setUpload(false);

            } else {
                chatRecord = initRecord(chatMessage);
            }
        } else if (chatMessage.type == ChatRecordViewFactory.GAME_ORDER_SERVER_TIP) {
            String result = chatMessage.attachment;
            final GameOrderMessageBean orderMessageBean = GsonUtil.getInstance().getServerBean(result, GameOrderMessageBean.class);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (orderMessageBean != null && orderMessageBean.step > 0 && orderMessageBean.orderInfoId == mGameOrderId) {
                        updateOrderStatusView(orderMessageBean.step);
                    }
                }
            });

            chatRecord = initRecord(chatMessage);
        } else {

            chatRecord = initRecord(chatMessage);
        }


        //私聊不需要显示用户等级
        chatRecord.setLevel(-1);
        chatRecord.setFLevel(-1);

        if (chatMessage.user.userid == getTargetID()) {
            // 判断是否为当前私聊的好友
            Message msg = mHandler.obtainMessage(HandleMsgCode.MSG_SESSION_PRIVATE_CHAT, chatRecord);
            mHandler.sendMessage(msg);

            if (!CommonFunction.isEmptyOrNullStr(chatRecord.getfIconUrl())) {
                fUser.setIcon(chatRecord.getfIconUrl());
            }
        }

        if (chatMessage.content != null && !TextUtils.isEmpty(chatMessage.content.toString())
                && chatMessage.content.toString().contains("\"currencytype\":2")) {
            if (chatMessage.mtype == 0) {
                fUser.setBlockStaus(1);
            } else {
                fUser.setBlockStaus(0);
            }

            refershBlockView();
        }

    }

    private ChatRecord initRecord(PrivateChatMessage bean) {
        ChatRecord record = null;
        if (bean != null) {
            record = new ChatRecord();
            record.setId(bean.msgid);
            record.setType(String.valueOf(bean.type));
            record.setSendType(MessageBelongType.RECEIVE);

            record.initMineInfo(Common.getInstance().loginUser);
            record.initFriendInfo(fUser);
            if (TextUtils.isEmpty(fUser.getIcon())) {
                record.setfIconUrl(bean.user.getIcon());
            }
            //添加groupid字段
            record.setGroupid(bean.groupid);

            record.setRelationship(bean.relation);
            record.setDatetime(bean.datetime);

            // 分享的情况下,需要把bean.content这个Object的类型转成Json字符串,否者会去除字符串的[""],
            if (record.getType() == String.valueOf(ChatRecordViewFactory.SHARE)) {
                record.setContent(GsonUtil.getInstance().getStringFromJsonObject(bean.content));
            } else {
                record.setContent(bean.content.toString());
            }

            record.setAttachment(bean.attachment);
        }

        return record;
    }

    /** Socket 接收处理-End **/

    /**
     * 成功发送私聊消息
     */
    private void handleSendMsgSucc(Message msg) {
        SocketSuccessResponse response = (SocketSuccessResponse) msg.obj;
        long flag = response.flag;
        long msgId = response.msgid;
        int distance = response.distance;

        // 更新本地对应消息的id，把本地id修改成服务器id
        int position = findCurSendMsg(mRecordList, flag);

        if (position != -1) {
            ChatRecord record = mRecordList.get(position);

            record.setId(msgId);
            record.setStatus(ChatRecordStatus.ARRIVED); // 已送达
            record.setDistance(distance);

            // 对于前一条是时间分界，需要更新距离
            int previousPos = position - 1;
            if (previousPos >= 0) {
                ChatRecord preRecord = mRecordList.get(previousPos);
                if (preRecord.getType().equals(TIME_LINE_TYPE)) {
                    preRecord.setDistance(distance);
                }
            }

            updateList();
            showListBottom();
        }

        if (quietMode && position > 0) {// 当在悄悄模式发送消息成功之后，关闭悄悄模式，发送最大msgid
            quietMode = false;
            ChatPersonalModel.getInstance().setQuietMode(false);
            long maxRecieveMsgId = ChatPersonalModel.getInstance()
                    .getFriendMaxId(mContext, String.valueOf(UserId),
                            String.valueOf(getTargetID()));
            SocketSessionProtocol.sessionSendMyReadedMaxId(mActivity, getTargetID(), String.valueOf(maxRecieveMsgId));
        }
    }

    /**
     * 发送私聊消息失败
     */
    private void handleSendMsgFail(Message msg) {
        SocketFailWithFlagResponse response = (SocketFailWithFlagResponse) msg.obj;

        long flag = response.flag;
        long error = response.error;

        int position = findCurSendMsg(mRecordList, flag);
        if (position != -1) {
            ChatRecord record = mRecordList.get(position);
            record.setStatus(ChatRecordStatus.FAIL);

            if (error == -30083010) { // 对方已将你拉黑
                CommonFunction.showToast(mActivity, getResString(R.string.se_30083010), 0);
            } else if (error == -40083010) {// 金币不足
                DialogUtil.showGoldDialog(mActivity);
            } else if (error == -110083010)// 双方为搭讪关系
            {
                // 1.isChat 为0
                // 把分组放在发出搭讪
                ischat = 0;
                ChatPersonalModel.getInstance().putAccostRelation(mContext, fUser.getUid(), ischat);
                changeChatSubgroup(SubGroupType.SendAccost);
            } else if (error == -130083010)// 双方为正常交流关系
            {
                // 和上面相反的操作
                // 把分组放在发出正常私聊
                ischat = 1;
                ChatPersonalModel.getInstance()
                        .putAccostRelation(mContext, fUser.getUid(), ischat);
                changeChatSubgroup(SubGroupType.NormalChat);
            } else if (error == -200083010) {
                ChatRecord chatRecord = mRecordList.get(position);
                ChatRecord timeRecord = new ChatRecord();
                timeRecord.setNickname(chatRecord.getNickname());
                timeRecord.setAttachment(chatRecord.getAttachment());
                timeRecord.setContent(chatRecord.getContent());
                timeRecord.setDatetime(chatRecord.getDatetime());
                timeRecord.setLocid(chatRecord.getLocid());
                timeRecord.setFlag(chatRecord.getFlag());
                timeRecord.setSendType(chatRecord.getSendType());
                timeRecord.setType("110");
                mRecordList.add(timeRecord);
                saveRecordToDatabase(timeRecord);
            } else if (error == -210083010) {
                ChatRecord chatRecord = mRecordList.get(position);
                ChatRecord timeRecord = new ChatRecord();
                timeRecord.setNickname(chatRecord.getNickname());
                timeRecord.setAttachment(chatRecord.getAttachment());
                timeRecord.setContent(response.getMessage());
                timeRecord.setDatetime(chatRecord.getDatetime());
                timeRecord.setLocid(chatRecord.getLocid());
                timeRecord.setFlag(chatRecord.getFlag());
                timeRecord.setSendType(chatRecord.getSendType());
                timeRecord.setType("111");
                mRecordList.add(timeRecord);
                saveRecordToDatabase(timeRecord);
            }

            updateList();
            autoShowListBottom();
        }
    }

    /**
     * 改变对方所在的分组
     *
     * @param subgroupType
     */
    private void changeChatSubgroup(int subgroupType) {
        ChatPersonalModel.getInstance().changeContactSubgroup(mContext, fUser.getUid(), subgroupType);
    }

    /**
     * 发送私聊语音开头失败
     */
    private void handleSendAudioMsgBeginFail(Message msg) {
        SocketFailWithFlagResponse response = (SocketFailWithFlagResponse) msg.obj;

        long flag = response.flag;
        long error = response.error;
        int position = findCurSendMsg(mRecordList, flag);
        if (position != -1) {
            ChatRecord record = mRecordList.get(position);
            record.setStatus(ChatRecordStatus.FAIL);

            if (error == -30083052) { // 对方已将你拉黑
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        CommonFunction
                                .showToast(mActivity, getResString(R.string.se_30083010), 0);
                    }
                }, 1000);
            }

            updateList();
            autoShowListBottom();
        }
    }

    /**
     * 接收私聊信息
     *
     * @param msg
     */
    private void handleFriendMsg(Message msg) {
        ChatRecord record = (ChatRecord) msg.obj;
        changeRecordAsReaded(record.getId());// 用于解决有时出现的对方发来消息但我上面还有送达的消息问题
        ChatPersonalModel.getInstance()
                .modifyMessageBigId(mContext, String.valueOf(record.getFuid()), record.getId());
        if (record.getId() >= 0 && !isBackstage && !quietMode) {
            // 组合发送到网络的数据
            SocketSessionProtocol.sessionSendMyReadedMaxId(mActivity, fUser.getUid(), String.valueOf(record.getId()));
        }
        if (mRecordList != null && mRecordList.size() > 0
                && record.getId() == mRecordList.get(mRecordList.size() - 1).getId()) {//为了不显示重复数据


        }
        addRecord(record);
        updateList();
        checkForUnSee();
        autoShowListBottom();
    }

    // 赠送钻石礼物提示兑换积分
    private void addCreditsTip(ChatRecord record) {
        String giftRemindType = String.valueOf(ChatRecordViewFactory.GIFE_REMIND);
        String creditsExchangeType = String
                .valueOf(ChatRecordViewFactory.FRIEND_CREDITS_EXCHANGE);
        String key = SharedPreferenceUtil.SHOWCREDITS_AVAILABLE;
        int isOpenCredits = SharedPreferenceUtil.getInstance(mContext).getInt(key);

        if (record.getType().equals(giftRemindType) && isOpenCredits == 1 && record.getSendType() == MessageBelongType.RECEIVE) {
            Gift gift = GsonUtil.getInstance().getServerBean(record.getContent(), Gift.class);
            if (gift.getCurrencytype() == 2) {// 非钻石礼物不展示该提示
                ChatRecord creditsNotice = new ChatRecord();
                creditsNotice.setType(creditsExchangeType);
                creditsNotice.setContent(getString(R.string.chat_credits_notice));
                creditsNotice.setDatetime(record.getDatetime());
                creditsNotice.setDistance(record.getDistance());
                mRecordList.add(creditsNotice);
            }
        }
    }


    /**
     * 当Socket发送消息，返回的发送情况,修改缓存的数据
     *
     * @param msg
     */
    private void handleSendMsgState(Message msg) {
        long flag = msg.getData().getLong("flag");
        int e = msg.getData().getInt("e");

        int position = findCurSendMsg(mRecordList, flag);

        if (position < 0) {
            return;
        }

        ChatRecord record = mRecordList.get(position);
        if (e == 0) {

            record.setStatus(ChatRecordStatus.FAIL);

            int recordType = Integer.valueOf(record.getType());
            if (recordType != ChatMessageType.MEET_GIFT) {
                Toast.makeText(mContext, R.string.send_err, Toast.LENGTH_SHORT).show();
            }
        } else {
            record.setStatus(ChatRecordStatus.ARRIVED); // 标识为已送达
        }

        updateList();
        autoShowListBottom();
    }

    /**
     * 接收已读私聊消息(最大的消息id)
     *
     * @param msg
     */
    private void handleMaxReadId(Message msg) {
        MaxReadIDBean bean = (MaxReadIDBean) msg.obj;
        changeRecordAsReaded(bean.messageid);
    }

    /**
     * 成功列入黑名单
     *
     * @param msg
     */
    private void handleAddToBlackListSucc(Message msg) {
        CommonFunction.showToast(mContext, getResString(R.string.add_black_suc), 0);

        FriendModel.getInstance(mContext).deleteFollow(getTargetID());
        MessageModel.getInstance().deleteNearContactRecord(mContext, getTargetID());
        UserBufferHelper.getInstance().remove(getTargetID());

        setResult(ChatPersonal.RESULT_ADDBLACK);
        finish();
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

                String localIdStr = String.valueOf(record.getLocid());
                ChatPersonalModel.getInstance().modifyMessageContent(mContext, localIdStr, record.getContent());
            } else {

                delOneRecord(record);
                dataHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (bean != null) {
                            if (bean.flag == 0) {
                                return;
                            }
                            Long localId = ChatPersonalModel.getInstance().getRecordLocalId(bean.flag);
                            if (localId != null) {
                                ChatPersonalModel.getInstance().deleteRecordByLocalId(mContext, localId.longValue());
                            }
                            ChatPersonalModel.getInstance().removeRecordLocalId(bean.flag);
                            backManager.callSendDataThreadCodeEnd(bean.flag);
                        }
                    }
                });
            }
            mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
        }
    }

    private void getHistoryRecord(Message msg) {
        if (msg.arg1 == 1 && msg.obj != null) { // 有数据
            @SuppressWarnings("unchecked") ArrayList<ChatRecord> historyRecords = (ArrayList<ChatRecord>) msg.obj;
            int size = 0;
            if (historyRecords != null) {
                size = historyRecords.size();
            }

            String creditsExchangeType = String
                    .valueOf(ChatRecordViewFactory.FRIEND_CREDITS_EXCHANGE);
            if (!mRecordList.isEmpty()) {//移除兑换礼物的提示语
                ArrayList<ChatRecord> tempList = new ArrayList<ChatRecord>();
                for (ChatRecord chatRecord : mRecordList) {
                    if (chatRecord.getType().equals(creditsExchangeType)) {
                        tempList.add(chatRecord);
                    }
                }
                mRecordList.removeAll(tempList);
            }

            historyRecords.addAll(mRecordList);
            mRecordList.clear();
            if (historyRecords != null && !historyRecords.isEmpty()) {
                for (ChatRecord record : historyRecords) {
                    if (record.getStatus() == 1) {
                        record.setStatus(4);
                    }
                    addRecord(record);
                }
            }

            updateList();
            chatRecordListView.setSelection(size);
        } else {
            Toast.makeText(mActivity, R.string.chat_no_history, Toast.LENGTH_SHORT).show();
        }
    }

    // 加载更多数据
    private OnRefreshAdapterDataListener mOnRefreshAdapterDataListener = new OnRefreshAdapterDataListener() {
        @Override
        public void refreshData() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 查完之后给主UI发刷新，加载页面
                    ArrayList<ChatRecord> list = ChatPersonalModel.getInstance()
                            .getChatRecord(mContext, String.valueOf(UserId),
                                    String.valueOf(getTargetID()), getRecordSize(), 30);
                    Message msg = new Message();
                    msg.what = HandleMsgCode.MSG_GET_HISTORY;
                    if (list != null && list.size() > 0) {
                        msg.arg1 = 1;
                        msg.obj = list;
                    } else {
                        msg.arg1 = 2;
                    }
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    };

    private OnListViewTopListener mOnListViewTopListener = new OnListViewTopListener() {
        @Override
        public boolean getIsListViewToTop() {
            View topListView = chatRecordListView
                    .getChildAt(chatRecordListView.getFirstVisiblePosition());
            return !((topListView == null) || (topListView.getTop() != 0));
        }
    };

    /**
     * 请求对方最近玩过的游戏
     */
    private void checkRecentGame() {
        long fuid = getTargetID();
        int lang = CommonFunction.getLang(mContext);
        RECENT_GAME_FLAG = GameHttpProtocol.gameRecentPlay(this, fuid, lang, new ChatPersonalHttpCallBack(this));
    }

    /**
     * 显示游戏组件
     */
    private void displayRecentGame(String result) {
        final RecentPlay recentPlay = GsonUtil.getInstance()
                .getServerBean(result, RecentPlay.class);
        if (recentPlay == null) {
            return;
        }
        if (recentPlay.isSuccess() && recentPlay.flag == 1) {
            // 获取最后一次该用户在玩的游戏的ID
            SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(mContext);
            long lastGameId = sp.getLong(
                    UserId + "_" + getTargetID() + "_" + SharedPreferenceUtil.CHAT_RECENT_GAME_TIPS);

            if (lastGameId == recentPlay.gameid) {
                return;// 不重复提示
            }

            // 保存并提示
            sp.putLong(UserId + "_" + getTargetID() + "_" + SharedPreferenceUtil.CHAT_RECENT_GAME_TIPS, recentPlay.gameid);

            // 显示UI
            NetImageView tvGameIcon = (NetImageView) recentGame.findViewById(R.id.game_icon);

            int borderSize = (int) (getResources().getDisplayMetrics().density);
            String borderColor = getResString(R.color.c_dedede);

            GlideUtil.loadRoundImage(BaseApplication.appContext, recentPlay.icon, 20, tvGameIcon.getImageView());
            recentGame.startAnimation(animShow);
            recentGame.setVisibility(View.VISIBLE);

            recentGame.findViewById(R.id.ivclose).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    recentGame.startAnimation(animDismiss);
                    recentGame.setVisibility(View.GONE);
                }
            });
            recentGame.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 跳转到兑吧页面
     */
    protected void handleDuibaBack(String result) {
        DuibaBackBean duibaBean = GsonUtil.getInstance()
                .getServerBean(result, DuibaBackBean.class);
        if (duibaBean != null && duibaBean.isSuccess()) {
        }
    }

    /**
     * 成功发送委托主持人或辅导员的返回
     *
     * @param result
     */
    private void handleDelegationBack(String result) {
    }


    // 初始化聊天对方的信息
    public void initTargetInfomation() {
        fUser = new User();
        fUser.setUid(getIntent().getLongExtra("fuid", 0));
        fUser.setNickname(getIntent().getStringExtra("fnickname"));
        String noteName = getIntent().getStringExtra("fnotename");
        fUser.setNoteName(noteName);
        // 性别、年龄、经纬度
        fUser.setSex(getIntent().getIntExtra("sex", 0));
        fUser.setAge(getIntent().getIntExtra("age", 20));
        fUser.setLat(getIntent().getIntExtra("lat", 0));
        fUser.setLng(getIntent().getIntExtra("lng", 0));
        fUser.setIcon(getIntent().getStringExtra("ficon"));
        fUser.setViplevel(getIntent().getIntExtra("fvip", 0));
        fUser.setSVip(getIntent().getIntExtra("fsvip", 0));
        fUser.setLevel(getIntent().getIntExtra("flevel", -1));
        fUser.setRelationship(getIntent().getIntExtra("relation", 0));
        fUser.setSign(getIntent().getStringExtra("signture"));
        isNewFollowFriend = getIntent().getBooleanExtra("isnewfollow", false);
        from = getIntent().getIntExtra("from", ChatFromType.UNKONW);
        quietMode = getIntent().getBooleanExtra("quietSee", false);

        if (CommonFunction.isEmptyOrNullStr(fUser.getIcon())) {
            fUser.setIcon("");
        }
        notesName = fUser.getNoteName(false);

        fromChatBar = getIntent().getBooleanExtra("fromchatbar", false);

//        Group play = (Group)getIntent().getSerializableExtra("playgroup");
//        if(play == null || play.name == null){
//            needLoadUserGroupInfo = true;
//        }else{
//            needLoadUserGroupInfo = false;
//            fUser.setPlayGroup(play);
//            ArrayList<Group> groups = (ArrayList<Group>)getIntent().getSerializableExtra("groups");
//            fUser.setGroups(groups);
//        }
    }

    private void popWarningDialog() {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(this);

        String pushNumberKey = SharedPreferenceUtil.PUSH_WARN_NUMBER + UserId;
        String pushTimeKey = SharedPreferenceUtil.PUSH_WARN_TIME + UserId;
        String fUserId = String.valueOf(getTargetID());

        try {
            if (!TimeFormat.IsAfterTodayTime(sp.getLong(pushTimeKey))) {
                if (sp.has(fUserId)) {
                    sp.remove(fUserId);
                }
            }

            if (fUser.getRelationship() == User.RELATION_STRANGER &&
                    ChatPersonalModel.getInstance().isFirstChat(mContext, fUser.getUid()) &&
                    (sp.getInt(pushNumberKey) > 0) && (!sp.has(fUserId))) {
                View view = View.inflate(mContext, R.layout.prevent_harassment_dialog, null);
                final Dialog buildDialog = DialogUtil.buildDialog(mContext, view, Gravity.CENTER);
                buildDialog.getWindow().setBackgroundDrawableResource(R.drawable.transparent);
                buildDialog.findViewById(R.id.btn_iknow)
                        .setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                buildDialog.dismiss();
                            }
                        });

                sp.putInt(pushNumberKey, sp.getInt(pushNumberKey) - 1);
            }
            sp.putLong(fUserId, fUser.getUid());

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void faceBtnClick() {
        displayFaceMenu();
    }

    @Override
    protected void toolBtnClick() {
        displayToolMenu();
    }

    protected void soundTextSwitcherClick() {
        hideKeyboard();
        updateSwitcherBtnState();
    }

    private void friendInfoBtnClick() {
        if (llPageFilter.isShown()) {
            llPageFilter.hide();
        }

    }

    private void handleSpecialFriendUI() {
        long uid = getTargetID();
        if (uid == 0) {
            CommonFunction.showToast(this, getResString(R.string.none_user), 0);
            finish();
        }
    }

    // 处理语音发送结束反馈（包括成功和失败）
    private void handleAudioSendEndBack(String result) {
        try {
            JSONObject json = new JSONObject(result);
            long flag = json.optLong("flag");

            backManager.RemoveSendThread(flag);
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    /**
     * 是否新关注
     */
    private void insertFollowMsg() {
        // 要先判断数据库插入是的前一条消息是否为关注标识，如果是，就不插入
        String mUID = String.valueOf(UserId);
        String fUID = String.valueOf(getTargetID());

        String lastContent = ChatPersonalModel.getInstance().getLastContent(mContext, mUID, fUID);
        if (!TextUtils.isEmpty(lastContent)) {
            try {
                JSONObject json = new JSONObject(lastContent);
                int type = json.getInt("type");
                if (type == ChatRecordViewFactory.FOLLOW) {
                    return;
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
        }

        int type = ChatRecordViewFactory.FOLLOW;
        String attachment = "";
        String contentRes = getResString(R.string.chat_follow_flag);
        String content = String.format(contentRes, fUser.getNickname());

        ChatRecord record = composeRecordForSend(getCurrentFlag(), type, attachment, "", content);
        record.setStatus(ChatRecordStatus.NONE);
        saveRecordToDatabase(record);
    }


    /**
     * 根据ischat，获取mtype的类型， mtype 定义的是（0-正常私聊消息，1-搭讪） ischat 定义的是 是否交流（0-否，1-有）
     * 关系是，当ischat == 0时， 意思是没有交流过，即处于搭讪关系 反之，当ischat == 1时， 意思是有交流过， 即处于正常私聊关系
     *
     * @return
     */
    public int getMType() {
        return ischat == 0 ? 1 : 0;
    }

    public User getFUser() {
        return fUser;
    }

    public int getFrom() {
        return from;
    }

    @Override
    protected ChatMessageBean composeChatMessageBean(ChatRecord record) {

        PrivateChatInfo chatInfo = new PrivateChatInfo();
        chatInfo.targetUserId = getTargetID();
        chatInfo.from = getFrom();
        chatInfo.mtype = getMType();

        ChatMessageBean messageBean = new ChatMessageBean();

        messageBean.chatRecord = record;
        messageBean.chatType = ChatMessageBean.PRIVATE_CHAT;
        messageBean.targetInfo = chatInfo;

        if (record.getType() == String.valueOf(ChatRecordViewFactory.IMAGE)) {
            messageBean.resourceType = FileUploadType.PIC_PRIVATE_CHAT;
        } else if (record.getType() == String.valueOf(ChatRecordViewFactory.VIDEO)) {
            messageBean.resourceType = FileUploadType.VIDEO_PRIVATE_CHAT;
        }

        return messageBean;
    }


    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = DialogUtil.showProgressDialog(mContext, "", getString(R.string.please_wait), null);
        }
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }

    static class ChatPersonalHttpCallBack implements HttpCallBack {
        private WeakReference<HttpCallBack> mCallback;

        public ChatPersonalHttpCallBack(HttpCallBack callBack) {
            mCallback = new WeakReference<HttpCallBack>(callBack);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            HttpCallBack callBack = mCallback.get();
            if (null != callBack) {
                callBack.onGeneralSuccess(result, flag);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            HttpCallBack callBack = mCallback.get();
            if (null != callBack) {
                callBack.onGeneralError(e, flag);
            }
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        super.onGeneralSuccess(result, flag);
//        if (flag == BACKLIST_FLAG) { // 成功列入黑名单
//            mHandler.sendEmptyMessage(HandleMsgCode.MSG_ADD_TO_BACKLIST_SUCC);
//        } else
        if (flag == RECENT_GAME_FLAG) { // 最近玩过的游戏
            Message msg = new Message();
            msg.what = HandleMsgCode.MSG_RECENT_SUCCES;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (flag == UPDATE_NOTE_FLAG) {// 更新长备注
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.optInt("status") != 200) {
                    CommonFunction.toastMsg(mContext, R.string.note_fail);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (flag == GET_USER_INFO_FLAG) {
            Me user = new Me();
            UserBasic userbasic = GsonUtil.getInstance().getServerBean(result, UserBasic.class);

            if (userbasic.isSuccess()) {
                userbasic.setUserBasicInfor(mContext, user);

                fUser = user;
                // 获取到基本资料后，接着获取其他资料
                ChatPersonalModel.getInstance().putAccostRelation(mContext, fUser.getUid(), ischat);

                isNeedRequestUserInfo = false;
                initChatView();
                setViewOnClickEnable(true);
            } else {
                userInfoGetFail();
            }
        } else if (flag == GET_DUIBA_URL_FLAG) {
            Message msg = new Message();
            msg.what = HandleMsgCode.MSG_GET_DUIBA_SUCCESS;
            msg.obj = result;
            mHandler.sendMessage(msg);
        } else if (loadUserGroupRequestFlag == flag) {
            CommonFunction.log("ChatPersonal", "get user info for group http callback=" + result);
//            UserInfoEntity userbasic = GsonUtil.getInstance().getServerBean(result, UserInfoEntity.class);
            ObtainInfoBean bean = GsonUtil.getInstance().getServerBean(result, ObtainInfoBean.class);

            if (null != bean && bean.isSuccess()) {
                Group group = new Group();
                group.id = String.valueOf(bean.getGid());
                group.icon = bean.getGicon();
                group.name = bean.getGname();
                fUser.setPlayGroup(group);
                fUser.setRelationship(bean.relation);
                if (Common.getInstance().loginUser.getSVip() > 0) {
                    fUser.setBlockStaus(1);
                } else {
                    fUser.setBlockStaus(bean.setBlockStaus);
                }
                fUser.setUserType(bean.userType);

                mOrderInfo = bean.orderInfo;
                mGameInfo = bean.gameInfo;
                if (mOrderInfo != null) {
                    mGameOrderId = mOrderInfo.orderId;
                }
                showUserGroupHint();
            }
        } else if (AGREE_ORDER_FLAG == flag || REFUSE_ORDER_FLAG == flag || AGREE_SERVER_MSG_FLAG == flag || REFUSE_REFUND_FLAG == flag
                || AGREE_REFUND_FLAG == flag || SEND_SERVIC_MSG_FLAG == flag) {
            hideProgressDialog();
            ManagerOrderResultBean resultBean = GsonUtil.getInstance().getServerBean(result, ManagerOrderResultBean.class);
            if (resultBean == null || !resultBean.isSuccess()) {
                getHistoryRecord();
                updateList();
                return;
            }
//            updateOrderStatusView(resultBean.orderStatus);
            GameOrderMessageBean bean = new GameOrderMessageBean();
            if (AGREE_ORDER_FLAG == flag) {//同意订单回调
                bean.step = 1;
                bean.orderStatus = 1;
            } else if (REFUSE_ORDER_FLAG == flag) {//拒绝订单回调
                bean.step = 1;
                bean.orderStatus = 2;
            } else if (AGREE_SERVER_MSG_FLAG == flag) {//用户同意开始服务
                bean.step = 3;
                bean.orderStatus = 1;
            } else if (REFUSE_REFUND_FLAG == flag) {//主播拒绝退款回调
                bean.step = 6;
                bean.orderStatus = 2;
            } else if (AGREE_REFUND_FLAG == flag) {//主播同意退款
                bean.step = 6;
                bean.orderStatus = 1;

            }
            String json = GsonUtil.getInstance().getStringFromJsonObject(bean);
            ChatPersonalModel.getInstance().modifyMessageAttachmentByMsgId(mContext, String.valueOf(resultBean.msgid), json);

        } else if (SEND_SERVIC_MSG_FLAG == flag) {//开始服务回调

        } else if (FINISH_ORDER_FLAG == flag) {//完成回调
            Log.d(TAG, "flag==" + flag + "==FINISH_ORDER_FLAG==" + FINISH_ORDER_FLAG);
            hideProgressDialog();
            ManagerOrderResultBean resultBean = GsonUtil.getInstance().getServerBean(result, ManagerOrderResultBean.class);
            if (resultBean != null && resultBean.isSuccess()) {
                InnerJump.JumpCommentGameOrder(mContext, mGameOrderId);
            }

        } else if (REFUND_CHECK_FLAG == flag) {//用户申请退款
            Log.d(TAG, "flag==" + flag + "==REFUND_CHECK_FLAG==" + REFUND_CHECK_FLAG);
            hideProgressDialog();
            ManagerOrderResultBean resultBean = GsonUtil.getInstance().getServerBean(result, ManagerOrderResultBean.class);
            if (resultBean != null && resultBean.isSuccess()) {
                updateOrderStatusView(resultBean.orderStatus);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        super.onGeneralError(e, flag);
        if (flag == UPDATE_NOTE_FLAG) {
            ErrorCode.toastError(mContext, e);
        } else if (flag == GET_USER_INFO_FLAG) {
            userInfoGetFail();
        } else if (AGREE_ORDER_FLAG == flag || REFUSE_ORDER_FLAG == flag || AGREE_SERVER_MSG_FLAG == flag || REFUSE_REFUND_FLAG == flag
                || AGREE_REFUND_FLAG == flag || SEND_SERVIC_MSG_FLAG == flag) {
            hideProgressDialog();
            getHistoryRecord();
            updateList();
            return;
        }
    }

    @Override
    protected int showMoreMenuBtn() {
        super.showMoreMenuBtn();
        return 1;
    }

    @Subscribe
    public void onEvent(String event) {
        if (event.contains("refer_chat_bar_skill")) {
            sendMsgNoStatus(getCurrentFlag(), MessageType.TEXT, "", "", event.substring("refer_chat_bar_skill".length(), event.length()));

            // 获取最新的历史数据加一个延时
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getHistoryRecord();
                    adapter.notifyDataSetChanged();
                    chatRecordListView.setSelection(chatRecordListView.getBottom());
                }
            }, 100);

        } else if (event.equals("refer_chat_block_gifts")) {
            fUser.setBlockStaus(1);
            refershBlockView();
        } else if (event.equals("audio_chat_record")) {
            // 获取最新的历史数据加一个延时
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    getHistoryRecord();
                    adapter.notifyDataSetChanged();
                    chatRecordListView.setSelection(chatRecordListView.getBottom());
                }
            }, 100);
        }
    }

    /**
     * 更新阻断View
     */
    private void refershBlockView() {

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (fUser.getRelationship() == 2) {
                    ivChatAttention.setSelected(false);
                    tvChatAttention.setText(getString(R.string.following));
                } else if (fUser.getRelationship() == 3 || fUser.getRelationship() == 1) {//relation == 3
                    ivChatAttention.setSelected(true);
                    tvChatAttention.setText(getString(R.string.dynamic_filter_follow));
                }

                if (Common.getInstance().getBlockStatus() > 0 && Common.getInstance().loginUser.getSVip() > 0) {
                    fUser.setBlockStaus(1);
                }

                if (Common.getInstance().getBlockStatus() > 0 && fUser.getBlockStaus() == 0) {
                    chatInputBarLayout.setVisibility(View.GONE);
                    blockChatLayout.setVisibility(View.VISIBLE);
                } else {
                    chatInputBarLayout.setVisibility(View.VISIBLE);
                    blockChatLayout.setVisibility(View.GONE);
                }
                mHandler.sendEmptyMessage(HandleMsgCode.NOTIFY_DATA_CHANGE);
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
            }
        }, 200);

    }

    /**
     * 关注、取消关注
     *
     * @param context
     * @param type
     * @param me
     * @param opt
     */
    public void updateRelation(final Context context, int type, User me, final int opt) {
        if (type == 1) {
            //cancle
            UN_FOLLOW_USER_FLAG = FriendHttpProtocol.userFanDislike(context, me.getUid(), new HttpCallBackImpl(this, opt));
        } else {
            //add
            int NODE_ID = (int) OtherInfoActivity.NODE_ID;
            try {
                if (me.getRelationLink().middle.id > 0) {
                    NODE_ID = me.getRelationLink().middle.id;
                }

                FOLLOW_USER_FLAG = FriendHttpProtocol.userFanLove(context, me.getUid(), 3, NODE_ID, new HttpCallBackImpl(this, opt));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleUnFollowUserSuccess(String result, long opt) {
        BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (serverBean.isSuccess()) {
            if (opt == Constants.SECRET_SET_HADLE_TYPE_DELETE) {
                //fUser.setRelationship(2);//0、没有关系 1、朋友 2、陌生人  3、关注  4、粉丝
                Toast.makeText(BaseApplication.appContext, BaseApplication.appContext.getResources().getString(R.string.other_info_cancle_follow), Toast.LENGTH_LONG).show();
                tvChatAttention.setText(getString(R.string.following));
                ivChatAttention.setSelected(false);

                if (fUser.getRelationship() == 3) {
                    fUser.setRelationship(2);
                } else if (fUser.getRelationship() == 1) {
                    fUser.setRelationship(4);
                }
            } else {
                fUser.setRelationship(3);
                ivChatAttention.setSelected(true);
                tvChatAttention.setText(getString(R.string.dynamic_filter_follow));
            }
        } else {
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }


    private void handleFollowUserSuccess(String result, long opt) {
        BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (serverBean.isSuccess()) {
            if (opt == Constants.SECRET_SET_HADLE_TYPE_ADD) {
                tvChatAttention.setText(getString(R.string.dynamic_filter_follow));
                //fUser.setRelationship(3);//0、没有关系 1、朋友 2、陌生人  3、关注  4、粉丝
                Toast.makeText(BaseApplication.appContext, BaseApplication.appContext.getResources().getString(R.string.other_info_follow1), Toast.LENGTH_LONG).show();
                ivChatAttention.setSelected(true);

                if (fUser.getRelationship() == 0 | fUser.getRelationship() == 2) {
                    fUser.setRelationship(3);
                } else if (fUser.getRelationship() == 4) {
                    fUser.setRelationship(1);
                    fUser.setBlockStaus(1);
                    refershBlockView();
                }
            } else {
                ivChatAttention.setSelected(false);
                fUser.setRelationship(2);
                tvChatAttention.setText(getString(R.string.following));
            }
        } else {
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }

    /**
     * 获取聊吧获得麦克风权限后执行操作
     */
    public void doMicShowPermissions() {
        ChatBarZoomWindow.getInstance().onRequestMicPermissionSuccess();
    }

    static class HttpCallBackImpl implements HttpCallBack {
        private WeakReference<ChatPersonal> mActivity;
        private long mExtra;

        public HttpCallBackImpl(ChatPersonal activity, long extra) {
            mActivity = new WeakReference<ChatPersonal>(activity);
            mExtra = extra;
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            ChatPersonal activity = mActivity.get();
            if (activity == null) {
                return;
            }

            if (activity.UN_FOLLOW_USER_FLAG == flag) {
                activity.handleUnFollowUserSuccess(result, mExtra);
            } else if (activity.FOLLOW_USER_FLAG == flag) {
                activity.handleFollowUserSuccess(result, mExtra);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            ChatPersonal activity = mActivity.get();
            if (activity == null) {
                return;
            }
        }
    }
}
