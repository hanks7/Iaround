
package net.iaround.ui.map;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.datamodel.UserInfo;
import net.iaround.ui.dynamic.NotificationFunction;
import net.iaround.ui.fragment.FragmentAMap;
import net.iaround.ui.fragment.FragmentGoogleMap;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: LocationGoogleMapActivityV2
 * @Description: GoogleMap地图呈现页面（v2.0）
 * @date 2013-9-28 下午4:45:45
 */
public class ActivityLocationMap extends FragmentActivity implements OnClickListener {
    public final static String MAP_TYPE = "loadtype";
    public final static String LNG = "Lng";
    public final static String LAT = "Lat";
    public final static String NAME = "Name";

    // 个人轨迹
    public final static int LOAD_MY_POS = 0;
    // 地图位置
    public final static int LOAD_POS_MAP = 1;
    // 附近用户
    public final static int LOAD_NEAR_FRIEND = 2;
    // 带有标题
    public final static int LOAD_NEAR_SEARCH = 3;
    // 建圈子
    public final static int LOAD_CREAT_GROUP = 4;


    public static final int REQUEST_CODE_GET_FILTER = 0x200;
    public static final int REQUEST_CODE_GET_LATLNG = 0x100;
    RelativeLayout mTitleLayout;

    private FrameLayout flLeft;
    private ImageView mTitleBack;
    private TextView mTitleName;
    private ImageView mTitleRight;

//	private NearbyFilterConditionsInfo filterData;

    private FragmentAMap fragmentAmap;
    private FragmentGoogleMap fragmentGoogle;

    public HashMap<String, UserInfo> usersMap = new HashMap<String, UserInfo>();
    public HashMap<String, View> usersViewMap = new HashMap<String, View>();
    // 附近用户列表
    public ArrayList<UserInfo> userInfoList = new ArrayList<UserInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        CloseAllActivity.getInstance().addActivity(this);
        init();
        Bundle tBundle = getIntent().getExtras();

        if (tBundle != null) {
            //标题
            TextView title_text = (TextView) findViewById(R.id.tv_title);
            String title = tBundle.getString("title", getResources().getString(R.string.position));

            title = TextUtils.isEmpty(title) ? getResources().getString(R.string.position) : title;
            title_text.setText(title);
            title_text.setCompoundDrawables(null, null, null, null);

            //标题右侧按钮
            ImageView title_right_img = (ImageView) findViewById(R.id.iv_right);
            title_right_img.setVisibility(View.GONE);
        }

        if (MapUtils.isLoadNativeMap(this) || GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            fragmentAmap = new FragmentAMap();
            fragmentAmap.setArguments(tBundle);
            getFragmentManager().beginTransaction().add(
                    R.id.map_contain, fragmentAmap).commit();

        } else {
            fragmentGoogle = new FragmentGoogleMap();
            fragmentGoogle.setArguments(tBundle);
            getFragmentManager().beginTransaction()
                    .add(R.id.map_contain, fragmentGoogle).commit();
        }

    }

    private void init() {
        //标题栏
        mTitleLayout = (RelativeLayout) findViewById(R.id.activity_title);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        mTitleBack = (ImageView) findViewById(R.id.iv_left);
        mTitleName = (TextView) findViewById(R.id.tv_title);
        mTitleRight = (ImageView) findViewById(R.id.iv_right);

        mTitleBack.setVisibility(View.VISIBLE);
        mTitleBack.setImageResource(R.drawable.title_back);
//		mTitleName.setText( R.string.friend_find );
//
//		initFilterData( );
//		String titleStr = NearbyFilterUtil.getNearbyFilterTitle( this , filterData );
//		mTitleName.setText( titleStr );
        flLeft.setOnClickListener(this);
        mTitleBack.setOnClickListener(this);
        mTitleRight.setOnClickListener(this);
        mTitleName.setOnClickListener(this);
    }

//	/** 初始化过滤用户条件 */
//	private void initFilterData( )
//	{
//		if ( filterData == null )
//		{
//			filterData = new NearbyFilterConditionsInfo( );
//		}
//		filterData = NearbyFilterUtil.getNearbyFilterData( this );
//	}


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        CloseAllActivity.getInstance().removeActivity(this);

        CommonFunction.log("shifengxiong", "ActivityLocationMap ---------onDestroy");
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        NotificationFunction.getInstatant(this).cancelNotification();
        CommonFunction.showScreanAd(this);

    }


    @Override
    public void finish() {
        // TODO Auto-generated method stub
        if (MapUtils.isLoadNativeMap(this) || GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            if (fragmentAmap != null) {
                fragmentAmap.finish();
                getFragmentManager().beginTransaction().remove(fragmentAmap).commitAllowingStateLoss();
            }
        } else {
            if (fragmentGoogle != null) {
                getFragmentManager().beginTransaction().remove(fragmentGoogle).commitAllowingStateLoss();
            }
        }
        super.finish();
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // 点击返回按钮
//        if (v.equals(mTitleBack) || v.equals(flLeft)) {
//            finish();
//        }
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.fl_left:
                finish();
                break;
            case R.id.tv_title:
                finish();
            default:
                break;
        }
