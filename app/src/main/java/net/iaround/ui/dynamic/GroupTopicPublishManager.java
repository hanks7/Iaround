
package net.iaround.ui.dynamic;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.PublishManager;
import net.iaround.connector.protocol.TopicHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.entity.GeoData;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.dynamic.bean.GroupTopicBackBean;
import net.iaround.ui.group.bean.GroupTopicPublishBean;
import net.iaround.ui.group.bean.PublishBaseBean;
import net.iaround.ui.group.bean.TopicListBean;
import net.iaround.ui.group.bean.TopicListContentBeen;
import net.iaround.ui.topic.TopicPublishRefreshInterface;

import java.util.ArrayList;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-16 下午4:04:52
 * @Description:
 */
public class GroupTopicPublishManager extends PublishManager implements HttpCallBack
{
	
	public GroupTopicPublishManager(Context context )
	{
		super( context );
	}
	
	@Override
	protected void publishDynamic( PublishBaseBean bean )
	{
		GroupTopicPublishBean dynamicBean = ( GroupTopicPublishBean ) bean;
		String photoStr = "";// 组装
		int count = dynamicBean.getPhotoList( ) != null ? bean.getPhotoList( ).size( ) : 0;
		for ( int i = 0 ; i < count ; i++ )
		{
			
			photoStr += dynamicBean.getPhotoList( ).get( i ) + ( count - i != 1 ? "," : "" );
		}
		
		
		GeoData geoData = LocationUtil.getCurrentGeo( mContext );
		PUBLISH_REQUEST_FLAG = TopicHttpProtocol.publishGroupTopic( mContext ,
				dynamicBean.groupid , dynamicBean.getContent( ) , photoStr , dynamicBean.type ,
				dynamicBean.getAddress( ) , dynamicBean.getShortaddress( ) ,
				geoData.getLat( ) , geoData.getLng( ) , dynamicBean.getSync( ) ,
				dynamicBean.getSyncvalue( ) , this );
		publishSparse.put( PUBLISH_REQUEST_FLAG , bean );
		
		//syncToOtherPlatform( dynamicBean );
	}
	
