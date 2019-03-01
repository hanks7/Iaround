
package net.iaround.ui.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PathUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import net.iaround.tools.picture.ScaledImageView.OnMatrixChangedListener;
import net.iaround.tools.picture.ScaledImageView;


/**
 * 图片裁剪工具
 * <p>
 * intent参数列表如下：<br>
 * bitmap -- String -- req -- 即将被裁减的图片路劲<br>
 * returnOriBm -- boolean -- opt -- 是否返回原图，默认为false<br>
 * output -- String -- req -- 裁减图片输出路径<br>
 * outputX -- int -- req -- 裁减图片输出宽度<br>
 * outputY -- int -- req -- 裁减图片输出高度
 * 
 * @author 余勋杰
 */
public class PhotoCropActivity extends BaseActivity implements Callback , OnMatrixChangedListener, Runnable{
	public static final String BITMAP = "bitmap";
	public static final String RETURN_ORI_BITMAP = "returnOriBm";
	public static final String RETURN_SMALL_ORI_BITMAP = "returnSmallOriBm";
	public static final String OUTPUT = "output";
	public static final String OUTPUT_X = "outputX";
	public static final String OUTPUT_Y = "outputY";
	
	private static final double MAX_INPUT_BOUND_LEN = 1280; // 图片最大边长
	private static final double MAX_INPUT_SIZE; // 图片最大体积128KB(256KB = 128KB *
												// 1.999...)
	private static final int MIN_HD_BOUND_LEN = 640; // 高清图最小边长
	private static final int MIN_ROUND_INTER = 500; // 0.5s内仅会响应一次旋转操作
	private static final int MARGIN = 20;
	private static final int MSG_CANCEL_FINISH = 0xf0;
	private static final int MSG_OK_FINISH = 0xf1;
	private static final int MSG_BITMAP_GOT = 0xf2;
	private static final int MSG_VIEW = 0xffff;
	private Bitmap bitmap;
	private String originalPath; // 裁剪前图片地址
	private String outputPath; // 裁剪后图片地址
	private int[ ] outputSize; // 输出尺寸
	private boolean returnOriBm; // 是否返回原图
	private ScaledImageView ivPhoto;
	private Rect cropRect;
	private Handler handler;
	private Dialog procDlg;
	private long lastClickTime;
	private View abTitle;
	private View blank;
	private View content;
	
	//是否聊天界面中跳转过来的
	public static String ISCHAT_KEY = "isChat";
	private boolean bIsChatView = false;
	
	static
	{
		long totalMem = CommonFunction.getTotalMemory( );
		CommonFunction.log( "System.out" , "totalMem = " + totalMem );
		if ( totalMem > 512 * 1024 * 1024 )
		{ // 512MB以上
			MAX_INPUT_SIZE = 256 * 1024;
		}
		else if ( totalMem > 256 * 1024 * 1024 )
		{ // 256MB
			MAX_INPUT_SIZE = 128 * 1024;
		}
		else if ( totalMem > 0 )
		{ // 256MB以下
			MAX_INPUT_SIZE = 64 * 1024;
		}
		else
		{ // 默认
			MAX_INPUT_SIZE = 128 * 1024;
		}
	}
	
	protected void onCreate( Bundle savedInstanceState )
	{
//		setTheme( android.R.style.Theme_NoTitleBar );
		super.onCreate( savedInstanceState );
		
	


		handler = new Handler( this );
	
		
		content = LayoutInflater.from( this ).inflate( R.layout.photo_crop_page ,null);
		
		setContentView( content );
		
		bIsChatView = getIntent( ).getBooleanExtra( ISCHAT_KEY , false );
		
		uiInit( );
		
		getData( );
		
		
		content.postDelayed( new Runnable( )
		{
			
			@Override
			public void run( )
			{
				// TODO Auto-generated method stub
				handler.sendEmptyMessage( MSG_VIEW );
			}
		} ,1000 ) ;
		
		
	}
	
	protected void onDestroy( )
	{
		if ( bitmap != null && !bitmap.isRecycled( ) )
		{
			bitmap.recycle( );
			System.gc( );
		}
		super.onDestroy( );
	}
	
