
package net.iaround.ui.datamodel;


import android.content.Context;


import net.iaround.tools.CommonFunction;
import net.iaround.tools.Hashon;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;


/**
 * 礼物数据体
 * 
 * @author 余勋杰
 */
public class Gift implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6559839982670551751L;
	private User user; // 礼物的接收/发送者
	private int id; // 礼物id
	private int mid; // 私藏礼物标识id
	private int labelBackColor = -46483; // 礼物图标文字背景颜色.默认粉红色
	private String[ ] name; // 国际化的名称{英语，简体中文，繁体中文}
	private String[ ] type;// 礼物类别{英语，简体中文，繁体中文}
	private String[ ] giftdesc; // 国际化的名称{英语，简体中文，繁体中文}
	private String iconUrl; // 图标地址
	private int price; // 礼物价格
	private long time; // 发送/接收时间
	private int charisma; // 所代表的魅力值
	private Object tag; // 额外信息载体
	private int vipLevel; // 可以使用此礼物的会员等级
	private boolean isEmpty; // 标志位，只用于ListView中占据一个空位
	private String flag ; //0：专属，1：优惠，3：新，4：热
	public int flag1 = -1 ;//圖標1
	public int flag2 = -1 ;//圖標2
	public String discountgoldnum;//优惠价格
	public int isNew ;
	public int isHot ;



	/**
	 * 新版礼物字段
	 * @return
     */
//	public String name;//礼物名称
	public String icon;//礼物图片
	public int goldnum;//礼物价格
	public long createTime;//产生时间
	public int charMnum;
	public String color;
	public int giftid;
	public int isnew;
	private int currencytype;// 1金币商品　2为钻石商品
	public String categoryname;//分类名称
	public int viptype;
	public int ishot;
	public int expvalue;//经验值

	public long startTime;
	public long endTime;
	public long charmnum;




	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public int getGoldnum() {
		return goldnum;
	}

	public void setGoldnum(int goldnum) {
		this.goldnum = goldnum;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getGiftid() {
		return giftid;
	}

	public void setGiftid(int giftid) {
		this.giftid = giftid;
	}

	public int getIsnew() {
		return isnew;
	}

	public void setIsnew(int isnew) {
		this.isnew = isnew;
	}

	public int getViptype() {
		return viptype;
	}

	public void setViptype(int viptype) {
		this.viptype = viptype;
	}

	public int getIshot() {
		return ishot;
	}

	public void setIshot(int ishot) {
		this.ishot = ishot;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public int getCharMnum() {
		return charMnum;
	}

	public void setCharMnum(int charMnum) {
		this.charMnum = charMnum;
	}

	public String getCategoryname( )
	{
		return categoryname;
	}
	
	public void setCategoryname( String categoryname )
	{
		this.categoryname = categoryname;
	}
	public String getFlag( )
	{
		return flag;
	}
	
	public void setflag( String flag )
	{
		this.flag = flag;
	}
	public int getCurrencytype( )
	{
		return currencytype;
	}
	
	public void setCurrencytype( int currencytype )
	{
		this.currencytype = currencytype;
	}
	
	public HashMap< String , Object > toMap( )
	{
		HashMap< String , Object > map = new HashMap< String , Object >( );
		try
		{
			Field[ ] flds = getClass( ).getDeclaredFields( );
			if ( flds != null )
			{
				for ( Field fld : flds )
				{
					fld.setAccessible( true );
					Object value = fld.get( this );
					if ( value == null )
					{
						continue;
					}
					
					if ( value.equals( name ) )
					{
						value = Arrays.asList( name );
					}
					else if ( user != null && user.equals( user ) )
					{
						value = user.getUid( );
					}
					else if ( value.equals( tag ) )
					{
						continue;
					}
					map.put( fld.getName( ) , value );
				}
			}
			
			return map;
		}
		catch ( Throwable t )
		{
			CommonFunction.log( t );
		}
		
		return null;
	}
	
	public String toString( )
	{
		return new Hashon( ).fromHashMap( toMap( ) );
	}
	
	public String getDiscountgoldnum( )
	{
		return discountgoldnum;
	}
	
	public void setDiscountgoldnum( String discountgoldnum )
	{
		this.discountgoldnum = discountgoldnum;
	}
	public int getIsHot( )
	{
		return isHot;
	}
	
	public void setIsHot( int isHot )
	{
		this.isHot = isHot;
	}
	public int getIsNew( )
	{
		return isNew;
	}
	
	public void setIsNew( int isNew )
	{
		this.isNew = isNew;
	}

	public int getVipLevel( )
	{
		return vipLevel;
	}
	
	public void setVipLevel( int vipLevel )
	{
		this.vipLevel = vipLevel;
	}
	
	public Object getTag( )
	{
		return tag;
	}
	
	public void setTag( Object tag )
	{
		this.tag = tag;
	}
	
	public int getId( )
	{
		return id;
	}
	
	public User getUser( )
	{
		return user;
	}
	
	public void setUser( User user )
	{
		this.user = user;
	}
	
	public long getTime( )
	{
		return time;
	}
	
	public void setTime( long time )
	{
		this.time = time;
	}
	
	public int getCharisma( )
	{
		return charisma;
	}
	
	public void setCharisma( int charisma )
	{
		this.charisma = charisma;
	}
	public int getExperience( )
	{
		return expvalue;
	}

	public void setExperience( int experience )
	{
		this.expvalue = experience;
	}

	public void setId( int id )
	{
		this.id = id;
	}
	
	public String getName(Context context )
	{
		int index = CommonFunction.getLanguageIndex( context );
		return ( name == null || name.length <= 0 ) ? "" : ( name.length <= index ? name[ 0 ]
				: name[ index ] );
	}
	
	public void setName( String name )
	{
		this.name = name.split( "\\|" );
	}

	public void setGiftdesc( String giftdesc )
	{
		this.giftdesc = giftdesc.split( "\\|" );
	}

	public String getGiftdesc(Context context )
	{
		int index = CommonFunction.getLanguageIndex( context );
		return ( giftdesc == null || giftdesc.length <= 0 ) ? "" : ( giftdesc.length <= index ? giftdesc[ 0 ]
				: giftdesc[ index ] );
	}
	
	public String getIconUrl( )
	{
		return iconUrl;
	}
	
	public void setIconUrl( String iconUrl )
	{
		this.iconUrl = iconUrl;
	}
	
	public int getPrice( )
	{
		return price;
	}
	
	public void setPrice( int price )
	{
		this.price = price;
	}
	
	public int getLabelBackColor( )
	{
		return labelBackColor;
	}
	
	public void setLabelBackColor( int color )
	{
		this.labelBackColor = color;
	}
	
	public boolean isFree( )
	{
		return price <= 0;
	}
	
	public boolean isEmpty( )
	{
		return isEmpty;
	}
	
	public void setEmpty( boolean isEmpty )
	{
		this.isEmpty = isEmpty;
	}
	
	public String getType(Context context )
	{
		int index = CommonFunction.getLanguageIndex( context );
		return ( type == null || type.length <= 0 ) ? "" : ( type.length <= index ? type[ 0 ]
				: type[ index ] );
	}
	
	public void setType( String type )
	{
		this.type = type.split( "\\|" );
	}
	
	public int getMid( )
	{
		return mid;
	}
	
	public void setMid( int mid )
	{
		this.mid = mid;
	}
	
}
