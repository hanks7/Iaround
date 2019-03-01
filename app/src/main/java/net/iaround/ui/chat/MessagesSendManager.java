
package net.iaround.ui.chat;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.FileUploadManager;
import net.iaround.connector.FileUploadManager.FileProfix;
import net.iaround.connector.FilterUtil;
import net.iaround.connector.SendDataThread;
import net.iaround.connector.UploadFileCallback;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.entity.GroupChatInfo;
import net.iaround.entity.PrivateChatInfo;
import net.iaround.entity.UploadFileResponse;
import net.iaround.interfaces.AudioSendDataCallback;
import net.iaround.model.im.ChatMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.ui.chat.SuperChat.HandleMsgCode;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @ClassName MessagesSendManager.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-4 下午7:02:31
 * @Description: 消息发送管理类-所有的消息（私聊、圈聊）发送，上传的过程都在这里统一管理<br>
 *               <b>消息发送方式的类型:<b/> @ChatMessageType<br>
 *               1.普通的消息(文本,位置,礼物,贴图)<br>
 *               2.资源文件上传消息(图片,视频)<br>
 *               3.语音分段发送消息
 */

public class MessagesSendManager implements UploadFileCallback, AudioSendDataCallback
{
	
	private final String TAG = "MessagesSendManager";
	
	// 语音发送的类型是3
	protected final int AUDIO_SEND_TYPE = 3;
	
	protected Context mContext;
	private static MessagesSendManager mInstance;
	
	// 聊天界面的handler
	private Handler mHandler;
	private LongSparseArray<ChatMessageBean> messageBeanSparseArray;
	
	/********** 语音消息发送相关变量 **********/
	// 保存发送线程，以flag来作为key
	private LongSparseArray<SendDataThread> sendThreadSparseArray;
	// 保存收到对应flag的回馈消息的计数
	private LongSparseArray< AtomicInteger > receiveCountMap;
	// 记录语音的时间
	private LongSparseArray< String > audioLengthSparseArray;
	
	
	private MessagesSendManager( Context context )
	{
		mContext = context.getApplicationContext( );
		
		setMessageBeanSparseArray( new LongSparseArray< ChatMessageBean >( ) );
		
		sendThreadSparseArray = new LongSparseArray< SendDataThread >( );
		audioLengthSparseArray = new LongSparseArray< String >( );
		receiveCountMap = new LongSparseArray< AtomicInteger >( );
	}
	
	public static MessagesSendManager getManager(Context mContext )
	{
		if ( mInstance == null )
		{
			mInstance = new MessagesSendManager( mContext );
		}
		return mInstance;
	}
	
