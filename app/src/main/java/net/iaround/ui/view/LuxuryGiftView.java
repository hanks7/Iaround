package net.iaround.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import net.iaround.tools.DownloadCallback;
import net.iaround.tools.OkhttpDownload;
import net.iaround.utils.ScreenUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 作者：zx on 2017/7/12 14:13
 * 豪华礼物，因动态设置，此view的父布局为RelativeLayout（暂时）
 */
public class LuxuryGiftView extends SimpleDraweeView {

    private Context mContext;
    private WindowManager wm;
    private int screenWidth = 1080;
    private int defaultMarginTop = 33;//view默认距上的距离
    private Animatable animatable;
    private Handler handler = new Handler();
    private static final String deskPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/iaround/.ia_download";
    private static final String FILE_SUFFIX = ".webp";
    private String currentTime;
    private LuxuryGiftPlayListener luxuryGiftPlayListener;
    private int width = 370;//view默认宽度
    private int height = 180;//view默认高度
    private long playTime = 3500;//view默认播放时间


    public LuxuryGiftView(Context context) {
        this(context, null);
    }

    public LuxuryGiftView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LuxuryGiftView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init() {
        wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
    }

    /**
     * 设置当前view距离上部的位置
     *
     * @param @param marginTop 当前动画距离顶部的位置，如果当前值设为0，则使用默认距离
     */
    public void setHeightPosition(int marginTop) {
        if (marginTop <= 0){
            marginTop = defaultMarginTop;
        }
        marginTop = ScreenUtils.dp2px(marginTop);
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(getLayoutParams());
        margin.setMargins(margin.leftMargin, marginTop, margin.rightMargin, margin.bottomMargin);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(margin);
        setLayoutParams(params);
    }

    /**
     * 获取sd上的礼物
     * @param url
     * @param fileName
     * @return
     */
    private File getGiftFile(final String url, final String fileName){
        final String suffixFileName = fileName + FILE_SUFFIX;
        File file = new File(deskPath, suffixFileName);
        if (!file.exists() || getFileSize(file) <= 0){//如果礼物不存在，重新下载
            OkhttpDownload downloadManager = new OkhttpDownload(mContext.getApplicationContext());
            downloadManager.downLoadCallback(url, fileName, new DownloadCallback() {
                @Override
                public void DownloadSuccess() {
                    showLuxuryGift(url, fileName, currentTime);
                }

                @Override
                public void DownloadFailure(String errorMsg) {

                }
            });
            return null;
        }
        return file;
    }


    /**
     * 获取指定文件大小
     * @param file
     * @return
     * @throws Exception 　　
     */
    public static long getFileSize(File file){
        long size = 0;
        try {
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                size = fis.available();
            } else {
                file.createNewFile();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * @param fileName  文件名 格式为：name_width_height_playTime
     *                      name：礼物名称
     *                      width：宽度
     *                      height：高度
     *                      playTime：播放时间
     *                      例如：十全十美_370_180_4
     *
     */
    @SuppressLint("WrongCall")
    public void showLuxuryGift(String url, String fileName, String time) {
        currentTime = time;
        if (TextUtils.isEmpty(fileName)) {
            stop();
            return;
        }
        File file = getGiftFile(url, fileName);
        if (null == file){
            stop();
            return;
        }
        //截取文件名，获取文件宽高和播放时间
        String[] giftInfos = fileName.split("_");
        int length = giftInfos.length;
        if (length > 2){
            width = Integer.parseInt(giftInfos[length - 3]);
            height = Integer.parseInt(giftInfos[length - 2]);
            playTime = (long) (Double.parseDouble(giftInfos[length - 1]) * 1000);
        }

        //设置view宽高
        setParams(width, height);
        Uri uri = null;
        if (file.exists()) {
            uri = Uri.parse("file://" + file);
        }

        AbstractDraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(controllerListener)
                .build();
        setController(controller);
        setVisibility(VISIBLE);
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, playTime);
    }

    private void setParams(int width, int height){
        int pxWidth = ScreenUtils.dp2px(width);
        int pxHeight = ScreenUtils.dp2px(height);
        if (pxWidth > screenWidth){
            pxWidth = (int) (screenWidth * 0.9);
            int i = ScreenUtils.dp2px(width);
            float i1 = ((float)pxWidth) / i;
            pxHeight = (int) (i1 * pxHeight);
        }
        //设置view宽高
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pxWidth, pxHeight);
        setLayoutParams(params);
        //设置view默认在屏幕上的位置
        setHeightPosition(defaultMarginTop);
    }

    private ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
            if (animatable != null) {
                animatable.start();
            }
        }
    };

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stop();
        }
    };

    /**
     * 停止动画
     */
    private void stop() {
        DraweeController controller = getController();
        if (null != controller){
            animatable = controller.getAnimatable();
            if (animatable != null) {
                animatable.stop();
                animatable = null;
            }
        }
        setVisibility(GONE);
        if (null != luxuryGiftPlayListener){
            luxuryGiftPlayListener.playEnd(currentTime);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void setLuxuryGiftPlayListener(LuxuryGiftPlayListener luxuryGiftPlayListener) {
        this.luxuryGiftPlayListener = luxuryGiftPlayListener;
    }

    public interface LuxuryGiftPlayListener{
        void playEnd(String currentTime);
    }

    public void upDateDefaultMargin(int marginTop){
        this.defaultMarginTop = marginTop;
    }
}
