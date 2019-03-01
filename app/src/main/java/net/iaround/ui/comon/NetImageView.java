package net.iaround.ui.comon;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;


/** 通过异步加载图片 */
public class NetImageView extends RelativeLayout
{
	private ImageView mImageView;
	private ImageView mHDImageView;
	private ImageView mIsVip;
	private ProgressBar mProgressBar;
	private int resId = R.drawable.z_common_vip_icon;
	public static final int DEFAULT_AVATAR_ROUND_LIGHT = R.drawable.default_avatar_round_light;
//	public static final int DEFAULT_FACE = R.drawable.default_face_small;
	public static final int DEFAULT_FACE = R.drawable.iaround_default_img;
	public static final int NEARUSER_DEFAULT_FACE = R.drawable.near_default_face_small;
	public static final int DEFAULT_SMALL = R.drawable.default_pitcure_small;

	private enum LoadType
	{
		Simple,
		Round,
		Fade,
		FadeRound,
		Blur,
		RoundFrame,
		NOEffect
	}
	
	public NetImageView(Context context )
	{
		super( context );
		init( context );
	}
	
	public NetImageView(Context context, AttributeSet attrs )
	{
		super( context, attrs );
		init( context );
	}
	
	public NetImageView(Context context, AttributeSet attrs, int defStyle )
	{
		super( context, attrs, defStyle );
		init( context );
	}

