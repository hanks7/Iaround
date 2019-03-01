
package net.iaround.connector;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.umeng.analytics.MobclickAgent;

import net.iaround.R;
import net.iaround.mic.AudioChatManager;
import net.iaround.conf.Common;
import net.iaround.conf.KeyWord;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.im.WebSocketManager;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.mic.FlyVideoRoom;
import net.iaround.mic.InitZegoLiveRoom;
import net.iaround.model.database.FriendModel;
import net.iaround.model.database.RegisterModel;
import net.iaround.model.database.RegisterModel.RegisterModelReqTypes;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.Me;
import net.iaround.service.APKDownloadService;
import net.iaround.share.facebook.FaceBookUtil;
import net.iaround.share.sina.weibo.SinaWeiboUtil;
import net.iaround.share.tencent.qqzone.QQZoneUtil;
import net.iaround.share.wechat.session.WechatSessionUtil;
import net.iaround.tools.AMapLocationUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GoogleLocationUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.RegisterNewActivity;
import net.iaround.ui.comon.LoginTimer;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.FaceCenterModel;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.TopicModel;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.ui.dynamic.NotificationFunction;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;


//import net.iaround.ui.map.AMapLocationUtil;

/**
 * 登录业务服：登录、重新登录、注销等功能 1、重新登录，服务器下发81018或者无权限访问的指令时执行
 *
 * @author linyg@iaround.net
 * @date 2012-1-5 下午3:36:14
 */
public class ConnectLogin {
    private static final String TAG = "ConnectLogin";
    private static ConnectLogin connect;
    private long connectTime = 0; // 最近一次登录的时间
    public static int counter = 0; // 当连续3次重登录失败时，将不再进行重登录
    public ConnectorManage connectorCore;
    public static int homeKey = 0; // 1为正在运行,2为后台

    public long relogin_flag = 0;

    private Context mContext;

    private ConnectLogin(Context context) {
        this.mContext = context;
        connectorCore = ConnectorManage.getInstance(context);
    }

    public static ConnectLogin getInstance(Context context) {
        if (connect == null) {
            connect = new ConnectLogin(context);
        }
        return connect;
    }

