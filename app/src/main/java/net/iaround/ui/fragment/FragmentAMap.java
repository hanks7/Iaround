package net.iaround.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapFragment;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.VisibleRegion;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.NearHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.AnimationController;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.LocationUtil.MLocationListener;
import net.iaround.ui.datamodel.UserInfo;
import net.iaround.ui.map.ActivityLocationMap;
import net.iaround.ui.map.MapUtils;
import net.nostra13.universalimageloader.core.assist.FailReason;
import net.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;


/**
 * @author
 * @ClassName: ActivityLocationAMap
 * @Description: 高德地图呈现页面（v2.0）
 * @date 2015-3-20 下午3:45:45
 */
public class FragmentAMap extends MapFragment implements OnClickListener, HttpCallBack
{

    /********************************
     * 变量声明部分
     ******************************/
    // 进入搜索页面的request code
    private static final int REQUEST_CODE_GET_LATLNG = 0x200;

    // 用于进入搜索时候判断

    /**
     * 加载进度显示
     */
    private ProgressBar mProgressBar;
    private static final int HINT_SHOW_TIME = 5000; // 提示显示时间 5秒
    private static final int FILTER_FLAG = -2000;
    private static final int FLITER_FLESH_DATA = -2001;

    // 定位位置
    private static final int POS_CURRENT_MAP = -2002;

    private int mLoadType = 0;


    private long GETNEARFLAG = 0;

    // 获取附近用户成功
    private final int SUCCES_NEAR_MEMBER = 1001;
    //
    private final int ERROR_NEAR_MEMBER = 1002;
    // 定位成功
    private final int GEO_SUCCESS = 3001;
    private final int FILTER_SEX = 3002;
    private final int DRAW_MEMBER = 1023;

    private ConnectorManage mConnectorCore;

    private ArrayList< Marker > mMarkerList;

    private LayoutInflater mInflater;
    private GeoData mCurrentGeo;
    private Bundle mBundle;

    // 任意拖动缩放地图可全球漫游 ->mNearbyHint
//    private RelativeLayout mNearbyHint;
    // 定位到当前位置 ->mBtn_tomylocation
//    private Button mBtn_tomylocation;
    private Runnable runAction;
    private AMap mAMap;
    private MapView mapView;
    private LatLng mCenterPoint;
    private LatLng mTempPoint;

    private float mPosCurrentLevel = 0;
    private boolean isFinish = false;

//    private NearbyFilterConditionsInfo filterData;

    // 地图中间点经纬度
    private String mCenter;
    // 左下角经纬度
    private String mLeft;
    // 右上角经纬度
    private String mRight;
    // 当前筛选的性别
    private String mGenderType;
    private int mMinAge = -1; // 最小年龄
    private int mMaxAge = -1; // 最大年龄
    private int mHoroscope; // 星座

    private int mIsVip; // vip
    private int mOccupation; // 职业
    private int mLove; // 情感状态
    private int mDialects; // 掌握语言
    private String mHometown; // 出生地

    public Activity activity;

    private HashMap< String, UserInfo> usersMap;
    private HashMap< String, View > usersViewMap;

    private ImageView ivmapHaloBg;
    private AnimationController mAnimationController;
    /**
     * 是否已定位个人位置，在定位成功后开启 否则移动地图不加载附近用户
     */
    private boolean mIsGetLoacation = false;

    /**
     * 我的位置标记
     */
    private Marker myLocationMarker;

    // 附近用户的标记
    // private ArrayList<Marker> nearMarkerList = new ArrayList<Marker>();

    // 附近用户列表
    private ArrayList< UserInfo > userInfoList;

    private UserInfo userSelfInfo;

    // marker与用户数据的对应map
    private WeakHashMap< UserInfo, Marker > userMarkerMap = new WeakHashMap< UserInfo, Marker >( );


    private View fragmentView;
//    private RelativeLayout rlMainView;

    /********************************
     * 生命周期部分
     ******************************/

    @Override
    public void onCreate( Bundle savedInstanceState )
    {
        try
        {
            super.onCreate( savedInstanceState );

        }
        catch ( Exception e )
        {

        }
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState )
    {
        activity = getActivity( );
        initData( );
        fragmentView = LayoutInflater.from( activity )
                .inflate( R.layout.activity_location_amap, null, false );


        // 地图模式联网
        mConnectorCore = ConnectorManage.getInstance( activity );


        initViews( savedInstanceState );

        MapUtils.setMapActivity( activity );

        loadTypeShowMap( );

        userSelfInfo = new UserInfo( );

        userSelfInfo.icon = Common.getInstance( ).loginUser.getIcon( );
        userSelfInfo.lat = Common.getInstance( ).loginUser.getLat( );
        userSelfInfo.lng = Common.getInstance( ).loginUser.getLng( );
        userSelfInfo.nickname = Common.getInstance( ).loginUser.getNickname( );
        userSelfInfo.userid = Common.getInstance( ).loginUser.getUid( );
        userSelfInfo.viplevel = Common.getInstance( ).loginUser.getViplevel( );
        userSelfInfo.distance = Common.getInstance( ).loginUser.getDistance( );
        userSelfInfo.gender = Common.getInstance( ).loginUser.getGender( );
        mAnimationController = new AnimationController( );
        return fragmentView;
    }

