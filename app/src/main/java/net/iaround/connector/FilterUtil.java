package net.iaround.connector;


import android.app.Activity;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;

import net.android.volley.Request;
import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.PushHttpProtocol;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.chat.GameOrderMessageBean;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.VisitorUser;
import net.iaround.model.im.AccostRelationes.Relation;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.Me;
import net.iaround.model.im.NewFansListBean;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserBasic;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.type.ChatMessageType;
import net.iaround.pay.bean.PayResultBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceLogoIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.RomUtils;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.FaceCenterModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupHistoryMessagesBean;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.MeetGameModel;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.dynamic.NotificationFunction;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupHelperActivity;
import net.iaround.ui.group.activity.GroupNoticeActivity;
import net.iaround.ui.group.bean.GroupNoticeListBean;
import net.iaround.ui.group.bean.GroupsMsgStatusBean;
import net.iaround.utils.eventbus.HeadImageNotifyEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * FilterUtil处理的内容是服务端主动推送下来的消息,所有主动推送的消息都在这里拦截
 * 如果消息需要UI线程处理,那么就指派给UIMainHandler来处理 一般在这里处理的数据,大多数与数据存储,修改有关[数据库存储,数据修改]
 * 最后,如果需要向页面指派数据,再向上派发消息. eg:消息提示铃声、积分、数据库保存、以及需要拦截处理的消息
 *
 * @author linyg
 */
public class FilterUtil {

    private static final String TAG = "FilterUtil";

