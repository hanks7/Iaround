package net.iaround.service;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import net.iaround.conf.ErrorCode;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-3-25 下午2:19:59
 * @ClassName LocaleChangeReceiver
 * @Description: 用于当系统语言发生改变后，应用重新启动
 */

public class LocaleChangeReceiver extends BroadcastReceiver
{

	private static boolean setPhoneStateListener = false;
	private String incoming_number;

	@Override
	public void onReceive(Context mContext, Intent intent )
	{
		if ( intent == null )
			return;
		String action = intent.getAction( );
		if ( TextUtils.isEmpty( action ) )
			return;

		if ( action.compareTo( Intent.ACTION_LOCALE_CHANGED ) == 0 )
		{
			MainFragmentActivity.bIsLocaleChange = true;
			ErrorCode.reset( );
			CloseAllActivity.getInstance( ).backToMainActivity( );
		}
		else if ( action.equals( Intent.ACTION_NEW_OUTGOING_CALL ) )
		{
			CommonFunction
				.log( "sherlock", "LocaleChangeReceiver.onReceive ACTION_NEW_OUTGOING_CALL 111" );
		}
		else
		{
			//如果是来电
			if ( !setPhoneStateListener )
			{
				setPhoneStateListener = true;
				TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService( Service.TELEPHONY_SERVICE );

				tm.listen( lisntener, PhoneStateListener.LISTEN_CALL_STATE );
			}
		}
	}

	PhoneStateListener lisntener = new PhoneStateListener( )
	{
		@Override
		public void onCallStateChanged( int state, String incomingNumber )
		{
			super.onCallStateChanged( state, incomingNumber );
			switch ( state )
			{
				case TelephonyManager.CALL_STATE_IDLE:
				{
					CommonFunction.log( "sherlock", "LocaleChangeReceiver.onCallStateChanged 挂断" );
				}
				break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
				{
					CommonFunction.log( "sherlock", "LocaleChangeReceiver.onCallStateChanged 接听" );
				}
				break;
				case TelephonyManager.CALL_STATE_RINGING:
				{
					CommonFunction.log( "sherlock",
						"LocaleChangeReceiver.onCallStateChanged 响铃:来电号码" + incomingNumber );
				}
				break;
			}
		}
	};
	
}
