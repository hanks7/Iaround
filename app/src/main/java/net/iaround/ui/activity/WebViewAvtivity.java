package net.iaround.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.sina.weibo.sdk.WeiboAppManager;
import com.tencent.tauth.Tencent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.share.sina.weibo.SinaWeiboUtil;
import net.iaround.share.tencent.qq.QQUtil;
import net.iaround.share.tencent.qqzone.QQZoneUtil;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.share.utils.ShareActionListener;
import net.iaround.share.wechat.group.IARWeixin;
import net.iaround.share.wechat.group.WechatGroupUtil;
import net.iaround.share.wechat.session.WechatSessionUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.DownLoadAPKThread;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.PathUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.UserFragment;
import net.iaround.ui.game.GameWebViewActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * 所有WebView的显示 (可用于显示点击底部广告Banner条打开的网页)
 */

public class WebViewAvtivity extends TitleActivity implements ShareActionListener, HttpCallBack {
    public static final String TAG = "WebViewActivity";
    public static final String WEBVIEW_URL = "url";
    public static final String WEBVIEW_TITLE = "title";
    public static final String WEBVIEW_NEW_SHRARE = "newShare";
    public static final String WEBVIEW_SHARE_SUBTYPE = "shareSubType";
    public static final String WEBVIEW_GET_PAGE_TITLE = "getPageTitle";
    public static final String IS_SHOW_CLOSE = "show_close_im";
    public static final String WEBVIEW_IGNORE_SSL_ERROR = "ignoreSSLError";


    private static long FOLLOW_USER_FLAG;//关注请求标识
    private static long UN_FOLLOW_USER_FLAG;//取消关注请求标识

    private LinearLayout mLlTitle;
    private WebView mWebview;
    private Dialog progessDialog;
    private String url = "";
    private String title = "";
    private boolean isShowCloseButton;
    private boolean getPageTitle = false;// 是否获取网页的标题

    //标题栏
    private ImageView ivLeft;
    private TextView tvLeft;

    private final static int DO_SETTITLE = 124;

    //Start---下载用变量和方法
    private boolean isDownloading = false;// 是否正在下载
    private boolean isDownErr = false;// 是否下载错误
    private int count = 1;// 用于log下载进度

    private boolean mIgnoreSSLError = false; //忽略SSL证书错误

    private boolean mIsPayComplete = false;//是否支付完成

