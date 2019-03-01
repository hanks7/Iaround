package net.iaround.floatwindow;

import android.content.Context;
import android.content.Intent;
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
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.utils.DeviceUtils;

/**
 * Created by liangyuanhuan on 25/08/2017.
 * 聊吧最小化悬浮窗口管理模块
 * 创建和移除悬浮窗口要配对
 * 使用窗口内容更新接口，可在非UI线程里更新窗口内容
 */

public class VideoChatFloatWindowHelper {
    private static final String TAG = "VideoChatFloatWindowHelper";
    /*
    * 窗口内容更新接口
    * */
    public interface IUpdateWindow{
        void onUpdateWindow();
    }

    private static VideoChatFloatWindowHelper sInstance = null;

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

    private Object mLock = new Object();

    //private boolean mOnBackground = true; //APP是否在后台

    private boolean mHiding = false; //正在隐藏悬浮窗
    private boolean mShowing = false; //正在展示悬浮窗

    private boolean mHinting = false; // >=7.1提示用户需要开启权限

    public static VideoChatFloatWindowHelper getInstance(){
        if(null == sInstance){
            synchronized (VideoChatFloatWindowHelper.class){
                if (null == sInstance){
                    sInstance = new VideoChatFloatWindowHelper();
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
            if(null==mContext) {
                mContext = context;
                mHandler = new Handler(context.getMainLooper());
                mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            }
        }
    }

    /**
     * 销毁所有资源
     */
    public void destroy(){
        synchronized (mLock) {
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
        mLock = null;
    }

    /**
     * 创建一个小悬浮窗。
     * @param view 悬浮窗VIEW
     *
     */
    public void createWindow(FloatWindowView view){
        if(DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >=23){
            //CommonFunction.log(TAG, "xiaomi create phone window");
            createPhoneWindow(view);
        }else {
            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
                createPhoneWindow(view);
            } else { //<= 7.0
                createToastWindow(view);
            }
        }
    }

    /**
     * 移除一个小悬浮窗。
     *
     * @param
     *
     */
    public void removeWindow(FloatWindowView view){
        if(DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >=23){
            //CommonFunction.log(TAG, "xiaomi remove phone window");
            removePhoneWindow(view);
        }else {
            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
                removePhoneWindow(view);
            } else { //<= 7.0
                removeToastWindow(view);
            }
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
            log(TAG,"悬浮窗权限未开启");
            if(mHinting==false) {
                mHinting = true;
                Context current = CloseAllActivity.getInstance().getTopActivity();
                if(current instanceof GroupChatTopicActivity){
                    current = CloseAllActivity.getInstance().getSecondActivity();
                }
                final Context fcurrent = current;
                if(null!=current) {
                    DialogUtil.showTowButtonDialog(current,
                            current.getResources().getString(R.string.prompt),
                            current.getResources().getString(R.string.allow_open_chatbar_zoom_window),
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
                                    fcurrent.startActivity(intent);
                                    mHinting = false;
                                }
                            });
                }
            }

            return false;
        }
        log(TAG,"悬浮窗权限已开启");
        return true;
    }

