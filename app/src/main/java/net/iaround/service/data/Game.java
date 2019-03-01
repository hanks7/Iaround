
package net.iaround.service.data;


import java.io.Serializable;


/**
 * 游戏实体,注意，在这里添加字段后，需要在{@link net.iaround.ui.game.GameTask}
 * 的copyWithGame方法中做一次赋值操作
 */
public class Game implements Serializable
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6303979893841789619L;

	private int gameId;// 游戏id
	
	private String name;// 游戏名称
	private String icon;// 游戏logo
	private String appid;// 包名
	private int platform;// 适用平台
	private int versionCode;// 版本号
	private String versionName;// 版本名称
	private String size;// 大小
	private long createTime; // 账号同步时间
	private long bindingTime; // 账号同步时间
	private String introduce;// 游戏介绍
	private String downloadUrl;// 下载地址
	private int downloadCount;// 下载次数
	private int visitCount;// 访问次数
	private int fansCount;// 粉丝数
	private int groupId;// 圈ID
	private boolean fan;// 是否已关注过
	private String bannerImg;// 展示图
	private String developers;
	private String content;// 有多少个朋友在玩之类的信息
	private String shareContent;// 分享文字
	private String shareImage;// 分享图
	private ScreenShot[ ] screenshots;// 截图
	private long groupCreaterUid;
	private int osVersionCode;
	private String osVersionName;
	
	private boolean newflag;// 新游戏标识
	
	private long gamePacketId;
	
	private int plat;//平台类型
	
	public static class ScreenShot
	{
		private String icon;
		private int id;
		
		public ScreenShot( )
		{
		}
		
		public ScreenShot( int id , String icon )
		{
			this.id = id;
			this.icon = icon;
		}
		
		public String getIcon( )
		{
			return icon;
		}
		
		public void setIcon( String icon )
		{
			this.icon = icon;
		}
		
		public int getId( )
		{
			return id;
		}
		
		public void setId( int id )
		{
			this.id = id;
		}
	}
	
	public int getGameId( )
	{
		return gameId;
	}
	
	public void setGameId( int gameId )
	{
		this.gameId = gameId;
	}
	
	public int getPlatform( )
	{
		return platform;
	}
	
	public void setPlatform( int platform )
	{
		this.platform = platform;
	}
	
	public int getVersionCode( )
	{
		return versionCode;
	}
	
	public void setVersionCode( int versionCode )
	{
		this.versionCode = versionCode;
	}
	
	public String getVersionName( )
	{
		return versionName;
	}
	
	public void setVersionName( String versionName )
	{
		this.versionName = versionName;
	}
	
	public String getSize( )
	{
		return size;
	}
	
	public void setSize( String size )
	{
		this.size = size;
	}
	
	public long getCreateTime( )
	{
		return createTime;
	}
	
	public void setCreateTime( long createTime )
	{
		this.createTime = createTime;
	}
	
	public long getBindingTime( )
	{
		return bindingTime;
	}
	
	public void setBindingTime( long bindingTime )
	{
		this.bindingTime = bindingTime;
	}
	
	public String getIntroduce( )
	{
		return introduce;
	}
	
	public void setIntroduce( String introduce )
	{
		this.introduce = introduce;
	}
	
	public int getVisitCount( )
	{
		return visitCount;
	}
	
	public void setVisitCount( int visitCount )
	{
		this.visitCount = visitCount;
	}
	
	public int getFansCount( )
	{
		return fansCount;
	}
	
	public void setFansCount( int fansCount )
	{
		this.fansCount = fansCount;
	}
	
	public int getGroupId( )
	{
		return groupId;
	}
	
	public void setGroupId( int groupId )
	{
		this.groupId = groupId;
	}
	
	public boolean isFan( )
	{
		return fan;
	}
	
	public void setFan( boolean fan )
	{
		this.fan = fan;
	}
	
	public ScreenShot[ ] getScreenshots( )
	{
		return screenshots;
	}
	
	public void setScreenshots( ScreenShot[ ] screenshots )
	{
		this.screenshots = screenshots;
	}
	
	public String getName( )
	{
		return name;
	}
	
	public void setName( String name )
	{
		this.name = name;
	}
	
	public String getIcon( )
	{
		return icon;
	}
	
	public void setIcon( String icon )
	{
		this.icon = icon;
	}
	
	public String getAppid( )
	{
		return appid;
	}
	
	public void setAppid( String appid )
	{
		this.appid = appid;
	}
	
	public String getDownloadUrl( )
	{
		return downloadUrl;
	}
	
	public void setDownloadUrl( String downloadUrl )
	{
		this.downloadUrl = downloadUrl;
	}
	
	public int getDownloadCount( )
	{
		return downloadCount;
	}
	
	public void setDownloadCount( int downloadCount )
	{
		this.downloadCount = downloadCount;
	}
	
	public String getBannerImg( )
	{
		return bannerImg;
	}
	
	public void setBannerImg( String bannerImg )
	{
		this.bannerImg = bannerImg;
	}
	
	public String getDevelopers( )
	{
		return developers;
	}
	
	public void setDevelopers( String developers )
	{
		this.developers = developers;
	}
	
	public String getContent( )
	{
		return content;
	}
	
	public void setContent( String content )
	{
		this.content = content;
	}
	
	public String getShareContent( )
	{
		return shareContent;
	}
	
	public void setShareContent( String shareContent )
	{
		this.shareContent = shareContent;
	}
	
	public String getShareImage( )
	{
		return shareImage;
	}
	
	public void setShareImage( String shareImage )
	{
		this.shareImage = shareImage;
	}
	
	public long getGroupCreaterUid( )
	{
		return groupCreaterUid;
	}
	
	public void setGroupCreaterUid( long groupCreaterUid )
	{
		this.groupCreaterUid = groupCreaterUid;
	}
	
	public int getOsVersionCode( )
	{
		return osVersionCode;
	}
	
	public void setOsVersionCode( int osVersionCode )
	{
		this.osVersionCode = osVersionCode;
	}
	
	public String getOsVersionName( )
	{
		return osVersionName;
	}
	
	public void setOsVersionName( String osVersionName )
	{
		this.osVersionName = osVersionName;
	}
	
	public long getGamePacketId( )
	{
		return gamePacketId;
	}
	
	public void setGamePacketId( long gamePacketId )
	{
		this.gamePacketId = gamePacketId;
	}
	
	public boolean isNew( )
	{
		return newflag;
	}
	
	public void setNewFlag( boolean newflag )
	{
		this.newflag = newflag;
	}

	public int getPlat( )
	{
		return plat;
	}

	public void setPlat( int plat )
	{
		this.plat = plat;
	}
	
}
