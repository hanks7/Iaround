
package net.iaround.service.data;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;


import net.iaround.tools.CryptoUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


public class FileLoadTask implements Runnable
{
	
	/**
	 * 任务的6种状态，等待/运行中/取消/暂停/完成/错误。
	 */
	public static class TaskStatus
	{
		public static int Waiting = 0;
		public static int Running = 0;
		public static int Cancel = 0;
		public static int Pause = 0;
		public static int Complete = 0;
		public static int Error = 0;
//		Waiting ,
//		Running ,
//		Cancel ,
//		Pause ,
//		Complete ,
//		Error
	}
	
	/**
	 * 结果来源，磁盘缓存/网络/未知
	 */
	public static class ResponseSource
	{
		public static int DiskCache = 0;
		public static int Network = 0;
		public static int None = 0;
//		DiskCache ,
//		Network ,
//		None
	}
	
	private final static int WHAT_START = 0;
	private final static int WHAT_PROGRESS = 1;
	private final static int WHAT_PAUSE = 2;
	private final static int WHAT_CANCEL = 3;
	private final static int WHAT_COMPLETE = 4;
	private final static int WHAT_ERROR = 5;
	private final static int WHAT_NEXT = 6;
	
	// 网络访问重试次数
	private final static int MAX_RETRY_COUNT = 3;
	
	private Downloader mDownloader;
	private RandomDiskCache mDiskCache;
	private Dispatch mDispatch;
	private int mRetryCount = MAX_RETRY_COUNT;
	private String mUrl;
	private File mResponse;// 当前任务结果
	private int mContentLength;// 当前任务长度
	private int mContentProgress;// 当前任务进度
	private boolean isSupportProgress = true;// 客户端是否支持进度
	private boolean isSupportRange = false;// 服务器是否支持断点续传
	
	private final MainHandler mHandler = new MainHandler( );
	
	private int mStatus = TaskStatus.Waiting;
	private int mSource = ResponseSource.None;
	
	private boolean mRunFlag = false;
	private boolean mCancelFlag = false;
	private ConcurrentLinkedQueue<ITaskProgress< FileLoadTask >> mProgressHolders = new ConcurrentLinkedQueue< ITaskProgress< FileLoadTask >>( );
	
	protected FileLoadTask(Downloader downloader, RandomDiskCache diskCache, Dispatch dispatch)
	{
		if ( downloader == null )
		{
			throw new IllegalArgumentException( "Downloader may not be null." );
		}
		
		if ( diskCache == null )
		{
			throw new IllegalArgumentException( "DiskCache may not be null." );
		}
		
		if ( dispatch == null )
		{
			throw new IllegalArgumentException( "Dispatch may not be null." );
		}
		
		mDownloader = downloader;
		mDiskCache = diskCache;
		mDispatch = dispatch;
	}
	
	public String getKey( )
	{
		return CryptoUtil.md5( mUrl );
	}
	
	public String getRequestUrl( )
	{
		return mUrl;
	}
	
	public void setRequestUrl( String url )
	{
		mUrl = url;
	}
	
	public int getTaskStatus( )
	{
		return mStatus;
	}
	
	public int getResponseSource( )
	{
		return mSource;
	}
	
	public boolean addProgressListener( ITaskProgress< FileLoadTask > listener )
	{
		if ( listener != null && !mProgressHolders.contains( listener ) )
		{
			return mProgressHolders.add( listener );
		}
		return false;
	}
	
	public void cancelAllProgressListener( )
	{
		mProgressHolders.clear( );
	}
	
	public boolean cancelProgressListener( ITaskProgress< FileLoadTask > listener )
	{
		if ( listener != null )
		{
			return mProgressHolders.remove( listener );
		}
		return false;
	}
	
	public void cancelTask( )
	{
		if ( mRunFlag )
		{
			// 正在运行则退出下载
			mCancelFlag = true;
			mDiskCache.setAbortSaveFlag( true );
		}
		else
		{
			// 没在运行则发送取消消息
			mCancelFlag = true;
			sendMessage( WHAT_CANCEL );
		}
	}
	