	@SuppressWarnings("ResourceType")
	private void init( Context context )
	{
		// 实际图片
		mImageView = new ImageView( context );
		mImageView.setId( 1000 );// 任意指定一个ID
		mImageView.setScaleType( ScaleType.FIT_XY );
		RelativeLayout.LayoutParams lpIv = new RelativeLayout.LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT );
		mImageView.setLayoutParams( lpIv );
		addView( mImageView );
	}

	public void setScaleType( ScaleType type )
	{
		if ( mImageView != null )
		{
			mImageView.setScaleType( type );
		}
	}

	/**
	 * 设置高清图标记
	 *
	 * @param type 1为默认 小图，2为大图
	 */
	public void setHDImageView( int type )
	{
		if ( mHDImageView == null )
		{
			// 高清标记
			mHDImageView = new ImageView( getContext( ) );
			mHDImageView.setImageResource( R.drawable.hd );
			mHDImageView.setVisibility( GONE );
			RelativeLayout.LayoutParams lpHd = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			lpHd.addRule( RelativeLayout.ALIGN_LEFT, mImageView.getId( ) );
			lpHd.addRule( RelativeLayout.ALIGN_BOTTOM, mImageView.getId( ) );
			mHDImageView.setLayoutParams( lpHd );
			addView( mHDImageView );
		}

		if ( type == 1 )
		{
			mHDImageView.setVisibility( VISIBLE );
			mHDImageView.setImageResource( R.drawable.hd );
		}
		else if ( type == 2 )
		{
			RelativeLayout.LayoutParams lpHd = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			lpHd.addRule( RelativeLayout.ALIGN_RIGHT, mImageView.getId( ) );
			lpHd.addRule( RelativeLayout.ALIGN_BOTTOM, mImageView.getId( ) );
			lpHd.bottomMargin = 8;
			lpHd.rightMargin = 8;
			mHDImageView.setLayoutParams( lpHd );
			mHDImageView.setVisibility( VISIBLE );
			mHDImageView.setImageResource( R.drawable.zoom_out );
		}
		else
		{
			mHDImageView.setVisibility( GONE );
		}
	}

	/**
	 * 返回高清图控件,必须先setHDImageView才能返回，因为hdImageView是动态按需加载的
	 *
	 * @return
	 */
	public ImageView getHDImageView( )
	{
		return mHDImageView;
	}

	public ImageView getImageView( )
	{
		return this.mImageView;
	}

	/**
	 * 在图片excute之前设置是否显示loading
	 */
	public void setProgressBarVisible( boolean isvisible )
	{
		if ( isvisible && mProgressBar == null )
		{
			// 滚动条
			mProgressBar = new ProgressBar( getContext( ) );
			int dp_24 = ( int ) ( getResources( ).getDimension( R.dimen.dp_1 ) * 24 );
			RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams( dp_24, dp_24 );
			lpPb.addRule( RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE );
			mProgressBar.setIndeterminate( true );
			mProgressBar
				.setIndeterminateDrawable( getResources( ).getDrawable( R.drawable.pull_ref_pb ) );
			mProgressBar.setLayoutParams( lpPb );
			addView( mProgressBar );
		}

		if ( mProgressBar != null )
		{
			mProgressBar.setVisibility( isvisible ? VISIBLE : GONE );
		}
	}


	public void setVipRes( int vipRes, int vipLevel )
	{
		resId = vipRes;
		setVipLevel( vipLevel, false );
	}


	public void setVipLevel( int vipLevel )
	{

		setVipLevel( vipLevel, false );
	}

	public void setVipLevel( int vipLevel, boolean forceSmall )
	{
		// 由于目前不分VIP等级，因此暂不考虑
		if ( mIsVip == null )
		{
			// vip标志
			mIsVip = new ImageView( getContext( ) );
			mIsVip.setVisibility( GONE );


			int width = this.getLayoutParams( ).width * 4 / 11;
			//			int marginSize = width / 3;
			//			int width = this.getLayoutParams().width;
			int marginSize = 0;
			RelativeLayout.LayoutParams lpVIP = new RelativeLayout.LayoutParams( width, width );
			lpVIP.addRule( RelativeLayout.ALIGN_RIGHT, mImageView.getId( ) );
			lpVIP.addRule( RelativeLayout.ALIGN_BOTTOM, mImageView.getId( ) );

			lpVIP.setMargins( 0, 0, marginSize, marginSize );
			mIsVip.setLayoutParams( lpVIP );
			mIsVip.setVisibility( GONE );
			addView( mIsVip );
		}

		if ( vipLevel > 0 )
		{

			ViewGroup.LayoutParams lp = getLayoutParams( );
			if ( lp != null )
			{
				int dp_40 = CommonFunction.dipToPx( getContext( ), 40 );
			}
			mIsVip.setImageResource( resId );
			mIsVip.setVisibility( VISIBLE );
		}
		else
		{
			mIsVip.setVisibility( GONE );
		}
	}

	public void setVipLevel( int vipLevel, boolean forceSmall, int XRelativeALIGN,
		int yRelativeALIGN )
	{
		// 由于目前不分VIP等级，因此暂不考虑
		if ( mIsVip == null )
		{
			// vip标志
			mIsVip = new ImageView( getContext( ) );
			mIsVip.setVisibility( GONE );
			RelativeLayout.LayoutParams lpVIP = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			lpVIP.addRule( XRelativeALIGN, mImageView.getId( ) );
			lpVIP.addRule( yRelativeALIGN, mImageView.getId( ) );
			int dp_6 = ( int ) ( getResources( ).getDimension( R.dimen.dp_1 ) * 6 );
			lpVIP.setMargins( 0, 0, dp_6, dp_6 );
			mIsVip.setLayoutParams( lpVIP );
			mIsVip.setVisibility( GONE );
			addView( mIsVip );
		}

		if ( vipLevel > 0 )
		{
			int resId = R.drawable.vip;
			ViewGroup.LayoutParams lp = getLayoutParams( );
			if ( lp != null )
			{
				int dp_40 = CommonFunction.dipToPx( getContext( ), 40 );
				if ( forceSmall || ( lp.width > 0 && lp.width <= dp_40 ) )
				{
					resId = R.drawable.vip;
				}
			}
			mIsVip.setImageResource( resId );
			mIsVip.setVisibility( VISIBLE );
		}
		else
		{
			mIsVip.setVisibility( GONE );
		}
	}

	public void setVipLevel40( int vipLevel )
	{
		// 由于目前不分VIP等级，因此暂不考虑
		if ( mIsVip == null )
		{
			// vip标志
			mIsVip = new ImageView( getContext( ) );
			mIsVip.setVisibility( GONE );
			RelativeLayout.LayoutParams lpVIP = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT );
			lpVIP.addRule( RelativeLayout.ALIGN_RIGHT, mImageView.getId( ) );
			lpVIP.addRule( RelativeLayout.ALIGN_BOTTOM, mImageView.getId( ) );
			int dp_6 = ( int ) ( getResources( ).getDimension( R.dimen.dp_1 ) * 6 );
			lpVIP.setMargins( 0, 0, dp_6, dp_6 );
			int screenWidth = CommonFunction.getScreenPixWidth( getContext( ) );
			lpVIP.width = ( int ) ( 40 / 480.0 * screenWidth );
			lpVIP.height = ( int ) ( 32 / 480.0 * screenWidth );
			mIsVip.setLayoutParams( lpVIP );
			mIsVip.setVisibility( GONE );
			addView( mIsVip );
		}
		
		if ( vipLevel > 0 )
		{
			int resId = R.drawable.vip;
			mIsVip.setBackgroundResource( resId );
			mIsVip.setVisibility( VISIBLE );
		}
		else
		{
			mIsVip.setVisibility( GONE );
		}
	}
	
	/**
	 * 加载一张圆角图片
	 *
	 * @param defaultImage
	 * @param url
	 */
	public void executeRound( int defaultImage, String url )
	{
		execute( LoadType.Round, defaultImage, url );
	}
	
	/**
	 * 渐入加载一张图片
	 *
	 * @param defaultImage
	 * @param url
	 */
	public void executeFadeIn( int defaultImage, String url )
	{
		execute( LoadType.Fade, defaultImage, url );
	}
	
