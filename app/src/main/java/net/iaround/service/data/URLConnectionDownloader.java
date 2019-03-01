
package net.iaround.service.data;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.text.TextUtils;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


public class URLConnectionDownloader extends Downloader
{
	private HttpURLConnection mHttpURLConnection;
	private Response mResponse;
	
	public static final String CTWAP = "ctwap";
	public static final String CMWAP = "cmwap";
	public static final String WAP_3G = "3gwap";
	public static final String UNIWAP = "uniwap";
	
	/** 网络不可用 */
	public static final int TYPE_NET_WORK_DISABLED = 0;
	public static final int TYPE_WIFI = 1;
	/** 移动联通wap10.0.0.172 */
	public static final int TYPE_CM_CU_WAP = 4;
	/** 电信wap 10.0.0.200 */
	public static final int TYPE_CT_WAP = 5;
	/** 电信,移动,联通 */
	public static final int TYPE_OTHER_NET = 6;
	
	public static Uri PREFERRED_APN_URI = Uri.parse( "content://telephony/carriers/preferapn" );
	
	public URLConnectionDownloader(Context context )
	{
		super( context );
	}
	
	@Override
	public Response getResponse( Request request )
	{
		// TODO Auto-generated method stub
		try
		{
			URL uri = new URL( request.getUrl( ) );
			int type = checkNetworkType( mContext );
			try
			{
				mHttpURLConnection = (HttpURLConnection) uri.openConnection( );
			}
			catch ( Exception e )
			{
				if ( type == TYPE_CM_CU_WAP )
				{
					// "10.0.0.172", 80
					mHttpURLConnection = (HttpURLConnection) uri.openConnection( );
					try
					{
						java.net.Proxy p = new java.net.Proxy( java.net.Proxy.Type.HTTP ,
								new InetSocketAddress( "10.0.0.172" , 80 ) );
						mHttpURLConnection = (HttpURLConnection) uri.openConnection( p );
					}
					catch ( Exception e1 )
					{
						mHttpURLConnection = (HttpURLConnection) uri.openConnection( );
					}
				}
				else if ( type == TYPE_CT_WAP )
				{
					// "10.0.0.200", 80
					try
					{
						java.net.Proxy p = new java.net.Proxy( java.net.Proxy.Type.HTTP ,
								new InetSocketAddress( "10.0.0.200" , 80 ) );
						mHttpURLConnection = (HttpURLConnection) uri.openConnection( p );
					}
					catch ( Exception e2 )
					{
						mHttpURLConnection = (HttpURLConnection) uri.openConnection( );
					}
				}
			}
			
			Map< String , String > headers = request.getHeaders( );
			if ( headers != null )
			{
				Iterator< Entry< String , String >> headerIterator = headers.entrySet( )
						.iterator( );
				while ( headerIterator.hasNext( ) )
				{
					Entry< String , String > header = headerIterator.next( );
					mHttpURLConnection.addRequestProperty( header.getKey( ) ,
							header.getValue( ) );
				}
			}
			mHttpURLConnection.setConnectTimeout( request.getConnectTimeout( ) );
			mHttpURLConnection.setReadTimeout( request.getReadTimeout( ) );
			mHttpURLConnection.setRequestMethod( "GET" );
			
			mResponse = new Response( );
			long starttime = System.nanoTime( );
			mResponse.setResponseCode( mHttpURLConnection.getResponseCode( ) );
			if ( mResponse.getResponseCode( ) == 200 || mResponse.getResponseCode( ) == 206 )
			{
				mResponse.setResponseContent( new FlushedInputStream( mHttpURLConnection
						.getInputStream( ) ) );
				mResponse.setResponseTime( System.nanoTime( ) - starttime );
				mResponse.setContentLength( mHttpURLConnection.getContentLength( ) );
				
				String acceptRange = mHttpURLConnection.getHeaderField( "Accept-Ranges" );
				if ( mResponse.getResponseCode( ) == 206
						|| "bytes".equalsIgnoreCase( acceptRange ) )
				{
					mResponse.setSupportRange( true );
				}
				else
				{
					mResponse.setSupportRange( false );
				}
				return mResponse;
			}
			return null;
		}
		catch ( MalformedURLException e )
		{
			return null;
		}
		catch ( IOException e )
		{
			return null;
		}
	}
	
	/**
	 * 判断网络类型
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
				return TYPE_NET_WORK_DISABLED;
			}
			else
			{
				int netType = networkInfo.getType( );
				if ( netType == ConnectivityManager.TYPE_WIFI )
				{
					return TYPE_WIFI;
				}
				else if ( netType == ConnectivityManager.TYPE_MOBILE )
				{
					// 电信wap
					final Cursor c = mContext.getContentResolver( ).query( PREFERRED_APN_URI ,
							null , null , null , null );
					try
					{
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
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					finally
					{
						if ( c != null )
						{
							c.close( );
						}
					}
				}
			}
		}
		catch ( Exception ex )
		{
			return TYPE_OTHER_NET;
		}
		return TYPE_OTHER_NET;
	}
	
	@Override
	public String getHead(String key )
	{
		// TODO Auto-generated method stub
		if ( mHttpURLConnection != null )
		{
			return mHttpURLConnection.getHeaderField( key );
		}
		return "";
	}
	
	@Override
	public void release( )
	{
		// TODO Auto-generated method stub
		if ( mHttpURLConnection != null )
		{
			mHttpURLConnection.disconnect( );
		}
		
		if ( mResponse != null )
		{
			mResponse.release( );
		}
	}
	
	public static class FlushedInputStream extends FilterInputStream
	{
		private InputStream in;
		
		protected FlushedInputStream( InputStream in )
		{
			super( in );
			this.in = in;
		}
		
		public long skip( long n ) throws IOException
		{
			long m = 0;
			while ( m < n )
			{
				long _m = in.skip( n - m );
				if ( _m == 0 )
				{
					break;
				}
				m += _m;
			}
			return m;
		}
	}
}