	public void setChatHanlder( Handler handler )
	{
		mHandler = handler;
	}
	
	
	/**
	 * 发送消息
	 * 
	 * @param isResend
	 *            是否重发.Note如果资源文件没上传过,不能算重发</br>
	 *            <b>也就是如果资源已经上传过,但发送失败才能算是重发,如果资源没有上传成功,那么isResend应该为false</b>
	 */
	public void sendMessage( ChatMessageBean messageBean , Boolean isResend , boolean isRepeater )
	{
		/**
		 * 0.上传文件 1.保存文件 2.保存数据库 3.发协议
		 */

		ChatRecord chatRecord = messageBean.chatRecord;
		
		int type = Integer.valueOf( chatRecord.getType( ) );
		int chatType = messageBean.chatType;
		long recordId = chatRecord.getId( );

		if ( getMessageBeanSparseArray( ).get( recordId ) != null )
		{
			return;
		}

		getMessageBeanSparseArray( ).put( recordId , messageBean );
		if ( isResend )
		{
			// 因为服务端语音同一个flag只能在4分钟后重复，所以添加1
			/*
			 * 以下操作是 1.为了修改数据库中对应消息的消息id以及发送状态. 2.记录发送的时间戳对已的消息id
			 */
			long oldFlag = chatRecord.getDatetime( );
			long newFlag = oldFlag + 1;// 这个是专门给语音用的
			
			long localId = chatRecord.getLocid( );
			String localIdStr = String.valueOf( chatRecord.getLocid( ) );
			String messageIdStr = String.valueOf( chatRecord.getDatetime( ) );
			String statusStr = String.valueOf( chatRecord.getStatus( ) );
			
			// 语音的重发机制与其它类型不相同,所以单独判断
			if ( type != ChatMessageType.SOUND )
			{
				
				chatRecord.setId( oldFlag );
				chatRecord.setDatetime( oldFlag );

				CommonFunction.log( TAG, "修改数据的数据id>>" + oldFlag + "newFlag>>" + newFlag );
				if ( chatType == ChatMessageBean.PRIVATE_CHAT )
				{
					ChatPersonalModel.getInstance( ).saveRecordLocalId( oldFlag , localId );
					try
					{
						ChatPersonalModel.getInstance( ).modifyMessageId( mContext , localIdStr , messageIdStr , statusStr , 0 );
					}
					catch ( JSONException e )
					{
						e.printStackTrace( );
					}
				}
				else
				{
					GroupChatListModel.getInstance( ).saveRecordLocalId( oldFlag , localId );
					GroupChatListModel.getInstance( ).modifyMessageIdAndStatus( mContext , localIdStr , messageIdStr , statusStr , 0 );
				}
				String attachment = chatRecord.getAttachment( );
				// 发送地理位置的时候,attachment放得是经纬度
				if ( type == ChatMessageType.LOCATION || TextUtils.isEmpty( attachment )
						|| attachment.contains( PathUtil.getHTTPPrefix( ) ) )
				{
					CommonFunction.log( TAG, "messagesSend >>"  );
					messagesSend( messageBean );
				}
				else
				{
					CommonFunction.log( TAG, "uploadResourceFile >>"  );
					uploadResourceFile( chatRecord );
				}
			}
			else
			{
				chatRecord.setId( newFlag );
				chatRecord.setDatetime( newFlag );
				if ( chatType == ChatMessageBean.PRIVATE_CHAT )
				{
					// 修改数据的数据id
					ChatPersonalModel.getInstance( ).removeRecordLocalId( oldFlag );
					ChatPersonalModel.getInstance( ).saveRecordLocalId( newFlag, localId );

					try
					{
						ChatPersonalModel.getInstance( ).modifyMessageId( mContext , localIdStr , messageIdStr , statusStr , 0 );
					}
					catch ( JSONException e )
					{
						e.printStackTrace( );
					}
				}
				else
				{
					GroupChatListModel.getInstance( ).removeRecordLocalId( oldFlag );
					GroupChatListModel.getInstance( ).saveRecordLocalId( newFlag , localId );
					GroupChatListModel.getInstance( ).modifyMessageIdAndStatus( mContext , localIdStr , messageIdStr , statusStr , 0 );
				}
				// 旧的文件改名
				String filePath = PathUtil.getAudioDir( ) + chatRecord.getDatetime( ) + PathUtil.getMP3Postfix( );
				File oldFile = new File( chatRecord.getAttachment( ) );
				File newFile = new File( filePath );
				try
				{
					newFile.createNewFile( );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
				oldFile.renameTo( newFile );
				
				getMessageBeanSparseArray( ).remove( oldFlag );
				getMessageBeanSparseArray( ).put( newFlag , messageBean );
				
				putAuidoRecordLength( newFlag , chatRecord.getContent( ) );
				// 重发
				sendAudioMessage( filePath , chatRecord.getDatetime( ) , isResend );
			}
			
		}
		else
		{
			// 图片和视频的发送需要上传的步骤,与其它类型不同;Note:语音的发送不会调用这个方法,单独
			if ( isRepeater )
			{
				// 保存到数据库中
				saveToDatabase( messageBean );
				messagesSend( messageBean );
			}
			else
			{
				if ( type == ChatMessageType.SOUND )
				{
					saveToDatabase( messageBean );
					sendAudioMessage( chatRecord.getAttachment( ) , chatRecord.getId( ) ,
							isResend );
				}
				else if ( type == ChatMessageType.IMAGE || type == ChatMessageType.VIDEO )
				{
					// 保存到文件
					saveToFile( chatRecord );
					// 保存到数据库中
					saveToDatabase( messageBean );
					uploadResourceFile( chatRecord );
					
				}
				else
				{
					// 保存到数据库中
					saveToDatabase( messageBean );
					messagesSend( messageBean );
				}
			}
		}
	}
	
	/*********************** 资源文件类型的操作 *****************************/
	
	/**
	 * 聊天中,上传图片,视频的方法
	 * 
	 * @param record
	 * @param url
	 * @param map
	 * @param flag
	 * @throws ConnectionException
	 */
	public void uploadFile(ChatRecord record , String url , FileProfix fileProfix ,
                           Map< String , String > map , long flag ) throws ConnectionException
	{
		FileUploadManager.createUploadTask( mContext , record.getAttachment( ) , fileProfix ,
				url , map , this , flag ).start( );

		CommonFunction.log( TAG, "聊天上传文件==========" + record.getAttachment( ) );
		CommonFunction.log( TAG, "聊天上传文件==========" + url );
	}
	
	@Override
	public void onUploadFileProgress( int lengthOfUploaded , long flag )
	{

		if ( mHandler != null )
		{
			Map< String , Object > map = new HashMap< String , Object >( );
			map.put( "flag" , flag );
			map.put( "len" , lengthOfUploaded );
			
			Message msg = mHandler.obtainMessage( HandleMsgCode.MSG_UPLOAD_PROGRESS , map );
			mHandler.sendMessage( msg );
		}
		else
		{
			CommonFunction.log( TAG , "onUploadFileProgress mHandler null" );
		}
		
	}
	
	@Override
	public void onUploadFileFinish( long flag , String result )
	{
		CommonFunction.log( TAG , "onUploadFileFinish"  +result);
		
		UploadFileResponse bean = GsonUtil.getInstance( ).getServerBean( result ,
				UploadFileResponse.class );
		
		if ( !bean.isSuccess( ) )
		{
			uploadFailHandle( "" , flag );
			
			if ( mHandler != null )
			{
				Map< String , Object > map = new HashMap< String , Object >( );
				map.put( "flag" , flag );
				
				Message msg = mHandler.obtainMessage( HandleMsgCode.MSG_UPLOAD_ERROR , map );
				mHandler.sendMessage( msg );
			}
			else
			{
				// 这个时候释放了,所以mHandler为空
				CommonFunction.log( TAG , "onUploadFileError mHandler null" );
			}
			return;
		}
		
		ChatRecord record = null;
		if ( getMessageBeanSparseArray( ).get( flag ) != null )
		{
			record = getMessageBeanSparseArray( ).get( flag ).chatRecord;
		}
		else
		{
			return;
		}
		
		int type = Integer.valueOf( record.getType( ) );
		if ( type == ChatMessageType.IMAGE )
		{
			
			String fileDir = PathUtil.getImageLoaderDir( );
			String fileName = CryptoUtil.SHA1( bean.url );
			String targetPath = fileDir + fileName;
			
			writeSourceToFolder( record.getAttachment( ) , targetPath );
			
			String thumSourcePath = getTargetPath( record.getAttachment( ) );
			String thumTargetPath = getTargetPath( bean.url );
			writeSourceToFolder( thumSourcePath , thumTargetPath );
			
			record.setAttachment( bean.url );
		}
		else if ( type == ChatMessageType.VIDEO )
		{
			record.setAttachment( bean.url );
		}
		
		// 这里要拿服务端下发的attachment
		uploadSuccessHandle( bean , flag );
		
		if ( mHandler != null )
		{
			Map< String , Object > map = new HashMap< String , Object >( );
			map.put( "flag" , flag );
			
			Message msg = mHandler.obtainMessage( HandleMsgCode.MSG_UPLOAD_FINISH , map );
			mHandler.sendMessage( msg );
		}
	}
	
	@Override
	public void onUploadFileError(String e , long flag )
	{
		CommonFunction.log( TAG , "onUploadFileError"  +e );
		
		ChatMessageBean bean = getMessageBeanSparseArray( ).get( flag );
		
		if ( bean == null )
			return;
//		if ( bean.chatType == ChatMessageBean.GROUP_CHAT )
		{
			uploadFailHandle( e , flag );
			getMessageBeanSparseArray( ).remove( flag );
			
			if ( mHandler != null )
			{
				Map< String , Object > map = new HashMap< String , Object >( );
				map.put( "flag" , flag );
				
				Message msg = mHandler.obtainMessage( HandleMsgCode.MSG_UPLOAD_ERROR , map );
				mHandler.sendMessage( msg );
			}
			else
			{
				// 这个时候释放了,所以mHandler为空
				CommonFunction.log( TAG , "onUploadFileError" );
			}
		}
	}
	
	/**
	 * 把资源文件写入到相应的文件夹中，复制成功后，删除源文件
	 */
	private void writeSourceToFolder(String sourcePath , String targetPath )
	{
		if ( !TextUtils.isEmpty( sourcePath ) )
		{
			if ( !new File( targetPath ).exists( ) )
			{
				int byteread = 0;
				File sourceFile = new File( sourcePath );
				if ( sourceFile.exists( ) )
				{
					try
					{
						InputStream inStream = new FileInputStream( sourcePath );
						FileOutputStream fs = new FileOutputStream( targetPath );
						byte[ ] buffer = new byte[ 1444 ];
						while ( ( byteread = inStream.read( buffer ) ) != -1 )
						{
							fs.write( buffer , 0 , byteread );
						}
						inStream.close( );
						fs.close( );
						
						sourceFile.delete( );
					}
					catch ( FileNotFoundException e )
					{
						
						e.printStackTrace( );
					}
					catch ( IOException e )
					{
						e.printStackTrace( );
					}
				}
			}
		}
	}
	
	private String getTargetPath(String sourcePath )
	{
		String targetThuPath = CommonFunction.thumPicture( sourcePath );
		String pathDir = PathUtil.getImageLoaderDir( );
		String sha1Name = CryptoUtil.generate( targetThuPath );
		
		String layer1 = sha1Name.substring( 0 , 2 );
		String layer2 = sha1Name.substring( 2 , 4 );
		String parentDir = pathDir + layer1 + "/" + layer2 + "/";
		new File( parentDir ).mkdirs( );
		
		return parentDir + sha1Name;
	}
	
	// 上传资源文件
	private void uploadResourceFile( ChatRecord chatRecord )
	{
		int type = Integer.valueOf( chatRecord.getType( ) );
		long flag = chatRecord.getId( );

		String urlStr = type == ChatMessageType.IMAGE ? Config.sPictureHost
				: Config.sVideoHost;
		FileProfix fileProfix = type == ChatMessageType.IMAGE ? FileProfix.PNG
				: FileProfix.GP3V;
		int uploadType = getFileUploadType( flag );

		Map< String , String > map = new HashMap< String , String >( );
		map.put( "key" , ConnectorManage.getInstance( mContext ).getKey( ) );
		map.put( "type", String.valueOf( type ) );

		File file = new File( chatRecord.getAttachment( ) );


		
		if ( file.exists( ) )
		{
			try
			{
				uploadFile( chatRecord , urlStr , fileProfix , map , flag );
			}
			catch ( ConnectionException e )
			{
				e.printStackTrace( );
				onUploadFileError( "" , flag );
			}
		}
		else
		{
			onUploadFileError( "" , flag );// 上传出错
		}
	}
	
	/*********************** 语音类型的操作 *****************************/
	
	/** 记录语音发送线程 */
	public void PutInSendThread( long flag , SendDataThread thread )
	{
		sendThreadSparseArray.put( flag , thread );
	}
	
	/** 移除语音发送线程 */
	public void RemoveSendThread( long flag )
	{
		sendThreadSparseArray.remove( flag );
	}
	
	/** 记录语音数据发送接收的返回数量 */
	public void PutInReceiveCount( long flag )
	{
		
		if ( receiveCountMap.get( flag ) != null )
		{
			AtomicInteger value = receiveCountMap.get( flag );
			value.addAndGet( 1 );
			receiveCountMap.put( flag , value );
		}
		else
		{
			AtomicInteger beginValue = new AtomicInteger( 0 );
			receiveCountMap.put( flag , beginValue );
		}
	}
	
	/** 获取当前已经接收的语音接收数量 */
	public AtomicInteger GetReceiveCount(long flag )
	{
		AtomicInteger value = null;
		if ( receiveCountMap.get( flag ) != null )
		{
			value = receiveCountMap.get( flag );
		}
		else
		{
			value = new AtomicInteger( 0 );
		}
		return value;
	}
	
	/** 移除语音发送的flag对应的记录 */
	public void RemoveReceiveFlag( long flag )
	{
		if ( receiveCountMap.get( flag ) != null )
		{
			receiveCountMap.remove( flag );
		}
	}
	
	/** 启动对应Flag的发送数据线程 */
	public void StartSendThread( long flag )
	{
		SendDataThread tempThread = sendThreadSparseArray.get( flag );
		if ( tempThread != null && !tempThread.isAlive( ) )
		{
			tempThread.start( );
		}
		else
		{
			CommonFunction.log( TAG , "StartSendThread--不存在" );
		}
	}
	
	/**
	 * 发送语音消息
	 */
	public void sendAudioMessage(String filePath , long recordStartTime , boolean isResend )
	{
		SendDataThread thread = new SendDataThread( filePath , !isResend , recordStartTime ,
				this );
		PutInSendThread( recordStartTime , thread );
		recordingBeginSend( recordStartTime );
	}
	
	/** 通知发送数据的线程,转码已经结束 */
	public void callSendDataThreadCodeEnd( long flag )
	{
		if ( sendThreadSparseArray.get( flag ) != null )
		{
			sendThreadSparseArray.get( flag ).stopThread( );
		}
	}
	
	@Override
	public void DataSendStart( long flag )
	{
		PutInReceiveCount( flag );
	}
	
	@Override
	public void DataSendProgress( long flag , int rank , byte[ ] data , int dataLength )
	{
		recordingDataSend( data , dataLength , flag , rank );
	}
	
	@Override
	public void DataSendFinish( long flag , int rank )
	{
		
		/**
		 * 数据读取完成后 开始检测发送协议的返回是否结束,结束发送最后的结束协议
		 */
		new CheckSendThread( flag , rank , getAudioRecordLength( flag ) ).start( );
	}
	
	@Override
	public void DataSendError( long flag )
	{
		// 数据发送失败,应该修改缓存和数据库的状态
	}
	
	public void putAuidoRecordLength( long flag , String lengthStr )
	{
		audioLengthSparseArray.put( flag , lengthStr );
	}
	
	public String getAudioRecordLength(long flag )
	{
		return audioLengthSparseArray.get( flag );
	}
	
	// 轮询是否接收到所有反馈
	public class CheckSendThread extends Thread
	{
		private long flag;
		private int rank;
		private String audioLength;
		AtomicInteger maxValue;
		
		public CheckSendThread( long flag , int rank , String audioLength )
		{
			this.flag = flag;
			this.rank = rank;
			this.audioLength = audioLength;
			
			maxValue = new AtomicInteger( rank );
		}
		
		@Override
		public void run( )
		{
			
			while ( true )
			{
				if ( maxValue.get( ) == GetReceiveCount( flag ).get( ) )
				{
					recordingEndSend( audioLength , flag , rank );
					RemoveReceiveFlag( flag );
					break;
				}
				
				try
				{
					sleep( 1000 );
				}
				catch ( InterruptedException e )
				{
					
					e.printStackTrace( );
				}
			}
		}
	}
	
	public void clearBind( )
	{
		mContext = null;
		mHandler = null;
	}
	
	/**
	 * 把消息记录保存到文件中
	 * 
	 * @param record
	 */
	private void saveToFile( ChatRecord record )
	{
		String sourceFilePath = record.getAttachment( );
		if ( sourceFilePath.contains( PathUtil.getHTTPPrefix( ) ) )
		{
			return;
		}
		int type = Integer.valueOf( record.getType( ) );
		
		String targetFilePath = sourceFilePath;
		if ( type == ChatMessageType.IMAGE )
		{
			String fileName = String.valueOf( record.getDatetime( ) );
			targetFilePath = PathUtil.getPictureDir( ) + fileName;
			copyFile( sourceFilePath , targetFilePath );// 保存一份在iaround/proPics/目录下
			
			String imageLoaderFilePath = PathUtil.getImageLoaderDir( )
					+ CryptoUtil.SHA1( targetFilePath );
			copyFile( sourceFilePath , imageLoaderFilePath );// 保存一份在/iaround/cacheimage_new目录下
			
			createThumbnail( targetFilePath );
		}
		else if ( type == ChatMessageType.VIDEO )
		{
			// 把视频文件改为以消息的时间命名
			targetFilePath = PathUtil.getVideoDir( ) + record.getDatetime( )
					+ PathUtil.get3GPPostfix( );
			File sourceFile = new File( sourceFilePath );
			File targetFile = new File( targetFilePath );
			
			if ( sourceFile.exists( ) )
			{
				sourceFile.renameTo( targetFile );
			}
		}
		
		record.setAttachment( targetFilePath );
	}
	
	/**
	 * 把一个文件从源路径写到目标路径
	 * 
	 * @param sourcePath
	 * @param targetPath
	 * @return false 复制失败，true 复制成功
	 */
	protected boolean copyFile(String sourcePath , String targetPath )
	{
		if ( !new File( targetPath ).exists( ) )
		{
			int byteread = 0;
			File sourceFile = new File( sourcePath );
			if ( sourceFile.exists( ) )
			{
				try
				{
					InputStream inStream = new FileInputStream( sourcePath );
					FileOutputStream fs = new FileOutputStream( targetPath );
					byte[ ] buffer = new byte[ 1444 ];
					while ( ( byteread = inStream.read( buffer ) ) != -1 )
					{
						fs.write( buffer , 0 , byteread );
					}
					inStream.close( );
					fs.close( );
					return true;
				}
				catch ( FileNotFoundException e )
				{
					
					e.printStackTrace( );
				}
				catch ( IOException e )
				{
					
					e.printStackTrace( );
				}
			}
		}
		
		return false;
	}
	
	/**
	 * 在cacheimage_new/文件夹下生成ImageLoader格式的一个缩略图
	 * 
	 * @param sourcePath
	 * @return 返回缩略图的路径,PathUtil.getImageLoaderDir( )+ CryptoUtil.generate(
	 *         sourcePath_s ),如果失败返回""字符串;
	 */
	protected String createThumbnail(String sourcePath )
	{
		File sourceFile = new File( sourcePath );
		if ( !sourceFile.exists( ) )
		{
			return "";
		}
		
		// 把缩略图，保存到imageLoader的文件夹中去
		String thumbnailPath = getTargetPath( sourcePath );// 缩略图的路径
		
		File thumbFile = new File( thumbnailPath );
		try
		{
			thumbFile.createNewFile( );
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		
		FileOutputStream fos = null;
		ByteArrayOutputStream bos = new ByteArrayOutputStream( );
		try
		{
			fos = new FileOutputStream( thumbFile );
			Bitmap thumbBitmap = CommonFunction.centerSquareScaleBitmap(CommonFunction.createBitmap( sourcePath ) , 136 );
			if(thumbBitmap!=null) {
				thumbBitmap.compress(CompressFormat.PNG, 0, bos);
				byte[] bitmapdata = bos.toByteArray();
				fos.write( bitmapdata );
			}
		}
		catch ( IOException e )
		{
			e.printStackTrace( );
		}
		finally
		{
			try
			{
				fos.close( );
				bos.close( );
			}
			catch ( IOException e )
			{
				e.printStackTrace( );
			}
		}
		
		return thumbnailPath;
	}
	
	/**
	 * 录音开始协议
	 * 
	 * @param flag
	 *            发送的时间标识
	 */
	public void recordingBeginSend( long flag )
	{
		
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		int chatType = messageBean.chatType;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			PrivateChatInfo chatInfo = ( PrivateChatInfo ) messageBean.targetInfo;
			CommonFunction.log( "sherlock" , "recordingBeginSend flag == "+flag );
			SocketSessionProtocol.seesionSendRecordingBegin( mContext , chatInfo.targetUserId ,
					AUDIO_SEND_TYPE , flag , chatInfo.mtype , chatInfo.from );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			GroupChatInfo chatInfo = ( GroupChatInfo ) messageBean.targetInfo;
			SocketGroupProtocol.groupSendRecodingBegin( mContext , chatInfo.getGroupId( ) ,
				AUDIO_SEND_TYPE, flag );
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
		}
		
	}
	
	/**
	 * 语音数据发送协议
	 * 
	 * @param data
	 *            发送的数据
	 * @param length
	 *            数据发送的长度
	 * @param flag
	 *            发送的时间标识
	 */
	public void recordingDataSend( byte[ ] data , int length , long flag , int rank )
	{
		
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		int chatType = messageBean.chatType;
		
		String base64Data = CryptoUtil.Base64Encoder( data , length );
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			SocketSessionProtocol
					.seesionSendRecodingData( mContext , flag , rank , base64Data );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			SocketGroupProtocol.groupSendRecodingData( mContext , flag , rank , base64Data );
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
		}
		
	}
	
