package net.iaround.share.interior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.PrivateAudioEndSuccess;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.datamodel.User;

import org.json.JSONException;

import java.util.HashMap;

/** 遇见好友分享 */
public class IAroundFriendUtil extends AbstractShareUtils implements CallBackNetwork
{
	/** 分享标识 */
	public static final int ID = 202;
	
	/** 分享名称 */
	private static final String SHARE_UTIL_NAME = "iaround_friend";
	
	/** 发布动态需要的参数 */
	private int TYPE = 2;
	private String ADDRESS = "";//当前地址
	private int LAT = 0;//当前纬度
	private int LNG = 0;//当前经度
	
	public static User receiver = new User( );
	
	private long SEND_SHARE_MESSAGE_FLAG = 0;
	private long SEND_CHAT_MESSAGE_FLAG = 0;
	
	private String SHARE_CONTENT = "";//分享消息
	public static String CHAT_CONTENT = "";//想说的聊天内容
	
	/** 系统禁言 */
	public static final int ERROR_SYSTEM_FORBID = -101;
	
	public IAroundFriendUtil(Context context , String userUid )
	{
		super( context , userUid , SHARE_UTIL_NAME );
		// TODO Auto-generated constructor stub
		initConfig( );
	}
	
	private void initConfig( ) {
		
		mWeiboDbVersion = 1;
		
		MAX_LENGTH = 140;
		
		mShareDb.saveToken( 1, SHARE_UTIL_NAME,
				SHARE_UTIL_NAME, 0, 0, SHARE_UTIL_NAME );
		
		GeoData geoData = LocationUtil.getCurrentGeo( mContext );
		if ( geoData != null )
		{
			LAT = geoData.getLat( );
			LNG = geoData.getLng( );
			ADDRESS = geoData.getSimpleAddress( );
		}
	}

