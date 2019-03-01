package net.iaround.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.GtAppDlgTask;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.SignClickHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.Item;
import net.iaround.model.im.Me;
import net.iaround.model.login.bean.HadRegister;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.Hashon;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.SharedPreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 注册
 * Created by gaohang on 2017/5/10.
 */

public class RegisterNewActivity extends ActionBarActivity implements View.OnClickListener, HttpCallBack {

    protected static final int MSG_GO_REGISTER = 0x02ff;
    protected static final int MSG_WHAT_LOGIN = 1;
    protected static final int MSG_ARG1_SUCCESS = 1;
    protected static final int MSG_ARG1_FAIL = 0;
    protected static final int MSG_LOGIN_RES = 0x01ff;
    protected static final int MSG_SHOW_GEET = 1000;  //极验弹窗请求
    protected static final int MSG_HAD_REGISTER = 1001;  //用户是否注册过
    protected static final int MSG_BIND_TELPHONE = 1002;  //绑定手机号
    protected static final int MSG_THIRD_PART_USER_INFO = 1003;  //获得第三方用户信息
    protected static final int SHOW_DIALOG = 10;

    protected static final int BIND_PHONE_REQUEST_CODE = 99;  //activity 跳转请求码

    public static final int MIN_CLICK_DELAY_TIME = 3000;

    public static int qqRegisterCount;
    public static int weChatRegisterCount;
    public static int faceBookRegisterCount;

    private int accounttype;
    private Dialog pd;

    private long lastClickTime = 0;

    private GtAppDlgTask gtAppDlgTask;//极验弹框

    private long DO_LOGIN_FLAG = -1; //(遇见本地用户手机号邮箱)用户登陆http请求标记,不需要处理登陆用户没注册情况
    private long DO_USER_REGISTER = -1; //注册用户http请求标记
    private long HAD_USER_REGISTER = -1; //用户是否注册过http请求标记

    private TheHandler mTheHandler = new TheHandler(this);

    private HttpCallBack httpCallBack = new HttpCallBackImpl(this); //登陆接口HTTP回调