	public boolean handleMessage( Message msg )
	{
		if ( procDlg != null && procDlg.isShowing( ) )
		{
			try
			{
				procDlg.dismiss( );
			}
			catch ( Throwable t )
			{
				t.printStackTrace( );
			}
		}
		
		switch ( msg.what )
		{
			case MSG_CANCEL_FINISH :
			{
				setResult( RESULT_CANCELED );
				finish( );
			}
				break;
			case MSG_OK_FINISH :
			{
				setResult( RESULT_OK , ( Intent ) msg.obj );
				finish( );
			}
				break;
			case MSG_BITMAP_GOT :
			{
				if(bIsChatView)
				{
					getRect( );
				}else
				{
					getCropRect( );
				}

				ivPhoto.setBitmap( bitmap );
			}
				break;
				
			case MSG_VIEW:
				
				content.invalidate( );
				
				break;
			
		}
		return false;
	}
	
	private void getData( )
	{
		Bundle extra = getIntent( ).getExtras( );
		if(extra == null){  //解决crash bug
			setResult( RESULT_CANCELED );
			finish( );
		}
		
		// 图片输入路径
		originalPath = extra.getString( BITMAP );
		if ( originalPath == null || originalPath.trim( ).length( ) <= 0 )
		{
			setResult( RESULT_CANCELED );
			finish( );
		}
		
		// 是否返回原图
		returnOriBm = extra.getBoolean( RETURN_ORI_BITMAP , false );
		getBitmap( );
		
		// 图片输出路径
		outputPath = extra.getString( OUTPUT );
		if ( outputPath == null || outputPath.trim( ).length( ) <= 0 )
		{
			setResult( RESULT_CANCELED );
			finish( );
		}
		
		// 图片输出尺寸
		outputSize = new int[ 2 ];
		outputSize[ 0 ] = extra.getInt( OUTPUT_X , 0 );
		outputSize[ 1 ] = extra.getInt( OUTPUT_Y , 0 );
		if ( outputSize[ 0 ] <= 0 || outputSize[ 1 ] <= 0 )
		{
			setResult( RESULT_CANCELED );
			finish( );
		}
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree( String path )
	{
		int degree = 0;
		try
		{
			ExifInterface exifInterface = new ExifInterface( path );
			int orientation = exifInterface.getAttributeInt( ExifInterface.TAG_ORIENTATION ,
					ExifInterface.ORIENTATION_NORMAL );
			switch ( orientation )
			{
				case ExifInterface.ORIENTATION_ROTATE_90 :
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180 :
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270 :
					degree = 270;
					break;
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		return degree;
	}
	
	/*
	 * 旋转图片
	 * 
	 * @param angle
	 * 
	 * @param bitmap
	 * 
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView( int angle , Bitmap bitmap )
	{
		// 旋转图片 动作
		Matrix matrix = new Matrix( );
		matrix.postRotate( angle );
		System.out.println( "angle2=" + angle );
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap( bitmap , 0 , 0 , bitmap.getWidth( ) ,
				bitmap.getHeight( ) , matrix , true );
		return resizedBitmap;
	}
	
	/** 造出图片来，如果图片尺寸太大，或者体积太大，会被缩小之后才显示 */
	private void getBitmap( )
	{
		if ( procDlg != null && procDlg.isShowing( ) )
		{
			procDlg.dismiss( );
		}
		procDlg = DialogUtil.showProgressDialog( this , R.string.photo_crop_title ,
				R.string.please_wait , null );
		new Thread( this ).start( );
	}
	
	/** 解析图片，子线程调用 */
	public void run( )
	{
		File originalFile = new File( originalPath );
		if ( !originalFile.exists( ) )
		{
			Message msg = new Message( );
			msg.what = MSG_CANCEL_FINISH;
			handler.sendMessage( msg );
			return;
		}
		
		try
		{
			/**
			 * 获取图片的旋转角度，有些系统把拍照的图片旋转了，有的没有旋转
			 */
			int degree = readPictureDegree( originalFile.getAbsolutePath( ) );
			
			// 获取采样比例
			Options oriOpt = new Options( );
			oriOpt.inJustDecodeBounds = true;
			CommonFunction.createBitmap( originalPath , oriOpt );
			int oriMaxBound = oriOpt.outWidth > oriOpt.outHeight ? oriOpt.outWidth
					: oriOpt.outHeight;
			double sizeSample = oriMaxBound / MAX_INPUT_BOUND_LEN;
			double lenSample = Math.sqrt( originalFile.length( ) / MAX_INPUT_SIZE );
			CommonFunction.log( "System.out" , "sizeSample = " + sizeSample + ", lenSample = "
					+ lenSample );
			double sampleSize = ( sizeSample > lenSample ? sizeSample : lenSample );
			returnOriBm = returnOriBm && ( oriMaxBound >= MIN_HD_BOUND_LEN );
			
			// 造图片
			if ( sampleSize > 1 )
			{
				Options bmOpt = new Options( );
				bmOpt.inSampleSize = ( int ) ( sampleSize + 0.5 );
				bitmap = CommonFunction.createBitmap( originalPath , bmOpt );
			}
			else
			{
				bitmap = CommonFunction.createBitmap( originalPath );
			}
			if ( bitmap == null || bitmap.isRecycled( ) )
			{
				Message msg = new Message( );
				msg.what = MSG_CANCEL_FINISH;
				handler.sendMessage( msg );
				return;
			}
			/**
			 * 把图片旋转为正的方向
			 */
			bitmap = rotaingImageView( degree , bitmap );
			
			CommonFunction.log( "shifengxiong","把图片旋转=================="+degree );
			
			// 如果想要返回大图，并且大图的体积还比体积上限大，则须要做压缩
			if (( returnOriBm && lenSample > 1 )||degree!=0)
			{
			
				originalPath = PathUtil.getPictureDir() + System.currentTimeMillis( );
				File newInputFile = new File( originalPath );
				if ( newInputFile.exists( ) )
				{
					newInputFile.delete( );
				}
				newInputFile.createNewFile( );
				FileOutputStream fos = new FileOutputStream( newInputFile );
				bitmap.compress( CompressFormat.JPEG , 100 , fos );
				fos.flush( );
				fos.close( );
			}
		

			
			Message msg = new Message( );
			msg.what = MSG_BITMAP_GOT;
			handler.sendMessage( msg );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			Message msg = new Message( );
			msg.what = MSG_CANCEL_FINISH;
			handler.sendMessage( msg );
		}
	}
	
	private void uiInit( ) {
		OnClickListener ocListener = new OnClickListener( ) {
			public void onClick( View v )
			{
				PhotoCropActivity.this.onClick( v );
			}
		};

		abTitle = content.findViewById( R.id.abTitle );
		( ( TextView ) abTitle.findViewById( R.id.title_name ) )
				.setText( R.string.photo_crop_title );
		( ( TextView ) abTitle.findViewById( R.id.title_left_text ) )
				.setText( R.string.cancel );
		abTitle.findViewById( R.id.title_left_text ).setOnClickListener( ocListener );
		( ( TextView ) abTitle.findViewById( R.id.title_right_text ) ).setText( R.string.ok );
		abTitle.findViewById( R.id.title_right_text ).setOnClickListener( ocListener );
		content.findViewById( R.id.btnLeft ).setOnClickListener( ocListener );
		content.findViewById( R.id.btnOut ).setOnClickListener( ocListener );
		content.findViewById( R.id.btnIn ).setOnClickListener( ocListener );
		content.findViewById( R.id.btnRight ).setOnClickListener( ocListener );
		content.findViewById( R.id.llBottom ).setOnTouchListener(new OnTouchListener( )
		{
			public boolean onTouch( View v , MotionEvent event )
			{
				return true;
			}
		} );
		ivPhoto = ( ScaledImageView ) content.findViewById( R.id.ivPhoto );
		ivPhoto.setOnMatrixChangedListener(this);
		blank = content.findViewById( R.id.TextView02 ) ;
		//对不同跳转的View显示不同
		showDifferentView(bIsChatView);
		
		
	}
	
	private void showDifferentView(boolean isChatView)
	{
		int isVisibility = isChatView ? View.GONE : View.VISIBLE;
		
		content.findViewById( R.id.rlContnet ).setVisibility( isVisibility );
		content.findViewById( R.id.linearLayout4 ).setVisibility( isVisibility );
		content.findViewById( R.id.vCrop ).setVisibility( isVisibility );
		content.findViewById( R.id.llBottom ).setVisibility( isVisibility );
		 
		
	}
	
	
	/* 获取裁剪页面的白色框的布局数据 -旧版本的图片处理*/
	private void getCropRect( )
	{
//		int statusBarHeight = CommonFunction.dipToPx( this , 25 );
		int statusBarHeight =CommonFunction.getStatusBarHeight(this);
		int toolBarHeight = CommonFunction.dipToPx( this , 70 );
		int scrWidth = CommonFunction.getScreenPixWidth( this );
		int scrHeight = CommonFunction.getScreenPixHeight( this );

		Drawable dTitle = getResources( ).getDrawable( R.drawable.theme_title_gray );
		int titleHeight = dTitle.getMinimumHeight( );
		int mWidth = scrWidth - MARGIN * 2;
		int TitleblankH = abTitle.getHeight( );
		int mHeight = scrHeight  - statusBarHeight - titleHeight - toolBarHeight;
		
		
		
		View vCrop = findViewById( R.id.vCrop );
		LinearLayout.LayoutParams lp = ( LinearLayout.LayoutParams ) vCrop.getLayoutParams( );
		if ( mWidth * outputSize[ 1 ] > outputSize[ 0 ] * mHeight )
		{
			lp.width = outputSize[ 0 ] * mHeight / outputSize[ 1 ];
			lp.height = mHeight;
		}
		else
		{
			lp.width = mWidth;
			lp.height = mWidth * outputSize[ 1 ] / outputSize[ 0 ];
		}
		vCrop.setLayoutParams( lp );
		
//		CommonFunction.log( "shifengxiong","height:"+lp.height +";width:"+lp.width );
//		CommonFunction.log( "shifengxiong","mHeight:"+mHeight );
//		CommonFunction.log( "shifengxiong","getHeight( ):"+vCrop.getHeight( ) );
		
		int h = vCrop.getHeight( )==0?mHeight:vCrop.getHeight( );
		int rectTop =( h - lp.height ) / 2  ;
		
		if(rectTop<=0)
		{
			rectTop = 0;
		}
		
		int rectLeft = MARGIN + ( mWidth - lp.width ) / 2;
		
		int rectRight = rectLeft + lp.width;
		int rectBottom = rectTop + lp.height;
		
//		String rect = "rect:"+"Left="+rectLeft+";Top:"+rectTop+";Right:"+rectRight+";Bottom:"+rectBottom;
//		CommonFunction.log( "shifengxiong",""+rect );
	
		cropRect = new Rect( rectLeft , rectTop , rectRight , rectBottom );
	
	}
	
	//重载getCropRect方法，获取整张图片的Rect 聊天界面
	private void getRect()
	{
		int statusBarHeight = CommonFunction.dipToPx( this , 25 );
		int navigationHeight = CommonFunction.getNavigationBarHeight( this );
		int scrWidth = CommonFunction.getScreenPixWidth( this );
		int scrHeight = CommonFunction.getScreenPixHeight( this );
		int mHeight = scrHeight - navigationHeight - statusBarHeight ;

		int rectLeft = MARGIN;
		int rectTop = 0;
		int rectRight = scrWidth - MARGIN;
		int rectBottom = mHeight;
		
		cropRect = new Rect( rectLeft , rectTop , rectRight , rectBottom );
	}
	
	
	public void onMactrixChage( Matrix matrix )
	{
		float[ ] matrixValue = new float[ 9 ];
		matrix.getValues( matrixValue );
		bitmap = ivPhoto.getBitmap( );
		float picRight = matrixValue[ 2 ] + bitmap.getWidth( ) * matrixValue[ 0 ];
		float picBottom = matrixValue[ 5 ] + bitmap.getHeight( ) * matrixValue[ 4 ];
		float picWidth = picRight - matrixValue[ 2 ];
		float picHeight = picBottom - matrixValue[ 5 ];
		
		// 调整左上偏移
		if ( picRight < cropRect.right )
		{
			matrixValue[ 2 ] = cropRect.right - picRight + matrixValue[ 2 ];
		}
		if ( matrixValue[ 2 ] > cropRect.left )
		{
			matrixValue[ 2 ] = cropRect.left;
		}
		if ( picBottom < cropRect.bottom )
		{
			matrixValue[ 5 ] = cropRect.bottom - picBottom + matrixValue[ 5 ];
		}
		if ( matrixValue[ 5 ] > cropRect.top )
		{
			matrixValue[ 5 ] = cropRect.top;
		}
		
		// 缩放
		if ( picWidth < cropRect.width( ) || picHeight < cropRect.height( ) )
		{
			boolean wLargerH = ( picWidth * cropRect.height( ) ) > ( picHeight * cropRect
					.width( ) );
			if ( wLargerH )
			{ // 以高为标准放大
				matrixValue[ 4 ] = matrixValue[ 4 ] * cropRect.height( ) / picHeight;
				matrixValue[ 0 ] = matrixValue[ 4 ];
			}
			else
			{
				matrixValue[ 0 ] = matrixValue[ 0 ] * cropRect.width( ) / picWidth;
				matrixValue[ 4 ] = matrixValue[ 0 ];
			}
			
			// 调整为居中
			if ( picWidth < cropRect.width( ) )
			{
				picWidth = bitmap.getWidth( ) * matrixValue[ 0 ];
				matrixValue[ 2 ] = cropRect.left + ( cropRect.width( ) - picWidth ) / 2;
			}
			if ( picHeight < cropRect.height( ) )
			{
				picHeight = bitmap.getHeight( ) * matrixValue[ 4 ];
				matrixValue[ 5 ] = cropRect.top + ( cropRect.height( ) - picHeight ) / 2;
			}
		}
		
		matrix.setValues( matrixValue );
	}

	private void onClick(View v)
	{
		long time = System.currentTimeMillis( );
		if ( time - lastClickTime < MIN_ROUND_INTER )
		{
			CommonFunction.log( "System.out" , "============= " + ( time - lastClickTime ) );
			return;
		}
		lastClickTime = time;
		if ( v.getId( ) == R.id.title_left_text )
		{
			setResult( RESULT_CANCELED );
			finish( );
			return;
		}
		else if ( v.getId( ) == R.id.title_right_text )
		{
			if ( procDlg != null && procDlg.isShowing( ) )
			{
				procDlg.dismiss( );
			}
			procDlg = DialogUtil.showProgressDialog( this , R.string.photo_crop_title ,
					R.string.please_wait , null );
			new Thread( )
			{
				public void run( )
				{
					if ( saveBitmap( ) )
					{
						Intent iRes = new Intent( );
						iRes.putExtra( PictureMultiSelectActivity.OUTPUT_PATH , outputPath );
						iRes.putExtra( PictureMultiSelectActivity.ORIGINAL_PATH ,
								returnOriBm ? originalPath : null );
						Message msg = new Message( );
						msg.what = MSG_OK_FINISH;
						msg.obj = iRes;
						handler.sendMessage( msg );
						CommonFunction.log( "picture","sendMessage  ===MSG_OK_FINISH" );
					}
					else
					{
						Message msg = new Message( );
						msg.what = MSG_CANCEL_FINISH;
						handler.sendMessage( msg );
					}
				}
			}.start( );
			return;
		}
		
		switch ( v.getId( ) )
		{
			case R.id.btnLeft :
				ivPhoto.rotateLeft( );
				
				break;
			case R.id.btnOut :
				ivPhoto.zoomIn( );
				break;
			case R.id.btnIn :
				ivPhoto.zoomOut( );
				break;
			case R.id.btnRight :
				ivPhoto.rotateRight( );
				break;
		}
	}
	
	private boolean saveBitmap( )
	{
		try
		{
			System.gc( );
			CommonFunction.log( "picture" ,"cropRect ==height:"+cropRect.height( )+";width:" +cropRect.width( ));
			CommonFunction.log( "picture" ,"outputSize==height:"+outputSize[ 0 ]+";width:" +outputSize[ 1 ]);
			
			Bitmap bmOrig = null;
			if(bIsChatView)
			{
				bmOrig = bitmap;
			}else
			{
				bmOrig = ivPhoto.getCropedBitmap( cropRect );
			}
			
			
			if(bmOrig==null)
			{
				CommonFunction.log( "picture" ,"000000000000000000000000000000000000000");
			}
			
			Bitmap bm = CommonFunction.scalePicture( bmOrig , outputSize[ 0 ] , outputSize[ 1 ] );
			
			bmOrig.recycle( );
			bmOrig = null;
			
			File file = new File( outputPath );
			CommonFunction.log( "picture" ,"保存文件的名 ==============="+outputPath);
			File folder = file.getParentFile( );
			if ( !folder.exists( ) )
			{
				folder.mkdirs( );
			}
			if ( file.exists( ) )
			{
				file.delete( );
			}
			file.createNewFile( );
			
			FileOutputStream fos = new FileOutputStream( file );
			
			BufferedOutputStream bos = new BufferedOutputStream( fos );
			
			bm.compress( CompressFormat.JPEG , 100 , bos );
			
			bos.flush( );
			
			bos.close( );
			
			bm.recycle( );
			
			fos.close( );
			fos = null;
			return true;
		}
		catch ( Throwable e )
		{
			CommonFunction.log( "picture","裁剪后图片文件 ===saveBitmap ======err ==="+e );
		}
		return false;
	}
	
}
