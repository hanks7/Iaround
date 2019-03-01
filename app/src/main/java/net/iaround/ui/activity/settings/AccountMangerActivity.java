package net.iaround.ui.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectLogin;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.entity.UserProfileBean;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.AuthenPhoneActivity;
import net.iaround.ui.activity.AuthenticationActivity;
import net.iaround.ui.activity.RegisterNewActivity;
import net.iaround.ui.activity.TitleActivity;
import net.iaround.ui.activity.VerifyPasswordActivity;
import net.iaround.ui.space.more.EnterPwdActivity;
import net.iaround.ui.space.more.EnterTelActivity;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;

import java.util.HashMap;

/**
 * 账号与安全界面
 */
public class AccountMangerActivity extends TitleActivity implements View.OnClickListener, HttpCallBack {

    private LinearLayout llBindPhone;
    //    private LinearLayout llBindEmail;
    private LinearLayout llAuthentication;
    private LinearLayout llChangePwd;
    private TextView tvAuthenticationStatus;//是否已认证
    private TextView tvRegistrationWay;//注册方式
    private TextView tvAccountNum;
    private TextView tvLogout;
    private TextView tvPhoneNum;

    private int phoneAuthenState;
    private static final int REQ_PHONE_AUTHEN_SUC = 101;
    private static final int REQ_PHONE_AUTHEN_NO = 102;
    private static final int REQ_AUTHEN_VIVIFY_PWD = 103;
    private static final int REQ_PHONE_AUTHEN = 104;

    /**
     * 获取用户信息的flag
     */
    private long FLAG_GET_USER_PRIVATE_DATA = 0;

    /**
     * 用户个人信息
     */
    private Me me;
    /**
     * 数据是否处理成功
     */
    private boolean isHandleDataSuccess = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();

    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.setting_accont_manager), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_account_manger);
        llBindPhone = findView(R.id.ll_bind_phone);
        llAuthentication = findView(R.id.ll_authentication);
        tvAuthenticationStatus = findView(R.id.tv_authentication_status);
        tvRegistrationWay = findView(R.id.tv_registration_way);
        tvAccountNum = findView(R.id.tv_account_num);
        tvPhoneNum = findView(R.id.tv_phone_num);
