
package net.iaround.ui.group.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.entity.type.GroupNoticeType;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.view.HeadPhotoView;


public class GroupNoticeDetailActivity extends BaseFragmentActivity implements OnClickListener,HttpCallBack
{
	private static GroupNoticeBean bean;
	private static final int MSG_REFRESH_VIEW = 1000;

	private User user;
	private TextView titleName;
	private HeadPhotoView icon;
	private RelativeLayout targetCard;
	private TextView nickName;
	private TextView age;
	private RelativeLayout rlAgeSex;
	private ImageView ivSex;
	private RelativeLayout groupCard;
	private HeadPhotoView groupIcon;
	private TextView groupName;
	private TextView groupType;
	private TextView statusContent1;
	private TextView dealer1;
	private TextView noticeTime;
	private TextView agreeButton;
	private TextView rejectButton;
	private TextView agreeStatus;
	
	private long agreeFlag;
	private long rejectFlag;
	
	public static void launch(Context context , GroupNoticeBean mbean )
	{
		bean = mbean;
		if ( context != null )
		{
			Intent intent = new Intent( );
			intent.setClass( context , GroupNoticeDetailActivity.class );
			context.startActivity( intent );
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.group_notice_detail_activity );
		
		initView( );
		initData( );
		setListener( );
	}
	
	private void initView( )
	{
		icon = (HeadPhotoView) findViewById( R.id.friend_icon );
		titleName = (TextView) findViewById( R.id.tv_title );
		nickName = (TextView) findViewById( R.id.friend_name );
		age = (TextView) findViewById( R.id.tvAge );
		rlAgeSex = (RelativeLayout) findViewById(R.id.rlAgeSex);
		ivSex = (ImageView) findViewById(R.id.ivSex);
		statusContent1 = (TextView) findViewById( R.id.notice_content1 );
		dealer1 = (TextView) findViewById( R.id.notice_dealer1 );
		noticeTime = (TextView) findViewById( R.id.notice_time );
		agreeButton = (TextView) findViewById( R.id.allow_button );
		rejectButton = (TextView) findViewById( R.id.reject_button );
		agreeStatus = (TextView) findViewById( R.id.join_status );
		targetCard = (RelativeLayout) findViewById( R.id.target_card );
		groupCard = (RelativeLayout) findViewById( R.id.group_card );
		groupName = (TextView) findViewById( R.id.group_name );
		groupType = (TextView) findViewById( R.id.group_type );
		groupIcon = (HeadPhotoView) findViewById( R.id.group_icon );

	}
	
