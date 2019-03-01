
package net.iaround.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import net.iaround.conf.Config;
import net.iaround.privat.library.ImageLoadUtil;
import net.iaround.privat.library.ImageLoadUtil.DisplayConfig;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.UrlReplaceUtil;
import net.nostra13.universalimageloader.core.ImageLoader;
import net.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import net.nostra13.universalimageloader.core.assist.LoadedFrom;
import net.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import net.nostra13.universalimageloader.core.display.FadeInRoundedBitmapDisplayer;
import net.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import net.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import net.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.io.File;


/**
 * 一个用于加载图片到ImageView的工具类，可加载网络图片 使用时先调用init进行初始化
 * 
 * 包装了Universal-Image-Loader开源项目
 * 
 * String imageUri = "http://site.com/image.png"; // from Web String imageUri =
 * "file:///mnt/sdcard/image.png"; // from SD card String imageUri =
 * "content://media/external/audio/albumart/13"; // from content provider String
 * imageUri = "assets://image.png"; // from assets String imageUri =
 * "drawable://" + R.drawable.image; // from drawables (only images, non-9patch)
 * 
 * @author Huyf Email:my519820363@gmail.com
 * 
 */
public class ImageViewUtil
{
	private static ImageViewUtil sDefaultInstance;
	private ImageLoadUtil mImageLoadUtil;
	
	private static class DefaultConfig
	{
		public static final String BITMAP_DISKCACHE_DIR = "/cacheimage_new/";
	}
	
	public ImageLoader getImageLoader( )
	{
		return mImageLoadUtil.getImageLoader( );
	}
	
	private ImageViewUtil(Context context )
	{
		ImageLoadUtil.initDefault( context , new File( CommonFunction.getSDPath( ) ,
				DefaultConfig.BITMAP_DISKCACHE_DIR ) , Config.DEBUG );
		mImageLoadUtil = ImageLoadUtil.getDefault( );
	}
	
	/**
	 * 初始化ImageViewUtil并生成单例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static ImageViewUtil initDefault(Context context )
	{
		if ( sDefaultInstance == null )
		{
			sDefaultInstance = new ImageViewUtil( context );
		}
		
		return sDefaultInstance;
	}
	
	/**
	 * 获取ImageViewUtil实例
	 * 
	 * @return
	 */
	public synchronized static ImageViewUtil getDefault( )
	{
		if ( sDefaultInstance == null )
		{
			throw new RuntimeException( "Must be call initDefault(Context) befor!" );
		}
		
		return sDefaultInstance;
	}
	
	public void clearDefaultLoaderMemoryCache( )
	{
		mImageLoadUtil.clearDefaultLoaderMemoryCache( );
	}
	
	public boolean loadImageWithMemtory(String imageUri , ImageView imageView ,
										int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		if ( stubImageRes > 0 )
		{
			imageView.setImageResource( stubImageRes );
		}
		Bitmap bitmap = mImageLoadUtil.findMemoryCachedBitmapForImageUri( imageView , newUrl );
		if ( bitmap != null && !bitmap.isRecycled( ) )
		{
			SimpleBitmapDisplayer displayer = new SimpleBitmapDisplayer( );
			displayer.display( bitmap , new ImageViewAware( imageView ) ,
					LoadedFrom.MEMORY_CACHE );
			return true;
		}
		else
		{
			if ( loadFailImageRes > 0 )
			{
				imageView.setImageResource( loadFailImageRes );
			}
			return false;
		}
	}
	
	public boolean loadRoundedImageWithMemtory(String imageUri , ImageView imageView ,
											   int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		if ( stubImageRes > 0 )
		{
			imageView.setImageResource( stubImageRes );
		}
		Bitmap bitmap = mImageLoadUtil
				.findMemoryCachedBitmapForImageUri( imageView , newUrl );
		if ( bitmap != null && !bitmap.isRecycled( ) )
		{
			RoundedBitmapDisplayer displayer = new RoundedBitmapDisplayer( 10 );
			displayer.display( bitmap , new ImageViewAware( imageView ) ,
					LoadedFrom.MEMORY_CACHE );
			return true;
		}
		else
		{
			if ( loadFailImageRes > 0 )
			{
				imageView.setImageResource( loadFailImageRes );
			}
			return false;
		}
	}
	
