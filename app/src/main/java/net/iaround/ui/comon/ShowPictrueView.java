
package net.iaround.ui.comon;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.picture.ScaledImageView;
import net.iaround.tools.picture.ScaledImageView.OnMatrixChangedListener;
import net.iaround.tools.picture.ScaledImageView.ScaledImageClickListener;
import net.iaround.tools.picture.ScaledImageView.ScaledImageLongClickListener;
import net.iaround.ui.activity.BaseFragmentActivity;

import java.io.File;


/**
 * 小图看大图的通用类，由于Activity和dialog都不能代码加载自定义动画，
 * 所以采用veiw的形式覆盖在activity的根布局，由于onlonglick事件与ontouch事件冲突
 * ，重写了ScaledImageView的长按和短按事件
 */
public class ShowPictrueView implements OnClickListener, ScaledImageClickListener,
		ScaledImageLongClickListener , OnMatrixChangedListener,
		IPictureViewHandler
{
	static ShowPictrueView instance;
	private static final int HANDLE_DOWLOAD_ERR = 0;
	private static final int HANDLE_DOWLOAD_SUCCESS = 1;
	private static final int HANDLE_DOWLOAD_PROGRESS = 2;
	private static final int HANDLE_TOOLBAR = 3;
	private static final int HANDLE_OPEN_END = 4;
	private static final int HANDLE_CLOSE_END = 5;
	private Context context;
	private SuperActivity mActivity;
	private BaseFragmentActivity mFragmentActivity;
	private String urlHD; // HD图片的URL
	private String url640; // 640x640图片的URL
	private String fileName; // 下载的文件名
	private String fileDir; // 下载的文件名
	private String filePath; // 下载文件路径包含文件名
	private int photoFlag;// 下载图片的标记
	private int openType = 1;// 0打开本地图片，1打开网络图片
	private final int animDuration = 300;// 动画播放时间
	private float xFloat;// Scale动画移动的中心点的X轴坐标
	private float yFloat;// Scale动画移动的中心点的Y轴坐标
	private float xPrecent;// 小图占屏幕宽度比例
	private float yPrecent;// 小图占屏幕全屏高度的比例
	private ChatPictureView contentView;// 承载的view
	private View samllImage;
	private View loading;
	private View toolControl;
	private TextView show_picture_bg;
	private TextView show_picture_precent;
	private TextView show_picture_loaded;
	private ScaledImageView ivViewer;
	private Bitmap bmShown;
	private Point screenCenter;
	private Animation animDismiss;
	private Animation animShow;
	private Animation inAnim;// 图片进入动画
	private Animation outAnim;// 图片出动画
	private long lastClickTime = 0;// 记录点击事件最后时间
	private long cancelLastClickTime = 0;// 取消记录点击事件最后时间
	private boolean isFragmentActivity;
	
	private DownloadFileCallback callback = new DownloadFileCallback( )
	{
		
		@Override
		public void onDownloadFileProgress( long lenghtOfFile , long LengthOfDownloaded , int flag )
		{
			if ( flag == photoFlag )
			{
				double dPrecent = ( double ) LengthOfDownloaded / ( double ) lenghtOfFile;
				Message msg = Message.obtain( );
				msg.what = HANDLE_DOWLOAD_PROGRESS;
				msg.obj = dPrecent;
				handler.sendMessage( msg );
			}
		}
		
		@Override
		public void onDownloadFileFinish(int flag , String fileName , String fileDir )
		{
			if ( flag == photoFlag )
			{
				Message msg = Message.obtain( );
				msg.what = HANDLE_DOWLOAD_SUCCESS;
				msg.obj = fileDir;
				handler.sendMessage( msg );
			}
		}
		
		@Override
		public void onDownloadFileError(int flag , String fileName , String fileDir )
		{
			if ( flag == photoFlag )
			{
				handler.sendEmptyMessage( HANDLE_DOWLOAD_ERR );
			}
		}
	};
	
	private Handler handler = new Handler( )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			switch ( msg.what )
			{
				case HANDLE_DOWLOAD_ERR :
				{
					loading.setVisibility( View.GONE );
					File file = new File( fileDir , fileName );
					if ( file.exists( ) )
					{
						file.delete( );
					}
				}
					break;
				case HANDLE_DOWLOAD_PROGRESS :
				{
					double dprecent = (Double) msg.obj;
					int precent = ( int ) ( dprecent * 100 );
					RelativeLayout.LayoutParams param = (LayoutParams) show_picture_loaded
							.getLayoutParams( );
					param.width = ( int ) ( dprecent * show_picture_bg.getWidth( ) );
					show_picture_precent.setText( precent + "%" );
					show_picture_loaded.setLayoutParams( param );
					show_picture_precent.invalidate( );
					show_picture_loaded.invalidate( );
				}
					break;
				case HANDLE_DOWLOAD_SUCCESS :
				{
					try
					{
						// bmShown = CommonFunction.createBitmapFillWindow(
						// filePath , context );
						// bmShown =
						// CommonFunction.createBitmap((String)msg.obj);
						
						//当图片下载完之后显示图片，等比放大或缩小至屏幕宽度
						BitmapFactory.Options options = new BitmapFactory.Options( );
						options.inJustDecodeBounds = false;
						bmShown = BitmapFactory.decodeFile( filePath , options );
						int height = Math.round(  bmShown.getHeight( )
								* CommonFunction.getScreenPixWidth( context ) / bmShown.getWidth( ));
						//设置宽高之后可能会像素丢失
						bmShown = Bitmap.createScaledBitmap( bmShown ,
								CommonFunction.getScreenPixWidth( context ) , height , true );
						
						
						
						ivViewer.setBitmap( bmShown );
						loading.setVisibility( View.GONE );
					}
					catch ( Throwable e )
					{
						loading.setVisibility( View.GONE );
						e.printStackTrace( );
					}
				}
					break;
				case HANDLE_TOOLBAR :
				{
					toolControl.startAnimation( animDismiss );
					toolControl.setVisibility( View.GONE );
				}
					break;
				case HANDLE_OPEN_END :
				{
					contentView.setBackgroundColor( context.getResources( ).getColor(
							R.color.c_0d0f11 ) );
					if ( openType == 1 )
					{// 网络
						loading.setVisibility( View.VISIBLE );
						File file = new File( fileDir , fileName );
						if ( file.exists( ) )
						{
							file.delete( );
						}
						photoFlag++;
						
						new Thread(new Runnable( )
						{
							
							@Override
							public void run( )
							{
								// TODO Auto-generated method stub
								FileDownloadManager manager;
								try
								{
									manager = new FileDownloadManager( context , callback , urlHD , fileName , fileDir , photoFlag );
									manager.run( );
								}
								catch ( ConnectionException e )
								{
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								
							}
						}).start();
					}
				}
					break;
				case HANDLE_CLOSE_END :
				{
					FrameLayout rootLayout = (FrameLayout) contentView.getParent( );
					rootLayout.removeView( contentView );
					CommonFunction.recyledBitmap( bmShown );
//					// 处理网络接口丢失的bug
					if(isFragmentActivity)
					{
//						ConnectorManage.getInstance( context ).setCallBackAction( mFragmentActivity );
					}
					else
					{
//						ConnectorManage.getInstance( context ).setCallBackAction( mActivity );
					}
				}
					break;
			}
		}
		
	};
	
	public static void ShowHDPictureView(Context context , View samllImage , String urlHD , String url640 , boolean isFragment)
	{
		if(instance==null)
		{
			instance = new ShowPictrueView(  context ,  samllImage ,  urlHD ,  url640 , isFragment) ;
		}
		else
		{
			instance.context = context;
			instance.urlHD = urlHD;
			instance.url640 = url640;
			instance.isFragmentActivity = isFragment;
			if(instance.isFragmentActivity)
			{
				instance.mFragmentActivity = (BaseFragmentActivity ) context;
			}
			else
			{
				instance.mActivity = ( SuperActivity ) context;
			}
			instance.init( );
		}
	}
	
	public ShowPictrueView(Context context , View samllImage , String urlHD , String url640 , boolean isFragment)
	{
		super( );
		this.context = context;
		this.urlHD = urlHD;
		this.url640 = url640;
		this.samllImage = samllImage;
//		this.mActivity = ( SuperActivity ) context;
		isFragmentActivity = isFragment;
		if(isFragmentActivity)
		{
			this.mFragmentActivity = (BaseFragmentActivity ) context;
		}
		else
		{
			this.mActivity = ( SuperActivity ) context;
		}
		
		init( );
	}
	
	public ShowPictrueView(Context context , View samllImage , String urlHD , String url640 )
	{
		super( );
		this.context = context;
		this.urlHD = urlHD;
		this.url640 = url640;
		this.samllImage = samllImage;
		this.mActivity = ( SuperActivity ) context;
		
		init( );
	}
	
	/** 初始化操作 */
	private void init( )
	{
		
		long lastShowTime = SharedPreferenceUtil.getInstance( context ).getLong(
				SharedPreferenceUtil.SHOW_BIG_PHOTO_TIME );
		if ( Math.abs( System.currentTimeMillis( ) - lastShowTime ) < 1000 )
		{
			return;
		}
		SharedPreferenceUtil.getInstance( context ).putLong(
				SharedPreferenceUtil.SHOW_BIG_PHOTO_TIME , System.currentTimeMillis( ) );
		if ( CommonFunction.isEmptyOrNullStr( urlHD ) )
		{ // 没有图片URL，就返回
			CommonFunction.showToast( context ,
					context.getResources( ).getString( R.string.chat_picture_no_exist ) , 0 );
			return;
		}
		initData( ); // 初始化数据
		initUi( ); // 初始化ui
		intiAnimation( );// 初始化动画
		loadImage( ); // 加载图片
		imageOpen( );// 打开动画播放
	}
	
	/** 初始化数据 */
	private void initData( )
	{
		int position[] = new int[ 2 ];
		samllImage.getLocationOnScreen( position );
		int imageWidth = samllImage.getWidth( );
		int imageHeigth = samllImage.getHeight( );
		int statusBarHeigth = CommonFunction.getStatusBarHeight( context );
		int screenWidth = CommonFunction.getScreenPixWidth( context );
		int screenHeigth = CommonFunction.getScreenPixHeight( context );
		screenCenter = new Point( screenWidth / 2 , screenHeigth / 2 );
		xPrecent = samllImage.getWidth( ) / ( float ) screenWidth;
		yPrecent = samllImage.getHeight( ) / ( float ) screenHeigth * screenHeigth / 640;
		xFloat = position[ 0 ] / ( float ) ( screenWidth - imageWidth );
		yFloat = ( position[ 1 ] - statusBarHeigth )
				/ ( float ) ( screenHeigth - statusBarHeigth - imageHeigth );
	}
	
	
	private void initUi( )
	{
		contentView = ( ChatPictureView ) LayoutInflater.from( context ).inflate(
				R.layout.show_picture , null );
		contentView.setFocusable( true );
		contentView.requestFocus( );
		contentView.setPicrureViewHandler( this );
		ivViewer = ( ScaledImageView ) contentView.findViewById( R.id.imageView );
		toolControl = contentView.findViewById( R.id.show_picture_filter );
		loading = contentView.findViewById( R.id.loading );
		show_picture_bg = (TextView) contentView.findViewById( R.id.show_picture_bg );
		show_picture_precent = (TextView) contentView.findViewById( R.id.show_picture_precent );
		show_picture_loaded = (TextView) contentView.findViewById( R.id.show_picture_loaded );
		contentView.findViewById( R.id.show_picture_save ).setOnClickListener( this );
		contentView.findViewById( R.id.show_picture_clane ).setOnClickListener( this );
		ivViewer.setScaledImageLongClickListener( this );
		ivViewer.setScaledImageClickListener( this );
		ivViewer.setOnMatrixChangedListener( this );
		toolControl.setVisibility( View.GONE );
	}
	
	public void intiAnimation( )
	{
		animDismiss = AnimationUtils.loadAnimation( context , R.anim.picture_filter_dismiss );
		animShow = AnimationUtils.loadAnimation( context , R.anim.picture_filter_show );
		inAnim = new ScaleAnimation( xPrecent , 1 , yPrecent , 1 , Animation.RELATIVE_TO_PARENT ,
				xFloat , Animation.RELATIVE_TO_PARENT , yFloat );
		inAnim.setDuration( animDuration );
//		outAnim = new ScaleAnimation( 1 , xPrecent , 1 , yPrecent , Animation.RELATIVE_TO_PARENT ,
//				xFloat , Animation.RELATIVE_TO_PARENT , yFloat );
		outAnim = new AlphaAnimation( 1f , 0.0f ) ;
		outAnim.setDuration( 1 );
		inAnim.setAnimationListener( new AnimationListener( )
		{
			@Override
			public void onAnimationStart( Animation animation )
			{
			}
			
			@Override
			public void onAnimationRepeat( Animation animation )
			{
				
			}
			
			@Override
			public void onAnimationEnd( Animation animation )
			{
				handler.sendEmptyMessageDelayed( HANDLE_OPEN_END , 100 );
			}
		} );
		outAnim.setAnimationListener( new AnimationListener( )
		{
			
			@Override
			public void onAnimationStart( Animation animation )
			{
//				contentView.setBackgroundColor( context.getResources( ).getColor(
//						R.color.transparent ) );
			}
			
			@Override
			public void onAnimationRepeat( Animation animation )
			{
			}
			
			@Override
			public void onAnimationEnd( Animation animation )
			{
				contentView.setVisibility( View.GONE );
				handler.sendEmptyMessageDelayed( HANDLE_CLOSE_END , 100 );
			}
		} );
	}

	
	/** 加载图片 */
	private void loadImage( )
	{
//		mConnector = ConnectorManage.getInstance( context );
//		mConnector.setCallBackAction( this );
		fileDir = PathUtil.getImageLoaderDir( );
		fileName = CryptoUtil.SHA1( urlHD );
		File file = new File( fileDir , fileName );
		filePath = file.getAbsolutePath( );
		if ( file.exists( ) && file.length( ) > 5120 )
		{// 判断如果小于5K就判断该文件不存在，重新下载
			try
			{
//				bmShown = CommonFunction.createBitmapFillWindow( filePath , context );
				// bmShown = CommonFunction.createBitmap(filePath);
				
				//显示图片，等比放大或缩小至屏幕宽度
				CommonFunction.log( "fan" ,"ShowPictrueView===========================");
				BitmapFactory.Options options = new BitmapFactory.Options( );
				options.inJustDecodeBounds = false;
				bmShown = BitmapFactory.decodeFile( filePath , options );
				int height = Math.round(  bmShown.getHeight( )
						* CommonFunction.getScreenPixWidth( context ) / bmShown.getWidth( ));
				//设置宽高之后可能会像素丢失
				bmShown = Bitmap.createScaledBitmap( bmShown ,
						CommonFunction.getScreenPixWidth( context ) , height , true );
				
				if ( bmShown != null )
				{
					openType = 0;
				}
				ivViewer.setBitmap( bmShown );
				contentView.setBackgroundColor( context.getResources( ).getColor(
						R.color.transparent ) );
			}
			catch ( Throwable e )
			{
				CommonFunction.log( "fan" ,"ShowPictrueView================Throwable ====" +e);
				e.printStackTrace( );
			}
		}
		else
		{
			openType = 1;
			CommonFunction.log( "fan" ,"ShowPictrueView===========================该文件不存在，重新下载");
			
		}
	}
	
	/** 图片打开 */
	private void imageOpen( )
	{
		contentView.startAnimation( inAnim );
		if(isFragmentActivity)
		{
			mFragmentActivity.addContentView( contentView , new FrameLayout.LayoutParams( -1 , -1 ) );
		}
		else
		{
			mActivity.addContentView( contentView , new FrameLayout.LayoutParams( -1 , -1 ) );
		}
		contentView.setFocusable( true );
		contentView.requestFocus( );
	}
	
	/** 图片关闭 */
	private void imageClose( )
	{
		loading.setVisibility( View.GONE );
		contentView.startAnimation( outAnim );
	}
	
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.show_picture_save :
			{
				save( );// 保存
			}
				break;
			case R.id.show_picture_clane :
			{
				if ( System.currentTimeMillis( ) - cancelLastClickTime < 2000 )
				{
					return;
				}
				cancelLastClickTime = System.currentTimeMillis( );
				toolControl.startAnimation( animDismiss );
				toolControl.setVisibility( View.GONE );
				imageClose( );// 关闭
			}
				break;
		}
	}
	
	@Override
	public void onScaledLongClick( View v )
	{
		if ( toolControl.getVisibility( ) != View.VISIBLE )
		{
			toolControl.startAnimation( animShow );
			toolControl.setVisibility( View.VISIBLE );
			handler.removeMessages( HANDLE_TOOLBAR );
			handler.sendEmptyMessageDelayed( HANDLE_TOOLBAR , 2500 );
			// 震动
			Vibrator mVibrator = (Vibrator) context.getSystemService( Context.VIBRATOR_SERVICE );
			long[ ] pattern =
				{ 0 , 80 }; // 停止 开启 停止 开启时长
			mVibrator.vibrate( pattern , -1 ); // 重复两次上面的pattern
												// 如果只想震动一次，index设为-1
		}
	}
	
	@Override
	public void onScaledClick( View v )
	{
		if ( System.currentTimeMillis( ) - lastClickTime < 1000 )
		{
			return;
		}
		lastClickTime = System.currentTimeMillis( );
		File file = new File( filePath );
		if ( file.exists( ) && file.length( ) < 1024 )
		{
			file.delete( );
		}
		if ( toolControl.getVisibility( ) == View.VISIBLE )
		{
			toolControl.startAnimation( animDismiss );
			toolControl.setVisibility( View.GONE );
		}
		imageClose( );// 关闭
	}
	
	/** 保存（其实是移动文件） */
	private void save( )
	{
		try
		{
			File hdFile = new File( filePath );
			String savedPath = Environment.getExternalStorageDirectory( ) + "/DCIM/iAround/"
					+ fileName + ".jpg";
			File savedFile = new File( savedPath );
			if ( !savedFile.getParentFile( ).exists( ) )
			{
				savedFile.getParentFile( ).mkdirs( );
			}
			if ( savedFile.exists( ) )
			{
				String message = context.getResources( ).getString( R.string.save_file_path )
						+ savedPath;
				CommonFunction.showToast( context , message , Toast.LENGTH_SHORT );
				return;
			}
			
			hdFile.renameTo( savedFile );
			if ( savedFile.exists( ) )
			{
				String message = context.getResources( ).getString( R.string.save_file_path )
						+ savedPath;
				CommonFunction.showToast( context , message , Toast.LENGTH_SHORT );
			}
			else
			{
				CommonFunction.showToast( context ,
						context.getResources( ).getString( R.string.save_error ) , 0 );
			}
		}
		catch ( Throwable e )
		{
			CommonFunction.log( e );
			CommonFunction
					.showToast( context , context.getResources( ).getString( R.string.save_error ) , 0 );
		}
	}
	
	public void onMactrixChage( Matrix matrix )
	{
		if ( bmShown == null )
		{
			return;
		}
		
		float[ ] matrixValue = new float[ 9 ];
		matrix.getValues( matrixValue );
		float picWidth = bmShown.getWidth( ) * matrixValue[ 0 ];
		float picHeight = bmShown.getHeight( ) * matrixValue[ 4 ];
		float picRight = matrixValue[ 2 ] + picWidth;
		float picBottom = matrixValue[ 5 ] + picHeight;
		int scrWidth = screenCenter.x * 2;
		int scrHeight = screenCenter.y * 2;
		
		// 保持不离开屏幕
		if ( matrixValue[ 2 ] > 0 )
		{
			matrixValue[ 2 ] = 0;
			picRight = picWidth;
		}
		if ( matrixValue[ 5 ] > 0 )
		{
			matrixValue[ 5 ] = 0;
			picBottom = picHeight;
		}
		if ( picRight < scrWidth )
		{
			picRight = scrWidth;
			matrixValue[ 2 ] = picRight - picWidth;
		}
		if ( picBottom < scrHeight )
		{
			picBottom = scrHeight;
			matrixValue[ 5 ] = picBottom - picHeight;
		}
		
		// 居中处理
		if ( picWidth < scrWidth )
		{
			matrixValue[ 2 ] = screenCenter.x - picWidth / 2;
		}
		if ( picHeight < scrHeight )
		{
			matrixValue[ 5 ] = screenCenter.y - picHeight / 2;
		}
		
		matrix.setValues( matrixValue );
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.iaround.ui.common.IPictureViewHandler#close()
	 */
	@Override
	public void close( )
	{
		if ( contentView.getVisibility( ) == View.VISIBLE )
		{
			imageClose( );
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see net.iaround.ui.common.IPictureViewHandler#isPictureShow()
	 */
	@Override
	public boolean isPictureShow( )
	{
        return contentView.getVisibility() == View.VISIBLE;
    }

}
