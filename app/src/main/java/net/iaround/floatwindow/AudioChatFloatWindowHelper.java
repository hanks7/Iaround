package net.iaround.floatwindow;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.view.floatwindow.AudioChatFloatWindow;
import net.iaround.utils.DeviceUtils;

/**
 * Created by liangyuanhuan on 25/08/2017.
 * 聊吧最小化悬浮窗口管理模块
 * 创建和移除悬浮窗口要配对
 * 使用窗口内容更新接口，可在非UI线程里更新窗口内容
 */

public class AudioChatFloatWindowHelper {
    private static final String TAG = "AudioChatFloatWindowHelper";

    private static AudioChatFloatWindowHelper sInstance = null;

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

    private IUpdateWindow mIUpdateWindow;

    private Object mLock = new Object();


    private boolean mShowing = false; //正在展示悬浮窗

    private boolean mHinting = false; // 用户是否开启权限

    public static AudioChatFloatWindowHelper getInstance() {
        if (null == sInstance) {
            synchronized (AudioChatFloatWindowHelper.class) {
                if (null == sInstance) {
                    sInstance = new AudioChatFloatWindowHelper();
                }
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context 必须为应用程序的Context.
     */
    public void init(Context context) {
        synchronized (mLock) {
            if (null == mContext) {
                mContext = context;
                mHandler = new Handler(context.getMainLooper());
                mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            }
        }
    }

    /**
     * 销毁所有资源
     */
    public void destroy() {
        synchronized (mLock) {
            //clear phone window
            if (mPhoneView != null) {

                if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                    if (mPhoneView.getParent() != null) {
                        mWindowManager.removeView(mPhoneView);
                    }
                    mPhoneView = null;
                    mPhoneViewParams = null;
                } else {
                    final WindowManager windowManager = mWindowManager;
                    final View fv = mPhoneView;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (fv.getParent() != null) {
                                windowManager.removeView(fv);
                            }
                            mPhoneView = null;
                            mPhoneViewParams = null;
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
                } else {
                    final FloatToast ft = mToast;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (null != ft) {
                                ft.hide();
                                ft.setView(null);
                            }
                            mToastView = null;
                            mToast = null;
                        }
                    });

                }
            }
            AudioChatFloatWindow.getInstance().close();
            mWindowManager = null;
            mContext = null;
            mHandler = null;
            mShowing = false;
        }
    }

    /**
     * 悬浮窗是否显示
     *
     * @return
     */
    public boolean isShowing() {
        return mShowing;
    }

    /**
     * 创建一个小悬浮窗。
     */
    public void createWindow() {

        if (DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >= 23) {
            CommonFunction.log(TAG, "xiaomi create phone window");
            createPhoneWindow();
        } else {
            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
                createPhoneWindow();
            } else { //<= 7.0
                createToastWindow();
            }
        }
    }

