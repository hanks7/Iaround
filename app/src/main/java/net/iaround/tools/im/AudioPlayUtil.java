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

import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PathUtil;

import java.io.File;
import java.io.IOException;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-10 下午5:19:50
 * @ClassName AudioPlayUtil.java
 * @Description: 语音播放工具类，如果文件不存在会去下载
 */

public class AudioPlayUtil implements OnCompletionListener, OnPreparedListener,
        DownloadFileCallback, OnErrorListener, SensorEventListener {
    //记得release
    private AudioManager audioManager;
    private SensorManager mSensorManager;
    private Sensor mSensor;

    private Context mContext;

    private static MediaPlayer mediaPlayer;
    private static AudioPlayUtil instance;

    //存放当前播放的Url
    private String currentPlayUrl;
    //文件下载后存放的位置,文件名为Url的md5
    private String downloadPath;

    //音频播放的回调
    private AudioPlayStateCallback audioCallback;
    //文件下载的回调
    private DownloadFileCallback downloadCallback;

    private AudioPlayUtil() {
        // TODO Auto-generated constructor stub
    }

    public static AudioPlayUtil getInstance() {
        if (instance == null) {
            instance = new AudioPlayUtil();
        }
        return instance;
    }


    public void playAudio(Context mContext, String url) {
        playAudio(mContext, url, null);
    }

    public void playAudio(Context mContext, String url, AudioPlayStateCallback playCallback) {
        playAudio(mContext, url, playCallback, null);
    }

    /**
     * 播放音频的接口
     *
     * @param mContext
     * @param url              文件的url
     * @param callback         播放的回调
     * @param downloadCallback 下载的回调
     */
    public void playAudio(Context mContext, String url, AudioPlayStateCallback callback, DownloadFileCallback downloadCallback) {
        initManager(mContext);
        downloadPath = PathUtil.getAudioDir();
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.setOnPreparedListener(this);
        }


        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            if (downloadCallback != null) {
                downloadCallback.onDownloadFileFinish(1, "", "");
            }
            if (this.audioCallback != null) {
                this.audioCallback.onPlayCompleted();
            }


            if (url == currentPlayUrl) {

            } else {
                currentPlayUrl = url;
                String fileUrl = url;
                this.audioCallback = callback;
                this.downloadCallback = downloadCallback;

                if (url.contains(PathUtil.getHTTPPrefix())) {
                    fileUrl = downloadPath + CryptoUtil.md5(url) + PathUtil.getMP3Postfix();
                }

                File audioFile = new File(fileUrl);
                if (audioFile.exists()) {
                    initMediaPlayer(audioFile.getAbsolutePath());
                } else {
                    //下载
                    new DownLoadAuidoThread(mContext, url, this).start();
                }
            }

        } else {
            mediaPlayer.reset();
            if (downloadCallback != null) {
                downloadCallback.onDownloadFileFinish(1, "", "");
            }

            String fileUrl = url;
            currentPlayUrl = url;
            this.audioCallback = callback;
            this.downloadCallback = downloadCallback;

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
     * 停止播放音频
     */
    public void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
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
        mp.start();
        if (audioCallback != null) {
            audioCallback.OnPlayStarted();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        // TODO Auto-generated method stub
        if (audioCallback != null) {
            audioCallback.onPlayCompleted();
        }

    }

    private void resetConfig() {
        audioCallback = null;
        downloadCallback = null;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
        }
    }

    public void releaseRes() {
        if (mediaPlayer != null) {
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
        //资源释放
        mContext = null;
        audioCallback = null;
        downloadCallback = null;
    }

    @Override
    public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag) {
        // TODO Auto-generated method stub
        if (downloadCallback != null && flag == currentPlayUrl.hashCode()) {
            downloadCallback.onDownloadFileProgress(lenghtOfFile, LengthOfDownloaded, flag);
        }
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

            if (downloadCallback != null && flag == currentPlayUrl.hashCode()) {
                if (completedFile.exists()) {
                    initMediaPlayer(completedFile.getAbsolutePath());
                }

                downloadCallback.onDownloadFileFinish(flag, completedFileName, savePath);
            }
        }
    }

    @Override
    public void onDownloadFileError(int flag, String fileName, String savePath) {
        // TODO Auto-generated method stub
        if (downloadCallback != null) {
            downloadCallback.onDownloadFileError(flag, fileName, savePath);
        }

    }

    //音频播放的回调
    public interface AudioPlayStateCallback {
        /**
         * 开始播放
         */
        void OnPlayStarted();

        /**
         * 正在播放
         */
        void OnPlayingProgress();

        /**
         * 正在播放完毕
         */
        void onPlayCompleted();

        /**
         * 正在播放失败
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
}

