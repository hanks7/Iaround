
package net.iaround.ui.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;



/**
 * @ClassName: LocationGoogleMapActivityV2
 * @Description: GoogleMap地图呈现页面（v2.0）
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-9-28 下午4:45:45
 * 
 */
public class FragmentGoogleMap extends Fragment {

	private GeoData mCurrentGeo;
	private Bundle mBundle;

	// Google地图
	private GoogleMap mGoogleMap;

	public Activity activity;
	
	/**
	 * 是否已定位个人位置，在定位成功后开启 否则移动地图不加载附近用户
	 */
	private boolean mIsGetLoacation = false;
	
	/**
	 * 我的位置标记
	 */
	private Marker myLocationMarker;
	
	private View fragmentView;
	
	@Override
	public View onCreateView( LayoutInflater inflater , ViewGroup container , Bundle savedInstanceState ) {
		activity =  getActivity( );
		initData();
		fragmentView = LayoutInflater.from( activity ).inflate(
				R.layout.activity_location_googlemap , null , false );
		initViews( );
		return fragmentView;
	}

	private void initData() {
		mBundle = getArguments();
	}
	
	
	/**
	 * @Title: initViews
	 * @Description: 初始化控件
	 */
	private void initViews(){
		if ( mGoogleMap == null ) {
			android.support.v4.app.Fragment frag = ( (FragmentActivity) activity )
					.getSupportFragmentManager( ).findFragmentById( R.id.google_map );
			mGoogleMap = ( (SupportMapFragment) frag ).getMap( );
		}

		if ( mGoogleMap != null ) {
			// 初始化地图
			initMap( );
		}
		
	}
	
	/**
	 * @Title: initMap
	 * @Description: 初始化地图
	 */
	private void initMap( ) {
		// 5.0隐藏地图放大缩小按钮
		mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
		mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
		mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());
		markLocation(mBundle.getDouble(Constants.LATITUDE_KEY), mBundle.getDouble(Constants.LONGITUDE_KEY), mBundle.getString(Constants.ADDRESS_KEY));
	}

	public void markLocation(double lat, double lon, String address){
		LatLng latLng = new LatLng(lat, lon);
		Bitmap bmpCompany = BitmapFactory.decodeResource( getResources( ) ,
				R.drawable.location_marker );
		Marker marker = mGoogleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(bmpCompany))
				.position(latLng));//.title(getString(R.string.location_pop_title))
		marker.setSnippet(address);
		marker.showInfoWindow();
		mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder( )
				.target( latLng ).zoom( 16 ).build( ) ));
	}


	private void posLocation( )
	{
		// 获取经纬度
		new LocationUtil( activity ).startListener(new LocationUtil.MLocationListener( )
		{
			@Override
			public void updateLocation( int type , int lat , int lng , String address ,
					String simpleAddress )
			{
				mCurrentGeo = new GeoData( );
				mCurrentGeo.setLat( lat );
				mCurrentGeo.setLng( lng );
				mCurrentGeo.setAddress( address );
			}
		} , 2 );
	}

	/**
	 * @Title: drawPosMap
	 * @Description: 绘制我的位置的标记
	 * @param geoPoint
	 *            位置
	 * @param address
	 *            地址
	 * @param isFromCreateGroup
	 */
	private void drawPosMap( LatLng geoPoint , String address , boolean isFromCreateGroup )
	{
		changeCamera( CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder( )
				.target( geoPoint ).zoom( 16 ).build( ) ) );
		addLocationMarker( geoPoint , address , isFromCreateGroup );
	}

	@Override
	public void onResume( ) {
		super.onResume( );
	}



	@Override
	public void onDestroy( )
	{
		mGoogleMap = null;
		super.onDestroy( );
	}

	/**
	 * @Title: addLocationMarker
	 * @Description: 绘制位置图标
	 */
	private void addLocationMarker( LatLng position , String address ,
			boolean isFromCreateGroup )
	{
		if ( myLocationMarker == null )
		{

			Bitmap bmpCompany = BitmapFactory.decodeResource( getResources( ) ,
					R.drawable.location_marker );
			myLocationMarker = mGoogleMap.addMarker( new MarkerOptions( )
					.icon( BitmapDescriptorFactory.fromBitmap( bmpCompany ) )
					.position( position ).title(address) );
			myLocationMarker.setSnippet( address );//.title( "我的位置" )
			CommonFunction.log( "shifengxiong" , "aaaaaaaaaaaaaaaaaaaaaaa" );

		}
		else
		{
			CommonFunction.log( "shifengxiong" , "bbbbbbbbbbbbbbbbbbbbbbb" );
			myLocationMarker.setPosition( position );
			myLocationMarker.setSnippet( address );
		}
		if ( !isFromCreateGroup )
		{
			CommonFunction.log( "shifengxiong" , "ccccccccccccccccccccccc" );
			myLocationMarker.showInfoWindow( );

		}
		else
		{
			CommonFunction.log( "shifengxiong" , "ddddddddddddddddddddd" );
			myLocationMarker.hideInfoWindow( );
		}

	}

	/**
	 * @Title: getDrawableFromView
	 * @Description: 从View中获取出Bitmap对象
	 * @param view
	 * @return Drawable
	 */
	private Bitmap getDrawableFromView( View view )
	{
		view.measure( MeasureSpec.makeMeasureSpec( 0 , MeasureSpec.UNSPECIFIED ) ,
				MeasureSpec.makeMeasureSpec( 0 , MeasureSpec.UNSPECIFIED ) );
		view.layout( 0 , 0 , view.getMeasuredWidth( ) , view.getMeasuredHeight( ) );
		view.buildDrawingCache( );
		Bitmap bitmap = view.getDrawingCache( );
		return bitmap;
	}

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update , GoogleMap.CancelableCallback callback )
	{
//		if(mGoogleMap!=null)
//		{
//			mGoogleMap.animateCamera( update , 1000 , callback );
//		}
	}

	/**
	 * @Title: changeCamera
	 * @Description: 改变地图视图状态（不带回调）
	 * @param update
	 */
	private void changeCamera( CameraUpdate update )
	{
		changeCamera( update , null );
	}

	/**
	 * @Title: setCenter
	 * @Description: 将地图移动到某个位置
	 * @param position
	 *            需要移动到的位置 （默认缩放级别为16）
	 */
	private void setCenter( LatLng position )
	{
		changeCamera( CameraUpdateFactory.newCameraPosition( new CameraPosition.Builder( )
				.target( position ).zoom( 16 ).build( ) ) );
	}

	class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
	{

		@Override
		public View getInfoContents( Marker marker )
		{
			return null;
		}

		@Override
		public View getInfoWindow( Marker marker )
		{
			View view = null;
			if ( marker.equals( myLocationMarker ) )
			{
				String address = marker.getSnippet( );
				view = activity.getLayoutInflater( ).inflate(
						R.layout.map_popview_show_location , null );
				TextView tv = ( TextView ) view.findViewById( R.id.tv_address );
				tv.setText( address );
			}
			return view;
		}

	}


}
