package net.iaround.service.data;


import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public abstract class Downloader
{
	public abstract Response getResponse( Request request );

	public abstract String getHead( String key );

	public abstract void release( );

	protected Context mContext;

	public Downloader( Context context )
	{
		mContext = context.getApplicationContext( );
	}

	public static class Request
	{
		private String url;
		private Map< String , String > headers;
		private int connectTimeout = 30 * 1000;
		private int readTimeout = 30 * 1000;

		public Request( String url )
		{
			this.url = url;
			this.headers = new HashMap< String , String >( );
		}

		public String getUrl( )
		{
			return url;
		}

		public void setUrl( String url )
		{
			this.url = url;
		}

		public void addHeader( String key , String value )
		{
			headers.put( key , value );
		}

		public Map< String , String > getHeaders( )
		{
			return headers;
		}

		public int getConnectTimeout( )
		{
			return connectTimeout;
		}

		public void setConnectTimeout( int connectTimeout )
		{
			this.connectTimeout = connectTimeout;
		}

		public int getReadTimeout( )
		{
			return readTimeout;
		}

		public void setReadTimeout( int readTimeout )
		{
			this.readTimeout = readTimeout;
		}
	}

	public static class Response extends LoaderResponse
	{
		private int responseCode;
		private boolean isSupportRange;
		private long responseTime;

		public int getResponseCode( )
		{
			return responseCode;
		}

		public void setResponseCode( int responseCode )
		{
			this.responseCode = responseCode;
		}

		public boolean isSupportRange( )
		{
			return isSupportRange;
		}

		public void setSupportRange( boolean isSupportRange )
		{
			this.isSupportRange = isSupportRange;
		}

		public long getResponseTime( )
		{
			return responseTime;
		}

		public void setResponseTime( long responseTime )
		{
			this.responseTime = responseTime;
		}
	}
}
