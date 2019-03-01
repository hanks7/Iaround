
package net.iaround.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.PushHttpProtocol;
import net.iaround.model.database.SpaceModel;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.SplashActivity;
import net.iaround.ui.dynamic.NotificationFunction;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;


/**
 * BackService是一个后台轮询服务，会在开机和解锁后自动执行。
 * <p>
 * BackService启动后会判断推送设置的设定，只有当前时间为设置的推送时间 而且有请求推送的类型，才会执行推送请求，否则会立刻退出服务。如果当前时
 * 间落在推动设置时间内，会执行10分钟一次的轮询
 * <p>
 * 主程序启动的时候，BackService会被关闭
 * 
 */
public class BackService extends Service implements Callback, HttpCallBack
{
	/** 停止BackService的广播类型 */
	public static final String STOP_SERVICE_BROADCAST_KEY = "net.iaround.service.backservice.stop";
	private static final long REQ_PERIOD = 1000 * 60 * 10; // 10分钟请求一次
	private static final long REQ_DELAY = 5000; // 延迟5秒，开始请求
	private static long lastReqTime; // 上次请求时间
	public static boolean launched; // 判断BackService是否启动
	private BroadcastReceiver stopServiceRec; // 服务暂停广播接收器
	private ConnectorManage conn; // 网络请求处理工具
	private Timer timer; // 轮询计时器
	private int recStartTime; // 推送开始时间
	private int recEndTime; // 推送结束时间
	private String reqToken;
	private Handler handler;
	
	public IBinder onBind(Intent intent )
	{
		return null;
	}
	
	public void onStart(Intent intent , int startId )
	{
		super.onStart( intent , startId );
		if ( launched )
		{
			long curTime = System.currentTimeMillis( );
			if ( curTime - lastReqTime > 5000 )
			{ // 5秒钟内解锁两次，是不会被执行的
				lastReqTime = curTime;
				new Thread( )
				{
					public void run( )
					{
						try
						{
							Thread.sleep( REQ_DELAY );
							req( );
						}
						catch ( Throwable e )
						{
							CommonFunction.log( e );
						}
					}
				}.start( );
			}
		}
		else
		{ // 启动服务
			CommonFunction.log( "BackService" , "==========================================" );
			CommonFunction.log( "BackService" , "========== BackService Launched ==========" );
			CommonFunction.log( "BackService" , "==========================================" );
			init( );
			startTimer( );
			launched = true;
		}
	}
	
	public void onDestroy( )
	{
		super.onDestroy( );
		CommonFunction.stopLogcatRedirect( );
		launched = false;
		CommonFunction.log( "BackService" , "==========================================" );
		CommonFunction.log( "BackService" , "========== BackService Stopped ===========" );
		CommonFunction.log( "BackService" , "==========================================" );
	}
	
	public boolean handleMessage( Message msg )
	{
		int[ ] data = ( int[ ] ) msg.obj;
		if ( data != null )
		{
			StringBuffer sb = new StringBuffer( );
			if ( data[ 0 ] > 0 )
			{ // 已收通知数量
				sb.append( String
						.format( getString( R.string.push_notify_letter ) , data[ 0 ] ) );
			}
			if ( data[ 3 ] > 0 )
			{ // 已收评论数量
				if ( sb.length( ) > 0 )
				{
					sb.append( ", " );
				}
				sb.append( String.format( getString( R.string.push_notify_personal ) ,
						data[ 3 ] ) );
			}
			if ( sb.length( ) > 0 )
			{
				String msgCon = getString( R.string.push_notify_head ) + sb.toString( );
				CommonFunction.log( "BackService" , "Receive push message: " + msgCon );
				NotificationFunction.getInstatant( getApplicationContext( ) ).cancelNotification( );
				
				SharedPreferenceUtil util = SharedPreferenceUtil
						.getInstance( BackService.this );
				String notifyKey = SharedPreferenceUtil.NOTFIC_SETTING
						+ Common.getInstance( ).loginUser.getUid( );
				if ( ( util.has( notifyKey ) && util.getBoolean( notifyKey ) )
						&& CommonFunction.isShakeState( BackService.this ) )
				{
					CommonFunction.notifyMsgVoice( BackService.this , -1 );
					CommonFunction.notifyMsgShake( BackService.this );
				}
				else if ( util.has( notifyKey ) && util.getBoolean( notifyKey ) )
				{
					CommonFunction.notifyMsgVoice( BackService.this , -1 );
				}
				else if ( CommonFunction.isShakeState( BackService.this ) )
				{
					CommonFunction.notifyMsgShake( BackService.this );
				}
				Intent i = new Intent( BackService.this , SplashActivity.class );
				String title = getString( R.string.app_name );
				String userNick = Common.getInstance( ).loginUser.getNickname( );
				if ( userNick != null && userNick.length( ) > 0 )
				{
					title += " - " + Common.getInstance( ).loginUser.getNickname( );
				}
				NotificationFunction.getInstatant( getApplicationContext() ).showNotification( BackService.this , i , title , msgCon ,
						R.drawable.icon ,-1);
			}
		}
		return false;
	}
	
