
package net.iaround.ui.datamodel;

import android.content.ContentValues;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;

import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.KeyWord;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.KeyWordWorker;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.Item;
import net.iaround.model.entity.VersionDetectBean;
import net.iaround.model.login.bean.LoginResponseBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DownLoadKeywords;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.RankingTitleUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.fragment.MessageFragmentIm;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


/**
 * 起始模块数据模型
 *
 * @author linyg
 *
 */
public class StartModel extends Model
{
	private static StartModel startModel;
	private long forbidTime = 0;// 禁言时间
	private int loginConnectStatus = 1;

	private StartModel( )
	{
	}

    public static StartModel getInstance( )
	{
		if ( startModel == null )
		{
			startModel = new StartModel( );
		}
		return startModel;
	}

	/**
	 * 登录后的全局参数初始化
	 *
	 * @param context
	 * @param resMap
	 *            登陆或注册协议返回体
	 */
	public void loginModel(Context context , HashMap< String , Object > resMap )
	{
		JSONObject json = new JSONObject(resMap);
		loginData(context, json.toString() );
	}

	/**
	 * 登陆
	 *
	 * @param jsonString
	 * @return
	 * @time 2011-6-16 上午09:26:14
	 * @author:linyg
	 * @throws JSONException
	 */

//	public LoginResponseBean loginData(Context context , String jsonString )
//	{
//		LoginResponseBean bean = GsonUtil.getInstance( )
//			.getServerBean( jsonString, LoginResponseBean.class );
//
//		if(bean!=null)
//		{
//			if(bean!=null)
//			{
//				if(bean.isSuccess())
//				{
//					bean.loginSuccess( context );
//				}
//			}
//		}
//		return  bean;
//	}
//
//
	/**
	 * 版本检测
	 *
	 * @param jsonString
	 * @return
	 * @time 2011-7-26 上午09:36:07
	 * @author:linyg
	 */
	public VersionDetectBean checkVersion(String jsonString )
	{
		VersionDetectBean bean = GsonUtil.getInstance( ).getServerBean( jsonString,
			VersionDetectBean.class );
		if ( bean != null && bean.isSuccess( ) )
		{
			Common.getInstance( ).currentSoftVersion = bean.newversion;
			Common.getInstance( ).currentSoftUrl = bean.url;

			if ( CommonFunction.isEmptyOrNullStr( Common.getInstance( ).versionContent ) )
			{
				Common.getInstance( ).versionContent = bean.contenturl;
			}
			Common.getInstance( ).versionFlag = ( int ) bean.flag;
		}

		return bean;
	}

//	// 获取本地国家数据
//	public ArrayList< Country > getContryList(Context context )
//	{
//		ArrayList< Country > allCountry = new ArrayList< Country >( );
//		String[ ] strCountrys = context.getResources( ).getStringArray( R.array.countrys );
//		try
//		{
//			for ( String strCountry : strCountrys )
//			{
//				String[ ] codeName = strCountry.split( "," );
//				if ( codeName.length > 1 )
//				{
//					Country country = new Country( );
//					country.setCode( codeName[ 0 ] );
//					country.setName( codeName[ 1 ] );
//					country.setAbbreve( codeName[ 2 ] );
//					allCountry.add( country );
//				}
//			}
//		}
//		catch ( Exception e )
//		{
//			e.printStackTrace( );
//		}
//		return allCountry;
//	}

	/** 禁言时间 **/
	public long getForbidTime( )
	{
		return this.forbidTime;
	}

