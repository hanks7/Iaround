
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.conf.MessageID;
import net.iaround.connector.ConnectorManage;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.CommonFunction;

import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;


/**
 * 圈子服务器 socket协议接口文档
 * 
 * @author linyg
 * 
 */
public class SocketGroupProtocol extends SocketProtocol
{
	private final static String TAG = SocketGroupProtocol.class.getName( );
	
	private static long groupSend(Context context , LinkedHashMap< String , Object > map ,
                                  int msgId )
	{
		String content = "";
		if ( map != null )
		{
			JSONObject json = new JSONObject( map );
			content = json.toString( );
		}
		return groupSend( context , content , msgId );
	}
	
	private static long groupSend(Context context , LinkedHashMap< String , Object > map ,
                                  int msgId , long flag )
	{
		String content = "";
		if ( map != null )
		{
			JSONObject json = new JSONObject( map );
			content = json.toString( );
		}
		return groupSend( context , content , msgId , flag );
	}
	
	private static long groupSend(Context context , String content , int msgId )
	{
		return groupSend( context , content , msgId , System.currentTimeMillis( ) );
	}
	
	private static long groupSend(Context context , String content , int msgId , long flag )
	{
		TransportMessage msg = new TransportMessage( );
		msg.setMethodId( msgId );
		msg.setContentBody( content );
		try
		{
			return ConnectorManage.getInstance( context ).sendGroupMessage( msg , flag );
		}
		catch ( IOException e )
		{
			CommonFunction.log( TAG , e.getMessage( ) );
			return -1;
		}
	}
	
	
	/**
	 * 登录圈组服务器
	 * 
	 * @param context
	 * @param key
	 * @return -1 发送失败
	 * @ID:71001
	 */
	public static long groupLogin(Context context , String key )
	{
        CommonFunction.log(TAG,"groupLogin() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "key" , key );
		return groupSend( context , map , MessageID.GROUP_LOGIN );
	}
	
	/**
	 * 获取我的圈子列表
	 * 
	 * @param context
	 * @return -1 发送失败
	 * @ID:71002
	 */
	public static long groupList( Context context )
	{
        CommonFunction.log(TAG,"groupList() into");
		return groupSend( context , "" , MessageID.GROUP_GET_LIST );
	}
	
	/**
	 * 进入群动作(当用户正在群的聊天页面时,网络断开,导致进入群的动作)
	 * 
	 * @param context
	 * @param groupid
	 * @ID:71004
	 */
	public static long groupComeIn(Context context , String groupid )
	{
		CommonFunction.log(TAG,"groupComeIn() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "chatroom" , 1 );
		return groupSend( context , map , MessageID.GROUP_COME_IN );
	}
	
	/**
	 * 退出group服务
	 * 
	 * @return -1 发送失败
	 * @ID:71012
	 */
	public static long groupLogout( Context context )
	{
		CommonFunction.log(TAG,"groupLogout() into");
		return groupSend( context , "" , MessageID.GROUP_LOGOUT );
	}
	
	/**
	 * 离开群组
	 * 
	 * @param groupId
	 *            群ID
	 * @param userId
	 *            用户ID
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long groupLeave(Context context , String groupId , long userId )
	{
		CommonFunction.log(TAG,"groupLeave() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
		return groupSend( context , map , MessageID.GROUP_LEAVE );
	}
	
	/**
	 * 获取群用户列表
	 * 
	 * @param groupId
	 *            群ID
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页的数目
	 * @return -1 发送失败
	 * @ID:71005
	 */
	public static long groupMemberList(Context context , String groupId , int pageNo ,
                                       int pageSize )
	{
        CommonFunction.log(TAG,"groupMemberList() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "pageno" , pageNo );
		map.put( "pagesize" , pageSize );
		return groupSend( context , map , MessageID.GROUP_LIST_MEMBER );
	}
	
	/**
	 * 踢人
	 * 
	 * @param context
	 * @param groupId
	 * @param userId
	 * @return -1 发送失败
	 * @ID:71009
	 */
	public static long groupKick(Context context , String groupId , long userId )
	{
        CommonFunction.log(TAG,"groupKick() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
		return groupSend( context , map , MessageID.GROUP_KICK );
	}
	
	/**
	 * 发送房间消息
	 * 
	 * @param context
	 * @param flag
	 * @param type {@link net.iaround.ui.chat.view.ChatRecordViewFactory}的类型
	 * @param attachment
	 * @param content
	 * @param groupid
	 * @param reply
	 * @return -1 发送失败
	 * @ID:71008
	 */
	public static long groupSendMsg(Context context , long flag , String type ,
                                    String attachment , String content , String groupid, String reply )
	{
        CommonFunction.log(TAG,"groupSendMsg() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "type" , type );
		map.put( "attachment" , attachment );
		map.put( "content" , content );
		map.put( "groupid" , groupid );
		map.put( "reply", reply);
		return groupSend( context , map , MessageID.GROUP_SEND_MESSAGE , flag );
	}
	
	/**
	 * 告诉服务端，本人已收到圈子的最大id
	 * @return -1 发送失败
	 * @ID：71028
	 */
	public static long groupSendGroupReceiveMaxId(Context context , long msgid , long groupid )
	{
        CommonFunction.log(TAG,"groupSendGroupReceiveMaxId() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "msgid" , msgid );
		map.put( "groupid" , groupid );
		return groupSend( context , map , MessageID.GROUP_REPORT_RECEIVE_MAX_ID );
	}
	
	
	
	
	/**
	 * 录音开始协议
	 * @return -1 发送失败
	 * @ID：71022
	 */
	public static long groupSendRecodingBegin(Context context , long groupid , int type, long flag )
	{
        CommonFunction.log(TAG,"groupSendRecodingBegin() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "type" , type );
		map.put( "flag" , flag );
		return groupSend( context , map , MessageID.SESSION_GROUP_AUDIO_BEGIN );
	}
	
	/**
	 * 语音数据发送协议
	 * @return -1 发送失败
	 * @ID：71028
	 */
	public static long groupSendRecodingData(Context context , long flag , int rank, String data )
	{
        CommonFunction.log(TAG,"groupSendRecodingData() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "rank" , rank );
		map.put( "data" , data );
		return groupSend( context , map , MessageID.SESSION_GROUP_AUDIO_SEND );
	}
	
	/**
	 *  语音录制结束协议
	 * @return -1 发送失败
	 * @ID：71024
	 */
	public static long groupSendRecodingEnd(Context context , long flag , int rank, String content )
	{
        CommonFunction.log(TAG,"groupSendRecodingEnd() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		map.put( "rank" , rank );
		map.put( "content" , content );
		return groupSend( context , map , MessageID.SESSION_GROUP_AUDIO_END );
	}
	
	/**
	 *  语音录制取消协议
	 * @return -1 发送失败
	 * @ID：71025
	 */
	public static long groupSendRecodingCancel(Context context , long flag )
	{
        CommonFunction.log(TAG,"groupSendRecodingCancel() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "flag" , flag );
		return groupSend( context , map , MessageID.SESSION_GROUP_AUDIO_CANCEL );
	}
	
	
	/**
	 *  获取圈子历史消息
	 * @return -1 发送失败
	 * @ID：71029
	 */
	public static long groupGetHistoryMessages(Context context , long groupid, long maxmsgid, int amount)
	{
        CommonFunction.log(TAG,"groupGetHistoryMessages() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "maxmsgid" ,maxmsgid );
		map.put( "amount" , amount );
		return groupSend( context , map , MessageID.GROUP_GET_HISTORY_MESSAGES );
	}

	/**
	 * 上麦
	 *
	 * @param groupId
	 *            群ID
	 * @param userId
	 *            用户ID
	 * @param position
	 *            位置
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long onMic(Context context , String groupId , long userId ,int position)
	{
        CommonFunction.log(TAG,"onMic() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
		map.put( "slot" , position );
		return groupSend( context , map , MessageID.SESSION_GROUP_ON_MIC_SEND );
	}

	/**
	 * 下麦
	 *
	 * @param groupId
	 *            群ID
	 * @param userId
	 *            用户ID
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long offMic(Context context , String groupId , long userId)
	{
        CommonFunction.log(TAG,"offMic() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
		return groupSend( context , map , MessageID.SESSION_GROUP_OFF_MIC_SEND );
	}

	/**
	 * 反馈上麦失败
	 *
	 * @param groupId
	 *            群ID
	 * @param managerId
	 *            用户ID
	 * @param position
	 *            位置
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long onMicError(Context context , String groupId , long managerId ,int position)
	{
        CommonFunction.log(TAG,"onMicError() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "managerid" , managerId );
		map.put( "slot" , position );
		return groupSend( context , map , MessageID.SESSION_GROUP_ON_MIC_ERROR_SEND );
	}


	/**
	 * 反馈上麦成功
	 *
	 * @param groupId
	 *            群ID
	 * @param managerId
	 *            用户ID
	 * @param position
	 *            位置
	 * @param pushflows
	 * 			  所有流
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long onMicSuccess(Context context , String groupId , long managerId ,int position,String pushflows)
	{
		CommonFunction.log(TAG,"onMicSuccess() into, groupId="+groupId + ", slot=" + position + ", pushflows=" + pushflows);
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "slot" , position );
		map.put( "pushflows" , pushflows );
		return groupSend( context , map , MessageID.SESSION_GROUP_ON_MIC_SUCCESS_SEND );
	}
	/**
	 * 反馈下麦成功
	 *
	 * @param groupId
	 *            群ID
	 * @param managerId  废弃
	 *            用户ID
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long offMicSuccess(Context context , String groupId , long managerId )
	{
        CommonFunction.log(TAG,"offMicSuccess() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupSend( context , map , MessageID.SESSION_GROUP_OFF_MIC_SUCCESS_SEND );
	}
/**
	 * 反馈下麦成功
	 *
	 * @param groupId
	 *            群ID
	 *            用户ID
	 * @return -1 发送失败
	 * @ID:71011
	 */
	public static long backChatRoom(Context context , String groupId )
	{
		CommonFunction.log(TAG,"backChatRoom() into");
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupSend( context , map , MessageID.SESSION_GROUP_BACK_CHAT_ROOM_SEND );
	}

}
