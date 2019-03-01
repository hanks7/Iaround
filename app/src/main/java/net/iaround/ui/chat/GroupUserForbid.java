
package net.iaround.ui.chat;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.ChatFromType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.JsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.ui.view.menu.CustomContextMenu;
import net.iaround.utils.ImageViewUtil;

import org.json.JSONException;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Map;


/**
 * 圈子：对成员禁言，并选择禁用时间
 * 
 * @author linyg
 * 
 */
public class GroupUserForbid extends SuperActivity implements OnClickListener
{
	/**
	 * 禁言/解除禁言的requestCode
	 */
	public static final int REQUEST_CODE_FORBID = 401;
	/**
	 * 消息：禁言成功
	 */
	private final int MSG_FORBID_SUCC = 1;
	/**
	 * 消息：禁言失败
	 */
	private final int MSG_FORBID_FAIL = 2;
	/**
	 * 消息：解除禁言成功
	 */
	private final int MSG_CANCLE_FORBID_SUCC = 3;
	/**
	 * 消息：解除禁言失败
	 */
	private final int MSG_CANCLE_FORBID_FAIL = 4;
	/**
	 * 消息：请求禁言用户信息成功
	 */
	private final int MSG_GET_FORBID_USER_INFO_SUCC = 5;
	/**
	 * 消息：请求禁言用户信息失败
	 */
	private final int MSG_GET_FORBID_USER_INFO_FAIL = 6;
	/**
	 * 消息：请求禁言用户的信息
	 */
	private final int MSG_GET_FORBID_USER_INFO = 7;

	//标题栏
	private TextView mTitleName;
	private ImageView mTitleBack;
	private FrameLayout flLeft;

	private HeadPhotoView userIconView;//头像
	private TextView userNameView;//用户名
	private LinearLayout genderBgView;//性别和年龄
	private TextView userAgeView;//年龄
	private ImageView genderTagView;//性别
	private TextView distanceView;//距离
	private TextView onlineTagView;//在线标志
	private TextView selfTextView;//个性签名
	private LinearLayout timeLayout;//选择禁言时间布局
	private Button okBtn;//确定按钮
	private LinearLayout cancleForbidLayout;
	private TextView remainTimeHour;
	private TextView remainTimeMin;
	private Button cancleForbidBtn;
	private Button sp_group_forbid;
	private RelativeLayout rlFrobid;
	private TextView remainTimeTips;
	private TextView tvForbidTime;


	/**
	 * 圈子ID
	 */
	private String groupId;
	/**
	 * 圈子名称
	 */
	private String groupName;
	
	/**
	 * 用户id
	 */
	private long userId;
	/**
	 * 用户头像
	 */
	private String userIcon;
	/**
	 * 用户昵称
	 */
	private String userName;
	/**
	 * VIP等级
	 */
	private int vipLevel;
	/**
	 * 用户年龄
	 */
	private int userAge;
	/**
	 * 用户性别
	 */
	private int userSex;
	/**
	 * 与用户的距离
	 */
	private String userDistance;
	/**
	 * 用户的在线情况
	 */
	private String userOnlineTag;
	/**
	 * 用户的个性签名
	 */
	private String selfText;
	/**
	 * 禁言请求的Flag
	 */
	private long forbidReqFlag;
	/**
	 * 解除禁言请求的Flag
	 */
	private long cancleForbidReqFlag;
	/**
	 * 禁言用户信息请求的Flag
	 */
	private long forbidUserInfoReqFlag;
	private Dialog loadingDialog;
	/**
	 * 是否已被禁言
	 */
	private boolean isForbid;
	/**
	 * 解禁的到期时间:单位为毫秒
	 */
	private long expiredTime;
	/**选择禁言时间的位置*/
	int position;

	private User user;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
		setContentView( R.layout.group_user_forbid );
		
		groupId = getIntent( ).getStringExtra( "group_id" );
		groupName = getIntent( ).getStringExtra( "group_name" );
		userId = getIntent( ).getLongExtra( "user_id" , 0 );

