
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.NewFansWorker;
import net.iaround.model.im.NewFansListBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.friend.bean.NewFansBean;
import net.iaround.ui.friend.bean.NewFansUserInfoBean;

import java.util.ArrayList;


/**
 * @Description: 新增粉丝数据模型
 * @author tanzy
 * @date 2015-4-16
 */
public class NewFansModel extends Model
{
	private static NewFansModel model;
	
	private NewFansModel( )
	{
		
	}
	
	public static NewFansModel getInstance( )
	{
		if ( model == null )
			model = new NewFansModel( );
		return model;
	}
	
	/** 接收新增粉丝数据 */
	public void receiveData(Context context , NewFansListBean bean , long muid )
	{
		if ( bean == null || bean.fans == null || bean.fans.size( ) <= 0 )
		{
			CommonFunction.log( "sherlock" , "bean is null or fans size is 0" );
			return;
		}
		
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		ArrayList< NewFansBean > fans = bean.fans;
		for ( int i = 0 ; i < fans.size( ) ; i++ )
		{
			NewFansBean tmp = bean.fans.get( i );
			String fuinfo = GsonUtil.getInstance( ).getStringFromJsonObject( tmp.userinfo );
			Cursor cursor = db.selectOneMessage( muid , tmp.userinfo.userid );
			if ( cursor.getCount( ) > 0 )
			{
				db.updateOneRecord( tmp.userinfo.userid , fuinfo , tmp.datetime ,
						tmp.userinfo.relation , 1 , muid );
			}
			else
			{
				db.insertOneRecord( tmp.userinfo.userid , fuinfo , tmp.datetime ,
						tmp.userinfo.relation , 1 , muid );
			}
			if ( cursor != null )
				cursor.close( );
		}
		if ( db != null )
			db.onClose( );
		
		SocketSessionProtocol.sessionReadedFansCount( context );
		
	}
	
	/** 获取新增粉丝信息 */
	public ArrayList< NewFansBean > getNewFansList(Context context , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		ArrayList< NewFansBean > beans = new ArrayList< NewFansBean >( );
		Cursor cursor = db.selectPage( muid );
		beans = cursorToBean( cursor );
		
		if ( db != null )
			db.onClose( );
		if ( cursor != null )
			cursor.close( );
		return beans;
	}
	
	/** 获取某时间点前的新增粉丝信息 */
	public ArrayList< NewFansBean > getNewFansList(Context context , long datetime , long muid )
	{
		if ( datetime == 0 )
			return getNewFansList( context , muid );
		
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		ArrayList< NewFansBean > beans = new ArrayList< NewFansBean >( );
		Cursor cursor = db.selectPage( muid , datetime );
		beans = cursorToBean( cursor );
		
		if ( db != null )
			db.onClose( );
		if(cursor!=null){
			cursor.close();
		}
		return beans;
	}

	/** 获取某个粉丝信息 */
	public NewFansBean getFans(Context context, long fuid) {
		NewFansWorker db = DatabaseFactory.getNewFriendWorker(context);
		Cursor cursor = db.selectOneMessage(fuid, Common.getInstance().loginUser.getUid());

		NewFansBean fansBean = new NewFansBean();
		if (cursor != null) {

			if (cursor.moveToFirst()) {

				NewFansUserInfoBean uInfo = new NewFansUserInfoBean();
				String sUinfo = cursor
						.getString(cursor.getColumnIndex(NewFansWorker.FUINFO));
				uInfo = GsonUtil.getInstance().getServerBean(sUinfo,
						NewFansUserInfoBean.class);
				fansBean.userinfo = uInfo;
				uInfo.relation = cursor
						.getInt(cursor.getColumnIndex(NewFansWorker.RELATION));
				fansBean.datetime = cursor
						.getLong(cursor.getColumnIndex(NewFansWorker.DATETIME));
			}

			if (db != null)
				db.onClose();
		}
		return fansBean;
	}


	
	/** 将表结构转换成NewFansBean */
	private ArrayList< NewFansBean > cursorToBean(Cursor cursor )
	{
		ArrayList< NewFansBean > beans = new ArrayList< NewFansBean >( );
		if ( cursor != null )
		{
			for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( ) ; cursor.moveToNext( ) )
			{
				NewFansBean bean = new NewFansBean( );
				NewFansUserInfoBean uInfo = new NewFansUserInfoBean( );
				String sUinfo = cursor
						.getString( cursor.getColumnIndex( NewFansWorker.FUINFO ) );
				uInfo = GsonUtil.getInstance( ).getServerBean( sUinfo ,
						NewFansUserInfoBean.class );
				bean.userinfo = uInfo;
				uInfo.relation = cursor
						.getInt( cursor.getColumnIndex( NewFansWorker.RELATION ) );
				bean.datetime = cursor
						.getLong( cursor.getColumnIndex( NewFansWorker.DATETIME ) );
				beans.add( bean );
			}
		}
		return beans;
	}
	
	/** 删除一条粉丝信息 */
	public void deleteOneMessage(Context context , long fuid , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		db.deleteOneRecord( fuid , muid );
		if ( db != null )
			db.onClose( );
	}
	
	/** 更新数据库内粉丝的关系 */
	public void updateRelation(Context context , long fuid , int relation , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		db.updateOneRelation( fuid , relation , muid );
		if ( db != null )
			db.onClose( );
	}
	
	/** 获取最新粉丝消息 */
	public NewFansBean getLatestMessage(Context context , long muid )
	{
		ArrayList< NewFansBean > beans = getNewFansList( context , muid );
		if ( beans != null && beans.size( ) > 0 )
			return beans.get( 0 );
		else
			return null;
	}
	
	/** 获取未读数 */
	public int getUnreadCount(Context context , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		Cursor cursor = db.getUnreadMsgs( muid );
		int count = cursor.getCount( );
		if ( db != null )
			db.onClose( );
		if ( cursor != null )
			cursor.close( );
		return count;
	}
	
	/** 标记所有消息为已读 */
	public void setAllRead(Context context , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		db.markAllRead( muid );
		if ( db != null )
			db.onClose( );
	}
	
	/** 删除用户所有消息 */
	public void deleteAllMessages(Context context , long muid )
	{
		NewFansWorker db = DatabaseFactory.getNewFriendWorker( context );
		db.deleteUserAllRecord( muid );
		if ( db != null )
			db.onClose( );
	}
	
	
	
}
