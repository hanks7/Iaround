
package net.iaround.ui.group.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.BaseActivity;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.group.adapter.GroupGatherAdapter;
import net.iaround.ui.group.bean.GatherListBean.joininfoBeen.JoinUser;
import net.iaround.ui.group.bean.GatherDetailBean.DetailBean;
import net.iaround.ui.group.bean.GatherListBean;
import net.iaround.ui.group.bean.GatherListBean.GatherItemBean;
import net.iaround.ui.group.bean.GroupPublishGatherBean;
import net.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import java.util.ArrayList;


/**
 * @ClassName: GroupGatherActivity
 * @Description: 5.6 圈聚会列表
 * @author zhengshaota
 * @date
 * 
 */
public class GroupGatherActivity extends BaseActivity implements OnClickListener,HttpCallBack

{
	private String groupId = "";
	private EmptyLayout mEmptyLayout;
	private RelativeLayout publish_btn;
	private GroupGatherAdapter mAdapter;
	private PullToRefreshListView mListView;
	private int pageNo = 1;
	private int amount;// 圈聚会总条数
	private final int pageSize = 10;// 每页请求的数据数量
	private boolean haveNext = true;// 是否有下一页的数据
	/** 当前用户在该圈中的角色（0创建者；1管理员；2圈成员；3非圈成员） */
	private static int grouprole = 0;
	public final int MSG_GET_LIST_DATA = 100;
	public final int MSG_STOP_PULLING = 200;
	private long GET_GATHER_LIST;// 请求我的圈子列表的Flag
	public final static int GATHER_REVISE = 0X100;// 聚会修改返回码
	public final static int GATHER_DETAIL_CODE = 1000;// 聚会详情请求码
	public final static int GATHER_REVISE_CODE = 1001;// 聚会修改请求码
	public static boolean isPublishOrDeleteGather = false;// 是否发布或删除圈聚会
	public static ArrayList<GatherItemBean> GatherList = new ArrayList< GatherItemBean >( );
	
	
	public static void launch(Context context , String groupId , int grouprole )
	{
		Intent intent = new Intent( context , GroupGatherActivity.class );
		intent.putExtra( "group_id" , groupId );
		intent.putExtra( "group_role" , grouprole );
		context.startActivity( intent );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_gather );
		groupId = getIntent( ).getStringExtra( "group_id" );
		grouprole = getIntent( ).getIntExtra( "group_role" , 2 );
		initViews( );
		setListeners( );
		initData( );
	}
	
	/**
	 * @Title: initViews
	 * @Description: 初始化所有控件
	 */
	private void initViews( )
	{
		boolean pauseOnScroll = true;
		boolean pauseOnFling = true;
//		PauseOnScrollListener listener = new PauseOnScrollListener( ImageViewUtil.getDefault( )
//				.getImageLoader( ) , pauseOnScroll , pauseOnFling );
//
		TextView titleName = (TextView) findViewById( R.id.title_name );
		titleName.setText( getString( R.string.group_inf_group_gatherings ) );
		ImageView titleBack = (ImageView) findViewById( R.id.title_back );
		titleBack.setOnClickListener( this );
		
		mListView = (PullToRefreshListView) findViewById( R.id.groupMeet_List );
		publish_btn = (RelativeLayout) findViewById( R.id.btn_publish_group );
		int publishWidth = (int) (CommonFunction.getScreenPixWidth(GroupGatherActivity.this) * 0.8);
		int offset = CommonFunction.dipToPx(GroupGatherActivity.this, 11);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(publishWidth, LayoutParams.WRAP_CONTENT );
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.setMargins(0, 0, 0, offset);
		publish_btn.setLayoutParams(params);


		View footerView = new View( GroupGatherActivity.this );
		footerView.setLayoutParams( new AbsListView.LayoutParams( 1 , 120 ) );
		mListView.getRefreshableView( ).addFooterView( footerView );

		mListView.setMode( Mode.BOTH );
//		mListView.setOnScrollListener( listener );
		mListView.setPullToRefreshOverScrollEnabled( false );// 禁止滚动过头
		mListView.getRefreshableView( ).setDividerHeight( 0 );
		mListView.getRefreshableView( ).setSelector( R.drawable.info_bg_center_selector );
		mListView.getRefreshableView( ).setFastScrollEnabled( false );

		mEmptyLayout = new EmptyLayout( GroupGatherActivity.this , mListView.getRefreshableView( ) );
		mEmptyLayout.setEmptyMessage( getString( R.string.group_no_gatherings ) );


	}

	/**
	 * 获取网络数据并显示
	 * */
	private void requestData( int pageNo )
	{
		GET_GATHER_LIST = GroupHttpProtocol.getGroupGatherList( this , groupId , pageNo , pageSize ,
				this );
	}

	private void initData( )
	{
		String Uid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		String data = SharedPreferenceCache.getInstance( GroupGatherActivity.this ).getString(
				"groupGather" + Uid + groupId );
		if ( data != null && !data.isEmpty( ) )
		{// 获取缓存展示
			GatherListBean bean = new GatherListBean( );
			bean = GsonUtil.getInstance( ).getServerBean( data , GatherListBean.class );
			if ( bean!=null&&bean.partys != null && bean.amount > 0 )
			{
				GatherList = bean.partys;
				showData( );
			}
			else
			{
				mEmptyLayout.showLoading( );
				publish_btn.setVisibility( View.GONE );
			}
		}
		else
		{
			mEmptyLayout.showLoading( );
			publish_btn.setVisibility( View.GONE );
		}

		pageNo = 1;
		requestData( pageNo );
	}

	/**
	 * @Title: setListeners
	 * @Description: 初始化监听器
	 */
	private void setListeners( )
	{
		publish_btn.setOnClickListener( this );
		// 设置当下拉和上拉刷新时的操作
		mListView.setOnRefreshListener( new PullToRefreshBase.OnRefreshListener2<ListView>( )
		{
			@Override
			public void onPullDownToRefresh( PullToRefreshBase< ListView > refreshView )
			{// 下拉刷新
				pageNo = 1;
				requestData( pageNo );
			}

			@Override
			public void onPullUpToRefresh( PullToRefreshBase< ListView > refreshView )
			{// 上拉刷新
				if ( !haveNext )
				{
					CommonFunction.toastMsg( GroupGatherActivity.this , R.string.no_more );
					handler.sendEmptyMessage( MSG_STOP_PULLING );
				}
				else
				{
					pageNo++;
					requestData( pageNo );
				}
			}
		} );

	}

	@SuppressLint( "HandlerLeak" )
	public Handler handler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
			
				case MSG_GET_LIST_DATA :
					
					GatherListBean bean = ( GatherListBean ) msg.obj;
					if(bean==null)break;
					amount = bean.amount;
					
					if ( bean.pageno == 1 && bean.partys != null )
					{// 如果是第一页的数据，则替换掉现在的列表
						GatherList = bean.partys;
					}
					else if ( bean.pageno == 1 && bean.partys == null )
					{
						GatherList.clear( );
					}
					else if ( bean.pageno > 1 )
					{// 如果是后面的页数，则在现有的列表中追加上去
						GatherList.addAll( bean.partys );
					}
					
					// 判断是否有下一页数据
                    haveNext = pageNo * pageSize < amount;
					showData( );
					break;
				case MSG_STOP_PULLING :
				{
					stopPulling( );
				}
					break;
			
			}
		}
    };
	
	/**
	 * 显示数据至listview
	 * */
	private void showData( )
	{
		stopPulling( );
		if ( mAdapter == null && GatherList.size( ) > 0 )
		{
			publish_btn.setVisibility( View.VISIBLE );
			mAdapter = new GroupGatherAdapter( GroupGatherActivity.this , GatherList , grouprole , groupId );
			mListView.setAdapter( mAdapter );
		}
		else if ( mAdapter != null && GatherList.size( ) > 0 )
		{
			publish_btn.setVisibility( View.VISIBLE );
			mAdapter.gatherList = GatherList;
			mAdapter.notifyDataSetChanged( );
		}
		else if ( mAdapter != null && GatherList.size( ) == 0 )
		{// 数据为空
			mAdapter.gatherList = GatherList;
			mEmptyLayout.showEmpty( );
			publish_btn.setVisibility( View.VISIBLE );
			mAdapter.notifyDataSetChanged( );
			String Uid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
			SharedPreferenceCache.getInstance( GroupGatherActivity.this ).putString( "groupGather" + Uid + groupId ,
					"" );
		}
		else
		{
			mEmptyLayout.showEmpty( );
			publish_btn.setVisibility( View.VISIBLE );
		}
	}
	
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		if ( requestCode == GATHER_DETAIL_CODE )
		{
			if ( resultCode == RESULT_OK )
			{
				handleDetailForResult( data );
			}
		}
		else if ( requestCode == GATHER_REVISE_CODE )
		{
			if ( resultCode == GATHER_REVISE )
			{
				handleReviseForResult( data );
			}
			
		}
	}
	
	/**
	 * 处理从修改页面回来的数据
	 * 
	 * @param data
	 */
	private void handleReviseForResult( Intent data )
	{
		String reviseInfo = data.getStringExtra( "reviseInfo" );
		GroupPublishGatherBean bean = GsonUtil.getInstance( ).getServerBean( reviseInfo ,
				GroupPublishGatherBean.class );
		if ( bean != null )
		{
			int count = mAdapter.gatherList.size( );
			for ( int i = 0 ; i < count ; i++ )
			{
				GatherItemBean detailbean = mAdapter.gatherList.get( i );
				if ( bean.getPartyid( ).equals( String.valueOf( detailbean.party.partyid ) ) )
				{
					detailbean.party.address = bean.getAddress( );
					detailbean.party.content = bean.getContent( );
					detailbean.party.jointime = bean.getJointime( );
					detailbean.party.photos = bean.getPhotoList( );
					
					if ( bean.getCost( ) != null && !bean.getCost( ).trim( ).isEmpty( ) )
					{
						detailbean.party.cost = bean.getCost( );
					}
					if ( bean.getPhone( ) != null && !bean.getPhone( ).trim( ).isEmpty( ) )
					{
						detailbean.party.phone = bean.getPhone( );
					}
					break;
				}
			}
		}
		mAdapter.notifyDataSetChanged( );
	}
	
	/**
	 * 处理从聚会详情返回来的数据
	 * 
	 * @param data
	 */
	private void handleDetailForResult( Intent data )
	{
		String gatherinfo = data.getStringExtra( "gatherInfo" );
		DetailBean bean = GsonUtil.getInstance( ).getServerBean( gatherinfo , DetailBean.class );
		if ( bean != null )
		{
			int count = mAdapter.gatherList.size( );
			for ( int i = 0 ; i < count ; i++ )
			{
				if ( bean.party.partyid == mAdapter.gatherList.get( i ).party.partyid )
				{
					GatherItemBean ItemBean = mAdapter.gatherList.get( i );
					ItemBean.joininfo.curruserjoin = bean.joininfo.curruserjoin;
					ItemBean.joininfo.total = bean.joininfo.total;
					ItemBean.party.address = bean.party.address;
					ItemBean.party.content = bean.party.content;
					ItemBean.party.cost = bean.party.cost;
					ItemBean.party.datetime = bean.party.datetime;
					ItemBean.party.jointime = bean.party.jointime;
					ItemBean.party.phone = bean.party.phone;
					ItemBean.party.status = bean.party.status;
					ItemBean.party.photos = bean.party.photos;
					
					
					if ( bean.joininfo.getjoinUsersList( ) != null
							&& bean.joininfo.getjoinUsersList( ).size( ) > 0 )
					{
						ArrayList<JoinUser> join = ItemBean.joininfo.getjoinUsersList( );
						join.clear( );
						ArrayList< JoinUser > JoinList = bean.joininfo.getjoinUsersList( );
						for ( int j = 0 ; j < JoinList.size( ) ; j++ )
						{
							if ( j > 7  )
							{
								continue;
							}
							JoinUser joinUser = ItemBean.joininfo.new JoinUser( );
							joinUser.icon = JoinList.get( j ).icon;
							joinUser.userid = JoinList.get( j ).userid;
							join.add( joinUser );
						}
					}
					
					break;
				}
			}
		}
		
		mAdapter.notifyDataSetChanged( );
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.title_back :
				finish( );
				
				break;
			case R.id.btn_publish_group :
				
				if ( grouprole != 3 )
				{
					if ( CommonFunction.forbidSay( GroupGatherActivity.this ) == true )
					{
						CommonFunction.toastMsg( GroupGatherActivity.this ,
								getString( R.string.group_getherings_noTalk_power ) );
					}
					else
					{
						Intent i = new Intent( GroupGatherActivity.this , GroupPublishGatherActivity.class );
						i.putExtra( "group_id" , groupId );
						i.putExtra( "group_role" , grouprole );
						startActivity( i );
					}
				}
				else
				{
					/** 非本圈成员 */
					CommonFunction.toastMsg( GroupGatherActivity.this ,
							getString( R.string.group_cannot_publish_gatherings ) );
				}
				
				
				break;
		}
	}
	
	public void performPulling( )
	{
		mListView.setRefreshing( );
	}
	
	public void stopPulling( )
	{
		mListView.onRefreshComplete( );
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		
		if ( isPublishOrDeleteGather == true )
		{
			performPulling( );
			isPublishOrDeleteGather = false;
		}
	}
	
	@Override
	public void onGeneralSuccess(final String result , long flag )
	{
		if ( flag == GET_GATHER_LIST )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				GatherListBean data = GsonUtil.getInstance( ).getServerBean( result ,
						GatherListBean.class );
				
				Message msg = handler.obtainMessage( );
				msg.what = MSG_GET_LIST_DATA;
				msg.obj = data;
				handler.sendMessage( msg );
				if (data!=null && data.partys != null )
				{
					// 缓存数据
					String Uid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
					SharedPreferenceCache.getInstance( GroupGatherActivity.this ).putString(
							"groupGather" + Uid + groupId , result );
				}
				
			}
			else
			{
				stopPulling( );
				mEmptyLayout.showError( );
				ErrorCode.showError( GroupGatherActivity.this , result );
			}
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		if ( flag == GET_GATHER_LIST )
		{
			stopPulling( );
			mEmptyLayout.showError( );
			ErrorCode.toastError( GroupGatherActivity.this , e );
			
		}
	}
	
	
}
