package net.iaround.ui.chat;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.AudioRecordThread;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.im.AudioBaseBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.PathUtil;
import net.iaround.ui.chat.SuperChat.HandleMsgCode;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.interfaces.AudioRecordCallBack;
import net.iaround.ui.interfaces.RecordBtnTouchCallBack;
import net.iaround.ui.view.floatwindow.ChatBarZoomWindow;
import net.iaround.utils.Mp3Lame;

/**
 * "按住说话"的长按触摸事件 处理与UI相关的操作 同时生成录音解码的线程
 * <p/>
 * 点击录制,开启解码. 弹起判断是否要发送 <br>
 * 1.需要发送,生成一条记录,数据库&缓存 <br>
 * 2.不需要发送,删除相应的文件,停止发送线程<br>
 *
 * @author chenlb
 */
public class ChatSendAudioTouchListener implements OnTouchListener, OnInfoListener, AudioRecordCallBack
{
	private static final String TAG = "ChatSendAudioTouchListener";
	// 录音器
	private AudioRecord audioRecord = null;
	// 音频获取源
	private int audioSource = MediaRecorder.AudioSource.MIC;
	// 设置音频采样率:44100, 22050，16000，11025
	private static int sampleRateInHz = 16000;
	// 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
	private static int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	// 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
	private static int audioFormat = AudioFormat.ENCODING_PCM_16BIT;

	// 缓冲区字节大小
	public static int bufferSizeInBytes = AudioRecord.getMinBufferSize( sampleRateInHz, channelConfig, audioFormat );
	private Context mContext;
	/** 显示正在播放 */
	private RelativeLayout chatAudioLayout;
	/** 按住说话 */
	//录语音按钮
	private LinearLayout chatVoice;
	private TextView chatVoiceSendTitle;
	private ChatAudioHolder holder;
	private int voiceButtonHeight;

	// 动画
	private AnimationSet setStart = new AnimationSet( false );
	private AnimationSet setMiddle = new AnimationSet( false );
	private AnimationSet setEnd = new AnimationSet( false );

	private ScaleAnimation scaleAnimationStart;
	private AlphaAnimation alphaAnimationStart;
	private ScaleAnimation scaleAnimationMiddle;
	private AlphaAnimation alphaAnimationMiddle;
	private ScaleAnimation scaleAnimationEnd;
	private AlphaAnimation alphaAnimationEnd;


	// 音频录制线程
	private AudioRecordThread threadRecord = null;

	// 消息：录音时的音量振幅
	public static final int MSG_RECORD_AMPLITUDE = 10101;
	// 没有SD卡，不能录制语音
	public static final int MSG_RECORD_NO_SDCARD = 10102;
	// 录音时间太短
	public static final int MSG_RECORD_SHORT = 10103;
	// 录音达到最大值
	public static final int MSG_RECORD_MAX = 10104;
	// 录音权限受限
	public static final int MSG_RECORD_ERROR = 10105;

	// 最大的录音时间（秒）
	public static int MAX_RECORD_TIME = 60 * 2;

	public RecorderHandler mHandler; //	public static RecorderHandler mHandler;
	private Handler chatHandler;
	// 开始录制时间
	private long recordStartTime;

	// 是否发送
	public boolean bIsSend = true;

	private MessagesSendManager sendManager = null;

	private RecordBtnTouchCallBack touchCallBack;

	// 记录按下录音按钮的时间
	private long actionDownTime = 0;

	// 录音的状态
	private final int STATUS_DEFAULT = 0;
	private final int STATUS_RECORDING = 1;
	private final int STATUS_CANCEL = 2;
	private final int STATUS_TOO_SHORT = 3;

