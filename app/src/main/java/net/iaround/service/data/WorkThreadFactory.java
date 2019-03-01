
package net.iaround.service.data;


import android.os.Process;

import java.util.concurrent.ThreadFactory;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;


public class WorkThreadFactory implements ThreadFactory
{
	public Thread newThread(Runnable r )
	{
		return new WorkThread( r );
	}
	
	private static class WorkThread extends Thread
	{
		public WorkThread( Runnable r )
		{
			super( r );
		}
		
		@Override
		public void run( )
		{
			Process.setThreadPriority( THREAD_PRIORITY_BACKGROUND );
			super.run( );
		}
	}
}
