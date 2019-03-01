package net.iaround.ui.view.popupwin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.connector.AudioRecordThread;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.interfaces.AudioRecordCallBack;
import net.iaround.ui.view.AudioControlView;
import net.iaround.utils.Mp3Lame;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yz on 2018/8/11.
 * 录制音频popupWindow 只有界面动画，录制音频逻辑在QualificationActivity
 */

public class RecordAudioPopupWindow implements AudioRecordCallBack {
    private static final String TAG = "RecordAudioPopupWindow";

    // 最大的录音时间（秒）
    public static int MAX_RECORD_TIME = 20;

    private Context mContext;

    private PopupWindow mWindow;
    private View mContentView;
    private AudioControlView mAcvControlView;
    private TextView mTvTime;
    private ImageView mIvPlay;
    private ImageView mIvSave;
    private TextView mTvClose;

    // 录音器
    private AudioRecord audioRecord = null;
    // 音频获取源
    private int audioSource = MediaRecorder.AudioSource.MIC;
    // 设置音频采样率:44100, 22050，16000，11025
    private static int sampleRateInHz = 16000;
    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    // 缓冲区字节大小
    public static int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    // 音频录制线程
    private AudioRecordThread threadRecord = null;
    // 开始录制时间
    private long recordStartTime;

    private int mCountTime;//计时时间
    private int mRecordDuration;//录音时长

    private Timer mTimer;
    private CountdownTimerTask mCountdownTask;
    private Handler mHandler = new Handler();
    private boolean isCountdown = false;//是否是倒计时 false-正计时 true-倒计时

    private MediaPlayer mMediaPlayer = null;
    private boolean isPlaying = false;
    private String mFilePath;

    private OnSaveAudioListener mOnSaveAudioListener;

