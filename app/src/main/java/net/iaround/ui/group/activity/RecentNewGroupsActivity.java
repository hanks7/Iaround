
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.GroupListBean;

import java.util.ArrayList;


public class RecentNewGroupsActivity extends SuperActivity
{
	private int PAGE_SIZE = 20;
	
	private ImageView mTitleBack;
	private TextView mTitleName;
	private PullToRefreshListView mListView;
	private EmptyLayout mEmptyLayout;
	
	private long GET_NEW_GROUPS_FLAG;
	
	private Dialog mWaitDialog;
	private DataAdapter mAdapter;
	private ArrayList<Group> mDataList = new ArrayList< Group >( );
	private GroupListBean mNewGroupsBean;
	
	private int mCurPage = 0;
	private int mTotalPage;
	
	private boolean isRecommendGroup = false ;

	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.space_joint_group_layout );
		isRecommendGroup = getIntent( ).getBooleanExtra( "Recommend" , false );
		initViews( );
		setListeners( );
		initData( );
	}
	
	private void initViews( )
	{
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleName.setText( getString( R.string.nearby_recent_group_title ) );
		if(isRecommendGroup)
		{
			mTitleName.setText( getString( R.string.group_inf_group_recommend_title ) );
		}
		
		mListView = (PullToRefreshListView) findViewById( R.id.group_listview );
		mListView.setMode( Mode.BOTH );
		
		mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , null);
		mWaitDialog.setCancelable( true );
		
		mEmptyLayout = new EmptyLayout( mContext , mListView.getRefreshableView( ) );
		mEmptyLayout.setEmptyMessage( getResString( R.string.empty_search_group ) );
		
	}
	
	private void setListeners( )
	{
		findViewById(R.id.fl_left).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mTitleBack.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				finish( );
			}
		} );
		
		mListView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2<ListView>( )
		{
			
			@Override
			public void onPullDownToRefresh( PullToRefreshBase< ListView > refreshView )
			{
				mCurPage = 0;
				requestData( mCurPage + 1 );
			}
			
			@Override
			public void onPullUpToRefresh( PullToRefreshBase< ListView > refreshView )
			{
				// TODO Auto-generated method stub
				if ( mCurPage < mTotalPage )
				{
					requestData( mCurPage + 1 );
				}
				else
				{
					mListView.postDelayed( new Runnable( )
					{
						public void run( )
						{
							mListView.onRefreshComplete( );
						}
					} , 200 );
					CommonFunction.toastMsg( mContext , getString( R.string.no_more ) );
				}
			}
		} );
		
		mListView.setOnItemClickListener( new OnItemClickListener( )
		{
			
			@Override
			public void onItemClick(AdapterView< ? > arg0 , View arg1 , int position ,
									long arg3 )
			{
				Group group = null;
				try
				{
					group = mDataList.get( position - 1 );
				}
				catch ( Exception e )
				{
					e.printStackTrace( );
				}
				if ( group != null )
				{
					Intent intent = new Intent( mContext , GroupInfoActivity.class );
					intent.putExtra( GroupInfoActivity.GROUPID , group.id + "" );
					intent.putExtra( GroupInfoActivity.CREATEID , group.user.userid );
					intent.putExtra( GroupInfoActivity.CREATEORICONURL , group.user.icon );
					intent.putExtra( GroupInfoActivity.FROMSEARCH , true );
					intent.putExtra( GroupInfoActivity.ISJOIN , false );
					mContext.startActivity( intent );
				}
			}
		} );
	}
	
	private void initData( )
	{
		mAdapter = new DataAdapter( );
		mListView.setAdapter( mAdapter );
		
		mCurPage = 0;
		requestData( mCurPage + 1 );	
	}
	
	private void requestData( int pageIndex )
	{
		showWaitDialog( true );
		int lat = 0;
		int lng = 0;
		if ( LocationUtil.getCurrentGeo( mActivity ) != null )
		{
			lat = LocationUtil.getCurrentGeo( mActivity ).getLat( );
			lng = LocationUtil.getCurrentGeo( mActivity ).getLng( );
		}
		if(isRecommendGroup)
		{
			GET_NEW_GROUPS_FLAG  = GroupHttpProtocol.getRecommendGroups(
					mContext , lat , lng , pageIndex , PAGE_SIZE , this );
		}
		else
		{
		GET_NEW_GROUPS_FLAG  = GroupHttpProtocol.getRecentNewGroups(
				mContext , lat , lng , pageIndex , PAGE_SIZE , this );
		}
	}
	
	private void showWaitDialog( boolean isShow )
	{
		if ( mWaitDialog != null )
		{
			if ( isShow )
			{
				mWaitDialog.show( );
			}
			else
			{
				mWaitDialog.hide( );
			}
		}
		
	}
	
	public void handleGroupData( ArrayList< Group > groups )
	{
		if ( groups != null )
		{
			int length = groups.size( );
			if ( length == 1 )
			{
				for ( Group group : groups )
				{
					group.isShowDivider = 0;
				}
			}
			else if ( length > 1 )
			{
				for ( int i = 0 ; i < length ; i++ )
				{
					Group group = groups.get( i );
					if ( i < ( length - 1 ) )
					{
						group.isShowDivider = 1;
					}
					else
					{
						group.isShowDivider = 0;
					}
				}
			}
			
			mDataList.addAll( groups );
		}
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
	@Override
	public void onGeneralError( final int e , final long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == GET_NEW_GROUPS_FLAG )
		{
			showWaitDialog( false );
			mListView.onRefreshComplete( );
			ErrorCode.toastError( mContext , e );
			mEmptyLayout.showError( );
		}
	}
	
	@Override
	public void onGeneralSuccess(final String result , final long flag )
	{
		CommonFunction.log( "group" , "newgroups***" + result );
		super.onGeneralSuccess( result , flag );
		if ( flag == GET_NEW_GROUPS_FLAG )
		{
			showWaitDialog( false );
			mListView.onRefreshComplete( );
			mNewGroupsBean = GsonUtil.getInstance( ).getServerBean( result ,
					GroupListBean.class );
			if ( mNewGroupsBean != null )
			{
				if ( mNewGroupsBean.isSuccess( ) )
				{
					if ( mCurPage == 0 )
					{
						mDataList.clear( );
					}
					mCurPage ++;
//					mCurPage = mNewGroupsBean.pageno;
					mTotalPage = mNewGroupsBean.amount / PAGE_SIZE;
					if ( mNewGroupsBean.amount % PAGE_SIZE > 0 )
					{
						mTotalPage++;
					}
					
					handleGroupData( mNewGroupsBean.groups );
					
					mAdapter.notifyDataSetChanged( );
					
					if ( mDataList.isEmpty( ) )
					{
						mEmptyLayout.showEmpty( );
					}
				}
				else
				{
					onGeneralError( mNewGroupsBean.error , flag );
				}
			}
			else
			{
				onGeneralError( 107 , flag );
			}
		}
	}
	
	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		if ( mWaitDialog != null )
		{
			mWaitDialog.dismiss( );
			mWaitDialog = null;
		}
	}
	
	class DataAdapter extends BaseAdapter
	{
		GeoData currentGeo = LocationUtil.getCurrentGeo( mContext );
		int distance = 0;
		
		@Override
		public int getCount( )
		{
			return mDataList.size( );
		}
		
		@Override
		public Object getItem(int position )
		{
			return mDataList.get( position );
		}
		
		@Override
		public long getItemId( int position )
		{
			return position;
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ViewHolder holder = null;
			if ( convertView == null )
			{
				holder = new ViewHolder( );
				convertView = LayoutInflater.from( mContext ).inflate(
						R.layout.group_joint_item_new, null );
				holder.groupHeader = (TextView) convertView.findViewById( R.id.header_text );
				holder.groupContent = (RelativeLayout) convertView
						.findViewById( R.id.content_layout );
				holder.groupImage = (ImageView) convertView.findViewById( R.id.group_img );
				holder.groupTypeImg = (ImageView) convertView.findViewById( R.id.group_type );//于超
				holder.groupName = (TextView) convertView.findViewById( R.id.group_name );
				holder.groupDesc = (TextView) convertView.findViewById( R.id.group_desc );
				holder.groupLevel = (RatingBar) convertView.findViewById( R.id.group_level );
//				holder.groupMemberNum = (TextView) convertView
//						.findViewById( R.id.group_member_num );
				
				holder.headerPart = convertView.findViewById( R.id.header_part );
				holder.groupFlag = (TextView) convertView.findViewById( R.id.group_flag );
				holder.groupDistance = (TextView) convertView
						.findViewById( R.id.group_distance );
				holder.groupCurrentMembers = (TextView) convertView
						.findViewById( R.id.group_current_members );
				holder.splitLine = (TextView) convertView.findViewById( R.id.split_line );
				holder.groupMaxMembers = (TextView) convertView
						.findViewById( R.id.group_max_members );
				holder.groupCategory = (TextView) convertView
						.findViewById( R.id.group_category );
				holder.bottomDivider = convertView.findViewById( R.id.bottom_divider );
				convertView.setTag( holder );
				holder.groupMembersIcon = (ImageView) convertView.findViewById(R.id.group_members_icon);
			}
			else
			{
				holder = ( ViewHolder ) convertView.getTag( );
			}
			
			Group group = mDataList.get( position );
			if ( group != null )
			{
				if ( group.isShowDivider == 1 )
				{
					holder.bottomDivider.setVisibility( View.VISIBLE );
				}
				else
				{
					holder.bottomDivider.setVisibility( View.GONE );
				}
				
				if ( group.contentType == 1 )
				{
					holder.headerPart.setVisibility( View.GONE );
					holder.groupHeader.setVisibility( View.VISIBLE );
					holder.groupContent.setVisibility( View.GONE );
					holder.groupHeader.setText( group.landmarkname + "" );
				}
				else
				{
					
					holder.headerPart.setVisibility( View.GONE );
					holder.groupHeader.setVisibility( View.GONE );
					holder.groupContent.setVisibility( View.VISIBLE );

					GlideUtil.loadCircleImage(BaseApplication.appContext,CommonFunction.thumPicture( group.icon ),holder.groupImage,PicIndex.DEFAULT_GROUP_SMALL,
							PicIndex.DEFAULT_GROUP_SMALL);
//					ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
//						group.categoryicon, holder.groupTypeImg, PicIndex.DEFAULT_GROUP_SMALL,
//						PicIndex.DEFAULT_GROUP_SMALL, null,0, "#00000000" );
//					GlideUtil.loadCircleImage(RecentNewGroupsActivity.this,group.categoryicon,holder.groupTypeImg,PicIndex.DEFAULT_GROUP_SMALL,
//							PicIndex.DEFAULT_GROUP_SMALL);
					if ( group.newcategoryid == 20 )//
					{
						holder.groupTypeImg.setVisibility( View.GONE );
					}
					
					SpannableString groupName = FaceManager.getInstance( parent.getContext( ) )
							.parseIconForString( holder.groupName , parent.getContext( ) ,
									group.name , 16 );
					holder.groupName.setText( groupName );

					//圈子介绍
					SpannableString groupDesc = FaceManager.getInstance( parent.getContext( ) )
							.parseIconForString( holder.groupDesc , parent.getContext( ) ,
									group.getGroupDesc( mContext ) , 13 );
					if (!TextUtils.isEmpty(groupDesc))
					{
						holder.groupDesc.setVisibility(View.VISIBLE);
						holder.groupDesc.setText(groupDesc);
					}
//					holder.groupLevel.setRating( ( float ) ( group.level / 2.0 ) );//取消圈子等级
//					holder.groupMemberNum.setText( group.usercount + "" );//于超
					
					/*
					 * if (group.flag == 1) {
					 * //holder.groupFlag.setImageResource
					 * (R.drawable.chat_theme_new);
					 * holder.groupFlag.setVisibility(View.GONE); } else if
					 * (group.flag == 2) {
					 * holder.groupFlag.setVisibility(View.VISIBLE);
					 * holder.groupFlag
					 * .setImageResource(R.drawable.group_hot_flag_img); } else
					 * { holder.groupFlag.setVisibility(View.GONE); }
					 */
					
					
					if ( currentGeo != null )
					{
						distance = LocationUtil.calculateDistance( currentGeo.getLng( ) ,
								currentGeo.getLat( ) , group.lng , group.lat );
					}
					else {
						distance = 0;
					}
					holder.groupDistance.setText( "  "+ CommonFunction.covertSelfDistance( distance ) );
					
					// 小圈显示当前人数和总人数，大圈和十万圈只显示当前人数
					holder.groupCurrentMembers.setText( String.valueOf( group.usercount ) );
					Drawable drawable = null;
					if (group.classify == 1) {
						holder.splitLine.setVisibility( View.VISIBLE );
						holder.groupMaxMembers.setText( String.valueOf( group.maxcount ) );
					} else if (group.classify == 2) {
						holder.groupMembersIcon
								.setImageResource(R.drawable.group_num_big_round);
						holder.splitLine.setVisibility( View.GONE );
						holder.groupMaxMembers.setText( "" );
					} else if (group.classify == 3) {
						holder.groupMembersIcon.setImageResource(R.drawable.group_num_big_round);
						holder.splitLine.setVisibility( View.GONE );
						holder.groupMaxMembers.setText( "" );
					}
					//圈子分类
					if (group.category != null)
					{
						String[ ] typeArray = group.category.split( "\\|" );
						int langIndex = CommonFunction.getLanguageIndex( mContext );
						holder.groupCategory.setText( typeArray[ langIndex ] );
					}
					
				}
			}
			return convertView;
		}
	}
	
	static class ViewHolder
	{
		public TextView groupHeader;
		public RelativeLayout groupContent;
		public ImageView groupImage;
		public ImageView groupTypeImg;
		public TextView groupName;
		public TextView groupDesc;
		public RatingBar groupLevel;
		public TextView groupMemberNum;
		
		public View headerPart;
		public TextView groupFlag;
		public TextView groupDistance;
		public TextView groupCurrentMembers;
		public TextView splitLine;
		public TextView groupMaxMembers;
		public TextView groupCategory;
		public View bottomDivider;
		private ImageView groupMembersIcon;
	}
}