	@Override
	protected int getImageUploadType( )
	{
		return FileUploadType.PIC_DYNAMIC_PUBLISH;
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		
		if ( PUBLISH_REQUEST_FLAG == flag )
		{
			
			
			GroupTopicBackBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					GroupTopicBackBean.class );
			
			GroupTopicPublishBean info = ( GroupTopicPublishBean ) publishSparse.get( flag );
			publishSparse.remove( flag );
			
			int taskID = ( int ) ( info.datetime & NOTIFICATION_TASK_ID_MASK );
			
			Activity topActivity = CloseAllActivity.getInstance( ).getTopActivity( );
			
			
			if ( bean.isSuccess( ) )
			{
				info.topic_index = bean.topicid;
				changeSendingListStatus( info , true );
				if ( topActivity instanceof TopicPublishRefreshInterface )
				{
					( (TopicPublishRefreshInterface)topActivity).refreshTopicListPage(
							info.datetime , bean.topicid );
				}
				CommonFunction.toastMsg( mContext , R.string.dynamic_publish_success );
			}
			else
			{
				changeSendingListStatus( info , false );
				if ( topActivity instanceof TopicPublishRefreshInterface )
				{
					( ( TopicPublishRefreshInterface ) topActivity ).refreshTopicListPage(
							info.datetime , info.datetime );
				}
				ErrorCode.showError( mContext , result );
				
				updateNotification( taskID , false , info );
				
			}
			
			
		}
		
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		
		if ( PUBLISH_REQUEST_FLAG == flag )
		{
			GroupTopicPublishBean bean = ( GroupTopicPublishBean ) publishSparse.get( flag );
			publishSparse.remove( flag );
			
			changeSendingListStatus( bean , false );
			Activity topActivity = CloseAllActivity.getInstance( ).getTopActivity( );
			if ( topActivity instanceof TopicPublishRefreshInterface )
			{
				( ( TopicPublishRefreshInterface ) topActivity ).refreshTopicListPage(
						bean.datetime , bean.datetime );
			}
			int taskID = ( int ) ( bean.datetime & NOTIFICATION_TASK_ID_MASK );
			updateNotification( taskID , false , bean );
			CommonFunction.toastMsg( mContext , R.string.dynamic_publish_fail );
			
			
		}
	}
	
	// 添加提示栏
	protected void showNotification( int taskId , PublishBaseBean bean )
	{
		
//		super.showNotification( taskId , bean );
//		String contentText = mContext.getResources( ).getString( R.string.dynamic_publish_ing );
//		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( mContext )
//				.setSmallIcon( R.drawable.icon ).setContentTitle( bean.getContent( ) )
//				.setContentText( contentText );
//		
//		Intent intent = new Intent( Intent.ACTION_MAIN );
//		intent.addCategory( Intent.CATEGORY_LAUNCHER );
//		intent.setClass( mContext , PublishDynamicActivity.class );
//		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK
//				| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
//		mBuilder.setContentIntent( PendingIntent.getActivity( mContext , 0 , intent , 0 ) );
//		mBuilder.build( ).flags = Notification.FLAG_AUTO_CANCEL;
//		
//		NotificationManager mNotificationManager = ( NotificationManager ) mContext
//				.getSystemService( Context.NOTIFICATION_SERVICE );
//		// mId allows you to update the notification later on.
//		mNotificationManager.notify( taskId , mBuilder.build( ) );
	}
	
	/**
	 * 更新Notification的状态。
	 * 
	 * @param taskId
	 *            任务id
	 * @param bIsPublishSuccess
	 *            是否发布成功
	 */
	protected void updateNotification( int taskId , boolean bIsPublishSuccess ,
			PublishBaseBean bean )
		
	{
		super.updateNotification( taskId , bIsPublishSuccess , bean );
		if ( bean == null )
		{
			return;
		}
//		
		if ( bIsPublishSuccess )
		{
//			NotificationManager mNotificationManager = ( NotificationManager ) mContext
//					.getSystemService( Context.NOTIFICATION_SERVICE );
//			String contentText = mContext.getResources( ).getString(
//					R.string.dynamic_publish_success );
//			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( mContext )
//					.setSmallIcon( R.drawable.icon ).setContentTitle( bean.getContent( ) )
//					.setContentText( contentText ).setAutoCancel( true );
//			
//			Intent intent = new Intent( Intent.ACTION_MAIN );
//			intent.addCategory( Intent.CATEGORY_LAUNCHER );
//			intent.setClass( mContext , PublishDynamicActivity.class );
//			intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK
//					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED );
//			mBuilder.setContentIntent( PendingIntent.getActivity( mContext , 0 , intent , 0 ) );
//			mBuilder.build( ).flags = Notification.FLAG_AUTO_CANCEL;
//			
//			mNotificationManager.notify( taskId , mBuilder.build( ) );
			NotificationManager mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(taskId);
		}
		else
		{
//			String dynamicInfoStr = GsonUtil.getInstance( ).getStringFromJsonObject( bean );
			
			
			String contentText = mContext.getResources( ).getString(
					R.string.dynamic_publish_fail );
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder( mContext )
					.setSmallIcon( R.drawable.icon ).setContentTitle( bean.getContent( ) )
					.setContentText( contentText ).setAutoCancel( true );
			
//			Intent resultIntent = new Intent(Intent.ACTION_MAIN);
//			resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
//			resultIntent.setClass(mContext, StartActivity.class);
//			resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
//			mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0,
//					resultIntent, 0));//jiqiang



//			Intent resultIntent = new Intent( mContext , PublishDynamicActivity.class );
//			resultIntent.putExtra( "dynamic" , dynamicInfoStr );
			
//			PendingIntent resultPendingIntent = PendingIntent.getActivity( mContext , 0 ,
//					resultIntent , Intent.FLAG_ACTIVITY_NEW_TASK );
//			mBuilder.setContentIntent( resultPendingIntent );
			
			NotificationManager mNotificationManager = (NotificationManager) mContext
					.getSystemService( Context.NOTIFICATION_SERVICE );
			// mId allows you to update the notification later on.
			mNotificationManager.notify( taskId , mBuilder.build( ) );
			
		}
	}
	
	@Override
	protected void handleUploadFail( long flag )
	{
		long key = 0;
		// TODO Auto-generated method stub
		for( int i = 0;i<dynamicSparseTask.size( );i++ )
		{
			key = dynamicSparseTask.keyAt( i );
			
			GroupTopicPublishBean bean  = ( GroupTopicPublishBean ) dynamicSparseTask.get( key );

			if(bean.datetime == flag)
			{
				changeSendingListStatus( bean , false );
				Activity topActivity = CloseAllActivity.getInstance( ).getTopActivity( );
				if ( topActivity instanceof TopicPublishRefreshInterface )
				{
					( ( TopicPublishRefreshInterface ) topActivity ).refreshTopicListPage(
							bean.datetime , bean.datetime );
				}
				int taskID = ( int ) ( bean.datetime & NOTIFICATION_TASK_ID_MASK );
				updateNotification( taskID , false , bean );
				CommonFunction.toastMsg( mContext , R.string.dynamic_publish_fail );
			}
			
		}
			
	}
	
	/*
	 * 如果发送成功，将话题项从本地保存的列表中删除，如果发送失败，将状态改成发送失败
	 */
	protected void changeSendingListStatus( GroupTopicPublishBean bean , boolean isSuccess )
	{
		
		int topicIndex = 0;
		
		TopicListBean topic;
		ArrayList< TopicListContentBeen > topicList = new ArrayList< TopicListContentBeen >( );
		String key = SharedPreferenceCache.TOPIC_SENDING_LIST + String.valueOf( bean.groupid ) + Common.getInstance( ).loginUser.getUid( );
		
		String listStr = SharedPreferenceCache.getInstance( mContext ).getString( key );
		if ( !TextUtils.isEmpty( listStr ) )
		{
			topic = GsonUtil.getInstance( ).getServerBean( listStr ,
					TopicListBean.class );
		}
		else
		{
			// 未曾保存过发送的数据
			return;
		}
		
		
		if ( topic.topics != null )
		{
			topicList.addAll( topic.topics );
			topic.topics.clear( );
		}
		
		for ( int i = 0 ; i < topicList.size( ) ; i++ )
		{
			if ( topicList.get( i ).topic.topicid == bean.datetime )
			{
				topicIndex = i;
				break;
			}
		}
		
		if ( isSuccess )
		{
			// 发送成功
			topicList.get( topicIndex ).sendStatus = 0;
			topicList.get( topicIndex ).topic.topicid = bean.topic_index;
			saveToCach( topicList.get( topicIndex ), String.valueOf( bean.groupid ));
			topicList.remove( topicIndex );
		}
		else
		{
			// 发送失败
			topicList.get( topicIndex ).sendStatus = 2;
		}
		topic.topics = topicList;
		
		String topicInfo = GsonUtil.getInstance( ).getStringFromJsonObject( topic );
		
		SharedPreferenceCache.getInstance( mContext ).putString( key , topicInfo );
		
	}
	
	
	protected void  saveToCach(TopicListContentBeen been , String groupid)
	{
		String key = SharedPreferenceCache.TOPIC_CACHED+groupid;
		TopicListBean savedTopicList;
		ArrayList< TopicListContentBeen > topicList = new ArrayList< TopicListContentBeen >( );
		String listStr = SharedPreferenceCache.getInstance( mContext ).getString(key) ;
		if(!TextUtils.isEmpty( listStr ))
		{
			savedTopicList = GsonUtil.getInstance( ).getServerBean( listStr ,
				TopicListBean.class );
		}
		else
		{
			savedTopicList = new TopicListBean();
		}
		//先将当前的bean保存到列表中
		topicList.add( been ) ;
		
		//再将缓存到本地的数据放到列表中
		if(savedTopicList.topics!=null)
		{
			topicList.addAll( savedTopicList.topics );
			savedTopicList.topics.clear( );
		}
		
		
		savedTopicList.topics = topicList;
				
		String topicInfo = GsonUtil.getInstance( ).getStringFromJsonObject(savedTopicList);
		
		SharedPreferenceCache.getInstance( mContext ).putString(  key, topicInfo ) ;
	}
	
}
