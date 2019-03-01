package net.iaround.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.inputmethod.InputMethodManager;


import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectGroup;
import net.iaround.connector.ConnectSession;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.im.STNManager;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.ui.activity.settings.AccountMangerActivity;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;

import java.util.List;

public class BaseFragmentActivity extends FragmentActivity implements HttpCallBack {
    protected Context mContext;
    private Dialog mWaitDialog = null;
    private int mPermissionRequestState = 0; //请求麦克风的权限
    public Activity mActivity;
    protected boolean mIsDestroy = false; //手动标记为已销毁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonFunction.log("BaseFragmentActivity", "onCreate() into, login user uid=" + Common.getInstance().loginUser.getUid());
        mContext = this;
        if (mContext == null) {
            mContext = BaseApplication.appContext;
        }
        mActivity = this;
        //如果是内存不够重起某个activity,但缺失登陆用户信息时,需要跳转到登陆窗口
        if (Common.getInstance().loginUser.getUid() <= 0) {
            if (this instanceof ContactsActivity) {

            } else {
                CommonFunction.log("BaseFragmentActivity", "onCreate() login user uid null, fragments not restore");
                if (savedInstanceState != null) {
                    savedInstanceState.remove("android:support:fragments");
                }
                mIsDestroy = true;
            }
        }
        super.onCreate(savedInstanceState);

        if (mIsDestroy == true) {
            CommonFunction.log("BaseFragmentActivity", "onCreate() login user uid null, goto login activity");
            CloseAllActivity.getInstance().addActivity(this);
            CloseAllActivity.getInstance().whatActivityInProgram();
            CloseAllActivity.getInstance().close();

            Intent intent = new Intent();
            intent.setClassName(BaseApplication.appContext.getPackageName(), SplashActivity.class.getName());
            startActivity(intent);
            return;
        }

        CloseAllActivity.getInstance().addActivity(this);
        CloseAllActivity.getInstance().whatActivityInProgram();

