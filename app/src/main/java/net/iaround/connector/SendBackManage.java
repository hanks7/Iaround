
package net.iaround.connector;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;


import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.protocol.PhotoHttpProtocol;
import net.iaround.model.im.Me;
import net.iaround.share.facebook.FaceBookUtil;
import net.iaround.share.tencent.qqzone.QQZoneUtil;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.share.utils.ShareActionListener;
import net.iaround.share.utils.ShareConstant;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.JsonUtil;
import net.iaround.ui.datamodel.SendTaskItem;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


/**
 * 后台发送队列(话题、照片)
 *
 * @author linyg
 */
public class SendBackManage implements UploadFileCallback, HttpCallBack {
    // 通知ID
    public static final int DOWNLOAD_NOTIFY_VIEW_ID = 103;
    private final int MSG_SEND_ERROR = 2; // 发送失败
    private final int MSG_SEND_SUCCESS = 3;// 发送成功
    private final int MSG_SEND_PROGRESS = 4;// 图片上传成功
    private final int MSG_UPFILE = 5;

    private final int MSG_SHARE = 6;
    private final int MSG_GET_SHARE_CONTENT = 7;
    private final int MSG_SUBMIT_SHARE_RESULT = 8;

    private static SendBackManage sendMessageTask;
    private Activity mActivity;
    private NotificationManager mNotificationManager;

    private Notification notification;
    private long lenghtOfFile = 1; // 发送文件的总长度
    private long curLenght = 1; // 当前发送的长度
    private boolean isSending;

    private SendTaskItem currentItem;// 当前正在执行的item;
    private String fileUrl; // 图片地址
    private String hdFileUrl; // 高清图片地址
    // 发送队列
    private Queue<SendTaskItem> queueTask;
    private RemoteViews contentView;

    private long uid;

    private static final String DEFAULT_LINK = "http://www.iaround.com";
    private String snsIds;
    private String shareId;
    private int shareNum;
    private int itemType;
    private HashMap<Long, Integer> sendHashMap;

    public static SendBackManage getInSendMessageManage(Activity activity) {
        if (null == sendMessageTask) {
            sendMessageTask = new SendBackManage(activity);
        }
        return sendMessageTask;
    }

    /**
     * 检查上传数量，用户每天最多只能上传10张照片
     *
     * @return true 为可以上传，false不能上传
     */
    public static boolean checkUploadNum(Context context) {
//		Date d = new Date( );
//		String strD = d.getYear( ) + "" + d.getMonth( ) + "" + d.getDate( );
//		try
//		{
//			long today = Long.parseLong( strD );
//			long uid = Common.getInstance( ).loginUser.getUid( );
//			String strMax = SharedPreferenceUtil.getInstance( context ).getString(
//					SharedPreferenceUtil.UPLOAD_MAXNUM + "_" + uid );
//			String[ ] maxs = strMax.split( "," );
//			if ( maxs != null && maxs.length > 1 )
//			{
//				maxs[ 0 ] = TextUtils.isEmpty( maxs[ 0 ] ) ? "0" : maxs[ 0 ];
//				maxs[ 1 ] = TextUtils.isEmpty( maxs[ 1 ] ) ? "0" : maxs[ 1 ];
//				long max1 = Long.parseLong( maxs[ 0 ] );
//				int max2 = Integer.parseInt( maxs[ 1 ] );
//				// 当今日的上传时间与缓存的一致时，则判断是否大于10张，若大于则提示用户不能上传。
//				// 若出现其它意外情况，则让用户上传，交由服务端判断
//				CommonFunction.log( "*************check" , today + "," + max1 + "," + max2 );
//				if ( today == max1 && max2 >= 10 )
//				{
//					Toast.makeText( context , R.string.upload_maxnum , Toast.LENGTH_LONG )
//							.show( );
//					return false;
//				}
//			}
//			else
//			{
//				return true;
//			}
//		}
//		catch ( Exception e )
//		{
//			e.printStackTrace( );
//		}

        return true;
    }

