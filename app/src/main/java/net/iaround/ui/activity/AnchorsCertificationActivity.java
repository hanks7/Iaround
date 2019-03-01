package net.iaround.ui.activity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alivc.player.AliVcMediaPlayer;
import com.alivc.player.MediaPlayer;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.CertificationUpload;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.UploadFileCallback;
import net.iaround.connector.protocol.AnchorsCertificationProtocol;
import net.iaround.entity.UploadFileResponse;
import net.iaround.model.entity.AnchorsCertificationBean;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.ProcessImageView;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 主播认证
 * Created by gh on 2017/12/7.
 */
public class AnchorsCertificationActivity extends BaseActivity implements View.OnClickListener, HttpCallBack {

    public long setAuthentication;//设置认证
    public long getAuthentication;//获取认证的状态

    private ProcessImageView ivVideo;
    private ProcessImageView ivCover;
    private TextView tvVideoHint;
    private TextView tvCoverHint;
    private RelativeLayout rlVideoAdd;
    private RelativeLayout rlCoverAdd;
    private TextView tvSubmit;

    private String videoLocalStr = "";//本地的地址
    private String coverLocalStr = "";//本地的地址

    private String videoStr = "";//上传后的地址
    private String coverStr = "";//上传后的地址

    private boolean mRequestLiveShowPermission = false;

    private SurfaceView mSvVideo; //用播放器来预览上传视频的第一贞
    private AliVcMediaPlayer mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CommonFunction.log("AnchorsCertification","onCreate() into");

        setContentView(R.layout.activity_anchors_certification);

        initView();

        reqStateData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonFunction.log("AnchorsCertification","onDestroy() into");

        destroyPlayer();

