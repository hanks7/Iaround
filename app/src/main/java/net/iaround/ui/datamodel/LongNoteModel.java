
package net.iaround.ui.datamodel;


import net.iaround.tools.CommonFunction;
import net.iaround.tools.PathUtil;

import java.util.HashMap;


public class LongNoteModel extends Model
{
	private static LongNoteModel model = null;
	
	public static LongNoteModel getInstent( )
	{
		if ( model == null )
		{
			model = new LongNoteModel( );
		}
		return model;
	}
	
	/**
	 * 获取用户id与长备注的hashMap
	 * */
	@SuppressWarnings( "unchecked" )
	public HashMap< String , String > getLongNoteMap( )
	{
		HashMap< String , String > noteMap = null;
		try
		{
			noteMap = (HashMap< String , String >) getBufferFromFile( PathUtil
					.getLongNoteFilePath( ) );
		}
		catch ( Exception e )
		{
			CommonFunction.log( "sherlock" , "get buffer error" + " ---- "
					+ this.getClass( ).getName( ) );
		}
		
		return noteMap;
	}
	
	/**
	 * 获取对应用户id的长备注
	 * */
	public String getLongNote(String uid )
	{
		HashMap< String , String > map = getLongNoteMap( );
		if ( map == null )
			return "";
		else
			return map.get( uid );
	}
	
	/**
	 * 保存用户id与对应的长备注
	 * */
	public void saveLongNote(String uid , String note )
	{
		HashMap< String , String > map = getLongNoteMap( );
		if ( map == null )
			map = new HashMap< String , String >( );
		
		if ( CommonFunction.isEmptyOrNullStr( note ) )
			map.remove( uid );
		else
			map.put( uid , note );
		saveBufferToFile( PathUtil.getLongNoteFilePath( ) , map );
	}
}
