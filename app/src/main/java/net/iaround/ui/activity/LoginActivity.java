package net.iaround.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.GtAppDlgTask;
import net.iaround.connector.GtAppDlgTask.GeetBackListener;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.SignClickHttpProtocol;
import net.iaround.contract.LoginContract;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.InitBean;
import net.iaround.model.entity.Item;
import net.iaround.model.im.Me;
import net.iaround.model.login.bean.HadRegister;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.presenter.LoginPresenter;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.Hashon;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.settings.ContactOurActivity;
import net.iaround.ui.adapter.EmailAutoCompleteAdapter;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.view.EmailAutoCompleteTextWatcher;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static net.iaround.connector.protocol.LoginHttpProtocol.weChatLoginCount;

/**
 * Class: 登录
 * Author：gh
 * Date: 2016/12/1 15:34
 * Emial：jt_gaohang@163.com
 */
public class LoginActivity extends ActionBarActivity implements LoginContract.View, View.OnClickListener, HttpCallBack {
    protected static final int SHOW_DIALOG = 10;
    protected static final int MSG_LOGIN_RES = 0x01ff;
    protected static final int MSG_GO_REGISTER = 0x02ff;
    protected static final int MSG_ARG1_FAIL = 0;
    protected static final int MSG_ARG1_SUCCESS = 1;
    protected static final int MSG_WHAT_LOGIN = 1;
    protected static final int MSG_WHAT_GENERALERROR = 0;
    protected static final int MSG_SHOW_GEET = 1000;  //极验弹窗请求
    protected static final int MSG_HAD_REGISTER = 1001;  //用户是否注册过
    protected static final int MSG_BIND_TELPHONE = 1002;  //绑定手机号
    protected static final int MSG_THIRD_PART_USER_INFO = 1003;  //获得第三方用户信息
    public static final int MIN_CLICK_DELAY_TIME = 3000;
    protected static final int BIND_PHONE_REQUEST_CODE = 99;  //activity 跳转请求码

    public static int qqLoginCount;
    public static int faceBookLoginCount;
    public static int weiboLoginCount;

    private TextView tvTitle;
    private ImageView ivLeft, ivRight;
    private HeadPhotoView ivAvatar;
    private AutoCompleteTextView editNumber;
    private EditText editPassword;
    private TextView btnLogin;
    private TextView tvPhoneAre;
    private TextView tvForgetPassword;

    private LinearLayout loginThreadLayout;

    protected boolean ssoLoginFlag;
    protected boolean chatLoginFlag;
    protected boolean spaceLoginFlag;
    protected boolean webJumpLoginFlag;

    private int flag;  //
    private int accounttype; //账户登陆的类型

    private LoginPresenter loginPresenter;
    private CustomContextDialog dynamicDetailsReportDialog;

    private int ipRetryCount = 0;   //联网错误次数
    private Dialog pd;

    private long lastClickTime = 0;

    private GtAppDlgTask gtAppDlgTask;//极验弹框

    private long DO_LOGIN_FLAG = -1; //(遇见本地用户手机号邮箱)用户登陆http请求标记
    private long DO_USER_REGISTER = -1; //注册用户http请求标记
    private long HAD_USER_REGISTER = -1; //用户是否注册过http请求标记

    private HttpCallBack httpCallBack = new HttpCallBackImpl(this);

    private TheHandler mTheHandler = new TheHandler(this);

    private String mCountryCode; //国家区号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunction.log("LoginActivity","onCreate() into");

        setContentView(R.layout.activity_login);
        hiddenKeyBoard(this);
        ssoLoginFlag = getIntent().getBooleanExtra("SSOLogin", false);
        chatLoginFlag = getIntent().getBooleanExtra("ChatLogin", false);
        spaceLoginFlag = getIntent().getBooleanExtra("SpaceLogin", false);
        webJumpLoginFlag = getIntent().getBooleanExtra("webJumpLogin", false);

        flag = getIntent().getIntExtra("flag", 1);

        dynamicDetailsReportDialog = new CustomContextDialog(LoginActivity.this, 0);
        dynamicDetailsReportDialog.setListenner(reportClick);

        initView();
        setMonitor();
        initActionbar();
        loginPresenter = new LoginPresenter();

