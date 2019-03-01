
package net.iaround.ui.group.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.comon.SuperActivity;


public class SuccessCreateGroupShareActivity extends SuperActivity implements OnClickListener
{
	public String groupId;
//	public NewThirdPartySharePage tPage;
	
	public static void JumpToSuccessCreatGroupActivity(Activity activity , String groupId ,
													   int requestCode )
	{
		Intent intent = new Intent( activity , SuccessCreateGroupShareActivity.class );
		intent.putExtra( "groupid" , groupId );
		activity.startActivityForResult( intent , requestCode );
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.group_create_success_share_layout );
		
//		if ( isShareToOther )
//		{
//			if ( CommonFunction.isRestartForShare( getBaseContext( ) ) )
//			{
//				return;
//			}
//		} //分享
		
		groupId = getIntent( ).getStringExtra( "groupid" );
		
		initView( );
		
		// 统计分享展现
//		StatisticsApi.statisticEventShareShow( this , Config.PLAT , ShareType.GROUP_INFO , 1 );
	}
	
	private void initView( )
	{
		
//		TextView backTextView = (TextView) findViewById( R.id.title_left_text );
//		backTextView.setText( R.string.dismiss );
//		backTextView.setOnClickListener( this );
		ImageView ivLeft = (ImageView) findViewById(R.id.iv_left);
		ivLeft.setImageResource(R.drawable.title_back);

		TextView title = (TextView) findViewById( R.id.tv_title );
		title.setText( R.string.create_group_success );
		
		Button shareButton = (Button) findViewById( R.id.share_button );
		shareButton.setBackgroundResource( R.drawable.chat_update_background_of_send );
		shareButton.setOnClickListener( this );
		ivLeft.setOnClickListener(this);
		findViewById(R.id.fl_left).setOnClickListener(this);
	}
	
	@Override
	public void onClick( View v )
	{
		// TODO Auto-generated method stub
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
				setResult( RESULT_OK );
				finish( );
				break;
			case R.id.share_button ://邀请
//				tPage = NewThirdPartySharePage.initNewThirdPartySharePage( tPage , mActivity ,
//						SuccessCreateGroupShareActivity.this , v );
//				tPage.setShareTypeInfo( ShareType.GROUP_INFO , 1 , groupId );
				
				GroupInviteActivity.launch( mContext , groupId );
				break;
			default :
				break;
		}
	}
	
	@Override
	public void onGeneralSuccess(final String result , final long flag )
	{
		super.onGeneralSuccess( result , flag );
	}
	
	@Override
	public void onGeneralError( final int e , final long flag )
	{
		super.onGeneralError( e , flag );
	}
	
	@Override
	public void onResume( )
	{
		super.onResume( );
//		if ( tPage != null )
//		{
//			tPage.onResume( );//分享 yuchao
//		}
	}
	
	public void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		// SSO 授权回调
		// 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
//		if ( tPage != null )
//		{
//			tPage.onActivityResult( requestCode , resultCode , data );//yuchao
//		}
	}
	
	protected void onSaveInstanceState( Bundle outState )
	{
		super.onSaveInstanceState( outState );
//		if ( tPage != null )
//		{
//			tPage.onSaveInstanceState( outState );//yuchao
//		}
	}
	
	protected void onDestroy( )
	{
		// TODO Auto-generated method stub
		try
		{
			super.onDestroy( );
		}
		catch ( Exception e )
		{
			// TODO: handle exception
		}
	}
}
