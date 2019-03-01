
package net.iaround.ui.store;



import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshExpandableListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.im.Me;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.im.BaseServerBean;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.store.LightTimer;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.datamodel.Gift;
import net.iaround.ui.datamodel.PayModel;
import net.iaround.ui.datamodel.ResourceListBean;
import net.iaround.ui.datamodel.ResourceListBean.ResourceItemBean;
import net.iaround.ui.datamodel.UserBufferHelper;
import net.iaround.utils.ImageViewUtil;

import net.iaround.ui.store.StoreDataListBean.Sections;
import net.iaround.ui.store.StoreDataListBean.Categorys;
import net.iaround.ui.store.StoreDataListBean.GiftsBags;

import net.iaround.ui.view.face.MyGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * 商店首页
 * 
 * @author zhengst
 * 
 */
public class StoreMainActivity extends BaseActivity implements OnClickListener,HttpCallBack
{
	// FLAG
	private long BUY_GIFT_FLAG;// 购买礼物的请求
	private long GET_BANNER_FLAG;// 获取Banner的请求
	private long GET_STORE_FLAG;// 获取商店数据的请求
	private long GET_STORE_ALL;// 获取商店所有礼物
	private static final int TYPE_GOLD_BUY = 1;// 金币支付
	private static final int TYPE_DIAMON_BUY = 2;// 钻石支付
	private static final int TYPE_DIAMON_EXCHANGE_BUY = 3;// 钻石直购
	private static final int HANDLE_GET_STOREDATA = 1001;// 获取数据成功
	private static final int HANDLE_GET_STORE_GIFT_LIST = 1004;// 获取数据礼物列表

	private static final int HANDLE_GET_STOREDATA_ERROR = 1002;// 获取数据失败
	private static final int HANDLE_PAY_SUCCESS= 1003;// 购买礼物成功

	private static final int HANDLE_CACHEDATA  = 1005;//显示 缓存的数据
	
	// VIEW
	private FrameLayout mTitleBack;
	private MyGridView MyGridView;
	private View giftClassifyView;
	private View gridview;
	private Dialog mProgressDialog;
	private EmptyLayout mEmptyLayout;
	private StoreAdViewPager bannerView;
	private ExpandableListAdapter mAdapter;
	private StoreGiftBagAdapter giftPacksAdapter;
	private PullToRefreshExpandableListView mExpandableListView;
//	private View personalView , topGoldView , mGoldView , divider;
//	private TextView tvGold , tvDiamond , topTvGold , topTvDiamond;
//	private RelativeLayout diamondly , goldly , topGoldly , topDiamondly;
	private TextView mTitleName , mTitleRight , giftBagsHeadText , NoDataHeadView;
	
	private String Uid = "";
	private Me user = new Me( );
	private int lastScrollY = 0;
	private LightTimer mAdBannerTimer;
	private StoreDataListBean StoreBean;
	private Gifts mCurrentGift;// 当前礼物
	private Boolean isPullToRefresh = false ;
	private Boolean isHasCachaData = false;// 是否有缓存数据
	private Boolean isShowHintHeadView = false; // 是否已展示网络异常时顶部的提示语
	private int exchangeGoldPrice = 0;// 前往钱包页面兑换的礼物的金币价格
	private ArrayList<StoreDataListBean.Sections> sectionsStore = new ArrayList< Sections >( );
	private ArrayList< GiftsBags > giftbags = new ArrayList< GiftsBags >( );
	private ArrayList< ResourceItemBean > bannerList = new ArrayList<ResourceItemBean>( );
	private GiftsList giftsList;
	private ArrayList< Gifts > gifts;

	private static String _goldNum , _diamondNum;

	private Activity mActivity;


	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.store_main );
		mActivity = this;
		_goldNum = getResources().getString( R.string.gold_balance );
		_diamondNum = getResources().getString( R.string.diamond_balance ) ;
		
		user = Common.getInstance( ).loginUser;
		Uid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		
		initView( );
		initCacheData( );
		requestData( );

		mTheMainHandler.postDelayed( new Runnable( )
		{
			@Override
			public void run( )
			{
				mTheMainHandler.sendEmptyMessage(HANDLE_CACHEDATA  );

			}
		},100 );

		//获取所有礼物