    public static void launchVerifyCode(Context context, int type, String url) {
        Intent i = new Intent(context, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        context.startActivity(i);
    }

    public static void launchVerifyCode(Context context, String url) {
        Intent i = new Intent(context, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_load);
        mLlTitle = (LinearLayout) findViewById(R.id.ll_title);
        tvLeft = (TextView) findViewById(R.id.tv_left);

        url = getIntent().getStringExtra(WEBVIEW_URL);
        title = getIntent().getStringExtra(WEBVIEW_TITLE);
        isShowCloseButton = getIntent().getBooleanExtra(IS_SHOW_CLOSE, false);
        getPageTitle = getIntent().getBooleanExtra(WEBVIEW_GET_PAGE_TITLE, false);
        mIgnoreSSLError = getIntent().getBooleanExtra(WEBVIEW_IGNORE_SSL_ERROR, false);

        progessDialog = DialogUtil.getProgressDialog(this, "", mContext.getResources().getString(R.string.please_wait), null);

        CommonFunction.log("WebViewAvtivity", "webview url == " + url);

        if (CommonFunction.isEmptyOrNullStr(url)) {
            finish();
        }

        if (url.contains("gamechat_userdetail/details.html") || url.contains("gamchat_orders/orderDetails.html") || url.contains("gamchat_orders/comment.html")
                || url.contains("gamchat_orders/orderFrom.html") || url.contains("topUp")) {
            mLlTitle.setVisibility(View.GONE);
        }

        createNotification();

        if (!CommonFunction.isEmptyOrNullStr(title)) {
            ((TextView) findViewById(R.id.tv_title)).setText(title);
        } else if (!isShowCloseButton) {
            getPageTitle = true;
        }

        ivLeft = (ImageView) findViewById(R.id.iv_left);
        ivLeft.setImageResource(R.drawable.title_back);
        findViewById(R.id.iv_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        tvLeft.setVisibility(View.VISIBLE);

        mWebview = (WebView) findViewById(R.id.webview);

        mWebview.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.requestFocus();
                }
                return false;
            }
        });
        mWebview.requestFocus();
        mWebview.setVisibility(View.VISIBLE);
        mWebview.getSettings().setPluginState(PluginState.ON);
        mWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.addJavascriptInterface(this, "callOjbcMethodInIos");// 添加接口给js，使他们可以调用我们的代码
        mWebview.getSettings().setSupportZoom(false);
        mWebview.getSettings().setBuiltInZoomControls(false);
        mWebview.setDownloadListener(new DownloadListenerImpl(this));
        mWebview.setWebViewClient(new WebViewClientImpl(this));
        mWebview.setWebChromeClient(new WebChromeClientImpl(this));
        mWebview.loadUrl(url);

    }

    static class WebChromeClientImpl extends WebChromeClient {
        private WeakReference<WebViewAvtivity> mActivity;

        public WebChromeClientImpl(WebViewAvtivity activity) {
            mActivity = new WeakReference<WebViewAvtivity>(activity);
        }

        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            WebViewAvtivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return;
            }
            if (newProgress > 0 && newProgress < 100) {
                if (activity.progessDialog != null && !activity.progessDialog.isShowing())
                    activity.progessDialog.show();
            } else {
                if (activity.progessDialog != null && activity.progessDialog.isShowing()) {
                    activity.progessDialog.hide();
                }
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            WebViewAvtivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return;
            }
            ((TextView) activity.findViewById(R.id.tv_title)).setText(title);
        }
    }

    static class DownloadListenerImpl implements DownloadListener {
        private WeakReference<WebViewAvtivity> mActivity;

        public DownloadListenerImpl(WebViewAvtivity activity) {
            mActivity = new WeakReference<WebViewAvtivity>(activity);
        }

        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            WebViewAvtivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return;
            }
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        }
    }

    class WebViewClientImpl extends WebViewClient {
        private WeakReference<WebViewAvtivity> mActivity;

        public WebViewClientImpl(WebViewAvtivity activity) {
            mActivity = new WeakReference<WebViewAvtivity>(activity);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            CommonFunction.log(TAG, "shouldOverrideUrlLoading() into, url=" + url);
            WebViewAvtivity activity = mActivity.get();
            if (CommonFunction.activityIsDestroyed(activity)) {
                return true;
            }
            if (activity.mWebview == null) {
                return true;
            }
            if (url.contains("http://") && url.contains("gamecenter")) {// 游戏中心的url
                GameWebViewActivity.launchGameCenter(activity, url);
            } else if (url.contains("http://") && url.contains(".apk")) {// 下载的连接
                activity.startDownLoad(url);
            } else if (url.contains("http://") || url.contains("https://")) {
                if (url.equals("http://www.baidu.com/") | url.equals("http://www.google.cn/")) {
                    activity.finish();
                }
                //微信支付H5界面需要的参数
                HashMap<String, String> map = new HashMap<>();
                map.put("Referer", "http://pay.iaround.com");
                // 内部浏览器跳转
                activity.mWebview.loadUrl(url, map);
            } else if (url.startsWith("iaround")) {
                InnerJump.Jump(activity, url, false);
            } else if (url.contains("callOjbcMethodInIos")) {
                //和Js交互
                String[] urls = url.split("/");
                CommonFunction.log(TAG, "String[] urls==" + urls.length);
                if (urls.length == 1) {

                } else if (urls.length == 4) {
                    String localfuncStr = urls[3];
                    if (localfuncStr.equals("getKey1") || localfuncStr.equals("getKey") || localfuncStr.equals("getKey2")) {
                        activity.mWebview.loadUrl("javascript:" + localfuncStr + "('" + ConnectorManage.getInstance(BaseApplication.appContext).getKey() + "')");
                    } else if (localfuncStr.equals("setUid")) {
                        activity.mWebview.loadUrl("javascript:" + localfuncStr + "('" + Common.getInstance().loginUser.getUid() + "')");
                    }
                } else if (urls.length == 5) {
                    String localMethodStr = urls[3];
                    String localValueStr = urls[4];
                    activity.mWebview.loadUrl("javascript:localfuncStr('" + ConnectorManage.getInstance(BaseApplication.appContext).getKey() + "')");
                    activity.mWebview.loadUrl("javascript:" + localMethodStr + "('" + ConnectorManage.getInstance(BaseApplication.appContext).getKey() + "','" + localValueStr + "')");
                }
            } else {
                // 其他链接时，唤醒第三方APP
                openApp(url);
                CommonFunction.log("WebViewAvtivity", "webViewActivity unknow url == " + url);
            }
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            CommonFunction.log(TAG, "onReceivedSslError() into, url=" + url);
            if (mIgnoreSSLError) {
                handler.proceed(); //忽略证书错误
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }
    }

    //判断app是否安装
    private boolean isInstall(Intent intent) {
        return BaseApplication.appContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    //打开app
    private boolean openApp(String url) {
        if (TextUtils.isEmpty(url)) return false;
        try {
            if (!url.startsWith("http") || !url.startsWith("https") || !url.startsWith("ftp")) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                if (isInstall(intent)) {
                    startActivity(intent);
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsPayComplete) {
                finish();
                return super.onKeyDown(keyCode, event);
            }
            if (mWebview.canGoBack()) {
                mWebview.goBack();
                return !super.onKeyDown(keyCode, event);
            } else {
                back();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 评价h5 点击提交按钮
     * js 调取Android原生 实现finish  无参数
     */
    @JavascriptInterface
    public void returnDetails() {
//        "A WebView method was called on thread '" +
//                Thread.currentThread().getName() + "'. " +
//                "All WebView methods must be called on the same thread. " +
//                "(Expected Looper " + mWebViewThread + " called on " + Looper.myLooper() +
//                ", FYI main Looper is " + Looper.getMainLooper() + ")"
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onKeyDown(KeyEvent.KEYCODE_BACK,new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
            }
        });
    }

    /**
     * 评价h5 点击提交按钮
     * js 调取Android原生 实现finish  有参数   目前此方法没有用
     */
    @JavascriptInterface
    public void returnDetails(String str) {
        CommonFunction.log(TAG, "str==" + str);
        if (str.equals("comment")) {
            finish();
        }
    }

    /**
     * h5跳转申请入驻
     */
    @JavascriptInterface
    public void goQualification(){
        Intent disturbIntent2 = new Intent(WebViewAvtivity.this, ApplyQualificationActivity.class);
        startActivity(disturbIntent2);
    }

    private void back() {
        if (mWebview.canGoBack()) {
            mWebview.goBack();
        } else {
            int openMiguRead = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(UserFragment.OPEN_MIGU_READ);
            String openMiguReadUrl = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getString(UserFragment.OPEN_MIGU_READ_URL);
            if (openMiguRead == 1 && openMiguReadUrl != null) {
                CloseAllActivity.getInstance().closeExcept(MainFragmentActivity.class);
            }
            EventBus.getDefault().post("updateCoin");
            finish();
        }
    }

    @Override
    public void finish() {
        try {
            if (mWebview != null) {
                CommonFunction.hideInputMethod(mContext, mWebview);
            }
            setResult(0); //聊吧页面
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onPause() {
        super.onPause();

        mWebview.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        mWebview.onResume();
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy();
            mWebview.stopLoading();
            mWebview.loadData("<a></a>", "text/html", "utf-8");
            mWebview.setDownloadListener(null);
            mWebview.setWebChromeClient(null);
            mWebview.setWebViewClient(null);
            mWebview.removeJavascriptInterface("callOjbcMethodInIos");
            mWebview = null;
            if (progessDialog != null) {
                progessDialog.cancel();
                progessDialog = null;
            }
            if (notification != null) {
                notification = null;
                contentIntent = null;
                contentView = null;
                mNotificationManager = null;
            }
        } catch (Exception e) {
            CommonFunction.log(TAG, "onDestroy() error happen");
            e.printStackTrace();
        }
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).remove(UserFragment.OPEN_MIGU_READ);
        SharedPreferenceUtil.getInstance(BaseApplication.appContext).remove(UserFragment.OPEN_MIGU_READ_URL);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    static class ApkDownloadFileCallback implements DownloadFileCallback {
        private WeakReference<WebViewAvtivity> mActivity;
        private int taskFlag;

        public ApkDownloadFileCallback(WebViewAvtivity activity, int flag) {
            mActivity = new WeakReference<WebViewAvtivity>(activity);
            taskFlag = flag;
        }

        @Override
        public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag) {
            final WebViewAvtivity activity = mActivity.get();
            if (null == activity) return;
            if (LengthOfDownloaded > 1024 * 1024 * activity.count) {
                CommonFunction.log("WebViewAvtivity", flag + " downLoad == " + LengthOfDownloaded + "/" + lenghtOfFile);
                activity.count++;

                final long currentLen = LengthOfDownloaded;
                final long totalLen = lenghtOfFile;
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        activity.updateNotification(taskFlag, currentLen, totalLen);
                    }
                });
            }
        }

        @Override
        public void onDownloadFileFinish(int flag, String fileName, String savePath) {
            final WebViewAvtivity activity = mActivity.get();
            if (null == activity)
                return;
            if (!activity.isDownErr) {
                activity.isDownloading = false;
                activity.installApp(activity, fileName);
            }
            activity.hideNotification(taskFlag);
        }

        @Override
        public void onDownloadFileError(int flag, String fileName, String savePath) {
            final WebViewAvtivity activity = mActivity.get();
            if (null == activity)
                return;
            activity.isDownErr = true;
            activity.isDownloading = false;
            CommonFunction.log("WebViewAvtivity", "WebViewAvtivity  onDownloadFileError");
            Message msg = new Message();
            msg.what = 1;
            activity.mHandler.sendMessage(msg);
            activity.hideNotification(taskFlag);
        }
    }

    /**
     * 启动线程下载应用，只支持单个任务下载
     */
    private void startDownLoad(String url) {
        if (!isDownloading) {

            final int taskFlag = url.hashCode();
            showNotification(taskFlag);
            DownloadFileCallback callback = new ApkDownloadFileCallback(this, taskFlag);
            DownLoadAPKThread thread = new DownLoadAPKThread(BaseApplication.appContext, url, callback);
            thread.start();
            isDownloading = true;
            isDownErr = false;
            CommonFunction.toastMsg(mContext, R.string.download_new_version_ticker_txt);
        } else {
            CommonFunction.toastMsg(mContext, R.string.download_apk_file);
        }
    }

    /**
     * 安装应用
     */
    private void installApp(Context context, String filePath) {
        if (TextUtils.isEmpty(filePath) || context == null) {
            return;
        }

        String dir = PathUtil.getAPKCacheDir();
        String[] args1 = {"chmod", "705", dir};
        String[] args2 = {"chmod", "604", dir + filePath};
        CommonFunction.log(TAG, "下载完后，立刻安装-------------------installApp =" + dir + filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName()
                    + ".fileprovider", new File(dir + filePath));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

        } else {
            intent.setDataAndType(Uri.fromFile(new File(dir + filePath)), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }

    @android.webkit.JavascriptInterface
    public void jsSetTitle(String title) {
        CommonFunction.log(TAG, "js call set title == " + title);
        Message msg = new Message();
        msg.what = DO_SETTITLE;
        msg.obj = title;
        mHandler.sendMessage(msg);
    }

    public void click(View v) {
        CommonFunction.log("TAG", "click() view=" + v);
    }


    static class WebViewHandler extends Handler {
        private WeakReference<WebViewAvtivity> mActivity;

        public WebViewHandler(WebViewAvtivity activity) {
            mActivity = new WeakReference<WebViewAvtivity>(activity);
        }

        public void handleMessage(Message msg) {
            WebViewAvtivity activity = mActivity.get();
            if (null == activity) {
                return;
            }
            switch (msg.what) {
                case DO_SETTITLE: {
                    String title = msg.obj.toString();
                    CommonFunction.log("WebViewAvtivity", "handleMessage() title == " + title);
                    ((TextView) activity.findViewById(R.id.title_name)).setText(title);
                }
                break;
                default:
                    break;
            }
        }
    }

    private Handler mHandler = new WebViewHandler(this);

    private NotificationManager mNotificationManager;
    private RemoteViews contentView;
    private PendingIntent contentIntent;
    private Notification notification;

    // 创建通知
    private void createNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        contentView = new RemoteViews(getPackageName(), R.layout.notify_download_progress);
        contentView.setTextViewText(R.id.title, getString(R.string.download_new_version_tip));

        Intent notificationIntent = new Intent();
        contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String tickerText = mContext.getString(R.string.download_new_version_ticker_txt);
        int icon = android.R.drawable.stat_sys_download;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon).setTicker(tickerText).setWhen(System.currentTimeMillis());
        notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
    }

    private void showNotification(int id) {
        contentView.setTextViewText(R.id.percentTip, "0%");
        contentView.setProgressBar(R.id.progressBar, 100, 0, false);
        notification.contentView = contentView;
        notification.contentIntent = contentIntent;
        mNotificationManager.notify(id, notification);
    }

    private void updateNotification(int id, long currentLen, long totalLen) {

        double pro1 = ((double) currentLen) / ((double) totalLen);
        int pro = (int) (pro1 * 100);
        if (contentView == null) return;
        contentView.setTextViewText(R.id.percentTip, pro + "%");
        contentView.setProgressBar(R.id.progressBar, 100, pro, false);
        mNotificationManager.notify(id, notification);
    }

    private void hideNotification(int id) {
        mNotificationManager.cancel(id);
    }


    /**
     * JS调取Android原生获取当前应用版本号
     *
     * @return
     */
    @JavascriptInterface
    public String jsLocalInfo() {
        return Config.APP_VERSION;
    }

    /**
     * JS调取Android原生获取当前用户id
     *
     * @return
     */
    @JavascriptInterface
    public String jsLocalUid() {
        return "" + Common.getInstance().loginUser.getUid();
    }

    /**
     * JS调取Android原生获取当前Key
     *
     * @return
     */
    @JavascriptInterface
    public String jsLocalKey() {
        return "" + ConnectorManage.getInstance(this).getKey();
    }


    /*js分享图片到微信
    * toWhere 1 微信好友 2 微信朋友圈 3 微博 4 qq好友 5 qq空间
    * pictureUrl 图片URL，服务上存在
    * js调用native
    * WeChat: window.callOjbcMethodInIos.sharePicture(1,"http://statics.iaround.com/icon/share.jpg?user=123456");
    * IOS: window.webkit.messageHandlers.sharePicture.postMessage({'type', 1; 'url': 'http://statics.iaround.com/icon/share.jpg?user=123456'});
    * */
    private boolean mSharing = false;

    @JavascriptInterface
    public int sharePicture(int toWhere, String pictureUrl) {
        CommonFunction.log(TAG, "sharePictureToWechat() pictureUrl=" + pictureUrl);
        if (true == mSharing) {
            CommonFunction.log(TAG, "sharePictureToWechat() sharing...");
            return -1;
        }

        if (1 == toWhere) {
            IARWeixin wechat = IARWeixin.getInstance();
            wechat.initApi(this.mContext, "wx0e4a408c5839e9c4"); //wx3a3e1883deda3c59
            if (false == wechat.isAvailable()) {
                CommonFunction.log(TAG, "sharePicture() no wechat app");
                return -2;
            }
            WechatSessionUtil share = (WechatSessionUtil) AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(), 26);
            if (null == share) {
                CommonFunction.log(TAG, "sharePicture() no wechat util");
                return -3;
            }
            mSharing = true;
            share.setShareActionListener(this);
            share.share2Weibo((Activity) mContext, null, null, null, null, pictureUrl);
        } else if (2 == toWhere) {
            IARWeixin wechat = IARWeixin.getInstance();
            wechat.initApi(this.mContext, "wx0e4a408c5839e9c4"); //wx3a3e1883deda3c59
            if (false == wechat.isAvailable()) {
                CommonFunction.log(TAG, "sharePicture() no wechat app");
                return -2;
            }
            WechatGroupUtil share = (WechatGroupUtil) AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(), 27);
            if (null == share) {
                return -3;
            }
            mSharing = true;
            share.setShareActionListener(this);
            share.share2Weibo((Activity) mContext, null, null, null, null, pictureUrl);
        } else if (3 == toWhere) {
            WeiboAppManager.WeiboInfo weibo = WeiboAppManager.getInstance(mContext).getWeiboInfo();
            if (weibo != null && weibo.isLegal() == false) {
                return -2;
            }
            SinaWeiboUtil share = (SinaWeiboUtil) AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(), 12);
            if (null == share) {
                return -3;
            }
            mSharing = true;
            share.setShareActionListener(this);
            share.share2Weibo((Activity) mContext, mContext.getString(R.string.share_title), mContext.getString(R.string.share_content), null, null, pictureUrl);
        } else if (4 == toWhere) {
            Tencent tencent = Tencent.createInstance("100297231", mContext);
            if (null != tencent && false == tencent.isSupportSSOLogin((Activity) mContext)) {
                return -2;
            }
            QQUtil share = (QQUtil) AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(), 28);
            if (null == share) {
                return -3;
            }
            mSharing = true;
            share.setShareActionListener(this);
            share.share2Weibo((Activity) mContext, null, null, null, null, pictureUrl);
        } else if (5 == toWhere) {
            Tencent tencent = Tencent.createInstance("100297231", mContext);
            if (null != tencent && false == tencent.isSupportSSOLogin((Activity) mContext)) {
                return -2;
            }
            QQZoneUtil share = (QQZoneUtil) AbstractShareUtils.getSingleShareUtil(mContext, Common.getInstance().loginUser.getUid(), 25);
            if (null == share) {
                return -3;
            }
            mSharing = true;
            share.setShareActionListener(this);
            share.share2Weibo((Activity) mContext, mContext.getString(R.string.share_title), mContext.getString(R.string.share_content), "http://notice.iaround.com/share/index.html", null, pictureUrl);
        } else {
            return -4;
        }

        return 1;
    }


    /**
     * Js调原生
     * h5 中的 关注/取消关注接口
     * long targetId, int type
     *
     * @param
     * @param
     */
    @JavascriptInterface
    public void userFanLoveOrDislike(String json) {
//        targetId 被关注/取消关注用户ID
//        type     0-关注 1-取消关注
        CommonFunction.log("tag", "json==" + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject == null || jsonObject.isNull("target_uid") || jsonObject.isNull("type")) {
                return;
            }
            long target_uid = jsonObject.getLong("target_uid");
            int type = jsonObject.getInt("type");
            CommonFunction.log("tag", "target_uid==" + target_uid + "==type==" + type);
            showWaitDialog();
            if (type == 0) {
                FOLLOW_USER_FLAG = FriendHttpProtocol.userFanLove(this, target_uid, 0, 0, this);
            } else {
                UN_FOLLOW_USER_FLAG = FriendHttpProtocol.userFanDislike(this, target_uid, this);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Js调Android 代码    并返回给android数据
     * h5点击私聊返回用户信息
     *
     * @param info
     */
    @JavascriptInterface
    public void returnUserChat(String info) {

        CommonFunction.log("tag", "returnUserChat==" + info);
        if (CommonFunction.isEmptyOrNullStr(info))
            return;

        User user = GsonUtil.getInstance().getServerBean(info, User.class);
        long uid = user.getUid();
        if (Common.getInstance().loginUser.getUid() == uid)
            return;
        ChatPersonal.skipToChatPersonal(this, user, 0);
    }

    /**
     * Js调Android代码 支付完成时调用此方法
     */
    @JavascriptInterface
    public void setPayComplete() {
        mIsPayComplete = true;
    }

    /*分享回调接口
    * */
    @Override
    public void onComplete(AbstractShareUtils abstractShareUtils, int action, Map<String, Object> map) {
        CommonFunction.log(TAG, "onComplete() into, action=" + action);
        if (action == AbstractShareUtils.ACTION_UPLOADING) {
            mSharing = false;
            CommonFunction.showToast(mContext, mContext.getString(R.string.share_success), Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onError(AbstractShareUtils abstractShareUtils, int action, Throwable throwable) {
        CommonFunction.log(TAG, "onError() into, action=" + action);
        mSharing = false;
        CommonFunction.showToast(mContext, mContext.getString(R.string.share_fail), Toast.LENGTH_SHORT);
    }

    @Override
    public void onCancel(AbstractShareUtils abstractShareUtils, int action) {
        CommonFunction.log(TAG, "onCancel() into, action=" + action);
        mSharing = false;
    }

    //关注/取消关注的回调
    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        BaseServerBean serverBean = GsonUtil.getInstance().getServerBean(result, BaseServerBean.class);
        if (serverBean.isSuccess()) {
            //function returnAttention(res) res 0-取消关注成功 1-关注成功
            if (FOLLOW_USER_FLAG == flag) {
                mWebview.loadUrl("javascript:returnAttention(" + 1 + ")");//1-关注成功
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.other_info_follow);
            } else if (UN_FOLLOW_USER_FLAG == flag) {
                mWebview.loadUrl("javascript:returnAttention(" + 0 + ")");//0-取消关注成功
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.other_info_cancle_follow);

            }
        } else {
            ErrorCode.showError(BaseApplication.appContext, result);
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }
}
