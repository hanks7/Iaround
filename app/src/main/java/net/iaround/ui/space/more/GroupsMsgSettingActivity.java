
package net.iaround.ui.space.more;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.tools.FaceManager;
import net.iaround.tools.PicIndex;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.group.bean.GroupMsgInfo;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.menu.CustomContextMenu;

import java.util.ArrayList;


/**
 * @Description: 设置中的圈消息设置
 * @author tanzy
 * @date 2015-5-13
 */
public class GroupsMsgSettingActivity extends BaseFragmentActivity implements OnClickListener,HttpCallBack
{

	private CheckBox showHelper;
//	private FlagImageView showHelper;
	private ListView listview;

	private ArrayList< GroupMsgInfo > dataList = new ArrayList< GroupMsgInfo >( );
	private DataAdapter adapter;

	private static final int REFRESH_DATA = 1000;
	private long clickedGroupID;
	private long getStatusFlag;
	private long setStatusFlag;
	private long setHelperFlag;

	private CustomContextDialog customContextDialog;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_groups_msg_setting);

		initView( );
		initData( );
	}


	private void initView( )
	{
		( (TextView) findViewById( R.id.tv_title ) ).setText( R.string.setting_notice_circle_msg_setting );
		findViewById( R.id.iv_left ).setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);
		showHelper = (CheckBox) findViewById( R.id.show_checkbox );
		listview = (ListView) findViewById( R.id.listview );
		adapter = new DataAdapter( );
		listview.setAdapter( adapter );

		// 获取圈消息接收状态列表，在FilterUtil中处理返回并保存至GroupAffairModel中
		getStatusFlag = GroupHttpProtocol.getGroupMsgReceiveStatus( mContext , this );

		customContextDialog = new CustomContextDialog(this,8,true);
		customContextDialog.setListenner(groupMsgSettingListener);
	}

	/**
	 * 从GroupAffairModel中获取数据并显示
	 */
	private void initData( ) {
		ArrayList<GroupMsgInfo> noticeGroups = new ArrayList<GroupMsgInfo>();
		ArrayList<GroupMsgInfo> notNoticeGroups = new ArrayList<GroupMsgInfo>();
		ArrayList<GroupMsgInfo> ignorGroups = new ArrayList<GroupMsgInfo>();

		if (GroupAffairModel.getInstance().getGroupHelperOnOff() == 1)
			showHelper.setChecked( true );
//			showHelper.setState(true);
		else
//			showHelper.setState(false);
			showHelper.setChecked( false );

//		showHelper.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				String value = showHelper.isSelected() ? "y" : "n";
//				GroupAffairModel.getInstance( ).setGroupHelperOnOff( !showHelper.isSelected() ? 1 : 0 );
//				setHelperFlag = GroupHttpProtocol.setGroupHelperStatus( mContext , value ,
//						GroupsMsgSettingActivity.this );
//				showHelper.setState(!showHelper.isSelected());
//			}
//		});
		showHelper.setOnCheckedChangeListener( new OnCheckedChangeListener( )
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView , boolean isChecked )
			{
				String value = isChecked ? "y" : "n";
				GroupAffairModel.getInstance( ).setGroupHelperOnOff( isChecked ? 1 : 0 );
				setHelperFlag = GroupHttpProtocol.setGroupHelperStatus( mContext , value ,
						GroupsMsgSettingActivity.this );
			}
		} );

		if ( GroupAffairModel.getInstance( ).groupsMsgStatus != null
				&& GroupAffairModel.getInstance( ).groupsMsgStatus.groups != null )
			for ( int i = 0 ; i < GroupAffairModel.getInstance( ).groupsMsgStatus.groups
					.size( ) ; i++ )
			{
				GroupMsgInfo info = GroupAffairModel.getInstance( ).groupsMsgStatus.groups
						.get( i );
				if ( info.type == GroupMsgReceiveType.RECEIVE_AND_NOTICE )
					noticeGroups.add( info );
				if ( info.type == GroupMsgReceiveType.RECEIVE_NOT_NOTICE )
					notNoticeGroups.add( info );
				if ( info.type == GroupMsgReceiveType.NOT_RECEIVE )
					ignorGroups.add( info );
			}

		GroupMsgInfo empty = new GroupMsgInfo( );
		GroupMsgInfo title1 = new GroupMsgInfo( );
		GroupMsgInfo title2 = new GroupMsgInfo( );
		GroupMsgInfo title3 = new GroupMsgInfo( );
		empty.type = 0;
		title1.type = -1;
		title2.type = -2;
		title3.type = -3;
		dataList.clear( );
		dataList.add( title1 );
		if ( noticeGroups.size( ) == 0 )
			dataList.add( empty );
		dataList.addAll( noticeGroups );
		dataList.add( title2 );
		if ( notNoticeGroups.size( ) == 0 )
			dataList.add( empty );
		dataList.addAll( notNoticeGroups );
		dataList.add( title3 );
		if ( ignorGroups.size( ) == 0 )
			dataList.add( empty );
		dataList.addAll( ignorGroups );
		handler.sendEmptyMessage( REFRESH_DATA );
	}

	/**
	 * 接收消息并提醒，收进圈助手且不提醒，屏蔽圈消息点击事件
     */
	private OnClickListener groupMsgSettingListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch ((int) v.getTag())
			{
				case 0:
					// TODO: 2017/4/26 接收消息并提醒
					GroupAffairModel.getInstance( ).setGroupMsgStatus( clickedGroupID ,
							GroupMsgReceiveType.RECEIVE_AND_NOTICE );
					setStatusFlag = GroupHttpProtocol.setGroupMsgReceiveStatus( mContext ,
							clickedGroupID , GroupMsgReceiveType.RECEIVE_AND_NOTICE ,
							GroupsMsgSettingActivity.this );
					initData( );
					handler.sendEmptyMessage( REFRESH_DATA );
					break;
				case 1:
					// TODO: 2017/4/26 收进圈助手且不提醒
					GroupAffairModel.getInstance( ).setGroupMsgStatus( clickedGroupID ,
							GroupMsgReceiveType.RECEIVE_NOT_NOTICE );
					setStatusFlag = GroupHttpProtocol.setGroupMsgReceiveStatus( mContext ,
							clickedGroupID , GroupMsgReceiveType.RECEIVE_NOT_NOTICE ,
							GroupsMsgSettingActivity.this );
					initData( );
					handler.sendEmptyMessage( REFRESH_DATA );
					break;
				case 2:
					// TODO: 2017/4/26 屏蔽圈消息
					GroupAffairModel.getInstance( ).setGroupMsgStatus( clickedGroupID ,
							GroupMsgReceiveType.NOT_RECEIVE );
					setStatusFlag = GroupHttpProtocol.setGroupMsgReceiveStatus( mContext ,
							clickedGroupID , GroupMsgReceiveType.NOT_RECEIVE ,
							GroupsMsgSettingActivity.this );
					initData( );
					handler.sendEmptyMessage( REFRESH_DATA );
					break;
			}
			customContextDialog.dismiss();
		}
	};

	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == getStatusFlag )
		{
			if ( Constant.isSuccess( result ) )
			{
				initData( );
			}
			else
			{
				ErrorCode.showError( mContext , result );
			}
		}
		else if ( flag == setStatusFlag )
		{
			if ( !Constant.isSuccess( result ) )
			{
				ErrorCode.showError( mContext , result );
			}
		}
		else if ( flag == setHelperFlag )
		{
			if ( !Constant.isSuccess( result ) )
			{
				ErrorCode.showError( mContext , result );
			}
		}
	}

	@Override
	public void onGeneralError( int e , long flag ) {}

	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
			{
				finish( );
			}
				break;
		}
	}

	private Handler handler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case REFRESH_DATA :
				{
					adapter.notifyDataSetChanged( );
				}
					break;

				default :
					break;
			}
		}
    };

	private class DataAdapter extends BaseAdapter
	{

		@Override
		public int getCount( )
		{
			return dataList.size( );
		}

		@Override
		public GroupMsgInfo getItem(int position )
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
			final ViewHolder holder;
			if ( null == convertView )
			{
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
				convertView = inflater.inflate( R.layout.setting_group_msg_setting_item ,
						parent , false );
				holder = new ViewHolder( );

				initMyNearItemHolder( convertView , holder );
				convertView.setTag( holder );
			}
			else
			{
				holder = ( ViewHolder ) convertView.getTag( );
			}

			holder.titleLayout.setVisibility( View.GONE );
			holder.itemLayout.setVisibility( View.GONE );
			holder.empty.setVisibility( View.GONE );
			final GroupMsgInfo bean = getItem( position );
			if ( bean.type < 0 )
			{// 标题项
				holder.titleLayout.setVisibility( View.VISIBLE );
				if ( bean.type == -1 )
				{
					holder.titleUp.setText( R.string.receive_and_notice_groups );
					holder.titleDown.setText( "" );
					holder.titleDown.setVisibility( View.GONE );
				}
				else if ( bean.type == -2 )
				{
					holder.titleUp.setText( R.string.receive_not_notice );
//					holder.titleDown.setText( R.string.receive_not_notice_groups_detail );
					holder.titleDown.setVisibility( View.GONE );
				}
				else if ( bean.type == -3 )
				{
					holder.titleUp.setText( R.string.not_receive );
					holder.titleDown.setText( "" );
					holder.titleDown.setVisibility( View.GONE );
				}
			}
			else if ( bean.type == 0 )
			{
				holder.empty.setVisibility( View.VISIBLE );
			}
			else
			{
				holder.itemLayout.setVisibility( View.VISIBLE );
//				holder.icon.executeRoundFrame( NetImageView.DEFAULT_AVATAR_ROUND_LIGHT ,
//						bean.group.icon );yuchao  加载圆形图片
				GlideUtil.loadCircleImage(BaseApplication.appContext, bean.group.icon, holder.icon, PicIndex.DEFAULT_GROUP_SMALL,
						PicIndex.DEFAULT_GROUP_SMALL);
				holder.name.setText( FaceManager.getInstance( mContext ).parseIconForString(
						holder.name , mContext , bean.group.name , 20 ) );

				holder.icon.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						Intent intent = new Intent( mContext , GroupInfoActivity.class );
						intent.putExtra( GroupInfoActivity.GROUPID , bean.group.id + "" );
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.startActivity( intent );
					}
				} );

				holder.itemLayout.setOnClickListener( new OnClickListener( )
				{
					@Override
					public void onClick( View v )
					{
						clickedGroupID = bean.group.id;
//						showContextMenu( holder.itemLayout , bean.type );
						customContextDialog.show();
					}
				} );

			}

			return convertView;
		}
	}

	private void initMyNearItemHolder(View convertView , ViewHolder holder )
	{
		holder.titleLayout = (LinearLayout) convertView.findViewById( R.id.title_layout );
		holder.itemLayout = (RelativeLayout) convertView.findViewById( R.id.item_layout );
		holder.empty = (RelativeLayout) convertView.findViewById( R.id.empty );
		holder.titleUp = (TextView) convertView.findViewById( R.id.title_up );
		holder.titleDown = (TextView) convertView.findViewById( R.id.title_down );
		holder.icon = (ImageView) convertView.findViewById( R.id.icon );
		holder.name = (TextView) convertView.findViewById( R.id.group_name );
	}

	private void showContextMenu(View view , int type )
	{
		int[ ] colours =
			{ Color.parseColor( "#157efb" ) , Color.parseColor( "#157efb" ) ,
					Color.parseColor( "#157efb" ) };

		OnClickListener[ ] clicks =
			{ notice , notNotice , notReceive };

		// 如果修改的类型跟原来的类型一样，则不操作
		if ( type > 0 && type <= colours.length && type <= clicks.length )
		{
			colours[ type - 1 ] = mContext.getResources( ).getColor( R.color.c_d42f2b );
			clicks[ type - 1 ] = null;
		}

		CustomContextMenu menu = new CustomContextMenu( mContext ,
				getString( R.string.group_msg_setting ) );
		// 点击后直接修改本地缓存并上报服务端，只对错误码处理
		menu.addMenuItem( 0 , getString( R.string.receive_and_notice ) , colours[ 0 ] ,
				clicks[ 0 ] , false );
		menu.addMenuItem( 1 , getString( R.string.receive_not_notice ) , colours[ 1 ] ,
				clicks[ 1 ] , false );
		menu.addMenuItem( 2 , getString( R.string.not_receive ) , colours[ 2 ] , clicks[ 2 ] ,
				true );
		menu.showMenu( view );
	}

	private OnClickListener notice = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{

		}
	};

	private OnClickListener notNotice = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{

		}
	};

	private OnClickListener notReceive = new OnClickListener( )
	{
		@Override
		public void onClick( View v )
		{

		}
	};


	public class ViewHolder
	{
		public LinearLayout titleLayout;
		public RelativeLayout itemLayout;
		public RelativeLayout empty;
		public TextView titleUp;
		public TextView titleDown;
		public ImageView icon;
		public TextView name;
	}

}
