
package net.iaround.ui.datamodel;


import net.iaround.conf.Common;
import net.iaround.model.im.Me;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * {@link Me}对象缓存工具
 * 
 * @author 余勋杰
 */
public class UserBufferHelper
{
	private static final int MAX_BUFFER_SIZE = 300;
	private static final long MAX_BUFFER_TIME = 7 * 24 * 60 * 60 * 1000; // 最长保存一周
	private static UserBufferHelper instance;
	private String bufferFolder; // 缓存文件夹
	private long oddestBufferdTime; // 最古老记录的缓存时间（是个具体时间，为当前的7天前）
	
	public static UserBufferHelper getInstance( )
	{
		if ( instance == null )
		{
			instance = new UserBufferHelper( );
		}
		return instance;
	}
	
	private UserBufferHelper( )
	{
		oddestBufferdTime = System.currentTimeMillis( ) - MAX_BUFFER_TIME;
		long uid = Common.getInstance( ).loginUser.getUid( );
		bufferFolder = CommonFunction.getSDPath( ) + "cacheuser/" + uid + "/";
		trimOddest( ); // 这行代码理论上执行不到
	}
	
	/**
	 * 当{@link #trimOddest}中的数据总量超过{@link #MAX_BUFFER_SIZE}，
	 * 则迭代删除其中最古老的记录，直到数据量等于 {@link #MAX_BUFFER_SIZE}
	 * 
	 * @return true，如果确实删除了文件
	 * @throws Throwable
	 */
	private void trimOddest( )
	{
		File folderFile = new File( bufferFolder );
		if ( !folderFile.exists( ) )
		{
			return;
		}
		
		File[ ] list = folderFile.listFiles( );
		int size = ( list == null ) ? 0 : list.length;
		int trimTime = size - MAX_BUFFER_SIZE;
		for ( int t = 0 ; t < trimTime ; t++ )
		{
			long oldestFile = Long.MAX_VALUE;
			int odestIndex = -1;
			for ( int i = 0 ; i < size ; i++ )
			{
				long fileTime = list[ i ].lastModified( );
				if ( fileTime < oldestFile )
				{
					oldestFile = fileTime;
					odestIndex = i;
				}
			}
			
			if ( odestIndex >= 0 )
			{
				try
				{
					list[ odestIndex ].delete( );
				}
				catch ( Throwable thr )
				{
					CommonFunction.log( thr );
				}
			}
			oldestFile = Long.MAX_VALUE;
		}
	}
	
	/** 重置内存中缓存数据 */
	public static void reset( )
	{
		if ( instance != null )
		{
			instance = null;
		}
	}
	
	/** 判断是否包含指定id用户 */
	public boolean contains( long uid )
	{
		String md5 = CryptoUtil.md5( String.valueOf( uid ) );
		if ( CommonFunction.isEmptyOrNullStr( md5 ) )
		{
			return false;
		}
		
		File file = new File( bufferFolder + md5 );
		if ( !file.exists( ) )
		{
			return false;
		}
		
		if ( file.lastModified( ) < oddestBufferdTime )
		{
			try
			{
				file.delete( );
			}
			catch ( Throwable t )
			{
				CommonFunction.log( t );
			}
            return false;
		}
		
		return true;
	}
	
	/** 获取一个缓存的{@link Me}对象 */
	public Me read( long uid )
	{
		CommonFunction.log( "buffer" , "requesting user by uid " + uid );
		if ( !contains( uid ) )
		{
			return null;
		}
		
		try
		{
			CommonFunction.log( "buffer" , uid +" is in buffer" );
			String md5 = CryptoUtil.md5( String.valueOf( uid ) );
			FileInputStream fis = new FileInputStream( bufferFolder + md5 );
			GZIPInputStream gzis = new GZIPInputStream( fis );
			ObjectInputStream ois = new ObjectInputStream( gzis );
			Me user = (Me) ois.readObject( );
			ois.close( );
			
			int dataVersion = user.getDataVersion( );
			if ( dataVersion != Me.VERSION )
			{ // 数据版本不对，放弃缓存数据
				user = null;
			}
			
			CommonFunction.log( "buffer" , uid +" is in buffer name =" +user.getNickname( ));
			return user;
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
		
		return null;
	}
	
	/** 缓存{@link Me}对象到文件中 */
	public void save( Me user )
	{
		CommonFunction.log( "buffer","UserBufferHelper ==============================save" );
		if ( user == null )
		{
			return;
		}
		
		String md5 = CryptoUtil.md5( String.valueOf( user.getUid( ) ) );
		if ( CommonFunction.isEmptyOrNullStr( md5 ) )
		{
			return;
		}
		
		File file = new File( bufferFolder + md5 );
		try
		{
			if ( file.exists( ) )
			{
				file.delete( );
			}
			
			File folderFile = file.getParentFile( );
			if ( !folderFile.exists( ) )
			{
				folderFile.mkdirs( );
			}
			
			FileOutputStream fos = new FileOutputStream( file );
			GZIPOutputStream gzos = new GZIPOutputStream( fos );
			ObjectOutputStream oos = new ObjectOutputStream( gzos );
			Me u = user.backUp( );
			u.setDataVersion( Me.VERSION );
			oos.writeObject( u );
			oos.flush( );
			oos.close( );
			CommonFunction.log( "buffer",
					"Add buffer record: " + file.getAbsolutePath( ) );
			
			trimOddest( );
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
			if ( file.exists( ) )
			{
				file.delete( );
			}
		}
	}
	
	/** 从缓存中删除指定uid的{@link Me}对象 */
	public boolean remove( long uid )
	{
		if ( contains( uid ) )
		{
			String md5 = CryptoUtil.md5( String.valueOf( uid ) );
			if ( CommonFunction.isEmptyOrNullStr( md5 ) )
			{
				return false;
			}
			
			try
			{
				File file = new File( bufferFolder + md5 );
				if ( file.exists( ) )
				{
					file.delete( );
				}
				return true;
			}
			catch ( Throwable t )
			{
				CommonFunction.log( t );
			}
		}
		
		return false;
	}
	
}
