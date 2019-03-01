
package net.iaround.service;


import net.iaround.service.data.Dispatch;
import net.iaround.service.data.Downloader;
import net.iaround.service.data.FileLoadTask;
import net.iaround.service.data.GameTask;
import net.iaround.service.data.RandomDiskCache;


public class ApkLoadTask extends FileLoadTask
{
	public GameTask mGame;
	public int from;
	
	ApkLoadTask(Downloader downloader , RandomDiskCache diskCache , Dispatch dispatch )
	{
		super( downloader , diskCache , dispatch );
		// TODO Auto-generated constructor stub
	}



	public void setGameTask( GameTask game )
	{
		mGame = game;
	}
	
	public GameTask getGameTask( )
	{
		return mGame;
	}
	
	@Override
	public String getKey( )
	{
		// TODO Auto-generated method stub
		return mGame.getAppid( ) + "_" + mGame.getVersionCode( ) + ".apk";
	}
}