    public RecordAudioPopupWindow(Context context) {
        this.mContext = context;
        mContentView = LayoutInflater.from(mContext).inflate(R.layout.record_audio_popup, null, false);
        initView();
        mWindow = new PopupWindow(mContentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWindow.setOutsideTouchable(true);
        mWindow.setTouchable(true);
        mWindow.setFocusable(false);

        mAcvControlView = (AudioControlView) mContentView.findViewById(R.id.acv_control_view);
        mTvTime = (TextView) mContentView.findViewById(R.id.tv_time);
//        mAcvControlView.bringToFront();
        initListener();

    }

    private void initView() {
        mAcvControlView = (AudioControlView) mContentView.findViewById(R.id.acv_control_view);
        mTvTime = (TextView) mContentView.findViewById(R.id.tv_time);
        mIvPlay = (ImageView) mContentView.findViewById(R.id.iv_play);
        mIvSave = (ImageView) mContentView.findViewById(R.id.iv_save);
        mTvClose = (TextView) mContentView.findViewById(R.id.tv_close);

    }

    private void initListener() {
        mAcvControlView.setOnRecordListener(new AudioControlView.OnRecordListener() {
            @Override
            public void onShortClick() {
                CommonFunction.log(TAG, "点击事件");
            }

            @Override
            public void OnRecordStartClick() {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.record_start);
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    stopPlaying();
                }
                startRecording();
                mCountTime = 0;
                mIvSave.setVisibility(View.INVISIBLE);
                mIvPlay.setVisibility(View.INVISIBLE);
            }

            @Override
            public void OnFinish(int resultCode) {
                switch (resultCode) {
                    case 0:
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.record_time_too_short);
                        stopRecord();
                        mCountTime = 0;
                        mTvTime.setText("00:00");
                        break;
                    case 1:
                        CommonFunction.toastMsg(BaseApplication.appContext, R.string.record_end);
                        CommonFunction.log(TAG, "录制结束");
                        stopRecord();
                        mIvSave.setVisibility(View.VISIBLE);
                        mIvPlay.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });

        mIvPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(mFilePath)){
                    isCountdown = true;
                    onPlay(isPlaying);
                    isPlaying = !isPlaying;
                    if(isPlaying){
                        mIvPlay.setImageResource(R.drawable.icon_pause);
                    }else {
                        mIvPlay.setImageResource(R.drawable.icon_play);
                    }
                }
            }
        });

        mIvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSaveAudioListener != null) {
                    mOnSaveAudioListener.onSave(mRecordDuration, mFilePath);
                }
                mWindow.dismiss();
            }
        });

        mWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    return true;
                }
                return false;
            }
        });

        mTvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWindow.dismiss();
            }
        });
        mWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilePath = null;
                if(mMediaPlayer!=null){
                    stopPlaying();
                }else {
                    stopCountdown();
                }
                releaseAudioRecorder();
            }
        });


    }

    public void setOnSaveAudioListener(OnSaveAudioListener listener) {
        mOnSaveAudioListener = listener;
    }

    public interface OnSaveAudioListener {
        void onSave(int duration, String filePath);
    }

    public void show() {
        mWindow.showAtLocation(((Activity) mContext).getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mTvTime.setText("00:00");
        mIvPlay.setVisibility(View.INVISIBLE);
        mIvSave.setVisibility(View.INVISIBLE);
        initAudioRecord();
    }

    public PopupWindow getWindow() {
        return mWindow;
    }

    /*倒计时任务
  * */
    static class CountdownTimerTask extends TimerTask {
        private WeakReference<RecordAudioPopupWindow> mWindow = null;

        public CountdownTimerTask(RecordAudioPopupWindow window) {
            mWindow = new WeakReference<RecordAudioPopupWindow>(window);
        }

        @Override
        public void run() {
            CommonFunction.log("", "CountdownTimerTask() time bingo");
            final RecordAudioPopupWindow window = mWindow.get();
            if (mWindow != null) {
                window.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        RecordAudioPopupWindow window = mWindow.get();
                        if (window != null) {
                            if (window.getWindow().isShowing()) {
                                window.doCountdown();
                            }
                        }
                    }
                });
            }
        }
    }

    private void doCountdown() {
        if (isCountdown) {
            mCountTime--;//倒计时
        } else {
            mCountTime++;//正常计时
        }
        if (mCountTime >= 0) {
            if (mCountTime > MAX_RECORD_TIME) {
                mCountTime = MAX_RECORD_TIME;
            }
            mTvTime.setText("00:" + String.format("%02d", mCountTime));
        } else {
            mTvTime.setText("00:00");
        }
    }


    //初始化计时器
    private void showCountdown() {
        if (mTimer == null) {
            mTimer = new Timer();
        }
        mCountdownTask = new CountdownTimerTask(this);
        mTimer.schedule(mCountdownTask, 1000, 1000);
    }

    private void stopCountdown() {
        if (mTimer != null) {
            mTimer.cancel();
            mCountdownTask = null;
            mTimer = null;
        }
    }

    /***播放部分***/
    // Play start/stop
    private void onPlay(boolean isPlaying) {
        if (!isPlaying) {
            //currently MediaPlayer is not playing audio
            if (mMediaPlayer == null) {
                startPlaying(); //start from beginning
            } else {
                resumePlaying(); //resume the currently paused MediaPlayer
            }

            showCountdown();
        } else {
            pausePlaying();
        }
    }

    private void startPlaying() {
        if (mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();
        }

        try {
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepare();

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mMediaPlayer.start();
                }
            });
        } catch (IOException e) {
            Log.e("recorder_test", "prepare() failed");
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopPlaying();
                mCountTime = mRecordDuration;
                mTvTime.setText("00:" + String.format("%02d", mCountTime));
            }
        });


    }

    private void pausePlaying() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            stopCountdown();
        }
    }

    private void resumePlaying() {
        if (mMediaPlayer == null) {
            mCountTime = mRecordDuration;
            startPlaying();
        } else {
            mMediaPlayer.start();
        }
    }

    private void stopPlaying() {
        mIvPlay.setImageResource(R.drawable.icon_play);
        stopCountdown();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        isPlaying = !isPlaying;

    }


    /***录音部分***/

    private void initAudioRecord() {
        CommonFunction.log(TAG, "initAudioRecord() into");
        // 创建录音器
        audioRecord = new AudioRecord(audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes);
        // 初始化lame
        Mp3Lame.initEncoder(1, sampleRateInHz, 128, 1, 3);
    }

    private void startRecording() {
        CommonFunction.log(TAG, "startRecording() into");
        if (audioRecord == null || isRecording()) {
            CommonFunction.log(TAG, "startRecording() record null or recording");
            return;
        }

        // 初始化录制开始时间
        recordStartTime = System.currentTimeMillis();

        // 开始录音
        audioRecord.startRecording();

        // 开启音频文件编码写入线程
        threadRecord = new AudioRecordThread(audioRecord, recordStartTime, this);
        threadRecord.start();

    }

    public void stopRecord() {
        CommonFunction.log(TAG, "stopRecord() into");
        if (audioRecord != null && isRecording()) {
            threadRecord.bActive = false;
            audioRecord.stop();// 停止录制
        }
    }

    /**
     * 释放录音器资源
     */
    public void releaseAudioRecorder() {
        CommonFunction.log(TAG, "releaseAudioRecorder() into");
        if (audioRecord != null) {
            if (isRecordInited()) {
                if (isRecording()) {
                    stopRecord();// 停止录制,判断是否正在录音
                }
            }
            audioRecord.release();// 释放资源
            audioRecord = null;
            // 析构lame
            Mp3Lame.destroyEncoder();
        }
    }

    /**
     * 判断AudioRecord是否初始化
     */
    private boolean isRecordInited() {
        if (audioRecord == null)
            return false;
        return audioRecord.getRecordingState() == AudioRecord.STATE_INITIALIZED;
    }

    /**
     * 判断AudioRecord是否正在录音
     */
    private boolean isRecording() {
        if (audioRecord == null)
            return false;
        return audioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING;
    }

    /**
     * 返回录音的时长(秒)
     *
     * @return
     */
    private int getRecordTime() {
        int time = Math.round((System.currentTimeMillis() - recordStartTime) / 1000);
        if (time < 1) {
            time = 0;
        }
        if (time > MAX_RECORD_TIME) {
            time = MAX_RECORD_TIME;
        }
        return time;
    }

    @Override
    public void AudioRecordStart(long flag, String filePath) {
        isCountdown = false;
        showCountdown();
    }

    @Override
    public void AudioVolumeFeedback(double volume) {
        //音量回调
    }

    @Override
    public void AudioRecordError(long flag) {

    }

    @Override
    public void AudioRecordEnd(long flag, String filePath) {
        mFilePath = filePath;
        mCountTime = getRecordTime();
        mRecordDuration = mCountTime;
        stopCountdown();

    }
}