    /**
     * 重新登陆获得key
     *
     * @param isfocuse true 为强制登录，false为自动登录
     * @return void
     * @throws ConnectionException
     */
    public synchronized void reDoLogin(final Context context, boolean isfocuse)
            throws ConnectionException {
        long currentTim = System.currentTimeMillis();
        long time = currentTim - connectTime;
        // 超过30秒钟，则认为是长久没连接上，可以重连
        Log.d("Time Out", "currentTime ===" + currentTim + "connectTime ==" + connectTime + "最终时间========" + time);
        if (!isfocuse && currentTim - connectTime < 180 * 1000) {
            return;
        }

        CommonFunction.log("volley", "断网失效重连 void reDoLogin( Context context , boolean isfocuse )");

        connectorCore.close(); // 将所有的socket干掉

        if (isfocuse || counter <= 2) {
            connectTime = System.currentTimeMillis();
            if (isfocuse) {
                counter = 0;
            } else {
                counter++;
            }
            if (Common.getInstance().loginUser == null) {
                Common.getInstance().loginUser = new Me();
            }

            final int loginType = SharedPreferenceUtil.getInstance(context).getInt(SharedPreferenceUtil.LOGIN_TYPE);

            if (Common.getInstance().getGeetestSwitch() == 1) {
                final GtAppDlgTask gtAppDlgTask = GtAppDlgTask.getInstance();
                gtAppDlgTask.setContext(null);
                gtAppDlgTask.show();
                gtAppDlgTask.setGeetBackListener(new GtAppDlgTask.GeetBackListener() {
                    @Override
                    public void onSuccess(String authKey) {
                        Common.getInstance().setGeetAuthKey(authKey);
                        if (6 == loginType) {
                            // iaround
                            String account = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_ACCOUNT);
                            String password = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_PASSWORD);
                            CommonFunction.log("hanggao", TAG + "222222222222222222222");
                            relogin_flag = LoginHttpProtocol.doIAroundLogin(context, account, password, "", httpCallBack);
                        } else {
                            relogin_flag = thirdTypeLogin(context, loginType, httpCallBack);
                        }

                        gtAppDlgTask.onDestory();
                    }

                    @Override
                    public void onClose() {

                        Activity activity = CloseAllActivity.getInstance().getTopActivity();
                        if (activity == null) return;

                        //关闭最小化悬浮窗
                        ChatBarZoomWindow.getInstance().close();
                        //用户注销逻辑
                        logoutCacel(activity);
                        //停止定位
//						LocationUtil.stop( activity );
                        //跳转去登陆界面
                        activity.startActivity(new Intent(context, RegisterNewActivity.class));
                        activity.finish();
                    }
                });
            } else {
                if (6 == loginType) {
                    // iaround账号
                    String account = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_ACCOUNT);
                    String password = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_PASSWORD);
                    CommonFunction.log("hanggao", TAG + "222222222222222222222");
                    relogin_flag = LoginHttpProtocol.doIAroundLogin(context, account, password, "", httpCallBack);
                } else {
                    //第三方账号
                    relogin_flag = thirdTypeLogin(context, loginType, httpCallBack);
                }
            }
        }

    }

    public synchronized long doLogin(Context context, boolean isfocuse, HttpCallBack callback)
            throws ConnectionException {
        // 超过30秒钟，则认为是长久没连接上，可以重连
        long login_flag = 0;
        if (System.currentTimeMillis() - connectTime < 30 * 1000) {
            counter = 0;
            return 0;
        }
        connectorCore.close(); // 将所有的socket干掉

        if (isfocuse || counter <= 2) {
            connectTime = System.currentTimeMillis();
            if (isfocuse) {
                counter = 0;
            } else {
                counter++;
            }
            if (Common.getInstance().loginUser == null) {
                Common.getInstance().loginUser = new Me();
            }

            int loginType = SharedPreferenceUtil.getInstance(context).getInt(SharedPreferenceUtil.LOGIN_TYPE);
            if (6 == loginType) { // iaround
                String account = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_ACCOUNT);
                if (Common.getInstance().loginUser.getUid() != 0) {
                    account = Common.getInstance().loginUser.getUid() + "";
                }
                String password = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_PASSWORD);
                CommonFunction.log("hanggao", TAG + "11111111111111111111111");
                login_flag = LoginHttpProtocol.doIAroundLogin(context, account, password, "", callback);
            } else {
                thirdTypeLogin(context, loginType, callback);
            }


        }
        return login_flag;
    }

    private long thirdTypeLogin(Context context, int loginType, HttpCallBack callback) {
        long login_flag = 0;
        String openId = "";
        String token = "";
        String unionId = "";
        RegisterModelReqTypes type = RegisterModelReqTypes.QQ_LOGIN;
        if (1 == loginType) {
            openId = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_ID_QQ);
            token = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_TOKEN_QQ);
            type = RegisterModelReqTypes.QQ_LOGIN;
        } else if (2 == loginType) {
            openId = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_ID_SINA);
            token = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_TOKEN_SINA);
            type = RegisterModelReqTypes.WEIBO_LOGIN;
        } else if (4 == loginType) {
            openId = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_ID_FACEBOOK);
            token = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
            type = RegisterModelReqTypes.FACEBOOK_LOGIN;
        } else if (5 == loginType) {
            openId = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_ID_TWITTER);
            token = SharedPreferenceUtil.getInstance(context).getString(
                    SharedPreferenceUtil.LOGIN_TOKEN_TWITTER);

            type = RegisterModelReqTypes.TWITTER_LOGIN;
        } else if (7 == loginType) {
            openId = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_ID_WECHAT);
            token = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
            unionId = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT);
            type = RegisterModelReqTypes.WECHAT_LOGIN;
        }

        try {
            login_flag = RegisterModel.getInstance().LoginReq(context, token, openId, "", callback, type, unionId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return login_flag;
    }


    /**
     * 处理登录响应返回
     *
     * @param jsonString
     * @return void
     * @throws IOException JSONException
     */
    public void doLoginResponse(final Context context, String jsonString, String url,
                                long flag) throws IOException, JSONException {

        JSONObject json;
        json = new JSONObject(jsonString);
        if (json != null) {
            if (json.optInt("status") == 200) {
                SharedPreferenceUtil.getInstance(context).putString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA, jsonString);
                Common.getInstance().isUserLogin = true;
//				CommonFunction.markOpenCount(context);//gh  遇见评分

                if (relogin_flag > 0 && flag == relogin_flag) {
                    if (connectorCore != null) {
                        connectorCore.close();
                    }
                    ConnectSession.getInstance(context).reset();
                    ConnectGroup.getInstance(context).reset();
                    counter = 0; // 登录成功，将计算器置为0

                    StartModel.getInstance().loginData(context, jsonString);


                    ConnectSession.getInstance(context).loginSession(context, true);
                    ConnectGroup.getInstance(context).loginGroup(context, true);
                    SpaceModel.getInstance(context).setAutoLogin(true);


                    // 发送经纬度
                    UIMainHandler handler = new UIMainHandler(context);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//							CommonFunction.log( TAG , "doLoginResponse() " );
                            GeoData geo = LocationUtil.getCurrentGeo(context);
                            if (geo != null) {
                                if (geo.getLat() == 0 || geo.getLng() == 0) {
                                    new LocationUtil(context).startListener(null, 1);
                                } else {
                                    new LocationUtil(context).sendAddressByHttp(geo.getLat(), geo.getLng(), geo.getAddress(), geo.getCity());
                                }
                            } else {
                                new LocationUtil(context).startListener(null, 1);
                            }
                        }
                    });
                    // 获取隐私
//					BusinessHttpProtocol.userPrivacyGet( context , null );
                }
            } else {
                if (Common.getInstance().loginUser.getUid() == 0) {
                    if (MainFragmentActivity.sInstance != null) {
                        MainFragmentActivity.sInstance.loginFail();
                    }
                }
                CommonFunction.log("ConnectLogin", "登录失败====" + json.optInt("status"));
            }
        }
    }

    /**
     * 退出请求
     *
     * @param type 退出类型 1正常退出 ， 2强制退出（无权限 访问） 3 后台运行 , 4注销
     * @time 2011-7-25 下午03:09:36
     * @author:linyg
     */
    public void logout(Context context, int type) {
        //登出
        MobclickAgent.onProfileSignOff();
//		AdModel.getInstance( context ).clearAd();
        APKDownloadService.stopAllTask(context);
//		FaceDownloadService.stopAllTask( context );
        AudioPlayUtil.getInstance().stopAudio();
        ChatBarZoomWindow.getInstance().close();

        LoginTimer.stopLoginTimer();
        FaceManager.getInstance(context.getApplicationContext()).clearFace();

        SocketGroupProtocol.groupLogout(context);
        SocketSessionProtocol.sessionLogout(context);

        long uid = Common.getInstance().loginUser.getUid();

        if (type == 2) {
            CommonFunction.showToast(context, context.getString(R.string.server_exception), 0);
        }

        reset(context, type == 3);
        LocationUtil.stop(context);
        reset();

        // 注销则重新打开
        if (type == 4) {
            SpaceModel.getInstance(context).clearUserState(uid);
            clearLogonData(context);
            MainFragmentActivity.setLogout();
            CloseAllActivity.getInstance().close();
            //YC 取消LoginMainActivity的使用
//			Intent loginReg = new Intent( context , LoginMainActivity.class );
            Intent loginReg = new Intent(context, RegisterNewActivity.class);
            loginReg.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Activity top = CloseAllActivity.getInstance().getTopActivity();
            CommonFunction.log(TAG, "注销重新打开登陆页面");
            if (null != top) {
                top.startActivity(loginReg);
            } else {
                if (context instanceof Activity) {
                    context.startActivity(loginReg);
                } else {
                    //解决 crash 崩溃 // Caused by: android.util.AndroidRuntimeException: Calling startActivity() from outside of an Activity  context requires the FLAG_ACTIVITY_NEW_TASK flag. Is this really what you want?
                    loginReg.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(loginReg);
                }
            }
        } else {
            CloseAllActivity.getInstance().close();
        }
    }

    // 清除社交网络方式登录的id
    private void clearLogonData(Context context) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);

        // 清除第三方登录帐号
        String uid = String.valueOf(Common.getInstance().loginUser.getUid());

        int loginType = sp.getInt(SharedPreferenceUtil.LOGIN_TYPE);
        CommonFunction.log("share", "loginType---" + loginType);
        if (loginType == 3) {
            CommonFunction.log("share", "loginType---QQZoneUtil");
            new QQZoneUtil(context, uid).removeAccount();
            sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
        } else if (loginType == 2) {
            CommonFunction.log("share", "loginType---SinaWeiboUtil");
            new SinaWeiboUtil(context, uid).removeAccount();
            sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
        } else if (loginType == 4) {
            CommonFunction.log("share", "loginType---FaceBookUtil");
            FaceBookUtil faceBookUtil = new FaceBookUtil(context, uid);
            faceBookUtil.removeAccount();
            sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
            faceBookUtil.clearSessionInfo();
        } else if (loginType == 5) {
            CommonFunction.log("share", "loginType---TwitterUtil");
//			new TwitterUtil(context, uid).removeAccount();//gh twiter取消掉
            sp.putString(SharedPreferenceUtil.LOGIN_ID_TWITTER, "");
        } else if (loginType == 7) {
            new WechatSessionUtil(context, uid).removeAccount();
            sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
        }

        sp.putInt(SharedPreferenceUtil.LOGIN_TYPE, 0);

        // 清缓存
        try {
            CookieSyncManager.createInstance(context);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 用户注销
     *
     * @return void
     */
    public void logoutCacel(Context context) {
        try {
            //HTTP接口注销用户
            BusinessHttpProtocol.userCacel(context, null);
//			XGPush.UnRegisterXGPush( context );//gh 没有用到
        } catch (Exception e) {
            CommonFunction.log(e);
        }

//		//释放通讯服务(主播)
//		if(VideoChatManager.getInstance().loginUserIsAnchor()){ //如果是主播
//			//注销消息监听
//			STNManager.removeAllPushMessageHandler();
//
//			//登出视频服务
//			CommonFunction.log("VideoChat", "发送登出任务");
//			LogoutTaskWrapper task = new LogoutTaskWrapper(null);
//			task.getProperties().putLong("uid", Common.getInstance().loginUser.getUid());
//			STNManager.startTask(task);
//
//			//停止通讯服务
//			//STNManager.stop(context);
//		}

        logout(context, 4);

        Common.getInstance().isUserLogin = false;
        Common.getInstance().loginUser.setUid(0);

        //释放推流资源
        InitZegoLiveRoom.unInitZego();
        FlyVideoRoom.unInitZego();

        //关掉WebSocket以及音频推拉流
        WebSocketManager.getsInstance().stopWebSocketService(context);

//		ChatBarAffairModel.getInstant().clearAllBuffer();
    }

    /**
     * 释放资源
     *
     * @param launchBackService
     * @return void
     */
    public void reset(Context context, boolean launchBackService) {
        NotificationFunction.getInstatant(context).cancelNotification();
        CommonFunction.release();
        connectorCore.reset();
        clearData(context);

        // 释放掉资源
        File picFolder = new File(CommonFunction.getSDPath() + "/proPics/");
        if (picFolder.exists()) {
            File[] pics = picFolder.listFiles();
            for (File pic : pics) {
                pic.delete();
            }
        }

        // 是否后台运行
        if (!launchBackService) {
            NotificationFunction.getInstatant(context).cancelNotification();
            CommonFunction.stopLogcatRedirect();
        }
    }

    /**
     * 清楚数据
     *
     * @param context
     * @return void
     */
    private void clearData(Context context) {
        ChatPersonalModel.getInstance().reset();
//		FocusModel.getInstance( ).reset( );//gh 废弃掉
        FriendModel.getInstance(context).reset();
        MessageModel.getInstance().reset();
        SpaceModel.getInstance(context).reset();
        SpaceModelNew.getInstance(context).reset();
        UserBufferHelper.reset();
        StartModel.getInstance().reset();
        PayModel.getInstance().reset();
        KeyWord.getInstance(context).reset();
        GroupChatListModel.getInstance().reset();
        GroupModel.getInstance().reset();
        DynamicModel.getInstent().reset();
        TopicModel.getInstance().reset();
//		PostbarModel.getInstance( ).reset( );//gh 废弃掉
        AMapLocationUtil.getInstance(context).removeListener();
        GoogleLocationUtil.getInstance(context).removeLister();

        ConnectorManage.getInstance(context).reset();
        Common.getInstance().reset();
        FaceCenterModel.getInstance(context).reset();
    }

    /**
     * 清空计算器
     */
    public static void fromHomeUp(Context context) {
        ConnectSession.getInstance(context).reconnectSeesionCount = 0;
        ConnectGroup.getInstance(context).connectGroupCount = 0;
        counter = 0;

        // 心跳停
        if (!ConnectSession.getInstance(context).checkSessionHeart()) {
            ConnectSession.getInstance(context).loginSession(context, true);
        }

        // 心跳停
        if (!ConnectGroup.getInstance(context).checkGroupHeart()) {
            ConnectGroup.getInstance(context).loginGroup(context, true);
        }


    }

    /**
     * 释放
     */
    public static void reset() {
        connect = null;
    }

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            try {
                JSONObject json = new JSONObject(result);
                if (json != null) {
                    if (json.optInt("status") == 200) {
                        SharedPreferenceUtil.getInstance(mContext).putString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA, result);
                        Common.getInstance().isUserLogin = true;
//				CommonFunction.markOpenCount(context);//gh  遇见评分

//				if ( relogin_flag > 0 && flag == relogin_flag )
//				{
                        if (connectorCore != null) {
                            connectorCore.close();
                        }
                        ConnectSession.getInstance(mContext).reset();
                        ConnectGroup.getInstance(mContext).reset();
                        counter = 0; // 登录成功，将计算器置为0

                        StartModel.getInstance().loginData(mContext, result);


                        ConnectSession.getInstance(mContext).loginSession(mContext, true);
                        ConnectGroup.getInstance(mContext).loginGroup(mContext, true);
                        SpaceModel.getInstance(mContext).setAutoLogin(true);


                        // 发送经纬度
                        UIMainHandler handler = new UIMainHandler(mContext);
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                CommonFunction.log("************", "doLoginResponse");
                                GeoData geo = LocationUtil.getCurrentGeo(mContext);
                                if (geo != null) {
                                    if (geo.getLat() == 0 || geo.getLng() == 0) {
                                        new LocationUtil(mContext).startListener(null, 1);
                                    } else {
                                        new LocationUtil(mContext).sendAddressByHttp(geo.getLat(),
                                                geo.getLng(), geo.getAddress(), geo.getCity());
                                    }
                                } else {
                                    new LocationUtil(mContext).startListener(null, 1);
                                }
                            }
                        });
                        // 获取隐私
//						BusinessHttpProtocol.userPrivacyGet( mContext , null );
//				}
                    } else {
                        if (Common.getInstance().loginUser.getUid() == 0) {
                            if (MainFragmentActivity.sInstance != null) {
                                MainFragmentActivity.sInstance.loginFail();
                            }
                        }
                        CommonFunction.log("shifengxiong", "登录失败====" + json.optInt("status"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };
}