//		else if ( v.equals( mTitleRight ) )
//		{
//			NearbySearchActivity.launch( this , NearbySearchActivity.TAB_INDEX_MAP ,
//					REQUEST_CODE_GET_LATLNG );
//		}
//		else if ( v.equals( mTitleName ) )
//		{
//			Intent intent = new Intent( this , NearbyFilterFragmentActivity.class );
//			intent.putExtra( NearbyFilterFragmentActivity.MODE_KEY ,
//					NearbyFilterFragmentActivity.MODE_MAP );
//			startActivityForResult( intent , REQUEST_CODE_GET_FILTER );
//		}
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

//		if(REQUEST_CODE_GET_LATLNG == requestCode)
//		{
//			String titleStr = NearbyFilterUtil.getNearbyFilterTitle( this , filterData );
//			mTitleName.setText( titleStr );
//			
//			
//		}
//		if ( requestCode == REQUEST_CODE_GET_FILTER
//				&& resultCode == Activity.RESULT_OK )
//		{
//			// 返回筛选条件
//			if ( data == null )
//				return;
//
//			NearbyFilterConditionsInfo info = ( NearbyFilterConditionsInfo ) data
//					.getSerializableExtra( NearbyFilterUtil.FILTER_CONDITIONS_KEY );
//			filterData = info;
//			// 刷新标题
//			String titleStr = data.getStringExtra( NearbyFilterUtil.FILTER_TITLE_KEY );
//			mTitleName.setText( titleStr );
//			if(fragmentAmap!=null)
//			{
//				fragmentAmap.refreshNearMember( );
//			}
//			else if(fragmentGoogle!=null)
//			{
//				fragmentGoogle.refreshNearMember( );
//			}
//
//		}

        if (fragmentGoogle != null) {
            fragmentGoogle.onActivityResult(requestCode, resultCode, data);
        }
        if (fragmentAmap != null) {
            fragmentAmap.onActivityResult(requestCode, resultCode, data);
        }

    }

    private View decorView = null;
//	public View getMapView(SuperActivity vActivity , int LoatType , Bundle bundle )
//	{
//
//		if(MapUtils.isLoadNativeMap( this ))
//		{
//
//
//		}
//		else
//		{
//			Intent intent = new Intent( vActivity , ActivityLocationMap.class );
//			intent.putExtra( "loadtype" , LoatType );
//			intent.putExtra( "data" , bundle );
//			intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
//
//			Window window = vActivity.getLocalActivityManager( ).startActivity(
//					ActivityLocationMap.class.getSimpleName( ) , intent );
//
//			decorView = window.getDecorView( );
//			decorView.dispatchWindowFocusChanged( true );
//
//			ViewGroup pageParent = (ViewGroup) decorView.getParent( );
//			if ( pageParent != null )
//			{
//				pageParent.removeView( decorView );
//			}
//
//		}
//		return decorView;
//	}
//
//	public void initViewGroup(ArrayList< UserInfo > infoList , int column , int row, double avgWidth, double avgHeight, double startWidth, double startHeight)
//	{
//
//		double tempStartHeight=startHeight;
//		for ( int i = 0 ; i < column ; i++ )
//		{
//			for ( int j = 0 ; j < row ; j++ )
//			{
//				int tmpEndWidth = ( int ) startWidth + ( int ) avgWidth;
//				int tmpEndHeight = ( int ) startHeight + ( int ) avgHeight;
//
//				UserInfo groupInfo = new UserInfo( );
//				ArrayList< UserInfo > tmpInfoList = new ArrayList< UserInfo >( );
//				for ( UserInfo markerInfo : infoList )
//				{
//					// 如果用户位置在网格内，则进行分组
//					if ( markerInfo.lng >= startWidth && markerInfo.lng < tmpEndWidth
//						&& markerInfo.lat >= startHeight && markerInfo.lat < tmpEndHeight )
//					{
//						tmpInfoList.add( markerInfo );
//					}
//				}
//				if ( tmpInfoList.size( ) == 1 )
//				{
//					groupInfo = tmpInfoList.get( 0 );
//					userInfoList.add( groupInfo );
//				}
//				else if ( tmpInfoList.size( ) > 1 )
//				{
//					groupInfo.userList = tmpInfoList;
//					groupInfo.isMerage = true;
//					userInfoList.add( groupInfo );
//				}
//				if ( j == row - 1 )
//				{
//					startHeight = tempStartHeight;
//				}
//				else
//				{
//					startHeight += avgHeight;
//				}
//			}
//			startWidth += avgWidth;
//		}
//	}
//
//	public View getView(int merageCount, String iconUrl, UserInfo userInfo, ImageLoadingListener imageLoadingListener)
//	{
//		View view = ((LayoutInflater) getSystemService( Context.LAYOUT_INFLATER_SERVICE )).inflate( R.layout.map_popview , null );
//
//		HeadPhotoView icon = ( HeadPhotoView ) view.findViewById( R.id.friend_icon );
//		TextView textView = (TextView) view.findViewById( R.id.merage_count );
//		if ( merageCount > 1 )
//		{
//			textView.setText( "" + merageCount );
//			textView.setVisibility( View.VISIBLE );
//		}
//		if ( iconUrl != "" )
//		{
//			icon.executeRoundFrame( PicIndex.DEFAULT_MAP_FACE , iconUrl  );
//			usersMap.put( iconUrl , userInfo );
//			usersViewMap.put( iconUrl , view );
//		}
//		return view;
//	}


}
