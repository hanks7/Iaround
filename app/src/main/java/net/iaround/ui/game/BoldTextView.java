
package net.iaround.ui.game;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


public class BoldTextView extends TextView
{
	
	public BoldTextView(Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
		init( );
	}
	
	public BoldTextView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		// TODO Auto-generated constructor stub
		init( );
	}
	
	public BoldTextView(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		// TODO Auto-generated constructor stub
		init( );
	}
	
	private void init( )
	{
		getPaint( ).setFakeBoldText( true );
	}
}