        if (!TextUtils.isEmpty(Common.getInstance().getIcon())) {
            GlideUtil.loadCircleImage(BaseApplication.appContext, Common.getInstance().getIcon(), ivAvatar.getImageView());
        }
        CommonFunction.log("LoginActivity","onCreate() out");
    }

    private void initActionbar() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivRight = (ImageView) findViewById(R.id.iv_right);

        tvTitle.setText(R.string.login_title);
        ivLeft.setImageResource(R.drawable.title_back);
        ivRight.setImageResource(R.drawable.ranking_order_help);

        ivRight.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
        findViewById(R.id.fl_right).setOnClickListener(this);
        findViewById(R.id.fl_back).setOnClickListener(this);

    }

    @Override
    public void initView() {
        ivAvatar = (HeadPhotoView) findViewById(R.id.iv_login_avatar);
        editNumber = (AutoCompleteTextView) findViewById(R.id.edit_login_number);
        editPassword = (EditText) findViewById(R.id.edit_login_password);
        btnLogin = (TextView) findViewById(R.id.btn_login);
        tvPhoneAre = (TextView) findViewById(R.id.tv_login_phone_are);
        tvForgetPassword = (TextView) findViewById(R.id.tv_login_forget_password);

        /**
         * 设置自动补全提示文本框
         */
        editNumber.requestFocus();
        editNumber.setSelection(editNumber.getText().length());
        editNumber.addTextChangedListener(textWatcher);

        loginThreadLayout = (LinearLayout) findViewById(R.id.ly_login_thread);

        if (loginThreadLayout != null) {
            loginThreadLayout.removeAllViews();
        }

        int[] loginFlags = Common.getInstance().getLoginFlag();
        if (loginFlags == null) {
            loginFlags = new int[]{1, 2, 4, 7};
        }
        for (int i = 0; i < loginFlags.length; i++) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.view_thread_ad, null);
            TextView name = (TextView) view.findViewById(R.id.tv_thread_ad_name);
            ImageView icon = (ImageView) view.findViewById(R.id.iv_thread_ad_icon);
            switch (loginFlags[i]) {
                case 1: //QQ
                    name.setText(mContext.getString(R.string.login_qq));
                    icon.setImageResource(R.drawable.thread_qq);
                    loginThreadLayout.addView(view);
                    view.setTag(R.layout.view_thread_ad, 1);
                    view.setOnClickListener(threadClick);

                    if (Config.isShowGoogleApp) {
                        if (!CommonFunction.isClientInstalled(this, "com.tencent.mobileqq")) {
                            loginThreadLayout.removeView(view);
                        }
                    }
                    break;
                case 2: //微博
                    name.setText(mContext.getString(R.string.login_sina));
                    icon.setImageResource(R.drawable.thread_weibo);
                    loginThreadLayout.addView(view);
                    view.setTag(R.layout.view_thread_ad, 2);
                    view.setOnClickListener(threadClick);
                    break;
                case 4: //facebook
                    //根据语言显示登录的按钮 1为简体 其他为 海外
                    int languageIndex = CommonFunction.getLanguageIndex(this);
                    if (languageIndex != 1) {
                        name.setText(mContext.getString(R.string.login_facebook));
                        icon.setImageResource(R.drawable.thread_facebook);
                        loginThreadLayout.addView(view);
                        view.setTag(R.layout.view_thread_ad, 4);
                        view.setOnClickListener(threadClick);
                    }
                    break;
                case 7: //微信
                    name.setText(mContext.getString(R.string.login_wechat));
                    icon.setImageResource(R.drawable.thread_weixin);
                    loginThreadLayout.addView(view);
                    view.setTag(R.layout.view_thread_ad, 7);
                    view.setOnClickListener(threadClick);
                    break;
            }
        }

        gtAppDlgTask = GtAppDlgTask.getInstance();
        gtAppDlgTask.setContext(LoginActivity.this);
        gtAppDlgTask.setGeetBackListener(geetBackListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            EmailAutoCompleteAdapter adapter = new EmailAutoCompleteAdapter(this);
            editNumber.setAdapter(adapter);
            editNumber.addTextChangedListener(new EmailAutoCompleteTextWatcher(editNumber, adapter));
        }
    }

    @Override
    public void setMonitor() {
        btnLogin.setOnClickListener(this);
        tvPhoneAre.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);

        editNumber.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
    }

    @Override
    protected void actionBarRightGoPressed() {
        super.actionBarRightGoPressed();

    }

    @Override
    public void onClick(View view) {
        SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, "");
        SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
        SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");
        switch (view.getId()) {
            case R.id.btn_login:
                accounttype = 6;  //遇见账号登陆（手机|邮箱|遇见ID）
                String number = editNumber.getText().toString();
                if (number == null || number.length() <= 0) { // 手机号为空
                    Toast.makeText(LoginActivity.this, R.string.register_please_enter_number, Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = editPassword.getText().toString();
                if (password == null || password.length() <= 0) { // 密码为空
                    Toast.makeText(LoginActivity.this, R.string.register_please_enter_password, Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedPreferenceUtil.getInstance(LoginActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(Constants.PHONE_NUM, number);

//                if (Common.getInstance().getGeetestSwitch() == 1){
//                    gtAppDlgTask.show();
//                }else{
//                    //遇见号登录的情况，密码MD5加密
//                    String md5Password = CryptoUtil.md5(password);
//                    //登陆接口回调不需要处理用户注册的情况
//                    DO_LOGIN_FLAG = loginPresenter.doLogin(this, number, md5Password, accounttype, new HttpCallbackFinalImpl(this), "");
//                    showWaitDialog();
//                }
                //是否弹极验开关由是否注册过接口返回
                Message message = mTheHandler.obtainMessage();
                message.what = MSG_HAD_REGISTER;
                message.obj = number;
                mTheHandler.sendMessage(message);
                break;
            case R.id.tv_login_phone_are:
                Intent intent = new Intent(LoginActivity.this, CountrySelectActivity.class);
                startActivityForResult(intent, 401);
                break;
            case R.id.tv_login_forget_password://忘记密码
                CommonFunction.hideInputMethod(mContext, btnLogin);
                dynamicDetailsReportDialog.show();
                break;
            case R.id.fl_left:
            case R.id.iv_left:
            case R.id.fl_back:
                hiddenKeyBoard(this);
                finish();
                break;
            case R.id.fl_right:
            case R.id.iv_right:
                Intent contactIntent = new Intent(mContext, ContactOurActivity.class);
                startActivity(contactIntent);
                break;
        }

    }

    public View.OnClickListener threadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((int) v.getTag(R.layout.view_thread_ad)) {
                case 4: //facebook登陆
                    accounttype = 4;
                    LoginHttpProtocol.claerLogin(LoginActivity.this, accounttype);
                    SharedPreferenceUtil.getInstance(LoginActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                    LoginHttpProtocol.facebookLogin(LoginActivity.this, mTheHandler, MSG_LOGIN_RES, "5", httpCallBack, SHOW_DIALOG);
                    setClickEnable(false);
                    statisticsfaceBookLoginCount(LoginActivity.this, "0");
                    break;
                case 1: //qq登陆
                    accounttype = 1;
                    LoginHttpProtocol.claerLogin(LoginActivity.this, accounttype);
                    SharedPreferenceUtil.getInstance(LoginActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                    LoginHttpProtocol.qqLogin(LoginActivity.this, mTheHandler, MSG_LOGIN_RES, "5", httpCallBack, SHOW_DIALOG);
                    setClickEnable(false);
                    statisticsqqLoginCount(LoginActivity.this, "0");
                    break;
                case 7: //wechat登陆
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTime;
                        accounttype = 7;
                        LoginHttpProtocol.claerLogin(LoginActivity.this, accounttype);
                        SharedPreferenceUtil.getInstance(LoginActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                        LoginHttpProtocol.wechatLogin(LoginActivity.this, mTheHandler, MSG_LOGIN_RES, "5", httpCallBack, SHOW_DIALOG);
                        setClickEnable(false);
                        statisticsweChatLoginCount(LoginActivity.this, "0");
                    }
                    break;
                case 2: //weibo登陆
                    accounttype = 2;
                    LoginHttpProtocol.claerLogin(LoginActivity.this, accounttype);
                    SharedPreferenceUtil.getInstance(LoginActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                    LoginHttpProtocol.weiboLogin(LoginActivity.this, mTheHandler, MSG_LOGIN_RES, "5", httpCallBack, SHOW_DIALOG);
                    setClickEnable(false);
                    statisticsweiboLoginCount(LoginActivity.this, "0");
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 401 && data != null) {
            String are = data.getStringExtra("area");
            if (!CommonFunction.isEmptyOrNullStr(are) && are.contains("+")) {
                // 清空原来输入的手机号
                editNumber.setText("");

                String[] spitString = are.split("\\+");
                String areaCode = "+" + spitString[1];
                editNumber.setText(areaCode);
                editNumber.setSelection(areaCode.length());
                if (spitString[1].equals("+86")) {
                    // 大陆手机限制11位输入
                    editNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                } else {
                    editNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
                }
            }
        }

        if (LoginHttpProtocol.shareUtils != null) {
            LoginHttpProtocol.shareUtils.onActivityResult(requestCode, resultCode, data);
        }
        setClickEnable(true);
    }

    /* 注册接口的网络回调
    * */
    static class HttpCallbackFinalImpl implements HttpCallBack{
        private WeakReference<LoginActivity> mActivity;
        public HttpCallbackFinalImpl(LoginActivity activity){
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            LoginActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralSuccess(result, flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            LoginActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralError(e,flag);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (DO_LOGIN_FLAG == flag) {
            hideWaitDialog();
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);
            }

            if (entity != null && entity.isSuccess()) {
                entity.setUrl();
                entity.loginSuccess(LoginActivity.this);
                //当用户使用自有账号登录时，可以这样统计：
                MobclickAgent.onProfileSignIn(Common.getInstance().loginUser.getUid()+"");
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.LOGIN_ACCOUNT, entity.account);
                SharedPreferenceUtil.getInstance(LoginActivity.this).putString(SharedPreferenceUtil.LOGIN_PASSWORD, entity.password);
                String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
                SharedPreferenceUtil.getInstance(LoginActivity.this).putBoolean(userStateKey, true);

                SpaceModel.getInstance(this).setAutoLogin(true);// 自动登录

                // 判断是否是第一次开启应用
                boolean isFirstOpen = SharedPreferenceUtil.getInstance(LoginActivity.this).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
                //用户登陆成功才有用户id来发送app 启动统计
                Statistics.onAppCreated();
                // 如果是第一次启动，则先进入功能引导页
                CommonFunction.showGuideView(LoginActivity.this);
                finish();

//                if (isFirstOpen) {
//                    SharedPreferenceUtil.getInstance(LoginActivity.this).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//                    Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//                } else {
//                    Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            } else {
                if (entity != null)
                {//登录失败
                    if (entity.error == 4102)
                    {
                        String errordesc = entity.errordesc;
                        if (errordesc != null && errordesc.contains(" ")) {
                            int index = errordesc.indexOf(" ");
                            String s = errordesc.substring(index + 1,index + 9);
                            String errorHint = String.format(getString(R.string.login_error_hint), s);
                            Toast.makeText(LoginActivity.this, errorHint, Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        ErrorCode.showError(LoginActivity.this, result);
                    }
                }
            }
        }else if (DO_USER_REGISTER == flag){
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);
            }
            if (entity != null && entity.isSuccess()) {
                //登陆接口回调不需要处理用户注册的情况
                DO_LOGIN_FLAG = LoginHttpProtocol.doLogin(LoginActivity.this, entity.account, entity.password, "", accounttype, new HttpCallbackFinalImpl(this),entity.unionid);
            } else {
                ErrorCode.showError(LoginActivity.this, result);
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
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if(HAD_USER_REGISTER == flag){
            LoginHttpProtocol.claerLogin(this,accounttype);
            Toast.makeText(BaseApplication.appContext, getString(R.string.e_101), Toast.LENGTH_SHORT).show();
        }else {
            btnLogin.setEnabled(true);
            ///mTheHandler.sendMessage(mTheHandler.obtainMessage(MSG_WHAT_GENERALERROR));
            ErrorCode.toastError(this, e);
        }
    }

    static class HttpCallBackImpl implements HttpCallBack {
        private WeakReference<LoginActivity> mActivity;
        public HttpCallBackImpl(LoginActivity activity){
            mActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            LoginActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.hideDialog();
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            if (entity != null && entity.isSuccess()) {
                entity.setUrl();
                entity.loginSuccess(activity);
                ConnectorManage.getInstance(BaseApplication.appContext).setKey(entity.key);
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
                String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(userStateKey, true);
                SpaceModel.getInstance(BaseApplication.appContext).setAutoLogin(true);// 自动登录

            }else{
                //登陆请求失败
            }
            HashMap<String, Object> res = new Hashon().fromJson(result);
            if (activity.accounttype == 2) {
                LoginHttpProtocol.processWeiboLogin(activity, result, flag, res, activity.mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "5", new HttpCallbackFinalImpl(activity));
            } else if (activity.accounttype == 1) {
                LoginHttpProtocol.processQqLogin(activity, result, flag, res, activity.mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "5", new HttpCallbackFinalImpl(activity));
            } else if (activity.accounttype == 4) {
                LoginHttpProtocol.processFacebookLogin(activity, result, flag, res, activity.mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "5", new HttpCallbackFinalImpl(activity));
            } else if (activity.accounttype == 7) {
                LoginHttpProtocol.processWechatLogin(activity, result, flag, res, activity.mTheHandler, MSG_LOGIN_RES, MSG_GO_REGISTER, "5", new HttpCallbackFinalImpl(activity));
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };

    static class TheHandler extends Handler {
        private WeakReference<LoginActivity> cActivity;

        public TheHandler(LoginActivity activity) {
            cActivity = new WeakReference<LoginActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            CommonFunction.log("LoginActivity", "ControlLoginActivity handle message " + msg.what);
            LoginActivity activity = cActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return;
            }
            activity.hideDialog();
            switch (msg.what) {
                case MSG_WHAT_LOGIN: {
                    if (msg.arg1 == MSG_ARG1_SUCCESS) {
                        activity.doJumpMainActivity();
                    } else {
                        CommonFunction.log("LoginActivity", "String:" + msg.obj);
                        if (msg.obj != null && cActivity != null) {
                            activity.editPassword.setText("");
                            if (!((String) msg.obj).contains("\"error\":4108")) {
                                ErrorCode.showError(cActivity.get(), (String) msg.obj);
                            }
                        }
                    }
                }
                break;
                case MSG_WHAT_GENERALERROR: {
                    CommonFunction.log("login", "MSG_WHAT_GENERALERROR login error");
                    activity.ipRetryCount++;
                    LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                    map.put("error", ErrorCode.E_101);
                    map.put("errordesc", "");
                    ErrorCode.showError(activity, JsonUtil.mapToJsonString(map));
                    if (activity.ipRetryCount > Config.loginUrls.length) {
                        return;
                    }
                    StartModel.getInstance().changeRegUrl(activity);
                }
                break;
                case MSG_LOGIN_RES: { // 第三方登录返回
                    // arg1 = 0 失败；arg1 = 1成功；arg2 = 1新用户；arg2 = 0老用户；obj -- 提示
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
                                if (jo.optInt("error") == 4102) {
                                    // 封停用户提示
                                    try {
                                        String errordesc = jo.getString("errordesc");
                                        if (errordesc != null && errordesc.contains("||")) {
                                            int index = errordesc.lastIndexOf("|");
                                            String s = errordesc.substring(index + 1,errordesc.length());
                                            String errorHint = String.format(activity.getString(R.string.login_error_hint), s);
                                            Toast.makeText(activity, errorHint, Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else if (msg.arg1 == MSG_ARG1_SUCCESS && cActivity.get() != null) {
                        CommonFunction.log("login", "MSG_LOGIN_RES login success");
                        // 登录成功后
                        activity.doJumpMainActivity();
                    }
                }
                break;
                case MSG_HAD_REGISTER:{
                    CommonFunction.log("login", "MSG_HAD_REGISTER check user had register");
                    String openId = (String)msg.obj;
                    activity.HAD_USER_REGISTER = LoginHttpProtocol.userHadRegister(activity.accounttype,openId,activity.accounttype,openId, new HttpCallbackFinalImpl(activity));
                }
                break;
                case MSG_GO_REGISTER: { // 第三方注册返回
                    CommonFunction.log("login", "MSG_GO_REGISTER register request");
                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putInt(SharedPreferenceUtil.LOGIN_TYPE, activity.accounttype);
                    activity.setClickEnable(false);
                    activity.registeredUser(activity,(Bundle) msg.obj);
                }
                break;
                case MSG_SHOW_GEET:  //极验弹框请求
                    if (activity.gtAppDlgTask != null) {
                        if(activity.gtAppDlgTask.getGeetBackListener()!=activity.geetBackListener){
                            activity.gtAppDlgTask.setContext(activity);
                            activity.gtAppDlgTask.setGeetBackListener(activity.geetBackListener);
                        }
                        activity.gtAppDlgTask.show();
                    }
                    break;
                case MSG_BIND_TELPHONE: {
                    if(activity.accounttype == 7){ //微信
                        LoginHttpProtocol.getWeChatUserInfo(activity,activity.mTheHandler);
                    }else if(activity.accounttype == 4){ //facebook
                        LoginHttpProtocol.getFacebookUserInfo(activity,activity.mTheHandler);
                    }else if(activity.accounttype == 1){ //QQ
                        LoginHttpProtocol.getQQUserInfo(activity,activity.mTheHandler);
                    }else if(activity.accounttype == 2){ //weibo
                        LoginHttpProtocol.getWeiboUserInfo(activity,activity.mTheHandler);
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

    protected void doJumpMainActivity() {
        //用户登陆成功才有用户id来发送app 启动统计
        Statistics.onAppCreated();

        // 判断是否是第一次开启应用
        CommonFunction.showGuideView(this);
        finish();
//        boolean isFirstOpen = SharedPreferenceUtil.getInstance(LoginActivity.this).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//        // 如果是第一次启动，则先进入功能引导页
//        if (isFirstOpen) {
//            SharedPreferenceUtil.getInstance(LoginActivity.this).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//            Intent intent = new Intent(LoginActivity.this, GuideActivity.class);
//            startActivity(intent);
//            finish();
//
//        } else {
//            Intent intent = new Intent(LoginActivity.this, MainFragmentActivity.class);
//            startActivity(intent);
//            finish();
//        }
    }

    private void setClickEnable(boolean isclick) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LoginHttpProtocol.shareUtils = null;
        destroyWaitDialog();
        if (gtAppDlgTask != null) {
            gtAppDlgTask.setGeetBackListener(null);
            gtAppDlgTask.onDestory();
        }
        LoginHttpProtocol.shareUtils = null;
        if(dynamicDetailsReportDialog!=null){
            dynamicDetailsReportDialog.setListenner(null);
            dynamicDetailsReportDialog.dismiss();
        }
        CommonFunction.log("LoginActivity", "onDestroy() out");
    }

    // EditText监听
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String number = editNumber.getText().toString();
            String password = editPassword.getText().toString();
            if (number.length() > 0 && password.length() > 0) {
                btnLogin.setEnabled(true);
            } else {
                btnLogin.setEnabled(false);
            }
        }
    };

    private View.OnClickListener reportClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch ((int) view.getTag()) {
                case 0:// 手机找回密码
                    if (flag == 1) {
                        Intent intent = new Intent(LoginActivity.this, ResetActivity.class);
                        startActivity(intent);
                    } else {
                        DialogUtil.showOKDialog(mContext, R.string.prompt, R.string.function_no_work, null);
                    }
                    break;
                case 1:// 邮箱找回密码
                    Intent i = new Intent(mContext, ResetEmailPasswordActivity.class);
                    startActivity(i);
                    break;
            }
            dynamicDetailsReportDialog.hide();
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

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(this);
        String result = shareDate.getString(SharedPreferenceUtil.START_PAGE_AD);
        if (!TextUtils.isEmpty(result)) {
            InitBean bean = GsonUtil.getInstance().getServerBean(result, InitBean.class);
            Common.getInstance().setLoginFlag(bean.loginflag);
            Common.getInstance().setRegisterFlag(bean.registerflag);
        }
    }

    private static void statisticsqqLoginCount(Context context, String enterType) {
        qqLoginCount = SharedPreferenceCache.getInstance(context).getInt("qqLoginCountAll", 0);
        qqLoginCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "5", "1", "" + qqLoginCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("qqLoginCountAll", qqLoginCount);
    }

    private static void statisticsweChatLoginCount(Context context, String enterType) {
        weChatLoginCount = SharedPreferenceCache.getInstance(context).getInt("weChatLoginCountAll", 0);
        weChatLoginCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "5", "7", "" + weChatLoginCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("weChatLoginCountAll", weChatLoginCount);
    }

    private static void statisticsweiboLoginCount(Context context, String enterType) {
        weiboLoginCount = SharedPreferenceCache.getInstance(context).getInt("weiboLoginCountAll", 0);
        weiboLoginCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "5", "2", "" + weiboLoginCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("weiboLoginCountAll", weiboLoginCount);
    }

    private static void statisticsfaceBookLoginCount(Context context, String enterType) {
        faceBookLoginCount = SharedPreferenceCache.getInstance(context).getInt("faceBookLoginCountAll", 0);
        faceBookLoginCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(context, "5", "4", "" + faceBookLoginCount, enterType, null);
        SharedPreferenceCache.getInstance(context).putInt("faceBookLoginCountAll", faceBookLoginCount);
    }

    private void registeredUser(Context context,Bundle bundle){
        String opneId = bundle.getString("id");
        String token = bundle.getString("token");
        String unionid = bundle.getString("unionid");
        long expires = bundle.getLong("expires");
        String nickName = bundle.getString("nickname");
        String gender = bundle.getString("gender");
        Me u = new Me();
        int wannaMeet = u.getWannaMeet();
        String birthday = bundle.getString("birthday");
        String iconUrl = bundle.getString("icon");
        showWaitDialog(context);
        DO_USER_REGISTER = LoginHttpProtocol.userRegisterOther(LoginActivity.this, ", accounttype=" + accounttype, token, opneId, expires, "", "", nickName, gender, wannaMeet, birthday, iconUrl, new HttpCallbackFinalImpl(this), unionid,"","","");
    }

    /**
     * 极验回调
     */
    private GeetBackListener geetBackListener = new GeetBackListener(){

        @Override
        public void onSuccess(String authKey) {

            Common.getInstance().setGeetAuthKey(authKey);
            userLogin();
        }

        @Override
        public void onClose() {

        }
    };

    /*根据登陆类型进行登陆
    * */
    private void userLogin(){
        SharedPreferenceUtil sharedPreferenceUtil = SharedPreferenceUtil.getInstance(BaseApplication.appContext);
        if (accounttype == 1) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_QQ);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_QQ);
            LoginHttpProtocol.doLogin(LoginActivity.this, id, token, "", 1, httpCallBack, "");
        } else if (accounttype == 2) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_SINA);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_SINA);
            LoginHttpProtocol.doLogin(LoginActivity.this, id, token, "", 2, httpCallBack, "");
        } else if (accounttype == 4) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_FACEBOOK);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_FACEBOOK);
            LoginHttpProtocol.doLogin(LoginActivity.this, id, token, "", 4, httpCallBack, "");
        } else if (accounttype == 7) {
            String id = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_ID_WECHAT);
            String token = sharedPreferenceUtil.getString(SharedPreferenceUtil.LOGIN_TOKEN_WECHAT);
            LoginHttpProtocol.doLogin(LoginActivity.this, id, token, "", 7, httpCallBack, "");
        } else if (accounttype == 6) {
            String number = editNumber.getText().toString();
            if (number == null || number.length() <= 0) { // 手机号为空
                Toast.makeText(LoginActivity.this, R.string.register_please_enter_number, Toast.LENGTH_SHORT).show();
                return;
            }
            String password = editPassword.getText().toString();
            //遇见号登录的情况，密码MD5加密
            String md5Password = CryptoUtil.md5(password);
            if (number == null || number.length() <= 0) { // 手机号为空
                Toast.makeText(LoginActivity.this, R.string.register_please_enter_password, Toast.LENGTH_SHORT).show();
                return;
            }
            sharedPreferenceUtil.putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
            sharedPreferenceUtil.putString(Constants.PHONE_NUM, number);
            //登陆接口回调不需要处理用户注册的情况
            DO_LOGIN_FLAG = loginPresenter.doLogin(LoginActivity.this, number, md5Password, accounttype, new HttpCallbackFinalImpl(this), "");
            showWaitDialog();
        }
    }
}
