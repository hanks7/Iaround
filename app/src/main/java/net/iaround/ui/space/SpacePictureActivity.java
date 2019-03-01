
package net.iaround.ui.space;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.BaseHttp;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.adapter.FragmentStatePagerAdapter;
import net.iaround.ui.comon.JazzyViewPager;
import net.iaround.ui.datamodel.Blog;
import net.iaround.ui.interfaces.SpacePicFragmentInterface;
import net.iaround.ui.view.SpaceTopicView;
import net.iaround.utils.ImageViewUtil;

import java.util.ArrayList;
import java.util.HashMap;


public class SpacePictureActivity extends BaseFragmentActivity implements
		OnPageChangeListener, OnClickListener,SpacePicFragmentInterface{
	public static final int RESEULT_CODE_REVIEW = 2001;
	private SpacePhotoAdapter mPhotoAdapter;
	protected JazzyViewPager mViewPager;
	private SpacePictureFragment[ ] mPagerViews;
	private Context mActivity;
	private TextView mIconNum;// 图片的数量
	private ImageView mDeleteIcon;// 删除照片按钮
	private String mPhotoId;// 当前photo的id
	private String mPhotoSmall; // 小图ID
	private long mUid;// 照片拥有者的UID
	private String mTag;// 从哪个页面进来的
	private ArrayList< String > mSmallPhotoIds; // 小图片ID列表
	private ArrayList< String > mPhotoIds; // 图片ID列表
	private int mPhotoIndex = 0; // 当前图片的位置
	public HashMap< String , Blog> mBlogDatas; // 保存所有请求过的数据
	private long delPhotoflag;
	
	private boolean isDeletedPhoto = false;//记录是否删除过照片
	
	
	
	private SpacePictureFragment currentDetailFragment;
	
	private int vipDivIndex = 8;
	private boolean isVipChange;
	
	
	public enum BUTTON_TYPE
	{
		REPORT , // 举报
		SET_ICON// 设置为头像
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.space_picture_detail );
		mActivity = this;
		
		mPhotoId = getIntent( ).getStringExtra( "photoid" );
		mPhotoSmall = getIntent( ).getStringExtra( "photoSmall" );
		mUid = getIntent( ).getLongExtra( "uid" , 0 );
		mTag = getIntent( ).getStringExtra( "tag" );
		
		if ( getIntent( ).hasExtra( "ids" ) )
		{
			mPhotoIds = getIntent( ).getStringArrayListExtra( "ids" );
		}
		if ( mPhotoIds == null )
		{
			mPhotoIds = new ArrayList< String >( );
			mPhotoIds.add( mPhotoId );
		}
		
		if ( getIntent( ).hasExtra( "smallPhotos" ) )
		{
			mSmallPhotoIds = getIntent( ).getStringArrayListExtra( "smallPhotos" );
			
		}
		if ( mSmallPhotoIds == null )
		{
			mSmallPhotoIds = new ArrayList< String >( );
			mSmallPhotoIds.add( mPhotoSmall );
		}
		
		mBlogDatas = new HashMap< String , Blog >( );
		
		CommonFunction.log( "fan" , "photoid:" + mPhotoId );
		CommonFunction.log( "fan" , mPhotoIds.size( ) + "ids:" + mPhotoIds );
		
		
		
		mDeleteIcon = (ImageView) findViewById( R.id.iv_del_picture );
		
		mDeleteIcon.setOnClickListener( this );
		
//		if ( Common.getInstance( ).loginUser.getUid( ) == getPhotoUid( ) )
//		{
//			//显示删除的按钮
//			findViewById( R.id.ly_del_banner ).setVisibility( View.VISIBLE );
//		}
		
		
		
		int num = 1;
		if ( mSmallPhotoIds != null && mSmallPhotoIds.size( ) > 0 )
		{
			num = mSmallPhotoIds.size( );
		}
		
		
		if ( mUid == 0 )
		{
			mPhotoIndex = getIntent( ).getIntExtra( "position" , 0 );
			mPhotoIds.clear( );
			mPhotoIds.addAll( mSmallPhotoIds ) ;
		}
		
		else
		{
			for ( int i = 0 ; i < num ; i++ )
			{
				if ( mPhotoId.equals( mPhotoIds.get( i ) ) )
				{
					mPhotoIndex = i;
					break;
				}
			}
		}
		
		int size = mSmallPhotoIds.size( );
		
		if ( mSmallPhotoIds != null && mSmallPhotoIds.size( ) > 0 )
		{
			mPagerViews = new SpacePictureFragment[ size ];
			
		}
		else
		{
			mPagerViews = new SpacePictureFragment[ 1 ];
		}
		
		mIconNum = (TextView) findViewById( R.id.right_text_num );
		mViewPager = ( JazzyViewPager ) findViewById( R.id.pager );
		
		
//		initViewPagerScroll( );
		// mViewPager.setTransitionEffect( TransitionEffect.RotateDown );
		mViewPager.setTransitionEffect( JazzyViewPager.TransitionEffect.Standard );
		
		CommonFunction
				.log( "shifengxiong" , "mPhotoIndex =====================" + mPhotoIndex );
		
		mPhotoAdapter = new SpacePhotoAdapter( getSupportFragmentManager( ) );
		mViewPager.setAdapter( mPhotoAdapter );
		mViewPager.setOnPageChangeListener( this );
		mViewPager.setCurrentItem( mPhotoIndex );
		mIconNum.setText( ( 1 + mPhotoIndex ) + "/" + mSmallPhotoIds.size( ) );
		mMainHandler.sendEmptyMessageDelayed( 1 , 500 );
	}
	
	public long getPhotoUid( )
	{
		return mUid;
	}
	
	@Override
	public void onPageScrollStateChanged( int state )
	{
	}
	
	@Override
	protected void onDestroy( )
	{
		ImageViewUtil.getDefault( ).clearDefaultLoaderMemoryCache( );
		System.gc( );
		clearFocusDetailFragment( );
		super.onDestroy( );
	}
	
	@Override
	public void onPageScrolled( int arg0 , float arg1 , int arg2 )
	{
	}
	
	
	@Override
	public void onPageSelected( int position )
	{
		// vip 查看更多
		if ( !Common.getInstance( ).loginUser.isVip( ) && position + 1 == 9
			&& ( mUid != Common.getInstance( ).loginUser.getUid( ) ) )
		{
			mViewPager.setCurrentItem( position - 1 );
			DialogUtil.showTobeVipDialog( mActivity , R.string.space_photo_dialog_title ,
				R.string.only_vip_can_view_all_pic );

			return;
		}
		else
		{
			if(mPagerViews[position]!=null)
			{
				mPagerViews[position].init( );
				//					mPagerViews[position].showImage( );
			}
		}

		int netType = BaseHttp.checkNetworkType( mActivity );
		
		if ( netType == BaseHttp.TYPE_NET_WORK_DISABLED )
		{
			CommonFunction.toastMsg( mActivity , R.string.network_req_failed );
		}
		mIconNum.setText( ( 1 + position ) + "/" + mSmallPhotoIds.size( ) );
	}
	
	private Handler mMainHandler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case 1 :
					
					onPageSelected( mViewPager.getCurrentItem( ) );
					
					break;
			}
		}
    };
	
	
	@Override
	public void onSaveInstanceState( Bundle outState ) {
		super.onSaveInstanceState( outState );
		
	}
	
	@Override
	public void onStop( ) {
		super.onStop( );
	}
	
	@Override
	public void onResume( ) {
		super.onResume( );

		isVipChange = Common.getInstance( ).loginUser.isVip( );
		
		if(isVipChange) {
			if(mPagerViews.length >vipDivIndex) {
				if(mPagerViews[vipDivIndex]!=null) {
					mPagerViews[ vipDivIndex ].reFlashData( vipDivIndex, mPhotoIds.get( vipDivIndex ), mSmallPhotoIds.get( vipDivIndex )  );
					mPhotoAdapter.notifyDataSetChanged( );
				}
			}
		}
	}



	private class SpacePhotoAdapter extends FragmentStatePagerAdapter {

		public SpacePhotoAdapter( FragmentManager fm ) {
			super( fm );
			
		}
		
		@Override
		public Fragment getItem(int arg0 ) {
			Common.setAvailMem( SpacePictureActivity.this );
			
			int index = arg0;
			if ( !Common.getInstance( ).loginUser.isVip( ) && index + 1 == 9 &&
				( mUid != Common.getInstance( ).loginUser.getUid( ) ) ) {
				mViewPager.setCurrentItem( index - 1 );
				mPagerViews[ arg0 ] = newInstance( index, mPhotoIds.get( index ),
					mSmallPhotoIds.get( arg0 ), true );

				vipDivIndex = arg0;

			}
			else {
				mPagerViews[ arg0 ] = newInstance( arg0, mPhotoIds.get( arg0 ),
					mSmallPhotoIds.get( arg0 ) );
			}

			mViewPager.setObjectForPosition( mPagerViews[ index ] , index );
			return mPagerViews[ index ];
		}
		
		@Override
		public int getCount( ) {
			
			return mPagerViews.length;
		}
		
		@Override
		public Object instantiateItem(ViewGroup arg0 , int arg1 ) {
			
			return super.instantiateItem( arg0 , arg1 );
		}
		
		@Override
		public void destroyItem(ViewGroup container , int position , Object object ) {
			CommonFunction.log( "shifengxiong" ,"destroyItem=="+position);
			super.destroyItem( container , position , object );
			if(mPagerViews.length>position) {
				mPagerViews[position] = null;
			}
		}
		
	}
	
	
	public SpacePictureFragment newInstance(int position , String PhotoId , String smallPhoto ) {
		
		SpacePictureFragment staticFocusFragment = new SpacePictureFragment( );
		
		Bundle args = new Bundle( );
		args.putString( "photoId" , PhotoId );
		args.putInt( "position" , position );
		args.putString( "smallPhoto" , smallPhoto );
		args.putBoolean(  "limit" , false );
		
		staticFocusFragment.setArguments( args );
		
		currentDetailFragment = staticFocusFragment;
		
		return staticFocusFragment;
		
	}
	
	public SpacePictureFragment newInstance(int position , String PhotoId , String smallPhoto , boolean islimit ) {
		
		SpacePictureFragment staticFocusFragment = new SpacePictureFragment( );
		
		Bundle args = new Bundle( );
		args.putString( "photoId" , PhotoId );
		args.putInt( "position" , position );
		args.putString( "smallPhoto" , smallPhoto );
		args.putBoolean(  "limit" , islimit );
		
		staticFocusFragment.setArguments( args );
		
		currentDetailFragment = staticFocusFragment;
		
		return staticFocusFragment;
		
	}
	
	void clearFocusDetailFragment( ) {

	}
	
	@Override
	public void onClick( View v ) {
		switch ( v.getId( ) ) {
			case R.id.iv_del_picture :
				
				if ( mPagerViews[ mViewPager.getCurrentItem( ) ] != null ) {
					mPagerViews[ mViewPager.getCurrentItem( ) ].onClick( v );
				}
				break;
		}
		
	}
	
	
	
	public void delPhoto( int index ) {
		
		mPhotoIds.remove( index );
		mSmallPhotoIds.remove( index );
		if ( mSmallPhotoIds.size( ) < 1 ) {
			finish( );
			return;
		}
		// mPagerViews[index] = null;
		
		SpacePictureFragment temp = mPagerViews[ index ];
		
		for ( int i = index , iMax = mPagerViews.length - 1 ; i < iMax ; i++ ) {
			mPagerViews[ i ] = mPagerViews[ i + 1 ];
		}
		
		mPagerViews = new SpacePictureFragment[ mSmallPhotoIds.size( ) ];
		mPhotoAdapter.notifyDataSetChanged( );
		mViewPager.setAdapter( mPhotoAdapter );
		if ( index > 0 ) {
			mViewPager.setCurrentItem( index - 1 );
		}
		else {
			mViewPager.setCurrentItem( 0 );
		}
		
		mIconNum.setText( ( 1 + mViewPager.getCurrentItem( ) ) + "/" + mPhotoIds.size( ) );
	}
	
	@Override
	public void onBackPressed() {
		if(isDeletedPhoto) {
			setResult(SpaceTopicView.RES_SPACE_DELETE);
		}
		finish();
	}
	
	@Override
	public void setIsDeletePhoto( boolean isDeletePhoto )
	{
		this.isDeletedPhoto = isDeletePhoto;
	}
	
	@Override
	public boolean getIsDeletePhoto( )
	{
		return this.isDeletedPhoto;
	}
	
	public static void launch(Context context , ArrayList< String > iconUrl ,
							  ArrayList< String > photoesId , int position , String selectPhotoid , Long uid ) {
		Intent i = new Intent( context , SpacePictureActivity.class );
		
		i.putExtra( "photoid" , selectPhotoid + "" );
		i.putExtra( "uid" , uid );
		i.putExtra( "position" , position );
		
		
		if ( photoesId != null && photoesId.size( ) > 0 ) {
			i.putExtra( "ids" , photoesId );
		}
		if ( iconUrl != null && iconUrl.size( ) > 0 ) {
			i.putExtra( "smallPhotos" , iconUrl );
		}
		
		context.startActivity( i );
		
	}
	
	public static void launch(Context context , ArrayList< String > iconUrl , int position ) {
		launch( context , iconUrl , null , position , "" , 0l );
	}

}
