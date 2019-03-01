
package net.iaround.ui.chat;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.SuperActivity;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 视频录制完后，预览视频的界面
 * 
 * @author chenlb
 * 
 */
public class ChatVideoRecordPreview extends SuperActivity implements OnClickListener,
        OnCompletionListener, OnErrorListener
{
	// private ImageView previewImage;
	private Button playBtn;
	private VideoView videoView;
	private TextView playTime;
	private Button cancelBtn;
	private RelativeLayout previewControl;
	private Button againBtn;
	private Button useBtn;
	private RelativeLayout playControl;
	private Button pauseBtn;
	private Button stopBtn;
	
	/**
	 * 视频的URI
	 */
	public static final String VIDEO_PATH = "video_path";
	
	/** 返回值的key：视频长度*/
	public static String VIDEO_LENGTH_KEY= "video_length_key";
	
	/** 视频的文件路径 */
	private String videoFilePath;
	/** 视频总时长(秒) */
	private int videoDuration;
	/**
	 * 播放时间的计时器
	 */
	private Timer playTimer;
	/**
	 * 播放时间的计时任务
	 */
	private TimerTask playTimerTask;
	/**
	 * 当前的播放进度(秒)
	 */
	private int curDuration;
	
	private PreviewHandler mHandler;
	/**
	 * 消息：更新播放时间
	 */
	private static final int MSG_UPDATE_PLAY_TIME = 1;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		// 全屏
		getWindow( ).setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN ,
				WindowManager.LayoutParams.FLAG_FULLSCREEN );

		videoFilePath = getIntent( ).getStringExtra( VIDEO_PATH );
		videoDuration = getIntent( ).getIntExtra( VIDEO_LENGTH_KEY, 0 );
		
		setContentView( R.layout.chat_video_record_preview );
		
		initComponent( );
		initData( );
	}
	
	/**
	 * 初始化视图组件
	 */
	private void initComponent( )
	{
		playBtn = (Button) findViewById( R.id.play );
		videoView = (VideoView) findViewById( R.id.videoView );
		playTime = (TextView) findViewById( R.id.time );
		cancelBtn = (Button) findViewById( R.id.cancel );
		previewControl = (RelativeLayout) findViewById( R.id.previewControl );
		againBtn = (Button) findViewById( R.id.again );
		useBtn = (Button) findViewById( R.id.use );
		playControl = (RelativeLayout) findViewById( R.id.playControl );
		pauseBtn = (Button) findViewById( R.id.pause );
		stopBtn = (Button) findViewById( R.id.stop );
		
		videoView.setOnCompletionListener( this );
		videoView.setOnErrorListener( this );
		
		playBtn.setOnClickListener( this );
		cancelBtn.setOnClickListener( this );
		againBtn.setOnClickListener( this );
		useBtn.setOnClickListener( this );
		pauseBtn.setOnClickListener( this );
		stopBtn.setOnClickListener( this );
		
		playControl.setVisibility( View.INVISIBLE );
	}
	
	/**
	 * 初始化数据
	 */
	private void initData( )
	{
		mHandler = new PreviewHandler( );
		curDuration = 0;
		
		setPlayTime( videoDuration );
		videoView.setVideoPath( videoFilePath );
	}
	
	@Override
	protected void onDestroy( )
	{
//		setRequestedOrientation( ActivityInfo.SCREEN_ORIENTATION_PORTRAIT ); // 设置为竖屏
		
		super.onDestroy( );
	}
	
	/**
	 * 设置时间进度
	 * 
	 * @param time
	 *            时间（秒）
	 */
	private void setPlayTime( int time )
	{
		playTime.setText( TimeFormat.secToTime( time ) );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		boolean back = super.onKeyDown( keyCode , event );
		if ( KeyEvent.KEYCODE_BACK == keyCode )
		{
			return true;
		}
		else
		{
			return back;
		}
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v == playBtn )
		{ // 播放
			play( );
		}
		else if ( v == cancelBtn )
		{ // 取消
			cancelRecord( );
		}
		else if ( v == againBtn )
		{ // 重拍
			delVideo( );
			
			setResult( Activity.RESULT_CANCELED );
			finish( );
		}
		else if ( v == useBtn )
		{ // 使用
			setResult( Activity.RESULT_OK );
			finish( );
		}
		else if ( v == pauseBtn )
		{ // 暂停播放
			pause( );
		}
		else if ( v == stopBtn )
		{ // 停止播放
			stop( );
		}
	}
	
	/**
	 * 退出，并删除视频文件的相关内容
	 */
	private void cancelRecord( )
	{
		delVideo( );
		
		Intent data = new Intent( );
		data.putExtra( "cancel" , "1" );
		setResult( Activity.RESULT_OK , data );
		finish( );
	}
	
	/**
	 * 删除视频文件的相关内容
	 */
	private void delVideo( )
	{
		File file = new File( videoFilePath );
		if ( file.exists( ) )
		{
			file.delete( );
		}
	}
	
	/**
	 * 播放
	 */
	private void play( )
	{
		playTimer = new Timer( );
		playTimerTask = new PlayTimerTask( curDuration );
		
		videoView.start( );
		setPlayTime( curDuration );
		// 启动播放时间计时器
		playTimer.schedule( playTimerTask , 1 * 1000 , 1 * 1000 );
		
		cancelBtn.setVisibility( View.GONE );
		playBtn.setVisibility( View.GONE );
		previewControl.setVisibility( View.GONE );
		playControl.setVisibility( View.VISIBLE );
	}
	
	/**
	 * 暂停播放
	 */
	private void pause( )
	{
		videoView.pause( );
		
		playTimer.cancel( );
		curDuration = videoView.getCurrentPosition( ) / 1000;
		
		cancelBtn.setVisibility( View.VISIBLE );
		playBtn.setVisibility( View.VISIBLE );
		previewControl.setVisibility( View.VISIBLE );
		playControl.setVisibility( View.GONE );
	}
	
	/**
	 * 停止播放
	 */
	private void stop( )
	{
		if ( playTimer != null )
		{
			playTimer.cancel( );
		}
		videoView.stopPlayback( );
		
		curDuration = 0;
		
		videoView.setVideoPath( videoFilePath );
		
		cancelBtn.setVisibility( View.VISIBLE );
		playBtn.setVisibility( View.VISIBLE );
		previewControl.setVisibility( View.VISIBLE );
		playControl.setVisibility( View.GONE );
		
		setPlayTime( videoDuration );
	}
	
	@Override
	public void onCompletion( MediaPlayer mp )
	{
		stop( );
	}
	
	@Override
	public boolean onError(MediaPlayer mp , int what , int extra )
	{
		// stop();
		CommonFunction.showToast( this , getResString( R.string.chat_video_recorder_error ) ,
				0 );
		cancelRecord( );
		
		return false;
	}
	
	/**
	 * 播放时间的计时任务
	 * 
	 * @author chenlb
	 * 
	 */
	private class PlayTimerTask extends TimerTask
	{
		private int time;
		
		/**
		 * 
		 * @param time
		 *            时间进度（秒）
		 */
		public PlayTimerTask( int time )
		{
			this.time = time;
		}
		
		@Override
		public void run( )
		{
			time++;
			
			if ( time > videoDuration )
			{
				return;
			}
			
			Message msg = mHandler.obtainMessage( );
			msg.what = MSG_UPDATE_PLAY_TIME;
			msg.arg1 = time;
			mHandler.sendMessage( msg );
		}
	}
	
	private class PreviewHandler extends Handler
	{
		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case MSG_UPDATE_PLAY_TIME :
					int time = msg.arg1;
					setPlayTime( time );
					break;
			}
			
			super.handleMessage( msg );
		}
	}
}
