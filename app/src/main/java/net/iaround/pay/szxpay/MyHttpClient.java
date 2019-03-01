
package net.iaround.pay.szxpay;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;


/*
 * httpClient初始化
 */
public class MyHttpClient
{
	private static final int TIMEOUT = 10000;
	private static final int TIMEOUT_SOCKET = 15000;
	
	public static final String CTWAP = "ctwap";
	public static final String CMWAP = "cmwap";
	public static final String WAP_3G = "3gwap";
	public static final String UNIWAP = "uniwap";
	
	/** @Fields TYPE_NET_WORK_DISABLED : 网络不可用 */
	public static final int TYPE_NET_WORK_DISABLED = 0;
	/** @Fields TYPE_CM_CU_WAP : 移动联通wap10.0.0.172 */
	public static final int TYPE_CM_CU_WAP = 4;
	/** @Fields TYPE_CT_WAP : 电信wap 10.0.0.200 */
	public static final int TYPE_CT_WAP = 5;
	/** @Fields TYPE_OTHER_NET : 电信,移动,联通,wifi 等net网络 */
	public static final int TYPE_OTHER_NET = 6;
	public static Uri PREFERRED_APN_URI = Uri.parse( "content://telephony/carriers/preferapn" );
	
	private MyHttpClient( )
	{
	}
	
	public static HttpClient getNewInstance(Context mContext )
	{
		HttpClient newInstance;
		
		HttpParams params = new BasicHttpParams( );
		HttpProtocolParams.setVersion( params , HttpVersion.HTTP_1_1 );
		HttpProtocolParams.setContentCharset( params , HTTP.UTF_8 ); // 默认字符集utf8
		HttpProtocolParams.setUseExpectContinue( params , true );
		
		ConnManagerParams.setTimeout( params , 5000 );
		HttpConnectionParams.setConnectionTimeout( params , TIMEOUT );
		HttpConnectionParams.setSoTimeout( params , TIMEOUT_SOCKET );
		
		SchemeRegistry schReg = new SchemeRegistry( );
		schReg.register( new Scheme( "http" , PlainSocketFactory.getSocketFactory( ) , 80 ) );
		schReg.register( new Scheme( "https" , SSLSocketFactory.getSocketFactory( ) , 443 ) );
		
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager( params , schReg );
		newInstance = new DefaultHttpClient( conMgr , params );
		
		// 当是为wap网络类型时，采用代理链接
		switch ( checkNetworkType( mContext ) )
		{
			case TYPE_CT_WAP :
			{
				// 通过代理解决中国移动联通GPRS中wap无法访问的问题
				HttpHost proxy = new HttpHost( "10.0.0.200" , 80 , "http" );
				newInstance.getParams( ).setParameter( ConnRoutePNames.DEFAULT_PROXY , proxy );
			}
				break;
			case TYPE_CM_CU_WAP :
			{
				// 通过代理解决中国移动联通GPRS中wap无法访问的问题
				HttpHost proxy = new HttpHost( "10.0.0.172" , 80 , "http" );
				newInstance.getParams( ).setParameter( ConnRoutePNames.DEFAULT_PROXY , proxy );
			}
				break;
		}
		return newInstance;
	}
	
	public static HttpResponse execute(Context context , HttpUriRequest paramHttpUriRequest )
			throws IOException
	{
		HttpResponse response = getNewInstance( context ).execute( paramHttpUriRequest );
		return response;
	}
	
	/**
	 * 获取Network具体类型
	 * 
	 * @param mContext
	 * @return
	 */
	public static int checkNetworkType( Context mContext )
	{
		try
		{
			final ConnectivityManager connectivityManager = (ConnectivityManager) mContext
					.getSystemService( Context.CONNECTIVITY_SERVICE );
			final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo( );
			if ( networkInfo == null || !networkInfo.isAvailable( ) )
			{
				// 某些机型无法读取，但网络照样可以链接
				return TYPE_NET_WORK_DISABLED;
			}
			else
			{
				int netType = networkInfo.getType( );
				if ( netType == ConnectivityManager.TYPE_WIFI )
				{
					return TYPE_OTHER_NET;
				}
				else if ( netType == ConnectivityManager.TYPE_MOBILE )
				{
					// 电信wap
					final Cursor c = mContext.getContentResolver( ).query( PREFERRED_APN_URI ,
							null , null , null , null );
					if ( c != null )
					{
						c.moveToFirst( );
						final String user = c.getString( c.getColumnIndex( "user" ) );
						if ( !TextUtils.isEmpty( user ) )
						{
							if ( user.startsWith( CTWAP ) )
							{
								return TYPE_CT_WAP;
							}
						}
					}
					c.close( );
					
					// 联通和移动wap
					String netMode = networkInfo.getExtraInfo( );
					if ( netMode != null )
					{
						netMode = netMode.toLowerCase( );
						if ( netMode.equals( CMWAP ) || netMode.equals( WAP_3G )
								|| netMode.equals( UNIWAP ) )
						{
							return TYPE_CM_CU_WAP;
						}
					}
				}
			}
		}
		catch ( Exception ex )
		{
			ex.printStackTrace( );
			return TYPE_OTHER_NET;
		}
		return TYPE_OTHER_NET;
	}
}
