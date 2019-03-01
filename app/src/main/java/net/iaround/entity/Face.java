
package net.iaround.entity;


import java.io.Serializable;


public class Face implements Serializable
{
	/**
	 *
	 */
	private static final long serialVersionUID = 5717051361012918489L;
	
	private int faceid;
	private int own;
	private String downUrl;
	private int goldNum;
	private String name;
	private int vipgoldnum;
	private String describ;
	private String icon;
	private String background;
	private String image;
	private String title;
	// 贴图是否是新的（1新；0旧）
	private int newflag;
	// 购买类型（1免费；2vip免费；3收费；4参加活动获得；5限时免费；6打折）
	private int feetype;
	private String authorIcon;
	private String authorName;
	private String authorDescribe;
	// 是否动态（1是；0否）
	private int dynamic;
	// 贴图系列标签
	private String tagname;
	// 原价
	private String oldgoldnum;
	// 说明（如：参与遇见女神活动免费获取）
	private String headcontent;
	// 补充说明（如：限时30天免费）
	private String implcontent;
	// 活动地址
	private String activeurl;
	// 是否参加了活动（1参加了；0未参加）
	private int attend;
	// 打开方式（1资料页面；2打开连接；3打开列表；4外部浏览器打开；5只能看）
	private int openType;
	private int currencytype;// 1金币商品　2为钻石商品
	private long startTime;//限时免费开始时间
	private long endTime; //限时免费结束时间
	private int percent;
	private int imagenum;//表情详情预览图个数
	private String baseurl;//预览图址
	
	public void setPercent(int percent){
		
		this.percent = percent;
	}

	public int getPercent(){
		
		return percent == 0 ? 0 : percent;
	}
	
	public void setEndTime(long endTime)
	{
		this.endTime = endTime ;
	}
	
	public long getEndTime()
	{
		return endTime;
	}
	
	public int getCurrencytype( )
	{
		return currencytype;
	}
	
	public void setCurrencytype( int currencytype )
	{
		this.currencytype = currencytype;
	}
	
	public int getFaceid( )
	{
		return faceid;
	}
	
	public void setFaceid( int faceid )
	{
		this.faceid = faceid;
	}
	
	public int getOwn( )
	{
		return own;
	}
	
	public void setOwn( int own )
	{
		this.own = own;
	}
	
	public String getDownUrl( )
	{
		return downUrl;
	}
	
	public void setDownUrl( String downUrl )
	{
		this.downUrl = downUrl;
	}
	
	public int getGoldNum( )
	{
		return goldNum;
	}
	
	public void setGoldNum( int goldNum )
	{
		this.goldNum = goldNum;
	}
	
	public String getName( )
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public int getVipgoldnum( )
	{
		return vipgoldnum;
	}
	
	public void setVipgoldnum( int vipgoldnum )
	{
		this.vipgoldnum = vipgoldnum;
	}
	
	public String getDescrib( )
	{
		return describ;
	}
	
	public void setDescrib( String describ )
	{
		this.describ = describ;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public String getImage( )
	{
		return image;
	}
	
	public void setImage( String image )
	{
		this.image = image;
	}
	
	public String getTitle( )
	{
		return title;
	}
	
	public void setTitle( String title )
	{
		this.title = title;
	}
	
	public String getBackground( )
	{
		return background;
	}
	
	public void setBackground( String background )
	{
		this.background = background;
	}
	
	public int getNewflag( )
	{
		return newflag;
	}
	
	public void setNewflag( int newflag )
	{
		this.newflag = newflag;
	}
	
	public int getFeetype( )
	{
		return feetype;
	}
	
	public void setFeetype( int feetype )
	{
		this.feetype = feetype;
	}
	
	public String getAuthorIcon( )
	{
		return authorIcon;
	}
	
	public void setAuthorIcon( String authorIcon )
	{
		this.authorIcon = authorIcon;
	}
	
	public String getAuthorName( )
	{
		return authorName;
	}
	
	public void setAuthorName( String authorName )
	{
		this.authorName = authorName;
	}
	
	public String getAuthorDescribe( )
	{
		return authorDescribe;
	}
	
	public void setAuthorDescribe( String authorDescribe )
	{
		this.authorDescribe = authorDescribe;
	}
	
	public static long getSerialversionuid( )
	{
		return serialVersionUID;
	}
	
	public int getDynamic( )
	{
		return dynamic;
	}
	
	public void setDynamic( int dynamic )
	{
		this.dynamic = dynamic;
	}
	
	public String getTagname( )
	{
		return tagname;
	}
	
	public void setTagname( String tagname )
	{
		this.tagname = tagname;
	}
	
	public String getOldgoldnum( )
	{
		return oldgoldnum;
	}
	
	public void setOldgoldnum( String oldgoldnum )
	{
		this.oldgoldnum = oldgoldnum;
	}
	
	public String getHeadcontent( )
	{
		return headcontent;
	}
	
	public void setHeadcontent( String headcontent )
	{
		this.headcontent = headcontent;
	}
	
	public String getImplcontent( )
	{
		return implcontent;
	}
	
	public void setImplcontent( String implcontent )
	{
		this.implcontent = implcontent;
	}
	
	public String getActiveurl( )
	{
		return activeurl;
	}
	
	public void setActiveurl( String activeurl )
	{
		this.activeurl = activeurl;
	}
	
	public int getAttend( )
	{
		return attend;
	}
	
	public void setAttend( int attend )
	{
		this.attend = attend;
	}
	
	public int getOpenType( )
	{
		return openType;
	}
	
	public void setOpenType( int openType )
	{
		this.openType = openType;
	}
	
	public int getImagenum( )
	{
		return imagenum;
	}
	
	public void setImagenum( int imagenum )
	{
		this.imagenum = imagenum;
	}
	
	public String getBaseurl( )
	{
		return baseurl;
	}
	
	public void setBaseurl( String baseurl )
	{
		this.baseurl = baseurl;
	}
}
