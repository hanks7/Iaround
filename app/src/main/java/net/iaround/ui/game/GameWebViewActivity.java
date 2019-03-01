package net.iaround.ui.game;


import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.model.entity.GeoData;
import net.iaround.service.APKDownloadService;
import net.iaround.share.interior.IAroundDynamicUtil;
import net.iaround.share.interior.IAroundFriendUtil;
import net.iaround.share.interior.IAroundGroupUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.comon.BaseWebViewActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class GameWebViewActivity extends BaseWebViewActivity implements OnClickListener
{

	private WebView mWebView;
	private TextView tv_loading;// loading进度条
	private Dialog progessDialog;
	private ImageView errView;
	private TextView tvTitle;

	private static Map< Integer, DownLoadAPKThread > flagToThreadMap = new HashMap< Integer, DownLoadAPKThread >( );
	private static Map< Integer, String > flagToPackageNameMap = new HashMap< Integer, String >( );
	private static Map< Integer, Boolean > flagToErrorMap = new HashMap< Integer, Boolean >( );
	private static Map< Integer, Long > flagToSize = new HashMap< Integer, Long >( );
	private static ArrayList< Integer > callInstallName = new ArrayList< Integer >( );

	private final static int DO_SHARE_INSIDE = 123;
	private final static int DO_SETTITLE = 124;
	private final static int DO_SHARE_BOTH_SIDE = 125;

	private String targetUrl;
	private String mShareTitle;
	private String mShareContent;
	private String mShareLink;
	private String mSharethumb;
	private String mSharePicture;
	private Map< String, String > currentUrlMap = new HashMap< String, String >( );// 当前URL中的参数名与值的map
	private int count = 1;


	public static void launchGameCenter(Context context, String url )
	{
		Intent intent = new Intent( );
		intent.setClass( context, GameWebViewActivity.class );
		intent.putExtra( "url", url );
		context.startActivity( intent );
	}

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.game_webview_activity );
		CommonFunction.log( "sherlock", "game webview oncreate" );
		findViewById( R.id.iv_left ).setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);


		targetUrl = getIntent( ).getStringExtra( "url" );
		if ( !CommonFunction.isEmptyOrNullStr( targetUrl ) )
			currentUrlMap = CommonFunction.paramURL( targetUrl );

		mWebView = (WebView) findViewById( R.id.game_webview );
		tv_loading = (TextView) findViewById( R.id.tv_loading );
		progessDialog = DialogUtil.getProgressDialog( mActivity, "",
			mContext.getResources( ).getString( R.string.please_wait ), null );

		errView = (ImageView) findViewById( R.id.err_view );

		final int width = CommonFunction.getScreenPixWidth( mContext );
		mWebView.requestFocus( );
		mWebView.setVisibility( View.VISIBLE );
		mWebView.getSettings( ).setPluginState( PluginState.ON );// 插件是否可用 ;
		mWebView.getSettings( ).setCacheMode( WebSettings.LOAD_DEFAULT );// 缓存模式
		mWebView.getSettings( ).setJavaScriptEnabled( true );// 使网页的js可以使用
		mWebView.getSettings( ).setSupportZoom( false );// 是否可以缩放
		mWebView.getSettings( ).setBuiltInZoomControls( false );// 是否显示缩放控件

		if ( CommonFunction.isEmptyOrNullStr( targetUrl ) )
			mWebView.loadUrl( ( Config.sGameCenter ) );
		else
			mWebView.loadUrl( ( targetUrl ) );

		mWebView.addJavascriptInterface( this, "callAndroid" );// 添加接口给js，使他们可以调用我们的代码

		WebChromeClient client = new WebChromeClient( )
		{
			public void onProgressChanged(WebView view, int newProgress )
			{
				// 网页加载时的loading
				super.onProgressChanged( view, newProgress );
				if ( newProgress > 0 && newProgress < 100 )
				{
					if ( progessDialog != null && !progessDialog.isShowing( ) )
						progessDialog.show( );
				}
				else
				{
					if ( progessDialog != null && progessDialog.isShowing( ) )
						progessDialog.hide( );
				}

			}

			@Override
			public boolean onConsoleMessage( ConsoleMessage consoleMessage )
			{
				CommonFunction.log( "sherlock",
					"web log == " + consoleMessage.message( ) + " : " + consoleMessage.sourceId( ) +
						" " + consoleMessage.lineNumber( ) );
				return super.onConsoleMessage( consoleMessage );
			}
		};

		mWebView.setWebViewClient( new WebViewClient( )
		{

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url )
			{// 使用本webview处理要跳转的URL，如果是iaround开头的则是内部跳转
				CommonFunction.log( "sherlock", "call shouldOverrideUrlLoading url == " + url );
				currentUrlMap = CommonFunction.paramURL( url );
				if ( !CommonFunction.isEmptyOrNullStr( url ) && url.startsWith( "iaround" ) )
					InnerJump.Jump( GameWebViewActivity.this, url );
				else
				{
					view.loadUrl( url );
					return false;
				}
				return true;
			}

			public void onReceivedError(WebView view, int errorCode, String description,
										String failingUrl )
			{
				CommonFunction.log( "sherlock",
					"errorCode : description : failingUrl == " + errorCode + " : " + description +
						" : " + failingUrl );
				errView.setVisibility( View.VISIBLE );
			}

        } );

		mWebView.setWebChromeClient( client );
	}

	@Override
	protected void onResume( )
	{
		super.onResume( );
		for ( int i = 0; i < callInstallName.size( ); i++ )
		{
			if ( isInstall( flagToPackageNameMap.get( callInstallName.get( i ) ) ) )
			{
				mWebView.loadUrl( "javascript:installSuccess(" + callInstallName.get( i ) + ")" );
			}
			else
			{
				mWebView.loadUrl( "javascript:installFailure(" + callInstallName.get( i ) + ")" );
			}
		}
		callInstallName.clear( );
//		if ( tPage != null )
//			tPage.onResume( );//jiqiang
	}

	@Override
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{// 机器的返回键的操作为返回网页上一页
			errView.setVisibility( View.GONE );
			if ( mWebView.canGoBack( ) )
			{
				mWebView.goBack( );
				return !super.onKeyDown( keyCode, event );
			}
			else
			{
				finish( );
			}
		}
		return !super.onKeyDown( keyCode, event );
	}

	public void installApp(Context context, String filePath, int flag )
	{
		if ( TextUtils.isEmpty( filePath ) || context == null )
		{
			return;
		}


		String dir = Environment.getExternalStorageDirectory( ).getPath( ) + "/";
		// String dir = PathUtil.getAPKCacheDir( );
		String[] args1 = { "chmod", "705", dir };
		APKDownloadService.exec( args1 );

		String[] args2 = { "chmod", "604", filePath };
		APKDownloadService.exec( args2 );
		CommonFunction.log( "sherlock", "下载完后，立刻安装-------------------installApp =" + filePath );

		Intent intent = new Intent( Intent.ACTION_VIEW );
		intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
			Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", new File(filePath));
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
			intent.setDataAndType(apkUri, "application/vnd.android.package-archive");

		} else {
			intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
		}
		CommonFunction.log( "sherlock", "going to install " + flag +";file===" + filePath);
		context.startActivity( intent );
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		super.onActivityResult( requestCode, resultCode, data );
	}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
			{
				errView.setVisibility( View.GONE );
				if ( mWebView.canGoBack( ) )
				{
					mWebView.goBack( );
				}
				else
				{
					finish( );
				}
			}
			break;
		}
	}

	@Override
	protected void onDestroy( )
	{
		// TODO Auto-generated method stub
		super.onDestroy( );
		if ( progessDialog != null )
		{
			progessDialog.cancel( );
			progessDialog = null;
		}
	}


	private Handler mHandler = new Handler(  )
	{
		@Override
		public void handleMessage( Message msg )
		{
			if ( msg.what == 0 )
			{
				String uri = (String) msg.obj;
				mWebView.loadUrl( uri );
			}
			else if ( msg.what == 1 )
			{
				CommonFunction.toastMsg( mContext, R.string.download_new_version_fail );
			}
			else if ( msg.what == DO_SHARE_INSIDE || msg.what == DO_SHARE_BOTH_SIDE )
			{
				String[] shareStrings = null;
				if ( msg.what == DO_SHARE_INSIDE )
					shareStrings = new String[]{ IAroundDynamicUtil.class.getName( ),
						IAroundGroupUtil.class.getName( ), IAroundFriendUtil.class.getName( ) };

				if ( !CommonFunction.isEmptyOrNullStr( mShareTitle ) &&
					!CommonFunction.isEmptyOrNullStr( mShareContent ) &&
					!CommonFunction.isEmptyOrNullStr( mShareLink ) &&
					!CommonFunction.isEmptyOrNullStr( mSharethumb ) &&
					!CommonFunction.isEmptyOrNullStr( mSharePicture ) )
				{
					String gameId = "";
					if ( currentUrlMap != null )
					{
						gameId = currentUrlMap.get( "gameid" );
					}
				}
				else
					CommonFunction.log( "sherlock",
						"" + CommonFunction.isEmptyOrNullStr( mShareTitle ) +
							CommonFunction.isEmptyOrNullStr( mShareContent ) +
							CommonFunction.isEmptyOrNullStr( mShareLink ) +
							CommonFunction.isEmptyOrNullStr( mSharethumb ) +
							CommonFunction.isEmptyOrNullStr( mSharePicture ) );
			}
			else if ( msg.what == DO_SETTITLE )
			{
				String title = msg.obj.toString( );
				CommonFunction.log( "sherlock", "mHandler title == " + title );
				( (TextView) findViewById( R.id.tv_title ) ).setText( title );
			}
			else
			{
				super.handleMessage( msg );
			}
		}
	};


	class DownLoadAPKThread extends Thread
	{

		private Context context;
		private DownloadFileCallback callback;
		private String fileUrl;
		private int flag;

		public DownLoadAPKThread(Context context, String url, DownloadFileCallback callback,
								 int flag )
		{
			// TODO Auto-generated constructor stub
			this.context = context;
			fileUrl = url;
			this.callback = callback;
			this.flag = flag;
		}

		@Override
		public void run( )
		{
			// TODO Auto-generated method stub
			String fileName = CryptoUtil.generate( fileUrl ) + ".apk";

			CommonFunction
				.log( "sherlock", "PathUtil.getAPKCacheDir( ) == " + PathUtil.getAPKCacheDir( ) );
			try
			{
				File cacheFile = new File(
					PathUtil.getAPKCacheDir( ) + "/" + fileName );

				FileDownloadManager manager = new FileDownloadManager( context, callback, fileUrl,
					fileName, PathUtil.getAPKCacheDir( ) + "/", flag );
				manager.run( );
			}
			catch ( ConnectionException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace( );
			}
			super.run( );
		}
	}

	private String addTime(String url )
	{

		String urichange = "";
		if ( url.contains( "?" ) )
		{
			urichange = url + "&nocache=" + System.currentTimeMillis( );
		}
		else
		{
			urichange = url + "?nocache=" + System.currentTimeMillis( );
		}

		return url;
		// return urichange;
	}


	// ************************js调用方法***************************************************/

	// js调用，查看是否有安装某个应用
	@android.webkit.JavascriptInterface
	public boolean isInstall( String packageName )
	{
		return CommonFunction.isClientInstalled( mContext, packageName );
	}

	// js调用，启动某个应用
	@android.webkit.JavascriptInterface
	public void startGame(String packageName, int gameID )
	{
		if ( !isInstall( packageName ) )
		{
			CommonFunction.log( "sherlock", "did not install " + packageName );
			return;
		}
//		StatisticsApi.statisticEventGameCenterStartGame( mContext, gameID );//jiqiang
		CommonFunction.launchClient( mContext, packageName );
	}

	// js调用，获取当前地理位置
	@android.webkit.JavascriptInterface
	public String getLocation( )
	{
		GeoData data = LocationUtil.getCurrentGeo( mContext );
		return GsonUtil.getInstance( )
			.getStringFromJsonObject( new LngLat( data.getLng( ), data.getLat( ) ) );
	}

	// js调用，获取正在下载的游戏的包名
	@android.webkit.JavascriptInterface
	public void pastPackageName(String packName, String gameId )
	{

	}

	@android.webkit.JavascriptInterface
	public String testLink(String text )
	{
		return text;
	}

	@android.webkit.JavascriptInterface
	public void downloadGame(final String url, final String packageName, final int gameId,
							 final long size )
	{
		String netType = PhoneInfoUtil.getInstance( mContext ).getNetType( ).toLowerCase( );
		CommonFunction.log( "sherlock", "call downloadGame" );
		CommonFunction.log( "sherlock", "current net type == " + netType );

		if ( CommonFunction.isEmptyOrNullStr( netType ) )
		{
			DialogUtil.showOKDialog( mContext, "", getString( R.string.network_error_please_check ),
				null );
			mWebView.loadUrl( "javascript:installFailure(" + gameId + ")" );
		}
		else if ( !"wifi".equals( netType ) )
		{
			Dialog dialog = DialogUtil
				.showTowButtonDialog( mContext, "", getString( R.string.no_wifi_download_warning ),
					getString( R.string.cancel ), getString( R.string.continue_ ),
					new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							mWebView.loadUrl( "javascript:installFailure(" + gameId + ")" );
						}
					}, new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							doDownLoad( url, packageName, gameId, size );
						}
					} );
			dialog.setCancelable( false );
		}
		else
			doDownLoad( url, packageName, gameId, size );
	}

	@android.webkit.JavascriptInterface
	public void doShare(String title, String content, String link, String thumb, String picture,
						int shareSide )
	{
		mShareTitle = title;
		mShareContent = content;
		mShareLink = link;
		mSharethumb = thumb;
		mSharePicture = picture;
		if ( shareSide == 1 )
			mHandler.sendEmptyMessage( DO_SHARE_INSIDE );
		else if ( shareSide == 2 )
			mHandler.sendEmptyMessage( DO_SHARE_BOTH_SIDE );
	}

	@android.webkit.JavascriptInterface
	public void jsSetTitle( String title )
	{
		CommonFunction.log( "sherlock", "js call set title == " + title );
		Message msg = new Message( );
		msg.what = DO_SETTITLE;
		msg.obj = title;
		mHandler.sendMessage( msg );
	}

	public void doDownLoad(String url, String packageName, int gameId, long size )
	{

		if ( !flagToThreadMap.containsKey( gameId ) )
		{
			final NotificationDownLoad downLoadNotification = new  NotificationDownLoad();
			CommonFunction.log( "sherlock",
				"url : packageName : gameId == " + url + " : " + packageName + " : " + gameId );

			final int taskFlag = url.hashCode( );

			downLoadNotification.createNotification( );
			downLoadNotification.showNotification( taskFlag );
			if ( flagToErrorMap.containsKey( gameId ) )
				flagToErrorMap.remove( gameId );
			DownloadFileCallback callback = new DownloadFileCallback( )
			{

				@Override
				public void onDownloadFileProgress( long lenghtOfFile, long LengthOfDownloaded,
					int flag )
				{
					if ( LengthOfDownloaded > 1024 * 1024 * count )
					{
						CommonFunction.log( "sherlock",
							flag + " downLoad == " + LengthOfDownloaded + "/" + lenghtOfFile );
						count++;

						final long currentLen = LengthOfDownloaded;
						final long totalLen = lenghtOfFile;

						runOnUiThread( new Runnable( )
						{
							public void run( )
							{
								downLoadNotification.updateNotification( taskFlag, currentLen,
									totalLen );
							}
						} );
					}
				}

				@Override
				public void onDownloadFileFinish(int flag, String fileName, String savePath )
				{
					if ( !flagToErrorMap.containsKey( flag ) )
					{
						CommonFunction.log( "sherlock",
							"download finished ,fileName : savePath == " + fileName + " : " +
								savePath );
						File file = new File( savePath + fileName );
						if ( file.exists( ) )
						{
							callInstallName.add( flag );
							installApp( mContext, savePath + fileName, flag );
						}
					}
					else
					{
						CommonFunction.log( "sherlock", "download erro " + flag );
						flagToErrorMap.remove( flag );
					}
					flagToThreadMap.remove( flag );
					count = 1;
					downLoadNotification.hideNotification( taskFlag );
				}

				@Override
				public void onDownloadFileError(int flag, String fileName, String savePath )
				{
					flagToErrorMap.put( flag, true );
					CommonFunction.log( "sherlock", "GameWebViewActivity ==  onDownloadFileError" );
					Message msg = new Message( );
					msg.what = 1;
					mHandler.sendMessage( msg );
					CommonFunction
						.log( "sherlock", "call installFailure in downloadError == " + flag );
					flagToThreadMap.remove( flag );
					flagToSize.remove( flag );
					mWebView.loadUrl( "javascript:installFailure(" + flag + ")" );
					count = 1;
					downLoadNotification.hideNotification( taskFlag );
				}
			};
			DownLoadAPKThread downloadThread = new DownLoadAPKThread( mContext, url, callback,
				gameId );
			downloadThread.start( );
			flagToPackageNameMap.put( gameId, packageName );
			flagToThreadMap.put( gameId, downloadThread );
			flagToSize.put( gameId, size );
			CommonFunction.log( "sherlock", "gameid : size == " + gameId + " : " + size );
			CommonFunction.toastMsg( mContext, R.string.download_new_version_ticker_txt );
		}
		else
		{
			CommonFunction.toastMsg( mContext, R.string.download_apk_file );
		}
	}

	class LngLat
	{
		public long lat;
		public long lng;

		public LngLat( long lng, long lat )
		{
			this.lat = lat;
			this.lng = lng;
		}
	}

	public class NotificationDownLoad
	{


		private NotificationManager mNotificationManager;
		private RemoteViews contentView;
		private PendingIntent contentIntent;
		private Notification notification;

		// 创建
		private void createNotification( )
		{
			mNotificationManager = (NotificationManager) getSystemService(
				Context.NOTIFICATION_SERVICE );
			contentView = new RemoteViews( getPackageName( ), R.layout.notify_download_progress );
			contentView
				.setTextViewText( R.id.title, getString( R.string.download_new_version_tip ) );

			Intent notificationIntent = new Intent( );
			contentIntent = PendingIntent.getActivity( GameWebViewActivity.this, 0, notificationIntent, 0 );

			String tickerText = mContext.getString( R.string.download_new_version_ticker_txt );
			int icon = android.R.drawable.stat_sys_download;
			//		Builder builder = new Builder(mContext);
			NotificationCompat.Builder builder = new NotificationCompat.Builder( GameWebViewActivity.this );
			builder.setSmallIcon( icon ).setTicker( tickerText )
				.setWhen( System.currentTimeMillis( ) );
			notification = builder.build( );
			notification.flags |= Notification.FLAG_ONGOING_EVENT;

		}

		private void showNotification( int id )
		{

			contentView.setTextViewText( R.id.percentTip, "0%" );
			contentView.setProgressBar( R.id.progressBar, 100, 0, false );
			notification.contentView = contentView;
			notification.contentIntent = contentIntent;
			mNotificationManager.notify( id, notification );
		}

		private void updateNotification( int id, long currentLen, long totalLen )
		{

			double pro1 = ( ( double ) currentLen ) / ( ( double ) totalLen );
			int pro = ( int ) ( pro1 * 100 );

			contentView.setTextViewText( R.id.percentTip, pro + "%" );
			contentView.setProgressBar( R.id.progressBar, 100, pro, false );
			mNotificationManager.notify( id, notification );
		}

		private void hideNotification( int id )
		{
			mNotificationManager.cancel( id );
		}
	}
}
