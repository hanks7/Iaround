package net.iaround.connector.protocol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

import com.ishumei.smantifraud.SmAntiFraud;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.entity.GeoData;
import net.iaround.model.login.bean.HadRegister;
import net.iaround.share.facebook.FaceBookUtil;
import net.iaround.share.sina.weibo.SinaWeiboUtil;
import net.iaround.share.tencent.qqzone.QQZoneUtil;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.share.utils.ShareActionListener;
import net.iaround.share.utils.ShareDb;
import net.iaround.share.wechat.group.IARWeixin;
import net.iaround.share.wechat.session.WechatSessionUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.RomUtils;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.StringUtil;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.utils.NativeLibUtil;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class: 登录模块协议接口
 * Author：gh
 * Date: 2016/12/2 16:09
 * Emial：jt_gaohang@163.com
 */
public class LoginHttpProtocol {
    //帐户类型
    public static final int TYPE_IAROUND = 6; //遇见ID|手机号|邮箱
    public static final int TYPE_WEIBO = 2;
    public static final int TYPE_QQ = 1;
    public static final int TYPE_FACEBOOK = 4;
    public static final int TYPE_TWITTER = 5;
    public static final int TYPE_WECHAT = 7;

    public static AbstractShareUtils shareUtils;

    private static String REGISTER = "/v1/user/register"; //注册接口
    public static String LOGIN = "/v1/login/login";     //登陆接口
    private static String HAD_REGISTER = "/v1/user/hadregister"; //是否注册过

    public static int qqLoginCount;
    public static int weChatLoginCount;
    public static int faceBookLoginCount;
    public static int weiboLoginCount;
    public static int qqRegisterCount;
    public static int weChatRegisterCount;
    public static int faceBookRegisterCount;
    public static int weiboRegisterCount;
    public static int qqAuthSuccessCount;
    public static int weChatAuthSuccessCount;
    public static int faceBookAuthSucessCount;
    public static int weiboAuthSucessCount;
    public static int qqAuthfailCount;
    public static int weChatAuthfailCount;
    public static int faceBookAuthfailCount;
    public static int weiboAuthfailCount;


    public static long loginPost(Context context, String url, LinkedHashMap<String, Object> map, HttpCallBack callback) {
        return ConnectorManage.getInstance(context).asynPost(url, map, ConnectorManage.HTTP_LOGIN, callback);
    }


    /**
     * @param context
     * @param account     遇见号或邮件 | 第三方用户ID
     * @param password    密码 | 第三方token
     * @param verifyCode
     * @param accountType 帐号类型(1、腾讯; 2、新浪微博; 3、人人网; 4、Facebook; 5、Twitter; 7、微信; 6、遇见ID或手机号或邮箱)
     * @param unionid       第三方统一用户ID（微信下面同一个公司的各个APP可以有一个唯一的用户ID）
     * @return
     * @Title: doLogin
     * @Description: 执行登录
     */
    public static long doLogin(Context context, String account, String password, String verifyCode, int accountType, HttpCallBack callback, String unionid) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        CommonFunction.log("LoginHttpProtocol", "doLogin() reset connection");
        ConnectorManage.getInstance(context).reset();// 重置Socket
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        PhoneInfoUtil phone = PhoneInfoUtil.getInstance(context);
        String strIMEI = phone.getIMEI();
        String macaddress = phone.macAddress();
        GeoData geo = LocationUtil.getCurrentGeo(context);
        String deviceID = phone.getDeviceId();
        entity.put("accounttype", accountType);
        entity.put("account", account);
        if (accountType == 6) {
            entity.put("password", password);
        } else {
            entity.put("password", password);
        }
        entity.put("logincode", CryptoUtil.SHA1(deviceID));
        entity.put("packageid", CommonFunction.getPackageMetaData(context));
        entity.put("plat", Config.PLAT);
        entity.put("devicecode", deviceID);
        entity.put("language", CommonFunction.getLang(context));
        entity.put("appversion", Config.APP_VERSION);
        entity.put("model", phone.getModel());
        entity.put("sysversion", phone.getFramework());
        if (accountType == 6) {
            addCheck(context, entity, account, password);
        } else {
            addCheck(context, entity, account, password);
        }
        if( null== unionid){
            entity.put("unionid", "");
        }else{
            entity.put("unionid", unionid);
        }