	private void init( )
	{
		// 初始化数量
		handler = new Handler( this );
		
		// 初始化网络
		conn = ConnectorManage.getInstance( this );
//		conn.setCallBackAction( this );
		
		// 停止广播接收器
		stopServiceRec = new BroadcastReceiver( )
		{
			public void onReceive(Context context , Intent intent )
			{
				try
				{
					unregisterReceiver( this );
					stopTimer( );
					stopSelf( );
					CommonFunction.log( "BackService" , getClass( ).getName( ) + " stopped" );
				}
				catch ( Throwable t )
				{
					CommonFunction.log( t );
				}
			}
		};
		registerReceiver( stopServiceRec , new IntentFilter( STOP_SERVICE_BROADCAST_KEY ) );
		
		// 读取设置数据
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( this );
		boolean reqPushData = isReqPush( sp ); // 判断是否需要执行推送的轮询
		if ( reqPushData )
		{
			try
			{
				int[ ] time = getPushTime( sp ); // 获取推送时间（单位：小时）
				if ( time != null && time.length == 2 )
				{
					recStartTime = time[ 0 ];
					recEndTime = time[ 1 ];					
				}
			}
			catch ( Exception e )
			{
			}
			reqToken = getRequestToken( sp );
			ConnectorManage.getInstance( this ).getKey( );
		}
		else
		{
			CommonFunction.stopBackService( this );
		}
	}
	
	/**
	 * 判断是否需要执行推送请求
	 * 
	 * @param sp
	 * @return true is need send request
	 */
	private boolean isReqPush( SharedPreferenceUtil sp )
	{
		if ( !SpaceModel.getInstance( this ).isAutoLogin( ) )
		{
			// 不自动登录，证明已经注销，或者不自动登录，不能推送
			return false;
		}
		
		String offlineMsgKey;
		String privateMsgKey;
		String commentsKey;
		String noticeKey;
		String autoLoginKey;
		
		if ( sp.has( SharedPreferenceUtil.USERID ) )
		{
			offlineMsgKey = SharedPreferenceUtil.REC_OFFLINE_MSG
					+ sp.getString( SharedPreferenceUtil.USERID );
			privateMsgKey = SharedPreferenceUtil.REC_PRIVATE_MSG
					+ sp.getString( SharedPreferenceUtil.USERID );
			commentsKey = SharedPreferenceUtil.REC_COMMENTS
					+ sp.getString( SharedPreferenceUtil.USERID );
			noticeKey = SharedPreferenceUtil.REC_NOTICE
					+ sp.getString( SharedPreferenceUtil.USERID );
			autoLoginKey = SharedPreferenceUtil.AUTO_LOGIN
					+ sp.getString( SharedPreferenceUtil.USERID );
		}
		else
		{
			offlineMsgKey = SharedPreferenceUtil.REC_OFFLINE_MSG;
			privateMsgKey = SharedPreferenceUtil.REC_PRIVATE_MSG;
			commentsKey = SharedPreferenceUtil.REC_COMMENTS;
			noticeKey = SharedPreferenceUtil.REC_NOTICE;
			autoLoginKey = SharedPreferenceUtil.AUTO_LOGIN;
		}
		boolean bl09 = true;
		try
		{
			bl09 = sp.getBoolean( offlineMsgKey );
		}
		catch ( Exception e )
		{
		}
		boolean bl10 = true;
		try
		{
			bl10 = sp.getBoolean( privateMsgKey );
		}
		catch ( Exception e )
		{
		}
		boolean bl11 = true;
		try
		{
			bl11 = sp.getBoolean( commentsKey );
		}
		catch ( Exception e )
		{
		}
		boolean bl12 = true;
		try
		{
			bl12 = sp.getBoolean( noticeKey );
		}
		catch ( Exception e )
		{
		}
		boolean bl16 = true;
		try
		{
			bl16 = sp.getBoolean( autoLoginKey );
		}
		catch ( Exception e )
		{
		}
		return ( ( bl09 || bl10 || bl11 || bl12 ) && bl16 );
	}
	
