
package net.iaround.ui.group.adapter;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.activity.GroupGatherActivity;
import net.iaround.ui.group.activity.GroupGatherDetail;
import net.iaround.ui.group.activity.GroupGatherRevise;
import net.iaround.ui.group.bean.GatherListBean.joininfoBeen.JoinUser;
import net.iaround.ui.group.bean.GatherListBean.GatherItemBean;
import net.iaround.ui.group.view.DynamicMultiImageView;
import net.iaround.ui.group.view.GatherIconView;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class GroupGatherAdapter extends BaseAdapter implements HttpCallBack
{
	public int groupRole;
	public String groupId;
	private Context mContext;
	public static GatherItemBean bean;
	private int CONTENT_TEXT_SIZE_DP = 16;
	private Dialog progressDialog;// 加载栏
	protected GatherIconView dcvComment;// 聚会已报名成员头像布局
	String leftStr = "【";
	String rightStr = "】";
	/** 取消圈聚会的flag */
	private long CANCEL_GATHER;
	/** 参加圈聚会的flag */
	private long JOIN_GATHER;
	/** 取消圈聚会成功的flag */
	public final int CANCEL_GATHER_SUCCESS = 100;
	/** 参加圈聚会成功的flag */
	public final int JOIN_GATHER_SUCCESS = 200;
	/** 当前用户在该圈中的角色（0创建者；1管理员；2圈成员；3非圈成员） */
	
	private HashMap< String , Integer > operateMap = new HashMap< String , Integer >( );// 临时存储修改的party的ID
	public ArrayList< GatherItemBean > gatherList = new ArrayList< GatherItemBean >( );// 圈聚会列表
	int dip10 = 0;
	long UserId = 0;
	
	public GroupGatherAdapter(Context mContext , ArrayList< GatherItemBean > list ,
                              int grouprole , String groupid )
	{
		this.mContext = mContext;
		gatherList = list;
		groupRole = grouprole;
		groupId = groupid;
		
	}
	
	@Override
	public int getCount( )
	{
		return gatherList != null ? gatherList.size( ) : 0;
	}
	
	@Override
	public Object getItem(int position )
	{
		return gatherList != null ? gatherList.get( position ) : null;
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
		
		final GatherItemBean data = ( GatherItemBean ) getItem( position );
		if ( convertView == null )
		{
			viewHolder = new ViewHolder( );
			convertView = View.inflate( mContext , R.layout.group_gather_publish_view , null );
			initView( viewHolder , convertView , data );
			setItemClickEvent( viewHolder );
			convertView.setTag( viewHolder );
		}
		else
		{
			viewHolder = ( ViewHolder ) convertView.getTag( );
		}
		
		// 头像
		viewHolder.icon.execute(ChatFromType.UNKONW, data.user.convertBaseToUser(),null );

		viewHolder.icon.setTag( data );
		
		// 昵称
		SpannableString spContent = FaceManager.getInstance( mContext ).parseIconForString(
				viewHolder.tvName , mContext , data.user.nickname , CONTENT_TEXT_SIZE_DP );
		viewHolder.tvName.setText( spContent );
		
		// SVIP
		if ( data.user.svip > 0 )
		{
			viewHolder.tvName.setTextColor( mContext.getResources( ).getColor(
					R.color.c_ee4552 ) );
		}
		else
		{
			viewHolder.tvName
					.setTextColor( mContext.getResources( ).getColor( R.color.black ) );
		}
		
		// 年龄
		if ( data.user.age <= 0 )
		{
			viewHolder.tvAgeSex.setText( R.string.unknown );
		}
		else
		{
			viewHolder.tvAgeSex.setText( String.valueOf( data.user.age ) );
		}
		
		// 性别
		String sex = data.user.gender;
		if ( "f".equals( sex ) )
		{
			viewHolder.tvAgeSex.setBackgroundResource( R.drawable.z_common_female_bg );
			viewHolder.tvAgeSex.setCompoundDrawablesWithIntrinsicBounds(
					R.drawable.z_common_female_icon , 0 , 0 , 0 );
		}
		else
		{
			int sexIcon = "m".equals( sex ) ? R.drawable.z_common_male_icon
					: R.drawable.z_common_female_icon;
			viewHolder.tvAgeSex.setCompoundDrawablesWithIntrinsicBounds( sexIcon , 0 , 0 , 0 );
			viewHolder.tvAgeSex
					.setBackgroundResource( "m".equals( sex ) ? R.drawable.z_common_male_bg
							: R.drawable.z_common_female_bg );
		}

		// 时间
		String timeStr = TimeFormat.timeFormat4( mContext , data.party.datetime );
		viewHolder.tvTime.setText( timeStr );
		// 参加时间
		viewHolder.Time_Content.setText( TimeFormat.convertTimeLong2String(
				data.party.jointime , Calendar.MINUTE ) );
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
			viewHolder.mainImageView.setVisibility( View.GONE );//YC 取消展示
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
			viewHolder.dcvComment.initList( data );
		}
		else
		{
			viewHolder.dcvComment.setVisibility( View.GONE );
		}
		
		displayBtn( data , viewHolder );// 根据需求展示按钮形式
		
		
		convertView.setOnClickListener( new OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				GroupGatherDetail.skipToGatherDetail( mContext , groupId , data.party.partyid ,
						GroupGatherActivity.GATHER_DETAIL_CODE );
			}
		} );
		
		return convertView;
	}
	
	/**
	 * 根据需求展示按钮形式
	 * 
	 * @param data
	 * @param viewHolder
	 */
	private void displayBtn( final GatherItemBean data , ViewHolder viewHolder )
	{
		if ( data.party.status == 1 || data.party.status == 2 )// 0-可用，1-取消，2-过期
		{
			viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
			viewHolder.join_btn.setVisibility( View.INVISIBLE );
			viewHolder.overdue_btn.setVisibility( View.VISIBLE );
			
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
				if ( data.user.userid == UserId )
				{
					viewHolder.join_btn.setVisibility( View.GONE );
				}
				else
				{
					viewHolder.join_btn.setVisibility( View.INVISIBLE );
					viewHolder.overdue_btn.setVisibility( View.VISIBLE );
					viewHolder.overdue_btn.setText( mContext
							.getString( R.string.group_alreadyJoin_gatherings ) );
				}
			}
			else
			{
				viewHolder.join_btn.setVisibility( View.VISIBLE );
				viewHolder.overdue_btn.setVisibility( View.GONE );
				if ( data.user.userid == UserId )
				{
					viewHolder.join_btn.setVisibility( View.GONE );
				}
				else
				{
					viewHolder.join_btn.setVisibility( View.VISIBLE );
					viewHolder.revise_or_cancel_ly.setVisibility( View.GONE );
					viewHolder.join_btn.setTag( data );
					viewHolder.join_btn.setClickable( true );
				}
			}
			if ( groupRole == 0 || groupRole == 1 || data.user.userid == UserId )
			// 0圈主；1管理员；2圈成员；3非圈成员
			{
				viewHolder.revise_or_cancel_ly.setVisibility( View.VISIBLE );
				viewHolder.cancel_btn.setTag( data );
				viewHolder.revise_btn.setTag( data );
				viewHolder.cancel_btn.setClickable( true );
				viewHolder.revise_btn.setClickable( true );
			}
			
		}
	}
	
	/**
	 * 设置按钮的点击事件
	 * 
	 * @param viewHolder
	 * @param convertView
	 */
	private void setItemClickEvent( ViewHolder viewHolder )
	{
		viewHolder.icon.setOnClickListener( IconClickListener );
		viewHolder.join_btn.setOnClickListener( joinClickListener );
		viewHolder.cancel_btn.setOnClickListener( cancelClickListener );
		viewHolder.revise_btn.setOnClickListener( reviseClickListener );
	}
	
	/**
	 * 修改该圈聚会
	 * 
	 * @param data
	 */
	private OnClickListener reviseClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			GatherItemBean data = ( GatherItemBean ) v.getTag( );
			setGatherItemBean( data );
			GroupGatherRevise.skipToGatherRevise( mContext , data.party.groupid ,
					data.party.partyid , GroupGatherActivity.GATHER_REVISE_CODE , false );
		}
	};
	
	/**
	 * 参加圈聚会
	 */
	private OnClickListener joinClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			GatherItemBean data = ( GatherItemBean ) v.getTag( );
			if ( data.party.status == 0 )
			{
				showProgressDialog( );
				JOIN_GATHER = GroupHttpProtocol.joinGroupGather( mContext , groupId ,
						data.party.partyid , GroupGatherAdapter.this );
				
				operateMap.put( "joinParty" , data.party.partyid );
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
	};
	
	/**
	 * 取消该圈聚会
	 * 
	 * @param data
	 */
	private OnClickListener cancelClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			final GatherItemBean data = ( GatherItemBean ) v.getTag( );
			DialogUtil.showOKCancelDialog( mContext ,
					mContext.getString( R.string.dialog_title ) ,
					mContext.getString( R.string.group_isCancel_gatherings ) ,
					new OnClickListener( )
					{
						@Override
						public void onClick( View v )
						{
							showProgressDialog( );
							CANCEL_GATHER = GroupHttpProtocol.cancelGroupGather( mContext ,
									groupId , data.party.partyid , GroupGatherAdapter.this );
							operateMap.put( "operateParty" , data.party.partyid );
							
						}
					} , null );
			
		}
	};
	
	private OnClickListener IconClickListener = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			GatherItemBean data = ( GatherItemBean ) v.getTag( );
			User user = new User();
			user.setUid( data.user.userid  );
			user.setIcon( data.user.icon );
			user.setViplevel( data.user.viplevel );
			user.setAge( data.user.age );
			user.setSVip( data.user.svip );
			//gh
