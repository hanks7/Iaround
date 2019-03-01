
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
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.GroupMessageSendSuccess;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.type.ChatMessageType;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.bean.Group;

import org.json.JSONObject;

import java.util.HashMap;


/** 遇见圈子分享 */
public class IAroundGroupUtil extends AbstractShareUtils implements CallBackNetwork
{
	/** 分享标识 */
	public static final int ID = 201;
	
	/** 分享名称 */
	private static final String SHARE_UTIL_NAME = "iaround_group";
	
	/** 发布动态需要的参数 */
	private int TYPE = 2;
	private String ADDRESS = "";// 当前地址
	private int LAT = 0;// 当前纬度
	private int LNG = 0;// 当前经度
	
	public static Group group = new Group( );
	
	private long SEND_SHARE_MESSAGE_FLAG = 0;
	private long SEND_CHAT_MESSAGE_FLAG = 0;
	
	private String SHARE_CONTENT = "";// 分享消息
	public static String CHAT_CONTENT = "";// 想说的聊天内容
	
	/** 系统禁言 */
	public static final int ERROR_SYSTEM_FORBID = -101;
	/** 圈子管理员已对您进行禁言 */
	public static final int ERROR_FORBID_SPEAKING = -400;
	/** 当前圈子为管理员说话模式 */
	public static final int ERROR_SPECIAL_MODE = -500;
	/** 圈子年费已到期 */
	public static final int ERROR_RENEW_GROUP = -600;
	/** 圈子在线人数已达上限 */
	public static final int ERROR_ONLINE_FULL = -40073004;
	
	public IAroundGroupUtil(Context context , String userUid )
	{
		super( context , userUid , SHARE_UTIL_NAME );
		
		initConfig( );
	}
	
	private void initConfig( )
	{
		
		mWeiboDbVersion = 1;
		
		MAX_LENGTH = 140;
		
		mShareDb.saveToken( 1 , SHARE_UTIL_NAME , SHARE_UTIL_NAME , 0 , 0 , SHARE_UTIL_NAME );
		
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
		
		
	}
	
	@Override
	protected int calculateLength( String value )
	{
		
		//int length = StringUtil.getLengthCN1( value );
		return 0;
	}
	
	@Override
	protected boolean checkRegister(Activity arg0 , int arg1 , Object arg2 )
	{
		
		return true;
	}
	
	@Override
	protected void follow(Activity arg0 , String arg1 )
	{
		
		
	}
	
	@Override
	public int getId( )
	{
		
		return ID;
	}
	
	@Override
	public String getName( )
	{
		
		return SHARE_UTIL_NAME;
	}
	
	@Override
	public void onActivityResult( int arg0 , int arg1 , Intent arg2 )
	{
		
		
	}
	
	@Override
	public void onCreate( Bundle arg0 )
	{
		
		
	}
	
	@Override
	public void onResume( )
	{
		
		
	}
	
	@Override
	public void onSaveInstanceState( Bundle arg0 )
	{
		
		
	}
	
	@Override
	public void onStop( )
	{
		
		
	}
	
	@Override
	public void register( Activity arg0 )
	{
		
		return;
	}
	
	@Override
	protected void sendMsg(Activity arg0 , String arg1 , String arg2 , String arg3 )
	{
		
		
	}
	
	@Override
	protected void show(Activity arg0 , String arg1 )
	{
		
		
	}
	
	@Override
	protected void timeline(Activity arg0 , int arg1 , int arg2 , String arg3 , String arg4 )
	{
		
		
	}
	
