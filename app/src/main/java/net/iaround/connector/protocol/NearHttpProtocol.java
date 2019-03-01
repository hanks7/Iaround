
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.conf.Config;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.im.JobStringArray;
import net.iaround.tools.LocationUtil;

import java.util.LinkedHashMap;


/**
 * 附近有关的接口
 * 
 * @author linyg
 * 
 */
public class NearHttpProtocol extends HttpProtocol
{
	public static long nearPost(Context context , String url ,
                                LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url , map ,
				ConnectorManage.HTTP_NEAR , callback );
	}
	
	/**
	 * 附件用户，地图模式
	 * 
	 * @param context
	 * @param center
	 * @param left
	 * @param right
	 * @param gender
	 * @param datetime
	 * @return
	 * @throws ConnectionException
	 */
//	public static long usersNearmap(Context context , String center , String left ,
//                                    String right , int gender , int datetime , int isvip , int minage , int maxage , int horoscope ,
//                                    int occupation , int love , int dialects , String hometown ,
//                                    HttpCallBack callback ) throws ConnectionException
//	{
//		int sendLove = love==4? 10:love;  //在情感状态中的love_status_s “同性”对应着 <!-- 婚姻状况 -->marriges_s的序号为10
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "center" , center );
//		map.put( "leftdown" , left );
//		map.put( "rightup" , right );
//		map.put( "gender" , gender <= 0 ? "all" : ( gender == 1 ? "m" : "f" ) );
//		map.put( "logintime" , datetime > 0 ? datetime : 0 );
//
//		map.put( "vipquery" , isvip );
//		map.put( "minage" , minage < 0 ? "" : minage );
//		map.put( "maxage" , maxage < 0 ? "" : maxage );
//		map.put( "horoscope" , horoscope == 0 ? "" : horoscope + "" );
//		map.put( "occupation" , occupation <= 0 ? "" : JobStringArray.getInstant( context )
//				.getIDByPosition( occupation - 1 ) + "" );
//		map.put( "love" , sendLove == 0 ? "" : sendLove + "" );
//		map.put( "dialects" , dialects == 0 ? "" : dialects + "" );
//		map.put( "hometown" , hometown );
//
//		return nearPost( context , "/users/nearmap_5_2" , map , callback );
//
//	}
	

	/**
	 * @Title: userNearList
	 * @Description: 5.0 附近用户接口
	 * @param context
	 * @param lat
	 * @param lng
	 * @param sex
	 * @param time
	 * @param minAge
	 *            最小年龄
	 * @param maxAge
	 *            最大年龄
	 * @param constellation
	 *            星座
	 * @param pageno
	 * @param pagesize
	 * @param callback
	 * @return
	 */
	public static long usersNearlist(Context context , int lat , int lng , int sex ,
                                     int time , int minAge , int maxAge , int constellation , int pageno ,
                                     int pagesize , int vipquery , int occupation , int love , int dialects ,
                                     String hometown , int distance , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );

		int sendLove = love==4? 10:love;  //在情感状态中的love_status_s “同性”对应着 <!-- 婚姻状况 -->marriges_s的序号为10
		entity.put( "lat" , lat );
		entity.put( "lng" , lng );
		// 性别
		String mGenderType = "all";
		if ( sex == 1 )
		{
			mGenderType = "m";
		}
		else if ( sex == 2 )
		{
			mGenderType = "f";
		}
		entity.put( "gender" , mGenderType );

		int loginTime = 0;
		switch ( time )
		{
			case 0 :
				loginTime = 15;
				break; // 15m
			case 1 :
				loginTime = 60;
				break; // 60m
			case 2 :
				loginTime = 24 * 60;
				break; // 1day
			case 3 :
				loginTime = 3 * 24 * 60;
				break; // 3day
		}
		entity.put( "logintime" , loginTime );

		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		entity.put( "vipquery" , vipquery );
		entity.put( "minage" , minAge < 0 ? "" : minAge );
		entity.put( "maxage" , maxAge < 0 ? "" : maxAge );
		entity.put( "horoscope" , constellation == 0 ? "" : constellation + "" );
		entity.put( "occupation" , occupation <= 0 ? "" : JobStringArray.getInstant( context )
				.getIDByPosition( occupation - 1 ) + "" );
		entity.put( "love" , sendLove == 0 ? "" : sendLove + "" );
		entity.put( "dialects" , dialects == 0 ? "" : dialects + "" );
		entity.put( "hometown" , hometown );
		entity.put( "distance" , distance == 0 ? "" : distance );

		entity.put("province" , LocationUtil.getCurrentGeo( context ).getProvince( ));
		entity.put("city" ,LocationUtil.getCurrentGeo( context ).getCity( ));

//		return nearPost( context , "/users/nearlist_6_0" , entity , callback );
		return nearPost( context , "/users/nearlist_7_0" , entity , callback );
	}

	
	/**
	 * 推荐好友列表(v6.0)
	 * 
	 * @param context
	 * @return
	 */
	public static long nearRecommend(Context context , int pageno , int pagesize, HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		return nearPost( context , "/users/recommend" , entity , callback );
	}
	/**
	 * 、附近竞价排行，射雕榜
	 *
	 *
	 **/
	public static long usersNearRank(Context context , int lat , int lng, HttpCallBack callback)
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "lat" , lat );
		entity.put( "lng" , lng );
		return nearPost( context , "/bidding/nearRank" , entity , callback );
	}

	/**
	 * 、附近竞价排行，射雕榜
	 *
	 *
	 **/
	public static long usersNearOffer(Context context , int lat , int lng, int offerprice, HttpCallBack callback)
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "lat" , lat );
		entity.put( "lng" , lng );
		entity.put( "offerprice" , offerprice );
		entity.put( "plat" , Config.PLAT );

		return nearPost( context , "/bidding/offer" , entity , callback );
	}
	
}
