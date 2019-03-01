
package net.iaround.ui.datamodel;


/**
 * 标签
 * 
 * @author linyg
 * 
 */
public class Tag
{
	private int id;
	private String name;
	private boolean ischeck;
	private boolean isdefault; // 是否为默认标签
	private String color;
	
	public String getColor( )
	{
		return color;
	}
	
	public void setColor( String color )
	{
		this.color = color;
	}
	
	public int getId( )
	{
		return id;
	}
	
	public void setId( int id )
	{
		this.id = id;
	}
	
	public String getName( )
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public boolean ischeck( )
	{
		return ischeck;
	}
	
	public void setIscheck( boolean ischeck )
	{
		this.ischeck = ischeck;
	}
	
	public boolean isIsdefault( )
	{
		return isdefault;
	}
	
	public void setIsdefault( boolean isdefault )
	{
		this.isdefault = isdefault;
	}
}
