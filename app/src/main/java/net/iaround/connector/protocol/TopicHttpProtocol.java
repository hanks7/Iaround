
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.conf.Config;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 话题协议接口
 * 
 * @author linyg
 * @date 2012-8-25 v2.6.0
 * 
 */
public class TopicHttpProtocol extends HttpProtocol
{
	private static int nettype = ConnectorManage.HTTP_GROUP;
	
	public static long topicPost(Context context , String url ,
								 LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context )
				.asynPost( url , map , nettype , callback );
	}
	
	/**
	 * 异步请求发送话题
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param groupid
	 *            圈子id
	 * @param content
	 *            发布话题内容
	 * @param url
	 *            话题附件地址
	 * @param type
	 *            附件类型：1：文本，2：图片，3：声音，4：视频，5.位置
	 * @return -1为请求失败
	 */
	@Deprecated
	public static long addTopic(Context context , String groupid , String content ,
								String url , int type , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "content" , content ); // 话题内容
		map.put( "url" , url ); // 话题附件地址
		map.put( "type" , type ); // 附件类型
		map.put( "plat" , Config.PLAT ); // 平台类型
		return topicPost( context , "/topic/add" , map , callback );
	}
	
	/**
	 * 同步请求发送话题
	 * 
	 * @param context
	 * @param groupid
	 * @param content
	 * @param url
	 * @param type
	 * @return
	 * @throws ConnectionException
	 */
	public static String synPublishTopic(Context context , String groupid , String content ,
										 String url , int type ) throws ConnectionException
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "content" , content ); // 话题内容
		map.put( "url" , url ); // 话题附件地址
		map.put( "type" , type ); // 附件类型
		map.put( "plat" , Config.PLAT ); // 平台类型
		return ConnectorManage.getInstance( context ).syncPost( "/topic/add" , map , nettype );
	}
	
	/**
	 * 发布圈子话题-5.6新街口
	 * @param context
	 * @param groupid
	 * @param content
	 * @param photos 照片url（多张用,号隔开）
	 * @param type 附件类型 1：文本 2：图片 3：声音 4：视频 5.位置 6.礼物提醒
	 * @param address
	 * @param shortAddress
	 * @param lat
	 * @param lng
	 * @param sync
	 * @param syncvalue
	 * @param callback
	 * @return
	 */
	public static long publishGroupTopic(Context context , long groupid, String content, String photos,
										 int type, String address, String shortAddress, int lat, int lng,
										 String sync, String syncvalue, HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "content" , content ); // 话题内容
		entity.put( "photos" , photos ); // 话题附件地址
		entity.put( "type" , type ); // 
		entity.put( "address" , address );
		entity.put( "shortaddress" , shortAddress );
		entity.put( "lat" , lat );
		entity.put( "lng" , lng );
		entity.put( "plat" , Config.PLAT ); // 平台类型
		entity.put( "sync" , sync );
		entity.put( "syncvalue" , syncvalue );
		return topicPost( context , "/topic/add_5_6" , entity , callback );
	}
	
	/**
	 * 删除话题
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @return -1为请求失败
	 */
	public static long delTopic(Context context , String topicid , String groupid ,
								HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/del" , map , callback );
	}
	
	/**
	 * 置顶话题
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @return -1为请求失败
	 */
	public static long topTopic(Context context , String topicid , String groupid ,
								HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/top" , map , callback );
	}
	
	/**
	 * 取消置顶
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 * @return -1为请求失败
	 */
	public static long distopTopic(Context context , String topicid , String groupid ,
								   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/canceltop" , map , callback );
	}
	
	/**
	 * 喜欢话题
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @return -1为网络请求失败
	 */
	public static long likeTopic(Context context , String topicid , String groupid ,
								 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/like" , map , callback );
	}
	
	/**
	 * 取消喜欢话题
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @return -1为请求失败
	 */
	public static long dislikeTopic(Context context , String topicid , String groupid ,
									HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/dislike" , map , callback );
	}
	
	/**
	 * 评论话题或引用评论
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @param parentid
	 *            若空，表示对主贴评论；否则为对评论进行评论
	 * @param content
	 * @return -1为网络请求失败
	 */
	
	public static long reviewTopic(Context context , String groupid,
								   String topicid , String parentid ,
								   String address , String lat, String lng, String content  , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		map.put( "content" , content );
		
		map.put( "address" , address );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		
		map.put( "parentid" , parentid );
		return topicPost( context , "/topic/review/add_5_6" , map , callback );
	}
	
	/**
	 * 删除话题评论
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param reviewid
	 *            评论id
	 * @return -1为请求失败
	 */
	public static long delReviewTopic(Context context , String reviewid ,
									  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "reviewid" , reviewid );
		return topicPost( context , "/topic/review/del" , map , callback );
	}
	
	/**
	 * 话题列表
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param groupid
	 *            圈子id
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页数量
	 * @return -1为请求失败
	 */
	
	public static long listTopic(Context context , String groupid , int pageno ,
								 int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return topicPost( context , "/topic/list_5_6" , map , callback );
	}
	
	/**
	 * 话题评论列表
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页数量
	 * @return -1为请求失败
	 */
	public static long reviewsTopic(Context context , String topicid , int pageno ,
									int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "topicid" , topicid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return topicPost( context , "/topic/reviews" , map , callback );
	}
	
	/**
	 * 称赞话题的用户列表
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param topicid
	 *            话题id
	 * @param lat
	 *            经度
	 * @param lng
	 *            纬度
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页数量
	 * @return -1为请求失败
	 */
	public static long likeUsersTopic(Context context , String topicid , long lat , long lng ,
									  int pageno , int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "topicid" , topicid );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return topicPost( context , "/topic/likeusers" , map , callback );
	}
	
	/**
	 * 获取某个话题详情
	 * 
	 * @param context
	 * @param topicid
	 *            话题id
	 * @return -1请求失败
	 */
	public static long topidDetail(Context context , String topicid , String groupid ,
								   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/detail_5_6" , map , callback );
	}
	
	
	/**
	 * 推荐话题
	 * 
	 * @return -1请求失败
	 */
	
	public static long topidRecommend(Context context  , String groupid,
									  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		return topicPost( context , "/topic/recommend" , map , callback );
	}
	
	/**
	 * 搜索话题列表
	 * 
	 * @param context
	 * @param 
	 * @return -1请求失败
	 */
	
	public static long hotTopicSearch(Context context , String content ,
									  int pageno , int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "content" , content );
		
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return topicPost( context , "/topic/search" , map , callback );
	}
	
	//圈话题精华
	/*setTopicEssence
	 * 将圈话题置为精华
	 */
	public static long setTopicEssence(Context context , String topicid , String groupid ,
									   HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/essence/add" , map , callback );
	}
	
	/*cancelTopicEssence
	 * 取消圈话题置精华
	 */
	public static long cancelTopicEssence(Context context , String topicid , String groupid ,
										  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "topicid" , topicid );
		return topicPost( context , "/topic/essence/cancel" , map , callback );
	}
	
}
