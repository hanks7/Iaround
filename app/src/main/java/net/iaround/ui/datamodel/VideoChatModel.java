
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.VideoChatWorker;
import net.iaround.im.proto.Iavchat;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.Me;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.im.UserTypeOne;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.friend.bean.VideoChatBean;
import net.iaround.ui.friend.bean.NewFansUserInfoBean;

import java.util.ArrayList;


/**
 * @Description: 视频聊天
 * @author tanzy
 * @date 2015-4-16
 */
public class VideoChatModel extends Model
{
	private static VideoChatModel model;

	private VideoChatModel( )
	{
		
	}
	
	public static VideoChatModel getInstance( )
	{
		if ( model == null )
			model = new VideoChatModel( );
		return model;
	}

	/**
	 * 视频会话产生数据
	 * @param context
	 * @param fuid  对方用户ID
	 * @param fname
	 * @param fnotename
	 * @param ficon
	 * @param fvipleave
	 * @param fsvip
	 * @param flat
	 * @param flng
	 * @param talkTime  通话时长
	 * @param state  通话状态
	 * @param roomid 发起会话用户ID
	 */
	public void insertData(Context context, int fuid, String fname, String fnotename, String ficon, int fvipleave, int fsvip, int flat, int flng, int talkTime, int state, int roomid, long ended,int fanchor) {
		if (state == Iavchat.STATE_CANCEL || state == Iavchat.STATE_REJECT || state == Iavchat.STATE_TIMEOUT || state == Iavchat.STATE_USER_BUSY || state == Iavchat.STATE_CALLER_CLOSE || state == Iavchat.STATE_CALLEE_CLOSE) {
			User fuser = new User();
			fuser.setUid(fuid);
			fuser.setNickname(fname);
			fuser.setNoteName(fnotename);
			fuser.setIcon(ficon);
			fuser.setSVip(fsvip);
			fuser.setViplevel(fvipleave);
			fuser.setLat(flat);
			fuser.setLng(flng);
			fuser.setUserType(fanchor);

			VideoChatBean bean = new VideoChatBean();
			bean.setDatetime(ended * 1000);
			bean.setTalkTime(TimeFormat.timeParse(talkTime));
			bean.setVideoState(state);

			String contentStr = GsonUtil.getInstance().getStringFromJsonObject(bean);

			Me me = Common.getInstance().loginUser;

			if (roomid == me.getUid()) {
				ChatRecord record = new ChatRecord();

				record.setId(-1); // 消息id
				record.setUid(me.getUid());
				record.setNickname(me.getNickname());
				record.setIcon(me.getIcon());
				record.setVip(me.getViplevel());
				record.setDatetime(ended * 1000);
				record.setType(Integer.toString(ChatRecordViewFactory.CHCAT_VIDEO));
				record.setStatus(ChatRecordStatus.ARRIVED); // TODO：发送中改为送达
				record.setContent(contentStr);
				record.setUpload(false);

				record.setfLat(me.getLat());
				record.setfLng(me.getLng());
				GeoData geo = LocationUtil.getCurrentGeo(BaseApplication.appContext);
				record.setDistance(LocationUtil.calculateDistance(me.getLng(), me.getLat(), geo.getLng(), geo.getLat()));
				ChatPersonalModel.getInstance().insertOneRecord(context, fuser, record, MessageBelongType.SEND, 1);
			} else {
				if (state == Iavchat.STATE_USER_BUSY) return;
				PrivateChatMessage message = new PrivateChatMessage();
				message.type = ChatRecordViewFactory.FRIEND_CHCAT_VIDEO;
				message.mtype = 0;
				message.content = contentStr;
				message.flag = TimeFormat.getCurrentTimeMillis();
				message.from = 0;
				message.relation = 0;//关系
				message.datetime = ended * 1000;
				UserTypeOne userTypeOne = new UserTypeOne();
				userTypeOne.convertUserToBaseUserInfor(fuser);
				message.user = userTypeOne;

				TransportMessage transportMessage = new TransportMessage();
				transportMessage.setContentBody(GsonUtil.getInstance().getStringFromJsonObject(message));
				transportMessage.setId(0);
				transportMessage.setContentLength(transportMessage.getContentBody().length());
				ChatPersonalModel.getInstance().insertOneRecord(context, "" + me.getUid(), transportMessage, ChatRecordStatus.NONE);
			}

			// 插入消息列表
			NewFansUserInfoBean fuserBean = new NewFansUserInfoBean();
			fuserBean.convertUserToBaseUserInfor(fuser);
			receiveData(context, fuid, GsonUtil.getInstance().getStringFromJsonObject(fuserBean), TimeFormat.getCurrentTimeMillis(), TimeFormat.timeParse(talkTime), state, me.getUid());
		}

	}



