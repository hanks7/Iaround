package net.iaround.ui.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.settings.*;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.UploadLogDialog;
import net.iaround.utils.DeviceUtils;
import net.iaround.videochat.VideoChatManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class SettingActivity extends TitleActivity implements View.OnClickListener {

    private static final int CLEAR_CACHED_SUC = 1;

//    private HeadPhotoView ivHeader;
//    private TextView tvUserId;
    private LinearLayout llAccountManager;
    private LinearLayout llNotificationSet;
    private LinearLayout llCheckUpdate;
    private LinearLayout llClearCached;
    private LinearLayout llAbout;
    private LinearLayout llRating;
    private LinearLayout llFeedBack;
    private LinearLayout llSecretSet;
    private TextView tvCurrentVersion;
    private ImageView ivCheckVersion;
    private LinearLayout lluploadLog;
    private View lluploadLogLine;
    private View llsimpleSwitchLine;

    private UploadLogDialog mUploadDialog;

    private LinearLayout llsimpleSwitch; //简单复杂开关
    private FloatWindowRunnable mAllowFloatWindowRun;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CLEAR_CACHED_SUC:
                    hideWaitDialog();
                    String builder = "共清除文件"+cachedFileCount+"个，节省空间"+cachedFileSize+"M";
                    CharSequence charSequenceTitle = getResString(R.string.setting_clear_cached_complete_title);
                    CharSequence charSequenceMsg = builder;
                    DialogUtil.showOKDialog(SettingActivity.this,charSequenceTitle,charSequenceMsg ,null);
                    break;
            }
        }
    };

    private ArrayList<File> deleteFiles = new ArrayList<>();
    private long cachedFileCount;
    private long cachedFileSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initDatas();
        initListeners();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mUploadDialog!=null){
            mUploadDialog.dismiss();
            mUploadDialog = null;
        }
        if(mAllowFloatWindowRun!=null) {
            handler.removeCallbacks(mAllowFloatWindowRun);
        }
    }

    private void initViews() {
        setTitle_LCR(false, R.drawable.title_back, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getString(R.string.setting_setting), true, 0, null, null);
        findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setContent(R.layout.activity_setting);
        llAccountManager = findView(R.id.ll_account_manager);
//        ivHeader = findView(R.id.iv_header);
//        tvUserId = findView(R.id.tv_user_id);
        llNotificationSet = findView(R.id.ll_notification_setting);
        llSecretSet = findView(R.id.ll_secret_setting);
        llFeedBack = findView(R.id.ll_feed_back);
        llRating = findView(R.id.ll_rating);
        llAbout = findView(R.id.ll_about);
        llClearCached = findView(R.id.ll_clear_cached);
        llCheckUpdate = findView(R.id.ll_check_update);
        tvCurrentVersion = findView(R.id.tv_current_version);
        ivCheckVersion = findView(R.id.iv_setting_check_status);
        lluploadLog = findView(R.id.ll_upload_log);
        lluploadLogLine = findView(R.id.divide_line_upload_log);
        llsimpleSwitch = findView(R.id.ll_simple_switch);
        llsimpleSwitchLine = findView(R.id.divide_line_simple_switch);

    }

    private void initDatas() {
//        ivHeader.execute(Common.getInstance().loginUser);
//        tvUserId.append(SharedPreferenceUtil.getInstance(this).getString(SharedPreferenceUtil.USER_ID));
        tvCurrentVersion.append(PhoneInfoUtil.getInstance(this).getVersionName());
        if (!TextUtils.isEmpty(Common.getInstance( ).currentSoftVersion)){
            if (Common.getInstance( ).currentSoftVersion.contains(".")){
                String versionCode = Common.getInstance( ).currentSoftVersion.replace(".","");
                if (Integer.valueOf(versionCode) > PhoneInfoUtil.getInstance(this).getVersionCode()){
                    ivCheckVersion.setVisibility(View.VISIBLE);
                }
            }
        }

        if (Config.isShowGoogleApp){
            findViewById(R.id.ll_check_update).setVisibility(View.GONE);
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) != ConnectionResult.SUCCESS) {
                llRating.setVisibility(View.GONE);
                findViewById(R.id.view_rating).setVisibility(View.GONE);
            }
        }

        if(VideoChatManager.getInstance().loginUserIsAnchor()==true){
            lluploadLog.setVisibility(View.VISIBLE);
            lluploadLogLine.setVisibility(View.VISIBLE);
            llsimpleSwitchLine.setVisibility(View.VISIBLE);
            llsimpleSwitch.setVisibility(View.VISIBLE);

            final int anchorVersion = Config.getAnchorVersionOpen();
            String content;
            if(anchorVersion == 1){
                content = getString(R.string.switch_roles);
            }else{
                content = getString(R.string.switch_roles);
            }
            TextView ss = findView(R.id.tv_simple_switch);
            ss.setText(content);

            allowFloatWindow();
        }else{
            if(Common.getInstance().getDebugSwitch()==1){
                lluploadLog.setVisibility(View.VISIBLE);
                lluploadLogLine.setVisibility(View.VISIBLE);
            }else {
                lluploadLog.setVisibility(View.GONE);
                lluploadLogLine.setVisibility(View.GONE);
            }
            llsimpleSwitchLine.setVisibility(View.GONE);
            llsimpleSwitch.setVisibility(View.GONE);
        }
    }

    private void initListeners() {
        llAccountManager.setOnClickListener(this);
        llNotificationSet.setOnClickListener(this);
        llSecretSet.setOnClickListener(this);
        llFeedBack.setOnClickListener(this);
        llRating.setOnClickListener(this);
        llAbout.setOnClickListener(this);
        llClearCached.setOnClickListener(this);
        llCheckUpdate.setOnClickListener(this);
        lluploadLog.setOnClickListener(this);
        llsimpleSwitch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.ll_account_manager://账号管理
                intent.setClass(this, AccountMangerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_notification_setting://通知管理
                intent.setClass(this, NotificationSetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_secret_setting://隐私设置
                intent.setClass(this, SecretSetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_feed_back://意见反馈
                intent.setClass(this, OptionFeedback.class);
                startActivity(intent);
                break;
            case R.id.ll_rating://评分
                if (Config.isShowGoogleApp){
                    CommonFunction.showMarket(this);
                }else {
                    CommonFunction.takeCredit(this);
                }
                break;
            case R.id.ll_about://关于遇见
                intent.setClass(this, AboutIaroundActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_clear_cached://清除缓存

                CharSequence charSequenceTitle = getString(R.string.setting_clear_cached_dialog_title);
                CharSequence charSequenceMsg = getString(R.string.setting_clear_cached_dialog_content);
                CharSequence charSequenceBtn1 = getString(R.string.setting_clear_cached_dialog_left);
                CharSequence charSequenceBtn2 = getString(R.string.setting_clear_cached_dialog_right);
                DialogUtil.showTowButtonDialog(this, charSequenceTitle, charSequenceMsg, charSequenceBtn1, charSequenceBtn2, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clearCached();
                    }
                });
                break;
            case R.id.ll_check_update://检查更新
//                DownloadNewVersionTask.getInstance(this, true).execute("http://dl.iaround.com/iAround_6.6.4.apk ");
                CommonFunction.showUpdateDialog(this);
                break;
            case R.id.ll_upload_log:
                // 邮件发送日志
//                if(Config.DEBUG) {
//                    EmailUtils.send(this,"yuanhuan.liang@iaround.com");
//                }
                //HTTP接口上传日志
                uploadLog();
                break;
            case R.id.ll_simple_switch:
                simpleSwitch();
                break;
        }
    }

    private void clearCached() {
        showWaitDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cached = new File(CommonFunction.getCachedPath());
                cachedFileCount = getDirChildCount(cached);
                cachedFileSize = getDirSize(cached) / 1024 / 1024;
                deleteDir(cached);
                handler.sendEmptyMessage(CLEAR_CACHED_SUC);
            }
        }).start();

    }

    // 获取文件的大小
    private long getDirSize( File directory){
        int size = 0;
        if(!directory.exists()){
            return 0L;
        }

        if(directory.isDirectory()){
            File files[] = directory.listFiles();
            if(files != null){
                for(File file : files){
                    size += getDirSize(file);
                }
            }
        } else {
            size += directory.length();
        }
        return size;
    }

    //获取文件子文件个数
    public long getDirChildCount(File directory){
        int count = 0;
        if(!directory.exists()){
            return 0;
        }
        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            if(files != null){
                for(File file : files){
                    count += getDirChildCount(file);
                }
            }
        } else {
            return 1;
        }
        return count;
    }

    public void deleteDir(File directory){
        if(directory.isDirectory()){
            File[] files = directory.listFiles();
            if(files != null){
                for(File file : files){
                    deleteDir(file);
                }
            }
            directory.delete();
        } else {
            directory.delete();
        }
    }

    /*上传日志到服务器
    * */
    private void uploadLog(){
        if(mUploadDialog==null) {
            mUploadDialog = new UploadLogDialog(this);
        }
        if(mUploadDialog.isShowing()==false){
            mUploadDialog.show();
            mUploadDialog.uploadLog();
        }
    }

    /*角色切换
    * */
    private void simpleSwitch(){
        CommonFunction.log("VideoChat", "退出APP");

        final int anchorVersion = Config.getAnchorVersionOpen();
        String content  = null;
        if(anchorVersion == 1){
            content = "切换到用户";
        }else{
            content = "切换到主播";
        }
        DialogUtil.showTowButtonDialog(this,
                this.getResources().getString(R.string.prompt),
                content,
                this.getResources().getString(R.string.cancel),
                this.getResources().getString(R.string.ok),
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if(anchorVersion==1) {
                                Config.setAnchorVersionOpen(0);
                            }else{
                                Config.setAnchorVersionOpen(1);
                            }
                            CloseAllActivity.getInstance().close();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("VideoChat", Log.getStackTraceString(e));
                        }
                    }
                });
    }

    /*悬浮窗授权
    * */
    private void allowFloatWindow(){
        //针对 OPPO 手机 弹出授权窗口
        if( true == DeviceUtils.isOPPO() ) {
            final int version = Build.VERSION.SDK_INT;
            int op = 24;
            if (version >= 19) {
                boolean allowFloatWindow = true;
                AppOpsManager manager = (AppOpsManager) mContext.getSystemService(Context.APP_OPS_SERVICE);
                try {
                    Class clazz = AppOpsManager.class;
                    Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                    if(AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), mContext.getPackageName())){
                        Log.e("VideoChat", "float window allowed ");
                        allowFloatWindow = true;
                    }else{
                        Log.e("VideoChat", "float window not allowed ");
                        allowFloatWindow = false;
                    }
                    if(allowFloatWindow==false) {
                        mAllowFloatWindowRun = new FloatWindowRunnable(this);
                        handler.postDelayed(mAllowFloatWindowRun, 3000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("VideoChat", "get float window allowed fail");
                }
            } else {
                Log.e("VideoChat", "Below API 19 cannot invoke!");
            }
        }
    }

    /*显示授权悬浮窗
    * */
    static class FloatWindowRunnable implements Runnable{
        private WeakReference<SettingActivity> mActivity;
        public FloatWindowRunnable(SettingActivity activity){
            mActivity = new WeakReference<SettingActivity>(activity);
        }

        @Override
        public void run() {
            final SettingActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            DialogUtil.showTowButtonDialog(activity,
                    activity.getResources().getString(R.string.prompt),
                    activity.getResources().getString(R.string.allow_open_float_window),
                    activity.getResources().getString(R.string.cancel),
                    activity.getResources().getString(R.string.ok),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClassName("com.coloros.safecenter", "com.coloros.safecenter.sysfloatwindow.FloatWindowListActivity");
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.e("VideoChat", Log.getStackTraceString(e));
                            }
                        }
                    });
        }
    }
}
