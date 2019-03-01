
package net.iaround.ui.face;

import java.util.ArrayList;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.share.interior.IAroundFriendUtil;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GroupMemberSearchBean;
import net.iaround.ui.group.bean.GroupSearchUser;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 分享表情选择好友界面
 */
public class SendFaceToFriends extends SuperActivity implements OnClickListener
{
	private ImageView titleBack;
	private ListView mListView;
	private FriendsListAdapter mAdapter;
	/** 获取联系人列表的flag */
	private long FLAG_GET_CONTACT;
	/** Handler-msg.what 1. 获取数据 2.获取失败 */
	private  final int MSG_NOTIFICATION_DATA = 1;
	private  final int MSG_DATA_GET_FAIL = 2;
	/** 联系人的数据bean */
	private GroupMemberSearchBean mContactListBean;
	/** 联系人的列表 */
	ArrayList< GroupSearchUser > list = new ArrayList< GroupSearchUser >( );
	private Dialog mProgressDialog;
	public static boolean isFromSendFriends = false;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.contact_invisible );

		initView( );
		initData( );
	}

	private void initView( )
	{
		mListView = ( ListView ) findViewById( R.id.ListView );
		titleBack = ( ImageView ) findViewById( R.id.iv_left );
		TextView titleName = ( TextView ) findViewById( R.id.tv_title );
		titleName.setText( R.string.select_send_friends );
		titleBack.setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);
	}

	private void initData( )
	{
		if ( mAdapter == null )
		{
			showProgressDialog( );
			requestData( );
		}
		else
		{
			mAdapter.notifyDataSetChanged( );
		}
	}

	/**
	 * 去掉小秘书
	 */
	private ArrayList< GroupSearchUser > removeSecretary( ArrayList< GroupSearchUser > list )
	{

		for ( int i = 0 ; i < list.size( ) ; i++ )
		{
			GroupSearchUser friend = list.get( i );
			if ( friend.user.userid == Config.CUSTOM_SERVICE_UID )
			{
				list.remove( i );
				i--;
			}
		}
		return list;

	}

	/** 向服务端请求数据 */
	private void requestData( )
	{
		// 获取联系人列表
		FLAG_GET_CONTACT = FriendHttpProtocol.friendsGet( mActivity , this );

		if ( FLAG_GET_CONTACT < 0 )
		{
			Message msg = new Message( );
			msg.what = MSG_DATA_GET_FAIL;
			msg.arg1 = ErrorCode.E_107;
			mMainHandler.sendMessage( msg );
		}
	}

	private Handler mMainHandler = new Handler( Looper.getMainLooper( ) )
	{
		@Override
		public void handleMessage( Message msg )
		{
			if ( msg.what == MSG_DATA_GET_FAIL )
			{
				hideProgressDialog( );
				ErrorCode.toastError( mActivity , msg.arg1 );
			}
			else if ( msg.what == MSG_NOTIFICATION_DATA )
			{
				hideProgressDialog( );
				list = mContactListBean.users;
				list = removeSecretary( list );
				if ( list.size( ) == 0 )
				{
					CommonFunction.toastMsg( mContext , getString( R.string.face_no_friends ) );
				}
				mAdapter = new FriendsListAdapter( mContext , list );
				mListView.setAdapter( mAdapter );
				mAdapter.notifyDataSetChanged( );
			}
		}
	};

	class FriendsListAdapter extends BaseAdapter
	{
		private ArrayList< GroupSearchUser > dataList;

		public FriendsListAdapter( Context context , ArrayList< GroupSearchUser > list )
		{
			mContext = context;
			dataList = list;
		}

		@Override
		public int getCount( )
		{
			return dataList == null ? 0 : dataList.size( );
		}

		@Override
		public Object getItem( int position )
		{
			return dataList == null ? null : dataList.get( position );
		}

		@Override
		public long getItemId( int position )
		{
			return 0;
		}

		@Override
		public View getView( int position , View convertView , ViewGroup parent )
		{
			ViewHolder holder = null;
			if ( convertView == null )
			{
				holder = new ViewHolder( );
				convertView = LayoutInflater.from( mContext ).inflate(
						R.layout.send_face_item , null );

				holder.friendIcon = ( ImageView ) convertView.findViewById( R.id.face_img );
				holder.nickName = ( TextView ) convertView.findViewById( R.id.face_name );

				convertView.setTag( holder );
			}
			else
			{
				holder = ( ViewHolder ) convertView.getTag( );
			}

			final GroupSearchUser user = ( GroupSearchUser ) getItem( position );
			// 头像
			int i = CommonFunction.dipToPx( mContext , 9 );
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView( user.user.icon ,
//					holder.friendIcon , NetImageView.NEARUSER_DEFAULT_FACE ,
//					NetImageView.NEARUSER_DEFAULT_FACE , null , i );
			GlideUtil.loadRoundImage(BaseApplication.appContext,user.user.icon,i,holder.friendIcon, NetImageView.NEARUSER_DEFAULT_FACE ,
					NetImageView.NEARUSER_DEFAULT_FACE);
			// 昵称
			if ( user.user.getDisplayName( true )== null || holder.nickName == null )
			{
				CommonFunction.log( "groupshare" , "group.name null+++++++++++" );
			}
			else {
				SpannableString groupName = FaceManager.getInstance( parent.getContext( ) )
						.parseIconForString( holder.nickName , parent.getContext( ) ,
								user.user.getDisplayName( true ) , 16 );
				holder.nickName.setText( groupName );
			}

			mListView.setOnItemClickListener( new OnItemClickListener( )
			{
				@Override
				public void onItemClick( AdapterView< ? > parent , View view , int position ,
										 long id )
				{
					CloseAllActivity.getInstance( ).closeTarget( ChatPersonal.class );
					GroupSearchUser Groupuser = ( GroupSearchUser ) getItem( position );
					User user = IAroundFriendUtil.createUser( Groupuser.user );
					ChatPersonal.skipToChatPersonal( mContext , user );
					isFromSendFriends = true ;
					finish();
				}
			} );

			return convertView;
		}
	}

	@Override
	public void onClick( View v )
	{
		if ( v == titleBack || v.getId() == R.id.fl_left )
		{
			finish( );
		}

	}

	// 显示加载框
	private void showProgressDialog( )
	{
		if ( mProgressDialog == null )
		{
			mProgressDialog = DialogUtil.showProgressDialog( mContext , "" ,
					getString( R.string.please_wait ) , null );
			mProgressDialog.setCanceledOnTouchOutside( false );
		}

		mProgressDialog.show( );
	}

	// 隐藏加载框
	private void hideProgressDialog( )
	{
		if ( mProgressDialog != null )
		{
			mProgressDialog.hide( );
		}
	}

	@Override
	public void onDestroy( )
	{
		super.onDestroy( );
		if ( mProgressDialog != null )
		{
			mProgressDialog.dismiss( );
			mProgressDialog = null;
		}

	}

	@Override
	public void onGeneralSuccess( String result , long flag )
	{
		super.onGeneralSuccess( result , flag );

		if ( flag == FLAG_GET_CONTACT )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean != null && bean.isSuccess( ) )
			{
				mContactListBean = GsonUtil.getInstance( ).getServerBean(
						result , GroupMemberSearchBean.class );
				if ( mContactListBean != null && mContactListBean.isSuccess( ) )
				{
					Message msg = mMainHandler.obtainMessage( );
					msg.what = MSG_NOTIFICATION_DATA;
					mMainHandler.sendMessage( msg );
				}
			}
		}
	}

	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		Message msg = new Message( );
		msg.what = MSG_DATA_GET_FAIL;
		msg.arg1 = e;
		mMainHandler.sendMessage( msg );
	}
	static class ViewHolder
	{
		public ImageView friendIcon;
		public TextView nickName;
	}
}
