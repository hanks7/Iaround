
package net.iaround.ui.group.activity;


import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GroupHttpProtocol;
import net.iaround.pay.activity.FragmentPayDiamond;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class GroupRenewServiceActivity extends SuperActivity implements OnClickListener,HttpCallBack
{
	
	/** 标题 */
	private TextView mTitleName;
	/** 返回按钮 */
	private ImageView mTitleBack;
	/** 圈子年度服务内容 */
	private TextView mRenewContent;
	/** 续费按钮 */
	private Button mRenewBtn;
	
	private String groupId;
	private String groupName;
	private String expireDate;
	private int renewCost = -1;
	
	private long GET_RENEW_INFO_FLAG;
	private long POST_RENEW_PAY_FLAG;
	
	/** 加载框 */
	private Dialog mWaitDialog;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_renew_service );
		
		groupId = getIntent( ).getStringExtra( "groupid" );
		groupName = getIntent( ).getStringExtra( "groupname" );
		CommonFunction.log( "group" , "groupName***" + groupName );
		
		initViews( );
		setListeners( );
		initData( );
	}
	
	/**
	 * @Title: initViews
	 * @Description: 初始化控件
	 */
	private void initViews( )
	{
		mTitleName = (TextView) findViewById( R.id.title_name );
		mTitleBack = (ImageView) findViewById( R.id.title_back );
		
		mRenewContent = (TextView) findViewById( R.id.renew_content );
		mRenewBtn = (Button) findViewById( R.id.btn_renew );
		
		mTitleName.setText( getString( R.string.group_renew_service_title ) );
		
		mWaitDialog = DialogUtil.getProgressDialog( mContext , "" ,
				getString( R.string.please_wait ) , null );
	}
	
	/**
	 * @Title: setListeners
	 * @Description: 设置监听器
	 */
	private void setListeners( )
	{
		mTitleBack.setOnClickListener( this );
		mRenewBtn.setOnClickListener( this );
	}
	
	/**
	 * @Title: initData
	 * @Description: 初始化数据
	 */
	private void initData( )
	{
		showWaitDialog( true );
		GET_RENEW_INFO_FLAG = GroupHttpProtocol.getGroupRenewInfo( mContext , groupId , this );
	}
	
	@Override
	public void onClick( View v )
	{
		// TODO Auto-generated method stub
		if ( v.equals( mTitleBack ) )
		{
			Intent intent = new Intent( );
			intent.putExtra( "expires" , expireDate );
			setResult( RESULT_OK , intent );
			finish( );
		}
		else if ( v.equals( mRenewBtn ) )
		{
			if ( renewCost != -1 )
			{
				DialogUtil.showTowButtonDialog( mContext ,
						getString( R.string.group_confirm_renew_title ) , String.format(
								getString( R.string.group_confirm_renew_msg ) , renewCost ) ,
						getString( R.string.cancel ) , getString( R.string.ok ) , null ,
						new OnClickListener( )
						{
							@Override
							public void onClick( View v )
							{
								// TODO Auto-generated method stub
								showWaitDialog( true );
//								POST_RENEW_PAY_FLAG = GoldHttpProtocol.payGroupRenewService(
//										mContext , groupId , GroupRenewServiceActivity.this ); yuchao
							}
						} );
			}
			/*
			 * else { CommonFunction.toastMsg( mContext , mContext.getString(
			 * R.string.e_107 ) ); }
			 */
			
		}
	}
	
	private void refreshView( String result )
	{
		JSONObject jsonObject;
		try
		{
			jsonObject = new JSONObject( result );
			
			if ( jsonObject.has( "cost" ) )
			{
				renewCost = jsonObject.optInt( "cost" );
				mRenewBtn.setText( String.format(
						getString( R.string.group_renew_service_btn_text ) , renewCost ) );
			}
			
			long expires = jsonObject.optLong( "expires" );
			SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd" );
			expireDate = format.format( new Date( expires ) );
			
			String name = String.format(
					getString( R.string.group_renew_service_content_part1 ) , groupName );
			SpannableString part1 = FaceManager.getInstance( mContext ).parseIconForString(
					mContext , name , 20 , null );
								
			SpannableString expireDatePart = new SpannableString( expireDate );
			ForegroundColorSpan redSpan = new ForegroundColorSpan( Color.RED );
			expireDatePart.setSpan( redSpan , 0 , expireDate.length( ) ,
					Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
			
			String part2 = getString( R.string.group_renew_service_content_part2 );
			
			//由于显示内容含有不同的格式，所以使用append方法来实现
			mRenewContent.append( part1 );
			mRenewContent.append( expireDatePart );
			mRenewContent.append( part2 );		
		}
		catch ( JSONException e )
		{
			// TODO Auto-generated catch block
			e.printStackTrace( );
		}
	}
	
	@Override
	public void onGeneralSuccess(final String result , final long flag )
	{
		showWaitDialog( false );
		super.onGeneralSuccess( result , flag );
		if ( result.contains( "\"status\":200" ) )
		{
			if ( GET_RENEW_INFO_FLAG == flag )
			{
				refreshView( result );
			}
//			else if ( POST_RENEW_PAY_FLAG == flag )
//			{
//				CommonFunction.toastMsg( mContext , getString( R.string.group_renew_success ) );
//				refreshView( result );
//			}yuchao
		}
		else
		{
			if ( result.contains( "\"error\":5930" ) )
			{
				// 钻石不足
				String msg = getString( R.string.diamond_not_enough );
				DialogUtil.showTowButtonDialog( mContext , "" , msg ,
						getString( R.string.cancel ) , getString( R.string.buy_diamonds ) ,
						null , new OnClickListener( )
						{
							@Override
							public void onClick( View v )
							{
								FragmentPayDiamond.jumpPayDiamondActivity(mContext);
							}
						} );
			}
			else
			{
				JSONObject jsonObject;
				try
				{
					jsonObject = new JSONObject( result );
					ErrorCode.toastError( mContext , jsonObject.optInt( "error" ) );
				}
				catch ( JSONException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace( );
				}
			}
		}
		
	}
	
	@Override
	public void onGeneralError( final int e , final long flag )
	{
		showWaitDialog( false );
		if ( GET_RENEW_INFO_FLAG == flag )
		{
			ErrorCode.toastError( mContext , e );
		}
//		else if ( POST_RENEW_PAY_FLAG == flag )
//		{
//			if ( e == 5930 )
//			{
//				// 钻石不足
//				String msg = getString( R.string.diamond_not_enough );
//				DialogUtil.showTowButtonDialog( mContext , "" , msg ,
//						getString( R.string.cancel ) , getString( R.string.buy_diamonds ) ,
//						null , new OnClickListener( )
//						{
//							@Override
//							public void onClick( View v )
//							{
//								FragmentPayDiamond.jumpPayDiamondActivity(mContext);
//							}
//						} );
//			}
//			else
//			{
//				ErrorCode.toastError( mContext , e );
//			}
//		}  yuchao
		
		super.onGeneralError( e , flag );
	}
	
	/**
	 * @Title: showWaitDialog
	 * @Description: 显示加载框
	 * @param isShow
	 */
	private void showWaitDialog( boolean isShow )
	{
		if ( mWaitDialog != null )
		{
			if ( isShow )
			{
				mWaitDialog.show( );
			}
			else
			{
				mWaitDialog.hide( );
			}
		}
	}
	
	@Override
	protected void onDestroy( )
	{
		if ( mWaitDialog != null )
		{
			mWaitDialog.dismiss( );
			mWaitDialog = null;
		}
		super.onDestroy( );
	}
}
