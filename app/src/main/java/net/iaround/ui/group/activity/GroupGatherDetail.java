
package net.iaround.ui.group.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.JsonParseUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.EmptyLayout;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GatherDetailBean;
import net.iaround.ui.group.bean.GatherDetailBean.DetailBean;
import net.iaround.ui.group.bean.GatherListBean.joininfoBeen.JoinUser;
import net.iaround.ui.group.view.DynamicMultiImageView;
import net.iaround.ui.group.view.GatherIconView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.menu.CustomContextMenu;
import net.nostra13.universalimageloader.core.assist.PauseOnScrollListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class GroupGatherDetail extends GroupHandleActivity implements OnClickListener
{
	private String groupId;
	private int partyid;
	private GatherDetailAdapter mAdapter;
	private PullToRefreshListView mListView;
	private EmptyLayout mEmptyLayout;
	public final int MSG_GET_DETAIL_DATA = 100;
	public final int MSG_STOP_PULLING = 200;
	public final int MSG_DEL_DETAIL_DATA = 300;
	/** 请求圈聚会详情的Flag */
	private long GET_GATHER_DETAIL;
	/** 删除圈聚会的Flag */
	private long DELETE_GATHER;
	private int CONTENT_TEXT_SIZE_DP = 16;
	private static DetailBean detailBean;
	private Dialog progressDialog;// 加载栏
	/** 圈管理 */
	private final int MENU_GROUP_MANAGER = 1;
	/** 举报圈子 */
	private final int MENU_REPORT_GROUP = 5;
	/** 取消 */
	private final int MENU_CANCEL = 6;
	/** 取消圈聚会的flag */
	private long CANCEL_GATHER;
	/** 取消圈聚会成功的flag */
	public final int CANCEL_GATHER_SUCCESS = 400;
	/** 参加圈聚会的flag */
	private long JOIN_GATHER;
	/** 参加圈聚会成功的flag */
	public final int JOIN_GATHER_SUCCESS = 500;
	/** 临时存储报名参加的party的viewholder */
	HashMap< String , ViewHolder > JoinMap = new HashMap< String , ViewHolder >( );
	private static DetailBean toRevisebean;
	public static boolean isInDetailReviseGather = false;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.group_gather_detail );
		
		groupId = getIntent( ).getStringExtra( "group_id" );
		partyid = getIntent( ).getIntExtra( "partyid" , 0 );
		initViews( );
		setListeners( );
		performPulling( );
	}
	
	/**
	 * 跳转到聚会详情
	 * 
	 * @param context
	 * @param groupId
	 * @param partyid
	 */
	public static void launch(Context context , String groupId , int partyid )
	{
		Intent intent = new Intent( context , GroupGatherDetail.class );
		intent.putExtra( "group_id" , groupId );
		intent.putExtra( "partyid" , partyid );
		context.startActivity( intent );
	}
	
	/**
	 * 跳转到聚会详情
	 * 
	 * @param context
	 * @param groupId
	 * @param partyid
	 */
	public static void skipToGatherDetail(Context context , String groupId , int partyid ,
                                          int requestCode )
	{
		Intent intent = new Intent( context , GroupGatherDetail.class );
		intent.putExtra( "group_id" , groupId );
		intent.putExtra( "partyid" , partyid );
		( (Activity) context ).startActivityForResult( intent , requestCode );
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
		//gh
		TextView titleName = (TextView) findViewById( R.id.title_name );
		titleName.setText( getString( R.string.group_inf_group_gatherings ) );
		ImageView titleBack = (ImageView) findViewById( R.id.title_back );
		titleBack.setOnClickListener( this );
		ImageView titleRight = (ImageView) findViewById( R.id.title_right_img );
		titleRight.setOnClickListener( this );
		
		mListView = (PullToRefreshListView) findViewById( R.id.groupDetail_List );
		mListView.setMode( Mode.PULL_FROM_START );
//		mListView.setOnScrollListener( listener );
		mListView.setPullToRefreshOverScrollEnabled( false );// 禁止滚动过头
		mListView.getRefreshableView( ).setDividerHeight( 0 );
		mListView.getRefreshableView( ).setSelector( R.drawable.info_bg_center_selector );
		mListView.getRefreshableView( ).setFastScrollEnabled( false );
		
		mEmptyLayout = new EmptyLayout( mContext , mListView.getRefreshableView( ) );
		mEmptyLayout.setEmptyMessage( getString( R.string.invisible_get_data_fail ) );
		
	}
	
	/**
	 * @Title: setListeners
	 * @Description: 初始化监听器
	 */
	private void setListeners( )
	{
		// 设置当下拉和上拉刷新时的操作
		mListView.setOnRefreshListener( new OnRefreshListener< ListView >( )
		{
			@Override
			public void onRefresh( PullToRefreshBase< ListView > refreshView )
			{
				requestData( );
			}
		} );
	}
	
	protected void requestData( )
	{
		mEmptyLayout.showLoading( );
		GET_GATHER_DETAIL = GroupHttpProtocol.getGroupGatherDetail( mContext , groupId , partyid ,
				GroupGatherDetail.this );
		
	}
	
	// 显示数据
	public void showData( )
	{
		stopPulling( );
		if ( mAdapter == null )
		{
			mEmptyLayout.showLoading( );
			mAdapter = new GatherDetailAdapter( detailBean );
			mListView.getRefreshableView( ).setAdapter( mAdapter );
		}
		else if ( mAdapter != null && detailBean != null )
		{
			mAdapter.bean = detailBean;
			mAdapter.notifyDataSetChanged( );
		}
		else if ( detailBean == null )
		{// 数据为空
			mEmptyLayout.showEmpty( );
		}
		
		
	}
	
	
	
	private class GatherDetailAdapter extends BaseAdapter
	{
		private DetailBean bean;
		
		public GatherDetailAdapter( DetailBean data )
		{
			bean = data;
		}
		
		@Override
		public int getCount( )
		{
			return bean != null ? 1 : 0;
		}
		
		@Override
		public Object getItem(int position )
		{
			return bean != null ? bean : null;
			
		}
		
		@Override
		public long getItemId( int position )
		{
			return position;
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			ViewHolder viewHolder = null;
			if ( convertView == null )
			{
				viewHolder = new ViewHolder( );
				convertView = View.inflate( mContext , R.layout.group_gather_publish_view , null );
				
				viewHolder.icon = (HeadPhotoView) convertView.findViewById( R.id.ivPhoto );
				viewHolder.tvName = (TextView) convertView.findViewById( R.id.tvName );
				viewHolder.tvTime = (TextView) convertView.findViewById( R.id.tvTime );
				viewHolder.tvAgeSex = (TextView) convertView.findViewById( R.id.tvAgeSex );
				viewHolder.top = (TextView) convertView.findViewById( R.id.tvTop );
				
				viewHolder.tvJoinTime = (TextView) convertView.findViewById( R.id.tvJoinTime );
				viewHolder.tvAddress = (TextView) convertView.findViewById( R.id.tvaddress );
				viewHolder.tvCost = (TextView) convertView.findViewById( R.id.tvCost );
				viewHolder.tvPhone = (TextView) convertView.findViewById( R.id.tvPhone );
				viewHolder.tvContent = (TextView) convertView.findViewById( R.id.tvcontent );
				
				viewHolder.Time_Content = (TextView) convertView.findViewById( R.id.Time_Content );
				viewHolder.address_content = (TextView) convertView
						.findViewById( R.id.address_content );
				viewHolder.cost_content = (TextView) convertView.findViewById( R.id.cost_content );
				viewHolder.phone_content = (TextView) convertView
						.findViewById( R.id.phone_content );
				viewHolder.meet_content = (TextView) convertView.findViewById( R.id.meet_content );
				viewHolder.join_btn = (RelativeLayout) convertView.findViewById( R.id.join_btn );
				viewHolder.revise_btn = (RelativeLayout) convertView
						.findViewById( R.id.revise_btn );
				viewHolder.cancel_btn = (RelativeLayout) convertView
						.findViewById( R.id.cancel_btn );
				viewHolder.overdue_btn = (Button) convertView.findViewById( R.id.overdue_btn );
				viewHolder.mainImageView = (DynamicMultiImageView) convertView
						.findViewById( R.id.dmivPic );
				viewHolder.dcvComment = (GatherIconView) convertView
						.findViewById( R.id.dcvComment );
				viewHolder.revise_or_cancel_ly = (LinearLayout) convertView
						.findViewById( R.id.revise_or_cancel_ly );
				
				convertView.setTag( viewHolder );
			}
			else
			{
				viewHolder = ( ViewHolder ) convertView.getTag( );
			}
			
			// 初始化
			viewHolder.tvName.setText( "" );
			viewHolder.tvTime.setText( "" );
			viewHolder.tvAgeSex.setText( "" );
			viewHolder.Time_Content.setText( "" );
			viewHolder.address_content.setText( "" );
			viewHolder.cost_content.setText( "" );
			viewHolder.phone_content.setText( "" );
			viewHolder.meet_content.setText( "" );
			viewHolder.join_btn.setVisibility( View.GONE );
			viewHolder.overdue_btn.setVisibility( View.GONE );
			viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
			viewHolder.mainImageView.setVisibility( View.GONE );
			viewHolder.dcvComment.setVisibility( View.GONE );
			viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
			viewHolder.top.setText( mContext.getString( R.string.group_I_publish_gather ) );
			String time = "【" + mContext.getString( R.string.group_search_time ) + "】";
			String address = "【" + mContext.getString( R.string.group_gathering_address ) + "】";
			String phone = "【" + mContext.getString( R.string.group_gathering_phone ) + "】";
			String Cost = "【" + mContext.getString( R.string.group_gathering_pay ) + "】";
			String content = "【" + mContext.getString( R.string.group_gathering_content ) + "】";
			viewHolder.tvJoinTime.setText( time );
			viewHolder.tvAddress.setText( address );
			viewHolder.tvPhone.setText( phone );
			viewHolder.tvCost.setText( Cost );
			viewHolder.tvContent.setText( content );
			
			
			final DetailBean data = ( DetailBean ) getItem( position );
			
			// 头像
			int i = CommonFunction.dipToPx( mContext , 10 );

			viewHolder.icon.execute(ChatFromType.UNKONW,data.user.convertBaseToUser(),null  );
			
			viewHolder.icon.executeRoundFrame( NetImageView.NEARUSER_DEFAULT_FACE , data.user.icon  );
			
			// 昵称
			SpannableString spContent = FaceManager.getInstance( mContext ).parseIconForString(
					viewHolder.tvName , mContext , data.user.nickname , CONTENT_TEXT_SIZE_DP );
			viewHolder.tvName.setText( spContent );
			// 年龄、性别
			viewHolder.tvAgeSex.setText( String.valueOf( data.user.age ) );
			if ( data.user.gender != null && data.user.gender.equals( "f" ) )
			{
				viewHolder.tvAgeSex.setBackgroundResource( R.drawable.sex_bg_gril );
				viewHolder.tvAgeSex.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 ,
						R.drawable.sex_icon_girl , 0 );
			}
			else
			{
				viewHolder.tvAgeSex.setBackgroundResource( R.drawable.sex_bg_boy );
				viewHolder.tvAgeSex.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 ,
						R.drawable.sex_icon_boy , 0 );
			}
			// 时间
			String timeStr = TimeFormat.timeFormat4( mContext , data.party.datetime );
			viewHolder.tvTime.setText( timeStr );
			// 参加时间
			viewHolder.Time_Content.setText( TimeFormat.convertTimeLong2String( data.party.jointime ,
					Calendar.MINUTE ) );
			// 聚会地点
			viewHolder.address_content.setText( FaceManager.getInstance( mContext )
					.parseIconForString( viewHolder.address_content , mContext ,
							data.party.address , CONTENT_TEXT_SIZE_DP ) );
			
			// 聚会费用
			if ( data.party.cost.isEmpty( ) )
			{
				viewHolder.cost_content.setVisibility( View.GONE );
				viewHolder.tvCost.setVisibility( View.GONE );
			}
			else
			{
				viewHolder.cost_content.setVisibility( View.VISIBLE );
				viewHolder.tvCost.setVisibility( View.VISIBLE );
				viewHolder.cost_content.setText( FaceManager.getInstance( mContext )
						.parseIconForString( viewHolder.cost_content , mContext , data.party.cost ,
								CONTENT_TEXT_SIZE_DP ) );
				
			}
			// 聚会电话
			if ( data.party.phone.isEmpty( ) )
			{
				viewHolder.phone_content.setVisibility( View.GONE );
				viewHolder.tvPhone.setVisibility( View.GONE );
			}
			else
			{
				viewHolder.phone_content.setVisibility( View.VISIBLE );
				viewHolder.tvPhone.setVisibility( View.VISIBLE );
				viewHolder.phone_content.setText( FaceManager.getInstance( mContext )
						.parseIconForString( viewHolder.phone_content , mContext ,
								data.party.phone , CONTENT_TEXT_SIZE_DP ) );
			}
			
			// 聚会内容
			if ( !CommonFunction.isEmptyOrNullStr( data.party.content ) )
			{
				viewHolder.meet_content.setText( FaceManager.getInstance( mContext )
						.parseIconForString( viewHolder.meet_content , mContext ,
								data.party.content , CONTENT_TEXT_SIZE_DP ) );
			}
			// 聚会照片
			if ( data.party.getPhotoList( ).size( ) > 0 )
			{
				viewHolder.mainImageView.setVisibility( View.GONE );//YC  取消展示
				viewHolder.mainImageView.setList( data.party.getPhotoList( ) );
			}
			else
			{
				viewHolder.mainImageView.setVisibility( View.GONE );
			}
			// 聚会已参加成员
			if ( data.joininfo.total > 0 )
			{
				viewHolder.dcvComment.setVisibility( View.GONE );//YC 取消展示
				viewHolder.dcvComment.initDetail( data );
			}
			else
			{
				viewHolder.dcvComment.setVisibility( View.GONE );
			}
			
			displayBtn( data , viewHolder );// 根据需求展示按钮形式
			

			
			return convertView;
		}
	}
	
	/**
	 * 根据需求展示按钮形式
	 * 
	 * @param data
	 * @param viewHolder
	 */
	private void displayBtn( final DetailBean data , ViewHolder viewHolder )
	{
		if ( data.party.status == 1 || data.party.status == 2 )// 0-可用，1-取消，2-过期
		{
			viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
			viewHolder.join_btn.setVisibility( View.INVISIBLE );
			viewHolder.overdue_btn.setVisibility( View.VISIBLE );
			viewHolder.join_btn.setClickable( false );
			viewHolder.overdue_btn.setClickable( false );
			
			if ( data.party.status == 1 )
			{
				viewHolder.overdue_btn.setText( mContext
						.getString( R.string.group_alreadycancel_gatherings ) );
				
			}
			else if ( data.party.status == 2 )
			{
				viewHolder.overdue_btn.setText( mContext
						.getString( R.string.group_alreadyoverdue_gatherings ) );
			}
		}
		else
		{
			viewHolder.overdue_btn.setVisibility( View.GONE );
			
			if ( data.joininfo.curruserjoin == 1 )// 当前用户是否参加（0-否，1-是）
			{
				if ( data.user.userid == Common.getInstance( ).loginUser.getUid( ) )
				{
					viewHolder.join_btn.setVisibility( View.GONE );
				}
				else
				{
					viewHolder.join_btn.setVisibility( View.INVISIBLE );
					viewHolder.overdue_btn.setVisibility( View.VISIBLE );
					viewHolder.overdue_btn.setText( mContext
							.getString( R.string.group_alreadyJoin_gatherings ) );
					viewHolder.overdue_btn.setClickable( false );
				}
			}
			else
			{
				viewHolder.join_btn.setVisibility( View.VISIBLE );
				viewHolder.overdue_btn.setVisibility( View.GONE );
				if ( data.user.userid == Common.getInstance( ).loginUser.getUid( ) )
				{
					viewHolder.join_btn.setVisibility( View.GONE );
				}
				else
				{
					viewHolder.join_btn.setVisibility( View.VISIBLE );
					viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
					viewHolder.join_btn.setTag( viewHolder );
					viewHolder.join_btn.setOnClickListener( new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{// 参加圈聚会
							final ViewHolder viewHolder = ( ViewHolder ) v.getTag( );
							clickJoinGatherBtn( data , viewHolder );
						}
					} );
					
				}
			}
			if ( data.grouprole == 0 || data.grouprole == 1
					|| data.user.userid == Common.getInstance( ).loginUser.getUid( ) )
			{
				viewHolder.revise_or_cancel_ly.setVisibility( View.VISIBLE );
				viewHolder.cancel_btn.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{// 取消圈聚会
						cancelGather( data );
					}
				} );
				
				viewHolder.revise_btn.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{// 修改圈聚会
						setDetailBean( data );
						GroupGatherRevise.skipToGatherRevise( mContext , data.party.groupid ,
								data.party.partyid , true );
					}
				} );
			}
			
		}
	}
	
	/**
	 * 执行点击加入圈聚会的操作
	 * 
	 * @param data
	 * @param viewHolder
	 */
	private void clickJoinGatherBtn( DetailBean data , ViewHolder viewHolder )
	{
		
		if ( data.party.status == 0 )
		{
			showProgressDialog( );
			JOIN_GATHER = GroupHttpProtocol.joinGroupGather( mContext , groupId ,
					data.party.partyid , GroupGatherDetail.this );
			
			JoinMap.put( "viewholder" , viewHolder );
		}
		else if ( data.party.status == 1 )
		{
			CommonFunction.toastMsg( mContext ,
					mContext.getString( R.string.group_alreadycancel_gatherings ) );
			
		}
		else if ( data.party.status == 2 )
		{
			CommonFunction.toastMsg( mContext ,
					mContext.getString( R.string.group_alreadyoverdue_gatherings ) );
		}
		
	}
	
	/**
	 * 取消该圈聚会
	 * 
	 * @param data
	 */
	private void cancelGather( final DetailBean data )
	{
		DialogUtil.showOKCancelDialog( mContext , getString( R.string.dialog_title ) ,
				mContext.getString( R.string.group_isCancel_gatherings ) , new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						showProgressDialog( );
						CANCEL_GATHER = GroupHttpProtocol.cancelGroupGather( mContext , groupId ,
								data.party.partyid , GroupGatherDetail.this );
						
					}
				} , null );
	}
	
	
	/**
	 * 参加聚会成功
	 */
	private void joinGatherSuccess( )
	{
		hideProgressDialog( );
		CommonFunction.toastMsg( mContext ,
				mContext.getString( R.string.group_gatherings_JoinSuccess ) );
		
		if ( detailBean.party.partyid == partyid )
		{
			detailBean.joininfo.curruserjoin = 1;
			detailBean.joininfo.total = detailBean.joininfo.total + 1;
			User user = Common.getInstance( ).loginUser;
			JoinUser joinUser = detailBean.joininfo.new JoinUser( );
			joinUser.icon = user.getIcon( );
			joinUser.userid = String.valueOf( user.getUid( ) );
			ArrayList< JoinUser > join = detailBean.joininfo.getjoinUsersList( );
			join.add( joinUser );
			ViewHolder viewHolder = JoinMap.get( "viewholder" );
			viewHolder.dcvComment.initDetail( detailBean );
		}
		
		mAdapter.notifyDataSetChanged( );
		
	}
	
	/** 删除聚会 */
	OnClickListener deleteGather = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			if ( detailBean.grouprole == 0 || detailBean.grouprole == 1 )
			{
				DialogUtil.showOKCancelDialog( mContext ,
						getString( R.string.group_cant_add_member_title_without_verify ) ,
						getString( R.string.group_isDelete_gatherings ) , new OnClickListener( )
						{
							
							@Override
							public void onClick( View v )
							{
								showProgressDialog( );
								DELETE_GATHER = GroupHttpProtocol.DeleteGroupGather( mContext ,
										groupId , partyid , GroupGatherDetail.this );
							}
						} , null );
			}
			else
			{
				CommonFunction.toastMsg( mContext ,
						mContext.getString( R.string.group_gatherings_noPower ) );
			}
			
		}
	};
	
	/** 举报圈聚会 */
	OnClickListener reportClick = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			// 举报圈聚会
