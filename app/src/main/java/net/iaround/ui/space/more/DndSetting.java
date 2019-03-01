
package net.iaround.ui.space.more;


import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.BaseFragmentActivity;
import net.iaround.ui.view.FlagImageView;


/**
 * @Description: 免打扰设置
 * @author tanzy
 * @date 2015-5-12
 */
public class DndSetting extends BaseFragmentActivity implements OnClickListener,
		OnCheckedChangeListener
{
	private ImageView title_back;
	private TextView title_name;
	private TextView tvStartTime;
	private TextView tvStopTime;
	private TextView showStartTime;
	private TextView showStopTime;
	private SeekBar sbStartTime;
	private SeekBar sbStopTime;
	private CheckBox cbDndSetting;
//	private FlagImageView cbDndSetting;
	private LinearLayout settingLayout;
	
	private SharedPreferenceUtil sp;
	private String dndKey;
	private String dndStartTimeKey;
	private String dndEndTimeKey;
	
	private boolean oldDndValue;// 免打扰设置是否开启
	private int[ ] oldDndTimeValue = new int[ 2 ];// [0]为startTime [1]为endTime
	
	public static int REQ_FROM_PUSH_NOTIFICATION = 10086;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.dnd_setting );
		initView( );
		initData( );
		setListener( );
	}
	
	private void initView( )
	{
		title_back = (ImageView) findViewById( R.id.iv_left );
		title_name = (TextView) findViewById( R.id.tv_title );
		title_name.setText( R.string.setting_notice_avoid_trouble );
		
		cbDndSetting = (CheckBox) findViewById( R.id.cbDndSetting );
//		cbDndSetting = (FlagImageView) findViewById( R.id.cbDndSetting );
		tvStartTime = (TextView) findViewById( R.id.tvStartTime );
		tvStopTime = (TextView) findViewById( R.id.tvStopTime );
		showStartTime = (TextView) findViewById( R.id.show_start_time );
		showStopTime = (TextView) findViewById( R.id.show_end_time );
		sbStartTime = (SeekBar) findViewById( R.id.sbStartTime );
		sbStopTime = (SeekBar) findViewById( R.id.sbStopTime );
		settingLayout = (LinearLayout) findViewById( R.id.dnd_setting_linear );
	}
	
	private void initData( )
	{
		long uid = Common.getInstance( ).loginUser.getUid( );
		sp = SharedPreferenceUtil.getInstance( mContext );
		dndKey = SharedPreferenceUtil.DND_SETTING + uid;
		dndStartTimeKey = SharedPreferenceUtil.REC_START_TIME + uid;
		dndEndTimeKey = SharedPreferenceUtil.REC_END_TIME + uid;

		oldDndValue = sp.getBoolean( dndKey , false );
		oldDndTimeValue[ 0 ] = sp.getInt( dndStartTimeKey , 0 );
		oldDndTimeValue[ 1 ] = sp.getInt( dndEndTimeKey , 0 );

		cbDndSetting.setChecked( oldDndValue );
//		cbDndSetting.setState(oldDndValue);
		settingLayout.setVisibility( oldDndValue ? View.VISIBLE : View.GONE );

		showTime( oldDndTimeValue[ 0 ] , oldDndTimeValue[ 1 ] );
		sbStartTime.setProgress( oldDndTimeValue[ 0 ] );
		sbStopTime.setProgress( oldDndTimeValue[ 1 ] );

	}
	
	private void setListener( )
	{
		findViewById(R.id.fl_left).setOnClickListener(this);
		title_back.setOnClickListener( this );
		cbDndSetting.setOnCheckedChangeListener( this );
//		cbDndSetting.setOnClickListener(this);
		sbStartTime.setOnSeekBarChangeListener( osbListner );
		sbStopTime.setOnSeekBarChangeListener( osbListner );
	}
	
	OnSeekBarChangeListener osbListner = new OnSeekBarChangeListener( )
	{
		public void onStopTrackingTouch( SeekBar seekBar )
		{
		}
		
		public void onStartTrackingTouch( SeekBar seekBar )
		{
		}
		
		public void onProgressChanged(SeekBar seekBar , int progress , boolean fromUser )
		{
			if ( seekBar.equals( sbStartTime ) )
				showTime( progress , -1 );
			else if ( seekBar.equals( sbStopTime ) )
				showTime( -1 , progress );
		}
	};
	
	private void showTime( int startTime , int endTime )
	{
		if ( startTime >= 0 )
		{
			tvStartTime.setText( startTime + "" );
			showStartTime.setText( TimeFormat.getTimeShort( startTime ) );
		}
		if ( endTime >= 0 )
		{
			tvStopTime.setText( endTime + "" );
			showStopTime.setText( TimeFormat.getTimeShort( endTime ) );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left:
			{
				uploadData( );
			}
				break;
			case R.id.cbDndSetting:
//				cbDndSetting.setState(!cbDndSetting.isSelected());
//				settingLayout.setVisibility(cbDndSetting.isSelected()?View.VISIBLE:View.GONE);
				break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView , boolean isChecked )
	{
		settingLayout.setVisibility( isChecked ? View.VISIBLE : View.GONE );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			uploadData( );
		}
		return super.onKeyDown( keyCode , event );
	}
	
	/** 上报免打扰开关和时间段 */
	private void uploadData( )
	{
		boolean newOnOff = cbDndSetting.isChecked() ;
		if ( newOnOff != oldDndValue )
		{
			sp.putBoolean( dndKey , newOnOff );
			UserHttpProtocol.userPrivacyUpdate( mContext ,
					PrivateSettingType.NON_DISTURB_SWITCH , newOnOff ? "y" : "n" , null );
			setResult( Activity.RESULT_OK );
		}

		int newStart = sbStartTime.getProgress( );
		int newEnd = sbStopTime.getProgress( );
		if ( newStart != oldDndTimeValue[ 0 ] || newEnd != oldDndTimeValue[ 1 ] )
		{
			sp.putInt( dndStartTimeKey , newStart );
			sp.putInt( dndEndTimeKey , newEnd );
			
			//6.0去掉时区计算
//			// 计算时区时差，将时间换算成东八区的时间上报
//			int msecOffset = TimeZone.getDefault( ).getRawOffset( )
//					- TimeZone.getTimeZone( "GMT+08:00" ).getRawOffset( );
//			int offset = msecOffset / 3600000;
//			int start = newStart - offset > 23 ? 23 : ( newStart - offset );
//			int end = newEnd - offset > 23 ? 23 : ( newEnd - offset );
//			String content = end + ":00-" + start + ":00";

			String content = newStart+":00-"+newEnd+":00";
			CommonFunction.log( "System.out" , "Push data time has been set as: " + content
					+ " (GMT+08:00)" );
			UserHttpProtocol.userPrivacyUpdate( mContext ,
					PrivateSettingType.REC_NOTIFICATION_PERIOD , content , null );
			setResult( Activity.RESULT_OK );
		}
		finish( );
	}
}