//    /**
//     * 通知app切换到后台。
//     *
//     * @param
//     *
//     */
//    public void onAppBackground() {
//        log(TAG,"onAppBackground() app goto background");
//        if(mContext == null) {
//            return;
//        }
//        synchronized (mLock) {
//            mOnBackground = true;
//        }
//        if(DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >=23){
//            //CommonFunction.log(TAG, "xiaomi remove phone window");
//            removePhoneWindow(mPhoneView);
//        }else {
//            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
//                removePhoneWindow(mPhoneView);
//            } else { //<= 7.0
//                removeToastWindow(mToastView);
//            }
//        }
//    }
//
//    /**
//     * 通知app切换到前台。
//     *
//     * @param
//     *
//     */
//    public void onAppForeground() {
//        log(TAG,"onAppForeground() app goto foreground");
//        if(mContext == null) {
//            return;
//        }
//        synchronized (mLock){
//            mOnBackground = false;
//        }
//
//        if(mPhoneView == null && mToastView == null) {
//            FloatWindowView view = ChatBarZoomWindow.getInstance().getView();
//            if (null != view) {
//                createWindow(view);
//            }
//        }
//    }


    private void createPhoneWindow(FloatWindowView view){
        synchronized (mLock) {
            log(TAG,"createPhoneWindow() into view=" + view );
            if(null == mContext)
                return;

            if(null == view) {
                return;
            }

//            if(mOnBackground){
//                return;
//            }

            if(false == checkPermission()) {
                return;
            }

            if(true == mShowing){
                log(TAG, "createPhoneWindow() showing... return");
                return;
            }

            if(mPhoneView != null) {
                //之前的窗口还没销毁
                log(TAG, "createPhoneWindow() window not remove yet");
                return ;
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
            view.setCanMove(true);

            if(mPhoneView!=view)
                mPhoneView = view;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG,"createPhoneWindow() in ui thread");

                if (view.getParent() == null) {
                    mWindowManager.addView(view, params);
                }
                mPhoneView = view;
                mShowing = false;
            }else{
                final WindowManager windowManager = mWindowManager;
                final FloatWindowView fv = view;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (mLock) {
                            if (fv.getParent() == null) {
                                windowManager.addView(fv, params);
                            }
                            mPhoneView = fv;
                            mShowing = false;
                            log(TAG, "createPhoneWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log(TAG,"createPhoneWindow() out");
        }
    }

    private void removePhoneWindow(FloatWindowView view){
        synchronized (mLock) {
            log(TAG,"removePhoneWindow() into view=" + view);
            if (null == mContext)
                return;

            if (null == view) {
                return;
            }

            if(true == mHiding){
                log(TAG,"removePhoneWindow() hiding... return");
                return;
            }

            if (mPhoneView == null){
                return;
            }

            if (mPhoneView != view) {
                return;
            }


            mHiding = true;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG,"removePhoneWindow() in ui thread");

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

            log(TAG,"removePhoneWindow() out");
        }
    }

    private void createToastWindow(FloatWindowView view){
        synchronized (mLock) {
            log(TAG,"createToastWindow() into");
            if (null == mContext) {
                log(TAG,"createToastWindow() mContext null");
                return;
            }
            if(view==null){
                log(TAG,"createToastWindow() view null");
                return;
            }

//            if(mOnBackground){
//                log(TAG,"createToastWindow() mOnBackground true");
//                return;
//            }

            if(true == mShowing){
                log(TAG, "createToastWindow() showing... return");
                return;
            }

            if(mToastView!=null){
                log(TAG, "createToastWindow() window not remove yet");
                return;
            }

            mShowing = true;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG,"createToastWindow() in ui thread");

                if(null == mToast) {
                    mToast = FloatToast.makeToast(mContext);
                }
                mToast.setView(view);
                mToast.setX(view.getScreenLeft());
                mToast.setY(view.getScreenTop());
                mToast.setWidth(view.getViewWidth());
                mToast.setHeight(view.getViewHeight());
                mToast.show();
                view.setParams(mToast.getLayoutParams());
                view.setCanMove(true);
                mToastView = view;
                mShowing = false;
            }else{
                final FloatWindowView fv = view;
                final Context context = mContext;
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
                            fv.setParams(mToast.getLayoutParams());
                            mToastView = fv;
                            mShowing = false;
                            log("WindowManagerHelper", "createToastWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log(TAG,"createToastWindow() out");
        }
    }

    private void removeToastWindow(FloatWindowView view){
        synchronized (mLock) {
            log(TAG,"removeToastWindow() into view="+view);
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
                log(TAG,"removeToastWindow() hiding... return");
                return;
            }
            mHiding = true;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG,"removeToastWindow() in ui thread");
                if(mToast!=null) {
                    mToast.hide();
                    mToast.setView(null);
                }
                mToastView = null;
                mToast = null;

                mHiding = false;
            }else{
                log(TAG,"removeToastWindow() not in ui thread");
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
                            log(TAG,"removeToastWindow() Main UI thread notify");
                        }
                    }
                });
            }
            log(TAG,"removeToastWindow() out");
        }
    }

    private VideoChatFloatWindowHelper(){

    }

    private static boolean LOG_ENABLE = true;
    private static void log(String tag, String content){
        if(LOG_ENABLE){
            Log.d(tag, content);
        }
    }

}