//		long flag = UserHttpProtocol.giftList( context, categoryid, pageno, pagesize, callback );
	}
	
	/**
	 * 初始化页面
	 */
	private void initView( )
	{
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleBack = (FrameLayout) findViewById( R.id.fl_left );
		mTitleRight = (TextView) findViewById( R.id.tv_right );
		mTitleName.setText( R.string.store );
		mTitleRight.setText( R.string.private_gift );
		findViewById(R.id.fl_right).setOnClickListener( this );
		mTitleBack.setOnClickListener( this );
		mTitleRight.setVisibility(View.VISIBLE);
		
		// 没有数据时显示的提示title
		NoDataHeadView = (TextView) findViewById( R.id.head_text );
		NoDataHeadView.getBackground( ).setAlpha( 242 );
		NoDataHeadView.setVisibility( View.GONE );
		
		mExpandableListView = (PullToRefreshExpandableListView) findViewById( R.id.store_listview );
		mExpandableListView.getRefreshableView( ).setGroupIndicator( null );
		mExpandableListView.setMode( Mode.PULL_FROM_START );
		
		mEmptyLayout = new EmptyLayout( mContext , mExpandableListView.getRefreshableView( ) );
		mEmptyLayout.setEmptyMessage( getString( R.string.load_data_fail ) );
		
		mExpandableListView.setOnRefreshListener( new OnRefreshListener< ExpandableListView >( )
		{
			@Override
			public void onRefresh( PullToRefreshBase< ExpandableListView > refreshView )
			{
				if ( isHasCachaData == false && StoreBean == null )
				{
//					mEmptyLayout.showLoading( );
					showProgressDialog( true );
				}
				requestData( );
			}
		} );
	}
	
	
	// 设置监听
	private void setListener( )
	{
//		goldly.setOnClickListener( this );
//		diamondly.setOnClickListener( this );
//		topGoldly.setOnClickListener( this );
//		topDiamondly.setOnClickListener( this );
//		personalView.findViewById( R.id.face_center ).setOnClickListener( this );
//		personalView.findViewById( R.id.vip_ly ).setOnClickListener( this );
		
		NoDataHeadView.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				mExpandableListView.setRefreshing( );
			}
		} );
		
		mExpandableListView.getRefreshableView( ).setOnScrollListener( new OnScrollListener( )
		{
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState )
			{
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                                 int totalItemCount )
			{
				showTopView( firstVisibleItem, visibleItemCount );
			}
		} );
	}

	/**
	 * 加载缓存数据
	 */
	private void initCacheData( )
	{
		String data = SharedPreferenceCache.getInstance( mContext ).getString( "store_data" + Uid );
//		String banner = SharedPreferenceCache.getInstance( mContext ).getString(
//				"store_banner" + Uid );

		if ( data != null && !data.isEmpty( )  )
		{// 获取缓存展示
			isHasCachaData = true;
			StoreDataListBean bean = new StoreDataListBean( );
			bean = GsonUtil.getInstance( ).getServerBean( data , StoreDataListBean.class );
			
//			if ( banner != null )
//			{
//				ResourceListBean bannerBean = new ResourceListBean( );
//				bannerBean = GsonUtil.getInstance( ).getServerBean( banner , ResourceListBean.class );
//				bannerList = bannerBean.resources;
//			}
			
			StoreBean = bean;
			giftbags = StoreBean.giftbags;
			sectionsStore.clear( );
			sectionsStore.addAll( setGiftFlagResourceId( StoreBean.sections ) );

			
		}else{
			isHasCachaData = false;
			showProgressDialog( true );
		}


		
	}
	
	
	/**
	 * 请求数据
	 */
	private void requestData( )
	{
		requestSectionsData( );
//		requsetBanner( );

		requseAllgift( );
	}
	
	/**
	 * 请求分类栏目及礼物数据
	 */
	private void requestSectionsData( )
	{
		GET_STORE_FLAG = BusinessHttpProtocol.getStoreData( mContext , this );
	}
	
	/**
	 * 请求广告banner
	 */
	private void requsetBanner( )
	{
		GET_BANNER_FLAG = BusinessHttpProtocol.getResourceList( mContext, 1, 3, this );
	}

	private void requseAllgift()
	{
		GET_STORE_ALL = UserHttpProtocol.giftList( mContext, 1, 1, 50, this );
	}
	
	
	/**
	 * 刷新页面
	 */
	private void refreshView()
	{
		if ( mAdapter == null ){
			 loadView( );
			 mAdapter = new ExpandableListAdapter( );
			 mExpandableListView.getRefreshableView( ).setAdapter( mAdapter );
		}else{
			 updateView( );
		}


	}
	
	
	/**
	 * 加载view
	 */
	private void loadView( )
	{
		if ( bannerList != null && bannerList.size( ) > 0 ){// banner栏
			initBannerView( );
		}
		
		initPersonalView( );// 个人信息栏
		initClassifyView( false );// 礼物分类栏
		
//		if ( StoreBean.giftbags != null && StoreBean.giftbags.size( ) > 0 ){// 大礼包
////			initGiftBagsView( );
//
//			Sections gifBag = StoreBean.newSections();
//
//			gifBag.title = getResString( R.string.store_giftbags );
//			gifBag.giftbags = StoreBean.giftbags;
//
//			sectionsStore.add( 1, gifBag );
//		}
//
		setListener( );
	}
	
	/**
	 * 更新view
	 */
	private void updateView( )
	{
		if ( bannerView != null && bannerView.isShown( ) )
		{
			if ( bannerList != null && bannerList.size( ) > 0 ){
				initBanner( );
			}else{
				mExpandableListView.getRefreshableView( ).removeHeaderView( bannerView );
			}
		}
		
		setGoldNum( );
		initClassifyView( true );
		
		if ( giftPacksAdapter == null )
		{
			if ( StoreBean.giftbags != null && StoreBean.giftbags.size( ) > 0 ){
				initGiftBagsView( );
			}
		}
		else
		{
			if ( StoreBean.giftbags != null && StoreBean.giftbags.size( ) > 0 ){
				giftBagsHeadText.setVisibility( View.VISIBLE );
			}else{
				giftBagsHeadText.setVisibility( View.GONE );
			}
			giftPacksAdapter.notifyDataSetChanged( );
		}
		
		isPullToRefresh = true ;
		mAdapter.notifyDataSetChanged( );
	}
	
	
	/**
	 * 初始化个人信息栏
	 */
	private void initPersonalView( )
	{
//		personalView = View.inflate( mContext , R.layout.store_personal_info , null );
//		mExpandableListView.getRefreshableView( ).addHeaderView( personalView );
//		HeadPhotoView UserIcon = ( HeadPhotoView ) personalView.findViewById( R.id.icon );
//		TextView UserName = (TextView) personalView.findViewById( R.id.name );
//		UserName.setText( Common.getInstance( ).loginUser.getNickname( ) );
//
//		UserIcon.setOnHeadPhotoViewClick( null );
//		UserIcon.execute( ChatFromType.UNKONW,user,null );
//
//		//vip
//		if (user.isSVip()) {// 包月svip
//			UserName.setTextColor(Color.parseColor("#ee4552"));
//		} else {// 仅为终身vip
//			UserName.setTextColor(Color.BLACK);
//		}
//
//		// 年龄
//		TextView tvAge = (TextView) personalView.findViewById( R.id.tvAge );
//		if ( Common.getInstance( ).loginUser.getAge( ) <= 0 ){
//			tvAge.setText( R.string.unknown );
//		}else{
//			tvAge.setText( String.valueOf( Common.getInstance( ).loginUser.getAge( ) ) );
//		}
//
//		// 性别
//		int sex = "m".equals( Common.getInstance( ).loginUser.getGender( ) ) ? 1 : 2;
//		if ( sex <= 0 ){
//			tvAge.setBackgroundResource( R.drawable.z_common_female_bg );
//			tvAge.setCompoundDrawablesWithIntrinsicBounds( R.drawable.z_common_female_icon , 0 , 0 ,
//					0 );
//		}else{
//			int sexIcon = sex == 1 ? R.drawable.z_common_male_icon
//					: R.drawable.z_common_female_icon;
//			tvAge.setCompoundDrawablesWithIntrinsicBounds( sexIcon , 0 , 0 , 0 );
//			tvAge.setBackgroundResource( sex == 1 ? R.drawable.z_common_male_bg
//					: R.drawable.z_common_female_bg );
//		}
		
		// 金币栏
//		mGoldView = View.inflate( mContext , R.layout.store_head_gold , null );
//		mExpandableListView.getRefreshableView( ).addHeaderView( mGoldView );
//		tvGold = (TextView) mGoldView.findViewById( R.id.tvGold );
//		tvDiamond = (TextView) mGoldView.findViewById( R.id.tvDiamond );
//		diamondly = (RelativeLayout) mGoldView.findViewById( R.id.diamond_ly );
//		goldly = (RelativeLayout) mGoldView.findViewById( R.id.gold_ly );
//
//		// 金币固定栏
//		topGoldView = findViewById( R.id.store_top_gold );
//		divider = (View) topGoldView.findViewById( R.id.divider );
//		topTvGold = (TextView) topGoldView.findViewById( R.id.tvGold );
//		topTvDiamond = (TextView) topGoldView.findViewById( R.id.tvDiamond );
//		topDiamondly = (RelativeLayout) topGoldView.findViewById( R.id.diamond_ly );
//		topGoldly = (RelativeLayout) topGoldView.findViewById( R.id.gold_ly );
		
//		setGoldNum( );
	}
	
	/**
	 * 初始化轮播Banner
	 */
	private void initBannerView( )
	{
		bannerView = new StoreAdViewPager( this );
		int layoutWidth = getResources( ).getDisplayMetrics( ).widthPixels;
		int layoutHeight = CommonFunction.dipToPx( mContext , 96 );
		bannerView.setLayoutParams( new ListView.LayoutParams( layoutWidth, layoutHeight ) );
		mExpandableListView.getRefreshableView( ).addHeaderView( bannerView );
		initBanner( );
	}
	
	/**
	 * 初始化轮播Banner数据
	 * 
	 */
	private void initBanner( )
	{
		if ( bannerList == null || bannerList.size( ) == 0 ){
			return;
		}
		
		for ( ResourceItemBean banner : bannerList )
		{
			if ( banner.banner == null || banner.banner.equals( "null" ) ){
				return;
			}
		}
		bannerView.setData( bannerList );
		bannerView.setOnItemClickListener( avBannerOnClickListener );
		
		if ( mAdBannerTimer != null )
		{
			mAdBannerTimer.stop( );
			mAdBannerTimer = null;
		}
		
		mAdBannerTimer = new LightTimer( )
		{
			@Override
			public void run( LightTimer timer ){
				bannerView.showNext( );
			}
		};
		mAdBannerTimer.startTimerDelay( 5000 , 5000 );
	}
	
	/**
	 * 初始化 大礼包
	 */
	private void initGiftBagsView( )
	{
		gridview = View.inflate( mContext , R.layout.x_storemain_giftbag_gridview , null );
		giftBagsHeadText = (TextView) gridview.findViewById( R.id.header_text );
		giftBagsHeadText.setText( R.string.store_giftbags );
		MyGridView = ( MyGridView ) gridview.findViewById( R.id.store_gridview );
		giftPacksAdapter = new StoreGiftBagAdapter(mContext,giftbags );
		MyGridView.setAdapter( giftPacksAdapter );
//		mExpandableListView.getRefreshableView( ).addHeaderView( gridview );
	}
	
	/**
	 * 初始化礼物分类栏
	 */
	private void initClassifyView( Boolean isAlreadyInit )
	{
		int[ ] textArray ={ R.id.text1 , R.id.text2 , R.id.text3 , R.id.text4 ,
				            R.id.text5 , R.id.text6 , R.id.text7 , R.id.text8 };
		int[ ] iconArray ={ R.id.icon1 , R.id.icon2 , R.id.icon3 , R.id.icon4 ,
				            R.id.icon5 , R.id.icon6 , R.id.icon7 , R.id.icon8 };
		
		if ( isAlreadyInit == false ){
			giftClassifyView = View.inflate( mContext , R.layout.store_gift_classify_gridview ,
					null );
//			mExpandableListView.getRefreshableView( ).addHeaderView( giftClassifyView );
		}
		
		for ( int i = 0 ; i < textArray.length ; i++ )
		{
			TextView text = (TextView) giftClassifyView.findViewById( textArray[ i ] );
			ImageView icon = (ImageView) giftClassifyView.findViewById( iconArray[ i ] );
			Categorys data = StoreBean.categorys.get( i );
			if ( data != null )
			{
				text.setText( CommonFunction.getLangText( mContext , data.name ) );
				ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView( CommonFunction.thumPicture(data.categoryicon) ,
						icon , R.drawable.z_find_storemain_default_icon ,
						R.drawable.z_find_storemain_default_icon , null , 0 , "#00000000" );
				
				icon.setTag( data );
				icon.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
//						addBtnEvent( DataTag.BTN_find_store_typeAll);
						Categorys data = ( Categorys ) v.getTag( );
						StoreGiftClassifyActivity.launcherGiftClassifyFromStore( mContext ,
								data.categoryid , CommonFunction.getLangText( mContext , data.name ) );
					}
				} );
			}}
	}
	
	
	class ExpandableListAdapter extends BaseExpandableListAdapter
	{
		@Override
		public int getGroupCount( )
		{
			CommonFunction.log( "shifengxiong",
				"getGroupCount sssssssssssss >>>>" + sectionsStore.size( ) );
			if ( sectionsStore != null )
			{
				return sectionsStore.size( ) > 0  ? sectionsStore.size( ) : 1 ;
			}
			return 1 ;
		}
		
		@Override
		public int getChildrenCount( int groupPosition )
		{
			CommonFunction.log( "shifengxiong","getChildrenCount sssssssssssss >>>>"+groupPosition );
			return 1;
		}
		
		@Override
		public Object getGroup(int groupPosition )
		{
			if ( sectionsStore != null && sectionsStore.size( ) > 0 )
			{
				CommonFunction.log( "shifengxiong","getGroup sssssssssssss >>>>"+groupPosition );
			   return sectionsStore.get( groupPosition );
			}
			return null;
		}
		
		@Override
		public Object getChild(int groupPosition , int childPosition )
		{
			if ( sectionsStore != null && sectionsStore.size( ) > 0 )
			{
				CommonFunction.log( "shifengxiong","getChild groupPosition >>>>"+groupPosition +";"+childPosition );
			  return sectionsStore.get( groupPosition ).gifts.get( childPosition );
			}
			return null;
		}
		
		@Override
		public long getGroupId( int groupPosition )
		{
			return groupPosition;
		}
		
		@Override
		public long getChildId( int groupPosition , int childPosition )
		{
			return childPosition;
		}
		
		@Override
		public boolean hasStableIds( )
		{
			return false;
		}
		
		
		@Override
		public View getGroupView(int groupPosition , boolean isExpanded , View convertView ,
                                 ViewGroup parent )
		{
			GroupHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new GroupHolder( );
				convertView = View.inflate( mContext , R.layout.x_store_expandable_head , null );
				viewHolder.headerText = (TextView) convertView.findViewById( R.id.header_text );
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( GroupHolder ) convertView.getTag( );
			}
			if ( sectionsStore != null && sectionsStore.size( ) > 0)
			{
				viewHolder.headerText.setVisibility( View.VISIBLE );
				String str = CommonFunction.getLangText(mContext,  sectionsStore.get( groupPosition ).title );
				viewHolder.headerText.setText(str);
				convertView.setClickable( false );
				mExpandableListView.getRefreshableView( ).expandGroup( groupPosition );
			}else {
				viewHolder.headerText.setVisibility( View.GONE );
			}
			// 隐掉分组
			convertView.setVisibility(View.GONE);
			return convertView;
		}
		
		@Override
		public View getChildView(final int groupPosition , final int childPosition ,
                                 boolean isLastChild , View convertView , ViewGroup parent )
		{


			if(sectionsStore.get( groupPosition ).type ==0)
			{
				ItemHolder viewHolder = null;
				if ( convertView == null )
				{
					viewHolder = new ItemHolder( );
					convertView = View.inflate( mContext , R.layout.x_store_expandabale_gridview , null );
					viewHolder.SectionsGridView = ( MyGridView ) convertView
						.findViewById( R.id.store_gridview );
//					convertView.findViewById( R.id.expandable_head ).setVisibility( View.GONE );//gh

					StoreMainGridAdapter SectionsAdapter = new StoreMainGridAdapter(
						sectionsStore.get( groupPosition ) , mContext );
					viewHolder.SectionsGridView.setAdapter( SectionsAdapter );

					convertView.setTag( viewHolder );
				}
				else
				{
					viewHolder = ( ItemHolder ) convertView.getTag( );
				}

				// 礼物点击事件
				viewHolder.SectionsGridView.setOnItemClickListener( new OnItemClickListener( )
				{
					@Override
					public void onItemClick(AdapterView< ? > parent, View view, int position,
                                            long id )
					{
//						addBtnEvent( DataTag.BTN_find_store_indexItem );
						Gifts gifts = ( Gifts ) parent.getAdapter( ).getItem( position );
						mCurrentGift = gifts;
						saleBuyGift( gifts );
					}
				} );

				if ( isPullToRefresh == true  )
				{
					StoreMainGridAdapter SectionsAdapter = ( StoreMainGridAdapter ) viewHolder.SectionsGridView.getAdapter( );
					if ( sectionsStore != null && sectionsStore.size( ) > 0 )
					{
						SectionsAdapter.data = sectionsStore.get( groupPosition );
					}else {
						SectionsAdapter.data = null;
					}
					SectionsAdapter.notifyDataSetChanged( );
					isPullToRefresh = false;
				}

				return convertView;

			}
			else if(sectionsStore.get( groupPosition ).type ==1)
			{

				CommonFunction.log( "shifengxiong","ssssssssssssssssssss" );
				if ( convertView == null )
				{
					gridview = View.inflate( mContext , R.layout.x_storemain_giftbag_gridview , null );
					convertView = gridview;

				}
				else
				{
					gridview = convertView;
				}

				giftBagsHeadText = (TextView) gridview.findViewById( R.id.header_text );
				giftBagsHeadText.setText( R.string.store_giftbags );
				giftBagsHeadText.setVisibility( View.GONE );
				MyGridView = ( MyGridView ) gridview.findViewById( R.id.store_gridview );
				giftPacksAdapter = new StoreGiftBagAdapter(mContext,giftbags );
				MyGridView.setAdapter( giftPacksAdapter );
				MyGridView.deferNotifyDataSetChanged();

			}

			return  convertView;
		}
		
		@Override
		public boolean isChildSelectable( int groupPosition , int childPosition )
		{
			return false;
		}
		
		class GroupHolder
		{
			TextView headerText;
		}
		
		class ItemHolder
		{
			MyGridView SectionsGridView;
		}
	}
	

	/**
	 * 点击礼物的操作 购买礼物
	 * 
	 *            —— 1：金币礼物 2：钻石礼物
	 * @param gift
	 *            —— 是否有优惠价
	 */
	private void saleBuyGift( Gifts gift )
	{
		if ( gift != null )
		{// 购买
			if ( !Common.getInstance( ).loginUser.isSVip()&&
				  !Common.getInstance( ).loginUser.isVip( ) && mCurrentGift.viptype == 1 )
			{
				DialogUtil.showTobeVipDialog( mContext , R.string.vip_gift ,
						R.string.tost_gift_vip_privilege );
				return;
			}
			
			int price = 0;
			if ( mCurrentGift.discountgoldnum != null
					&& !mCurrentGift.discountgoldnum.equals( "null" ) )
			{// 先判断是否有折扣价，若有使用折扣价
				price = Integer.parseInt( mCurrentGift.discountgoldnum );
			}
			else
			{
				price = mCurrentGift.goldnum;
			}
			
			if ( mCurrentGift.currencytype == 1 )
			{// 金币礼物
				buyGoldGiftMode( price );
			}
			else
			{// 钻石礼物
				buyDiamonGiftMode( price );
			}
		}
	}
	
	/**
	 * 钻石礼物购买方式
	 * 
	 * @param price
	 */
	private void buyDiamonGiftMode( int price )
	{
		if ( StoreBean.diamondnum >= price )
		{// 钻石充足，直接购买
			buyGift( price );
		}
		else
		{// 充值
			DialogUtil.showDiamondDialog( mActivity );
		}
	}
	
	
	/**
	 * 金币礼物购买方式
	 * 
	 * @param price
	 */
	private void buyGoldGiftMode( int price )
	{
		if ( StoreBean.goldnum >= price )
		{// 金币充足，直接购买
			buyGift( price );
		}
		else if ( StoreBean.discount != 0 && StoreBean.ratio != 0 )
		{// 金币不足情况下，判断按比例兑换后的钻石数量是否足够以直购消费
			if ( ( StoreBean.diamondnum >= price * ( ( double ) StoreBean.discount / 100 )
					/ StoreBean.ratio ) )
			{
				showDiamonConvertBuyDialog( price , mCurrentGift.currencytype );
			}
			else
			{
				DialogUtil.showDiamondDialog( mActivity );
			}
		}
		else
		{// 提示充值对话框
			DialogUtil.showDiamondDialog( mActivity );
		}
	}
	
	/**
	 * 钻石直购对话框
	 */
	private void showDiamonConvertBuyDialog( final int price , int currencytype )
	{
		DialogUtil.showDiamonConvertBuyDialog( mContext , currencytype , price ,
				StoreBean.discount , StoreBean.ratio , new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						showProgressDialog(false );
						// 确定以钻石直购的方式购买礼物
						BUY_GIFT_FLAG = BusinessHttpProtocol.BuyGift( mContext ,
								mCurrentGift.giftid , TYPE_DIAMON_EXCHANGE_BUY ,
								StoreMainActivity.this );
					}
				} , new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{// 跳转到兑换金币页面兑换金币后自动购买
						exchangeGoldPrice = price;
						FragmentPayBuyGlod.jumpPayBuyGlodActivityFromStore(mContext, 1000);
//						PayMainActivity.lancuPayMainActivity( mContext , 1000 );
					}
				} );
	}
	
	
	/**
	 * 购买礼物
	 */
	private void buyGift( int price )
	{
		try
		{
			showBuyGiftDialog( price );// 购买礼物的对话框
		}
		catch ( Throwable t )
		{
			hideProgressDialog( );
			Toast.makeText( this , R.string.network_req_failed , Toast.LENGTH_SHORT ).show( );
		}
	}
	
	/**
	 * 弹出购买礼物的对话框
	 */
	private void showBuyGiftDialog( int price )
	{
		DialogUtil.showTwoButtonDialog( mActivity,
			getResources( ).getString( R.string.store_get_gift_tip ),
			mCurrentGift.currencytype == 2 ? String
				.format( getResources( ).getString( R.string.you_want_to_use_diamond_get_gift ),
					String.valueOf(price) ) : String
				.format( getResources( ).getString( R.string.you_want_to_use_gold_get_gift ),
					String.valueOf(price) ), getResources( ).getString( R.string.cancel ),
			getResources( ).getString( R.string.ok ), null, new OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					showProgressDialog( false );
					int paytype = 0;
					if ( mCurrentGift.currencytype == 1 )
					{
						paytype = TYPE_GOLD_BUY;
					}
					else if ( mCurrentGift.currencytype == 2 )
					{
						paytype = TYPE_DIAMON_BUY;
					}
					BUY_GIFT_FLAG = BusinessHttpProtocol
						.BuyGift( mContext, mCurrentGift.giftid, paytype, StoreMainActivity.this );
				}
			} );
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		hideProgressDialog( );
		mExpandableListView.onRefreshComplete( );
		if ( flag == GET_STORE_FLAG )
		{
			if ( isHasCachaData == true ){
				showHintHeadView( );
			}else{
				mEmptyLayout.showError( );
			}
		}
		else if ( flag == GET_BANNER_FLAG ){
			CommonFunction.log( "" , "--->banner is null==" + e );
		}else{
			ErrorCode.toastError( mContext , e );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		HashMap< String , Object > res = null;
		try
		{
			res = SpaceModel.getInstance( this ).getRes( result , flag );
		}
		catch ( Throwable e )
		{
			CommonFunction.log( e );
		}
		
		if ( res == null ){
			return;
		}
		
		if ( flag == GET_STORE_FLAG )
		{// 获取商店数据
			parseGetStoreData( result );
		}
		else if ( flag == GET_BANNER_FLAG )
		{// 获取广告数据
			ResourceListBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					ResourceListBean.class );
			if ( bean != null )
			{
				bannerList = bean.resources;
				// 缓存数据,即使是空数据也替换掉原有数据，使广告栏消失
				SharedPreferenceCache.getInstance( mContext ).putString( "store_banner" + Uid ,
						result );
			}
		}
		else if ( flag == BUY_GIFT_FLAG )
		{// 购买礼物操作
			Message msg = Message.obtain( );
			msg.what = HANDLE_PAY_SUCCESS;
			msg.obj = result;
			mTheMainHandler.sendMessage( msg );
		}
		else if(flag == GET_STORE_ALL)
		{
			parseGetStoreDataList(  result );
			SharedPreferenceCache.getInstance( mContext ).putString( "store_all_data" + Uid,
				result );
		}
	}

	/**
	 * 解析获取商店全部礼物数据
	 *
	 * @param result
	 */
	private void parseGetStoreDataList( String result )
	{
		giftsList = GsonUtil.getInstance().getServerBean( result,GiftsList.class );
		if(giftsList.isSuccess())
		{
			gifts = giftsList.gifts;
			mTheMainHandler.sendEmptyMessage( HANDLE_GET_STORE_GIFT_LIST );
		}
	}
	
	
	/**
	 * 解析获取商店数据的返回
	 * 
	 * @param result
	 */
	private void parseGetStoreData( String result )
	{
		BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result , BaseServerBean.class );
		if ( bean != null && bean.isSuccess( ) )
		{
			StoreDataListBean data = GsonUtil.getInstance( ).getServerBean( result ,
					StoreDataListBean.class );
			if ( data != null  )
			{// 获取成功
				Message msg = Message.obtain( );
				msg.what = HANDLE_GET_STOREDATA;
				msg.obj = data;
				mTheMainHandler.sendMessage( msg );
				// 缓存数据
				SharedPreferenceCache.getInstance( mContext ).putString( "store_data" + Uid ,
						result );
			}
			else
			{// 获取失败
				Message msg = Message.obtain( );
				msg.what = HANDLE_GET_STOREDATA_ERROR;
				mTheMainHandler.sendMessage( msg );
			}
		}
		else
		{
			Message msg = Message.obtain( );
			msg.what = HANDLE_GET_STOREDATA_ERROR;
			mTheMainHandler.sendMessage( msg );
		}
	}
	
	private Handler mTheMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			super.handleMessage( msg );
			
			switch ( msg.what )
			{
				case HANDLE_PAY_SUCCESS :// 处理购买礼物
					handleBuyGift( msg.obj );
					break;
				
				case HANDLE_GET_STOREDATA :// 获取商店数据后的操作
					StoreBean = ( StoreDataListBean ) msg.obj;
					handleParseStoreData();
					break;

				case HANDLE_GET_STORE_GIFT_LIST:
					handleParseStoreData( );
					break;
				
				case HANDLE_GET_STOREDATA_ERROR :// 获取商店数据失败
					hideProgressDialog( );
//					tvGold.setText( _goldNum + PayModel.getInstance( ).getGoldNum() );
//					tvDiamond.setText( _diamondNum + PayModel.getInstance( ).getDiamondNum() );//gh
					mExpandableListView.onRefreshComplete( );
					if ( isHasCachaData == true ){
						showHintHeadView( );
					}else{
						mEmptyLayout.showEmpty( );
					}
					break;
				case  HANDLE_CACHEDATA:

					String alldata= SharedPreferenceCache.getInstance( mContext ).getString( "store_all_data" + Uid );
					if( !TextUtils.isEmpty( alldata ))
					{
						parseGetStoreDataList( alldata );
					}


				break;
			}
		}
	};
	
	/**
	 * 处理购买礼物
	 * 
	 * @param obj
	 */
	public void handleBuyGift( Object obj )
	{
		try
		{
			hideProgressDialog( );
			JSONObject result = new JSONObject( String.valueOf( obj ) );
			if ( result.optInt( "status" ) == 200 )
			{
				StoreBean.goldnum = Integer.parseInt( result.optString( "goldnum" ) );
				StoreBean.diamondnum = Integer.parseInt( result.optString( "diamondnum" ) );
				setGoldNum( );
				Toast.makeText( mContext , R.string.buy_gift_success , Toast.LENGTH_SHORT ).show( );
				updateLoginUserMineGiftsBuf( ConvertGiftsToGift( mCurrentGift ) );
			}
			else
			{
				if ( result.optInt( "error" ) == 4000 ){// 金币不足
					DialogUtil.showGoldDialog( mActivity );
				}else if ( result.optInt( "error" ) == 5930 ){// 钻石不足
					DialogUtil.showDiamondDialog( mActivity );
				}else{
					ErrorCode.showError( mActivity , String.valueOf( obj ) );
				}
			}
		}
		catch ( JSONException e )
		{
			e.printStackTrace( );
		}
	}
	
	/**
	 * 获取商店数据后的操作
	 */
	protected void handleParseStoreData( )
	{
		mExpandableListView.onRefreshComplete( );
		sectionsStore.clear( );
		if ( StoreBean == null )
			return;
//		sectionsStore.addAll( setGiftFlagResourceId( StoreBean.sections ) );
//		if ( StoreBean.giftbags != null && StoreBean.giftbags.size( ) > 0 )
//		{
//			giftbags = StoreBean.giftbags;
//			Sections gifBag = StoreBean.newSections();
//			gifBag.type =1;
//			gifBag.title = getResources().getString( R.string.store_giftbags );
//			gifBag.giftbags = StoreBean.giftbags;
//
//			sectionsStore.add( 1, gifBag );
//		}
//		else
//		{
//			giftbags = new ArrayList< GiftsBags >( );
//		}

		if(giftsList!=null && giftsList.gifts!=null)
		{
			Sections giftList = StoreBean.newSections();
			giftList.type =0;
			giftList.title = getResources().getString(R.string.store_all_gifts);
			giftList.gifts = giftsList.gifts;
			sectionsStore.add(giftList );
		}

		hideHintHeadView( );
		hideProgressDialog( );
		refreshView( );

		CommonFunction.log( "shifengxiong","sectionsStore.size() ====>>>" +sectionsStore.size() );
	}

	@Override
	public void onClick( View v )
	{
		// gh
		switch ( v.getId( ) )
		{
//			case R.id.gold_ly :// 点击金币按钮
////				addBtnEvent( DataTag.BTN_find_store_buyGold);
//				FragmentPayBuyGlod.jumpPayBuyGlodActivity(mContext, DataTag.BTN_find_store_buyGold);
//				break;
//
//			case R.id.diamond_ly :// 点击钻石按钮
////				addBtnEvent( DataTag.BTN_find_store_buyDiamon);
//				FragmentPayDiamond.jumpPayDiamondActivity(mContext, DataTag.BTN_find_store_buyDiamon);
//				break;
//
			case R.id.fl_left :// 点击返回键
				finish( );
				break;

//			case R.id.face_center :// 点击表情中心
////				addBtnEvent( DataTag.BTN_find_store_face);
//				Common.newFaceCount = 0;
//				FaceMainActivity.launch( mContext );
//				break;
//
//			case R.id.vip_ly :// 点击VIP充值
////				addBtnEvent( DataTag.BTN_find_store_buyVip);
//				Intent vipIntent = new Intent( mContext , UserVipOpenActivity.class );
//				startActivity( vipIntent );
//				break;
//
			case R.id.fl_right :// 点击私藏礼物
//				addBtnEvent( DataTag.BTN_find_store_myGift);
//				StoreMineGiftActivity.launchMineGiftToFollow( mContext , user );
				//YC
				Intent intent = new Intent(StoreMainActivity.this,StoreMineGiftActivityNew.class);
				startActivity(intent);
				break;
		}
	}
		
	
	/**
	 * banner条点击监听事件
	 */
	private OnClickListener avBannerOnClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			ResourceItemBean ad = ( ResourceItemBean ) v.getTag( );
			if (!CommonFunction.isEmptyOrNullStr(ad.banner.getLink( ))) {
				bannerJump( ad );
			}
		}
	};
	
	private void bannerJump( ResourceItemBean ad )
	{// 1遇见内部浏览器打开,2内部跳转,3遇见外部浏览器打开
		if ( ad.banner.getLink( ).contains( "http://" ) )
		{
			if ( ad.banner.jumptype == 1 )
			{
				Uri uri = Uri.parse( ad.banner.getLink( ) );
				Intent i = new Intent( mContext , WebViewAvtivity.class );
				i.putExtra( WebViewAvtivity.WEBVIEW_TITLE , ad.banner.title );
				i.putExtra( WebViewAvtivity.WEBVIEW_URL , uri.toString( ) );
				startActivity( i );
			}
			else if ( ad.banner.jumptype == 3 )
			{
				Uri uri = Uri.parse( ad.banner.getLink( ) );
				Intent intent = new Intent( Intent.ACTION_VIEW );
				intent.setData( uri );
				startActivity( intent );
			}
		}
		else if ( ad.banner.getLink( ).contains( "iaround://" ) )
		{
			InnerJump.Jump( mContext , ad.banner.getLink( ) );
		}
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		
		if ( mAdapter != null )
		{
			if (PayModel.getInstance( ).getGoldNum( ) != 0) {
				StoreBean.goldnum = Integer.parseInt( String.valueOf( PayModel.getInstance( )
						.getGoldNum( ) ) );
			}if (PayModel.getInstance( ).getDiamondNum( ) != 0) {
				StoreBean.diamondnum = Integer.parseInt( String.valueOf( PayModel.getInstance( )
						.getDiamondNum( ) ) );
			}
			setGoldNum( );
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( resultCode == RESULT_OK )
		{
			showProgressDialog( false );
			mTheMainHandler.postDelayed( new Runnable( )
			{
				@Override
				public void run( )
				{
					hideProgressDialog( );
					buyGoldGiftMode( exchangeGoldPrice );
				}
			} , 300 );
		}
	}
	
	/**
	 * 更新当前登陆用户的本地缓存的私藏礼物信息
	 * 
	 * @param gift
	 */
	private void updateLoginUserMineGiftsBuf( Gift gift )
	{
		Me user = Common.getInstance( ).loginUser;
		if ( user != null )
		{
			user.setMineGiftnum( user.getMineGiftnum( ) + 1 );
			user.getMineGifts( ).add( gift );
		}
		UserBufferHelper.getInstance( ).save( user );
	}
	
	
	
	/**
	 * 展示空数据的提示语
	 */
	private void showHintHeadView( )
	{
		isShowHintHeadView = true;
		NoDataHeadView.setVisibility( View.VISIBLE );
	}
	
	/**
	 * 关闭空数据的提示语
	 */
	private void hideHintHeadView( )
	{
		if ( NoDataHeadView.isShown( ) )
		{
			isShowHintHeadView = false;
			NoDataHeadView.setVisibility( View.GONE );
		}
	}
	
	// 显示加载框
	private void showProgressDialog(Boolean isCancelable)
	{
		if ( mProgressDialog == null )
		{
			mProgressDialog = DialogUtil.showProgressDialog( mContext , "" ,
					getString( R.string.please_wait ) , null );
			mProgressDialog.setCancelable( isCancelable );
		}
		
		mProgressDialog.show( );
	}
	
	private void hideProgressDialog( )
	{
		if ( mProgressDialog != null && mProgressDialog.isShowing( ) )
		{
			mProgressDialog.cancel( );
		}
	}
	
	/**
	 * 跳转到商店首页
	 * 
	 */
	public static void launcherStoreMainActivity( Context context )
	{
		Intent intent = new Intent( context , StoreMainActivity.class );
		intent.setClass( context , StoreMainActivity.class );
		context.startActivity( intent );
	}
	
	
	/**
	 * 设置金币 、钻石数量
	 */
	private void setGoldNum( )
	{
//		tvGold.setText( _goldNum + StoreBean.goldnum );
//		tvDiamond.setText( _diamondNum + StoreBean.diamondnum );
//		topTvGold.setText( _goldNum + StoreBean.goldnum );
//		topTvDiamond.setText( _diamondNum + StoreBean.diamondnum );//gh
		PayModel.getInstance( ).setGoldNum( StoreBean.goldnum );
		PayModel.getInstance( ).setDiamondNum( StoreBean.diamondnum );
	}
	
	/**
	 * 判断礼物标签属性，按顺序只显示两个图标
	 * 
	 * @param sectionsList
	 * @return 显示顺序：专属--优惠--新--热
	 */
	protected ArrayList< Sections > setGiftFlagResourceId(ArrayList< Sections > sectionsList )
	{
		if ( sectionsList != null && sectionsList.size( ) > 0 )
		{
			for ( Sections setc : sectionsList )
			{
				for ( Gifts gift : setc.gifts )
				{
					int firstFlagFromIndex = -1;
					firstFlagFromIndex = setFirstFlagIcon( gift , firstFlagFromIndex );
					setSecondFlagIcon( gift , firstFlagFromIndex );
				}
				setc.type =0;
			}
		}
		return sectionsList;
	}
	
	
	
	private Gift ConvertGiftsToGift( Gifts gifts )
	{
		Gift gift = new Gift( );
		gift.setId( gifts.giftid );
		gift.setName( CommonFunction.getLangText( mContext , gifts.name ) );
		gift.setIconUrl( gifts.icon );
		gift.setPrice( gifts.goldnum );
		gift.setCharisma( gifts.charmnum );
		gift.setExperience(gifts.expvalue );
		gift.setCurrencytype( gifts.currencytype );
		gift.setVipLevel( gifts.viptype );
		
		return gift;
	}
	
	
	/**
	 * 获取listview滚动距离
	 * 
	 * @param firstVisibleItem
	 * @return
	 */
	public int getScrollY( int firstVisibleItem )
	{
		int ScrollY = 0;
		int missViewHeight = 0;
		View currentView = null;
		Boolean isNotHeadView = false;
		if ( bannerView != null && bannerView.isShown( ) )
		{
//			switch ( firstVisibleItem )
//			{
//				case 0 :
//					currentView = bannerView;
//					break;
//				case 1 :
//					currentView = personalView;
//					missViewHeight = bannerView.getHeight( );
//					break;
//				case 2 :
//					currentView = mGoldView;
//					missViewHeight = bannerView.getHeight( ) + personalView.getHeight( );
//					break;
//				case 3 :
//					currentView = giftClassifyView;
//					missViewHeight = bannerView.getHeight( ) + personalView.getHeight( )
//							+ mGoldView.getHeight( );
//					break;
//				default :
//					isNotHeadView = true;
//					break;
//			}//gh
		}
		else
		{
//			switch ( firstVisibleItem )
//			{
//				case 0 :
//					currentView = personalView;
//					break;
//				case 1 :
//					currentView = mGoldView;
//					missViewHeight = personalView.getHeight( );
//					break;
//				case 2 :
//					currentView = giftClassifyView;
//					missViewHeight = mGoldView.getHeight( ) + personalView.getHeight( );
//					break;
//				default :
//					isNotHeadView = true;
//					break;
//			}//gh
		}
		
		if ( isNotHeadView == true )
		{
			ScrollY = 999999;
		}
		else
		{
			int currentViewTop = currentView.getTop( );
			ScrollY = -currentViewTop + missViewHeight;
		}
		return ScrollY;
	}
	
	
	/**
	 * 悬浮框是否展示
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 */
	protected void showTopView(int firstVisibleItem , int visibleItemCount )
	{
//		int ScrollY = getScrollY( firstVisibleItem );
//		int MaxHeight = 0;
//		if (bannerView != null && bannerView.isShown()) {
//			MaxHeight = personalView.getHeight() + bannerView.getHeight();
//		} else {
//			MaxHeight = personalView.getHeight();
//		}
//
//		if (ScrollY > lastScrollY) {
//			if (visibleItemCount >= 0 && ScrollY > MaxHeight) {
//				lastScrollY = ScrollY;
//				topGoldView.setVisibility(View.VISIBLE);
//				divider.setVisibility(View.VISIBLE);
//				if (isShowHintHeadView == true) {
//					NoDataHeadView.bringToFront();
//					NoDataHeadView.setVisibility(View.VISIBLE);
//				} else {
//					NoDataHeadView.setVisibility(View.GONE);
//				}
//			}
//		} else {
//			if (ScrollY <= MaxHeight) {
//				lastScrollY = ScrollY;
//				topGoldView.setVisibility(View.GONE);
//				divider.setVisibility(View.GONE);
//			}
//		}//gh
	}
	
	
	/**
	 * 显示礼物的第一个图标
	 * 
	 * @param gift
	 * @param firstFlagFromIndex
	 */
	private int setFirstFlagIcon( Gifts gift , int firstFlagFromIndex )
	{
		if ( gift.viptype == 1 )
		{// 是否VIP专属
			gift.flag1 = R.drawable.z_store_gift_vipflag;
			firstFlagFromIndex = 0;
		}
		else if ( gift.discountgoldnum != null && !gift.discountgoldnum.equals( "null" ) )
		{
			gift.flag1 = R.drawable.z_store_gift_saleflag;
			firstFlagFromIndex = 1;
		}
		else if ( gift.isnew == 1 )
		{// 是否最新 0：不是，1：是
			gift.flag1 = R.drawable.z_store_gift_newflag;
			firstFlagFromIndex = 2;
		}
		else if ( gift.ishot == 1 )
		{// 是否热门 0：不是，1：是
			gift.flag1 = R.drawable.z_store_gift_hotflag;
			firstFlagFromIndex = 3;
		}
		else
		{
			gift.flag1 = -1;
		}
		return firstFlagFromIndex;
	}
	
	/**
	 * 显示礼物的第二个图标
	 * 
	 * @param gift
	 * @param firstFlagFromIndex
	 */
	private void setSecondFlagIcon( Gifts gift , int firstFlagFromIndex )
	{
		switch ( firstFlagFromIndex )
		{
			case 0 :// 第一个为vip专属
			if (gift.discountgoldnum != null
					&& !gift.discountgoldnum.equals("null")) {
				gift.flag2 = R.drawable.z_store_gift_saleflag;
			} else if (gift.isnew == 1) {
				gift.flag2 = R.drawable.z_store_gift_newflag;
			} else if (gift.ishot == 1) {
				gift.flag2 = R.drawable.z_store_gift_hotflag;
			} else {
				gift.flag2 = -1;
			}
				break;
			
			case 1 :// 第一个为优惠
			if (gift.isnew == 1) {
				gift.flag2 = R.drawable.z_store_gift_newflag;
			} else if (gift.ishot == 1) {
				gift.flag2 = R.drawable.z_store_gift_hotflag;
			} else {
				gift.flag2 = -1;
			}
				break;
			
			case 2 :// 第一个为最新
			if (gift.ishot == 1) {
				gift.flag2 = R.drawable.z_store_gift_hotflag;
			} else {
				gift.flag2 = -1;
			}
				break;
			
			case 3 :
				gift.flag2 = -1;
				break;
			
			default :
				gift.flag2 = -1;
				break;
		}
	}

//	@Override
//	public String getTag( )
//	{
//		return DataTag.VIEW_find_store;
//	}
	
}
