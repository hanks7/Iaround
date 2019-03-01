/*
 * 文件名: FriendHttpProtocol.java
 * 版    权：  Copyright iAround Tech. Co. Ltd. All Rights Reserved.
 * 描    述: [该类的简要描述]
 * 创建人: yangxl
 * 创建时间:2012-11-5
 *
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */

package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 好友协议接口<BR>
 * [功能详细描述]
 * 
 * @author yangxl
 * @version [iAround Client, 2012-11-5]
 */
public class FriendHttpProtocol extends HttpProtocol
{

	public static long businessPost( Context context , String url ,
									 LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url, map,
				ConnectorManage.HTTP_BUSINESS, callback );
	}
	/**
	 * 好友业务请求
	 * 
	 * @param url
	 * @param map
	 * @return 返回-1时，表示请求失败
	 */
	public static long friendPost(Context context , String url ,
								  LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url , map ,
				ConnectorManage.HTTP_FRIEND , callback );
	}
	
	/**
	 * 好友列表 已更新至2.4版本
	 * 
	 * @param context
	 * @return
	 */
	public static long friendsGet(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return friendPost( context , "/friends/get_2_4" , entity , callback );
	}
	/**
	 * 获取隐私设置各表数据
	 * */
	public static long getPrivacyDataList( Context context , int type , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "type" , type );
		return businessPost( context , "/user/privacy/optionslist" , entity , callback );
	}

	/**
	 * 粉丝列表 ,已更新到2.4接口
	 * 
	 * @param context
	 * @param userid
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public static long userFans(Context context , long userid , int pageno , int pagesize ,
								HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		return friendPost( context , "/user/fans_2_4" , entity , callback );
	}

	/**
	 * 关注列表,已更新至2.4版本
	 * 
	 * @param context
	 * @param userid
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public static long userAttentions(Context context , long userid , int pageno ,
									  int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		return friendPost( context , "/user/attentions_2_4" , entity , callback );
	}
	
	/**
	 * 添加关注 请求响应
	 * 
	 * @param context
	 * @param userid 需要关注的用户的ID
	 * @param contact
	 * @param middleid 中间节点（圈子为圈子id，游戏为游戏id）没有就为空（为0）
	 * @return
	 */
	public static long userFanLove(Context context , long userid ,
								   int contact , long middleid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		entity.put( "contact" , contact );
		entity.put( "middleid" , middleid );
		return friendPost( context , "/user/fan/love_5_5" , entity , callback );
	}
	
	/**
	 * 取消关注数据响应
	 * 
	 * @param context
	 * @param userid
	 * @return
	 */
	public static long userFanDislike(Context context , long userid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		return friendPost( context , "/user/fan/dislike" , entity , callback );
	}
	

	/**
	 * @Title: getFriendsAndAttention
	 * @Description: 获取好友和关注列表
	 * @param context
	 * @return
	 */
	public static long getFriendsAndAttention(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return friendPost( context ,  "/relations/list", entity , callback );
		
	}

	
	/**
	 * @Title: searchAttention
	 * @Description: 通过关键字搜索关注列表
	 * @param context
	 * @param keyword
	 * @param callback
	 * @return
	 */
	public static long searchAttention(Context context , String keyword ,
									   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , keyword );
		return friendPost( context , "/attentions/search" , entity , callback );
	}

	/**
	 * 新增粉丝黑名单用户过滤
	 * @param context
	 * @param userids
	 * @param callback
	 * @return
	 */
	public static long getBlacklistFiltering(Context context , String userids ,
									   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userids" , userids );
		return businessPost( context , "/user/devil/check" , entity , callback );
	}
}
