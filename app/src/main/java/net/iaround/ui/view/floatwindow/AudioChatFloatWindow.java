package net.iaround.ui.view.floatwindow;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.floatwindow.AudioChatFloatWindowHelper;
import net.iaround.floatwindow.FloatWindowView;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.AudioChatActivity;
import net.iaround.ui.activity.CloseAllActivity;

/**
 * 语音聊天最小化窗口
 */
public class AudioChatFloatWindow implements AudioChatFloatWindowHelper.IUpdateWindow {
    private static final String TAG = "AudioChatFloatWindow";

    private FloatWindowView mView;
    private static AudioChatFloatWindow sInstance;
    private boolean mIsInit;
    private TextView mTvTime;
    private Handler mHandler;
    private boolean isShown = false;

    private AudioChatFloatWindow() {

    }

    public static AudioChatFloatWindow getInstance() {
        if (sInstance == null) {
            synchronized (AudioChatFloatWindow.class) {
                if (sInstance == null) {
                    sInstance = new AudioChatFloatWindow();
                }
            }
        }
        return sInstance;
    }

    public synchronized void initView() {
        CommonFunction.log(TAG, "initView " + mIsInit);
        if (mIsInit) {
            return;
        }
//        mHandler = new Handler(Looper.getMainLooper());
        mIsInit = true;
        mView = (FloatWindowView) LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.view_audio_chat_float_window, null);
        mTvTime = (TextView) mView.findViewById(R.id.tv_time);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAudioChat();
            }
        });
        AudioChatFloatWindowHelper.getInstance().setUpdateWindowListener(this);
//        AudioChatFloatWindowHelper.getInstance().createWindow(mView);
    }

    public FloatWindowView getmView(){
        return mView;
    }

    /**
     * 跳转到语音聊天界面
     */
    private void openAudioChat() {
        Activity activity = CloseAllActivity.getInstance().getTopActivity();
        Intent intent = new Intent(activity, AudioChatActivity.class);
        intent.putExtra("status", AudioChatActivity.STATUS_CONNECT);
        activity.startActivity(intent);
    }


    public boolean isShown() {
        return isShown;
    }

    /**
     * 更新通话时长
     * @param time
     */
    @Override
    public void onUpdateWindow(String time) {
        mTvTime.setText(time);
    }

//    /**
//     * 更新通话时长
//     * @param time
//     */
//    public synchronized void setCurrentDuration(final String time) {
//        if (mTvTime != null) {
//            mHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    mTvTime.setText(time);
//
//                }
//            });
//        }
//    }

    public synchronized void close() {
        isShown = false;
        mIsInit = false;
        AudioChatFloatWindowHelper.getInstance().setUpdateWindowListener(null);
//        AudioChatFloatWindowHelper.getInstance().destroy();
    }
}
