package net.iaround.ui.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.UploadImageCallback;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.RegisterHttpProtocol;
import net.iaround.contract.RegisterContract;

import net.iaround.model.database.RegisterModel;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.UploadPicEntity;
import net.iaround.model.im.Me;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CustomDialog;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.Hashon;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Class: 注册
 * Author：gh
 * Date: 2016/11/30 21:22
 * Emial：jt_gaohang@163.com
 */
public class RegisterNextActivity extends ActionBarActivity implements RegisterContract.View, View.OnClickListener, HttpCallBack {

    //极验
    public static final String captchaURL = Config.loginUrl + "/v1/system/geeinit";
    public static final String validateURL = Config.loginUrl + "/v1/system/geevalidate";
    protected static final int MSG_WHAT_LOGIN = 1;
    private static final int MSG_PICK_IMAGE = 2;

    //点击间隔时间判断
    public static final int MIN_CLICK_DELAY_TIME = 3000;
    private long lastClickTime = 0;

    protected static final int SHOW_DIALOG = 10;
    protected static final int MSG_LOGIN_RES = 0x01ff;
    protected static final int MSG_GO_REGISTER = 0x02ff;

    private long GET_USER_INFO_FLAG;
    private long GET_USER_REGISTER;
    private long GET_USER_LGIN;
    private long bindFlag = 0;
    private long GET_GEETTEST_FLAG;


    //    private ImageView ivAvatar;
    private HeadPhotoView ivAvatar;
    private EditText editNicknam;
    private RadioGroup rgSex;
    private TextView tvBirthday;
    private TextView tvSexHint;
    private TextView btnNext;

    //标题栏
    private TextView tvTitle;
    private ImageView ivLeft;

    private LinearLayout btnFacebook;
    private LinearLayout btnQQ;
    private LinearLayout btnWechat;
    private LinearLayout btnSina;

    private LinearLayout loginThreadLayout;

    private String sex;
    private String iconUrl = "";
    private String birthday;
    private String opneId;
    private String unionid;
    private String token;
    private long expires;
    private int accounttype;

    private TimePickerView pvTime;

    private CustomDialog dialog;

    private boolean isCheck = false;// 用于判断“性别不能修改”提示是否已弹出，false为已弹出
    private boolean isCheckSex = true;//用于判断是否选择性别
    private int isShowSexDialogNum = 1;//用于记录显示性别选择对话框的次数

    private GT3GeetestUtilsBind gt3GeetestUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);
//        setActionBarTitle(R.string.register_title);
        accounttype = getIntent().getIntExtra("accounttype", 6);

        initView();
        setMonitor();

        if (accounttype != 6) {
            initBundle(getIntent().getExtras());
        }
        findViewById(R.id.ly_thread_login).setVisibility(View.INVISIBLE);
//        else {
//            findViewById(R.id.ly_thread_login).setVisibility(View.VISIBLE);
//        }
        dialog = new CustomDialog(RegisterNextActivity.this,
                getString(R.string.prompt), getResString(R.string.register_forgo),
                getString(R.string.cancel), null,
                getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        dialog.setCancelable(false);


        Log.d("validateURL", validateURL);
//        new GT3GeetestUrl().setCaptchaURL(captchaURL);
//        new GT3GeetestUrl().setValidateURL(validateURL);
        if(gt3GeetestUtils == null) {
            gt3GeetestUtils = new GT3GeetestUtilsBind(RegisterNextActivity.this);
        }
//        gt3GeetestUtils.gtDologo();//加载验证码之前判断有没有logo
//
//        gt3GeetestUtils.setGtListener(gt3Listener);

    }

    @Override
    public void initView() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivAvatar = (HeadPhotoView) findViewById(R.id.iv_register_next_avatar);
        editNicknam = (EditText) findViewById(R.id.edit_register_next_nickname);
        rgSex = (RadioGroup) findViewById(R.id.rg_register_next_sex);
        tvBirthday = (TextView) findViewById(R.id.tv_register_next_birthday);
        tvSexHint = (TextView) findViewById(R.id.tv_register_sex_hint);
        btnNext = (TextView) findViewById(R.id.btn_register_next);

        tvTitle.setText(R.string.register_title);
        ivLeft.setImageResource(R.drawable.title_back);