    /**
     * @Title: initViews
     * @Description: 初始化控件
     */
    private void initViews( Bundle savedInstanceState )
    {
        // 初始化标题

//        rlMainView = ( RelativeLayout ) fragmentView.findViewById( R.id.amap_ll );
//
//        mBtn_tomylocation = ( Button ) fragmentView.findViewById( R.id.btn_tomylocation );
//        mNearbyHint = ( RelativeLayout ) fragmentView.findViewById( R.id.map_nearby_hint );//gh


        // 如果是加载附近的人，则将标题隐藏
        if ( mLoadType == ActivityLocationMap.LOAD_NEAR_FRIEND )
        {

        }
        else if ( mLoadType == MapUtils.LOAD_TYPE_POS_MAP )
        {

        }

//        mProgressBar = new ProgressBar( activity );
//        mProgressBar.setIndeterminate( true );
//        int dp_24 = ( int ) ( getResources( ).getDimension( R.dimen.dp_1 ) * 24 );
//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( dp_24, dp_24 );
//        mProgressBar.setLayoutParams( lp );
//        mProgressBar.setIndeterminateDrawable(
//                activity.getResources( ).getDrawable( R.drawable.pull_round_pb ) );
//        mProgressBar.setVisibility( View.GONE );
//        ( ( LinearLayout ) fragmentView.findViewById( R.id.progressBar_layout ) )
//                .addView( mProgressBar );//gh

        mInflater = ( LayoutInflater ) activity.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        mapView = ( MapView ) fragmentView.findViewById( R.id.bmapView );

        mapView.onCreate( savedInstanceState );// 此方法必须重写

        if ( mAMap == null )
        {
            mAMap = mapView.getMap( );
            if ( mAMap != null )
            {
                // 初始化地图
                initMap( );
            }
        }

    }

    /**
     * @Title: initMap
     * @Description: 初始化地图
     */
    private void initMap( )
    {
        // 5.0隐藏地图放大缩小按钮
        mAMap.getUiSettings( ).setZoomControlsEnabled( false );


//        mAMap.setInfoWindowAdapter( new CustomInfoWindowAdapter( ) );

        mAMap.setOnCameraChangeListener( new OnCameraChangeListener( )
        {

            @Override
            public void onCameraChange( CameraPosition position )
            {


            }

            @Override
            public void onCameraChangeFinish( CameraPosition arg0 )
            {
                // TODO Auto-generated method stub
                if ( isFinish )
                    return;
                mAMap.getUiSettings( ).setScaleControlsEnabled( true );
                mCenterPoint = arg0.target;
                mTempPoint = mCenterPoint;
                if ( ( mLoadType == ActivityLocationMap.LOAD_NEAR_FRIEND ||
                        ActivityLocationMap.LOAD_NEAR_SEARCH == mLoadType ) && mIsGetLoacation )
                {


                    // clearNearMarkers( );
                    userInfoList.clear( );
//                    getNearMember( );//gh

                }
                mPosCurrentLevel = mAMap.getCameraPosition( ).zoom;

            }
        } );

        mAMap.setOnMarkerClickListener( new OnMarkerClickListener( )
        {

            @Override
            public boolean onMarkerClick( Marker marker )
            {
                Iterator< Entry< UserInfo, Marker > > iter = userMarkerMap.entrySet( ).iterator( );
                UserInfo userInfo = null;
                while ( iter.hasNext( ) )
                {
                    Map.Entry< UserInfo, Marker > entry = iter.next( );
                    if ( entry.getValue( ).equals( marker ) )
                    {
                        userInfo = entry.getKey( );
                        break;
                    }
                }
                if ( userInfo != null )
                {
                    if ( userInfo.isMerage )
                    {
//                        // 如果点击的是合并的标记点，则打开用户列表界面
//                        Intent intent = new Intent( activity, NearbyGroupGridActivity.class );
//                        intent.putParcelableArrayListExtra( "users", userInfo.userList );
//                        startActivity( intent );//gh
                    }
                    else
                    {
//                        User user = new User( );
//                        user.setUid( userInfo.userid );
//                        user.setIcon( userInfo.icon );
//                        user.setNickname( userInfo.nickname );
//                        SpaceOther
//                                .launchUser( activity, user.getUid( ), user, ChatFromType.NearMap );//gh


                    }
                }


                return true;
            }
        } );

    }