    private String mCountryCode; //国家区号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunction.log("RegisterNewActivity","onCreate() into");

        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_register_new);

        initView();

        CommonFunction.log("RegisterNewActivity","onCreate() out");
    }

    private void initView() {
        LinearLayout faceBookLy = (LinearLayout) findViewById(R.id.btn_register_new_abroad_facebook);
        LinearLayout qqLy = (LinearLayout) findViewById(R.id.btn_register_new_abroad_qq);
        LinearLayout weCahtAbroadLy = (LinearLayout) findViewById(R.id.btn_register_new_abroad_wechat);

        findViewById(R.id.ll_old_user_login1).setOnClickListener(this);
        findViewById(R.id.tv_login_old_user).setOnClickListener(this);
        weCahtAbroadLy.setOnClickListener(this);
        faceBookLy.setOnClickListener(this);
        qqLy.setOnClickListener(this);

        //根据语言显示登录的按钮 1为简体 其他为 海外
        int languageIndex = CommonFunction.getLanguageIndex(this);
        //服务端获取的注册方式
        int[] loginFlags = Common.getInstance().getRegisterFlag();
        if (languageIndex == 1) {
            faceBookLy.setVisibility(View.GONE);
            if (loginFlags != null) {
                for (int i :loginFlags){
                    if (i == 1){
                        qqLy.setVisibility(View.VISIBLE);
                        if (Config.isShowGoogleApp) {
                            if (!CommonFunction.isClientInstalled(this,"com.tencent.mobileqq")){
                                qqLy.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }

            weCahtAbroadLy.setVisibility(View.VISIBLE);
        } else {
            faceBookLy.setVisibility(View.VISIBLE);
            weCahtAbroadLy.setVisibility(View.VISIBLE);
        }

        gtAppDlgTask = GtAppDlgTask.getInstance();
        gtAppDlgTask.setContext(RegisterNewActivity.this);
        gtAppDlgTask.setGeetBackListener(geetBackListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register_new_abroad_wechat:
                accounttype = 7;
                long currentTimeRegister = Calendar.getInstance().getTimeInMillis();
                if (currentTimeRegister - lastClickTime > MIN_CLICK_DELAY_TIME)
                {
                    lastClickTime = currentTimeRegister;
                    LoginHttpProtocol.claerLogin(RegisterNewActivity.this, accounttype);
                    SharedPreferenceUtil.getInstance(RegisterNewActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                    LoginHttpProtocol.wechatLogin(RegisterNewActivity.this, mTheHandler, MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    statisticsweChatRegisterCount(RegisterNewActivity.this, "0");
                }
                break;
            case R.id.btn_register_new_abroad_facebook:
                accounttype = 4;
                long currentTimeFacebook = Calendar.getInstance().getTimeInMillis();
                if (currentTimeFacebook - lastClickTime > MIN_CLICK_DELAY_TIME) {
                    lastClickTime = currentTimeFacebook;
                    LoginHttpProtocol.claerLogin(RegisterNewActivity.this, accounttype);
                    SharedPreferenceUtil.getInstance(RegisterNewActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                    LoginHttpProtocol.facebookLogin(RegisterNewActivity.this, mTheHandler, MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    statisticsfaceBookRegisterCount(RegisterNewActivity.this, "0");
                }
                break;
            case R.id.btn_register_new_abroad_qq:
                accounttype = 1;
                LoginHttpProtocol.claerLogin(RegisterNewActivity.this, accounttype);
                SharedPreferenceUtil.getInstance(RegisterNewActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                LoginHttpProtocol.qqLogin(RegisterNewActivity.this, mTheHandler, MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                statisticsqqRegisterCount(RegisterNewActivity.this, "0");
                break;
            case R.id.ll_old_user_login1:
            case R.id.tv_login_old_user:
                Intent intent = new Intent(RegisterNewActivity.this, LoginActivity.class);
                startActivity(intent);
                break;

        }

    }

    private static void statisticsqqRegisterCount(Context context, String enterType) {
        qqRegisterCount = SharedPreferenceCache.getInstance(context).getInt("qqRegisterCountAll", 0);
        qqRegisterCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "6", "1", "" + qqRegisterCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("qqRegisterCountAll", qqRegisterCount);
    }

    private static void statisticsweChatRegisterCount(Context context, String enterType) {
        weChatRegisterCount = SharedPreferenceCache.getInstance(context).getInt("weChatRegisterCountAll", 0);
        weChatRegisterCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "6", "7", "" + weChatRegisterCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("weChatRegisterCountAll", weChatRegisterCount);
    }

    private static void statisticsfaceBookRegisterCount(Context context, String enterType) {
        faceBookRegisterCount = SharedPreferenceCache.getInstance(context).getInt("faceBookRegisterCountAll", 0);
        faceBookRegisterCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "6", "4", "" + faceBookRegisterCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("faceBookRegisterCountAll", faceBookRegisterCount);
    }

    /* 登陆接口的网络回调
    * */
    class HttpCallBackImpl implements HttpCallBack{
        private WeakReference<RegisterNewActivity> mActivity;
        public HttpCallBackImpl(RegisterNewActivity activity){
            mActivity = new WeakReference<RegisterNewActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            RegisterNewActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);
            }
            if (entity != null && entity.isSuccess()) {
                entity.setUrl();
                entity.loginSuccess(BaseApplication.appContext);

                ConnectorManage.getInstance(activity).setKey(entity.key); //遇见http接口 token

                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);

                String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(userStateKey, true);

                SpaceModel.getInstance(BaseApplication.appContext).setAutoLogin(true);// 自动登录

                //用户登陆成功才能发送app启动统计
                Statistics.onAppCreated();
            } else {
                //登陆接口失败
            }
            //下面进一步对登陆返回结果进行处理
            HashMap<String, Object> res = new Hashon().fromJson(result);
            if (activity.accounttype == 2) {
                LoginHttpProtocol.processWeiboLogin(activity, result, flag, res, mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "6", null );
            } else if (activity.accounttype == 1) {
                LoginHttpProtocol.processQqLogin(activity, result, flag, res, mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "6", null );
            } else if (activity.accounttype == 4) {
                LoginHttpProtocol.processFacebookLogin(activity, result, flag, res, mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "6", null );
            } else if (activity.accounttype == 7) {
                LoginHttpProtocol.processWechatLogin(activity, result, flag, res, mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "6", null );
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {
            RegisterNewActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            LoginHttpProtocol.claerLogin(activity,accounttype);
            Message msg = new Message();
            msg.what = MSG_LOGIN_RES;
            msg.arg1 = MSG_ARG1_FAIL;
            mTheHandler.sendMessage(msg);
        }
    };

    protected void showDialog() {
        hideDialog();
        if (pd == null) {
            pd = DialogUtil.showProgressDialog(this, R.string.login, R.string.please_wait, null);
            return;
        }
        pd.show();
    }

    protected void hideDialog() {
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public void destroyDialog( )
    {
        if ( pd != null ) {
            pd.dismiss( );
            pd = null;
        }
    }

    /* 注册接口的网络回调
    * */
    static class HttpCallbackFinalImpl implements HttpCallBack{
        private WeakReference<RegisterNewActivity> mActivity;
        public HttpCallbackFinalImpl(RegisterNewActivity activity){
            mActivity = new WeakReference<RegisterNewActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            RegisterNewActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralSuccess(result, flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            RegisterNewActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralError(e,flag);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
       if (DO_USER_REGISTER == flag){
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);
            }
            if (entity != null && entity.isSuccess()) {
                //这个位置不需要密码不需要MD5加密
                DO_LOGIN_FLAG = LoginHttpProtocol.doLogin(RegisterNewActivity.this, entity.account, entity.password, "", accounttype, new HttpCallbackFinalImpl(this), entity.unionid);
            } else {
                ErrorCode.showError(RegisterNewActivity.this, result);
            }
        }else if(HAD_USER_REGISTER == flag){
           HadRegister bean = GsonUtil.getInstance().getServerBean(result, HadRegister.class);
           if(bean == null){
               //转换失败让其直接登陆，但是登陆时候不存在还会继续注册
               Toast.makeText(BaseApplication.appContext, getString(R.string.e_101), Toast.LENGTH_SHORT).show();
               return;
           }
           mCountryCode = bean.countryCode;
           //极验弹窗由是否注册过接口返回
           Common.getInstance().setGeetestSwitch(bean.geetestSwitch);
           if(bean.hadReg == 1){ //用户已经注册过
               CommonFunction.log("login", "user had register, geetestSwitch="+Common.getInstance().getGeetestSwitch());
               if (Common.getInstance().getGeetestSwitch() == 1) {
                   mTheHandler.sendEmptyMessage(MSG_SHOW_GEET);
               }else{
                   userLogin();
               }
           }else{ //用户没注册
               CommonFunction.log("login", "user not register");
               if(Common.getInstance().getBindPhoneSwitch() == 0) { //不需要绑定手机号
                   if (Common.getInstance().getGeetestSwitch() == 1) {
                       mTheHandler.sendEmptyMessage(MSG_SHOW_GEET);
                   }else{
                       userLogin();
                   }
               }else {
                   //进行绑定手机号逻辑
                   mTheHandler.sendEmptyMessage(MSG_BIND_TELPHONE);
               }
           }
        }else if(DO_LOGIN_FLAG == flag){
           hideWaitDialog();
           LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
           Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
           if(item != null && entity != null){
               entity.setItem(item);

           }
           if (entity != null && entity.isSuccess()) {
               entity.setUrl();
               entity.loginSuccess(BaseApplication.appContext);

               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);

               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_ACCOUNT, entity.account);
               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LOGIN_PASSWORD, entity.password);

               String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
               SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(userStateKey, true);

               SpaceModel.getInstance(BaseApplication.appContext).setAutoLogin(true);// 自动登录

               //用户登陆成功才能发送app启动统计
               Statistics.onAppCreated();

               // 判断是否是第一次开启应用
               CommonFunction.showGuideView(RegisterNewActivity.this);
               finish();

//               boolean isFirstOpen = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//               // 如果是第一次启动，则先进入功能引导页
//               if (isFirstOpen) {
//                   SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//                   Intent intent = new Intent(RegisterNewActivity.this, GuideActivity.class);
//                   startActivity(intent);
//                   finish();
//               } else {
//                   Intent intent = new Intent(RegisterNewActivity.this, MainFragmentActivity.class);
//                   startActivity(intent);
//                   finish();
//               }
           } else {
               ErrorCode.showError(BaseApplication.appContext, result);
           }
       }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if(HAD_USER_REGISTER == flag){
            LoginHttpProtocol.claerLogin(this,accounttype);
            Toast.makeText(BaseApplication.appContext, getString(R.string.e_101), Toast.LENGTH_SHORT).show();
        }else {
            ErrorCode.toastError(this, e);
        }
    }

    static class TheHandler extends Handler {
        private WeakReference<RegisterNewActivity> cActivity;

        public TheHandler(RegisterNewActivity activity) {
            cActivity = new WeakReference<RegisterNewActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CommonFunction.log("RegisterNewActivity", "TheHandler handle message " + msg.what);
            RegisterNewActivity activity = cActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return;
            }
            switch (msg.what) {
                case MSG_WHAT_LOGIN: {
                        if (msg.arg1 == MSG_ARG1_SUCCESS) {
                            // 跳转到主页面
                            activity.startActivity(new Intent(activity, MainFragmentActivity.class));
                            activity.finish();
                        } else {
                            CommonFunction.log("RegisterNewActivity", "String:" + msg.obj);
                            if (msg.obj != null) {
                                if (!((String) msg.obj).contains("\"error\":4108")) {
                                    ErrorCode.showError(activity, (String) msg.obj);
                                }
                            }
                        }
                    }
                    break;
                case MSG_HAD_REGISTER:{ //邀请验证用户是否注册过
                        CommonFunction.log("login", "MSG_HAD_REGISTER check user had register");
                        String openId = (String)msg.obj;
                        activity.HAD_USER_REGISTER = LoginHttpProtocol.userHadRegister(activity.accounttype,"",activity.accounttype,openId, new HttpCallbackFinalImpl(activity));
                    }
                    break;
                case MSG_GO_REGISTER: { // 第三方注册返回
                        SharedPreferenceUtil.getInstance(activity).putInt(SharedPreferenceUtil.LOGIN_TYPE, activity.accounttype);
                        activity.registeredUser(activity,(Bundle) msg.obj);
                    }
                    break;
                case MSG_LOGIN_RES: { // 第三方登录返回
                        if (msg.arg1 == MSG_ARG1_FAIL) { // 失败
                            CommonFunction.log("login", "MSG_LOGIN_RES login fail");
                            if (msg.obj != null) {
                                String obj = (String) msg.obj;
                                JSONObject jo = null;
                                try {
                                    jo = new JSONObject(obj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                if (jo != null) {
                                    if (jo.optInt("error") == 4102) { // 封停用户提示
                                        try {
                                            String errordesc = jo.getString("errordesc");
                                            if (errordesc != null && errordesc.contains("||")) {
                                                int index = errordesc.lastIndexOf("|");
                                                String s = errordesc.substring(index + 1,errordesc.length());
                                                String errorHint = String.format(activity.getString(R.string.login_error_hint), s);
                                                Toast.makeText(BaseApplication.appContext, errorHint, Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }else{
                                Toast.makeText(BaseApplication.appContext, activity.getString(R.string.e_101), Toast.LENGTH_SHORT).show();
                            }
                        } else if (msg.arg1 == MSG_ARG1_SUCCESS) {
                            CommonFunction.log("login", "MSG_LOGIN_RES login success");
                            //判断是否是第一次进入app
                            CommonFunction.showGuideView(activity);

                            // 成功，跳转到主页面
//                            activity.startActivity(new Intent(activity, MainFragmentActivity.class));
                            activity.finish();
                        }
                    }
                    break;
                case MSG_SHOW_GEET: {
                        if (activity.gtAppDlgTask != null) {
                            if(activity.gtAppDlgTask.getGeetBackListener()!=activity.geetBackListener){
                                activity.gtAppDlgTask.setContext(activity);
                                activity.gtAppDlgTask.setGeetBackListener(activity.geetBackListener);
                            }
                            activity.gtAppDlgTask.show();
                        }
                    }
                    break;
                case MSG_BIND_TELPHONE: {
                       if(activity.accounttype == 7){ //微信
                           LoginHttpProtocol.getWeChatUserInfo(activity,activity.mTheHandler);
                       }else if(activity.accounttype == 4){ //facebook
                           LoginHttpProtocol.getFacebookUserInfo(activity,activity.mTheHandler);
                       }else if(activity.accounttype == 1){ //QQ
                           LoginHttpProtocol.getQQUserInfo(activity,activity.mTheHandler);
                       }
                    }
                    break;
                case MSG_THIRD_PART_USER_INFO:{
                        if(msg.obj!=null) {
                            Bundle data = (Bundle)msg.obj;
                            data.putInt("accountType",activity.accounttype);
                            data.putString("countryCode",activity.mCountryCode);
                            Intent t = new Intent(activity, BindingTelPhoneActivity.class);
                            t.putExtra("userInfo", data); //用户信息
                            activity.startActivityForResult(t, BIND_PHONE_REQUEST_CODE);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CommonFunction.log("RegisterNewActivity", "onActivityResult() requestCode=" + requestCode + ", resultCode="+resultCode);
        if(BIND_PHONE_REQUEST_CODE == requestCode){

        }else {
            if (LoginHttpProtocol.shareUtils != null) {
                LoginHttpProtocol.shareUtils.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void registeredUser(Context context,Bundle bundle){
        String opneId = bundle.getString("id");
        String unionid = bundle.getString("unionid");
        String token = bundle.getString("token");
        long expires = bundle.getLong("expires");
        String nickName = bundle.getString("nickname");
        String gender = bundle.getString("gender");//取消默认展示性别
        Me u = new Me();
        int wannaMeet = u.getWannaMeet();
        String birthday = bundle.getString("birthday");
        String iconUrl = bundle.getString("icon");
        showWaitDialog(context);
        DO_USER_REGISTER = LoginHttpProtocol.userRegisterOther(RegisterNewActivity.this, "" + accounttype, token, opneId, expires, "", "", nickName, gender, wannaMeet, birthday, iconUrl, new HttpCallbackFinalImpl(RegisterNewActivity.this) , unionid,"","","");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonFunction.log("RegisterNewActivity", "onDestroy() into");

        EventBus.getDefault().unregister(this);
        if (gtAppDlgTask != null) {
            gtAppDlgTask.setGeetBackListener(null);
            gtAppDlgTask.onDestory();
        }
        LoginHttpProtocol.shareUtils = null;
    }

    @Subscribe
    public void onEvent(String event){
        if (event.equals("register_login")){
            initView();
        }

    }

    /**
     * 极验回调
     */
    private GtAppDlgTask.GeetBackListener geetBackListener = new GtAppDlgTask.GeetBackListener(){

        @Override
        public void onSuccess(String authKey) {
            CommonFunction.log("RegisterNewActivity", "geet auth success");
            Common.getInstance().setGeetAuthKey(authKey);
            userLogin();
        }

        @Override
        public void onClose() {

        }
    };

    /*用户登陆接口，需要处理用户已注册和新注册情况，HTTP回调（httpCallBack）
    * */
    private void userLogin(){
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(BaseApplication.appContext);
        if (accounttype == 1) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_QQ);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ);
            LoginHttpProtocol.doLogin(RegisterNewActivity.this, id, token, "", 1, httpCallBack, "");
        } else if (accounttype == 2) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_SINA);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA);
            LoginHttpProtocol.doLogin(RegisterNewActivity.this, id, token, "", 2, httpCallBack, "");
        } else if (accounttype == 4) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
            LoginHttpProtocol.doLogin(RegisterNewActivity.this, id, token, "", 4, httpCallBack, "");
        } else if (accounttype == 7) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_WECHAT);
            String unionid = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_UNIONID_WECHAT);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
            LoginHttpProtocol.doLogin(RegisterNewActivity.this, id, token, "", 7, httpCallBack, unionid);
        }
    }
}
