package net.iaround.tools.im;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PathUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @ClassName AudioPlayUtils.java
 * @Description: 语音播放工具类，如果文件不存在会去下载
 */

public class AudioPlayUtils implements OnCompletionListener, OnPreparedListener,
        DownloadFileCallback, OnErrorListener, SensorEventListener {

    private final static String TAG = AudioPlayUtils.class.getSimpleName();

    //记得release
    private AudioManager audioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Context mContext;
    public Handler mHandler;
    private static MediaPlayer mediaPlayer;
    private static AudioPlayUtils instance;

    //存放当前播放的Url
    private String currentPlayUrl;
    //文件下载后存放的位置,文件名为Url的md5
    private String downloadPath;

    private Timer mTimer;
    private CountdownTimerTask mCountdownTask;

    //音频播放的回调
    private AudioPlayStateCallback audioCallback;

    private AudioPlayUtils() {
        // TODO Auto-generated constructor stub
        mHandler = new Handler(Looper.getMainLooper());
    }

    public static AudioPlayUtils getInstance() {
        if (instance == null) {
            instance = new AudioPlayUtils();
        }
        return instance;
    }

    /**
     * 播放音频的接口
     *
     * @param mContext
     * @param url                文件的url
     * @param isInItPlayDuration
     * @param callback           播放的回调
     */
    public void playAudio(Context mContext, String url, boolean isInItPlayDuration, AudioPlayStateCallback callback) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        initManager(mContext);
        if (isInItPlayDuration) {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            initPlayResources(url, callback);
            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if (audioCallback != null) {
                        audioCallback.getPlayDuration(mediaPlayer.getDuration() / 1000);
                    }
                    stopPlaying();
                }
            });
        } else {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                initPlayResources(url, callback);
                mediaPlayer.setOnPreparedListener(this);
                mediaPlayer.setOnCompletionListener(this);
            } else {
                if (isPlayingAudio()) {
                    pausePlaying();
                } else {
                    startPlay();
                }
            }
        }
    }

    public void initPlayResources(String url, AudioPlayStateCallback callback) {
        this.audioCallback = callback;
        String fileUrl = url;
        currentPlayUrl = url;
        downloadPath = PathUtil.getAudioDir(); //文件下载后存放的位置,文件名为Url的md5
        if (url.contains(PathUtil.getHTTPPrefix())) {
            fileUrl = downloadPath + CryptoUtil.md5(url) + PathUtil.getMP3Postfix();
        }
        File audioFile = new File(fileUrl);
        if (audioFile.exists()) {
            initMediaPlayer(fileUrl);
        } else {
            //下载
            new DownLoadAuidoThread(mContext, url, this).start();
        }
    }

    private void initManager(Context mContext) {
        if (this.mContext == null || !this.mContext.equals(mContext)) {
            this.mContext = mContext;
        }

        if (audioManager == null) {
            audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
            //需要判断设置了的听筒模式
            if (!audioManager.isSpeakerphoneOn()) {
                audioManager.setSpeakerphoneOn(true);
            }
            audioManager.setMode(AudioManager.MODE_NORMAL);
        }

        if (mSensorManager == null) {
            mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
            if (mSensor == null) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            }
            //是否要注册监听器需要判断
            mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void initMediaPlayer(String url) {
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            if (audioCallback != null) {
                audioCallback.OnPlayError();
                resetConfig();
            }
        }
    }

    /**
     * 是否正在播放音频
     */
    public boolean isPlayingAudio() {
        if (mediaPlayer != null)
            return mediaPlayer.isPlaying();
        else
            return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        // TODO Auto-generated method stub
        startPlay();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        if (audioCallback != null) {
            audioCallback.onPlayCompleted(mediaPlayer.getDuration() / 1000);
        }
        stopPlaying();
    }

    @Override
    public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDownloadFileFinish(int flag, String fileName, String savePath) {
        // TODO Auto-generated method stub

        String completedFileName = "";
        if (fileName.contains(PathUtil.getTMPPostfix())) {
            int endIndex = fileName.lastIndexOf(PathUtil.getTMPPostfix());
            completedFileName =
                    fileName.subSequence(0, endIndex) + PathUtil.getMP3Postfix();
            File tmpFile = new File(savePath + fileName);
            final File completedFile = new File(savePath + completedFileName);
            try {
                completedFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            if (tmpFile.exists()) {
                boolean isSuccess = tmpFile.renameTo(completedFile);
            }
            if (flag == currentPlayUrl.hashCode()) {
                if (completedFile.exists()) {
                    initMediaPlayer(completedFile.getAbsolutePath());
                }
            }
        }
    }

    @Override
    public void onDownloadFileError(int flag, String fileName, String savePath) {
        // TODO Auto-generated method stub

    }

    //音频播放的回调
    public interface AudioPlayStateCallback {
        /**
         * 获取时长
         *
         * @param duration
         */
        void getPlayDuration(int duration);

        /**
         * 暂停播放
         */
        void onPauseStarted();

        /**
         * 播放进度
         *
         * @param mCountTime
         */
        void OnPlayingProgress(int mCountTime);

        /**
         * 播放完毕
         *
         * @param duration
         */
        void onPlayCompleted(int duration);

        /**
         * 播放失败
         */
        void OnPlayError();
    }

    //Audio下载线程
    class DownLoadAuidoThread extends Thread {

        private Context context;
        private DownloadFileCallback callback;
        private String fileUrl;

        public DownLoadAuidoThread(Context context, String url, DownloadFileCallback callback) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.callback = callback;
            fileUrl = url;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String fileName = CryptoUtil.md5(fileUrl);
            //临时文件名
            String fileNameTemp = fileName + PathUtil.getTMPPostfix();
            try {
                FileDownloadManager manager = new FileDownloadManager(context, callback, fileUrl, fileNameTemp, downloadPath, fileUrl.hashCode());
                manager.run();
            } catch (ConnectionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            super.run();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        float range = event.values[0];

        if (mContext == null || audioManager == null) {
            return;
        }
//        if (audioManager.isSpeakerphoneOn()) {
//            audioManager.setSpeakerphoneOn(false);
//        }

        CommonFunction.log(TAG, "mSensor.getMaximumRange()："+ mSensor.getMaximumRange() + "， range："+range);

        if (range >= mSensor.getMaximumRange()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);//正常模式
            audioManager.setSpeakerphoneOn(true);
        } else {
            int now = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);//听筒模式
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, now, 0);
            audioManager.setSpeakerphoneOn(false);
        }
    }

    /*倒计时任务 */
    class CountdownTimerTask extends TimerTask {

        private WeakReference<Context> mContext = null;

        public CountdownTimerTask(Context context) {
            mContext = new WeakReference<>(context);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "CountdownTimerTask() time bingo");
            Context context = mContext.get();
            if (context != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Context context = mContext.get();
                        if (context != null) {
                            doCountdown();
                        }
                    }
                });
            }
        }
    }

    private void doCountdown() { //时间进度不对
        if (mediaPlayer != null) {
            CommonFunction.log(TAG, "getCurrentPosition: " + mediaPlayer.getCurrentPosition() / 1000 + "  mAudioDuration:" + mediaPlayer.getDuration());
            int mCountTime = (mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()) / 1000;//倒计时
            if (audioCallback != null) {
                audioCallback.OnPlayingProgress(mCountTime);
            }
        }
    }

    /**
     * 开始播放
     */
    public void startPlay(){
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
        showCountdown();
    }

    /**
     * 停止播放
     */
    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        stopCountdown();
    }

    /**
     * 暂停播放
     */
    private void pausePlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
        stopCountdown();
        if (this.audioCallback != null) {
            this.audioCallback.onPauseStarted();
        }
    }

    /**
     * 重置mediaPlayer
     */
    private void resetConfig() {
        audioCallback = null;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    /**
     * 释放资源
     */
    public void releaseRes() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
        mSensor = null;
        audioManager = null;
        mSensorManager = null;
        mContext = null;
        audioCallback = null;
        currentPlayUrl = null;
        stopCountdown();
    }

    /**
     * 初始化计时器
     */
    private void showCountdown() {
        mTimer = new Timer();
        mCountdownTask = new CountdownTimerTask(mContext);
        mTimer.schedule(mCountdownTask, 0, 1000);
    }

    /**
     * 停止计时
     */
    private void stopCountdown() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
            mCountdownTask = null;
        }
    }
}