    private void loadTypeShowMap( )
    {

        mLoadType = ActivityLocationMap.LOAD_NEAR_FRIEND;
        mBundle = getArguments( );
        if ( mBundle != null )
        {
            mBundle.getDouble( ActivityLocationMap.LNG, 0 );
            mBundle.getDouble( ActivityLocationMap.LAT, 0 );
            mBundle.getString( ActivityLocationMap.NAME, "" );
            mLoadType = mBundle.getInt( ActivityLocationMap.MAP_TYPE, 0 );
        }

        Boolean isFromCreateGroup = false;
        /*if ( mLoadType == ActivityLocationMap.LOAD_NEAR_FRIEND )
        {
            // 来自附近用户
            mBtn_tomylocation.setVisibility( View.VISIBLE );
            mBtn_tomylocation.setOnClickListener( this );
            mNearbyHint.setVisibility( View.VISIBLE );

            posLocation( );

            mNearbyHint.postDelayed( new Runnable( )
            {

                @Override
                public void run( )
                {
                    // TODO Auto-generated method stub
                    mNearbyHint.setVisibility( View.GONE );

                }
            }, HINT_SHOW_TIME );
        }
        else */if ( mLoadType == ActivityLocationMap.LOAD_POS_MAP )
        {

            if ( mBundle != null )
            {

                if ( mBundle.getBoolean( "create_group" ) )
                {
                    isFromCreateGroup = true;


                }
                else
                {
                    if ( CommonFunction.isEmptyOrNullStr( mBundle.getString( "title" ) ) )
                    {
                    }
                    else
                    {

                    }
                }
            }


            if ( mBundle.containsKey( ActivityLocationMap.LAT ) &&
                    mBundle.containsKey( ActivityLocationMap.LNG ) )
            {
                double lat = mBundle.getDouble( ActivityLocationMap.LAT, 0 );
                double lng = mBundle.getDouble( ActivityLocationMap.LNG, 0 );
                // 绘制我的位置的标记
                drawPosMap( new LatLng( lat, lng ), mBundle.getString( "address" ),
                        isFromCreateGroup );
            }

        }
        else if ( ActivityLocationMap.LOAD_NEAR_SEARCH == mLoadType )
        {
            mIsGetLoacation = true;
            LatLng latlngSearch = new LatLng( mBundle.getDouble( ActivityLocationMap.LAT, 0 ),
                    mBundle.getDouble( ActivityLocationMap.LNG, 0 ) );

            String addressName = mBundle.getString( ActivityLocationMap.NAME, "" );
            searchResultBack( latlngSearch, addressName );
        }
    }

    private void posLocation( )
    {
        // 获取经纬度
        new LocationUtil( activity ).startListener(new MLocationListener( )
        {
            @Override
            public void updateLocation( int type, int lat, int lng, String address,
                                        String simpleAddress )
            {
                try
                {
                    if ( isFinish )
                        return;
                    mCurrentGeo = new GeoData( );
                    mCurrentGeo.setLat( lat );
                    mCurrentGeo.setLng( lng );
                    mCurrentGeo.setAddress( address );
                    mHandler.sendEmptyMessage( GEO_SUCCESS );

                    userSelfInfo.lat = lat;
                    userSelfInfo.lng = lng;
                }
                catch ( Exception e )
                {

                }

            }
        }, 2 );
        // mAMap.getUiSettings( ).setScaleControlsEnabled( false );
        mProgressBar.setVisibility( View.GONE );
    }

