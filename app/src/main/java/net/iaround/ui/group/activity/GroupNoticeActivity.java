
package net.iaround.ui.group.activity;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.GroupNoticeType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.menu.CustomContextMenu;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @Description: 圈通知列表
 * @author tanzy
 * @date 2015-4-23
 */
public class GroupNoticeActivity extends BaseFragmentActivity implements OnClickListener,HttpCallBack
{
	private ImageView ivLeft,ivRight;

	private final int DELETE_ALL_MESSAGE = 1;
	private final static int MSG_NOTIFY_DATA_CHANGE = 1000;
	public final static int MSG_INITDATA = 1001;
	
	private ListView listView;
	
	private DataAdapter adapter;
	private ArrayList< GroupNoticeBean > dataList = new ArrayList< GroupNoticeBean >( );
	private HashMap< Long , GroupNoticeBean > allowJoinMap = new HashMap< Long , GroupNoticeBean >( );
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.group_notice_activity );
//		mContext = this;
		initView( );
	}
	
	@Override
	protected void onResume( )
	{
		super.onResume( );
		initData( );
	}
	
	private void initView( )
	{
		ivLeft = (ImageView) findViewById(R.id.iv_left);
		ivRight = (ImageView) findViewById(R.id.iv_right);

		ivRight.setImageResource(R.drawable.title_more);
		ivLeft.setImageResource(R.drawable.title_back);
		( (TextView) findViewById( R.id.tv_title ) ).setText( R.string.group_notice_msg );
		listView = (ListView) findViewById( R.id.notice_listview );
		listView.setDivider( null );

		ivLeft.setOnClickListener(this);
		ivRight.setOnClickListener(this);
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById(R.id.fl_right).setOnClickListener(this);
	}
	
	public void initData( )
	{
		dataList = GroupAffairModel.getInstance( ).getNoticeList( GroupNoticeActivity.this );
		if ( dataList == null || dataList.size( ) <= 0 )
		{
			listView.setVisibility( View.INVISIBLE );
			findViewById( R.id.empty_text ).setVisibility( View.VISIBLE );
		}
		else
		{
			listView.setVisibility( View.VISIBLE );
			findViewById( R.id.empty_text ).setVisibility( View.GONE );
			
			if ( adapter == null )
				adapter = new DataAdapter( );
			listView.setAdapter( adapter );
			handler.sendEmptyMessage( MSG_NOTIFY_DATA_CHANGE );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left :
				finish( );
				break;
			case R.id.fl_right:
			case R.id.iv_right:
			{
//				CustomContextMenu menu = new CustomContextMenu( mContext );
//
//				menu.addMenuItem( DELETE_ALL_MESSAGE ,
//						getString( R.string.delete_all_group_notic ) , deleleAllNotice , false );
//				menu.showMenu( v );
				CustomContextDialog customContextDialog = new CustomContextDialog(this,9);
				customContextDialog.setListenner(deleleAllNotice);
				customContextDialog.show();
			}
				break;
		
		}
	}
	
	public Handler handler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case MSG_NOTIFY_DATA_CHANGE :
				{
					adapter.notifyDataSetChanged( );
				}
					break;
				case MSG_INITDATA :
				{
					initData( );
				}
					break;
				default :
					break;
			}
		}
    };
	
	OnClickListener deleleAllNotice = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{
			if ((int)v.getTag() == 1) {
				CommonFunction.log("sherlock", "have click delete all");
				GroupAffairModel.getInstance().deleteUserAllNotice(GroupNoticeActivity.this);
				handler.sendEmptyMessage(MSG_INITDATA);
			}
		}
	};
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( allowJoinMap.containsKey( flag ) )
		{
			if ( Constant.isSuccess( result ) )
			{
				GroupNoticeBean data = allowJoinMap.get( flag );
				GroupAffairModel.getInstance( ).agreeRejectUpadteData( GroupNoticeActivity.this , data ,
						GroupNoticeType.ALLOW_JOIN_GROUP );
			}
			else
			{
				ErrorCode.showError( GroupNoticeActivity.this , result );
			}
			handler.sendEmptyMessage( MSG_INITDATA );
			allowJoinMap.remove( flag );
		}
	}


    @Override
	public void onGeneralError( int e , long flag )
	{
		ErrorCode.toastError( GroupNoticeActivity.this , e );
		if ( allowJoinMap.containsKey( flag ) )
			allowJoinMap.remove( flag );
	}
	
	public class DataAdapter extends BaseAdapter
	{
		@Override
		public int getCount( )
		{
			return dataList.size( );
		}
		
		@Override
		public GroupNoticeBean getItem(int position )
		{
			return dataList.get( position );
		}
		
		@Override
		public long getItemId( int position )
		{
			return 0;
		}
		
		@Override
		public View getView(int position , View convertView , ViewGroup parent )
		{
			if ( convertView == null )
			{
				LayoutInflater inflater = (LayoutInflater) GroupNoticeActivity.this
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				convertView = inflater.inflate( R.layout.group_notice_item , parent , false );
				
				ViewHolder holder = new ViewHolder( );
				holder.icon = (HeadPhotoView) convertView.findViewById( R.id.friend_icon );
				holder.noticeName = (TextView) convertView.findViewById( R.id.notice_name );
				holder.noticeContent = (TextView) convertView
						.findViewById( R.id.notice_content );
				holder.noticeDealer = (TextView) convertView
						.findViewById( R.id.notice_dealer );
				holder.btn = (RelativeLayout) convertView
						.findViewById( R.id.notice_allow_reject );
				holder.btnContent = (TextView) convertView.findViewById( R.id.btn_content );
				holder.line = convertView.findViewById( R.id.divider_view );
				
				convertView.setTag( holder );
			}
			
			ViewHolder holder = ( ViewHolder ) convertView.getTag( );
			GroupNoticeBean bean = getItem( position );
			
			// 头像和标题
			if ( bean.type <= 4 )
			{// 申请，同意，拒绝，退出显示被处理这名字
				holder.noticeName.setText( FaceManager.getInstance( GroupNoticeActivity.this )
						.parseIconForString( holder.noticeName , GroupNoticeActivity.this ,
								bean.targetuser.nickname , 20 ) );
				holder.icon.execute( ChatFromType.UNKONW,bean.targetuser.convertBaseToUser(),null);
			}
			else
			{// 被踢，设置管理员，取消管理员显示圈名
				holder.noticeName.setText( FaceManager.getInstance( GroupNoticeActivity.this )
						.parseIconForString( holder.noticeName , GroupNoticeActivity.this , bean.groupname ,
								20 ) );
				holder.icon.execute( ChatFromType.UNKONW, bean.targetuser.convertBaseToUser( ),
					null );
			}
			
			// 正文
			String content = "";
			switch ( bean.type )
			{
				case GroupNoticeType.APPLY_JOIN_GROUP :
				case GroupNoticeType.ALLOW_JOIN_GROUP :
				case GroupNoticeType.REJECT_JOIN_GROUP :
				{
					content = GroupNoticeActivity.this.getString( R.string.apply_join_group ) + bean.groupname;
				}
					break;
				case GroupNoticeType.QUIT_GROUP :
				{
					content = GroupNoticeActivity.this.getString( R.string.quit_group , "" , bean.groupname );
				}
					break;
				case GroupNoticeType.KICK_OUT_GROUP :
				{
					if ( bean.targetuser.userid != Common.getInstance( ).loginUser.getUid( ) )
						content = GroupNoticeActivity.this.getString( R.string.kick_sb_out_group ,
								bean.targetuser.nickname );
					else
						content = GroupNoticeActivity.this.getString( R.string.kick_you_out );
				}
					break;
				case GroupNoticeType.SET_MANAGER :
				{
					if ( bean.targetuser.userid != Common.getInstance( ).loginUser.getUid( ) )
						content = GroupNoticeActivity.this.getString( R.string.have_set_manager ,
								bean.targetuser.nickname );
					else
						content = GroupNoticeActivity.this.getString( R.string.have_set_you_manager );
				}
					break;
				case GroupNoticeType.REMOVE_MANAGER :
				{
					if ( bean.targetuser.userid != Common.getInstance( ).loginUser.getUid( ) )
						content = GroupNoticeActivity.this.getString( R.string.remove_manager , "" ,
								bean.targetuser.nickname );
					else
						content = GroupNoticeActivity.this.getString( R.string.remove_you_manager , "" );
				}
					break;
				default :
				{
					CommonFunction
							.log( "sherlock" , "group notice error type == " + bean.type );
					content = GroupNoticeActivity.this.getString( R.string.low_version );
				}
					break;
			}
			holder.noticeContent.setText( FaceManager.getInstance( GroupNoticeActivity.this )
					.parseIconForString( holder.noticeContent , GroupNoticeActivity.this , content , 12 ) );
			
			// 处理者
			switch ( bean.type )
			{
				case GroupNoticeType.APPLY_JOIN_GROUP :
				case GroupNoticeType.REJECT_JOIN_GROUP :
				case GroupNoticeType.QUIT_GROUP :
					holder.noticeDealer.setVisibility( View.GONE );
					break;
				case GroupNoticeType.ALLOW_JOIN_GROUP :
				case GroupNoticeType.KICK_OUT_GROUP :
				case GroupNoticeType.SET_MANAGER :
				case GroupNoticeType.REMOVE_MANAGER :
				{
					if (bean.dealuser == null)
						break;
					holder.noticeDealer.setVisibility( View.VISIBLE );
					holder.noticeDealer.setText( FaceManager.getInstance( GroupNoticeActivity.this )
							.parseIconForString(
									holder.noticeDealer ,
									GroupNoticeActivity.this ,
									GroupNoticeActivity.this.getString( R.string.group_notice_dealer ) + ":"
											+ bean.dealuser.nickname , 12 ) );
				}
					break;
				default :
				{
					CommonFunction.log( "sherlock" , "unkonw type ==" + bean.type );
					holder.noticeDealer.setVisibility( View.INVISIBLE );
				}
					
					break;
			}
			
			// 同意按钮、已同意、已拒绝
			if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP )
			{
				holder.btn.setVisibility( View.VISIBLE );
//				holder.btn.setBackgroundResource( R.drawable.z_fans_btn_red );
//				holder.btnContent.setCompoundDrawablesWithIntrinsicBounds(
//						R.drawable.z_fans_focus , 0 , 0 , 0 );
				holder.btnContent.setText( R.string.agree );
				holder.btnContent.setTextColor( getResources().getColor(R.color.white) );
			}
			else if ( bean.type == GroupNoticeType.ALLOW_JOIN_GROUP )
			{
				holder.btn.setVisibility( View.VISIBLE );
//				holder.btn.setBackgroundResource( 0 );
//				holder.btnContent.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 , 0 , 0 );
				holder.btn.setBackgroundResource(R.drawable.z_fans_btn_white_new);
				holder.btnContent.setText( R.string.is_agreed );
				holder.btnContent.setTextColor(getResources().getColor(R.color.c_999999 ));
			}
			else if ( bean.type == GroupNoticeType.REJECT_JOIN_GROUP )
			{
				holder.btn.setVisibility( View.VISIBLE );
//				holder.btn.setBackgroundResource( 0 );
//				holder.btnContent.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 , 0 , 0 );
				holder.btn.setBackgroundResource(R.drawable.z_fans_btn_white_new);
				holder.btnContent.setText( R.string.is_refused );
				holder.btnContent.setTextColor( getResources().getColor(R.color.c_999999) );
			}
			else
			{
				holder.btn.setVisibility( View.INVISIBLE );
			}
			
			if ( position >= dataList.size( ) - 1 )
			{
				holder.line.setVisibility( View.INVISIBLE );
			}
			else
			{
				holder.line.setVisibility( View.VISIBLE );
			}
			
			// 点击事件
			final GroupNoticeBean tmp = bean;
			holder.btn.setOnClickListener( new OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					if ( tmp.type == GroupNoticeType.APPLY_JOIN_GROUP )
					{
						long flag = GroupHttpProtocol.agreenApplyMessage( GroupNoticeActivity.this ,
								String.valueOf( tmp.groupid ) ,
								String.valueOf( tmp.targetuser.userid ) ,
								GroupNoticeActivity.this );
						allowJoinMap.put( flag , tmp );
					}
				}
			} );
			
			convertView.setOnClickListener( new OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					GroupNoticeDetailActivity.launch( GroupNoticeActivity.this , tmp );
				}
			} );
			
			return convertView;
		}
		
	}
	
	public class ViewHolder
	{
		public HeadPhotoView icon;
		public TextView noticeName;
		public TextView noticeContent;
		public TextView noticeDealer;
		public RelativeLayout btn;
		public TextView btnContent;
		public View line;
	}
	
}
