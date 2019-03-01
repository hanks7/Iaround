
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;

import java.util.LinkedHashMap;


/**
 * @Description: 关系业务协议请求
 * @author tanzy
 * @date 2014-10-13
 */
public class RelationHttpProtocol extends HttpProtocol
{
	/**
	 * 业务请求
	 * 
	 * @param url
	 * @param map
	 * @return 返回-1时，表示请求失败
	 */
	public static long relationPost(Context context , String url ,
									LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url , map ,
				ConnectorManage.HTTP_RELATION , callback );
	}
	
	
	/**
	 * 上传手机通讯录 联系人与手机号码
	 * 
	 * @param context
	 * @param content
	 * @param callback
	 * @return
	 */
	public static long getContactNumber(Context context , String content ,
										HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "phonebook" , content );
		return relationPost( context , "/user/phonebook" , entity , callback );
	}
	
	/**
	 * 获取推荐好友列表
	 * 
	 * @author tanzy
	 * */
	public static long getRecommendFriendList(Context context , int pageNo , int pageSize ,
											  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "pageno" , pageNo );
		entity.put( "pagesize" , pageSize );
		return relationPost( context , "/user/recommend" , entity , callback );
	}
	
	/**
	 * 获取用户关系链
	 * 
	 * @author tanzy
	 * */
	public static long getUserRelationLink(Context context , long userid ,
										   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		return relationPost( context , "/user/contact/get_5_5_1" , entity , callback );
	}
	
	/**
	 * @Description: 获取通信录好友列表
	 * @author tanzy
	 * @date 2014-9-19
	 */
	public static long getContactFriendList(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "language" , CommonFunction.getLang( context ) );
		return relationPost( context , "/user/phonebook/friendlist" , entity , callback );
	}
	
	/**
	 * @Description: 获取文案
	 * @author zst
	 * @date 2014-9-19
	 */
	public static long getContactText(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "language" , CommonFunction.getLang( context ) );
		entity.put( "plat" , 1 );
		return relationPost( context , "/text/get" , entity , callback );
	}
	
	/**
	 * @Description:6.0上报通讯录
	 * @author tanzy
	 * @date 2015-5-24
	 */
	public static long uploadContact(Context context , String phonebook ,
									 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "phonebook" , phonebook );
		return relationPost( context , "/user/phonebook_6_0" , entity , callback );
	}
	
}