    /**
     * @param geoPoint          位置
     * @param address           地址
     * @param isFromCreateGroup
     * @Title: drawPosMap
     * @Description: 绘制我的位置的标记
     */
    private void drawPosMap( LatLng geoPoint, String address, boolean isFromCreateGroup )
    {
        changeCamera( CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder( ).target( geoPoint ).zoom( 16 ).build( ) ) );
        addLocationMarker( geoPoint, address, isFromCreateGroup );
        mPosCurrentLevel = mAMap.getCameraPosition( ).zoom;
    }


    @Override
    public void onSaveInstanceState( Bundle outState )
    {
        super.onSaveInstanceState( outState );
        if ( mapView != null )
        {
            mapView.onSaveInstanceState( outState );
        }

    }

    @Override
    public void onResume( )
    {
        super.onResume( );
        if ( mapView != null )
        {
            mapView.onResume( );
        }

//        initFilterData( );
    }

    @Override
    public void onPause( )
    {
        // TODO Auto-generated method stub
        super.onPause( );

        if ( mapView != null )
        {
            mapView.onPause( );
        }

    }

    public void finish( )
    {
        isFinish = true;
        if ( mAMap != null )
        {
            CommonFunction.log( "shifengxiong", "FragmentAMap --------- mAMap.clear( )" );
            mAMap.clear( );
            mAMap = null;
            CommonFunction.log( "shifengxiong", "FragmentAMap --------- clearNearMarkers" );
        }

        if ( mapView != null )
        {
            CommonFunction.log( "shifengxiong", "FragmentAMap ---------mapView.onDestroy" );
            mapView.onDestroy( );
            CommonFunction.log( "shifengxiong", "FragmentAMap ---------mapView.onDestroy OK" );
            mapView = null;
        }
    }


    @Override
    public void onDestroy( )
    {
        try
        {
            super.onDestroy( );
            CommonFunction
                    .log( "shifengxiong", "FragmentAMap ---------onDestroy clearNearMarkers" );
            clearNearMarkers( );


            if ( mapView != null )
            {
                CommonFunction.log( "shifengxiong", "FragmentAMap ---------mapView.onDestroy" );
                mapView.onDestroy( );
                CommonFunction.log( "shifengxiong", "FragmentAMap ---------mapView.onDestroy OK" );
                mapView = null;
            }
            CommonFunction.log( "shifengxiong", "FragmentAMap ---------onDestroy" );

        }
        catch ( Exception e )
        {
            // TODO: handle exception
            e.printStackTrace( );
            CommonFunction
                    .log( "shifengxiong", "FragmentAMap ---------onDestroy" + e.getMessage( ) );
        }


    }

    /********************************
     * 私有方法
     * ******************************/

    /** 初始化过滤用户条件 */
    private void initFilterData( )
    {
//        if ( filterData == null )
//        {
//            filterData = new NearbyFilterConditionsInfo( );
//        }
//        filterData = NearbyFilterUtil.getNearbyFilterData( activity );
    }


    private Handler mHandler = new Handler( )
    {

        @Override
        public void handleMessage( Message msg )
        {
            if ( isFinish )
                return;
            switch ( msg.what )
            {
                case SUCCES_NEAR_MEMBER:
                    // 获取附近用户成功
//                    try
//                    {
//
//                        mProgressBar.setVisibility( View.GONE );
//                        GETNEARFLAG = 0;
//                        String result = String.valueOf( msg.obj );
//                        MapUserListBean bean = GsonUtil.getInstance( )
//                                .getServerBean( result, MapUserListBean.class );
//                        JSONObject json = new JSONObject( result );
//                        if ( bean != null && bean.isSuccess( ) )
//                        {
//                            clearNearMarkers( );
//                            userInfoList.clear( );
//                            if ( ivmapHaloBg != null )
//                            {
//                                rlMainView.removeView( ivmapHaloBg );
//                                ivmapHaloBg = null;
//                            }
//                            ArrayList< UserInfo > userList = bean.getUserInfos( );
//                            userList.add( 0, userSelfInfo );
//                            drawMember( userList, 4, 7 );
//                        }
//                        else
//                        {
//                            if ( json.has( "error" ) )
//                            {
//                                ErrorCode.showError( activity, result );
//                            }
//                        }
//
//                        if ( mTempPoint != null )
//                        {
//                            getNearMember( );
//                        }
//                    }
//                    catch ( Exception e )
//                    {
//                        e.printStackTrace( );
//                    }//gh

                    break;
                case ERROR_NEAR_MEMBER:
                    GETNEARFLAG = 0;
                    mProgressBar.setVisibility( View.GONE );
                    break;
                case GEO_SUCCESS:
                    mCenterPoint = new LatLng( mCurrentGeo.getLat( ) * 1.0 / 1E6,
                            mCurrentGeo.getLng( ) * 1.0 / 1E6 );
                    changeCamera( CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder( ).target( mCenterPoint ).zoom( 16 )
                                    .build( ) ) );

                    if ( mLoadType == ActivityLocationMap.LOAD_NEAR_FRIEND ||
                            ActivityLocationMap.LOAD_NEAR_SEARCH == mLoadType )
                    {
                        // getNearMember();
                        mIsGetLoacation = true;
                    }
                    break;
                case FILTER_SEX:

                    break;
                case POS_CURRENT_MAP:
                    if ( LocationUtil.getCurrentGeo( activity ) != null )
                    {
                        mCenterPoint = new LatLng(
                                LocationUtil.getCurrentGeo( activity ).getLat( ) * 1.0 / 1E6,
                                LocationUtil.getCurrentGeo( activity ).getLng( ) * 1.0 / 1E6 );
                    }
                    else
                    {
                        mCenterPoint = new LatLng( mCurrentGeo.getLat( ) * 1.0 / 1E6,
                                mCurrentGeo.getLng( ) * 1.0 / 1E6 );
                    }
                    setCenter( mCenterPoint );
                    break;
                case DRAW_MEMBER:
//                    try
//                    {
//                        CommonFunction.log( "googlemap", "get image complete, then draw member" );
//                        String requestUrl = String.valueOf( msg.obj );
//                        UserInfo info = usersMap.remove( requestUrl );
//                        View view = usersViewMap.remove( requestUrl );
//                        if ( info != null && view != null )
//                        {
//                            Marker marker = userMarkerMap.get( info );
//                            if ( marker != null )
//                            {
//                                view.destroyDrawingCache( );
//                                NetImageView icon = ( NetImageView ) view
//                                        .findViewById( R.id.friend_icon );
//                                icon.execute( PicIndex.DEFAULT_MAP_FACE, requestUrl );
//                                Bitmap bitmap = getDrawableFromView( view );
//                                marker.setIcon( BitmapDescriptorFactory.fromBitmap( bitmap ) );
//                            }
//                        }
//
//                        // mapView.invalidate( );
//                        mAMap.invalidate( );
//                    }
//                    catch ( Exception e )
//                    {
//                        e.printStackTrace( );
//                    }//gh
                    break;

                default:
                    break;
            }
        }

    };

    /**
     * @Title: addLocationMarker
     * @Description: 绘制位置图标
     */
    private void addLocationMarker( LatLng position, String address, boolean isFromCreateGroup )
    {
        if ( myLocationMarker == null )
        {

            Bitmap bmpCompany = BitmapFactory
                    .decodeResource( getResources( ), R.drawable.location );
            // myLocationMarker = mAMap.addMarker( new MarkerOptions( ).title(
            // "我的位置" )
            // .icon( BitmapDescriptorFactory.fromBitmap( bmpCompany ) )
            // .position( position ) );
            MarkerOptions opt = new MarkerOptions( );
            opt.title( "我的位置" );
            opt.icon( BitmapDescriptorFactory.fromBitmap( bmpCompany ) );
            opt.position( position );
            myLocationMarker = mAMap.addMarker( opt );
            myLocationMarker.setSnippet( address );
            // MarkerOptions mMarkOption = new
            // MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bmpCompany));
            // mMarkOption.title("我的位置");
            // mMarkOption.snippet("snippet");
            // myLocationMarker = mGoogleMap.addMarker(mMarkOption);

        }
        else
        {
            myLocationMarker.setPosition( position );
            myLocationMarker.setSnippet( address );
        }
        if ( !isFromCreateGroup )
        {

            if ( !myLocationMarker.isInfoWindowShown( ) )
            {
                myLocationMarker.showInfoWindow( );
            }

        }
        else
        {
            myLocationMarker.hideInfoWindow( );
        }

    }

    /**
     * @param infoList
     * @Title: drawMember
     * @Description: 绘制附近的人的标记点
     */
