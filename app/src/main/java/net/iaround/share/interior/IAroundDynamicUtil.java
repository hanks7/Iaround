
package net.iaround.share.interior;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.entity.GeoData;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.MapSearchIaround;
import net.iaround.tools.MapSearchIaround.SearchIaroundResultListener;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.dynamic.DynamicPublishManager;
import net.iaround.ui.dynamic.bean.DynamicPublishBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


/** 遇见动态分享 */
public class IAroundDynamicUtil extends AbstractShareUtils implements HttpCallBack
{
	/** 分享标识 */
	public static final int ID = 200;
	
	/** 分享名称 */
	private static final String SHARE_UTIL_NAME = "iaround_dynamic";
	
	/****** 发布动态需要的参数 *****/
	
	/** 类型（ 1-图文，2-分享 ） */ 
	private int TYPE = 2;
	/** 当前地址 */ 
	private String ADDRESS = "";
	/** 当前短地址 */ 
	private String SHORT_ADDRESS = "";
	/** 当前纬度 */
	private int LAT = 0;
	/** 当前经度 */ 
	private int LNG = 0;
	
	/** 同步目标类型（1-贴吧，2-圈子，3-动态）同步多个用，隔开 */ 
	private String SYNC = "";
	
	/**
	 * sync对应的value 当sysnc为1:时，该值为贴吧id 当sysnc为2:时，该值为圈子id 当sysnc为3:时，该值为用户id
	 * 多个并行时，排序与sync排序一致
	 */
	private String SYNC_VALUE = "";
	
	/** 1-贴吧话题，2-圈子话题，3-广告，4-小秘书 */
	private int SHARE_SOURCE = 0;
	
	/**
	 * 当shareSource为1时，值为贴吧话题id， 当shareSource为2时，值为圈子话题id
	 * 当sharesource为3,4时，值为0[新增分享来源]
	 */
	private long SHARE_VALUE = 0;
	
	/*************************************/
	
	private long PUBLISH_DYNAMIC_FLAG;
	public static String CUSTOM_CONTENT = "";// 想说的话
	// 分享生成动态的发布管理类
	private DynamicPublishManager manager;
	
	/** 系统禁言 */
	public static final int ERROR_SYSTEM_FORBID = -101;
	
	private MapSearchIaround searchGetAddress;// 获取地址的类
	private SearchIaroundResultListener listener;// 返回获取地址结果的监听器
	/** 是否使用特殊格式的地址，地址格式：市 + 标志性建筑 */
	private boolean isUserSpecialAddress = false;
	
	public IAroundDynamicUtil(Context context , String userUid )
	{
		super( context , userUid , SHARE_UTIL_NAME );
		// TODO Auto-generated constructor stub
		initConfig( );
	}
	
	private void initConfig( )
	{
		
		mWeiboDbVersion = 1;
		
		MAX_LENGTH = 140;
		manager = new DynamicPublishManager( mContext );
		
		mShareDb.saveToken( 1 , SHARE_UTIL_NAME , SHARE_UTIL_NAME , 0 , 0 , SHARE_UTIL_NAME );
		
		GeoData geoData = LocationUtil.getCurrentGeo( mContext );
		if ( geoData != null )
		{
			LAT = geoData.getLat( );
			LNG = geoData.getLng( );
			ADDRESS = geoData.getAddress( );
			SHORT_ADDRESS = geoData.getShortAddress( );
		}
	}
	
