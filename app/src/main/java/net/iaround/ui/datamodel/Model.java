
package net.iaround.ui.datamodel;


import net.iaround.tools.CommonFunction;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


/**
 * @Description Model层用于处理业务成与数据层（数据库、文件）之间的处理
 * @Note 在5.2版本之前，model层有时用来解析服务端下发的json串，5.2以后对于下发的json串统一用Gson解析。
 */

public class Model
{
	
	/**
	 * 统一解析User
	 * 
	 * @param userJson
	 * @param type
	 *            1：需要解析userid、nickname、notename、icon、vip
	 *            2：解析1的同时，还需要解析gender、lat
	 *            、lng、age、selftext、isonline、lastonlinetime、weibo、occupation
	 * @return
	 */
	public User parseUser(JSONObject userJson , int type )
	{
		User user = new User( );
		if ( userJson != null )
		{
			try
			{
				// 基本资料
				user.setUid( userJson.optLong( "userid" ) );
				user.setNickname( CommonFunction.jsonOptString( userJson,  "nickname" ) );
				user.setNoteName(  CommonFunction.jsonOptString( userJson, "notes" ) );
				user.setIcon( CommonFunction.jsonOptString( userJson,"icon" ) );
				user.setViplevel( userJson.optInt( "viplevel" ) );
				user.setPhotoNum( userJson.optInt( "photonum" ) ) ;
				user.setJob(  userJson.optInt( "occupation" ,-1) );
				
				// if(type == 2 ){
				int sex = 0;
				if ( "f".equals( CommonFunction.jsonOptString(userJson, "gender" ) ) )
				{
					sex = 2;
				}
				else if ( "m".equals( CommonFunction.jsonOptString(userJson, "gender" ) ) )
				{
					sex = 1;
				}
				user.setSex( sex );
				user.setLat( userJson.optInt( "lat" ) );
				user.setLng( userJson.optInt( "lng" ) );
				user.setAge( userJson.optInt( "age" ) );
				user.setBanned( userJson.optInt( "forbid" ) );
				user.setPersonalInfor( CommonFunction.jsonOptString(userJson, "selftext" ) );
				if ( "y".equals( CommonFunction.jsonOptString( userJson,"isonline" ) ) )
				{
					user.setOnline( true );
				}
				else
				{
					user.setOnline( false );
				}
				user.setLastLoginTime( userJson.optLong( "lastonlinetime" ) );
				user.setWeibo( CommonFunction.jsonOptString(userJson, "weibo" ) );
				user.setDistance( userJson.optInt( "distance" ) );
				user.setGamelevel( userJson.optInt( "gamelevel" ) );
				// }
			}
			catch ( Exception e )
			{
				CommonFunction.log( e );
			}
		}
		return user;
	}
	
	/** 把文件转成对象*/
	protected Object getBufferFromFile(String path)
	{
		File bufferFile = new File( path );
		if(bufferFile.exists( ))
		{
			try
			{
				FileInputStream fis = new FileInputStream( bufferFile );
				GZIPInputStream gzis = new GZIPInputStream( fis );
				ObjectInputStream ois = new ObjectInputStream( gzis );
				Object obj = ois.readObject( );
			
				ois.close( );
				gzis.close( );
				fis.close( );
				
				return obj;
			}
			catch ( Exception e )
			{
				// TODO: handle exception
				e.printStackTrace( );
				bufferFile.delete();
				return null;
			}
		}
		return null;
	}
	
	/** 把对象保存成文件*/
	protected boolean saveBufferToFile(String path, Object obj)
	{
		File bufferFile = new File( path );
		
		if(bufferFile.exists( ))
		{
			bufferFile.delete( );
		}
		
		FileOutputStream fos;
		GZIPOutputStream gzos;
		ObjectOutputStream oos;
		try
		{
			fos = new FileOutputStream( bufferFile );
			gzos = new GZIPOutputStream( fos );
			oos = new ObjectOutputStream( gzos );
			oos.writeObject( obj );
			
			oos.flush( );
			oos.close( );
			gzos.close( );
			fos.close( );
		}
		catch ( FileNotFoundException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			bufferFile.delete();
			return false;
		}
		catch ( IOException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			bufferFile.delete();
			return false;
		}
		
		return true;
	}
}
