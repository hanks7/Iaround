
package net.iaround.ui.comon;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import net.iaround.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Abstraction of a {@link QuickAction} wrapper. A QuickActionWidget is
 * displayed on top of the user interface (it overlaps all UI elements but the
 * notification bar). Clients may listen to user actions using a
 * {@link OnQuickActionClickListener} .
 * 
 */
public abstract class QuickActionWidget extends PopupWindow
{
	
	private static final int MEASURE_AND_LAYOUT_DONE = 1 << 1;
	
	private final int[ ] mLocation = new int[ 2 ];
	private final Rect mRect = new Rect( );
	
	private int mPrivateFlags;
	
	private Context mContext;
	private HashMap< Object , Object > tags;
	
	private boolean mDismissOnClick;
	private int mArrowOffsetY;
	
	private int mPopupY;
	private boolean mIsOnTop;
	
	private int mScreenHeight;
	private int mScreenWidth;
	private boolean mIsDirty;
	protected boolean mIsForChat;
	protected boolean mIsOnLeft;
	
	private OnQuickActionClickListener mOnQuickActionClickListener;
	private ArrayList< QuickAction > mQuickActions = new ArrayList< QuickAction >( );
	
	public QuickAction getQuickAction(int position)
	{
		if(position < mQuickActions.size())
		{
			return mQuickActions.get(position);
		}
		
		return null;
	}
	
	/**
	 * Interface that may be used to listen to clicks on quick actions.
	 * 
	 * @author Benjamin Fellous
	 * @author Cyril Mottier
	 */
	public interface OnQuickActionClickListener
	{
		/**
		 * Clients may implement this method to be notified of a click on a
		 * particular quick action.
		 * 
		 * @param position
		 *            Position of the quick action that have been clicked.
		 */
		void onQuickActionClicked(QuickActionWidget widget, int position);
	}
	
	/**
	 * Creates a new QuickActionWidget for the given context.
	 * 
	 * @param context
	 *            The context in which the QuickActionWidget is running in
	 */
	public QuickActionWidget(Context context )
	{
		super( context );
		
		mContext = context;
		tags = new HashMap< Object , Object >( );
		
		initializeDefault( );
		
		setFocusable( true );
		setTouchable( true );
		setOutsideTouchable( true );
		setWidth( WindowManager.LayoutParams.FILL_PARENT );
		setHeight( WindowManager.LayoutParams.WRAP_CONTENT );
		
		final WindowManager windowManager = (WindowManager) mContext
				.getSystemService( Context.WINDOW_SERVICE );
		mScreenWidth = windowManager.getDefaultDisplay( ).getWidth( );
		mScreenHeight = windowManager.getDefaultDisplay( ).getHeight( );
	}
	
	/**
	 * Equivalent to {@link PopupWindow#setContentView(View)} but with a layout
	 * identifier.
	 * 
	 * @param layoutId
	 *            The layout identifier of the view to use.
	 */
	public void setContentView( int layoutId )
	{
		setContentView( LayoutInflater.from( mContext ).inflate( layoutId , null ) );
	}
	
	private void initializeDefault( )
	{
		mDismissOnClick = true;
		mArrowOffsetY = mContext.getResources( ).getDimensionPixelSize(
				R.dimen.gd_arrow_offset );
	}
	
	public boolean isOnTop( )
	{
		return mIsOnTop;
	}
	
	/**
	 * Returns the arrow offset for the Y axis.
	 * 
	 * @see {@link #setArrowOffsetY(int)}
	 * @return The arrow offset.
	 */
	public int getArrowOffsetY( )
	{
		return mArrowOffsetY;
	}
	
	/**
	 * Sets the arrow offset to a new value. Setting an arrow offset may be
	 * particular useful to warn which view the QuickActionWidget is related to.
	 * By setting a positive offset, the arrow will overlap the view given by
	 * {@link #show(View)}. The default value is 5dp.
	 * 
	 * @param offsetY
	 *            The offset for the Y axis
	 */
	public void setArrowOffsetY( int offsetY )
	{
		mArrowOffsetY = offsetY;
	}
	
