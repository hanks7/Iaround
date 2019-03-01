
package net.iaround.ui.group.activity;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.protocol.UserHttp2Protocol;
import net.iaround.entity.type.ReportTargetType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.space.more.ChatRecordReport;

import java.util.ArrayList;


/**
 * 举报页面界面,选着原因和选择举报的聊天记录<br>
 * Note:reportTargetType,举报的内容的是单个用户,在圈聊中举报的也是某个人的记录<br>
 * reportFrom只是为了取数据时候,从圈聊取还是私聊去<br>
 * 
 * @author KevinSu
 */

public class ReportChatAcitvity extends SuperActivity implements View.OnClickListener
{
	
	private ImageView ivLeft;
	private TextView tvTitle;
	private ImageView ivRight;
	
	private RelativeLayout rlMore;
	private TextView tvCountSeleted;
	
	private RadioGroup rgReason;
	private ArrayList< RadioButton > mRadios = new ArrayList< RadioButton >( );
	
	private long REPORT_FLAG = 0;
	
	private int GET_REPORT_CONTENT_FLAG = 1;
	
	public static String REPORT_CONTENT = "REPORT_CONTENT";
	public static String REPORT_COUNT = "REPORT_COUNT";
	
	private String mReportContent;
	private int mReportCount;
	
	public final static String USER_ID_KEY = "user_id";
	public final static String REPORT_FROM_KEY = "report_from";
	public final static String GROUP_ID_KEY = "group_id";
	
	private long userId;
	private final int reportTargetType = ReportTargetType.CHAT_RECORD;// 举报类型@ReportTargetType
	private int reportFrom;// @ChatRecordReport.TYPE_PERSON OR
							// ChatRecordReport.TYPE_ROOM
	private long groupId;//

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_report_chat_acitvity );
		
		userId = getIntent( ).getLongExtra( ReportChatAcitvity.USER_ID_KEY , 0 );
		reportFrom = getIntent( ).getIntExtra( ReportChatAcitvity.REPORT_FROM_KEY ,
				ChatRecordReport.TYPE_PERSON );
		groupId = getIntent( ).getLongExtra( ReportChatAcitvity.GROUP_ID_KEY , 0 );
		
		initView( );
		
		showSubmitButton( );
	}
	
	private void initView( )
	{
		ivLeft = (ImageView) findViewById( R.id.iv_left );
//		tvLeft.setText( R.string.cancel );
		ivLeft.setOnClickListener( this );
		findViewById(R.id.fl_left).setOnClickListener(this);

		ivRight = (ImageView) findViewById( R.id.iv_right );
//		tvRight.setText( R.string.submit );
		ivRight.setImageResource(R.drawable.icon_publish);
		ivRight.setVisibility(View.GONE);
		ivRight.setOnClickListener( this );
		
		tvTitle = (TextView) findViewById( R.id.tv_title );
		tvTitle.setText( R.string.report );
		
		rlMore = (RelativeLayout) findViewById( R.id.rlMore );
		rlMore.setOnClickListener( this );
		tvCountSeleted = (TextView) findViewById( R.id.tvCountSeleted );
		
		rgReason = (RadioGroup) findViewById( R.id.rgReason );
		
		ArrayList< View > tmpViews = new ArrayList< View >( );
		rgReason.findViewsWithText( tmpViews ,
				getResources( ).getString( R.string.dynamic_report_sex ) ,
				View.FIND_VIEWS_WITH_TEXT );
		
		String[ ] radiosNameList = getResources( ).getStringArray( R.array.report_types );
		
		if ( radiosNameList.length != tmpViews.size( ) )
		{
			throw new IllegalArgumentException( "report type count not equal to radio buttons" );
		}
		
		int count = tmpViews.size( );
		for ( int i = 0 ; i < count ; i++ )
		{
			RadioButton mButton = (RadioButton) tmpViews.get( i );
			mButton.setText( radiosNameList[ i ] );
			mButton.setId( i + 1 );
			mRadios.add( mButton );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		
		if ( v.getId() == R.id.fl_left || v.getId() == R.id.iv_left )
		{
			setResult( RESULT_CANCELED );
			finish( );
		}
		else if ( v.equals( ivRight ) )
		{
			report( );
		}
		else if ( v.equals( rlMore ) )
		{
			Intent intent = new Intent( );
			intent.setClass( mContext , ChatRecordReport.class );
			intent.putExtra( ReportChatAcitvity.USER_ID_KEY , userId );
			intent.putExtra( ReportChatAcitvity.REPORT_FROM_KEY , reportFrom );
			intent.putExtra( ReportChatAcitvity.GROUP_ID_KEY , groupId );
			startActivityForResult( intent , GET_REPORT_CONTENT_FLAG );
		}
	}
	
	@Override
	protected void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		super.onActivityResult( requestCode , resultCode , data );
		if ( requestCode == GET_REPORT_CONTENT_FLAG )
		{
			if ( RESULT_OK == resultCode )
			{
				if ( data == null )
					return;
				
				mReportContent = data.getStringExtra( REPORT_CONTENT );
				mReportCount = data.getIntExtra( REPORT_COUNT , 0 );
				
				if ( mReportCount <= 0 )
				{
					tvCountSeleted.setText( "无" );
				}
				else
				{
					tvCountSeleted.setText( mReportCount + "条记录" );
				}
				
				showSubmitButton( );
				
			}
			else if ( RESULT_CANCELED == resultCode )
			{
				
			}
		}
	}
	
	private void showSubmitButton( )
	{
		if ( mReportCount > 0 && !TextUtils.isEmpty( mReportContent ) )
		{
//			tvRight.setTextColor( getResColor( R.color.c_ffffff ) );
//			tvRight.setEnabled( true );
			ivRight.setVisibility(View.VISIBLE);
			ivRight.setEnabled(true);
		}
		else
		{
//			tvRight.setTextColor( getResColor( R.color.c_333333 ) );
//			tvRight.setEnabled( false );
			ivRight.setVisibility(View.GONE);
			ivRight.setEnabled(false);
		}
	}
	
	private void report( )
	{
		int type = rgReason.getCheckedRadioButtonId( );
		if ( type < 0 )
		{
			String title = getResString( R.string.dialog_title );
			String message = getResString( R.string.report_select_reason_message );
			String btnStr = getResString( R.string.ok );
			DialogUtil.showOneButtonDialog( mContext , title , message , btnStr , null );
		}
		else
		{
			String targetid = String.valueOf( userId );
			REPORT_FLAG = UserHttp2Protocol.systemReport( mContext , type , reportTargetType ,
					targetid , mReportContent , this );
		}
		
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		if ( REPORT_FLAG == flag )
		{
			if ( Constant.isSuccess( result ) )
				CommonFunction.toastMsg( mContext , R.string.report_return_title );
			else
				ErrorCode.showError( mContext , result );
			finish( );
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		if ( REPORT_FLAG == flag )
		{
			CommonFunction.toastMsg( mContext , R.string.operate_fail );
			finish( );
		}
	}
}