		initComponent();
		initData( );
	}
	
	private void initComponent( )
	{
		
		mTitleName = (TextView) findViewById( R.id.tv_title );
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		flLeft = (FrameLayout) findViewById(R.id.fl_left);
		mTitleName.setText( R.string.group_forbid_say_title );
		mTitleBack.setImageResource(R.drawable.title_back);
		
		userIconView = (HeadPhotoView) findViewById( R.id.friend_icon );
		userNameView = (TextView) findViewById( R.id.userName );
		genderBgView = (LinearLayout) findViewById( R.id.genderBg );
		userAgeView = (TextView) findViewById( R.id.userAge );
		genderTagView = (ImageView) findViewById( R.id.genderTag );
		distanceView = (TextView) findViewById( R.id.distance );
		onlineTagView = (TextView) findViewById( R.id.onlineTag );
		selfTextView = (TextView) findViewById( R.id.selfText );
		timeLayout = (LinearLayout) findViewById( R.id.timeLayout );
		okBtn = (Button) findViewById( R.id.okBtn );
		cancleForbidLayout = (LinearLayout) findViewById( R.id.cancleForbidLayout );
		remainTimeHour = (TextView) findViewById( R.id.remain_time_hour );
		remainTimeMin = (TextView) findViewById( R.id.remain_time_min );
		cancleForbidBtn = (Button) findViewById( R.id.cancleForbidBtn );
		sp_group_forbid = (Button) findViewById( R.id.sp_group_forbid );
		rlFrobid = (RelativeLayout) findViewById(R.id.rl_forbid);
		remainTimeTips = (TextView) findViewById( R.id.forbid_time_tip );
		tvForbidTime = (TextView) findViewById(R.id.tv_forbid_time);

		tvForbidTime.setText("5分钟");
		flLeft.setOnClickListener(this);
		mTitleBack.setOnClickListener( this );
		userIconView.setOnClickListener(new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				// TODO Auto-generated method stub
//				SpaceOther.launchUser( mActivity , userId , user ,  ChatFromType.Group , GroupInfoForRelation.GROUP_NAME , GroupInfoForRelation.GROUP_ID  );
			//jiqiang
			}
		});
		
		okBtn.setOnClickListener( this );
		cancleForbidBtn.setOnClickListener( this );
		rlFrobid.setOnClickListener(this);
		sp_group_forbid.setOnClickListener(this);
	}
	
	private void initData( )
	{
		isForbid = false;
		displayLoadingDialog( );
		mhandler.sendEmptyMessageDelayed( MSG_GET_FORBID_USER_INFO , 100 );
	}
	
	/**
	 * 初始禁言用户信息
	 * 
	 * @param map
	 */
	private void initForbidUserInfo( Map< String , Object > map )
	{
		user = ( User ) map.get( "user" );
		userIcon = user.getIcon( );
		userName = user.getNoteName( true );
		vipLevel = user.getViplevel( );
		userAge = user.getAge( );
		userSex = user.getSexIndex( );
		selfText = user.getSign();
		selfText = user.getPersonalInfor( mActivity );
		if ( CommonFunction.isEmptyOrNullStr( selfText ) )
			selfText = "";
		double distance = Double.valueOf( String.valueOf( map.get( "distance" ) ) );
		distance = distance / 1000.0; // 距离
		if ( distance > 10 )
		{
			int d = ( int ) distance;
			userDistance = d + "km";
		}
		else
		{
			DecimalFormat df = new DecimalFormat( "0.00" );
			String result = df.format( distance );
			userDistance = result + "km";
		}
		
		String online = String.valueOf( map.get( "online" ) );
		boolean isOnline = false;
        isOnline = "y".equals(online);
//		if ( isOnline )
//		{ // 是否在线：在线
//			userOnlineTag = getResString( R.string.current_online );
//		}
//		else
//		{ // 离线
//			long lastLoginTime = Long.valueOf( String.valueOf( map.get( "logintime" ) ) );
//			userOnlineTag = TimeFormat.timeFormat1( mActivity , lastLoginTime );
//		}
		
		long lastLoginTime = Long.valueOf( String.valueOf( map.get( "logintime" ) ) );
		userOnlineTag = TimeFormat.timeFormat1( mActivity , lastLoginTime );
		
		String forbid = String.valueOf( map.get( "isforbid" ) );
		if ( "y".equals( forbid ) )
		{
			isForbid = true;
		}
		expiredTime = Long.valueOf( String.valueOf( map.get( "expiredtime" ) ) );
	}
	
	/**
	 * 显示禁言用户的信息
	 */
	private void displayForbidUserInfo( ) {
		boolean isSvip = user.getSVip() == 0;
		boolean isVip = user.getViplevel() == 1;
		userIconView.setVipLevel(isSvip,isVip,false,null);
		userIconView.execute(user);
		// 用户昵称
		userNameView.setText(FaceManager.getInstance(mContext).parseIconForString(
				userNameView, mActivity, userName, 20));

		// 用户年龄
		userAgeView.setText("" + userAge);

		// 用户性别
		if (1 == userSex) { // 男
			genderBgView.setBackgroundResource(R.drawable.group_member_age_man_bg); // 性别的背景色
			genderTagView.setImageResource(R.drawable.thread_register_man_select); // 性别的图标
		} else if (2 == userSex) { // 女
			genderBgView.setBackgroundResource(R.drawable.group_member_age_girl_bg);
			genderTagView.setImageResource(R.drawable.thread_register_woman_select);
		} else { // 0 保密
//			genderBgView.setBackgroundResource( R.drawable.user_age_girl_bg );
			genderTagView.setVisibility(View.GONE);
		}
		distanceView.setText(userDistance); // 与用户的距离
		onlineTagView.setText(userOnlineTag); // 用户在线状态

		//个性签名
		if (!"".equals(selfText))
		{
			selfTextView.setVisibility(View.VISIBLE);
			selfTextView.setText(selfText);
		}else
			selfTextView.setVisibility(View.GONE);

		if ( isForbid )
		{ // 已禁言，显示解除禁言UI
			cancleForbidLayout.setVisibility( View.VISIBLE );
			displayExpiredTime( );
		}
		else
		{ // 显示禁言UI

			timeLayout.setVisibility( View.VISIBLE );
//			// 初始化选择项
//			ArrayAdapter< CharSequence > adapter = ArrayAdapter.createFromResource( this ,
//					R.array.group_forbid_time , R.layout.simple_spinner_item_black );
//			adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );
//			sp_group_forbid.setAdapter( adapter );//jiqiang
		}
		
//		if ( CommonFunction.isShowGroupChatForbidGuide( mActivity ) )
//		{
////			ImageView v = new ImageView( this );
////			v.setBackgroundResource( R.drawable.group_chat_forbid_guide );
////			HPopupGuide popupGuide = new HPopupGuide( v , Color.parseColor( "#cc000000" ) );
////			int dp_5 = ( int ) ( 5 * getResources( ).getDisplayMetrics( ).density );
////			popupGuide.showAtLocation( this , HPopupGuide.SHOW_TOP + dp_5 );
////			popupGuide.setOnDissmissListener( new OnDissmissListener( )
////			{
////
////				@Override
////				public void onDissmiss( )
////				{
////					// TODO Auto-generated method stub
////					CommonFunction.setNoShowGroupChatForbidGuide( mActivity );
////				}
////			} );//jiqiang
//		}
	}
	
	/**
	 * 显示解禁的到期时间
	 */
	private void displayExpiredTime( )
	{
		Date d = new Date( );
		Long nowTime = Common.getInstance( ).serverToClientTime + d.getTime( ); // 同步服务器时间
		expiredTime -= nowTime;
		if ( expiredTime < 0 || expiredTime / 1000 > 30 * 24 * 60 * 60 )
		{ // 小于0或大于30天被认为永久禁言
			findViewById( R.id.remain_time ).setVisibility( View.GONE );
			remainTimeTips.setVisibility( View.INVISIBLE );
			findViewById( R.id.remain_time_forever ).setVisibility( View.VISIBLE );
		}
		else
		{
			findViewById( R.id.remain_time ).setVisibility( View.VISIBLE );
			findViewById( R.id.remain_time_forever ).setVisibility( View.GONE );
			long sec = expiredTime / 1000;
			int min = ( int ) ( sec / 60 );
			int hour = min / 60;
			min = min % 60;
			remainTimeHour.setText( "" + hour );
			remainTimeMin.setText( "" + min );
		}
	}

	/**
	 * 显示加载对话框
	 */
	private void displayLoadingDialog( )
	{
//		loadingDialog = DialogUtil.showProgressDialog( mActivity , "" ,
//				mActivity.getResString( R.string.please_wait ) , null );//jiqiang
	}

	/**
	 * 隐藏加载对话框
	 */
	private void dimissLoadingDialog( )
	{
		if ( loadingDialog != null && loadingDialog.isShowing( ) )
		{
			loadingDialog.dismiss( );
			loadingDialog = null;
		}
	}
	
	/**
	 * 点击返回
	 */
	private void backClick( )
	{
		finish( );
	}
	
	/**
	 * 确定
	 */
	private void okClick(int which)
	{
		// 计算禁言时间
		long forbidTime = 0;
//		int position = sp_group_forbid.getSelectedItemPosition( );
		int position = which;
		switch ( position )
		{
			case 0 : // 5分钟
				forbidTime = 5 * 60 * 1000;
				break;
			case 1 :// 10分钟
				forbidTime = 10 * 60 * 1000;
				break;
			case 2 :// 1小时
				forbidTime = 60 * 60 * 1000;
				break;
			case 3 :// 8小时
				forbidTime = 8 * 60 * 60 * 1000;
				break;
			case 4 :// 1天
				forbidTime = 24 * 60 * 60 * 1000;
				break;
			case 5 :// 永久禁言
				forbidTime = -1;
				break;
			default :
				forbidTime = 5 * 60 * 1000;
		}
		displayLoadingDialog( );
		forbidReqFlag = GroupHttpProtocol.groupManageForbid( mActivity , groupId ,
				String.valueOf( userId ) , forbidTime , this );
		if ( forbidReqFlag == -1 )
		{
			mhandler.sendEmptyMessage( MSG_FORBID_FAIL );
		}//jiqiang
	}
	
	/**
	 * 解除禁言
	 */
	private void cancleForbid( )
	{
		displayLoadingDialog( );
		cancleForbidReqFlag = GroupHttpProtocol.groupManagerCacelForbid( mActivity , groupId ,
				String.valueOf( userId ) , this );
		if ( -1 == cancleForbidReqFlag )
		{
			mhandler.sendEmptyMessage( MSG_CANCLE_FORBID_FAIL );
		}//jiqiang
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v == mTitleBack  || v == flLeft)
		{ // 返回
			backClick( );
		}
		else if ( v == okBtn )
		{ // 确定
			okClick(position);
		}
		else if ( v == cancleForbidBtn )
		{ // 马上解除禁言
			cancleForbid( );
		}else if (v == rlFrobid || v == sp_group_forbid)
		{
			DialogUtil.showSingleChoiceDialog(this, getString(R.string.group_user_forbid_title_time), getResources().getStringArray(R.array.group_forbid_time_sel)
					,position , new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					String[] itemArray = getResources().getStringArray(R.array.group_forbid_time_sel);
					String itemText = itemArray[which];
					tvForbidTime.setText(itemText);
					position = which;
				}
			});
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		
		if ( forbidReqFlag == flag )
		{ // 禁言
			Message msg = mhandler.obtainMessage( MSG_FORBID_SUCC , result );
			mhandler.sendMessage( msg );
		}
		else if ( cancleForbidReqFlag == flag )
		{ // 解除禁言
			Message msg = mhandler.obtainMessage( MSG_CANCLE_FORBID_SUCC , result );
			mhandler.sendMessage( msg );
		}
		else if ( forbidUserInfoReqFlag == flag )
		{ // 禁言用户信息
			Message msg = mhandler.obtainMessage( MSG_GET_FORBID_USER_INFO_SUCC , result );
			mhandler.sendMessage( msg );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		
		if ( forbidReqFlag == flag )
		{ // 禁言
			mhandler.sendEmptyMessage( MSG_FORBID_FAIL );
		}
		else if ( cancleForbidReqFlag == flag )
		{ // 解除禁言
			mhandler.sendEmptyMessage( MSG_CANCLE_FORBID_FAIL );
		}
		else if ( forbidUserInfoReqFlag == flag )
		{ // 禁言用户信息
			mhandler.sendEmptyMessage( MSG_GET_FORBID_USER_INFO_FAIL );
		}
	}
	
	/**
	 * 请求禁言用户的信息
	 */
	private void loadForbidUserInfo( )
	{
		forbidUserInfoReqFlag = GroupHttpProtocol.forbinUserInfo( mActivity , groupId ,
				userId , this );
		if ( -1 == forbidUserInfoReqFlag )
		{
			mhandler.sendEmptyMessage( MSG_GET_FORBID_USER_INFO_FAIL );
		}//jiqiang
	}
	
	/**
	 * 禁言成功
	 * 
	 * @param msg
	 */
	private void handlerForbidSucc( Message msg )
	{
		dimissLoadingDialog( );
		String result = String.valueOf( msg.obj );
		Map< String , Object > map = JsonUtil.jsonToMap( result );
		
		if ( map != null && map.containsKey( "status" ) )
		{
			int status = (Integer) map.get( "status" );
			if ( status == 200 )
			{
				CommonFunction.showToast( mActivity ,
						getResString( R.string.operate_success ) , 0 );
				setResult( RESULT_OK );
				finish( );
			}
			else
			{
				if ( map.containsKey( "error" ) )
				{
					int e = (Integer) map.get( "error" );
					ErrorCode.showError( mActivity , result );
				}
			}
		}
		else
		{
			CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
		}
	}
	
	/**
	 * 禁言失败
	 * 
	 * @param msg
	 */
	private void handlerForbidFail( Message msg )
	{
		dimissLoadingDialog( );
		CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
	}
	
	/**
	 * 解除禁言成功
	 * 
	 * @param msg
	 */
	private void handleCancleForbidSucc( Message msg )
	{
		dimissLoadingDialog( );
		String result = String.valueOf( msg.obj );
		Map< String , Object > map = JsonUtil.jsonToMap( result );
		
		if ( map != null && map.containsKey( "status" ) )
		{
			int status = (Integer) map.get( "status" );
			if ( status == 200 )
			{
				CommonFunction.showToast( mActivity ,
						getResString( R.string.operate_success ) , 0 );
				Intent data = new Intent();
				data.putExtra( "userid" , userId );
				setResult( SuperActivity.RESULT_OK, data );
				finish( );
			}
			else
			{
				if ( map.containsKey( "error" ) )
				{
					ErrorCode.showError( mActivity , result );
				}
			}
		}
		else
		{
			CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
		}
	}
	
	/**
	 * 解除禁言失败
	 * 
	 * @param msg
	 */
	private void handleCancleForbidFail( Message msg )
	{
		dimissLoadingDialog( );
		CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
	}
	
	/**
	 * 请求禁言用户信息成功
	 * 
	 * @param msg
	 */
	private void handleForbidUserInfoSucc( Message msg )
	{
		dimissLoadingDialog( );
		
		String result = String.valueOf( msg.obj );
		Map< String , Object > map = JsonUtil.jsonToMap( result );
		if ( map != null && map.containsKey( "status" ) )
		{
			int status = (Integer) map.get( "status" );
			if ( status == 200 )
			{
				try
				{
					map = GroupModel.getInstance( ).parseForbidUserInfo( result );
					initForbidUserInfo( map );
					displayForbidUserInfo( );
				}
				catch ( JSONException e )
				{
					e.printStackTrace( );
					CommonFunction.showToast( mActivity ,
							getResString( R.string.operate_fail ) , 0 );
				}
			}
			else
			{
				if ( map.containsKey( "error" ) )
				{
					int e = (Integer) map.get( "error" );
					ErrorCode.showError( mActivity , result );
				}
			}
		}
		else
		{
			CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
		}
	}
	
	/**
	 * 请求禁言用户信息失败
	 * 
	 * @param msg
	 */
	private void handleForbidUserInfoFail( Message msg )
	{
		dimissLoadingDialog( );
		CommonFunction.showToast( mActivity , getResString( R.string.operate_fail ) , 0 );
	}
	
	private Handler mhandler = new Handler( )
	{
		public void handleMessage( Message msg )
		{
			switch ( msg.what )
			{
				case MSG_FORBID_SUCC : // 禁言成功
					handlerForbidSucc( msg );
					break;
				case MSG_FORBID_FAIL : // 禁言失败
					handlerForbidFail( msg );
					break;
				case MSG_CANCLE_FORBID_SUCC : // 解除禁言成功
					handleCancleForbidSucc( msg );
					break;
				case MSG_CANCLE_FORBID_FAIL : // 解除禁言失败
					handleCancleForbidFail( msg );
					break;
				case MSG_GET_FORBID_USER_INFO : // 请求禁言用户的信息
					loadForbidUserInfo( );
					break;
				case MSG_GET_FORBID_USER_INFO_SUCC : // 请求禁言用户信息成功
					handleForbidUserInfoSucc( msg );
					break;
				case MSG_GET_FORBID_USER_INFO_FAIL : // 请求禁言用户信息失败
					handleForbidUserInfoFail( msg );
					break;
			}
			
			super.handleMessage( msg );
		}
	};
}
