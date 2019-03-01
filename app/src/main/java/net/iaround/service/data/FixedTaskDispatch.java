
package net.iaround.service.data;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FixedTaskDispatch implements Dispatch
{
	
	private final ExecutorService executorService;
	
	private static FixedTaskDispatch defaultFixedTaskDispatch;
	
	public static synchronized FixedTaskDispatch getDefault( )
	{
		if ( defaultFixedTaskDispatch == null )
		{
			defaultFixedTaskDispatch = new FixedTaskDispatch( );
		}
		return defaultFixedTaskDispatch;
	}
	
	public FixedTaskDispatch( )
	{
		executorService = Executors.newFixedThreadPool( 2 , new WorkThreadFactory( ) );
	}
	
	@Override
	public void runTask( Runnable task )
	{
		// TODO Auto-generated method stub
		executorService.submit( task );
	}
	
	@Override
	public void shutDown( )
	{
		executorService.shutdown( );
	}
	
}