	public boolean fadeInLoadWithMemtory(String imageUri , ImageView imageView ,
										 int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		if ( stubImageRes > 0 )
		{
			imageView.setImageResource( stubImageRes );
		}
		Bitmap bitmap = mImageLoadUtil
				.findMemoryCachedBitmapForImageUri( imageView , newUrl );
		if ( bitmap != null && !bitmap.isRecycled( ) )
		{
			FadeInBitmapDisplayer displayer = new FadeInBitmapDisplayer( 300 );
			displayer.display( bitmap , new ImageViewAware( imageView ) ,
					LoadedFrom.MEMORY_CACHE );
			return true;
		}
		else
		{
			if ( loadFailImageRes > 0 )
			{
				imageView.setImageResource( loadFailImageRes );
			}
			return false;
		}
	}
	
	public boolean fadeInRoundLoadWithMemtory(String imageUri , ImageView imageView ,
											  int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		if ( stubImageRes > 0 )
		{
			imageView.setImageResource( stubImageRes );
		}
		Bitmap bitmap = mImageLoadUtil
				.findMemoryCachedBitmapForImageUri( imageView , newUrl );
		if ( bitmap != null && !bitmap.isRecycled( ) )
		{
			FadeInRoundedBitmapDisplayer displayer = new FadeInRoundedBitmapDisplayer( 10 ,
					300 );
			displayer.display( bitmap , new ImageViewAware( imageView ) ,
					LoadedFrom.MEMORY_CACHE );
			return true;
		}
		else
		{
			if ( loadFailImageRes > 0 )
			{
				imageView.setImageResource( loadFailImageRes );
			}
			return false;
		}
	}
	