//	public void executeFadeIn(int defaultImage, String url, ImageLoadingListener listener )
//	{
//		execute( LoadType.Fade, defaultImage, url, listener );
//	}
	
	public void executeFadeInRound( int defaultImage, String url )
	{
		execute( LoadType.FadeRound, defaultImage, url );
	}
	
//	public void executeFadeInRound(int defaultImage, String url, ImageLoadingListener listener )
//	{
//		execute( LoadType.FadeRound, defaultImage, url, listener );
//	}
	
	public void executeBlur( int defaultImage, String url )
	{
		execute( LoadType.Blur, defaultImage, url );
	}
	
//	public void executeBlur(int defaultImage, String url ,)
//	{
//		execute( LoadType.Blur, defaultImage, url, listener );
//	}
	
	public void executeRoundFrame( int defaultImage, String url )
	{
		execute( LoadType.RoundFrame, defaultImage, url );
	}
	
//	public void executeRoundFrame(int defaultImage, String url)
//	{
//		execute( LoadType.RoundFrame, defaultImage, url, listener );
//	}
	/**
	 * 加载图片
	 *
	 * @param defaultImage
	 * @param url
	 */
	public void execute( int defaultImage, String url ,int round)
	{
		execute( LoadType.Round, defaultImage, url );
	}
	/**
	 * 加载图片
	 *
	 * @param defaultImage
	 * @param url
	 */
	public void execute( int defaultImage, String url )
	{
		execute( LoadType.Simple, defaultImage, url );
	}
	
	/**
	 * 加载没有默认图片的图片
	 *
	 * @param url
	 */
	public void execute(String url )
	{
		execute( LoadType.NOEffect, 0, url );
	}
	
//	/**
//	 * 加载图片
//	 *
//	 * @param defaultImage
//	 * @param url
//	 * @param listener
//	 */
//	public void execute(int defaultImage, String url, ImageLoadingListener listener )
//	{
//		execute( LoadType.Simple, defaultImage, url, listener );
//	}
	
	/**
	 * 加载图片
	 *
	 * @param type         类型
	 * @param defaultImage 默认图片
	 * @param url          图片链接
	 * @param onlyMem      仅仅从内存加载
	 * @param listener     监听器
	 * @return
	 */
	public boolean execute( LoadType type, int defaultImage, String url )
	{
		if ( CommonFunction.isEmptyOrNullStr( url ) )
		{
			// 当图片链接为空，设置默认图片并且隐藏进度条
			mImageView.setImageResource( defaultImage );
			hideProgressBar( );
		}
		
		if ( url != null && mImageView.getTag( ) != null &&
			url.equals( String.valueOf( mImageView.getTag( ) ) ) )
		{
			// 与上次加载的是同一张图片
			hideProgressBar( );
			return true;
		}
		
//		mImageView.setTag( url );
		switch ( type )
		{
			case Simple:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
				break;
			case Round:
				GlideUtil.loadRoundImage(BaseApplication.appContext,url,32,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( )
//					.loadRoundedImageInConvertView( url, mImageView, defaultImage, defaultImage,
//							null );
				break;
			case Fade:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( )
//					.fadeInLoadImageInConvertView( url, mImageView, defaultImage, defaultImage,
//							null );
				break;
			case FadeRound:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( )
//					.fadeInRoundLoadImageInConvertView( url, mImageView, defaultImage, defaultImage,
//							null );
				break;
			case Blur:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( )
//					.loadBlurImageInConvertView( url, mImageView, defaultImage, defaultImage, this,
//						getContext( ), 12 );
				break;
			case RoundFrame:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( )
//					.loadRoundFrameImageInConvertView( url, mImageView, defaultImage, defaultImage,
//							null, 0, "#ffffff" );
				break;
			case NOEffect:
				GlideUtil.loadImage(BaseApplication.appContext,url,mImageView,defaultImage,defaultImage);
//				ImageViewUtil.getDefault( ).LoadImageNoEffect( url, mImageView, null );
				break;
		}
		
		return false;
	}
	
	private void hideProgressBar( )
	{
		if ( mProgressBar != null )
		{
			mProgressBar.setVisibility( View.GONE );
		}
	}
	
	
}
