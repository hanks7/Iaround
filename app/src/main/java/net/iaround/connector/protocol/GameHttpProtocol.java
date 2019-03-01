
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 与游戏有关的协议接口
 * 
 * @author Administrator
 * 
 */
public class GameHttpProtocol extends HttpProtocol
{

	/**
	 * 业务请求
	 *
	 * @param url
	 * @param map
	 * @返回-1时，表示请求失败
	 */
	public static long post(Context context , String url ,
								LinkedHashMap< String , Object > map, HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost(url , map ,
				ConnectorManage.HTTP_GAME , callback );
	}
	

	/**
	 * 获取对方用户最近玩的游戏
	 * 
	 * @param context
	 * @param userid
	 *            对方id
	 * @param language
	 *            语言
	 * @return
	 */
	public static long gameRecentPlay(Context context , long userid , int language , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , ""+userid );
		entity.put( "language" , ""+language );
		entity.put( "gameid" , "" );
		return post( context , "/game/user/recentplay" , entity , callback );
	}

}
