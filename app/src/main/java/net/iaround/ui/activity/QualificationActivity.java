package net.iaround.ui.activity;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.liangmutian.mypicker.DataPickerDialog;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.BatchUploadManager;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.FileUploadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.UploadFileCallback;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.entity.UploadFileResponse;
import net.iaround.im.WebSocketManager;
import net.iaround.mic.AudioChatManager;
import net.iaround.model.entity.ApplyQualificationBean;
import net.iaround.model.entity.BaseEntity;
import net.iaround.model.entity.QualificationBean;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.PermissionUtils;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.view.face.MyGridView;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.ui.view.popupwin.RecordAudioPopupWindow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Created by yz on 2018/8/5.
 * 资质认证界面
 */

public class QualificationActivity extends TitleActivity implements HttpCallBack, View.OnClickListener, BatchUploadManager.BatchUploadCallBack, UploadFileCallback {
    private static final String TAG = "QualificationActivity";
    private static final int UPDATE_IMAGE_LAYOUT_FLAG = 1;
    private static final int UPDATE_AUDIO_LAYOUT_FLAG = 2;
    private static final int REQUEST_CODE_GALLERY = 1001;


    private ApplyQualificationBean.GameItem mGameItem;
    private LinearLayout mLlStatus;
    private LinearLayout mLlUploadPic;
    private ImageView mIvStatus;
    private TextView mTvStatus;
    private TextView mTvPicCaption;//上传资质图说明
    private TextView mTvAudioCaption;//上传音频说明
    private TextView mTvSample;//示例
    private MyGridView mMgvUploadPics;
    private LinearLayout mLlRecordAudio;
    private RelativeLayout mRlShowRecordedVideo;
    private TextView mTvAudioLength;
    private TextView mTvReRecorded;//重新录制
    private TextView mTvLevelName;//选择等级
    private EditText mEtInstruction;
    private ImageView mIvPlayAudio;
    private TextView mTvSubmit;
    private View mLine;//分割线
    private LinearLayout mLlSelectLevel;//分割线

    private RecordAudioPopupWindow mWindow;
    private Dialog mChooseLevel;

    private QualificationBean.AuthInfo mAuthInfo;
    private ArrayList<String> mImageList = new ArrayList<>();
    private ArrayList<String> mLevelName = new ArrayList<>();
    private MyHandler mHandler = new MyHandler();
    private PicAdapter mPicAdapter;

    private Timer mTimer;
    private CountdownTimerTask mCountdownTask;
    private MediaPlayer mMediaPlayer = null;
    private boolean isPlaying = false;
    private String mFilePath;
    private long mCountTime;//计时时间
    private long mAudioDuration;//音频时长

    private long mGetQualificationFlag;
    private long mPutQualificationFlag;
    private BatchUploadManager mBatchUploadManager;
    private boolean isUploadPicSuccess = false;//上传图片是否成功
    private boolean isUploadAudioSuccess = false;//上传音频是否成功
    private String mPhotos = "";//上传成功后的图片url字符串，以","隔开
    private String mVoice;//上传音频成功后的URL

    private boolean isSubmit = false;//是否已经提交

