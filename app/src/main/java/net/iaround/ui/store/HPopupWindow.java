
package net.iaround.ui.store;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;


/**
 * 对话框效果的PopupWindow
 * 
 * @author Huyunfeng Email:my519820363@gmail.com
 * 
 */
public class HPopupWindow
{
	private Context mContext;
	private PopupWindow mPopupWindow;
	private View mDownPointView , mUpPointView;
	private int mPopupWidth;
	private static int _screenWidth;
	
	/**
	 * 创建一个HPopupWindow对象
	 * 
	 * @param context
	 * @param contentViewId
	 * @param width
	 *            PopWindow的宽
	 * @param height
	 *            PopWindow的高
	 * @param upPointViewId
	 *            PopWindow上面的对话图标的id
	 * @param downPointViewId
	 *            PopWindow下面的对话图标的id
	 */
	public HPopupWindow(Context context , int contentViewId , int width , int height ,
                        int upPointViewId , int downPointViewId )
	{
		this( context , ( (LayoutInflater) context
				.getSystemService( Activity.LAYOUT_INFLATER_SERVICE ) ).inflate(
				contentViewId , null ) , width , height , upPointViewId , downPointViewId );
	}
	
	/**
	 * 创建一个HPopupWindow对象
	 * 
	 * @param context
	 * @param contentView
	 * @param width
	 *            PopWindow的宽
	 * @param height
	 *            PopWindow的高
	 * @param upPointViewId
	 *            PopWindow上面的对话图标的id
	 * @param downPointViewId
	 *            PopWindow下面的对话图标的id
	 */
	public HPopupWindow(Context context , View contentView , int width , int height ,
                        int upPointViewId , int downPointViewId )
	{
		mPopupWindow = new PopupWindow( contentView , width , height );
		mDownPointView = contentView.findViewById( downPointViewId );
		mUpPointView = contentView.findViewById( upPointViewId );
		mPopupWidth = width;
		mContext = context;
		
		if ( _screenWidth == 0 )
		{
			_screenWidth = mContext.getResources( ).getDisplayMetrics( ).widthPixels;
		}
	}
	
	public void setOutsideTouchable( boolean touchable )
	{
		mPopupWindow.setOutsideTouchable( touchable );
	}
	
	public void setFocusable( boolean focusable )
	{
		mPopupWindow.setFocusable( focusable );
	}
	
	public void setTouchable( boolean touchable )
	{
		mPopupWindow.setTouchable( touchable );
	}
	
	public void setAnimationStyle( int animationStyle )
	{
		mPopupWindow.setAnimationStyle( animationStyle );
	}
	
	public void dismiss( )
	{
		if ( mPopupWindow.isShowing( ) )
		{
			mPopupWindow.dismiss( );
		}
	}
	
	public void showAsDropDown( View anchor )
	{
		if ( mPopupWindow.isShowing( ) )
		{
			mPopupWindow.dismiss( );
		}
		mPopupWindow.showAsDropDown( anchor );
		
		int[ ] anchorLocation = new int[ 2 ];
		anchor.getLocationOnScreen( anchorLocation );
		int location = 0;
		int viewLeftOnScreen = anchorLocation[ 0 ];
		int viewRightOnscreen = _screenWidth - viewLeftOnScreen;
		if ( viewRightOnscreen < mPopupWidth )
		{
			location = viewLeftOnScreen - _screenWidth + mPopupWidth
					+ anchor.getMeasuredWidth( ) / 2;
		}
		else
		{
			location = anchor.getMeasuredWidth( ) / 2;
		}
		
		if ( mPopupWindow.isAboveAnchor( ) )
		{
			mUpPointView.setVisibility( View.INVISIBLE );
			mDownPointView.setVisibility( View.VISIBLE );
			LayoutParams layoutParams = (LayoutParams) mDownPointView.getLayoutParams( );
			layoutParams.setMargins( location , 0 , 0 , 0 );
			mDownPointView.setLayoutParams( layoutParams );
		}
		else
		{
			mUpPointView.setVisibility( View.VISIBLE );
			mDownPointView.setVisibility( View.INVISIBLE );
			LayoutParams layoutParams = (LayoutParams) mUpPointView.getLayoutParams( );
			layoutParams.setMargins( location , 0 , 0 , 0 );
			mUpPointView.setLayoutParams( layoutParams );
		}
	}
	
	public PopupWindow getPopupWindow( )
	{
		return mPopupWindow;
	}
}