	// 读取推送时间区间
	private int[ ] getPushTime( SharedPreferenceUtil sp )
	{
		String startKey;
		String endKey;
		
		if ( sp.has( SharedPreferenceUtil.USERID ) )
		{
			startKey = SharedPreferenceUtil.REC_START_TIME
					+ sp.getString( SharedPreferenceUtil.USERID );
			endKey = SharedPreferenceUtil.REC_END_TIME
					+ sp.getString( SharedPreferenceUtil.USERID );
		}
		else
		{
			startKey = SharedPreferenceUtil.USER_SETTING;
			endKey = SharedPreferenceUtil.REC_END_TIME;
		}
		int startTime = sp.getInt( startKey );
		int endTime = sp.getInt( endKey );
		return new int[ ]
			{ startTime , endTime };
	}
	
	private String getRequestToken(SharedPreferenceUtil sp )
	{
		if ( sp.has( SharedPreferenceUtil.TOKEN ) )
		{
			return sp.getString( SharedPreferenceUtil.TOKEN );
		}
		return null;
	}
	
	private void startTimer( )
	{
		if ( timer == null )
		{
			timer = new Timer( );
			timer.schedule( new TimerTask( )
			{
				public void run( )
				{
					req( );
				}
			} , REQ_DELAY , REQ_PERIOD );
		}
	}
	
	/** 发送请求 */
	private void req( )
	{
		Calendar cal = Calendar.getInstance( );
		cal.setTimeInMillis( System.currentTimeMillis( ) );
		int curHour = cal.get( Calendar.HOUR_OF_DAY );
		if ( curHour >= recStartTime && curHour < recEndTime )
		{			
			// 免打扰时间段内，不请求推送消息		
		}
		else
		{
			
			CommonFunction.log( "BackService" , "Try to request push data" );
			try
			{
				LinkedHashMap< String , Object > ent = new LinkedHashMap< String , Object >( );
				ent.put( "token" , reqToken );
				ent.put( "appversion" , Config.APP_VERSION );
				
				PushHttpProtocol.pushNotify( this , ent , this );// 发送请求
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			
			
		}
	}
	
	private void stopTimer( )
	{
		if ( timer != null )
		{
			timer.cancel( );
			timer = null;
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		try
		{
			JSONObject json = new JSONObject( result );
			int status = json.optInt( "status" );
			if ( status == 200 )
			{
				Message msg = new Message( );
				int[ ] data = new int[ ]
					{ json.optInt( "letter" ) , json.optInt( "notice" ) ,
							json.optInt( "comment" ) , json.optInt( "personal" ) };
				msg.obj = data;
				handler.sendMessage( msg );
			}
			else
			{
				onGeneralError( json.optInt( "error" ) , flag );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		CommonFunction.log( "BackService" , getClass( ).getName( ) + " throws error:\n\t" + e );
	}
	
	public void onUploadFileProgress( int lengthOfUploaded , long flag )
	{
		
	}
	
	public void onUploadFileFinish( long flag , String result )
	{
		
	}
	
	public void onUploadFileError(String e , long flag )
	{
		
	}
	
	public void onDownloadFileProgress( long lenghtOfFile , long LengthOfDownloaded , int flag )
	{
		
	}
	
	public void onDownloadFileFinish(int flag , String fileName , String fileDir )
	{
		
	}
	
	public void onDownloadFileError(int flag , String fileName , String fileDir )
	{
		
	}
	
}