	/** 禁言时间 **/
	public void setForbidTime( String time )
	{
		if ( time != null )
		{
			JSONObject json;
			try
			{
				json = new JSONObject( time );
				int type = json.optInt( "type" );
				if ( type == 1 )
				{ // 禁言
					this.forbidTime = json.optLong( "datetime" );
				}
				else
				{
					this.forbidTime = 0;// 解禁言
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
	}
//	/**
//	 * 获取用户的头像
//	 *
//	 * @param url
//	 * @return void
//	 */
//	public void getIconBitmap(final Context context , final String url )
//	{
//		new Thread(new Runnable( )
//		{
//			@Override
//			public void run( )
//			{
//				try
//				{
//					int dp_6 = CommonFunction.dipToPx( context , 6 );
//					Bitmap bm = ConnectorManage.getInstance( context ).getBitmap( url ,
//							CommonFunction.getSDPath( ) + "/cacheimage_new/" , "png" , dp_6 ,
//							true );
//					Common.getInstance( ).loginUser.setIconBitmap( bm );
//				}
//				catch ( Exception e )
//				{
//				}
//			}
//		} ).start( );
//	}
//
//	/**
//	 * 获取reg的请求地址,当每访问一次，返回失败或网络异常时，将其记录为域名解析失败。
//	 *
//	 * @return String
//	 */
//	public String getRegUrl(Context context , int ipcount )
//	{
////		String tmpLoginUrl = SharedPreferenceUtil.getInstance( context ).getString(
////				SharedPreferenceUtil.REG_URL );
//		// 先判断上一次成功的域名是否存在
//		String tmpLoginUrl = "";
//		if ( CommonFunction.isEmptyOrNullStr( tmpLoginUrl ) )
//		{
//			if ( ipcount != 0 )
//			{
//				Config.loginUrl = Config.loginUrls[ 0 ];
//			}
//		}
//		else
//		{
//			Config.loginUrl = tmpLoginUrl;
//		}
//		return Config.loginUrl;
//	}
//
//	/**
//	 * 更新成功，则将本地保存
//	 *
//	 * @param context
//	 */
//	public void updateDeviceLocal(Context context , String result )
//	{
//		SharedPreferenceUtil.getInstance( context ).putBoolean(
//				SharedPreferenceUtil.DEVICE_ACTIVITY , true );
//	}
//
	// 1-正常网络，2-网络正常登录中，3-网络连接失败 4-登录超时
	public static final int TYPE_NET_LOGIN_TIME_OUT = 4;
	public static final int TYPE_NET_WORK_DISABLED = 3;
	public static final int TYPE_NET_WORK_LOGINING = 2;
	public static final int TYPE_NET_HAVED_LOGIN = 1;


	/*
	 * 1-正常网络，2-网络正常登录中，3-网络连接失败
	 */
	public int getLoginConnnetStatus( )
	{
		return loginConnectStatus;
	}

	/**
	 * 登陆
	 *
	 * @param jsonString
	 * @return
	 * @time 2011-6-16 上午09:26:14
	 * @author:linyg
	 * @throws JSONException
	 */

	public LoginResponseBean loginData(Context context , String jsonString )
	{
		LoginResponseBean bean = GsonUtil.getInstance( ).getServerBean( jsonString, LoginResponseBean.class );
		Item item = RankingTitleUtil.getInstance().getTitleItemFromLogin(jsonString);
		if(item != null && bean != null){bean.setItem(item);

		}

		if(bean!=null)
		{
			if(bean!=null)
			{
				if(bean.isSuccess())
				{
					bean.loginSuccess( context );
				}
			}
		}
		return  bean;
	}

	/**
	 * 获取reg的请求地址
	 *
	 * @param context
	 * @return
	 */
	public String changeRegUrl( Context context )
	{
		if ( Config.loginUrls[ 0 ].equals( Config.loginUrl ) )
		{
			Config.loginUrl = Config.loginUrls[ 1 ];
		}
		else
		{
			Config.loginUrl = Config.loginUrls[ 0 ];
		}

		return Config.loginUrl;
	}

	public void setLoginConnnetStatus( int status )
	{
		loginConnectStatus = status;
		if(MessageFragmentIm.instant!=null)
		{
			Message msg = new Message( );
			msg.what = MessageFragmentIm.REFRESH_TITLE;
			msg.arg1 = status;
			MessageFragmentIm.instant.mHandler.sendMessage(msg );
		}
	}

	/**
	 * 释放
	 *
	 * @time 2011-6-16 上午09:30:03
	 * @author:linyg
	 */
	public void reset( )
	{
		startModel = null;
	}

	/**
	 * 隐私设置
	 *
	 * @param result
	 * @time 2011-9-8 上午09:49:50
	 * @author:linyg
	 */
	public void paramPrivacy( Context activity , String result )
	{
		if ( result != null && !"".equals( result ) )
		{
			JSONObject json;
			try
			{
				json = new JSONObject( result );
				if ( json.optInt( "status" ) == 200 )
				{
					String pushStartTime = CommonFunction.jsonOptString( json ,
							"pushbegintime" );
					String pushEndTime = CommonFunction.jsonOptString( json , "pushendtime" );
					pushStartTime = pushStartTime.split( ":" )[ 0 ];
					pushEndTime = pushEndTime.split( ":" )[ 0 ];
					saveData( activity , json );

				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
	}

	private void saveData( Context context , JSONObject json )
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		long uid = Common.getInstance( ).loginUser.getUid( );
		String recCommentsKey = SharedPreferenceUtil.REC_COMMENTS + uid;
		String recStartTimeKey = SharedPreferenceUtil.REC_START_TIME + uid;
		String recEndTimeKey = SharedPreferenceUtil.REC_END_TIME + uid;
		String autoLoginKey = SharedPreferenceUtil.AUTO_LOGIN + uid;
		String dndTimeKey = SharedPreferenceUtil.DND_SETTING + uid;
		String showContentKey = SharedPreferenceUtil.HIDE_CHAT_DETAIL + uid;
//		String pushChatKey = SharedPreferenceUtil.REC_CHAT_GREETING + uid;
		String privateNewsKey = SharedPreferenceUtil.PRIVATE_NEWS_NOTIFY + uid;
		String dynamicNewsKey = SharedPreferenceUtil.DYNAMIC_NEWS_NOTIFY + uid;
		String accostNewsKey = SharedPreferenceUtil.ACCOST_NEWS_NOTIFY + uid;

		sp.putBoolean( autoLoginKey , true );
		sp.putBoolean( recCommentsKey , CommonFunction.jsonOptString( json , "pushreview" )
				.equals( "y" ) );// 是否推送评论
		sp.putInt(
				recStartTimeKey ,
				Integer.valueOf( CommonFunction.jsonOptString( json , "pushbegintime" ).split(
						":" )[ 0 ] ) );// 推送结束时间（级免打扰开始时间）
		sp.putInt(
				recEndTimeKey ,
				Integer.valueOf( CommonFunction.jsonOptString( json , "pushendtime" ).split(
						":" )[ 0 ] ) );// 推送开始时间（即免打扰结束时间）
		sp.putBoolean( dndTimeKey ,
				CommonFunction.jsonOptString( json , "nodisturb" ).equals( "y" ) );// 是否开启免打扰
		sp.putBoolean( showContentKey , CommonFunction.jsonOptString( json , "showcontent" )
				.equals( "n" ) );// 推送是否显示正文内容（y为显示即“隐藏私聊正文”关闭，n为不显示即“隐藏私聊正文”开启）
//		sp.putBoolean( pushChatKey ,
//				CommonFunction.jsonOptString( json , "pushchat" ).equals( "y" ) );// 是否推送搭讪
		sp.putBoolean(dynamicNewsKey,CommonFunction.jsonOptInt(json,"dynamicPush") == 1);//私聊消息通知
		sp.putBoolean(privateNewsKey,CommonFunction.jsonOptInt(json,"chatPush") == 1);//动态消息通知
		sp.putBoolean(accostNewsKey,CommonFunction.jsonOptInt(json,"accostPush") == 1);//搭讪消息通知

	}

	/**
	 * 经纬度解析
	 *
	 * @param result
	 * @time 2011-8-29 上午09:49:44
	 * @author:linyg
	 */
	public void paramOffsetGeo( String result )
	{
		if ( result != null && !"".equals( result ) )
		{
			JSONObject json;
			try
			{
				json = new JSONObject( result );
				GeoData geo = new GeoData( );
				geo.setLat( json.optInt( "latoffset" ) );
				geo.setLng( json.optInt( "lngoffset" ) );
				// Common.getInstance().offsetLatLng = geo;
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
	}

	/*
	关键字处理  有新的关键字，如果关键字的个数超过了100条，
	 */
	public void handlerKeyWord( Context context , String jsonString )
	{
		try
		{
			JSONObject json = new JSONObject( jsonString );
			JSONArray arrayJson = json.optJSONArray( "keywords" );
			String url = CommonFunction.jsonOptString( json , "download" );
			if ( arrayJson != null && arrayJson.length( ) > 0 )
			{
				KeyWordWorker keywordWorker = DatabaseFactory.getKeyWordWorker( context );
				for ( int i = 0 ; i < arrayJson.length( ) ; i++ )
				{
					JSONObject keywordJson = arrayJson.getJSONObject( i );
					int flag = keywordJson.optInt( "flag" );
					int key = keywordJson.optInt( "keywordid" );
					int rank = keywordJson.optInt( "rank" );
					String value = CommonFunction.jsonOptString( keywordJson , "keywordname" );
					if ( flag == 1 )
					{ // 添加
						ContentValues values = new ContentValues( );
						values.put( KeyWordWorker.K_KID , key );
						values.put( KeyWordWorker.K_KEYWORD , value );
						values.put( KeyWordWorker.K_KEYWORD_LEVEL , rank );
						keywordWorker.onInsert( values );
						KeyWord.getInstance( context ).addKeyword( context , value,rank );
					}
					else if ( flag == 2 )
					{ // 修改
						ContentValues values = new ContentValues( );
						values.put( KeyWordWorker.K_KEYWORD , value );
						values.put( KeyWordWorker.K_KEYWORD_LEVEL , rank );
						String where = KeyWordWorker.K_KID + " = " + key;
						int success =  keywordWorker.onUpdate( values , where );
						if(success <=0)
						{
							values.put( KeyWordWorker.K_KID , key );
							keywordWorker.onInsert( values );
						}
						KeyWord.getInstance( context ).modifyKeyword( context,  value,rank);
					}
					else if ( flag == 3 )
					{ // 删除
						keywordWorker.delete( key );
						KeyWord.getInstance( context ).removeKeyword( value );
					}
				}
				// 更新当前的敏感字版本
				SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
				sp.putLong( SharedPreferenceUtil.KEYWORD_VERSION , System.currentTimeMillis( )
						+ Common.getInstance( ).serverToClientTime );
			}
			else if(!TextUtils.isEmpty( url ) && url.contains( "http" ))
			{
				CommonFunction.log( "shifengxiong","keyword download url=====>"+url );
				new DownLoadKeywords(context ,url).run();

			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}

	/**
	 * 检查敏感字符
	 */
	public void checkKeyWord( Context context )
	{
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		if ( sp.has( SharedPreferenceUtil.KEYWORD_VERSION ) )
		{
			SocketSessionProtocol.sessionKeywordVersion( context ,
					sp.getLong( SharedPreferenceUtil.KEYWORD_VERSION ) );
		}
	}



}
