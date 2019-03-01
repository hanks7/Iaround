
package net.iaround.database;


import net.iaround.conf.Common;
import net.iaround.tools.CommonFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class ReportLog
{
	private static ReportLog instance;
	private final long MAX_REPORT_TIME = 86400000;
	private final int MAX_REPORT_COUNT = 16;
	private File logFile;
	private HashMap< String , Object > logMap;
	
	public static ReportLog getInstance( )
	{
		if ( instance == null )
		{
			instance = new ReportLog( );
		}
		return instance;
	}
	
	@SuppressWarnings( "unchecked" )
	private ReportLog( )
	{
		// 判断文件
		try
		{
			long uid = Common.getInstance( ).loginUser.getUid( );
			String logPath = CommonFunction.getSDPath( ) + "cacheuser/" + uid + "/.log";
			logFile = new File( logPath );
			if ( !logFile.getParentFile( ).exists( ) )
			{
				logFile.getParentFile( ).mkdirs( );
			}
			if ( !logFile.exists( ) )
			{
				logFile.createNewFile( );
			}
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			if ( logFile != null && logFile.exists( ) )
			{
				logFile.delete( );
			}
			logFile = null;
		}
		
		// 导入数据
		if ( logFile == null || !logFile.exists( ) )
		{
			return;
		}
		
		try
		{
			FileInputStream fis = new FileInputStream( logFile );
			GZIPInputStream gis = new GZIPInputStream( fis );
			ObjectInputStream ois = new ObjectInputStream( gis );
			logMap = (HashMap< String , Object >) ois.readObject( );
			ois.close( );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			logMap = new HashMap< String , Object >( );
		}
	}
	
	/**
	 * 判断是否重复
	 * 
	 * @param report
	 * @return 如果重复，返回true
	 */
	public boolean checkReport( HashMap< String , Object > report )
	{
		if ( logMap == null )
		{
			return false;
		}
		
		if ( report == null || report.isEmpty( ) )
		{
			return false;
		}
		
		@SuppressWarnings( "unchecked" )
		ArrayList< HashMap< String , Object >> reports = (ArrayList< HashMap< String , Object >>) logMap
				.get( "reports" );
		if ( reports == null )
		{
			reports = new ArrayList< HashMap< String , Object >>( );
		}
		
		String targetid = String.valueOf( report.get( "targetid" ) );
		if ( CommonFunction.isEmptyOrNullStr( targetid ) )
		{
			return false;
		}
		
		for ( HashMap< String , Object > item : reports )
		{
			String id = String.valueOf( item.get( "targetid" ) );
			if ( targetid.equals( id ) )
			{
				try
				{
					long time = (Long) item.get( "time" );
					if ( System.currentTimeMillis( ) - time > MAX_REPORT_TIME )
					{
						reports.remove( item );
						return true;
					}
					
					int type = (Integer) item.get( "type" );
					if ( type != (Integer) report.get( "type" ) )
					{
						return false;
					}
					
					if ( type != 2 && type != 4 && type != 6 && type != 11 && type != 12 )
					{
						return false;
					}
					
					int targettype = (Integer) item.get( "targettype" );
					if ( targettype != (Integer) report.get( "targettype" ) )
					{
						return false;
					}
					
					// String content = String.valueOf(item.get("content"));
					// if
					// (!content.equals(String.valueOf(report.get("content"))))
					// {
					// return false;
					// }
				}
				catch ( Throwable t )
				{
					CommonFunction.log( t );
					return false;
				}
				return true;
			}
		}
		return false;
	}
	
	public void logReport( HashMap< String , Object > report )
	{
		if ( logMap == null )
		{
			return;
		}
		
		if ( report == null || report.isEmpty( ) )
		{
			return;
		}
		
		HashMap< String , Object > savedRep = new HashMap< String , Object >( );
		savedRep.putAll( report );
		long time = System.currentTimeMillis( );
		savedRep.put( "time" , time );
		
		@SuppressWarnings( "unchecked" )
		ArrayList< HashMap< String , Object >> reports = (ArrayList< HashMap< String , Object >>) logMap
				.get( "reports" );
		if ( reports == null )
		{
			reports = new ArrayList< HashMap< String , Object >>( );
			logMap.put( "reports" , reports );
		}
		reports.add( savedRep );
		if ( reports.size( ) > MAX_REPORT_COUNT )
		{
			reports.remove( 0 );
		}
		logMap.put( "last" , time );
		
		save( );
	}
	
	private void save( )
	{
		if ( logMap == null )
		{
			logMap = new HashMap< String , Object >( );
		}
		
		if ( logFile == null )
		{
			return;
		}
		
		try
		{
			if ( !logFile.getParentFile( ).exists( ) )
			{
				logFile.getParentFile( ).mkdirs( );
			}
			if ( logFile.exists( ) )
			{
				logFile.delete( );
			}
			logFile.createNewFile( );
			
			FileOutputStream fos = new FileOutputStream( logFile );
			GZIPOutputStream gos = new GZIPOutputStream( fos );
			ObjectOutputStream oos = new ObjectOutputStream( gos );
			oos.writeObject( logMap );
			oos.flush( );
			oos.close( );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
	}
	
}
