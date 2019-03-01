package net.iaround.tools.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import net.iaround.conf.Common;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.UrlReplaceUtil;
import net.iaround.ui.face.FaceDetailActivityNew;

/**
 * Created by Jzy on 2016/11/21.
 */
public class GlideUtil {

    /*通知加载图片完成
     */
    public interface IOnLoadBitmap{
        public void onLoadBitmapSussess(Bitmap bitmap);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).centerCrop().into(imageView);
    }
    /**
     * 加载图片-有缓存
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageCache(Context context, String imageUrl, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
    }

    /**
     * 加载图片-没有缓存
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageNoCache(Context context, String imageUrl, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().into(imageView);
    }




    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImage(Context context, String imageUrl, GlideDrawableImageViewTarget imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).centerCrop().into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImagePicture(Context context, String imageUrl, GlideDrawableImageViewTarget imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImage(Context context, String imageUrl, ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().centerCrop().placeholder(placeDrable).error(errorDrable).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageGif(Context context, String imageUrl, ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asGif().placeholder(placeDrable).error(errorDrable).into(imageView);
    }

    /**
     * 加载本地动态图片
     *
     * @param context
     * @param resource
     * @param imageView
     */
    public static void loadImageLocalGif(Context context, int resource, ImageView imageView) {
        //Glide.with(context).load(resource).asGif().centerCrop().placeholder(placeDrable).error(errorDrable).into(imageView);
        Glide.with(context).load(resource).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    /**
     * 加载网络图片
     *
     * @param context
     * @param path
     * @param imageView
     */
    public static void loadImageInfo(Context context, String path, ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(path);
        Glide.with(context).load(newUrl).placeholder(placeDrable).error(errorDrable).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param target
     */
    public static void loadImage(Context context, String imageUrl, SimpleTarget target) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().into(target);
    }
    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param target
     */
    public static void loadImageCrop(Context context, String imageUrl, SimpleTarget target) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().centerCrop().into(target);
    }


    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadRoundImage(final Context context, final String imageUrl, final int round, final ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).centerCrop().bitmapTransform(new GlideRoundTransform(context, round)).into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadRoundImage(final Context context, final String imageUrl, final int round, final ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).centerCrop().placeholder(placeDrable).error(errorDrable).bitmapTransform(new GlideRoundTransform(context, round)).into(imageView);
    }

    /**
     * 加载圆角图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadRoundImageNew(final Context context, final String imageUrl, final int round, final ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).placeholder(placeDrable).dontAnimate().error(errorDrable).bitmapTransform(new CenterCrop(context),new GlideRoundTransform(context, round)).into(imageView);
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param placeDrable
     */
    public static void loadCircleImage(final Context context, final String imageUrl, final ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().placeholder(placeDrable).error(errorDrable).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    /**
     * 加载圆形图片-缓存
     *
     * @param context
     * @param imageUrl
     * @param imageView
     * @param placeDrable
     */
    public static void loadCircleChaceImage(final Context context, final String imageUrl, final ImageView imageView, int placeDrable, int errorDrable) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).placeholder(placeDrable).error(errorDrable).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadCircleImage(final Context context, final String imageUrl, final ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    /**
     * 加载圆形图片设置边框
     * @param context
     * @param url
     * @param imageView
     * @param placeDrable
     * @param borderSize
     */
    public static void loadCircleImageFrame(final Context context, final String url, final ImageView imageView, int placeDrable, int borderSize,int color) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(url);
        Glide.with(context).load(newUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().dontAnimate().placeholder(placeDrable).transform(new GlideCircleTransform(context,borderSize,context.getResources().getColor(color))).into(imageView);
    }

    /**
     * 加载圆形图片设置边框
     * @param context
     * @param url
     * @param imageView
     * @param placeDrable
     * @param borderSize
     * @param isCache
     */
    public static void loadCircleImageFrame(final Context context, final String url, final ImageView imageView, int placeDrable, int borderSize,int color,boolean isCache) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(url);
        Glide.with(context).load(newUrl).skipMemoryCache(isCache).diskCacheStrategy(DiskCacheStrategy.NONE).centerCrop().dontAnimate().placeholder(placeDrable).transform(new GlideCircleTransform(context,borderSize,context.getResources().getColor(color))).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageDefault(Context context, int imageUrl, ImageView imageView) {
        Glide.with(context).load(imageUrl).into(imageView);
    }
    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageDefault(Context context, String imageUrl, RequestListener listener, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).listener(listener).into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImage(Context context, String imageUrl, SimpleTarget target, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().into(target);
    }
    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImage(Context context, String imageUrl, RequestListener listener, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().dontAnimate().listener(listener).centerCrop().into(imageView);
    }

    /**
     * 加载图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadImageDynamic(Context context, String imageUrl, ImageView imageView) {
        String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().dontAnimate().centerCrop().into(imageView);
    }


    /**
     * 加载圆形图片
     *
     * @param context
     * @param imageUrl
     * @param imageView
     */
    public static void loadCircleImage(final Context context, final String imageUrl, final ImageView imageView, int placeHolder, final IOnLoadBitmap onLoadBitmap) {
        final String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUrl);
        Glide.with(context).load(newUrl).asBitmap().placeholder(placeHolder).centerCrop().into(new BitmapImageViewTarget(imageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                imageView.setImageDrawable(circularBitmapDrawable);
                if(onLoadBitmap!=null) {
                    onLoadBitmap.onLoadBitmapSussess(resource);
                }
            }
        });
    }


}
