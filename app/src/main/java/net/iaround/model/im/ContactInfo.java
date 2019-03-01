/**
 * Copyright 2010 u6 to MeYou
 *
 * @Description
 * @Date 2010-11-22 上午11:27:26
 * @Author guansj
 * @Version v1.0
 */

package net.iaround.model.im;

public class ContactInfo
{
	private String contactId;
	private String contactName;
	private String contactPhone;
	private long contactUri;
	
	private boolean isCheck;
	
	private boolean isbind;
	private long uid;
	private String icon;
	
	private String nickname;
	private int vip;
	
	/**
	 * @return the contactId
	 */
	public String getContactId( )
	{
		return contactId;
	}
	
	/**
	 * @param contactId
	 *            the contactId to set
	 */
	public void setContactId( String contactId )
	{
		this.contactId = contactId;
	}
	
	/**
	 * @return the contactName
	 */
	public String getContactName( )
	{
		return contactName;
	}
	
	/**
	 * @param contactName
	 *            the contactName to set
	 */
	public void setContactName( String contactName )
	{
		this.contactName = contactName;
	}
	
	/**
	 * @return the contactPhone
	 */
	public String getContactPhone( )
	{
		return contactPhone;
	}
	
	/**
	 * @param contactPhone
	 *            the contactPhone to set
	 */
	public void setContactPhone( String contactPhone )
	{
		this.contactPhone = contactPhone;
	}
	
	/**
	 * @param isCheck
	 *            the isCheck to set
	 */
	public void setCheck( boolean isCheck )
	{
		this.isCheck = isCheck;
	}
	
	/**
	 * @return the isCheck
	 */
	public boolean isCheck( )
	{
		return isCheck;
	}
	
	public long getUid( )
	{
		return uid;
	}
	
	public void setUid( long uid )
	{
		this.uid = uid;
	}
	
	public boolean isIsbind( )
	{
		return isbind;
	}
	
	public void setIsbind( boolean isbind )
	{
		this.isbind = isbind;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public String getNickname( )
	{
		return nickname;
	}
	
	public void setNickname( String nickname )
	{
		this.nickname = nickname;
	}
	
	public int getVip( )
	{
		return vip;
	}
	
	public void setVip( int vip )
	{
		this.vip = vip;
	}
	
	public long getContactUri( )
	{
		return contactUri;
	}
	
	public void setContactUri( long contactUri )
	{
		this.contactUri = contactUri;
	}
}
