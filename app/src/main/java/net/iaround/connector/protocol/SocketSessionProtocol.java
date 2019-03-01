
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.conf.MessageID;
import net.iaround.connector.ConnectorManage;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.utils.DeviceUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;


/**
 * session服务协议接口
 * 
 * @author linyg
 * 
 */
public class SocketSessionProtocol extends SocketProtocol
{
	private final static String TAG = "SocketSessionProtocol";
	
	private static long sessionSend(Context context , LinkedHashMap< String , Object > map ,
                                    int msgId )
	{
		String param = "";
		if ( map != null )
		{
			JSONObject json = new JSONObject( map );
			param = json.toString( );
		}
		return sessionSend( context , param , msgId );
	}
	
	private static long sessionSend(Context context , LinkedHashMap< String , Object > map ,
                                    int msgId , long flag )
	{
		String param = "";
		if ( map != null )
		{
			JSONObject json = new JSONObject( map );
			param = json.toString( );
		}
		return sessionSend( context , param , msgId , flag );
	}
	
	private static long sessionSend(Context context , String param , int msgId )
	{
		return sessionSend( context , param , msgId , System.currentTimeMillis( ) );
	}
	
	private static long sessionSend(Context context , String param , int msgId , long flag )
	{
		TransportMessage msg = new TransportMessage( );
		msg.setMethodId( msgId );
		msg.setContentBody( param );
		try
		{
			return ConnectorManage.getInstance( context ).sendSessionMessage( msg , flag );
		}
		catch ( IOException e )
		{
			CommonFunction.log( TAG , e.getMessage( ) );
			return -2;
		}
	}
	
	/**
	 * 登录业务服
	 * 
	 * @param context
	 * @param key
	 * @return -1 发送失败
	 * @ID:81006
	 */
	public static long sessionLogin(Context context , String key )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "key", key);
		//消息推送功能要求检测手机设备的厂商类型以推送系统级别的消息