	public void pauseTask( )
	{
		if ( mRunFlag )
		{
			mDiskCache.setAbortSaveFlag( true );
		}
	}
	
	public void resumeTask( )
	{
		submit( );
	}
	
	public void submit( String url )
	{
		setRequestUrl( url );
		submit( );
	}
	
	public void submit( )
	{
		if ( TextUtils.isEmpty( mUrl ) )
		{
			throw new IllegalArgumentException( "Request Url may not be null." );
		}
		
		try
		{
			new URL( mUrl );
		}
		catch ( MalformedURLException e )
		{
			throw new IllegalArgumentException( "Request Url may not be available." );
		}
		
		if ( mRunFlag )
		{
			// 任务正在运行
			return;
		}
		
		init( );
		
		submitTaskToRunPool( this );
	}
	
	private void init( )
	{
		mRetryCount = MAX_RETRY_COUNT;
		mSource = ResponseSource.None;
		mStatus = TaskStatus.Waiting;
		mResponse = null;
		mCancelFlag = false;
		mContentLength = 0;
		mContentProgress = 0;
		mDiskCache.setAbortSaveFlag( false );
	}
	
	@Override
	public final void run( )
	{
		if ( mCancelFlag )
		{
			return;
		}
		
		mRunFlag = true;
		sendMessage( WHAT_START );
		
		DiskCache.Response diskResponse = null;
		Downloader.Response netResponse = null;
		try
		{
			diskResponse = mDiskCache.getCache( getKey( ) );
			mContentProgress = mDiskCache.getCacheRange( getKey( ) );
			
			if ( diskResponse != null )
			{
				mResponse = new File( diskResponse.getPath( ) );
				mSource = ResponseSource.DiskCache;
				sendMessage( WHAT_COMPLETE );
				return;
			}
			
			Downloader.Request request = new Downloader.Request( mUrl );
			if ( mContentProgress > 0 )
			{
				request.addHeader( "Range" , "bytes=" + mContentProgress + "-" );
			}
			
			while ( mRetryCount-- > 0 )
			{
				netResponse = mDownloader.getResponse( request );
				if ( netResponse != null )
				{
					break;
				}
			}
			
			if ( netResponse == null )
			{
				// 网络读取错误
				sendMessage( WHAT_ERROR );
				return;
			}
			
			if ( netResponse.isSupportRange( ) )
			{
				this.isSupportRange = true;
			}
			
			if ( isSupportProgress )
			{
				netResponse.setResponseProgress( mResponseProgress );
			}
			
			mContentLength = mContentProgress + netResponse.getContentLength( );
			mDiskCache.saveCache( getKey( ) , netResponse );
			if ( mDiskCache.isAbortSaveFlag( ) )
			{
				if ( mCancelFlag )
				{
					// 取消了任务
					mDiskCache.clearCache( getKey( ) );
					sendMessage( WHAT_CANCEL );
				}
				else
				{
					// 暂停了任务
					if ( !isSupportRange )
					{
						mDiskCache.clearCache( getKey( ) );
					}
					sendMessage( WHAT_PAUSE );
				}
				return;
			}
			
			DiskCache.Response r = mDiskCache.getCache( getKey( ) );
			if ( r != null )
			{
				mResponse = new File( r.getPath( ) );
				mSource = ResponseSource.Network;
				sendMessage( WHAT_COMPLETE );
			}
			else
			{
				sendMessage( WHAT_ERROR );
			}
		}
		catch ( IOException e )
		{
			sendMessage( WHAT_ERROR );
		}
		finally
		{
			if ( diskResponse != null )
			{
				diskResponse.release( );
			}
			if ( netResponse != null )
			{
				netResponse.release( );
			}
			mRunFlag = false;
			sendMessage( WHAT_NEXT );
		}
	}
	
