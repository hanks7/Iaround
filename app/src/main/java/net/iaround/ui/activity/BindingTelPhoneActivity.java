
package net.iaround.ui.activity;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.VerifyBean;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.Hashon;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.utils.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


/**
 * 登陆时绑定手机号页面
 */
public class BindingTelPhoneActivity extends ActionBarActivity implements HttpCallBack
{
	public static final String TAG = "BindTelPhoneActivity";
	private EditText mEtPhoneNumber; //手机号输入框
	private RelativeLayout mRlPhoneAreaContainer; //区号容器
	private TextView mTvPhoneArea;		//区号文字
	private boolean mGettingPhoneArea = false; //正在获取区号

	private EditText mEtVerifyCode; //验证码输入框
	private RelativeLayout mRlVerifyContainer; //验证码容器
	private TextView mTvVerifyCode;		//获取验证码文字
	private boolean mGettingVerifyCode = false; //正在获取验证码

	private LinearLayout mLlAgreementContainer; //同意协议容器
	private ImageView mIvAgreement; //勾选图片
	private boolean mUserAgreement = true;  //是否同意
	private TextView mTvAgreement; //用户协议

	private Button mBtnLogin;  //登陆按钮

	private String mCountryCode; 	//国家代码
	private String mTelPhone;	//用户手机号
	private String mVerifyCode; //验证码

	private Dialog mWaitDialog;

	private long mHttpGetVerifyCodeFlag = -1; //网络接口请求验证码

	private GtAppDlgTask gtAppDlgTask;//极验弹框
	private GtAppDlgTask.GeetBackListener geetBackListener = new GeetBackListenerImpl(this);

	private Timer mVerifyCodeTimer = null;
	private VerifyCodeTimerTask mVerifyCodeTimerTask = null;

	private Handler mHandler = new UIMainHandler(this);

	private int mVerifyCodeTimeCount = 60; //验证码倒计时初始值

	private Bundle mUserInfo = null; //用户信息

	private long HTTP_FLAG_USER_REGISTER = -1;
	private long HTTP_FLAG_USER_LOGIN = -1;

