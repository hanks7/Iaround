
package net.iaround.ui.space.more;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BaseHttp;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.im.BaseServerBean;
import net.iaround.model.login.bean.GeetestBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.fragment.UserFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: BindTelphoneActivity
 * @Description: 绑定手机号/修改手机号/重置密码的界面
 * @date 2013-11-23 上午10:17:38
 */
public class BindTelphoneActivity extends BaseFragmentActivity implements OnClickListener, HttpCallBack {
    private static final int MSG_DOWNLOAD_IMAGE = 1;
    private static final int MSG_PICK_IMAGE = 2;
    private static final int MSG_UPLOAD_ICON = 3;
    private static final int MSG_REGISTER = 4;
    private static final int REQ_BIND = 5;
    private static final int MSG_SMS_CODE_ERR = 6;

    /**
     * 标题栏
     */
    private ImageView title_back;
    private TextView title_name;
    private TextView title_right_text;
    /**
     * 验证码已发送至**号码的文字
     */
    TextView mTelInfo;

    /**
     * 验证码输入框
     */
    EditText mEdtMsgCode;

    /**
     * 密码输入框
     */
    EditText mEdtPwd;

    // /** 显示密码 */
    // CheckBox mShowPwd;

    /**
     * 分割线
     */
    View mSpitView;

    /**
     * 输入密码的布局
     */
    LinearLayout mSetPwdLayout;

    /**
     * 绑定按钮
     */
    Button mBtnBind;

    /**
     * 重发验证码
     */
    Button mBtnResendMsg;

    /**
     * 用户的登录方式
     */
    int mLoginType;

    /**
     * 加载等待框
     */
    private Dialog waitDialog;

    /**
     * 验证码倒计时计数器
     */
    private Timer mTimer;

    /**
     * 验证码发送间隔（秒）
     */
    private int mMsgCodeRate = 60;
    /**
     * 临时计数器
     */
    private int tmpRate = mMsgCodeRate;

    /**
     * 用户的手机号码
     */
    private String mTelphoneNum;

    /**
     * 区域号码
     */
    private String mAreaCode;

    /**
     * 类型（0：绑定手机且需设置密码/1：绑定手机/2：修改手机/3：找回密码/5:手机注册/6:第三方注册绑定手机）
     */
    private int type = 0;

    private long flag_check_code = -1;

    protected boolean ssoLoginFlag;
    protected boolean chatLoginFlag;
    protected boolean spaceLoginFlag;
    private int loginType;
    private String token;
    private String id;
    private String birthday;
    private String nickname;
    private String gender;
    private int wannaMeet;
    private String iconUrl;

    private String sign;
    private boolean verified;
    private String verifiedReason;
    private int verifiedType;
    private String tvAge;
    private String mCountryCode;
    private String uploadKey;
    private String uploadPictureUrl;

    private String md5Pwd;
    /**
     * 验证码
     */
    private String msgCode;
    private String msgRsa;


    GeetestBack geetestBack = new GeetestBack();

//    GtAppDlgTask gtAppDlgTask;

    private long flagGetMsgCode;
//    private HttpCallBack httpCallBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ssoLoginFlag = getIntent().getBooleanExtra("SSOLogin", false);
        chatLoginFlag = getIntent().getBooleanExtra("ChatLogin", false);
        spaceLoginFlag = getIntent().getBooleanExtra("SpaceLogin", false);

        loginType = getIntent().getExtras().getInt("loginType");
        token = getIntent().getExtras().getString("token");
        id = getIntent().getExtras().getString("id");
        birthday = getIntent().getExtras().getString("birthday");
        nickname = getIntent().getExtras().getString("nickname");
        gender = getIntent().getExtras().getString("gender");
        wannaMeet = getIntent().getExtras().getInt("wannaMeet");
        iconUrl = getIntent().getExtras().getString("iconUrl");
        uploadKey = getIntent().getExtras().getString("uploadKey");
        uploadPictureUrl = getIntent().getExtras().getString("uploadPictureUrl");


        geetestBack.geetest_challenge = getIntent().getExtras().getString("challenge");
        geetestBack.geetest_validate = getIntent().getExtras().getString("validate");
        geetestBack.geetest_seccode = getIntent().getExtras().getString("seccode");

