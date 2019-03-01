
package net.iaround.service.data;


import android.content.Context;


import net.iaround.tools.CommonFunction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;


public final class RandomDiskCache implements
		DiskCache< Downloader.Response , DiskCache.Response >
{
	private Context mContext;
	private String mDir;
	private boolean abortSaveFlag;
	
	private static RandomDiskCache defaultRandomDiskCache;
	
	public static synchronized RandomDiskCache getDefault( Context context )
	{
		if ( defaultRandomDiskCache == null )
		{
			defaultRandomDiskCache = new RandomDiskCache( context , "default" );
		}
		return defaultRandomDiskCache;
	}
	
	public RandomDiskCache(Context context , String dir )
	{
		mContext = context;
		mDir = dir;
	}
	
	@Override
	public void saveCache(String key , Downloader.Response response ) throws IOException
	{
		// TODO Auto-generated method stub
		File file = getCacheFile( key );
		if ( file.exists( ) )
		{
			deleteFile( file );
		}
		
		File tmpFile = getTmpFile( key );
		int range = 0;
		if ( tmpFile.exists( ) )
		{
			range = getCacheRange( key );
		}
		
		RandomAccessFile randomAccessFile = null;
		try
		{
			randomAccessFile = new RandomAccessFile( tmpFile , "rw" );
			if ( randomAccessFile.length( ) < response.getContentLength( ) )
			{
				randomAccessFile.setLength( response.getContentLength( ) );
			}
			randomAccessFile.seek( range );
			
			int length = 0;
			byte[ ] buffer = new byte[ 4096 ];
			while ( ( length = response.readResponseContent( buffer ) ) > 0 )
			{
				if ( !abortSaveFlag )
				{
					randomAccessFile.write( buffer , 0 , length );
					range += length;
				}
				else
				{
					// 保存进度
					saveCacheRange( key , range );
					return;
				}
			}
			
			if ( length == 0 )
			{// 没有存入任何数据，存储失败
				deleteFile( tmpFile );
				deleteFile( getInfoFile( key ) );
				return;
			}
			
			if ( tmpFile.renameTo( file ) )
			{
				deleteFile( getInfoFile( key ) );// 删除info文件
			}
			else
			{
				deleteFile( tmpFile );
				deleteFile( getInfoFile( key ) );
			}
		}
		catch ( IOException e )
		{
			saveCacheRange( key , range );
			deleteFile( getCacheFile( key ) );
			throw e;
		}
		finally
		{
			if ( randomAccessFile != null )
			{
				randomAccessFile.close( );
			}
		}
	}
	
	@Override
	public DiskCache.Response getCache( String key )
	{
		// TODO Auto-generated method stub
		File file = getCacheFile( key );
		if ( file.exists( ) )
		{
			DiskCache.Response response = new DiskCache.Response( );
			response.setContentLength( ( int ) file.length( ) );
			response.setPath( file.getAbsolutePath( ) );
			try
			{
				response.setResponseContent( new FileInputStream( file ) );
				return response;
			}
			catch ( IOException e )
			{
				return null;
			}
		}
		return null;
	}
	
	@Override
	public void clearCache( int maxSize )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void clearCache( String key )
	{
		// TODO Auto-generated method stub
		deleteFile( getCacheFile( key ) );
		deleteFile( getInfoFile( key ) );
		deleteFile( getTmpFile( key ) );
	}
	
	public void setAbortSaveFlag( boolean abortSaveFlag )
	{
		this.abortSaveFlag = abortSaveFlag;
	}
	
	public boolean isAbortSaveFlag( )
	{
		return this.abortSaveFlag;
	}
	
	public final int getCacheRange( String key )
	{
		File infoFile = getInfoFile( key );
		if ( infoFile.exists( ) )
		{
			BufferedReader in = null;
			try
			{
				in = new BufferedReader( new FileReader( infoFile ) );
				String str = in.readLine( );
				if ( str != null )
				{
					return Integer.valueOf( str );
				}
			}
			catch ( IOException e )
			{
				return 0;
			}
			catch ( NumberFormatException e )
			{
				return 0;
			}
			finally
			{
				try
				{
					if ( in != null )
					{
						in.close( );
					}
				}
				catch ( IOException e )
				{
				}
			}
		}
		return 0;
	}
	
	public final void saveCacheRange(String key , int range )
	{
		File infoFile = getInfoFile( key );
		deleteFile( infoFile );
		
		BufferedWriter out = null;
		try
		{
			out = new BufferedWriter( new FileWriter( infoFile ) );
			out.write( String.valueOf( range ) );
			out.flush( );
		}
		catch ( IOException e )
		{
			deleteFile( infoFile );
		}
		finally
		{
			if ( out != null )
			{
				try
				{
					out.close( );
				}
				catch ( IOException e )
				{
				}
			}
		}
	}
	
	public File getCacheFile(String key )
	{
		File fileDir = new File( CommonFunction.getSDPath( ) , mDir );
		if ( !fileDir.exists( ) )
		{
			fileDir.mkdirs( );
		}
		
		return new File( fileDir , key );
	}
	
	public File getTmpFile(String key )
	{
		if ( getCacheFile( key ) != null )
		{
			return new File( getCacheFile( key ).getAbsolutePath( ) + ".tmp" );
		}
		else
		{
			return null;
		}
	}
	
	public File getInfoFile(String key )
	{
		if ( getCacheFile( key ) != null )
		{
			return new File( getCacheFile( key ).getAbsolutePath( ) + ".info" );
		}
		else
		{
			return null;
		}
	}
	
	public void deleteFile( File file )
	{
		if ( file != null && file.exists( ) )
		{
			file.delete( );
		}
	}
}
