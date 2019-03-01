package net.iaround.ui.chat.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.entity.RecordFaceBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.ui.face.FaceDetailActivityNew;
import net.iaround.utils.ImageViewUtil;
import net.nostra13.universalimageloader.core.assist.FailReason;
import net.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @ClassName FriendFaceRecordView.java
 * @Description: 朋友的表情消息
 */

public class FriendFaceRecordView extends FriendBaseRecordView implements View.OnClickListener {

    private ImageView mImageView;
    private LinearLayout llContent;

    private final int DEFAULT_FLAG = 0;
    private final String UNDERLINE = "_";

    public FriendFaceRecordView(Context context) {
        super(context);
        mImageView = (ImageView) findViewById(R.id.content_img);
        llContent = (LinearLayout) findViewById(R.id.content);
    }

    @Override
    protected void inflatView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.chat_record_gifface_other, this);
    }

    @Override
    public void initRecord(Context context, ChatRecord record) {
        super.initRecord(context, record);

        // 设置点击事件 & 长按事件
        if (!bIsSystemUser(record.getFuid())) {
            llContent.setOnClickListener(this);
            llContent.setOnLongClickListener(mRecordLongClickListener);
        }

    }

    @Override
    public void showRecord(Context context, ChatRecord record) {

        RecordFaceBean bean = GsonUtil.getInstance()
                .getServerBean(record.getContent(), RecordFaceBean.class);
        if (bean == null) {
            return;
        }
        String face = bean.pkgid + UNDERLINE + bean.mapid;
        face = face.replaceAll("\\.0", "");
        File faceFile = FaceManager.getInstance(context).getFaceStreamFromPath(face);
        if (faceFile != null && faceFile.exists()) {
            boolean isgif = faceFile.getAbsolutePath().contains(PathUtil.getGifPostfix());
            boolean isDynamic = faceFile.getAbsolutePath()
                    .contains(PathUtil.getDynamicFacePostfix());
            if (isgif || isDynamic) {
                handleGIFDisplay(context, faceFile.getAbsolutePath());
            } else {
                handlePNGDisplay(context, faceFile.getAbsolutePath());
            }
        } else {
            // 下载的不存在的表情（包括gif，png）
            final String url = record.getAttachment();
            if (!url.contains(PathUtil.getGifPostfix())) {
//				ImageViewUtil.getDefault( ).loadImage( url, mImageView, defSmall, defSmall );//jiqiang
//				GlideUtil.loadImage(getContext(),url,mImageView, defSmall, defSmall );
//				Glide.with(context).load(url).centerCrop().placeholder(defSmall)
//						.error(defSmall).into(mImageView);
            } else {
                String imageloaderPath = CryptoUtil.generate(url);
                String filePath = PathUtil.getFaceDir() + imageloaderPath;

                File gifFile = new File(filePath);
                if (gifFile.exists()) {
                    handleGIFDisplay(context, filePath);
                } else {
                    // 先展示静态图片,加载静态图片成功后,下载gif图片
//					Glide.with(context).load(url).centerCrop().placeholder(defSmall)
//							.error(defSmall).into(mImageView);
                    ImageViewUtil.getDefault()
                            .loadImage(url, mImageView, defSmall, defSmall, new ImageLoadingListener() {

                                @Override
                                public void onLoadingStarted(String arg0, View arg1) {
                                }

                                @Override
                                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                                    mImageView.setImageResource(defShare);
                                }

                                @Override
                                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                                    if (url.contains(PathUtil.getGifPostfix())) {
                                        new DownLoadGifThread(getContext(), url, callback).start();
                                    }
                                }

                                @Override
                                public void onLoadingCancelled(String arg0, View arg1) {
                                }
                            });//jiqiang
                }
            }
        }

        // 替换成未通过的头像
        if(record.getId() == Common.getInstance().loginUser.getUid()){
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                record.setIcon(verifyicon);
            }
        }

        setTag(record);
        checkbox.setChecked(record.isSelect());
        checkbox.setTag(record);
        llContent.setTag(R.id.im_preview_uri, record);
        setUserNotename(context, record);
        setUserNameDis(context, record);
        // 设置头像
        setUserIcon(context, record, mIconView);
    }

    @Override
    public void reset() {
        mIconView.getImageView().setImageBitmap(null);
        mImageView.setImageBitmap(null);
        llContent.setBackgroundResource(R.drawable.transparent);
    }

    private void handlerUIDisplay(Context context, int flag, final String fileName,
                                  final String savePath) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                String gifPath = savePath + fileName;
                File gifFile = new File(gifPath);
                try {
                    GifDrawable gifDrawable = new GifDrawable(gifFile);
                    mImageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                    gifFile.delete();
                }
            }
        });
    }

    /**
     * 展示动态图
     */
    private void handleGIFDisplay(Context context, final String filePath) {
        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                File gifFile = new File(filePath);
                try {
                    GifDrawable gifDrawable = new GifDrawable(gifFile);
                    mImageView.setImageDrawable(gifDrawable);
                } catch (IOException e) {
                    e.printStackTrace();
                    gifFile.delete();
                    mImageView.setImageResource(defShare);
                }
            }
        });
    }

    /**
     * 展示静态图
     */
    private void handlePNGDisplay(Context context, final String filePath) {

        ((Activity) context).runOnUiThread(new Runnable() {

            @Override
            public void run() {
                try {
                    Bitmap bitmap = CommonFunction.createBitmap(filePath);
                    mImageView.setImageBitmap(bitmap);
                    ImageViewUtil.getDefault()
                            .loadImage(PathUtil.getFILEPrefix() + filePath, mImageView, defSmall,
                                    defSmall);//jiqiang
//					GlideUtil.loadImage(getContext(),PathUtil.getFILEPrefix( ) + filePath,mImageView,defSmall,defSmall);
                } catch (Exception e) {
                    e.printStackTrace();
                    mImageView.setImageResource(defShare);
                }
            }
        });
    }

    private void deleteFailFile(String fileName, String savePath) {
        File failFile = new File(savePath + fileName);
        if (failFile.exists()) {
            failFile.delete();
        }
    }

    class DownLoadGifThread extends Thread {

        private Context context;
        private DownloadFileCallback callback;
        private String fileUrl;

        public DownLoadGifThread(Context context, String url, DownloadFileCallback callback) {
            this.context = context;
            fileUrl = url;
            this.callback = callback;
        }

        @Override
        public void run() {
            String fileName = CryptoUtil.generate(fileUrl);
            String fileNameTemp = fileName + PathUtil.getTMPPostfix();
            try {
                FileDownloadManager manager = new FileDownloadManager(context, callback, fileUrl,
                        fileNameTemp, PathUtil.getFaceDir(), DEFAULT_FLAG);
                manager.run();
            } catch (ConnectionException e) {
                e.printStackTrace();
            }
            super.run();
        }
    }

    private DownloadFileCallback callback = new DownloadFileCallback() {

        @Override
        public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded, int flag) {

        }

        @Override
        public void onDownloadFileFinish(int flag, String fileName, String savePath) {
            String completedFileName = "";
            if (fileName.contains(PathUtil.getTMPPostfix())) {

                int endIndex = fileName.lastIndexOf(PathUtil.getTMPPostfix());
                completedFileName = (String) fileName.subSequence(0, endIndex);
                File tmpFile = new File(savePath + fileName);
                File completedFile = new File(savePath + completedFileName);
                if (tmpFile.exists()) {
                    tmpFile.renameTo(completedFile);
                }
            }
            handlerUIDisplay(getContext(), flag, completedFileName, savePath);
        }

        @Override
        public void onDownloadFileError(int flag, String fileName, String savePath) {
            deleteFailFile(fileName, savePath);
        }
    };

    @Override
    public void onClick(View v) {
        ChatRecord record = (ChatRecord) v.getTag(R.id.im_preview_uri);
        RecordFaceBean bean = GsonUtil.getInstance()
                .getServerBean(record.getContent(), RecordFaceBean.class);

        if (bean != null) {
//			FaceDetailActivity.launch( (Activity) getContext( ), Integer.parseInt( bean.pkgid ) );
            FaceDetailActivityNew.launch((Activity) getContext(), Integer.parseInt(bean.pkgid));
        }
    }

    @Override
    public void setContentClickEnabled(boolean isEnable) {
        llContent.setEnabled(isEnable);
    }

}