	/**
	 * 语音录制结束协议
	 * 
	 * @param audioLength
	 *            音频的长度
	 * @param flag
	 *            发送的时间标识
	 */
	public void recordingEndSend(String audioLength , long flag , int rank )
	{
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		int chatType = messageBean.chatType;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			SocketSessionProtocol
					.seesionSendRecodingEnd( mContext , flag , rank , audioLength );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			SocketGroupProtocol.groupSendRecodingEnd( mContext , flag , rank , audioLength );
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
		}
		
	}
	
	/**
	 * 录音取消协议
	 * 
	 * @param flag
	 *            发送的时间标识
	 */
	public void recordingCancel( long flag )
	{
		
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		// 这个时候有可能还没插入
		if ( messageBean == null )
		{
			return;
		}
		int chatType = messageBean.chatType;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			SocketSessionProtocol.seesionSendRecodingCancel( mContext , flag );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			SocketGroupProtocol.groupSendRecodingCancel( mContext , flag );
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
		}
		
		// 删除本地文件
		File mp3File = new File( PathUtil.getAudioDir( ) + flag + PathUtil.getMP3Postfix( ) );
		if ( mp3File.exists( ) )
		{
			mp3File.delete( );
		}
		
	}
	
	/**
	 * 上传失败处理
	 * 
	 * @param e
	 * @param flag
	 */
	public void uploadFailHandle(String e , long flag )
	{
		
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		int chatType = messageBean.chatType;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			Long localId = ChatPersonalModel.getInstance( ).getRecordLocalId( flag ); // 记录的本地ID
			
			if ( localId != null )
			{
				String arrived = String.valueOf( ChatRecordStatus.FAIL );// 4为失败
				String localIDStr = String.valueOf( localId );
				String msgIDStr = String.valueOf( messageBean.chatRecord.getId( ) );
				
				try
				{
					ChatPersonalModel.getInstance( ).modifyMessageId( mContext , localIDStr ,
							msgIDStr , arrived , ChatPersonalModel.UNKNOWN_DISTANCE );
				}
				catch ( JSONException e1 )
				{
					e1.printStackTrace( );
				}
			}
			else
			{
				CommonFunction.log( TAG , "onUploadFileError----flag==" + flag );
			}//方法注释掉了，刷新消息数目
			FilterUtil.refreshSlidingMenuAndChildViewBadge( );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			Long localId = GroupChatListModel.getInstance( ).getRecordLocalId( flag ); // 记录的本地ID
			
			if ( localId != null )
			{
				String arrived = String.valueOf( ChatRecordStatus.FAIL );// 4为失败
				GroupChatListModel.getInstance( ).modifyGroupMessageStatus( mContext ,
						localId , flag , arrived , 0 );
			}
			else
			{
				CommonFunction.log( TAG , "group-onUploadFileError----flag==" + flag );
			}
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
			
		}
		
	}
	
	/**
	 * 上传成功处理
	 * 
	 * @param bean
	 * @param flag
	 */
	public void uploadSuccessHandle( UploadFileResponse bean , long flag )
	{
		
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		ChatRecord record = messageBean.chatRecord;
		
		int chatType = messageBean.chatType;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			Long localId = ChatPersonalModel.getInstance( ).getRecordLocalId( flag ); // 记录的本地ID
			if ( localId != null )
			{
				ChatPersonalModel.getInstance( ).modifyMessageAttachment( mContext ,
						String.valueOf( localId ) , bean.url );
				record.setAttachment( bean.url );
			}
			else
			{
				CommonFunction.log( TAG , "onUploadFileFinish----flag==" + flag );
			}
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			Long localId = GroupChatListModel.getInstance( ).getRecordLocalId( flag );
			if ( localId != null )
			{
				GroupChatListModel.getInstance( ).modifyGroupMessageAttachment( mContext ,
						localId , bean.url );
				record.setAttachment( bean.url );
			}
			else
			{
				CommonFunction.log( TAG , "group-onUploadFileFinish----flag==" + flag );
			}
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
			
		}
		
		messagesSend( messageBean );
		
	}
	
	/**
	 * 把消息记录保存到数据库中
	 */
	protected void saveToDatabase( ChatMessageBean messageBean )
	{
		
		int chatType = messageBean.chatType;
		ChatRecord record = messageBean.chatRecord;
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			PrivateChatInfo chatInfo = ( PrivateChatInfo ) messageBean.targetInfo;
			
			User user = new User( );
			user.setUid( record.getFuid( ) );
			user.setNickname( record.getfNickName( ) );
			user.setNoteName( record.getfNoteName( true ) );
			user.setIcon( record.getfIconUrl( ) );
			user.setViplevel( record.getfVipLevel( ) );
			user.setSVip( record.getfSVip( ) );
			user.setSex( record.getfSex( ) );
			user.setAge( record.getfAge( ) );
			user.setLat( record.getfLat( ) );
			user.setLng( record.getfLng( ) );
			
			int subgroup;
			if ( chatInfo.mtype == 1 )
			{
				subgroup = SubGroupType.SendAccost;
			}
			else
			{
				subgroup = SubGroupType.NormalChat;
			}
			long localId = ChatPersonalModel.getInstance( ).insertOneRecord( mContext , user ,
					record , subgroup , chatInfo.from );
			record.setLocid( localId );
			
			ChatPersonalModel.getInstance( ).saveRecordLocalId( record.getDatetime( ) ,
					localId );
			
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			GroupChatInfo chatInfo = ( GroupChatInfo ) messageBean.targetInfo;
			
			String dateTime = String.valueOf( record.getDatetime( ) );
			String uidStr = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
			int status = ChatRecordStatus.SENDING;// 初始化状态
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
				userMap.put( "svip" , record.getSVip( ) );
				content.put( "user" , userMap );
				content.put( "datetime" , record.getDatetime( ) );
				content.put( "content" , record.getContent( ) );
				content.put( "attachment" , record.getAttachment( ) );
				content.put( "groupid" , chatInfo.getGroupId( ) );
				contentStr = content.toString( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			
			long localID = GroupChatListModel.getInstance( ).InsertOneRecord( mContext , record.getUid( ),chatInfo.getGroupId( ) , record , contentStr , status );
			record.setLocid( localID );

			GroupModel.getInstance( ).UpdateGroupContact( mContext , uidStr ,
					String.valueOf( chatInfo.getGroupId( ) ) , chatInfo.getGroupName( ) ,
					chatInfo.getGroupIcon( ) , contentStr , dateTime , noReadCount , status ,
					false );
			
			GroupChatListModel.getInstance( ).saveRecordLocalId( record.getDatetime( ) , localID );

			//调试
			CommonFunction.myWriteLog(TAG , FilterUtil.getGroupMsgLog( "saveToDatabase" , localID , chatInfo.getGroupId( ) , 0l , 0l ) );
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
			
		}
		
	}
	
	/**
	 * 发送消息-协议
	 */
	public void messagesSend( ChatMessageBean messageBean )
	{
		
		int chatType = messageBean.chatType;
		ChatRecord chatRecord = messageBean.chatRecord;
		int messageType = Integer.valueOf( chatRecord.getType( ) );
		
		if ( messageType == ChatMessageType.MEET_GIFT )
		{
			chatRecord.setMyGiftStatus( 1 );
		}
		
		
		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			PrivateChatInfo chatInfo = ( PrivateChatInfo ) messageBean.targetInfo;
			long result = SocketSessionProtocol.sessionPrivateMsg( mContext , chatRecord.getDatetime( ) ,
					chatInfo.targetUserId , chatInfo.mtype , chatRecord.getType( ) ,
					chatRecord.getAttachment( ) , chatInfo.from , chatRecord.getContent( ) );
		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			GroupChatInfo chatInfo = ( GroupChatInfo ) messageBean.targetInfo;
			
			SocketGroupProtocol.groupSendMsg( mContext , chatRecord.getDatetime( ) ,
					chatRecord.getType( ) , chatRecord.getAttachment( ) ,
					chatRecord.getContent( ) , String.valueOf( chatInfo.getGroupId( ) ) ,
					chatInfo.getReply( ) );
			
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{
		}
		
		
	}
	
	/**
	 * 获取上传的类型
	 */
	protected int getFileUploadType( long flag )
	{
		ChatMessageBean messageBean = getMessageBeanSparseArray( ).get( flag );
		return messageBean.resourceType;
	}
	
	public void ResendSendingMessage( )
	{
		for ( int i = 0 ; i < getMessageBeanSparseArray( ).size( ) ; i++ )
		{
			
			ChatMessageBean bean = getMessageBeanSparseArray( ).get(
					getMessageBeanSparseArray( ).keyAt( i ) );
			if ( bean.chatType == ChatMessageBean.PRIVATE_CHAT )
			{
				sendMessage( bean , true , false );
			}
		}
	}
	
	public LongSparseArray< ChatMessageBean > getMessageBeanSparseArray( )
	{
		return messageBeanSparseArray;
	}
	
	public void setMessageBeanSparseArray(
			LongSparseArray< ChatMessageBean > messageBeanSparseArray )
	{
		this.messageBeanSparseArray = messageBeanSparseArray;
	}

	/**
	 * 把消息记录保存到数据库中本地以吧主身份插入一条欢迎语，仅当前用户可见
	 */
	public void saveToDatabaseWelcome( ChatMessageBean messageBean )
	{

		int chatType = messageBean.chatType;
		ChatRecord record = messageBean.chatRecord;

		if ( chatType == ChatMessageBean.PRIVATE_CHAT )
		{
			PrivateChatInfo chatInfo = ( PrivateChatInfo ) messageBean.targetInfo;

			User user = new User( );
			user.setUid( record.getFuid( ) );
			user.setNickname( record.getfNickName( ) );
			user.setNoteName( record.getfNoteName( true ) );
			user.setIcon( record.getfIconUrl( ) );
			user.setViplevel( record.getfVipLevel( ) );
			user.setSVip( record.getfSVip( ) );
			user.setSex( record.getfSex( ) );
			user.setAge( record.getfAge( ) );
			user.setLat( record.getfLat( ) );
			user.setLng( record.getfLng( ) );

			int subgroup;
			if ( chatInfo.mtype == 1 )
			{
				subgroup = SubGroupType.SendAccost;
			}
			else
			{
				subgroup = SubGroupType.NormalChat;
			}
			long localId = ChatPersonalModel.getInstance( ).insertOneRecord( mContext , user ,
					record , subgroup , chatInfo.from );
			record.setLocid( localId );

			ChatPersonalModel.getInstance( ).saveRecordLocalId( record.getDatetime( ) ,
					localId );

		}
		else if ( chatType == ChatMessageBean.GROUP_CHAT )
		{
			GroupChatInfo chatInfo = ( GroupChatInfo ) messageBean.targetInfo;

			String dateTime = String.valueOf( record.getDatetime( ) );
			String uidStr = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
			int status = ChatRecordStatus.SENDING;// 初始化状态
			int noReadCount = 0;
			String contentStr = "";
			try
			{
				JSONObject content = new JSONObject( );
				content.put( "msgid" , record.getId( ) );
				content.put( "type" , record.getType( ) );
				JSONObject userMap = new JSONObject( );
				userMap.put( "userid" , record.getFuid() );
				userMap.put( "nickname" , record.getfNickName() );
				userMap.put( "icon" , record.getfIconUrl() );
				userMap.put( "viplevel" , record.getfVipLevel() );
				userMap.put( "svip" , record.getfSVip() );
				content.put( "user" , userMap );
				content.put( "datetime" , record.getDatetime( ) );
				content.put( "content" , record.getContent( ) );
				content.put( "attachment" , record.getAttachment( ) );
				content.put( "groupid" , chatInfo.getGroupId( ) );
				contentStr = content.toString( );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}

			long localID = GroupChatListModel.getInstance( ).InsertOneRecord( mContext ,
					record.getUid( ),chatInfo.getGroupId( ) , record , contentStr , status );
			record.setLocid( localID );


			GroupModel.getInstance( ).UpdateGroupContact( mContext , uidStr ,
					String.valueOf( chatInfo.getGroupId( ) ) , chatInfo.getGroupName( ) ,
					chatInfo.getGroupIcon( ) , contentStr , dateTime , noReadCount , status ,
					false );

			GroupChatListModel.getInstance( ).saveRecordLocalId( record.getDatetime( ) ,
					localID );
			CommonFunction.myWriteLog(
					TAG ,
					FilterUtil.getGroupMsgLog( "saveToDatabase" , localID ,
							chatInfo.getGroupId( ) , 0l , 0l ) );
		}
		else if ( chatType == ChatMessageBean.CHATBAR_CHAT )
		{

		}

	}
}