        CommonFunction.log("BaseFragmentActivity", "onCreate() out");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsDestroy == true) {
            return;
        }
        if (1 == mPermissionRequestState) {
            requestMicshowPermissions();
        }
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsDestroy == true) {
            return;
        }
        CloseAllActivity.setPauseSystemTimeTime();
        MobclickAgent.onPause(this);

    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            ((InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsDestroy == true) {
            return;
        }
        CommonFunction.log("BaseFragmentActivity", "===============onDestroy=================");
        mWaitDialog = null;
        mContext = null;
        CloseAllActivity.getInstance().removeActivity(this);
        CloseAllActivity.getInstance().whatActivityInProgram();
    }

    public void showWaitDialog() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogUtil.getProgressDialog(this, getString(R.string.dialog_title), getString(R.string.please_wait), null);
        }
        mWaitDialog.show();
    }

    /**
     * 关闭等待对话框
     */
    void cancleWaitDialog() {
        if (mWaitDialog != null && mWaitDialog.isShowing()) {
            mWaitDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void requestMainActivity2Permission() {
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.mainActivity2Permissions, mPermissionGrant, PermissionUtils.CODE_MULTI_MAINACTIVITY2);
    }

    /**
     * 获取麦克风
     */
    public void requestMicshowPermissions() {
        CommonFunction.log("BaseFragmentActivity", "requestMicshowPermissions() into");
        mPermissionRequestState = 1;
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.micPermissions, mPermissionGrant, PermissionUtils.CODE_MIC_RECORD_AUDIO);
    }

    protected void doMicshowPermissions() {
        mPermissionRequestState = 2;
        ChatBarZoomWindow.getInstance().onRequestMicPermissionSuccess();
    }

    public void requestLiveShowPermission() {
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.liveShowPermissions, mPermissionGrant, PermissionUtils.CODE_MULTI_LIVESHOW);
    }

    public void requestProfilePermission() {
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.profileTakePhoto, mPermissionGrant, PermissionUtils.CODE_MULTI_PROFILE);
    }

    /**
     * 获取权限
     */
    public void requestPermission(String[] requestPermissions) {
        PermissionUtils.requestMultiPermissions(this, requestPermissions, mPermissionGrant, PermissionUtils.CODE_MULTI_PERMISSION);
    }

    /**
     * 获取联系人权限
     */
    public void requestGetAccounts() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_GET_ACCOUNTS, mPermissionGrant);
    }

    /**
     * 获取写入sd卡权限
     */
    public void requestWriteStorage() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE, mPermissionGrant);
    }

    /**
     * 获取wifi热点定位
     */
    public void requestCoarseLocation() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_COARSE_LOCATION, mPermissionGrant);
    }

    /**
     * 获取精良定位
     */
    public void requestFineLocation() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_ACCESS_FINE_LOCATION, mPermissionGrant);
    }

    /**
     * 获取摄像头权限
     */
    public void requestCamera() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_CAMERA, mPermissionGrant);
    }

    public void doCamera() {

    }

    /**
     * 获取读取手机状态权限
     */
    public void requestReadPhoneState() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, mPermissionGrant);
    }

    private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {
            switch (requestCode) {
                case PermissionUtils.CODE_CAMERA:
                    doCamera();
                    break;
                case PermissionUtils.CODE_MULTI_PERMISSION:
                    doPermission();
                    break;
                case PermissionUtils.CODE_MULTI_MAINACTIVITY2:
                    doMainActiviy2Perssiomison();
                    break;
                case PermissionUtils.CODE_MIC_RECORD_AUDIO://获取麦克风权限
                    doMicshowPermissions();
                    break;
            }
        }
    };

    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    /**
     * 显示再次申请提现对话框
     */
    public void showMessageSdDialog(final Activity context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            requestMainActivity2Permission();
            return;
        }
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(context, PERMISSION_WRITE_EXTERNAL_STORAGE);
        } catch (RuntimeException e) {
            return;
        }
        //检查权限是否开启
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(context.getString(R.string.confirm), okListener)
                    .setNegativeButton(context.getString(R.string.cancel), cancelListener)
                    .create()
                    .show();
        } else {
            requestMainActivity2Permission();
        }

    }

    public void doMainActiviy2Perssiomison() {

    }

    public void doPermission() {

    }

    // 保存数据
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        CommonFunction.log("BaseFragmentActivity", "----BaseFramentActivity onSaveInstanceState-------");
        if (outState != null) {
            outState.putString("businessUrl", Config.sBusinessHost);
            outState.putString("pictureUrl", Config.sPictureHost);
            outState.putString("videoUrl", Config.sVideoHost);
//			outState.putString( "soundUrl", Config.sSoundHost );//gh
            outState.putString("payUrl", Config.sPayHost);
//			outState.putString( "SMS_ADDR", Config.sSMSAddrHost );//gh
            outState.putString("groupUrl", Config.sGroupHost);
//			outState.putString( "userUrl", Config.sUserHost );//gh
            outState.putString("photoUrl", Config.sPhotoHost);
            outState.putString("friendUrl", Config.sFriendHost);
            outState.putString("goldUrl", Config.sGoldHost);
            outState.putString("nearUrl", Config.sNearHost);
            outState.putString("gameUrl", Config.sGameHost);
//			outState.putString( "recommendUrl", Config.sRecommendHost );//gh

            outState.putString("socialgameUrl", Config.sSocialgame);
            outState.putString("dynamicUrl", Config.sDynamic);
            outState.putString("relationUrl", Config.sRelaction);
            outState.putString("chatbarUrl", Config.sChatBar);

            outState.putString("sessionAdress", ConnectSession.getSessionAddress());
            outState.putString("groupAdress", ConnectGroup.getGroupAddress());

            outState.putString("key", ConnectorManage.getInstance(mActivity).getKey());
            outState.putLong("uid", Common.getInstance().loginUser.getUid());
            outState.putString("nickname", Common.getInstance().loginUser.getNickname());
            outState.putInt("viplevel", Common.getInstance().loginUser.getViplevel());
            outState.putString("icon", Common.getInstance().loginUser.getIcon());
            // outState.putInt( "complrate" , Common.getInstance(
            // ).loginUser.getCompleteRate( ) );
            // outState.putInt( "basicrate" , Common.getInstance(
            // ).loginUser.getBasicRate( ) );
            // outState.putInt( "secretrate" , Common.getInstance(
            // ).loginUser.getSecretRate( ) );
            outState.putInt("total", Common.getInstance().loginUser.getInfoTotal());
            outState.putInt("complete", Common.getInstance().loginUser.getInfoComplete());

            outState.putInt("windowWidth", Common.windowWidth);
            outState.putInt("windowHeight", Common.windowHeight);

            outState.putIntArray("loginFlag", Common.getInstance().getLoginFlag());
            outState.putIntArray("registerFlag", Common.getInstance().getRegisterFlag());

            DynamicModel.getInstent().saveLikeUnSendSuccessToCache(mContext);
        }
    }

    // 恢复数据
    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        if (outState != null) {
            try {
                CommonFunction.log("shifengxiong", "***************BaseFramentActivity  onRestoreInstanceState******************");
                Config.sBusinessHost = outState.getString("businessUrl");
                Config.sPictureHost = outState.getString("pictureUrl");
                Config.sVideoHost = outState.getString("videoUrl");
//				Config.sSoundHost = outState.getString( "soundUrl" );//gh
                Config.sPayHost = outState.getString("payUrl");
//				Config.sSMSAddrHost = outState.getString( "SMS_ADDR" );//gh
                Config.sGroupHost = outState.getString("groupUrl");
//				Config.sUserHost = outState.getString( "userUrl" );//gh
                Config.sPhotoHost = outState.getString("photoUrl");
                Config.sFriendHost = outState.getString("friendUrl");
                Config.sGoldHost = outState.getString("goldUrl");
                Config.sNearHost = outState.getString("nearUrl");
                Config.sGameHost = outState.getString("gameUrl");
//				Config.sRecommendHost = outState.getString( "recommendUrl" );//gh
                Config.sSocialgame = outState.getString("socialgameUrl");
                Config.sDynamic = outState.getString("dynamicUrl");
                Config.sRelaction = outState.getString("relationUrl");
                Config.sChatBar = outState.getString("chatbarUrl");

                ConnectSession.setSessionAddress(outState.getString("sessionAdress"));
                ConnectGroup.setGroupAddress(outState.getString("groupAdress"));

                ConnectorManage.getInstance(mActivity).setKey(outState.getString("key"));
                Common.getInstance().loginUser.setUid(outState.getLong("uid"));
                Common.getInstance().loginUser.setNickname(outState.getString("nickname"));
                Common.getInstance().loginUser.setNoteName(outState.getString("notes"));
                Common.getInstance().loginUser.setIcon(outState.getString("icon"));
                Common.getInstance().loginUser.setViplevel(outState.getInt("viplevel"));
                Common.getInstance().loginUser.setInfoTotal(outState.getInt("total"));
                Common.getInstance().loginUser.setInfoComplete(outState.getInt("complete"));

                Common.windowWidth = outState.getInt("windowWidth");
                Common.windowHeight = outState.getInt("windowHeight");

                Common.getInstance().setLoginFlag(outState.getIntArray("loginFlag"));
                Common.getInstance().setRegisterFlag(outState.getIntArray("registerFlag"));

                // 重置重连次数
                ConnectSession.getInstance(BaseApplication.appContext).reconnectSeesionCount = 0;
                ConnectGroup.getInstance(BaseApplication.appContext).connectGroupCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {

    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    public static boolean isTopActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasksInfo = activityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (context.getPackageName().equals(tasksInfo.get(0).topActivity.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

}