//        btnFacebook = (LinearLayout)findViewById(R.id.btn_login_facebook);
//        btnQQ = (LinearLayout)findViewById(R.id.btn_login_qq);
//        btnWechat = (LinearLayout)findViewById(R.id.btn_login_wechat);
//        btnSina = (LinearLayout)findViewById(R.id.btn_login_sina);

        loginThreadLayout = (LinearLayout) findViewById(R.id.ly_login_thread);

        if (loginThreadLayout != null) {
            loginThreadLayout.removeAllViews();
        }

        int[] loginFlags = Common.getInstance().getRegisterFlag();
        if (loginFlags != null) {
            if (loginFlags.length >= 0) {
                for (int i = 0; i < loginFlags.length; i++) {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.view_thread_ad, null);
                    TextView name = (TextView) view.findViewById(R.id.tv_thread_ad_name);
                    ImageView icon = (ImageView) view.findViewById(R.id.iv_thread_ad_icon);
                    switch (loginFlags[i]) {
                        case 1:
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
                        case 2:
                            name.setText(mContext.getString(R.string.login_sina));
                            icon.setImageResource(R.drawable.thread_weibo);
                            loginThreadLayout.addView(view);
                            view.setTag(R.layout.view_thread_ad, 2);
                            view.setOnClickListener(threadClick);
                            break;
                        case 4:
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
                        case 7:
                            name.setText(mContext.getString(R.string.login_wechat));
                            icon.setImageResource(R.drawable.thread_weixin);
                            loginThreadLayout.addView(view);
                            view.setTag(R.layout.view_thread_ad, 7);
                            view.setOnClickListener(threadClick);
                            break;


                    }
                }
            }

        }

        //时间选择器
        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        //控制时间范围
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);
        //时间选择后回调
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {
                birthday = "" + TimeFormat.getTime(date);
//                tvBirthday.setText(TimeFormatOld.getTime(date));
                tvBirthday.setText(birthday);
                checkContent();
            }
        });

    }

    public void initBundle(Bundle bundle) {
        isCheck = false;

        String nickName = bundle.getString("nickname");
        iconUrl = bundle.getString("icon");
        Log.i("qqqqqq", "----------------iconUrl" + iconUrl);
        opneId = bundle.getString("id");
        unionid = bundle.getString("unionid");
        token = bundle.getString("token");
        expires = bundle.getLong("expires");
        editNicknam.setText(nickName);
        int mTextMaxlenght = 0;
        Editable editable = editNicknam.getText();
        String str = editable.toString().trim();
        int selEndIndex = str.length();
        editNicknam.setSelection(selEndIndex);

        for (int i = 0; i < str.length(); i++) {

            char charAt = str.charAt(i);

            //32-122包含了空格，大小写字母，数字和一些常用的符号，

            //如果在这个范围内则算一个字符，

            //如果不在这个范围比如是汉字的话就是两个字符

            if (charAt >= 32 && charAt <= 122) {

                mTextMaxlenght++;

            } else {

                mTextMaxlenght += 2;

            }

            // 当最大字符大于14时，进行字段的截取，并进行提示字段的大小

            if (mTextMaxlenght > 14) {

                // 截取最大的字段

                String newStr = str.substring(0, i);

                editNicknam.setText(newStr);

                // 得到新字段的长度值

                editable = editNicknam.getText();

                int newLen = editable.length();

                if (selEndIndex > newLen) {

                    selEndIndex = editable.length();

                }
                Selection.setSelection(editable, selEndIndex);
                break;
            }
        }
//        tvBirthday.setText(birthday);

        /**
         * 判断昵称是否为空，如果字符长度大于14则只能取14个字符
         * 反之则取nickname长度
         */
        editNicknam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int mTextMaxlenght = 0;
                Editable editable = editNicknam.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);

                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，

                //如果是汉字则为两个字符

                for (int i = 0; i < str.length(); i++) {

                    char charAt = str.charAt(i);

                    //32-122包含了空格，大小写字母，数字和一些常用的符号，

                    //如果在这个范围内则算一个字符，

                    //如果不在这个范围比如是汉字的话就是两个字符

                    if (charAt >= 32 && charAt <= 122) {

                        mTextMaxlenght++;

                    } else {

                        mTextMaxlenght += 2;

                    }

                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小

                    if (mTextMaxlenght > 14) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        editNicknam.setText(newStr);

                        // 得到新字段的长度值

                        editable = editNicknam.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置

                        Selection.setSelection(editable, selEndIndex);

//                        Toast.makeText(RegisterNextActivity.this, "最大长度为14个字符或7个汉字！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
//        Matcher matcher = pattern.matcher(nickName);
//        if (!CommonFunction.isEmptyOrNullStr(nickName)) {
//            if (matcher.matches())
//            {
//                editNicknam.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
//            }else {
//                if (nickName.length() > 14) {
//                    editNicknam.setText(nickName.substring(0, 13));
//                    editNicknam.setSelection(14);
//                }
//                editNicknam.setText(nickName);
//                editNicknam.setSelection(nickName.length());
//            }
//        }

        GlideUtil.loadCircleImage(BaseApplication.appContext, iconUrl, ivAvatar.getImageView(),
                R.drawable.iaround_default_img,
                R.drawable.iaround_default_img);
    }

    @Override
    public void setMonitor() {
        tvBirthday.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        ivLeft.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);