//    protected void drawMember( ArrayList< UserInfo > infoList, int column, int row )
//    {
//
//        VisibleRegion visibleRegion = mAMap.getProjection( ).getVisibleRegion( );
//        LatLng leftBottom = visibleRegion.nearLeft;
//        LatLng rightTop = visibleRegion.farRight;
//        double lngWidth = rightTop.longitude * 1E6 - leftBottom.longitude * 1E6;
//        double latHeight = rightTop.latitude * 1E6 - leftBottom.latitude * 1E6;
//        double avgWidth = lngWidth / column;
//        double avgHeight = latHeight / row;
//        double startWidth = leftBottom.longitude * 1E6;
//        double startHeight = leftBottom.latitude * 1E6;
//        if ( activity instanceof ActivityLocationMap )
//            ( ( ActivityLocationMap ) activity )
//                    .initViewGroup( infoList, column, row, avgWidth, avgHeight, startWidth,
//                            startHeight );
//
//
//        for ( UserInfo userInfo : userInfoList )
//        {
//            int merageCount = 1;
//
//            String iconUrl = "";
//            UserInfo showUser = null;
//            if ( userInfo.isMerage )
//            {
//                showUser = userInfo.userList.get( 0 );
//                merageCount = userInfo.userList.size( );
//                iconUrl = userInfo.userList.get( 0 ).icon;
//            }
//            else
//            {
//                showUser = userInfo;
//                iconUrl = userInfo.icon;
//            }
//            if ( activity instanceof ActivityLocationMap )
//            {
//                View view = ( ( ActivityLocationMap ) activity )
//                        .getView( merageCount, iconUrl, userInfo, imageLoadingListener );
//                drawMember( userInfo, showUser, view, 1 );
//            }
//
//        }
//
//
//    }//gh

    /**
     * @param userInfo
     * @param view
     * @Title: drawMember
     * @Description: 添加附近的人的头像标记点到地图
     */
//    private void drawMember( UserInfo groupInfo, UserInfo userInfo, View view, int type )
//    {
//        if ( userInfo != null )
//        {
//            LatLng position = null;
//            UserInfo info = null;
//            boolean isShowAnim = false;
//            if ( userInfo.isMerage )
//            {
//                position = userInfo.userList.get( 0 ).getAMapLatLng( );
//                info = userInfo.userList.get( 0 );
//                if ( info.userid == Common.getInstance( ).loginUser.getUid( ) )
//                {
//                    isShowAnim = true;
//                }
//            }
//            else
//            {
//                position = userInfo.getAMapLatLng( );
//                info = userInfo;
//                if ( info.userid == Common.getInstance( ).loginUser.getUid( ) )
//                {
//                    isShowAnim = true;
//                }
//            }
//
//            if ( isShowAnim )
//            {
//
//                addAnimationMarker( position );
//
//            }
//
//            view.destroyDrawingCache( );
//            Bitmap bitmap = getDrawableFromView( view );
//            Marker marker = mAMap.addMarker( new MarkerOptions( ).title( userInfo.nickname )
//                    .icon( BitmapDescriptorFactory.fromBitmap( bitmap ) ).position( position ) );
//            marker.setSnippet( info.icon );
//            userMarkerMap.put( groupInfo, marker );
//
//
//        }
//    }//gh

