package net.iaround.pay;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.model.im.DuibaBackBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.view.FlagImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 兑吧页面
 * 
 * Version 1.0.1
 * @author duiba
 * 1、设置toolbar标题title为单行显示。
 * 2、设置tile最大宽度为200dp。
 * 3、修改toolbar高度为dip单位，20dip。
 * 4、修改返回图标为靠左居中，margin-left=10dp。
 * 5、添加dip2px()单位转换方法。
 */
/**
 * Version 1.0.2
 * @author duiba
 * 1、修复未登录用户登录后回到页面，后退到之前的页面时会刷新一次，去掉未登录状态。
 * 2、后退刷新方法修复。
 * 3、将栈内Activity对象改为CreditActivity。
 */
/**
 * Version 1.0.3
 * @author duiba fxt
 * 1、添加分享功能，支持分享的页面的导航栏会显示“分享”，需到兑吧管理后台配置并开启。
 * 2、添加各个功能的注释信息。
 * 3、分享接口和自动登录接口改为AlertDialog的展示形式。
 */
/**
 * Version 1.0.4
 * @author duiba fxt
 * 删除webview配置： settings.setLoadWithOverviewMode(true); 
 * 上面配置可能导致页面无法点击，页面适配等问题。
 */

/**
 * Version 1.0.5
 * @author duiba fxt
 * 在onConsume方法中加入刷新积分的js请求。如果页面含有onDBNewOpenBack()方法,则调用该js方法(刷新积分)
 * 根据api版本，4.4之后使用evaluateJavascript方法。
 */
public class CreditActivity extends SuperActivity {
	private static String ua;
	private static Stack<CreditActivity> activityStack;
	public static final String VERSION="1.0.5";
    public static CreditsListener creditsListener;

    public interface CreditsListener{
        /**
         * 当点击分享按钮被点击
         * @param shareUrl 分享的地址
         * @param shareThumbnail 分享的缩略图
         * @param shareTitle 分享的标题
         * @param shareSubtitle 分享的副标题
         */
        void onShareClick(WebView webView, String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle);

        /**
         * 当点击登录
         * @param webView 用于登录成功后返回到当前的webview并刷新。
         * @param currentUrl 当前页面的url
         */
        void onLoginClick(WebView webView, String currentUrl);
    }

    protected String url;
    
    protected String shareUrl;			//分享的url
    protected String shareThumbnail;	//分享的缩略图
    protected String shareTitle;		//分享的标题
    protected String shareSubtitle;		//分享的副标题

    protected Boolean ifRefresh = false;
    protected Boolean delayRefresh = false;

    protected String navColor;
    protected String titleColor;
    protected Long shareColor;
    
    protected WebView mWebView;
//    protected LinearLayout mLinearLayout;
//    protected RelativeLayout mNavigationBar;
    protected TextView mTitle;
    protected FrameLayout mBackView;
//    protected TextView mShare;
    protected LinearLayout mRightText;
    private Dialog mProgressDialog;

    private int RequestCode=100;
	private long HTTP_GET_DUIBA;
	private final static int  HANDLE_GET_DUIBA_SUCCESS = 1001;
	private final static int  HANDLE_GET_DUIBA_FAIL = 1002;
	private DuibaBackBean duibaBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);	//锁定竖屏显示
        
        url=getIntent().getStringExtra("url");
        if(url==null){
            throw new RuntimeException("url can't be blank");
        }

        if (activityStack == null) {
            activityStack = new Stack<CreditActivity>();
        }
        activityStack.push(this);
        
        //初始化页面
        initView();
        //隐藏系统默认的ActionBar
        ActionBar actionBar = getActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
 
        //添加后退按钮监听事件
        mBackView.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                onBackClick();
            }
        });
        findViewById(R.id.fl_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackClick();
            }
        });
        //添加分享按钮的监听事件