//        btnFacebook.setOnClickListener(this);
//        btnQQ.setOnClickListener(this);
//        btnWechat.setOnClickListener(this);
//        btnSina.setOnClickListener(this);

        rgSex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

//                showSexHintDialog();
                if (i == R.id.rb_register_next_man) {
                    if (isShowSexDialogNum == 1) {

                        isCheck = true;
                        showSexHintDialog();
                    } else {
                        isCheck = false;
                    }
                    sex = "m";
                    isCheckSex = false;
                } else {
                    if (isShowSexDialogNum == 1) {
                        isCheck = true;
                        showSexHintDialog();
                    } else {
                        isCheck = false;
                    }
                    sex = "f";
                    isCheckSex = false;
                }
                isShowSexDialogNum++;
                checkContent();
                tvSexHint.setVisibility(View.VISIBLE);
            }
        });

        editNicknam.addTextChangedListener(textWatcher);
    }

    /**
     * 展示选择性别提示框
     */
    private void showSexHintDialog() {
        if (isCheck && isShowSexDialogNum <= 1) {
            // 提示性别
            DialogUtil.showOKDialog(this, R.string.prompt,
                    R.string.select_sex_no_chanage, null);
            isCheck = false;
        }
    }

    @Override
    public void onClick(View view) {
        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, "");
        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.LOGIN_TOKEN_QQ, "");
        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.LOGIN_ID_QQ, "");

        switch (view.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                if (dialog != null)
                    dialog.show();
                break;
            case R.id.iv_register_next_avatar://选择头像
                requestCamera();
                break;
            case R.id.btn_register_next://下一步
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.USER_AVATAR, iconUrl);
                String nickName = editNicknam.getText().toString();
                if (nickName == null || nickName.length() <= 0) { // 昵称为空
                    Toast.makeText(this, R.string.register_next_please_nickname, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                if (isCheckSex) {
                    // 性别不能为空
                    Toast.makeText(this, R.string.select_sex_no, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                String birthday = tvBirthday.getText().toString();
                if (birthday == null || birthday.length() <= 0) { // 生日为空
                    Toast.makeText(this, R.string.register_next_please_birthday, Toast.LENGTH_SHORT)
                            .show();
                    return;
                }

                if (accounttype == 6) {
                    Intent intent = new Intent(RegisterNextActivity.this, RegisterActivity.class);
                    intent.putExtra(RegisterActivity.KEY_HEAD, iconUrl);
                    intent.putExtra(RegisterActivity.KEY_NAME, nickName);
                    intent.putExtra(RegisterActivity.KEY_BIRTHDAY, birthday);
                    intent.putExtra(RegisterActivity.KEY_SEX, sex);
                    startActivity(intent);
                } else {
                    Me u = new Me();
                    int wannaMeet = u.getWannaMeet();
                    showWaitDialog();
                    GET_USER_REGISTER = LoginHttpProtocol.userRegisterOther(this, "" + accounttype, token, opneId, expires, "", "", nickName, sex, wannaMeet, birthday, iconUrl, this,unionid,"","","");

                }
                SharedPreferenceUtil.getInstance(this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                break;
            case R.id.tv_register_next_birthday://选择生日
                pvTime.show();
                hiddenKeyBoard(this);
                break;

        }
    }

    private final int REQUEST_CODE_GALLERY = 1001;

    @Override
    public void doCamera() {
        super.doCamera();
//        PictureMultiSelectActivity.skipToPictureMultiSelectAlbumCrop(mContext,
//                MSG_PICK_IMAGE);
        GalleryUtils.getInstance().openGallerySingleCrop(this,REQUEST_CODE_GALLERY,mOnHanlderResultCallback);
    }

    private View.OnClickListener threadClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch ((int) v.getTag(R.layout.view_thread_ad)) {
                case 4:
                    long currentTimefacebook = Calendar.getInstance().getTimeInMillis();
                    if (currentTimefacebook - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTimefacebook;
                        accounttype = 4;
                        LoginHttpProtocol.claerLogin(RegisterNextActivity.this, accounttype);
                        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                        LoginHttpProtocol
                                .facebookLogin(RegisterNextActivity.this, new TheHandler(RegisterNextActivity.this), MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    }
                    break;
                case 1:
                    long currentTimeQQ = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeQQ - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTimeQQ;
                        accounttype = 1;
                        LoginHttpProtocol.claerLogin(RegisterNextActivity.this, accounttype);
                        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                        LoginHttpProtocol
                                .qqLogin(RegisterNextActivity.this, new TheHandler(RegisterNextActivity.this), MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    }
                    break;
                case 7:
                    long currentTimeWechat = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeWechat - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTimeWechat;
                        accounttype = 7;
                        LoginHttpProtocol.claerLogin(RegisterNextActivity.this, accounttype);
                        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                        LoginHttpProtocol
                                .wechatLogin(RegisterNextActivity.this, new TheHandler(RegisterNextActivity.this), MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    }
                    break;
                case 2:
                    long currentTimeWeibo = Calendar.getInstance().getTimeInMillis();
                    if (currentTimeWeibo - lastClickTime > MIN_CLICK_DELAY_TIME) {
                        lastClickTime = currentTimeWeibo;
                        accounttype = 2;
                        LoginHttpProtocol.claerLogin(RegisterNextActivity.this, accounttype);
                        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putInt(SharedPreferenceUtil.LOGIN_TYPE, accounttype);
                        LoginHttpProtocol
                                .weiboLogin(RegisterNextActivity.this, new TheHandler(RegisterNextActivity.this), MSG_LOGIN_RES, "6", httpCallBack, SHOW_DIALOG);
                    }
                    break;
            }
        }
    };

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (GET_USER_REGISTER == flag) {
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(
                    result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);

            }

            if (entity != null && entity.isSuccess()) {
                //gh 这个位置不需要密码不需要MD5加密
                LoginHttpProtocol.doLogin(RegisterNextActivity.this, entity.account, entity.password, "", accounttype, httpCallBack, unionid);
            } else if (entity.error == 8200 | entity.error == 8201) {
                if(gt3GeetestUtils == null)
                    return;
                gt3GeetestUtils.getGeetest(RegisterNextActivity.this, captchaURL, validateURL, null,gt3Listener);//极验验证开始
            } else {
                ErrorCode.showError(RegisterNextActivity.this, result);
            }
        } /*else if (GET_GEETTEST_FLAG == flag){
            GeetestData geetest = GsonUtil.getInstance( )
                    .getServerBean( result, GeetestData.class );
            if (geetest.isSuccess()){

                int height = getResources().getDisplayMetrics().heightPixels;
                int width = getResources().getDisplayMetrics().widthPixels;
                float scale = getResources().getDisplayMetrics().density;

                String gt_mobile_req_url = geetest.url
                        + "?gt=" + geetest.gt
                        + "&challenge=" + geetest.challenge
                        + "&success=" + (!true ? 0 : 1)
                        + "&product=" + "embed"
                        + "&debug=" + false
                        + "&width=" + (int)(width / scale + 0.5f);
                gt3GeetestUrl.setValidateURL(gt_mobile_req_url);

            }else{
                ErrorCode.showError(RegisterNextActivity.this,result);
            }

        }*/ else {
            hideWaitDialog();
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(
                    result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);

            }
            if (entity != null && entity.isSuccess()) {
                entity.setUrl();
                entity.loginSuccess(RegisterNextActivity.this);
                ConnectorManage.getInstance(RegisterNextActivity.this).setKey(entity.key);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
                String userStateKey = SharedPreferenceUtil.USER_STATE
                        + Common.getInstance().loginUser.getUid();
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putBoolean(userStateKey, true);
                SpaceModel.getInstance(this).setAutoLogin(true);// 自动登录

                // 判断是否是第一次开启应用
                CommonFunction.showGuideView(RegisterNextActivity.this);
                finish();

//                boolean isFirstOpen = SharedPreferenceUtil.getInstance(RegisterNextActivity.this).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//                // 如果是第一次启动，则先进入功能引导页
//                if (isFirstOpen) {
//                    SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//                    Intent intent = new Intent(RegisterNextActivity.this, GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    Intent intent = new Intent(RegisterNextActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
            } else {
                ErrorCode.showError(RegisterNextActivity.this, result);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        ErrorCode.toastError(this, e);
    }

    public HttpCallBack httpCallBack = new HttpCallBack() {
        @Override
        public void onGeneralSuccess(String result, long flag) {
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(
                    result, LoginResponseBean.class);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if(item != null && entity != null){
                entity.setItem(item);

            }

            if (entity != null && entity.isSuccess()) {
                HashMap<String, Object> res = null;
                try {
                    res = RegisterModel.getInstance().getRes(RegisterNextActivity.this, result, flag);
                } catch (Throwable e) {
                    CommonFunction.log(e);
                }
                entity.setUrl();
                entity.loginSuccess(RegisterNextActivity.this);
                ConnectorManage.getInstance(RegisterNextActivity.this).setKey(entity.key);
//                SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.LIVE_USER,entity.live);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
                String userStateKey = SharedPreferenceUtil.USER_STATE
                        + Common.getInstance().loginUser.getUid();
                SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putBoolean(userStateKey, true);
                SpaceModel.getInstance(RegisterNextActivity.this).setAutoLogin(true);// 自动登录
                LoginHttpProtocol.afterLogin(RegisterNextActivity.this, res, accounttype, opneId, token, expires, this, unionid);

                // 判断是否是第一次开启应用
                CommonFunction.showGuideView(RegisterNextActivity.this);
                finish();

//                boolean isFirstOpen = SharedPreferenceUtil.getInstance(RegisterNextActivity.this).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//                // 如果是第一次启动，则先进入功能引导页
//                if (isFirstOpen) {
//                    SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//                    Intent intent = new Intent(RegisterNextActivity.this, GuideActivity.class);
//                    startActivity(intent);
//                    finish();
//
//                } else {
//                    Intent intent = new Intent(RegisterNextActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                    finish();
//                }

            }
            HashMap<String, Object> res = new Hashon().fromJson(result);
            if (accounttype == 2) {
                LoginHttpProtocol
                        .processWeiboLogin(RegisterNextActivity.this, result, flag, res, new TheHandler(RegisterNextActivity.this),
                                MSG_LOGIN_RES, MSG_GO_REGISTER, "6", RegisterNextActivity.this);
            } else if (accounttype == 1) {
                LoginHttpProtocol
                        .processQqLogin(RegisterNextActivity.this, result, flag, res, new TheHandler(RegisterNextActivity.this), MSG_LOGIN_RES,
                                MSG_GO_REGISTER, "6", RegisterNextActivity.this);
            } else if (accounttype == 4) {
                LoginHttpProtocol
                        .processFacebookLogin(RegisterNextActivity.this, result, flag, res, new TheHandler(RegisterNextActivity.this),
                                MSG_LOGIN_RES, MSG_GO_REGISTER, "6", RegisterNextActivity.this);
            } else if (accounttype == 7) {
                LoginHttpProtocol
                        .processWechatLogin(RegisterNextActivity.this, result, flag, res, new TheHandler(RegisterNextActivity.this),
                                MSG_LOGIN_RES, MSG_GO_REGISTER, "6", RegisterNextActivity.this);
            }
        }

        @Override
        public void onGeneralError(int e, long flag) {

        }
    };

    // arg1 = 0 失败；arg1 = 1成功；arg2 = 1新用户；arg2 = 0老用户；obj -- 提示
    private class TheHandler extends Handler {
        private WeakReference<Activity> cActivity;

        public TheHandler(Activity activity) {
            cActivity = new WeakReference<Activity>(activity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            CommonFunction.log("shifengxiong", "ControlRegisterActivity handle message " + msg.what);
            switch (msg.what) {
                case MSG_WHAT_LOGIN:
                    // 判断是否是第一次开启应用
                    CommonFunction.showGuideView(RegisterNextActivity.this);
                    finish();

//                    boolean isFirstOpen = SharedPreferenceUtil.getInstance(RegisterNextActivity.this).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//                    // 如果是第一次启动，则先进入功能引导页
//                    if (isFirstOpen) {
//
//                        SharedPreferenceUtil.getInstance(RegisterNextActivity.this).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//                        Intent intent = new Intent(RegisterNextActivity.this, GuideActivity.class);
//                        startActivity(intent);
//                        finish();
//
//                    } else {
//                        Intent intent = new Intent(RegisterNextActivity.this, MainFragmentActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                    break;
                case MSG_GO_REGISTER: {
                    if (cActivity.get() != null) {
                        if (accounttype != 6) {
                            initBundle((Bundle) msg.obj);
                            findViewById(R.id.ly_thread_login).setVisibility(View.INVISIBLE);
                        } else {
                            findViewById(R.id.ly_thread_login).setVisibility(View.VISIBLE);
                        }

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
        super.onActivityResult(requestCode, resultCode, data);
        if (LoginHttpProtocol.shareUtils != null) {
            LoginHttpProtocol.shareUtils.onActivityResult(requestCode, resultCode, data);
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                final ArrayList<String> list = new ArrayList<>();

                for (PhotoInfo photoInfo : resultList) {
                    list.add(photoInfo.getPhotoPath());
                }

                iconUrl = "";
                String iconPath = list.get(0);

                // 上传头像
                try {
                    if (!CommonFunction.isEmptyOrNullStr(iconPath) && (new File(iconPath)).exists()) {
                        Map<String, File> picFileMap = new HashMap<>();
                        picFileMap.put(iconPath + ".png", new File(iconPath));
                        RegisterHttpProtocol.uploadHeadPic(picFileMap, uploadImageCallback);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(RegisterNextActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private UploadImageCallback uploadImageCallback = new UploadImageCallback() {

        @Override
        public void onUploadFileFinish(String result) {
            UploadPicEntity uploadPicEntity = GsonUtil.getInstance().getServerBean(result, UploadPicEntity.class);
            if (uploadPicEntity.isSuccess()) {
                iconUrl = uploadPicEntity.getUrl();
                GlideUtil.loadCircleImage(mContext, CommonFunction.getThumPicUrl(iconUrl), ivAvatar.getImageView(),
                        R.drawable.iaround_default_img,
                        R.drawable.iaround_default_img);
            } else {
                ErrorCode.showError(RegisterNextActivity.this, result);
            }
        }

        @Override
        public void onUploadFileError(String e) {

        }
    };

    /**
     * 判断输入内容是否符合标准
     */
    private void checkContent() {
        String birthday = tvBirthday.getText().toString();
        String nickName = editNicknam.getText().toString();
        if (birthday.length() > 0 && nickName.length() > 0 && nickName.length() <= 14 && !isCheck) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }

    /**
     * EditText的监听
     */
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
            /**
             * 判断输入昵称是否超过字符限制
             */
            if (s.length() > 14) {
                Toast.makeText(RegisterNextActivity.this, "字数超过限制,请重新输入", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            checkContent();
        }
    };

    private GT3GeetestBindListener gt3Listener = new GT3GeetestBindListener() {

        @Override
        public void gt3CloseDialog(int i) {
            super.gt3CloseDialog(i);
            CommonFunction.showToast(RegisterNextActivity.this,"验证未通过 请重试", Toast.LENGTH_SHORT);
        }

        @Override
        public Map<String, String> gt3CaptchaApi1() {
            return super.gt3CaptchaApi1();
        }

        @Override
        public boolean gt3SetIsCustom() {
            return super.gt3SetIsCustom();
        }

        @Override
        public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
            super.gt3GeetestStatisticsJson(jsonObject);
        }

        @Override
        public void gt3GetDialogResult(boolean b, String s) {
            super.gt3GetDialogResult(b, s);
        }

        @Override
        public void gt3DialogOnError(String s) {
            super.gt3DialogOnError(s);
        }

        //验证码加载准备完成
        @Override
        public void gt3DialogReady() {

        }

        @Override
        public void gt3FirstResult(JSONObject jsonObject) {

        }

        @Override
        public Map<String, String> gt3SecondResult() {
            return null;
        }

        //拿到验证返回的结果,此时还未进行二次验证
        @Override
        public void gt3GetDialogResult(String result) {
            open(result);
        }


        //二次验证请求之后的结果
        @Override
        public void gt3DialogSuccessResult(String result) {
            CommonFunction.log("Other","gt3DialogSuccessResult  = "+result);

            if (null == result)return;
            if (TextUtils.isEmpty(result))return;
            String authKey = "";
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (null == status)return;
                if (TextUtils.isEmpty(status))return;
                if (status.equals("success")){
                    authKey = jsonObject.getString("authKey");
                }else {
                    return;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Me u = new Me();
            int wannaMeet = u.getWannaMeet();
            String nickName = editNicknam.getText().toString();
            GET_USER_REGISTER = LoginHttpProtocol.userRegisterOther(RegisterNextActivity.this, "" + accounttype, token, opneId, expires, "", "", nickName, sex, wannaMeet, birthday, iconUrl,RegisterNextActivity.this, unionid,"","","");

        }

    };

    private String geetest_challenge;
    private String geetest_seccode;
    private String geetest_validate;

    private void open(String result) {
        CommonFunction.log("Other","gt3GetDialogResult  = "+result);
        try {
            JSONObject res_json = new JSONObject(result);
            Map<String, String> params = new HashMap<String, String>();
            geetest_challenge = res_json.getString("geetest_challenge");
            geetest_seccode = res_json.getString("geetest_seccode");
            geetest_validate = res_json.getString("geetest_validate");
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (dialog != null) {
                dialog.show();
            }

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
