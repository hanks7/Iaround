package net.iaround.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;
import com.inmobi.ads.listeners.NativeAdEventListener;

import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.R;
import net.iaround.annotation.IAroundAD;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BaseHttp;
import net.iaround.connector.ConnectLogin;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.GtAppDlgTask;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.InitHttpProtocol;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.connector.protocol.SignClickHttpProtocol;
import net.iaround.contract.SplashContract;
import net.iaround.model.database.RegisterModel;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.InitBean;
import net.iaround.model.entity.Item;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.presenter.SplashPresenter;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.OkhttpDownload;
import net.iaround.tools.OkhttpDownloadStatus;
import net.iaround.tools.PathUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.dynamic.NotificationFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * Class: 启动页
 * Author：gh
 * Date: 2016/11/26 21:37
 * Emial：jt_gaohang@163.com
 */
public class SplashActivity extends BaseActivity implements SplashContract.View, HttpCallBack, View.OnClickListener {
    private static final String TAG = "SplashActivity";

    private SplashContract.Presenter mPresenter;

    private LinearLayout llloading;
    private RelativeLayout adParent;
    private RelativeLayout mStartLoginBg;
    private ImageView startBackground;//默认背景
    private ImageView inmobiAd;
    private TextView startBtn;

    private final static int LOGIN_SUCCES = 1001;
    private final static int LOGIN_ERROR = 1002;
    private final static int SKIP_LOGIN = 1003;
    private final static int SHOW_WEB_VIEW_AD = 4000;
    private final static int SHOW_LINGJI_AD = 7001; //灵集广告点击的requestcode

    private long loadFlag;
    protected long loginFlag;

    private SharedPreferenceUtil spUtil;
    private InitBean startADBean;

    private Object splashADListener; //com.qq.e.ads.splash.SplashADListener

    private boolean isAdImageFileExist;
    /*是否点击了广告链接*/
    private boolean isClickADView = false;

    private boolean isAutoLogin;

    public boolean canJump = false;
    public boolean canInMobiJump = false;
    private boolean permissonNo = false;

    private Object nativeAd; //com.inmobi.ads.InMobiNative

    /**
     * 登录类型
     */
    protected int mLoginType = -1;

    protected String mVerifyCode = "";
    private int startUpCount;

