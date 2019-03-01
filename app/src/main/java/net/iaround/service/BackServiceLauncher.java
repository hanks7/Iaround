
package net.iaround.service;


import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * BackService启动广播接收器，可以接收解锁和主程序发来的广播
 * 
 * @author 余勋杰
 */
public class BackServiceLauncher extends BroadcastReceiver
{
	
	public void onReceive( Context context , Intent intent )
	{
		if ( Config.BACK_SERVICE_ENABLE && !CommonFunction.uiRunning )
		{
			Intent i = new Intent( context , BackService.class );
			context.startService( i );
		}
	}
}