//    private void addAnimationMarker( LatLng latLng )
//    {
//        ArrayList< BitmapDescriptor > giflist = new ArrayList< BitmapDescriptor >( );
//        //添加每一帧图片。
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_00 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_01 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_02 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_03 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_04 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_05 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_06 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_07 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_08 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_09 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_10 ) );
//        giflist.add( BitmapDescriptorFactory.fromResource( R.drawable.z_map_marker_11 ) );
//        MarkerOptions markerOptions = new MarkerOptions( );
//        markerOptions.position( latLng );
//        markerOptions.title( "" ).snippet( "" );
//        markerOptions.period( 1 );
//        markerOptions.draggable( false );
//        markerOptions.icons( giflist );
//        markerOptions.visible( true );
//        markerOptions.anchor( 0.5f, 0.5f );
//        Marker marker = mAMap.addMarker( markerOptions );
//        marker.setSnippet( userSelfInfo.icon );
//
//        //		ivmapHaloBg = new ImageView( activity );
//        //		Point point = mAMap.getProjection( ).toScreenLocation( latLng ) ;
//        //		ivmapHaloBg.setImageResource( R.drawable.zd_map_center_anim_ ) ;
//        //		int h =getResources( ).getDrawable( R.drawable.zd_map_center_anim_ ).getMinimumHeight( );
//        //		int w=getResources( ).getDrawable( R.drawable.zd_map_center_anim_ ).getMinimumWidth( );
//        //		Drawable drawable = getResources( ).getDrawable( R.drawable.zd_map_center_anim_ );
//        //		int x = point.x -w/2;
//        //		int y = point.y - h/2;
//        //		ivmapHaloBg.setX( x );
//        //		ivmapHaloBg.setY( y );
//        //		rlMainView.addView( ivmapHaloBg ) ;
//
//
//    }//gh

    /**
     * @Title: getNearMember
     * @Description: 获取附近的用户数据，并绘制到地图上
     */
//    protected void getNearMember( )
//    {
//		/*
//		 * if(mCenterPoint != null){
//		 * changeCamera(CameraUpdateFactory.newCameraPosition(new
//		 * CameraPosition.Builder().target(mCenterPoint).zoom(16).build())); }
//		 */
//        if ( mAMap == null )
//        {
//            return;
//        }
//        try
//        {
//            VisibleRegion visibleRegion = mAMap.getProjection( ).getVisibleRegion( );
//            LatLng leftBottom = visibleRegion.nearLeft;
//            LatLng rightTop = visibleRegion.farRight;
//
//            mProgressBar.setVisibility( View.VISIBLE );
//            // 先将上一次请求中断
//            mConnectorCore.closeGeneralHttp( GETNEARFLAG );
//
//            LinkedHashMap< String, Object > map = new LinkedHashMap< String, Object >( );
//            mCenter = ( int ) ( mCenterPoint.latitude * 1E6 ) + "," +
//                    ( int ) ( mCenterPoint.longitude * 1E6 );
//            map.put( "center", mCenter );
//            mLeft = ( int ) ( leftBottom.latitude * 1E6 ) + "," +
//                    ( int ) ( leftBottom.longitude * 1E6 );
//            map.put( "leftdown", mLeft );
//            mRight =
//                    ( int ) ( rightTop.latitude * 1E6 ) + "," + ( int ) ( rightTop.longitude * 1E6 );
//            map.put( "rightup", mRight );
//
//            // 性别
//            mGenderType = "all";
//            int sex = NearbyFilterUtil.getGenderFilter( activity );
//            if ( sex == 1 )
//            {
//                mGenderType = "m";
//            }
//            else if ( sex == 2 )
//            {
//                mGenderType = "f";
//            }
//
//            // 时间统一换算成分钟。 0表示不限
//            // (2.7版本新增参数说明)－1表示在线
//            int datetime = 0;
//            int time = NearbyFilterUtil.getTimeFilter( activity );
//            switch ( time )
//            {
//                case 0:
//                    datetime = 15;
//                    break; // 15m
//                case 1:
//                    datetime = 60;
//                    break; // 1h
//                case 2:
//                    datetime = 24 * 60;
//                    break; // 1day
//                case 3:
//                    datetime = 3 * 24 * 60;
//                    break; // 3day
//            }
//
//            map.put( "gender", mGenderType );
//            map.put( "logintime", datetime );
//
//
//            mHoroscope = NearbyFilterUtil.getConstellationFilter( activity );
//
//            map.put( "minage", mMinAge );
//            map.put( "maxage", mMaxAge );
//            map.put( "horoscope", mHoroscope == 0 ? "" : mHoroscope );
//
//            // GETNEARFLAG =
//            // mConnectorCore.businessPost(Config.sGetNearbyUserMap,
//            // map, this);
//            if ( mTempPoint != null )
//            {
//                mIsVip = Common.getInstance( ).loginUser.isVip() ?1:0;
//                mMinAge = filterData.minage;
//                mMaxAge = filterData.maxage;
//                mHoroscope = filterData.constellation;
//                mOccupation = filterData.profession;
//                mLove = filterData.love;
//                mDialects = filterData.dialects;
//
//                mHometown = filterData.hometown;
//                GETNEARFLAG = NearHttpProtocol
//                        .usersNearmap( activity, mCenter, mLeft, mRight, sex, datetime, mIsVip, mMinAge,
//                                mMaxAge, mHoroscope, mOccupation, mLove, mDialects, mHometown, this );
//                // GETNEARFLAG = NearHttpProtocol.mapNearUserList( this , map ,
//                // this );
//                mTempPoint = null;
//            }
//        }
//        catch ( Exception e )
//        {
//            GETNEARFLAG = 0;
//            mProgressBar.setVisibility( View.GONE );
//            Toast.makeText( activity, R.string.network_req_failed, Toast.LENGTH_SHORT ).show( );
//        }
//    }//gh

    /**
     * @Title: clearNearMarkers
     * @Description: 清除附近的人的标记
     */
    private void clearNearMarkers( )
    {

        if ( userMarkerMap != null )
        {
            CommonFunction.log( "shifengxiong", "FragmentAMap --------- userMarkerMap.clear" );
            userMarkerMap.clear( );
        }
        if ( mAMap != null )
        {
            CommonFunction.log( "shifengxiong", "FragmentAMap --------- mAMap.clear( )" );
            mAMap.clear( );
            CommonFunction.log( "shifengxiong", "FragmentAMap --------- clearNearMarkers" );
        }
    }

    /**
     * @param view
     * @return Drawable
     * @Title: getDrawableFromView
     * @Description: 从View中获取出Bitmap对象
     */
    private Bitmap getDrawableFromView( View view )
    {
        view.measure( MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED ),
                MeasureSpec.makeMeasureSpec( 0, MeasureSpec.UNSPECIFIED ) );
        view.layout( 0, 0, view.getMeasuredWidth( ), view.getMeasuredHeight( ) );
        view.buildDrawingCache( );
        Bitmap bitmap = view.getDrawingCache( );
        return bitmap;
    }

    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera( CameraUpdate update, AMap.CancelableCallback callback )
    {
        if ( mAMap != null )
        {
            mAMap.animateCamera( update, 1000, callback );
        }
        // aMap.moveCamera(update);
    }

    /**
     * @param update
     * @Title: changeCamera
     * @Description: 改变地图视图状态（不带回调）不带回调
     */
    private void changeCamera( CameraUpdate update )
    {
        changeCamera( update, null );
    }

    /**
     * @param position 需要移动到的位置 （默认缩放级别为16）
     * @Title: setCenter
     * @Description: 将地图移动到某个位置
     */
    private void setCenter( LatLng position )
    {
        changeCamera( CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder( ).target( position ).zoom( 16 ).build( ) ) );
    }

