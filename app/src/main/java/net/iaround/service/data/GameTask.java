
package net.iaround.service.data;


import net.iaround.service.ApkLoadTask;

import java.io.File;


public class GameTask extends Game
{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1003054689872805392L;

	public enum GameTaskStatus
	{
		NoInstall , // 未安装
		Installed , // 已安装
		Downloading , // 下载中
		Update// 要升级
	}
	
	private GameTaskStatus mStatus;
	private ApkLoadTask mApkLoadTask;
	private File mLocalFileCache;
	
	public GameTaskStatus getStatus( )
	{
		return mStatus;
	}
	
	public void setStatus( GameTaskStatus status )
	{
		this.mStatus = status;
	}
	
	public ApkLoadTask getTask( )
	{
		return mApkLoadTask;
	}
	
	public void setTask( ApkLoadTask task )
	{
		this.mApkLoadTask = task;
	}
	
	public File getLocalFileCache( )
	{
		return mLocalFileCache;
	}
	
	public void setLocalFileCache( File localFileCache )
	{
		this.mLocalFileCache = localFileCache;
	}
	
	public void copyWithGame( Game game )
	{
		setGameId( game.getGameId( ) );
		setName( game.getName( ) );
		setIcon( game.getIcon( ) );
		setAppid( game.getAppid( ) );
		setPlatform( game.getPlatform( ) );
		setVersionCode( game.getVersionCode( ) );
		setVersionName( game.getVersionName( ) );
		setSize( game.getSize( ) );
		setCreateTime( game.getCreateTime( ) );
		setBindingTime( game.getBindingTime( ) );
		setIntroduce( game.getIntroduce( ) );
		setDownloadUrl( game.getDownloadUrl( ) );
		setDownloadCount( game.getDownloadCount( ) );
		setVisitCount( game.getVisitCount( ) );
		setFansCount( game.getFansCount( ) );
		setGroupId( game.getGroupId( ) );
		setFan( game.isFan( ) );
		setScreenshots( game.getScreenshots( ) );
		setBannerImg( game.getBannerImg( ) );
		setDevelopers( game.getDevelopers( ) );
		setContent( game.getContent( ) );
		setShareContent( game.getShareContent( ) );
		setShareImage( game.getShareImage( ) );
		setGroupCreaterUid( game.getGroupCreaterUid( ) );
		setOsVersionCode( game.getOsVersionCode( ) );
		setOsVersionName( game.getOsVersionName( ) );
		setGamePacketId( game.getGamePacketId( ) );
		setNewFlag( game.isNew( ) );
		setPlat( game.getPlat( ) );
	}
}