    /**
     * 保存每日上传的图片总数
     *
     * @param context
     * @param json
     */
    public static void saveMaxNum(Context context, JSONObject json) {
        if (json != null) {
            // {"date":"20130123","todaynum":1,"status":200}
            Me me = Common.getInstance().loginUser;
//			int maxNum = json.optInt( "todaynum" );
//			long uid = me.getUid( );
//			if ( maxNum > 0 )
//			{
//				Date d = new Date( );
//				String strD = d.getYear( ) + "" + d.getMonth( ) + "" + d.getDate( );
//				CommonFunction.log( "*************" , strD + "," + maxNum );
//				SharedPreferenceUtil.getInstance( context ).putString(
//						SharedPreferenceUtil.UPLOAD_MAXNUM + "_" + uid , strD + "," + maxNum );
//			}
            //保存剩余上传图片数
//			int todayLeftPhotos = me.getTodayphotos();
//			if (todayLeftPhotos > 0) {
//				me.setTodayphotos( todayLeftPhotos - 1 );
//			}
            int totalLeftPhotos = me.getPhotouploadleft();
            if (totalLeftPhotos > 0) {
                me.setPhotouploadleft(totalLeftPhotos - 1);
            }
        }
    }

    private SendBackManage(Activity activity) {
        mActivity = activity;
        isSending = false;
        uid = Common.getInstance().loginUser.getUid();
        // 初始化当前的发送队列
        queueTask = new LinkedList<SendTaskItem>();
        // 发送进度通知消息
        mNotificationManager = (NotificationManager) activity
                .getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 请求发送，加入发送队列当中
     */
//	public void doSendTask(Context context , SendTaskItem item )
//	{
//		CommonFunction.log( "后台发送" , "正式加入队列" );
//		// 提示消息进入发送队列
//		Toast.makeText( mActivity , mActivity.getString( R.string.queue_send_add_queue ) ,
//				Toast.LENGTH_SHORT ).show( );
//		if ( queueTask == null )
//		{
//			queueTask = new LinkedList< SendTaskItem >( );
//		}
//
//		queueTask.offer( item );
//
//		// 如果当前没有正在发送，则直接进入发送流程
//		if ( !isSending )
//		{
//			isSending = true;
//			new Thread( new SendRunnable( item ) ).start( );
//		}
//	}

    // 发送线程
    private class SendRunnable implements Runnable {
        public SendRunnable(SendTaskItem item) {
            fileUrl = "";
            hdFileUrl = "";
            lenghtOfFile = 1;
            currentItem = item;
        }

        @Override
        public void run() {
            CommonFunction.log("后台发送", "启动发布线程");
            mHandler.sendEmptyMessage(MSG_SEND_PROGRESS);
            if (currentItem != null) {
                // 若图片
                if (!CommonFunction.isEmptyOrNullStr(currentItem.getFilePath())) {
                    File bmFile = new File(currentItem.getFilePath());
                    if (bmFile != null && bmFile.exists()) {
                        lenghtOfFile = bmFile.length();
                    }

                    // 若有高清图，则将其大小计算到总数中;
                    if (!CommonFunction.isEmptyOrNullStr(currentItem.gethFilePath())) {
                        File hdFile = new File(currentItem.gethFilePath());
                        if (hdFile != null && hdFile.exists()) {
                            lenghtOfFile += hdFile.length();
                        }
                    }
                } else {
                    // 若为话题，并没有图片，则直接发送问题，但是若为发送图片，又没图片地址，则认为是无效的
                    if (currentItem.getType() == 2) {
                        upData();
                    } else {
                        mHandler.sendEmptyMessage(MSG_SEND_ERROR);
                        return;
                    }
                }

                Message msg = new Message();
                msg.what = MSG_UPFILE;
                msg.obj = currentItem.getFilePath();
                msg.arg1 = currentItem.getUploadType();
                mHandler.sendMessage(msg);
            }
        }
    }

    // 上传
    private void upFile(String path, int uploadType) {
        if (path != null && path.length() > 0) {
            CommonFunction.log("后台发送", "正式上传图片");
            File bmFile = new File(path);
            if (bmFile.exists()) {
                try {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("key", ConnectorManage.getInstance(mActivity).getKey());
                    map.put("type", String.valueOf(uploadType));

                    FileUploadManager
                            .createUploadTask(mActivity, path, FileUploadManager.FileProfix.JPG, Config.sPictureHost, map, SendBackManage.this,
                                    System.currentTimeMillis())
                            .start();

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_SEND_ERROR);
                }
            }
        }
    }