//        if(mShare!=null){
//        	 mShare.setOnClickListener(new OnClickListener() {
//                 public void onClick(View view) {
//                 	creditsListener.onShareClick(mWebView, shareUrl,shareThumbnail, shareTitle, shareSubtitle);
//                 }
//             });
//        }
        
        mRightText.setOnClickListener(new  OnClickListener() {
			@Override
			public void onClick(View arg0) {
				CreditSourcesActivity.jumpCreditSourcesActivity(CreditActivity.this);
			}
		});
        
        if(ua==null){
            ua=mWebView.getSettings().getUserAgentString()+" Duiba/"+VERSION;
        }
        mWebView.getSettings().setUserAgentString(ua);
        mWebView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String title) {
                CreditActivity.this.onReceivedTitle(view, title);
            }
        });
        
        if (!"".equals(url)) {
        	loadUrl();
		}else {
			reuqestDuibaData();
		}
    }
    
    private void loadUrl(){
        mWebView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return shouldOverrideUrlByDuiba(view, url);
            }
            //页面加载结束时获取页面分享信息，如含分享信息，则导航栏上显示分享按钮
            @Override
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:if(document.getElementById('duiba-share-url')){duiba_app.shareInfo(document.getElementById(\"duiba-share-url\").getAttribute(\"content\"));}");
                super.onPageFinished(view, url);
            }
        });

        //js调java代码接口。
        mWebView.addJavascriptInterface(new Object(){
        	//用于回传分享url和title。
            @JavascriptInterface
            public void shareInfo(String content) {
                if(content!=null){
                    String[] dd=content.split("\\|");
                    if(dd.length==4){
                        setShareInfo(dd[0],dd[1],dd[2],dd[3]);
//                        mShare.setVisibility(View.VISIBLE);
                    }
                }
            }
            
            //用于跳转用户登录页面事件。
            @JavascriptInterface
            public void login(){
            	if(creditsListener!=null){
            		mWebView.post(new Runnable() {
						@Override
						public void run() {
							creditsListener.onLoginClick(mWebView, mWebView.getUrl());
						}
					});
            	}
            }
        },"duiba_app");
        
        mWebView.loadUrl(url);
    }
    
    //配置分享信息
    protected void setShareInfo(String shareUrl, String shareThumbnail, String shareTitle, String shareSubtitle){
    	this.shareUrl = shareUrl;
    	this.shareThumbnail = shareThumbnail;
    	this.shareSubtitle = shareSubtitle;
    	this.shareTitle = shareTitle;
    }
    
    protected void onBackClick(){
        Intent intent=new Intent();
        setResult(99,intent);
        finishActivity(this);
    }

    /**
	 * 请求兑吧自动登录URL
	 */
	private void reuqestDuibaData() {
		showProgressDialog();
		HTTP_GET_DUIBA = GoldHttpProtocol.autoLoginDuiba(mContext,"", this);
	}
	
    //初始化页面
    protected void initView(){

        mTitle = (TextView)findViewById(R.id.tv_title);
        TextView right = (TextView)findViewById(R.id.tv_right);
        mBackView = (FrameLayout)findViewById(R.id.fl_left);
        mRightText = (LinearLayout)findViewById(R.id.fl_right);
        mWebView = (WebView)findViewById(R.id.webview);
        right.setVisibility(View.VISIBLE);
        right.setText(getString(R.string.points_sources));

//        mLinearLayout=new LinearLayout(this);
//        mLinearLayout.setBackgroundColor(Color.GRAY);
//        mLinearLayout.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//        mLinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//        int height50dp = dip2px(this,50);
//        //自定义导航栏
//        initNavigationBar();
//
//        LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
//                LayoutParams.FILL_PARENT, height50dp);
//
//        mLinearLayout.addView(mNavigationBar,mLayoutParams);
        //初始化WebView配置
        initWebView();

//        mLinearLayout.addView(mWebView);

    }

  //自定义导航栏，包含 后退按钮，页面标题，积分来源按钮，分享按钮（默认隐藏）
