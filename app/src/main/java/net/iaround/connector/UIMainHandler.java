
package net.iaround.connector;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.FocusOffline;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.entity.type.GroupNoticeType;
import net.iaround.im.WebSocketManager;
import net.iaround.model.entity.AdSourceEntity;
import net.iaround.model.entity.InitBean;
import net.iaround.model.entity.VersionDetectBean;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.group.GroupOut;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupHandleActivity;
import net.iaround.ui.group.bean.GroupNoticeListBean;
import net.iaround.ui.view.dialog.CustomAdDialog;
import net.iaround.ui.view.dialog.ReceivedCallDialog;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.ui.view.floatwindow.SkillAttactBannerView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;

/**
 * 主线程UI handler处理，处理一些公共部分的消息，比如：积分提示、网络错误提示等，需要从子线程提取出来的消息进行提示
 *
 * @author linyg@iaround.net
 * @date 2012-4-11 下午2:24:08
 */
class UIMainHandler extends Handler {
    public final int UPDATE_INSTALL = 10001;
    public final int UPDATE_INSTALL_SOCKET = 10003;
    public final int GROUP_CHECK_FORBID = 10002;
    public final int SKILL_ATTACT_SOCKET = 10004;
    public final int MAIN_AD_RESULT = 10005;
    public final int SPLASH_RESULT = 10006;

    private Context context;

    private boolean isShowDialogGroupTransfer = false;

    public UIMainHandler(Context _context) {
        super(Looper.getMainLooper());
        this.context = _context;
    }

