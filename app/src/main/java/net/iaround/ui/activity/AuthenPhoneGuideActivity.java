package net.iaround.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModelNew;
import net.iaround.model.entity.UserProfileBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.space.more.EnterPwdActivity;
import net.iaround.ui.space.more.EnterTelActivity;

import java.util.HashMap;

public class AuthenPhoneGuideActivity extends TitleActivity implements HttpCallBack {

    private TextView tvTitle;
    private TextView tvPhoneNum;
    private Button btnPhoneAuthenNext;
    private TextView tvPhoneAuthenStatus;
    private LinearLayout llPhoneAuthenNum;
    private TextView tvPhoneAuthenTips;

    private int phoneAuthenStatus;
    private String mTelPhoneNum;
    private net.iaround.model.im.Me me;
    private String phoneNum;
    /**
     * 获取用户信息的flag
     */
    private long FLAG_GET_USER_PRIVATE_DATA = 0;


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
        }, null, true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_authen_phone_guide);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvPhoneAuthenStatus = findView(R.id.tv_phone_authen_status);
        llPhoneAuthenNum = findView(R.id.ll_phone_authen_num);
        tvPhoneAuthenTips = findView(R.id.tv_phone_authen_tips);
        tvPhoneNum = findView(R.id.tv_phone_num);
        btnPhoneAuthenNext = findView(R.id.btn_phone_authen_next);

    }

    private void initDatas() {
        privateDataReq();
        phoneAuthenStatus = getIntent().getIntExtra(Constants.PHONE_AUTHEN_STATUS, 0);
        mTelPhoneNum = getIntent().getStringExtra(Constants.PHONE_NUM);
        if (mTelPhoneNum != null || phoneAuthenStatus == Constants.PHONE_AUTHEN_SUC)
        {
            tvTitle.setText(getResString(R.string.authen_phone_change_phoennum));
        }else
        {
            tvTitle.setText(getResString(R.string.authen_phone_title));
        }
        switch (phoneAuthenStatus) {
            case Constants.PHONE_AUTHEN_SUC://已绑定手机号
                tvPhoneAuthenStatus.setVisibility(View.VISIBLE);
                llPhoneAuthenNum.setVisibility(View.VISIBLE);
                tvPhoneAuthenTips.setText(getString(R.string.authen_phone_suc_tips));
                btnPhoneAuthenNext.setText(getString(R.string.authen_phone_change_phoennum));
                break;
            case Constants.PHONE_AUTHEN_NO://未绑定手机号
                tvPhoneAuthenStatus.setVisibility(View.GONE);
                llPhoneAuthenNum.setVisibility(View.GONE);
                tvPhoneAuthenTips.setText(getString(R.string.authen_phone_no_tips));
                btnPhoneAuthenNext.setText(getString(R.string.authen_phone_bind_phoennum));
                break;
        }

        if (me != null) {
            phoneNum = me.getPhone();
        }

        if (!TextUtils.isEmpty(phoneNum)) {
            tvPhoneNum.setText(phoneNum);
        }
    }

    private void initListeners() {
        btnPhoneAuthenNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                switch (phoneAuthenStatus) {
                    case Constants.PHONE_AUTHEN_SUC://已绑定手机号
                        if (Common.getInstance().loginUser.getHasPwd() == 1) {
                            // 有密码
                            intent.setClass(AuthenPhoneGuideActivity.this, EnterPwdActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS,Constants.PHONE_AUTHEN_SUC);
                            intent.putExtra("type", 1);// 需输入已有密码
                            mContext.startActivity(intent);
                        } else {
                            intent.setClass(AuthenPhoneGuideActivity.this, EnterTelActivity.class);
                            intent.putExtra("type", 0);// 需设置新密码
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS,Constants.PHONE_AUTHEN_SUC);
                            mContext.startActivity(intent);
                        }
                        break;
                    case Constants.PHONE_AUTHEN_NO://未绑定手机号
                        if (Common.getInstance().loginUser.getHasPwd() == 1) {
                            intent.setClass(AuthenPhoneGuideActivity.this, EnterPwdActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS,Constants.PHONE_AUTHEN_NO);
                            intent.putExtra("type", 1);// 需输入已有密码
                            mContext.startActivity(intent);
                        } else {
                            // 无密码
                            intent.setClass(AuthenPhoneGuideActivity.this, EnterTelActivity.class);
                            intent.putExtra(Constants.PHONE_AUTHEN_STATUS,Constants.PHONE_AUTHEN_NO);
                            intent.putExtra("type", 0);// 需设置新密码
                            mContext.startActivity(intent);
                        }
                        break;

                }

            }
        });
    }

    /**
     * 个人信息
     */
    public void privateDataReq() {

        try {
            FLAG_GET_USER_PRIVATE_DATA = SpaceModelNew.getInstance(this)
                    .privateDataReq(this, this);
            if (FLAG_GET_USER_PRIVATE_DATA == -1) {
                Toast.makeText(this, R.string.network_req_failed, Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (Throwable e) {
            CommonFunction.log(e);
            Toast.makeText(AuthenPhoneGuideActivity.this, R.string.operate_fail, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if ((flag == FLAG_GET_USER_PRIVATE_DATA)) {
            UserProfileBean bean = GsonUtil.getInstance().getServerBean(
                    result, UserProfileBean.class);

            if (bean.isSuccess()) {

            } else {
                ErrorCode.showError(AuthenPhoneGuideActivity.this, result);
            }

            SpaceModelNew netwrokInterface = SpaceModelNew.getInstance(AuthenPhoneGuideActivity.this);
            HashMap<String, Object> res = null;
            try {
                res = netwrokInterface.getRes(result, flag);
            } catch (Throwable e) {
                CommonFunction.log(e);
            }
            if (res != null) {
                SpaceModel.SpaceModelReqTypes reqType = (SpaceModel.SpaceModelReqTypes) res
                        .get("reqType");
                if (reqType != null) {
                    switch (reqType) {
                        case PRIVATE_DATA: {
                            int status = (Integer) res.get("status");
                            if (status == 200) {

//                                me.setRealName((String) res.get("realname"));
//                                me.setEmail((String) res.get("email"));
//                                me.setIsauth((String) res.get("isauth"));
//                                me.setPhone((String) res.get("phone"));
//                                me.setRealAddress((String) res.get("address"));
//                                me.setHasPwd((Integer) res.get("haspwd"));
//                                me.setCanChangePhone((Integer) res
//                                        .get("canchgphone"));
                                tvPhoneNum.setText((String) res.get("phone"));

//                                SharedPreferenceUtil.getInstance(AuthenPhoneGuideActivity.this).putString(Constants.PHONE_NUM, me.getPhone());
//                                if (!CommonFunction.isEmptyOrNullStr(me.getPhone())) {
//                                    me.setBindPhone(true);
//                                }

                            }
                        }
                        break;
                    }
                }
            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {

    }
}