    /**
     * 过滤socket接收回来的消息
     *
     * @param context
     * @param message
     * @return 返回是否需要向上派发消息 true表示需要派发,false表示不需要派发
     */
    protected static boolean filterReceiveSocket(Context context, TransportMessage message) {

        boolean isNeedDispatch = true;
        int id = message.getMethodId();

        switch (id) {
            case MessageID.LOGINSESSION_Y:// session连接服务
            case MessageID.LOGINSESSION_N:
//                if (MessageID.LOGINSESSION_Y == id){
//                    updateDeviceToken(context);
//
//                }

                ConnectSession.getInstance(context).sessionResponse(context, message);
                break;
            case MessageID.SESSION_REGET_KEY: // session或group重登
            case MessageID.GROUP_REASSIGN:
                try {
                    CommonFunction.log(TAG, "filterReceiveSocket() no permission == " + message.getMethodId(), ConnectorManage.getInstance(context).getKey());
                    ConnectLogin.getInstance(context).reDoLogin(context, false);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
                break;
            case MessageID.SESSION_PUSH_MEET: {
                try {
                    JSONObject json = new JSONObject(message.getContentBody());
                    int optInt = json.optInt("num");
                    long time = json.optLong("time");
                    if (time != 0) {
                        MeetGameModel.getInstance().saveMeetGame(context, json, id);
                    }
                    Common.getInstance().setMeetWantCount(optInt);
                    refreshSlidingMenuAndChildViewBadge();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            case MessageID.SESSION_DYNAMIC_MESSAGE:// 邂逅游戏或最新动态消息数量推送
            case MessageID.SESSION_WANT_MEET: {
                try {
                    JSONObject json = new JSONObject(message.getContentBody());
                    if (id == MessageID.SESSION_DYNAMIC_MESSAGE) {
                        Common.getInstance().setDynamicCount(
                                Common.getInstance().getDynamicCount() + json.optInt("num"));
                    } else if (id == MessageID.SESSION_WANT_MEET) {
                        Common.getInstance().setMeetGameCount(json.optInt("num"));
                        MeetGameModel.getInstance().saveMeetGame(context, json, id);
                    } else {

                    }
                    refreshSlidingMenuAndChildViewBadge();
                } catch (Exception e) {
                    if (id == MessageID.SESSION_DYNAMIC_MESSAGE) {
                        Common.getInstance()
                                .setDynamicCount(Common.getInstance().getDynamicCount() + 1);
                    } else {
                        Common.getInstance()
                                .setMeetGameCount(Common.getInstance().getMeetGameCount() + 1);
                    }
                }
            }
            break;
            case MessageID.PUSH_SESSION_DYNAMIC_MESSAGE: {// 动态消息
                try {
                    SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
                    JSONObject json = new JSONObject(message.getContentBody());
                    int sorttype = json.optInt("sorttype");
                    int num = json.optInt("num");
                    if (sp.has(SharedPreferenceUtil.DYNAMIC_NOTICE)) {
                        if (sp.getBoolean(SharedPreferenceUtil.DYNAMIC_NOTICE) == true) {
                            if (sorttype == 1) {// 最近来访
                                VisitorUser vUser = GsonUtil.getInstance()
                                        .getServerBean(CommonFunction.jsonOptString(json, "user"),
                                                VisitorUser.class);
                                if (vUser != null) {
                                    if (!CommonFunction.isEmptyOrNullStr(vUser.icon)) {
                                        Common.getInstance().loginUser
                                                .setNewVisitorsIcon(vUser.icon);
                                        Common.getInstance().loginUser.setHasNewVisitors(true);
                                        refreshSlidingMenuAndChildViewBadge();
                                    }
                                }
                            }
                            if (sorttype == 2) {
                                Common.getInstance().setmMyComments(
                                        Common.getInstance().getmMyComments() + num);

                                DynamicModel.getInstent().insertOrUpdateOneRecord(context, json);

                                NotificationFunction.getInstatant(context)
                                        .showNotification(message);
                                refreshSlidingMenuAndChildViewBadge();
                            }
                            if (sorttype == 3) {
                                // 好友动态（协议中user字段与最近来访一样）
                                VisitorUser vUser = GsonUtil.getInstance()
                                        .getServerBean(CommonFunction.jsonOptString(json, "user"),
                                                VisitorUser.class);
                                if (vUser != null) {
                                    Common.getInstance().loginUser
                                            .setNewFriendDynamicIcon(vUser.icon);
                                    Common.getInstance().loginUser.setHasNewFriendDynamic(true);
                                }
                                Common.getInstance().setmDynamicViewCount(
                                        Common.getInstance().getmDynamicViewCount() + num);
                                refreshSlidingMenuAndChildViewBadge();
                            }
                            if (sorttype == 4) {

                            }
                        }
                    } else {
                        sp.putBoolean(SharedPreferenceUtil.DYNAMIC_NOTICE, true);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case MessageID.GROUP_PUSH_HISTORY: {
                // 需要更新增加的数量
                handleReceiveGroupMessage(context, message);
                refreshSlidingMenuAndChildViewBadge();
            }
            break;
            case MessageID.GROUP_PUSH_MESSAGE: {
                try {
                    handleReceiveOneGroupMessage(context, message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                if (activity instanceof GroupHelperActivity) {
                    if (((GroupHelperActivity) activity).instant != null)
                        ((GroupHelperActivity) activity).instant.handler
                                .sendEmptyMessage(GroupHelperActivity.REFRESH_DATA);
                }

                refreshSlidingMenuAndChildViewBadge();
            }
            break;
            case MessageID.SESSION_GROUP_ON_LINE_RESULT:// 不在聊吧内上线下线消息
            case MessageID.SESSION_GROUP_OFF_LINERESULT:
                Activity groupChatTopicActivity = CloseAllActivity.getInstance().getSecondActivity();
                if (groupChatTopicActivity instanceof GroupChatTopicActivity) {
                    if (((GroupChatTopicActivity) groupChatTopicActivity).instant != null)
                        ((GroupChatTopicActivity) groupChatTopicActivity).instant.onReceiveMessage(message);
                }

                break;
            case MessageID.SESSION_PRIVATE_CHAT: // 接收私聊消息
            {
                try {
                    long locid = handleReceiveMessage(context, message);
                    if (locid <= 0) {
                        isNeedDispatch = false;
                    }
                    refreshSlidingMenuAndChildViewBadge();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case MessageID.SESSION_PUSH_CHAT_PERSION_READED:// 推送已读消息
                JSONObject json;
                try {
                    // 该协议下发的messageid是服务端当前统计所有私聊消息的最大id
                    json = new JSONObject(message.getContentBody());
                    long messageId = json.getLong("messageid");
                    long userId = json.getLong("readuserid");

                    String mUId = String.valueOf(Common.getInstance().loginUser.getUid());

                    // 根据服务端下发的服务端最大的id，找出本地 <=该id的localid消息
                    String serverId = String.valueOf(messageId);
                    String fUID = String.valueOf(userId);

                    long localMaxId = ChatPersonalModel.getInstance().getLocalMaxId(context, mUId, fUID, serverId);

                    // 把 localMaxId以上的所有消息，状态不为失败的消息都改为已读
                    ChatPersonalModel.getInstance().modifyMessageBigId(context, fUID, localMaxId);

                    // 判断最近联系人的消息状态
                    ChatRecord record = ChatPersonalModel.getInstance().getRecordByLocalId(context, String.valueOf(localMaxId));

                    if (record != null) {
                        long dateTime = record.getDatetime();
                        ChatPersonalModel.getInstance().updateNearChatStatus(context, mUId, fUID, dateTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                refreshSlidingMenuAndChildViewBadge();
                break;
            case MessageID.SESSION_ADD_FANS:// 未读粉丝数量
            {
                String content = message.getContentBody();
                NewFansListBean bean = GsonUtil.getInstance()
                        .getServerBean(content, NewFansListBean.class);
                NewFansModel.getInstance()
                        .receiveData(context, bean, Common.getInstance().loginUser.getUid());
                refreshSlidingMenuAndChildViewBadge();
            }
            break;
            case MessageID.SESSION_OFFSET_GEO_RETURN:// 经纬度校正
                StartModel.getInstance().paramOffsetGeo(message.getContentBody());
                break;
            case MessageID.SESSION_KEYWORD_Y:// 敏感关键字
                StartModel.getInstance().handlerKeyWord(context, message.getContentBody());
                break;
            case MessageID.ADMIN_FORBID_SAY:// 禁言
                StartModel.getInstance().setForbidTime(message.getContentBody());
                break;
            case MessageID.SESSION_AD:// 广告
//				AdModel.getInstance( context ).paramAd( message.getContentBody( ) );
                break;
            case MessageID.GROUP_LOGIN_Y:// 圈子登录成功
                ConnectGroup.getInstance(context).groupLoginResponse(context, message);

//                updateDeviceToken(context);
                break;
            case MessageID.GROUP_PUSH_DELETE_MESSAGE: // 删除圈子
                GroupModel.getInstance().parseJsonDeleteGroupMessage(context, message);
                break;
            case MessageID.SESSION_TURN_VIP: // 接收到vip
                if (!CommonFunction.isEmptyOrNullStr(message.getContentBody())) {
                    try {
                        JSONObject vipJson = new JSONObject(message.getContentBody());
                        Common.getInstance().loginUser.setViplevel(vipJson.optInt("viplevel"));

                        if (MainFragmentActivity.sInstance != null) {
                            MainFragmentActivity.sInstance.freshSpaceVipFlag();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case MessageID.SESSION_NEW_FACE_NOTICE:// 新贴图通知
                try {
                    JSONObject jsonObject = new JSONObject(message.getContentBody());
                    int newFaceCount = jsonObject.optInt("num");
                    CommonFunction
                            .log("face", "get new face count:" + newFaceCount + " from server");
                    Common.newFaceCount = newFaceCount;
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            // 4.2
            case MessageID.SESSION_NEED_VERIFY_CODE:// 需验证验证码
//				VerifyCodeActivity.launch( context, iVerifyCodeCallback );
                break;
            case MessageID.SESSION_PUSH_WARN_USER_NUMBER:// 推送警告用户次数：
                try {
                    // 服务端每次登陆推送一次，次数是固定的，也就是每次登陆都是一样的次数，需要客户端记录。
                    JSONObject jsonObject = new JSONObject(message.getContentBody());
                    int pushCount = jsonObject.optInt("num");
                    long time = System.currentTimeMillis();

                    long uid = Common.getInstance().loginUser.getUid();
                    String pushNumberKey = SharedPreferenceUtil.PUSH_WARN_NUMBER + uid;
                    String pushTimeKey = SharedPreferenceUtil.PUSH_WARN_TIME + uid;

                    SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);

                    if (!sp.has(pushTimeKey) ||
                            !TimeFormat.IsAfterTodayTime(sp.getLong(pushTimeKey))) {
                        sp.putLong(pushTimeKey, time);
                        sp.putInt(pushNumberKey, pushCount);
                    }
                    Common.pushCount = pushCount;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case MessageID.SESSION_AD_OUT_OF_STOCK:
                handleADOutOfStock(context, message.getContentBody());
                break;
            case MessageID.SESSION_PUSH_GROUP_NEW_MEMBER:
                // 6.0新增成员及审核由圈通知下发@by tanzy
                handleMemberPush(context, message.getContentBody());
                break;
            case MessageID.SESSION_PUSH_AGREED_ADD_GROUP:
                handleAgreedNewGroup(context);
                break;
            case MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC:// 推送用户未读动态
            {

                String result = message.getContentBody();
                DynamicNewNumberBean bean = GsonUtil.getInstance()
                        .getServerBean(result, DynamicNewNumberBean.class);
                if (bean != null) {
                    bean.time = TimeFormat.getCurrentTimeMillis();
                    DynamicModel.getInstent().setNewNumBean(bean);
                }
                // 显示通知栏通知
                NotificationFunction.getInstatant(context).showNotification(message);

                // 刷新动态中心-我的动态Bar,我的动态中左上角的数字
                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                if (activity instanceof MainFragmentActivity) {
                    ((MainFragmentActivity) activity).freshAllCount();
                }

                // 如果当前页面是发现页面，就需要刷新一下发现页面
                refreshSlidingMenuAndChildViewBadge();
            }
            break;
            case MessageID.SESSION_PUSH_UNREAD_RECOMMEND_FRIEND: {
                String result = message.getContentBody();
                try {
                    JSONObject obj = new JSONObject(result);
                    int num = obj.optInt("num", 0);
                    Common.getInstance().loginUser.setNewRecommendFriendNum(num);
                    refreshSlidingMenuAndChildViewBadge();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case MessageID.SESSION_PUSH_POSTBAR_GROUP: {
                String result = message.getContentBody();
                try {
                    JSONObject obj = new JSONObject(result);
                    int num = obj.optInt("num", 0);
                    Common.getInstance().setmPostbarTopicCount(num);
                    refreshSlidingMenuAndChildViewBadge();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
//            case MessageID.SESSION_PUSH_GROUP_TOPIC_UNREAD:// 推送圈话题关于我的未读数
//            {
//                String result = message.getContentBody();
//                try {
//                    JSONObject obj = new JSONObject(result);
//                    int num = obj.optInt("num", 0);
//                    int likeNum = obj.optInt("likenum", 0);
//                    TopicModel.getInstance().setTopicAboutMe(num);
//                    TopicModel.getInstance().setLikeNum(likeNum);
//                    Activity activity = CloseAllActivity.getInstance().getTopActivity();
//                    if (activity instanceof GroupListActivity) {
//                        CommonFunction.log(TAG, "refresh GroupListActivity");
//                        ((GroupListActivity) activity).updateNewMessage();
//                    }
//                    refreshSlidingMenuAndChildViewBadge();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            break;
            // 收到搭讪关系推送后，告诉服务端
            case MessageID.SESSION_PUSH_ACCOST_RELATION: {
                String contentJson = message.getContentBody();
                Relation bean = GsonUtil.getInstance()
                        .getServerBean(contentJson, Relation.class);
                ChatPersonalModel.getInstance()
                        .putAccostRelation(context, bean.userid, bean.ischat);

                if (bean.type == 10) {// 因发送钻石礼物而解除
                    showSendDiamondCanTalkDialog();
                }

                // 还需要处理数据库，把想要的消息放到对应的分组中去
                ChatPersonalModel.getInstance().changeContactSubgroup(context, bean.userid, SubGroupType.NormalChat);

                SocketSessionProtocol.seesionSendGetRelationReport(context, bean.userid);
                refreshSlidingMenuAndChildViewBadge();
            }
            break;
            case MessageID.SESSION_PUSH_GROUP_NOTICE_MESSAGE: {// 接收到圈通知消息
                CommonFunction.log(TAG, "recieve SESSION_PUSH_GROUP_NOTICE_MESSAGE");
                String content = message.getContentBody();
                GroupNoticeListBean bean = GsonUtil.getInstance()
                        .getServerBean(content, GroupNoticeListBean.class);
                GroupAffairModel.getInstance().recieveNotice(context, bean.messages);


                Activity activity = CloseAllActivity.getInstance().getTopActivity();
                if (activity instanceof GroupNoticeActivity) {
                    ((GroupNoticeActivity) activity).handler
                            .sendEmptyMessage(GroupNoticeActivity.MSG_INITDATA);
                }

                //gh 不在聊吧内修改用户角色
                Activity groupChatTopicActivity1 = CloseAllActivity.getInstance().getSecondActivity();
                if (groupChatTopicActivity1 instanceof GroupChatTopicActivity) {
                    if (((GroupChatTopicActivity) groupChatTopicActivity1).instant != null)
                        ((GroupChatTopicActivity) groupChatTopicActivity1).instant.onReceiveMessage(message);
                }
                refreshSlidingMenuAndChildViewBadge();
            }
            break;

            case MessageID.GROUP_GET_HISTORY_MESSAGES_SUCCESS:
                handleGroupMessageRequest(context, message);
                break;

            case MessageID.SESSION_PUHS_VIP_EXPIRE:// 推送包月会员过期
                Common.getInstance().loginUser.setSVip(0);

                if (MainFragmentActivity.sInstance != null) {
                    MainFragmentActivity.sInstance.freshSpaceVipFlag();
                }
                break;
            case MessageID.SESSION_PUHS_PAY_RESULT:// 推送推送充值结果 会员或者钻石
                PayResultBean resultBean = GsonUtil.getInstance()
                        .getServerBean(message.getContentBody(), PayResultBean.class);
                if (resultBean.goodstype == 2) {
                    if (resultBean.expiredtime > 0) {
                        Common.getInstance().loginUser.setSVip(1);
                        EventBus.getDefault().post(new HeadImageNotifyEvent());
                        if (MainFragmentActivity.sInstance != null) {
                            MainFragmentActivity.sInstance.freshSpaceVipFlag();
                        }
                    }
                }
                break;
            case MessageID.SESSION_PUHS_NEW_CREDITS://推送是否有新的积分产生
                String result = message.getContentBody();
                JSONObject obj;
                try {
                    obj = new JSONObject(result);
                    double credits = obj.optDouble("credits");
                    SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
                    String Uid = String.valueOf(Common.getInstance().loginUser.getUid());
                    String creditsKey = SharedPreferenceUtil.NEW_CREDITS_PRODUCE + Uid;
                    String key = SharedPreferenceUtil.SHOWCREDITS_AVAILABLE;//兑吧是否开启
                    int isShowDuiba = SharedPreferenceUtil.getInstance(context).getInt(key);
                    if (credits > 0 && isShowDuiba == 1) {
                        sp.putBoolean(creditsKey, true);
                        refreshSlidingMenuAndChildViewBadge();
                    } else {
                        sp.putBoolean(creditsKey, false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case MessageID.SESSION_GROUP_SEND_PACAGE_GIFT_SUCCESS:
                break;
            case MessageID.SESSION_GROUP_WORLD_MESSAGE:
                Activity groupChatTopicActivity1 = CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                if (null != groupChatTopicActivity1 && groupChatTopicActivity1 instanceof GroupChatTopicActivity) {
                    if (((GroupChatTopicActivity) groupChatTopicActivity1).instant != null)
                        ((GroupChatTopicActivity) groupChatTopicActivity1).instant.onReceiveMessage(message);
                }
                break;
            case MessageID.SESSION_PRIVATE_VERSION_UPDATE:
            case MessageID.SESSION_GROUP_VERSION_UPDATE:
                //非Google渠道的会提示更新
                if (!Config.isShowGoogleApp) {
                    // Socket协议更新版本
                    String update = message.getContentBody();
                    UIMainHandler handler = new UIMainHandler(context);
                    Message msg = new Message();
                    msg.what = handler.UPDATE_INSTALL_SOCKET;
                    msg.obj = update;
                    handler.sendMessageDelayed(msg, 3000);
                }

                break;

        }

        // 拦截群组消息
        if (GroupModel.getInstance().isInGroupChat() &&
                (id == MessageID.GROUP_PUSH_MESSAGE || id == MessageID.GROUP_PUSH_DISSOLVE ||
                        id == MessageID.GROUP_PUSH_KICK)) {
            GroupModel.getInstance().handleMessage(message);
        }

        // 将部分需要提示的消息过滤出来
        if (IsNeedHandleInUIThread(id)) {
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = id;
            msg.obj = message;
            handler.sendMessage(msg);
        }

        return isNeedDispatch;
    }

    /**
     * 更新推送设备Token
     *
     * @param context
     */
    public static void updateDeviceToken(Context context, HttpCallBack httpCallBack) {
        String token = Common.getInstance().getDeviceToken();
        if (token != null && !TextUtils.isEmpty(token)) {
            if (RomUtils.isHuaweiRom()) {
//                SocketSessionProtocol.uploadHuaweiToken(context, token, "3");
                PushHttpProtocol.pushDeviceType(context, token, "3", httpCallBack);
            } else if (RomUtils.isFlymeRom()) {
//                SocketSessionProtocol.uploadHuaweiToken(context, token, "1");
                PushHttpProtocol.pushDeviceType(context, token, "1", httpCallBack);
            } else if (RomUtils.isMiuiRom()) {
//                SocketSessionProtocol.uploadHuaweiToken(context, token, "2");
                PushHttpProtocol.pushDeviceType(context, token, "2", httpCallBack);
            } else {
//                SocketSessionProtocol.uploadHuaweiToken(context, token, "0");
                PushHttpProtocol.pushDeviceType(context, token, "0", httpCallBack);
            }
        }
    }

    /**
     * 过滤http接收的消息
     **/
    @SuppressWarnings("unchecked")
    protected static void filterReceiveHttp(Context context, String url, String result, long flag,
                                            Request<?> request) {
        if (url.contains("/user/privacy/get_5_4")) {
            //CommonFunction.log(TAG, "filterReceiveHttp() result--------" + result);
            StartModel.getInstance().paramPrivacy(context, result);
        } else if (IsNeedUpdate(url)) {//检查版本号
            /** 版本更新 **/
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = handler.UPDATE_INSTALL;
            msg.obj = result;
            handler.sendMessageDelayed(msg, 3000);
        } else if (url.contains("/login/login")) {
            try {
                // 登录
                ConnectLogin.getInstance(context).doLoginResponse(context, result, url, flag);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.contains("/software/activate")) {
            // 设备激活，更新本地
//			StartModel.getInstance( ).updateDeviceLocal( context, result );//jiqiang
        } else if (url.contains("/group/checkforbid")) {
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = handler.GROUP_CHECK_FORBID;
            msg.obj = result;
            handler.sendMessageDelayed(msg, 3000);
        } else if (url.contains("/user/logo/update_1_3")) {
            // 修改头像，获取完成度
            if (!CommonFunction.isEmptyOrNullStr(result)) {
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.optInt("status") == 200) {
                        UserBufferHelper.getInstance().save(Common.getInstance().loginUser);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (url.contains(BusinessHttpProtocol.sProtocolGetUserFace)) {

            ArrayList<FaceLogoIcon> faces = new ArrayList<FaceLogoIcon>();
            HashMap<String, Object> hasMap = FaceCenterModel
                    .getInstance(context).getRes(result, flag);
            if (hasMap != null) {
                faces = (ArrayList<FaceLogoIcon>) hasMap.get("faces");

            }
            if (faces != null) {
                FaceManager.mOwnGifFaces.addAll(faces);
            }
        } else if (url.contains("/user/info/basic_")) {
            try {
                //CommonFunction.log(TAG, "filterReceiveHttp() basic_6_0:===" + result);
                JSONObject jsonObject = new JSONObject(result);
                JSONObject userJsonObject = null;
                if (jsonObject.optJSONObject("user") != null) {
                    userJsonObject = jsonObject.optJSONObject("user");
                }

                if (Common.getInstance().loginUser != null && userJsonObject != null) {
                    if (Common.getInstance().loginUser.getUid() == userJsonObject.optInt("userid")) {
                        UserBasic userbasic = GsonUtil.getInstance().getServerBean(result, UserBasic.class);
                        if (userbasic != null && userbasic.isSuccess()) {
                            userbasic.setUserBasicInfor(context, Common.getInstance().loginUser);
                        }

                        if (MainFragmentActivity.sInstance != null) {
                            MainFragmentActivity.sInstance.freshSpaceVipFlag();
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } else if (url.contains("/group/message/optionlist")) {// 获取圈消息接收状态
            try {
                GroupsMsgStatusBean bean = GsonUtil.getInstance().getServerBean(result, GroupsMsgStatusBean.class);
                if (bean != null && bean.isSuccess()) {
                    GroupAffairModel.getInstance().groupsMsgStatus = bean;
                    GroupAffairModel.getInstance().saveStatusToFile();
                    refreshSlidingMenuAndChildViewBadge();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (url.contains("/postbar/topic/like/add") ||
                url.contains("/user/dynamic/love/add") ||
                url.contains("/user/dynamic/love/cancel") ||
                url.contains("/postbar/topic/like/cancel")) {//动态点赞操作
            handleLikeSuccess(flag, result);
            return;
        } else if (url.contains("/v1/ad/index")) {
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = handler.MAIN_AD_RESULT;
            msg.obj = result;
            handler.sendMessage(msg);
        } else if (url.contains("v1/system/load")) {
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = handler.SPLASH_RESULT;
            msg.obj = result;
            handler.sendMessage(msg);
        } else if (url.contains("/postbar/topic/like/add")) {
            return;
        }

        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (null != bean && !bean.isSuccess()) {
            if (bean.error == 911110) { //需要验证码
                TransportMessage noReadMessage = new TransportMessage();
                noReadMessage.setId(911110);
                noReadMessage.setContentBody(result);

                UIMainHandler handler = new UIMainHandler(context);
                Message msg = new Message();
                msg.what = MessageID.VERIFY_JUMP_WEB_VIEW;
                msg.obj = noReadMessage;
                handler.sendMessage(msg);
            } else if (bean.error == 4100 && !MainFragmentActivity.getIsLogot()) {    // 如果处于登出状态，不重新登录
                try {
                    ConnectLogin.getInstance(context).reDoLogin(context, true);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
            // 拦截异常数据
            if (context == null) {
                context = BaseApplication.appContext;
            }
            ErrorCode.showError(context, result);
        } else if (null == bean) {
            CommonFunction.log(TAG, "filterReceiveHttp() server return NULL, result=" + result);
        }
    }

    /**
     * 过滤接http请求错误
     *
     * @param e
     * @param flag
     * @return void
     */
    protected static void filterReceiveHttpError(Context context, int e, long flag) {
        // 当前正在请求的才使用错误提示，避免多次请求导致多次错误提示
        if (flag == ConnectorManage.getInstance(context).getHttpRequestCount()) {
            UIMainHandler handler = new UIMainHandler(context);
            Message msg = new Message();
            msg.what = e;
            handler.sendMessage(msg);
        }
    }

    /**
     * @Title: refreshSlidingMenu
     * @Description: 更新首页侧栏显示 和 子View的Badge
     */
    public static void refreshSlidingMenuAndChildViewBadge() {
        try {
            if (CloseAllActivity.getInstance().getTopActivity() instanceof MainFragmentActivity) {
                ((MainFragmentActivity) CloseAllActivity.getInstance().getTopActivity()).freshAllCount();
            } else {
                Activity activity = CloseAllActivity.getInstance().getTargetActivityOne(MainFragmentActivity.class);
                if (activity != null)
                    ((MainFragmentActivity) activity).freshAllCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 判断是否有需要在主线程（UI Thread）中处理;
    private static boolean IsNeedHandleInUIThread(int id) {
        // 推送私聊消息
// 积分提示
// 推送解散圈子
// 推送踢人消息
// 服务器异常需要重新登录
// 圈消息
// 圈通知
// 未读动态消息
// 未读侃啦消息
        return id == MessageID.SESSION_PRIVATE_MESSAGE ||
                id == MessageID.PUSH_SESSION_DYNAMIC_MESSAGE || id == MessageID.SESSION_PRIVATE_CHAT
                // 推送私聊消息
                || id == MessageID.SESSION_SCORE // 积分提示
                || id == MessageID.GROUP_PUSH_DISSOLVE // 推送解散圈子
                || id == MessageID.GROUP_PUSH_KICK // 推送踢人消息
                || id == MessageID.SESSION_EXCEPTION // 服务器异常需要重新登录
                || id == MessageID.GROUP_SERVER_EXCEPTION || id == MessageID.SESSION_FORCE_OUT ||
                id == MessageID.SESSION_OTHER_PROBLEM || id == MessageID.GROUP_OTHER_PROBLEM ||
                id == MessageID.ADMIN_FORCE_OUT || id == MessageID.GROUP_PUSH_FORBID_SAY ||
                id == MessageID.SESSION_PRINVATE_NO_READ || id == MessageID.SESSION_WANT_MEET ||
                id == MessageID.SESSION_PUSH_MEET || id == MessageID.SESSION_RECEIVE_EXPIRED ||
                id == MessageID.SESSION_RECEIVE_NEWGAME_COUNT ||
                id == MessageID.SESSION_PUSH_GROUP_NEW_MEMBER ||
                id == MessageID.SESSION_PUSH_TRANSFER_GROUP || id == MessageID.GROUP_PUSH_MESSAGE// 圈消息
                || id == MessageID.SESSION_PUSH_GROUP_NOTICE_MESSAGE // 圈通知
                || id == MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC// 未读动态消息
                || id == MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES// 未读侃啦消息
                || id == MessageID.SESSION_ADD_FANS || id == MessageID.CHATBAR_PUSH_INVITATION ||
                id == MessageID.VERIFY_JUMP_WEB_VIEW || id == MessageID.SESSION_GROUP_CALL_MESSAGE;
    }

    // 是否需要更新
    private static boolean IsNeedUpdate(String url) {
        return url.contains("/system/version_");
    }

    /**
     * 处理广告下架推送
     */
    private static void handleADOutOfStock(Context mContext, String result) {
    }

    /**
     * 推送圈子新成员数量和未审核成员数量消息
     */
    private static void handleMemberPush(Context mContext, String result) {
    }

    /**
     * 推送同意加入圈子
     */
    private static void handleAgreedNewGroup(Context mContext) {
        GroupModel.getInstance().isNeedRefreshGroupList = true;
        refreshSlidingMenuAndChildViewBadge();
    }

    /**
     * 接收私聊消息
     *
     * @throws IOException
     * @throws JSONException
     */
    private static long handleReceiveMessage(Context mContext, TransportMessage msg) throws IOException {

        String content = msg.getContentBody();

        PrivateChatMessage bean = JSON.parseObject(content, PrivateChatMessage.class);

        // 告诉服务器，已经收到消息
        SocketSessionProtocol.sessionPrivateVilify(mContext, String.valueOf(bean.msgid));

        // 语音消息，需要把data字段的数据保存成文件
        if (bean.type == ChatMessageType.SOUND) {
            String strData = bean.data;
            if (!TextUtils.isEmpty(strData)) {
                // 如果数据不为空的，就用data的数据，否者就用原本的attachment
                byte[] byteData = CryptoUtil.Base64Decoder(strData);

                String fileName =
                        PathUtil.getAudioDir() + bean.datetime + PathUtil.getMP3Postfix();
                File dataFile = new File(fileName);
                dataFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(dataFile);
                fos.write(byteData);
                fos.close();

                // 把数据字段设置为空，不然当语音比较长的时候会出现读写数据库出错的情况
                bean.data = "";
                bean.attachment = fileName;
            }
        }

        String modifyContent = GsonUtil.getInstance().getStringFromJsonObject(bean);

        msg.setContentBody(modifyContent);

        String uid = String.valueOf(Common.getInstance().loginUser.getUid());
        // 判断收到搭讪时是否需要添加未读消息数，主要为收到搭讪只添加人数
        String fuid = bean.user.userid + "";
        boolean need = ChatPersonalModel.getInstance().needAddNoRead(mContext, fuid);
        long locid = 0;
        if (bean.type == 15) {
            //技能消息
            String result = (String) bean.content;
            if (result != null)
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String skillResult = jsonObject.getString("content");
                    if (skillResult != null) {
                        SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(skillResult, SkillAttackResult.class);
                        // TODO: 2017/8/31 如果发送者是当前登陆者需要插入到自己发送的消息记录
                        if (skillAttackResult.user.UserID == Common.getInstance().loginUser.getUid()) {
                            //设置被攻击用户
                            User fUser = new User();
                            fUser.setNickname(skillAttackResult.targetUser.NickName);
                            fUser.setIcon(skillAttackResult.targetUser.ICON);
                            fUser.setSex("m".equals(skillAttackResult.targetUser.Gender) ? 1 : 2);
                            fUser.setSVip(skillAttackResult.targetUser.VIP);
                            fUser.setViplevel(skillAttackResult.targetUser.VipLevel);
                            fUser.setAge(skillAttackResult.targetUser.Age);
                            fUser.setUid(skillAttackResult.targetUser.UserID);
                            //设置聊天内容
                            ChatRecord record = new ChatRecord();
                            Me me = Common.getInstance().loginUser;
                            record.setId(-1); // 消息id
                            record.setUid(me.getUid());
                            record.setNickname(me.getNickname());
                            record.setIcon(me.getIcon());
                            record.setVip(me.getViplevel());
                            record.setDatetime(System.currentTimeMillis());
                            record.setType(Integer.toString(ChatRecordViewFactory.USER_SKILL_MSG));
                            record.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
                            record.setContent(result);
                            record.setUpload(false);
                            if (skillAttackResult.user != null) {
                                record.setfLat(Common.getInstance().loginUser.getLat());
                                record.setfLng(Common.getInstance().loginUser.getLng());
                                GeoData geo = LocationUtil.getCurrentGeo(mContext);
                                record.setDistance(LocationUtil.calculateDistance(Common.getInstance().loginUser.getLng(), Common.getInstance().loginUser.getLat(), geo.getLng(), geo.getLat()));
                            }
                            locid = ChatPersonalModel.getInstance().insertOneRecord(mContext, fUser, record, MessageBelongType.SEND, 1);
                        } else {
                            locid = ChatPersonalModel.getInstance().insertOneRecord(mContext, uid, msg, ChatRecordStatus.NONE);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
        } else if (bean.type == 21) {

            String result = bean.attachment;
            if (!TextUtils.isEmpty(result)) {

                GameOrderMessageBean orderMessageBean = GsonUtil.getInstance().getServerBean(result, GameOrderMessageBean.class);
                //2018/8/24 如果发送者是当前登陆者需要插入到自己发送的消息记录
                if (orderMessageBean != null && orderMessageBean.senderId == Common.getInstance().loginUser.getUid()) {
                    //设置对话用户
                    User fUser = new User();
                    fUser.setNickname(bean.user.getNickname());
                    fUser.setIcon(bean.user.getIcon());
                    fUser.setSex("m".equals(bean.user.getGender()) ? 1 : 2);
                    fUser.setSVip(bean.user.vip);
                    fUser.setViplevel(bean.user.viplevel);
                    fUser.setAge(bean.user.age);
                    fUser.setUid(bean.user.userid);
                    //设置聊天内容
                    ChatRecord record = new ChatRecord();
                    Me me = Common.getInstance().loginUser;
                    record.setId(-1); // 消息id
                    record.setUid(me.getUid());
                    record.setNickname(me.getNickname());
                    record.setIcon(me.getIcon());
                    record.setVip(me.getViplevel());
                    record.setDatetime(System.currentTimeMillis());
                    record.setType(Integer.toString(ChatRecordViewFactory.GAME_ORDER_TIP));
                    record.setStatus(ChatRecordStatus.ARRIVED); // 发送中改为送达
                    record.setContent((String) bean.content);
                    record.setUpload(false);
                    locid = ChatPersonalModel.getInstance().insertOneRecord(mContext, fUser, record, MessageBelongType.SEND, 1);

                } else {
                    locid = ChatPersonalModel.getInstance().insertOneRecord(mContext, uid, msg, ChatRecordStatus.NONE);
                }
            }
        } else if (bean.type != 23) {//不存储服务器发过来的类型为23的消息
            locid = ChatPersonalModel.getInstance().insertOneRecord(mContext, uid, msg, ChatRecordStatus.NONE);
        }

        // 插入失败就返回
        if (locid <= 0) {
            return locid;
        }

        if (need)
            Common.getInstance().setNoReadMsgCount(Common.getInstance().getNoReadMsgCount() + 1);

        NotificationFunction.getInstatant(mContext).showNotification(msg);

        // 发送攻击技能消息
        UIMainHandler handler = new UIMainHandler(mContext);
        Message message = new Message();
        message.what = handler.SKILL_ATTACT_SOCKET;
        message.obj = bean;
        handler.sendMessage(message);
        return locid;
    }

    /**
     * 处理推送圈子历史消息
     */
    private static void handleReceiveGroupMessage(Context mContext, TransportMessage msg) {
        String content = msg.getContentBody();
        GroupHistoryMessagesBean bean = JSON.parseObject(content, GroupHistoryMessagesBean.class);

        if (bean == null)
            return;

        ArrayList<GroupChatMessage> list = bean.getMessageList();
        if (list == null) {
            return;
        }
        ArrayList<GroupChatMessage> insertList = new ArrayList<GroupChatMessage>();
        int count = list.size();
        long groupId = bean.groupid;
        for (int i = 0; i < count; i++) {
            long messageId = list.get(i).msgid;
            long increaseId = list.get(i).incmsgid;
            //小秘书消息的msgid为-1,自增id为0,这些消息不判断是否重复
            if (messageId <= 0 || increaseId <= 0 || !GroupChatListModel.getInstance()
                    .checkMessageIsExist(mContext, groupId, messageId, increaseId)) {
                if (list.get(i).type == 1 || list.get(i).type == 9 || list.get(i).type == 10 || list.get(i).type == 13) {
                    insertList.add(list.get(i));
                    CommonFunction.myWriteLog(TAG, getGroupMsgLog("handleReceiveGroupMessage", -1, groupId, messageId, increaseId));
                }

            }
        }
        GroupChatListModel.getInstance().BatchInsertOneRecord(mContext, insertList);

        boolean isBeAt = false;
        String mUserIdStr = String.valueOf(Common.getInstance().loginUser.getUid());

        for (int i = 0; i < list.size(); i++) {
            GroupChatMessage item = list.get(i);
            if (item.getReply().contains(mUserIdStr)) {
                isBeAt = true;
                break;
            }
        }

        // 更新最近圈聊的表
        if (list.size() > 0) {
            GroupChatMessage lastItem = list.get(list.size() - 1);

            String contentStr = GsonUtil.getInstance().getStringFromJsonObject(lastItem);
            GroupModel.getInstance()
                    .UpdateGroupContact(mContext, lastItem.groupid, bean.getGroupName(),
                            bean.getIcon(), contentStr, lastItem.datetime, bean.getAmount(),
                            ChatRecordStatus.NONE, isBeAt);
        }

    }

    /**
     * 处理推送圈子历史消息
     */
    private static void handleGroupMessageRequest(Context mContext, TransportMessage msg) {

        String content = msg.getContentBody();
        CommonFunction.log(TAG, "handleGroupMessageRequest have get receive");
//		GroupHistoryMessagesBean bean = GsonUtil.getInstance( )
//			.getServerBean( content, GroupHistoryMessagesBean.class );
        GroupHistoryMessagesBean bean = JSON.parseObject(content, GroupHistoryMessagesBean.class);
        ArrayList<GroupChatMessage> list = bean.getMessageList();
        ArrayList<GroupChatMessage> insertList = new ArrayList<GroupChatMessage>();
        int count = list.size();
        long groupId = bean.groupid;
        for (int i = 0; i < count; i++) {
            long messageId = list.get(i).msgid;
            long increaseId = list.get(i).incmsgid;
            // 小秘书消息的msgid为-1,自增id为0,这些消息不判断是否重复
            if (messageId <= 0 || increaseId <= 0 || !GroupChatListModel.getInstance()
                    .checkMessageAndIncreaseIsExist(mContext, groupId, messageId, increaseId)) {
                if (list.get(i).type == 1 || list.get(i).type == 9 || list.get(i).type == 10 || list.get(i).type == 13)
                    insertList.add(list.get(i));
                CommonFunction.myWriteLog(TAG,
                        getGroupMsgLog("handleGroupMessageRequest", -1, groupId, messageId,
                                increaseId));
            }
        }
        GroupChatListModel.getInstance().BatchInsertOneRecord(mContext, insertList);
    }

    /**
     * 处理单条圈聊消息的推送 71007
     *
     * @throws IOException
     */
    private static void handleReceiveOneGroupMessage(Context mContext, TransportMessage message) throws IOException {

        //gh 文本类型
        GroupChatMessage bean = JSON.parseObject(message.getContentBody(), GroupChatMessage.class);
        Item item = RankingTitleUtil.getInstance().getTitleItemFromChatBar(message.getContentBody());
        if (item != null) {
            bean.item = item;
        }
        if (bean.type == ChatMessageType.TEXT | bean.type == ChatMessageType.SHARE | bean.type == ChatMessageType.FACE | bean.type == ChatMessageType.TELETEXT) { //gh 过滤聊吧只接收圈文本

            // 语音消息，需要把data字段的数据保存成文件
            if (bean.type == ChatMessageType.SOUND) {
                String strData = bean.getData();
                if (!TextUtils.isEmpty(strData)) {
                    // 如果数据不为空的，就用data的数据，否者就用原本的attachment
                    byte[] byteData = CryptoUtil.Base64Decoder(strData);

                    String fileName = PathUtil.getAudioDir() + bean.datetime + PathUtil.getMP3Postfix();
                    File dataFile = new File(fileName);
                    dataFile.createNewFile();
                    FileOutputStream fos = new FileOutputStream(dataFile);
                    fos.write(byteData);
                    fos.close();

                    // 把数据字段设置为空，不然当语音比较长的时候会出现读写数据库出错的情况
                    bean.setData("");
                    bean.setAttachment(fileName);
                }
            }

            SocketGroupProtocol.groupSendGroupReceiveMaxId(mContext, bean.msgid, bean.groupid);

            GroupModel.getInstance().pushNoReadMessage(mContext, message);

            //小秘书消息的msgid为-1,自增id为0,这些消息不判断是否重复
            if (bean.msgid <= 0 || bean.incmsgid <= 0 || !GroupChatListModel.getInstance()
                    .checkMessageIsExist(mContext, bean.groupid, bean.msgid, bean.incmsgid)) {
                long localId = GroupChatListModel.getInstance().InsertOneRecord(mContext, bean);
                CommonFunction.myWriteLog(TAG, getGroupMsgLog("handleReceiveOneGroupMessage", localId, bean.groupid, bean.msgid, bean.incmsgid));
            }
        }

    }

    /**
     * 显示发送钻石礼物可聊天对话框
     */
    private static void showSendDiamondCanTalkDialog() {
    }


    public static String getGroupMsgLog(String tag, long localId, long groupId, long messageId, long increseId) {
        //LocalId == -1 表示是拉数据库下来的消息
        //如果messageId和increaseId都为0表示本地插入
        //如果groupId为0的话,表示这条记录是更新数据库的记录
        return tag + "-LocalId == " + localId + ",GroupId == " + groupId + ",messageId == " + messageId + ",increseId == " + increseId;
    }

    /**
     * 处理重新点赞操作的返回
     *
     * @param flag
     * @param result
     */
    private static void handleLikeSuccess(long flag, String result) {
        BaseServerBean bean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (bean != null) {
            if (bean.isSuccess() || bean.error == 9301 || bean.error == 9414 || bean.error == 9300) {
                HashMap<Long, Long> flagMap = DynamicModel.getInstent().getLikeSendFlagMap();
                if (flagMap.size() > 0 && flagMap.containsKey(flag)) {
                    String dynamicId = String.valueOf(flagMap.get(flag));
                    DynamicModel.getInstent().removeLikeUnSendSuccessMap(dynamicId);
                    DynamicModel.getInstent().removeLikeSendFlagMap(flag);
                    CommonFunction.log("FilterUtil", "--->收到回调移除该条数据 flag==" + flag + "**id == " + dynamicId);
                }
            }
        }
    }
}
