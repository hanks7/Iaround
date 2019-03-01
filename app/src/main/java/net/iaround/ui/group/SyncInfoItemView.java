
package net.iaround.ui.group;


import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.model.im.SyncInfo;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.datamodel.UserInfo;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.group.activity.GroupMemberViewActivity;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;


/** 贴吧和圈子话题详情页顶部的同步View */
public class SyncInfoItemView extends RelativeLayout
{
	private Context context;
	
	private TextView syncFirstText;
	private TextView syncName;// 贴吧名称或圈子名称
	private TextView syncSecondText;
	private TextView syncEntranceBtn;// 跳转的按钮
	private LinearLayout iconsView;// 头像列表View
	private ImageView arrow;// 箭头
	
	private long userid = 0;
	
	public SyncInfoItemView(Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	
	public SyncInfoItemView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		this.context = context;
	}
	
	public SyncInfoItemView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		this.context = context;
	}
	
	public void refreshView( final SyncInfo.SyncItemBean syncInfo )
	{
		
		View itemView;
		OnClickListener entranceClickListener = null;
		OnClickListener arrowClickListener = null;
		if ( syncInfo.sync == SyncInfo.FROM_DYNAMIC )
		{
			if ( syncName == null )
			{
				itemView = LayoutInflater.from( getContext( ) ).inflate(
						R.layout.sync_from_dynamic_view , this );
				syncFirstText = (TextView) itemView.findViewById( R.id.sync_first_text );
				syncName = (TextView) itemView.findViewById( R.id.sync_name );
				syncSecondText = (TextView) itemView.findViewById( R.id.sync_second_text );
				syncEntranceBtn = (TextView) itemView.findViewById( R.id.sync_entrance );
			}
			
			entranceClickListener = new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					// TODO Auto-generated method stub
					// 跳转到该用户个人资料页面
//					SpaceOther.launch( getContext( ) , syncInfo.syncvalue ,
//							ProfileEntrance.POSTBAR  );
					Intent intent = new Intent(getContext(), UserInfoActivity.class);
					intent.putExtra(Constants.UID, Common.getInstance().loginUser.getUid());
					getContext().startActivity(intent);
				}
			};
			
			syncFirstText.setText( context
					.getString( R.string.postbar_sync_dynamic_first_text ) );
			syncSecondText.setText( context
					.getString( R.string.postbar_sync_dynamic_second_text ) );
		}
		else if ( syncInfo.sync == SyncInfo.FROM_GROUP )
		{
			if ( syncName == null )
			{
				itemView = LayoutInflater.from( getContext( ) ).inflate(
						R.layout.sync_from_group_view , this );
				syncFirstText = (TextView) itemView.findViewById( R.id.tvTitle );
				syncName = (TextView) itemView.findViewById( R.id.tvFrom );
				syncEntranceBtn = (TextView) itemView.findViewById( R.id.tvLink );
				arrow = (ImageView) itemView.findViewById( R.id.ivArrow );
				iconsView = (LinearLayout) itemView.findViewById( R.id.llMembers );
			}
			
			entranceClickListener = new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					// TODO Auto-generated method stub
					// 点击去看看跳转到圈基本资料页面
					Intent intent = new Intent( context , GroupInfoActivity.class );
					intent.putExtra( GroupInfoActivity.GROUPID ,
							String.valueOf( syncInfo.syncvalue ) );
					context.startActivity( intent );
				}
			};
			
			arrowClickListener = new OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					// TODO Auto-generated method stub
					// 点击>跳转到圈成员列表页
					Intent i = new Intent( context , GroupMemberViewActivity.class );
					i.putExtra( "groupId" , String.valueOf( syncInfo.syncvalue ) );
					i.putExtra( "groupRole" , 2 );
					i.putExtra( "groupName" , syncInfo.getSyncName( ) );
					i.putExtra( "isShowManage" , false );
					context.startActivity( i );
				}
			};
			
			syncFirstText
					.setText( context.getString( R.string.postbar_sync_group_first_text ) );
		}
		else if ( syncInfo.sync == SyncInfo.FROM_POSTBAR )
		{
			//分享   YC
//			if ( syncName == null )
//			{
//				itemView = LayoutInflater.from( getContext( ) ).inflate(
//						R.layout.dynamic_share_sync_bar , this );
//				syncFirstText = (TextView) itemView.findViewById( R.id.tvTitle );
//				syncName = (TextView) itemView.findViewById( R.id.tvFrom );
//				syncEntranceBtn = (TextView) itemView.findViewById( R.id.tvLink );
//				arrow = (ImageView) itemView.findViewById( R.id.ivArrow );
//				iconsView = (LinearLayout) itemView.findViewById( R.id.llMembers );
//			}
			
//			entranceClickListener = new OnClickListener( )
//			{
//
//				@Override
//				public void onClick( View v )
//				{
//					// TODO Auto-generated method stub
//					// 点击去看看跳转到贴吧的资料页面
//					ThemeSquareActivity.launch( context , syncInfo.syncvalue ,
//							syncInfo.getSyncName( ) );
//				}
//			};
			
//			arrowClickListener = new OnClickListener( )
//			{
//
//				@Override
//				public void onClick( View v )
//				{
//					// TODO Auto-generated method stub
//					// 点击>跳转到贴吧附件成员列表
//					ThemeNearbyUserListActivity.launch( context , syncInfo.syncvalue );
//				}
//			};
			
//			syncFirstText
//					.setText( context.getString( R.string.postbar_sync_group_first_text ) );
		}
		
		CommonFunction.log( "sync" , "SyncName***" + syncInfo.getSyncName( ) );
		SpannableString nameStr = FaceManager.getInstance( context )
				.parseIconForStringBaseline( syncName , context , syncInfo.getSyncName( ) , 0 );
		syncName.setText( nameStr );
		
		syncEntranceBtn.setOnClickListener( entranceClickListener );
		
		if ( arrow != null )
		{
			arrow.setOnClickListener( arrowClickListener );
		}
		
		if ( iconsView != null )
		{
			int offset = CommonFunction.dipToPx( context , 11 );
			int memberPicSize = ( CommonFunction.getScreenPixWidth( context ) - offset * 6 ) / 10;
			
			int defRes = R.drawable.default_face_small;
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( memberPicSize ,
					memberPicSize );
			
			ArrayList<UserInfo> imageList = syncInfo.getMemberList( );
			
			if ( iconsView.getChildCount( ) > 0 )
			{
				iconsView.removeAllViews( );
			}
			for ( int i = 0 ; i < imageList.size( ) ; i++ )
			{
				HeadPhotoView iv = new HeadPhotoView( context );
				iv.setScaleType( ScaleType.CENTER_CROP );
				params.setMargins( 0 , 0 , offset , 0 );
				iv.setLayoutParams( params );
				
				iv.execute( defRes , imageList.get( i ).convertBaseToUser(),null );

				userid = imageList.get( i ).userid;

				iconsView.addView( iv );
			}
		}
		
	}
}
