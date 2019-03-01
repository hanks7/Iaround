package net.iaround.tools;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import net.iaround.R;

public class VerCodeTimerTask {

    public static Context mContext;

    // 常量
    public static final int EXPIRED_TIME = 61; // 验证码超时时间,从60s开始
    public static final int COUNTDOWN_INTERVAL = 1000; // 倒计时时间间隔

    // 消息类型
    public static final int MSG_BASE = 100;
    public static final int MSG_COUNT_DOWN = MSG_BASE + 1; // 倒计时的消息标识

    // 变脸相关
    public static int mCountdownLeft; // 倒计时剩余时间
    public static Timer mTimer; // 计时任务

    public static TextView sendCodeBtn;

    public static String verCode;
    public static boolean isRestVar;


    public static void setTimer(Context context, View view) {
        mContext = context;
        sendCodeBtn = (TextView) view;

        verCode = sendCodeBtn.getText().toString();

        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                mCountdownLeft--;
                mHandler.sendEmptyMessage(MSG_COUNT_DOWN);
            }
        }, 0, COUNTDOWN_INTERVAL);
        // 设置倒计时时间
        mCountdownLeft = EXPIRED_TIME;
        // 设置验证按钮状态
        sendCodeBtn.setEnabled(false);
    }

    // 消息处理器
    @SuppressLint("HandlerLeak")
    private static Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_COUNT_DOWN: // 倒计时消息

                    isRestVar = verCode.equals(mContext.getResources().getString(R.string.register_ver_send_code));
                    if (mCountdownLeft <= 0) { // 倒计时结束
                        // 取消倒计时任务
                        if (mTimer != null) {
                            mTimer.cancel();
                            mTimer = null;
                        }
                        // 更改态验证码按钮状态和文字
                        sendCodeBtn.setText(mContext.getString(R.string.register_ver_re_send_code));
                        sendCodeBtn.setEnabled(true);
                        sendCodeBtn.setTextColor(mContext.getResources().getColor(R.color.common_grey));
                    } else { // 倒计时仍在进行

                        if (!isRestVar) {
                            sendCodeBtn.setText(String.format(mContext.getString(R.string.register_ver_again_send), mCountdownLeft));
                        } else {
                            sendCodeBtn.setText(mCountdownLeft + "s");
                        }

                    }
                    break;
            }
        }
    };

}
