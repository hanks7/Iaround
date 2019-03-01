
package net.iaround.ui.group;


import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.database.SharedPreferenceCache;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.group.bean.MyGroupBean;
import net.iaround.utils.ImageViewUtil;

import java.util.ArrayList;


/**
 * @Description: 圈子列表，点击后退出该圈
 * @author tanzy
 * @date 2015-8-5
 */
public class GroupListToQuit extends BaseFragmentActivity implements OnClickListener
{
	private TextView titleName;
	private ListView listview;
	private TextView errorText;
	
	private DataAdapter adapter;
	private ArrayList< Group > dataList = new ArrayList< Group >( );
	private Dialog progressDialog;
	private String clickID;
	
	private final static int MSG_NOTIFY_DATA_CHANGE = 1000;
	private long getListFlag = 0;
	private long quitFlag = 0;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.group_list_to_quit );
		
		initView( );
		initData( );
	}
	
	private void initView( )
	{
		titleName = (TextView) findViewById( R.id.tv_title );
		titleName.setText( R.string.menu_exit_group );
		
		listview = (ListView) findViewById( R.id.listview );
		adapter = new DataAdapter( );
		listview.setAdapter( adapter );
		errorText = (TextView) findViewById( R.id.error_text );
		progressDialog = DialogUtil.getProgressDialog( this , "" ,
				mContext.getString( R.string.please_wait ) , null );
		
		findViewById( R.id.iv_left ).setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);
	}
	
	private void initData( )
	{
		if ( progressDialog != null )
			progressDialog.show( );
		
		getListFlag = GroupHttpProtocol.groupMylist( mContext , this );
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( progressDialog != null )
			progressDialog.hide( );
		
		if ( flag == getListFlag )
		{// 获取我的圈子列表
			MyGroupBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					MyGroupBean.class );
			if ( bean.isSuccess( ) )
			{
				if ( bean.groups == null || bean.groups.size( ) <= 0 )
				{
					showErrorView( );
				}
				else
				{
					dataList.clear( );
					long muid = Common.getInstance( ).loginUser.getUid( );
					for ( int i = 0 ; i < bean.groups.size( ) ; i++ )
					{
						long ownerId = bean.groups.get( i ).user.userid;
						if ( muid != ownerId )
						{// 自己创建的圈子不显示
							dataList.add( bean.groups.get( i ) );
						}
					}
					
					if ( dataList.size( ) <= 0 )
					{
						showErrorView( );
					}
					else
					{
						handler.sendEmptyMessage( MSG_NOTIFY_DATA_CHANGE );
					}
				}
			}
			else
			{
				ErrorCode.showError( mContext , result );
				showErrorView( );
			}
		}
		else if ( flag == quitFlag )
		{// 退出圈子
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean(
					result , BaseServerBean.class );
			if ( bean != null )
			{
				if ( bean.isSuccess( ) )
				{
					GroupModel.getInstance( ).removeGroupFromCache( clickID );
					GroupModel.getInstance( ).clearByGroupID( Integer.valueOf( clickID ) );
//					ContactsView.newGroupMenber = true;
					SharedPreferenceCache.getInstance( mContext ).putString( clickID , "" );
					try
					{
						GroupModel.getInstance( ).dissolveGroup( mContext , clickID );
						GroupModel.getInstance( ).isNeedRefreshGroupList = true;
						// 退出或解散圈子，删除掉消息列表记录，并返回到带侧栏的界面
						String userIdStr = Common.getInstance( ).loginUser.getUid( ) + "";
						GroupModel.getInstance( ).removeGroupAndAllMessage( mContext ,
								userIdStr , clickID );
					}
					catch ( Exception e )
					{
						e.printStackTrace( );
					}
					finish( );
				}
				else
				{
					ErrorCode.showError( mContext , result );
				}
			}
			else
			{
				ErrorCode.showError( mContext , result );
			}
		}
		super.onGeneralSuccess( result , flag );
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		ErrorCode.toastError( mContext , e );
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
				finish( );
				break;
		}
	}
	
	private void showErrorView( )
	{
		errorText.setVisibility( View.VISIBLE );
		listview.setVisibility( View.INVISIBLE );
	}
	
	private Handler handler = new Handler( )
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
			}
		}
    };
	
	protected void onDestroy( )
	{
		if ( progressDialog != null )
		{
			progressDialog.cancel( );
			progressDialog = null;
		}
		super.onDestroy( );
	}

    private class DataAdapter extends BaseAdapter
	{
		
		@Override
		public int getCount( )
		{
			return dataList.size( );
		}
		
		@Override
		public Group getItem(int position )
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
				convertView = LayoutInflater.from( mContext ).inflate(
						R.layout.share_select_group_item , null );
			}
			
			final Group bean = getItem( position );
			
			// 圈图标
			ImageView icon = (ImageView) convertView.findViewById( R.id.group_img );
			ImageViewUtil.getDefault( ).loadRoundFrameImageInConvertView(
					CommonFunction.thumPicture( bean.icon ) , icon ,
				R.drawable.group_item_default_icon , R.drawable.group_item_default_icon ,
					null , 0 , "" );
			
			// 圈名
			TextView name = (TextView) convertView.findViewById( R.id.group_name );
			SpannableString groupName = FaceManager.getInstance( parent.getContext( ) )
					.parseIconForString( name , parent.getContext( ) , bean.name , 16 );
			name.setText( groupName );
			
			LinearLayout line = (LinearLayout) convertView
					.findViewById( R.id.bottom_divider );
			if ( position == getCount( ) - 1 )
			{
				line.setVisibility( View.GONE );
			}
			else
			{
				line.setVisibility( View.VISIBLE );
			}
			
			convertView.setOnClickListener( new OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					clickID = bean.id;
					if ( bean.user.userid == Common.getInstance( ).loginUser.getUid( ) )
					{
						CommonFunction.toastMsg( mContext , R.string.operation_canceled );
						return;
					}
					
					DialogUtil.showTowButtonDialog( GroupListToQuit.this ,
							mContext.getString( R.string.menu_exit_group ) ,
							mContext.getString( R.string.room_info_quit_info ) ,
							mContext.getString( R.string.cancel ) ,
							mContext.getString( R.string.ok ) , null , new OnClickListener( )
							{
								@Override
								public void onClick( View v )
								{
									if ( progressDialog != null )
										progressDialog.show( );
									quitFlag = GroupHttpProtocol.groupDelUser( mContext ,
											clickID , PhoneInfoUtil.getInstance( mContext )
													.loginCode( mContext ) ,
											GroupListToQuit.this );
								}
							} );
				}
			} );
			return convertView;
		}
	}
}
