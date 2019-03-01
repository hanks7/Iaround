package net.iaround.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;


import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectGroup;
import net.iaround.connector.ConnectSession;
import net.iaround.connector.ConnectorManage;
import net.iaround.im.STNManager;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.ui.activity.settings.ContactOurActivity;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;


public class BaseActivity extends FragmentActivity {
    public Context mContext;
    private FrameLayout mFlipper;
    private Dialog mWaitDialog = null;
    private int mPermissionRequestState = 0;
    protected boolean mIsDestroy = false; //   手动标记为已销毁

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        CommonFunction.log("BaseActivity", "onCreate() into, login user uid=" + Common.getInstance().loginUser.getUid());
        mContext = this;
        //如果是内存不够重起某个activity,但缺失登陆用户信息时,需要跳转到登陆窗口
        if (Common.getInstance().loginUser.getUid() <= 0) {
            if (this instanceof SplashActivity || this instanceof GuideActivity || this instanceof LoginActivity || this instanceof RegisterNewActivity || this instanceof ResetActivity || this instanceof ResetEmailPasswordActivity || this instanceof CountrySelectActivity || this instanceof ResetPasswordActivity || this instanceof ContactOurActivity || this instanceof BindingTelPhoneActivity || this instanceof WebViewAvtivity) {

            } else {
                CommonFunction.log("BaseActivity", "onCreate() login user uid null, goto login activity");
                if (savedInstanceState != null) {
                    savedInstanceState.remove("android:support:fragments");
                }
                mIsDestroy = true;
            }
        }
        super.onCreate(savedInstanceState);