//			SpaceOther.launchUser( v.getContext( ) , user.getUid( ) , user , ChatFromType.UNKONW );
		}
	};
	
	
	@SuppressLint( "HandlerLeak" )
	public Handler handler = new Handler( )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case JOIN_GATHER_SUCCESS :// 参加聚会成功
					handleJoinGather( );
					break;
				
				case CANCEL_GATHER_SUCCESS :// 取消聚会成功
					handleCancelGather( );
					break;
			}
		}
	};
	
	/**
	 * 处理取消聚会的返回
	 */
	private void handleCancelGather( )
	{
		hideProgressDialog( );
		int partyid = operateMap.get( "operateParty" );
		
		for ( GatherItemBean data : gatherList )
		{
			if ( data.party.partyid == partyid )
			{
				data.party.status = 1;
			}
		}
		CommonFunction.toastMsg( mContext ,
				mContext.getString( R.string.group_gatherings_CancelSuccess ) );
		notifyDataSetChanged( );
	}
	
	/**
	 * 处理加入聚会的返回
	 */
	private void handleJoinGather( )
	{
		hideProgressDialog( );
		int partyid = operateMap.get( "joinParty" );
		
		for ( GatherItemBean data : gatherList )
		{
			if ( data.party.partyid == partyid )
			{
				data.joininfo.curruserjoin = 1;
				data.joininfo.total = data.joininfo.total + 1;
				ArrayList< JoinUser > join = data.joininfo.getjoinUsersList( );
				if ( join.size( ) < 8 )
				{
					User user = Common.getInstance( ).loginUser;
					JoinUser joinUser = data.joininfo.new JoinUser( );
					joinUser.icon = user.getIcon( );
					joinUser.userid = String.valueOf( user.getUid( ) );
					join.add( joinUser );
				}
			}
		}
		CommonFunction.toastMsg( mContext ,
				mContext.getString( R.string.group_gatherings_JoinSuccess ) );
		notifyDataSetChanged( );
		
	}
	
	
	public void onGeneralSuccess(final String result , long flag )
	{
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
	
	
	public void onGeneralError( int e , long flag )
	{
		if ( flag == CANCEL_GATHER )
		{
			hideProgressDialog( );
			ErrorCode.toastError( mContext , e );
		}
		if ( flag == JOIN_GATHER )
		{
			hideProgressDialog( );
			ErrorCode.toastError( mContext , e );
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
	
	
	private void setGatherItemBean( GatherItemBean data )
	{
		bean = data;
	}
	
	public static GatherItemBean getGatherItemBean( )
	{
		return bean;
	}
	
	private void initView(ViewHolder viewHolder , View convertView , GatherItemBean data )
	{
		viewHolder.icon = (HeadPhotoView) convertView.findViewById( R.id.ivPhoto );
		viewHolder.tvName = (TextView) convertView.findViewById( R.id.tvName );
		viewHolder.tvTime = (TextView) convertView.findViewById( R.id.tvTime );
		viewHolder.tvAgeSex = (TextView) convertView.findViewById( R.id.tvAgeSex );
		viewHolder.tvJoinTime = (TextView) convertView.findViewById( R.id.tvJoinTime );
		viewHolder.tvAddress = (TextView) convertView.findViewById( R.id.tvaddress );
		viewHolder.tvCost = (TextView) convertView.findViewById( R.id.tvCost );
		viewHolder.tvPhone = (TextView) convertView.findViewById( R.id.tvPhone );
		viewHolder.tvContent = (TextView) convertView.findViewById( R.id.tvcontent );
		viewHolder.svip = (ImageView) convertView.findViewById( R.id.svip );
		viewHolder.Time_Content = (TextView) convertView.findViewById( R.id.Time_Content );
		viewHolder.address_content = (TextView) convertView
				.findViewById( R.id.address_content );
		viewHolder.cost_content = (TextView) convertView.findViewById( R.id.cost_content );
		viewHolder.phone_content = (TextView) convertView.findViewById( R.id.phone_content );
		viewHolder.meet_content = (TextView) convertView.findViewById( R.id.meet_content );
		viewHolder.join_btn = (RelativeLayout) convertView.findViewById( R.id.join_btn );
		viewHolder.revise_btn = (RelativeLayout) convertView.findViewById( R.id.revise_btn );
		viewHolder.cancel_btn = (RelativeLayout) convertView.findViewById( R.id.cancel_btn );
		viewHolder.overdue_btn = (Button) convertView.findViewById( R.id.overdue_btn );
		viewHolder.mainImageView = (DynamicMultiImageView) convertView
				.findViewById( R.id.dmivPic );
		viewHolder.dcvComment = ( GatherIconView ) convertView.findViewById( R.id.dcvComment );
		viewHolder.revise_or_cancel_ly = (LinearLayout) convertView
				.findViewById( R.id.revise_or_cancel_ly );
		
		String time = leftStr + mContext.getString( R.string.group_search_time ) + rightStr;
		String address = leftStr + mContext.getString( R.string.group_gathering_address )
				+ rightStr;
		String phone = leftStr + mContext.getString( R.string.group_gathering_phone )
				+ rightStr;
		String cost = leftStr + mContext.getString( R.string.group_gathering_pay ) + rightStr;
		String content = leftStr + mContext.getString( R.string.group_gathering_content )
				+ rightStr;
		viewHolder.tvJoinTime.setText( time );
		viewHolder.tvAddress.setText( address );
		viewHolder.tvPhone.setText( phone );
		viewHolder.tvCost.setText( cost );
		viewHolder.tvContent.setText( content );
		
		dip10 = CommonFunction.dipToPx( mContext , 10 );
		UserId = Common.getInstance( ).loginUser.getUid( );
	}
	
	static class ViewHolder
	{
		HeadPhotoView icon;
		TextView tvName;
		TextView tvTime;
		TextView tvAgeSex;
		TextView tvJoinTime;
		TextView tvAddress;
		TextView tvCost;
		TextView tvPhone;
		TextView tvContent;
		ImageView svip;
		TextView Time_Content;
		TextView address_content;
		TextView cost_content;
		TextView phone_content;
		TextView meet_content;
		RelativeLayout join_btn;
		RelativeLayout revise_btn;
		RelativeLayout cancel_btn;
		Button overdue_btn;
		DynamicMultiImageView mainImageView;
		GatherIconView dcvComment;
		LinearLayout revise_or_cancel_ly;
	}
	
}