//        llBindEmail = findView(R.id.ll_bind_email);
        llChangePwd = findView(R.id.ll_change_pwd);
        tvLogout = findView(R.id.tv_logout);
    }

    private void initDatas() {
        me = Common.getInstance().loginUser;
        String phoneNum = SharedPreferenceUtil.getInstance(this).getString(Constants.PHONE_NUM);
        setTvPhoneNum(phoneNum);
        phoneAuthenState = TextUtils.isEmpty(phoneNum) ? Constants.PHONE_AUTHEN_NO : Constants.PHONE_AUTHEN_SUC;

        tvAccountNum.setText(SharedPreferenceUtil.getInstance(this).getString(SharedPreferenceUtil.USER_ID));
        if (me.getVerifyPersion() == 1) {

            tvAuthenticationStatus.setText(R.string.authen_status_suc);
        } else {
            tvAuthenticationStatus.setText(R.string.authen_status_no);

        }

        /*
         * 根据下发的haspwd标志来显示修改密码
		 */
        if (Common.getInstance().loginUser.getHasPwd() == 1) {
            findViewById(R.id.line_change_pwd).setVisibility(View.VISIBLE);
            findViewById(R.id.ll_change_pwd).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.line_change_pwd).setVisibility(View.GONE);
            findViewById(R.id.ll_change_pwd).setVisibility(View.GONE);
        }

        privateDataReq();
    }

    private void initListeners() {
        llBindPhone.setOnClickListener(this);
        llChangePwd.setOnClickListener(this);
        llAuthentication.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.ll_bind_phone://手机绑定
                if (isHandleDataSuccess) {
                    if (CommonFunction.isEmptyOrNullStr(Common.getInstance().loginUser.getPhone())) {
                        // 未绑定
//                        if (me.getHasPwd() == 1) {
//                            // 有密码
//                            intent = new Intent(mContext, BindTelphoneForOne.class);
//                            intent.putExtra("type", 1);// 需输入密码
//                            mContext.startActivity(intent);
//                        } else {
//                            // 无密码
//                            intent = new Intent(mContext, BindTelphoneForOne.class);
//                            intent.putExtra("type", 0);// 需设置密码
//                            mContext.startActivity(intent);
//                        }
                        if (Common.getInstance().loginUser.getHasPwd() == 1) {
                            intent.setClass(AccountMangerActivity.this, EnterPwdActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_NO);
                            intent.putExtra("type", 1);// 需输入已有密码
                            mContext.startActivity(intent);
                        } else {
                            // 无密码
                            intent.setClass(AccountMangerActivity.this, EnterTelActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_NO);
                            intent.putExtra("type", 0);// 需设置新密码
                            mContext.startActivity(intent);
                        }
                    } else {
                        // 已绑定，修改
//                        if (Common.getInstance().loginUser.isCanChangeTelphone == 1) {
//                            String phone = Common.getInstance().loginUser.getPhone();
//                            // 允许更换手机号
//                            intent = new Intent(mContext, BindTelphoneForTwo.class);
//                            intent.putExtra("type", 2);// （已绑定）更换手机号
//                            intent.putExtra("phone", phone);
//                            mContext.startActivity(intent);
//                            // finish( );
//                        } else {
//                            // 不允许更换手机号
//                            DialogUtil.showOKDialog(mContext, getString(R.string.can_not_change_tel_title), getString(R.string.can_not_change_telphone), null);
//                        }
                        if (Common.getInstance().loginUser.getHasPwd() == 1) {
                            // 有密码
                            intent.setClass(AccountMangerActivity.this, EnterPwdActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_SUC);
                            intent.putExtra("type", 1);// 需输入已有密码
                            mContext.startActivity(intent);
                        } else {
                            intent.setClass(AccountMangerActivity.this, EnterTelActivity.class);
                            intent.putExtra("type", 0);// 需设置新密码
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS, Constants.PHONE_AUTHEN_SUC);
                            mContext.startActivity(intent);
                        }
                    }
                }

                break;
            case R.id.ll_change_pwd://修改密码
                intent.setClass(this, ChangePwdActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_logout://退出登录
                CharSequence charSequenceTitle = getResources().getString(R.string.dialog_title);
                CharSequence charSequenceMessage = getResString(R.string.logout_message);
                CharSequence charSequenceButton1 = getResString(R.string.cancel);
                CharSequence charSequenceButton2 = getResString(R.string.ok);
                DialogUtil.showTowButtonDialog(this, charSequenceTitle, charSequenceMessage, charSequenceButton1, charSequenceButton2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //关闭最小化悬浮窗
                        ChatBarZoomWindow.getInstance().close();
                        //用户注销逻辑
                        ConnectLogin.getInstance(AccountMangerActivity.this).logoutCacel(AccountMangerActivity.this);
                        //停止定位
                        LocationUtil.stop(AccountMangerActivity.this);
                        //跳转去登陆界面
                        startActivity(new Intent(AccountMangerActivity.this, RegisterNewActivity.class));
                        finish();
                    }
                });
                break;
            case R.id.ll_authentication:
                Intent in = new Intent(this, AuthenticationActivity.class);
                startActivity(in);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent();
            switch (requestCode) {
                case REQ_PHONE_AUTHEN_SUC:
                    intent.setClass(this, VerifyPasswordActivity.class);
                    startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    break;
                case REQ_PHONE_AUTHEN_NO:
                    if (CommonFunction.getLoginType(this) != Constants.ACCOUNT_LOGIN) {
                        intent.setClass(this, AuthenPhoneActivity.class);
                        intent.putExtra(Constants.HAVE_PASSWORD, false);
                        startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    } else {//邮箱登录
                        intent.setClass(this, VerifyPasswordActivity.class);
                        startActivityForResult(intent, REQ_AUTHEN_VIVIFY_PWD);
                    }
                    break;
                case REQ_AUTHEN_VIVIFY_PWD:
                    intent.setClass(this, AuthenPhoneActivity.class);
                    intent.putExtra(Constants.HAVE_PASSWORD, true);
                    startActivityForResult(intent, REQ_PHONE_AUTHEN);
                    break;
                case REQ_PHONE_AUTHEN:
                    //手机绑定成功后会更新sp里面的值，在这里可以直接取sp里面的手机号码即可
                    setTvPhoneNum(SharedPreferenceUtil.getInstance(this).getString(Constants.PHONE_NUM));
                    phoneAuthenState = Constants.PHONE_AUTHEN_SUC;
                    break;
            }
        }
    }

    public void setTvPhoneNum(String phoneNum) {
        tvPhoneNum.setText(phoneNum);
    }

    /**
     * 个人信息
     */
    public void privateDataReq() {
        try {
            FLAG_GET_USER_PRIVATE_DATA = SpaceModelNew.getInstance(this).privateDataReq(this, this);
            if (FLAG_GET_USER_PRIVATE_DATA == -1) {
                Toast.makeText(this, R.string.network_req_failed, Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            CommonFunction.log(e);
            Toast.makeText(AccountMangerActivity.this, R.string.operate_fail, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGeneralSuccess(final String result, final long flag) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (flag == FLAG_GET_USER_PRIVATE_DATA) {
                    UserProfileBean bean = GsonUtil.getInstance().getServerBean(result, UserProfileBean.class);

                    if (bean.isSuccess()) {

                    } else {
                        ErrorCode.showError(AccountMangerActivity.this, result);
                    }

                    SpaceModelNew netwrokInterface = SpaceModelNew.getInstance(mContext);
                    HashMap<String, Object> res = null;
                    try {
                        res = netwrokInterface.getRes(result, flag);
                    } catch (Throwable e) {
                        CommonFunction.log(e);
                    }
                    if (res != null) {
                        SpaceModelReqTypes reqType = (SpaceModelReqTypes) res.get("reqType");
                        if (reqType != null) {
                            switch (reqType) {
                                case PRIVATE_DATA: {
                                    int status = (Integer) res.get("status");
                                    if (status == 200) {

                                        me.setRealName((String) res.get("realname"));
                                        me.setEmail((String) res.get("email"));
                                        me.setIsauth((String) res.get("isauth"));
                                        me.setPhone((String) res.get("phone"));
                                        me.setRealAddress((String) res.get("address"));
                                        me.setHasPwd((Integer) res.get("haspwd"));
                                        me.setCanChangePhone((Integer) res.get("canchgphone"));
                                        me.setRegType((String) res.get("regType"));


                                        SharedPreferenceUtil.getInstance(AccountMangerActivity.this).putString(Constants.PHONE_NUM, me.getPhone());
                                        if (!CommonFunction.isEmptyOrNullStr(me.getPhone())) {
                                            me.setBindPhone(true);
                                        }
                                        if (me.getHasPwd() == 1) {
                                            findViewById(R.id.line_change_pwd).setVisibility(View.VISIBLE);
                                            findViewById(R.id.ll_change_pwd).setVisibility(View.VISIBLE);
                                        } else {
                                            findViewById(R.id.line_change_pwd).setVisibility(View.GONE);
                                            findViewById(R.id.ll_change_pwd).setVisibility(View.GONE);
                                        }
                                        if (!TextUtils.isEmpty(me.getRegType())) {
                                            tvRegistrationWay.setText(me.getRegType());
                                        }
                                        isHandleDataSuccess = true;
                                        handlePrivateDataSuccess();
                                    } else if (res.containsKey("error")) {
                                        handlePrivateDataFail(Integer.valueOf(res.get("error").toString()));
                                    }
                                }
                                break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        handlePrivateDataFail(1);
                    }
                }
            }
        });
    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (flag == FLAG_GET_USER_PRIVATE_DATA) {
            handlePrivateDataFail(e);
        }
    }

    /**
     * @param errorCode
     * @Title: handlePrivateDataFail
     * @Description: 处理获取个人资料出错
     */

    private void handlePrivateDataFail(final int errorCode) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ErrorCode.toastError(mContext, errorCode);
                if (llBindPhone != null) {
                    llBindPhone.setEnabled(false);
                }
            }
        });
    }

    /**
     * @Title: handlePrivateDataSuccess
     * @Description: 获取到数据，初始化界面数据显示，若第三方帐号有绑定则显示
     */
    private void handlePrivateDataSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 获取到数据，界面数据显示
                initTelphone();
                if (llBindPhone != null) {
                    llBindPhone.setEnabled(true);
                }
            }
        });
    }

    /**
     * @Title: initTelphone
     * @Description: 初始化手机号设置项，控制显示修改密码
     */
    private void initTelphone() {

        if (CommonFunction.isEmptyOrNullStr(me.getPhone()) || "null".equals(me.getPhone())) {
            tvPhoneNum.setText(getResString(R.string.not_bind));
        } else {
            tvPhoneNum.setText(me.getPhone());
        }
    }

    /** 注销对话框 */
//    @SuppressWarnings ( "deprecation" )
//    private void showLogoutDialog( final Context context )
//    {
//        try {
//            AlertDialog dialog = new AlertDialog.Builder( context ).create( );
//            dialog.setTitle( R.string.dialog_title );
//            dialog.setMessage( context.getString( R.string.logout_message ) );
//            dialog.setButton( context.getString( R.string.cancel ) ,
//                    new DialogInterface.OnClickListener( ) {
//                        @Override
//                        public void onClick( DialogInterface dialog , int which )
//                        {
//                        }
//                    } );
//            dialog.setButton2( context.getString( R.string.ok ) ,
//                    new DialogInterface.OnClickListener( )
//                    {
//
//                        @Override
//                        public void onClick( DialogInterface dialog , int which )
//                        {
//                            LocationUtil.stop( context );
//                            ConnectLogin.getInstance( context ).logoutCacel( context );
//                            //YC 取消LoginMainActivity的使用
//                            startActivity(new Intent(AccountMangerActivity.this, RegisterNewActivity.class));
//                            finish();
//                        }
//                    } );
//
//            dialog.show( );
//        }
//        catch ( Throwable t )
//        {
//            t.printStackTrace( );
//        }
//    }

}