	private LoaderResponse.ResponseProgress mResponseProgress = new LoaderResponse.ResponseProgress( )
	{
		
		@Override
		public void progress( int length )
		{
			mContentProgress += length;
			sendProgressMessage( );
		}
	};
	
	protected void sendProgressMessage( )
	{
		sendMessage( WHAT_PROGRESS );
	}
	
	private void sendMessage( int what )
	{
		mHandler.sendMessage( mHandler.obtainMessage( what , this ) );
	}
	
	private static class MainHandler extends Handler
	{
		public MainHandler( )
		{
			super( Looper.getMainLooper( ) );
		}
		
		@Override
		public void handleMessage( Message msg )
		{
			// TODO Auto-generated method stub
			FileLoadTask task = ( FileLoadTask ) msg.obj;
			if ( task != null )
			{
				switch ( msg.what )
				{
					case WHAT_START :
						task.start( );
						break;
					case WHAT_PROGRESS :
						task.progress( );
						break;
					case WHAT_PAUSE :
						task.pause( );
						break;
					case WHAT_CANCEL :
						task.cancel( );
						break;
					case WHAT_COMPLETE :
						task.complete( );
						break;
					case WHAT_ERROR :
						task.error( );
						break;
					case WHAT_NEXT :
						task.runNextWaittingTask( );
						break;
					default :
						break;
				}
				
				task = null;
				super.handleMessage( msg );
			}
		}
	}
	
	private void start( )
	{
		mStatus = TaskStatus.Running;
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskStart( this );
		}
	}
	
	private void progress( )
	{
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskProgress( this );
		}
	}
	
	private void cancel( )
	{
		mStatus = TaskStatus.Cancel;
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskCancel( this );
		}
	}
	
	private void pause( )
	{
		mStatus = TaskStatus.Pause;
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskPause( this );
		}
	}
	
	private void complete( )
	{
		mStatus = TaskStatus.Complete;
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskComplete( this );
		}
	}
	
	private void error( )
	{
		mStatus = TaskStatus.Error;
		Iterator< ITaskProgress< FileLoadTask >> iterator = mProgressHolders.iterator( );
		while ( iterator.hasNext( ) )
		{
			ITaskProgress< FileLoadTask > iTaskProgress = iterator.next( );
			iTaskProgress.onTaskError( this );
		}
	}
	
	private final static byte[ ] _tasksQueueLock = new byte[ 0 ];// 用锁来保证同步
	private final static HashMap< String , FileLoadTask > _runTaskMap = new HashMap< String , FileLoadTask >( );
	private final static Queue< FileLoadTask > _waittingTaskList = new LinkedList< FileLoadTask >( );
	
	private void submitTaskToRunPool( FileLoadTask task )
	{
		// TODO Auto-generated method stub
		synchronized ( _tasksQueueLock )
		{
			// 提交到任务池
			String key = task.getKey( );
			if ( !_runTaskMap.containsKey( key ) )
			{
				_runTaskMap.put( key , task );
				mDispatch.runTask( task );
			}
			else
			{
				_waittingTaskList.offer( task );
			}
		}
	}
	
	private void runNextWaittingTask( )
	{
		// TODO Auto-generated method stub
		synchronized ( _tasksQueueLock )
		{
			_runTaskMap.remove( getKey( ) );
			FileLoadTask task = _waittingTaskList.poll( );
			if ( task != null )
			{
				task.submit( );
			}
		}
	}
	
	public int getContentLength( )
	{
		return mContentLength;
	}
	
	public int getContentProgress( )
	{
		return mContentProgress;
	}
	
	public void setDiskCache( RandomDiskCache diskCache )
	{
		mDiskCache = diskCache;
	}
	
	public boolean isSupportRange( )
	{
		return isSupportRange;
	}
	
	public void setSupportProgress( boolean issupport )
	{
		isSupportProgress = issupport;
	}
	
	public File getResponse( )
	{
		return mResponse;
	}
}