    // 发送数据
    private void upData() {
        if (currentItem == null) {
            return;
        }
        if (currentItem.getType() == 1) {
            sendPhotoData();
        } else if (currentItem.getType() == 2) {
            sendTopicData();
        }
    }

    // 发送图片内容
    private void sendPhotoData() {
        if (CommonFunction.isEmptyOrNullStr(fileUrl)) {
            mHandler.sendEmptyMessage(MSG_SEND_ERROR);
            return;
        }
        long uid = Common.getInstance().loginUser.getUid();
        JSONObject json;
        try {
            json = new JSONObject(currentItem.getContentJsonToString());
            int plat = json.optInt("plat");
            String tags = CommonFunction.jsonOptString(json, "tags");
            String bmUrl = fileUrl;
            String hdUrl = hdFileUrl;
            String description = CommonFunction.jsonOptString(json, "description");
            if ("null".equalsIgnoreCase(description)) {
                description = "";
            }
            int lat = json.optInt("lat");
            int lng = json.optInt("lng");
            String address = CommonFunction.jsonOptString(json, "address");
            String snsids = CommonFunction.jsonOptString(json, "snsids");
			/*String result = PhotoHttpProtocol.sysPublishPhoto( mActivity , uid , plat , tags ,
					bmUrl , hdUrl , CommonFunction.replaceBlank( description ) , lat , lng ,
					address , snsids );
			if ( CommonFunction.isEmptyOrNullStr( result ) )
			{
				mHandler.sendEmptyMessage( MSG_SEND_ERROR );
			}
			else
			{
				//shareToWeibo( snsids , description , bmUrl );
				*//*Message shareMessage = mHandler.obtainMessage();
				shareMessage.what = MSG_SHARE;
				shareMessage.obj = new Object[]{snsids, description, bmUrl};
				mHandler.sendMessage(shareMessage);*//*
				CommonFunction.log("share", "result---" + result);
				CommonFunction.log("share", "snsids---" + snsids);
				snsIds = snsids;
				itemType = currentItem.getType();
				
				Message msg = new Message( );
				msg.what = MSG_SEND_SUCCESS;
				msg.obj = result;
				mHandler.sendMessage( msg );	
				
				Message shareMessage = new Message( );
				shareMessage.what = MSG_GET_SHARE_CONTENT;
				shareMessage.obj = result;
				mHandler.sendMessage(shareMessage);						
			}*/
        } catch (Exception e) {
            mHandler.sendEmptyMessage(MSG_SEND_ERROR);
        }
    }

    // 发送话题内容
    private void sendTopicData() {
//		JSONObject json;
//		try
//		{
//			json = new JSONObject( currentItem.getContentJsonToString( ) );
//			String groupid = CommonFunction.jsonOptString(json, "groupid" );
//			String content = CommonFunction.jsonOptString(json, "content" );
//			if ( "null".equalsIgnoreCase( content ) )
//			{
//				content = "";
//			}
//			String url = fileUrl;
//			int type = json.optInt( "type" );
//			String result = TopicHttpProtocol.synPublishTopic( mActivity , groupid ,
//					CommonFunction.replaceBlank( content ) , url , type );
//			//shareToWeibo( CommonFunction.jsonOptString( json,"snsids" ) , content , url );
//
//			/*Message shareMessage = mHandler.obtainMessage();
//			shareMessage.what = MSG_SHARE;
//			shareMessage.obj = new Object[]{CommonFunction.jsonOptString( json,"snsids" ),
//														content, url};
//			mHandler.sendMessage(shareMessage);*/
//			snsIds = CommonFunction.jsonOptString( json,"snsids" );
//			itemType = currentItem.getType();
//
//			CommonFunction.log("share", "result---" + result);
//
//			Message msg = new Message( );
//			msg.what = MSG_SEND_SUCCESS;
//			msg.obj = result;
//			mHandler.sendMessage( msg );
//
//			Message shareMessage = new Message( );
//			shareMessage.what = MSG_GET_SHARE_CONTENT;
//			shareMessage.obj = result;
//			mHandler.sendMessage(shareMessage);
//
//		}
//		catch ( Exception e )
//		{
//			e.printStackTrace( );
//			mHandler.sendEmptyMessage( MSG_SEND_ERROR );
//		}
    }

