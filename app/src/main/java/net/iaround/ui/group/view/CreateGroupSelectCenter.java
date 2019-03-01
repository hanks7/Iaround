
package net.iaround.ui.group.view;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.util.URIUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.LocationUtil.MLocationListener;
import net.iaround.tools.MD5Utility;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;
import net.iaround.ui.fragment.FragmentAMap;
import net.iaround.ui.group.ICreateGroupParentCallback;
import net.iaround.ui.group.INextCheck;
import net.iaround.ui.group.bean.BuildingInfo;
import net.iaround.ui.group.bean.BuildingInfoBean;
import net.iaround.ui.group.bean.GoogleBuildingBean;
import net.iaround.ui.group.bean.GoogleBuildingInfo;
import net.iaround.ui.group.bean.GroupNextStep;
import net.iaround.ui.map.ActivityLocationMap;
import net.iaround.ui.map.MapUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;


/**
 * @ClassName: CreateGroupSelectCenter
 * @Description: 创建圈子——选择圈中心
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-9 下午4:14:14
 *
 */
public class CreateGroupSelectCenter extends SuperView implements INextCheck,
		OnPoiSearchListener
{

	private SuperActivity mActivity;

	private int switchAdress = 3;

	/** 大众点评开关 ***/
	private boolean isSwitchDianpin;
	/** 地图的view */
	private View mMapView;

	/** 地图容器 */
	private LinearLayout mMapLayout;
	/** 地标选择列表 */
	private PullToRefreshListView mListView;
	/** empty layout */
	private EmptyLayout mEmptyLayout;

	private RadioButton mCategoryCommunity;
	private RadioButton mCategoryBusiness;
	private RadioButton mCategorySchool;
	private RadioButton mCategoryAll;
	private RadioButton[ ] mCategorys = new RadioButton[ 4 ];
	private String[ ] mStrCategorys = new String[ 4 ];
	private List< PoiItem > poiItems;// poi数据

	private int mCurrentPage = 0;
	private int mTotalPage = 0;
	private int PAGE_SIZE = 20;
	private boolean hasNextPage;
	/** 数据适配器 */
	private DataAdapter mAdapter;

	/** 地标信息bean */
	private BuildingInfoBean mBuidingInfoBean;

	/** 谷歌地标信息bean */
	private GoogleBuildingBean mGoogleBuildingInfoBean;

	/** 数据列表 */
	private ArrayList< BuildingInfo > mDataList = new ArrayList< BuildingInfo >( );

	/** ViewPager上层回调 */
	private ICreateGroupParentCallback mParentCallback;

	/** 是否已加载数据 */
	private boolean isInitData = false;

	/** 当前可显示的地图类型 */
	private MapUtils.MapType mCurMapType;

	private GeoData mCurGeoData;

	// private String mCurTypeId = "";

	/** 地标下发标示 */
	private int mBuildingFlag = 1;

	/** 获取地标信息的flag */
	private long FLAG_GET_BUILDING_INFO;

	/** 用户所选的地标 */
	private BuildingInfo mSelBuilding;

	/** Google数据下一页的token */
	private String mNextPageToken;

	/**
	 * 记录上一次点击的RadioButton（由于radiogroup的机制是先选中下一个radiobutton，再取消上一个radiobutton，
	 * 防止遍历radiobutton时获取出错
	 */
	private RadioButton mLastCheckRadioButton;

	public CreateGroupSelectCenter( SuperActivity activity ,
									ICreateGroupParentCallback createGroupActivity , GeoData geoData )
	{
		super( activity , R.layout.view_create_group_selectcenter );
		this.mActivity = activity;
		this.mParentCallback = createGroupActivity;
		mCurMapType = MapUtils.getMapType( activity );
		this.mCurGeoData = geoData;

		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( mActivity );
		if ( sp.has( SharedPreferenceUtil.SWITCH ) )
		{
			String currentItemStatus = sp.getString( SharedPreferenceUtil.SWITCH );
			if ( currentItemStatus.length( ) >= 2 )
			{
				isSwitchDianpin = currentItemStatus.charAt( 0 ) == '1';
				if ( isSwitchDianpin )
				{
					switchAdress = 0;
				}
				else
				{
					isSwitchDianpin = currentItemStatus.charAt( 1 ) == '1';
					if ( isSwitchDianpin )
					{
						switchAdress = 1;
					}
				}

			}

		}
		initViews( );
		setListeners( );
		CommonFunction.log( "create_group" , "CreateGroupSelectCenter initView" );
	}

	private void initViews( )
	{


		mMapLayout = ( LinearLayout ) findViewById( R.id.map_layout );
		mListView = ( PullToRefreshListView ) findViewById( R.id.group_center_listview );
		mEmptyLayout = new EmptyLayout( mActivity , mListView.getRefreshableView( ) );
		mCategoryCommunity = ( RadioButton ) findViewById( R.id.category_community );
		mCategoryCommunity.setTag( 1 );
		mCategorys[ 0 ] = mCategoryCommunity;
		mCategoryBusiness = ( RadioButton ) findViewById( R.id.category_business );
		mCategoryBusiness.setTag( 2 );
		mCategorys[ 1 ] = mCategoryBusiness;
		mCategorySchool = ( RadioButton ) findViewById( R.id.category_school );
		mCategorySchool.setTag( 3 );
		mCategorys[ 2 ] = mCategorySchool;
		mCategoryAll = ( RadioButton ) findViewById( R.id.category_all );
		mCategoryAll.setTag( 0 );
		mCategorys[ 3 ] = mCategoryAll;
		mLastCheckRadioButton = mCategoryAll;


		hasNextPage = true;
		if ( switchAdress == 0 )
		{
			// 大众点评分类
			mStrCategorys[ 0 ] = new String( "小区" );
			mStrCategorys[ 1 ] = new String( "商务楼" );
			mStrCategorys[ 2 ] = new String( "学校" );
			mStrCategorys[ 3 ] = new String( "小区,商务楼,学校" );
		}
		else // switchAdress == 1 或其他)
		{
			// 高德地图关键字搜索方式(分类方式)
			findViewById( R.id.dianping_logo ).setVisibility( View.GONE );
			mStrCategorys[ 0 ] = new String( "小区" );
			mStrCategorys[ 1 ] = new String( "写字楼" );
			mStrCategorys[ 2 ] = new String( "学校" );
			mStrCategorys[ 3 ] = new String( "小区|写字楼|学校" );
		}
	}

	private void setListeners( )
	{
		mEmptyLayout.setErrorButtonClickListener( new View.OnClickListener( )
		{

			@Override
			public void onClick( View v )
			{
				hasNextPage = true;

				switch ( switchAdress )
				{
					case 0 :// 大众点评
						requestFromeDianping( 1 );
						break;
					case 1 : // 高德地图

						doSearchIaround( mCurrentPage );
						break;
					default :
						requestPageData( 1 );
						break;
				}

			}
		} );

		mListView.setMode( Mode.BOTH );
		mListView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2< ListView >( )
		{

			@Override
			public void onPullDownToRefresh( PullToRefreshBase< ListView > refreshView )
			{
				mBuildingFlag = 1;
				mCurrentPage = 0;
				hasNextPage = true;
				switch ( switchAdress )
				{
					case 0 :
						mCurrentPage++;
						requestFromeDianping( mCurrentPage );
						break;
					case 1 :
						doSearchIaround( mCurrentPage );
						break;
					default :
						requestPageData( 1 + mCurrentPage );
						break;
				}

			}

			@Override
			public void onPullUpToRefresh( PullToRefreshBase< ListView > refreshView )
			{
				if ( hasNextPage )
				{
					switch ( switchAdress )
					{
						case 0 :
							mCurrentPage++;
							requestFromeDianping( mCurrentPage );
							break;
						case 1 :
							doSearchIaround( mCurrentPage++ );
							break;
						default :
							requestPageData( mCurrentPage + 1 );
							break;
					}


				}
				else
				{
					mListView.postDelayed( new Runnable( )
					{
						public void run( )
						{
							mListView.onRefreshComplete( );
							mHandler.sendEmptyMessage( 1 );
						}
					} , 200 );
				}
			}
		} );
		mListView.setOnItemClickListener( new OnItemClickListener( )
		{

			@Override
			public void onItemClick( AdapterView< ? > arg0 , View arg1 , int position ,
									 long arg3 )
			{
				// TODO 创建圈子跳转到下一步，或者选m择中心返回
				BuildingInfo info = null;
				try
				{
					info = mDataList.get( position - 1 );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
				if ( info != null )
				{
					mSelBuilding = info;
					mParentCallback.goNext( getGroupNextStep( ) );
				}
			}
		} );
		mAdapter = new DataAdapter( );
		mListView.setAdapter( mAdapter );
		for ( int i = 0 ; i < mCategorys.length ; i++ )
		{
			mCategorys[ i ].setOnCheckedChangeListener( onCheckChangedListener );
		}
	}

	OnCheckedChangeListener onCheckChangedListener = new OnCheckedChangeListener( )
	{

		@Override
		public void onCheckedChanged( CompoundButton buttonView , boolean isChecked )
		{
			if ( isChecked )
			{
				hasNextPage = true;
				mTotalPage = 0;
				mLastCheckRadioButton.setChecked( false );
				mListView.setRefreshing( true , Mode.PULL_FROM_START );
				mLastCheckRadioButton = ( RadioButton ) buttonView;
			}
		}
	};

	/**
	 * @Title: initMap
	 * @Description: 初始化地图
	 */
	private void initMap( )
	{
		if ( mCurGeoData == null )
		{
			mCurGeoData = LocationUtil.getCurrentGeo( mActivity );
			if ( mCurGeoData == null )
			{
				new LocationUtil( mActivity ).startListener( new MLocationListener( )
				{

					@Override
					public void updateLocation( int type , int lat , int lng , String address ,
												String simpleAddress )
					{
						mCurGeoData = LocationUtil.getCurrentGeo( mActivity );
						initMap( );
					}
				} , 1 );
			}
			else
			{
				initMap( );
			}
		}
		else
		{
			if ( mMapView == null )
			{

				if(MapUtils.isLoadNativeMap( mActivity ))
				{

				}
				else
				{

				}
				int lat = mCurGeoData.getLat( );
				int lng = mCurGeoData.getLng( );

				Double latitude = mCurGeoData.getLat( ) * 1.0 / 1E6;
				Double longitude = mCurGeoData.getLng( ) * 1.0 / 1E6;

				Bundle bundle = new Bundle( );
				bundle.putBoolean( "create_group" , true );
//				bundle.putInt( "lat" , lat );
//				bundle.putInt( "lng" , lng );

				bundle.putDouble( ActivityLocationMap.LNG , longitude );
				bundle.putDouble( ActivityLocationMap.LAT , latitude );

				bundle.putInt( ActivityLocationMap.MAP_TYPE ,
						ActivityLocationMap.LOAD_POS_MAP );

				FragmentAMap firstFragment = new FragmentAMap();

//				FragmentGoogleMap firstFragment = new FragmentGoogleMap();
				firstFragment.setArguments(bundle);

				// Add the fragment to the 'fragment_container' FrameLayout
				mActivity. getFragmentManager().beginTransaction()
						.add( R.id.map_layout, firstFragment).commit();

//				mMapView = new MapUtils( ).getMapView( mActivity , MapUtils.LOAD_TYPE_POS_MAP ,
//						bundle );
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//						LayoutParams.FILL_PARENT , LayoutParams.FILL_PARENT );
//				mMapLayout.addView( mMapView , lp );
			}
			mCurrentPage = 0;

			switch ( switchAdress )
			{
				case 0 :
					mCurrentPage++;
					requestFromeDianping( mCurrentPage );
					break;
				case 1 :
					doSearchIaround( mCurrentPage );
					break;
				default :
					requestPageData( 1 + mCurrentPage );
					break;
			}



		}
	}

	/**
	 * @Title: requestPageData
	 * @Description: 加载列表数据
	 * @param pageIndex
	 */
	private void requestPageData( int pageIndex )
	{
		if ( mCurrentPage == 0 )
		{
			mEmptyLayout.showLoading( );
		}
		int language = mCurMapType == MapUtils.MapType.Native_MAP ? 1 : 2;
		int type = 0;
		// TODO 根据用户所选类型设置type
		for ( int i = 0 ; i < mCategorys.length ; i++ )
		{
			if ( mCategorys[ i ].isChecked( ) )
			{
				type = ( Integer ) mCategorys[ i ].getTag( );
				break;
			}
		}
		if ( mBuildingFlag == 1 )
		{
			FLAG_GET_BUILDING_INFO = GroupHttpProtocol.getGroupBuildingInfo( mActivity ,
					language , type + "" , mCurGeoData.getLat( ) , mCurGeoData.getLng( ) ,
					pageIndex , PAGE_SIZE , mActivity );
			if ( FLAG_GET_BUILDING_INFO < 0 )
			{
				onGeneralError( 104 , FLAG_GET_BUILDING_INFO );
			}
		}
		else
		{
			// 从Google取
			String nextPageToken = "";
			if ( !TextUtils.isEmpty( mNextPageToken ) )
			{
				nextPageToken = "&pagetoken=" + mNextPageToken;
			}
			FLAG_GET_BUILDING_INFO = GroupHttpProtocol.getGroupBuildingInfoFromGoogle(
					mActivity , mBuidingInfoBean.url + nextPageToken , this );
			if ( FLAG_GET_BUILDING_INFO < 0 )
			{
				onGeneralError( 104 , FLAG_GET_BUILDING_INFO );
				mParentCallback.showWaitDialog( false );
			}

		}
	}

	@Override
	public void onGeneralSuccess( String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		if ( flag == FLAG_GET_BUILDING_INFO )
		{
			// 获取地标返回
			mParentCallback.showWaitDialog( false );
			mListView.onRefreshComplete( );
			if ( mBuildingFlag == 1 )
			{
				mBuidingInfoBean = GsonUtil.getInstance( ).getServerBean(
						result , BuildingInfoBean.class );
				if ( mBuidingInfoBean != null )
				{
					if ( mBuidingInfoBean.isSuccess( ) )
					{
						mBuildingFlag = mBuidingInfoBean.flag;
						if ( mBuildingFlag == 1 )
						{
							// 使用服务端下发的bean
							if ( mCurrentPage == 0 )
							{
								mDataList.clear( );
							}
							mCurrentPage = mBuidingInfoBean.pageno;
							mTotalPage = mBuidingInfoBean.amount / PAGE_SIZE;
							if ( mBuidingInfoBean.amount % PAGE_SIZE > 0 )
							{
								mTotalPage++;
							}
							if ( mBuidingInfoBean.landmarks != null )
							{
								mDataList.addAll( mBuidingInfoBean.landmarks );

							}
							if ( mDataList.isEmpty( ) )
							{
								mEmptyLayout.showEmpty( );
							}
							mAdapter.notifyDataSetChanged( );
						}
						else
						{
							// 使用url下发，则需要再重新去google取一次
							mParentCallback.showWaitDialog( true );
							FLAG_GET_BUILDING_INFO = GroupHttpProtocol
									.getGroupBuildingInfoFromGoogle( mActivity ,
											mBuidingInfoBean.url , this );
							if ( FLAG_GET_BUILDING_INFO < 0 )
							{
								onGeneralError( 104 , FLAG_GET_BUILDING_INFO );
								mParentCallback.showWaitDialog( false );
							}
						}
					}
					else
					{
						onGeneralError( mBuidingInfoBean.error , flag );
					}
				}
				else
				{
					onGeneralError( 104 , flag );
				}
			}
			else
			{
				mGoogleBuildingInfoBean = GsonUtil.getInstance( )
						.getServerBean( result , GoogleBuildingBean.class );
				if ( mGoogleBuildingInfoBean != null )
				{
					if ( mGoogleBuildingInfoBean.status.equals( "OK" ) )
					{
						// 获取地标信息成功
						if ( mCurrentPage == 0 )
						{
							mDataList.clear( );
						}
						mCurrentPage++;
						if ( TextUtils.isEmpty( mGoogleBuildingInfoBean.next_page_token ) )
						{
							// 没有下一页了
							mTotalPage = mCurrentPage;
							hasNextPage = false;
						}
						else
						{
							mNextPageToken = mGoogleBuildingInfoBean.next_page_token;
						}
						for ( GoogleBuildingInfo info : mGoogleBuildingInfoBean.results )
						{
							BuildingInfo buildingInfo = new BuildingInfo( );
							buildingInfo.id = info.id;
							buildingInfo.lat = ( int ) ( info.geometry.location.lat * 10E6 );
							buildingInfo.lng = ( int ) ( info.geometry.location.lng * 10E6 );
							buildingInfo.name = info.name;
							mDataList.add( buildingInfo );
						}
						mAdapter.notifyDataSetChanged( );
						if ( mDataList.isEmpty( ) )
						{
							mEmptyLayout.showEmpty( );
						}
					}
					else
					{
						onGeneralError( 104 , flag );
					}
				}
				else
				{
					onGeneralError( 104 , flag );
				}
			}
		}
	}

	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == FLAG_GET_BUILDING_INFO )
		{
			// 获取地标返回
			mParentCallback.showWaitDialog( false );
			mListView.onRefreshComplete( );
			ErrorCode.toastError( mActivity , e );
			if ( mCurrentPage == 0 )
			{
				mEmptyLayout.showError( );
			}
		}
	}

	/**
	 * @ClassName: DataAdapter
	 * @Description: 数据适配器
	 * @author zhonglong kylin17@foxmail.com
	 * @date 2013-12-31 下午2:43:43
	 *
	 */
	class DataAdapter extends BaseAdapter
	{

		@Override
		public int getCount( )
		{
			// TODO Auto-generated method stub
			return mDataList.size( );
		}

		@Override
		public Object getItem( int arg0 )
		{
			return mDataList.get( arg0 );
		}

		@Override
		public long getItemId( int position )
		{
			return position;
		}

		@Override
		public View getView( int position , View convertView , ViewGroup parent )
		{
			ViewHolder holder = null;
			if ( convertView == null )
			{
				holder = new ViewHolder( );
				convertView = LayoutInflater.from( mActivity ).inflate(
						R.layout.building_list_item , null );
				holder.itemLayout = ( RelativeLayout ) convertView
						.findViewById( R.id.item_layout );
				holder.buildingName = ( TextView ) convertView
						.findViewById( R.id.building_name );
				holder.view = convertView.findViewById(R.id.create_group_divider);
				convertView.setTag( holder );
			}
			else
			{
				holder = ( ViewHolder ) convertView.getTag( );
			}
			BuildingInfo info = mDataList.get( position );
			if ( info != null )
			{
				holder.buildingName.setText( info.name );
//				if (position == mDataList.size() - 1)
//				{
//					holder.view.setVisibility(GONE);
//				}
//				if ( position == 0 )
//				{
//					holder.itemLayout.setBackgroundResource( R.drawable.info_bg_top_selector );
//				}
//				else if ( position == mDataList.size( ) - 1 )
//				{
//					holder.itemLayout
//							.setBackgroundResource( R.drawable.info_bg_bottom_selector );
//				}
//				else
//				{
//					holder.itemLayout
//							.setBackgroundResource( R.drawable.info_bg_center_selector );
//				}
//				holder.itemLayout.setPadding( 13 , 16 , 13 , 16 );
			}
			return convertView;
		}
	}

	static class ViewHolder
	{
		RelativeLayout itemLayout;
		TextView buildingName;
		View view;
	}

	/*****************************************
	 *
	 * INextCheck接口实现
	 *
	 *****************************************/

	@Override
	public void initData(BaseServerBean mServerBean , boolean isBack )
	{
		CommonFunction.log( "create_group" , "CreateGroupSelectCenter initData" );
		/*
		 * if(mCurTypeId.equals("") ||
		 * !mParentCallback.getGroupInfo().groupTypeId.equals(mCurTypeId)){
		 * //需要重新加载数据 isInitData = false; }
		 */
		if ( !isInitData )
		{
			// TODO 加载数据
			isInitData = true;
			// mCurTypeId = mParentCallback.getGroupInfo().groupTypeId;
			initMap( );
		}
	}

	@Override
	public GroupNextStep getGroupNextStep( )
	{
		GroupNextStep step = new GroupNextStep( );
		if ( mSelBuilding == null )
		{
			step.nextMsg = "请选择圈子中心位置";
		}
		else
		{
			step.nextMsg = "";
			step.nextParams = new String[ 7 ];
			step.nextParams[ 0 ] = mSelBuilding.id + "";
			step.nextParams[ 1 ] = mSelBuilding.name;
			step.nextParams[ 2 ] = mSelBuilding.lat + "";
			step.nextParams[ 3 ] = mSelBuilding.lng + "";
			step.nextParams[ 4 ] = mCurGeoData.getLat( ) + "";
			step.nextParams[ 5 ] = mCurGeoData.getLng( ) + "";
			step.nextParams[ 6 ] = mCurGeoData.getAddress( );
		}
		return step;
	}

	/*
	 * 从大众点评获取地理位置信息 String category 分类名称 int pageno 分页
	 */

	private void requestFromeDianping( final int pageno )
	{
		int type = 0;
		int Category = 0;
		// TODO 根据用户所选类型设置type
		for ( int i = 0 ; i < mCategorys.length ; i++ )
		{
			if ( mCategorys[ i ].isChecked( ) )
			{
				type = ( Integer ) mCategorys[ i ].getTag( );
				Category = i;
				break;
			}
		}
		final String category = mStrCategorys[ Category ];
		new Thread( )
		{
			public void run( )
			{
				try
				{
					String apiUrl = "http://api.dianping.com/v1/business/find_businesses";
					String appKey = "88715318"; // 请替换为自己的 App Key 和 App secret
					String secret = "67215dadd62d4f1fb3cc29cbff006de3";
					Map< String , String > paramMap = new HashMap< String , String >( );

					mCurGeoData = LocationUtil.getCurrentGeo( mActivity );
					Double latitude = mCurGeoData.getLat( ) * 1.0 / 1E6;
					Double longitude = mCurGeoData.getLng( ) * 1.0 / 1E6;

					// paramMap.put( "category" , "生活服务" );
					paramMap.put( "latitude" , latitude + "" );
					paramMap.put( "longitude" , longitude + "" );
					paramMap.put( "category" , category );

					paramMap.put( "limit" , "20" );
					paramMap.put( "page" , pageno + "" );
					paramMap.put( "offset_type" , "1" );
					paramMap.put( "platform" , "2" );
					paramMap.put( "out_offset_type" , "1" );
					paramMap.put( "sort" , "1" );
					paramMap.put( "format" , "json" );

					StringBuilder stringBuilder = new StringBuilder( );

					// 对参数名进行字典排序
					String[ ] keyArray = paramMap.keySet( ).toArray( new String[ 0 ] );
					Arrays.sort( keyArray );
					// 拼接有序的参数名-值串
					stringBuilder.append( appKey );
					for ( String key : keyArray )
					{
						stringBuilder.append( key ).append( paramMap.get( key ) );
					}
					String codes = stringBuilder.append( secret ).toString( );
					// SHA-1编码，
					// 这里使用的是Apache-codec，即可获得签名(shaHex()会首先将中文转换为UTF8编码然后进行sha1计算，使用其他的工具包请注意UTF8编码转换)
					String sign = org.apache.commons.codec.digest.DigestUtils.shaHex( codes )
							.toUpperCase( );

					// 添加签名
					stringBuilder = new StringBuilder( );
					stringBuilder.append( "appkey=" ).append( appKey ).append( "&sign=" )
							.append( sign );
					for ( Entry< String , String > entry : paramMap.entrySet( ) )
					{
						stringBuilder.append( '&' ).append( entry.getKey( ) ).append( '=' )
								.append( entry.getValue( ) );
					}
					String queryString = stringBuilder.toString( );

					StringBuffer response = new StringBuffer( );
					HttpClientParams httpConnectionParams = new HttpClientParams( );
					httpConnectionParams.setConnectionManagerTimeout( 1000 );
					HttpClient client = new HttpClient( httpConnectionParams );
					HttpMethod method = new GetMethod( apiUrl );

					BufferedReader reader = null;
					String encodeQuery = URIUtil.encodeQuery( queryString , "UTF-8" ); // UTF-8
					// 请求
					method.setQueryString( encodeQuery );
					client.executeMethod( method );
					reader = new BufferedReader( new InputStreamReader(
							method.getResponseBodyAsStream( ) , "UTF-8" ) );
					String line = null;
					while ( ( line = reader.readLine( ) ) != null )
					{
						response.append( line )
								.append( System.getProperty( "line.separator" ) );
					}
					reader.close( );
					method.releaseConnection( );

					// CommonFunction.log( "shifengxiong" , "result==" +
					// response.toString( ) );

					Message msg = new Message( );
					msg.what = 0;
					msg.obj = response.toString( );
					mHandler.sendMessage( msg );
				}
				catch ( Exception e )
				{
					// TODO: handle exception
				}
			}
		}.start( );
	}



	@Override
	public void onPoiItemDetailSearched( PoiItemDetail arg0 , int arg1 )
	{
		// TODO Auto-generated method stub
		CommonFunction.log( "shifengxiong" , "PoiItemDetail resutl =" + arg0 );

	}

	@Override
	public void onPoiSearched( PoiResult result , int arg1 )
	{
		// TODO Auto-generated method stub
		// CommonFunction.log( "shifengxiong" , "onPoiSearched resutl =" +
		// arg0.toString( ));

		// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
		if ( result != null )
		{
			poiItems = result.getPois( );

		}
		mHandler.sendEmptyMessage( 2 );
	}

	protected void doSearchIaround( int page )
	{

		int type = 0;
		int Category = 0;
		// TODO 根据用户所选类型设置type
		for ( int i = 0 ; i < mCategorys.length ; i++ )
		{
			if ( mCategorys[ i ].isChecked( ) )
			{
				type = ( Integer ) mCategorys[ i ].getTag( );
				Category = i;
				break;
			}
		}
		final String category = mStrCategorys[ Category ];
		Query query = new PoiSearch.Query( "" , category , "" );//
		// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）

		query.setPageSize( 20 );
		query.setPageNum( page );

		mCurGeoData = LocationUtil.getCurrentGeo( mActivity );
		Double latitude = mCurGeoData.getLat( ) * 1.0 / 1E6;
		Double longitude = mCurGeoData.getLng( ) * 1.0 / 1E6;
		SearchBound bound = new PoiSearch.SearchBound(
				new LatLonPoint( latitude , longitude ) , 2000 );
		//
		//
		PoiSearch poiSearch = new PoiSearch( mActivity , query );
		//
		poiSearch.setBound( bound );
		//
		poiSearch.setOnPoiSearchListener( this );
		poiSearch.searchPOIAsyn( );


	}



	private Handler mHandler = new Handler( )
{
	public void handleMessage( android.os.Message msg )
	{
		mParentCallback.showWaitDialog( false );
		mListView.onRefreshComplete( );

		switch ( msg.what )
		{
			case 0 :

				CommonFunction.log( "shifengxiong" , "result==" + msg.obj );



				JSONObject jsonObject;
				try
				{
					jsonObject = new JSONObject( String.valueOf( msg.obj ) );
					JSONArray busineesses = ( JSONArray ) jsonObject.get( "businesses" );
					int total = jsonObject.optInt( "total_count" );
					int count = jsonObject.optInt( "count" );

					mTotalPage += count;

					if ( mTotalPage >= total )
					{
						hasNextPage = false;
					}

					if ( mCurrentPage == 1 )
					{
						if ( mDataList.size( ) > 0 )
						{
							mDataList.clear( );
						}

					}


					for ( int i = 0 , iMax = busineesses.length( ) ; i < iMax ; i++ )
					{
						BuildingInfo addInfo = new BuildingInfo( );

						addInfo.id = MD5Utility.md5Appkey( busineesses.getJSONObject( i )
								.optString( "address" ) );
						addInfo.name = busineesses.getJSONObject( i ).optString( "name" );
						addInfo.lat = ( int ) ( busineesses.getJSONObject( i ).optDouble(
								"latitude" ) * 1E6 );
						addInfo.lng = ( int ) ( busineesses.getJSONObject( i ).optDouble(
								"longitude" ) * 1E6 );

						mDataList.add( addInfo );

					}
					if ( mAdapter == null )
					{
						mAdapter = new DataAdapter( );
						mListView.setAdapter( mAdapter );
					}
					else
					{
						mAdapter.notifyDataSetChanged( );
					}

					// setProgressBarVisible( false );
				}
				catch ( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace( );

				}

				break;

			case 1 :


				CommonFunction.toastMsg( getContext( ) , R.string.no_more_data );
				break;
			case 2 :

				if ( mCurrentPage == 0 )
				{
					if ( mDataList.size( ) > 0 )
					{
						mDataList.clear( );
					}

				}
				if ( poiItems != null && poiItems.size( ) > 0 )
				{

					for ( int i = 0 , iMax = poiItems.size( ) ; i < iMax ; i++ )
					{
						BuildingInfo addInfo = new BuildingInfo( );

						addInfo.id = MD5Utility
								.md5Appkey( poiItems.get( i ).getSnippet( ) );
						addInfo.name = poiItems.get( i ).toString( );
						addInfo.lat = ( int ) ( poiItems.get( i ).getLatLonPoint( )
								.getLatitude( ) * 1E6 );
						addInfo.lng = ( int ) ( poiItems.get( i ).getLatLonPoint( )
								.getLongitude( ) * 1E6 );

						mDataList.add( addInfo );

					}
					if ( mAdapter == null )
					{
						mAdapter = new DataAdapter( );
						mListView.setAdapter( mAdapter );
					}
					else
					{
						mAdapter.notifyDataSetChanged( );
					}

				}
				break;
		}
	}
};



}
