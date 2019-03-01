
package net.iaround.ui.chat;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.SuperActivity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 录制视频的界面
 * 
 * @author chenlb
 * 
 */
public class ChatVideoRecorder extends SuperActivity implements OnClickListener,
        OnInfoListener, SurfaceHolder.Callback
{
	private SurfaceView videoView;
	private Button cancelBtn;
	private Button startRecord;
	private Button stopRecord;
	private TextView recordTime;
	
	/**
	 * 返回值
	 */
	private Intent resultData = null;
	/**
	 * 返回值的key：视频的Path
	 */
	public static String URI_PATH = "uri_path";
	
	/**
	 * 消息：视频文件扫描完成
	 */
	public static final int MSG_SCAN_COMPLETE = 0;
	/**
	 * 消息：做录制视频前的准备，并且显示预览
	 */
	public static final int MSG_PREPARE_RECORDER = 1;
	/**
	 * 消息：设置录制时间进度
	 */
	public static final int MSG_SET_RECORD_TIME = 2;
	/**
	 * 消息：显示录制控制栏
	 */
	public static final int MSG_SHOW_RECORD_CONTROLLER = 3;
	/**
	 * 消息：设置停止录制可操作
	 */
	public static final int MSG_ENABLE_STOP = 4;
	
	private ChatVideoRecordHandler mHandler = null;
	
	/**
	 * 视频预览请求码
	 */
	private static final int VIDEO_PREVIEW_REQUSET_CODE = 1;
	
	/**
	 * 视频文件保存的路径
	 */
	private String mRecVideoFile;
	private SurfaceHolder mSurfaceHolder;
	private File dir;
	private MediaRecorder recorder;
	
	private int videoLength;
	
	/**
	 * 录制视频的最大时间长度
	 */
	public static final int VIDEO_MAX_TIME = 60 * 1000;
	/**
	 * 时间进度的计时器
	 */
	private Timer recordTimer;
	/**
	 * 时间进度的计时任务
	 */
	private TimerTask recordTimerTask;
	
	private View controlView;
	private PopupWindow controlWin;
	private RelativeLayout rootLayout;
	private Camera mCamera;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		// 判读是否存在SD卡，并可写
		if ( !PathUtil.isSDcardExist( ) )
		{ // 不存在
			CommonFunction
					.showToast( this , getResString( R.string.chat_video_no_sdcard ) , 1 );
			finish( );
			
			return;
		}
		
		setContentView( R.layout.chat_video_recorder );

		initComponent( );
		initData( );
	}
	
	@Override
	protected void onDestroy( )
	{
		
		if(recorder != null)
		{
			recorder.reset();
			recorder.release();
			recorder = null;
			mCamera.lock();
		}
		
		if ( mCamera != null )
		{
			mCamera.release( );
			mCamera = null;
		}
		
		// setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //
		// 设置为竖屏
		
		super.onDestroy( );
	}
	
	private void addSurfaceView(int width, int height)
	{
		rootLayout.removeAllViews( );
		
		videoView = new SurfaceView( ChatVideoRecorder.this );
		int realWidth = LayoutParams.MATCH_PARENT;
		int realHeight = CommonFunction.getScreenPixWidth(mContext) * height / width; 
		
		LayoutParams params = new LayoutParams( realWidth, realHeight );
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		rootLayout.addView( videoView , params );
		
		
		mSurfaceHolder = videoView.getHolder( );
		mSurfaceHolder.addCallback( this );
		if ( Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB )
		{
			mSurfaceHolder.setType( SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS );
		}
	}
	
	/**
	 * 初始化视图组件
	 */
	private void initComponent( )
	{
		rootLayout = (RelativeLayout) findViewById( R.id.rootLayout );
		
		controlView = LayoutInflater.from( this ).inflate(
				R.layout.chat_video_recorder_control , null );
		cancelBtn = (Button) controlView.findViewById( R.id.cancel );
		startRecord = (Button) controlView.findViewById( R.id.startRecord );
		stopRecord = (Button) controlView.findViewById( R.id.stopRecord );
		recordTime = (TextView) controlView.findViewById( R.id.time );
		
		int matchParent = LayoutParams.MATCH_PARENT;
		controlWin = new PopupWindow( controlView , matchParent, matchParent , true );
		
		cancelBtn.setOnClickListener( this );
		startRecord.setOnClickListener( this );
		stopRecord.setOnClickListener( this );
		
		cancelBtn.setVisibility( View.INVISIBLE );
		startRecord.setVisibility( View.INVISIBLE );
		stopRecord.setVisibility( View.INVISIBLE );
		recordTime.setVisibility( View.INVISIBLE );
	}
	
	/**
	 * 初始化数据
	 */
	private void initData( )
	{
		mHandler = new ChatVideoRecordHandler( );
		
		String path = PathUtil.getVideoDir( );
		dir = new File( path );
		if ( !dir.exists( ) )
		{
			dir.mkdirs( );
		}
		
		mHandler.sendEmptyMessageDelayed( MSG_PREPARE_RECORDER , 1000 ); // 做录制视频前的准备，并且显示预览
		mHandler.sendEmptyMessageDelayed( MSG_SHOW_RECORD_CONTROLLER , 1500 );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		boolean back = super.onKeyDown( keyCode , event );
		if ( KeyEvent.KEYCODE_BACK == keyCode )
		{
			return true;
			/*
			 * if (isRecord) { // 正在录制视频时 } else { // 非录制状态，则释放录制视频的资源
			 * releaseRecorder(); }
			 */
		}
		
		return back;
	}
	
	private class ChatVideoRecordHandler extends Handler
	{
		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case MSG_SCAN_COMPLETE : // 视频文件扫描完成
					String path = (String) msg.obj;
					
					if(recorder != null)
					{
						recorder.release( );
					}
					if(mCamera != null)
					{
						mCamera.release( );
					}
					
					Intent intent = new Intent( ChatVideoRecorder.this ,
							ChatVideoRecordPreview.class );
					intent.putExtra( ChatVideoRecordPreview.VIDEO_PATH , path );
					intent.putExtra( ChatVideoRecordPreview.VIDEO_LENGTH_KEY , videoLength );
					startActivityForResult( intent , VIDEO_PREVIEW_REQUSET_CODE );
					break;
				
				case MSG_PREPARE_RECORDER : // 做录制视频前的准备，并且显示预览
					
					if ( null != mCamera )
					{
						mCamera.release( );
					}
					
					int cameraWidth = 0;
					int cameraHeight = 0;
					try {
						mCamera = Camera.open( );
						Parameters paras = mCamera.getParameters();
						cameraWidth = paras.getPreviewSize().width;
						cameraHeight = paras.getPreviewSize().height;
						mCamera.setDisplayOrientation( 90 );
					} catch (Exception e) {
						e.printStackTrace();
						CommonFunction.toastMsg(mContext, R.string.permission_check_content);
						controlWin.dismiss();
						finish();
						break;
					}
					
					addSurfaceView( cameraHeight, cameraWidth);
					break;
				
				case MSG_SHOW_RECORD_CONTROLLER : // 显示录制控制栏
					try
					{
						if ( rootLayout != null && controlWin != null )
						{
							controlWin.showAtLocation( rootLayout , Gravity.RIGHT
									| Gravity.BOTTOM , 0 , 0 );
							
							startRecord.setVisibility( View.VISIBLE );
							cancelBtn.setVisibility( View.VISIBLE );
						}
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					break;
				
				case MSG_SET_RECORD_TIME :
					int time = msg.arg1;
					videoLength = time;
					setRecordTime( time );
					break;
				
				case MSG_ENABLE_STOP :
					stopRecord.setEnabled( true );
					break;
			}
			
			super.handleMessage( msg );
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		
		if ( Activity.RESULT_OK == resultCode )
		{
			if ( VIDEO_PREVIEW_REQUSET_CODE == requestCode )
			{ // 使用视频
				if ( data != null )
				{
					String result = data.getStringExtra( "cancel" );
					if ( result != null && !"".equals( result ) )
					{ // 取消拍摄，退出
						finish( );
						return;
					}
				}
				
				handleUseVideo();
			}
		}
		else if ( Activity.RESULT_CANCELED == resultCode )
		{
			if ( VIDEO_PREVIEW_REQUSET_CODE == requestCode )
			{ // 重拍视频
				mHandler.sendEmptyMessageDelayed( MSG_PREPARE_RECORDER , 1000 ); // 做录制视频前的准备，并且显示预览
				mHandler.sendEmptyMessageDelayed( MSG_SHOW_RECORD_CONTROLLER , 1500 );
			}
			
		}
	}
	
	/** Use Video need detect net type*/
	private void handleUseVideo()
	{
		String message = mContext.getResources( ).getString( R.string.no_wifi_send_pic );
		String btnStr1 = mContext.getResources( ).getString( R.string.cancel );
		String btnStr2 = mContext.getResources( ).getString( R.string.continue_);
		

		if(PhoneInfoUtil.getInstance( mContext ).getNetType( ).equalsIgnoreCase( "mobile" ))
		{
			
			
			Dialog dialog = DialogUtil.showTowButtonDialog( mContext , "" , message , btnStr1 , btnStr2 , new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					finish( );
					return;
				}
			} , new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					resultData = new Intent( );
					resultData.putExtra( URI_PATH , mRecVideoFile );
					setResult( Activity.RESULT_OK , resultData );
					
					finish( );
				}
			} );
			dialog.setOnCancelListener( new OnCancelListener( )
			{
				
				@Override
				public void onCancel( DialogInterface dialog )
				{
					finish( );
					return;
				}
			} );
			
			dialog.setCanceledOnTouchOutside( false );
		}else
		{
			resultData = new Intent( );
			resultData.putExtra( URI_PATH , mRecVideoFile );
			setResult( Activity.RESULT_OK , resultData );
			
			finish( );
		}
	}
	
	/**
	 * 做录制视频前的准备，并且显示预览
	 */
	private void prepareRecorder( )
	{
		recorder = new MediaRecorder( );
		recorder.setOnInfoListener( this );
		
		//文件用时间来命名，../xxx.3gp
		mRecVideoFile = dir.getAbsolutePath( ) + System.currentTimeMillis( ) + PathUtil.get3GPPostfix( );
		
		mCamera = Camera.open( );
		mCamera.setDisplayOrientation( 90 );
		mCamera.unlock( );
		recorder.setCamera( mCamera );
		
		recorder.setOrientationHint(90);
		recorder.setVideoSource( MediaRecorder.VideoSource.CAMERA );// 视频源
		recorder.setAudioSource( MediaRecorder.AudioSource.CAMCORDER ); // 录音源为麦克风
		
		recorder.setProfile( CamcorderProfile.get( CamcorderProfile.QUALITY_480P ) );
		recorder.setOutputFile( mRecVideoFile );// 保存路径
		
		recorder.setMaxDuration( VIDEO_MAX_TIME );// 最大录制时间
		
		recorder.setPreviewDisplay( mSurfaceHolder.getSurface( ) );// 预览
		
		try
		{
			recorder.prepare( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
			recorder.release();
		}
	}
	
	/**
	 * 开始录制视频
	 */
	private void startRecordVideo( )
	{
		mSurfaceHolder.removeCallback( this );
		if ( mCamera != null )
		{
			mCamera.release( );
			mCamera = null;
		}
		
		try
		{
			prepareRecorder( );
			recorder.start( );
			
//			mCamera.setPreviewDisplay( mSurfaceHolder );
//			mCamera.startPreview( );
			
			//视频长度重置
			videoLength = 0;
			startTimer( );
			// isRecord = true;
			
			startRecord.setVisibility( View.INVISIBLE );
			stopRecord.setEnabled( false );
			stopRecord.setVisibility( View.VISIBLE );
			cancelBtn.setVisibility( View.INVISIBLE );
			recordTime.setVisibility( View.VISIBLE );
			
			mHandler.sendEmptyMessageDelayed( MSG_ENABLE_STOP , 1500 );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
	
	/**
	 * 停止录制视频
	 */
	private void stopRecordVideo( )
	{
		recorder.stop( );
		recorder.reset( );
		recorder.release( );
		recorder = null;
		
		stopTimer( );
		
		startRecord.setVisibility( View.INVISIBLE );
		stopRecord.setVisibility( View.INVISIBLE );
		cancelBtn.setVisibility( View.INVISIBLE );
		recordTime.setVisibility( View.INVISIBLE );
		
		processVideoFile( );
	}
	
	/**
	 * 释放录制视频的资源
	 */
	private void releaseRecorder( )
	{
		if ( recorder != null )
		{
			recorder.reset( );
			recorder.release( );
			recorder = null;
		}
		
		if ( !TextUtils.isEmpty( mRecVideoFile ))
		{
			File file = new File( mRecVideoFile );
			if ( file.exists( ) )
			{
				file.delete( );
			}
		}
	}
	
	/**
	 * 录制完后，把视频的绝对路径返回
	 */
	private void processVideoFile( )
	{
		File videoFile = new File( mRecVideoFile );
		boolean isExist = videoFile.exists( );

		if(isExist)
		{
			Message msg = mHandler.obtainMessage( );
			msg.what = MSG_SCAN_COMPLETE;
			msg.obj = videoFile.getAbsolutePath( );
			mHandler.sendMessage( msg );
		}
		
	}
	
	
	@Override
	public void onClick( View v )
	{
		if ( v == startRecord )
		{ // 开始录制视频
			startRecordVideo( );
		}
		else if ( v == stopRecord )
		{ // 停止录制视频
			controlWin.dismiss( );
			stopRecordVideo( );
		}
		else if ( v == cancelBtn )
		{ // 取消
			controlWin.dismiss( );
			releaseRecorder( );
			finish( );
		}
	}
	

	@Override
	public void onInfo(MediaRecorder mr , int what , int extra )
	{
		if ( MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED == what )
		{ // 到达最大录制时间
			stopRecordVideo( );
		}
	}
	
	/**
	 * 开始计时
	 */
	private void startTimer( )
	{
		setRecordTime( 0 );
		
		recordTimer = new Timer( );
		recordTimerTask = new RecordTimerTask( );
		recordTimer.schedule( recordTimerTask , 1 * 1000 , 1 * 1000 );
	}
	
	/**
	 * 停止计时
	 */
	private void stopTimer( )
	{
		recordTimer.cancel( );
	}
	
	/**
	 * 设置时间进度
	 */
	private void setRecordTime( int time )
	{
		recordTime.setText( TimeFormat.secToTime( time ) );
	}
	
	/**
	 * 录制时间进度计时器的任务
	 * 
	 * @author chenlb
	 * 
	 */
	private class RecordTimerTask extends TimerTask
	{
		/**
		 * 累计的时间长度
		 */
		private int time = 0;
		
		@Override
		public void run( )
		{
			time++;
			
			Message msg = mHandler.obtainMessage( );
			msg.what = MSG_SET_RECORD_TIME;
			msg.arg1 = time;
			mHandler.sendMessage( msg );
		}
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder , int format , int width , int height )
	{
		try
		{
			if ( mCamera != null )
			{
				mCamera.setPreviewDisplay( holder );
				mCamera.startPreview( );
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
	}
	
	@Override
	public void surfaceCreated( SurfaceHolder holder )
	{
	}
	
	@Override
	public void surfaceDestroyed( SurfaceHolder holder )
	{
	}
	
}
