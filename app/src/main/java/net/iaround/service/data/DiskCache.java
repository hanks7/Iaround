
package net.iaround.service.data;


import java.io.IOException;



public interface DiskCache< K , V >
{
	void saveCache(String key, K response) throws IOException;
	
	V getCache(String key);
	
	void clearCache(int maxSize);
	
	void clearCache(String key);
	
	class Response extends LoaderResponse
	{
		private String path;
		
		public String getPath( )
		{
			return path;
		}
		
		public void setPath( String path )
		{
			this.path = path;
		}
		
	}
}
