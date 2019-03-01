
package net.iaround.ui.dynamic;


import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.RemoteViews;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.Me;
import net.iaround.model.im.PrivateChatMessage;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.NotifyUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.im.ChatGameActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.MenuBadgeHandle;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;


/**
 * 显示和取消通知栏的推送
 * 
 * @author tanzy
 * */
public class NotificationFunction
{
	private static NotificationFunction instant;
	
	static final int KANKA_PUSH_ID = 10;
	static final int DYNAMIC_PUSH_ID = 20;
	static final int CHAT_PUSH_ID = 30;
	
	NotificationManager notificationManager = null;
	private Context mContext;
	private long muid;
	private SharedPreferenceUtil sp;
	
	private TransportMessage mMsg;
	private long msgID;
	
	private Intent intent;// 目标页面
	private String rollTitle;// 滚动标题
	private String title;// 通知标题
	private String content;// 通知正文
	private int smallIconID;// 小图标id
	private int iconID;// 通知正文图标id
	
	private int chatPersonalCount;// 私聊未读数量
	private int accostCount;// 收到搭讪数量
	private int chatSender;// 私聊发送者数量
	
	private boolean accostONOFF;
	private boolean dynamicONOFF;
	private boolean privateONOFF;
	private boolean kanlaONOFF;
	private boolean hideChatDetailONOFF;

	private NotifyUtil currentNotify;
	
	private NotificationFunction(Context context )
	{
		this.mContext = context.getApplicationContext();
		this.notificationManager = (NotificationManager) mContext
				.getSystemService( Context.NOTIFICATION_SERVICE );
		this.sp = SharedPreferenceUtil.getInstance( mContext );
	}
	
	public static NotificationFunction getInstatant(Context context )
	{
		if ( instant == null )
		{
			instant = new NotificationFunction( context );
		}
		return instant;
	}
	