	@Override
	public void update(Activity activity , String title , String text , String link )
	{
		
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
	public void upload(Activity activity , String title , String text , String link ,
					   String thumb , String pic )
	{
		
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
		SocketGroupProtocol.groupComeIn( mContext , group.id );
	}
	
	@Override
	public void onReceiveMessage( TransportMessage message )
	{
		
		try
		{
			switch ( message.getMethodId( ) )
			{
				case MessageID.GROUP_COME_IN_Y :// 进入群成功
					CommonFunction.log( "share" , "GROUP_COME_IN_Y----------" );
					if ( SEND_SHARE_MESSAGE_FLAG == 0 )
					{
						long currentTime = System.currentTimeMillis( );
						CommonFunction.log( SHARE_TAG , "content***" + SHARE_CONTENT );
						SEND_SHARE_MESSAGE_FLAG = SocketGroupProtocol.groupSendMsg( mContext ,
								currentTime , String.valueOf( ChatMessageType.SHARE ) ,
								"" , SHARE_CONTENT , group.id, "" );
					}
					break;
				case MessageID.GROUP_COME_IN_N : // 进入群失败
					CommonFunction.log( "share" , "GROUP_COME_IN_N>>>>>>" );
					String groupresult = message.getContentBody( );
					JSONObject jsonObj = new JSONObject( groupresult );
					int grouperror = jsonObj.getInt( "error" );
					if ( grouperror == ERROR_ONLINE_FULL )
					{
						handler.sendEmptyMessage( ERROR_ONLINE_FULL );
					}
					else {
						handler.sendEmptyMessage( FAILURE_CODE );
					}					
					break;
				case MessageID.GROUP_SEND_MESSAGE_Y : // 成功发送群消息
					String json = message.getContentBody( );
					GroupMessageSendSuccess bean = GsonUtil.getInstance( ).getServerBean( json ,
							GroupMessageSendSuccess.class );
					if ( SEND_SHARE_MESSAGE_FLAG == bean.flag  )
					{
						long currentTime = System.currentTimeMillis( );
						//分享的消息实体
						ChatRecord record = assembleChatRecord(ChatRecordViewFactory.SHARE, SHARE_CONTENT);
						record.setId( bean.msgid );
						insertRecord2DB(record,bean.incmsgid);
						
						if( !CHAT_CONTENT .equals( "" ))
						{
							SEND_CHAT_MESSAGE_FLAG = SocketGroupProtocol.groupSendMsg( mContext ,
									currentTime , String.valueOf( ChatRecordViewFactory.TEXT ) ,
									"" , CHAT_CONTENT , group.id, "" );
							return;
						}
					}
					
					if(SEND_CHAT_MESSAGE_FLAG == bean.flag)
					{
						ChatRecord record = assembleChatRecord(ChatRecordViewFactory.TEXT, CHAT_CONTENT);
						insertRecord2DB(record,bean.incmsgid);
						CHAT_CONTENT = "";
					}

					SocketGroupProtocol.groupLeave( mContext , group.id , group.user.userid );
					//handler.sendEmptyMessage( SUCCESS_CODE );
					SEND_CHAT_MESSAGE_FLAG = 0;
					SEND_SHARE_MESSAGE_FLAG = 0;
					break;
				case MessageID.GROUP_SEND_MESSAGE_N : // 发送群消息失败
					String result = message.getContentBody( );
					JSONObject jsonObject = new JSONObject( result );
					int error = jsonObject.getInt( "error" );
					if ( error == ERROR_RENEW_GROUP )
					{
						handler.sendEmptyMessage( ERROR_RENEW_GROUP );
					}
					else if ( error == ERROR_FORBID_SPEAKING )
					{
						//handler.sendEmptyMessage( ERROR_FORBID_SPEAKING );
					}
					else if ( error == ERROR_SPECIAL_MODE )
					{
						handler.sendEmptyMessage( ERROR_SPECIAL_MODE );
					}
					else if ( error == ERROR_ONLINE_FULL )
					{
						handler.sendEmptyMessage( ERROR_ONLINE_FULL );
					}
					else {
						handler.sendEmptyMessage( FAILURE_CODE );
					}				
					break;
				case MessageID.GROUP_PUSH_FORBID_SAY :
					handler.sendEmptyMessage( ERROR_FORBID_SPEAKING );
					break;
				case MessageID.GROUP_LEAVE_Y :
					handler.sendEmptyMessage( SUCCESS_CODE );					
					break;
				case MessageID.GROUP_LEAVE_N :
					Message msg = new Message( );
					msg.what = SUCCESS_CODE;
					msg.arg1 = -1;
					handler.sendMessage( msg );
					break;
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		
	}
	
	@Override
	public void onSendCallBack( int e , long flag )
	{
		
		
	}
	
	@Override
	public void onConnected( )
	{
		
		
	}
	
	private Handler handler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case SUCCESS_CODE :
					if ( msg.arg1 == -1 )
					{
						callbackSuccess( ACTION_UPLOADING , msg.arg1 );
					}
					else {
						callbackSuccess( ACTION_UPLOADING, 0 );
					}					
					break;
				case FAILURE_CODE :
					callbackFailure( ACTION_UPLOADING , FAILURE_CODE  );
					break;	
				case ERROR_SYSTEM_FORBID:
					callbackFailure( ACTION_UPLOADING, ERROR_SYSTEM_FORBID );
					break;
				case ERROR_FORBID_SPEAKING :
					callbackFailure( ACTION_UPLOADING, ERROR_FORBID_SPEAKING );
					break;
				case ERROR_SPECIAL_MODE :
					callbackFailure( ACTION_UPLOADING, ERROR_SPECIAL_MODE );
					break;
				case ERROR_RENEW_GROUP :
					callbackFailure( ACTION_UPLOADING, ERROR_RENEW_GROUP );
					break;
				case ERROR_ONLINE_FULL:
					callbackFailure( ACTION_UPLOADING , ERROR_ONLINE_FULL );
					break;
				default :
					break;
			}
		}
	};
	
