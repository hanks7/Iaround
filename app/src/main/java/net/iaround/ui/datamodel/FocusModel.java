
package net.iaround.ui.datamodel;


import android.content.Context;

import net.iaround.R;
import net.iaround.model.entity.Quote;
import net.iaround.model.entity.Review;
import net.iaround.tools.CommonFunction;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 聚焦数据源
 * 
 * @author linyg
 * 
 */
public class FocusModel extends Model
{
	private static FocusModel focusModel;
	private LinkedHashMap< Integer , Tag > tags = new LinkedHashMap< Integer , Tag >( );
	public boolean islike = false;
	private String language = "";
	public int recommondFocusPage = 0;
	
	private FocusModel( )
	{
		// this.language = language;
	}
	
	private void loadTag( Context context )
	{
		String[ ] strTags = context.getResources( ).getStringArray( R.array.tags );
		if ( strTags != null )
		{
			int count = strTags.length;
			for ( int i = 0 ; i < count ; i++ )
			{
				String[ ] strTag = strTags[ i ].split( "," );
				Tag tag = new Tag( );
				tag.setId( Integer.parseInt( strTag[ 0 ] ) );
				tag.setName( strTag[ 1 ] );
				tags.put( Integer.parseInt( strTag[ 0 ] ) , tag );
			}
		}
	}
	
	public static FocusModel getInstance( )
	{
		if ( focusModel == null )
		{
			focusModel = new FocusModel( );
		}
		return focusModel;
	}
	
