
package net.iaround.ui.map;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PhoneInfoUtil;


/**
 * 地图显示调度工具，根据不同的手机型号，选择不同的地图模式
 * 
 * @author linyg@iaround.net
 * @date 2012-5-23 下午2:06:44
 */
public class MapUtils
{
	public final static int LOAD_TYPE_MY_POS = 0; // 个人轨迹
	public final static int LOAD_TYPE_POS_MAP = 1; // 地图位置
	public final static int LOAD_TYPE_NEAR_FRIEND = 2;// 附近用户
	public final static int LOAD_TYPE_GROUP_USER = 3; // 群组用户
	public final static int LOAD_TYPE_ACCOST_SEND = 4;// 搭讪助手
	public static int load_nativemap_lib = 0;
	
	private View decorView = null;
	private static Activity activityMap;
	/**
	 * @ClassName: MapType
	 * @Description: 地图类型枚举
	 * @author zhonglong kylin17@foxmail.com
	 * @date 2013-12-31 上午10:19:54
	 * 
	 */
	public enum MapType
	{
		Native_MAP ,
		GOOGLE_MAP
	}
	
	/**
	 * @Title: getMapType
	 * @Description: 获取当前系统可显示的地图类型
	 * @param context
	 * @return
	 */
	public static MapType getMapType( Context context )
	{
		if ( isLoadNativeMap( context ) )
		{
			return MapType.Native_MAP;
		}
		else
		{
			return MapType.GOOGLE_MAP;
		}
	}
	
	/**
	 * 根据当前手机型号，显示不同的地图View模式
	 * 
	 * @param vActivity
	 * @return View
	 */
	public View getMapView(Activity vActivity , int LoatType )
	{
		
		return getMapView( vActivity , LoatType , null );
	}
	
	/**
	 * 根据当前手机型号，显示不同的地图View模式
	 * 
	 * @param vActivity
	 * @return View
	 */
	public View getMapView(Activity vActivity , int LoatType , Bundle bundle )
	{
		
		if ( isLoadNativeMap( vActivity ) )
		{ 			
			CommonFunction.log( "shifengxiong" ,"getMapView ===============checkMapActivity");
			if(decorView==null)
			{
//				Intent intent = new Intent( vActivity , LocationAMapActivity.class );
//				intent.putExtra( "loadtype" , LoatType );
//				intent.putExtra( "data" , bundle );
//				intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				
//				Window window = vActivity.getLocalActivityManager( ).startActivity(
//						LocationAMapActivity.class.getSimpleName( ) , intent );
//				
//				decorView = window.getDecorView( );
//				decorView.dispatchWindowFocusChanged( true );
				
//				ViewGroup pageParent = ( ViewGroup ) decorView.getParent( );
//				if ( pageParent != null )
//				{
//					pageParent.removeView( decorView );
//				}
			}
		}
		else if ( checkMapActivity( vActivity ) )
		{ // 显示google
			
			CommonFunction.log( "shifengxiong" ,"getMapView ===============checkMapActivity");
			if(decorView==null)
			{
//				Intent intent = new Intent( vActivity , LocationGoogleMapActivity.class );
//				intent.putExtra( "loadtype" , LoatType );
//				intent.putExtra( "data" , bundle );
//				intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				
//				Window window = vActivity.getLocalActivityManager( ).startActivity(
//						LocationGoogleMapActivity.class.getSimpleName( ) , intent );
//				
//				decorView = window.getDecorView( );
//				decorView.dispatchWindowFocusChanged( true );
//				
//				ViewGroup pageParent = ( ViewGroup ) decorView.getParent( );
//				if ( pageParent != null )
//				{
//					pageParent.removeView( decorView );
//				}
			}
		}
		
		else
		{ // 显示web
		
			if ( LoatType == LOAD_TYPE_NEAR_FRIEND )
			{ // 附近好友
//				CommonFunction.log( "shifengxiong" ,"getMapView ===============LocationWebMapNearMember");
//				decorView = new LocationWebMapNearMember( vActivity );
			}
			else
			{
				Toast.makeText( vActivity , R.string.not_support_map , Toast.LENGTH_SHORT )
						.show( );
				return new View( vActivity );
			}
		}
		return decorView;
	}
	
