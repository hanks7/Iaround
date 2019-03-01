
package net.iaround.service.data;


import java.io.IOException;
import java.io.InputStream;


public class LoaderResponse
{
	private int contentLength;
	private InputStream responseContent;
	private ResponseProgress responseProgress;
	
	public int getContentLength( )
	{
		return contentLength;
	}
	
	public void setContentLength( int contentLength )
	{
		this.contentLength = contentLength;
	}
	
	public int readResponseContent( byte[ ] buffer ) throws IOException
	{
		if ( responseContent != null )
		{
			int length = 0;
			if ( ( length = responseContent.read( buffer ) ) != -1 )
			{
				if ( responseProgress != null )
				{
					responseProgress.progress( length );
				}
				return length;
			}
			else
			{
				return -1;
			}
		}
		return 0;
	}
	
	public InputStream getResponseContent( )
	{
		return responseContent;
	}
	
	public void setResponseContent( InputStream responseContent )
	{
		this.responseContent = responseContent;
	}
	
	public void release( )
	{
		if ( responseContent != null )
		{
			try
			{
				responseContent.close( );
			}
			catch ( IOException e )
			{
			}
		}
	}
	
	public ResponseProgress getResponseProgress( )
	{
		return responseProgress;
	}
	
	public void setResponseProgress( ResponseProgress responseProgress )
	{
		this.responseProgress = responseProgress;
	}
	
	public interface ResponseProgress
	{
		void progress(int length);
	}
}