	private void initData( )
	{
		if ( bean == null || bean.type > GroupNoticeType.REMOVE_MANAGER )
		{
			CommonFunction.toastMsg( GroupNoticeDetailActivity.this , R.string.load_data_fail );
			return;
		}
		user = bean.targetuser.convertBaseToUser();
		// 标题
		int titleID = 0;
		switch ( bean.type )
		{
			case GroupNoticeType.APPLY_JOIN_GROUP :
			case GroupNoticeType.ALLOW_JOIN_GROUP :
			case GroupNoticeType.REJECT_JOIN_GROUP :
				titleID = R.string.notice_title_apply;
				break;
			case GroupNoticeType.QUIT_GROUP :
				titleID = R.string.notice_title_quit;
				break;
			case GroupNoticeType.KICK_OUT_GROUP :
				titleID = R.string.notice_title_kick;
				break;
			case GroupNoticeType.SET_MANAGER :
				titleID = R.string.notice_title_set;
				break;
			case GroupNoticeType.REMOVE_MANAGER :
				titleID = R.string.notice_title_remove;
				break;
		}
		if ( titleID != 0 )
			titleName.setText( titleID );
		
		// 头像、名字、年龄
		if ( bean.type == GroupNoticeType.KICK_OUT_GROUP
				|| bean.type == GroupNoticeType.SET_MANAGER
				|| bean.type == GroupNoticeType.REMOVE_MANAGER )
		{// 显示圈图标很圈名
			targetCard.setVisibility( View.GONE );
			groupCard.setVisibility( View.VISIBLE );
			groupIcon.executeRoundFrame( NetImageView.DEFAULT_FACE , bean.icon );
			if ( !CommonFunction.isEmptyOrNullStr( bean.category ) )
			{
				String[ ] names = bean.category.split( "\\|" );
				groupType.setText( names[ CommonFunction.getLanguageIndex( GroupNoticeDetailActivity.this ) ] );
			}
			groupName.setText( FaceManager.getInstance( GroupNoticeDetailActivity.this ).parseIconForString(
					groupName , GroupNoticeDetailActivity.this , bean.groupname , 15 ) );
		}
		else if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP
				|| bean.type == GroupNoticeType.ALLOW_JOIN_GROUP
				|| bean.type == GroupNoticeType.REJECT_JOIN_GROUP
				|| bean.type == GroupNoticeType.QUIT_GROUP )
		{
			targetCard.setVisibility( View.VISIBLE );
			groupCard.setVisibility( View.GONE );
			icon.executeRoundFrame( R.drawable.iaround_default_img ,
					bean.targetuser.icon );
			nickName.setText( FaceManager.getInstance( GroupNoticeDetailActivity.this ).parseIconForString(
					nickName , GroupNoticeDetailActivity.this , bean.targetuser.nickname , 15 ) );
			age.setVisibility( View.VISIBLE );
			if ( "m".equals( bean.targetuser.gender ) )
			{
				rlAgeSex.setBackgroundResource(R.drawable.group_member_age_man_bg);
				ivSex.setImageResource(R.drawable.thread_register_man_select);
//				age.setBackgroundResource( R.drawable.sex_bg_boy );
//				age.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 , R.drawable.sex_icon_boy ,
//						0 );
			}
			else
			{
				rlAgeSex.setBackgroundResource(R.drawable.group_member_age_girl_bg);
				ivSex.setImageResource(R.drawable.thread_register_woman_select);
//				age.setBackgroundResource( R.drawable.sex_bg_gril );
//				age.setCompoundDrawablesWithIntrinsicBounds( 0 , 0 , R.drawable.sex_icon_girl ,
//						0 );
			}
			age.setText( bean.targetuser.age + "" );
		}
		