        // 4.2增加验证码机制
        entity.put("authcodeinfo", verifyCode);
        //6.2新增
        entity.put("mac", macaddress);
        entity.put("imei", strIMEI);
        entity.put("lat", geo.getLat());
        entity.put("lng", geo.getLng());
        entity.put("address", geo.getAddress());
        long expires = 0;
        if (accountType == 1) {//QQ
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0L);
        } else if (accountType == 7) {//微信
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0L) * 1000 + System.currentTimeMillis();
        } else if (accountType == 4) {//facebook
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0L);
        } else if (accountType == 2) {//微博
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0L) * 1000 + System.currentTimeMillis();
        }
        entity.put("expriers", expires);

        antiFraud(context,entity);

        //极验
        entity.put("authKey",Common.getInstance().getGeetAuthKey());

        Common.getInstance().setGeetAuthKey("");
        return loginPost(context, LOGIN, entity, callback);
    }


    /**
     * QQ登录，主线程调用
     */
    public static void qqLogin(final Activity activity, final Handler handler, final int msgWhat, final String enterType, final HttpCallBack callback, final int msgDialog) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String qqToken = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ);
        String openId = sp.getString(SharedPreferenceUtil.LOGIN_ID_QQ);

        CommonFunction.log("login", "qqLogin() qq openid>>>" + openId);
        CommonFunction.log("login", "qqLogin() qq token>>>" + qqToken);

        // 本地不存在帐号
        if (CommonFunction.isEmptyOrNullStr(openId) || CommonFunction.isEmptyOrNullStr(qqToken)) {
            QQZoneUtil qq = new QQZoneUtil(activity, "0");
            shareUtils = qq;
            qq.setShareActionListener(new ShareActionListener() {
                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    CommonFunction.log("share", "onError() into, action=" + action);
                    statisticsqqAuthfailCount(activity, enterType);
                    if (action == AbstractShareUtils.ACTION_CANCEL && t != null) {
                        if ("no QQ client".equals(t.getMessage())) {
                            CommonFunction.toastMsg(activity, activity.getResources().getString(R.string.qq_client_unavailable));
                        }
                        if ("QQ client not match".equals(t.getMessage())) {
                            CommonFunction.toastMsg(activity, activity.getResources().getString(R.string.qq_client_not_match));
                        }
                    } else {
                        handler.sendEmptyMessage(msgWhat);
                    }
                }

                public void onComplete(AbstractShareUtils qq, int action, Map<String, Object> res) {
                    CommonFunction.log("share", "onComplete() into, action=" + action);
                    statisticsqqAuthSuccessCount(activity, enterType);
                    if (action == AbstractShareUtils.ACTION_REGISTERING) {
                        ShareDb db = new ShareDb(activity);
                        db.setUid("0");
                        db.setWeiboName(qq.getName());
                        long qqexpires = (long) res.get("expiresin");
                        String qqToken = (String) res.get("accesstoken");
                        String openId = (String) res.get("openid");
                        if (CommonFunction.isEmptyOrNullStr(openId) || CommonFunction.isEmptyOrNullStr(qqToken)) {
                            shareUtils = null;
                            qq.removeAccount();
                            handler.sendEmptyMessage(msgWhat);
                            return;
                        }

//                        if(Common.getInstance().getBindPhoneSwitch() == 0) { //不需要绑定手机号
//                            // 尝试登录，如果登录成功则是之注册过的，否则要去注册
//                            CommonFunction.log("share","qqLogin() no need bind phone");
//                            try {
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, qqToken);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, openId);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, qqexpires);
//
//                                handler.sendEmptyMessage(msgDialog);
//                                if (Common.getInstance().getGeetestSwitch() == 1) {
//                                    handler.sendEmptyMessage(1000);
//                                } else {
//                                    doLogin(activity, openId, qqToken, "", 1, callback, null);
//                                }
//                            } catch (Throwable t) {
//                                CommonFunction.log("share", "qqLogin() exception happen:"+t.toString());
//                                qq.removeAccount();
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
//                                handler.sendEmptyMessage(msgWhat);
//                            }
//                        }else
                        { //需要绑定手机号
                            CommonFunction.log("share","onComplete() save user info, check user is register");

                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, qqToken);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, openId);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, qqexpires);

                            Message message = handler.obtainMessage();
                            message.what = 1001;
                            message.obj = openId;
                            handler.sendMessage(message);
                        }
                    }
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    CommonFunction.log("share", "onCancel() into, action=" + action);

                    Message msg = new Message();
                    msg.what = msgWhat;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);
                }
            });
            //登录
            qq.register(activity);
            return;
        }else {
            // 本地存在
//            if (Common.getInstance().getGeetestSwitch() == 1) {
//                handler.sendEmptyMessage(1000);
//            } else {
//                try {
//                    doLogin(activity, openId, qqToken, "", 1, callback, null);
//                } catch (Throwable t) {
//                    CommonFunction.log(t);
//                    handler.sendEmptyMessage(msgWhat);
//                }
//            }
            CommonFunction.log("login","qqLogin() local have user info, check user is register" );
            Message message = handler.obtainMessage();
            message.what = 1001;
            message.obj = openId;
            handler.sendMessage(message);
        }

    }

    /**
     * weibo登录，主线程调用
     */
    public static void weiboLogin(final Activity activity, final Handler handler,
                                  final int msgWhat, final String enterType, final HttpCallBack callback, final int msgDialog) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String sinaToken = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA);
        String sinaId = sp.getString(SharedPreferenceUtil.LOGIN_ID_SINA);

        CommonFunction.log("login","weiboLogin() sina openid>>>" + sinaId);
        CommonFunction.log("login","weiboLogin() sina token>>>" + sinaToken);

        // 本地不存在帐号
        if (CommonFunction.isEmptyOrNullStr(sinaId) || CommonFunction.isEmptyOrNullStr(sinaToken)) {
            SinaWeiboUtil weibo = new SinaWeiboUtil(activity, "0");
            shareUtils = weibo;
            weibo.setShareActionListener(new ShareActionListener() {
                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    CommonFunction.log("share","onError() into, action=" + action);
                    statisticsweiboAuthfailCount(activity, enterType);
                    handler.sendEmptyMessage(msgWhat);
                }

                public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                    CommonFunction.log("share","onComplete() into, action=" + action);
                    statisticsweiboAuthSucessCount(activity, enterType);
                    if (action == AbstractShareUtils.ACTION_REGISTERING) {
                        ShareDb db = new ShareDb(activity);
                        db.setUid("0");
                        db.setWeiboName(weibo.getName());
                        String sinaToken = db.getToken();
                        String sinaId = db.getShareId();
                        long sinaexpires = (long) res.get("expires_in");
                        sp.putLong("sina_expires", db.getTime() + (Long) res.get("expires_in"));
                        CommonFunction.log("share","weiboLogin() onComplete() sina sinaId=" + sinaId);
                        CommonFunction.log("share","weiboLogin() onComplete() sina token=" + sinaToken);
                        CommonFunction.log("share","weiboLogin() onComplete() sina sinaexpires=" + sinaexpires);
                        if (CommonFunction.isEmptyOrNullStr(sinaId) || CommonFunction.isEmptyOrNullStr(sinaToken)) {
                            shareUtils = null;
                            weibo.removeAccount();
                            handler.sendEmptyMessage(msgWhat);
                            return;
                        }

//                        if(Common.getInstance().getBindPhoneSwitch() == 0) { //不需要绑定手机号
//                            CommonFunction.log("share","onComplete() no need bind phone");
//                            // 尝试登录
//                            try {
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, sinaToken);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, sinaId);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, sinaexpires);
//
//                                handler.sendEmptyMessage(msgDialog);
//
//                                if (Common.getInstance().getGeetestSwitch() == 1) {
//                                    handler.sendEmptyMessage(1000);
//                                } else {
//                                    doLogin(activity, sinaId, sinaToken, "", 2, callback, null);
//                                }
//                            } catch (Throwable t) {
//                                CommonFunction.log(t);
//                                weibo.removeAccount();
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
//                                handler.sendEmptyMessage(msgWhat);
//                            }
//                        }else
                        { //需要绑定手机号
                            //获取第三方用户是否注册过
                            CommonFunction.log("share","onComplete() save user info, check user is register");

                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, sinaToken);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, sinaId);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, sinaexpires);

                            Message message = handler.obtainMessage();
                            message.what = 1001;
                            message.obj = sinaId;
                            handler.sendMessage(message);
                        }
                    }
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    CommonFunction.log("share","onCancel() into, action=" + action);
                    Message msg = new Message();
                    msg.what = msgWhat;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);
                }
            });
            weibo.register(activity);
            return;
        }else {
            // 本地存在
//            if (Common.getInstance().getGeetestSwitch() == 1) {
//                handler.sendEmptyMessage(1000);
//            } else {
//                try {
//                    doLogin(activity, sinaId, sinaToken, "", 2, callback, null);
//                } catch (Throwable t) {
//                    CommonFunction.log(t);
//                    handler.sendEmptyMessage(msgWhat);
//                }
//            }
            CommonFunction.log("login","weiboLogin() local have user info, check user is register" );
            Message message = handler.obtainMessage();
            message.what = 1001;
            message.obj = sinaId;
            handler.sendMessage(message);
        }
    }

    /**
     * facebook登录，主线程调用
     */
    public static void facebookLogin(final Activity activity, final Handler handler,
                                     final int msgWhat, final String enterType, final HttpCallBack callback, final int msgDialog) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String facebookToken = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
        String appId = sp.getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK);

        CommonFunction.log("login", "facebookLogin() facebook Token>>>" + facebookToken);
        CommonFunction.log("login", "facebookLogin() facebook openID>>>" + appId);

        // 本地不存在帐号
        if (CommonFunction.isEmptyOrNullStr(appId) || CommonFunction.isEmptyOrNullStr(facebookToken)) {
            FaceBookUtil facebook = new FaceBookUtil(activity, "0");
            shareUtils = facebook;
            facebook.clearSessionInfo();
            facebook.setShareActionListener(new ShareActionListener() {
                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    CommonFunction.log("share", "facebookLogin() onError() into, action=" + action);
                    statisticsfaceBookAuthfailCount(activity, enterType);
                    if (t != null && t.getMessage() != null) {
                        CommonFunction.toastMsg(activity, t.getMessage());
                    } else {
                        handler.sendEmptyMessage(msgWhat);
                    }
                }

                public void onComplete(AbstractShareUtils facebook, int action, Map<String, Object> res) {
                    CommonFunction.log("share", "facebookLogin() onComplete() into, action=" + action);
                    statisticsfaceBookAuthSucessCount(activity, enterType);
                    if (action == AbstractShareUtils.ACTION_SHOW) {
                        @SuppressWarnings("unchecked")
                        HashMap<String, Object> hashMap = (HashMap<String, Object>) res.get("hashmap");
                        String facebookToken = (String) hashMap.get("oauth_token");
                        String userId = (String) hashMap.get("id");
                        long facebookexpires = 0;
                        CommonFunction.log("share", "facebookLogin() userId:" + userId + ";facebookToken:" + facebookToken);
                        facebookexpires = (long) res.get("expiresin");
                        if (CommonFunction.isEmptyOrNullStr(userId)) {
                            facebook.removeAccount();
                            shareUtils = null;
                            return;
                        }

//                        if(Common.getInstance().getBindPhoneSwitch() == 0) { //不需要绑定手机号
//                            CommonFunction.log("share","no need bind phone");
//                            // 尝试登录
//                            try {
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, facebookToken);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, userId);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, facebookexpires);
//
//                                handler.sendEmptyMessage(msgDialog);
//                                if (Common.getInstance().getGeetestSwitch() == 1) {
//                                    handler.sendEmptyMessage(1000);
//                                } else {
//                                    doLogin(activity, userId, facebookToken, "", 4, callback, null);
//                                }
//                            } catch (Throwable t) {
//                                CommonFunction.log(t);
//                                facebook.removeAccount();
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
//                                handler.sendEmptyMessage(msgWhat);
//                            }
//                        }else
                        { //需要绑定手机号
                            //获取第三方用户是否注册过
                            CommonFunction.log("share","onComplete() save user info, check user is register");

                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, facebookToken);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, userId);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, facebookexpires);

                            Message message = handler.obtainMessage();
                            message.what = 1001;
                            message.obj = userId;
                            handler.sendMessage(message);
                        }
                    }
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    CommonFunction.log("share", "facebookLogin() onCancel() into, action=" + action);
                    Message msg = new Message();
                    msg.what = msgWhat;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);
                }
            });
            facebook.showUser(activity, null);
            return;
        }else {
            // 本地账号存在
//            if (Common.getInstance().getGeetestSwitch() == 1) {
//                handler.sendEmptyMessage(1000);
//            } else {
//
//                try {
//                    doLogin(activity, appId, facebookToken, "", 4, callback, null);
//                } catch (Throwable t) {
//                    CommonFunction.log(t);
//                    handler.sendEmptyMessage(msgWhat);
//                }
//            }
            CommonFunction.log("login","facebookLogin() local have user info, check user is register" );
            Message message = handler.obtainMessage();
            message.what = 1001;
            message.obj = appId;
            handler.sendMessage(message);
        }
    }

    /**
     * 微信登录，主线程调用
     */
    public static void wechatLogin(final Activity activity, final Handler handler, final int msgWhat, final String enterType, final HttpCallBack callback, final int msgDialog) {
        CommonFunction.log("login", "wechatLogin() into");

        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        String twitterToken = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
        String userId = sp.getString(SharedPreferenceUtil.LOGIN_ID_WECHAT);
        String unionid = sp.getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT);

        CommonFunction.log("login", "wechatLogin() wechat Token>>>" + twitterToken);
        CommonFunction.log("login", "wechatLogin() wechat openID>>>" + userId);
        CommonFunction.log("login", "wechatLogin() wechat unionid>>>" + unionid);
        // 本地不存在帐号
        if (CommonFunction.isEmptyOrNullStr(userId) || CommonFunction.isEmptyOrNullStr(twitterToken)) {
            WechatSessionUtil wechat = new WechatSessionUtil(activity, "0");
            shareUtils = wechat;
            wechat.setShareActionListener(new ShareActionListener() {
                public void onError(AbstractShareUtils weibo, int action, final Throwable t) {
                    CommonFunction.log("share","onError() into, action=" + action);
                    statisticsweChatAuthfailCount(activity, enterType);
                    if (t != null && t.getMessage() != null) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (IARWeixin.ERROR_NO_CLIENT.equals(t.getMessage())) {
                                    CommonFunction.toastMsg(activity, activity.getResources().getString(R.string.weixin_client_no_install_tips));
                                } else {
                                    CommonFunction.toastMsg(activity, t.getMessage());
                                }
                            }
                        });
                    } else {
                        handler.sendEmptyMessage(msgWhat);
                    }
                }

                public void onComplete(AbstractShareUtils wechat, int action, Map<String, Object> res) {
                    CommonFunction.log("share", "onComplete() into, action=" + action);
                    statisticsweChatAuthSuccessCount(activity, enterType);
                    if (action == AbstractShareUtils.ACTION_REGISTERING) {
                        ShareDb db = new ShareDb(activity);
                        db.setUid("0");
                        db.setWeiboName(wechat.getName());
                        String twitterToken = (String) res.get("access_token"); //微信token
                        String userId = (String) res.get("open_id"); //微信 openid
                        String unionid = (String) res.get("unionid"); //微信 unionid
                        long twitterexpires = 0;
                        if (res.get("expires_in") != null) {
                            twitterexpires = (long) res.get("expires_in");
                        }
                        CommonFunction.log("share", "onComplete() wechat access_token>>>" + twitterToken);
                        CommonFunction.log("share", "onComplete() wechat open_id>>>" + userId);
                        CommonFunction.log("share", "onComplete() wechat unionid>>>" + unionid);
                        if (CommonFunction.isEmptyOrNullStr(userId) || CommonFunction.isEmptyOrNullStr(twitterToken)) {
                            CommonFunction.log("share","onComplete() wechat user info null");
                            shareUtils = null;
                            wechat.removeAccount();
                            handler.sendEmptyMessage(msgWhat);
                            return;
                        }

//                        if(Common.getInstance().getBindPhoneSwitch() == 0) { //不需要绑定手机号
//                            CommonFunction.log("share","no need bind phone, login directly");
//                            //如果注册过直接走登陆
//                            try {
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, twitterToken);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, userId);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, unionid);
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, twitterexpires);
//
//                                handler.sendEmptyMessage(msgDialog);
//                                if (Common.getInstance().getGeetestSwitch() == 1) {
//                                    handler.sendEmptyMessage(1000);
//                                } else {
//                                    doLogin(activity, userId, twitterToken, "", 7, callback, unionid);
//                                }
//                            } catch (Throwable t) {
//                                CommonFunction.log(t);
//                                wechat.removeAccount();
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
//                                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
//                                handler.sendEmptyMessage(msgWhat);
//                            }
//                        }else
                        { //需要绑定手机号
                            //获取第三方用户是否注册过
                            CommonFunction.log("share","onComplete() save user info, check user is register");

                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, twitterToken);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, userId);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, unionid);
                            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, twitterexpires);

                            Message message = handler.obtainMessage();
                            message.what = 1001;
                            message.obj = userId;
                            handler.sendMessage(message);
                        }
                    }
                }
                public void onCancel(AbstractShareUtils weibo, int action) {
                    CommonFunction.log("share","onCancel() into, action=" + action);
                    Message msg = new Message();
                    msg.what = msgWhat;
                    msg.arg1 = -1;
                    handler.sendMessage(msg);
                }
            });
            wechat.register(activity);
            return;
        }else {
            // 本地存在账号
//            if (Common.getInstance().getGeetestSwitch() == 1) {
//                handler.sendEmptyMessage(1000);
//            } else {
//                try {
//                    doLogin(activity, userId, twitterToken, "", 7, callback, unionid);
//                } catch (Throwable t) {
//                    CommonFunction.log(t);
//                    handler.sendEmptyMessage(msgWhat);
//                }
//            }
            CommonFunction.log("login","wechatLogin() local have user info, check user is register" );
            Message message = handler.obtainMessage();
            message.what = 1001;
            message.obj = userId;
            handler.sendMessage(message);
        }
    }


    /**
     * 找回密码
     *
     * @param context
     * @param email
     * @return
     */
    public static long passwordReset(Context context, String email, HttpCallBack callback) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("email", email);
        entity.put("language", CommonFunction.getLanguageIndex(context) + 1);
        return loginPost(context, "/password/reset", entity, callback);
    }


    /**
     * 处理微信登录返回结果
     * @param result 登陆接口返回的json结果
     * @param flag  登陆接口返回的网络标记
     * @param res  result转化为hashMap的结果
     * @param handler
     * @param msgWhat
     * @param msgRegister
     * @param enterType
     * @param callback
     */
    public static void processWechatLogin(Activity activity, String result, long flag,
                                          HashMap<String, Object> res, final Handler handler, final int msgWhat,
                                          final int msgRegister, String enterType, HttpCallBack callback) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        int status = -1;
        if(null!=res){
            Object statusStr = res.get("status");
            if(null!=statusStr) {
                status = (Integer) statusStr;
            }
        }
        if (status == 200) { // 已有账号登录成功
            //保存一些用户参数
            String id = sp.getString(SharedPreferenceUtil.LOGIN_ID_WECHAT);
            String unionid = sp.getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT);
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
            long expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT);
            SharedPreferenceUtil.getInstance(activity).putString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA, result);
            afterLogin(activity, res, TYPE_WECHAT, id, token, expires, callback,unionid);

            //Handler处理第三方登陆成功消息
            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = 1;
            handler.sendMessage(msg);

            //统计
            statisticsweChatLoginCount(activity, enterType);
        } else if (result.trim().contains("\"error\":4208")) {   //用户不存在, 注册新帐号
            statisticsweChatRegisterCount(activity, enterType);
            CommonFunction.log("login", "login error 4208:" + result);
            WechatSessionUtil wechat = new WechatSessionUtil(activity, "0");
            wechat.setShareActionListener(new ShareActionListener() {
                public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                    CommonFunction.log("share", "processWechatLogin wechat  onComplete***" + action);
                    if (action == AbstractShareUtils.ACTION_SHOW) {
                        Bundle data = new Bundle();
                        data.putString("type", String.valueOf(TYPE_WECHAT));
                        data.putString("nickname", String.valueOf(res.get("nickname")));
                        if (res.get("sex") != null){
                            int sex = (Integer) res.get("sex");
                            data.putString("gender", sex == 1 ? "m" : "f");
                        }else{
                            data.putString("gender", "m");
                        }
                        //设置app时间和服务器一致？
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                        data.putString("birthday", "");
                        String icon = (String) res.get("headimgurl");

                        data.putString("icon", icon);
                        data.putString("id", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_ID_WECHAT));
                        data.putString("unionid", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT));
                        data.putString("token", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT));
                        data.putLong("expires", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT));

                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                        SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);

                        Message msg = new Message();
                        msg.what = msgRegister;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    }
                }

                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    CommonFunction.log("share", "twitter  onError");
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    CommonFunction.log("share", "twitter  onCancel");
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

            });
            wechat.showUser(activity, null);
        } else { // 其他错误
            WechatSessionUtil twitter = new WechatSessionUtil(activity, "0");
            twitter.removeAccount();
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
            sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
            sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
            sp.putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
            sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
            Message msg = new Message();
            msg.what = msgWhat;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    /**
     * facebook登录返回，网络线程调用
     */
    public static void processFacebookLogin(Activity activity, String result, long flag,
                                            HashMap<String, Object> res, final Handler handler, final int msgWhat,
                                            final int msgRegister, String enterType, HttpCallBack callback) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        int status = -1;
        if(null!=res){
            Object statusStr = res.get("status");
            if(null!=statusStr) {
                status = (Integer) statusStr;
            }
        }
        if (status == 200) { // 登录成功
            String id = sp.getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK);
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
            long expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK);
            SharedPreferenceUtil.getInstance(activity).putString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA, result);
            afterLogin(activity, res, TYPE_FACEBOOK, id, token, expires, callback, null);

            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = 1;
            handler.sendMessage(msg);
            statisticsfaceBookLoginCount(activity, enterType);
        } else if (result.trim().contains("\"error\":4208")) { // 新帐号
            statisticsfaceBookRegisterCount(activity, enterType);
            FaceBookUtil facebook = new FaceBookUtil(activity, "0");
            facebook.setShareActionListener(new ShareActionListener() {
                public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> hashMap = (HashMap<String, Object>) res.get("hashmap");
                    Bundle data = new Bundle();
                    data.putString("type", String.valueOf(TYPE_FACEBOOK));
                    data.putString("nickname", (String) hashMap.get("name"));
                    String gender = (String) hashMap.get("gender");
                    data.putString("gender", gender.substring(0, 1));
                    if (hashMap.containsKey("birthday") && ((String) hashMap.get("birthday")).contains("/")) {
                        String[] birthStrings = ((String) hashMap.get("birthday")).split("/");
                        data.putString("birthday", birthStrings[2] + "-" + birthStrings[0] + "-" + birthStrings[1]);
                    } else {
                        data.putString("birthday", "");
                    }

                    data.putString("icon", (String) hashMap.get("picture"));
                    data.putString("id", sp.getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK));
                    data.putString("token", sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK));
                    data.putLong("expires", sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK));

                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);

                    Message msg = new Message();
                    msg.what = msgRegister;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }

                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
                    handler.sendEmptyMessage(msgWhat);
                }
            });
            facebook.showUser(activity, null);
        } else { // 错误
            FaceBookUtil facebook = new FaceBookUtil(activity, "0");
            facebook.removeAccount();
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
            sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
            sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
            sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
            Message msg = new Message();
            msg.what = msgWhat;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    /**
     * 微博登录返回，网络线程调用
     */
    public static void processWeiboLogin(Activity activity, String result, long flag,
                                         HashMap<String, Object> res, final Handler handler, final int msgWhat,
                                         final int msgRegister, String enterType, HttpCallBack callback) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        int status = -1;
        if(null!=res){
            Object statusStr = res.get("status");
            if(null!=statusStr) {
                status = (Integer) statusStr;
            }
        }
        if (status == 200) { // 登录成功
            String id = sp.getString(SharedPreferenceUtil.LOGIN_ID_SINA);
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA);
            long expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA);

            SharedPreferenceUtil.getInstance(activity).putString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA, result);
            afterLogin(activity, res, TYPE_WEIBO, id, token, expires, callback, null);

            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = 1;
            handler.sendMessage(msg);
            statisticsweiboLoginCount(activity, enterType);
        } else if (result.trim().contains("\"error\":4208")) { // 新帐号
            statisticsweiboRegisterCount(activity, enterType);
            SinaWeiboUtil weibo = new SinaWeiboUtil(activity, "0");
            weibo.setShareActionListener(new ShareActionListener() {

                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

                public void onComplete(AbstractShareUtils weibo, int action,
                                       Map<String, Object> res) {
                    Bundle data = new Bundle();
                    data.putString("type", String.valueOf(TYPE_WEIBO));
                    data.putString("nickname", (String) res.get("screen_name"));
                    data.putString("gender", (String) res.get("gender"));
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                    data.putString("birthday", "");
                    data.putString("icon", (String) res.get("avatar_large"));
                    data.putString("id", sp.getString(SharedPreferenceUtil.LOGIN_ID_SINA));
                    data.putString("token", sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA));
                    data.putLong("expires", sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA));
                    data.putString("sign", (String) res.get("description"));
                    data.putString("verified_reason", (String) res.get("verified_reason"));
                    data.putBoolean("verified", (Boolean) res.get("verified"));
                    data.putInt("verified_type", (Integer) res.get("verified_type"));

                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);

                    Message msg = new Message();
                    msg.what = msgRegister;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
                    handler.sendEmptyMessage(msgWhat);
                }
            });
            weibo.showUser(activity, null);
        } else { // 错误
            SinaWeiboUtil weibo = new SinaWeiboUtil(activity, "0");
            weibo.removeAccount();
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA);
            sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
            sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
            sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
            Message msg = new Message();
            msg.what = msgWhat;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }

    /**
     * qq登录返回，网络线程调用
     */
    public static void processQqLogin(final Activity activity, String result, long flag,
                                      HashMap<String, Object> res, final Handler handler, final int msgWhat,
                                      final int msgRegister, String enterType, HttpCallBack callback) {
        final SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        int status = -1;
        if(null!=res){
            Object statusStr = res.get("status");
            if(null!=statusStr) {
                status = (Integer) statusStr;
            }
        }
        if (status == 200) { // 登录成功
            String id = sp.getString(SharedPreferenceUtil.LOGIN_ID_QQ);
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ);
            long expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ);
            SharedPreferenceUtil.getInstance(activity).putString(
                    SharedPreferenceUtil.LOGIN_SUCCESS_DATA, result);
            afterLogin(activity, res, TYPE_QQ, id, token, expires, callback, null);

            Message msg = new Message();
            msg.what = msgWhat;
            msg.arg1 = 1;
            msg.obj = result;
            handler.sendMessage(msg);
            statisticsqqLoginCount(activity, enterType);
        } else if (result.trim().contains("\"error\":4208")) { // 新帐号
            statisticsqqRegisterCount(activity, enterType);
            QQZoneUtil qq = new QQZoneUtil(activity, "0");
            qq.setShareActionListener(new ShareActionListener() {
                public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                    Bundle data = new Bundle();
                    data.putString("type", String.valueOf(TYPE_QQ));
                    data.putString("nickname", (String) res.get("nick_name"));
                    String gender = (String) res.get("gender");
                    data.putString("gender", gender != null && !CommonFunction.isEmptyOrNullStr(gender) ? gender : BaseApplication.appContext.getString(R.string.man));
                    int[] birth = new int[3];
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis()
                            + Common.getInstance().serverToClientTime);
                    data.putString("birthday", "");
                    String icon = ( String ) res.get( "icon" );
                    data.putString("icon", icon != null ? icon : "");
                    data.putString("id", sp.getString(SharedPreferenceUtil.LOGIN_ID_QQ));
                    data.putString("token", sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ));
                    data.putLong("expires", sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ));

                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);

                    Message msg = new Message();
                    msg.what = msgRegister;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }

                public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

                public void onCancel(AbstractShareUtils weibo, int action) {
                    weibo.removeAccount();
                    sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                    sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                    sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
                    handler.sendEmptyMessage(msgWhat);
                }

            });
            qq.showUser(activity, null);
        } else { // 错误
            QQZoneUtil qq = new QQZoneUtil(activity, "0");
            qq.removeAccount();
            String token = sp.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ);
            sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
            sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
            sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
            Message msg = new Message();
            msg.what = msgWhat;
            msg.obj = result;
            handler.sendMessage(msg);
        }
    }


    /**
     * 获取登录信息
     *
     * @param context
     * @return
     */
    public static long reProfile(Context context, HttpCallBack callback) {
        PhoneInfoUtil phone = PhoneInfoUtil.getInstance(context);
        String devicecode = phone.getDeviceId();
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("packageid", CommonFunction.getPackageMetaData(context));
        map.put("devicecode", devicecode);
        return loginPost(context, "/user/info", map, callback);
    }


    /**
     * 注册或登录接口，都将添加上该参数
     *
     * @param context
     * @param map
     * @param account
     * @param password （md5加密的密码）
     */
    private static void addCheck(Context context, HashMap<String, String> map,
                                 String account, String password) {
        String safecode = new NativeLibUtil().cbb(context, account, password);
        map.put("safecode", safecode);
    }

    public static long getMsgCode_662(Context context, String countryCode, String telphone, int type, HttpCallBack callback, String geetest_challenge, String geetest_validate, String geetest_seccode, String authKey) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("countrycode", countryCode);
        map.put("phone", telphone);
        map.put("type", type);
        map.put("plat", Config.PLAT);
        map.put("packageid", CommonFunction.getPackageMetaData(context));
        map.put("geetest_challenge", geetest_challenge);
        map.put("geetest_validate", geetest_validate);
        map.put("geetest_seccode", geetest_seccode);
        map.put("authKey", authKey);
        return loginPost(context, "/code/getsmscode", map, callback);

    }

    public static long doIAroundLogin(Context context, String account, String password, String verifyCode, HttpCallBack callback) {
        return doLogin(context, account, password, verifyCode, 6, callback, null);
    }

    /**
     * @param context
     * @param countryCode 国家代码
     * @param telphone    手机号
     * @param smsCode     短信验证码
     * @param password    用户md5密码
     * @return
     * @Title: findUserPwd
     * @Description: 找回密码
     */
    public static long findUserPwd(Context context, String countryCode, String telphone,
                                   String smsCode, String password, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("countrycode", countryCode);
        map.put("phone", telphone);
        map.put("smscode", smsCode);
        map.put("password", password);
        map.put("language", CommonFunction.getLang(context));
        return loginPost(context, "/password/resetbyphone", map, callback);
    }

    /**
     * @param context
     * @param countryCode 国家码
     * @param telphone    手机号
     * @param smscode     短信验证码
     * @param callback
     * @return 手机注册短信验证码
     */
    public static long getMsgIdentifyingCode(Context context, String countryCode,
                                             String telphone, String smscode, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("countrycode", countryCode);
        map.put("phone", telphone);
        map.put("smscode", smscode);
        return loginPost(context, "/code/validregsmscode", map, callback);

    }

    /**
     * @param context
     * @param password 用md5加密的密码串
     * @return
     * @Title: checkPassword
     * @Description: (4.2)校验用户密码
     */
    public static long checkPassword(Context context, String password, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("password", password);
        return loginPost(context, "/password/check", map, callback);
    }

    /**
     * 反馈
     *
     * @param context
     * @param content
     * @param type    问题类型 1咨询，2投诉，3建议
     * @return
     * @update v3.1
     */
    public static long systemFeedBack(Context context, String content, int type,
                                      HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("content", content);
        map.put("plat", Config.PLAT);
        PhoneInfoUtil phone = PhoneInfoUtil.getInstance(context);
        map.put("model", phone.getModel());
        map.put("sysversion", phone.getFramework());
        map.put("appversion", Config.APP_VERSION);
        map.put("type", type);
        return loginPost(context, "/system/feedback_3_1", map, callback);
    }

    /**
     * 注册或登录接口，都将添加上该参数
     *
     * @param context
     * @param map
     * @param account
     * @param password （md5加密的密码）
     */
    private static void addCheck(Context context, LinkedHashMap<String, Object> map,
                                 String account, String password) {
        String safecode = new NativeLibUtil().cbb(context, account, password);
        map.put("safecode", safecode);
    }


    /**
     * 第三方注册
     *
     * @param context
     * @param accounttype       1-QQ  2-微博  4-FaceBook  7-w微信
     * @param accesstoken
     * @param weiboid
     * @param expires
     * @param account
     * @param password
     * @param nickname
     * @param gender
     * @param wannaMeet
     * @param birthday
     * @param icon
     * @param callback
     * @return
     */
    public static long userRegisterOther(Context context, String accounttype,
                                         String accesstoken, String weiboid, long expires, String account, String password,
                                         String nickname, String gender, int wannaMeet, String birthday, String icon, HttpCallBack callback, String unionid,
                                         String BindPhone, String SmsCode, String CountryCode) {

        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        PhoneInfoUtil phone = PhoneInfoUtil.getInstance(context);
        String deviceID = phone.getDeviceId();
        String strIMEI = phone.getIMEI();
        String macaddress = phone.macAddress();

        if (nickname != null && !TextUtils.isEmpty(nickname)){
            try {
                if (nickname.getBytes("GBK").length > 14){
                    try {
                        nickname = StringUtil.subStr(nickname,14);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        GeoData geo = LocationUtil.getCurrentGeo(context);
        map.put("accounttype", accounttype);
        map.put("account", account);
        map.put("unionid", unionid);
        map.put("password", password);
        map.put("logincode", CryptoUtil.SHA1(deviceID));
        map.put("packageid", CommonFunction.getPackageMetaData(context));
        map.put("plat", Config.PLAT);
        map.put("devicecode", deviceID);
        map.put("language", CommonFunction.getLang(context));
        map.put("appversion", Config.APP_VERSION);
        map.put("model", phone.getModel());
        map.put("sysversion", phone.getFramework());
        map.put("nickname", nickname);
        map.put("gender", gender);
        map.put("icon", icon == null ? "" : icon);
        map.put("accesstoken", accesstoken);
        map.put("openid", weiboid);
        map.put("expires", expires);
        addCheck(context, map, weiboid, accesstoken);
        map.put("wantknow", wannaMeet);
        map.put("openuuid", strIMEI + macaddress);
        map.put("authcodeinfo", "");
        map.put("uniquecode", PhoneInfoUtil.getInstance(context).getUniqueCode());
        map.put("mac", PhoneInfoUtil.getInstance(context).macAddress());

        map.put("imei", strIMEI);
        map.put("lat", geo.getLat());
        map.put("lng", geo.getLng());
        map.put("address", geo.getAddress());
        map.put("interface", "signup");
        map.put("birthday", birthday);

        if ("1".equals(accounttype)) {//QQ
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0L);

        } else if ("7".equals(accounttype)) {//微信
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0L) * 1000 + System.currentTimeMillis();
        } else if ("4".equals(accounttype)) {//facebook
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0L);
        } else if ("2".equals(accounttype)) {//微博
            expires = sp.getLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0L);
        }
        map.put("expriers", expires);
        antiFraud(context,map);

        map.put("BindPhone", BindPhone);
        map.put("SmsCode", SmsCode);
        map.put("CountryCode", CountryCode);

        //极验
        map.put("authKey",Common.getInstance().getGeetAuthKey());

        return loginPost(context, REGISTER, map, callback);
    }

    /**
     * 反垃圾需要传入设备Id
     * @param mContext
     * @param map
     */
    public static void antiFraud(Context mContext,LinkedHashMap<String, Object> map){
        String deviceId = SmAntiFraud.getDeviceId();
        map.put("nx_device_id",deviceId);
    }

    /**
     * 第三方平台登录后处理，将token，id，expires存进sharepreferenceutil
     */
    public static void afterLogin(Context activity, HashMap<String, Object> res, int type, String id, String token, long expires, HttpCallBack callback, String unionid) {

        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);
        sp.putInt(SharedPreferenceUtil.LOGIN_TYPE, type);
        sp.putString(SharedPreferenceUtil.LOGIN_ACCOUNT, String.valueOf(Common.getInstance().loginUser.getUid()));
        String jsonResult = sp.getString(SharedPreferenceUtil.LOGIN_SUCCESS_DATA);
        if (TextUtils.isEmpty(jsonResult)) {
            //gh Map集合内的内部对象解析失败
            StartModel.getInstance().loginModel(activity, res);
        } else {
            //gh 换成Json串解析方式
            StartModel.getInstance().loginData(activity, jsonResult);
        }

        BusinessHttpProtocol.userNetwork(activity, null); // 更新手机信息
        getIconBitmap(activity, Common.getInstance().loginUser.getIcon()); // 请求头像

        switch (type) {
            case TYPE_QQ: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, token);
                sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, id);
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, expires);
            }
            break;
            case TYPE_WEIBO: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, token);
                sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, id);
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, expires);
            }
            break;
            case TYPE_FACEBOOK: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, token);
                sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, id);
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, expires);
            }
            break;
            case TYPE_TWITTER: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_TWITTER, token);
                sp.putString(SharedPreferenceUtil.LOGIN_ID_TWITTER, id);
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_TWITTER, expires);
            }
            break;
            case TYPE_WECHAT: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, token);
                sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, id);
                sp.putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, unionid);
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, expires);
            }
            break;
        }
    }

    /**
     * 系统激活
     */
    public static long softwareActivate(Context context, HttpCallBack callback) {
        if (Config.MOBILE_TYPE != 0
                && !SharedPreferenceUtil.getInstance(context).has(
                SharedPreferenceUtil.DEVICE_ACTIVITY)) {
            String imei = "";
            if (Config.MOBILE_TYPE == 3) { // 三码机
                imei = PhoneInfoUtil.getInstance(context).macAddress();
                if (CommonFunction.isEmptyOrNullStr(imei)) {
                    imei = PhoneInfoUtil.getInstance(context).getIMEI();
                }
            } else if (Config.MOBILE_TYPE == 5) {// 五码机
                imei = PhoneInfoUtil.getInstance(context).getIMEI();
                if (CommonFunction.isEmptyOrNullStr(imei)) {
                    imei = PhoneInfoUtil.getInstance(context).macAddress();
                }
            }
            if (!CommonFunction.isEmptyOrNullStr(imei)) {
                LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("logincode", imei);
                map.put("packageid", PhoneInfoUtil.getInstance(context).getDeviceId());
                map.put("appversion", Config.APP_VERSION);
                return loginPost(context, "/software/activate", map, callback);
            }
        }
        return 0;
    }

    public static void claerLogin(Context activity, int type) {
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(activity);

        switch (type) {
            case TYPE_QQ: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                sp.putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
            }
            break;
            case TYPE_WEIBO: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                sp.putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
            }
            break;
            case TYPE_FACEBOOK: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                sp.putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
            }
            break;
            case TYPE_TWITTER: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_TWITTER, "");
                sp.putString(SharedPreferenceUtil.LOGIN_ID_TWITTER, "");
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_TWITTER, 0);
            }
            break;
            case TYPE_WECHAT: {
                sp.putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                sp.putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                sp.putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                sp.putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
            }
            break;
        }
    }

    /**
     * 版本检测
     *
     * @param context
     * @param type    1简版，2详情
     * @return
     */
    public static long checkVersion(Context context, int type, HttpCallBack callback) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("userid", Common.getInstance().loginUser.getUid());
        map.put("plat", Config.PLAT);
        map.put("version", Config.APP_VERSION);
        map.put("type", type); // 简版类型
        map.put("packageid", CommonFunction.getPackageMetaData(context));
        return loginPost(context, "/system/version_5_7_1", map, callback);
    }


    public static long geetestVerification(Context context, HttpCallBack callBack) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        return loginPost(context, "/system/geetestVerification", entity, callBack);

    }

    /**
     *
     * @param context
     * @param callBack
     * @return
     */
    public static long mainAdSource(Context context, HttpCallBack callBack) {
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("plat", Config.PLAT);
        entity.put("appversion", Config.APP_VERSION);
        entity.put("packageid", CommonFunction.getPackageMetaData(context));
        return loginPost(context, "/v1/ad/index", entity, callBack);

    }

    // 获取用户的头像
    private static void getIconBitmap(final Context activity, final String url) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    int dp_6 = CommonFunction.dipToPx(activity, 6);
                    Bitmap bm = ConnectorManage.getInstance(activity).getBitmap(url,
                            CommonFunction.getSDPath() + "/cacheimage_new/", "png", dp_6, true);
                    Common.getInstance().setIconBitmap(bm);
                } catch (Exception e) {
                }
            }
        }).start();
    }

    private static void statisticsqqLoginCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            qqLoginCount = SharedPreferenceCache.getInstance(context).getInt("qqLoginCountLogin", 0);
            qqLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "1", "" + qqLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqLoginCountLogin", qqLoginCount);
        } else if ("6".equals(enterType)) {
            qqLoginCount = SharedPreferenceCache.getInstance(context).getInt("qqLoginCountRegister", 0);
            qqLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "1", "" + qqLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqLoginCountRegister", qqLoginCount);
        } else {
            qqLoginCount = SharedPreferenceCache.getInstance(context).getInt("qqLoginCount", 0);
            qqLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "1", "" + qqLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqLoginCount", qqLoginCount);
        }
    }

    private static void statisticsweChatLoginCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weChatLoginCount = SharedPreferenceCache.getInstance(context).getInt("weChatLoginCountLogin", 0);
            weChatLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "7", "" + weChatLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatLoginCountLogin", weChatLoginCount);
        } else if ("6".equals(enterType)) {
            weChatLoginCount = SharedPreferenceCache.getInstance(context).getInt("weChatLoginCountRegister", 0);
            weChatLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "7", "" + weChatLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatLoginCountRegister", weChatLoginCount);
        } else {
            weChatLoginCount = SharedPreferenceCache.getInstance(context).getInt("weChatLoginCount", 0);
            weChatLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "7", "" + weChatLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatLoginCount", weChatLoginCount);
        }
    }

    private static void statisticsweiboLoginCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weiboLoginCount = SharedPreferenceCache.getInstance(context).getInt("weiboLoginCountLogin", 0);
            weiboLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "2", "" + weiboLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboLoginCountLogin", weiboLoginCount);
        } else if ("6".equals(enterType)) {
            weiboLoginCount = SharedPreferenceCache.getInstance(context).getInt("weiboLoginCountRegister", 0);
            weiboLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "2", "" + weiboLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboLoginCountRegister", weiboLoginCount);
        } else {
            weiboLoginCount = SharedPreferenceCache.getInstance(context).getInt("weiboLoginCount", 0);
            weiboLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "2", "" + weiboLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboLoginCount", weiboLoginCount);
        }
    }

    private static void statisticsfaceBookLoginCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            faceBookLoginCount = SharedPreferenceCache.getInstance(context).getInt("faceBookLoginCountLogin", 0);
            faceBookLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "4", "" + faceBookLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookLoginCountLogin", faceBookLoginCount);
        } else if ("6".equals(enterType)) {
            faceBookLoginCount = SharedPreferenceCache.getInstance(context).getInt("faceBookLoginCountRegister", 0);
            faceBookLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "4", "" + faceBookLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookLoginCountRegister", faceBookLoginCount);
        } else {
            faceBookLoginCount = SharedPreferenceCache.getInstance(context).getInt("faceBookLoginCount", 0);
            faceBookLoginCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "1", "4", "" + faceBookLoginCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookLoginCount", faceBookLoginCount);
        }
    }

    private static void statisticsqqRegisterCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            qqRegisterCount = SharedPreferenceCache.getInstance(context).getInt("qqRegisterCountLogin", 0);
            qqRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "1", "" + qqRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqRegisterCountLogin", qqRegisterCount);
        } else if ("6".equals(enterType)) {
            qqRegisterCount = SharedPreferenceCache.getInstance(context).getInt("qqRegisterCountRegister", 0);
            qqRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "1", "" + qqRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqRegisterCountRegister", qqRegisterCount);
        } else {
            qqRegisterCount = SharedPreferenceCache.getInstance(context).getInt("qqRegisterCount", 0);
            qqRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "1", "" + qqRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqRegisterCount", qqRegisterCount);
        }
    }

    private static void statisticsweChatRegisterCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weChatRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weChatRegisterCountLogin", 0);
            weChatRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "7", "" + weChatRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatRegisterCountLogin", weChatRegisterCount);
        } else if ("6".equals(enterType)) {
            weChatRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weChatRegisterCountRegister", 0);
            weChatRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "7", "" + weChatRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatRegisterCountRegister", weChatRegisterCount);
        } else {
            weChatRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weChatRegisterCount", 0);
            weChatRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "7", "" + weChatRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatRegisterCount", weChatRegisterCount);
        }
    }

    private static void statisticsweiboRegisterCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weiboRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weiboRegisterCountLogin", 0);
            weiboRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "2", "" + weiboRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboRegisterCountLogin", weiboRegisterCount);
        } else if ("6".equals(enterType)) {
            weiboRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weiboRegisterCountRegister", 0);
            weiboRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "2", "" + weiboRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboRegisterCountRegister", weiboRegisterCount);
        } else {
            weiboRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weiboRegisterCount", 0);
            weiboRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "2", "" + weiboRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboRegisterCount", weiboRegisterCount);
        }
    }

    private static void statisticsfaceBookRegisterCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            faceBookRegisterCount = SharedPreferenceCache.getInstance(context).getInt("faceBookRegisterCountLogin", 0);
            faceBookRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "4", "" + faceBookRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookRegisterCountLogin", faceBookRegisterCount);
        } else if ("6".equals(enterType)) {
            faceBookRegisterCount = SharedPreferenceCache.getInstance(context).getInt("faceBookRegisterCountRegister", 0);
            faceBookRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "4", "" + faceBookRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookRegisterCountRegister", faceBookRegisterCount);
        } else {
            faceBookRegisterCount = SharedPreferenceCache.getInstance(context).getInt("faceBookRegisterCount", 0);
            faceBookRegisterCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "2", "4", "" + faceBookRegisterCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookRegisterCount", faceBookRegisterCount);
        }
    }

    private static void statisticsqqAuthSuccessCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            qqAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthSuccessCountLogin", 0);
            qqAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "1", "" + qqAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthSuccessCountLogin", qqAuthSuccessCount);
        } else if ("6".equals(enterType)) {
            qqAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthSuccessCountRegister", 0);
            qqAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "1", "" + qqAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthSuccessCountRegister", qqAuthSuccessCount);
        } else {
            qqAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthSuccessCount", 0);
            qqAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "1", "" + qqAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthSuccessCount", qqAuthSuccessCount);
        }
    }

    private static void statisticsweChatAuthSuccessCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weChatAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthSuccessCountLogin", 0);
            weChatAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "7", "" + weChatAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthSuccessCountLogin", weChatAuthSuccessCount);
        } else if ("6".equals(enterType)) {
            weChatAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthSuccessCountRegister", 0);
            weChatAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "7", "" + weChatAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthSuccessCountRegister", weChatAuthSuccessCount);
        } else {
            weChatAuthSuccessCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthSuccessCount", 0);
            weChatAuthSuccessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "7", "" + weChatAuthSuccessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthSuccessCount", weChatAuthSuccessCount);
        }
    }

    private static void statisticsweiboAuthSucessCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weiboAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthSucessCountLogin", 0);
            weiboAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "2", "" + weiboAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthSucessCountLogin", weiboAuthSucessCount);
        } else if ("6".equals(enterType)) {
            weiboAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthSucessCountRegister", 0);
            weiboAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "2", "" + weiboAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthSucessCountRegister", weiboAuthSucessCount);
        } else {
            weiboAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthSucessCount", 0);
            weiboAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "2", "" + weiboAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthSucessCount", weiboAuthSucessCount);
        }
    }

    private static void statisticsfaceBookAuthSucessCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            faceBookAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthSucessCountLogin", 0);
            faceBookAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "4", "" + faceBookAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthSucessCountLogin", faceBookAuthSucessCount);
        } else if ("6".equals(enterType)) {
            faceBookAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthSucessCountRegister", 0);
            faceBookAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "4", "" + faceBookAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthSucessCountRegister", faceBookAuthSucessCount);
        } else {
            faceBookAuthSucessCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthSucessCount", 0);
            faceBookAuthSucessCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "3", "4", "" + faceBookAuthSucessCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthSucessCount", faceBookAuthSucessCount);
        }
    }

    private static void statisticsqqAuthfailCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            qqAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthfailCountLogin", 0);
            qqAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "1", "" + qqAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthfailCountLogin", qqAuthfailCount);
        } else if ("6".equals(enterType)) {
            qqAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthfailCountRegister", 0);
            qqAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "1", "" + qqAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthfailCountRegister", qqAuthfailCount);
        } else {
            qqAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("qqAuthfailCount", 0);
            qqAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "1", "" + qqAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("qqAuthfailCount", qqAuthfailCount);
        }
    }

    private static void statisticsweChatAuthfailCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weChatAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthfailCountLogin", 0);
            weChatAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "7", "" + weChatAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthfailCountLogin", weChatAuthfailCount);
        } else if ("6".equals(enterType)) {
            weChatAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthfailCountRegister", 0);
            weChatAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "7", "" + weChatAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthfailCountRegister", weChatAuthfailCount);
        } else {
            weChatAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weChatAuthfailCount", 0);
            weChatAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "7", "" + weChatAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weChatAuthfailCount", weChatAuthfailCount);
        }
    }

    private static void statisticsweiboAuthfailCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            weiboAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthfailCountLogin", 0);
            weiboAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "2", "" + weiboAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthfailCountLogin", weiboAuthfailCount);
        } else if ("6".equals(enterType)) {
            weiboAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthfailCountRegister", 0);
            weiboAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "2", "" + weiboAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthfailCountRegister", weiboAuthfailCount);
        } else {
            weiboAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("weiboAuthfailCount", 0);
            weiboAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "2", "" + weiboAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("weiboAuthfailCount", weiboAuthfailCount);
        }
    }

    private static void statisticsfaceBookAuthfailCount(Context context, String enterType) {
        if ("5".equals(enterType)) {
            faceBookAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthfailCountLogin", 0);
            faceBookAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "4", "" + faceBookAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthfailCountLogin", faceBookAuthfailCount);
        } else if ("6".equals(enterType)) {
            faceBookAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthfailCountRegister", 0);
            faceBookAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "4", "" + faceBookAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthfailCountRegister", faceBookAuthfailCount);
        } else {
            faceBookAuthfailCount = SharedPreferenceCache.getInstance(context).getInt("faceBookAuthfailCount", 0);
            faceBookAuthfailCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(context, "4", "4", "" + faceBookAuthfailCount, enterType, null);
            SharedPreferenceCache.getInstance(context).putInt("faceBookAuthfailCount", faceBookAuthfailCount);
        }
    }

    /* 用户是否注册过 HTTP请求
    * @param return -1 网络失败 0 未注册 1 已注册
    * */
    public static long userHadRegister(int accountType,String account,int thirdPartType, String thirdPartOpenID, HttpCallBack callBack){
        LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
        entity.put("accounttype",accountType); //账户类型，6是遇见账户，其他是第三方
        entity.put("account",account);  //遇见账号，当accounttype是6的时候，必填
        entity.put("thirdparttype",thirdPartType);  //第三方的登录方式
        entity.put("thirdpartid",thirdPartOpenID);  //第三方登录的openid
        long flag = ConnectorManage.getInstance(BaseApplication.appContext).asynPost(HAD_REGISTER,entity,ConnectorManage.HTTP_LOGIN, callBack);
        return flag;
    }


    /*获取微信用户头像和昵称等信息
    * */
    public static void getWeChatUserInfo(Activity activity, final Handler handler){
        CommonFunction.log("login", "getWeChatUserInfo() into");
        WechatSessionUtil wechat = new WechatSessionUtil(activity, "0");
        wechat.setShareActionListener(new ShareActionListener() {
            public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                CommonFunction.log("share", "getWeChatUserInfo() wechat onComplete() action=" + action);
                if (action == AbstractShareUtils.ACTION_SHOW) {
                    Bundle data = new Bundle();
                    data.putString("type", String.valueOf(TYPE_WECHAT));
                    data.putString("nickname", String.valueOf(res.get("nickname")));
                    if (res.get("sex") != null){
                        int sex = (Integer) res.get("sex");
                        data.putString("gender", sex == 1 ? "m" : "f");
                    }else{
                        data.putString("gender", "m");
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                    data.putString("birthday", "");
                    String icon = (String) res.get("headimgurl");
                    data.putString("icon", icon);
                    data.putString("id", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_ID_WECHAT));
                    data.putString("unionid", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT));
                    data.putString("token", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT));
                    data.putLong("expires", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT));

                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);

                    Message msg = new Message();
                    msg.what = 1003; //通知获取用户信息完成
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            }

            public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                CommonFunction.log("share", "getWeChatUserInfo()  onError() into");
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
                handler.sendEmptyMessage(0x01ff);
            }

            public void onCancel(AbstractShareUtils weibo, int action) {
                CommonFunction.log("share", "getWeChatUserInfo()  onCancel() into");
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_WECHAT, 0);
                handler.sendEmptyMessage(0x01ff);
            }

        });
        wechat.showUser(activity, null);
    }

    /*获取facebook用户头像和昵称等信息
    * */
    public static void getFacebookUserInfo(Activity activity, final Handler handler){
        FaceBookUtil facebook = new FaceBookUtil(activity, "0");
        facebook.setShareActionListener(new ShareActionListener() {
            public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                @SuppressWarnings("unchecked")
                HashMap<String, Object> hashMap = (HashMap<String, Object>) res.get("hashmap");
                Bundle data = new Bundle();
                data.putString("type", String.valueOf(TYPE_FACEBOOK));
                data.putString("nickname", (String) hashMap.get("name"));
                String gender = (String) hashMap.get("gender");
                data.putString("gender", gender.substring(0, 1));
                if (hashMap.containsKey("birthday") && ((String) hashMap.get("birthday")).contains("/")) {
                    String[] birthStrings = ((String) hashMap.get("birthday")).split("/");
                    data.putString("birthday", birthStrings[2] + "-" + birthStrings[0] + "-" + birthStrings[1]);
                } else {
                    data.putString("birthday", "");
                }

                data.putString("icon", (String) hashMap.get("picture"));
                data.putString("id", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK));
                data.putString("token", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK));
                data.putLong("expires", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK));

                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);

                Message msg = new Message();
                msg.what = 1003;
                msg.obj = data;
                handler.sendMessage(msg);
            }

            public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
                handler.sendEmptyMessage(0x01ff);
            }

            public void onCancel(AbstractShareUtils weibo, int action) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_FACEBOOK, 0);
                handler.sendEmptyMessage(0x01ff);
            }
        });
        facebook.showUser(activity, null);
    }

    /*获取QQ用户头像和昵称等信息
    * */
    public static void getQQUserInfo(Activity activity, final Handler handler){
        QQZoneUtil qq = new QQZoneUtil(activity, "0");
        qq.setShareActionListener(new ShareActionListener() {
            public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                Bundle data = new Bundle();
                data.putString("type", String.valueOf(TYPE_QQ));
                data.putString("nickname", (String) res.get("nick_name"));
                String gender = (String) res.get("gender");
                data.putString("gender", gender != null && !CommonFunction.isEmptyOrNullStr(gender) ? gender : BaseApplication.appContext.getString(R.string.man));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                data.putString("birthday", "");
                String icon = ( String ) res.get( "icon" );
                data.putString("icon", icon != null ? icon : "");
                data.putString("id", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_ID_QQ));
                data.putString("token", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ));
                data.putLong("expires", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ));

                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);

                Message msg = new Message();
                msg.what = 1003; //通知获取用户信息完成
                msg.obj = data;
                handler.sendMessage(msg);
            }

            public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
                handler.sendEmptyMessage(0x01ff);
            }

            public void onCancel(AbstractShareUtils weibo, int action) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_QQ, 0);
                handler.sendEmptyMessage(0x01ff);
            }

        });
        qq.showUser(activity, null);
    }

    /*获取weibo用户头像和昵称等信息
    * */
    public static void getWeiboUserInfo(Activity activity, final Handler handler){
        SinaWeiboUtil weibo = new SinaWeiboUtil(activity, "0");
        weibo.setShareActionListener(new ShareActionListener() {

            public void onError(AbstractShareUtils weibo, int action, Throwable t) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
                handler.sendEmptyMessage(0x01ff);
            }

            public void onComplete(AbstractShareUtils weibo, int action, Map<String, Object> res) {
                Bundle data = new Bundle();
                data.putString("type", String.valueOf(TYPE_WEIBO));
                data.putString("nickname", (String) res.get("screen_name"));
                data.putString("gender", (String) res.get("gender"));
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                data.putString("birthday", "");
                data.putString("icon", (String) res.get("avatar_large"));
                data.putString("id", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_ID_SINA));
                data.putString("token", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA));
                data.putLong("expires", SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA));
                data.putString("sign", (String) res.get("description"));
                data.putString("verified_reason", (String) res.get("verified_reason"));
                data.putBoolean("verified", (Boolean) res.get("verified"));
                data.putInt("verified_type", (Integer) res.get("verified_type"));

                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);

                Message msg = new Message();
                msg.what = 1003; //通知获取用户信息完成
                msg.obj = data;
                handler.sendMessage(msg);
            }

            public void onCancel(AbstractShareUtils weibo, int action) {
                weibo.removeAccount();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_TOKEN_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ID_SINA, "");
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(SharedPreferenceUtil.LOGIN_EXPIRES_SINA, 0);
                handler.sendEmptyMessage(0x01ff);
            }
        });
        weibo.showUser(activity, null);
    }
}
