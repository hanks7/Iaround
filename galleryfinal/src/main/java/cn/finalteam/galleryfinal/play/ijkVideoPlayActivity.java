//package cn.finalteam.galleryfinal.play;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.WindowManager;
//
//import java.io.File;
//
//import cn.finalteam.galleryfinal.GalleryFinal;
//import cn.finalteam.galleryfinal.R;
//import cn.jzvd.JZVideoPlayer;
//import cn.jzvd.JZVideoPlayerStandard;
//import cn.jzvd.demo.CustomMediaPlayer.CustomMediaPlayerAssertFolder;
//import cn.jzvd.demo.CustomMediaPlayer.JZMediaIjkplayer;
//import cn.jzvd.demo.CustomView.MyJZVideoPlayerStandard;
//
///**
// * 视频播放控件
// * Created by Administrator on 2017/11/29.
// */
//
//public class ijkVideoPlayActivity extends Activity {
//
//    private MyJZVideoPlayerStandard jzVideoPlayerStandard;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.ijk_activity_video_play);
//        String video_path = getIntent().getStringExtra("video_path");
//
//        jzVideoPlayerStandard = (MyJZVideoPlayerStandard) findViewById(R.id.video_view);
//
////        video_path = "http://www.jmzsjy.com/UploadFile/%E5%BE%AE%E8%AF%BE/%E5%9C%B0%E6%96%B9%E9%A3%8E%E5%91%B3%E5%B0%8F%E5%90%83%E2%80%94%E2%80%94%E5%AE%AB%E5%BB%B7%E9%A6%99%E9%85%A5%E7%89%9B%E8%82%89%E9%A5%BC.mp4";
//        // 创建File
//        File mFile = new File(video_path);
//        // 取得文件名
//        String fileName = mFile.getName();
//        jzVideoPlayerStandard.setUp(video_path
//                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, fileName);
//        jzVideoPlayerStandard.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//               jzVideoPlayerStandard.startVideo();
//            }
//        },300);
//
//        jzVideoPlayerStandard.setVideoImageDisplayType(JZVideoPlayer.VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL);
//
//        GalleryFinal.getCoreConfig().getImageLoader().loadImageFrame(getApplication(),video_path,jzVideoPlayerStandard.thumbImageView);
//
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        JZVideoPlayer.setMediaInterface(new JZMediaIjkplayer());//进入此页面修改MediaInterface，让此页面的jzvd正常工作
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (JZVideoPlayer.backPress()) {
//            return;
//        }
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        JZVideoPlayer.releaseAllVideos();
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                finish();
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//}