        // 绑定用的后续字段
        sign = getIntent().getExtras().getString("sign");
        verified = getIntent().getExtras().getBoolean("verified");
        verifiedReason = getIntent().getExtras().getString("verified_reason");
        verifiedType = getIntent().getExtras().getInt("verified_type", -1);
        tvAge = getIntent().getExtras().getString("tvAge");
        setContentView(R.layout.activity_check_telphone_new);

        title_back = (ImageView) findViewById(R.id.iv_left);
        title_name = (TextView) findViewById(R.id.tv_title);
        title_right_text = (TextView) findViewById(R.id.tv_right);
        title_right_text.setOnClickListener(this);
        title_right_text.setVisibility(View.GONE);
        mTelInfo = (TextView) findViewById(R.id.TextView02);
        mEdtMsgCode = (EditText) findViewById(R.id.edittext_msg_code);
        mSetPwdLayout = (LinearLayout) findViewById(R.id.set_pwd_layout);
        mSpitView = findViewById(R.id.spit_view);
        mEdtPwd = (EditText) findViewById(R.id.etPassword);
        // mShowPwd = ( CheckBox ) findViewById( R.id.cbShowPsw );
        mBtnResendMsg = (Button) findViewById(R.id.btn_resend_code);
        mBtnBind = (Button) findViewById(R.id.btn_start_bind);

        findViewById(R.id.fl_left).setOnClickListener(this);
        title_back.setOnClickListener(this);
        mBtnResendMsg.setOnClickListener(this);
        mBtnBind.setOnClickListener(this);
        // mShowPwd.setOnCheckedChangeListener( this );
        findViewById(R.id.delete_password_btn).setOnClickListener(this);

        mLoginType = SharedPreferenceUtil.getInstance(mActivity).getInt(
                SharedPreferenceUtil.LOGIN_TYPE);

        type = getIntent().getIntExtra("type", 0);
        mTelphoneNum = getIntent().getStringExtra("telphone");
        mCountryCode = getIntent().getStringExtra("mCountryCode");
        mAreaCode = getIntent().getStringExtra("areaCode");

        mEdtPwd.addTextChangedListener(tw);
        mEdtMsgCode.addTextChangedListener(twEdtMsgCode);

