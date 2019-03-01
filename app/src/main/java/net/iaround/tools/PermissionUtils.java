package net.iaround.tools;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lining on 2016/10/8.
 * 适配6.0权限动态申请
 */
public class PermissionUtils {

    /**
     * 6.0权限需要动态申请一下九组权限，每组只要有一个权限申请成功了，就默认整组权限都可以使用了。
     * <p>
     * 联系人权限组
     * group:android.permission-group.CONTACTS
     * permission:android.permission.WRITE_CONTACTS
     * permission:android.permission.GET_ACCOUNTS
     * permission:android.permission.READ_CONTACTS
     * <p>
     * 电话权限组
     * group:android.permission-group.PHONE
     * permission:android.permission.READ_CALL_LOG
     * permission:android.permission.READ_PHONE_STATE
     * permission:android.permission.CALL_PHONE
     * permission:android.permission.WRITE_CALL_LOG
     * permission:android.permission.USE_SIP
     * permission:android.permission.PROCESS_OUTGOING_CALLS
     * permission:com.android.voicemail.permission.ADD_VOICEMAIL
     * <p>
     * 日历权限组
     * group:android.permission-group.CALENDAR
     * permission:android.permission.READ_CALENDAR
     * permission:android.permission.WRITE_CALENDAR
     * <p>
     * 照相机权限组
     * group:android.permission-group.CAMERA
     * permission:android.permission.CAMERA
     * <p>
     * 重力感应权限组
     * group:android.permission-group.SENSORS
     * permission:android.permission.BODY_SENSORS
     * <p>
     * 地理位置权限组
     * group:android.permission-group.LOCATION
     * permission:android.permission.ACCESS_FINE_LOCATION
     * permission:android.permission.ACCESS_COARSE_LOCATION
     * <p>
     * 存储空间权限组
     * group:android.permission-group.STORAGE
     * permission:android.permission.READ_EXTERNAL_STORAGE
     * permission:android.permission.WRITE_EXTERNAL_STORAGE
     * <p>
     * 麦克风权限组
     * group:android.permission-group.MICROPHONE
     * permission:android.permission.RECORD_AUDIO
     * <p>
     * 短消息权限组
     * group:android.permission-group.SMS
     * permission:android.permission.READ_SMS
     * permission:android.permission.RECEIVE_WAP_PUSH
     * permission:android.permission.RECEIVE_MMS
     * permission:android.permission.SEND_SMS
     * permission:android.permission.READ_CELL_BROADCASTS
     */

    private static final String TAG = PermissionUtils.class.getSimpleName();

    public static final int CODE_GET_ACCOUNTS = 0;//获取联系人
    public static final int CODE_READ_EXTERNAL_STORAGE = 1;//读取sd卡
    public static final int CODE_WRITE_EXTERNAL_STORAGE = 2;//写入sd卡
    public static final int CODE_RECORD_AUDIO = 3;//获得麦克风
    public static final int CODE_READ_PHONE_STATE = 4;//读取手机状态
    public static final int CODE_CAMERA = 5;//获取摄像头
    public static final int CODE_ACCESS_FINE_LOCATION = 6;//精良定位
    public static final int CODE_ACCESS_COARSE_LOCATION = 7;//wifi热点定位
    public static final int CODE_RECEIVE_SMS = 8;//接收短信
    public static final int CODE_SEND_SMS = 9;//发送短信
    public static final int CODE_MULTI_MAINACTIVITY2 = 10;//首页多权限申请
    public static final int CODE_MULTI_LIVESHOW = 11;//直播页面多权限申请
    public static final int CODE_MULTI_PROFILE = 12;//个人信息修改页面权限申请
    public static final int CODE_MIC_RECORD_AUDIO = 13;//聊吧麦克风
    public static final int CODE_MULTI_PERMISSION = 100;//上述多权限申请

    public static final String PERMISSION_GET_ACCOUNTS = Manifest.permission.GET_ACCOUNTS;
    public static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_RECORD_AUDIO = Manifest.permission.RECORD_AUDIO;
    public static final String PERMISSION_READ_PHONE_STATE = Manifest.permission.READ_PHONE_STATE;
    public static final String PERMISSION_CAMERA = Manifest.permission.CAMERA;
    public static final String PERMISSION_ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String PERMISSION_ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String PERMISSION_RECEIVE_SMS = Manifest.permission.RECEIVE_SMS;
    public static final String PERMISSION_SEND_SMS = Manifest.permission.SEND_SMS;
    public static final String PERMISSION_SYSTEM_ALERT_WINDOW = Manifest.permission.SYSTEM_ALERT_WINDOW;


