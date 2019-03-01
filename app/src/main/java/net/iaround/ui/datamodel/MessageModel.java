
package net.iaround.ui.datamodel;

import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupContactWorker;
import net.iaround.database.GroupMessageWorker;
import net.iaround.database.MessageWorker;
import net.iaround.database.NearContactWorker;
import net.iaround.database.PersonalMessageWorker;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.chat.view.ChatRecordViewFactory;


/**
 * 消息列表数据模型
 */
public class MessageModel extends Model
{
	private static MessageModel messageModel;
	
	private MessageModel( ){}

    public static MessageModel getInstance( )
	{
		if ( messageModel == null )
		{
			messageModel = new MessageModel( );
		}
		return messageModel;
	}
	
	/**
	 * 读取通知消息数
	 * 
	 * @param uid
	 * @time 2011-9-19 下午02:16:03
	 * @author:linyg
	 */
	public void getNoticeCount(String uid , Context context )
	{
		MessageWorker db = DatabaseFactory.getMessageWorker( context );
		Common.getInstance( ).setNoticeCount(
				db.countMethodid( uid , MessageID.SESSION_NOTICE_MESSAGE ) );
	}
	
	/**
	 * 将某人的收到搭讪的未读数置零
	 * */
	public void clearReceiveAccostNoneRead(String muid , Context context )
	{
		NearContactWorker db = DatabaseFactory.getNearContactWorker( context );
		db.updateReceiveAccost( muid );
	}
	
	/**
	 * 删除某个人的在联系人列表的记录
	 * @param context
	 * @param friendUid
	 * @return
	 */
	public int deleteNearContactRecord(Context context, long friendUid )
	{
		String loginUserIdStr = String.valueOf(Common.getInstance().loginUser.getUid());
		String friendUidStr = String.valueOf(friendUid);
		NearContactWorker ncWorker = DatabaseFactory.getNearContactWorker( context );
		return ncWorker.deleteRecord( loginUserIdStr , friendUidStr );
	}
	
	/**
	 * 删除某人在联系列表以及所有聊天记录
	 * @param mUid
	 * @param fUid
	 * @param context
	 * @return
	 */
	public int deleteRecordWithPerson(String mUid , String fUid , Context context )
	{
		NearContactWorker ncWorker = DatabaseFactory.getNearContactWorker( context );
		PersonalMessageWorker pmWorker = DatabaseFactory.getChatMessageWorker( context );
		
		pmWorker.deleteMsg( mUid , fUid );
		return ncWorker.deleteRecord( mUid , fUid );
	}
	
	
	/**
	 * 删除搭讪游戏真心话大冒险的题目和答案,如果与对方已经无消息，则删除最近联系的人的入口
	 * @param context
	 * @param mUid
	 * @param fUid
	 */
	public void deleteAccostGameMessage(Context context, String mUid , String fUid)
	{
		PersonalMessageWorker pmWorker = DatabaseFactory.getChatMessageWorker( context );
		pmWorker.delteMsgByMessagesType( mUid , fUid , ChatRecordViewFactory.ACCOST_GAME_ANS );
		pmWorker.delteMsgByMessagesType( mUid , fUid , ChatRecordViewFactory.ACCOST_GAME_QUE );
		
		Cursor cursor  = pmWorker.onSelectPage( context , mUid , fUid , 0 , 10 );
		if(cursor != null && cursor.getCount( ) == 0)
		{
			NearContactWorker ncWorker = DatabaseFactory.getNearContactWorker( context );
			ncWorker.deleteRecord( mUid , fUid );
		}
	}
	
	/**
	 * 清空数据库所有聊天记录（私聊,不清除邂逅消息）
	 */
	public void deleteAllMessage(Context context, long uid)
	{
		NearContactWorker pcWorker = DatabaseFactory.getNearContactWorker( context );
		pcWorker.deleteAll( uid );
		
		PersonalMessageWorker pmWorker = DatabaseFactory.getChatMessageWorker( context );
		pmWorker.deleteAll( uid );
		
		GroupContactWorker gcWorker = DatabaseFactory.getGroupContactWorker( context );
		gcWorker.deleteAll( uid );
		
		GroupMessageWorker gmWorker = DatabaseFactory.getGroupMessageWorker( context );
		gmWorker.deleteAll(uid);
		
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		
		String MeetGameKey = SharedPreferenceUtil.USER_MEET_DATA + uid;
		if ( sp.has( MeetGameKey ) )
		{
			sp.remove( MeetGameKey );
		}
	}
	
	/**
	 * 释放资源
	 * 
	 * @time 2011-6-16 下午01:32:50
	 * @author:linyg
	 */
	public void reset( )
	{
		messageModel = null;
	}
}