	@Override
	public void bind(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected int calculateLength( String value )
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	protected boolean checkRegister(Activity arg0 , int arg1 , Object arg2 )
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void follow(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getId( )
	{
		// TODO Auto-generated method stub
		return ID;
	}
	
	@Override
	public String getName( )
	{
		// TODO Auto-generated method stub
		return SHARE_UTIL_NAME;
	}
	
	@Override
	public void onActivityResult( int arg0 , int arg1 , Intent arg2 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCreate( Bundle arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume( )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onSaveInstanceState( Bundle arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onStop( )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void register( Activity arg0 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void sendMsg(Activity arg0 , String arg1 , String arg2 , String arg3 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void show(Activity arg0 , String arg1 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void timeline(Activity arg0 , int arg1 , int arg2 , String arg3 , String arg4 )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void update(Activity activity , String title , String text , String link )
	{
		// TODO Auto-generated method stub
		if ( CommonFunction.forbidSay( mContext ) )
		{
			callbackFailure( ACTION_UPDATEING , ERROR_SYSTEM_FORBID );
			return;
		}
		CommonFunction.log( "IAroundDynamicUtil" , "CUSTOM_CONTENT***" + CUSTOM_CONTENT );
		String customTitle = "";
		if ( !CUSTOM_CONTENT.equals( "" ) )
		{
			customTitle = CUSTOM_CONTENT;
		}
		else
		{
			customTitle = title;
		}
		
		ArrayList< String > photos = new ArrayList< String >( );
		if ( !isUserSpecialAddress )
		{
			DynamicPublishBean bean = initDynamicPublishBean( customTitle , text , photos ,
					link , String.valueOf( SHARE_SOURCE ) , String.valueOf( SHARE_VALUE ) );
			
			PUBLISH_DYNAMIC_FLAG = manager.publishDynamic( bean , this );
			CUSTOM_CONTENT = "";
		}
		else
		{
			publishWithSpecialAddress( customTitle , text , photos , link ,
					String.valueOf( SHARE_SOURCE ) , String.valueOf( SHARE_VALUE ) );
		}
	}
	
	@Override
	public void upload(Activity activity , String title , String text , String link ,
					   String thumb , String pic )
	{
		// TODO Auto-generated method stub
		if ( CommonFunction.forbidSay( mContext ) )
		{
			callbackFailure( ACTION_UPLOADING , ERROR_SYSTEM_FORBID );
			return;
		}
		
		CommonFunction.log( "IAroundDynamicUtil" , "thumb***" + thumb );
		CommonFunction.log( "IAroundDynamicUtil" , "pic***" + pic );
		CommonFunction.log( "IAroundDynamicUtil" , "CUSTOM_CONTENT***" + CUSTOM_CONTENT );
		String customTitle = "";
		if ( !CUSTOM_CONTENT.equals( "" ) )
		{
			customTitle = CUSTOM_CONTENT;
		}
		else
		{
			customTitle = title;
		}
		
		ArrayList< String > photos = new ArrayList< String >( );
		photos.add( pic );
		
		if ( !isUserSpecialAddress )
		{
			DynamicPublishBean bean = initDynamicPublishBean( customTitle , text , photos ,
					link , String.valueOf( SHARE_SOURCE ) , String.valueOf( SHARE_VALUE ) );
			
			PUBLISH_DYNAMIC_FLAG = manager.publishDynamic( bean , this );
			CUSTOM_CONTENT = "";
		}
		else
		{
			publishWithSpecialAddress( customTitle , text , photos , link ,
					String.valueOf( SHARE_SOURCE ) , String.valueOf( SHARE_VALUE ) );
		}
		
	}
	
	// 初始化获取地址的组件
	private void publishWithSpecialAddress(final String title , final String content ,
										   final ArrayList< String > photos , final String link , final String shareSource ,
										   final String shareValue )
	{
		searchGetAddress = new MapSearchIaround( );
		searchGetAddress.doSearchIaround( mContext , 1 , "" );
		
		listener = new SearchIaroundResultListener( )
		{
			
			@Override
			public void onSearchResulted( List< GeoData > geoDatas )
			{
				CommonFunction.log( "IAroundDynamicUtil" , "onSearchResulted>>>>>" );
				if ( geoDatas != null && geoDatas.size( ) > 0 )
				{
					CommonFunction.log( "IAroundDynamicUtil" , "onSearchResulted>>>>> not null" );
					// 位置参数
					GeoData geo = geoDatas.get( 0 );
					ADDRESS = geo.getSimpleAddress( );
					
					GeoData geoData = LocationUtil.getCurrentGeo( mContext );
					if ( geoData != null )
					{
						SHORT_ADDRESS = geoData.getProvince( ) + geoData.getCity( );
						LAT = geoData.getLat( );
						LNG = geoData.getLng( );
					}
					else
					{
						SHORT_ADDRESS = "";
						LAT = 0;
						LNG = 0;
					}
				}
				else
				{
					CommonFunction.log( "IAroundDynamicUtil" , "onSearchResulted>>>>>null" );
					SHORT_ADDRESS = "";
					ADDRESS = "";
					LAT = 0;
					LNG = 0;
				}
				DynamicPublishBean bean = initDynamicPublishBean( title , content , photos ,
						link , shareSource , shareValue );
				
				PUBLISH_DYNAMIC_FLAG = manager.publishDynamic( bean , IAroundDynamicUtil.this );
				CUSTOM_CONTENT = "";
			}
		};
		searchGetAddress.setSearchIaroundResult( listener );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		// TODO Auto-generated method stub
		if ( Constant.isSuccess( result ) )
		{
			if ( PUBLISH_DYNAMIC_FLAG == flag )
			{
				callbackSuccess( ACTION_UPLOADING );
			}
		}
		else
		{
			if ( PUBLISH_DYNAMIC_FLAG == flag )
			{
				bannedPublishDynamicBack(result ,  flag) ;
				
			}
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		// TODO Auto-generated method stub
		if ( PUBLISH_DYNAMIC_FLAG == flag )
		{
			callbackFailure( ACTION_UPLOADING , 0 );
		}
	}
	
	private void callbackSuccess( int action )
	{
		HashMap< String , Object > res = new HashMap< String , Object >( );
		res.put( "status" , SUCCESS_CODE );
		if ( mShareActionListener != null )
		{
			mShareActionListener.onComplete( IAroundDynamicUtil.this , action , res );
		}
	}
	
	private void bannedPublishDynamicBack(String errorDesc , long flag){
		try {
			JSONObject json = new JSONObject(errorDesc);
			int error = json.optInt("error");
			if(error == 9306)
			{
				String desc = CommonFunction.jsonOptString( json , "errordesc" ) ;
				
				JSONObject jsonDesc = new JSONObject(desc);
				
				long datetime = jsonDesc.optLong("datetime");
				int hour = jsonDesc.optInt("hour");
				String hourStr = TimeFormat.convertTimeLong2String(datetime, Calendar.MINUTE);
				String errStr = mContext.getString(R.string.banned_publish_dynamic, hourStr , hour);
				CommonFunction.toastMsg(mContext, errStr);
				callbackFailure( ACTION_UPLOADING , 0 );
			}
			else
			{
				callbackFailure( ACTION_UPLOADING , 0 );
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private void callbackFailure( int action , int code )
	{
		HashMap< String , Object > res = new HashMap< String , Object >( );
		res.put( "status" , code );
		if ( mShareActionListener != null )
		{
			
			mShareActionListener.onComplete( IAroundDynamicUtil.this , action , res );
		}
	}
	
	// 初始化要发布的动态对象
	private DynamicPublishBean initDynamicPublishBean(String title , String content ,
													  ArrayList< String > photos , String link , String shareSource , String shareValue )
	{
		CommonFunction.log( "IAroundDynamicUtil" , "ADDRESS---" + ADDRESS );
		CommonFunction.log( "IAroundDynamicUtil" , "SHORT_ADDRESS---" + SHORT_ADDRESS );
		DynamicPublishBean bean = new DynamicPublishBean( );
		bean.setTitle( title );
		bean.type = TYPE;
		bean.setContent( content );
		bean.setPhotoList( photos );
		bean.setAddress( ADDRESS );
		bean.setShortaddress( SHORT_ADDRESS );
		bean.setUrl( link );
		bean.setSync( SYNC );
		bean.setSyncvalue( SYNC_VALUE );
		bean.setSharesource( shareSource );
		bean.setSharevalue( shareValue );
		
		return bean;
	}
	
	public void initShareSource( int source , long value )
	{
		SHARE_SOURCE = source;
		SHARE_VALUE = value;
	}
	
	/** 初始化发布动态的参数 */
	public void initPublishParams(int type , String sync , String syncValue , int shareSource , long shareValue  )
	{
		this.TYPE = type;
		this.SYNC = sync;
		this.SYNC_VALUE = syncValue;
		this.SHARE_SOURCE = shareSource;
		this.SHARE_VALUE = shareValue;
	}
	
	public boolean isUserSpecialAddress( )
	{
		return isUserSpecialAddress;
	}
	
	public void setUserSpecialAddress( boolean isUserSpecialAddress )
	{
		this.isUserSpecialAddress = isUserSpecialAddress;
	}
}