    public static final String[] requestPermissions = {
            PERMISSION_GET_ACCOUNTS,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE,
            PERMISSION_RECORD_AUDIO,
            PERMISSION_CAMERA,
            PERMISSION_READ_PHONE_STATE,
            PERMISSION_ACCESS_FINE_LOCATION,
            PERMISSION_ACCESS_COARSE_LOCATION,
            PERMISSION_RECEIVE_SMS,
            PERMISSION_SEND_SMS,
            PERMISSION_RECORD_AUDIO
    };
    /**
     * 获取SD卡权限和手机状态权限,通讯录
     */
    public static final String[] mainActivity2Permissions = {PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_PHONE_STATE, PERMISSION_ACCESS_FINE_LOCATION};
    /**
     * 获取AUDIO
     */
    public static final String[] micPermissions = {PERMISSION_RECORD_AUDIO};
    /**
     * 获取CAMERA,AUDIO
     */
    public static final String[] liveShowPermissions = {PERMISSION_CAMERA, PERMISSION_RECORD_AUDIO};
    /**
     * 获取CAMERA和SD卡权限
     */
    public static final String[] profileTakePhoto = {PERMISSION_CAMERA, PERMISSION_WRITE_EXTERNAL_STORAGE};

    /**
     * 申请权限结果返回，回调接口
     */
    public interface PermissionGrant {
        void onPermissionGranted(int requestCode);
    }