//    class CustomInfoWindowAdapter implements InfoWindowAdapter
//    {
//
//        @Override
//        public View getInfoContents( Marker marker )
//        {
//            return null;
//        }
//
//        @Override
//        public View getInfoWindow( Marker marker )
//        {
//            View view = null;
//            if ( marker.equals( myLocationMarker ) )
//            {
//                String address = marker.getSnippet( );
//                view = activity.getLayoutInflater( )
//                        .inflate( R.layout.map_popview_sendlocation, null );
//                TextView tv = ( TextView ) view.findViewById( R.id.tv_address );
//                tv.setText( address );
//                marker.setAnchor( 0.6f, 0.5f );
//                // view.findViewById( R.id.google_point_icon ).setVisibility(
//                // View.GONE );
//
//                // view.findViewById( R.id.google_point_icon ).setVisibility(
//                // View.VISIBLE );
//                // view.setVisibility( View.INVISIBLE );
//                return view;
//            }
//            else
//            {
//                UserInfo info = null;
//                Iterator< Entry< UserInfo, Marker > > iter = userMarkerMap.entrySet( ).iterator( );
//                while ( iter.hasNext( ) )
//                {
//                    Map.Entry< UserInfo, Marker > entry = iter.next( );
//                    if ( marker.equals( entry.getValue( ) ) )
//                    {
//                        info = entry.getKey( );
//                        break;
//                    }
//                }
//                if ( info != null && !info.isMerage )
//                {
//                    view = activity.getLayoutInflater( ).inflate( R.layout.map_popview_info, null );
//                    final NetImageView info_icon = ( NetImageView ) view
//                            .findViewById( R.id.iv_icon );
//                    final TextView info_nickname = ( TextView ) view
//                            .findViewById( R.id.tv_nickname );
//                    final TextView info_distance = ( TextView ) view
//                            .findViewById( R.id.tv_distance );
//                    info_icon.execute( PicIndex.DEFAULT_MAP_FACE, info.icon );
//                    SpannableString spSign = FaceManager.getInstance( activity )
//                            .parseIconForString( info_nickname, activity, info.nickname, 16 );
//                    info_nickname.setText( spSign );
//
//                    GeoData geo = LocationUtil.getCurrentGeo( activity );
//                    if ( geo != null )
//                    {
//                        int distance = LocationUtil
//                                .calculateDistance( info.lng, info.lat, geo.getLng( ), geo.getLat( ) );
//                        info_distance.setText( CommonFunction.covertSelfDistance( distance ) );
//                    }
//                }
//
//            }
//            return view;
//        }
//
//    }

    /**
     * 图片加载的监听器
     */
    private ImageLoadingListener imageLoadingListener = new ImageLoadingListener( )
    {

        @Override
        public void onLoadingStarted( String url, View arg1 )
        {

        }

        @Override
        public void onLoadingFailed( String url, View arg1, FailReason arg2 )
        {
            usersMap.remove( url );
            usersViewMap.remove( url );
        }

        @Override
        public void onLoadingComplete( String url, View arg1, Bitmap arg2 )
        {
            CommonFunction.log( "googlemap", "onLoadingComplete" );
            mHandler.sendMessage( mHandler.obtainMessage( DRAW_MEMBER, url ) );
        }

        @Override
        public void onLoadingCancelled( String url, View arg1 )
        {
            usersMap.remove( url );
            usersViewMap.remove( url );
        }
    };

    /********************************
     * OnClickListener接口实现
     ******************************/

    @Override
    public void onClick( View v )
    {
//        if ( v.equals( mBtn_tomylocation ) )
//        {
//            posLocation( );
//        }//gh
        // 筛选按钮


    }

    //


    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data )
    {
        // TODO Auto-generated method stub
        super.onActivityResult( requestCode, resultCode, data );
        if ( requestCode == ActivityLocationMap.REQUEST_CODE_GET_LATLNG )
        {
            if ( resultCode == Activity.RESULT_OK)
            {
                if ( data != null )
                {
                    LatLng latlngSearch = new LatLng( data.getDoubleExtra( "Lat", 0 ),
                            data.getDoubleExtra( "Lng", 0 ) );
                    String addressName = this.getString( R.string.map_search_result_back ) +
                            data.getStringExtra( "Name" );

                    searchResultBack( latlngSearch, addressName );
                }
            }
        }

    }


    private void searchResultBack( LatLng latlngSearch, String addressName )
    {
        mCenterPoint = latlngSearch;

        setCenter( mCenterPoint );

//        mNearbyHint.setVisibility( View.VISIBLE );


//        ( ( TextView ) mNearbyHint.findViewById( R.id.map_hint ) ).setText( addressName );

        if ( runAction != null )
        {

//            mNearbyHint.removeCallbacks( runAction );
        }
        else
        {
            runAction = new Runnable( )
            {

                @Override
                public void run( )
                {
                    // TODO Auto-generated method stub
//                    mNearbyHint.setVisibility( View.GONE );

                }
            };
        }
//        mNearbyHint.postDelayed( runAction, HINT_SHOW_TIME );
    }


    @Override
    public void onGeneralSuccess( String result, long flag )
    {
        if ( isFinish )
            return;
        if ( GETNEARFLAG == flag )
        {
            Message msg = new Message( );
            msg.what = SUCCES_NEAR_MEMBER;
            msg.obj = result;
            mHandler.sendMessage( msg );
        }
        else if ( ( FILTER_FLAG == flag || ActivityLocationMap.LOAD_NEAR_SEARCH == mLoadType ) &&
                mLoadType == ActivityLocationMap.LOAD_NEAR_FRIEND )
        {
            // 男女条件过滤
            CommonFunction.log( "mapmapmap........", "hello_map" );
            mHandler.sendEmptyMessage( FILTER_SEX );
        }
        else if ( POS_CURRENT_MAP == flag )
        {
            // 定位位置
            mHandler.sendEmptyMessage( POS_CURRENT_MAP );
        }
    }

    @Override
    public void onGeneralError( int e, long flag )
    {
        if ( isFinish )
            return;
        if ( GETNEARFLAG == flag )
        {
            Message msg = new Message( );
            msg.what = ERROR_NEAR_MEMBER;
            msg.obj = e;
            mHandler.sendMessage( msg );
        }
    }


    /********************************
     * 静态公开方法
     * ******************************/

    /**
     * 筛选性别
     *
     * @param
     * @return void
     */
    public static void filterSex( Context context )
    {
        // ConnectorManage.getInstance(context).onGeneralSuccess("", "",
        // FILTER_FLAG);
    }

    public void refreshNearMember( )
    {
        mTempPoint = mCenterPoint;

        //		clearNearMarkers( );
        //
        //		if ( userInfoList != null )
        //		{
        //			userInfoList.clear( );
        //		}
//        getNearMember( );//gh

    }

    private void initData( )
    {
        if ( activity instanceof ActivityLocationMap )
        {
            usersViewMap = ( ( ActivityLocationMap ) activity ).usersViewMap;
            usersMap = ( ( ActivityLocationMap ) activity ).usersMap;
            userInfoList = ( ( ActivityLocationMap ) activity ).userInfoList;
        }
        else
        {
            usersViewMap = new HashMap< String, View >( );
            usersMap = new HashMap< String, UserInfo >( );
            userInfoList = new ArrayList< UserInfo >( );
        }
    }

}