	/**
	 * 判断并显示通知栏推送
	 * */
	public void showNotification( TransportMessage msg )
	{
		//gh 私聊 获取消息和发送者数量
		Object[ ] objects = MenuBadgeHandle.getInstance( mContext ).countNewMessageNum( mContext );
		int accostAll = (Integer)objects[ 0 ];

		//gh 动态
		DynamicNewNumberBean bean = DynamicModel.getInstent( ).getNewNumBean( );

//		//gh 侃啦
//		PostbarUnreadMessagesBean postbarBean = PostbarModel.getInstance( )
//				.getUnreadMessages( );

//		int allCount = accostAll + bean.getCommentNum( ) + bean.getLikenum( ) + postbarBean.likenum
//				+ postbarBean.num;
		int allCount = accostAll + bean.getCommentNum( ) + bean.getLikenum( );
		//gh 设置未读消息数量
		ShortcutBadger.applyCount(BaseApplication.appContext, allCount);//gh 暂时注释掉


		PowerManager pm = (PowerManager) BaseApplication.appContext.getSystemService(Context.POWER_SERVICE);
		boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。

		if (isScreenOn && CommonFunction.isTopActivity( mContext ) )
		{// 如果是在前台运行则不推送
			return;
		}
		this.mMsg = msg;
		this.muid = Common.getInstance( ).loginUser.getUid( );
		this.msgID = msg.getMethodId( );
		accostONOFF = sp.getBoolean( SharedPreferenceUtil.ACCOST_NEWS_NOTIFY + muid , false );
		privateONOFF = sp.getBoolean( SharedPreferenceUtil.PRIVATE_NEWS_NOTIFY + muid , true );

		if ( sp.contains( SharedPreferenceUtil.DYNAMIC_NEWS_NOTIFY + muid ) )
			dynamicONOFF = sp.getBoolean( SharedPreferenceUtil.DYNAMIC_NEWS_NOTIFY + muid ,
					true );
		else
			dynamicONOFF = sp.getBoolean( SharedPreferenceUtil.REC_COMMENTS + muid , true );
		
		kanlaONOFF = sp.getBoolean( SharedPreferenceUtil.KANLA_REPLY_NOTIC + muid , true );
		hideChatDetailONOFF = sp.getBoolean( SharedPreferenceUtil.HIDE_CHAT_DETAIL + muid ,
				false );
		
		if ( inDndTime( ) )
		{// 如果在免打扰时间内则不推送
			return;
		}
		if ( !canNotifyByID( ) )
		{// 根据id判断是否需要显示推送
			return;
		}
		
//		intent = new Intent( mContext , NotificationJumpMainActivity.class );
//		intent.putExtra( "msgID" , msgID );
//		intent.addCategory( Intent.CATEGORY_LAUNCHER );
		smallIconID = R.drawable.icon;
		iconID = R.drawable.icon;
		
		
		if ( msgID == MessageID.SESSION_PRIVATE_CHAT )
		{// 私聊
			readyChatNotify( );
		}
		else if ( msgID == MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC )
		{// 动态
			readyDynamicNotify( );

		}
		else if ( msgID == MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES )
		{// 侃啦
//			readyKanlaNotify( );//gh
		}
		
		// 有一项为空则不显示推送
		if ( intent == null || CommonFunction.isEmptyOrNullStr( rollTitle )
				|| CommonFunction.isEmptyOrNullStr( title )
				|| CommonFunction.isEmptyOrNullStr( content ) || smallIconID == 0
				|| iconID == 0 )
		{
			CommonFunction
					.log( "sherlock" ,
							"notification error intent : rollTitle : title : content : smallID : iconID == " ,
							intent , rollTitle , title , content , smallIconID , iconID );
			return;
		}
		
		try
		{
//			if ( notificationManager == null )
//			{
//				notificationManager = ( NotificationManager ) mContext
//						.getSystemService( Context.NOTIFICATION_SERVICE );
//			}
//			Notification notification;
//			notification = new Notification( smallIconID , rollTitle ,
//					System.currentTimeMillis( ) );
//			notification.defaults = Notification.DEFAULT_SOUND;
			
			PendingIntent pi = PendingIntent.getActivity( mContext , 0 , intent ,
					PendingIntent.FLAG_UPDATE_CURRENT );

//			notification.contentView = getNotificationContentView( mContext , content , title ,
//					iconID );
//			notification.contentIntent = pi;
//			notification.flags = Notification.FLAG_AUTO_CANCEL;
			
			// 动态推送和私聊推送分两个通知显示
			if ( msgID == MessageID.SESSION_PRIVATE_CHAT )
			{

				NotifyUtil notify1 = new NotifyUtil(mContext, 1);
				notify1.notify_normal_singline(pi, iconID, rollTitle, title, content, true, true, false);
				currentNotify = notify1;
//				notificationManager.cancel( CHAT_PUSH_ID );
//				notificationManager.notify( CHAT_PUSH_ID , notification );
			}
			else if ( msgID == MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC )
			{
				NotifyUtil notify2 = new NotifyUtil(mContext, 2);
				notify2.notify_normal_singline(pi, iconID, rollTitle, title, content, true, true, false);
				currentNotify = notify2;
//				notificationManager.cancel( DYNAMIC_PUSH_ID );
//				notificationManager.notify( DYNAMIC_PUSH_ID , notification );
			}
			else if ( msgID == MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES )
			{
				NotifyUtil notify3 = new NotifyUtil(mContext, 3);
				notify3.notify_normal_singline(pi, iconID, rollTitle, title, content, true, true, false);
				currentNotify = notify3;
//				notificationManager.cancel( KANKA_PUSH_ID );
//				notificationManager.notify( KANKA_PUSH_ID , notification );
			}
		}
		catch ( Exception e )
		{
			CommonFunction.log( e );
		}

	}
	
	/** 判断免打扰模式是否开启且是否在免打扰时间内 */
	private boolean inDndTime( )
	{
		boolean in = false;
		// 判断免打扰是否开启
		boolean dndOn = sp.getBoolean( SharedPreferenceUtil.DND_SETTING + muid );
		if ( !dndOn )
			return false;
		
		SimpleDateFormat formatter = new SimpleDateFormat( "Hmm" );
		Date currentTime = new Date( );
		String dateString = formatter.format( currentTime );
		int nowTimeInt = Integer.parseInt( dateString );
		int startTime = sp.getInt( SharedPreferenceUtil.REC_START_TIME + muid ) * 100;
		int endTime = sp.getInt( SharedPreferenceUtil.REC_END_TIME + muid ) * 100;
		if ( startTime < endTime )
		{// 当时间段不跨日
            in = nowTimeInt > startTime && nowTimeInt < endTime;
		}
		else if ( startTime > endTime )
		{// 当时间段跨日
            in = nowTimeInt > startTime || nowTimeInt < endTime;
		}
		else
		{// 当开始结束时间一样时，则不显示推送
			in = true;
		}
		return in;
	}
	