	/**
	 * 好友动态
	 * 
	 * @param userid
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @time 2011-6-17 下午06:24:03
	 * @author:linyg
	 */
	public LinkedHashMap< String , Object > doFriendPhotos(String userid , String pageno ,
														   String pagesize )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return map;
	}
	
	/**
	 * 解析好友动态数据
	 * 
	 * @param strJson
	 * @return 数据解析出错，则返回null.否则返回Map
	 * @time 2011-6-20 上午10:10:04
	 * @author:linyg
	 */
	public Map< String , Object > onFriendPhotos(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{ // 请求正确
				// 页码
				map.put( "pageno" , json.optInt( "pageno" ) );
				map.put( "pagesize" , json.optInt( "pagesize" ) );
				map.put( "amount" , json.optInt( "amount" ) );
				
				// 每条动态
				JSONArray jsonArr = json.optJSONArray( "frienddynamic" );
				if ( jsonArr != null )
				{
					ArrayList< Blog > blogs = new ArrayList< Blog >( );
					int count = jsonArr.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject blogJson = jsonArr.getJSONObject( i );
						Blog blog = new Blog( );
						blog.setPhontoid( CommonFunction.jsonOptString( blogJson,"photoid" ) );
						
						// 用户
						User user = null;
						JSONObject userJson = blogJson.optJSONObject( "user" );
						if ( userJson != null )
						{
							user = parseUser( userJson , 1 );
						}
						blog.setUser( user );
						blog.setAddress( CommonFunction.jsonOptString(blogJson, "address" ) );
						blog.setDate( blogJson.optLong( "datetime" ) );
						blog.setImage( CommonFunction.jsonOptString(blogJson, "image" ) );
						blog.setContent( CommonFunction.jsonOptString(blogJson, "content" ) );
						blog.setLikecount( blogJson.optInt( "likecount" ) );
						blog.setCurruserlike( CommonFunction.jsonOptString( blogJson,"curruserlike" ) );
						blog.setReviewcount( blogJson.optInt( "reviewcount" ) );
						blog.setPlat( CommonFunction.jsonOptString( blogJson,"platname" ) );
						blogs.add( blog );
					}
					map.put( "bloglist" , blogs );
				}
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
			
		}
		catch ( JSONException e )
		{ // 解析出错
			e.printStackTrace( );
			return null;
		}
		
		return map;
	}
	
	/**
	 * 解析推荐相片数据
	 * 
	 * @param strJson
	 * @return 若解析出错，则返回null;否则返回Map
	 * @time 2011-6-20 上午10:13:24
	 * @author:linyg
	 */
	public Map< String , Object > onHotPhoto(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		JSONObject json;
		try
		{
			json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				map.put( "pageno" , json.optInt( "pageno" ) );
				map.put( "pagesize" , json.optInt( "pagesize" ) );
				map.put( "amount" , json.optInt( "amount" ) );
				
				JSONArray jsonArr = json.optJSONArray( "photos" );
				ArrayList< Blog > blogs = new ArrayList< Blog >( );
				ArrayList< String > ids = new ArrayList< String >( );
				if ( jsonArr != null )
				{
					int count = jsonArr.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject blogJson = jsonArr.getJSONObject( i );
						Blog blog = new Blog( );
						blog.setPhontoid( CommonFunction.jsonOptString( blogJson,"photoid" ) );
						blog.setImage( CommonFunction.jsonOptString(blogJson, "image" ) );
						blog.setIshdimage( blogJson.optInt( "ishdimage" ) == 0 );
						blogs.add( blog );
						ids.add( blog.getPhontoid( ) );
					}
				}
				map.put( "ids" , ids );
				map.put( "hotlist" , blogs );
				
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
			return null;
		}
		return map;
	}
	
	/**
	 * 解析最新相片数据
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-6-20 上午10:30:37
	 * @author:linyg
	 */
	public Map< String , Object > onNewPhoto(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		JSONObject json;
		try
		{
			json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				map.put( "pageno" , json.optInt( "pageno" ) );
				map.put( "pagesize" , json.optInt( "pagesize" ) );
				map.put( "amount" , json.optInt( "amount" ) );
				
				JSONArray jsonArr = json.optJSONArray( "photos" );
				ArrayList< Blog > blogs = new ArrayList< Blog >( );
				if ( jsonArr != null )
				{
					int count = jsonArr.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject blogJson = jsonArr.getJSONObject( i );
						Blog blog = new Blog( );
						blog.setPhontoid( CommonFunction.jsonOptString(blogJson, "photoid" ) );
						blog.setImage( CommonFunction.jsonOptString(blogJson, "image" ) );
						blog.setIshdimage( blogJson.optInt( "ishdimage" ) == 0 );
						blogs.add( blog );
					}
				}
				map.put( "newlist" , blogs );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
			return null;
		}
		return map;
	}
	
	/**
	 * 标签列表数据解析
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-6-20 上午10:25:19
	 * @author:linyg
	 */
	public Map< String , Object > onTag(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		JSONObject json;
		try
		{
			json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				JSONArray jsonArr = json.optJSONArray( "tags" );
				ArrayList< Tag > tags = new ArrayList< Tag >( );
				if ( jsonArr != null )
				{
					int count = jsonArr.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject blogJson = jsonArr.getJSONObject( i );
						Tag tag = new Tag( );
						tag.setId( blogJson.optInt( "id" ) );
						tag.setName( CommonFunction.jsonOptString( blogJson,"tagname" ) );
						if ( CommonFunction.jsonOptString( blogJson,"defaulttag" ).equals( "y" ) )
						{
							tag.setIsdefault( true );
						}
						else
						{
							tag.setIsdefault( false );
						}
						tags.add( tag );
					}
				}
				map.put( "taglist" , tags );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
			return null;
		}
		return map;
	}
	
	/**
	 * 评论列表
	 * 
	 * @param photoid
	 * @param pageno
	 * @param pagesize
	 * @return
	 * @time 2011-6-20 上午10:32:00
	 * @author:linyg
	 */
	public LinkedHashMap< String , Object > doReview(String photoid , String pageno ,
													 String pagesize )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "photoid" , photoid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return map;
	}
	
	/**
	 * 解析评论数据
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-6-20 上午10:54:11
	 * @author:linyg
	 */
	public Map< String , Object > onReview(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		JSONObject json;
		try
		{
			json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				// 页码
				map.put( "pageno" , json.optInt( "pageno" ) );
				map.put( "pagesize" , json.optInt( "pagesize" ) );
				map.put( "amount" , json.optInt( "amount" ) );
				
				JSONArray jsonArr = json.optJSONArray( "reviews" );
				
				ArrayList<Review> reviews = new ArrayList< Review >( );
				if ( jsonArr != null )
				{
					int count = jsonArr.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject reviewJson = jsonArr.getJSONObject( i );
						Review review = new Review( );
						
						// 用户
						JSONObject userjson = reviewJson.getJSONObject( "user" );
						review.setUser( parseUser( userjson , 2 ) );
						
						review.setDate( reviewJson.optLong( "datetime" ) );
						review.setContent( CommonFunction.jsonOptString(reviewJson, "content" ) );
						review.setId( CommonFunction.jsonOptString(reviewJson, "reviewid" ) );
						
						// 引用
						JSONObject quoteJson = reviewJson.optJSONObject( "quote" );
						if ( quoteJson != null )
						{
							Quote quote = new Quote( );
							quote.setUserid( quoteJson.optLong( "userid" ) );
							quote.setNickname( CommonFunction.jsonOptString( quoteJson, "nickname") );
							quote.setQuoteid( CommonFunction.jsonOptString(quoteJson, "quoteid" ) );
							quote.setContent( CommonFunction.jsonOptString( quoteJson,"qcontent" ) );
							quote.setStatus( quoteJson.optInt( "status" ) );
							review.setQuote( quote );
						}
						review.setStatus( reviewJson.optInt( "status" ) );
						reviews.add( review );
					}
				}
				map.put( "reviewlist" , reviews );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
			return null;
		}
		return map;
	}
	
	/**
	 * 标签
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-6-24 下午03:00:24
	 * @author:linyg
	 */
	public Map< String , Object > onHandTag(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				ArrayList< Tag > tags = new ArrayList< Tag >( );
				JSONArray tagsjson = json.optJSONArray( "tags" );
				if ( tagsjson != null )
				{
					for ( int i = 0 ; i < tagsjson.length( ) ; i++ )
					{
						JSONObject tagjson = tagsjson.getJSONObject( i );
						Tag tag = new Tag( );
						tag.setId( tagjson.optInt( "tagid" ) );
						tag.setName( CommonFunction.jsonOptString(tagjson, "tagname" ) );
						tags.add( tag );
					}
				}
				map.put( "tags" , tags );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
			
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		return map;
	}
	
	/**
	 * 相片详情
	 * 
	 * @param strJson
	 * @return
	 * @time 2011-6-24 下午03:47:17
	 * @author:linyg
	 */
	public Map< String , Object > onPhotoDetail(String strJson )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		JSONObject json;
		try
		{
			json = new JSONObject( strJson );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				Blog blog = new Blog( );
				blog.setPhontoid( CommonFunction.jsonOptString(json, "photoid" ) );
				
				// 用户
				JSONObject userJson = json.optJSONObject( "user" );
				if ( userJson != null )
				{
					blog.setUser( parseUser( userJson , 2 ) );
				}
				// 内容
				blog.setPlat( CommonFunction.jsonOptString(json, "platname" ) );
				blog.setAddress( CommonFunction.jsonOptString(json, "address" ) );
				blog.setDate( json.optLong( "datetime" ) );
				blog.setImage( CommonFunction.jsonOptString(json, "image" ) );
				blog.setHdimage( CommonFunction.jsonOptString(json, "hdimage" ) );
				blog.setContent( CommonFunction.jsonOptString(json, "content" ) );
				blog.setLikecount( json.optInt( "likecount" ) );
				blog.setReviewcount( json.optInt( "reviewcount" ) );
				blog.setCurruserlike( CommonFunction.jsonOptString(json, "curruserlike" ) );
				blog.setBrowsetimes( CommonFunction.jsonOptString( json,"browsetimes" ) );
				// 评论
				ArrayList< Review > reviews = new ArrayList< Review >( );
				JSONArray jsonReviews = json.optJSONArray( "reviews" );
				if ( jsonReviews != null )
				{
					for ( int i = 0 ; i < jsonReviews.length( ) ; i++ )
					{
						Review review = new Review( );
						JSONObject jsonReview = jsonReviews.getJSONObject( i );
						// 评论内容
						review.setId( CommonFunction.jsonOptString( jsonReview,"reviewid" ) );
						review.setDate( jsonReview.optLong( "datetime" ) );
						review.setContent( CommonFunction.jsonOptString( jsonReview,"content" ) );
						review.setStatus( jsonReview.optInt( "status" ) );
						// 评论用户
						
						JSONObject revUserJson = jsonReview.optJSONObject( "user" );
						if ( revUserJson != null )
						{
							review.setUser( parseUser( revUserJson , 2 ) );
						}
						// 评论引用
						JSONObject quoJson = jsonReview.optJSONObject( "quote" );
						if ( quoJson != null )
						{
							Quote quote = new Quote( );
							quote.setQuoteid( CommonFunction.jsonOptString(quoJson, "quoteid" ) );
							quote.setUserid( quoJson.optLong( "userid" ) );
							quote.setNickname( CommonFunction.jsonOptString( quoJson, "nickname") );
							quote.setContent( CommonFunction.jsonOptString( quoJson,"qcontent" ) );
							quote.setStatus( quoJson.optInt( "status" ) );
							review.setQuote( quote );
						}
						reviews.add( review );
					}
					blog.setReviews( reviews );
				}
				map.put( "blog" , blog );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		return map;
	}
	
	/**
	 * 解析相片称赞数据
	 * 
	 * @param strResult
	 * @return
	 * @time 2011-6-27 上午11:30:10
	 * @author:linyg
	 */
	public Map< String , Object > onLove(String strResult )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strResult );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				map.put( "lovecount" , json.optInt( "lovecount" ) );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		
		return map;
	}
	
	/**
	 * 解析喜欢的用户列表
	 * 
	 * @param strResult
	 * @return
	 * @time 2011-9-22 下午04:28:08
	 * @author:linyg
	 */
	public Map< String , Object > onParamMemberList(String strResult )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strResult );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status == 200 )
			{
				map.put( "pageno" , json.optInt( "pageno" ) );
				map.put( "pagesize" , json.optInt( "pagesize" ) );
				map.put( "amount" , json.optInt( "amount" ) );
				
				ArrayList< User > users = new ArrayList< User >( );
				JSONArray userJsons = json.optJSONArray( "loveusers" );
				if ( userJsons != null )
				{
					int count = userJsons.length( );
					for ( int i = 0 ; i < count ; i++ )
					{
						JSONObject memberJson = userJsons.getJSONObject( i );
						JSONObject userJson = memberJson.optJSONObject( "user" );
						User user = parseUser( userJson , 2 );
						user.setOnlineTime( memberJson.optLong( "time" ) );// 用onlinetime暂时存储称赞时间
						user.setPersonalInfor( CommonFunction.jsonOptString( memberJson,"sign" ) );
						user.setDistance( memberJson.optInt( "distance" ) );
						users.add( user );
					}
				}
				map.put( "users" , users );
			}
			else
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		return map;
	}
	
	/**
	 * 删除相片
	 * 
	 * @param strResult
	 * @return
	 * @time 2011-6-27 下午04:23:28
	 * @author:linyg
	 */
	public Map< String , Object > onDelBlog(String strResult )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strResult );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status != 200 )
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		return map;
	}
	
	/**
	 * 删除评论
	 * 
	 * @param strResult
	 * @return
	 * @time 2011-6-27 下午04:24:55
	 * @author:linyg
	 */
	public Map< String , Object > onDelReview(String strResult )
	{
		Map< String , Object > map = new HashMap< String , Object >( );
		try
		{
			JSONObject json = new JSONObject( strResult );
			int status = json.optInt( "status" );
			map.put( "status" , status );
			if ( status != 200 )
			{
				map.put( "error" , json.optInt( "error" ) );
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
		return map;
	}
	
	/**
	 * 获取本地标签名，如不存在则使用原始的
	 * 
	 * @param id
	 * @param name
	 * @return
	 * @time 2011-8-29 下午02:22:05
	 * @author:linyg
	 */
	public String getTag(int id , String name , Context context )
	{
		String country = context.getResources( ).getConfiguration( ).locale.getCountry( );
		// 当语言发生变化时，重新加载
		if ( !country.equals( this.language ) )
		{
			this.language = country;
			this.loadTag( context );
		}
		
		if ( tags != null && tags.containsKey( id ) )
		{
			return tags.get( id ).getName( );
		}
		else
		{
			return name;
		}
	}
	
	/** 重置 **/
	public void reset( )
	{
		focusModel = null;
	}
}
