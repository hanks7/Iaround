
package net.iaround.model.entity;

public class FaceAd
{
	public final static int TYPE_FACE = 1;
	public final static int TYPE_WEBVIEW_URL = 2;
	public final static int TYPE_FACE_LIST = 3;
	public final static int TYPE_URL = 4;
	
	private int id;
	private int type;
	private String content;
	private int faceId;
	private String name;
	private String imgUrl;
	private String jumpUrl;
	
	private int currencyType;
	
	public int getId( )
	{
		return id;
	}
	
	public void setId( int id )
	{
		this.id = id;
	}
	
	public int getType( )
	{
		return type;
	}
	
	public void setType( int type )
	{
		this.type = type;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public String getImgUrl( )
	{
		return imgUrl;
	}
	
	public void setImgUrl( String imgUrl )
	{
		this.imgUrl = imgUrl;
	}
	
	public String getName( )
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public int getFaceId( )
	{
		return faceId;
	}
	
	public void setFaceId( int faceId )
	{
		this.faceId = faceId;
	}
	
	public String getJumpUrl( )
	{
		return jumpUrl;
	}
	
	public void setJumpUrl( String jumpUrl )
	{
		this.jumpUrl = jumpUrl;
	}
	
	public int getCurrencyType( )
	{
		return currencyType;
	}
	
	public void setCurrencyType( int currencyType )
	{
		this.currencyType = currencyType;
	}
	
}
