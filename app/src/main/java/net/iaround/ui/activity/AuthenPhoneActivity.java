package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.contract.AuthenPhoneContract;
import net.iaround.presenter.AuthenPhonePresenter;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;

public class AuthenPhoneActivity extends TitleActivity implements View.OnClickListener, AuthenPhoneContract.View {

    private AuthenPhonePresenter phonePresenter;

    private static final int SELECT_COUNRTY_REQ = 99;

    private EditText etPhone;
    private Button btnNext;

    private static final int INPUT_PHONE_NUM = 1;
    private static final int INPUT_VERIFY_CODE = 2;
    private int authenStatus = INPUT_PHONE_NUM;
    private LinearLayout llInpuPhone;
    private LinearLayout llInpuVerifycode;
    private ImageView ivLeft;
    private TextView tvTitle;
    private FrameLayout flLeft;

    private TextView tvPhone;

    private int verifyTime = 60;
    private static final int TIMER = 3;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == TIMER){
                if(verifyTime > 0){
                    verifyTime--;
                    tvSendVerifycode.setText(getString(R.string.authen_phone_verifycode_send_again)+"("+ verifyTime +")");
                    handler.sendEmptyMessageDelayed(TIMER, 1000);
                } else {
                    tvSendVerifycode.setClickable(true);
                }
            }
        }
    };
    private TextView tvSendVerifycode;
    private LinearLayout llSelectCountry;
    private TextView tvArea;
    private EditText etPassword;
    private Button btnComplete;
    private EditText etVerifycode;
    private Boolean havePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phonePresenter = new AuthenPhonePresenter(this);
        initVies();
        initDatas();
        initListeners();
    }

    private void initVies() {
        setTitle_LCR(false, R.drawable.title_back, null, null, getString(R.string.authen_phone_title), true, 0, null, null);
        setContent(R.layout.activity_authen_phone);

        ivLeft = findView(R.id.iv_left);
        tvTitle = findView(R.id.tv_title);
        flLeft = findView(R.id.fl_left);

        llInpuPhone = findView(R.id.ll_input_phone);
        llSelectCountry = findView(R.id.ll_select_country);
        llInpuVerifycode = findView(R.id.ll_input_verifycode);
        tvArea = findView(R.id.tv_area);
        etPhone = findView(R.id.et_phone);
        btnNext = findView(R.id.btn_next);
        tvPhone = findView(R.id.tv_phone);
        tvSendVerifycode = findView(R.id.tv_send_verifycode);
        etVerifycode = findView(R.id.et_verifycode);
        etPassword = findView(R.id.et_password);
        btnComplete = findView(R.id.btn_complete);
        changeUI();
    }

    private void initDatas() {
        havePassword = getIntent().getBooleanExtra(Constants.HAVE_PASSWORD,false);
        if(!havePassword){
            etPassword.setVisibility(View.VISIBLE);
        } else {
            etPassword.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        llSelectCountry.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        tvSendVerifycode.setOnClickListener(this);
        btnComplete.setOnClickListener(this);
    }

    public void changeUI(){
        if(authenStatus == INPUT_PHONE_NUM){
            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            flLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            tvTitle.setText(getString(R.string.authen_phone_title));
            llInpuPhone.setVisibility(View.VISIBLE);
            llInpuVerifycode.setVisibility(View.GONE);
        } else if(authenStatus == INPUT_VERIFY_CODE){
            flLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authenStatus = INPUT_PHONE_NUM;
                    changeUI();
                    stopClock();
                }
            });
            ivLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    authenStatus = INPUT_PHONE_NUM;
                    changeUI();
                    stopClock();
                }
            });
            tvTitle.setText(getString(R.string.authen_phone_verifycode_title));
            llInpuPhone.setVisibility(View.GONE);
            llInpuVerifycode.setVisibility(View.VISIBLE);
            tvPhone.setText(etPhone.getText().toString());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.ll_select_country:
                intent.setClass(this, CountrySelectActivity.class);
                startActivityForResult(intent, SELECT_COUNRTY_REQ);
                break;
            case R.id.btn_next:
                String phoneNum = etPhone.getText().toString();
                if(TextUtils.isEmpty(phoneNum)){
                    Toast.makeText(getActivity(), getString(R.string.authen_phone_check_null), Toast.LENGTH_SHORT).show();
                } else if(!phoneNum.startsWith("1")){
                    Toast.makeText(getActivity(), getString(R.string.authen_phone_check_just), Toast.LENGTH_SHORT).show();
                } else {
                    phonePresenter.getVerifyCode(phoneNum);
                }
                break;
            case R.id.tv_send_verifycode:
                phonePresenter.getVerifyCode(etPhone.getText().toString());
                startClock();
                break;
            case R.id.btn_complete:
                String verifyCode = etVerifycode.getText().toString();
                String password = etPassword.getText().toString();
                if(TextUtils.isEmpty(verifyCode)){
                    Toast.makeText(getActivity(), getString(R.string.authen_verify_check_null), Toast.LENGTH_SHORT).show();
                } else if((!havePassword) && TextUtils.isEmpty(password)){
                    Toast.makeText(getActivity(), getString(R.string.authen_pwd_check_null), Toast.LENGTH_SHORT).show();
                } else {
                    phonePresenter.bind(verifyCode, tvPhone.getText().toString(),password);
                }
                break;
        }
    }

    public void startClock(){
        verifyTime = 60;
        tvSendVerifycode.setClickable(false);
        handler.sendEmptyMessage(TIMER);
    }

    public void stopClock(){
        verifyTime = 0;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 1){
            switch (requestCode){
                case SELECT_COUNRTY_REQ:
                    String are = data.getStringExtra( "area" );
                    if ( !CommonFunction.isEmptyOrNullStr( are ) && are.contains( "+" ) )
                    {
                        String area = data.getStringExtra( "area" );
                        if ( !CommonFunction.isEmptyOrNullStr( area ) && area.contains( "+" ) )
                        {
                            // 清空原来输入的手机号
                            etPhone.setText( "" );

                            CommonFunction.log( "country" , "get country result : " + area );
                            String[ ] spitString = area.split( "\\+" );
                            String areaCode = "(+" + spitString[ 1 ] + ")";
                            String countryName = spitString[ 0 ];
                            tvArea.setText( countryName + areaCode.trim( ) );
                            if ( spitString[ 1 ].equals( "86" ) ) {
                                // 大陆手机限制11位输入
                                etPhone.setFilters( new InputFilter[]{ new InputFilter.LengthFilter( 11 )} );
                            }
                            else {
                                etPhone.setFilters( new InputFilter[]{ new InputFilter.LengthFilter( 50 ) } );
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void change2Verify() {
        authenStatus = INPUT_VERIFY_CODE;
        changeUI();
        startClock();
    }

    @Override
    public void change2Phone() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void bindSuccess() {
        String phoneNum = tvPhone.getText().toString();
        SharedPreferenceUtil.getInstance(this).putString(Constants.PHONE_NUM, phoneNum);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