	private String createShareContent(String title , String text , String link ,
									  String thumb , String pic )
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
		//CommonFunction.log( SHARE_TAG , "content***" + content );
		
		return content;
	}
	
	private void callbackSuccess( int action, int isleave )
	{
		HashMap< String , Object > res = new HashMap< String , Object >( );
		res.put( "status" , SUCCESS_CODE );
		if ( isleave == -1 )
		{
			res.put( "isleave" , isleave );
		}
		if ( mShareActionListener != null )
		{
			mShareActionListener.onComplete( IAroundGroupUtil.this , action , res );
		}
	}
	
	private void callbackFailure( int action , int code )
	{
		HashMap< String , Object > res = new HashMap< String , Object >( );
		res.put( "status" , code );
		if ( mShareActionListener != null )
		{
			mShareActionListener.onComplete( IAroundGroupUtil.this , action , res );
		}
		SEND_CHAT_MESSAGE_FLAG = 0;
		SEND_SHARE_MESSAGE_FLAG = 0;
	}
	
	// 组装一条消息记录
	private ChatRecord assembleChatRecord( int mRecordType , String mContent )
	{
		ChatRecord chatRecord = new ChatRecord( );
		chatRecord.initMineInfo( Common.getInstance( ).loginUser );
		chatRecord.setFuid( Long.valueOf( group.id ) );
		long dateTime = TimeFormat.getCurrentTimeMillis( );
		chatRecord.setId(dateTime);
		
		int recordType = mRecordType;
		String attachment = "";
		String content = mContent;
		int from = ChatFromType.UNKONW;
		chatRecord.initBaseInfo( dateTime , recordType , attachment , content , from );
		return chatRecord;
		
	}
	
	// 插入到数据库当中
	private void insertRecord2DB( ChatRecord record ,long incmsgid)
	{
		String dateTime = String.valueOf( record.getDatetime( ) );
		String uidStr = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		int status = ChatRecordStatus.ARRIVED;
		int noReadCount = 0;
		String contentStr = "";
		try
		{
			JSONObject content = new JSONObject( );
			content.put( "msgid" , record.getId( ) );
			content.put( "type" , record.getType( ) );
			JSONObject userMap = new JSONObject( );
			userMap.put( "userid" , record.getUid( ) );
			userMap.put( "nickname" , record.getNickname( ) );
			userMap.put( "icon" , record.getIcon( ) );
			userMap.put( "viplevel" , record.getVip( ) );
			content.put( "user" , userMap );
			content.put( "datetime" , record.getDatetime( ) );
			content.put( "content" , record.getContent( ) );
			content.put( "attachment" , record.getAttachment( ) );
			content.put( "groupid" , Long.valueOf( group.id ) );
			content.put( "incmsgid" , incmsgid );
			contentStr = content.toString( );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}

		long localID = GroupChatListModel.getInstance( )
			.InsertOneRecord( mContext, record.getUid( ), Long.valueOf( group.id ), record,
				contentStr, status, ( int ) incmsgid );
		record.setLocid(localID);

		String recordContent = GsonUtil.getInstance().getStringFromJsonObject(record);
		String groupNameStr = group == null ? "" : group.name;
		String groupIconStr = group == null ? "" : group.icon;
		GroupModel.getInstance( ).UpdateGroupContact(mContext, uidStr, group.id, groupNameStr,
				groupIconStr, recordContent, dateTime, noReadCount, status, false);

		GroupChatListModel.getInstance( ).saveRecordLocalId(record.getDatetime(), localID);
	}
	
	public static void launchGroupChatActivtiy( Context context )
	{
		// 直接跳转到圈聊
		Intent intent = new Intent( context , GroupChatTopicActivity.class );
		intent.putExtra( "id" , group.id );
		intent.putExtra( "icon" , group.icon );
		intent.putExtra( "name" , group.name );
		intent.putExtra( "userid" , group.user.userid );
		intent.putExtra( "usericon" , group.user.icon );
		intent.putExtra( "grouprole" , group.grouprole );
		intent.putExtra( "isChat" , true );
		intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
		context.startActivity( intent );
	}
}
