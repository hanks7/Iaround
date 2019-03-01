package net.iaround.ui.comon;

import net.iaround.conf.Common;
import net.iaround.connector.ConnectorManage;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.BaseFragmentActivity;

/**
 * JS调用Android原生 7.0.2后废弃掉
 */
public class BaseWebViewActivity extends BaseFragmentActivity
{

	@android.webkit.JavascriptInterface
	public String getKey( )
	{
		return ConnectorManage.getInstance( mContext ).getKey( );
	}

	@android.webkit.JavascriptInterface
	public String jsGetUid( )
	{
		String suid = String.valueOf( Common.getInstance( ).loginUser.getUid( ) );
		CommonFunction.log( "BaseWebViewActivity", "js call geUid and return " + suid );
		return suid;
	}

}
