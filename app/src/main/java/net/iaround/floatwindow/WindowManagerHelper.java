package net.iaround.floatwindow;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;


import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.CloseAllActivity;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by liangyuanhuan on 25/08/2017.
 * 悬浮窗口管理模块
 * 先在 Application 里初始化本模块
 * 创建和移除悬浮窗口要配对
 * 使用窗口内容更新接口，可在非UI线程里更新窗口内容
 */

public class WindowManagerHelper {

    /*
    * 窗口内容更新接口
    * */
    public interface IUpdateWindow{
        void onUpdateWindow();
    }

    private static WindowManagerHelper sInstance = null;

    //Application context
    private Context mContext = null;

    //用于在UI线程中更新悬浮窗内容。
    private Handler mHandler = null;

    //用于控制在屏幕上添加或移除悬浮窗
    private WindowManager mWindowManager = null;

    /* Phone 窗口*/
    FloatWindowView mPhoneView = null;
    LayoutParams mPhoneViewParams = null;

    /* Toast 窗口*/
    private FloatToast mToast = null;
    private FloatWindowView mToastView = null;

    /* 定时器 */
    private Timer mTimer = null;

    private Object mLock = new Object();
//    private Object mNotify = new Object();

    private boolean mOnBackground = true;

    private boolean mHiding = false; //正在隐藏悬浮窗
    private boolean mShowing = false; //正在展示悬浮窗

    private boolean mHinting = false; // >=7.1提示用户需要开启权限

