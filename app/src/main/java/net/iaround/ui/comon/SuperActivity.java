/****************
 * Copyright 2011 u6 to MeYou
 * <p/>
 * activity super  使用MeeyouViewFlipper添加 view进来，通过stack的方式储存view.
 *
 * @author linyg
 * @Date:2011-04-29 11:34:21
 * @version v2.0
 */

package net.iaround.ui.comon;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constant;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectGroup;
import net.iaround.connector.ConnectSession;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.im.STNManager;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PermissionUtils;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.RegisterNewActivity;
import net.iaround.ui.activity.SplashActivity;
import net.iaround.ui.dynamic.NotificationFunction;

import java.lang.ref.WeakReference;
import java.util.List;


@SuppressWarnings("deprecation")
public class SuperActivity extends Activity implements CallBackNetwork, HttpCallBack {
    private static final String TAG = "SuperActivity";
    public Context mContext;
    public LayoutInflater mInflater;
    private BaseApplication mApplication;

    public SuperActivity mActivity;
    private FrameLayout mFlipper;
    private boolean mIsBack; // 是否为返回
    private SuperView mCurrentView;
    private ConnectorManage mConnectorCore;

    public static boolean stoppedByHomeKey;
    public boolean isShareToOther;
    protected boolean isRestart = false;