    private boolean isChangePic = false;//是否更改过图片
    private boolean isChangeAudio = false;//是否更改过音频
    private boolean isChangeLevel = false;//是否更改段位等级
    private String mDescription = "";//文字描述
    private String mSampleUrl = "";//示例图url
    private ArrayList<String> mSampleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameItem = (ApplyQualificationBean.GameItem) getIntent().getSerializableExtra("GameItem");
        setTitle_C(mGameItem.gameName);
        setContent(R.layout.activity_qualification);
        mBatchUploadManager = new BatchUploadManager(this);
        initView();
        initListener();
        showWaitDialog();
        mGetQualificationFlag = GameChatHttpProtocol.getQualification(this, mGameItem.gameId, this);
    }

    private void initView() {

        mIvStatus = (ImageView) findViewById(R.id.iv_status);
        mTvStatus = (TextView) findViewById(R.id.tv_status);
        mTvPicCaption = (TextView) findViewById(R.id.tv_pic_caption);
        mTvAudioCaption = (TextView) findViewById(R.id.tv_audio_caption);
        mTvSample = (TextView) findViewById(R.id.tv_sample);
        mMgvUploadPics = (MyGridView) findViewById(R.id.mgv_upload_pics);
        mLlUploadPic = (LinearLayout) findViewById(R.id.ll_upload_pic);
        mLlStatus = (LinearLayout) findViewById(R.id.ll_status);
        mLlRecordAudio = (LinearLayout) findViewById(R.id.ll_record_audio);
        mEtInstruction = (EditText) findViewById(R.id.et_instruction);
        mTvAudioLength = (TextView) findViewById(R.id.tv_audio_length);
        mTvReRecorded = (TextView) findViewById(R.id.tv_re_recorded);
        mLlSelectLevel = (LinearLayout) findViewById(R.id.ll_select_level);
        mLine = findViewById(R.id.view_line);
        mTvLevelName = (TextView) findViewById(R.id.tv_level_name);
        mIvPlayAudio = (ImageView) findViewById(R.id.iv_play_audio);
        mRlShowRecordedVideo = (RelativeLayout) findViewById(R.id.rl_show_recorded_video);
        mTvSubmit = (TextView) findViewById(R.id.tv_submit);

        mEtInstruction.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});
        mEtInstruction.setFocusable(false);
        mLlUploadPic.setVisibility(View.VISIBLE);
        mMgvUploadPics.setVisibility(View.GONE);


        mPicAdapter = new PicAdapter();
        mMgvUploadPics.setAdapter(mPicAdapter);

        mWindow = new RecordAudioPopupWindow(this);

    }

    private void initListener() {
        mTvSample.setOnClickListener(this);
        mLlUploadPic.setOnClickListener(this);
        mLlRecordAudio.setOnClickListener(this);
        mEtInstruction.setOnClickListener(this);
        mTvReRecorded.setOnClickListener(this);
        mTvLevelName.setOnClickListener(this);
        mIvPlayAudio.setOnClickListener(this);
        mTvSubmit.setOnClickListener(this);
        mWindow.setOnSaveAudioListener(new RecordAudioPopupWindow.OnSaveAudioListener() {
            @Override
            public void onSave(int duration, String filePath) {
                mRlShowRecordedVideo.setVisibility(View.VISIBLE);
                mLlRecordAudio.setVisibility(View.GONE);
                mAudioDuration = duration;
                mFilePath = filePath;
                mTvAudioLength.setText(String.format("%02d", mAudioDuration) + "s");
                isChangeAudio = true;
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mWindow.getWindow() != null && mWindow.getWindow().isShowing()) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && mWindow.getWindow() != null && mWindow.getWindow().isShowing()) {
            mWindow.getWindow().dismiss();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sample:
                if (!TextUtils.isEmpty(mSampleUrl)) {
                    mSampleList.clear();
                    mSampleList.add(mSampleUrl);
                    PictureDetailsActivity.launch(mContext, mSampleList, 0);
                }
                break;
            case R.id.ll_upload_pic:
                showPicSelect();
                break;
            case R.id.ll_record_audio:
            case R.id.tv_re_recorded:
                requestMicshowPermissions();
//                requestPermission(new String[]{PermissionUtils.PERMISSION_RECORD_AUDIO});
                break;
            case R.id.et_instruction:
                mEtInstruction.setFocusable(true);
                mEtInstruction.setFocusableInTouchMode(true);
                mEtInstruction.requestFocus();
                showInputMethod();
                break;
            case R.id.tv_level_name:
                if (mLevelName.size() > 0) {
                    showChooseDialog(mLevelName);
                }
                break;
            case R.id.iv_play_audio:
                onPlay(isPlaying);
                isPlaying = !isPlaying;
                if (isPlaying) {
                    mIvPlayAudio.setImageResource(R.drawable.btn_stop);
                } else {
                    mIvPlayAudio.setImageResource(R.drawable.btn_play);
                }
                break;
            case R.id.tv_submit:
                if (checkOptions() && !isSubmit) {
                    isSubmit = true;
                    showWaitDialog();
                    uploadPic();
                    uploadAudio();
                }
                break;

        }
    }


    @Override
    protected void doMicshowPermissions() {
        super.doMicshowPermissions();
        //关闭聊吧悬浮窗
        ChatBarZoomWindow.getInstance().close();
        /**判断是否在语音聊天中
         * 关闭语音聊天悬浮窗, 退出语音通话*/
        if (AudioChatManager.getsInstance().isHasLogin()) {
            DialogUtil.showTowButtonDialog(this, BaseApplication.appContext.getString(R.string.prompt), getString(R.string.close_audio_chat_for_record),
                    BaseApplication.appContext.getString(R.string.cancel), BaseApplication.appContext.getString(R.string.ok),
                    null, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mWindow.show();
                            /**关闭语音聊天*/
                            WebSocketManager.getsInstance().logoutAudioRoom(true);
                        }
                    });
        } else {
            mWindow.show();
        }


        if (mMediaPlayer != null) {
            stopPlaying();
        }
        stopCountdown();
    }

    private boolean checkOptions() {
        if (mLlSelectLevel.isShown() && getLevelType(mTvLevelName.getText().toString().trim()) <= 0) {
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.information_incompleteness);
            return false;

        }
        if (mImageList.size() <= 0 || TextUtils.isEmpty(mFilePath) || TextUtils.isEmpty(mEtInstruction.getText().toString().trim())) {
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.information_incompleteness);
            return false;
        }
        if (isChangePic || isChangeAudio || isChangeLevel || !mDescription.equals(mEtInstruction.getText().toString().trim())) {
            return true;
        } else {
            CommonFunction.toastMsg(BaseApplication.appContext, getString(R.string.not_change_information));
            return false;
        }
    }

    //显示选择游戏等级弹窗
    private void showChooseDialog(List<String> mlist) {
        DataPickerDialog.Builder builder = new DataPickerDialog.Builder(this);
        mChooseLevel = builder.setData(mlist).setSelection(1).setTitle(getString(R.string.cancel))
                .setOnDataSelectedListener(new DataPickerDialog.OnDataSelectedListener() {
                    @Override
                    public void onDataSelected(String itemValue, int position) {
                        isChangeLevel = true;
                        mTvLevelName.setText(itemValue);
                    }

                    @Override
                    public void onCancel() {

                    }
                }).create();

        mChooseLevel.show();
    }

    //网络请求成功
    @Override
    public void onGeneralSuccess(String result, long flag) {

        if (mGetQualificationFlag == flag) {
            QualificationBean bean = GsonUtil.getInstance().getServerBean(result, QualificationBean.class);
            if (bean != null && bean.authInfo != null) {
                mAuthInfo = bean.authInfo;
                if (mAuthInfo != null) {
                    if (mAuthInfo.photos != null && mAuthInfo.photos.size() > 0) {
                        mImageList.clear();
                        mImageList.addAll(mAuthInfo.photos);
                        mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FLAG);
                    }
                    setStatus(mAuthInfo.status);
                    if (!TextUtils.isEmpty(mAuthInfo.picCaption)) {
                        mTvPicCaption.setText(mAuthInfo.picCaption);
                    }
                    if (!TextUtils.isEmpty(mAuthInfo.voiceCaption)) {
                        mTvAudioCaption.setText(mAuthInfo.voiceCaption);
                    }
                    if (!TextUtils.isEmpty(mAuthInfo.sample)) {
                        mSampleUrl = mAuthInfo.sample;
                    }
                    if (!TextUtils.isEmpty(mAuthInfo.voice) && mAuthInfo.voice.contains("http")) {
                        mFilePath = mAuthInfo.voice;
//                        mFilePath = "http://sc1.111ttt.cn/2018/1/03/13/396131227447.mp3";
                        mHandler.sendEmptyMessage(UPDATE_AUDIO_LAYOUT_FLAG);
                    } else {
                        destroyWaitDialog();
                    }
                    if (!TextUtils.isEmpty(mAuthInfo.description)) {
                        mDescription = mAuthInfo.description;
                        mEtInstruction.setText(mAuthInfo.description);
                    }
                    if (mAuthInfo.levelList != null && mAuthInfo.levelList.size() > 0) {
                        mLlSelectLevel.setVisibility(View.VISIBLE);
                        mLine.setVisibility(View.VISIBLE);
                        String name = getLevelName(mAuthInfo.gameLevel);
                        if (!TextUtils.isEmpty(name)) {
                            mTvLevelName.setText(name);
                        }
                    } else {
                        mLlSelectLevel.setVisibility(View.GONE);
                        mLine.setVisibility(View.GONE);
                    }

                } else {
                    destroyWaitDialog();
                }
            } else {
                destroyWaitDialog();
            }
        } else if (mPutQualificationFlag == flag) {
            destroyWaitDialog();
            BaseEntity entity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
            if (entity.isSuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.apply_join_group_success);
                finish();
            } else {
                isSubmit = false;
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(mContext, e);
        isSubmit = false;
    }


    //上传图片
    private void uploadPic() {
        final ArrayList<String> updateImgs = new ArrayList<>();
        for (String outputPath : mImageList) {
            String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath.replace(PathUtil.getFILEPrefix(), "") : outputPath;
            updateImgs.add(url);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mBatchUploadManager.uploadImage(TimeFormat.getCurrentTimeMillis(), updateImgs, FileUploadType.PIC_ALBUM, QualificationActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    isUploadPicSuccess = false;
                    isSubmit = false;
                    CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_pic_fail);
                    destroyWaitDialog();
                }
            }
        }).start();
    }

    //上传音频
    private void uploadAudio() {
        if (!TextUtils.isEmpty(mFilePath)) {

            if (!mFilePath.contains("http:")) {
                Map<String, String> map = new HashMap<>();
                map.put("key", ConnectorManage.getInstance(BaseApplication.appContext).getKey());
                map.put("type", String.valueOf(FileUploadType.AUDIO_QUALIFICATION));
                FileUploadManager.createUploadTask(this, mFilePath, FileUploadManager.FileProfix.MP3, Config.sVideoHost, map, this, TimeFormat.getCurrentTimeMillis(), 1000 * 60, 1000 * 60 * 3).start();
            } else {
                isUploadAudioSuccess = true;
                mVoice = mFilePath;
                if (isUploadAudioSuccess && isUploadPicSuccess) {
                    putQualification();
                }
            }
        } else {
            destroyWaitDialog();
            isUploadAudioSuccess = false;
            isSubmit = false;
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_audio_fail);
        }
    }

    //提交申请
    private void putQualification() {
        int levelType = getLevelType(mTvLevelName.getText().toString().trim());
        String des = mEtInstruction.getText().toString().trim();
        mPutQualificationFlag = GameChatHttpProtocol.putQualification(this, mGameItem.gameId, mPhotos, mVoice, levelType, des, this);
    }

    //图片上传的callback
    @Override
    public void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList) {

        int count = serverUrlList.size();
        for (int i = 0; i < count; i++) {
            mPhotos += serverUrlList.get(i) + (count - 1 != i ? "," : "");
        }
        isUploadPicSuccess = true;
        if (isUploadAudioSuccess && isUploadPicSuccess) {
            putQualification();
        }

    }

    @Override
    public void batchUploadFail(long taskFlag) {
        isUploadPicSuccess = false;
        destroyWaitDialog();
        isSubmit = false;
        CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_pic_fail);

    }

    //音频上传的callback
    @Override
    public void onUploadFileProgress(int lengthOfUploaded, long flag) {

    }

    @Override
    public void onUploadFileFinish(long flag, String result) {
        UploadFileResponse bean = GsonUtil.getInstance().getServerBean(result, UploadFileResponse.class);
        if (bean != null && bean.isSuccess()) {
            mVoice = bean.url;
            isUploadAudioSuccess = true;
            if (isUploadAudioSuccess && isUploadPicSuccess) {
                putQualification();
            }
        } else {
            destroyWaitDialog();
            isSubmit = false;
            isUploadAudioSuccess = false;
            CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_audio_fail);
        }

    }

    @Override
    public void onUploadFileError(String e, long flag) {
        isUploadAudioSuccess = false;
        isSubmit = false;
        destroyWaitDialog();
        CommonFunction.toastMsg(BaseApplication.appContext, R.string.upload_audio_fail);
    }


    //设置审核状态
    private void setStatus(int status) {
        switch (status) {
            case 0:
                mLlStatus.setVisibility(View.GONE);
                break;
            case 1:
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_success);
                mTvStatus.setText(R.string.application_successful);
                break;
            case 2:
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_failure);
                mTvStatus.setText(R.string.application_refuse);
                break;
            case 3:
                mLlStatus.setVisibility(View.VISIBLE);
                mIvStatus.setImageResource(R.drawable.pic_under_review);
                mTvStatus.setText(R.string.under_review);
                mTvSubmit.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                mTvSubmit.setTextColor(getResources().getColor(R.color.common_black));
                mTvSubmit.setEnabled(false);
                break;
        }
    }

    //根据游戏的type得到对应的称号
    private String getLevelName(int type) {
        String name = "";
        for (int i = 0; i < mAuthInfo.levelList.size(); i++) {
            mLevelName.add(mAuthInfo.levelList.get(i).name);
            if (type == mAuthInfo.levelList.get(i).type) {
                name = mAuthInfo.levelList.get(i).name;
            }
        }
        return name;
    }

    //根据游戏称号得到对应的type
    private int getLevelType(String levelName) {
        for (int i = 0; i < mAuthInfo.levelList.size(); i++) {
            if (levelName.equals(mAuthInfo.levelList.get(i).name)) {
                return mAuthInfo.levelList.get(i).type;
            }
        }
        return 0;
    }

    //选择照片
    private void showPicSelect() {
        CommonFunction.hideInputMethod(this, mEtInstruction);
        GalleryUtils.getInstance().openGalleryMuti(this, REQUEST_CODE_GALLERY, 6, mOnHandlerResultCallback);
    }

    private GalleryFinal.OnHanlderResultCallback mOnHandlerResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {
            if (resultList != null) {
                final List<String> list = new ArrayList<>();
                for (PhotoInfo photoInfo : resultList) {
                    list.add(photoInfo.getPhotoPath());
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mImageList.clear();
                        isChangePic = true;
                        for (int i = 0; i < list.size(); i++) {
                            rotatingImage(list.get(i));
                        }
                        mHandler.sendEmptyMessage(UPDATE_IMAGE_LAYOUT_FLAG);
                    }
                }).start();
            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            CommonFunction.toastMsg(BaseApplication.appContext, errorMsg);
        }
    };

    // 处理图片旋转的问题
    private void rotatingImage(String path) {
        String outputPath = PathUtil.getImageRotatePath(path);
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path);

            int degree = PhotoCropActivity.readPictureDegree(path);
            if (degree == 0) {
                outputPath = path;

            } else {
                File outputFile = new File(outputPath);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                    bitmap = PhotoCropActivity.rotaingImageView(degree, bitmap);
                    FileOutputStream os = new FileOutputStream(outputFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                }
            }
            //create一个小图
            File thumbFile = new File(CommonFunction.thumPicture(outputPath));
            Bitmap thumbBitmap = CommonFunction.centerSquareScaleBitmap(bitmap, 136);
            FileOutputStream thumbOs = new FileOutputStream(thumbFile);
            thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, thumbOs);
            thumbOs.flush();
            thumbOs.close();

        } catch (Exception e) {
            e.printStackTrace();
            outputPath = path;
        }
        String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath : PathUtil.getFILEPrefix() + outputPath;
        mImageList.add(url);
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_IMAGE_LAYOUT_FLAG:
                    if (mImageList.size() > 0) {
                        mMgvUploadPics.setVisibility(View.VISIBLE);
                        mLlUploadPic.setVisibility(View.GONE);
                        mPicAdapter.update(mImageList);
                    }
                    break;
                case UPDATE_AUDIO_LAYOUT_FLAG:
                    if (TextUtils.isEmpty(mFilePath)) {
                        mRlShowRecordedVideo.setVisibility(View.GONE);
                        mLlRecordAudio.setVisibility(View.VISIBLE);
                    } else {
                        getUrlAudioDuration();
                        mRlShowRecordedVideo.setVisibility(View.VISIBLE);
                        mLlRecordAudio.setVisibility(View.GONE);
                    }

                    break;
            }
        }
    }

    /**
     * 图片适配器
     */
    class PicAdapter extends BaseAdapter {
        private ArrayList<String> picList;


        private void update(ArrayList<String> picList) {
            if (picList != null && picList.size() > 0) {
                this.picList = picList;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            if (picList != null) {
                return picList.size() + 1;
            }
            return 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(QualificationActivity.this).inflate(R.layout.item_qualification_pic, null);
            ImageView iv = (ImageView) convertView.findViewById(R.id.iv_pic);
            LinearLayout llSelect = (LinearLayout) convertView.findViewById(R.id.ll_select_pic);
            if (picList.size() == position) {
                llSelect.setVisibility(View.VISIBLE);
                iv.setVisibility(View.GONE);
            } else {
                GlideUtil.loadRoundImageNew(BaseApplication.appContext, picList.get(position), 10, iv, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
                llSelect.setVisibility(View.GONE);
                iv.setVisibility(View.VISIBLE);

            }
            llSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPicSelect();
                }
            });

            return convertView;
        }
    }

    /*倒计时任务 */
    static class CountdownTimerTask extends TimerTask {
        private WeakReference<QualificationActivity> mActivity = null;

        public CountdownTimerTask(QualificationActivity activity) {
            mActivity = new WeakReference<QualificationActivity>(activity);
        }

        @Override
        public void run() {
            CommonFunction.log(TAG, "CountdownTimerTask() time bingo");
            QualificationActivity activity = mActivity.get();
            if (activity != null) {
                activity.mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        QualificationActivity activity = mActivity.get();
                        if (activity != null) {
                            if (Build.VERSION.SDK_INT >= 17) {
                                if (activity.isDestroyed()) {
                                    return;
                                }
                            }
                            activity.doCountdown();
                        }
                    }
                });
            }
        }
    }

    private void doCountdown() {
        CommonFunction.log(TAG, "getCurrentPosition: " + mMediaPlayer.getCurrentPosition() / 1000 + "  mAudioDuration:" + mAudioDuration);
        if (mMediaPlayer != null) {

            mCountTime = mAudioDuration - mMediaPlayer.getCurrentPosition() / 1000;//倒计时

            if (mCountTime >= 0) {
                mTvAudioLength.setText(String.format("%02d", mCountTime) + "s");
            } else {
                mTvAudioLength.setText("0s");
            }
        }
    }

    //初始化计时器
    private void showCountdown() {
        mTimer = new Timer();
        mCountdownTask = new CountdownTimerTask(this);
        mTimer.schedule(mCountdownTask, 0, 1000);

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
        //关闭聊吧悬浮窗
        ChatBarZoomWindow.getInstance().close();
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

    //获取音频时长并初始化MediaPlayer
    private void getUrlAudioDuration() {
        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(mFilePath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    mMediaPlayer.start();
                    destroyWaitDialog();
                    mAudioDuration = mMediaPlayer.getDuration() / 1000;
                    CommonFunction.log(TAG, "prepare() mAudioDuration: " + mAudioDuration);
                    mTvAudioLength.setText(String.format("%02d", mAudioDuration) + "s");
                    stopPlaying();
                }
            });
        } catch (IOException e) {
            destroyWaitDialog();
            e.printStackTrace();
            CommonFunction.log(TAG, "prepare() failed");
        }
    }

    private void startPlaying() {

        mMediaPlayer = new MediaPlayer();

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
                stopCountdown();
                stopPlaying();
                mCountTime = mAudioDuration;
                mTvAudioLength.setText(String.format("%02d", mCountTime) + "s");
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
            mCountTime = mAudioDuration;
            startPlaying();
        } else {
            mMediaPlayer.start();
        }
    }

    private void stopPlaying() {
        mIvPlayAudio.setImageResource(R.drawable.btn_play);
        mMediaPlayer.pause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        isPlaying = false;

    }

    @Override
    protected void onDestroy() {
        if (mMediaPlayer != null) {
            stopPlaying();
        }
        stopCountdown();
        super.onDestroy();
    }
}