//		if(DeviceUtils.isMeizu()){
//			map.put( "modeltype", 1);
//		}else if(DeviceUtils.isXiaomi()){
//			map.put( "modeltype", 2);
//		}else if(DeviceUtils.isHuawei()){
//			map.put( "modeltype", 3);
//		}else{
//			map.put( "modeltype", 0);
//		}
		return sessionSend( context , map , MessageID.LOGINSESSION );
	}
	
	/**
	 * 退出session服务
	 * 
	 * @param context
	 * @return -1 发送失败
	 * @ID：81012
	 */
	public static long sessionLogout( Context context )
	{
		return sessionSend( context , "" , MessageID.SESSION_LOGOUT );
	}
	
	/**
	 * 发送确认私聊消息
	 * 
	 * @param context
	 * @param msgid
	 * @return -1 发送失败
	 * @ID:81030
	 */
	public static synchronized long sessionPrivateVilify(Context context , String msgid )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "msgid" , msgid );
		return sessionSend( context , map , MessageID.SESSION_PRIVATEVILIFY );
	}
	
	/**
	 * 告诉服务端，本人已读对方消息的最大记录ID
	 * 
	 * @param context
	 * @param userid
	 *            对方的UID
	 * @param msg
	 * @return -1 发送失败
	 * @ID：81022
	 */
	public static long sessionSendMyReadedMaxId(Context context , long userid , String msg )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "readuserid" , userid );
		map.put( "messageid" , msg );
		return sessionSend( context , map , MessageID.SESSION_CHAT_PERSION_READED );
	}
	
	/**
	 * 获取对方已读最大消息ID
	 * 
	 * @param context
	 * @param fuserid
	 * @return -1 发送失败
	 * @ID:81023
	 */
	public static long sessionGetFriendReadedMaxId(Context context , long fuserid )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "readuserid" , fuserid );
		return sessionSend( context , map , MessageID.SESSION_GET_CHAT_PERSION_READED );
	}
	
	/**
	 * 发送私聊消息
	 * 
	 * @param context
	 * @param flag
	 * @param fuserid
	 * @param mtype  定义的是（0-正常私聊消息，1-搭讪）
	 * @param type   {@link ChatRecordViewFactory}的类型
	 * @param attachment
	 * @param from {@link ChatFromType} 
	 * @param content
	 * @return -1 发送失败
	 * @ID:81010
	 */
	public static long sessionPrivateMsg(Context context , long flag , long fuserid , int mtype,
                                         String type , String attachment , int from, String content )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "rid" , fuserid );
		map.put( "mtype" , mtype );
		map.put( "type" , type );
		map.put( "attachment" , attachment );
		map.put( "from" , from );
		map.put( "content" , content );
		return sessionSend( context , map , MessageID.SESSION_SEND_PRIVATE_CHAT , flag );
	}
	
	/**
	 * 检查敏感字版本
	 * 
	 * @param context
	 * @param updatetime
	 * @return -1 发送失败
	 * @ID:81005
	 */
	public static long sessionKeywordVersion(Context context , long updatetime )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "updatetime" , updatetime );
		return sessionSend( context , map , MessageID.SEESION_KEYWORD );
	}
	
	/**
	 * 同步本地通讯录
	 * 
	 * @param context
	 * @param content
	 * @return -1 发送失败
	 * @ID：81014
	 */
	public static long sessionPushContact(Context context , String content )
	{
		return sessionSend( context , content , MessageID.SESSION_PUSH_CONTACT );
	}
	
	/**
	 * 标记全部私聊消息为已读（最近联系人）
	 * 
	 * @param context
	 * @param body
	 * @return -1 发送失败
	 * @ID:81036
	 */
	public static long sessionMarkAllNearContactRead(Context context , String body )
	{
		return sessionSend( context , body , MessageID.MY_NEAR_CONTACT_MARK_ALL_READ );
	}
	
	/**
	 * 发送已读粉丝数量
	 * 
	 * @param context
	 * @return -1 发送失败
	 * @ID:81028
	 */
	public static long sessionReadedFansCount( Context context )
	{
		return sessionSend( context , "" , MessageID.SESSION_READED_FANS );
	}
	
	/**
	 * 发送当前的经纬度信息
	 * 
	 * @param context
	 * @param lat
	 * @param lng
	 * @param address
	 * @return -1 发送失败
	 * @ID:81029
	 */
	public static long sessionUpdateLocation(Context context , int lat , int lng ,
                                             String address )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "address" , address );
		return sessionSend( context , map , MessageID.SESSION_UPDATE_LOCATION );
	}
	
	/**
	 * 发送场景
	 * 
	 * @param context
	 * @param rid
	 *            接受者id
	 * @param sceneid
	 *            场景id
	 * @return
	 */
	public static long sessionSendTheme(Context context , long rid , int sceneid )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "rid" , rid );
		map.put( "sceneid" , sceneid );
		return sessionSend( context , map , MessageID.SESSION_SEND_THEME );
	}
	
	/**
	 * 获取用户搭讪关系
	 * @param context
	 * @param userid 用户的id
	 * @return
	 */
	public static long seesionSendAccostRelation(Context context , long userid )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		return sessionSend( context , map , MessageID.SESSION_GET_ACCOST_RELATION );
	}
	
	/**
	 * 用户收到搭讪关系推送上报
	 * @param context
	 * @param userid 用户的id
	 * @return
	 */
	public static long seesionSendGetRelationReport(Context context , long userid )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		return sessionSend( context , map , MessageID.SESSION_REPORT_RECEIVE_ACCOST_RELATION );
	}
	
	/**
	 * 更新国家地区和语言
	 * @param context
	 * @return
	 */
	public static long sessionUpdateCountryAndLanguage( Context context )
	{	
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long updatetime = sp.getLong( SharedPreferenceUtil.UPDATE_COUNTRY_AND_LANGUAGE_TIME, 0L );
		map.put( "updatetime" , updatetime );
		return sessionSend( context , map , MessageID.SESSION_UPDATE_COUNTRY_AND_LANGUAGE );
	}
	
	
	
	
	/**
	 * 录音开始协议
	 */
	public static long seesionSendRecordingBegin(Context context , long rid, int type, long flag, int mtype, int from )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "rid" , rid );
		map.put( "type" , type );
		map.put( "flag" , flag );
		map.put( "mtype" , mtype );
		map.put( "from" , from );
		return sessionSend( context , map , MessageID.SESSION_PRIVATE_AUDIO_BEGIN );
	}
	
	/**
	 * 语音数据发送协议
	 */
	public static long seesionSendRecodingData(Context context , long flag , int rank, String data )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "rank" , rank );
		map.put( "data" , data );
		return sessionSend( context , map , MessageID.SESSION_PRIVATE_AUDIO_SEND );
	}
	
	/**
	 * 语音录制结束协议
	 */
	public static long seesionSendRecodingEnd(Context context , long flag , int rank, String content )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "rank" , rank );
		map.put( "content" , content );
		return sessionSend( context , map , MessageID.SESSION_PRIVATE_AUDIO_END );
	}
	
	/**
	 * 语音录制取消协议
	 */
	public static long seesionSendRecodingCancel(Context context , long flag )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		return sessionSend( context , map , MessageID.SESSION_GET_ACCOST_RELATION );
	}
	
	/**上报已读圈通知最大时间*/
	public static long seesionSendGroupNoticeLatestTime(Context context , long datetime )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "readtime" , datetime );
		return sessionSend( context , map , MessageID.SESSION_SEND_GROUP_NOTICE_LATEST_TIME );
	}

	/** 上报已接收的聊吧通知中最大时间 */
	public static long sessionSendChatBarNoticeLatestTime(Context context , long datetime)
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "readtime" , datetime );
		return sessionSend( context , map , MessageID.CHATBAR_SEND_NOTICE_MAXTIME );
	}

	/** 上报已接收的聊吧邀请函中最大时间 */