	private HttpCallBackImpl  mHttpCallBack = new HttpCallBackImpl(this);

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );

		CommonFunction.log(TAG, "onCreate() into");

		setContentView( R.layout.activity_binding_telphone);

		initView();

		initListener();

		initData();
	}

	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );

		CommonFunction.log(TAG, "onDestroy() into");

		if(gtAppDlgTask != null) {
			gtAppDlgTask.onDestory();
		}

		if(mWaitDialog!=null){
			mWaitDialog.dismiss();
			mWaitDialog = null;
		}
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK ) {
			doBack( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}


	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if ( requestCode == 1  ) {
			mGettingPhoneArea = false;
			if(data != null) {
				//选择了某个国家的区号
				String area = data.getStringExtra("area");
				if (!CommonFunction.isEmptyOrNullStr(area) && area.contains("+")) {
					// 清空原来输入的手机号
					mEtPhoneNumber.setText("");
					String[] spitString = area.split("\\+");
					if (spitString != null && spitString.length >= 2) {
						mCountryCode = "+" + spitString[1];
						mCountryCode = mCountryCode.trim();
						if (spitString.length > 2) {
							String flag = spitString[2];//国旗编码
							mTvPhoneArea.setText(flag + "\n" + mCountryCode);
						} else {
							mTvPhoneArea.setText(mCountryCode);
						}
						if (spitString[1].equals("+86")) {
							// 大陆手机限制11位输入
							mEtPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
						} else {
							//非大陆手机50位输入
							mEtPhoneNumber.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
						}
					}
				}
			}
		}
	}

	//手机号码输入监听器
	private TextWatcher mTelPhoneTextWatcher = new TextWatcher(){
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			String telPhone = mEtPhoneNumber.getText().toString();
			String verifyCode = mEtVerifyCode.getText().toString();
			if(telPhone!=null && telPhone.length()>0 && verifyCode!=null && verifyCode.length()>=4){
				if(mBtnLogin.isEnabled()==false) {
					mBtnLogin.setEnabled(true);
					mBtnLogin.setBackgroundResource(R.drawable.shape_gray_boder_red_bg_radius_23);
				}
			}else{
				if(mBtnLogin.isEnabled()) {
					mBtnLogin.setEnabled(false);
					mBtnLogin.setBackgroundResource(R.drawable.shape_gray_boder_gary_bg_radius_23);
				}
			}
		}
	};

	//验证码文字输入监听器
	private TextWatcher mVerifyCodeTextWatcher = new TextWatcher(){
		@Override
		public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

		}

		@Override
		public void afterTextChanged(Editable editable) {
			String telPhone = mEtPhoneNumber.getText().toString();
			String verifyCode = mEtVerifyCode.getText().toString();
			if(telPhone!=null && telPhone.length()>0 && verifyCode!=null && verifyCode.length()>=4){
				if(mBtnLogin.isEnabled()==false) {
					mBtnLogin.setEnabled(true);
					mBtnLogin.setBackgroundResource(R.drawable.shape_gray_boder_red_bg_radius_23);
				}
			}else{
				if(mBtnLogin.isEnabled()) {
					mBtnLogin.setEnabled(false);
					mBtnLogin.setBackgroundResource(R.drawable.shape_gray_boder_gary_bg_radius_23);
				}
			}
		}
	};


	//网络回调接口
	@Override
	public void onGeneralSuccess(String result, long flag) {
		if (mHttpGetVerifyCodeFlag == flag) {
			mGettingVerifyCode = false;
			VerifyBean entity = GsonUtil.getInstance().getServerBean(result, VerifyBean.class);
//				JSONObject object = new JSONObject(result);
//				String status = object.optString("status");
			if (entity != null) {
				int status = entity.status;
				if (status == -100) {
					if (entity.error == 5608) {
						ErrorCode.showError(mContext, result);
					}
				} else if (entity.isSuccess()) {
					//成功获取验证码 60秒计算时
					mRlVerifyContainer.setEnabled(false);
					mRlVerifyContainer.setBackgroundResource(R.drawable.shape_half_gray_boder_gray_bg_radius_23);
					mVerifyCodeTimeCount = 60;
					mTvVerifyCode.setText(mVerifyCodeTimeCount + "s");

					//开启定时器
					startVerifyCodeTimer();
					if (entity.error == 5613) {
						CommonFunction.toastMsg(BaseApplication.appContext, R.string.user_findpwd_by_phone_hint);
						return;
					}
					if (entity.sms_send_type == 2) {
						Dialog dialog = DialogUtil.showOKDialog(mContext, getString(R.string.prompt), String.format(getString(R.string.sms_voice_send_tip), entity.qu_hao), null);
						dialog.setCancelable(true);
						dialog.setCanceledOnTouchOutside(true);
					}
				}
			}
		}else if(HTTP_FLAG_USER_REGISTER == flag){ //注册请求
			LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
			Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
			if(item != null && entity != null){
				entity.setItem(item);
			}
			if (entity != null && entity.isSuccess()) {
				CommonFunction.log(TAG, "onGeneralSuccess() register success, login now");
				//这个位置不需要密码不需要MD5加密
				HTTP_FLAG_USER_LOGIN = LoginHttpProtocol.doLogin(this, entity.account, entity.password, "", mUserInfo.getInt("accountType"), mHttpCallBack, entity.unionid);
			} else {
				mWaitDialog.dismiss();
				//ErrorCode.showError(this, result);
			}

		}else if(HTTP_FLAG_USER_LOGIN == flag){ //登陆请求
			LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
			Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
			if(item != null && entity != null){
				entity.setItem(item);
			}
			if (entity != null && entity.isSuccess()) {
				entity.setUrl();
				entity.loginSuccess(BaseApplication.appContext);
				ConnectorManage.getInstance(BaseApplication.appContext).setKey(entity.key); //遇见http接口 token
				SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
				SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
				SharedPreferenceUtil.getInstance(BaseApplication.appContext).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
				String userStateKey = SharedPreferenceUtil.USER_STATE + Common.getInstance().loginUser.getUid();
				SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(userStateKey, true);
				SpaceModel.getInstance(BaseApplication.appContext).setAutoLogin(true);// 自动登录

				//用户登陆成功才能发送app启动统计
				Statistics.onAppCreated();
				CommonFunction.log(TAG, "onGeneralSuccess() login success, status=" + entity.status);
			} else {
				//登陆接口失败
				CommonFunction.log(TAG, "onGeneralSuccess() login fail");
			}
			//下面进一步对登陆返回结果进行处理
			HashMap<String, Object> res = new Hashon().fromJson(result);
			int accountType = mUserInfo.getInt("accountType");
			if (accountType == 2) {
				LoginHttpProtocol.processWeiboLogin(this, result, flag, res, mHandler, UIMainHandler.MSG_LOGIN_RES, UIMainHandler.MSG_GO_REGISTER, "6", null );
			} else if (accountType == 1) {
				LoginHttpProtocol.processQqLogin(this, result, flag, res, mHandler, UIMainHandler.MSG_LOGIN_RES, UIMainHandler.MSG_GO_REGISTER, "6", null );
			} else if (accountType == 4) {
				LoginHttpProtocol.processFacebookLogin(this, result, flag, res, mHandler, UIMainHandler.MSG_LOGIN_RES, UIMainHandler.MSG_GO_REGISTER, "6", null );
			} else if (accountType == 7) {
				LoginHttpProtocol.processWechatLogin(this, result, flag, res, mHandler, UIMainHandler.MSG_LOGIN_RES, UIMainHandler.MSG_GO_REGISTER, "6", null );
			}

			mWaitDialog.dismiss();
		}else if (flag <= 0) {
			Toast.makeText(BindingTelPhoneActivity.this, getString(R.string.start_reconnect), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGeneralError(int e, long flag) {
		if (mHttpGetVerifyCodeFlag == flag) {
			mGettingVerifyCode = false;
		}else if(HTTP_FLAG_USER_REGISTER == flag){
			mWaitDialog.dismiss();
		}else if(HTTP_FLAG_USER_LOGIN == flag){
			mWaitDialog.dismiss();
		}
	}

	private void initView(){
		mEtPhoneNumber = (EditText) findViewById(R.id.binding_telphone_phonenumber);
		mRlPhoneAreaContainer = (RelativeLayout) findViewById(R.id.binding_telphone_phone_area_container);
		mTvPhoneArea = (TextView)findViewById(R.id.binding_telphone_phone_area);

		mEtVerifyCode = (EditText) findViewById(R.id.binding_telphone_verify_code);
		mRlVerifyContainer = (RelativeLayout) findViewById(R.id.binding_telphone_verify_container);
		mTvVerifyCode = (TextView)findViewById(R.id.binding_telphone_verify_text);

		mLlAgreementContainer = (LinearLayout)findViewById(R.id.binding_telphone_agreement_container);
		mIvAgreement = (ImageView)findViewById(R.id.binding_telphone_agreement_imageview);
		mTvAgreement = (TextView) findViewById(R.id.binding_telphone_agreement_textview);

		mBtnLogin =(Button) findViewById(R.id.binding_telphone_login_button);

		mWaitDialog = DialogUtil.getProgressDialog( mContext , "" , getString( R.string.please_wait ) , null );
	}

	private void initListener(){
		//返回上一级箭头
		findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doBack();
			}
		});
		findViewById(R.id.iv_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doBack();
			}
		});

		//输入变化
		mEtPhoneNumber.addTextChangedListener(mTelPhoneTextWatcher);
		mEtVerifyCode.addTextChangedListener(mVerifyCodeTextWatcher);

		//点击区号容器
		mRlPhoneAreaContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickAreaContainer();
			}
		});

		mRlVerifyContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getVerifyCode();
			}
		});

		mLlAgreementContainer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				agreementChange();
			}
		});

		mBtnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doLogin();
			}
		});

		mTvAgreement.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				readAgreement();
			}
		});
	}

	private void initData(){
		//标题
		setActionBarTitle(R.string.setting_bind_phone);
		//默认数据
		mEtPhoneNumber.setFilters( new InputFilter[ ]{ new InputFilter.LengthFilter( 11 ) } );
		//极验弹框
		gtAppDlgTask = GtAppDlgTask.getInstance();
		gtAppDlgTask.setContext(this);
		gtAppDlgTask.setGeetBackListener(geetBackListener);

		//登陆的账户类型：遇见ID|手机|邮箱, 微信, QQ, facebook 等

		mUserInfo = getIntent().getBundleExtra("userInfo");
		mCountryCode = mUserInfo.getString("countryCode");
		if (TextUtils.isEmpty(mCountryCode)) {
			mCountryCode = "+86";
		}
		mTvPhoneArea.setText(getCountryFlag(mCountryCode));
		CommonFunction.log(TAG, "initData() account type=" + mUserInfo.getInt("accountType")
				+ "\ninitData() login type=" + mUserInfo.getString("type")
				+ "\ninitData() openid=" + mUserInfo.getString("id")
				+ "\ninitData() access token=" + mUserInfo.getString("token")
				+ "\ninitData() expires=" + mUserInfo.getLong("expires")
				+ "\ninitData() nickname=" + mUserInfo.getString("nickname")
				+ "\ninitData() icon=" + mUserInfo.getString("icon")
				+ "\ninitData() countryCode=" + mUserInfo.getString("countryCode")
				+ "\ninitData() unionid=" + mUserInfo.getString("unionid"));
	}

	/**
	 * 根据传入的国家区号返回相应的国旗，其中台湾显示 “臺灣”
	 * @param countryCode
	 * @return
	 */
	private String getCountryFlag(String countryCode) {
		String[] countries = getResources().getStringArray(R.array.select_countrys);
		for (int i = 0; i < countries.length; i++) {
			if (countries[i].contains(countryCode)) {
				String[] spitString = countries[i].split("\\+");
				if (spitString != null && spitString.length >= 2) {
					mCountryCode = "+" + spitString[1];
					mCountryCode = mCountryCode.trim();
					if (spitString.length > 2) {
						String flag = spitString[2];//国旗编码
						countryCode = flag + "\n" + mCountryCode;
					} else {
						countryCode = mCountryCode;
					}
				}
			}
		}
		return countryCode;
	}

	/*跳转去获取区号
	* */
	private void onClickAreaContainer(){
		if(mGettingPhoneArea==false) {
			mGettingPhoneArea = true;
			Intent intent = new Intent(mContext, CountrySelectActivity.class);
			startActivityForResult(intent, 1);
		}
	}

	/*获取验证码
	* */
	private void getVerifyCode(){
		if(mGettingVerifyCode == false){
			mGettingVerifyCode = true;

			if(checkTelPhone()==false){
				mGettingVerifyCode = false;
				return;
			}

			//弹出极验
			if (Common.getInstance().getGeetestSwitch() == 1){
				if(gtAppDlgTask!=null) {
					if(gtAppDlgTask.getGeetBackListener()!=geetBackListener){
						gtAppDlgTask.setContext(this);
						gtAppDlgTask.setGeetBackListener(geetBackListener);
					}
					gtAppDlgTask.show();
				}
			}else{
				mHttpGetVerifyCodeFlag = LoginHttpProtocol.getMsgCode_662(this,mCountryCode,mTelPhone,5,mHttpCallBack,"","","","");
			}
		}
	}

	/*同意协议变化
	* */
	private void agreementChange(){
		mUserAgreement = !mUserAgreement;
		if(mUserAgreement==false){
			mIvAgreement.setBackgroundResource(R.drawable.rectangle_uncheck);
		}else {
			mIvAgreement.setBackgroundResource(R.drawable.rectangle_check);
		}
	}

	/*登陆注册
	* */
	private void doLogin(){
		if(mUserAgreement==false){
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.register_please_user_protocol);
			return;
		}
		if(checkTelPhone()==false){
			return;
		}
		if(checkVerifyCode()==false){
			return;
		}
		if(mUserInfo==null){
			return;
		}
		//先注册
		int accountType = mUserInfo.getInt("accountType");
		String opneId = mUserInfo.getString("id");
		String unionid = mUserInfo.getString("unionid");
		String token = mUserInfo.getString("token");
		long expires = mUserInfo.getLong("expires");
		String nickName = mUserInfo.getString("nickname");
		String gender = mUserInfo.getString("gender");//取消默认展示性别
		String birthday = mUserInfo.getString("birthday");
		String iconUrl = mUserInfo.getString("icon");

		CommonFunction.log(TAG, "doLogin() mTelPhone="+mTelPhone + ", mVerifyCode=" + mVerifyCode + ", mCountryCode=" + mCountryCode);

		mWaitDialog.show();

		HTTP_FLAG_USER_REGISTER = LoginHttpProtocol.userRegisterOther(this, "" + accountType, token, opneId, expires, "", "", nickName, gender, 0, birthday, iconUrl, mHttpCallBack, unionid,mTelPhone,mVerifyCode,mCountryCode);
	}

	/*检查手机号规则
	* */
	private boolean checkTelPhone(){
		String telphone = mEtPhoneNumber.getText( ).toString( ).trim( );
		Pattern decimalPattern = Pattern.compile( "\\d*" );
		if ( CommonFunction.isEmptyOrNullStr( telphone ) ) {
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.telphone_cannot_be_null );
			return false;
		} else if ( !decimalPattern.matcher( telphone ).matches( ) ) {
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.telphone_not_correct );
			return false;
		}
		// 对大陆手机号做11位判断
		boolean isPhoneNum = CommonFunction.isChinaPhoneLegal(telphone);
		if ( mCountryCode.trim().equals( "+86" ) && (telphone.trim( ).length( ) != 11 || !isPhoneNum) ) {
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.telphone_not_correct );
			return false;
		}
		mTelPhone = telphone;
		return true;
	}

	/*检查验证码合法
	* */
	private boolean checkVerifyCode(){
		String verifyCode = mEtVerifyCode.getText( ).toString( ).trim( );
		Pattern decimalPattern = Pattern.compile( "\\d*" );
		if ( CommonFunction.isEmptyOrNullStr( verifyCode ) ) {
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.msg_code_cannot_be_null );
			return false;
		} else if ( !decimalPattern.matcher( verifyCode ).matches( ) ) {
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.input_correct_code );
			return false;
		}
		if(verifyCode.length()<4){
			CommonFunction.toastMsg(BindingTelPhoneActivity.this, R.string.input_correct_code );
			return false;
		}
		mVerifyCode = verifyCode;
		return true;
	}

	/*返回上一级
	* */
	private void doBack(){
		CommonFunction.hideInputMethod( mContext , mEtPhoneNumber);
		CommonFunction.hideInputMethod( mContext , mEtVerifyCode);
		if(mUserInfo!=null) {
			LoginHttpProtocol.claerLogin(this, mUserInfo.getInt("accountType"));
		}
		finish( );
	}

	private void readAgreement(){
		String str = getResString(R.string.about_agreement);
		String url = CommonFunction.getLangText(mContext, Config.USER_AGREEMENT_URL);
		Intent intent = new Intent(mContext, WebViewAvtivity.class);
		intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE, str);
		intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
		startActivity(intent);
	}

	/**
	 * 极验回调
	 */
	static class GeetBackListenerImpl implements GtAppDlgTask.GeetBackListener{
		private WeakReference<BindingTelPhoneActivity> mActivity;

		public GeetBackListenerImpl(BindingTelPhoneActivity activity){
			mActivity = new WeakReference<BindingTelPhoneActivity>(activity);
		}

		@Override
		public void onSuccess(String authKey) {
			BindingTelPhoneActivity activity = mActivity.get();
			if(activity!=null){
				activity.mGettingVerifyCode = false;
				Common.getInstance().setGeetAuthKey(authKey);
				activity.mHttpGetVerifyCodeFlag = LoginHttpProtocol.getMsgCode_662(activity,activity.mCountryCode,activity.mTelPhone,5,activity.mHttpCallBack,"","","",authKey);
			}
		}

		@Override
		public void onClose() {
			BindingTelPhoneActivity activity = mActivity.get();
			if(activity!=null){
				activity.mGettingVerifyCode = false;
			}
		}
	}

	/* HTTP接口回调
	* */
	static class HttpCallBackImpl implements HttpCallBack{
		private WeakReference<BindingTelPhoneActivity> mActivity;
		public HttpCallBackImpl(BindingTelPhoneActivity activity){
			mActivity = new WeakReference<BindingTelPhoneActivity>(activity);
		}

		@Override
		public void onGeneralSuccess(String result, long flag) {
			BindingTelPhoneActivity activity = mActivity.get();
			if(activity!=null){
				activity.onGeneralSuccess(result,flag);
			}
		}

		@Override
		public void onGeneralError(int e, long flag) {
			BindingTelPhoneActivity activity = mActivity.get();
			if(activity!=null){
				activity.onGeneralError(e,flag);
			}
		}
	}

	/* 开启定时器
  * */
	private void startVerifyCodeTimer(){
		if(mVerifyCodeTimer!=null){
			mVerifyCodeTimer.cancel();
			mVerifyCodeTimer = null;
		}
		mVerifyCodeTimer = new Timer();
		mVerifyCodeTimerTask = new VerifyCodeTimerTask(this);
		mVerifyCodeTimer.schedule(mVerifyCodeTimerTask,0, 1000);
	}

	/* 关闭定时器
    * */
	private void stopVerifyCodeTimer(){
		if(mVerifyCodeTimer!=null){
			mVerifyCodeTimer.cancel();
			mVerifyCodeTimerTask.cancel();
			mVerifyCodeTimerTask = null;
			mVerifyCodeTimer = null;
		}
	}
	/*验证码倒计时
	* */
	static class VerifyCodeTimerTask extends TimerTask {
		private WeakReference<BindingTelPhoneActivity> mActivity = null;

		public VerifyCodeTimerTask(BindingTelPhoneActivity activity){
			mActivity = new WeakReference<BindingTelPhoneActivity>(activity);
		}

		@Override
		public void run() {
			BindingTelPhoneActivity activity = mActivity.get();
			if(CommonFunction.activityIsDestroyed(activity)){
				return;
			}
			//处理定时器刷新
			activity.refreshVerifyCodeTimer();
		}
	}

	/*刷新倒计时
	* */
	private void refreshVerifyCodeTimer(){
		Message message = mHandler.obtainMessage();
		message.what = UIMainHandler.REFRESH_VERIFY_CODE_TIME;
		mHandler.sendMessage(message);
	}

	static class UIMainHandler extends Handler {
		public static final int REFRESH_VERIFY_CODE_TIME = 3001; //更新倒计时秒数
		public static final int MSG_LOGIN_RES = 0x01ff;
		public static final int MSG_GO_REGISTER = 0x02ff;
		public static final int MSG_ARG1_SUCCESS = 1;
		public static final int MSG_ARG1_FAIL = 0;

		private WeakReference<BindingTelPhoneActivity> mActivity;

		public UIMainHandler(BindingTelPhoneActivity activity){
			mActivity = new WeakReference<BindingTelPhoneActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH_VERIFY_CODE_TIME:
					{
						final BindingTelPhoneActivity activity = mActivity.get();
						if(CommonFunction.activityIsDestroyed(activity)) {
							return;
						}
						activity.mVerifyCodeTimeCount--;
						activity.mTvVerifyCode.setText(activity.mVerifyCodeTimeCount + "s");
						if(activity.mVerifyCodeTimeCount == 0){
							activity.stopVerifyCodeTimer();
							activity.mTvVerifyCode.setText(activity.getString(R.string.get_verification_code));
							activity.mRlVerifyContainer.setEnabled(true);
							activity.mRlVerifyContainer.setBackgroundResource(R.drawable.shape_half_gray_boder_red_bg_radius_23);
							activity.mRlVerifyContainer.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									activity.getVerifyCode();
								}
							});
						}
					}
					break;
				case MSG_GO_REGISTER: { // 第三方注册返回
						CommonFunction.log(TAG, "MSG_GO_REGISTER need register");
						final BindingTelPhoneActivity activity = mActivity.get();
						if(CommonFunction.activityIsDestroyed(activity)) {
							return;
						}
						//之前的注册失败
						Toast.makeText(BaseApplication.appContext, activity.getString(R.string.e_4203), Toast.LENGTH_SHORT).show();
					}
					break;
				case MSG_LOGIN_RES: { // 第三方登录返回
						final BindingTelPhoneActivity activity = mActivity.get();
						if(CommonFunction.activityIsDestroyed(activity)) {
							return;
						}
						if (msg.arg1 == MSG_ARG1_FAIL) { // 失败
							CommonFunction.log(TAG, "MSG_LOGIN_RES login fail");
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
												String s = errordesc.substring(index + 1, errordesc.length());
												String errorHint = String.format(activity.getString(R.string.login_error_hint), s);
												Toast.makeText(BaseApplication.appContext, errorHint, Toast.LENGTH_SHORT).show();
											}
										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								}
							}
						} else if (msg.arg1 == MSG_ARG1_SUCCESS) {
							CommonFunction.log(TAG, "MSG_LOGIN_RES login success");
							// 成功，跳转到主页面
							// 判断是否是第一次开启应用
							CommonFunction.showGuideView(activity);
							CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
							activity.finish();
//							boolean isFirstOpen = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(SharedPreferenceUtil.FIRST_OPEN, true);
//							if (isFirstOpen) { 	// 如果是第一次启动，则先进入功能引导页
//								SharedPreferenceUtil.getInstance(BaseApplication.appContext).putBoolean(SharedPreferenceUtil.FIRST_OPEN, false);
//								Intent intent = new Intent(activity, GuideActivity.class);
//								activity.startActivity(intent);
//								CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
//								activity.finish();
//							} else {
//								activity.startActivity(new Intent(activity, MainFragmentActivity.class));
//								CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
//								activity.finish();
//							}
						}
					}
					break;
				default:
					break;
			}
		}

	}
}