	/**
	 * Returns the width of the screen.
	 * 
	 * @return The width of the screen
	 */
	protected int getScreenWidth( )
	{
		return mScreenWidth;
	}
	
	/**
	 * Returns the height of the screen.
	 * 
	 * @return The height of the screen
	 */
	protected int getScreenHeight( )
	{
		return mScreenHeight;
	}
	
	/**
	 * By default, a {@link QuickActionWidget} is dismissed once the user
	 * clicked on a {@link QuickAction}. This behavior can be changed using this
	 * method.
	 * 
	 * @param dismissOnClick
	 *            True if you want the {@link QuickActionWidget} to be dismissed
	 *            on click else false.
	 */
	public void setDismissOnClick( boolean dismissOnClick )
	{
		mDismissOnClick = dismissOnClick;
	}
	
	public boolean getDismissOnClick( )
	{
		return mDismissOnClick;
	}
	
	/**
	 * @param listener
	 */
	public void setOnQuickActionClickListener( OnQuickActionClickListener listener )
	{
		mOnQuickActionClickListener = listener;
	}
	
	/**
	 * Add a new QuickAction to this {@link QuickActionWidget}. Adding a new
	 * {@link QuickAction} while the {@link QuickActionWidget} is currently
	 * being shown does nothing. The new {@link QuickAction} will be displayed
	 * on the next call to {@link #show(View)}.
	 * 
	 * @param action
	 *            The new {@link QuickAction} to add
	 */
	public void addQuickAction( QuickAction action )
	{
		if ( action != null )
		{
			mQuickActions.add( action );
			mIsDirty = true;
		}
	}
	
	/**
	 * Removes all {@link QuickAction} from this {@link QuickActionWidget}.
	 */
	public void clearAllQuickActions( )
	{
		if ( !mQuickActions.isEmpty( ) )
		{
			mQuickActions.clear( );
			mIsDirty = true;
		}
	}
	
	/**
	 * Call that method to display the {@link QuickActionWidget} anchored to the
	 * given view.
	 * 
	 * @param anchor
	 *            The view the {@link QuickActionWidget} will be anchored to.
	 */
	public void show( View anchor )
	{
		
		final View contentView = getContentView( );
		
		if ( contentView == null )
		{
			throw new IllegalStateException(
					"You need to set the content view using the setContentView method" );
		}
		
		// Replaces the background of the popup with a cleared background
		setBackgroundDrawable( new ColorDrawable( Color.TRANSPARENT ) );
		
		final int[ ] loc = mLocation;
		anchor.getLocationOnScreen( loc );
		mRect.set( loc[ 0 ] , loc[ 1 ] , loc[ 0 ] + anchor.getWidth( ) ,
				loc[ 1 ] + anchor.getHeight( ) );
		
		if ( mIsDirty )
		{
			clearQuickActions( );
			populateQuickActions( mQuickActions );
		}
		
		onMeasureAndLayout( mRect , contentView );
		
		if ( ( mPrivateFlags & MEASURE_AND_LAYOUT_DONE ) != MEASURE_AND_LAYOUT_DONE )
		{
			throw new IllegalStateException(
					"onMeasureAndLayout() did not set the widget specification by calling"
							+ " setWidgetSpecs()" );
		}
		
		if ( mIsForChat )
		{
			showArrowForChat( );
		}
		else
		{
			showArrow( );
		}
		prepareAnimationStyle( );
		showAtLocation( anchor , Gravity.NO_GRAVITY , 0 , mPopupY );
	}
	
	protected void clearQuickActions( )
	{
		if ( !mQuickActions.isEmpty( ) )
		{
			onClearQuickActions( );
		}
	}
	
	protected void onClearQuickActions( )
	{
	}
	
	protected abstract void populateQuickActions( List< QuickAction > quickActions );
	
	protected abstract void onMeasureAndLayout(Rect anchorRect , View contentView );
	
	protected void setWidgetSpecs( int popupY , boolean isOnTop )
	{
		mPopupY = popupY;
		mIsOnTop = isOnTop;
		
		mPrivateFlags |= MEASURE_AND_LAYOUT_DONE;
	}
	