//    /**
//     * 移除一个小悬浮窗。
//     *
//     * @param
//     */
//    public void removeWindow(FloatWindowView view) {
//        if (DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >= 23) {
//            //CommonFunction.log(TAG, "xiaomi remove phone window");
//            removePhoneWindow(view);
//        } else {
//            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
//                removePhoneWindow(view);
//            } else { //<= 7.0
//                removeToastWindow(view);
//            }
//        }
//    }


    /*
     * 窗口内容更新接口
     * */
    public interface IUpdateWindow {
        void onUpdateWindow(String time);
    }

    public void setUpdateWindowListener(IUpdateWindow iUpdateWindow) {
        mIUpdateWindow = iUpdateWindow;
    }

    /**
     * 更新悬浮窗内容。
     */
    public void updateWindow(final String time) {
        synchronized (mLock) {
            if (null == mContext)
                return;
            if (mIUpdateWindow != null) {

                if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                    mIUpdateWindow.onUpdateWindow(time);
                } else {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mIUpdateWindow != null) {
                                mIUpdateWindow.onUpdateWindow(time);
                            }
                        }
                    });
                }
            }
        }
    }


    /**
     * 检查悬浮窗权限是否开启。
     *
     * @param
     */
    public boolean checkPermission() {
        if (mContext != null && !Settings.canDrawOverlays(mContext)) {
            log(TAG, "悬浮窗权限未开启");
            return false;
        }
        log(TAG, "悬浮窗权限已开启");
        return true;
    }

    /**
     * 显示开启悬浮窗权限弹窗
     */
    public void showOpenPermissionDialog() {
        Context current = CloseAllActivity.getInstance().getTopActivity();
        final Context fcurrent = current;
        if (null != current) {
            DialogUtil.showTowButtonDialog(current,
                    current.getResources().getString(R.string.prompt),
                    current.getResources().getString(R.string.allow_open_audio_chat_float_window),
                    current.getResources().getString(R.string.cancel),
                    current.getResources().getString(R.string.ok),
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                            fcurrent.startActivity(intent);
                        }
                    });
        }

    }

    private void closeAudioChatActivity() {
        Context current = CloseAllActivity.getInstance().getTopActivity();
        if (current instanceof AudioChatActivity) {
            ((AudioChatActivity) current).finish();
        }
    }

    /**
     * 通知app切换到后台。
     *
     * @param
     */
    public void onAppBackground() {
        log(TAG, "onAppBackground() app goto background");
        if (mContext == null) {
            return;
        }
//        synchronized (mLock) {
//            mOnBackground = true;
//        }
//        if (DeviceUtils.isXiaomi() && Build.VERSION.SDK_INT >= 23) {
//            //CommonFunction.log(TAG, "xiaomi remove phone window");
//            removePhoneWindow(mPhoneView);
//        } else {
//            if (Build.VERSION.SDK_INT > 24) { // >= 7.1
//                removePhoneWindow(mPhoneView);
//            } else { //<= 7.0
//                removeToastWindow(mToastView);
//            }
//        }
    }

    /**
     * 通知app切换到前台。
     *
     * @param
     */
    public void onAppForeground() {
        log(TAG, "onAppForeground() app goto foreground");
        if (mContext == null) {
            return;
        }

//        if (mPhoneView == null && mToastView == null) {
//            FloatWindowView view = ChatBarZoomWindow.getInstance().getView();
//            if (null != view) {
//                createWindow(view);
//            }
//        }
    }


    private void createPhoneWindow() {
        synchronized (mLock) {
            log(TAG, "createPhoneWindow() into ");
            if (null == mContext)
                return;


            if (!checkPermission()) {
                showOpenPermissionDialog();
                return;
            }

            if (mShowing) {
                log(TAG, "createPhoneWindow() showing... return");
                return;
            }

            if (mPhoneView != null) {
                //之前的窗口还没销毁
                log(TAG, "createPhoneWindow() window not remove yet");
                return;
            }
            AudioChatFloatWindow.getInstance().initView();
            FloatWindowView view = AudioChatFloatWindow.getInstance().getmView();
            if (null == view) {
                return;
            }

            if (null == mPhoneViewParams) {
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

            if (mPhoneView != view)
                mPhoneView = view;

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG, "createPhoneWindow() in ui thread");

                if (view.getParent() == null) {
                    mWindowManager.addView(view, params);
                }
                mPhoneView = view;
                mShowing = true;
                closeAudioChatActivity();
            } else {
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
                            mShowing = true;
                            closeAudioChatActivity();
                            log(TAG, "createPhoneWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log(TAG, "createPhoneWindow() out");
        }
    }

//    private void removePhoneWindow(FloatWindowView view) {
//        synchronized (mLock) {
//            log(TAG, "removePhoneWindow() into view=" + view);
//            if (null == mContext)
//                return;
//
//            if (null == view) {
//                return;
//            }
//
//            if (true == mHiding) {
//                log(TAG, "removePhoneWindow() hiding... return");
//                return;
//            }
//
//            if (mPhoneView == null) {
//                return;
//            }
//
//            if (mPhoneView != view) {
//                return;
//            }
//
//
//            mHiding = true;
//
//            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
//                log(TAG, "removePhoneWindow() in ui thread");
//
//                if (view.getParent() != null) {
//                    mWindowManager.removeView(view);
//                }
//                mPhoneView = null;
//                mHiding = false;
//            } else {
//                final WindowManager windowManager = mWindowManager;
//                final View fv = view;
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        synchronized (mLock) {
//                            if (fv.getParent() != null) {
//                                windowManager.removeView(fv);
//                            }
//                            mPhoneView = null;
//                            mHiding = false;
//                            log("WindowManagerHelper", "removePhoneWindow() Main UI thread notify");
//                        }
//                    }
//                });
//            }
//
//            log(TAG, "removePhoneWindow() out");
//        }
//    }

    private void createToastWindow() {
        synchronized (mLock) {
            log(TAG, "createToastWindow() into");
            if (null == mContext) {
                return;
            }


            if (mShowing) {
                log(TAG, "createToastWindow() showing... return");
                return;
            }

            if (mToastView != null) {
                log(TAG, "createToastWindow() window not remove yet");
                return;
            }

            AudioChatFloatWindow.getInstance().initView();
            FloatWindowView view = AudioChatFloatWindow.getInstance().getmView();

            if (view == null) {
                return;
            }

            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
                log(TAG, "createToastWindow() in ui thread");

                if (null == mToast) {
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
                mShowing = true;
                closeAudioChatActivity();
            } else {
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
                            mShowing = true;
                            closeAudioChatActivity();
                            log("WindowManagerHelper", "createToastWindow() Main UI thread notify");
                        }
                    }
                });
            }

            log(TAG, "createToastWindow() out");
        }
    }

//    private void removeToastWindow(FloatWindowView view) {
//        synchronized (mLock) {
//            log(TAG, "removeToastWindow() into view=" + view);
//            if (null == mContext) {
//                return;
//            }
//            if (null == view) {
//                return;
//            }
//            if (mToastView == null) {
//                return;
//            }
//            if (mToastView != view) {
//                return;
//            }
//
//            if (true == mHiding) {
//                log(TAG, "removeToastWindow() hiding... return");
//                return;
//            }
//            mHiding = true;
//
//            if (Looper.myLooper() == Looper.getMainLooper()) { // UI主线程
//                log(TAG, "removeToastWindow() in ui thread");
//                if (mToast != null) {
//                    mToast.hide();
//                    mToast.setView(null);
//                }
//                mToastView = null;
//                mToast = null;
//
//                mHiding = false;
//            } else {
//                log(TAG, "removeToastWindow() not in ui thread");
//                mHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        synchronized (mLock) {
//                            if (null != mToast) {
//                                mToast.hide();
//                                mToast.setView(null);
//                            }
//                            mToastView = null;
//                            mToast = null;
//
//                            mHiding = false;
//                            log(TAG, "removeToastWindow() Main UI thread notify");
//                        }
//                    }
//                });
//            }
//            log(TAG, "removeToastWindow() out");
//        }
//    }

    private AudioChatFloatWindowHelper() {

    }

    private static boolean LOG_ENABLE = true;

    private static void log(String tag, String content) {
        if (LOG_ENABLE) {
            Log.d(tag, content);
        }
    }

}