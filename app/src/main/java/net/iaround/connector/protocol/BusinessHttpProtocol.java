
package net.iaround.connector.protocol;


import android.app.Activity;
import android.content.Context;
import android.util.Log;

import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PhoneInfoUtil;

import java.util.LinkedHashMap;


/**
 * 业务协议接口
 * 
 * @author linyg
 * @date 2012-8-25 v2.6.0
 */
public class BusinessHttpProtocol{

	/*********************************
	 * 表情相关接口
	 *********************************/
	/** 获取表情列表的接口 */
	public static String sProtocolGetFaceList = "/maps/list_4_1";
	/** 获取表情详情的接口 */
	public static String sProtocolGetFaceDetail = "/maps/detail_4_1";
	/** 购买表情的接口 */
	public static String sProtocolBuyFace = "/maps/buy";
	/** 获取用户拥有的表情的接口 */
	public static String sProtocolGetUserFace = "/maps/userownslist";
	/** 获取表情购买记录的接口 */
	public static String sProtocolFaceBuyHistory = "/maps/buyhistory";


	public static long businessPost( Context context , String url ,
			LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url, map,
			ConnectorManage.HTTP_BUSINESS, callback );
//		return 100;
	}

//	public static void businessPost(int id, Context context , String url ,
//									HashMap< String , String > map , HttpStringCallback callback )
//	{
//		OkHttpManager.getInstance( context ).asynPost( id,url, map,
//			ConnectorManage.HTTP_BUSINESS, callback );
//	}
	

	/**
	 * 获取圈子列表头部的广告
	 * 
	 * @param context
	 * @param lat
	 * @param lng
	 * @param address
	 * @param widthAndHeight
	 *            分辨率 320*480
	 * @return
	 */
	public static long businessAdChat( Context context , int lat , int lng , String address ,
			String widthAndHeight , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "address" , address );
		map.put( "dpi" , widthAndHeight );
		return businessPost( context, "/ad/chat", map, callback );
	}
	
	/**
	 * 经纬度矫正
	 * 
	 * @param context
	 * @param lat
	 * @param lng
	 * @return
	 * @throws ConnectionException
	 */
	public static String userLngLatset( Context context , int lat , int lng )
			throws ConnectionException
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
//		return ConnectorManage.getInstance( context ).syncPost( "/user/lnglatset", map,
//			ConnectorManage.HTTP_BUSINESS );
		return "";
	}
	
	/**
	 * 注销
	 * 
	 * @param context
	 * @return
	 */
	public static long userCacel( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost(context, "/user/cancel" , map , callback );
	}
	
	/**
	 * 获取隐私
	 * 
	 * @param context
	 * @return
	 * @v3.0 下发参数有变
	 * @v5.4 下发参数有变
	 */
	public static long userPrivacyGet( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/user/privacy/get_5_4" , map , callback );// get_3_0
																					// get_5_4
	}
	


	/**
	 * 登录成功之后，更新用户手机信息
	 * 
	 * @param context
	 * @return
	 */
	public static long userNetwork( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "devicecode" , PhoneInfoUtil.getInstance( context ).getDeviceId( ) );
		map.put( "dpi" , Constants.windowWidth + "*" + Constants.windowHeight );
		map.put( "network" , PhoneInfoUtil.getInstance( context ).getNetType( ) );
		map.put( "plat" , PhoneInfoUtil.getInstance( context ).getPlat( ) );
		map.put( "devicemanufacturer" , PhoneInfoUtil.getInstance( context )
				.phoneManufacturer( ) );
		map.put( "carriername" , PhoneInfoUtil.getInstance( context ).phoneCarrierName( ) );
		map.put( "carriercountrycode" , PhoneInfoUtil.getInstance( context )
				.phoneCarrierCountryIso( ) );
		map.put( "mobilecountrycode" , PhoneInfoUtil.getInstance( context ).phoneOperator( ) );
		map.put( "crack" , "" ); // 是否越狱
		return businessPost( context , "/user/network_2_1" , map , callback );
	}
	
	/**
	 * @Title: userSearch
	 * @Description: 通过id或昵称搜索用户
	 * @param context
	 * @param keyword
	 *            关键字
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页数
	 * @param callback
	 *            回调
	 * @return
	 */
	public static long userSearch( Context context , String keyword , int pageNo ,
			int pageSize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , keyword );
		entity.put( "cityid" , "" );
		entity.put( "countryid" , "" );
		entity.put( "age" , "" );
		entity.put( "gender" , "" );
		entity.put( "distance" , "" );
		entity.put( "isonline" , "" );
		entity.put( "love" , "" );
		entity.put( "thinktext" , "" );
		entity.put( "bodytype" , "" );
		entity.put( "pageno" , pageNo );
		entity.put( "pagesize" , pageSize );
		return businessPost( context , "/users/search_3_3" , entity , callback );
	}
	

	/**
	 * 焦点列表 数据响应处理
	 * 
	 * @param pageno
	 * @param firstpagesize
	 * @param nextpagesize
	 * @param @throws Throwable
	 * @return long
	 */
	public static long focusList( Context context , int pageno , int firstpagesize ,
			int nextpagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "pageno" , pageno );
		entity.put( "firstpagesize" , firstpagesize );
		entity.put( "nextpagesize" , nextpagesize );
		return businessPost( context , "/focus/list" , entity , callback );
	}
	
	/**
	 * 出价信息
	 * 
	 * @param context
	 * @return
	 */
	public static long focusPriceInfo( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "toprank" , 1 );
		entity.put( "tailrank" , 10 );
		return businessPost( context , "/focus/priceinfo_4_0" , entity , callback );
	}
	
	/**
	 * 全球焦点商品列表
	 * 
	 * @param context
	 * @return
	 */
	public static long focusGoodsList( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );

		return businessPost( context , "/focus/goodslist" , entity , callback );
	}
	
	/**
	 * 我的出价
	 * 
	 * @param context
	 * @param type // type =1：金币，2：钻石
	 * @return
	 */
	public static long focusMyprice( Context context , int type , int goldnum ,
			int diamondnum , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "goldnum" , goldnum );
		entity.put( "diamondnum" , diamondnum );
		entity.put( "type" , type ); // type =1：金币，2：钻石
		return businessPost( context , "/focus/myprice_5_5" , entity , callback );
	}
	

	/**
	 * 使用同步发送位置信息
	 * 
	 * @param context
	 * @param lat
	 * @param lng
	 * @param address
	 * @return
	 */
	public static String sendLoaction( Context context , int lat , int lng , String address ,String city)
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "lat" , lat );
		entity.put( "lng" , lng );
		entity.put( "address" , address );
		entity.put( "city" , city );
		Log.d("sendLoaction","sendLoaction" +city);
		return ConnectorManage.getInstance( context ).syncPost( "/user/updateaddress" ,
				entity , ConnectorManage.HTTP_BUSINESS );
	}
	

	public static long getFaceMainData( Context context , int pageno , int pagesize ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "language" , CommonFunction.getLang( context ) );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return businessPost( context , sProtocolGetFaceList , map , callback );
	}
	
	public static long getFaceDetailData( Context context , int faceId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "language" , CommonFunction.getLang( context ) );
		map.put( "pkgid" , faceId );
		return businessPost( context , sProtocolGetFaceDetail , map , callback );
	}
	
	public static long buyFace( Context context , int faceId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "pkgid" , faceId );
		map.put( "language" , CommonFunction.getLang( context ) );
		return businessPost( context , sProtocolBuyFace , map , callback );
	}
	
	public static long ownFaces( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "language" , CommonFunction.getLang( context ) );
		return businessPost( context , sProtocolGetUserFace , map , callback );
	}
	
	/**
	 * 获取表情购买记录
	 * 
	 * @param
	 * @return 5.6 版本
	 */
	public static long FaceBuyHistory( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , sProtocolFaceBuyHistory , map , callback );
	}
	
	

	/**
	 * @Title: bindUserPwd
	 * @Description: 绑定手机号或更换绑定手机
	 * @param context
	 * @param countryCode
	 * @param phone
	 * @param smsCode
	 * @param userPwd
	 * @return
	 */
	public static long bindUserPwd( Activity context , String countryCode , String phone ,
			String smsCode , String userPwd , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "countrycode" , countryCode );
		map.put( "phone" , phone );
		map.put( "smscode" , smsCode );
		map.put( "password" , userPwd );
		return businessPost( context , "/sms/bindingcheck_4_2" , map , callback );
	}
	
	public static long GetCountryList( Activity context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/country/list" , map , callback );
	}
	
	public static long GetProvinceList( Activity context , int countryid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "countryid" , countryid );
		return businessPost( context , "/province/list" , map , callback );
	}
	
	public static long GetCityList( Activity context , int provinceid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "provinceid" , provinceid );
		return businessPost( context , "/province/citys" , map , callback );
	}
	
	/** 我的名片获取二维码 */
	public static long getQRCodeUrl( Activity context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/user/info/mycard" , map , callback );
	}
	
	/** android推送设备注册 */
	public static long iAroundPushRegister( Context context , String token ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "token" , token );
		return businessPost( context , "/push/register" , map , callback );
	}

	/** 获取个人长备注 */
	public static long getLongNote( Context context , long userid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		return businessPost( context , "/user/notes/getremarks" , map , callback );
	}
	
	/** 修改个人长备注 */
	public static long updateLongNote(Context context , long userid , String remarks ,
									  HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "userid" , userid );
		map.put( "remarks" , remarks );
		return businessPost( context , "/user/notes/updateremarks" , map , callback );
	}
	
	/** 中华电信4G用户信息上报 */
	public static long SendZhongHua4GUserInfo( Context context , String nop , String subtype ,
			String subno , String sac , String transid , String mac , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "nop" , nop );
		map.put( "subtype" , subtype );
		map.put( "subno" , subno );
		map.put( "sac" , sac );
		map.put( "transid" , transid );
		map.put( "mac" , mac );
		
		return businessPost( context , "/activity/chunghwa/userinfo" , map , callback );
	}
	
	/**
	 * @Description:获取隐私过滤恢复用户列表
	 * @author tanzy
	 * @date 2014-9-28
	 */
	public static long GetPrivacyUserlist( Context context , HttpCallBack callback )
	{
		return businessPost( context , "/user/privacy/userlist" , null , callback );
	}
	
	/**
	 * 获取对他（她）的隐私设置项信息
	 */
	public static long getInvisibilitySettingInfo( Context context , long userid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		return businessPost( context , "/user/privacy/options" , entity , callback );
	}
	
	/**
	 * 更新对他（她）的隐私设置
	 */
	public static long updateInvisibilitySettingInfo( Context context , long userid ,
			int type , String value , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "userid" , userid );
		entity.put( "type" , type );
		entity.put( "value" , value );
		return businessPost( context , "/user/privacy/updateoptions" , entity , callback );
	}
	
	/**
	 * 获取隐私设置各表数据
	 * */
	public static long getPrivacyDataList( Context context , int type , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "type" , type );
		return businessPost( context , "/user/privacy/optionslist" , entity , callback );
	}
	
	/**
	 * 批量设置隐私设置
	 * */
	public static long updatePrivacyList(Context context, int type , String value,String userids,HttpCallBack callback)
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "type" , type );
		entity.put( "value" , value );
		entity.put( "userids" , userids );
		return businessPost( context , "/user/privacy/updateoptionsbatch" , entity , callback );
	}
	
	public static long getMapRoaminglist( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/city/roaminglist" , map , callback );
	}
	

	/**
	 * 获取发现列表
	 * */
	public static long getFindList( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "plat" , 1 );
		map.put( "version" , Config.APP_VERSION );
		map.put( "packageid" , CommonFunction.getPackageMetaData( context ) );
		return businessPost( context , "/system/discoverylist" , map , callback );
	}
	
	/**
	 * 关键字搜索大学
	 * */
	public static long getSchoolList( Context context , String keyword , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "keyword" , keyword );
		map.put( "pageno" , 1 );
		map.put( "pagesize" , 20 );
		return businessPost( context , "/system/school/search" , map , callback );
	}
	
	/**
	 * 获取院系列表
	 * */
	public static long getDepartmentList( Context context , int schoolid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "schoolid" , schoolid );
		return businessPost( context , "/system/school/departmentlist" , map , callback );
	}
	
	/**
	 * 获取表情中文描述
	 * 
	 * @param context
	 * @param pkgid
	 * @param pkgid
	 * @param callback
	 * @return
	 */
	public static long getFaceDescribe( Context context , int pkgid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "pkgid" , pkgid );
		map.put( "plat" , 1 );
		return businessPost( context , "/maps/describe" , map , callback );
	}
	
	
	/**
	 * 获取资源列表
	 * 
	 * @param context
	 * @param plat
	 *            平台id
	 * @param position
	 *            1-附近,2-动态,3商店,4VIP页面,5左侧栏 6会员中心
	 * @param callback
	 * @return
	 */
	public static long getResourceList( Context context , int plat , int position ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "plat" , plat );
		map.put( "position" , position );
		map.put("province" , LocationUtil.getCurrentGeo( context ).getProvince( ));
		map.put("city" ,LocationUtil.getCurrentGeo( context ).getCity( ));
		return businessPost( context , "/resource/list" , map , callback );
	}
	
	/**
	 * 获取商店首页分类礼物的数据
	 * 
	 * @param context
	 * @param callback
	 * @return
	 */
	public static long getStoreData( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/gift/store" , map , callback );
	}
	

	/**
	 * 获取礼包详情
	 * 
	 * @param giftbagid
	 *            礼包ID
	 * @return
	 */
	public static long getGiftBagDetail( Context context , int giftbagid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "giftbagid" , giftbagid );
		return businessPost( context , "/gift/giftbag/detail" , map , callback );
	}
	
	
	/**
	 * 购买大礼包
	 * 
	 * @param giftbagid
	 *            礼包ID
	 * @param paytype
	 *            支付类型 1：金币购买，2：钻石购买，3：金币商品钻石直购
	 * @return
	 */
	public static long BuyGiftBag( Context context , int paytype , int giftbagid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "giftbagid" , giftbagid );
		map.put( "paytype" , paytype );
		return businessPost( context , "/gift/giftbag/buy" , map , callback );
	}
	
	/**
	 * 6.0版本新增 购买礼物
	 * 
	 * @param context
	 * @param paytype
	 *            支付方式 1：金币购买，2：钻石购买，3：金币商品钻石直购
	 * @param giftid
	 * @param callback
	 * @return
	 */
	public static long BuyGift( Context context , int giftid , int paytype ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "giftid" , giftid );
		map.put( "paytype" , paytype );
		return businessPost( context , "/user/gift/buy_6_0" , map , callback );
	}
	
	/**
	 * 获取任务列表
	 * 
	 * @param status 0全部，1已完成，2进行中
	 * @param pageno 第几页
	 * @param pagesize 每页显示数量
	 * */
	public static long getRewardTaskList( Context context , int status , int pageno ,
			int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "status" , status );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return businessPost( context , "/task/list" , map , callback );
	}
	
	/**
	 * 领取任务奖励
	 * 
	 * @param taskid 任务id
	 * */
	public static long ReceiveTaskReward( Context context , int taskid ,
			HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "taskid" , taskid );
		return businessPost( context , "/task/freeget" , map , callback );
	}
	
	/**
	 * 每日签到任务初始化
	 * */
	public static long getSignatureCondition( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/task/signinit" , map , callback );
	}

	/**
	 * 获取当天签到状态
	 * */
	public static long getSignatureStatus( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/task/signstatus" , map , callback );
	}

	/**
	 * 开始签到
	 * */
	public static long DoDailySign( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/task/sign" , map , callback );
	}
	
	/**
	 * 获取推荐礼物详细信息（6.0.1新接口）
	 * */
	public static long getGiftRecommend( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/gift/recommend" , map , callback );
	}

	/**
	 * 咪咕阅读 token
	 * */
	public static long getMiguReadToken( Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return businessPost( context , "/system/migutoken" , map , callback );
	}
}
