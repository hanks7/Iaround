
package net.iaround.ui.comon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.BaseHttp;
import net.iaround.connector.ConnectLogin;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.LoginActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.datamodel.StartModel;

import java.util.Timer;
import java.util.TimerTask;


public class LoginTimer extends Timer {
    private static LoginTimer sLoginTimerInstance;

    private Context mContext;
    private int sendloginCount = 0;
    private LoginCallback loginCallback;
    private boolean isNetworkConnected = true;
    private TimerTask task;

    private LoginTimer() {
    }


    public static synchronized LoginTimer getInstance(Context context) {
        if (sLoginTimerInstance == null) {
            sLoginTimerInstance = newInstance(context);
            sLoginTimerInstance.mContext = context;
        }

        return sLoginTimerInstance;
    }

    public static synchronized void stopLoginTimer() {
        if (sLoginTimerInstance != null) {
            sLoginTimerInstance.cancel();
            sLoginTimerInstance = null;
        }
        sLoginTimerInstance = null;
    }


    private static LoginTimer newInstance(Context context) {
        if (sLoginTimerInstance == null) {
            sLoginTimerInstance = new LoginTimer();
            // 每十秒进行一次
            if (sLoginTimerInstance.task == null) {
                sLoginTimerInstance.task = new TimerTask() {
                    public void run() {
                        boolean isAutoLogin = SpaceModel.getInstance(sLoginTimerInstance.mContext).isAutoLogin();

                        int type = BaseHttp.checkNetworkType(sLoginTimerInstance.mContext);
                        // 定时检测网络连接情况
                        if (type == BaseHttp.TYPE_NET_WORK_DISABLED) {
                            // 无法连接网络
                            if (sLoginTimerInstance.isNetworkConnected ^ false) {
                                sLoginTimerInstance.isNetworkConnected = false;

                                StartModel.getInstance().setLoginConnnetStatus(StartModel.TYPE_NET_WORK_DISABLED);

                                LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                                if (callback != null) {
                                    callback.netConnectFail();
                                }
                            }
                        } else {
                            if(sLoginTimerInstance != null){
                                if (sLoginTimerInstance.isNetworkConnected ^ true) {

                                    StartModel.getInstance().setLoginConnnetStatus(StartModel.TYPE_NET_HAVED_LOGIN);
                                    sLoginTimerInstance.isNetworkConnected = true;

                                    LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                                    if (callback != null) {
                                        callback.netConnectSuccess();
                                    }
                                }
                            }
                        }
                        if (isAutoLogin) {
                            if (!Common.getInstance().isUserLogin) {
                                if (sLoginTimerInstance.sendloginCount % 2 == 0) // 间隔20秒发送一次登录请求
                                {
                                    StartModel.getInstance().setLoginConnnetStatus(StartModel.TYPE_NET_WORK_LOGINING);
                                    CommonFunction.log("shifengxiong", "autoLogin ==发送登录请求" + System.currentTimeMillis());
                                    /* 自动登录不发送登录请求 */
                                    LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                                    if (callback != null) {
                                        callback.autoLoginConnecting();
                                    }
                                    Activity topActivity = CloseAllActivity.getInstance().getTopActivity();
                                    if (!(topActivity instanceof WebViewAvtivity)) {
                                        try {
                                            Log.d("Other", "restLogin");
                                            ConnectLogin.getInstance(sLoginTimerInstance.mContext).doLogin(sLoginTimerInstance.mContext, true, sLoginTimerInstance.httpCallback);
                                        } catch (ConnectionException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }


                                    if (sLoginTimerInstance.sendloginCount >= 6) {
                                        // 重新连接已经超过四次
                                        StartModel.getInstance().setLoginConnnetStatus(StartModel.TYPE_NET_LOGIN_TIME_OUT);

                                        LoginCallback callbackTmp = sLoginTimerInstance.getLoginCallback();
                                        if (callbackTmp != null) {
                                            callbackTmp.autoLoginFail(0);
                                        }
                                    }
                                }

                                sLoginTimerInstance.sendloginCount++;
                            }
                        }
                    }
                };
            }

            sLoginTimerInstance.schedule(sLoginTimerInstance.task, 1, 10000);
        }
        return sLoginTimerInstance;
    }

    // 重置
    public static synchronized void reset() {
        if (sLoginTimerInstance != null) {
            sLoginTimerInstance.cancel();
            sLoginTimerInstance.task.cancel();
            sLoginTimerInstance.task = null;
            sLoginTimerInstance.loginCallback = null;
        }

        sLoginTimerInstance = null;
    }

    public static synchronized void setLoginCallback(LoginCallback callback) {
        if (sLoginTimerInstance != null) {
            sLoginTimerInstance.loginCallback = callback;
        }
    }

    public static synchronized LoginCallback getLoginCallback() {
        if (sLoginTimerInstance == null) {
            return null;
        }
        return sLoginTimerInstance.loginCallback;
    }


    public interface LoginCallback {
        int LoginFailNeedVerify = 8100; // 需要验证码登录
        int LoginFailVerifyError = 8101;// 验证码错误
        int LoginFail = 4108; // 非正式包，请下载正式包
        int LoginFailError = 4208; // 用户账号或密码错误

        void autoLoginSuccess();

        void autoLoginFail(int type);

        void autoLoginConnecting();

        void netConnectFail();

        void netConnectSuccess();
    }

    HttpCallBack httpCallback = new HttpCallBack() {

        @Override
        public void onGeneralSuccess(String result, long flag) {
            if (sLoginTimerInstance.mContext == null) {
                sLoginTimerInstance.mContext = BaseApplication.appContext;
            }
            LoginResponseBean bean = StartModel.getInstance().loginData(sLoginTimerInstance.mContext, result);
            if (bean != null) {
                if (bean.isSuccess()) { // 登陆成功
                    bean.setUrl();
                    bean.loginSuccess(mContext);
                    Common.getInstance().isUserLogin = true;
                    LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                    if (callback != null) {
                        callback.autoLoginSuccess();
                        sendloginCount = 0;
                        return;
                    }
                } else {
                    final String fjo = bean.errordesc;
                    final int type = bean.error;
                    if (bean.error == 4108) {
                        DialogUtil.showOneButtonDialog(mContext,
                                mContext.getString(R.string.dialog_title),
                                mContext.getString(R.string.download_offical),
                                mContext.getString(R.string.game_center_task_download),
                                new OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        Uri uri = Uri.parse(fjo);
                                        mContext.startActivity(new Intent(Intent.ACTION_VIEW, uri));
                                        LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                                        if (callback != null) {
                                            callback.autoLoginFail(type);
                                        }
                                    }
                                });
                        if (sLoginTimerInstance != null) {
                            sLoginTimerInstance.cancel();
                        }
                    } else if (type == 4208 || type == 8101 || type == 4207) {
                        LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                        if (callback != null) {
                            callback.autoLoginFail(type);
                            sLoginTimerInstance.cancel();
                            return;
                        }
                        if (sLoginTimerInstance != null) {
                            sLoginTimerInstance.cancel();
                        }
                    } else {
                        if (Common.getInstance().loginUser.getUid() == 0) {
                            LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                            if (callback != null) {
                                callback.autoLoginFail(4208);
                                sLoginTimerInstance.cancel();
                                return;
                            }
                        }
                        LoginCallback callback = sLoginTimerInstance.getLoginCallback();
                        if (callback != null) {
                            callback.autoLoginFail(0);
                            return;
                        }
                        if (sLoginTimerInstance != null) {
                            sLoginTimerInstance.cancel();
                        }
                    }
                }
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };

}