	/**
	 * 接收视频消息
	 * @param context
	 * @param fuid
	 * @param fuinfo
	 * @param datetime
	 * @param talkTime
	 * @param state
	 * @param muid
	 */
	public void receiveData(Context context, int fuid, String fuinfo, long datetime, String talkTime, int state, long muid)
	{
		
		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		db.insertOneRecord( fuid , fuinfo , datetime , talkTime , state , muid );
		if ( db != null )
			db.onClose( );
	}
	
	/** 获取视频会话信息 */
	public ArrayList< VideoChatBean > getVideoChatList(Context context , long muid )
	{
		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		ArrayList< VideoChatBean > beans = new ArrayList< VideoChatBean >( );
		Cursor cursor = db.selectPage( muid );
		beans = cursorToBean( cursor );
		
		if ( db != null )
			db.onClose( );
		if ( cursor != null )
			cursor.close( );
		return beans;
	}
	
	/** 获取某时间点前的视频会话信息 */
	public ArrayList< VideoChatBean > getVideoChatList(Context context , long datetime , long muid )
	{
		if ( datetime == 0 )
			return getVideoChatList( context , muid );

		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		ArrayList< VideoChatBean > beans = new ArrayList< VideoChatBean >( );
		Cursor cursor = db.selectPage( muid , datetime );
		beans = cursorToBean( cursor );
		
		if ( db != null )
			db.onClose( );
		return beans;
	}

	/** 获取视频会话 */
	public VideoChatBean getVideoChat(Context context, long fuid) {
		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		Cursor cursor = db.selectOneMessage(fuid, Common.getInstance().loginUser.getUid());

		VideoChatBean fansBean = new VideoChatBean();
		if (cursor != null) {

			if (cursor.moveToFirst()) {

				NewFansUserInfoBean uInfo = new NewFansUserInfoBean();
				String sUinfo = cursor
						.getString(cursor.getColumnIndex(VideoChatWorker.FUINFO));
				uInfo = GsonUtil.getInstance().getServerBean(sUinfo,
						NewFansUserInfoBean.class);
				fansBean.userinfo = uInfo;

				fansBean.talkTime = cursor.getString(cursor.getColumnIndex(VideoChatWorker.TALKTIME));
				fansBean.videoState = cursor
						.getInt(cursor.getColumnIndex(VideoChatWorker.STATE));
				fansBean.datetime = cursor
						.getLong(cursor.getColumnIndex(VideoChatWorker.DATETIME));
			}

			if (db != null)
				db.onClose();
		}
		return fansBean;
	}


	
	/** 将表结构转换成VideoChatBean */
	private ArrayList< VideoChatBean > cursorToBean(Cursor cursor )
	{
		ArrayList< VideoChatBean > beans = new ArrayList< VideoChatBean >( );
		if ( cursor != null )
		{
			for ( cursor.moveToFirst( ) ; !cursor.isAfterLast( ) ; cursor.moveToNext( ) )
			{
				VideoChatBean bean = new VideoChatBean( );
				String sUinfo = cursor
						.getString( cursor.getColumnIndex( VideoChatWorker.FUINFO ) );
				NewFansUserInfoBean uInfo = GsonUtil.getInstance( ).getServerBean( sUinfo ,
						NewFansUserInfoBean.class );
				bean.userinfo = uInfo;
				bean.talkTime = cursor.getString(cursor.getColumnIndex(VideoChatWorker.TALKTIME));
				bean.videoState = cursor
						.getInt(cursor.getColumnIndex(VideoChatWorker.STATE));
				bean.datetime = cursor
						.getLong( cursor.getColumnIndex( VideoChatWorker.DATETIME ) );
				beans.add( bean );
			}
		}
		return beans;
	}
	
	/** 删除一条视频会话信息 */
	public void deleteOneMessage(Context context , long fuid , long muid )
	{
		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		db.deleteOneRecord( fuid , muid );
		if ( db != null )
			db.onClose( );
	}

	
	/** 获取最新视频消息 */
	public VideoChatBean getLatestMessage(Context context , long muid )
	{
		ArrayList< VideoChatBean > beans = getVideoChatList( context , muid );
		if ( beans != null && beans.size( ) > 0 )
			return beans.get( 0 );
		else
			return null;
	}


	/** 删除用户所有消息 */
	public void deleteAllMessages(Context context , long muid )
	{
		VideoChatWorker db = DatabaseFactory.getVideoChatWorker( context );
		db.deleteUserAllRecord( muid );
		if ( db != null )
			db.onClose( );
	}
	
	
	
}