    // 处理消息
    public void handleMessage(Message msg) {
        CommonFunction.log("UIMainHandler", "id:" + msg.what);
        switch (msg.what) {
            case MessageID.VERIFY_GEETEST_VIEW:
                break;
            case MessageID.VERIFY_JUMP_WEB_VIEW:
                break;
            case MessageID.SESSION_RECEIVE_EXPIRED:
                try {
                    TransportMessage noReadMessage = (TransportMessage) msg.obj;
                    JSONObject noReadJson = new JSONObject(noReadMessage.getContentBody());
                    int type = noReadJson.optInt("type");
                    if (type == 1) {
                        focusGoDownNotify();
                    } else if (type == 2) {
                        focusExpiredNotify();
                    } else if (type == 3) {
                        vipExpiredNotify();
                    }
                } catch (JSONException e) {
                }
                break;
            case MessageID.SESSION_RECEIVE_NEWGAME_COUNT:
                try {
                    TransportMessage noReadMessage = (TransportMessage) msg.obj;
                    JSONObject noReadJson = new JSONObject(noReadMessage.getContentBody());
                    int count = noReadJson.optInt("num");
                    Common.getInstance().setNewGameCount(count);
                    if (MainFragmentActivity.sInstance != null) {
                        MainFragmentActivity.sInstance.freshAllCount();
                    }
                } catch (JSONException e) {
                }
                break;
            // 统计未读消息数量、播放声音或震动、推送私聊消息、新增粉丝、新增圈成员、邂逅消息
            // case MessageID.SESSION_PUSH_GROUP_NEW_MEMBER :
            // case MessageID.PUSH_SESSION_DYNAMIC_MESSAGE :
            // case MessageID.SESSION_WANT_MEET :
            case MessageID.SESSION_ADD_FANS:
            case MessageID.SESSION_PRIVATE_CHAT:
            case MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC:
            case MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES:
            case MessageID.SESSION_PUSH_GROUP_NOTICE_MESSAGE:
            case MessageID.CHATBAR_PUSH_NOTICE:
            case MessageID.CHATBAR_PUSH_INVITATION:
            case MessageID.GROUP_PUSH_MESSAGE: {
                NoticeSoundNVibrate(msg);
            }
            break;
            // http网络连接超时、失败、网络未打开等等
            case ErrorCode.E_100:
            case ErrorCode.E_101:
            case ErrorCode.E_103:
            case ErrorCode.E_104:
            case ErrorCode.E_105:
            case ErrorCode.E_106:
            case ErrorCode.E_107: {
                if (CommonFunction.uiRunning) { // 后台运行时，不再弹出
                    if (MainFragmentActivity.isTopActivity(context)) {
                        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        map.put("error", msg.what);
                        map.put("errordesc", "");
                        ErrorCode.showError(context, JsonUtil.mapToJsonString(map));
                    }
                }
            }
            break;

            // 积分提示
            case MessageID.SESSION_SCORE: {
            }
            break;

            // 群组解散
            case MessageID.GROUP_PUSH_DISSOLVE:
                handleGroupDissolve((TransportMessage) msg.obj);
                break;
            // 推送被群主踢的提示
            case MessageID.GROUP_PUSH_KICK:
                handleGroupKicked((TransportMessage) msg.obj);
                break;

            case MessageID.SESSION_PUSH_MEET:
            case MessageID.PUSH_CIRCLE_MESSAGE_NUMBER: {
                if (MainFragmentActivity.sInstance != null) {
                    MainFragmentActivity.sInstance.freshAllCount();
                }
            }
            break;
            // 推送session或room未知错误提示
            case MessageID.SESSION_OTHER_PROBLEM:
            case MessageID.GROUP_OTHER_PROBLEM: {
                try {
                    TransportMessage message = (TransportMessage) msg.obj;
                    String resultProblem = message.getContentBody();
                    JSONObject json = new JSONObject(resultProblem);
                    String problem = CommonFunction.jsonOptString(json, "message");
                    if (!CommonFunction.isEmptyOrNullStr(problem)) {
                        CommonFunction.showToast(context, problem, 0);
                    } else {
                        CommonFunction.showToast(context, context.getString(R.string.e_1),
                                0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;

            // 在其它地方被登录，强制退出
            case MessageID.SESSION_FORCE_OUT:
                Activity groupChatTopicActivity = CloseAllActivity.getInstance().getTopActivity();
                if (groupChatTopicActivity instanceof GroupChatTopicActivity) {
                    if (((GroupChatTopicActivity) groupChatTopicActivity).instant != null)
                        ((GroupChatTopicActivity) groupChatTopicActivity).instant.errorStop();
                }
                String contentBody = ((TransportMessage) msg.obj).getContentBody();
                ForceQuit(contentBody);
                break;
            // 被管理员强制退出
            case MessageID.ADMIN_FORCE_OUT: {

                ConnectSession.getInstance(context).reset();
                ConnectGroup.getInstance(context).reset();
                Context activity = CloseAllActivity.getInstance().getTopActivity();
                if (activity instanceof AudioChatActivity) { //先退出语音聊天界面
                    ((AudioChatActivity) activity).finish();
                    activity = CloseAllActivity.getInstance().getSecondActivity();
                }
                //关掉WebSocket以及音频推拉流
                WebSocketManager.getsInstance().stopWebSocketService(context);

                if (CloseAllActivity.getInstance().getTopActivity() != null) {
                    DialogUtil.showOKDialog(activity,
                            context.getString(R.string.dialog_title),
                            ErrorCode.getErrorMessageId(ErrorCode.E_4102),
                            new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    ConnectLogin.getInstance(context).logout(context, 4);
                                }

                            });
                }
            }
            break;
            // 登录失败，被强制退出
            case MessageID.LOGINSESSION_N: {
                //关掉WebSocket以及音频推拉流
                WebSocketManager.getsInstance().stopWebSocketService(context);
                TransportMessage message = (TransportMessage) msg.obj;
                try {
                    JSONObject json = new JSONObject(message.getContentBody());
                    int error = json.optInt("error");
                    if (error == -30053001 || error == -30083006) {
                        ConnectLogin.getInstance(context).logout(context, 4);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            break;
            case UPDATE_INSTALL: // 版本更新
                StartModel.getInstance().checkVersion(String.valueOf(msg.obj));
                if (Common.getInstance().versionFlag > 0
                        && CloseAllActivity.getInstance().getTopActivity() != null
                        && !MainFragmentActivity.getIsLogot()) {
                    CommonFunction.showUpdateDialog(CloseAllActivity.getInstance().getTopActivity());
                }
                break;
            case UPDATE_INSTALL_SOCKET:
                VersionDetectBean bean = GsonUtil.getInstance().getServerBean(String.valueOf(msg.obj),
                        VersionDetectBean.class);
                Common.getInstance().currentSoftVersion = bean.newversion;
                Common.getInstance().currentSoftUrl = bean.url;

                if (CommonFunction.isEmptyOrNullStr(Common.getInstance().versionContent)) {
                    Common.getInstance().versionContent = bean.contenturl;
                }
                Common.getInstance().versionFlag = (int) bean.flag;

                if (Common.getInstance().versionFlag > 0
                        && CloseAllActivity.getInstance().getTopActivity() != null
                        && !MainFragmentActivity.getIsLogot()) {
                    CommonFunction.showUpdateDialog(CloseAllActivity.getInstance().getTopActivity());
                }
                break;
            case MessageID.GROUP_PUSH_FORBID_SAY: // 推送禁言时间
                GroupChatListModel.getInstance().updateForbidSay(context, (TransportMessage) msg.obj);
                break;
            case GROUP_CHECK_FORBID: // 检测圈子里的用户是否还被禁言
                GroupChatListModel.getInstance().checkforbidCallBack(context, String.valueOf(msg.obj));
                break;
            case MessageID.SESSION_PUSH_TRANSFER_GROUP:
                // 收到圈子转让消息
                managerGroupTransfer(context, ((TransportMessage) msg.obj).getContentBody());
                break;
            case MessageID.SESSION_GROUP_CALL_MESSAGE:
                try {
                    JSONObject jsonObject = new JSONObject(((TransportMessage) msg.obj).getContentBody());
                    final long groupId = jsonObject.optLong("groupid");
                    String userId = jsonObject.optString("ownnerid");
                    String groupName = jsonObject.optString("groupname");
                    final String content = jsonObject.optString("text");
                    final Activity activity = CloseAllActivity.getInstance().getTopActivity();
                    //gh 过滤自己发的召唤消息
                    if (Common.getInstance().getUid().equals(userId)) return;
                    if (activity instanceof GroupChatTopicActivity) {
                        if (((GroupChatTopicActivity) activity).instant.getCurrentGroupId().equals("" + groupId))
                            return;
                    }
                    DialogUtil.showReceivedCallDialog(activity, groupName, content, new ReceivedCallDialog.SureClickListener() {
                        @Override
                        public void onAgree() {
                            if (activity instanceof GroupChatTopicActivity) {
                                ((GroupChatTopicActivity) activity).isGroupIn = true;
                                activity.finish();
                            }
                            Intent intent = new Intent(context, GroupChatTopicActivity.class);
                            intent.putExtra("id", groupId + "");
                            intent.putExtra("isChat", true);
                            activity.startActivity(intent);
                        }

                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
            case SKILL_ATTACT_SOCKET:
                PrivateChatMessage privateChatMessage = (PrivateChatMessage) msg.obj;

                Activity activity = CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
                if (activity != null) {
                    if (((GroupChatTopicActivity) activity).instant != null) {
                        if (privateChatMessage.type == 15) {
                            // 更新聊吧 - 私聊标志位
                            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(privateChatMessage.content.toString());
                            com.alibaba.fastjson.JSONObject message = JSON.parseObject(jsonObject.getString("content"));
                            String json = JSON.toJSONString(message);
                            SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(json, SkillAttackResult.class);

                            ((GroupChatTopicActivity) activity).instant.handleSkillAttackResult(skillAttackResult);

                        }
                        // 更新聊吧 - 私聊标志位
                        ((GroupChatTopicActivity) activity).instant.refershPrivateData();
                    }
                }
                if (privateChatMessage.type == 15) {
                    com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(privateChatMessage.content.toString());
                    com.alibaba.fastjson.JSONObject message = JSON.parseObject(jsonObject.getString("content"));
                    String json = JSON.toJSONString(message);
                    SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(json, SkillAttackResult.class);
                    if (skillAttackResult.user.UserID != Common.getInstance().loginUser.getUid()) {
                        // 无影脚技能缓存
                        if (skillAttackResult.skillId == 4) {
                            Common.getInstance().setSkillResult(json);
                        } else {
                            Common.getInstance().setSkillResult("");
                        }
                        SkillAttactBannerView skillAttactBannerView = SkillAttactBannerView.getInstance(BaseApplication.appContext);
                        skillAttactBannerView.refershData(skillAttackResult);
                        skillAttactBannerView.createView(3000);
                        if (!(activity instanceof GroupChatTopicActivity) && skillAttackResult.skillId == 4) {
                            if (skillAttackResult.isHit != 0) {
                                ChatBarZoomWindow.getInstance().onKickOffChatbar(String.valueOf(skillAttackResult.groupId));
                            }
                        }
                    }
                }
                break;
            case MAIN_AD_RESULT:
                final Activity activity1 = CloseAllActivity.getInstance().getTopActivity();
                if (activity1 instanceof MainFragmentActivity) {
                    final AdSourceEntity entity = GsonUtil.getInstance().getServerBean(String.valueOf(msg.obj), AdSourceEntity.class);
                    if (null != entity && entity.isSuccess()) {
                        if (entity.indexAd != null) {
                            if (!TextUtils.isEmpty(entity.indexAd.getAdPath())) {
                                DialogUtil.showAdDialogNormal(activity1, entity.indexAd.adScale, entity.indexAd.adPath, new CustomAdDialog.SureClickListener() {
                                    @Override
                                    public void onClick() {
                                        Statistics.onPageClick(Statistics.PAGE_INDEX_AD);
                                        String url = entity.indexAd.adJumpPath;
                                        if (url.startsWith("iaround://")) {
                                            InnerJump.Jump(activity1, url, false);
                                        } else {
                                            WebViewAvtivity.launchVerifyCode(activity1, entity.indexAd.adJumpPath);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }

                break;
            case SPLASH_RESULT:
                InitBean initBean = GsonUtil.getInstance().getServerBean(String.valueOf(msg.obj), InitBean.class);
                if (initBean != null && initBean.isSuccess()) {
                    initBean.setActiveUrl();
                    SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(CloseAllActivity.getInstance().getTopActivity());
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD, String.valueOf(msg.obj));
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_URL, initBean.url);
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_LINK, initBean.link);
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_OPENURL, initBean.openurl);
                    shareDate.putInt(SharedPreferenceUtil.GIFT_DIAMOND_MIN_NUM, initBean.giftDiamondMinNum);

                    Common.getInstance().setLoginFlag(initBean.loginflag);
                    Common.getInstance().setRegisterFlag(initBean.registerflag);
                    if (Config.isShowGoogleApp) {
                        shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL, "");
                    } else {
                        shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL, initBean.advertInfo);
                    }
                    shareDate.putInt(SharedPreferenceUtil.GAMECENTER_AVAILABLE, initBean.gamecenter);
                    shareDate.putInt(SharedPreferenceUtil.GOOGLE_APP, initBean.googleApp);
                    shareDate.putInt(SharedPreferenceUtil.SHOWMIGU, initBean.showMigu);
                    shareDate.putInt(SharedPreferenceUtil.SHOW_MANLIAN, initBean.showManlian);
                    shareDate.putInt(SharedPreferenceUtil.ACCOMPANY_IS_SHOW, initBean.accompanyIsShow);

                    Common.getInstance().setDefaultTab(initBean.defaultTab);
                    Common.getInstance().setBlockStatus(initBean.relationswitch);
                    Common.getInstance().setDefaultTopOption(initBean.defaultTopOption);
//					Common.getInstance().setGeetestSwitch(initBean.geetestSwitch);
                    Common.getInstance().setBindPhoneSwitch(initBean.bindPhoneSwitch);
                    Common.getInstance().setDefaultTopShow(initBean.defaultTopShow);
                    EventBus.getDefault().post("register_login");
                }
                break;
        }
    }

    // VIP会员过期提醒
    private void vipExpiredNotify() {
        Common.getInstance().loginUser.setViplevel(0);
        if (CloseAllActivity.getInstance().getTopActivity() != null) {
            if (MainFragmentActivity.sInstance != null) {
                MainFragmentActivity.sInstance.freshSpaceVipFlag();
            }
            DialogUtil.showTowButtonDialog(CloseAllActivity.getInstance().getTopActivity(),
                    context.getString(R.string.dialog_title),
                    context.getString(R.string.vip_expired_message),
                    context.getString(R.string.i_want_vip),
                    context.getString(R.string.dialog_cancel), new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (MainFragmentActivity.sInstance != null) {
                                MainFragmentActivity.sInstance.startActivity(new Intent(
                                        MainFragmentActivity.sInstance, UserVIPActivity.class));
                            }
                        }
                    }, null);
        }
    }

    // 全球焦点过期提醒
    private void focusExpiredNotify() {
//		if ( CloseAllActivity.getInstance( ).getTopActivity( ) != null )
//		{
//			DialogUtil.showTowButtonDialog( CloseAllActivity.getInstance( ).getTopActivity( ),
//				context.getString( R.string.bid ),
//				Html.fromHtml( context.getString( R.string.bid_expired_message ) ),
//				context.getString( R.string.dialog_cancel ),
//				context.getString( R.string.focus_pay_double ), null, new View.OnClickListener( )
//				{
//
//					@Override
//					public void onClick( View v )
//					{
//						if ( MainActivity.sInstance != null )
//						{
//							MainActivity.sInstance.startActivity(
//								new Intent( MainActivity.sInstance, FocusBid.class ) );
//						}
//					}
//				} );
//		}
    }

    // 全球焦点排名下降提醒
    private void focusGoDownNotify() {
//		if ( CloseAllActivity.getInstance( ).getTopActivity( ) != null )
//		{
//			DialogUtil.showTwoButtonDialog( CloseAllActivity.getInstance( ).getTopActivity( ),
//				context.getString( R.string.dialog_title ),
//				context.getString( R.string.worldfocus_expired_message ),
//				context.getString( R.string.dialog_cancel ),
//				context.getString( R.string.focus_pay_double ), null, new View.OnClickListener( )
//				{
//
//					@Override
//					public void onClick( View v )
//					{
//						if ( MainActivity.sInstance != null )
//						{
//							MainActivity.sInstance.startActivity(
//								new Intent( MainActivity.sInstance, FocusBid.class ) );
//						}
//					}
//				} );
//		}
    }

    // 提示声音与振动
    private void NoticeSoundNVibrate(Message msg) {
        boolean isTop = MainFragmentActivity.isTopActivity(context);
        if (isTop) {
            handlerForwardNotice(msg);
        }
    }

    /**
     * 获取声音和振动的开关，0：声音，1：圈声音，2：振动，3：圈振动
     */
    private boolean getVoiceShakeByType(int type) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        String key = "";
        if (type == 0) {
            key = SharedPreferenceUtil.VOICE + Common.getInstance().loginUser.getUid();
            return sp.getBoolean(key, true);
        }
        if (type == 1) {
            key = SharedPreferenceUtil.GROUP_VOICE + Common.getInstance().loginUser.getUid();
            return sp.getBoolean(key, false);
        }
        if (type == 2) {
            key = SharedPreferenceUtil.SHAKE + Common.getInstance().loginUser.getUid();
            return sp.getBoolean(key, false);
        }
        if (type == 3) {
            key = SharedPreferenceUtil.GROUP_SHAKE + Common.getInstance().loginUser.getUid();
            return sp.getBoolean(key, false);
        }
        return false;
    }

    // 处理前台运行时声音和震动提示情况
    private void handlerForwardNotice(Message msg) {
        TransportMessage message = (TransportMessage) msg.obj;
        switch (msg.what) {
            case MessageID.SESSION_ADD_FANS:// 新增粉丝
            {
                if (getVoiceShakeByType(0))
                    CommonFunction.notifyMsgVoice(context);
                if (getVoiceShakeByType(2))
                    CommonFunction.notifyMsgShake(context);
            }
            break;
            case MessageID.SESSION_PRIVATE_CHAT:// 私聊
            {
                String content = message.getContentBody();
                PrivateChatMessage bean = JSON.parseObject(content, PrivateChatMessage.class);

                if (bean.type == 15) {
                    String result = (String) bean.content;
                    if (result != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String skillresult = jsonObject.getString("content");
                            if (skillresult != null) {
                                SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(skillresult, SkillAttackResult.class);
                                if (skillAttackResult.user.UserID == Common.getInstance().loginUser.getUid()) {
                                    return;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                long fuid = bean.user.userid;
                int mtype = bean.mtype;
                if (ChatPersonalModel.getInstance().isInPersonalChat()) {// 目前在私聊界面
                    long tmp = ChatPersonalModel.getInstance().getChatTargetId();
                    if (ChatPersonalModel.getInstance().getChatTargetId() != fuid) {// 当前用户为该消息的用户就不用处理，否则响铃振动
                        if (!(mtype == 1 && !ChatPersonalModel.getInstance().newManAccost)) {
                            // 当消息为搭讪且不是第一个搭讪消息时不响铃振动，否则响铃振动
                            if (getVoiceShakeByType(0))
                                CommonFunction.notifyMsgVoice(context);
                            if (getVoiceShakeByType(2))
                                CommonFunction.notifyMsgShake(context);
                        }
                    }
                } else {// 已经离开私聊界面
                    if (!(mtype == 1 && !ChatPersonalModel.getInstance().newManAccost)) {
                        // 当消息为搭讪且不是第一个搭讪消息时不响铃振动，否则响铃振动
                        if (getVoiceShakeByType(0))
                            CommonFunction.notifyMsgVoice(context);
                        if (getVoiceShakeByType(2))
                            CommonFunction.notifyMsgShake(context);
                    }
                }

                // try
                // {
                // long targetID = 0;
                // JSONObject obj = new JSONObject(
                // ( ( TransportMessage ) msg.obj ).getContentBody( ) );
                // JSONObject userObj = obj.getJSONObject( "user" );
                // targetID = userObj.getLong( "userid" );
                //
                // if ( CloseAllActivity.getInstance( ).getTopActivity( )
                // instanceof ChatPersonal )
                // {// 当前处于私聊界面
                // // 如果当前聊天的对象与收到消息的对象不相同
                // if ( ChatPersonalModel.getInstance( ).getChatTargetId( ) !=
                // targetID )
                // {
                // if ( obj.getInt( "mtype" ) == 1
                // && !ChatPersonalModel.getInstance( ).newManAccost )
                // {
                // // 如果收到的是搭讪，且不需要响铃或振动，则不响铃不震动
                // }
                // else
                // {
                // ;
                // }
                // }
                // }
                // else
                // {// 当前不是处于私聊界面
                // if ( obj.getInt( "mtype" ) == 1
                // && !ChatPersonalModel.getInstance( ).newManAccost )
                // {
                // // 如果收到的是搭讪，且不需要响铃或振动，则不响铃不震动
                // }
                // else
                // {
                //
                // }
                // }
                // }
                // catch ( JSONException e )
                // {
                // e.printStackTrace( );
                // }
            }
            break;
            case MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC:// 动态新消息
            {
                DynamicNewNumberBean bean = GsonUtil.getInstance().getServerBean(
                        message.getContentBody(), DynamicNewNumberBean.class);

                if (bean != null && bean.getCommentNum() + bean.getLikenum() > 0) {
                    if (getVoiceShakeByType(0))
                        CommonFunction.notifyMsgVoice(context);
                    if (getVoiceShakeByType(2))
                        CommonFunction.notifyMsgShake(context);
                }
            }
            break;
//			case MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES :// 侃啦新消息
//			{
//				PostbarUnreadMessagesBean bean = GsonUtil.getInstance( ).getServerBean(
//						message.getContentBody( ) , PostbarUnreadMessagesBean.class );
//				if ( bean != null && bean.num + bean.likenum > 0 )
//				{
//					if ( getVoiceShakeByType( 0 ) )
//						CommonFunction.notifyMsgVoice( context );
//					if ( getVoiceShakeByType( 2 ) )
//						CommonFunction.notifyMsgShake( context );
//				}
//
//			}
//				break;
            case MessageID.SESSION_PUSH_GROUP_NOTICE_MESSAGE:// 圈通知
            {
                String content = message.getContentBody();
                GroupNoticeListBean bean = GsonUtil.getInstance().getServerBean(content,
                        GroupNoticeListBean.class);
                for (int i = 0; i < bean.messages.size(); i++) {
                    if (bean.messages.get(i).type == GroupNoticeType.APPLY_JOIN_GROUP) {
                        if (getVoiceShakeByType(0))
                            CommonFunction.notifyMsgVoice(context);
                        if (getVoiceShakeByType(2))
                            CommonFunction.notifyMsgShake(context);
                        break;
                    }
                }
            }
            break;
            case MessageID.GROUP_PUSH_MESSAGE:// 圈消息
            {
                GroupChatMessage bean = JSON.parseObject(message.getContentBody(), GroupChatMessage.class);

                long targetid = bean.groupid;
                int receiveeType = GroupAffairModel.getInstance().getGroupMsgStatus(targetid);
                if (GroupModel.getInstance().isInGroupChat()) {// 在圈聊内
                    String gid = GroupModel.getInstance().getGroupId();
                    if (!CommonFunction.isEmptyOrNullStr(gid) && !(Long.parseLong(gid) == targetid) && receiveeType == GroupMsgReceiveType.RECEIVE_AND_NOTICE) {// 如果当前不是该消息的圈子且该圈设置为接收并提醒，就响铃振动
                        if (getVoiceShakeByType(0) && getVoiceShakeByType(1))
                            CommonFunction.notifyMsgVoice(context);
                        if (getVoiceShakeByType(2) && getVoiceShakeByType(3))
                            CommonFunction.notifyMsgShake(context);
                    }
                } else {// 在圈外
                    if (receiveeType == GroupMsgReceiveType.RECEIVE_AND_NOTICE) {
                        if (getVoiceShakeByType(0) && getVoiceShakeByType(1))
                            CommonFunction.notifyMsgVoice(context);
                        if (getVoiceShakeByType(2) && getVoiceShakeByType(3))
                            CommonFunction.notifyMsgShake(context);
                    }
                }
            }
            break;
            case MessageID.CHATBAR_PUSH_NOTICE:
            case MessageID.CHATBAR_PUSH_INVITATION: {
                if (getVoiceShakeByType(0))
                    CommonFunction.notifyMsgVoice(context);
                if (getVoiceShakeByType(2))
                    CommonFunction.notifyMsgShake(context);
            }
            break;
            // case MessageID.PUSH_SESSION_DYNAMIC_MESSAGE :
            // {// 动态当为照片被评论或被赞时响铃或振动
            // try
            // {
            // TransportMessage noReadMessage = ( TransportMessage ) msg.obj;
            // JSONObject json = new JSONObject( noReadMessage.getContentBody( ) );
            // // 类型2
            // int sortType = json.getInt( "sorttype" );
            // if ( sortType == 2 )
            // {
            // soundShaktByType( );
            // }
            // }
            // catch ( Exception e )
            // {
            // e.printStackTrace( );
            // }
            // }
            // break;
            // case MessageID.SESSION_WANT_MEET :
            // {// 邂逅
            // String result = ( ( TransportMessage ) msg.obj ).getContentBody( );
            // try
            // {
            // JSONObject obj = new JSONObject( result );
            // int num = obj.optInt( "num" );
            // if ( num > 0 )
            // {
            // soundShaktByType( );
            // }
            // }
            // catch ( JSONException e )
            // {
            // e.printStackTrace( );
            // }
            // }
            // break;
            // case MessageID.SESSION_PUSH_GROUP_NEW_MEMBER :// 有新圈成员或未审核成员
            // break;
        }
    }

    // 强制退出
    private void ForceQuit(String content) {
        Context activity = CloseAllActivity.getInstance().getTopActivity();
        if (activity instanceof AudioChatActivity) { //先退出语音聊天界面
            ((AudioChatActivity) activity).finish();
            activity = CloseAllActivity.getInstance().getSecondActivity();
        }
        final Context mContext = activity;
        // 断开session
        ConnectorManage.getInstance(mContext).reset();
        MainFragmentActivity.setLogout();

        BusinessHttpProtocol.userCacel(mContext, null);

//		XGPush.UnRegisterXGPush( mContext );

        //关掉WebSocket以及音频推拉流
        WebSocketManager.getsInstance().stopWebSocketService(mContext);

        FocusOffline bean = GsonUtil.getInstance().getServerBean(content, FocusOffline.class);

        String format = " H:mm ";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        String time = sdf.format(bean.time);
        String device = bean.model;
        String formatStr = mContext.getResources().getString(R.string.focus_out_to_login);

        // 展示给用户的信息
        String message = String.format(formatStr, time, device);
        String title = mContext.getResources().getString(R.string.dialog_title);
        String btnStr = mContext.getResources().getString(R.string.ok);
        final Dialog d = DialogUtil.showOneButtonDialog(mContext, title, message, btnStr,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConnectLogin.getInstance(mContext).logout(mContext, 4);

                    }
                });
        d.show();
    }

    // 推送群组解散
    private void handleGroupDissolve(TransportMessage message) {
        try {
            JSONObject json = new JSONObject(message.getContentBody());
            JSONObject userjson = json.optJSONObject("user");
            String nickname = CommonFunction.jsonOptString(userjson, "nickname");
            String chatname = CommonFunction.jsonOptString(json, "groupname");
            String groupid = CommonFunction.jsonOptString(json, "groupid");

            GroupModel.getInstance().dissolveGroup(context, groupid);

            Common.groupKickDisbandedMap.put(groupid, message);
            GroupModel.getInstance().removeGroupFromCache(groupid);

            String toastMsg = String.format(context.getString(R.string.group_dissolve_notice), nickname, chatname);
            CommonFunction.toastMsg(context, toastMsg);

            if (CloseAllActivity.getInstance().getTopActivity() instanceof GroupOut) {
                GroupHandleActivity activity = (GroupHandleActivity) CloseAllActivity.getInstance().getTopActivity();
                activity.HandleOutOfGroupEvent();
            }

            ChatBarZoomWindow.getInstance().onDissolveChatbar(groupid);

            FilterUtil.refreshSlidingMenuAndChildViewBadge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 推送被群组踢出
    private void handleGroupKicked(TransportMessage message) {
        try {
            JSONObject json = new JSONObject(message.getContentBody());
            String chatname = CommonFunction.jsonOptString(json, "groupname");
            String groupid = CommonFunction.jsonOptString(json, "groupid");
            String nickname = CommonFunction.jsonOptString(json, "kickname");
            GroupModel.getInstance().dissolveGroup(context, groupid);

            Common.groupKickDisbandedMap.put(groupid, message);
            GroupModel.getInstance().removeGroupFromCache(groupid);

            String toastMsg = String.format(
                    context.getString(R.string.group_kick_from_group), nickname, chatname);
            CommonFunction.toastMsg(context, toastMsg);

            if (CloseAllActivity.getInstance().getTopActivity() instanceof GroupOut) {
                GroupHandleActivity activity = (GroupHandleActivity) CloseAllActivity.getInstance().getTopActivity();
                activity.HandleOutOfGroupEvent();
            }
            ChatBarZoomWindow.getInstance().onKickOffChatbar(groupid);
            FilterUtil.refreshSlidingMenuAndChildViewBadge();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void managerGroupTransfer(final Context mContext, String result) {

        if (isShowDialogGroupTransfer)
            return;
        try {
            JSONObject jsonObject = new JSONObject(result);

            final long groupId = jsonObject.optLong("groupid");
            String groupName = jsonObject.optString("name");

            String content = String.format(
                    mContext.getString(R.string.group_inf_group_transfer_tips), groupName);
            final Context context = CloseAllActivity.getInstance().getTopActivity();
            Dialog dialog = DialogUtil.showTowButtonDialog(context,
                    context.getString(R.string.prompt), content,
                    mContext.getString(R.string.refuse),
                    mContext.getString(R.string.accept), new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            GroupHttpProtocol.transferManagerGroup(context, groupId + "",
                                    false, null);
                        }
                    }, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            GroupHttpProtocol.transferManagerGroup(context, groupId + "",
                                    true, null);
                        }
                    });
            isShowDialogGroupTransfer = true;
            dialog.setCancelable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