        if (mIsDestroy == true) {
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
        CommonFunction.log("BaseActivity", "onCreate() out");
    }

    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsDestroy == true) {
            return;
        }
        if (mPermissionRequestState == 1) {
            requestMicshowPermissions();
        }
        MobclickAgent.onResume(this);
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
     * 设置ViewFlipper对象,作为切换视图的容器bumang
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
     * 获取String数组
     *
     * @param id
     * @return
     */
    public String[] getResStringArr(int id) {
        return getResources().getStringArray(id);
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

    public void showWaitDialog() {
        if (mWaitDialog == null) {
            mWaitDialog = DialogUtil.getProgressDialog(this, getString(R.string.dialog_title), getString(R.string.please_wait), null);
        }
        mWaitDialog.show();
    }

    public void showWaitDialog(Context context) {
        if (mWaitDialog == null) {
            mWaitDialog = DialogUtil.getProgressDialog(context, getString(R.string.dialog_title), getString(R.string.please_wait), null);
        }
        mWaitDialog.show();
    }

    public void destroyWaitDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismiss();
            mWaitDialog = null;
        }
    }

    public void hideWaitDialog() {
        if (mWaitDialog != null) {
            if (mWaitDialog.isShowing()) {
                mWaitDialog.hide();
            }
        }
    }

    /**
     * 关闭等待对话框
     */
    void cancleWaitDialog() {
        if (mWaitDialog != null) {
            if (mWaitDialog.isShowing()) {
                mWaitDialog.dismiss();
            }
        }
    }


    public <T> T findView(int id) {
        return (T) findViewById(id);
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
    protected void onDestroy() {
        super.onDestroy();
        if (mIsDestroy == true) {
            return;
        }
        CloseAllActivity.getInstance().removeActivity(this);
        CloseAllActivity.getInstance().whatActivityInProgram();
    }

    /**
     * 设置透明状态栏
     */
    private void setTranslucentWindows(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 弹出软键盘
     */
    protected void showInputMethod() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) BaseApplication.appContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 隐藏软键盘
     */
    protected void hideInputMethod() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) BaseApplication.appContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void requestMainActivity2Permission() {
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.mainActivity2Permissions, mPermissionGrant, PermissionUtils.CODE_MULTI_MAINACTIVITY2);
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

    /**
     * 获取读取手机状态权限
     */
    public void requestReadPhoneState() {
        PermissionUtils.requestPermission(this, PermissionUtils.CODE_READ_PHONE_STATE, mPermissionGrant);
    }

    /**
     * 获取麦克风
     */
    public void requestMicshowPermissions() {
        CommonFunction.log("BaseActivity", "requestMicshowPermissions() into");
        mPermissionRequestState = 1;
        PermissionUtils.requestMultiPermissions(this, PermissionUtils.micPermissions, mPermissionGrant, PermissionUtils.CODE_MIC_RECORD_AUDIO);
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
                case PermissionUtils.CODE_MULTI_LIVESHOW:
                    doLiveShowPerssiomison();
                    break;
                case PermissionUtils.CODE_ACCESS_FINE_LOCATION:
                    doFineLocation();
                    break;
            }
        }
    };

    public void doMainActiviy2Perssiomison() {

    }

    public void doFineLocation() {

    }

    public void doPermission() {

    }

    public void doCamera() {

    }

    protected void doMicshowPermissions() {
        mPermissionRequestState = 2;
        ChatBarZoomWindow.getInstance().onRequestMicPermissionSuccess();
    }

    public void doLiveShowPerssiomison() {

    }

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

    @Override
    public void onRequestPermissionsResult(final int requestCode, String[] permissions, int[] grantResults) {
        PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mPermissionGrant);
    }

    // 保存数据
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (outState != null) {
            CommonFunction.log("BaseActivity", "----BaseFramentActivity onSaveInstanceState------- outState===" + outState);
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

            outState.putString("key", ConnectorManage.getInstance(this).getKey());
            outState.putLong("uid", Common.getInstance().loginUser.getUid());
            outState.putString("nickname", Common.getInstance().loginUser.getNickname());
            outState.putInt("viplevel", Common.getInstance().loginUser.getViplevel());
            outState.putString("icon", Common.getInstance().loginUser.getIcon());
            outState.putInt("total", Common.getInstance().loginUser.getInfoTotal());
            outState.putInt("complete", Common.getInstance().loginUser.getInfoComplete());
            outState.putInt("userType", Common.getInstance().loginUser.getUserType());
            outState.putInt("blockStatus", Common.getInstance().loginUser.getBlockStaus());

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
                CommonFunction.log("BaseActivity", "***************BaseFramentActivity  onRestoreInstanceState******************");
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

                ConnectorManage.getInstance(this).setKey(outState.getString("key"));
                Common.getInstance().loginUser.setUid(outState.getLong("uid"));
                Common.getInstance().loginUser.setNickname(outState.getString("nickname"));
                Common.getInstance().loginUser.setNoteName(outState.getString("notes"));
                Common.getInstance().loginUser.setIcon(outState.getString("icon"));
                Common.getInstance().loginUser.setViplevel(outState.getInt("viplevel"));
                Common.getInstance().loginUser.setInfoTotal(outState.getInt("total"));
                Common.getInstance().loginUser.setInfoComplete(outState.getInt("complete"));
                //Common.getInstance( ).loginUser.setUserType( outState.getInt( "userType" ) );
                Common.getInstance().loginUser.setBlockStaus(outState.getInt("blockStatus"));

                Common.windowWidth = outState.getInt("windowWidth");
                Common.windowHeight = outState.getInt("windowHeight");

                Common.getInstance().setLoginFlag(outState.getIntArray("loginFlag"));
                Common.getInstance().setRegisterFlag(outState.getIntArray("registerFlag"));

                // 重置重连次数
                ConnectSession.getInstance(this).reconnectSeesionCount = 0;
                ConnectGroup.getInstance(this).connectGroupCount = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity) {
        if (activity == null) return;
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public Resources getResources() {
        // 手机字体设置为超大模式 极验验证码显示不全 http://www.jb51.net/article/111285.htm
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return resources;
    }

    /**
     * 跳转到webview
     * <p>
     * 2-遇见充值帮助
     */
    public void jumpWebViewActivity() {
        String str = getResString(R.string.common_questions);
        String url = CommonFunction.getLangText(mContext, Config.iAroundPayFAQUrl);
        Intent intent = new Intent(mContext, WebViewAvtivity.class);
        intent.putExtra(WebViewAvtivity.WEBVIEW_TITLE, str);
        intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        startActivity(intent);
    }

}