        destroyWaitDialog();
    }

    private void initView(){
        TextView tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(getString(R.string.anchors_certification_title));
        ivVideo = (ProcessImageView) findViewById(R.id.iv_certification_video);
        ivCover = (ProcessImageView) findViewById(R.id.iv_certification_cover);

        tvVideoHint = (TextView) findViewById(R.id.tv_certification_video_hint);
        tvCoverHint = (TextView) findViewById(R.id.tv_certification_cover_hint);

        rlVideoAdd = (RelativeLayout) findViewById(R.id.rl_certification_video_add);
        rlCoverAdd = (RelativeLayout) findViewById(R.id.rl_certification_cover_add);

        tvSubmit = (TextView)findViewById(R.id.tv_anchors_certification_submit);

        findViewById(R.id.fl_back).setOnClickListener(this);
        tvSubmit.setOnClickListener(this);

        rlVideoAdd.setOnClickListener(this);
        rlCoverAdd.setOnClickListener(this);

        ivVideo.setOnClickListener(this);
        ivCover.setOnClickListener(this);

        tvVideoHint.setOnClickListener(this);
        tvCoverHint.setOnClickListener(this);

        mSvVideo = (SurfaceView) findViewById(R.id.sv_certification_video);

        mSvVideo.setBackgroundResource(R.color.transparent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mRequestLiveShowPermission == false){
            mRequestLiveShowPermission = true;
            requestLiveShowPermission();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                backDialog();
                break;
            case R.id.tv_anchors_certification_submit:

                if (TextUtils.isEmpty(videoStr) || TextUtils.isEmpty(coverStr)){
                    CommonFunction.toastMsg(AnchorsCertificationActivity.this,getResString(R.string.authentication_submit_empty_hint));
                    return;
                }

                if (ivVideo.progress == 100 || ivCover.progress == 100 ){
                    commitAuthentication();
                }

                break;
            case R.id.iv_certification_video:
            case R.id.rl_certification_video_add:
                if (ivVideo.progress == 100 || ivVideo.progress == 0){
                    GalleryUtils.getInstance().openVideoSingle(this,201,mOnHanlderResultCallback);
                }
                break;
            case R.id.iv_certification_cover:
            case R.id.rl_certification_cover_add:
                if (ivCover.progress == 100 || ivCover.progress == 0){

                    GalleryUtils.getInstance().openGallerySingle(this,202,mOnHanlderResultCallback);
                }
                break;

            case R.id.tv_certification_video_hint:
                if (videoLocalStr != null && !TextUtils.isEmpty(videoLocalStr)){

                    CertificationUpload.uploadResourceFile(this,videoLocalStr, 4, uploadFileVideoCallback);
                    tvVideoHint.setVisibility(View.GONE);
                }
                break;
            case R.id.tv_certification_cover_hint:
                if (coverLocalStr != null && !TextUtils.isEmpty(coverLocalStr)){
                    CertificationUpload.uploadResourceImgFile(this,coverLocalStr, 2, uploadFileImgCallback);
                    tvCoverHint.setVisibility(View.GONE);
                }
                break;
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                if (resultList.size() > 0) {
                    PhotoInfo photoInfo = resultList.get(0);
                    if (reqeustCode == 201) {
                        videoLocalStr = photoInfo.getPhotoPath();

                        rlVideoAdd.setVisibility(View.GONE);
                        ivVideo.setVisibility(View.VISIBLE);
                        mSvVideo.setVisibility(View.GONE);
                        tvVideoHint.setVisibility(View.GONE);

                        rlVideoAdd.post(new Runnable() {
                            @Override
                            public void run() {
                                GlideUtil.loadImage(AnchorsCertificationActivity.this,videoLocalStr,ivVideo);
                                CertificationUpload.uploadResourceFile(AnchorsCertificationActivity.this,videoLocalStr, 4, uploadFileVideoCallback);
                            }
                        });
                    } else if (reqeustCode == 202) {
                        coverLocalStr = photoInfo.getPhotoPath();

                        rlCoverAdd.setVisibility(View.GONE);
                        ivCover.setVisibility(View.VISIBLE);
                        tvCoverHint.setVisibility(View.GONE);

                        ivCover.post(new Runnable() {
                            @Override
                            public void run() {
                                GlideUtil.loadImage(AnchorsCertificationActivity.this,coverLocalStr,ivCover);
                            }
                        });

                        tvVideoHint.post(new Runnable() {
                            @Override
                            public void run() {
                                CertificationUpload.uploadResourceImgFile(AnchorsCertificationActivity.this,coverLocalStr, 2, uploadFileImgCallback);
                            }
                        });

                    }

                    if (!TextUtils.isEmpty(videoLocalStr) | !TextUtils.isEmpty(coverLocalStr) ){
                        tvSubmit.setText(getString(R.string.anchors_certification_submit));
                        tvSubmit.setEnabled(true);
                    }
                }
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(AnchorsCertificationActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    private UploadFileCallback uploadFileVideoCallback = new UploadFileCallbackImpl(this);
    static class UploadFileCallbackImpl implements UploadFileCallback{
        private WeakReference<AnchorsCertificationActivity> mActivity;
        public UploadFileCallbackImpl(AnchorsCertificationActivity activity){
            mActivity = new WeakReference<AnchorsCertificationActivity>(activity);
        }

        @Override
        public void onUploadFileProgress(int lengthOfUploaded, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            if (lengthOfUploaded == 100) {
                activity.tvVideoHint.setVisibility(View.GONE);
            } else {
                activity.ivVideo.setProgress(lengthOfUploaded);
            }
            CommonFunction.log(this.getClass().getName(), "  video lengthOfUploaded = " + lengthOfUploaded);
        }

        @Override
        public void onUploadFileFinish(long flag, String result) {
            if(result==null)
                return;
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            UploadFileResponse bean = GsonUtil.getInstance().getServerBean(result, UploadFileResponse.class);
            if (bean!=null && bean.isSuccess()) {
                activity.videoStr = bean.url;
                activity.ivVideo.setProgress(100);

                CommonFunction.log(this.getClass().getName(), "videoStr = " + activity.videoStr + "    coverStr =" + activity.coverStr);
            } else {
                activity.tvVideoHint.setVisibility(View.VISIBLE);
                activity.tvVideoHint.setText(activity.getString(R.string.dynamic_send_fail_resend_tips));
            }
        }

        @Override
        public void onUploadFileError(String e, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.tvVideoHint.setVisibility(View.VISIBLE);
            activity.tvVideoHint.setText(activity.getString(R.string.dynamic_send_fail_resend_tips));

        }
    };


    private UploadFileCallback uploadFileImgCallback = new UploadFileImageCallbackImpl(this);
    static class UploadFileImageCallbackImpl implements UploadFileCallback{
        private WeakReference<AnchorsCertificationActivity> mActivity;
        public UploadFileImageCallbackImpl(AnchorsCertificationActivity activity){
            mActivity = new WeakReference<AnchorsCertificationActivity>(activity);
        }
        @Override
        public void onUploadFileProgress(int lengthOfUploaded, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            if (lengthOfUploaded == 100) {
                activity.tvCoverHint.setVisibility(View.GONE);
            } else {
                activity.ivCover.setProgress(lengthOfUploaded);
            }
            CommonFunction.log(this.getClass().getName(), "  cover lengthOfUploaded = " + lengthOfUploaded);
        }

        @Override
        public void onUploadFileFinish(long flag, String result) {
            if(result==null)
                return;
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            UploadFileResponse bean = GsonUtil.getInstance().getServerBean(result, UploadFileResponse.class);

            if (bean!=null && bean.isSuccess()) {
                activity.coverStr = bean.url;
                activity.ivCover.setProgress(100);

                CommonFunction.log(this.getClass().getName(), "videoStr = " + activity.videoStr + "    coverStr =" + activity.coverStr);
            } else {

                activity.tvCoverHint.setVisibility(View.VISIBLE);
                activity.tvCoverHint.setText(activity.getString(R.string.dynamic_send_fail_resend_tips));
            }
        }

        @Override
        public void onUploadFileError(String e, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.tvCoverHint.setVisibility(View.VISIBLE);
            activity.tvCoverHint.setText(activity.getString(R.string.dynamic_send_fail_resend_tips));
        }
    };


    /**
     * 提交认证
     */
    private void commitAuthentication(){
        showWaitDialog();
        setAuthentication = AnchorsCertificationProtocol.setAnchorDisturbAnchorPic(this,coverStr,videoStr,new HttpCallbackImpl(this));
    }

    /**
     * 获取认证状态
     */
    private void reqStateData(){
        showWaitDialog();
        getAuthentication = AnchorsCertificationProtocol.getAnchorDisturbAnchor(this,new HttpCallbackImpl(this));
    }

    static class HttpCallbackImpl implements HttpCallBack{
        private WeakReference<AnchorsCertificationActivity> mActivity;
        public HttpCallbackImpl(AnchorsCertificationActivity activity){
            mActivity = new WeakReference<AnchorsCertificationActivity>(activity);
        }

        @Override
        public void onGeneralSuccess(String result, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralSuccess(result, flag);
        }

        @Override
        public void onGeneralError(int e, long flag) {
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            activity.onGeneralError(e, flag);
        }
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        hideWaitDialog();
        if (getAuthentication == flag){
            AnchorsCertificationBean bean = GsonUtil.getInstance().getServerBean(result,AnchorsCertificationBean.class);
            if (bean.isSuccess()){
                if (bean.VerifyStatus == 0){
                    tvSubmit.setText(getString(R.string.anchors_certification_submit));
                    tvSubmit.setEnabled(true);
                }else if (bean.VerifyStatus == 2){
                    tvSubmit.setText(getString(R.string.authentication_state_audit));
                    ivCover.setEnabled(false);
                    ivVideo.setEnabled(false);
                    tvSubmit.setEnabled(false);
                }else if (bean.VerifyStatus == 1){
                    tvSubmit.setText(getString(R.string.authentication_state_complete));
                    ivCover.setEnabled(true);
                    ivVideo.setEnabled(true);
                    tvSubmit.setEnabled(false);
                }else if (bean.VerifyStatus == 3){
                    tvSubmit.setText(getString(R.string.authentication_state_not_through));
                    ivCover.setEnabled(true);
                    ivVideo.setEnabled(true);
                    tvSubmit.setEnabled(false);
                }

                if (bean.Video != null && !TextUtils.isEmpty(bean.Video)){
                    videoStr = bean.Video;
                    rlVideoAdd.setVisibility(View.GONE);
//              最新版 Glide 图片加载有问题会引起图片闪烁，应该是我们使用不当造成
//                    Glide.with(this.getApplicationContext())
//                            .setDefaultRequestOptions(
//                                    new RequestOptions()
//                                            .frame(1000000)
//                                            .dontTransform()
//                                            .centerCrop())
//                            .load(bean.Video)
//                            .into(ivVideo);

                    //使用播放器显示第一贞的方式
                    initPlayer(bean.Video);

                    tvVideoHint.setVisibility(View.GONE);
                    ivVideo.setVisibility(View.VISIBLE);
                    mSvVideo.setVisibility(View.VISIBLE);
                }

                if (bean.Pic != null && !TextUtils.isEmpty(bean.Pic)){
                    coverStr = bean.Pic;
                    rlCoverAdd.setVisibility(View.GONE);
                    GlideUtil.loadImage(BaseApplication.appContext,bean.Pic,ivCover);
                    tvCoverHint.setVisibility(View.GONE);
                    ivCover.setVisibility(View.VISIBLE);
                }

            }
        }else if (setAuthentication == flag){
            BaseServerBean bean = GsonUtil.getInstance().getServerBean(result,BaseServerBean.class);
            if (bean.isSuccess()){
                videoLocalStr = "";
                coverLocalStr = "";
                videoStr = "";
                coverStr = "";
                CommonFunction.toastMsg(AnchorsCertificationActivity.this,getResString(R.string.chat_modify_room_succ_need_check));

                ivVideo.setProgress(0);
                ivCover.setProgress(0);
                tvSubmit.setText(getString(R.string.authentication_state_audit));
                ivCover.setEnabled(false);
                ivVideo.setEnabled(false);
                tvSubmit.setEnabled(false);
            }else{
                onGeneralError(bean.error, flag);
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        hideWaitDialog();
        ErrorCode.toastError(getActivity(), e);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            backDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    // 后退提示
    private void backDialog() {
        if (!TextUtils.isEmpty(videoLocalStr) | !TextUtils.isEmpty(coverLocalStr) ){
            DialogUtil.showTwoButtonDialog(AnchorsCertificationActivity.this,
                    mContext.getString(R.string.dialog_title),
                    mContext.getString(R.string.edit_cancel_tip_title),
                    mContext.getString(R.string.give_up),
                    mContext.getString(R.string.continue_editing),
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            finish();

                        }
                    });
        } else {
            finish();
        }
    }

    private boolean saveBitmapTofile(Bitmap bmp, String filename) {
        if (bmp == null || filename == null)
            return false;
        CompressFormat format = CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            stream = new FileOutputStream(Environment.getExternalStorageDirectory()+"/iAround/" + filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }


    private void initPlayer(String url){
        CommonFunction.log("AnchorsCertification","initPlayer() into, url="+url );
        if(TextUtils.isEmpty(url)){
            return;
        }
        mSvVideo.getHolder().addCallback(new SurfaceHolderImpl(this, url));
    }

    private void startToPlay(String url){
        CommonFunction.log("AnchorsCertification","startToPlay() into" );
        if(mPlayer==null) {
            mPlayer = new AliVcMediaPlayer(this, mSvVideo);
        }
        mPlayer.disableNativeLog();
        mPlayer.setDefaultDecoder(1);
        mPlayer.setMediaType(MediaPlayer.MediaType.Vod);
        mPlayer.setFrameInfoListener(new FrameInfoListenerImpl(this));
        mPlayer.setVideoScalingMode(MediaPlayer.VideoScalingMode.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mPlayer.prepareAndPlay(url);
    }

    private void destroyPlayer(){
        CommonFunction.log("AnchorsCertification","destroyPlayer() into" );
        if(mPlayer!=null){
            mPlayer.destroy();
            mPlayer = null;
        }
    }

    static class SurfaceHolderImpl implements SurfaceHolder.Callback{
        private WeakReference<AnchorsCertificationActivity> mActivity;
        private String mUrl;
        public SurfaceHolderImpl(AnchorsCertificationActivity activity, String url){
            mActivity = new WeakReference<AnchorsCertificationActivity>(activity);
            mUrl = url;
        }
        @SuppressWarnings("deprecation")
        public void surfaceCreated(SurfaceHolder holder) {
            CommonFunction.log("AnchorsCertification", "onSurfaceCreated() into");

            AnchorsCertificationActivity activity = mActivity.get();
            if(activity==null){
                return;
            }
            holder.setType(SurfaceHolder.SURFACE_TYPE_NORMAL);
            holder.setKeepScreenOn(true);

            // 重点:
            if (activity.mPlayer != null) {
                // 对于从后台切换到前台,需要重设surface;部分手机锁屏也会做前后台切换的处理
                activity.mPlayer.setVideoSurface(activity.mSvVideo.getHolder().getSurface());
            } else {
                // 创建并启动播放器
                activity.startToPlay(mUrl);
            }

            CommonFunction.log("AnchorsCertification", "onSurfaceCreated() out");
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            CommonFunction.log("AnchorsCertification", "onSurfaceChanged() is valid ? " + holder.getSurface().isValid());
            AnchorsCertificationActivity activity = mActivity.get();
            if(activity==null){
                return;
            }
            if (activity.mPlayer != null) {
                activity.mPlayer.setSurfaceChanged();
            }
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            CommonFunction.log("AnchorsCertification", "onSurfaceDestroy()");
        }
    };


    private void handleFirstFrame(){
        CommonFunction.log("AnchorsCertification","handleFirstFrame() into" );
        if(mPlayer==null){
            return;
        }
        mPlayer.pause();
    }

    static class FrameInfoListenerImpl implements MediaPlayer.MediaPlayerFrameInfoListener{
        private WeakReference<AnchorsCertificationActivity> mActivity;

        public FrameInfoListenerImpl(AnchorsCertificationActivity activity){
            mActivity = new WeakReference<AnchorsCertificationActivity>(activity);
        }
        @Override
        public void onFrameInfoListener() {
            CommonFunction.log("AnchorsCertification","onFrameInfoListener() into" );
            AnchorsCertificationActivity activity = mActivity.get();
            if(CommonFunction.activityIsDestroyed(activity)){
                return;
            }
            //首帧显示时触发
            activity.handleFirstFrame();
        }
    }

}
