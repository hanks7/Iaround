
package net.iaround.ui.group.activity;


import net.iaround.R;
import net.iaround.model.im.Me;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.comon.SuperView;


/**
 * 我的资料页
 */
public class SpaceMe extends SuperView
{
	public static final int REQ_SPACE_CHILD_PAGE = 0x0fff0002;
	public static final int MSG_PROGRESS_UPDATE = 0xffff0;
	public static final int MSG_CLOSE_PD = 0xffff1;
	public static final int MSG_SHOW_ERROR = 0xffff2;
	private static final int MSG_BASIC_INFOR = 3;
	public static final int MSG_REFRESH = 0xffff4;
	public static final int MSG_ERROR = 0xffff5;
	public static final int MSG_IMAGE_CHANGED = 0xffff7;
	
	private static boolean sForceRef;// 强制刷新
	public static SpaceMe sSpaceMe;
	
	private Me mMySelfInfo;
//	private PullToRefSpaceView mPullToRefSpaceView;
	
	
	public static void performRefresh( boolean forceRef )
	{
		SpaceMe.sForceRef = forceRef;
	}
	
	public SpaceMe(SuperActivity context )
	{
		super( context , R.layout.space_me_new );
//		sSpaceMe = this;
//		mMySelfInfo = Common.getInstance( ).loginUser;
//		initView( );
//		refreshTitle( );
//		
////		showPerfectTip( ); 
//		
//		postHandlerDelayed( new Runnable( )
//		{
//			public void run( )
//			{
//				reqUserData( false );
//			}
//		} );
	}
	

	public void refreshBasicInfo( Me user )
	{
		sForceRef = false;
		mMySelfInfo = user;
//		mPullToRefSpaceView.setUser( mMySelfInfo );
//		refreshTitle( );
//		mPullToRefSpaceView.performRefresh( );
	}
	
	
	
	/**
	 * 从刷新用户分享途径
	 */
	public void refreshShareAccount( )
	{
//		mPullToRefSpaceView.refreshShareAccountShowView( mMySelfInfo );
	}
	
}