	/**
	 * 显示一个位置点的坐标
	 * 
	 * @param vActivity
	 * @param lat
	 *            经度
	 * @param lng
	 *            纬度
	 * @param address
	 *            地址信息
	 * @param title
	 *            标题
	 */
	public static void showOnePositionMap(Context vActivity , int loadtype , int lat ,
                                          int lng , String address , String title )
	{
		if ( lat == 0 && lng == 0 )
		{
			return;
		}

		Intent intent = new Intent( );
		intent.setClass( vActivity , ActivityLocationMap.class );

		if ( intent != null )
		{
			
			Double latitude = lat * 1.0 / 1E6;
			Double longitude = lng * 1.0 / 1E6;
			
			Bundle bundle = new Bundle( );

			bundle.putDouble( ActivityLocationMap.LNG , longitude );
			bundle.putDouble( ActivityLocationMap.LAT , latitude );
			bundle.putInt( "loadtype" , loadtype );
			bundle.putString( "address" , address );
			bundle.putString( "title" , title );

			bundle.putInt( ActivityLocationMap.MAP_TYPE ,
					ActivityLocationMap.LOAD_POS_MAP );
			
			intent.putExtras( bundle ) ;
			
			vActivity.startActivity( intent );
		}
	}
	
	/**
	 * 晒选地图中的相应用户列表
	 * 
	 * @param vActivity
	 * @param view
	 * @return void
	 */
	public void filterSexMap( Activity vActivity , View view )
	{
		if ( isLoadNativeMap( vActivity ) )
		{
//			if ( baiduMapView != null )
//			{
//				baiduMapView.filterSex( vActivity );
//			}
		}
		else if ( checkMapActivity( vActivity ) )
		{
//			LocationGoogleMapActivity.filterSex( vActivity );
		}
		else
		{
//			if ( view != null && view instanceof LocationWebMapNearMember )
//			{
//				( ( LocationWebMapNearMember ) view ).filterSex( );
//			}
		}
	}
	
	/**
	 * 检测本地是否包含google map
	 * 
	 * @Title: checkMapActivity
	 */
	public static boolean checkMapActivity( Context context )
	{
		int status = ConnectionResult.SERVICE_MISSING;
		try
		{
			// zhonglong 更新googlemap2.0后，修改判断方法
			// Class.forName("com.google.android.maps.MapActivity");
			status = GooglePlayServicesUtil.isGooglePlayServicesAvailable( context );
			
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
        return status == ConnectionResult.SUCCESS;
	}
	
	/**
	 * 判断是否可以加载百度地图
	 * 
	 * @param context
	 * @return boolean
	 */
	public static boolean isLoadNativeMap( Context context )
	{
		if ( load_nativemap_lib > 0 )
		{
			return load_nativemap_lib == 1;
		}
		load_nativemap_lib = 1;
		if ( PhoneInfoUtil.getInstance( context ).isChinaCarrier( ) )
		
		{
			CommonFunction.log( "****************" , "e1:" + load_nativemap_lib );
		}
		else
		{
			load_nativemap_lib = 2;
		}
		CommonFunction.log( "****************" , "e2:" + load_nativemap_lib );
		return load_nativemap_lib == 1;
	}
	
	/**
	 * @Title: isSupportBaiduMap
	 * @Description: 判断是否支持百度地图so库
	 * @return boolean
	 */
	public static boolean isSupportBaiduMap( )
	{
		try
		{
			String cpuABI = "";
			cpuABI = Build.CPU_ABI;
            return !cpuABI.contains("x86");
        }
		catch ( Exception e )
		{
			e.printStackTrace( );
			return false;
		}
	}
	
	/**
	 * 移除显示的地图，由于将map转成view之后，其将不具备activity的生命周期
	 * 
	 * @param vActivity
	 * @return void
	 */
	public static void destroyMapView( Activity vActivity )
	{
		if ( checkMapActivity( vActivity ) )
		{
//			vActivity.getLocalActivityManager( ).destroyActivity(
//					LocationGoogleMapActivity.class.getSimpleName( ) , true );
		}
	}
	
	public void HideGoogleMapView()
	{
		if(decorView!=null)
		{
			decorView.setVisibility( View.GONE );
		}
	}
	
	public static void setMapActivity(Activity mapActivity)
	{
		activityMap = mapActivity ;
	}
	public static Activity getMapActivity()
	{
		return activityMap ;
	}
	
	public void refreshNearMember()
	{
		if(activityMap!=null)
		{
//			if(activityMap instanceof LocationGoogleMapActivity)
//			{
//				((LocationGoogleMapActivity)activityMap).refreshNearMember( );
//			}
//			else if(activityMap instanceof LocationAMapActivity)
//			{
//				((LocationAMapActivity)activityMap).refreshNearMember( );
//			}
		}
	}
}
