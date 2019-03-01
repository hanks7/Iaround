package net.iaround.ui.chat.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import net.iaround.R;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Administrator on 2015/12/23.
 */
public class ChatFacePointView extends View
{

	private int count,item;
	private final Paint mPaintPageFill = new Paint( ANTI_ALIAS_FLAG );
	private final Paint mPaintStroke = new Paint( ANTI_ALIAS_FLAG );
	private final Paint mPaintFill = new Paint( ANTI_ALIAS_FLAG );
	private float mRadius,pageOffset;

	public ChatFacePointView( Context context )
	{
		this( context,null );
	}

	public ChatFacePointView(Context context, AttributeSet attrs )
	{
		this( context , attrs , R.attr.vpiCirclePageIndicatorStyle );
	}

	public ChatFacePointView(Context context , AttributeSet attrs , int defStyle )
	{
		super(context,attrs,defStyle);
		if ( isInEditMode( ) )
			return;
		final Resources res = getResources( );
		final int defaultPageColor = res
			.getColor( R.color.default_circle_indicator_page_color );
		final int defaultFillColor = res
			.getColor( R.color.default_circle_indicator_fill_color );
		final int defaultStrokeColor = res
			.getColor( R.color.default_circle_indicator_stroke_color );
		final float defaultStrokeWidth = res
			.getDimension( R.dimen.default_circle_indicator_stroke_width );
		TypedArray a = context.obtainStyledAttributes( attrs ,
			R.styleable.CirclePageIndicator , defStyle , 0 );
		mPaintPageFill.setStyle( Paint.Style.FILL );
		mPaintPageFill.setColor( a.getColor( R.styleable.CirclePageIndicator_pageColor ,
			defaultPageColor ) );
		//画实心的
		mPaintStroke.setStyle( Paint.Style.FILL_AND_STROKE );
//		mPaintStroke.setStyle( Paint.Style.STROKE );
		mPaintStroke.setColor( a.getColor( R.styleable.CirclePageIndicator_strokeColor ,
			defaultStrokeColor ) );
		mPaintStroke.setStrokeWidth( a.getDimension(
			R.styleable.CirclePageIndicator_strokeWidth , defaultStrokeWidth ) );
		mPaintFill.setStyle( Paint.Style.FILL );
		mPaintFill.setColor( a.getColor( R.styleable.CirclePageIndicator_fillColor ,
			defaultFillColor ) );
		final float defaultRadius = res.getDimension( R.dimen.default_circle_indicator_radius );
		mRadius = a.getDimension( R.styleable.CirclePageIndicator_radius , defaultRadius );
		Drawable background = a
			.getDrawable( R.styleable.CirclePageIndicator_android_background );
		if ( background != null )
		{
			setBackgroundDrawable( background );
		}

		a.recycle( );
	}

	public void setCount(int count){
		this.count=count;
	}

	public void setCurrentItem( int item ){
		this.item=item;
	}

	public void setPageOffset(float pageOffset){
		this.pageOffset=pageOffset;
	}

	@Override
	protected void onDraw( Canvas canvas )
	{
		super.onDraw( canvas );
		int longSize;
		int longPaddingBefore;
		int longPaddingAfter;
		int shortPaddingBefore;
		longSize = getWidth( );
		longPaddingBefore = getPaddingLeft( );
		longPaddingAfter = getPaddingRight( );
		shortPaddingBefore = getPaddingTop( );
		final float threeRadius = mRadius * 4;
		final float shortOffset = shortPaddingBefore + mRadius;
		float longOffset = longPaddingBefore + mRadius;
		longOffset += ( ( longSize - longPaddingBefore - longPaddingAfter ) / 2.0f )
			- ( ( count * threeRadius ) / 2.0f );
		float dX;
		float dY;

		for ( int iLoop = 0 ; iLoop < count ; iLoop++ )
		{
			float drawLong = longOffset + ( iLoop * threeRadius );
			dX = drawLong;
			dY = shortOffset;
			canvas.drawCircle( dX , dY , mRadius , mPaintStroke );
		}

		float cx = item * threeRadius;
		cx += pageOffset * threeRadius;
		dX = longOffset + cx;
		dY = shortOffset;
		canvas.drawCircle( dX , dY , mRadius , mPaintFill );

	}

	@Override
	protected void onMeasure( int widthMeasureSpec , int heightMeasureSpec )
	{
		setMeasuredDimension( widthMeasureSpec ,
			measureShort( heightMeasureSpec ) );
	}


	private int measureShort( int measureSpec )
	{
		int result;
		int specMode = MeasureSpec.getMode( measureSpec );
		int specSize = MeasureSpec.getSize( measureSpec );

		if ( specMode == MeasureSpec.EXACTLY )
		{
			// We were told how big to be
			result = specSize;
		}
		else
		{
			// Measure the height
			result = ( int ) ( 2 * mRadius + getPaddingTop( ) + getPaddingBottom( ) + 1 );
			// Respect AT_MOST value if that was what is called for by
			// measureSpec
			if ( specMode == MeasureSpec.AT_MOST )
			{
				result = Math.min( result , specSize );
			}
		}
		return result;
	}

}