	/**
	 * 以无缓存的形式加载一张图片
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadImageNoCache(String imageUri , ImageView imageView , int stubImageRes ,
								 int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.supportDiskCache = true;
		displayConfig.supportMemoryCache = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadImageNoCache(String imageUri , ImageView imageView , int stubImageRes ,
								 int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.supportDiskCache = true;
		displayConfig.supportMemoryCache = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig );
	}
	
	/**
	 * 以无内存缓存的形式加载一张图片
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadRoundedImageNoCache(String imageUri , ImageView imageView ,
										int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildRounded( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.supportDiskCache = true;
		displayConfig.supportMemoryCache = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadImage(String imageUri , ImageView imageView , int stubImageRes ,
						  int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadImage(String imageUri , ImageView imageView , int stubImageRes ,
						  int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片，且用于ConvertView中（重置了ImageView）
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadImageInConvertView(String imageUri , ImageView imageView ,
									   int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadImageInConvertView(String imageUri , ImageView imageView ,
									   int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).build( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadRoundedImage(String imageUri , ImageView imageView , int stubImageRes ,
								 int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildRounded( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadBlurImage(String imageUri , ImageView imageView , int stubImageRes ,
							  int loadFailImageRes , Context context , int radius )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildBlur(
				context.getApplicationContext( ) , radius );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , null );
	}
	
	public void loadBlurImage(String imageUri , ImageView imageView , int stubImageRes ,
							  int loadFailImageRes , ImageLoadingListener listener , Context context , int radius )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildBlur(
				context.getApplicationContext( ) , radius );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadBlurImageNoCache(String imageUri , ImageView imageView ,
									 int stubImageRes , int loadFailImageRes , ImageLoadingListener listener ,
									 Context context , int radius )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildBlur(
				context.getApplicationContext( ) , radius );
		displayConfig.supportDiskCache = true;
		displayConfig.supportMemoryCache = false;
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadBlurImageInConvertView(String imageUri , ImageView imageView ,
										   int stubImageRes , int loadFailImageRes , ImageLoadingListener listener ,
										   Context context , int radius )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildBlur(
				context.getApplicationContext( ) , radius );
		displayConfig.isResetView = true;
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadRoundFrameImageInConvertView(String imageUri , ImageView imageView ,
												 int stubImageRes , int loadFailImageRes , ImageLoadingListener listener ,
												 int borderSize , String borderColor )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFrame( borderSize ,
				borderColor );
		displayConfig.isResetView = true;
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void loadRoundFrameImageInConvertViewNoMemoryCache(String imageUri , ImageView imageView ,
															  int stubImageRes , int loadFailImageRes , ImageLoadingListener listener ,
															  int borderSize , String borderColor )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFrame( borderSize ,
				borderColor );
		displayConfig.isResetView = true;
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.supportDiskCache = true;
		displayConfig.supportMemoryCache = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片，且用于ConvertView中（重置了ImageView）
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void loadRoundedImageInConvertView(String imageUri , ImageView imageView ,
											  int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildRounded( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片，且用于ConvertView中（重置了ImageView）
	 * 
	 * @param imageUri
	 * @param image
	 * @param stubImageRes
	 * @param loadFailImageRes
	 */
	public void fadeInLoadImageInConvertView(String imageUri , ImageView imageView ,
											 int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFadein( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
		
	}
	
	public void fadeInLoadImageInConvertView(String imageUri , ImageView imageView ,
											 int stubImageRes , int loadFailImageRes )
	{
		fadeInLoadImageInConvertView( imageUri , imageView , stubImageRes , loadFailImageRes , null );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片，且用于ConvertView中（重置了ImageView）
	 * 
	 * @param imageUri
	 * @param imageView
	 * @param stubImageRes
	 * @param loadFailImageRes
	 * @param listener
	 */
	public void fadeInRoundLoadImage(String imageUri , ImageView imageView ,
									 int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFadeInRound( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void fadeInLoadImageNoCache(String imageUri , ImageView imageView ,
									   int stubImageRes , int loadFailImageRes )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFadein( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = false;
		displayConfig.supportMemoryCache = false;
		displayConfig.supportDiskCache = false;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig );
	}
	
	/**
	 * 保存到双缓存的形式加载一张图片，且用于ConvertView中（重置了ImageView）
	 * 
	 * @param imageUri 图片地址
	 * @param imageView 图片控件
	 * @param stubImageRes 默认图
	 * @param loadFailImageRes 失败图
	 * @param listener 监听
	 */
	public void fadeInRoundLoadImageInConvertView(String imageUri , ImageView imageView ,
												  int stubImageRes , int loadFailImageRes , ImageLoadingListener listener )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFadeInRound( );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	/**
	 * @Title: fadeInRoundLoadImageInConvertView
	 * @Description: 显示一张自定义圆角的图片
	 * @param imageUri
	 *            图片地址
	 * @param imageView
	 *            图片控件
	 * @param stubImageRes
	 *            默认图
	 * @param loadFailImageRes
	 *            失败图
	 * @param listener
	 *            监听器
	 * @param roundPix
	 *            角度
	 */
	public void fadeInRoundLoadImageInConvertView(String imageUri , ImageView imageView ,
												  int stubImageRes , int loadFailImageRes , ImageLoadingListener listener ,
												  int roundPix )
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		DisplayConfig displayConfig = new DisplayConfig.Builder( ).buildFadeInRound( roundPix ,
				300 );
		displayConfig.stubImageRes = stubImageRes;
		displayConfig.loadFailImageRes = loadFailImageRes;
		displayConfig.isResetView = true;
		mImageLoadUtil.displayImage( newUrl , imageView , displayConfig , listener );
	}
	
	public void LoadImageNoEffect(String imageUri , ImageView imageView , ImageLoadingListener listener)
	{
		String newUrl = UrlReplaceUtil.getInstance().replaceImageUrl(imageUri);
		mImageLoadUtil.displayImage( newUrl , imageView, listener);
	}
	
}
