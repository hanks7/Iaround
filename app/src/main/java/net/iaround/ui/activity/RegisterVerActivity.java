package net.iaround.ui.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.contract.RegisterContract;
import net.iaround.tools.VerCodeTimerTask;


/**
 * Class: 注册验证手机号
 * Author：gh
 * Date: 2016/12/2 16:16
 * Emial：jt_gaohang@163.com
 */
public class RegisterVerActivity extends TitleActivity implements RegisterContract.View ,View.OnClickListener ,HttpCallBack{

    public static final String AVATOR_PATH = "avator_path";
    public static final String COUNTRY_ARE = "country_are";
    public static final String NUMBER = "number";
    public static final String PASSWORD = "password";
    public static final String NICKNAME = "nickname";
    public static final String BIRTHDAY = "birthday";
    public static final String SEX = "sex";

//    private RegisterPresenter registerPresenter;

    //标题栏
    private TextView tvTitle;

    private TextView tvNumber;
    private EditText editVer;
    private TextView btnSend;
    private TextView btnTo;

    private String countryAre;
    private String number;
    private String password;
    private String name;
    private String birthday;
    private String icon;
    private int sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_ver);

        initView();
        setMonitor();

//        registerPresenter = new RegisterPresenter();
//        registerPresenter.registerConext(this);
//        registerPresenter.requestMsgCode(RegisterVerActivity.this,number,httpStringCallback);
        VerCodeTimerTask.setTimer(RegisterVerActivity.this,btnSend);
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(R.string.ver_title);
        tvNumber = (TextView) findViewById(R.id.tv_register_ver_number);
        editVer = (EditText) findViewById(R.id.edit_register_ver);
        btnSend = (TextView) findViewById(R.id.btn_register_send);
        btnTo = (TextView) findViewById(R.id.btn_register_to_iaround);

        countryAre = getIntent().getStringExtra(COUNTRY_ARE);
        number = getIntent().getStringExtra(NUMBER);
        password = getIntent().getStringExtra(PASSWORD);
        name = getIntent().getStringExtra(RegisterActivity.KEY_NAME);
        birthday = getIntent().getStringExtra(RegisterActivity.KEY_BIRTHDAY);
        icon = getIntent().getStringExtra(RegisterActivity.KEY_HEAD);
        sex = getIntent().getIntExtra(RegisterActivity.KEY_SEX,-1);
        tvNumber.setText(countryAre + number);

    }

    @Override
    public void setMonitor() {
        btnSend.setOnClickListener(this);
        btnTo.setOnClickListener(this);

        editVer.addTextChangedListener(textWatcher);

        findViewById(R.id.iv_left).setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_register_send:
//                registerPresenter.requestMsgCode(RegisterVerActivity.this,number,httpStringCallback);
                VerCodeTimerTask.setTimer(RegisterVerActivity.this,btnSend);
                break;
            case R.id.btn_register_to_iaround:
                String verCode = editVer.getText().toString();
                if ( verCode == null || verCode.length( ) <= 0 )
                { // 验证码为空
                    Toast.makeText( this , R.string.register_ver_please_code , Toast.LENGTH_SHORT )
                            .show( );
                    return;
                }
                btnTo.setEnabled(false);
                showWaitDialog();
//                SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN,"");
//                long bith = TimeFormat.convertTimeString2Long(birthday, Calendar.DATE);
//                registerPresenter.requestNext(6,number,"",password,name,sex,verCode,""+bith,icon,httpStringCallback);

                break;
        }
    }

//    private HttpStringCallback httpStringCallback = new HttpStringCallback() {
//        @Override
//        public void onGeneralSuccess(String result, int id) {
//            if (id == 102) {
//                BaseBean<RegisterNextBean> bean = BaseBean.fromJson(result, RegisterNextBean.class);
//                if (bean.isSuccess()) {
//                    SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, bean.getData().authtoken);
//                    LoginHttpProtocol.doLogin(RegisterVerActivity.this, number, password, bean.getData().authtoken, 6, RegisterVerActivity.this);
//                } else {
//                    hideWaitDialog();
//                    btnTo.setEnabled(true);
//                    ErrorCode.showError(RegisterVerActivity.this,result);
//                }
//            } else if (id == 99) {
//                LoginResponseBean entity = GsonUtil.getInstance().getServerBean(
//                        result, LoginResponseBean.class);
//                if (entity.isSuccess()) {
////                    CommonFunction.saveDataLive(entity.getData().key,entity.getData().live);
////                    SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LIVE_USER,entity.getData().live);
//
//                    SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN,entity.key);
//                    registerPresenter.userProfile(RegisterVerActivity.this,RegisterVerActivity.this);
//                }else{
//                    ErrorCode.showError(RegisterVerActivity.this,result);
//                }
//            }else if (id == 109){
//                hideWaitDialog();
//                btnTo.setEnabled(true);
//                SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.USER_PROFILE,result);
//                BaseBean<UserProfileBean> bean = BaseBean.fromJson(Common.getInstance().getUserProfile(),UserProfileBean.class);
//                if (bean.isSuccess()){
//                    SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.USER_ID,""+bean.getData().uid);
//                    SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.USER_AVATAR,bean.getData().headPic);
//                    Intent intent = new Intent(RegisterVerActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }else if (id == 107){
//                BaseBean baseBean = BaseBean.fromJson(result,BaseBean.class);
//                if (baseBean.isSuccess()){
//                    CommonFunction.toastMsg(RegisterVerActivity.this, getString( R.string.msg_code_have_send ) + countryAre == null ? "" : countryAre
//                            + " " + number );
//                }else{
//                    ErrorCode.showError(RegisterVerActivity.this,result);
//                }
//            }else{
//                try{
//                    JSONObject object = new JSONObject(result);
//                    String signture = object.getString("sig");
//                    SharedPreferenceUtil.getInstance(RegisterVerActivity.this).putString(SharedPreferenceUtil.CHAT_SIGNTURE,signture);
////                    registerPresenter.chatInit(RegisterVerActivity.this,MODE_PRIVATE);
////                    registerPresenter.chatLogin(RegisterVerActivity.this);
//
//                    Intent intent = new Intent(RegisterVerActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//
//            }
//        }
//
//        @Override
//        public void onGeneralError(String error, Exception e, int id) {
//            if (id == 102){
//                hideWaitDialog();
//                btnTo.setEnabled(true);
//            }
//        }
//    };

    private HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {

        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };

    // EditText监听
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            String ver = editVer.getText().toString();
            if (ver.length() > 0){
                btnTo.setEnabled(true);
            }else {
                btnTo.setEnabled(false);
            }
        }
    };

    @Override
    public void onGeneralSuccess(String result, long flag) {

    }

    @Override
    public void onGeneralError(int e, long flag) {

    }
}