	/** 根据id判断该消息是否需要显示推送 */
	private boolean canNotifyByID( )
	{
		boolean can = false;
		if ( msgID == MessageID.SESSION_PRIVATE_CHAT )
		{// 私聊
			PrivateChatMessage bean = JSON.parseObject(mMsg.getContentBody( ),PrivateChatMessage.class);

			if ( bean.mtype == 0 )
				can = privateONOFF;// 普通私聊都显示
			else if ( bean.mtype == 1 )
				can = accostONOFF;// 搭讪时查看是否开启搭讪提醒开关
		}
		else if ( msgID == MessageID.SESSION_PUSH_USER_UNREAD_DYNAMIC )
		{// 动态
			can = dynamicONOFF;
		}
		else if ( msgID == MessageID.SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES )
		{// 侃啦
			can = kanlaONOFF;
		}
		return can;
	}
	
	private void readyChatNotify( )
	{
		// 获取消息和发送者数量
		getMsgAndSenderCount( );
		
		PrivateChatMessage bean = JSON.parseObject(mMsg.getContentBody( ),PrivateChatMessage.class);
		
		// 获取滚动标题
		if ( bean.mtype == 0 )
		{// 正常私聊
			rollTitle = rollTitlePrivateChat( bean );
			intent = new Intent(mContext, ChatPersonal.class);
			intent.putExtra("fuid", bean.user.userid);
			intent.putExtra("fnickname", bean.user.nickname);
			intent.putExtra("fnotename", bean.user.notename);
			intent.putExtra("ficon", bean.user.getIcon());
			intent.putExtra("fvip", bean.user.viplevel);
			intent.putExtra("fsvip", bean.user.vip);
			intent.putExtra("sex", bean.user.getGender().equals("m") ? 1 : 0);
			intent.putExtra("age", bean.user.age);
			intent.putExtra("lat", bean.user.lat);
			intent.putExtra("lng", bean.user.lng);
			intent.putExtra("relation", bean.user.relation);
			intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//			CloseAllActivity.getInstance().closeTarget(ChatPersonal.class);
		}
		else if ( bean.mtype == 1 )
		{// 收到搭讪
			rollTitle = mContext.getString( R.string.receive_one_accost );
			intent = new Intent(mContext, ChatGameActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			CloseAllActivity.getInstance().closeTarget(ChatGameActivity.class);
		}
		
		// 获取下拉标题
		if ( accostONOFF && accostCount > 0 )
			title = mContext.getString( R.string.notice_n , chatPersonalCount + accostCount );
		else
			title = mContext.getString( R.string.notice_n , chatPersonalCount );
		
		// 获取下拉正文
		String tmp = mContext.getString( R.string.receive_n_new_message , 1 );
		if ( chatPersonalCount > 0 )
		{
			if ( accostCount > 0 && accostONOFF )
			{// 有私聊也有搭讪且搭讪开关为开启——N条新消息，M条新搭讪
				tmp = mContext.getString( R.string.n_new_chat , chatPersonalCount );
				if ( CommonFunction.getLang( mContext ) == 1 )
					tmp += ",";
				else
					tmp += "、";
				tmp += mContext.getString( R.string.n_new_accost , accostCount );
			}
			else
			{// 只有私聊消息
				if ( chatPersonalCount == 1 )
				{
					if ( hideChatDetailONOFF )
					{// 只有一条私聊且隐藏私聊正文——收到1条新消息
						tmp = mContext.getString( R.string.receive_n_new_message , 1 );
					}
					else
					{// 只有一条私聊且显示私聊正文——xxx：消息内容
						tmp = getChatDetail( bean );
					}
				}
				else if ( chatPersonalCount > 1 )
				{
					if ( chatSender == 1 )
					{// 一个人发来多条私聊——xxx发来N条消息
						tmp = mContext.getString( R.string.send_you_message ,
								replaceFaceToEmotion( bean.user.getNickname( ) ) ,
								chatPersonalCount );
					}
					else if ( chatSender > 1 )
					{// 多个人发来多条私聊——N个联系人发来M条消息
						tmp = mContext.getString( R.string.x_send_you_message , chatSender ,
								chatPersonalCount );
					}
				}
			}
		}
		else if ( accostCount > 0 && accostONOFF )
		{// 只有搭讪消息且搭讪开关为开启——N条新搭讪
			tmp = mContext.getString( R.string.n_new_accost , accostCount );
		}
		content = tmp;
		
	}
	
	private void readyDynamicNotify( )
	{
		DynamicNewNumberBean bean = GsonUtil.getInstance( ).getServerBean(
				mMsg.getContentBody( ) , DynamicNewNumberBean.class );
		intent = new Intent(mContext, DynamicMessagesActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		
		StringBuffer sb = new StringBuffer( );
		String name = "";
		// 获取滚动标题
		if ( bean.msg != null )
		{
			name = replaceFaceToEmotion( bean.msg.user.nickname );
			sb.append( name );
			sb.append( mContext.getString( R.string.they_comply_your_dynamic ) );
		}
		else
		{// 当第一个动态回复为赞时，msg为空导致空指针
			sb.append( mContext.getString( R.string.you_get_n_feed_comment ,
					bean.getCommentNum( ) + bean.getLikenum( ) ) );
		}
		rollTitle = sb.toString( );
		
		// 获取下拉标题
		title = mContext.getString( R.string.dynamic_n ,
				bean.getCommentNum( ) + bean.getLikenum( ) );
		
		// 获取下拉正文
		content = mContext.getString( R.string.your_dynamic_rec_n_reply , bean.getCommentNum( )
				+ bean.getLikenum( ) );
	}
	
//	private void readyKanlaNotify( )
//	{
//		PostbarUnreadMessagesBean bean = GsonUtil.getInstance( ).getServerBean(
//				mMsg.getContentBody( ) , PostbarUnreadMessagesBean.class );
//
//		// 获取滚动标题
//		StringBuffer sb = new StringBuffer( );
//		if ( bean.user != null )
//		{
//			String name = replaceFaceToEmotion( bean.user.nickname );
//			sb.append( name );
//			sb.append( mContext.getString( R.string.they_comply_your_kanla ) );
//		}
//		else
//		{
//			sb.append( mContext.getString( R.string.your_kanla_rec_n_reply , bean.likenum
//					+ bean.num ) );
//		}
//
//		rollTitle = sb.toString( );
//
//		// 获取下拉标题
//		title = mContext.getString( R.string.kanla_n , bean.num + bean.likenum );
//
//		// 获取下拉正文
//		content = mContext.getString( R.string.your_kanla_rec_n_reply , bean.num
//				+ bean.likenum );
//
//	}
	
	
	/**
	 * 获取消息数和发送者数
	 * */
	private void getMsgAndSenderCount( )
	{
		chatPersonalCount = ChatPersonalModel.getInstance( ).countNoRead( mContext ,
				String.valueOf( muid ) );
		accostCount = ChatPersonalModel.getInstance( ).getReceiveAccostCount( mContext ,
				String.valueOf( muid ) );
		chatSender = ChatPersonalModel.getInstance( ).countNoReadSender( mContext ,
				String.valueOf( muid ) );
	}
	
	/**
	 * 生成推送布局
	 * */
	public static RemoteViews getNotificationContentView(Context context , String content ,
                                                         String title , int icon )
	{
		Me me = Common.getInstance( ).loginUser;
		RemoteViews rv;

		rv = new RemoteViews( context.getPackageName( ) ,
				R.layout.notification_view );

		rv.setTextViewText( R.id.tvTitle , FaceManager.getInstance( context )
				.parseIconForString( context, title, 20 ,null ) );
		
		if ( icon != 0 )
		{
			rv.setImageViewResource( R.id.ivHead , icon );
		}
		else
		{
			rv.setImageViewResource( R.id.ivHead , R.drawable.default_face_big );
		}
		//gh 不对角色进行区分
//		rv.setImageViewResource( R.id.ivVip , me.isVip( ) ? R.drawable.vip : 0 );
		rv.setTextViewText( R.id.tvContent , content );
		return rv;
	}
	
	/**
	 * 正常私聊滚动标题
	 * */
	private String rollTitlePrivateChat(PrivateChatMessage bean )
	{
		StringBuffer sb = new StringBuffer( );
		if ( hideChatDetailONOFF )
		{// 隐藏私聊正文
			sb.append( mContext.getString( R.string.receive_n_new_message , 1 ) );
		}
		else
		{// 显示私聊正文
			sb.append( getChatDetail( bean ) );
		}
		
		return sb.toString( );
	}
	
	/**
	 * 获取私聊正文内容
	 * */
	private String getChatDetail(PrivateChatMessage bean )
	{
		StringBuffer sb = new StringBuffer( );
		sb.append( replaceFaceToEmotion( bean.user.getNickname( ) ) );
		switch ( bean.type )
		{
			case ChatMessageType.TEXT :
			{// 文本信息
			
				sb.append( ":" );
				String message = replaceFaceToEmotion( bean.content.toString( ) );
				
				if ( message.length( ) > 20 )
				{
					sb.append( message.substring( 0 , 19 ).trim( ) );
					sb.append( "..." );
				}
				else
				{
					sb.append( message );
				}
			}
				break;
			case ChatMessageType.IMAGE :
			{// 图片
				sb.append( mContext.getString( R.string.sent_you_photo ) );
			}
				break;
			case ChatMessageType.SOUND :
			{// 语音
				sb.append( mContext.getString( R.string.sent_you_voice ) );
			}
				break;
			case ChatMessageType.VIDEO :
			{// 录像
				sb.append( mContext.getString( R.string.sent_you_video ) );
			}
				break;
			case ChatMessageType.LOCATION :
			{// 地理位置
				sb.append( mContext.getString( R.string.sent_you_location ) );
			}
				break;
			case ChatMessageType.GIFE_REMIND :
			{// 礼物
				sb.append( mContext.getString( R.string.sent_you_gift ) );
			}
				break;
			case ChatMessageType.MEET_GIFT :
			{// 约会场景礼物
				sb.append( mContext.getString( R.string.sent_you_meet_gift ) );
			}
				break;
			case ChatMessageType.FACE :
			{// 表情贴图
				sb.append( mContext.getString( R.string.sent_you_sticker ) );
			}
				break;
			case ChatMessageType.SHARE :
			{// 分享连接
				sb.append( ":" + mContext.getString( R.string.share_a_link ) );
			}
				break;
			default :
				sb.append( mContext.getString( R.string.you_got_messages ) );
				break;
		}
		return sb.toString( );
	}
	
	/**
	 * 将[#...#]替换成[表情]
	 * */
	private String replaceFaceToEmotion(String text )
	{
		Pattern p = Pattern.compile( "\\[\\#\\w+\\#\\]" );// 匹配[# #]
		Matcher m = p.matcher( text );// 开始编译
		
		while ( m.find( ) )
		{
			String icon = m.group( );
			// 查找对应的图标
			text = text.replace( icon , mContext.getString( R.string.replace_emotion ) );
		}
		return text;
	}
	
	/**
	 * 取消显示一个Notification
	 * */
	public void cancelNotification( )
	{
//		if ( notificationManager != null )
//		{
//			// 只消除私聊和动态的推送
//			notificationManager.cancel( CHAT_PUSH_ID );
//			notificationManager.cancel( DYNAMIC_PUSH_ID );
//			notificationManager.cancel( KANKA_PUSH_ID );
//		}

		if (currentNotify != null) {
			currentNotify.clear();
		}
	}

	/** 在系统状态栏显示一个Notification */
	@SuppressWarnings( "deprecation" )
	public synchronized void showNotification(Context context , Intent intent , String title ,
                                              String content , int icon , int chatType )
	{
		// 判断是否为免打扰时间段
		if ( inDndTime( ) )
			return;
		
		switch ( chatType )
		{
			case -1 :
			case ChatMessageType.FACE :
			case ChatMessageType.GIFE_REMIND :
			case ChatMessageType.MEET_GIFT :
			case ChatMessageType.IMAGE :
			case ChatMessageType.LOCATION :
			case ChatMessageType.SOUND :
			case ChatMessageType.TEXT :
			case ChatMessageType.VIDEO :
				break;
			
			default :
				return;
		}
		
		try
		{
//			if ( notificationManager == null )
//			{
//				notificationManager = ( NotificationManager ) context
//						.getSystemService( Context.NOTIFICATION_SERVICE );
//			}
//			Notification notification;
//			notification = new Notification( R.drawable.icon ,
//					title == null ? context.getString( R.string.app_name ) : title ,
//					System.currentTimeMillis( ) );
//			notification.contentView = getNotificationContentView( context , content , title ,
//					icon );
			PendingIntent pt = PendingIntent.getActivity( context , 0 , intent ,
					PendingIntent.FLAG_UPDATE_CURRENT );
			
//			notification.contentIntent = pt;
//			notification.flags = Notification.FLAG_AUTO_CANCEL;

			// notificationManager.cancelAll( );
			if ( chatType == -1 ){
				NotifyUtil notify4 = new NotifyUtil(mContext, 4);
				notify4.notify_normal_singline(pt, iconID, rollTitle, title, content, true, true, false);
				currentNotify = notify4;
//				notificationManager.notify( DYNAMIC_PUSH_ID , notification );
			}
			else{
				NotifyUtil notify5 = new NotifyUtil(mContext, 5);
				notify5.notify_normal_singline(pt, iconID, rollTitle, title, content, true, true, false);
				currentNotify = notify5;
//				notificationManager.notify( CHAT_PUSH_ID , notification );
			}

		}
		catch ( Exception e )
		{
			CommonFunction.log( e );
		}
	}
}