    /**
     * 更新状态栏
     *
     * @param type   1正在发送中，2发送成功，3发送失败
     * @param length
     */
    private void updateNotify(int type, long length) {
        if (type > 3 || type < 1) {
            return;
        }
        PendingIntent contentIntent;
        String tickerText = "";
        int icon = 0;
        Intent notificationIntent = new Intent();

        if (type == 1) {// 更改发送进度
            // 第一次显示时，则清空状态栏
            if (length == 0) {
                mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);
            }
            double pro1 = 0;
            int pro = 1;
            if (contentView == null) {
                contentView = new RemoteViews(mActivity.getPackageName(),
                        R.layout.notify_queue_progress);
                contentView.setTextViewText(R.id.title,
                        mActivity.getString(R.string.queue_sending));
            } else {
                pro1 = ((double) length) / ((double) lenghtOfFile);
                pro = (int) (pro1 * 100);
            }

            // 图片上传完成之后，剩下的1%作为数据请求
            if (pro > 99) {
                pro = 99;
            }
            contentView.setTextViewText(R.id.percentTip, pro + "%");
            contentView.setProgressBar(R.id.progressBar, 100, pro, false);
            contentIntent = PendingIntent.getActivity(mActivity, 0, notificationIntent, 0);
//			tickerText = mActivity.getString( R.string.queue_sendind_title );
//			icon = R.drawable.queue_icon_send;//jiqiang
//			notification = new Notification( icon , tickerText , System.currentTimeMillis( ) );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity);
            notification = builder.build();
//			builder.setLargeIcon( icon );

            notification.contentView = contentView;
            notification.contentIntent = contentIntent;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
        } else if (type == 2) {// 发送成功
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);
            tickerText = mActivity.getString(R.string.queue_send_succes);
//			contentIntent = PendingIntent.getActivity( mActivity , 0 , notificationIntent , 0 );
//			icon = R.drawable.queue_icon_success;//jiqiang

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity);

            builder.setContentText(tickerText);
            builder.setSmallIcon(R.drawable.queue_icon_success);
            notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        } else { // 发送失败
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);
            tickerText = mActivity.getString(R.string.queue_send_error);
            // notificationIntent.setClass(mActivity, DraftMessage.class);
            contentIntent = PendingIntent.getActivity(mActivity, 0, notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mActivity);

            builder.setContentText(tickerText);
            builder.setSmallIcon(R.drawable.queue_icon_miss);

            notification = builder.build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL;
        }
        mNotificationManager.notify(DOWNLOAD_NOTIFY_VIEW_ID, notification);
        if (type == 2) {
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);
        }
    }

    @Override
    public void onUploadFileProgress(int lengthOfUploaded, long flag) {
        CommonFunction.log("UploadFileProgress", "UploadFileProgress:" + lengthOfUploaded);
        curLenght = lengthOfUploaded;
        mHandler.sendEmptyMessage(MSG_SEND_PROGRESS);
    }

    @Override
    public void onUploadFileFinish(long flag, String result) {
        // 图片上传结束
        Map<String, Object> map = JsonUtil.jsonToMap(result);
        // 保存下发的地址
        if (map != null && map.containsKey("status")
                && ((Integer) map.get("status")) == 200) {
            if (map.containsKey("url")) {
                if (CommonFunction.isEmptyOrNullStr(fileUrl)) {
                    fileUrl = String.valueOf(map.get("url"));
                    if (!CommonFunction.isEmptyOrNullStr(currentItem.gethFilePath())
                            && currentItem.getType() == 1) {
                        Message msg = new Message();
                        msg.what = MSG_UPFILE;
                        msg.obj = currentItem.gethFilePath();
                        msg.arg1 = currentItem.getUploadType();
                        mHandler.sendMessage(msg);
                        return;
                    }
                } else {
                    hdFileUrl = String.valueOf(map.get("url"));
                }
            }
        }
        // 发送数据
        upData();
    }

    @Override
    public void onUploadFileError(String e, long flag) {
        mHandler.sendEmptyMessage(MSG_SEND_ERROR);
    }

    /**
     * 保存到草稿箱
     *
     * @param item
     */
    public void saverDraft(SendTaskItem item) {
        // 添加到数据库草稿箱中
//		DraftsWorker dbAdd = DatabaseFactory.getDraftsWorker( mActivity );
//		dbAdd.onInsert( item.getId( ) , uid , item.getType( ) , item.getFilePath( ) ,
//				item.gethFilePath( ) , item.getContentJsonToString( ) );
    }

    /**
     * 分享微博
     *
     * @param snsids
     * @param content
     * @param filepath
     */
	/*private void shareToWeibo( final String snsids , final String content ,
			final String filepath )
	{
		if ( CommonFunction.isEmptyOrNullStr( snsids ) )
		{
			return;
		}
		
		String[ ] weiboid = snsids.split( "," );
		if ( weiboid.length > 0 )
		{
			for ( String id : weiboid )
			{
				final AbstractWeiboUtils weibo = AbstractWeiboUtils.getWeibo( mActivity , uid ,
						Integer.parseInt( id ) );
				if ( weibo != null && weibo.isValid( ) )
				{
					new Thread( new Runnable( )
					{
						@Override
						public void run( )
						{
							CommonFunction.log( "shareToweibo" , "snsids:" + snsids
									+ ",content:" + content + ",filepath:" + filepath );
							String contentStr = content;
							if ( CommonFunction.isEmptyOrNullStr( contentStr ) )
							{
								contentStr = "";
							}
							
							weibo.setWeiboActionListener( new WeiboActionListener( )
							{
								@Override
								public void onError( AbstractWeiboUtils arg0 , int arg1 ,
										Throwable arg2 )
								{
									CommonFunction.log( "weibo" , "error:" + arg2 );
									CommonFunction.log( arg2 );
								}
								
								@Override
								public void onComplete( AbstractWeiboUtils arg0 , int arg1 ,
										Map< String , Object > arg2 )
								{
									CommonFunction.log( "weibo" , "complete" );
								}
								
								@Override
								public void onCancel( AbstractWeiboUtils arg0 , int arg1 )
								{
									CommonFunction.log( "weibo" , "cancel" );
								}
							} );
							
							if ( !CommonFunction.isEmptyOrNullStr( filepath ) )
							{
								if ( weibo.getId( ) == QZoneUtil.ID )
								{
									( ( QZoneUtil ) weibo ).share2Weibo( mActivity ,
											contentStr , "http://www.iaround.net" , fileUrl );
								}
								else
								{
									weibo.share2Weibo( mActivity , contentStr , null ,
											filepath );
								}
							}
							else
							{
								if ( weibo.getId( ) == QZoneUtil.ID )
								{
									( ( QZoneUtil ) weibo ).share2Weibo( mActivity ,
											contentStr , "http://www.iaround.net" , null );
								}
								else
								{
									weibo.share2Weibo( mActivity , contentStr , null , null );
								}
							}
						}
					} ).start( );
				}
			}
		}
	}*/

    /**
     * 分享微博
     *
     * @param content
     */
    private void shareToMultiPlatform(int shareId, String title, String content,
                                      String link, String thumb, String pic) {
        shareNum = 0;

        final AbstractShareUtils weibo = AbstractShareUtils.getSingleShareUtil(mActivity,
                uid, shareId);
        Me me = Common.getInstance().loginUser;
        if (weibo != null && weibo.isValid()) {
            if (itemType == 1) {
                weibo.SHARE_FLAG = ShareConstant.PUBLISH_PICTURE_DYNAMIC;
            } else if (itemType == 2) {
                weibo.SHARE_FLAG = ShareConstant.PUBLISH_TOPIC_DYNAMIC;
            }
            CommonFunction.log("share", "id:" + weibo.getId()
                    + ",content:" + content + ",filepath:" + pic);
            String contentStr = content;
            if (CommonFunction.isEmptyOrNullStr(contentStr)) {
                contentStr = "";
            }

            weibo.setShareActionListener(new ShareActionListener() {
                @Override
                public void onError(AbstractShareUtils arg0, int action,
                                    Throwable arg2) {
                    CommonFunction.log("share", "error:" + arg2);
                    CommonFunction.log(arg2);
                    if (arg2 != null
                            && (action == AbstractShareUtils.ACTION_UPDATEING
                            || action == AbstractShareUtils.ACTION_UPLOADING)) {
                        shareNum++;
                        CommonFunction.log("share", "shareNum**" + shareNum);
                        //if (shareNum <= weiboid.length) {
                        CommonFunction.log("share", "MSG_SUBMIT_SHARE_RESULT**");
                        Message msg = new Message();
                        msg.what = MSG_SUBMIT_SHARE_RESULT;
                        msg.arg1 = 0;
                        msg.arg2 = arg0.getId();
                        //msg.obj = errorMsg;
                        mHandler.sendMessage(msg);
                        //}
                    }
                }

                @Override
                public void onComplete(AbstractShareUtils arg0, int action,
                                       Map<String, Object> arg2) {
                    CommonFunction.log("share", "complete");
                    if (arg2 != null &&
                            (action == AbstractShareUtils.ACTION_UPDATEING
                                    || action == AbstractShareUtils.ACTION_UPLOADING)) {
                        if ((Integer) arg2.get("status") == 200) {
                            shareNum++;
                            CommonFunction.log("share", "shareNum**" + shareNum);
                            //if (shareNum <= weiboid.length) {
                            CommonFunction.log("share", "MSG_SUBMIT_SHARE_RESULT**");
                            Message msg = new Message();
                            msg.what = MSG_SUBMIT_SHARE_RESULT;
                            msg.arg1 = 1;
                            msg.arg2 = arg0.getId();
                            mHandler.sendMessage(msg);
                            //}
                        }
                    }
                }

                @Override
                public void onCancel(AbstractShareUtils arg0, int arg1) {
                    CommonFunction.log("share", "cancel");
                }
            });

            try {
                if (!CommonFunction.isEmptyOrNullStr(pic)) {
                    if (weibo.getId() == QQZoneUtil.ID) {
                        weibo.share2Weibo(mActivity, title,
                                contentStr, link, thumb, fileUrl);
                    } else if (weibo.getId() == FaceBookUtil.ID) {
										/*if (me.findWeibo(FaceBookUtil.ID) != null) {
											( ( FaceBookUtil ) weibo ).setSession(null, 0);
										}	*/
                        weibo.share2Weibo(mActivity, title, contentStr, link,
                                thumb, pic);
                    } else {
                        weibo.share2Weibo(mActivity, title, contentStr, link,
                                thumb, pic);
                    }
                } else {
                    if (weibo.getId() == QQZoneUtil.ID) {
                        weibo.share2Weibo(mActivity, title,
                                contentStr, link);
                    } else if (weibo.getId() == FaceBookUtil.ID) {
										/*if (me.findWeibo(FaceBookUtil.ID) != null) {
											( ( FaceBookUtil ) weibo ).setSession(null, 0);
										}*/
                        weibo.share2Weibo(mActivity, title,
                                contentStr, link);
                    } else {
                        weibo.share2Weibo(mActivity, title, contentStr, link);
                    }
                }

                if (itemType == 1) {
//									StatisticsApi.statisticEventShareClick(mActivity, Config.PLAT, ShareType.MY_PHOTO, 1);
                } else {
//									StatisticsApi.statisticEventShareClick(mActivity, Config.PLAT, ShareType.GROUP_TOPIC, 1);//jiqiang
                }

            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

        }
    }

    // 主线程数据处理
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SEND_ERROR: // 发送失败
                    if (currentItem != null) {
                        // long eId = currentItem.getId();
                        queueTask.remove(currentItem);
                        // 保存到草稿箱中
                        saverDraft(currentItem);
                    }
                    uMengCount(false);
                    updateNotify(3, curLenght);
                    isSending = false;
                    break;
                case MSG_SEND_SUCCESS: // 发送成功
//					if ( MainActivity.sInstance != null )
//					{
//						MainActivity.sInstance.refreshMeSpacePhoto( );
//					}
//
//					String result = String.valueOf( msg.obj );
//					JSONObject json;
//					try
//					{
//						json = new JSONObject( result );
//						if ( json != null && json.optInt( "status" ) == 200 )
//						{
//							// 先将其从队列中移除
//							queueTask.poll( );
//							// 从数据库草稿箱中移除
//							DraftsWorker dbDel = DatabaseFactory.getDraftsWorker( mActivity );
//							dbDel.onRemove( currentItem.getId( ) );
//							updateNotify( 2 , curLenght );
//							// 发送成功，刷新数据
//							refreshData( );
//							// 保存当前最大上传图片数量
//							if ( currentItem.getType( ) == 1 )
//							{
//								saveMaxNum( mActivity , json );
//							}
//							uMengCount( true );
//							currentItem = null;
//							// 查看当前队列是否还有，若有则读取，继续发送
//							isSending = false;
//							if ( !isSending && queueTask.size( ) > 0 )
//							{
//								new Thread( new SendRunnable( queueTask.peek( ) ) ).start( );
//							}
//							return;
//						}
//						else
//						{
//							ErrorCode.showError( mActivity , result );
//						}
//					}
//					catch ( JSONException e )
//					{
//						e.printStackTrace( );
//					}
//					catch ( Exception e )
//					{
//						e.printStackTrace( );
//					}
//					mHandler.sendEmptyMessage( MSG_SEND_ERROR );
                    break;
                case MSG_SEND_PROGRESS:
                    updateNotify(1, curLenght);
                    break;
                case MSG_UPFILE:
                    //msg.arg1保存上传的FileUploadType
                    upFile(String.valueOf(msg.obj), msg.arg1);
                    break;
                case MSG_GET_SHARE_CONTENT:
//					JSONObject jsonObject = null;
//					try {
//						jsonObject = new JSONObject(String.valueOf(msg.obj));
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					if ( itemType == 1 )
//					{
//						if (jsonObject.optString("photoid") != null) {
//							String[ ] weiboid = snsIds.split( "," );
//							CommonFunction.log("share", "**********" + weiboid.toString());
//							if ( weiboid.length > 0 )
//							{
//								long flag = 0;
//								if (sendHashMap == null) {
//									sendHashMap = new HashMap<Long, Integer>();
//								}
//								else {
//									if (sendHashMap.size() > 0) {
//										sendHashMap.clear();
//									}
//								}
//								for ( int i = 0; i < weiboid.length; i++)
//								{
//									CommonFunction.log("share", "id---" + weiboid[i]);
//									if (weiboid[i] != null && !weiboid[i].equals("")) {
//										flag =  ShareHttpProtocol.requestShareContent(mActivity, ShareType.MY_PHOTO, 1, weiboid[i],
//												jsonObject.optString("photoid"), SendBackManage.this);
//										sendHashMap.put(flag, Integer.parseInt( weiboid[i] ));
//									}
//								}
//							}
//						}
//					}
//					else if ( itemType == 2 )
//					{
//						if (jsonObject.optString("topicid") != null) {
//							String[ ] weiboid = snsIds.split( "," );
//							if ( weiboid.length > 0 )
//							{
//								long flag = 0;
//								if (sendHashMap == null) {
//									sendHashMap = new HashMap<Long, Integer>();
//								}
//								else {
//									if (sendHashMap.size() > 0) {
//										sendHashMap.clear();
//									}
//								}
//								for ( String id : weiboid )
//								{
//									CommonFunction.log("share", "id---" + id);
//									if (id != null && !id.equals("")) {
//										flag = ShareHttpProtocol.requestShareContent(mActivity, ShareType.GROUP_TOPIC, 1, id,
//												jsonObject.optString("topicid"), SendBackManage.this);
//										sendHashMap.put(flag, Integer.parseInt(id));
//									}
//								}
//							}
//						}
//					}
                    break;
                case MSG_SHARE:
//					String res = String.valueOf(msg.obj);
//					int shareId = msg.arg1;
//					ShareContentBean shareContentBean = ShareModel.parseShareContent(res);
//					shareToMultiPlatform(shareId, shareContentBean.title, shareContentBean.content,
//							shareContentBean.link, shareContentBean.thumb, shareContentBean.pic);
                    break;
                case MSG_SUBMIT_SHARE_RESULT:
//					String toastMsg = mActivity.getResources().getString(R.string.shareing);
//
//					if (msg.arg2 == SinaWeiboUtil.ID) {
//						toastMsg += mActivity.getResources().getString(R.string.sina);
//					}
//					else if (msg.arg2== TencentWeiboUtil.ID) {
//						toastMsg += mActivity.getResources().getString(R.string.qq);
//					}
//					else if (msg.arg2 == FaceBookUtil.ID) {
//						toastMsg +=  mActivity.getResources().getString(R.string.facebook);
//					}
//					else if (msg.arg2 == TwitterUtil.ID) {
//						toastMsg += mActivity.getResources().getString(R.string.twitter);
//					}
//
//					if (msg.arg1 == 0) {
//						toastMsg += mActivity.getResources().getString(R.string.share_to_fail);
//					}
//					else {
//						toastMsg += mActivity.getResources().getString(R.string.share_to_success);
//					}
//					CommonFunction.toastMsg(mActivity,
//								toastMsg);
                    break;
            }
        }
    };

    /**
     * 发送成功,进行友盟统计
     */
    private void uMengCount(boolean type) {
//		UmengUtils u = new UmengUtils( mActivity );
//		try
//		{
//			JSONObject json = new JSONObject( currentItem.getContentJsonToString( ) );
//			boolean withimage = !CommonFunction.isEmptyOrNullStr( currentItem.getFilePath( ) );
//			boolean isns = !CommonFunction.isEmptyOrNullStr( CommonFunction.jsonOptString(json, "snsids" ) );
//			if ( currentItem.getType( ) == 1 )
//			{ // 发送图片
//				boolean location = !CommonFunction.isEmptyOrNullStr(
//						CommonFunction.jsonOptString( json,"address" ) );
//				boolean isOriginal = !CommonFunction.isEmptyOrNullStr( currentItem
//						.gethFilePath( ) );
//				u.uploadImageEvent( u.getMyGender( ) , u.getMyAge( ) , type , isOriginal ,
//						location , isns );
//			}
//			else if ( currentItem.getType( ) == 2 )
//			{ // 发送话题
//				String groupid = CommonFunction.jsonOptString( json,"groupid" );
//				u.publishTopicEvent( u.getMyGender( ) , u.getMyAge( ) , withimage ,
//						isns ? TopicShareType.WEIBO : TopicShareType.NONE , groupid );
//			}
//		}
//		catch ( Exception e )
//		{
//			e.printStackTrace( );
//		}
    }

    /**
     * @Title: refreshData
     * @Description: 发送成功，刷新数据
     */
    private void refreshData() {
//		if ( CloseAllActivity.getInstance( ).getTopActivity( ) instanceof GroupChatTopicActivity )
//		{
//			// 刷新话题列表
//			( ( GroupChatTopicActivity ) CloseAllActivity.getInstance( ).getTopActivity( ) )
//					.refreshTopicList( );
//		}
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        // TODO Auto-generated method stub
        if (sendHashMap != null && sendHashMap.size() > 0) {
            if (sendHashMap.containsKey(flag)) {
                CommonFunction.log("share", "shareId---" + sendHashMap.get(flag));
                if (result.contains("\"status\":200")) {
                    Message shareMessage = new Message();
                    shareMessage.what = MSG_SHARE;
                    shareMessage.arg1 = sendHashMap.get(flag);
                    shareMessage.obj = result;
                    mHandler.sendMessage(shareMessage);
                }
                sendHashMap.remove(flag);
            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        // TODO Auto-generated method stub
        if (sendHashMap != null && sendHashMap.size() > 0) {
            if (sendHashMap.containsKey(flag)) {
                sendHashMap.remove(flag);
            }
        }
    }
}