//    protected void initNavigationBar(){
//    	int dp200 = dip2px(this,200);
//    	int dp50 = dip2px(this,50);
//    	int dp30 = dip2px(this,30);
//    	int dp20 = dip2px(this,20);
//    	int dp10 = dip2px(this,10);
//    	int dp5 = dip2px(this,5);
//
//        mNavigationBar=new RelativeLayout(this);
//        mNavigationBar.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.FILL_PARENT,dp20));
//        mNavigationBar.setBackgroundResource(R.drawable.z_common_title_bg);
//        //标题栏
//        mTitle=new TextView(this);
//        mTitle.setMaxWidth(dp200);
//        mTitle.setLines(1);
//        mTitle.setTextSize(18.0f);
//        mTitle.setTextColor(Color.WHITE);
//        mNavigationBar.addView(mTitle);
//        android.widget.RelativeLayout.LayoutParams lp=(android.widget.RelativeLayout.LayoutParams)mTitle.getLayoutParams();
//        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
//        //返回键
//        mBackView = new ImageView(this);
//        mBackView.setBackgroundResource(R.drawable.title_back);
//        RelativeLayout.LayoutParams mBackLayout=new RelativeLayout.LayoutParams(dp30, dp30);
//        mBackLayout.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        mBackLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//        mBackLayout.setMargins(dp5, 0, 0, 0);
//        mNavigationBar.addView(mBackView,mBackLayout);
//        //积分来源
//        mRightText=new TextView(this);
//        mRightText.setLines(1);
//        mRightText.setTextSize(14.0f);
//        mRightText.setText(getString(R.string.points_sources));
//        mRightText.setPadding(dp20, dp10, dp20, dp10);
//        mRightText.setTextColor(Color.WHITE);
//        mNavigationBar.addView(mRightText);
//        android.widget.RelativeLayout.LayoutParams mRightTextLp=(android.widget.RelativeLayout.LayoutParams)mRightText.getLayoutParams();
//        mRightTextLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        mRightTextLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//
//        //在导航栏的右侧添加分享按钮（无分享信息的页面隐藏）
//        mShare=new TextView(this);
//        mShare.setLines(1);
//        mShare.setTextSize(14.0f);
//        mShare.setText(getString(R.string.share));
//        mShare.setPadding(0, 0, dp20, 0);
//        mShare.setTextColor(Color.WHITE);
//        mNavigationBar.addView(mShare);
//        android.widget.RelativeLayout.LayoutParams shareLp=(android.widget.RelativeLayout.LayoutParams)mShare.getLayoutParams();
//        shareLp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        shareLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        //设置为默认不显示
//        mShare.setVisibility(View.INVISIBLE);
//    }

    //初始化WebView配置
    protected void initWebView(){
//        mWebView=new WebView(this);
//        mWebView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        WebSettings settings = mWebView.getSettings();

        // User settings
        settings.setJavaScriptEnabled(true);	//设置webview支持javascript
        settings.setLoadsImagesAutomatically(true);	//支持自动加载图片
        settings.setUseWideViewPort(true);	//设置webview推荐使用的窗口，使html界面自适应屏幕
        settings.setSaveFormData(true);	//设置webview保存表单数据
        settings.setSavePassword(true);	//设置webview保存密码
        settings.setDefaultZoom(ZoomDensity.MEDIUM);	//设置中等像素密度，medium=160dpi
        settings.setSupportZoom(true);	//支持缩放

        CookieManager.getInstance().setAcceptCookie(true);

        if (Build.VERSION.SDK_INT > 8) {
            settings.setPluginState(PluginState.ON_DEMAND);
        }

        // Technical settings
        settings.setSupportMultipleWindows(true);
        mWebView.setLongClickable(true);
        mWebView.setScrollbarFadingEnabled(true);
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.setDrawingCacheEnabled(true);

        settings.setAppCacheEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setDomStorageEnabled(true);
    }

    protected void onReceivedTitle(WebView view, String title){
        mTitle.setText(title);
    }

    /**
     * 拦截url请求，根据url结尾执行相应的动作。
     * 用途：模仿原生应用体验，管理页面历史栈。（重要）
     * @param view
     * @param url
     * @return
     */
    protected boolean shouldOverrideUrlByDuiba(WebView view, String url){
        if(this.url.equals(url)){
            view.loadUrl(url);
            return true;
        }
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            return false;
        }
        if(url.contains("dbnewopen")){	//新开页面
            Intent intent = new Intent();
            intent.setClass(CreditActivity.this, CreditActivity.this.getClass());
            intent.putExtra("navColor", navColor);
            intent.putExtra("titleColor", titleColor);
            url = url.replace("dbnewopen", "none");
            intent.putExtra("url", url);
            startActivityForResult(intent, RequestCode);
        }else if(url.contains("dbbackrefresh")){	//后退并刷新
            url = url.replace("dbbackrefresh", "none");
            Intent intent = new Intent();
            intent.putExtra("url", url);
            intent.putExtra("navColor", navColor);
            intent.putExtra("titleColor", titleColor);
            setResult(RequestCode,intent);
            finishActivity(this);
        }else if (url.contains("dbbackrootrefresh")){	//回到积分商城首页并刷新
            url = url.replace("dbbackrootrefresh", "none");
            if(activityStack.size()==1){
            	finishActivity(this);
            }else{
            	activityStack.get(0).ifRefresh = true;
                finishUpActivity();
            }
        }else if (url.contains("dbbackroot")){	//回到积分商城首页
            url = url.replace("dbbackroot", "none");
            if(activityStack.size()==1){
            	finishActivity(this);
            }else{
                finishUpActivity();
            }
        }else if(url.contains("dbback")){	//后退
            url = url.replace("dbback", "none");
            finishActivity(this);
        }else{
        	if(url.endsWith(".apk") || url.contains(".apk?")){	//支持应用链接下载
                Uri uri = Uri.parse(url);
                Intent viewIntent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(viewIntent);
                return true;
            }
        	if(url.contains("autologin")&&activityStack.size()>0){	//未登录用户登录后返回，所有历史页面置为待刷新
        		//将所有已开Activity设置为onResume时刷新页面。
        		setAllActivityDelayRefresh();
        	}
            view.loadUrl(url);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode==100){
            if(intent.getStringExtra("url")!=null){
                this.url=intent.getStringExtra("url");
                mWebView.loadUrl(this.url);
                ifRefresh = false;
            }
        }
    }

    @Override
	protected void onResume() {
		super.onResume();
		if (ifRefresh) {
			this.url = getIntent().getStringExtra("url");
			mWebView.loadUrl(this.url);
			ifRefresh = false;
		} else if (delayRefresh) {
			mWebView.reload();
			delayRefresh = false;
		} else {
	    	// 返回页面时，如果页面含有onDBNewOpenBack()方法,则调用该js方法。
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
				mWebView.evaluateJavascript("if(window.onDBNewOpenBack){onDBNewOpenBack()}", new ValueCallback<String>() {
					@Override
					public void onReceiveValue(String value) {
						Log.e("credits", "刷新积分");
					}
				});
			} else {
				mWebView.loadUrl("javascript:if(window.onDBNewOpenBack){onDBNewOpenBack()}");
			}
		}
	}

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBackClick();
            return true;
        }else{
            return super.onKeyDown(keyCode, event);
        }
    }
    
    @Override
	public void onGeneralError(int e, long flag) {
		super.onGeneralError(e, flag);
		if (flag == HTTP_GET_DUIBA) {
			Message msg = new Message();
			msg.what = HANDLE_GET_DUIBA_FAIL;
			mHandler.sendMessage(msg);
		}
	}
	
	@Override
	public void onGeneralSuccess(String result, long flag) {
		super.onGeneralSuccess(result, flag);
		if (flag == HTTP_GET_DUIBA) {
			duibaBean = GsonUtil.getInstance().getServerBean(result,
					DuibaBackBean.class);
			if (duibaBean != null && duibaBean.isSuccess()) {
				Message msg = new Message();
				msg.what = HANDLE_GET_DUIBA_SUCCESS;
				mHandler.sendMessage(msg);
			} else {
				Message msg = new Message();
				msg.what = HANDLE_GET_DUIBA_FAIL;
				mHandler.sendMessage(msg);
			}
		}
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			hideProgressDialog();
			switch (msg.what) {
			case HANDLE_GET_DUIBA_SUCCESS:
				handleDuibaBackSuccess();
				break;
			case HANDLE_GET_DUIBA_FAIL:
				handleDuibaBackFail();
				break;
			}
		}
    };
	
	/**
	 * 跳转到兑吧页面
	 */
	protected void handleDuibaBackSuccess() {
        url = duibaBean.loginurl;
        loadUrl();
	}
	
	/**
	 * 获取兑吧URL失败
	 */
	protected void handleDuibaBackFail() {
		CommonFunction.toastMsg(mContext, R.string.e_104);
	}

    
    //--------------------------------------------以下为工具方法----------------------------------------------

    /**
     * 结束除了最底部一个以外的所有Activity
     */
    public void finishUpActivity() {
        int size = activityStack.size();
        for (int i = 0;i < size-1; i++) {
            activityStack.pop().finish();
        }
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
        }
    }
    
    /**
     * 设置栈内所有Activity为返回待刷新。
     * 作用：未登录用户唤起登录后，将所有栈内的Activity设置为onResume时刷新页面。
     */
    public void setAllActivityDelayRefresh(){
    	int size = activityStack.size();
        for (int i = 0;i < size; i++) {
        	if(activityStack.get(i)!=this){
        		activityStack.get(i).delayRefresh = true;
        	}
        }
    }
    
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  

    /**
     * 查询手机内非系统应用
     * @param context
     * @return
     */
    public List<PackageInfo> getAllApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        //获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (int i = 0; i < paklist.size(); i++) {
            PackageInfo pak = paklist.get(i);
            //判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        return apps;
    }

    /**
     * 获取用户最近一次的地理位置，经纬度。
     * @param context
     * @return
     */
    public static String getLocation(Context context) {
        android.location.Location location = null;

        String provider = null;
        double latitude = 0;
        double longitude = 0;
        double accuracy = 0;

        String userLocation = null;

        LocationManager lManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        if (lManager == null) {
            Log.e("location","LocationManager is null");
            return null;
        }

        android.location.Location aLocation = null;

        // 开启了gps
        if (lManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            aLocation = lManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (aLocation != null) {
                latitude = aLocation.getLatitude();
                longitude = aLocation.getLongitude();
                accuracy = aLocation.getAccuracy();
                userLocation = "location: latitude="+latitude+";longitude="+longitude+";accuracy="+accuracy;
                return userLocation;
            }
        }

        if (lManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            aLocation = lManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }

        // 如果net能取到位置，则返回
        if (aLocation != null) {
            latitude = aLocation.getLatitude();
            longitude = aLocation.getLongitude();
            accuracy = aLocation.getAccuracy();
            userLocation = "location: latitude="+latitude+";longitude="+longitude+";accuracy="+accuracy;
            return userLocation;
        }
        TelephonyManager telephonyManager=  (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // 否则判断是否cmda定位
        if (telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA) {
            CdmaCellLocation cdmaCellLocation = (CdmaCellLocation) telephonyManager.getCellLocation();
            if (cdmaCellLocation != null) {
                provider = "cdma";
                latitude = (double) cdmaCellLocation
                        .getBaseStationLatitude() / 14400;
                longitude = (double) cdmaCellLocation
                        .getBaseStationLongitude() / 14400;
                userLocation = "location: latitude="+latitude+";longitude="+longitude+";accuracy="+accuracy;
                return userLocation;
            }
        }

        return null;
    }
    
 // 显示加载框
 	private void showProgressDialog() {
 		if (mProgressDialog == null) {
 			mProgressDialog = DialogUtil.showProgressDialog(mContext, "",
 					getString(R.string.please_wait), null);
 		}
 		mProgressDialog.show();
 	}

 	private void hideProgressDialog() {
 		if (mProgressDialog != null && mProgressDialog.isShowing()) {
 			mProgressDialog.cancel();
 		}
 	}
}