    public static WindowManagerHelper getInstance(){
        if(null == sInstance){
            synchronized (WindowManagerHelper.class){
                if (null == sInstance){
                    sInstance = new WindowManagerHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     * @param context 必须为应用程序的Context.
     *
     */
    public void init(Context context){
        synchronized (mLock) {
            mContext = context;
            mHandler = new Handler(context.getMainLooper());
            mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        }
    }

    /**
     * 销毁所有资源
     */
    public void destroy(){
        synchronized (mLock) {
            if (mTimer != null) {
                mTimer.cancel();
                mTimer = null;
            }

            //clear phone window
            if (mPhoneView != null) {
                mPhoneView = null;
                mPhoneViewParams = null;
                if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                    if (mPhoneView.getParent() != null) {
                        mWindowManager.removeView(mPhoneView);
                    }
                }else{
                    final WindowManager windowManager = mWindowManager;
                    final View fv = mPhoneView;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (fv.getParent() != null) {
                                windowManager.removeView(fv);
                            }
                        }
                    });
                }

            }
            //clear toast window
            if (mToastView != null) {
                if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                    mToast.hide();
                    mToast.setView(null);
                    mToastView = null;
                    mToast = null;
                }else{
                    final FloatToast ft = mToast;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(null!=ft) {
                                ft.hide();
                                ft.setView(null);
                            }
                        }
                    });
                    mToastView = null;
                    mToast = null;
                }
            }
            mWindowManager = null;
            mContext = null;
            mHandler = null;
        }
        //mNotify = null;
        mLock = null;
    }

    /**
     * 创建一个小悬浮窗。
     *
     * @param showtime 单位毫秒 窗口显示的时间 0 表示一直显示
     *
     */
    public void createWindow(FloatWindowView view, int showtime){
        if(Build.VERSION.SDK_INT > 24) { // >= 7.1
            createPhoneWindow(view,showtime);
        } else{ //<= 7.0
            createToastWindow(view,showtime);
        }
    }

    /**
     * 移除一个小悬浮窗。
     *
     * @param
     *
     */
    public void removeWindow(FloatWindowView view){
        if(Build.VERSION.SDK_INT > 24) { // >= 7.1
            removePhoneWindow(view);
        } else{ //<= 7.0
            removeToastWindow(view);
        }
    }

    /**
     * 更新悬浮窗内容。
     *
     * @param callback 调用者实现的回调接口，在接口内对UI内容更新
     *
     */
    public void updateWindow(IUpdateWindow callback){
        synchronized (mLock) {
            if (null == mContext)
                return;
            final IUpdateWindow fb = callback;
            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                fb.onUpdateWindow();
            }else{
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        fb.onUpdateWindow();
                    }
                });
            }
        }
    }


    /**
     * 检查悬浮窗权限是否开启。
     *
     * @param
     *
     */
    public boolean checkPermission(){
        if (!Settings.canDrawOverlays(mContext)) {
            log("FloatWindowView","悬浮窗权限未开启");
            if(mHinting==false) {
                mHinting = true;
//                log("FloatWindowView","show dialog...");
                Context current = CloseAllActivity.getInstance().getTopActivity();
                if(null!=current) {
                    DialogUtil.showTowButtonDialog(current,
                            current.getResources().getString(R.string.prompt),
                            current.getResources().getString(R.string.allow_open_alert_window),
                            current.getResources().getString(R.string.cancel),
                            current.getResources().getString(R.string.ok),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mHinting = false;
                                }
                            }, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                    mContext.startActivity(intent);
                                    mHinting = false;
                                }
                            });
                }
            }

            return false;
        }
        log("FloatWindowView","悬浮窗权限已开启");
        return true;
    }

    /**
     * 通知app切换到后台。
     *
     * @param
     *
     */
    public void onAppBackground() {
        log("WindowManagerHelper","onAppBackground() app goto background");
        if(null == mContext){
            return;
        }
        synchronized (mLock) {
            mOnBackground = true;
        }

        if(Build.VERSION.SDK_INT > 24) { // >= 7.1
            removePhoneWindow(mPhoneView);
        } else{ //<= 7.0
            removeToastWindow(mToastView);
        }
    }

    /**
     * 通知app切换到前台。
     *
     * @param
     *
     */
    public void onAppForeground() {
        log("WindowManagerHelper","onAppForeground() app goto foreground");
        if(null == mContext){
            return;
        }
        synchronized (mLock){
            mOnBackground = false;
        }
    }


    private void createPhoneWindow(FloatWindowView view, int time){
        synchronized (mLock) {
            log("WindowManagerHelper","createPhoneWindow() into view=" + view + ", time=" + time);
            if(null == mContext)
                return;

            if(null == view)
                return;

            if(time <0)
                return;

            if(mOnBackground){
                return;
            }

            if(false == checkPermission()) {
                return;
            }

            if(true == mShowing){
                log("WindowManagerHelper", "createPhoneWindow() showing... return");
                return;
            }

            if(mPhoneView != null) {
                if(view != mPhoneView) {
                    //之前的窗口还没销毁
                    log("WindowManagerHelper", "createPhoneWindow() window not remove yet");
                    return;
                }else{
                    //view只是想更新内容 重置定时器
                    if(mTimer!=null){
                        mTimer.cancel();
                        mTimer = new Timer();
                        mTimer.schedule(new CloseWindowTask(), time);
                        log("WindowManagerHelper", "createPhoneWindow() reset timer");
                        return;
                    }
                }
            }

            mShowing = true;

            if(null == mPhoneViewParams){
                mPhoneViewParams = new LayoutParams();
                if(Build.VERSION.SDK_INT >= 26){//8.0新特性
                    mPhoneViewParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
                }else {
                    mPhoneViewParams.type = LayoutParams.TYPE_PHONE; //LayoutParams.TYPE_TOAST;
                }
                mPhoneViewParams.format = PixelFormat.TRANSLUCENT;
                mPhoneViewParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
                mPhoneViewParams.gravity = Gravity.LEFT | Gravity.TOP;
            }

            //设置坐标和大小
            final LayoutParams params = mPhoneViewParams; //new LayoutParams();
            params.width = view.getViewWidth();
            params.height = view.getViewHeight();
            params.x = view.getScreenLeft();
            params.y = view.getScreenTop();

            view.setParams(params);

            if(mPhoneView!=view)
                mPhoneView = view;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log("WindowManagerHelper","createPhoneWindow() in ui thread");

                if (view.getParent() == null) {
                    mWindowManager.addView(view, params);
                }
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
                if (time > 0) {
                    mTimer = new Timer();
                    mTimer.schedule(new CloseWindowTask(), time);
                }
                mPhoneView = view;
                mShowing = false;
            }else{
                final WindowManager windowManager = mWindowManager;
                final FloatWindowView fv = view;
                final int ftime = time;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {
                            if (fv.getParent() == null) {
                                windowManager.addView(fv, params);
                            }
                            if (mTimer != null) {
                                mTimer.cancel();
                                mTimer = null;
                            }
                            if (ftime > 0) {
                                mTimer = new Timer();
                                mTimer.schedule(new CloseWindowTask(), ftime);
                            }
                            mPhoneView = fv;
                            mShowing = false;
                            log("WindowManagerHelper", "createPhoneWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log("WindowManagerHelper","createPhoneWindow() out");
        }
    }

    private void removePhoneWindow(FloatWindowView view){
        synchronized (mLock) {
            log("WindowManagerHelper","removePhoneWindow() into view=" + view);
            if (null == mContext)
                return;

            if (null == view) {
                return;
            }

            if(true == mHiding){
                log("WindowManagerHelper","removePhoneWindow() hiding... return");
                return;
            }

            if (mPhoneView == null){
                return;
            }

            if (mPhoneView != view) {
                return;
            }


            mHiding = true;

            if(mTimer!=null){
                mTimer.cancel();
                mTimer = null;
            }

//            mPhoneView = null;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log("WindowManagerHelper","removePhoneWindow() in ui thread");

                if (view.getParent() != null) {
                    mWindowManager.removeView(view);
                }
                mPhoneView = null;
                mHiding = false;
            }else{
                final WindowManager windowManager = mWindowManager;
                final View fv = view;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {
                            if (fv.getParent() != null) {
                                windowManager.removeView(fv);
                            }
                            mPhoneView = null;
                            mHiding = false;
                            log("WindowManagerHelper", "removePhoneWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log("WindowManagerHelper","removePhoneWindow() out");
        }
    }

    private void createToastWindow(FloatWindowView view, int time){
        synchronized (mLock) {
            log("WindowManagerHelper","createToastWindow() into view="+view+" time=" + time);
            if (null == mContext) {
                return;
            }
            if(time<0){
                return;
            }
            if(null==view){
                return;
            }
            if(mOnBackground){
                return;
            }

            if(true == mShowing){
                log("WindowManagerHelper", "createToastWindow() showing... return");
                return;
            }

            if(mToastView!=null){
                if (mToastView != view) {
                    log("WindowManagerHelper", "createToastWindow() window not remove yet");
                    return;
                }else{
                    //view只是想更新内容 重置定时器
                    if(mTimer!=null){
                        mTimer.cancel();
                        mTimer = new Timer();
                        mTimer.schedule(new CloseWindowTask(), time);
                        log("WindowManagerHelper", "createToastWindow() reset timer");
                        return;
                    }
                }
            }

            mShowing = true;

//            if(mToastView!=view)
//                mToastView = view;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log("WindowManagerHelper","createToastWindow() in ui thread");

                if(null == mToast) {
                    mToast = FloatToast.makeToast(mContext);
                }
                mToast.setView(view);
                mToast.setX(view.getScreenLeft());
                mToast.setY(view.getScreenTop());
                mToast.setWidth(view.getViewWidth());
                mToast.setHeight(view.getViewHeight());
                mToast.show();

                if(mTimer!=null){
                    mTimer.cancel();
                    mTimer = null;
                }
                if(time>0) {
                    mTimer = new Timer();
                    mTimer.schedule(new CloseWindowTask(), time);
                }
                mToastView = view;
                mShowing = false;
            }else{
                final FloatWindowView fv = view;
                final Context context = mContext;
                final int ftime = time;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {

                            if (null == mToast) {
                                mToast = FloatToast.makeToast(context);
                            }
                            mToast.setView(fv);
                            mToast.setX(fv.getScreenLeft());
                            mToast.setY(fv.getScreenTop());
                            mToast.setWidth(fv.getViewWidth());
                            mToast.setHeight(fv.getViewHeight());
                            mToast.show();

                            if(mTimer!=null){
                                mTimer.cancel();
                                mTimer = null;
                            }
                            if(ftime>0) {
                                mTimer = new Timer();
                                mTimer.schedule(new CloseWindowTask(), ftime);
                            }
                            mToastView = fv;
                            mShowing = false;
                            log("WindowManagerHelper", "createToastWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log("WindowManagerHelper","createToastWindow() out");
        }
    }

    private void removeToastWindow(FloatWindowView view){
        synchronized (mLock) {
            log("WindowManagerHelper","removeToastWindow() into view="+view);
            if (null == mContext) {
                return;
            }
            if (null == view) {
                return;
            }
            if (mToastView == null){
                return;
            }
            if (mToastView != view) {
                return;
            }

            if(true == mHiding){
                log("WindowManagerHelper","removeToastWindow() hiding... return");
                return;
            }
            mHiding = true;

            if(mTimer!=null){
                mTimer.cancel();
                mTimer = null;
            }

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log("WindowManagerHelper","removeToastWindow() in ui thread");
                if(mToast!=null) {
                    mToast.hide();
                    mToast.setView(null);
                }
                mToastView = null;
                mToast = null;

                mHiding = false;
            }else{
                log("WindowManagerHelper","removeToastWindow() not in ui thread");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {
                            if(null != mToast) {
                                mToast.hide();
                                mToast.setView(null);
                            }
                            mToastView = null;
                            mToast = null;

                            mHiding = false;
                            log("WindowManagerHelper","removeToastWindow() Main UI thread notify");
                        }
                    }
                });
            }
            log("WindowManagerHelper","removeToastWindow() out");
        }
    }

    private WindowManagerHelper(){

    }

    class CloseWindowTask extends TimerTask{
        @Override
        public void run() {
            log("WindowManagerHelper","CloseWindowTask() time bingo");

            if(Build.VERSION.SDK_INT > 24) { // >= 7.1
                removePhoneWindow(mPhoneView);
            } else{ //<= 7.0
                removeToastWindow(mToastView);
            }
        }
    }

    private static boolean LOG_ENABLE = false;
    private static void log(String tag, String content){
        if(LOG_ENABLE){
            Log.d(tag, content);
        }
    }

}