//	public static long sessionSendChatBarInvitationLatestTime(Context context , long datetime)
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "readtime" , datetime );
//		return sessionSend( context , map , MessageID.CHATBAR_SEND_INVITATION_MAXTIME );
//	}

	/**
	 * 发送验证码请求
	 *
	 * @param context
	 * @return -1 发送失败
	 * @ID:911110
	 */
	public static long sessionVrcCheck(Context context , String vrcCode )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "vrc" , vrcCode );
		return sessionSend( context , map , MessageID.VERIFY_JUMP_WEB_VIEW );
	}

	public static long getWorldMessageHistory(Context context, long timeStamp){
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("type", "1");
		params.put("ts", timeStamp);
		return sessionSend(context, params, MessageID.SESSION_CHAT_PRESONLA_WORLD_MESSAGE, timeStamp);
	}
	public static long getSkillMessageHistory(Context context, String groupId, long timeStamp){
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("type", "2");
		params.put("groupid", groupId);
		params.put("ts", timeStamp);
		return sessionSend(context, params, MessageID.SESSION_CHAT_PRESONLA_WORLD_MESSAGE, timeStamp);
	}


	/**
	 * 上传推送设备信息
	 * @param context
	 * @param token
	 * @param type  极光0，小米1，魅族2，华为3
	 * @return
	 */
	public static long uploadHuaweiToken(Context context, String token, String type){
		LinkedHashMap<String, Object> params = new LinkedHashMap<>();
		params.put("pushtype", type);
		params.put("pushtoken", token);
//		CommonFunction.toastMsg(context, "pushtype=" + type);
		return sessionSend( context , params , MessageID.UPLOAD_HUAWEI_TOKEN );
	}


}
