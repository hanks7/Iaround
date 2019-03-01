
package net.iaround.pay.szxpay;


import android.content.Context;
import android.os.Handler;


import net.iaround.tools.CommonFunction;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/**
 * http post 或 get
 * 
 * @date 2012-9-22 上午10:06:35
 * @version V1.0
 */
public class HttpRequest
{
	public static final int BUFFER_SIZE = 4 * 1024;
	// 使用线程池，来重复利用线程，优化内存
	private static Handler mHandler = null;
	private Context mContext;
	private OnRequestStringCallBack stringCallBack;
	private String url;
	private HashMap< String , String > postData = new HashMap< String , String >( );
	
	/**
	 * 实例化请求
	 * 
	 * @param context
	 * @param baseUrl
	 * @param params
	 * @return
	 */
	public static HttpRequest getInstance(Context context , String baseUrl ,
                                          NameValuePair... params )
	{
		String url = baseUrl + concatParams( params );
		HttpRequest request = new HttpRequest( context , url );
		return request;
	}
	
	/**
	 * 实例化请求
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static HttpRequest getInstance(Context context , String url )
	{
		HttpRequest request = new HttpRequest( context , url );
		return request;
	}
	
	// 采用异步请求时，检查当前是否在主线程
	static void checkHandler( )
	{
		try
		{
			if ( mHandler == null )
			{
				mHandler = new Handler( );
			}
		}
		catch ( Exception e )
		{
			CommonFunction.log( new Throwable( ).getStackTrace( )[ 0 ].toString( )
					+ " Exception " , e.getMessage( ) );
			mHandler = null;
		}
	}
	
	/**
	 * 拼装组合参数
	 * 
	 * @param params
	 * @return
	 */
	private static String concatParams(NameValuePair[ ] params )
	{
		if ( params == null || params.length <= 0 )
		{
			return "";
		}
		StringBuilder sb = new StringBuilder( );
		for ( int i = 0 ; i < params.length ; i++ )
		{
			NameValuePair param = params[ i ];
			if ( i == 0 )
			{
				sb.append( "?" );
				sb.append( URLEncoder.encode( param.getName( ) ) + "="
						+ URLEncoder.encode( param.getValue( ) ) );
			}
			else
			{
				sb.append( "&" );
				sb.append( URLEncoder.encode( param.getName( ) ) + "="
						+ URLEncoder.encode( param.getValue( ) ) );
			}
		}
		return sb.toString( );
	}
	
	public HttpRequest(Context context , String url )
	{
		this.mContext = context;
		this.url = url;
	}
	
	/**
	 * 设置post参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setPostValue(String key , String value )
	{
		postData.put( key , value );
	}
	
	/**
	 * 异步请求，需要在主线程
	 * 
	 * @param callBack
	 */
	public void startAsynRequestString( OnRequestStringCallBack callBack )
	{
		checkHandler( );
		setStringCallBack( callBack );
		new Thread(new Runnable( )
		{
			@Override
			public void run( )
			{
				final String content = startSyncRequestString( );
				if ( stringCallBack != null && content != null )
				{
					if ( mHandler != null )
					{
						mHandler.post( new Runnable( )
						{
							@Override
							public void run( )
							{
								stringCallBack.stringLoaded( content );
							}
						} );
					}
					else
					{
						stringCallBack.stringLoaded( content );
					}
				}
			}
		} ).start( );
	}

    /**
	 * 同步请求
	 */
	public InputStream startSynchronous( )
	{
		HttpResponse response = requestHttp( );
		if ( response == null )
		{
			return null;
		}
		try
		{
			int statusCode = response.getStatusLine( ).getStatusCode( );
			if ( statusCode == 200 )
			{
				InputStream is = new BufferedInputStream( response.getEntity( ).getContent( ) );
				return is;
			}
		}
		catch ( Exception e )
		{
			CommonFunction.log( new Throwable( ).getStackTrace( )[ 0 ].toString( )
					+ " Exception " , e.getMessage( ) );
		}
		return null;
	}
	
	/**
	 * @Title: startGetStringSynchronous
	 * @Description:同步请求String
	 * @param @return 传入参数名字
	 * @return String 返回类型
	 * @date 2012-3-23 下午4:26:31
	 * @throw
	 */
	public String startSyncRequestString( )
	{
		InputStream is = startSynchronous( );
		if ( is == null )
		{
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );
		byte[ ] buffer = new byte[ BUFFER_SIZE ];
		int len = 0;
		try
		{
			while ( ( len = is.read( buffer ) ) != -1 )
			{
				baos.write( buffer , 0 , len );
			}
			is.close( );
		}
		catch ( Exception e )
		{
			CommonFunction.log( new Throwable( ).getStackTrace( )[ 0 ].toString( )
					+ " Exception " , e.getMessage( ) );
		}
		return baos.toString( );
	}
	
	/**
	 * http请求
	 * 
	 * @return
	 */
	private HttpResponse requestHttp( )
	{
		if ( url == null || url.equals( "" ) )
		{
			return null;
		}
		HttpResponse response = null;
		try
		{
			if ( postData.size( ) > 0 )
			{ // post数据的方式
				HttpPost request = new HttpPost( url );
				List< NameValuePair > nameValuePairs = new ArrayList< NameValuePair >( );
				HashMap< String , String > params = postData;
				Set< String > keyset = params.keySet( );
				for ( String key : keyset )
				{
					String value = params.get( key );
					nameValuePairs.add( new BasicNameValuePair( key , value ) );
				}
				
				request.setEntity( new UrlEncodedFormEntity( nameValuePairs , "UTF-8" ) );
				response = MyHttpClient.execute( mContext , request );
			}
			else
			{
				HttpGet request = new HttpGet( url );
				response = MyHttpClient.execute( mContext , request );
			}
		}
		catch ( Exception e )
		{
			CommonFunction.log( new Throwable( ).getStackTrace( )[ 0 ].toString( )
					+ " Exception " , e.getMessage( ) );
		}
		return response;
	}
	
	public void setStringCallBack( OnRequestStringCallBack stringCallBack )
	{
		this.stringCallBack = stringCallBack;
	}
	
	public interface OnRequestStringCallBack
	{
		void stringLoaded(String content);
	}
}