	/**
	 * “按住说话”的触摸事件监听
	 */
	public ChatSendAudioTouchListener( Context context, RelativeLayout chatAudioLayout,
									   LinearLayout chatVoice, MessagesSendManager sendManager, Handler chatHandler,
									   RecordBtnTouchCallBack callBack, int maxRecordTime )
	{
		this.mContext = context;
		this.chatAudioLayout = chatAudioLayout;
		this.chatVoice = chatVoice;
		this.sendManager = sendManager;
		this.chatHandler = chatHandler;
		this.touchCallBack = callBack;
		if ( maxRecordTime > 0 ) {
			MAX_RECORD_TIME = maxRecordTime;
		}

		chatVoiceSendTitle = ( TextView ) chatVoice.findViewById( R.id.sendTitle );
		voiceButtonHeight = chatVoice.getMeasuredHeight( );
		initHolder( );
		initAnimation( );
		showLayout( STATUS_DEFAULT );
		initAudioRecord( );
	}

	public ChatSendAudioTouchListener( Context context, RelativeLayout chatAudioLayout, LinearLayout chatVoice, MessagesSendManager sendManager, Handler chatHandler )
	{
		this( context, chatAudioLayout, chatVoice, sendManager, chatHandler, null, 0 );
	}

	private void initAudioRecord( )
	{
		CommonFunction.log(TAG, "initAudioRecord() into");
		// 创建录音器
		audioRecord = new AudioRecord( audioSource, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes );
		// 初始化lame
//		Mp3Lame.initEncoder( 1, 16000, 128, 3, 5 );
		Mp3Lame.initEncoder( 1, sampleRateInHz, 128, 1, 3 );
		mHandler = new RecorderHandler( );
	}

	private void initHolder( )
	{
		holder = new ChatAudioHolder( );

		holder.rlContent = ( RelativeLayout ) chatAudioLayout.findViewById( R.id.rlContent );
		holder.ivAnimationStart = ( ImageView ) chatAudioLayout.findViewById( R.id.ivAnimationStart );
		holder.ivAnimationMiddle = ( ImageView ) chatAudioLayout.findViewById( R.id.ivAnimationMiddle );
		holder.ivAnimationEnd = ( ImageView ) chatAudioLayout.findViewById( R.id.ivAnimationEnd );
		holder.tvTime = ( TextView ) chatAudioLayout.findViewById( R.id.tvTime );
		holder.ivRecordImageFull = ( ImageView ) chatAudioLayout.findViewById( R.id.ivRecordImageFull );
		holder.ivRecordImageEmpty = ( ImageView ) chatAudioLayout.findViewById( R.id.ivRecordImageEmpty );
		holder.tvNotice = ( TextView ) chatAudioLayout.findViewById( R.id.tvNotice );
		holder.ivRecordImageDelete = ( ImageView ) chatAudioLayout.findViewById( R.id.ivRecordImageDelete );
	}