        initViews();
        initListeners();
    }

    private void initListeners() {

    }

    private void initViews() {
        CommonFunction.log("shifengxiong", "the type is == " + type + " ---- " + this.getClass().getName());
        if (type == 0) {
            // 第三方登录
            title_name.setText(getString(R.string.bind_telphone));
            mSetPwdLayout.setVisibility(View.VISIBLE);
            mSpitView.setVisibility(View.VISIBLE);
            title_right_text.setVisibility(View.INVISIBLE);
        } else if (type == 1) {
            // 遇见登录 （绑定手机 by tanzy）
            title_name.setText(getString(R.string.bind_telphone));
            mSetPwdLayout.setVisibility(View.GONE);
            mSpitView.setVisibility(View.GONE);
            title_right_text.setVisibility(View.INVISIBLE);
        } else if (type == 2) {
            // 修改手机号
            title_name.setText(getString(R.string.space_bind_mobile_desc2));
            mSetPwdLayout.setVisibility(View.GONE);
            mSpitView.setVisibility(View.GONE);
            title_right_text.setVisibility(View.INVISIBLE);
        } else if (type == 5) {
            // 手机注册
            title_name.setText(R.string.register_step2);
//			title_right_text.setVisibility( View.VISIBLE );
            title_right_text.setText(R.string.next);

            mBtnBind.setText(getString(R.string.next));
        } else if (type == 6) {
            title_name.setText(getString(R.string.bind_telphone));
            mBtnBind.setText(R.string.ok);
        } else {
            // 通过手机找回密码
            title_name.setText(getString(R.string.reset_password));
            mSetPwdLayout.setVisibility(View.VISIBLE);
            mEdtPwd.setHint(getString(R.string.enter_new_login_password));
            mSpitView.setVisibility(View.VISIBLE);
            mBtnBind.setText(getString(R.string.reset_password));
            title_right_text.setVisibility(View.INVISIBLE);
        }


        String msgString = getString(R.string.msg_code_have_send);
        String showString = msgString + mAreaCode + " " + mTelphoneNum;

        SpannableString spanString = new SpannableString(showString);
        spanString.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.chat_update_edit_user_value)),
                msgString.length(), showString.length(),
                SpannableString.SPAN_INCLUSIVE_EXCLUSIVE);
        mTelInfo.setText(spanString);
        if (Common.telphoneMsgMap.containsKey(mTelphoneNum)
                && SystemClock.elapsedRealtime() - Common.telphoneMsgMap.get(mTelphoneNum) < mMsgCodeRate * 1000) {
            // 上一次发送短信的60秒限制时间未结束
            CommonFunction.log("shifengxiong", "need limit current:" + SystemClock.elapsedRealtime()
                    + " last:" + Common.telphoneMsgMap.get(mTelphoneNum));
            tmpRate = mMsgCodeRate
                    - (int) ((SystemClock.elapsedRealtime() - Common.telphoneMsgMap
                    .get(mTelphoneNum)) / 1000);
            mBtnResendMsg.setEnabled(false);
            CommonFunction.log("bind", "get tmpRate :" + tmpRate);
            mTimer = new Timer();
            mTimer.scheduleAtFixedRate(new MyTimerTask(), 1, 1 * 1000);
        } else {
            mBtnResendMsg.setEnabled(true);
        }

        CommonFunction.showInputMethodForQuery(this, mEdtMsgCode);
    }

    /**
     * @author zhonglong kylin17@foxmail.com
     * @ClassName: MyTimerTask
     * @Description: 发送短信倒计时的TimerTask
     * @date 2013-11-23 上午10:19:51
     */
    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            if (tmpRate > 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 更新剩余时间以及界面显示
                        mBtnResendMsg.setEnabled(false);
                        mBtnResendMsg.setText(tmpRate + "s");
                        mBtnResendMsg.setTextColor(Color.BLACK);
                    }
                });
                tmpRate--;
            } else {
                CommonFunction.log("bind", "normal");
                runOnUiThread(new Runnable() {
                    public void run() {
                        // 更新剩余时间以及界面显示
                        mBtnResendMsg.setText(getString(R.string.register_resend_code));
                        mBtnResendMsg.setEnabled(true);
                        mBtnResendMsg.setTextColor(Color.WHITE);
                    }
                });
                tmpRate = mMsgCodeRate;
                mTimer.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mTimer != null) {
            mTimer.cancel();
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setBackEvent();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.equals(title_back) || v.getId() == R.id.fl_left) {
            setBackEvent();
            return;
        } else if (v.getId() == R.id.delete_password_btn) {
            mEdtPwd.setText("");
            findViewById(R.id.delete_password_layout).setVisibility(View.INVISIBLE);
            return;
        }
        showWaitDialog(true);
        switch (v.getId()) {
            case R.id.btn_resend_code://重新发送验证码

                int netType = BaseHttp.checkNetworkType(this);
                if (netType == BaseHttp.TYPE_NET_WORK_DISABLED) {
                    CommonFunction.toastMsg(this, R.string.network_req_failed);
                    showWaitDialog(false);
                    return;
                }

                mBtnResendMsg.setEnabled(false);
                mBtnResendMsg.setText("60s");
                int reqType = 3;
                if (type == 0 || type == 1) {
                    // 绑定手机号
                    reqType = 2;
                } else if (type == 2) {
                    // 修改手机
                    reqType = 3;
                } else if (type == 3) {
                    // 找回密码
                    reqType = 1;
                } else if (type == 5) {
                    // 注册
                    reqType = 4;
                } else if (type == 6) {
                    // 第三方注册
                    reqType = 4;
                }
                flagGetMsgCode = LoginHttpProtocol.getMsgCode_662(mActivity, mAreaCode, mTelphoneNum, reqType, this, geetestBack.geetest_challenge, geetestBack.geetest_validate, geetestBack.geetest_seccode,"");

                if (flagGetMsgCode <= 0) {
                    handleFail("");
                    mBtnResendMsg.setEnabled(true);
                    showWaitDialog(false);
                }
                break;
            case R.id.btn_start_bind: {

                msgCode = mEdtMsgCode.getText().toString();
                if (CommonFunction.isEmptyOrNullStr(msgCode)) {
                    toastMsg(getString(R.string.msg_code_cannot_be_null));
                    showWaitDialog(false);
                    return;
                }
                // 绑定手机需验证密码
                String strPwd = mEdtPwd.getText().toString();

                if ((type != 1 && type != 2) && (strPwd.length() > 16)) {
                    toastMsg(getString(R.string.password_should_not_be_greater_than_16));
                    showWaitDialog(false);
                    return;
                }

                if ((type != 1 && type != 2) && CommonFunction.isEmptyOrNullStr(strPwd)) {
                    toastMsg(getString(R.string.pwd_cannot_be_null));
                    showWaitDialog(false);
                    return;
                }
                if ((type != 1 && type != 2) && strPwd.length() < 6) {
                    toastMsg(getString(R.string.pwd_is_less_than_6));
                    showWaitDialog(false);
                    return;
                }
                if ((type != 1 && type != 2)
                        && CommonFunction.isPasswordValid(strPwd, strPwd) == 0) {
                    // 密码非法
                    Toast.makeText(this, R.string.password_invalid, Toast.LENGTH_SHORT)
                            .show();
                    showWaitDialog(false);
                    mEdtPwd.setText("");
                    return;
                }

                md5Pwd = CryptoUtil.md5(strPwd);
                if (type == 3) {
                    long flag_find = SpaceModelNew.getInstance(mActivity).findUserPwd(
                            mActivity, mAreaCode, mTelphoneNum, msgCode, md5Pwd, this);
                    if (flag_find <= 0) {
                        handleFail("");
                        showWaitDialog(false);
                    }
                } else if (type == 5) {
                    flag_check_code = LoginHttpProtocol.getMsgIdentifyingCode(mContext,
                            mAreaCode, mTelphoneNum, msgCode, this);
                } else if (type == 6) {

                } else {
                    long flag_bind = SpaceModelNew.getInstance(mActivity).bindUserTelphone(
                            mActivity, mAreaCode, mTelphoneNum, msgCode, md5Pwd, this);
                    if (flag_bind <= 0) {
                        handleFail("");
                        showWaitDialog(false);
                    }else {
                        int openMiguRead = SharedPreferenceUtil.getInstance(this).getInt(UserFragment.OPEN_MIGU_READ);
                        String openMiguReadUrl = SharedPreferenceUtil.getInstance(this).getString(UserFragment.OPEN_MIGU_READ_URL);
                        if (openMiguRead == 1 && !TextUtils.isEmpty(openMiguReadUrl)) {
                            Common.getInstance().setNewGameCount(0);// 先将新游戏数量清零，使new标志消失
//                            CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
                            Intent intent = new Intent();
                            intent.setClass(BindTelphoneActivity.this, WebViewAvtivity.class);
                            intent.putExtra(WebViewAvtivity.WEBVIEW_URL, openMiguReadUrl);
                            intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE, true);
                            startActivity(intent);
                        }
                    }
                }
            }
            break;

            case R.id.title_right_text: {
                msgCode = mEdtMsgCode.getText().toString();
                if (CommonFunction.isEmptyOrNullStr(msgCode.trim())) { // 去掉尾部空格后判断字符串是否为空
                    toastMsg(getString(R.string.msg_code_cannot_be_null));
                    showWaitDialog(false);
                    return;
                }
                for (int i = 0; i < msgCode.length(); i++) { // 判断是否为数字
                    if (!Character.isDigit(msgCode.charAt(i))) {
                        toastMsg(getString(R.string.input_correct_code));
                        showWaitDialog(false);
                        return;
                    }
                }

                // 绑定手机需验证密码
                String strPwd = mEdtPwd.getText().toString();
                if (CommonFunction.isEmptyOrNullStr(strPwd)) {
                    toastMsg(getString(R.string.pwd_cannot_be_null));
                    showWaitDialog(false);
                    return;
                }
                if (strPwd.length() < 6) {
                    toastMsg(getString(R.string.pwd_is_less_than_6));
                    showWaitDialog(false);
                    return;
                }
                if (strPwd.length() > 16) {
                    toastMsg(getString(R.string.password_should_not_be_greater_than_16));
                    showWaitDialog(false);
                    return;
                }
                if (CommonFunction.isPasswordValid(strPwd, strPwd) == 0) {
                    // 密码非法
                    Toast.makeText(this, R.string.password_invalid, Toast.LENGTH_SHORT)
                            .show();
                    showWaitDialog(false);
                    mEdtPwd.setText("");
                    return;
                }
                md5Pwd = CryptoUtil.md5(strPwd);

                try {
                    // 加密
                    msgRsa = CryptoUtil.rsaEncrypt(msgCode, CryptoUtil.VERIFY_PUBLIC_KEY);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (type == 6) {
//					String email = mCountryCode + "|" + mTelphoneNum;
//					ThirdPartyLoginHelper.doRegister( this , handler , MSG_REGISTER , token ,
//							id , 0 , email , strPwd , nickname , iconUrl , gender , wannaMeet ,
//							birthday , loginType , msgRsa , this,"","","","","" );//jiqiang

                } else {
                    flag_check_code = LoginHttpProtocol.getMsgIdentifyingCode(mContext,
                            mAreaCode, mTelphoneNum, msgCode, this);
                }
            }
            break;

            default:
                break;
        }

    }

    /**
     * 处理返回事件
     */
    private void setBackEvent() {
        CommonFunction.hideInputMethod(mContext, findViewById(R.id.input_layout_foucus));
        finish();
    }


    @Override
    public void onGeneralError(final int e, final long flag) {
        super.onGeneralError(e, flag);
        showWaitDialog(false);
        ErrorCode.toastError(mContext, e);

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                CommonFunction.log("bind", "before run:" + e + " and flag:" + flag);
                SpaceModelReqTypes reqType = SpaceModel.getInstance(mActivity).getReqType(
                        flag);
                if (reqType != null) {
                    if (reqType.equals(SpaceModelReqTypes.GET_MSG_CODE)) {
                        mBtnResendMsg.setEnabled(true);
                    }
                }
            }
        });

    }

    @Override
    public void onGeneralSuccess(final String result, final long flag) {
        super.onGeneralSuccess(result, flag);
        if (flag == flag_check_code) {
            showWaitDialog(false);
            try {
                JSONObject json = new JSONObject(result);
                if (json != null) {
                    if (json.optInt("status") == 200) {

                        net.iaround.ui.datamodel.User user = Common.getInstance().loginUser;
                        user.setBindphone(mTelphoneNum);
                        user.setPhone(mTelphoneNum);
                        finish();
                    } else if (json.optInt("status") == -100) {
                        ErrorCode.showError(mContext, result);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (flag == flagGetMsgCode) {
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (bean.isSuccess()) {
                // 短信发送成功，记录号码和当前时间
                toastMsg(getString(R.string.msg_code_have_send) + mAreaCode + " " + mTelphoneNum);
                Common.telphoneMsgMap.put(mTelphoneNum, SystemClock.elapsedRealtime());

                if (mTimer != null) {
                    mTimer = new Timer();
                    mTimer.scheduleAtFixedRate(new MyTimerTask(), 1, 1 * 1000);
                }
            } else if (!bean.isSuccess()) {
                if (bean.error == 8200 || bean.error == 8201) {
                    handler.sendEmptyMessage(MSG_SMS_CODE_ERR);
                }

            }

        }
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                showWaitDialog(false);
                CommonFunction.log("bind", "get result:" + result);
                HashMap<String, Object> response = null;
                SpaceModelReqTypes reqType = SpaceModel.getInstance(mActivity).getReqType(
                        flag);
                try {
                    response = SpaceModelNew.getInstance(mActivity).getRes(result, flag);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    handleFail("");
                    if (reqType.equals(SpaceModelReqTypes.GET_MSG_CODE)) {
                        mBtnResendMsg.setEnabled(true);
                    }
                    return;
                }
                int resultCode = (Integer) response.get("status");
                if (resultCode == 200) {
                    if (reqType == SpaceModelReqTypes.BIND_USER_TELPHONE) {
                        // 手机号绑定/修改成功
                        toastMsg(getString(R.string.bind_tel_success));
                        Common.getInstance().finishAllBindActivity();
                        // 绑定成功之后，修改本地账号的状态
                        if (type == 0) {
                            Common.getInstance().loginUser.setHasPwd(1);
                        }
                        Common.getInstance().loginUser.setPhone(mAreaCode + mTelphoneNum);

                        SharedPreferenceUtil.getInstance(mContext).putString(
                                SharedPreferenceUtil.LOGIN_ACCOUNT,
                                mAreaCode + mTelphoneNum);
                        // Intent intent = new Intent( getActivity( ) ,
                        // AccountManagement.class );
                        // startActivity( intent );
                        finish();
                    } else if (reqType == SpaceModelReqTypes.GET_USER_PASSWORD) {
                        // 找回密码成功
                        toastMsg(R.string.find_pwd_success);
                        Common.getInstance().finishAllBindActivity();
                        finish();
                    } else if (reqType == SpaceModelReqTypes.GET_MSG_CODE) {
                        // 短信发送成功，记录号码和当前时间
                        toastMsg(getString(R.string.msg_code_have_send) + mAreaCode + " "
                                + mTelphoneNum);
                        Common.telphoneMsgMap.put(mTelphoneNum,
                                SystemClock.elapsedRealtime());

                        if (mTimer != null) {
                            mTimer = new Timer();
                            mTimer.scheduleAtFixedRate(new MyTimerTask(), 1, 1 * 1000);
                        }
                    }
                } else {
                    // 各种异常码的处理
                    if (reqType == SpaceModelReqTypes.GET_MSG_CODE) {
                        mBtnResendMsg.setEnabled(true);

                        mBtnResendMsg.setText(getString(R.string.register_resend_code));
                        mBtnResendMsg.setEnabled(true);
                        mBtnResendMsg.setTextColor(Color.WHITE);
                        mTimer.cancel();
                        int error = -1;
                        try {
                            error = Integer.valueOf(response.get("error").toString());
                        } catch (Exception e) {
                            error = 1000;
                        }
                        ErrorCode.toastError(mContext, error);
                    } else if (resultCode == -100
                            && (reqType == SpaceModelReqTypes.BIND_USER_TELPHONE || reqType == SpaceModelReqTypes.GET_USER_PASSWORD)) {
                        ErrorCode.toastError(mContext,
                                Integer.valueOf(response.get("error").toString()));
                    }
                }
            }
        });
    }

    /**
     * @param errMsg
     * @Title: handleFail
     * @Description: 处理失败，弹出提示
     */
    private void handleFail(String errMsg) {
        if (!CommonFunction.isEmptyOrNullStr(errMsg)) {
            toastMsg(errMsg);
        } else {
            toastMsg(R.string.network_req_failed);
        }
    }

    TextWatcher twEdtMsgCode = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEdtMsgCode.getText().length() > 0) {

                if (mEdtPwd.getText().length() > 0 || mSetPwdLayout.getVisibility() != View.VISIBLE) {

                    mBtnBind.setEnabled(true);
                }
            } else {
                mBtnBind.setEnabled(false);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void afterTextChanged(Editable s) {

        }
    };


    TextWatcher tw = new TextWatcher() {
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mEdtPwd.getText().length() > 0) {
                findViewById(R.id.delete_password_layout).setVisibility(View.VISIBLE);
                if (mEdtMsgCode.getText().length() > 0) {
                    findViewById(R.id.btn_start_bind).setEnabled(true);

                }
            } else {
                findViewById(R.id.delete_password_layout).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_start_bind).setEnabled(false);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * @param msg Toast内容
     * @Title: toastMsg
     * @Description: Toast一个消息
     */
    private void toastMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @param r_msg Toast内容对应的string
     * @Title: toastMsg
     * @Description: Toast一个消息
     */
    private void toastMsg(int r_msg) {
        Toast.makeText(mContext, r_msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * @Title: showWaitDialog
     * @Description: 显示加载框
     */
    private void showWaitDialog(boolean isShow) {
        CommonFunction.log("waitDialog", "show wait dialog:" + isShow);
        if (isShow) {
            waitDialog = DialogUtil.getProgressDialog(mContext, "",
                    getString(R.string.please_wait), new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                        }
                    });
            waitDialog.show();
        } else {
            if (waitDialog != null) {
                waitDialog.dismiss();
            }
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_REGISTER: {
                    if (msg.arg1 == 0) {
                        ErrorCode.toastError(BindTelphoneActivity.this,
                                Integer.valueOf(String.valueOf(msg.obj)));
                    } else {
                        doJumpMainActivity();
                    }

                }
                break;
                case MSG_SMS_CODE_ERR:

                    break;

                default:
                    break;
            }
        }

    };

    protected void doJumpMainActivity() {
        if (ssoLoginFlag) {
            finish();
        } else if (chatLoginFlag) {

        } else if (spaceLoginFlag) {

        } else {
            String icon = Common.getInstance().loginUser.getIcon();
            if (icon != null && icon.length() > 0) {
                getIconBitmap(icon);
            }
        }
    }

    // 获取用户的头像
    private void getIconBitmap(final String url) {
        final int dp_6 = CommonFunction.dipToPx(this, 6);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Bitmap bm = ConnectorManage.getInstance(getApplicationContext()).getBitmap(url, CommonFunction.getSDPath() + "/cacheimage_new/", "png", dp_6, true);
                    Common.getInstance().loginUser.setIconBitmap(bm);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showWaitDialog(false);
    }
}
