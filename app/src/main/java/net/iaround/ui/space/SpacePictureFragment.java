
package net.iaround.ui.space;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.Blog;
import net.iaround.ui.datamodel.FocusModel;
import net.iaround.ui.group.activity.SpaceMe;
import net.iaround.ui.interfaces.SpacePicFragmentInterface;
import net.iaround.ui.view.SpaceTopicView;
import net.iaround.ui.view.menu.CustomContextMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Map;


public class SpacePictureFragment extends Fragment implements HttpCallBack, OnClickListener,
		PagerImageView.OnMatrixChangedListener, PagerImageView.ScaledImageClickListener

{
	
	private static final int MSG_REPORT = 11;
	
	
	public Blog blog; // 当前图片
	private long fleshTime = 0; // 刷新数据，避免连续点击
	private Activity mActivity;
	
	
	
	private long delblogflag;
	private long sendFlag;
	private int loadImageFlag; // 下载图片
	private final int INIT_DATA = 1000;
	private final int SUCCESS = 1001;
	private final int ERROR = 1002;
	private final int INIT = 1003;
	
	private final int DEL_BLOG_SUCCESS = 1004;
	private final int SHOW_BITMAP = 1005;
	
	private final int SAVE_SUCCESS = 1007;
	public final int FLESH_VIEW = 2002;
	
	private final int HANDLE_TOOLBAR = 4001;
	
	private View view;
	private View toolControl;
	
	private int retryCount = 0;
	PagerImageView image;
	
	Bitmap bmShown;
	float  maxScale = 3;
	private SpacePicFragmentInterface mainView;
	
	private long eroFlag;
	
	protected Context mContext;
	
	public LayoutInflater mInflater;
	
	public String photoId;
	public String smallPhoto;
	public int initNum;
	
	private String photoString;
	private boolean isImageFileExist;
	
	
//	public NewThirdPartySharePage tPage;
	
	private String filePath;
	private ProgressBar mProgressBar;
	private String fileName;
	private boolean isLimit = false;
	
	private void hideProgressBar( )
	{
		if ( mProgressBar != null )
		{
			mProgressBar.setVisibility( View.GONE );
		}
	}
	
	public void setProgressBarVisible( boolean isvisible )
	{
		try
		{
			if ( isvisible && mProgressBar == null )
			{
				// 滚动条
				mProgressBar = new ProgressBar( mContext );
				int dp_24 = ( int ) ( getResources( ).getDimension( R.dimen.dp_1 ) * 24 );
				RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams( dp_24 , dp_24 );
				lpPb.addRule( RelativeLayout.CENTER_IN_PARENT , RelativeLayout.TRUE );
				mProgressBar.setIndeterminate( true );
				mProgressBar.setIndeterminateDrawable( getResources( ).getDrawable(
						R.drawable.pull_ref_pb ) );
				mProgressBar.setLayoutParams( lpPb );
				( (ViewGroup) view ).addView( mProgressBar );
			}
			
			if ( mProgressBar != null )
			{
				mProgressBar.setVisibility( isvisible ? View.VISIBLE : View.GONE );
			}
		}
		catch ( Exception e )
		{
			// TODO: handle exception
			e.printStackTrace( );
		}
		
	
	}
	
	private DownloadFileCallback callback = new DownloadFileCallback( )
	{
		
		@Override
		public void onDownloadFileProgress( long lenghtOfFile , long LengthOfDownloaded ,
				int flag )
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onDownloadFileFinish(int flag , String fileName , String savePath )
		{
			// TODO Auto-generated method stub
			if ( loadImageFlag == flag )
			{
				
				mHandler.sendEmptyMessage( SUCCESS );
				
			}
		}
		
		@Override
		public void onDownloadFileError(int flag , String fileName , String savePath )
		{
			// TODO Auto-generated method stub
			
			mHandler.sendEmptyMessage( ERROR );
		}
	};
	
	@Override
	public void onAttach( Activity activity )
	{
		// TODO Auto-generated method stub
		reSet( );
		mContext = activity;
		mActivity = activity;
		mainView = ( SpacePicFragmentInterface ) activity;
		
		Bundle bundle = getArguments( );
		if ( bundle != null )
		{
			
			int upImageId = bundle.getInt( "position" );
			photoId = bundle.getString( "photoId" );
			smallPhoto = bundle.getString( "smallPhoto" );
			isLimit = bundle.getBoolean( "limit" , false );
			initNum = upImageId;
			
			
			if ( !CommonFunction.isEmptyOrNullStr( smallPhoto ) )
			{
				
				photoString = CommonFunction.sourcePicture( smallPhoto );
			}


            isImageFileExist = PathUtil.isExistMD5UserCacheFile(photoString);
		}
		
		int screenWidth = CommonFunction.getScreenPixWidth( mContext );
		int screenHeigth = CommonFunction.getScreenPixHeight( mContext );
		new Point( screenWidth / 2 , screenHeigth / 2 );
		
		CommonFunction.log( "SpacePictureFragment" , "super.onAttach( activity ):" + initNum );
		super.onAttach( activity );
	}
	
	
	
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		// TODO Auto-generated method stub
		super.onCreate( savedInstanceState );
		
	}
	
	@Override
	public void onDestroy( )
	{
		// TODO Auto-generated method stub
		
		super.onDestroy( );
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater , ViewGroup container ,
							 Bundle savedInstanceState )
	{
		
		
		view = inflater.inflate( R.layout.space_show_picture , container , false );
		
		image = ( PagerImageView ) view.findViewById( R.id.imageView );
		image.setVisibility( View.VISIBLE );
		
		BitmapFactory.Options options = new BitmapFactory.Options( );
		options.inJustDecodeBounds = false;
		
		if (smallPhoto!=null&& smallPhoto.contains( PathUtil.getHTTPPrefix( ) ) )
		{
			filePath = PathUtil.getExistMD5UserCacheFile( smallPhoto );
		}
		else
		{
			filePath = smallPhoto;
			isImageFileExist = true;
		}
		
		fileName = CryptoUtil.generate( photoString );
		
		bmShown = BitmapFactory.decodeFile( filePath , options );
	
		if(isLimit)
		{
			
			image.setImageResource( R.drawable.default_picture_big );
			
		}
		else
		{
			if ( bmShown != null )
			{
				int height = Math.round( bmShown.getHeight( )
						* CommonFunction.getScreenPixWidth( mContext ) / bmShown.getWidth( ) );
				bmShown = Bitmap.createScaledBitmap( bmShown ,
						CommonFunction.getScreenPixWidth( mContext ) , height , true );
				center( true , true );
				
				image.setBitmap( bmShown );
			}
			else
			{
				image.setImageResource( R.drawable.default_picture_big );
			}
		}
		//开始 加载图片
		mHandler.sendEmptyMessageDelayed( INIT , 200 );
		return view;
		
	}
	
	private void showImage( )
	{
		 loadImage( );
	}
	
	private void initView( )
	{
		
		loadImage( );
		
//		center( true , true );
		
		toolControl = view.findViewById( R.id.show_picture_filter );
		view.findViewById( R.id.show_picture_save ).setOnClickListener( this );
		view.findViewById( R.id.show_picture_clane ).setOnClickListener( this );
		view.findViewById( R.id.iv_del_picture ).setOnClickListener( this );
		view.findViewById( R.id.iv_report_picture ).setOnClickListener( this );

		//去掉保存图片
//		image.setOnLongClickListener( ScaledLongClick );
		
		
		image.setScaledImageClickListener( new PagerImageView.ScaledImageClickListener( )
		{
			
			@Override
			public void onScaledClick( View v )
			{
				// TODO Auto-generated method stub
				mActivity.finish( );
			}
		} );
		
		image.setLongClickable( true );
		
		image.setClickable( true );
		
	}
	
	private void loadImage( )
	{
	
		if ( CommonFunction.isEmptyOrNullStr( photoString ) )
		{
			return;
		}
		if ( PathUtil.isExistMD5UserCacheFile( photoString )
				|| !smallPhoto.contains( PathUtil.getHTTPPrefix( ) ) )
		{
			
			if ( !photoString.contains( PathUtil.getHTTPPrefix( ) ) )
			{
				return;
			}
			
			new Thread(new Runnable( )
			{
				
				@Override
				public void run( )
				{
					// TODO Auto-generated method stub
					String filePath = PathUtil.getExistMD5UserCacheFile( photoString );
					try
					{
						CommonFunction.log( "SpacePictureFragment" , "本地文件存在 ============" );
//						bmShown = BitmapFactory.decodeFile( filePath , options );
						byte[] bitData = CommonFunction.decodeBitmap( filePath ,mContext);
						Bitmap bmtemp = null;
						if(null!=bitData) {
							bmtemp = BitmapFactory.decodeByteArray(bitData, 0, bitData.length);
						}else{
							CommonFunction.log("SpacePictureFragment","decode bitmap fail!");
						}
						if ( !isLimit && bmtemp != null )
						{
							int height = Math.round( bmtemp.getHeight( )
									* CommonFunction.getScreenPixWidth( mContext )
									/ bmtemp.getWidth( ) );
							
							if(bmtemp.getHeight( ) >bmtemp.getWidth( ) )
							{
								maxScale =  ((bmtemp.getHeight( )
										* CommonFunction.getScreenPixWidth( mContext )+0.1f)
										/ (bmtemp.getWidth( )* CommonFunction.getScreenPixHeight( mContext )) );
								
								CommonFunction.log( "SpacePictureFragment" ,"maxScale ====="+maxScale);
							}
							else
							{
								maxScale =  Math.round((bmtemp.getWidth( )
										* CommonFunction.getScreenPixHeight( mContext )+0.1f)
										/ (bmtemp.getHeight( )* CommonFunction.getScreenPixWidth( mContext )) );
								
								CommonFunction.log( "SpacePictureFragment" ,"maxScale ====="+maxScale);
							}
							
//							bmShown = Bitmap.createScaledBitmap( bmShown ,
//									CommonFunction.getScreenPixWidth( mContext ) , height , true );
							
							CommonFunction.log( "SpacePictureFragment" , "本地文件存在 ============setBitmap" );
							bmShown = bmtemp;
							
							mHandler.sendEmptyMessage( SHOW_BITMAP );
						}
						else if(bmShown == null )
						{
//							image.setImageResource( R.drawable.default_picture_big );
							File file = new File( filePath);
							if(file.exists( ))
							{
								file.delete( );
								loadImage( );
							}
							
							BitmapFactory.Options options = new BitmapFactory.Options( );
							options.inJustDecodeBounds = false;
							bmShown = BitmapFactory.decodeFile( filePath , options );
							
							if ( bmShown != null )
							{
								int height = Math.round( bmShown.getHeight( ) * CommonFunction.getScreenPixWidth( mContext ) / bmShown.getWidth( ) );
								bmShown = Bitmap.createScaledBitmap( bmShown , CommonFunction.getScreenPixWidth( mContext ) , height , true );
								center( true , true );
								
								//image.setBitmap( bmShown );
								mHandler.sendEmptyMessage( SHOW_BITMAP );
							}
						}
						else 
						{
							mHandler.sendEmptyMessage( SHOW_BITMAP );
							
						}
					}
					catch ( OutOfMemoryError err  )
					{
						err.printStackTrace( );
						// TODO: handle exception
					}
					
				}
			} ).start( );
			
			
			
		}
		else
		{
			
			setProgressBarVisible( true );
			new Thread(new Runnable( )
			{
				
				@Override
				public void run( )
				{
					// TODO Auto-generated method stub
					FileDownloadManager manager;
					try
					{
						// String fileName = CryptoUtil.generate( photoString )
						// ;
						
						String pathDir = PathUtil.getImageLoaderDir( );
						fileName = CryptoUtil.generate( photoString );
						CommonFunction.log( "SpacePictureFragment" ,"下载 图片 num="+initNum +":"+fileName + ":" + photoString );
						
						String layer1 = fileName.substring( 0 , 2 );
						String layer2 = fileName.substring( 2 , 4 );
						String parentDir = pathDir + layer1 + "/" + layer2 + "/";
						
						loadImageFlag = initNum;
						
						File dir = new File( parentDir );
						if ( !dir.exists( ) )
						{
							dir.mkdirs( );
						}
						
						manager = new FileDownloadManager( mContext , callback , photoString , fileName , parentDir , loadImageFlag );
						manager.run( );
						
						
					}
					catch ( ConnectionException e )
					{
						// TODO Auto-generated catch block
						e.printStackTrace( );
					}
					
				}
			} ).start( );
			
			
			
		}
	}
	
	public void InitData( String result )
	{
		if ( CommonFunction.isEmptyOrNullStr( result ) )
		{
			return;
		}
		Map< String , Object > map = FocusModel.getInstance( ).onPhotoDetail( result );
		if ( map.containsKey( "status" ) && (Integer) map.get( "status" ) == 200 )
		{
			
			if ( map.containsKey( "blog" ) )
			{
				blog = (Blog) map.get( "blog" );
				
			}
		}
		else
		{
			if ( map.containsKey( "error" ) )
			{
				ErrorCode.showError( mContext , result );
			}
		}
	}
	
	public void reFlashData(int position , String photoid , String small )
	{
		initNum = position;
		photoId = photoid;
		smallPhoto = small;
		sendFlag = 0;
		isLimit = false;
		view.invalidate( );
	}
	
	
	
	public void init( )
	{
		showImage( );
	}
	
	
	
	// 刷新数据
	public void fleshData( )
	{
		try
		{
			// 限制连续点击刷洗
			if ( System.currentTimeMillis( ) - fleshTime > 2000 )
			{
				fleshTime = System.currentTimeMillis( );
				
				CommonFunction.log( "SpacePictureFragment" , initNum + "=======PhotoHttpProtocol photoGet ======" + photoId );
				if ( sendFlag == 0 )
				{
					sendFlag = PhotoHttpProtocol.photoGet( mContext , photoId , this );
				}
			}
			
			// mainView.showDialog( null , mainView.getResources( ).getString(
			// R.string.please_wait ) );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			// mainView.dissmissDialog( );
			Toast.makeText( mContext , R.string.network_req_failed , Toast.LENGTH_SHORT ).show( );
		}
	}
	
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		
		mHandler.sendEmptyMessage( ERROR );
		
		
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		
		
		if ( delblogflag == flag )
		{ // 删除相片
			Message msg = new Message( );
			msg.what = DEL_BLOG_SUCCESS;
			msg.obj = result;
			mHandler.sendMessage( msg );
		}
		else if ( eroFlag == flag )
		{
			Message msge = new Message( );
			msge.what = MSG_REPORT;
			msge.obj = result;
			mHandler.sendMessage( msge );
		}
	}
	
	
	//
	// /** 评论列表 **/
	class HoldItem
	{
		public NetImageView icon;
		public TextView nickname;
		public TextView datetime;
		public TextView location;
		public TextView description;
		public LinearLayout layout;
	}
	
	private Handler mHandler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{

			switch ( msg.what )
			{
				case INIT :

					initView( );
					break;

				case INIT_DATA :

					InitData( String.valueOf( msg.obj ) );
					break;
				case SHOW_BITMAP :
					if( !isLimit && bmShown != null )
					{
						image.setBitmap( bmShown );
						maxScale = maxScale>3?maxScale:3;
						image.setMaxScale( maxScale );
					}
					else
					{
						image.setImageResource( R.drawable.default_picture_big );
					}
					break;
				case SUCCESS :
					isImageFileExist = true;
					loadImage( );
					hideProgressBar( );
					break;
				case ERROR :
					//五次下载失败，提示网络连接失败
					retryCount ++;
					if(retryCount < 5)
					{
						mHandler.postDelayed( new Runnable( )
						{

							@Override
							public void run( )
							{
								// TODO Auto-generated method stub
								loadImage( );
							}
						} , 200 );
					}
					else
					{
						hideProgressBar( );

					}


					break;

				case DEL_BLOG_SUCCESS : // 删除相片

					delHandler( String.valueOf( msg.obj ) );
					mainView.setIsDeletePhoto( true );
					break;


				case SAVE_SUCCESS :
					CommonFunction.showToast( mContext ,
							mContext.getString( R.string.save_success ) , 0 );
					break;
				case HANDLE_TOOLBAR :
					toolControl.setVisibility( View.GONE );
					break;
				case MSG_REPORT :


					try
					{
						JSONObject rJson = new JSONObject( String.valueOf( msg.obj ) );
						if ( rJson.optInt( "status" ) == 200 )
						{
							Toast.makeText( mContext , R.string.report_return_content_photo ,
									Toast.LENGTH_LONG ).show( );

							// 保存为已举报
							long uid = Common.getInstance( ).loginUser.getUid( );
							SharedPreferenceUtil.getInstance( mContext ).putBoolean(
									uid + photoId + "erotic" , true );
							SharedPreferenceUtil.getInstance( mContext ).putLong(
									uid + photoId + "erotic" + "time" ,
									System.currentTimeMillis( ) + ( 24 * 60 * 60 * 1000 ) );

							return;
						}
						else
						{
							try
							{
								int error = rJson.getInt( "error" );
								if ( error == 5404 )
								{
									// 保存为已举报
									long uid = Common.getInstance( ).loginUser.getUid( );
									SharedPreferenceUtil.getInstance( mContext ).putBoolean(
											uid + photoId + "erotic" , true );
									SharedPreferenceUtil.getInstance( mContext ).putLong(
											uid + photoId + "erotic" + "time" ,
											System.currentTimeMillis( )
													+ ( 24 * 60 * 60 * 1000 ) );
								}
							}
							catch ( JSONException e )
							{
								e.printStackTrace( );
							}
						}
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					ErrorCode.showError( mContext , String.valueOf( msg.obj ) );

					break;

			}

		}
	};

	//



	private void reSet( )
	{
		blog = null;
		sendFlag = 0;
		photoString = "";
	}

	@Override
	public void onResume( )
	{
		super.onResume( );


	}

	public void onMactrixChage( Matrix matrix )
	{

	}


	/**
	 * 横向、纵向居中
	 */
	protected void center( boolean horizontal , boolean vertical )
	{
		if(isLimit)
		{
			return;
		}
		if ( bmShown == null )
			return;

		Matrix m = new Matrix( );
		m.set( image.getImageMatrix( ) );
		RectF rect = new RectF( 0 , 0 , bmShown.getWidth( ) , bmShown.getHeight( ) );
		m.mapRect( rect );

		float height = rect.height( );
		float width = rect.width( );

		float deltaX = 0 , deltaY = 0;

		if ( vertical )
		{
			// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下方留空则往下移
			int screenHeight = CommonFunction.getScreenPixHeight( mContext );
			if ( height < screenHeight )
			{
				deltaY = ( screenHeight - height ) / 2 - rect.top;
			}
			else if ( rect.top > 0 )
			{
				deltaY = -rect.top;
			}
			else if ( rect.bottom < screenHeight )
			{
				deltaY = image.getHeight( ) - rect.bottom;
			}
		}

		if ( horizontal )
		{
			int screenWidth = CommonFunction.getScreenPixWidth( mContext );
			if ( width < screenWidth )
			{
				deltaX = ( screenWidth - width ) / 2 - rect.left;
			}
			else if ( rect.left > 0 )
			{
				deltaX = -rect.left;
			}
			else if ( rect.right < screenWidth )
			{
				deltaX = screenWidth - rect.right;
			}
		}
		image.getImageMatrix( ).postTranslate( deltaX , deltaY );
	}


	private void delPhoto( )
	{
		delblogflag = PhotoHttpProtocol.photoDel( mContext , photoId , this );
	}

	// /** 删除照片处理 */
	private void delHandler( String delResult )
	{
		if ( CommonFunction.isEmptyOrNullStr( delResult ) )
		{
			return;
		}
		Map< String , Object > delBlogMap = FocusModel.getInstance( ).onDelBlog( delResult );
		if ( delBlogMap.containsKey( "status" )
				&& (Integer) delBlogMap.get( "status" ) == 200 )
		{

			mActivity.runOnUiThread( new Runnable( )
			{

				@Override
				public void run( )
				{
					CommonFunction.toastMsg( mContext , R.string.delete_succuss );
				}
			} );

			int totalLeftPhotos = Common.getInstance( ).loginUser.getPhotouploadleft( );
			Common.getInstance( ).loginUser.setPhotouploadleft( totalLeftPhotos + 1 );
			SpaceMe.performRefresh( false );
			// Intent intentDel = new Intent( );
			// mainView.setResult( SpaceTopicView.RES_SPACE_DELETE , intentDel
			// );
			// mainView.finish( );
		}
		else
		{
			if ( delBlogMap.containsKey( "error" ) )
			{
				ErrorCode.showError( mContext , delResult );
			}
		}
	}


	@Override
	public void onClick( View v )
	{
		// TODO Auto-generated method stub

		switch ( v.getId( ) )
		{

			case R.id.show_picture_save :
			{
				save( );// 保存
			}
				break;
			case R.id.show_picture_clane :
			{

				toolControl.setVisibility( View.GONE );

			}
				break;
			case R.id.iv_del_picture :
			{
				DialogUtil.showOKCancelDialog( mContext , R.string.prompt ,
						R.string.tips_del_photo , new OnClickListener( )
						{

							@Override
							public void onClick( View v )
							{
								// TODO Auto-generated method stub
								mainView.delPhoto( initNum );
								delPhoto( );
								// 删除照片
								mActivity.setResult( Activity.RESULT_OK );
							}
						} );
			}
				break;
			case R.id.iv_report_picture :

				popReportDialog( );
				break;
		}
	}

	android.view.View.OnLongClickListener ScaledLongClick = new OnLongClickListener( )
	{

		@Override
		public boolean onLongClick( View v )
		{
			// TODO Auto-generated method stub

			if ( !isImageFileExist )
			{
				return false;
			}
			if ( toolControl.getVisibility( ) != View.VISIBLE )
			{

				toolControl.setVisibility( View.VISIBLE );

				if ( Common.getInstance( ).loginUser.getUid( ) == mainView.getPhotoUid( ) )
				{
					toolControl.findViewById( R.id.show_picture_del ).setVisibility(
							View.VISIBLE );
					toolControl.findViewById( R.id.show_picture_report ).setVisibility(
							View.GONE );
				}
				else
				{
					toolControl.findViewById( R.id.show_picture_del )
							.setVisibility( View.GONE );
					toolControl.findViewById( R.id.show_picture_report ).setVisibility(
							View.VISIBLE );
				}
				if ( mainView.getPhotoUid( ) == 0 )
				{
					toolControl.findViewById( R.id.show_picture_report ).setVisibility(
							View.GONE );
				}
				mHandler.removeMessages( HANDLE_TOOLBAR );
				mHandler.sendEmptyMessageDelayed( HANDLE_TOOLBAR , 2500 );
				// 震动
				Vibrator mVibrator = (Vibrator) mActivity
						.getSystemService( Context.VIBRATOR_SERVICE );
				long[ ] pattern =
					{ 0 , 80 }; // 停止 开启 停止 开启时长
				mVibrator.vibrate( pattern , -1 ); // 重复两次上面的pattern
													// 如果只想震动一次，index设为-1
			}
			return false;
		}
	};


	/** 保存（其实是移动文件） */
	private void save( )
	{
		try
		{


			String pathDir = PathUtil.getExistMD5UserCacheFile( photoString );

			if ( !photoString.contains( PathUtil.getHTTPPrefix( ) ) )
			{
				pathDir = photoString;
			}

			File hdFile = new File( pathDir );



			String savedPath = Environment.getExternalStorageDirectory( ) + "/DCIM/iAround/"
					+ fileName + ".jpg";
			File savedFile = new File( savedPath );
			if ( !savedFile.getParentFile( ).exists( ) )
			{
				savedFile.getParentFile( ).mkdirs( );
			}
			if ( savedFile.exists( ) )
			{
				String message = mContext.getResources( ).getString( R.string.save_file_path )
						+ savedPath;
				CommonFunction.showToast( mContext , message , Toast.LENGTH_SHORT );
				return;
			}

			hdFile.renameTo( savedFile );

			if ( savedFile.exists( ) )
			{
				String message = mContext.getResources( ).getString( R.string.save_file_path ) + savedPath;

				CommonFunction.showToast( mContext , message , Toast.LENGTH_SHORT );
			}
			else
			{
				CommonFunction.showToast( mContext ,
						mContext.getResources( ).getString( R.string.save_error ) , 0 );
			}
		}
		catch ( Throwable e )
		{
			CommonFunction.log( e );
			CommonFunction.showToast( mContext ,
					mContext.getResources( ).getString( R.string.save_error ) , 0 );
		}
	}


	// // 弹出举报窗口
	private void popReportDialog( )
	{
		final CustomContextMenu menu = new CustomContextMenu( mContext );
		menu.addMenuItem( 0 , getResources( ).getString( R.string.report_type_erotic ) ,
				new OnClickListener( )
				{

					@Override
					public void onClick( View v )
					{
						long uid = Common.getInstance( ).loginUser.getUid( );
						boolean isReported = SharedPreferenceUtil.getInstance( mContext )
								.getBoolean( uid + photoId + "erotic" );
						long expirestime = SharedPreferenceUtil.getInstance( mContext )
								.getLong( uid + photoId + "erotic" + "time" );
						if ( !isReported && expirestime < System.currentTimeMillis( ) )
						{
							reportErotic( );
						}
						else
						{
							Toast.makeText( mContext , R.string.report_repeated ,
									Toast.LENGTH_SHORT ).show( );
						}
					}
				} , false );

		menu.addMenuItem( 1 , getResources( ).getString( R.string.report_type_steal ) ,
				new OnClickListener( )
				{

					@Override
					public void onClick( View v )
					{
						long uid = Common.getInstance( ).loginUser.getUid( );
						boolean isReported = SharedPreferenceUtil.getInstance( mContext )
								.getBoolean( uid + photoId );
						long expirestime = SharedPreferenceUtil.getInstance( mContext )
								.getLong( uid + photoId + "time" );
						if ( !isReported && expirestime < System.currentTimeMillis( ) )
						{
							reportSteal( );
						}
						else
						{
							Toast.makeText( mContext , R.string.report_repeated ,
									Toast.LENGTH_SHORT ).show( );
						}
					}
				} , true );

		menu.showMenu( view );
	}

	// 举报色情
	private void reportErotic( )
	{
		try
		{
			// 色情图片举报
			if ( !CommonFunction.forbidSay( mContext ) )
			{
				eroFlag = SpaceModel.getInstance( mContext ).reportReq( mContext , 1 , 2 ,
						photoId , "" , this );
				if ( eroFlag == 1 )
				{
					// mainView.dissmissDialog( );
				}
			}
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			// mainView.dissmissDialog( );
			Toast.makeText( mContext , R.string.network_req_failed , Toast.LENGTH_SHORT )
					.show( );
		}
	}

	// // 举报盗用
	private void reportSteal( )
	{
	}

	android.view.View.OnClickListener onScaledClick = new OnClickListener( )
	{
		
		@Override
		public void onClick( View v )
		{
			// TODO Auto-generated method stub
			mActivity.finish( );
		}
	};
	
	@Override
	public void onScaledClick( View v )
	{
		// TODO Auto-generated method stub
		if (mainView.getIsDeletePhoto( ) )
		{
			mActivity.setResult( SpaceTopicView.RES_SPACE_DELETE );
		}
		mActivity.finish( );
	}
	
	
	
}
