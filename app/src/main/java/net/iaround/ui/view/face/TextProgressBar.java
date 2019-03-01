
package net.iaround.ui.view.face;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.text.NumberFormat;


public class TextProgressBar extends ProgressBar
{
	private String mText;
	private Paint mDefaultTextPaint;
	private Rect mRect;
	private Bitmap mImage;
	private int dp_5 = 5;

	public TextProgressBar(Context context )
	{
		super( context );
		// TODO Auto-generated constructor stub
		initText( );
	}

	public TextProgressBar(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		// TODO Auto-generated constructor stub
		initText( );
	}

	public TextProgressBar(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		// TODO Auto-generated constructor stub
		initText( );
	}

	@Override
	public synchronized void setProgress( int progress )
	{
		// TODO Auto-generated method stub
		setText( progress );
		super.setProgress( progress );
	}

	public void setImage( Bitmap bm )
	{
		mImage = bm;
		invalidate( );
	}

	public void setTextSize( float textSize )
	{
		this.mDefaultTextPaint.setTextSize( textSize );
	}

	public void setTextColor( int textColor )
	{
		this.mDefaultTextPaint.setColor( textColor );
	}

	@Override
	protected synchronized void onDraw( Canvas canvas )
	{
		// TODO Auto-generated method stub
		super.onDraw( canvas );

		this.mDefaultTextPaint.getTextBounds( this.mText , 0 , this.mText.length( ) , mRect );


		if ( mImage != null )
		{
			if ( !TextUtils.isEmpty( mText ) )
			{
				canvas.drawBitmap(
						mImage ,
						( getWidth( ) - mRect.width( ) - mImage.getWidth( ) - ( dp_5 / 2 ) ) / 2 ,
						( getHeight( ) - mImage.getHeight( ) ) / 2 , mDefaultTextPaint );
			}
			else
			{
				canvas.drawBitmap( mImage , ( getWidth( ) - mImage.getWidth( ) ) / 2 ,
						( getHeight( ) - mImage.getHeight( ) ) / 2 , mDefaultTextPaint );
			}
		}

		if ( !TextUtils.isEmpty( mText ) )
		{
			if ( mImage != null )
			{
				canvas.drawText( mText ,
						( getWidth( ) - mRect.width( ) ) / 2 + mImage.getWidth( )
								- ( dp_5 / 2 ) , ( getHeight( ) + mRect.height( ) ) / 2 ,
						mDefaultTextPaint );
			}
			else
			{
				canvas.drawText( mText , ( getWidth( ) - mRect.width( ) ) / 2 ,
						( getHeight( ) + mRect.height( ) ) / 2 , mDefaultTextPaint );
			}
		}
	}

	// 初始化，画笔
	private void initText( )
	{
		this.mRect = new Rect( );
		this.mDefaultTextPaint = new Paint( );
		this.mDefaultTextPaint.setAntiAlias( true );
		this.mDefaultTextPaint.setColor( Color.WHITE );
		this.dp_5 = ( int ) ( 5 * getResources( ).getDisplayMetrics( ).density );
		this.mDefaultTextPaint.setTextSize( dp_5 * 3 );
	}

	// 设置文字内容
	public void setText( String text )
	{
		if ( text == null )
		{
			text = "";
		}
		this.mText = text;
		invalidate( );
	}

	// 设置文字内容
	private void setText( int progress )
	{
		this.mText = getPercentage( progress , getMax( ) );
	}

	private static String getPercentage( float p1 , float p2 )
	{
		NumberFormat nf = NumberFormat.getPercentInstance( );
		return nf.format( p1 / p2 );
	}
}