	private void showArrowForChat( )
	{
		
		final View contentView = getContentView( );
		final int arrowId = mIsOnTop ? R.id.gdi_arrow_down : R.id.gdi_arrow_up;
		final View arrow = contentView.findViewById( arrowId );
		final View arrowUp = contentView.findViewById( R.id.gdi_arrow_up );
		final View arrowDown = contentView.findViewById( R.id.gdi_arrow_down );
		
		if ( arrowId == R.id.gdi_arrow_up )
		{
			arrowUp.setVisibility( View.VISIBLE );
			arrowDown.setVisibility( View.INVISIBLE );
		}
		else if ( arrowId == R.id.gdi_arrow_down )
		{
			arrowUp.setVisibility( View.INVISIBLE );
			arrowDown.setVisibility( View.VISIBLE );
		}
		
		ViewGroup chatBar = (ViewGroup) contentView.findViewById( R.id.gdi_rack );
		ViewGroup.MarginLayoutParams chatBarParam = ( ViewGroup.MarginLayoutParams ) chatBar
				.getLayoutParams( );
		ViewGroup.MarginLayoutParams arrowParam = ( ViewGroup.MarginLayoutParams ) arrow
				.getLayoutParams( );
		
		if ( mIsOnLeft )
		{
			arrowParam.leftMargin = mRect.left + ( arrow.getMeasuredWidth( ) ) / 2 + 15;
			chatBarParam.leftMargin = mRect.left;
		}
		else
		{
			arrowParam.leftMargin = mRect.right - ( arrow.getMeasuredWidth( ) ) / 2 - 15;
			chatBarParam.leftMargin = mRect.right - chatBar.getMeasuredWidth( );
		}
	}
	
	private void showArrow( )
	{
		
		final View contentView = getContentView( );
		final int arrowId = mIsOnTop ? R.id.gdi_arrow_down : R.id.gdi_arrow_up;
		final View arrow = contentView.findViewById( arrowId );
		final View arrowUp = contentView.findViewById( R.id.gdi_arrow_up );
		final View arrowDown = contentView.findViewById( R.id.gdi_arrow_down );
		
		if ( arrowId == R.id.gdi_arrow_up )
		{
			arrowUp.setVisibility( View.VISIBLE );
			arrowDown.setVisibility( View.INVISIBLE );
		}
		else if ( arrowId == R.id.gdi_arrow_down )
		{
			arrowUp.setVisibility( View.INVISIBLE );
			arrowDown.setVisibility( View.VISIBLE );
		}
		
		ViewGroup.MarginLayoutParams param = ( ViewGroup.MarginLayoutParams ) arrow
				.getLayoutParams( );
		param.leftMargin = mRect.centerX( ) - ( arrow.getMeasuredWidth( ) ) / 2;
	}
	
	private void prepareAnimationStyle( )
	{
		//
		// final int screenWidth = mScreenWidth;
		// final boolean onTop = mIsOnTop;
		// final int arrowPointX = mRect.centerX();
		//
		// if (arrowPointX <= screenWidth / 4) {
		// setAnimationStyle(onTop ? R.style.QuickAction_Animation_PopUp_Left
		// : R.style.QuickAction_Animation_PopDown_Left);
		// } else if (arrowPointX >= 3 * screenWidth / 4) {
		// setAnimationStyle(onTop ? R.style.QuickAction_Animation_PopUp_Right
		// : R.style.QuickAction_Animation_PopDown_Right);
		// } else {
		// setAnimationStyle(onTop ? R.style.QuickAction_Animation_PopUp_Center
		// : R.style.QuickAction_Animation_PopDown_Center);
		// }
	}
	
	protected Context getContext( )
	{
		return mContext;
	}
	
	protected OnQuickActionClickListener getOnQuickActionClickListener( )
	{
		return mOnQuickActionClickListener;
	}
	
	public void setTag( Object tag )
	{
		setTag( this , tag );
	}
	
	public void setTag(Object key , Object tag )
	{
		tags.put( key , tag );
	}
	
	public Object getTag( )
	{
		return getTag( this );
	}
	
	public Object getTag(Object key )
	{
		return tags.get( key );
	}
}