	@Override
	public void bind(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected int calculateLength( String value )
	{
		// TODO Auto-generated method stub
		//int length = StringUtil.getLengthCN1( value );
		return 0;
	}
	
	@Override
	protected boolean checkRegister(Activity arg0 , int arg1 , Object arg2 )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void follow(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getId( )
	{
		// TODO Auto-generated method stub
		return ID;
	}
	
	@Override
	public String getName( )
	{
		// TODO Auto-generated method stub
		return SHARE_UTIL_NAME;
	}
	
	@Override
	public void onActivityResult( int arg0 , int arg1 , Intent arg2 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCreate( Bundle arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume( )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSaveInstanceState( Bundle arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStop( )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register( Activity arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void sendMsg(Activity arg0 , String arg1 , String arg2 , String arg3 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void show(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void timeline(Activity arg0 , int arg1 , int arg2 , String arg3 , String arg4 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Activity activity, String title, String text,
					   String link )
	{
		// TODO Auto-generated method stub
		if ( CommonFunction.forbidSay( mContext ) )
		{
			handler.sendEmptyMessage( ERROR_SYSTEM_FORBID );
			return;
		}
		
		if ( text.length( ) > MAX_LENGTH )
		{
			text = text.substring( 0 , MAX_LENGTH );
		}
		
		SHARE_CONTENT = createShareContent( title , text , link , "" , "" );
	
		beginShare( );
	}
	
	@Override
	public void upload(Activity activity, String title, String text,
					   String link, String thumb, String pic )
	{
		// TODO Auto-generated method stub
		if ( CommonFunction.forbidSay( mContext ) )
		{
			handler.sendEmptyMessage( ERROR_SYSTEM_FORBID );
			return;
		}
		
		if ( text.length( ) > MAX_LENGTH )
		{
			text = text.substring( 0 , MAX_LENGTH );
		}
		
		SHARE_CONTENT = createShareContent( title , text , link , thumb , pic );
		
		beginShare( );
	}
	
	private void beginShare( )
	{
		ConnectorManage.getInstance( mContext ).setCallBackAction( this );
		long currentTime = System.currentTimeMillis( );
		CommonFunction.log( SHARE_TAG , "content***" + SHARE_CONTENT );
		SEND_SHARE_MESSAGE_FLAG = SocketSessionProtocol.sessionPrivateMsg(
																mContext , currentTime , receiver.getUid( ) ,
																1 , String.valueOf( ChatRecordViewFactory.SHARE ) ,  "",
																ChatFromType.UNKONW , SHARE_CONTENT );
	}

	@Override
	public void onReceiveMessage( TransportMessage message )
	{
		// TODO Auto-generated method stub
		switch ( message.getMethodId( ) )
		{
			case MessageID.SESSION_SEND_PRIVATE_CHAT_SUCC : // 发送私聊消息：成功
				PrivateAudioEndSuccess bean = GsonUtil.getInstance( ).
				getServerBean( message.getContentBody( ) , PrivateAudioEndSuccess.class );
				
				if(SEND_SHARE_MESSAGE_FLAG == bean.flag)
				{
					//分享的消息实体
					ChatRecord record = assembleChatRecord(ChatRecordViewFactory.SHARE, SHARE_CONTENT);
					insertRecord2DB(bean, record);
					
					if(!CHAT_CONTENT.equals( "" ))
					{
						long currentTime = System.currentTimeMillis( );
						SEND_CHAT_MESSAGE_FLAG = SocketSessionProtocol.sessionPrivateMsg(
																				mContext , currentTime , receiver.getUid( ) , 1 ,
																				String.valueOf( ChatRecordViewFactory.TEXT ) ,
																				"", ChatFromType.UNKONW , CHAT_CONTENT );
					}
					else {
						handler.sendEmptyMessage( SUCCESS_CODE );
					}
				}
				
				if(SEND_CHAT_MESSAGE_FLAG == bean.flag)
				{
					handler.sendEmptyMessage( SUCCESS_CODE );
					//想说的话的实体
					ChatRecord record = assembleChatRecord(ChatRecordViewFactory.TEXT, CHAT_CONTENT);
					insertRecord2DB(bean, record);
					CHAT_CONTENT = "";
				}		
				break;
			case MessageID.SESSION_SEND_PRIVATE_CHAT_FAIL : // 发送私聊消息：失败
				handler.sendEmptyMessage( FAILURE_CODE );
				break;
		}
	}

	@Override
	public void onSendCallBack( int e , long flag )
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected( )
	{
		// TODO Auto-generated method stub
		
	}
	
	private String createShareContent(String title, String text,
									  String link, String thumb, String pic )
	{
		String content = "";
		
		RecordShareBean bean = new RecordShareBean( );
		bean.title = title;
		bean.content = text;
		bean.link = link;
		
		String imageUrl = "";
		if ( !"".equals( thumb ) && thumb != null ) {
			imageUrl = thumb;
		} else {
			if ( !"".equals( pic ) && pic != null ) {
				imageUrl = pic;
			}
		}
		bean.thumb = imageUrl;
		
		content = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
		CommonFunction.log( SHARE_TAG , "content***" + content );
		
		return content;
	}
	
	private void callbackSuccess( int action )
	{
		HashMap<String, Object> res = new HashMap<String, Object>( );
		res.put( "status", SUCCESS_CODE );
		if ( mShareActionListener != null ) {
			mShareActionListener.onComplete( IAroundFriendUtil.this,
					action, res );
		}
		SEND_CHAT_MESSAGE_FLAG = 0;
		SEND_SHARE_MESSAGE_FLAG = 0;
	}
	
	private void callbackFailure( int action , int code )
	{
		HashMap< String , Object > res = new HashMap< String , Object >( );
		res.put( "status" , code );
		if ( mShareActionListener != null )
		{
			mShareActionListener.onComplete( IAroundFriendUtil.this , action , res );
		}
		SEND_CHAT_MESSAGE_FLAG = 0;
		SEND_SHARE_MESSAGE_FLAG = 0;
	}
	
	private Handler handler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( Message msg )
		{			
			switch ( msg.what )
			{				
				case SUCCESS_CODE :				
					callbackSuccess( ACTION_UPLOADING );
					break;
				case FAILURE_CODE:
					callbackFailure( ACTION_UPLOADING, FAILURE_CODE );
					break;
				case ERROR_SYSTEM_FORBID:
					callbackFailure( ACTION_UPLOADING, ERROR_SYSTEM_FORBID );
					break;
				default :
					break;
			}
		}
	};
	
	//组装一条消息记录
	private ChatRecord assembleChatRecord(int mRecordType, String mContent)
	{
		ChatRecord chatRecord = new ChatRecord();
		chatRecord.initMineInfo(Common.getInstance().loginUser);
		if(receiver != null)
		{
			chatRecord.initFriendInfo(receiver);
		}

		long dateTime = TimeFormat.getCurrentTimeMillis();
		int recordType = mRecordType;
		String attachment = "";
		String content = mContent;
		int from = ChatFromType.UNKONW;
		chatRecord.initBaseInfo(dateTime, recordType, attachment, content, from);
		return chatRecord;
		
	}
	
	//插入到数据库当中
	private void insertRecord2DB(PrivateAudioEndSuccess bean, ChatRecord record)
	{
		long userid = Common.getInstance().loginUser.getUid();
		long fUserid = receiver.getUid();
		int accostRelation = ChatPersonalModel.getInstance().getAccostRelation(mContext, userid, fUserid);
		int subGroupType = accostRelation == 1 ? SubGroupType.NormalChat : SubGroupType.SendAccost;
		record.setStatus(ChatRecordStatus.ARRIVED);
		long localID = ChatPersonalModel.getInstance().insertOneRecord(mContext, receiver, record,
				subGroupType, ChatFromType.UNKONW);
	
		String localIdStr = String.valueOf(localID);
		String msgidStr = String.valueOf(bean.msgid);
		String statusStr = String.valueOf(ChatRecordStatus.ARRIVED);
		try {
			ChatPersonalModel.getInstance().modifyMessageId(mContext, localIdStr, msgidStr, statusStr, bean.distance);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static User createUser( GroupUser groupUser )
	{
		User user = new User( );
		user.setUid( groupUser.userid );
		user.setNickname( groupUser.nickname );
		user.setNoteName( groupUser.notes );
		user.setIcon( groupUser.icon );
		user.setViplevel( groupUser.vip );
		user.setSVip( groupUser.svip);
		user.setSex( groupUser.getSex( ) );
		user.setAge( groupUser.age );
		user.setLat( groupUser.lat );
		user.setLng( groupUser.lng );
		user.setDistance( 0 );
		return user;
	}
	
//	public static void launchPersonalChatActivity( Context context )
//	{
//		ChatPersonal.skipToChatPersonal( context , receiver );
//	}
}