//			Intent intent = new Intent( mActivity , SpaceReport.class );
//			intent.putExtra( "targetId" , String.valueOf( partyid ) );
//			intent.putExtra( "targetType" , 9 );
//			startActivity( intent );
			//gh
		}
	};
	@SuppressLint( "HandlerLeak" )
	public Handler handler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case MSG_GET_DETAIL_DATA :// 获取聚会数据成功
					hideProgressDialog( );
					GatherDetailBean bean = ( GatherDetailBean ) msg.obj;
					if ( bean.partyinfo.party == null )
					{
						mEmptyLayout.showEmpty( );
					}
					
					detailBean = bean.partyinfo;
					showData( );
					
					break;
				
				case MSG_DEL_DETAIL_DATA :// 删除聚会成功
					hideProgressDialog( );
					CommonFunction.toastMsg( mContext ,
							mContext.getString( R.string.group_gatherings_DeleteSuccess ) );
					GroupGatherActivity.isPublishOrDeleteGather = true;
					isInDetailReviseGather = true;
					finish( );
					break;
				
				case JOIN_GATHER_SUCCESS :// 参加聚会成功
					joinGatherSuccess( );
					break;
				
				case CANCEL_GATHER_SUCCESS :// 取消聚会成功
					hideProgressDialog( );
					CommonFunction.toastMsg( mContext ,
							mContext.getString( R.string.group_gatherings_CancelSuccess ) );
					if ( detailBean.party.partyid == partyid )
					{
						detailBean.party.status = 1;
					}
					mAdapter.notifyDataSetChanged( );
					break;
			}
		}
	};
	
	// 点击返回按键
	private void backBtnClick( )
	{
		Intent data = new Intent( );
		String gatherInfo = GsonUtil.getInstance( ).getStringFromJsonObject( detailBean );
		data.putExtra( "gatherInfo" , gatherInfo );
		setResult( RESULT_OK , data );
		finish( );
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
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.title_back :
				backBtnClick( );
				break;
			case R.id.title_right_img :
				// 右上角"更多"按钮点击
				if ( detailBean != null )
				{
					showContextMenu( v );
				}
				break;
		
		}
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		// TODO Auto-generated method stub
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			backBtnClick( );
		}
		return super.onKeyDown( keyCode , event );
	}
	
	private void showContextMenu( View view )
	{
		if ( detailBean.grouprole >= 0 )
		{
			CustomContextMenu menu = new CustomContextMenu( mContext );
			
			if ( detailBean.grouprole == 0 || detailBean.grouprole == 1 )
			{
				// 圈主或管理员
				menu.addMenuItem( MENU_REPORT_GROUP ,
						getString( R.string.space_other_menu_item_report ) , reportClick , false );
				menu.addMenuItem( MENU_GROUP_MANAGER ,
						getString( R.string.group_gatherings_Delete ) , deleteGather , true );
			}
			else if ( detailBean.grouprole == 2 )
			{
				// 普通用户
				menu.addMenuItem( MENU_REPORT_GROUP ,
						getString( R.string.space_other_menu_item_report ) , reportClick , false );
				
			}
			
			menu.showMenu( view );
		}
		
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		if ( isInDetailReviseGather == true )
		{
			mListView.setRefreshing( );
			isInDetailReviseGather = false;
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu , View v , ContextMenuInfo menuInfo )
	{
		if ( detailBean.grouprole >= 0 )
		{
			menu.add( 0 , MENU_REPORT_GROUP , 1 ,
					mContext.getString( R.string.space_other_menu_item_report ) );
			if ( detailBean.grouprole == 0 || detailBean.grouprole == 1 )
			{
				// 圈主或管理员
				menu.add( 0 , MENU_GROUP_MANAGER , 2 ,
						mContext.getString( R.string.group_gatherings_Delete ) );
			}
			
			menu.add( 0 , MENU_CANCEL , 3 , getString( R.string.cancel ) );
		}
	}
	
	// 显示加载框
	private void showProgressDialog( )
	{
		if ( progressDialog == null )
		{
			progressDialog = DialogUtil.showProgressDialog( mContext , R.string.dialog_title ,
					R.string.content_is_loading , null );
			progressDialog.setCancelable( false );
		}
		
		progressDialog.show( );
	}
	
	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( progressDialog != null )
		{
			progressDialog.hide( );
		}
	}
	
	@Override
	protected void onDestroy( )
	{
		super.onDestroy( );
		if ( progressDialog != null )
		{
			progressDialog.dismiss( );
			progressDialog = null;
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( flag == GET_GATHER_DETAIL )
		{
			hideProgressDialog( );
			stopPulling( );
			mEmptyLayout.showError( );
			ErrorCode.toastError( mContext , e );
			
		}
		else
		{
			hideProgressDialog( );
			ErrorCode.toastError( mContext , e );
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		
		if ( flag == GET_GATHER_DETAIL )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				GatherDetailBean data = GsonUtil.getInstance( ).getServerBean( result ,
						GatherDetailBean.class );
				if ( data != null && data.partyinfo != null )
				{
					Message msg = handler.obtainMessage( );
					msg.what = MSG_GET_DETAIL_DATA;
					msg.obj = data;
					handler.sendMessage( msg );
				}
				else
				{
					hideProgressDialog( );
					mEmptyLayout.showEmpty( );
				}
			}
			else
			{
				hideProgressDialog( );
				stopPulling( );
				JSONObject json = null;
				try
				{
					json = new JSONObject( result );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
				}
				int error = JsonParseUtil.getInt( json , "error" , -1 );
				if ( error == 6054 )
				{
					GroupGatherActivity.isPublishOrDeleteGather = true;
					CommonFunction.toastMsg( mContext , getString( R.string.gather_non_existent ) );
					finish( );
				}
				else
				{
					ErrorCode.showError( mContext , result );
				}
				
				
				
			}
		}
		if ( flag == DELETE_GATHER )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				Message msg = handler.obtainMessage( );
				msg.what = MSG_DEL_DETAIL_DATA;
				handler.sendMessage( msg );
			}
			else
			{
				hideProgressDialog( );
				ErrorCode.showError( mContext , result );
			}
		}
		if ( flag == JOIN_GATHER )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				Message msg = handler.obtainMessage( );
				msg.what = JOIN_GATHER_SUCCESS;
				handler.sendMessage( msg );
			}
			else
			{
				hideProgressDialog( );
				ErrorCode.showError( mContext , result );
				
			}
		}
		if ( flag == CANCEL_GATHER )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				Message msg = handler.obtainMessage( );
				msg.what = CANCEL_GATHER_SUCCESS;
				handler.sendMessage( msg );
			}
			else
			{
				hideProgressDialog( );
				ErrorCode.showError( mContext , result );
			}
		}
		
	}
	
	static class ViewHolder
	{
		HeadPhotoView icon;
		TextView tvName;
		TextView tvTime;
		TextView tvAgeSex;
		TextView top;
		TextView tvJoinTime;
		TextView tvAddress;
		TextView tvCost;
		TextView tvPhone;
		TextView tvContent;
		TextView Time_Content;
		TextView address_content;
		TextView cost_content;
		TextView phone_content;
		TextView meet_content;
		RelativeLayout join_btn;
		RelativeLayout revise_btn;
		Button overdue_btn;
		RelativeLayout cancel_btn;
		DynamicMultiImageView mainImageView;
		GatherIconView dcvComment;
		LinearLayout revise_or_cancel_ly;
	}
	
	@SuppressWarnings( "static-access" )
	public void setDetailBean( DetailBean data )
	{
		this.toRevisebean = data;
	}
	
	public static DetailBean getDetailBean( )
	{
		return toRevisebean;
	}
	
	@Override
	protected String getGroupId( )
	{
		return null;
	}
}
