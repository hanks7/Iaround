
package net.iaround.service;


import android.content.Context;

import net.iaround.service.data.Dispatch;
import net.iaround.service.data.FixedTaskDispatch;
import net.iaround.service.data.GameTask;
import net.iaround.service.data.RandomDiskCache;
import net.iaround.service.data.URLConnectionDownloader;


public class ApkTaskLoader
{
	private RandomDiskCache mDiskCache;
	private Dispatch mDispatch;
	
	public ApkTaskLoader(RandomDiskCache diskCache , Dispatch dispatch )
	{
		mDiskCache = diskCache;
		mDispatch = dispatch;
	}
	
	public ApkLoadTask createTask(Context context , GameTask gameTask , int from)
	{
		ApkLoadTask task = new ApkLoadTask( new URLConnectionDownloader( context ) ,
				mDiskCache , mDispatch );
		task.setGameTask( gameTask );
		task.setRequestUrl( gameTask.getDownloadUrl( ) );
		return task;
	}
	
	public void releaseDispatch( )
	{
		mDispatch.shutDown( );
	}
	
	public static class Builder
	{
		private RandomDiskCache bDiskCache;
		private Dispatch bDispatch;
		
		public Builder setDispatch( Dispatch dispatch )
		{
			if ( dispatch == null )
			{
				throw new IllegalArgumentException( "Dispatch may not be null." );
			}
			
			bDispatch = dispatch;
			return this;
		}
		
		public Builder setDiskCache( RandomDiskCache diskCache )
		{
			if ( diskCache == null )
			{
				throw new IllegalArgumentException( "DiskCache may not be null." );
			}
			
			bDiskCache = diskCache;
			return this;
		}
		
		public ApkTaskLoader build( Context context )
		{
			ensureSaneDefaults( context.getApplicationContext( ) );
			return new ApkTaskLoader( bDiskCache , bDispatch );
		}
		
		private void ensureSaneDefaults( Context context )
		{
			if ( bDiskCache == null )
			{
				bDiskCache = new RandomDiskCache( context , "/apkcache/" );
			}
			
			if ( bDispatch == null )
			{
				bDispatch = FixedTaskDispatch.getDefault( );
			}
		}
	}
}
