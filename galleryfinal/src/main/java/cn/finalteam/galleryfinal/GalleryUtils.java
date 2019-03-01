package cn.finalteam.galleryfinal;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * Class: 图库工具类
 * Author：gh
 * Date: 2017/8/16 17:45
 * Email：jt_gaohang@163.com
 */
public class GalleryUtils {

    /**
     * 单例对象实例
     */
    private static GalleryUtils instance = null;

    public static GalleryUtils getInstance() {
        if (instance == null) {
            instance = new GalleryUtils();
        }
        return instance;
    }

    private ImageLoader imageLoader;
    private PauseOnScrollListener pauseOnScrollListener;
    private GalleryFinal.OnHanlderResultCallback callback;

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public void setPauseOnScrollListener(PauseOnScrollListener pauseOnScrollListener) {
        this.pauseOnScrollListener = pauseOnScrollListener;
    }

    /**
     * 选择多张图片
     * @param mContect
     * @param requestCode
     * @param maxCount
     * @param callback
     */
    public void openGalleryMuti(Context mContect, int requestCode, int maxCount, ArrayList<String> select, GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setMutiSelectMaxSize(maxCount);
        functionConfigBuilder.setSelected(select);
        final FunctionConfig functionConfig = functionConfigBuilder.build();
        functionConfig.setMimeType(FunctionConfig.TYPE_IMAGE);
        CoreConfig coreConfig = new CoreConfig.Builder(mContect, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGalleryMuti(requestCode,functionConfig , callback);
    }

    /**
     * 选择多张图片
     * @param mContect
     * @param requestCode
     * @param maxCount
     * @param callback
     */
    public void openGalleryMuti(Context mContect, int requestCode, int maxCount, GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setMutiSelectMaxSize(maxCount);
        final FunctionConfig functionConfig = functionConfigBuilder.build();
        functionConfig.setMimeType(FunctionConfig.TYPE_IMAGE);
        CoreConfig coreConfig = new CoreConfig.Builder(mContect, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGalleryMuti(requestCode,functionConfig , callback);
    }

    /**
     * 选择多张图片头像-裁剪
     * @param mContect
     * @param requestCode
     * @param callback
     */
    public void openGallerySingleCrop(Context mContect, int requestCode,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        functionConfigBuilder.setEnableEdit(true);
        functionConfigBuilder.setEnableCrop(true);
        functionConfigBuilder.setForceCrop(true);
        final FunctionConfig functionConfig = functionConfigBuilder.build();
        functionConfig.setMimeType(FunctionConfig.TYPE_IMAGE);
        CoreConfig coreConfig = new CoreConfig.Builder(mContect, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGallerySingle(requestCode, functionConfig, callback);
    }

    /**
     * 选择多张图片
     * @param mContect
     * @param requestCode
     * @param callback
     */
    public void openGallerySingle(Context mContect, int requestCode,GalleryFinal.OnHanlderResultCallback callback){
        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        final FunctionConfig functionConfig = functionConfigBuilder.build();
        functionConfig.setMimeType(FunctionConfig.TYPE_IMAGE);
        CoreConfig coreConfig = new CoreConfig.Builder(mContect, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGallerySingle(requestCode, functionConfig, callback);
    }


    /**
     * 选择单个视频
     * @param mContect
     * @param requestCode
     * @param callback
     */
    public void openVideoSingle(Context mContect, int requestCode,GalleryFinal.OnHanlderResultCallback callback){

        FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
        final FunctionConfig functionConfig = functionConfigBuilder.build();
        functionConfig.setMimeType(FunctionConfig.TYPE_VIDEO);
        CoreConfig coreConfig = new CoreConfig.Builder(mContect, imageLoader)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(pauseOnScrollListener)
                .setNoAnimcation(true)
                .build();
        GalleryFinal.init(coreConfig);

        GalleryFinal.openGallerySingle(requestCode, functionConfig, callback);
    }

    private ArrayList<PhotoInfo> mCurrent;

    public ArrayList<PhotoInfo> getmCurrent() {
        return mCurrent;
    }

    public void setmCurrent(ArrayList<PhotoInfo> mCurrent) {
        this.mCurrent = mCurrent;
    }
}
