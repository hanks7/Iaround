
package net.iaround.ui.comon;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * 视频播放页面
 *
 * @author chenlb
 */
public class VideoPlayer extends SuperActivity implements OnClickListener,
        OnPreparedListener, OnCompletionListener, OnErrorListener {
    private VideoView videoView;
    private TextView time;
    private LinearLayout loading;
    private Button playBtn;
    private Button stopBtn;
    private Button cancelBtn;
    private boolean isShowControl = true;

    /**
     * 视频文件的路径
     */
    private String mediaPath;
    private long dataTime;

    private VideoPlayerHandler mHandler;
    /**
     * 消息：视频文件下载完毕
     */
    private final int MSG_DOWNLOAD_VIDEO_FINISH = 1;
    /**
     * 消息：播放出错，退出
     */
    private final int MSG_PLAY_FAIL_QUIT = 2;
    /**
     * 消息：刷新播放时间
     */
    private final int MSG_UPDATE_PLAY_TIME = 3;
    /**
     * 消息：显示控制栏
     */
    private final int MSG_SHOW_CONTROL = 4;
    /**
     * 消息：更新下载栏
     */
    private final int MSG_UPDATA_PROGRESS = 5;


    /**
     * 是否已经下载过视频文件
     */
    private boolean hasDownloadVideo = false;

    private View controlView;
    private PopupWindow controlWin;
    private View rootLayout;


    private TextView progressTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataTime = getIntent().getLongExtra("record_datatime", 0);
        mediaPath = getIntent().getStringExtra("media_url");

        mHandler = new VideoPlayerHandler();
        setContentView(R.layout.video_player);

        initComponent();
        resetFilePath();

        mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROL, 1000);

        if (CommonFunction.isEmptyOrNullStr(mediaPath)) {
            CommonFunction.showToast(this,
                    mActivity.getResString(R.string.video_player_error), 0);
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //
        // 设置为竖屏

        super.onDestroy();
    }

    /**
     * 初始化视图组件
     */
    private void initComponent() {
        rootLayout = findViewById(R.id.rootLayout);
        videoView = (VideoView) findViewById(R.id.videoView);
        progressTextView = (TextView) findViewById(R.id.progress_textview);
        progressBar = (ProgressBar) findViewById(R.id.download_progress_bar);

        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText(R.string.chat_video);

        controlView = LayoutInflater.from(mContext).inflate(R.layout.video_player_control,
                null);
        time = (TextView) controlView.findViewById(R.id.time);
        loading = (LinearLayout) controlView.findViewById(R.id.loading);
        playBtn = (Button) controlView.findViewById(R.id.play);
        stopBtn = (Button) controlView.findViewById(R.id.stop);
        cancelBtn = (Button) controlView.findViewById(R.id.cancel);

        controlWin = new PopupWindow(controlView, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, false);

        playBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    /**
     * 准备视频播放器：准备完毕后，通过监听准备完毕的事件(onPrepared)，立马播放视频
     *
     * @param mediaPath 视频的路径
     */
    private void prepareVideo() {
        time.setVisibility(View.INVISIBLE);
        loading.setVisibility(View.VISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);

        videoView.setOnPreparedListener(this);
        videoView.setOnCompletionListener(this);
        videoView.setOnErrorListener(this);

        videoView.setVideoPath(mediaPath);
    }

    /**
     * 下载视频文件
     *
     * @param mediaUrl
     * @throws IOException
     */
    private void downloadVideo(String mediaUrl) {
        InputStream stream = null;
        FileOutputStream out = null;

        try {
            int unit = 1024;//单位KB
            URLConnection downloadConnect = new URL(mediaUrl).openConnection();
            downloadConnect.connect();
            int length = downloadConnect.getContentLength() / unit;

            Message msg1 = new Message();
            msg1.what = MSG_UPDATA_PROGRESS;
            msg1.arg1 = 0;
            msg1.arg2 = length;
            mHandler.sendMessage(msg1);

            stream = downloadConnect.getInputStream();
            if (stream == null) {
                mHandler.sendEmptyMessage(MSG_PLAY_FAIL_QUIT);
                return;
            }

            // 视频下载后，保存的文件
            File file = new File(PathUtil.getVideoDir());
            if (!file.exists()) {
                file.mkdir();
            }

            //下载过程中的临时文件名，后缀.tmp
            String tmpFileName = dataTime + PathUtil.getTMPPostfix();
            //下载成功后的文件名，后缀.3gp
            String downloadedName = dataTime + PathUtil.get3GPPostfix();

            File downloadedFile = new File(file, downloadedName);
            File tmpFile = new File(file, tmpFileName);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }

            out = new FileOutputStream(tmpFile);
            byte buf[] = new byte[1024];
            int numRead = 0; // 每次读取的流的字节大小
            int current = 0;

            while ((numRead = stream.read(buf)) != -1) {
                out.write(buf, 0, numRead); // 将流写到文件
                current += numRead;
                Message msg2 = new Message();
                msg2.what = MSG_UPDATA_PROGRESS;
                msg2.arg1 = current / unit;
                msg2.arg2 = length;
                mHandler.sendMessage(msg2);
            }

            hasDownloadVideo = true;
            isShowControl = true;
            mHandler.sendEmptyMessageDelayed(MSG_SHOW_CONTROL, 1000);

            //下载完成，修改文件后缀
            tmpFile.renameTo(downloadedFile);

            Message msg = mHandler.obtainMessage(MSG_DOWNLOAD_VIDEO_FINISH,
                    downloadedFile);
            mHandler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_PLAY_FAIL_QUIT);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        controlWin.dismiss();
        super.onBackPressed();
    }

    /**
     * 下载视频线程
     *
     * @author chenlb
     */
    private class DownloadVideoThread extends Thread {
        @Override
        public void run() {
            downloadVideo(mediaPath);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == playBtn) { // 播放
            prepareVideo();
        } else if (v == stopBtn) { // 停止播放
            if (videoView.isPlaying()) {
                videoView.stopPlayback();

                mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);

                time.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.INVISIBLE);
                playBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);
            }
        } else if (v == cancelBtn) { // 退出
            controlWin.dismiss();

            finish();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        videoView.start(); // 开始播放视频

        mHandler.sendEmptyMessage(MSG_UPDATE_PLAY_TIME);

        time.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.INVISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        time.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        playBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.INVISIBLE);

        mHandler.removeMessages(MSG_UPDATE_PLAY_TIME);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (!hasDownloadVideo) { // 没有下载过视频文件，则下载后，尝试播放
            new DownloadVideoThread().start();

            return true;
        }

        mHandler.sendEmptyMessage(MSG_PLAY_FAIL_QUIT);

        return true;
    }


    /**
     * 如果本地文件存在，mediaPath使用本地文件
     */
    private void resetFilePath() {
        String localFilePath = PathUtil.getVideoDir() + dataTime + PathUtil.get3GPPostfix();
        String tmpPath = PathUtil.getVideoDir() + dataTime + PathUtil.getTMPPostfix();
        File tmpFile = new File(tmpPath);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }

        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            mediaPath = localFilePath;
            hasDownloadVideo = true;

            showDownloadLayout(false);
            isShowControl = true;
            prepareVideo();

        } else {
            // 文件本地不存在，开启下载线程
            showDownloadLayout(true);
            isShowControl = false;

            new DownloadVideoThread().start();
        }
    }

    private void showDownloadLayout(boolean isShow) {
        findViewById(R.id.title).setVisibility(isShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.download_layout).setVisibility(isShow ? View.VISIBLE : View.GONE);
        findViewById(R.id.videoView).setVisibility(!isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * 设置播放时间
     *
     * @param time 当前的播放时间（毫秒）
     */
    private void setPlayTime(int time) {
        time /= 1000; // 转换为秒
        int min = time / 60; // 获取分钟
        int sec = time - (min * 60); // 获取秒

        String timeStr = String.format("%02d:%02d", min, sec);
        this.time.setText(timeStr);
    }

    private class VideoPlayerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            File downloadedMedia;

            switch (msg.what) {
                case MSG_DOWNLOAD_VIDEO_FINISH: // 视频文件下载完毕

                    showDownloadLayout(false);
                    downloadedMedia = (File) msg.obj;
                    CommonFunction.log("下载后，需要播放的文件路径：", downloadedMedia.getAbsolutePath());
                    mediaPath = downloadedMedia.getAbsolutePath();
                    prepareVideo(); // 开始播放

                    break;

                case MSG_PLAY_FAIL_QUIT: // 播放出错，退出
                    controlWin.dismiss();
                    CommonFunction.showToast(VideoPlayer.this,
                            getResString(R.string.video_player_error), 1);
                    isShowControl = false;
                    mHandler.removeMessages(MSG_SHOW_CONTROL);
                    finish();
                    break;

                case MSG_SHOW_CONTROL: // 显示控制栏
                    try {
                        if (isShowControl) {
                            controlWin.showAtLocation(rootLayout, Gravity.RIGHT
                                    | Gravity.BOTTOM, 0, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CommonFunction.showToast(VideoPlayer.this,
                                getResString(R.string.video_player_error), 1);
                        finish();
                    }
                    break;

                case MSG_UPDATE_PLAY_TIME: // 刷新播放时间
                    if (videoView.isPlaying()) {
                        setPlayTime(videoView.getCurrentPosition());

                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PLAY_TIME, 990);
                    }
                    break;
                case MSG_UPDATA_PROGRESS:
                    int current = msg.arg1;
                    int total = msg.arg2;
                    int percent = (int) ((float) current / total * 100);
                    progressTextView.setText(current + "KB" + " / " + total + "KB");
                    progressBar.setProgress(percent);
                    break;
            }

            super.handleMessage(msg);
        }
    }

}
