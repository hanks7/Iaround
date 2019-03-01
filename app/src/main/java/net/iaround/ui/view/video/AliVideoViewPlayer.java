package net.iaround.ui.view.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;
import com.alivc.player.MediaPlayer.MediaType;

import cn.finalteam.galleryfinal.utils.Utils;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 类描述：视频播放控制类
 *
 * @author Super南仔
 * @time 2016-9-19
 */
public class AliVideoViewPlayer extends RelativeLayout {
    private static String TAG = "AliVideoViewPlayer";

    private Context context;
    private View contentView;
    private AliVcMediaPlayer mPlayer;
    private ProgressBar progressBar;
    private String url;
    private Query $;
    private int STATUS_ERROR = -1;
    private int STATUS_IDLE = 0;
    private int STATUS_LOADING = 1;
    private int STATUS_PLAYING = 2;
    private int STATUS_PAUSE = 3;
    private int STATUS_COMPLETED = 4;
    private int status = STATUS_IDLE;

    private OnInitListener onInitListener;

    private SurfaceView mSurfaceView = null;

    private boolean mIsPlayerDestroy = true;

    public AliVideoViewPlayer(Context context) {
        this(context, null);
    }

    public AliVideoViewPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AliVideoViewPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        if (UPDATE_PROGRESS_TIMER == null)
            UPDATE_PROGRESS_TIMER = new Timer();
        if (mProgressTimerTask == null)
            mProgressTimerTask = new ProgressTimerTask();
        UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 100);
        //初始化view和其他相关的
        initView();
    }

    private Timer UPDATE_PROGRESS_TIMER;
    private ProgressTimerTask mProgressTimerTask;

    private boolean initPlay = true;
    private boolean isWifiShow = true;

    private int currentPosition;

    private void doPauseResume() {
        if (status == STATUS_COMPLETED) {
            seekTo(0);
            start();
        } else if (mPlayer.isPlaying()) {
            statusChange(STATUS_PAUSE);
            mPlayer.pause();
        } else {
            start();
        }
    }

    @SuppressWarnings("HandlerLeak")
    private Handler handler = new Handler() ;

    /**
     * 初始化视图
     */
    @SuppressLint({"WrongViewCast", "NewApi"})
    public void initView() {
        contentView = View.inflate(context.getApplicationContext(), R.layout.view_ali_video_view, this);

        $ = new Query(contentView);

        mSurfaceView = (SurfaceView) contentView.findViewById(R.id.video_view);

        mSurfaceView.getHolder().addCallback(mSurfaceHolderCB);

        startToPlay();

        progressBar = (ProgressBar) contentView.findViewById(R.id.app_video_seekBar);
        progressBar.setMax(1000);
        $.id(R.id.app_video_play).clicked(new OnClickListener() {
            @Override
            public void onClick(View v) {
                $.id(R.id.app_video_play).gone();
                doPauseResume();
            }
        });

    }

    private SurfaceHolder.Callback mSurfaceHolderCB = new SurfaceHolder.Callback() {
        @SuppressWarnings("deprecation")
        public void surfaceCreated(SurfaceHolder holder) {
//                holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);
            holder.setKeepScreenOn(true);
            CommonFunction.log(TAG, "surfaceCreated ");
            // Important: surfaceView changed from background to front, we need reset surface to mediaplayer.
            // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
            if (mPlayer != null) {
                mPlayer.setVideoSurface(holder.getSurface());
            }

        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

            CommonFunction.log(TAG, "surfaceChanged ");
            if (mPlayer != null) {
                mPlayer.setSurfaceChanged();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            CommonFunction.log(TAG, "surfaceDestroyed ");
        }
    };

    private boolean startToPlay() {
        CommonFunction.log(TAG, "start play.");
//        resetUI();

        if (mPlayer == null) {
            // 初始化播放器
            mPlayer = new AliVcMediaPlayer(context, mSurfaceView);
            mPlayer.setPreparedListener(new VideoPreparedListener(this));
            mPlayer.setErrorListener(new VideoErrorListener(this));
            mPlayer.setInfoListener(new VideoInfolistener(this));
            mPlayer.setSeekCompleteListener(new VideoSeekCompletelistener());
            mPlayer.setCompletedListener(new VideoCompletelistener(this));
            mPlayer.setVideoSizeChangeListener(new VideoSizeChangelistener(this));
            mPlayer.setBufferingUpdateListener(new VideoBufferUpdatelistener());
            mPlayer.setStoppedListener(new VideoStoppedListener());
            mPlayer.setFrameInfoListener(new VideoFrameInfoListener(this));
            mPlayer.setCircleStartListener(new VideoCircleStartListener());


            if (Config.DEBUG){
                // 重点: 在调试阶段可以使用以下方法打开native log
                mPlayer.enableNativeLog();
            }else{
                mPlayer.disableNativeLog();
            }
            mPlayer.setDefaultDecoder(1);

            mPlayer.setMediaType(MediaType.Vod);

            setPlayerDestroy(false);
        }
        return true;
    }

    private void resetUI() {
        progressBar.setProgress(0);
    }

    private synchronized boolean isPlayerDestroy(){
        return mIsPlayerDestroy;
    }

    private synchronized void setPlayerDestroy(boolean destroy){
        mIsPlayerDestroy = destroy;
    }

    /**
     * 准备完成监听器:调度更新进度
     */
    static class VideoPreparedListener implements MediaPlayer.MediaPlayerPreparedListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoPreparedListener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        @Override
        public void onPrepared() {
            CommonFunction.log("AliVideoViewPlayer", "onPrepared() into");
            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
            if(aliVideoViewPlayer==null) return;
            if(aliVideoViewPlayer.isPlayerDestroy()) return;

            if (aliVideoViewPlayer.mPlayer != null) {
                aliVideoViewPlayer.mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT);
                if (aliVideoViewPlayer.onInitListener != null) {
                    aliVideoViewPlayer.onInitListener.onInit();
                }
            }
        }
    }


    /**
     * 错误处理监听器
     */
    static class VideoErrorListener implements MediaPlayer.MediaPlayerErrorListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoErrorListener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        @Override
        public void onError(int i, String s) {
            CommonFunction.log("AliVideoViewPlayer","onError() into, i=" + i + ", s=" +s);
            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
            if(aliVideoViewPlayer==null)return;
            if(aliVideoViewPlayer.isPlayerDestroy()) return;

            int errCode = 0;
            aliVideoViewPlayer.statusChange(aliVideoViewPlayer.STATUS_ERROR);
            if (aliVideoViewPlayer.mPlayer == null) {
                return;
            }

            errCode = aliVideoViewPlayer.mPlayer.getErrorCode();
            switch (errCode) {
                case MediaPlayer.ALIVC_ERR_LOADING_TIMEOUT:
                    CommonFunction.log("AliVideoViewPlayer","onError() 缓冲超时,请确认网络连接正常后重试");
                    aliVideoViewPlayer.mPlayer.reset();
                    break;
//                case MediaPlayer.ALIVC_ERR_NO_INPUTFILE:
//                    CommonFunction.log(TAG,"no input file");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_VIEW:
//                    CommonFunction.log(TAG,"no surface");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_INVALID_INPUTFILE:
//                    CommonFunction.log(TAG,"视频资源或者网络不可用");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_SUPPORT_CODEC:
//                    CommonFunction.log(TAG,"no codec");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_FUNCTION_DENIED:
//                    CommonFunction.log(TAG,"no priority");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_UNKNOWN:
//                    CommonFunction.log(TAG,"unknown error");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_NO_NETWORK:
//                    CommonFunction.log(TAG,"视频资源或者网络不可用");
//                    mPlayer.reset();
//                    break;
//                case MediaPlayer.ALIVC_ERR_ILLEGALSTATUS:
//                    CommonFunction.log(TAG,"illegal call");
//                    break;
//                case MediaPlayer.ALIVC_ERR_NOTAUTH:
//                    CommonFunction.log(TAG,"auth failed");
//                    break;
//                case MediaPlayer.ALIVC_ERR_READD:
//                    CommonFunction.log(TAG,"资源访问失败,请重试");
//                    mPlayer.reset();
//                    break;
                default:
                    break;

            }
        }
    }

    /**
     * 信息通知监听器:重点是缓存开始/结束
     */
    static class VideoInfolistener implements MediaPlayer.MediaPlayerInfoListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoInfolistener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        public void onInfo(int what, int extra) {
            CommonFunction.log("AliVideoViewPlayer", "onInfo() into, what = " + what + " extra = " + extra);
            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
            if(aliVideoViewPlayer==null)return;
            if(aliVideoViewPlayer.isPlayerDestroy()) return;
            aliVideoViewPlayer.$.id(R.id.app_video_play).visible();
            switch (what) {
                case MediaPlayer.MEDIA_INFO_UNKNOW:
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    //pause();

                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    //start();

                    break;
                case MediaPlayer.MEDIA_INFO_TRACKING_LAGGING:
                    break;
                case MediaPlayer.MEDIA_INFO_NETWORK_ERROR:

                    break;
                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    if (aliVideoViewPlayer.mPlayer != null)
                        CommonFunction.log("AliVideoViewPlayer", "on Info first render start : " + ((long) aliVideoViewPlayer.mPlayer.getPropertyDouble(AliVcMediaPlayer.FFP_PROP_DOUBLE_1st_VFRAME_SHOW_TIME, -1) - (long) aliVideoViewPlayer.mPlayer.getPropertyDouble(AliVcMediaPlayer.FFP_PROP_DOUBLE_OPEN_STREAM_TIME, -1)));
                    aliVideoViewPlayer.statusChange(aliVideoViewPlayer.STATUS_LOADING);
                    break;
            }
        }
    }

    /**
     * 快进完成监听器
     */
    static class VideoSeekCompletelistener implements MediaPlayer.MediaPlayerSeekCompleteListener {

        public void onSeekCompleted() {

        }
    }

    /**
     * 视频播完监听器
     */
    static class VideoCompletelistener implements MediaPlayer.MediaPlayerCompletedListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoCompletelistener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        public void onCompleted() {
            CommonFunction.log("AliVideoViewPlayer", "onCompleted() into");
            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
            if(aliVideoViewPlayer==null)return;
            if(aliVideoViewPlayer.isPlayerDestroy()) return;
            aliVideoViewPlayer.statusChange(aliVideoViewPlayer.STATUS_COMPLETED);
            aliVideoViewPlayer.currentProgress = 0;

            aliVideoViewPlayer.mPlayer.stop();
            aliVideoViewPlayer.mPlayer.prepareAndPlay(aliVideoViewPlayer.url);
            aliVideoViewPlayer.mPlayer.setMuteMode(true);
            aliVideoViewPlayer.mPlayer.play();

            aliVideoViewPlayer.resetUI();
            aliVideoViewPlayer.seekTo(0);
        }
    }

    /**
     * 视频大小变化监听器
     */
    static class VideoSizeChangelistener implements MediaPlayer.MediaPlayerVideoSizeChangeListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoSizeChangelistener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        public void onVideoSizeChange(int width, int height) {
            CommonFunction.log("AliVideoViewPlayer", "onVideoSizeChange() into, width = " + width + " height = " + height);
//            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
//            if(aliVideoViewPlayer==null)return;
//            if(aliVideoViewPlayer.isPlayerDestroy()) return;

        }
    }

    /**
     * 视频缓存变化监听器: percent 为 0~100之间的数字】
     */
    static class VideoBufferUpdatelistener implements MediaPlayer.MediaPlayerBufferingUpdateListener {

        public void onBufferingUpdateListener(int percent) {

        }
    }

    static class VideoStoppedListener implements MediaPlayer.MediaPlayerStoppedListener {
        @Override
        public void onStopped() {
            CommonFunction.log("AliVideoViewPlayer", "onStopped() into");
        }
    }

    static class VideoFrameInfoListener implements MediaPlayer.MediaPlayerFrameInfoListener {
        private WeakReference<AliVideoViewPlayer> mAliVideoViewPlayer;

        public VideoFrameInfoListener(AliVideoViewPlayer view){
            mAliVideoViewPlayer = new WeakReference<AliVideoViewPlayer>(view);
        }

        @Override
        public void onFrameInfoListener() {
            CommonFunction.log("AliVideoViewPlayer", "onFrameInfoListener() into");
            AliVideoViewPlayer aliVideoViewPlayer = mAliVideoViewPlayer.get();
            if(aliVideoViewPlayer==null)return;
            if(aliVideoViewPlayer.isPlayerDestroy()) return;
            aliVideoViewPlayer.mPlayer.pause();
            aliVideoViewPlayer.mPlayer.setMuteMode(false);
            if (aliVideoViewPlayer.onInitListener != null){
                aliVideoViewPlayer.onInitListener.onInit();
            }
        }
    }

    static class VideoCircleStartListener implements MediaPlayer.MediaPlayerCircleStartListener {

        @Override
        public void onCircleStart() {
            CommonFunction.log("AliVideoViewPlayer", "onCircleStart() into");

        }
    }

    /**
     * 重新加载
     * @param mp
     */
    private void reLoading(final MediaPlayer mp){
        if (handler == null)return;
        if (status != STATUS_PLAYING){
            mp.setVolume(0);
            seekTo(currentPosition);
            start();
            initPlay = true;

            try {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mPlayer.pause();
                        statusChange(STATUS_PAUSE);
                        $.id(R.id.app_video_play).visible();
                        mp.setVolume(1);
                    }
                },100);
            }catch (IllegalStateException e){
                e.printStackTrace();
            }

        }
    }

    /**
     * 视频播放状态的改变
     *
     * @param newStatus
     */
    private void statusChange(int newStatus) {
        status = newStatus;
        if (newStatus == STATUS_COMPLETED) {// 当视频播放完成的时候

        } else if (newStatus == STATUS_ERROR) {

        } else if (newStatus == STATUS_LOADING) {

        } else if (newStatus == STATUS_PLAYING) {

            $.id(R.id.app_video_play).invisible();
        }

        CommonFunction.log(TAG,"status = "+status);

    }

    /**
     * 暂停
     */
    public void onPause() {
        if (mPlayer == null) return;
        if (status == STATUS_PLAYING) {
            mPlayer.pause();
            $.id(R.id.app_video_play).visible();
            currentPosition = mPlayer.getCurrentPosition();
        }
    }

    public void onResume() {
        if (mPlayer == null) return;

        if (currentPosition > 0) {
            mPlayer.seekTo(currentPosition);
        }
    }


    /**
     * 在activity中的onDestroy中需要回调
     */
    public void onDestroy() {

        // 重点:在 activity destroy的时候,要停止播放器并释放播放器
        if (mPlayer != null) {
            currentProgress = mPlayer.getCurrentPosition();
            setPlayerDestroy(true);
            mPlayer.destroy();
        }

        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER.cancel();
            UPDATE_PROGRESS_TIMER.purge();
            UPDATE_PROGRESS_TIMER = null;
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask.cancel();
            mProgressTimerTask = null;
        }

    }

    /**
     * 开始播放
     *
     * @param url 播放视频的地址
     */
    public void play(String url) {
        this.url = url;

        if (mPlayer != null){
            mPlayer.setMuteMode(true);
            mPlayer.prepareToPlay(url);
            mPlayer.play();
        }
    }

    /**
     * 切换播放地址
     * @param url
     */
    public void switchPlay(String url){
        this.url = url;
        if (mPlayer != null){
            mPlayer.prepareToPlay(url);
        }
    }

    public int currentProgress;

    private long setProgress() {

        if (mPlayer == null)return 0;
        long position = mPlayer.getCurrentPosition();
        long duration = mPlayer.getDuration();
        if (!initPlay && progressBar != null) {
            if (duration > 0) {
                long pos = 1000L * position / duration;
                if (currentProgress < pos) {
                    if (pos > 0) {
                        pos = pos + 60;
                    }
                    progressBar.setProgress((int) pos);
                    currentProgress = (int) pos;
                }

            }
        }

        return position;
    }

    public void start() {
        if (!CommonFunction.isNetworkConnected(BaseApplication.appContext)){
            $.id(R.id.app_video_play).visible();
            CommonFunction.toastMsg(BaseApplication.appContext,R.string.network_req_failed);
            return;
        }
        if (isWifiShow && getContext() != null && !Utils.isWifiConnected(getContext())) {
            Toast.makeText(getContext(), getResources().getString(R.string.start_play_hint), Toast.LENGTH_SHORT).show();
            isWifiShow = false;
        }
        if(mPlayer!=null)mPlayer.play();
        $.id(R.id.app_video_play).invisible();
        statusChange(STATUS_PLAYING);
        initPlay = false;
    }

    /**
     * 播放或者暂停
     */
    public void playOrPause() {
        if (mPlayer.isPlaying()) {
            statusChange(STATUS_PAUSE);
            mPlayer.pause();
            $.id(R.id.app_video_play).visible();
        } else {
            $.id(R.id.app_video_play).gone();
            statusChange(STATUS_PLAYING);
            start();
        }
    }

    public void pause() {
        mPlayer.pause();
        statusChange(STATUS_PAUSE);
        $.id(R.id.app_video_play).visible();
    }


    static class Query {
        private WeakReference<View> mContentView;
        private View view = null;

        public Query(View contentView) {
            mContentView = new WeakReference<View>(contentView);
        }

        public Query id(int id) {
            View contentView = mContentView.get();
            if(contentView != null) {
                view = contentView.findViewById(id);
            }
            return this;
        }

        public Query image(int resId) {
            if (view != null && view instanceof ImageView) {
                ((ImageView) view).setImageResource(resId);
            }
            return this;
        }

        public Query visible() {
            if (view != null) {
                view.setVisibility(View.VISIBLE);
            }
            return this;
        }

        public Query gone() {
            if (view != null) {
                view.setVisibility(View.GONE);
            }
            return this;
        }

        public Query invisible() {
            if (view != null) {
                view.setVisibility(View.INVISIBLE);
            }
            return this;
        }

        public Query clicked(OnClickListener handler) {
            if (view != null) {
                view.setOnClickListener(handler);
            }
            return this;
        }

        public Query text(CharSequence text) {
            if (view != null && view instanceof TextView) {
                ((TextView) view).setText(text);
            }
            return this;
        }

        public Query visibility(int visible) {
            if (view != null) {
                view.setVisibility(visible);
            }
            return this;
        }
    }

    /**
     * 是否正在播放
     *
     * @return
     */
    public boolean isPlaying() {
        return mPlayer != null ? mPlayer.isPlaying() : false;
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    /**
     * 停止播放，（仅仅对列表播放）
     */
    public void stopPlayVideo() {
        if (this != null) {
            mPlayer.stop();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        mPlayer.seekTo(0);
    }

    /**
     * seekTo position
     *
     * @param msec millisecond
     */
    public void seekTo(int msec) {
        if (mPlayer == null)return;
        CommonFunction.log(TAG,"seek msec = "+msec);
        mPlayer.seekTo(msec);
        currentPosition = msec;
    }

    /**
     * 获取视频的总长度
     *
     * @return
     */
    public int getDuration() {
        return mPlayer.getDuration();
    }

    /**
     * 当前播放的长度
     *
     * @return
     */
    public int getCurrentPosition() {
        return mPlayer.getCurrentPosition();
    }


    public interface OnInitListener {
        void onInit();
    }

    public void setOnInitListener(OnInitListener onInitListener) {
        this.onInitListener = onInitListener;
    }

    /**
     * 获得某个控件
     *
     * @param ViewId
     * @return
     */
    public View getView(int ViewId) {
        Activity activity = (Activity)context;
        return activity.findViewById(ViewId);
    }

    private class ProgressTimerTask extends TimerTask {
        @Override
        public void run() {
            if (status == STATUS_PLAYING){

                setProgress();
            }
        }

    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

}