	@Override
	public boolean onTouch( View v, MotionEvent event )
	{
		if ( v != chatVoice || CommonFunction.forbidSay( mContext ) ) {
			//被禁言
			return true;
		}

		//正在推流不能发语音消息
		final FlyAudioRoom flyAudioRoom = FlyAudioRoom.getInstance();
		if (flyAudioRoom!=null && flyAudioRoom.isPublishing()){
			CommonFunction.toastMsg(mContext,mContext.getResources().getString(R.string.on_mic_cannot_use_voice));
			return true;
		}

		switch ( event.getAction( ) )
		{
			case MotionEvent.ACTION_DOWN:
				if ( touchCallBack != null )
					touchCallBack.onRecordBtnTouchDown( );
				// 按下的时间
				long offsetTime = System.currentTimeMillis( ) - actionDownTime;
				actionDownTime = System.currentTimeMillis( );
				if ( offsetTime < 500 )
				{
					break;
				}

				if ( detectSDcardExist( ) )
				{
					startRecording( );
					updateTime( "" );
					showLayout( STATUS_RECORDING );
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if ( touchCallBack != null )
					touchCallBack.onRecordBtnTouchMove( );
				if ( isRecording( ) )
				{
					if ( event.getY( ) <= voiceButtonHeight - 120 )
					{
						showLayout( STATUS_CANCEL );
					}
					else
					{
						showLayout( STATUS_RECORDING );
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				if ( touchCallBack != null )
					touchCallBack.onRecordBtnTouchUp( );
				if ( event.getY( ) <= voiceButtonHeight - 120 )
				{
					showLayout( STATUS_DEFAULT );
					bIsSend = false;
				}
				else if ( isRecordShort( ) )
				{
					bIsSend = false;
				}
				else
				{
					showLayout( STATUS_DEFAULT );
					bIsSend = true;
				}
				stopRecord( );
				break;
			case MotionEvent.ACTION_CANCEL:
				if ( touchCallBack != null )
					touchCallBack.onRecordBtnTouchCancel( );
				showLayout( STATUS_DEFAULT );
				bIsSend = false;
				stopRecord( );
				break;
		}
		return true;
	}

	/**
	 * 初始化语音控件
	 *
	 * @param type
	 */
	private void showLayout( int type )
	{

		if ( type == STATUS_DEFAULT )
		{ //初始化状态是
			holder.tvTime.setVisibility( View.VISIBLE );
			holder.ivRecordImageFull.setVisibility( View.VISIBLE );
			holder.ivRecordImageEmpty.setVisibility( View.VISIBLE );
			stopAnimation( );
			holder.tvNotice.setVisibility( View.VISIBLE );
			holder.ivRecordImageDelete.setVisibility( View.GONE );
			holder.rlContent.setBackgroundResource(R.drawable.z_new_chat_audio_back_gray);
			chatVoice.setBackgroundResource(R.drawable.chat_bar_new_send_voice_bg_up);
			chatAudioLayout.setVisibility( View.GONE );
			chatVoiceSendTitle.setText( R.string.chat_press_voice );
		}
		else if ( type == STATUS_RECORDING )
		{//正常录制声音

			holder.tvTime.setVisibility( View.VISIBLE );
			holder.ivRecordImageFull.setVisibility( View.VISIBLE );
			holder.ivRecordImageEmpty.setVisibility( View.VISIBLE );
			if ( holder.ivAnimationStart.getVisibility( ) != View.VISIBLE )
			{
				playAnimation( );
			}
			holder.tvNotice.setVisibility( View.VISIBLE );
			holder.tvNotice.setText( R.string.chat_update_audio_cancel );
			holder.ivRecordImageDelete.setVisibility( View.GONE );
			holder.rlContent.setBackgroundResource(R.drawable.z_new_chat_audio_back_gray);
			chatVoice.setBackgroundResource(R.drawable.chat_bar_new_send_voice_bg_down);
			chatAudioLayout.setVisibility( View.VISIBLE );
			chatVoiceSendTitle.setText( R.string.chat_audio_send );

		}
		else if ( type == STATUS_CANCEL )
		{//滑动到此处取消发送

			holder.tvTime.setVisibility( View.INVISIBLE );
			holder.ivRecordImageFull.setVisibility( View.GONE );
			holder.ivRecordImageEmpty.setVisibility( View.GONE );
			holder.tvNotice.setVisibility( View.VISIBLE );
			stopAnimation( );
			holder.tvNotice.setText( R.string.chat_audio_send_cancel );
			holder.ivRecordImageDelete.setVisibility( View.VISIBLE );
			holder.ivRecordImageDelete.setImageResource(R.drawable.chat_new_audio_delet);
			holder.rlContent.setBackgroundResource( R.drawable.z_new_chat_audio_back_gray );
			chatVoice.setBackgroundResource( R.drawable.chat_bar_new_send_voice_bg_down);
			chatVoiceSendTitle.setText( R.string.chat_audio_send_cancel );

		}
		else if ( type == STATUS_TOO_SHORT )
		{//录音时间太短

			holder.tvTime.setVisibility( View.INVISIBLE );
			holder.ivRecordImageFull.setVisibility( View.GONE );
			holder.ivRecordImageEmpty.setVisibility( View.GONE );
			holder.tvNotice.setVisibility( View.VISIBLE );
			stopAnimation( );
			holder.tvNotice.setText( R.string.chat_audio_record_short );
			holder.ivRecordImageDelete.setVisibility( View.VISIBLE );
			holder.ivRecordImageDelete.setImageResource( R.drawable.z_new_chat_audio_short );
			holder.rlContent.setBackgroundResource( R.drawable.z_new_chat_audio_back_gray );

			chatVoice.setBackgroundResource( R.drawable.chat_bar_new_send_voice_bg_down);
			chatVoiceSendTitle.setText( R.string.chat_audio_send_cancel );

			mHandler.postDelayed( new Runnable( )
			{
				@Override
				public void run( )
				{
					showLayout( STATUS_DEFAULT );
				}
			}, 500 );
		}
	}

	private void startRecording( )
	{
		CommonFunction.log(TAG, "startRecording() into");
		if ( audioRecord == null || isRecording( ) ) {
			CommonFunction.log(TAG, "startRecording() record null or recording");
			return;
		}

		// 做一个震动反馈
		Vibrator v = ( Vibrator ) mContext.getSystemService( SuperActivity.VIBRATOR_SERVICE );
		v.vibrate( 50 );

		// 初始化录制开始时间
		recordStartTime = System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime;

		// 开始录音
		audioRecord.startRecording( );

		// 开启音频文件编码写入线程
		threadRecord = new AudioRecordThread( audioRecord, recordStartTime, this );
		threadRecord.start( );

	}

	/**
	 * 返回录音的时长(秒)
	 *
	 * @return
	 */
	private int getRecordTime( )
	{
		int time = Math.round(
				( System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime -
						recordStartTime ) / 1000 );
		if ( time < 1 )
		{
			time = 0;
		}
		if ( time > MAX_RECORD_TIME )
		{
			time = MAX_RECORD_TIME;
		}
		return time;
	}

	/**
	 * 录音时间是否太短
	 *
	 * @return
	 */
	private boolean isRecordShort( )
	{
		int time = Math.round(
				( System.currentTimeMillis( ) + Common.getInstance( ).serverToClientTime -
						recordStartTime ) / 1000 );
		if ( time < 1 )
		{
			// 录音时间少于1秒
			mHandler.sendEmptyMessage( MSG_RECORD_SHORT );
			return true;
		}
		return false;
	}

	@Override
	public void onInfo( MediaRecorder mr, int what, int extra )
	{
		if ( what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED )
		{ // 到达录音的限制时间
			stopRecord( );
			mHandler.sendEmptyMessage( MSG_RECORD_MAX );
		}
	}

	private class RecorderHandler extends Handler
	{

		@Override
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case MSG_RECORD_AMPLITUDE: // 绘制录音的输入音量 ；最大值：32767 / 225
					int time = getRecordTime( );
					if ( time >= MAX_RECORD_TIME )
					{
						mHandler.sendEmptyMessage( MSG_RECORD_MAX );
					}
					else
					{
						updateTime( time <= 0 ? "" : ( time + "”" ) );
						int height = ( Integer ) msg.obj;
						updateRecord( height );
					}
					break;
				case MSG_RECORD_SHORT: // 录音时间太短
					showLayout( STATUS_TOO_SHORT );
					break;
				case MSG_RECORD_NO_SDCARD: // 没有SD卡
					stopRecord( );
					Toast.makeText( mContext, R.string.chat_video_no_sdcard, Toast.LENGTH_SHORT ).show( );
					break;
				case MSG_RECORD_MAX:
					showLayout( STATUS_DEFAULT );
					stopRecord( );
					break;
				case MSG_RECORD_ERROR:
					showLayout( STATUS_DEFAULT );
					stopRecord( );
					Resources re = mContext.getResources( );
					String title = re.getString( R.string.dialog_title );
					String content = re.getString( R.string.record_permission_tips );
					String btnStr = re.getString( R.string.ok );
					DialogUtil.showOneButtonDialog( mContext, title, content, btnStr, null );
			}
			super.handleMessage( msg );
		}
	}

	/** 更新录音界面 */
	private void updateRecord( int heigth )
	{
		CommonFunction.log(TAG, "updateRecord() into, high=" + heigth);
		int parHeigth = holder.ivRecordImageFull.getMeasuredHeight( );
		int emptyHeight = parHeigth - heigth;

		if ( emptyHeight < 0 )
		{
			emptyHeight = 0;
		}
		else if ( emptyHeight > parHeigth )
		{
			emptyHeight = parHeigth;
		}

		RelativeLayout.LayoutParams param = ( LayoutParams ) holder.ivRecordImageEmpty.getLayoutParams( );
		//保持宽度不变
		param.width = holder.ivRecordImageEmpty.getMeasuredWidth();
		//更新高度值，让高度随着声音分贝的大小而改变
		param.height = emptyHeight;
		holder.ivRecordImageEmpty.setScaleType(ImageView.ScaleType.MATRIX);
		holder.ivRecordImageEmpty.setLayoutParams( param );
		holder.ivRecordImageEmpty.invalidate( );
	}

	/** 更新录音倒计时 */
	private void updateTime( String str )
	{
		holder.tvTime.setText( str );
		holder.tvTime.invalidate( );
	}

	public void stopRecord( )
	{
		CommonFunction.log(TAG, "stopRecord() into");
		mHandler.removeMessages( MSG_RECORD_AMPLITUDE );
		if ( audioRecord != null && isRecording( ) )
		{
			threadRecord.bActive = false;
			audioRecord.stop( );// 停止录制
		}
	}

	private boolean detectSDcardExist( )
	{
		if ( !PathUtil.isSDcardExist( ) )
		{ // SD卡不存在
			mHandler.sendEmptyMessage( MSG_RECORD_NO_SDCARD );
			audioRecord.release( );
			audioRecord = null;
			return false;
		}
		return true;
	}

	/**
	 * 释放录音器资源
	 */
	public void releaseAudioRecorder( )
	{
		CommonFunction.log(TAG, "releaseAudioRecorder() into");
		if ( audioRecord != null )
		{
			if ( isRecordInited( ) )
			{
				if ( isRecording( ) )
				{
					audioRecord.stop( );// 停止录制,判断是否正在录音
				}
			}
			audioRecord.release( );// 释放资源
			audioRecord = null;
		}
		// 析构lame
		Mp3Lame.destroyEncoder( );
	}

	@Override
	public void AudioRecordStart( long flag, String filePath )
	{
		AudioBaseBean bean = new AudioBaseBean( );
		bean.flag = recordStartTime;
		bean.filePath = filePath;
		bean.audioLength = "";
		Message msg = new Message( );
		msg.what = HandleMsgCode.AUDIO_DATA_RECORD_START;
		msg.obj = bean;
		chatHandler.sendMessage( msg );
	}

	@Override
	public void AudioVolumeFeedback( double volume )
	{
		Message msg = new Message( );
		msg.what = MSG_RECORD_AMPLITUDE;
		msg.obj = ( int ) volume;
		mHandler.sendMessage( msg );
	}

	@Override
	public void AudioRecordError( long flag )
	{
		bIsSend = false;

		Message msg = new Message( );
		msg.what = MSG_RECORD_ERROR;
		mHandler.sendMessage( msg );
	}

	@Override
	public void AudioRecordEnd( long flag, String filePath )
	{

		String audioLength = String.valueOf( getRecordTime( ) );
		AudioBaseBean bean = new AudioBaseBean( );
		bean.flag = recordStartTime;
		bean.filePath = filePath;
		bean.audioLength = audioLength;

		CommonFunction.log( TAG, "ChatSendAudioTouchListener.AudioRecordEnd bIsSend == " + bIsSend );
		if ( bIsSend )
		{
			bean.isSend = true;
			sendManager.putAuidoRecordLength( bean.flag, audioLength );
		}
		else
		{
			// 取消发送,应该告诉页面删除消息,同时删除数据库的消息
			bean.isSend = false;
			sendManager.recordingCancel( recordStartTime );
		}

		Message msg = new Message( );
		msg.what = HandleMsgCode.AUDIO_DATA_RECORD_FINISH;
		msg.obj = bean;
		chatHandler.sendMessage( msg );
	}

	/** 判断AudioRecord是否初始化 */
	private boolean isRecordInited( )
	{
		if ( audioRecord == null )
			return false;
		return audioRecord.getRecordingState( ) == AudioRecord.STATE_INITIALIZED;
	}

	/** 判断AudioRecord是否正在录音 */
	private boolean isRecording( )
	{
		if ( audioRecord == null )
			return false;
		return audioRecord.getRecordingState( ) == AudioRecord.RECORDSTATE_RECORDING;
	}

	public static class ChatAudioHolder
	{

		RelativeLayout rlContent;

		ImageView ivAnimationStart;
		ImageView ivAnimationMiddle;
		ImageView ivAnimationEnd;

		TextView tvTime;
		ImageView ivRecordImageFull;
		ImageView ivRecordImageEmpty;
		TextView tvNotice;
		ImageView ivRecordImageDelete;
	}

	private void stopAnimation( )
	{

		holder.ivAnimationStart.setVisibility( View.GONE );
		holder.ivAnimationMiddle.setVisibility( View.GONE );
		holder.ivAnimationEnd.setVisibility( View.GONE );


		holder.ivAnimationStart.clearAnimation( );
		holder.ivAnimationMiddle.clearAnimation( );
		holder.ivAnimationEnd.clearAnimation( );
	}

	private void playAnimation( )
	{
		holder.ivAnimationStart.setVisibility( View.VISIBLE );
		holder.ivAnimationMiddle.setVisibility( View.VISIBLE );
		holder.ivAnimationEnd.setVisibility( View.VISIBLE );


		holder.ivAnimationStart.setAnimation( setStart );
		holder.ivAnimationMiddle.setAnimation( setMiddle );
		holder.ivAnimationEnd.setAnimation( setEnd );

		setStart.reset( );
		setMiddle.reset( );
		setEnd.reset( );

		setStart.startNow( );
		mHandler.postDelayed( new Runnable( )
		{

			@Override
			public void run( )
			{
				setMiddle.startNow( );
			}
		}, 1200 );
		mHandler.postDelayed( new Runnable( )
		{

			@Override
			public void run( )
			{
				setEnd.startNow( );
			}
		}, 2400 );
	}

	/**
	 * 初始化动画
	 */
	private void initAnimation( )
	{

		scaleAnimationStart = new ScaleAnimation( 1.0f, 1.6f, 1.0f, 1.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
		alphaAnimationStart = new AlphaAnimation( 1f, 0f );

		scaleAnimationMiddle = new ScaleAnimation( 1.0f, 1.6f, 1.0f, 1.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
		alphaAnimationMiddle = new AlphaAnimation( 1f, 0f );

		scaleAnimationEnd = new ScaleAnimation( 1.0f, 1.6f, 1.0f, 1.6f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f );
		alphaAnimationEnd = new AlphaAnimation( 1f, 0f );

		alphaAnimationStart.setRepeatCount( Animation.INFINITE );
		scaleAnimationStart.setRepeatCount( Animation.INFINITE );

		alphaAnimationMiddle.setRepeatCount( Animation.INFINITE );
		scaleAnimationMiddle.setRepeatCount( Animation.INFINITE );

		alphaAnimationEnd.setRepeatCount( Animation.INFINITE );
		scaleAnimationEnd.setRepeatCount( Animation.INFINITE );

		setStart.setDuration( 3600 );
		setStart.addAnimation( scaleAnimationStart );
		setStart.addAnimation( alphaAnimationStart );

		setMiddle.setDuration( 3600 );
		setMiddle.addAnimation( scaleAnimationMiddle );
		setMiddle.addAnimation( alphaAnimationMiddle );

		setEnd.setDuration( 3600 );
		setEnd.addAnimation( scaleAnimationEnd );
		setEnd.addAnimation( alphaAnimationEnd );

		holder.ivAnimationStart.setAnimation( setStart );
		holder.ivAnimationMiddle.setAnimation( setMiddle );
		holder.ivAnimationEnd.setAnimation( setEnd );
	}

}