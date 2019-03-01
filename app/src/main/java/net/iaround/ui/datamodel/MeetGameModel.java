
package net.iaround.ui.datamodel;


import android.content.Context;
import android.database.Cursor;

import net.iaround.conf.Common;
import net.iaround.conf.MessageID;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.MeetGameWorker;
import net.iaround.model.entity.Basic;
import net.iaround.model.entity.MeetingUser;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 邂逅游戏
 * 
 * @author Administrator
 * 
 */
public class MeetGameModel extends Model
{
	private static MeetGameModel meetGameModel;
	
	private MeetGameModel( )
	{
		
	}
	
	public static MeetGameModel getInstance( )
	{
		if ( meetGameModel == null )
		{
			meetGameModel = new MeetGameModel( );
		}
		return meetGameModel;
	}
	
	/**
	 * 将整个邂逅数据插入数据库
	 * 
	 * @param context
	 * @param result
	 */
	public void insertData(Context context , String result )
	{
		if ( CommonFunction.isEmptyOrNullStr( result ) )
		{
			return;
		}
		try
		{
			JSONObject json = new JSONObject( result );
			if ( json.optInt( "status" ) == 200 )
			{
				if ( CommonFunction.isEmptyOrNullStr( CommonFunction.jsonOptString( json, "wants" ) ) )
				{
					return;
				}
				JSONArray items = json.optJSONArray( "wants" );
				int count = items.length( );
				if ( count > 0 )
				{
					// 每次插入数据时，将原来的邂逅数据都移除掉
					MeetGameWorker db = DatabaseFactory.getMeetGameWorker( context );
					long uid = Common.getInstance( ).loginUser.getUid( );
					db.removeAll( uid );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject item = items.optJSONObject( i );
						if ( item != null )
						{
							User user = parseUser( item.optJSONObject( "user" ) , 2 );
							JSONArray jsonArray = item.getJSONArray( "photos" );
							JSONObject basic = item.getJSONObject( "basic" );
							ArrayList< Object > objects = new ArrayList< Object >( );
							for ( int j = 0 ; j < jsonArray.length( ) ; j++ )
							{
								Object object = jsonArray.get( j );
								objects.add( object );
							}
							if ( objects != null )
							{
								user.setObjects( objects );
							}
							
							long muid = user.getUid( );
							String content = item.toString( );
							long time = System.currentTimeMillis( );
							db.insetMeetData( uid , muid , content , time );
						}
					}
				}
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	/**
	 * 将相见你的人load到内存
	 * 
	 * @param context
	 * @param status
	 *            1已看，0未看
	 * @return
	 */
	public ArrayList<MeetingUser> getMeetGame(Context context , int status )
	{
		ArrayList< MeetingUser > users = new ArrayList< MeetingUser >( );
		MeetGameWorker db = DatabaseFactory.getMeetGameWorker( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		Cursor cursor = db.selectData( uid , status );
		cursor.moveToFirst( );
		while ( ! ( cursor.isAfterLast( ) ) )
		{
			try
			{
				String strJson = cursor.getString( cursor
						.getColumnIndex( MeetGameWorker.M_CONTENT ) );
				JSONObject json;
				json = new JSONObject( strJson );
				JSONObject jsonBasic = json.getJSONObject( "basic" );
				Basic basic = new Basic( );
				basic.setBeginage( jsonBasic.optInt( "beginage" ) );
				basic.setEndage( jsonBasic.optInt( "endage" ) );
				basic.setThinktext( CommonFunction.jsonOptString( jsonBasic,"thinktext" ) );
				basic.setWithwho( jsonBasic.optInt( "withwho" ) );
				String content = CommonFunction.jsonOptString(json, "content" );
				User user = parseUser( json.optJSONObject( "user" ) , 2 );
				JSONArray jsonArray = json.getJSONArray( "photos" );
				ArrayList< Object > objects = new ArrayList< Object >( );
				for ( int j = 0 ; j < jsonArray.length( ) ; j++ )
				{
					Object object = jsonArray.get( j );
					objects.add( object );
				}
				if ( objects != null )
				{
					user.setObjects( objects );
				}
				int compositerate = json.optInt( "compositerate" );
				MeetingUser itemMeet = new MeetingUser( user , basic , content , compositerate );
				users.add( itemMeet );
			}
			catch ( Exception e )
			{
				e.printStackTrace( );
			}
			finally
			{
				cursor.moveToNext( );
			}
		}
		if ( cursor != null )
		{
			cursor.close( );
		}
		return users;
	}
	
	/**
	 * 当邂逅之后，将状态标记为已读
	 * 
	 * @param context
	 * @param muid
	 */
	public void updateMeetStatus(Context context , long muid )
	{
		MeetGameWorker db = DatabaseFactory.getMeetGameWorker( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		db.updateMeetData( uid , muid );
	}
	
	/**
	 * 保存邂逅的数据至sp，用于消息列表显示
	 * */
	public void saveMeetGame(Context context , JSONObject json , int pushID)
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		String key = SharedPreferenceUtil.USER_MEET_DATA + uid;
		if(sp.has( key ))
		{
			String lastPush = sp.getString( key ,"");
			if(!CommonFunction.isEmptyOrNullStr( lastPush ))
			{
				MeetNoticeBean lastBean = GsonUtil.getInstance( ).getServerBean( lastPush , MeetNoticeBean.class );
				if(json.optLong( "time" )>lastBean.time)
				{
					sp.putString( key , json.toString( ) );
					String meetFlag = SharedPreferenceUtil.MEET_GAME_FLAG;
					if ( pushID == MessageID.SESSION_PUSH_MEET )
						sp.putInt( meetFlag + uid , 2 );
					else if ( pushID == MessageID.SESSION_WANT_MEET )
						sp.putInt( meetFlag + uid , 1 );
				}
			}
		}
		else
		{
			sp.putString( key , json.toString( ) );
			String meetFlag = SharedPreferenceUtil.MEET_GAME_FLAG;
			if ( pushID == MessageID.SESSION_PUSH_MEET )
				sp.putInt( meetFlag + uid , 2 );
			else if ( pushID == MessageID.SESSION_WANT_MEET )
				sp.putInt( meetFlag + uid , 1 );
		}
	}
	
	/**
	 * 把邂逅的数量设置为0
	 * 
	 * @param context
	 */
	public void EraseNoReadNum( Context context )
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		String key = SharedPreferenceUtil.USER_MEET_DATA + uid;
		String content = "";
		if ( sp.has( key ) )
		{
			content = sp.getString( key );
			
			JSONObject jsonObject = null;
			try
			{
				jsonObject = new JSONObject( content );
				jsonObject.put( "num" , 0l );
			}
			catch ( JSONException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			sp.putString( key , jsonObject.toString( ) );
		}
		
		
	}
	
	public String getParseJson(Context context )
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		String key = SharedPreferenceUtil.USER_MEET_DATA + uid;
		if ( sp.has( key ) )
		{
			return sp.getString( key );
		}
		return null;
	}
	
	public int getMeetMessageType( Context context )
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		String key = SharedPreferenceUtil.MEET_GAME_FLAG + uid;
		if ( sp.has( key ) )
		{
			return sp.getInt(  key );
		}
		return 0;
	}
	
	
	public void getData( Context context )
	{
		try
		{
			JSONObject jsonObject = new JSONObject( getParseJson( context ) );
			jsonObject.optLong( "time" );
			jsonObject.optLong( "num" );
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
}