		// 圈通知上部分正文
		if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP
				|| bean.type == GroupNoticeType.ALLOW_JOIN_GROUP
				|| bean.type == GroupNoticeType.REJECT_JOIN_GROUP )
		{// 申请、通过、拒绝：“申请加入圈”
			statusContent1.setText( R.string.apply_join_group );
		}
		else if ( bean.type == GroupNoticeType.QUIT_GROUP )
		{// 退出圈子：“退出圈”
			statusContent1.setText( R.string.notice_title_quit );
		}
		else if ( bean.type == GroupNoticeType.KICK_OUT_GROUP
				|| bean.type == GroupNoticeType.SET_MANAGER
				|| bean.type == GroupNoticeType.REMOVE_MANAGER )
		{// 踢出圈子、设置管理员、移除管理员：“处理人：XXX”
			String name = mContext.getString( R.string.dealer ) + " : "
					+ "<font color=\"#ef555e\">" + bean.dealuser.nickname + "</font>";
			statusContent1
					.setText( Html.fromHtml( FaceManager.getInstance( mContext )
							.parseIconForString( statusContent1 , mContext , name , 12 )
							.toString( ) ) );
		}
		else
		{
			statusContent1.setText( "" );
		}
		
		// 圈通知下部分正文
		long muid = Common.getInstance( ).loginUser.getUid( );
		if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP
				|| bean.type == GroupNoticeType.REJECT_JOIN_GROUP
				|| bean.type == GroupNoticeType.ALLOW_JOIN_GROUP
				|| bean.type == GroupNoticeType.QUIT_GROUP )
		{// 申请进圈、同意、拒绝、退出：“圈名”
			String name = "<font color=\"#ef555e\">" + bean.groupname + "</font>";
			dealer1.setText( Html.fromHtml( FaceManager.getInstance( mContext )
					.parseIconForString( dealer1 , mContext , name , 12 ).toString( ) ) );
		}
		else if ( bean.type == GroupNoticeType.KICK_OUT_GROUP )
		{// 被踢：“已将XX（你）移出圈”
			if ( muid == bean.targetuser.userid )
			{
				dealer1.setText( mContext.getString( R.string.kick_you_out ) );
			}
			else
			{
				String name = mContext.getString( R.string.kick_sb_out_group ,
						"<font color=\"#ef555e\">" + bean.targetuser.nickname + "</font>" );
				dealer1.setText( Html.fromHtml( FaceManager.getInstance( mContext )
						.parseIconForString( dealer1 , mContext , name , 12 ).toString( ) ) );
			}
		}
		else if ( bean.type == GroupNoticeType.SET_MANAGER )
		{// 设置管理员：“已将XX（你）设为管理员”
			if ( muid == bean.targetuser.userid )
			{
				dealer1.setText( R.string.have_set_you_manager );
			}
			else
			{
				String name = mContext.getString( R.string.have_set_manager ,
						"<font color=\"#ef555e\">" + bean.targetuser.nickname + "</font>" );
				dealer1.setText( Html.fromHtml( FaceManager.getInstance( mContext )
						.parseIconForString( dealer1 , mContext , name , 12 ).toString( ) ) );
			}
		}
		else if ( bean.type == GroupNoticeType.REMOVE_MANAGER )
		{// 移除管理员：“已取消XX（你）的管理员身份”
			if ( muid == bean.targetuser.userid )
			{
				dealer1.setText( mContext.getString( R.string.remove_you_manager , "" ) );
			}
			else
			{
				String name = mContext.getString( R.string.remove_manager , "" ,
						"<font color=\"#ef555e\">" + bean.targetuser.nickname + "</font>" );
				dealer1.setText( Html.fromHtml( FaceManager.getInstance( mContext )
						.parseIconForString( dealer1 , mContext , name , 12 ).toString( ) ) );
			}
		}
		
		// 时间
		noticeTime.setText( TimeFormat.timeFormat4( mContext , bean.datetime ) );
		
		// 同意拒绝操作
		agreeButton.setVisibility( View.INVISIBLE );
		rejectButton.setVisibility( View.INVISIBLE );
		if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP )
		{
			agreeButton.setVisibility( View.VISIBLE );
			rejectButton.setVisibility( View.VISIBLE );
			agreeStatus.setVisibility( View.INVISIBLE );
		}
		else if ( bean.type == GroupNoticeType.ALLOW_JOIN_GROUP )
		{
			agreeStatus.setVisibility( View.VISIBLE );
			agreeStatus.setText( FaceManager.getInstance( mContext ).parseIconForString(
					agreeStatus , mContext ,
					bean.dealuser.nickname + mContext.getString( R.string.have_allow_apply ) ,
					12 ) );
		}
		else if ( bean.type == GroupNoticeType.REJECT_JOIN_GROUP )
		{
			agreeStatus.setVisibility( View.VISIBLE );
			agreeStatus.setText( R.string.have_reject_apply );
		}
		else
		{
			agreeStatus.setVisibility( View.INVISIBLE );
		}
		
	}
	
	private void setListener( )
	{
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById( R.id.iv_left ).setOnClickListener( this );
		targetCard.setOnClickListener( this );
		groupCard.setOnClickListener( this );
		statusContent1.setOnClickListener( this );
		dealer1.setOnClickListener( this );
		agreeButton.setOnClickListener( this );
		rejectButton.setOnClickListener( this );
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left :
			{
				finish( );
			}
				break;
			case R.id.target_card :
			{
				// 跳他人资料
//				long uid = bean.targetuser.userid;
				Intent intent = new Intent();
				intent.setClass(this,OtherInfoActivity.class);
				intent.putExtra("user",user);
				intent.putExtra(Constants.UID,user.getUid());
				startActivity(intent);
//				SpaceOther.launchUser( mContext , uid , bean.targetuser.convertBaseToUser( ) , ChatFromType.UNKONW );//gh
			}
				break;
			case R.id.group_card :
			{
				// 跳圈资料
				Intent intent = new Intent( mContext , GroupInfoActivity.class );
				intent.putExtra( GroupInfoActivity.GROUPID , bean.groupid + "" );
				mContext.startActivity( intent );
			}
				break;
			case R.id.notice_content1 :
			{
				// 跳个人资料
//				long uid = bean.dealuser.userid;
//				SpaceOther.launchUser( mContext , uid , bean.dealuser.convertBaseToUser( ) , ChatFromType.UNKONW );//gh
				Intent intent = new Intent();
				intent.setClass(this, OtherInfoActivity.class);
				intent.putExtra("user",user);
				intent.putExtra(Constants.UID,user.getUid());
				startActivity(intent);
			}
				break;
			case R.id.notice_dealer1 :
			{
				if ( bean.type == GroupNoticeType.APPLY_JOIN_GROUP
						|| bean.type == GroupNoticeType.ALLOW_JOIN_GROUP
						|| bean.type == GroupNoticeType.REJECT_JOIN_GROUP
						|| bean.type == GroupNoticeType.QUIT_GROUP )
				{// 跳圈资料
					Intent intent = new Intent( mContext , GroupInfoActivity.class );
					intent.putExtra( GroupInfoActivity.GROUPID , bean.groupid + "" );
					mContext.startActivity( intent );
				}
				else if ( bean.type == GroupNoticeType.KICK_OUT_GROUP
						|| bean.type == GroupNoticeType.SET_MANAGER
						|| bean.type == GroupNoticeType.REMOVE_MANAGER )
				{// 跳个人资料
//					long uid = bean.targetuser.userid;
//					SpaceOther.launchUser( mContext , uid , bean.targetuser.convertBaseToUser( ) , ChatFromType.UNKONW ) ;//gh
					Intent intent = new Intent();
					intent.setClass(this, OtherInfoActivity.class);
					intent.putExtra(Constants.UID,user.getUid());
					intent.putExtra("user",user);
					startActivity(intent);
				}
			}
				break;
			case R.id.reject_button ://拒绝按钮
			{
				rejectFlag = GroupHttpProtocol.refuseApplyMessage( mContext ,
						String.valueOf( bean.groupid ) ,
						String.valueOf( bean.targetuser.userid ) , this );
			}
				break;
			case R.id.allow_button ://同一按钮
			{
				agreeFlag = GroupHttpProtocol.agreenApplyMessage( mContext ,
						String.valueOf( bean.groupid ) ,
						String.valueOf( bean.targetuser.userid ) , this );
			}
				break;
		}
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		if ( flag == agreeFlag || flag == rejectFlag )
		{
			BaseServerBean bean = GsonUtil.getInstance( ).getServerBean( result ,
					BaseServerBean.class );
			if ( bean.isSuccess( ) )
			{// 同意或拒绝之后服务端不向处理者推送session，由客户端自己改数据库
				GroupAffairModel.getInstance( ).agreeRejectUpadteData(
						mContext ,
                        GroupNoticeDetailActivity.bean,
						flag == agreeFlag ? GroupNoticeType.ALLOW_JOIN_GROUP
								: GroupNoticeType.REJECT_JOIN_GROUP );
				handler.sendEmptyMessage( MSG_REFRESH_VIEW );
			}
			else
			{
				ErrorCode.showError( mContext , result );
				if ( bean.error == 6046 || bean.error == 6047 )
				{// 该消息已被其他管理员处理
					int type = bean.error == 6046 ? GroupNoticeType.ALLOW_JOIN_GROUP
							: GroupNoticeType.REJECT_JOIN_GROUP;
					GroupNoticeBean newBean = GroupAffairModel.getInstance( ).getGroupTypeOne(
							mContext , GroupNoticeDetailActivity.bean.groupid , GroupNoticeDetailActivity.bean.targetuser.userid , type );
					if ( newBean != null )
					{
						GroupNoticeDetailActivity.bean = newBean;
						handler.sendEmptyMessage( MSG_REFRESH_VIEW );
					}
				}
			}
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		ErrorCode.toastError( mContext , e );
	}
	
	Handler handler = new Handler( Looper.getMainLooper( ) )
	{
		public void handleMessage( android.os.Message msg )
		{
			switch ( msg.what )
			{
				case MSG_REFRESH_VIEW :
				{
					initData( );
				}
					break;
			}
		}
    };
	
}