    private static final int PERMISSION_REQUEST_CODE = 0; // 系统权限管理页面的参数
    protected boolean isRequireCheck;        // 是否需要系统权限检测, 防止和系统提示框重叠
    private int typeMissing = 0;            //1-弹出读写缺失权限；2-弹出相机缺失权限；3-弹出麦克风缺失权限；

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Common.getInstance().loginUser.getUid() <= 0) {
            CommonFunction.log(TAG, "onCreate() into, login user null, activity maybe restart");
            if (savedInstanceState != null) {
                savedInstanceState.remove("android:fragments");
            }
            isRestart = true;
        }
        super.onCreate(savedInstanceState);

        mActivity = this;
        mContext = this;

        if (isRestart == true) {
            CommonFunction.log(TAG, "onCreate() will go to login activity");
            CloseAllActivity.getInstance().addActivity(this);
            CloseAllActivity.getInstance().whatActivityInProgram();
            CloseAllActivity.getInstance().close();

            Intent intent = new Intent();
            intent.setClassName(BaseApplication.appContext.getPackageName(), SplashActivity.class.getName());
            startActivity(intent);
            return;
        }
        onCreate(savedInstanceState, true);
    }

    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }

    protected void onCreate(Bundle savedInstanceState, boolean addToStack) {

        mApplication = (BaseApplication) getApplication();
        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);

        //slidingmenu改造后的子Activity继承与superactivity，不能加进CloseAllActivity，否则MainActivity无法成为顶Activity
        if (addToStack)
            CloseAllActivity.getInstance().addActivity(this);

        if (Common.getInstance().loginUser != null) {

        }

        if (savedInstanceState != null) {
            isShareToOther = savedInstanceState.getBoolean(Constant.KEY_SHARE_MARK, false);
        } else {
            isShareToOther = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isRestart == true) {
            return;
        }
        stoppedByHomeKey = false;
        setComponentListener(this);
        mIsBack = false;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isRestart == true) {
            return;
        }
        CommonFunction.log(TAG, "onResume() into");

        NotificationFunction.getInstatant(mContext).cancelNotification();

        if (getCurrentView() != null) {
            getCurrentView().onActivityResume();
        }

        if (!CommonFunction.isTopActivity(mContext)) {
            CommonFunction.moveTaskToFront(mActivity, mContext);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isRestart == true) {
            return;
        }
        CloseAllActivity.setPauseSystemTimeTime();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isRestart == true) {
            return;
        }
        stoppedByHomeKey = false;
        mIsBack = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRestart == true) {
            return;
        }
        if (getCurrentView() != null) {
            getCurrentView().onActivityStop();
        }
        //
        if (!CommonFunction.isRunningForeground(mContext)) {

        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (isRestart == true) {
            return;
        }
        if (getCurrentView() != null) {
            getCurrentView().onLowMemory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isRestart == true) {
            return;
        }
        CloseAllActivity.getInstance().removeActivity(this);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        if (view != null && view instanceof SuperView) {
            mCurrentView = (SuperView) view;
        }
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        if (view != null && view instanceof SuperView) {
            mCurrentView = (SuperView) view;
        }
    }

    /**
     * 取得连接管理器
     *
     * @return
     */
    public ConnectorManage getConnectorManage() {
        if (mConnectorCore == null) {
            mConnectorCore = ConnectorManage.getInstance(BaseApplication.appContext);
        }
        return mConnectorCore;
    }

    public static boolean isTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isBack() {
        return mIsBack;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) { // 增加媒体音量
            CommonFunction.changeMediaVolume(this, 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) { // 降低媒体音量
            CommonFunction.changeMediaVolume(this, -1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 获取acitivity的根布局
     */
    public View getRootView() {
        return ((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * 设置视图组件事件监听器
     *
     * @param context 环境对象
     */
    protected void setComponentListener(SuperActivity context) {

    }

    /**
     * 表情界面隐藏，显示键盘
     */
    public void hideFaceShowKeyboard() {

    }

    /**
     * 把软键盘隐藏
     */
    public boolean hiddenKeyBoard(Activity mActivity) {
        // 点击屏幕任何地方则把软键盘隐藏
        if (mActivity.getCurrentFocus() != null) {
            ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    /**
     * 获取当前显示的SuperView
     *
     * @return
     */
    public SuperView getCurrentView() {
        return this.mCurrentView;
    }


    /**
     * 返回LayoutInflater，对象，该对象用于将布局XML转化成View
     */
    public LayoutInflater getLayoutInflater() {
        return mInflater;
    }

    /**
     * 返回Application对象 ，可以设置全局对象
     *
     * @return
     */
    public BaseApplication getLocalApplication() {
        return mApplication;
    }

    /**
     * 返回当前Activity
     *
     * @return
     */
    public SuperActivity getActivity() {
        return this.mActivity;
    }

    /**
     * 返回ViewFlipper对象
     *
     * @return
     */
    public FrameLayout getFlipper() {
        return mFlipper;
    }

    /**
     * 设置ViewFlipper对象,作为切换视图的容器
     *
     * @param flipper
     */
    public void setFlipper(FrameLayout flipper) {
        this.mFlipper = flipper;
    }

    /**
     * 获取string
     *
     * @param id
     * @return
     */
    public String getResString(int id) {
        return getResources().getString(id);
    }

    /**
     * 获取图片资源
     *
     * @param id
     * @return
     */
    public Drawable getResDrawable(int id) {
        return getResources().getDrawable(id);
    }

    /**
     * 获取color值
     *
     * @param id
     * @return
     */
    public int getResColor(int id) {
        return getResources().getColor(id);
    }

    /**
     * 返回
     */
    public void onReturn() {
        this.finish();
    }


    // 接收消息
    @Override
    public void onReceiveMessage(TransportMessage message) {
        if (getCurrentView() != null) {
            getCurrentView().onReceiveMessageListener(message);
        }
    }

    // socket发送回调
    @Override
    public void onSendCallBack(int e, long flag) {
        if (getCurrentView() != null) {
            getCurrentView().onSendMessageListener(e, flag);
        }
    }

    // http连接错误信息
    @Override
    public void onGeneralError(int e, long flag) {
    }

    // http请求响应信息
    @Override
    public void onGeneralSuccess(String result, long flag) {
    }


    @Override
    public void onConnected() {

    }

    // 保存数据
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CommonFunction.log(TAG, "----SuperActivity onSaveInstanceState-------");
        if (outState != null) {
            outState.putString("businessUrl", Config.sBusinessHost);
            outState.putString("pictureUrl", Config.sPictureHost);
            outState.putString("videoUrl", Config.sVideoHost);
//			outState.putString( "soundUrl", Config.sSoundHost );
            outState.putString("payUrl", Config.sPayHost);
//			outState.putString( "SMS_ADDR", Config.sSMSAddrHost );
            outState.putString("groupUrl", Config.sGroupHost);
//			outState.putString( "userUrl", Config.sUserHost );
            outState.putString("photoUrl", Config.sPhotoHost);
            outState.putString("friendUrl", Config.sFriendHost);
            outState.putString("goldUrl", Config.sGoldHost);
            outState.putString("nearUrl", Config.sNearHost);
            outState.putString("gameUrl", Config.sGameHost);
//			outState.putString( "recommendUrl", Config.sRecommendHost );

            outState.putString("socialgameUrl", Config.sSocialgame);
            outState.putString("dynamicUrl", Config.sDynamic);
            outState.putString("relationUrl", Config.sRelaction);
//			outState.putString( "postbarUrl", Config.sPostbar );
            outState.putString("gameCenterUrl", Config.sGameCenter);
            outState.putString("chatbarUrl", Config.sChatBar);

            outState.putString("sessionAdress", ConnectSession.getSessionAddress());
            outState.putString("groupAdress", ConnectGroup.getGroupAddress());

            outState.putString("key", ConnectorManage.getInstance(mContext).getKey());
            outState.putLong("uid", Common.getInstance().loginUser.getUid());
            outState.putString("nickname", Common.getInstance().loginUser.getNickname());
            outState.putInt("viplevel", Common.getInstance().loginUser.getViplevel());
            outState.putString("icon", Common.getInstance().loginUser.getIcon());
            outState.putInt("total", Common.getInstance().loginUser.getInfoTotal());
            outState.putInt("complete", Common.getInstance().loginUser.getInfoComplete());
            outState.putInt("userType", Common.getInstance().loginUser.getUserType());
            outState.putInt("blockStatus", Common.getInstance().loginUser.getBlockStaus());

            outState.putInt("windowWidth", Common.windowWidth);
            outState.putInt("windowHeight", Common.windowHeight);//jiqiang
        }
    }


    // 恢复数据
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        if (outState != null) {
            try {
                CommonFunction.log(TAG, "***************superActivity  onRestoreInstanceState******************");
                Config.sBusinessHost = outState.getString("businessUrl");
                Config.sPictureHost = outState.getString("pictureUrl");
                Config.sVideoHost = outState.getString("videoUrl");
//				Config.sSoundHost = outState.getString( "soundUrl" );
                Config.sPayHost = outState.getString("payUrl");
//				Config.sSMSAddrHost = outState.getString( "SMS_ADDR" );
                Config.sGroupHost = outState.getString("groupUrl");
//				Config.sUserHost = outState.getString( "userUrl" );
                Config.sPhotoHost = outState.getString("photoUrl");
                Config.sFriendHost = outState.getString("friendUrl");
                Config.sGoldHost = outState.getString("goldUrl");
                Config.sNearHost = outState.getString("nearUrl");
                Config.sGameHost = outState.getString("gameUrl");
//				Config.sRecommendHost = outState.getString( "recommendUrl" );
                Config.sSocialgame = outState.getString("socialgameUrl");
                Config.sDynamic = outState.getString("dynamicUrl");
                Config.sRelaction = outState.getString("relationUrl");
//				Config.sPostbar = outState.getString( "postbarUrl" );
                Config.sGameCenter = outState.getString("gameCenterUrl");
                Config.sChatBar = outState.getString("chatbarUrl");

                ConnectSession.setSessionAddress(outState.getString("sessionAdress"));
                ConnectGroup.setGroupAddress(outState.getString("groupAdress"));

                ConnectorManage.getInstance(mContext).setKey(outState.getString("key"));
                Common.getInstance().loginUser.setUid(outState.getLong("uid"));
                Common.getInstance().loginUser.setNickname(outState.getString("nickname"));
                Common.getInstance().loginUser.setNoteName(outState.getString("notes"));
                Common.getInstance().loginUser.setIcon(outState.getString("icon"));
                Common.getInstance().loginUser.setViplevel(outState.getInt("viplevel"));
                Common.getInstance().loginUser.setInfoTotal(outState.getInt("total"));
                Common.getInstance().loginUser.setInfoComplete(outState.getInt("complete"));
//				Common.getInstance( ).loginUser.setUserType( outState.getInt( "userType" ) );
                Common.getInstance().loginUser.setBlockStaus(outState.getInt("blockStatus"));

                Common.windowWidth = outState.getInt("windowWidth");
                Common.windowHeight = outState.getInt("windowHeight");

                // 重置重连次数
                ConnectSession.getInstance(mContext).reconnectSeesionCount = 0;
                ConnectGroup.getInstance(mContext).connectGroupCount = 0;//jiqiang
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 是否显示缺失权限提示
    protected void setMissingPermission(int typeMissing) {
        this.typeMissing = typeMissing;
    }

    // 请求权限兼容低版本
    protected void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    // 全部权限均已获取
    protected void allPermissionsGranted() {
    }


    // 含有全部的权限
    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    // 显示缺失权限提示
    private void showMissingPermissionDialog(int[] grantResults) {
    }

    // 启动应用的设置
    private void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    /**
     * 获取摄像头权限
     */
    public void requestCamera() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
    }

    /**
     * 获取摄像头权限后执行操作
     */
    public void doCamera() {
    }

    /**
     * 获取获得麦克风权限
     */
    public void requestRecordAudio() {
        CommonFunction.log(TAG, "requestRecordAudio() into");
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_RECORD_AUDIO, mPermissionGrant);
    }

    /**
     * 获取获得麦克风权限后执行操作
     */
    public void doRecordAudio() {
        CommonFunction.log(TAG, "doRecordAudio() into");
    }

    /**
     * 获取麦克风
     */
    public void requestMicshowPermissions() {
        CommonFunction.log(TAG, "requestMicshowPermissions() into");
        mPermissionRequestState = 1;
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.micPermissions, mPermissionGrant, PermissionUtils.CODE_MIC_RECORD_AUDIO);
    }

    /**
     * 获取摄像头和麦克风
     */
    public void requestLiveShowPermissions() {

        PermissionUtils.requestMultiPermissions(this, PermissionUtils.liveShowPermissions, mPermissionGrant, PermissionUtils.CODE_MULTI_LIVESHOW);
    }

    /**
     * 获取聊吧获得麦克风权限后执行操作
     */
    public void doMicShowPermissions() {
        mPermissionRequestState = 2;
    }

    public void doLiveShowPerssiomison() {

    }

    static class IAroundPermissionGrant implements PermissionUtils.PermissionGrant {
        private WeakReference<SuperActivity> mActivity;

        public IAroundPermissionGrant(SuperActivity activity) {
            mActivity = new WeakReference<SuperActivity>(activity);
        }

        @Override
        public void onPermissionGranted(int requestCode) {
            CommonFunction.log("socket", "onPermissionGranted() requestCode=" + requestCode);
            SuperActivity activity = mActivity.get();
            switch (requestCode) {
                case PermissionUtils.CODE_RECORD_AUDIO://获取麦克风权限
                    if (activity != null)
                        activity.doRecordAudio();
                    break;
                case PermissionUtils.CODE_CAMERA://获取摄像头权限
                    if (activity != null)
                        activity.doCamera();
                    break;
                case PermissionUtils.CODE_MIC_RECORD_AUDIO://获取麦克风权限
                    if (activity != null) {
                        activity.doMicShowPermissions();
                    } else {
                        CommonFunction.log("socket", "onPermissionGranted() activity null");
                    }
                    break;
                case PermissionUtils.CODE_MULTI_LIVESHOW:
                    if (activity != null)
                        activity.doLiveShowPerssiomison();
                    break;
            }
        }
    }

    protected int mPermissionRequestState = 0; //0 没操作 1 请求中 2 请求完毕
    private PermissionUtils.PermissionGrant mPermissionGrant = new IAroundPermissionGrant(this);

}
