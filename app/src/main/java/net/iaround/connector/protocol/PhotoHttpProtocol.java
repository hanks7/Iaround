
package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;


/**
 * 图片协议接口
 * 
 * @author linyg
 * 
 */
public class PhotoHttpProtocol extends HttpProtocol
{
//	public static long photoPost(Context context , String url ,
//								 LinkedHashMap< String , Object > map , HttpCallBack callback )
//	{
////		return ConnectorManage.getInstance( context ).asynPost( url, map,
////			ConnectorManage.HTTP_PHOTO, callback );
//		return 10000;
//	}
	public static long photoPost( Context context , String url ,
								  LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url, map,
				ConnectorManage.HTTP_PHOTO, callback );
	}
	/**
	 * 最新照片
	 * 
	 * @param context
	 * @param pno
	 *            页码
	 * @param pagesize
	 *            每页数量
	 * @return -1请求失败
	 */
	public static long photoNew(Context context , int pno , int pagesize ,
								HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "pageno" , pno );
		map.put( "pagesize" , pagesize );
		return photoPost( context , "/users/photos" , map , callback );
	}
	
	/**
	 * 热门照片
	 * 
	 * @param context
	 * @param pno
	 *            页码
	 * @param pagesize
	 *            每页数量
	 * @return -1 请求失败
	 */
//	public static long photoHot( Context context , int pno , int pagesize ,
//			HttpCallBack callback )
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "pageno" , pno );
//		map.put( "pagesize" , pagesize );
//		return photoPost( context, "/users/photos/hot", map, callback );
//	}
	

	/**
	 * 获取图片详情
	 * 
	 * @param context
	 * @param photoId
	 *            用户id
	 * @return
	 */
	public static long photoGet(Context context , String photoId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "photoid" , photoId );
		return photoPost( context, "/user/photo/get_1_2", map, callback );
	}
	
	/**
	 * 同步的方式发送相片
	 * 
	 * @param context
	 * @param uid
	 *            发布人uid
	 * @param plat
	 *            发送平台
	 * @param tags
	 *            标签列表
	 * @param bmUrl
	 *            照片url
	 * @param hdUrl
	 *            照片高清url
	 * @param des
	 *            照片描述
	 * @param lat
	 *            经度
	 * @param lng
	 *            纬度
	 * @param address
	 *            发送地址
	 * @param snsids
	 *            分享的sns地址
	 * @return -1发送失败
	 * @throws ConnectionException
	 */
//	public static String sysPublishPhoto(Context context , long uid , int plat , String tags ,
//										 String bmUrl , String hdUrl , String des , int lat , int lng , String address ,
//										 String snsids ) throws ConnectionException
//	{
//		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
//		map.put( "userid" , uid );
//		map.put( "plat" , plat );
//		map.put( "tags" , tags );
//		map.put( "image" , bmUrl );
//		map.put( "hdimage" , hdUrl );
//		map.put( "description" , des );
//		map.put( "lat" , lat );
//		map.put( "lng" , lng );
//		map.put( "address" , address );
//		map.put( "snsids" , snsids );
//		return ConnectorManage.getInstance( context ).syncPost( "/user/photo/add_1_2", map,
//			ConnectorManage.HTTP_PHOTO );
//	}
	
	
	
	
	/**
	 * 异步上传照片到相册
	 * @param context
	 * @param plat 平台 1为android
	 * @param image 图片的url,用,来隔开多个图片:"http://image/1.jpg,http://image/2.jpg"
	 * @param des 照片的描述
	 * @param lat 纬度	
	 * @param lng 经度
	 * @param address 地址
	 * @param sync 同步目标类型（1-贴吧，2-圈子，3-动态）, 5.7版本只能同步到个人动态
	 * @param callback 
	 * @return
	 */
	public static long asynPublishPhoto(Context context , int plat , String image,
										String des , int lat , int lng , String address ,
										String sync , HttpCallBack callback)
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "plat" , plat );
		map.put( "image" , image );
		map.put( "description" , des );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "address" , address );
		map.put( "sync" , sync);
		
		return photoPost( context , "/user/photo/add_5_7" , map , callback );
	}
	
	
	
	
	/**
	 * 删除相片
	 * 
	 * @param context
	 * @param photoId
	 *            相片id
	 * @return
	 */
	public static long photoDel(Context context , String photoId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "photoid" , photoId );
		return photoPost( context , "/user/photo/del" , map , callback );
	}
	
	/**
	 * 对照片发表评论
	 * 
	 * @param context
	 * @param photoid
	 *            照片id
	 * @param parentid
	 *            被引用的评论id
	 * @param content
	 *            评论内容
	 * @return
	 */
	public static long photoReviewAdd(Context context , String photoid , String parentid ,
									  String content , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "photoid" , photoid );
		map.put( "parentid" , parentid );
		map.put( "content" , content );
		return photoPost( context , "/user/photo/review/add" , map , callback );
	}
	

	

	/**
	 * 喜欢的照片列表
	 * 
	 * @param context
	 * @param userid
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public static long photoLove(Context context , long userid , int pageno , int pagesize ,
								 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return photoPost( context , "/user/photos/love" , map , callback );
	}
	
	/**
	 * 获取用户的照片列表
	 * 
	 * @param context
	 * @param userid
	 * @param pageno
	 * @param pagesize
	 * @return
	 */
	public static long photoUser(Context context , long userid , int pageno , int pagesize ,
								 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		return photoPost( context , "/user/photos" , entity , callback );
	}
}
