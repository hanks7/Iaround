
package net.iaround.ui.comon;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.lang.ref.WeakReference;


/**
 * A QuickAction implements an item in a {@link QuickActionWidget}. A
 * QuickAction represents a single action and may contain a text and an icon.
 * 
 */
public class QuickAction
{
	
	public Drawable mDrawable;
	public CharSequence mTitle;
	private Object tag;
	
	/* package */WeakReference< View > mView;
	
	public QuickAction(Drawable d , CharSequence title )
	{
		mDrawable = d;
		mTitle = title;
	}
	
	public QuickAction(Context ctx , int drawableId , CharSequence title )
	{
		mDrawable = ctx.getResources( ).getDrawable( drawableId );
		mTitle = title;
	}
	
	public QuickAction(Context ctx , Drawable d , int titleId )
	{
		mDrawable = d;
		mTitle = ctx.getResources( ).getString( titleId );
	}
	
	public QuickAction(Context ctx , int drawableId , int titleId )
	{
		mDrawable = ctx.getResources( ).getDrawable( drawableId );
		mTitle = ctx.getResources( ).getString( titleId );
	}
	
	public void setTag(Object obj)
	{
		tag = obj;
	}
	
	public Object getTag()
	{
		return tag;
	}
	
	public String getTitle(){
		return (String) mTitle;
	}
}
