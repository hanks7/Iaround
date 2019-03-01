
package net.iaround.service;


import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.service.data.FileLoadTask;
import net.iaround.service.data.Game;
import net.iaround.service.data.GameTask;
import net.iaround.service.data.GameTask.GameTaskStatus;
import net.iaround.service.data.ITaskProgress;
import net.iaround.tools.CommonFunction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;


public class APKDownloadService extends Service
{
	private static final String APK_DOWNLOAD_STOP_BROADCAST = "net.iaround.apk.download.stop";
	
	private ConcurrentHashMap< String , ApkLoadTask> taskMap = new ConcurrentHashMap< String , ApkLoadTask >( );
	
	private final IBinder mBinder = new LocalBinder( );
	
	private APKDownloadBroadcastReciver mAPKDownloadBroadcastReciver;
	
	private static LocalBroadcastManager sLbm;
	
	public class LocalBinder extends Binder
	{
		public APKDownloadService getService( )
		{
			return APKDownloadService.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent )
	{
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public int onStartCommand(Intent intent , int flags , int startId )
	{
		// TODO Auto-generated method stub
		return super.onStartCommand( intent , flags , startId );
	}
	
	@Override
	public void onCreate( )
	{
		// TODO Auto-generated method stub
		super.onCreate( );
		sLbm = LocalBroadcastManager.getInstance( this );
		IntentFilter intentFilter = new IntentFilter( );
		intentFilter.addAction( APK_DOWNLOAD_STOP_BROADCAST );
		mAPKDownloadBroadcastReciver = new APKDownloadBroadcastReciver( );
		sLbm.registerReceiver( mAPKDownloadBroadcastReciver , intentFilter );
	}
	
	@Override
	public void onDestroy( )
	{
		// TODO Auto-generated method stub
		super.onDestroy( );
		sLbm.unregisterReceiver( mAPKDownloadBroadcastReciver );
	}
	
	/**
	 * 传入从服务器获取的应用列表，经过与本地已安装应用和已下载应用的比对处理
	 * 
	 * @param games
	 * @return 返回后的应用任务列表将具有可用的状态
	 */
	public ArrayList<GameTask> makeGameTaskLists(List<Game> games )
	{
		if ( games == null )
		{
			return new ArrayList< GameTask >( );
		}
		
		ArrayList< GameTask > gameTasks = new ArrayList< GameTask >( );
		for ( Game game : games )
		{
			GameTask task = makeGameTask( game );
			if ( task != null )
			{
				gameTasks.add( task );
			}
		}
		return gameTasks;
	}
	
	public GameTask makeGameTask( Game game )
	{
		GameTask gameTask = new GameTask( );
		gameTask.copyWithGame( game );
		int appStatus = checkApkInstalled( game.getAppid( ) , game.getVersionCode( ) );
		if ( appStatus == -1 )
		{
			// 未安装
			gameTask.setStatus( GameTaskStatus.NoInstall );
		}
		else if ( appStatus == 0 )
		{
			// 要升级
			gameTask.setStatus( GameTaskStatus.Update );
		}
		else if ( appStatus == 1 )
		{
			// 已安装
			gameTask.setStatus( GameTaskStatus.Installed );
			return gameTask;
		}
		
		Map< String , ApkLoadTask > runTasks = getRunTasks( );
		ApkLoadTask apkLoadTask = runTasks.get( gameTask.getAppid( ) );
		if ( apkLoadTask != null )
		{
			// 正在下载中
			gameTask.setStatus( GameTaskStatus.Downloading );
			gameTask.setTask( apkLoadTask );
		}
		else
		{
			gameTask.setLocalFileCache( checkApkDownloaded( gameTask.getAppid( ) ,
					gameTask.getVersionCode( ) ) );
		}
		
		return gameTask;
	}
	
	private ITaskProgress<FileLoadTask> apkLoadListener = new ITaskProgress< FileLoadTask >( )
	{
		
		@Override
		public void onTaskStart( FileLoadTask task )
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTaskProgress( FileLoadTask task )
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTaskCancel( FileLoadTask task )
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTaskPause( FileLoadTask task )
		{
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onTaskComplete( FileLoadTask task )
		{
			// TODO Auto-generated method stub
			
			if(task instanceof ApkLoadTask)
			{
				//gh 暂时不考虑上报
//				StatisticsApi
//						.statisticEventGame( getApplicationContext( ) ,
//								StatisticsApi.EVENT_ID_DOWNLOAD_SUCCESS_80205 ,
//								( ( ApkLoadTask ) task ).from ,
//								( ( ApkLoadTask ) task ).mGame.getGameId( ) );
			}
			
			taskMap.remove( task.getKey( ).substring( 0 , task.getKey( ).indexOf( "_" ) ) );
			task.cancelProgressListener( apkLoadListener );
			installApp( APKDownloadService.this , task.getResponse( ).getAbsolutePath( ) );
		}
		
		@Override
		public void onTaskError( FileLoadTask task )
		{
			// TODO Auto-generated method stub
		}
	};
	
	/**
	 * 
	 * @param context
	 * @param packageName
	 * @param versionCode
	 * @return -1是未安装，0是需要升级，1是已安装
	 */
	private int checkApkInstalled(String packageName , int versionCode )
	{
		return CommonFunction.isClientInstalled( this , packageName , versionCode );
	}
	
	private File checkApkDownloaded(String packageName , int versionCode )
	{
		String filename = packageName + "_" + versionCode + ".apk";
		File cacheDir = new File( CommonFunction.getSDPath( ) + "/apkcache/" );
		File cacheFile = new File( cacheDir , filename );
		if ( cacheFile.exists( ) && cacheFile.isFile( ) )
		{
			// 有缓存文件
			return cacheFile;
		}
		else
		{
			// 无缓存文件
			return null;
		}
	}
	
	/**
	 * 获取正在运行的任务
	 */
	public Map< String , ApkLoadTask > getRunTasks( )
	{
		return taskMap;
	}
	
	public static void bindService(Context context , ServiceConnection connection )
	{
		Intent service = new Intent( context , APKDownloadService.class );
		context.startService( service );
		context.bindService( service , connection , BIND_AUTO_CREATE );
	}
	
	public ApkLoadTask download( GameTask gametask ,int from)
	{
		if ( gametask == null )
		{
			return null;
		}
		
		ApkLoadTask task = new ApkTaskLoader.Builder( ).build( this ).createTask( this ,
				gametask,from );
		taskMap.put( gametask.getAppid( ) , task );
		task.addProgressListener( apkLoadListener );
		task.submit( );
		return task;
	}
	
	public void cancel( String packageName )
	{
		ApkLoadTask task = taskMap.get( packageName );
		if ( task != null )
		{
			task.cancelTask( );
		}
	}
	
	public void pause( String packageName )
	{
		ApkLoadTask task = taskMap.get( packageName );
		if ( task != null )
		{
			task.pauseTask( );
		}
	}
	
	public void resume( String packageName )
	{
		ApkLoadTask task = taskMap.get( packageName );
		if ( task != null )
		{
			task.resumeTask( );
		}
	}
	
	public void installApp(Context context , String filePath )
	{
		if ( TextUtils.isEmpty( filePath ) || context == null )
		{
			return;
		}
		
        // [文件夹705:drwx---r-x]
		String dir = CommonFunction.getSDPath( ) + "apkcache/";
        String[] args1 = { "chmod", "705", dir};
        String result = exec(args1);
        
      
        // [文件604:-rw----r--]
        String[] args = { "pm", "install", "-r", filePath };
        String[] args2 = { "chmod", "604", filePath  };
        result =  exec(args2);
		CommonFunction.log( "shifengxiong","下载完后，立刻安装-------------------installApp" +filePath);
		
		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		intent.setDataAndType( Uri.fromFile( new File( filePath ) ) ,
				"application/vnd.android.package-archive" );
		context.startActivity( intent );
	}
	
	public void openApp(Activity activity , String packageName )
	{
		if ( TextUtils.isEmpty( packageName ) || activity == null )
		{
			return;
		}
		
		PackageManager pm = getApplicationContext( ).getPackageManager( );
		Intent i = pm.getLaunchIntentForPackage( packageName );
		if ( i != null )
		{
			activity.startActivity( i );
		}
		else
		{
			Toast.makeText( this , R.string.game_center_openapp_fail , Toast.LENGTH_LONG )
					.show( );
		}
	}
	
	public static void stopAllTask( Context context )
	{
		if ( sLbm != null )
		{
			sLbm.sendBroadcast( new Intent( APK_DOWNLOAD_STOP_BROADCAST ) );
			sLbm = null;
		}
	}
	
	private class APKDownloadBroadcastReciver extends BroadcastReceiver
	{
		
		@Override
		public void onReceive(Context context , Intent intent )
		{
			String action = intent.getAction( );
			if ( APK_DOWNLOAD_STOP_BROADCAST.equals( action ) )
			{
				Iterator< Entry< String , ApkLoadTask >> tasks = taskMap.entrySet( )
						.iterator( );
				while ( tasks.hasNext( ) )
				{
					Entry< String , ApkLoadTask > t = tasks.next( );
					t.getValue( ).pauseTask( );
					tasks.remove( );
				}
			}
		}
	}
	
    /** 执行Linux命令，并返回执行结果。 */
    public static String exec(String[] args) {
        String result = "";
        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = null;
        InputStream errIs = null;
        InputStream inIs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int read = -1;
            process = processBuilder.start();
            errIs = process.getErrorStream();
            while ((read = errIs.read()) != -1) {
                baos.write(read);
            }
            baos.write('\n');
            inIs = process.getInputStream();
            while ((read = inIs.read()) != -1) {
                baos.write(read);
            }
            byte[] data = baos.toByteArray();
            result = new String(data);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (errIs != null) {
                    errIs.close();
                }
                if (inIs != null) {
                    inIs.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }
}