    private GtAppDlgTask gtAppDlgTask;//极验弹框

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunction.log(TAG, "onCreate() into");
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);

        initView();

        ///加密解密协议号
        spUtil = SharedPreferenceUtil.getInstance(getApplicationContext());
        spUtil.putString(spUtil.FLAG_NET, "021");

        ///请求权限
        requestMainActivity2Permission();

        mPresenter = new SplashPresenter();

        CommonFunction.log(TAG, "onCreate() out");
    }

    @Override
    public void initView() {
        startBackground = (ImageView) findViewById(R.id.iv_start_background);
        inmobiAd = (ImageView) findViewById(R.id.inmobi_ad);
        adParent = (RelativeLayout) findViewById(R.id.splashId);
        mStartLoginBg = (RelativeLayout) findViewById(R.id.start_login_bg);

        llloading = (LinearLayout) findViewById(R.id.llloading);
        startBtn = (TextView) findViewById(R.id.start_relogin_btn);
        startBtn.setOnClickListener(this);
        ProgressBar pgbConnecting = new ProgressBar(this);
        pgbConnecting.setIndeterminate(true);
        int dp_24 = (int) (getResources().getDimension(R.dimen.dp_1) * 24);
        LinearLayout.LayoutParams lpPb = new LinearLayout.LayoutParams(dp_24, dp_24);
        pgbConnecting.setLayoutParams(lpPb);
        pgbConnecting.setIndeterminateDrawable(getResources().getDrawable(R.drawable.pull_ref_pb));
        llloading.addView(pgbConnecting);
    }

    private void statisticsCount() {
        startUpCount = SharedPreferenceCache.getInstance(SplashActivity.this).getInt("startUpCount", 0);
        startUpCount++;
        SignClickHttpProtocol.getInstance().syncGetStatistics(SplashActivity.this, "7", "7", "" + startUpCount, "0", null);
        SharedPreferenceCache.getInstance(SplashActivity.this).putInt("startUpCount", startUpCount);
        SharedPreferenceCache.getInstance(SplashActivity.this).putBoolean("splashIsShow", true);
    }

    private void initAdvertDate() {
        ///是否展示广告，后端控制
        startADBean = GsonUtil.getInstance().getServerBean(spUtil.getString(spUtil.START_PAGE_AD), InitBean.class);

        String avaliable = spUtil.getString(spUtil.START_PAGE_AD);

        InitBean available = GsonUtil.getInstance().getServerBean(avaliable, InitBean.class);
        if (available != null && available.isSuccess()) {
            Config.isShowThridSplashAd = TextUtils.isEmpty(startADBean.url);
        }

        startBtn.setVisibility(View.GONE);
        int delayTime = 500;
        if (isAdImageFileExist) {
            delayTime = 3000;
        }

        startBackground.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (startADBean != null && startADBean.isSuccess()) {
                    String url = startADBean.url;
                    if (url != null & !TextUtils.isEmpty(url)) {
                        if (PathUtil.isExistMD5UserCacheFile(url)) {
                            startBtn.setVisibility(View.VISIBLE);
                            isAdImageFileExist = true;
                            startBackground.setTag(R.id.splash_ad, startADBean);
                            startBackground.setOnClickListener(SplashActivity.this);
                        }
                        ///自己的图片广告，老代码，已经不使用
                        initAdImage(url, 1);
                    } else {
                        if (Config.isShowThridSplashAd) {
                            if (BuildConfig.showAdvert == false) {
                                loginAppType();
                                return;
                            }
                            String adTypeControl = SharedPreferenceUtil.getInstance(SplashActivity.this).getString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL);
                            if (TextUtils.isEmpty(adTypeControl)) {
                                loginAppType();
                                return;
                            }
                            /**
                             * 控制展示广告的类型
                             * 启动广告:附近:动态
                             * 广告类型:广告类型,位置,广告类型,位置:广告类型,位置,广告类型,位置
                             * 1:1,4,2,13:1,2,2,10
                             * 广告类型：1、广点通  2、inmobi
                             */
                            ///附近，动态涉及展示位置；启动页不涉及
                            String[] split = adTypeControl.split(":");
                            if ("1".equals(split[0])) {
                                if (Config.isShowGoogleApp) { //谷歌渠道不能显示广告
                                    loginAppType();
                                } else {
                                    try {
                                        if (CommonFunction.updateAdCount(TAG)) {
                                            initGdTAd();
                                        } else {
                                            loginAppType();
                                        }
                                    } catch (ExceptionInInitializerError e) {
                                        e.printStackTrace();
                                        loginAppType();
                                    }
                                }
                            } else if ("2".equals(split[0])) {
                                isAdImageFileExist = true;
                                if (CommonFunction.updateAdCount(TAG)) {
                                    loadInmobiSplashAd();
                                } else {
                                    loginAppType();
                                }
                            } else {
                                loginAppType();
                            }
                            return;
                        }
                    }

                } else {
                    loginAppType();
                }

            }
        }, delayTime);
    }

    private void loadInmobiSplashAd() {
        loadInMobi();
    }

    /**
     * @param url
     * @param type 0、无意义     1、广点通    2、inmobi
     */
    private void initAdImage(String url, int type) {
        CommonFunction.log(TAG, "initAdImage into");

        int delayTime = 500;
        if (isAdImageFileExist) {
            delayTime = 4000;
        }

        startBackground.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isClickADView) {
                    loginAppType();
                }
            }
        }, delayTime);

        inmobiAd.setVisibility(View.VISIBLE);
        GlideUtil.loadImageDefault(BaseApplication.appContext, url, new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, com.bumptech.glide.request.target.Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                startBtn.setVisibility(View.VISIBLE);
                return false;
            }
        }, inmobiAd);

    }

    /**
     * 广点通
     */
    @IAroundAD
    private void initGdTAd() {
        if (BuildConfig.showAdvert == true) {
            CommonFunction.log(TAG, "initGdTAd into");
            splashADListener = new com.qq.e.ads.splash.SplashADListener() {
                @Override
                public void onADDismissed() {
                    CommonFunction.log(TAG, "gdt ad dismissed");
                    next();
                }

                @Override
                public void onNoAD(com.qq.e.comm.util.AdError adError) {
                    CommonFunction.log(TAG, "gdt ad failed " + adError.getErrorCode());
                    loginAppType();
                }

                @Override
                public void onADPresent() {
                    startBackground.setVisibility(View.GONE);
                    CommonFunction.log(TAG, "gdt ad present");
                }

                @Override
                public void onADClicked() {
                    CommonFunction.log(TAG, "gdt ad click");
                }

                @Override
                public void onADTick(long l) {
                    CommonFunction.log(TAG, "gdt ad onADTick");
                }

            };

            new com.qq.e.ads.splash.SplashAD(this, adParent, Config.GDT_APPID, Config.GDT_SPLASH_POST_ID, (com.qq.e.ads.splash.SplashADListener) splashADListener, 3000);
            startBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void next() {

        if (canJump) {
            loginAppType();
        } else {
            canJump = true;
        }
    }

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    private void nextInMobi() {

        if (canInMobiJump) {
            loginAppType();
        } else {
            canInMobiJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseInMobi();
        if (permissonNo) {
            canJump = false;
            canInMobiJump = true;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        resumeInMobi();
        if (permissonNo) {
            if (canJump) {
                next();
            }
            canJump = true;
            if (canInMobiJump) {
                nextInMobi();
            }
            canInMobiJump = true;
        }

    }

    public void loginAppType() {
        // TODO Auto-generated method stub
        int type = BaseHttp.checkNetworkType(getApplicationContext());
        if (type == BaseHttp.TYPE_NET_WORK_DISABLED) {
            // 无法连接网络

        }

        if (type == BaseHttp.TYPE_CM_CU_WAP || type == BaseHttp.TYPE_CT_WAP) {
            // 用户使用了WAP联网
            Toast.makeText(this, R.string.setting_wap, Toast.LENGTH_LONG).show();
        }

        int loginType = spUtil.getInt(spUtil.LOGIN_TYPE);
        String result = spUtil.getString(spUtil.LOGIN_SUCCESS_DATA);
        CommonFunction.log("SplashActivity", "自动登录用户信息:" + result);

        switch (loginType) {
            case 0: {
                // 上一次退出，使用注销功能
                // (handler发送可以有延时效果)
                // autoLoginFail();
                //CommonFunction.log("Other", "zhu");
                if (mHandler != null)
                    mHandler.sendMessage(mHandler.obtainMessage(LOGIN_ERROR));
            }
            break;
            case 6: { // 遇见帐号登录

                String account = spUtil.getString(spUtil.LOGIN_ACCOUNT);

                String password = spUtil.getString(spUtil.LOGIN_PASSWORD);

                boolean isAutoLogin = SpaceModel.getInstance(this).isAutoLogin();
                if (!isAutoLogin || CommonFunction.isEmptyOrNullStr(account) ||
                        CommonFunction.isEmptyOrNullStr(password)) {
                    // 用户名或密码不存在或者不是自动登录
                    // autoLoginFail();
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(SKIP_LOGIN), 500);
                } else {
                    //					mLoginType = ThirdPartyLoginHelper.TYPE_IAROUND;
                    //					mHandler.sendMessageDelayed( mHandler.obtainMessage( LOGIN_SUCCES ) , 500 );
                    loginFlag = LoginHttpProtocol
                            .doIAroundLogin(this, account, password, mVerifyCode, this);
                    CommonFunction.log("hanggao", TAG + "00000000000000000000000");
                    if (loginFlag == -1) {
                        autoLoginFail();
                    }
                    //					autoLoginFail();

                }
            }
            break;

            case 1: { // QQ登录
                String qqOpenId = spUtil.getString(spUtil.LOGIN_ID_QQ);
                String qqToken = spUtil.getString(spUtil.LOGIN_TOKEN_QQ);

                try {
                    mLoginType = 1;
                    loginFlag = RegisterModel.getInstance().LoginReq(this, qqToken, qqOpenId, mVerifyCode, this, RegisterModel.RegisterModelReqTypes.QQ_LOGIN, null);
                } catch (Exception e) {
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
                }
            }
            break;
            case 2: { // 新浪微博登录
                String wOpenId = spUtil.getString(spUtil.LOGIN_ID_SINA);
                String wToken = spUtil.getString(spUtil.LOGIN_TOKEN_SINA);

                try {
                    mLoginType = 2;
                    loginFlag = RegisterModel.getInstance().LoginReq(this, wToken, wOpenId, mVerifyCode, this, RegisterModel.RegisterModelReqTypes.WEIBO_LOGIN, null);
                } catch (Exception e) {
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
                }
            }
            break;
            case 4: { // facebook
                String fbOpenId = spUtil.getString(spUtil.LOGIN_ID_FACEBOOK);
                String fbToken = spUtil.getString(spUtil.LOGIN_TOKEN_FACEBOOK);
                try {
                    mLoginType = 4;
                    loginFlag = RegisterModel.getInstance().LoginReq(this, fbToken, fbOpenId, mVerifyCode, this, RegisterModel.RegisterModelReqTypes.FACEBOOK_LOGIN, null);
                } catch (Exception e) {
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
                    // autoLoginFail();
                }
            }
            break;
            case 7: {
                String wxOpenId = spUtil.getString(spUtil.LOGIN_ID_WECHAT);
                String wxToken = spUtil.getString(spUtil.LOGIN_TOKEN_WECHAT);
                String wxUnionId = spUtil.getString(spUtil.LOGIN_UNIONID_WECHAT);
                try {
                    mLoginType = 7;
                    loginFlag = RegisterModel.getInstance().LoginReq(this, wxToken, wxOpenId, mVerifyCode, this, RegisterModel.RegisterModelReqTypes.WECHAT_LOGIN, wxUnionId);
                } catch (Exception e) {
                    // autoLoginFail();
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
                }
            }

            break;
            default:
                // autoLoginFail();
                if (mHandler != null)
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
        }
    }

    private void startMainActivity() {

        initInmobiAd(); ///初始化 Inmobi Ad

        isAutoLogin = SpaceModel.getInstance(this).isAutoLogin();

        if (isAutoLogin) {
            // 统计事件用，先保存首次启动时间
            if (!spUtil.contains(spUtil.IAROUND_FIRST_START)) {
                spUtil.putLong(spUtil.IAROUND_FIRST_START, System.currentTimeMillis());
            }
        } else {
            ///可能不使用了
            LoginHttpProtocol.softwareActivate(this, this);// 请求激活设备
        }

        ///system load接口
        loadFlag = InitHttpProtocol.doInit(this, this);

        GeoData geo = LocationUtil.getCurrentGeo(this);
        if (geo != null) {
            if (geo.getLat() == 0 || geo.getLng() == 0) {
                //传this对象会导致activity泄漏
                new LocationUtil(BaseApplication.appContext).startListener(null, 1);
            }
        } else {
            //传this对象会导致activity泄漏
            new LocationUtil(BaseApplication.appContext).startListener(null, 1);
        }

        initAdvertDate(); ///控制广告的显示和类型位置
    }

    @IAroundAD
    public void initInmobiAd() {
        if (BuildConfig.showAdvert == true) {
            com.inmobi.sdk.InMobiSdk.init(this, Config.INMOBI_APPID);
            com.inmobi.sdk.InMobiSdk.setLogLevel(com.inmobi.sdk.InMobiSdk.LogLevel.DEBUG);
            com.inmobi.sdk.InMobiSdk.setLocation(new LocationUtil(BaseApplication.appContext).getLocation());
            com.inmobi.sdk.InMobiSdk.setGender(com.inmobi.sdk.InMobiSdk.Gender.MALE);
            com.inmobi.sdk.InMobiSdk.setAge(25);
        }
    }

    /**
     * 屏蔽物理返回按钮
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (loginFlag == flag) {
            LoginResponseBean entity = GsonUtil.getInstance().getServerBean(result, LoginResponseBean.class);
            if (entity != null && entity.isSuccess()) {
                synchronized (SplashActivity.this) {
                    Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
                    if (item != null && entity != null) {
                        entity.setItem(item);

                    }

                    entity.setUrl();
                    entity.loginSuccess(SplashActivity.this);
                    SharedPreferenceUtil.getInstance(SplashActivity.this).putString(SharedPreferenceUtil.ACCESS_TOKEN, entity.key);
                    SharedPreferenceUtil.getInstance(SplashActivity.this).putString(SharedPreferenceUtil.USER_ID, "" + entity.user.userid);
                    SharedPreferenceUtil.getInstance(SplashActivity.this).putString(SharedPreferenceUtil.USER_AVATAR, entity.user.icon);
                    //用户登陆成功才能发送app启动统计
                    Statistics.onAppCreated();
                    if (mHandler != null)
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_SUCCES, result), 0);
                }
            } else if (entity != null && entity.error == 8202) {
//                if (Common.getInstance().getGeetestSwitch() == 1){
//                    gtAppDlgTask.show();
//                }else{
//                    if (mHandler != null)
//                        mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
//                }
                //自动登陆时由登陆接口返回 8202错误码表示需要弹极验
                Common.getInstance().setGeetestSwitch(1);
                gtAppDlgTask.show();
            } else {
                if (mHandler != null)
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
            }
        } else if (loadFlag == flag) {
            ///system load 接口返回
            InitBean bean = GsonUtil.getInstance().getServerBean(result, InitBean.class);
            if (bean != null && bean.isSuccess()) {
                bean.setActiveUrl();
                SharedPreferenceUtil shareDate = SharedPreferenceUtil.getInstance(this);
                shareDate.putString(SharedPreferenceUtil.START_PAGE_AD, result);
                shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_URL, bean.url);
                shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_LINK, bean.link);
                shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_OPENURL, bean.openurl);
                shareDate.putInt(SharedPreferenceUtil.GIFT_DIAMOND_MIN_NUM, bean.giftDiamondMinNum);

                Common.getInstance().setLoginFlag(bean.loginflag);
                Common.getInstance().setRegisterFlag(bean.registerflag);
                if (Config.isShowGoogleApp) { ///谷歌包不显示广告
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL, "");
                } else {
                    shareDate.putString(SharedPreferenceUtil.START_PAGE_AD_TYPE_CONTROL, bean.advertInfo);
                }

                shareDate.putInt(shareDate.GAMECENTER_AVAILABLE, bean.gamecenter);
                shareDate.putInt(shareDate.GOOGLE_APP, bean.googleApp);
                shareDate.putInt(shareDate.SHOWMIGU, bean.showMigu);
                shareDate.putInt(shareDate.SHOW_MANLIAN, bean.showManlian);
                shareDate.putInt(shareDate.ACCOMPANY_IS_SHOW, bean.accompanyIsShow);

                Common.getInstance().setDefaultTab(bean.defaultTab);
                Common.getInstance().setBlockStatus(bean.relationswitch);
                Common.getInstance().setDefaultTopOption(bean.defaultTopOption);
//                Common.getInstance().setGeetestSwitch(bean.geetestSwitch);
                Common.getInstance().setBindPhoneSwitch(bean.bindPhoneSwitch);
                Common.getInstance().setDefaultTopShow(bean.defaultTopShow);
                handleGiftInfos(bean.giftUpdateList);
            }

        }

    }

    /**
     * 处理礼物下载信息，此方法首先会查询当前APP是否存在上次下载失败的礼物信息，如果有加入下载队列
     *
     * @param giftList
     */
    private void handleGiftInfos(InitBean.GiftUpdateList giftList) {
        List<InitBean.GiftPackage> giftInfoList = new ArrayList<>();
        //1.获取上次下载失败的礼物信息
        List<InitBean.GiftPackage> failureGifts = OkhttpDownloadStatus.getDownloadFailureGift(getApplicationContext());
        if (null != failureGifts && failureGifts.size() > 0 && null != giftInfoList) {
            giftInfoList.addAll(failureGifts);
        }
        if (null != giftList) {
            //2.保存时间戳，在doInit的时候获取
            String lastUpdate = giftList.giftLastUpdate;
            SharedPreferenceUtil.getInstance(getApplicationContext()).putString(SharedPreferenceUtil.GIFT_LAST_UPDATE, lastUpdate);
            //3.获取下载连接地址
            if (null != giftList.list && giftList.list.size() > 0) {
                giftInfoList.addAll(giftList.list);
            }
        }

        //4.批量下载
        if (null != giftInfoList && giftInfoList.size() > 0) {
            OkhttpDownload downloadManager = new OkhttpDownload(getApplicationContext());
            for (InitBean.GiftPackage giftPackage : giftInfoList) {
                //此时网络不是wifi，需保存失败礼物信息
                if (!CommonFunction.isWifi(getApplicationContext())) {
                    OkhttpDownloadStatus.putDownloadFailureGift(getApplicationContext(), giftPackage.downloadUrl, giftPackage.animationId);
                    continue;
                }
                downloadManager.downLoad(giftPackage.downloadUrl, giftPackage.animationId);

            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        if (loginFlag == flag) {
            if (mHandler != null)
                mHandler.sendMessageDelayed(mHandler.obtainMessage(LOGIN_ERROR), 500);
            return;
        }

        ErrorCode.toastError(SplashActivity.this, e);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_relogin_btn:
                loginAppType();
                break;
            case R.id.iv_start_background:
                // 自己广告点击
                if (TextUtils.isEmpty(startADBean.link) || !isAdImageFileExist) {
                    return;
                }

                if (startADBean.link.contains(".apk")) {

                    mPresenter.getAdvertInstance(SplashActivity.this).createNotification();
                    mPresenter.getDownLoadApk(SplashActivity.this).startDownLoad(startADBean.link);
                } else {

                    Intent i = new Intent(SplashActivity.this, WebViewAvtivity.class);
                    i.putExtra(WebViewAvtivity.WEBVIEW_URL, startADBean.link);
                    i.putExtra(WebViewAvtivity.IS_SHOW_CLOSE, true);
                    isClickADView = true;
                    startActivityForResult(i, SHOW_WEB_VIEW_AD);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHOW_WEB_VIEW_AD || SHOW_LINGJI_AD == requestCode) {
            loginAppType();
            CommonFunction.log("Other", "onActivityResult ------------------广告点击返回");
        }
    }

    private void autoLoginFail() {
        int type = BaseHttp.checkNetworkType(this);
        if (type == BaseHttp.TYPE_NET_WORK_DISABLED) {
            // 无法连接网络
            netConnectFail();
        } else {
            gotoLoginActivity();
        }
    }

    private void netConnectFail() {
        gotoLoginActivity();
    }


    /**
     * @Title: gotoLoginActivity
     * @Description: 跳转到登录界面
     */
    protected void gotoLoginActivity() {
        //YC 取消LoginMainActivity的使用
//        Intent loginMainIntent = new Intent(this, LoginMainActivity.class);
        Intent loginMainIntent = new Intent(this, RegisterNewActivity.class);
        startActivity(loginMainIntent);
        finish();
    }

    @SuppressWarnings("unchecked")
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOGIN_ERROR:
                    autoLoginFail();
                    break;
                case LOGIN_SUCCES:
                    autoLoginSuccess(String.valueOf(msg.obj));
                    break;
                case SKIP_LOGIN:
                    autoLoginFail();
                    break;
            }
        }
    };


    protected void autoLoginSuccess(String result) {
        if (TextUtils.isEmpty(result)) {
            return;
        }
        try {


            // TODO Auto-generated method stub
            LoginResponseBean bean = StartModel.getInstance().loginData(this, result);
            Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(result);
            if (item != null && bean != null) {
                bean.setItem(item);

            }
            // 保存本次登录成功的域名
            spUtil.putString(spUtil.REG_URL, Config.loginUrl);
            // 流程：1、新老用户登录，则直接登录进去，再判断是否绑定
            // 2、若为用户绑定，并且帐号与密码不正确，则提示并跳转到登录页面
            if (bean != null) {
                if (bean.isSuccess()) { // 登陆成功
                    gotoMain();
                } else {
                    if (bean.error == 4108) {
                        final String desc = bean.errordesc;
                        DialogUtil
                                .showOneButtonDialog(mContext, getString(R.string.dialog_title),
                                        getString(R.string.download_offical),
                                        getString(R.string.game_center_task_download),
                                        new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                Uri uri = Uri.parse(desc);
                                                startActivity(new Intent(Intent.ACTION_VIEW, uri));

                                            }
                                        });
                    } else if (bean.error == 4208) {
                        ErrorCode.showError(this, result);
                        gotoLoginActivity();
                    } else {
                        ErrorCode.showError(this, result);
                        gotoLoginActivity();
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void gotoMain() {
        startActivity(new Intent(this, MainFragmentActivity.class));
        finish();
    }

    @Override
    public void doMainActiviy2Perssiomison() {
        super.doMainActiviy2Perssiomison();
        permissonNo = true;
        initModule();
        startMainActivity();
        statisticsCount();
    }

    protected void initModule() {
        CommonFunction.redirectLogcat();// 重定向Logcat
        ConnectorManage.getInstance(getApplicationContext()).reset();
        ConnectLogin.reset();
        CommonFunction.stopBackService(getApplicationContext());
        NotificationFunction.getInstatant(mContext).cancelNotification();

        // 程序启动生成key,kek
        ConnectorManage.getInstance(getApplicationContext()).getKey();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Common.windowWidth = dm.widthPixels;
        Common.windowHeight = dm.heightPixels;

//        addSelfShortcut();// 加入快捷方式到桌面
        ///极验
        gtAppDlgTask = GtAppDlgTask.getInstance();
        gtAppDlgTask.setContext(SplashActivity.this);
        gtAppDlgTask.setGeetBackListener(geetBackListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonFunction.log(TAG, "onDestroy() into");

        destroyInMobi();

        if (mHandler != null) {
            mHandler.removeMessages(LOGIN_ERROR);
            mHandler.removeMessages(LOGIN_SUCCES);
            mHandler.removeMessages(SKIP_LOGIN);
            mHandler = null;
        }

        if (gtAppDlgTask != null) {
            gtAppDlgTask.onDestory();
        }
        CommonFunction.log(TAG, "onDestroy() out");
    }

    /**
     * 极验回调
     */
    private GtAppDlgTask.GeetBackListener geetBackListener = new GtAppDlgTask.GeetBackListener() {

        @Override
        public void onSuccess(String authKey) {

            Common.getInstance().setGeetAuthKey(authKey);

            loginAppType();

        }

        @Override
        public void onClose() {
            gotoLoginActivity();
        }
    };

    /* InMobi 广告
     * */
    @IAroundAD
    private void loadInMobi() {
        if (BuildConfig.showAdvert == true) {
            com.inmobi.ads.InMobiNative ads = new InMobiNative(SplashActivity.this, Config.INMOBI_SPLASH_PLACEMENTID, new NativeAdEventListener() {
                @Override
                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                    super.onAdLoadSucceeded(inMobiNative);
                    adParent.setVisibility(View.VISIBLE);
                    adParent.removeAllViews();
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    adParent.addView(inMobiNative.getPrimaryViewOfWidth(SplashActivity.this, adParent, mStartLoginBg, displayMetrics.widthPixels));
                    int delayTime = 500;
                    if (isAdImageFileExist) {
                        delayTime = 4000;
                    }
                    startBtn.setVisibility(View.VISIBLE);

                    startBackground.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isClickADView) {
                                loginAppType();
                            }
                        }
                    }, delayTime);
                }

                @Override
                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onAdLoadFailed(inMobiNative, inMobiAdRequestStatus);
                    loginAppType();
                    CommonFunction.log(TAG, "onAdLoadFailed inMobiAdRequestStatus=" + inMobiAdRequestStatus.getMessage());
                }

                @Override
                public void onAdFullScreenDismissed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDismissed(inMobiNative);
                }

                @Override
                public void onAdFullScreenWillDisplay(InMobiNative inMobiNative) {
                    super.onAdFullScreenWillDisplay(inMobiNative);
                }

                @Override
                public void onAdFullScreenDisplayed(InMobiNative inMobiNative) {
                    super.onAdFullScreenDisplayed(inMobiNative);
                }

                @Override
                public void onUserWillLeaveApplication(InMobiNative inMobiNative) {
                    super.onUserWillLeaveApplication(inMobiNative);
                }

                @Override
                public void onAdImpressed(InMobiNative inMobiNative) {
                    super.onAdImpressed(inMobiNative);
                }

                @Override
                public void onAdClicked(InMobiNative inMobiNative) {
                    super.onAdClicked(inMobiNative);
                    isClickADView = true;
                    canInMobiJump = false;
                    nextInMobi();
                }

                @Override
                public void onAdStatusChanged(InMobiNative inMobiNative) {
                    super.onAdStatusChanged(inMobiNative);
                }

                @Override
                public void onRequestPayloadCreated(byte[] bytes) {
                    super.onRequestPayloadCreated(bytes);
                }

                @Override
                public void onRequestPayloadCreationFailed(InMobiAdRequestStatus inMobiAdRequestStatus) {
                    super.onRequestPayloadCreationFailed(inMobiAdRequestStatus);
                }
            });
//            com.inmobi.ads.InMobiNative ad = new com.inmobi.ads.InMobiNative(SplashActivity.this, Config.INMOBI_SPLASH_PLACEMENTID, new com.inmobi.ads.InMobiNative.NativeAdListener() {
//                @Override
//                public void onAdLoadSucceeded(com.inmobi.ads.InMobiNative inMobiNative) {
//                    adParent.setVisibility(View.VISIBLE);
//                    adParent.removeAllViews();
//                    DisplayMetrics displayMetrics = new DisplayMetrics();
//                    getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//                    adParent.addView(inMobiNative.getPrimaryViewOfWidth(adParent, mStartLoginBg, displayMetrics.widthPixels));
//                    int delayTime = 500;
//                    if (isAdImageFileExist) {
//                        delayTime = 4000;
//                    }
//                    startBtn.setVisibility(View.VISIBLE);
//
//                    startBackground.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (!isClickADView) {
//                                loginAppType();
//                            }
//                        }
//                    }, delayTime);
//                }
//
//                @Override
//                public void onAdLoadFailed(com.inmobi.ads.InMobiNative inMobiNative, com.inmobi.ads.InMobiAdRequestStatus inMobiAdRequestStatus) {
//                    loginAppType();
//                    CommonFunction.log(TAG, "onAdLoadFailed inMobiAdRequestStatus=" + inMobiAdRequestStatus.getMessage());
//                }
//
//                @Override
//                public void onAdFullScreenDismissed(com.inmobi.ads.InMobiNative inMobiNative) {
//
//                }
//
//                @Override
//                public void onAdFullScreenDisplayed(com.inmobi.ads.InMobiNative inMobiNative) {
//
//                }
//
//                @Override
//                public void onUserWillLeaveApplication(com.inmobi.ads.InMobiNative inMobiNative) {
//
//                }
//
//                @Override
//                public void onAdImpressed(@NonNull com.inmobi.ads.InMobiNative inMobiNative) {
//
//                }
//
//                @Override
//                public void onAdClicked(@NonNull com.inmobi.ads.InMobiNative inMobiNative) {
//                    isClickADView = true;
//                    canInMobiJump = false;
//                    nextInMobi();
//                }
//
//                @Override
//                public void onMediaPlaybackComplete(@NonNull com.inmobi.ads.InMobiNative inMobiNative) {
//
//                }
//            });
            ads.load();
            nativeAd = ads;
        }
    }

    @IAroundAD
    private void pauseInMobi() {
        if (BuildConfig.showAdvert == true) {
            if (nativeAd != null) {
                com.inmobi.ads.InMobiNative ad = (com.inmobi.ads.InMobiNative) nativeAd;
                ad.pause();
            }
        }
    }

    @IAroundAD
    private void resumeInMobi() {
        if (BuildConfig.showAdvert == true) {
            if (nativeAd != null) {
                com.inmobi.ads.InMobiNative ad = (com.inmobi.ads.InMobiNative) nativeAd;
                ad.resume();
            }
        }
    }

    @IAroundAD
    private void destroyInMobi() {
        if (BuildConfig.showAdvert == true) {
            if (nativeAd != null) {
                com.inmobi.ads.InMobiNative ad = (com.inmobi.ads.InMobiNative) nativeAd;
                ad.destroy();
                nativeAd = null;
            }
        }
    }
}