    /**
     * 申请单个权限
     * Requests permission.
     *
     * @param activity
     * @param requestCode     权限申请操作返回码
     * @param permissionGrant 申请返回后续操作回调接口
     */
    public static void requestPermission(final Activity activity, final int requestCode, PermissionGrant permissionGrant) {
        //判断activity是否为空
        if (activity == null) {
            return;
        }
        //判断返回码是否有效
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            Log.w(TAG, "requestPermission illegal requestCode:" + requestCode);
            return;
        }
        final String requestPermission = requestPermissions[requestCode];
        //如果是6.0以下的手机，ActivityCompat.checkSelfPermission()会始终等于PERMISSION_GRANTED，
        // 但是，如果用户关闭了你申请的权限，ActivityCompat.checkSelfPermission(),会导致程序崩溃(java.lang.RuntimeException: Unknown exception code: 1 msg null)，
        // 你可以使用try{}catch(){},处理异常，也可以在这个地方，低于23就什么都不做，
        // 个人建议try{}catch(){}单独处理，提示用户开启权限。
//        if (Build.VERSION.SDK_INT < 23) {
//            return;
//        }
        int checkSelfPermission;
        try {
            checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
        } catch (RuntimeException e) {
            Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "RuntimeException:" + e.getMessage());
            return;
        }
        //检查权限是否开启
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                /*上次申请用户没有通过授权，再次提醒用户进行授权，如果有必要可以弹出对话框提醒用户，没有必要可以直接申请权限，这里采用直接申请权限*/
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{requestPermission}, requestCode);
            }
            return;
        } else {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    /**
     * 处理多权限申请返回结果
     */
    private static void requestMultiResult(Activity activity, int requestCode, String[] permissions, int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        Map<String, Integer> perms = new HashMap<>();
        ArrayList<String> notGranted = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            perms.put(permissions[i], grantResults[i]);
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                notGranted.add(permissions[i]);
            }
        }
        if (notGranted.size() == 0) {
            permissionGrant.onPermissionGranted(requestCode);
        } else {
            openSettingActivity(activity, activity.getString(R.string.request_permission));
        }

    }

    /**
     * 一次申请多个权限
     */
    public static void requestMultiPermissions(final Activity activity, String[] requestPermissions, PermissionGrant grant, int requestCode) {
        if (requestPermissions.length > 0) {
            boolean isRequest = true;
            for (int i = 0; i < requestPermissions.length; i++) {
                int checkSelfPermission;
                try {
                    checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermissions[i]);
                } catch (RuntimeException e) {
                    Toast.makeText(activity, "please open this permission", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "RuntimeException:" + e.getMessage());
                    return;
                }
                //检查权限是否开启
                if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                    isRequest = false;
                }
            }
            if (isRequest == false) {
                ActivityCompat.requestPermissions(activity, requestPermissions, requestCode);
                return;
            }
            grant.onPermissionGranted(requestCode);
        } else {
            grant.onPermissionGranted(requestCode);
        }
    }


    /**
     * 一次申请多个权限
     */
    private static void requestMultiPermissions(final Activity activity, PermissionGrant grant) {

        /*获取没有授权的权限且没有申请过的权限*/
        final List<String> permissionsList = getNoGrantedPermission(activity, false);
        /*获取没有授权的权限且申请过的权限*/
        final List<String> shouldRationalePermissionsList = getNoGrantedPermission(activity, true);
        if (permissionsList == null || shouldRationalePermissionsList == null) {
            return;
        }
        if (permissionsList.size() > 0) {
            ActivityCompat.requestPermissions(activity, permissionsList.toArray(new String[permissionsList.size()]),
                    CODE_MULTI_PERMISSION);
        } else if (shouldRationalePermissionsList.size() > 0) {
            /*上次申请用户没有通过授权，再次提醒用户进行授权，如果有必要可以弹出对话框提醒用户，没有必要可以直接申请权限，这里采用直接申请权限*/
            ActivityCompat.requestPermissions(activity, shouldRationalePermissionsList.toArray(new String[shouldRationalePermissionsList.size()]),
                    CODE_MULTI_PERMISSION);
        } else {
            grant.onPermissionGranted(CODE_MULTI_PERMISSION);
        }
    }

    /**
     * 显示再次申请提现对话框
     */
    private static void showMessageOKCancel(final Activity context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.confirm), okListener)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();

    }

    /**
     * 显示再次申请提现对话框
     */
    public static void showMessageSdDialog(final Activity context, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancelListener) {
        //不需要调用就启动检查
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            ((BaseActivity) (context)).requestMainActivity2Permission();
//            return;
//        }
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
            ((BaseActivity) (context)).requestMainActivity2Permission();
        }

    }

    /**
     * 处理权限申请返回结果，重写AppCompatActivity里的onRequestPermissionsResult()调用
     *
     * @param activity
     * @param requestCode  Need consistent with requestPermission
     * @param permissions
     * @param grantResults
     */
    public static void requestPermissionsResult(final Activity activity, final int requestCode, @NonNull String[] permissions,
                                                @NonNull int[] grantResults, PermissionGrant permissionGrant) {

        if (activity == null) {
            return;
        }
        //处理多权限申请返回结果
        if (permissions != null && permissions.length > 1) {
            requestMultiResult(activity, requestCode, permissions, grantResults, permissionGrant);
            return;
        }
        if (requestCode < 0 || requestCode >= requestPermissions.length) {
            return;
        }
        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            permissionGrant.onPermissionGranted(requestCode);
        }
    }

    /**
     * 打开当前应用程序详解信息界面
     */
    private static void openSettingActivity(final Activity activity, String message) {
        showMessageOKCancel(activity, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Log.d(TAG, "getPackageName(): " + activity.getPackageName());
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivity(intent);
            }
        });
    }


    /**
     * 获取当前没有授权的权限
     *
     * @param activity
     * @param isShouldRationale true: 返回没有授予且需要再次申请的权限, false:返回没有授予且没有申请授权过的权限
     * @return
     */
    public static ArrayList<String> getNoGrantedPermission(Activity activity, boolean isShouldRationale) {

        ArrayList<String> permissions = new ArrayList<>();

        for (int i = 0; i < requestPermissions.length; i++) {
            String requestPermission = requestPermissions[i];
            int checkSelfPermission = -1;
            try {
                checkSelfPermission = ActivityCompat.checkSelfPermission(activity, requestPermission);
            } catch (RuntimeException e) {
                Toast.makeText(activity, "please open those permission", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, requestPermission)) {
                    if (isShouldRationale) {
                        permissions.add(requestPermission);
                    }

                } else {
                    if (!isShouldRationale) {
                        permissions.add(requestPermission);
                    }
                }
            }
        }
        return permissions;
    }
}
