
package net.iaround.ui.view.user;


import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;

import java.util.Calendar;


/**
 * 一个简易的日期选择器，通过设置OnDateChangedListener，可以 对选择结果进行监听。
 * 
 * @author 余勋杰
 */
public class IARDatePicker extends LinearLayout
{
	/* 可以输入的最大年限 */
	private  int MAX_YEAR = Integer.MAX_VALUE;
	/* 可以输入的最低年限 */
	private int MIN_YEAR = 1;
	/* 会被显示的最大年限，即便输入超过这个年限，控件也会跳回显示这个数字 */
//	private static final int MAX_YEAR_SELECTED = 2100;
	private static int MAX_YEAR_SELECTED = 2017;
	/* 会被显示的最小年限，即便输入小于这个年限，控件也会跳回显示这个数字 */
	private static int MIN_YEAR_SELECTED = 1917;
	/* 会被显示的最大月份 */
	private static final int MAX_MONTH = 12;
	/* 会被显示的最小月份 */
	private static final int MIN_MONTH = 1;
	/* 每个月的天数 */
	private static final int[ ] MAX_DATE = new int[ ]
		{ 31 , 28 , 31 , 30 , 31 , 30 , 31 , 31 , 30 , 31 , 30 , 31 };
	/* 日期的最小数字 */
	private static final int MIN_DATE = 1;
	private boolean addViewLock;
	private ImageButton ibAddYear; // 年份“+”按钮
	private ImageButton ibSubtractYear; // 年份“-”按钮
	private ImageButton ibAddMonth; // 月份“+”按钮
	private ImageButton ibSubtractMonth; // 月份“-”按钮
	private ImageButton ibAddDate; // 日期“+”按钮
	private ImageButton ibSubtractDate; // 日期“-”按钮
	private EditText etYear; // 年份输入框
	private EditText etMonth; // 月份输入框
	private EditText etDate; // 日期输入框
	private int year; // 已经输入的年份
	private int curYear; // 当前的年份
	private int month; // 已经输入的月份
	private int curMonth; // 当前的月份
	private int date; // 已经选择 的日期
	private int curDate; // 当前选择的日期
	private OnDateChangedListener odcListener;
	
	public IARDatePicker(Context context )
	{
		super( context );
		init( context );
	}
	
	public IARDatePicker(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		init( context );
	}
	
	private void init( Context context )
	{
		addViewLock = false;
		OnClickListener ocListener = new OnClickListener( )
		{
			public void onClick( View v )
			{
				IARDatePicker.this.onClick( v );
			}
		};
		OnFocusChangeListener ofcListener = new OnFocusChangeListener( )
		{
			public void onFocusChange( View v , boolean hasFocus )
			{
				IARDatePicker.this.onFocusChange( ( EditText ) v , hasFocus );
			}
		};
		// int dp_10 = (int)(getResources().getDimension(R.dimen.dp_1) * 10);
		setWeightSum( 75 );
		Calendar cal = Calendar.getInstance( );
		cal.setTimeInMillis( System.currentTimeMillis( ) );
		MAX_YEAR_SELECTED = cal.get(Calendar.YEAR);
		MIN_YEAR_SELECTED = cal.get(Calendar.YEAR) - 100;
		year = cal.get( Calendar.YEAR )-18;//默认选择18年前的今天
		curYear = year;
		month = cal.get( Calendar.MONTH ) ;
		curMonth = month;
		date = cal.get( Calendar.DAY_OF_MONTH );
		curDate = date;
		// setBackgroundResource(R.drawable.dp_bg);
		// setPadding(dp_10, dp_10, dp_10, dp_10);
		
		// 年
		LinearLayout llYear = new LinearLayout( context );
		llYear.setOrientation( VERTICAL );
		LayoutParams lpYear = new LayoutParams( LayoutParams.WRAP_CONTENT ,
				LayoutParams.FILL_PARENT );
		lpYear.weight = 29;
		llYear.setLayoutParams( lpYear );
		addView( llYear );

		// 年+号按钮
		ibAddYear = new ImageButton( context );
		ibAddYear.setScaleType( ScaleType.CENTER_INSIDE );
		LayoutParams lpButton = new LayoutParams( LayoutParams.FILL_PARENT ,
				LayoutParams.WRAP_CONTENT );
		ibAddYear.setLayoutParams( lpButton );
		ibAddYear.setOnClickListener( ocListener );
		ibAddYear.setImageResource( R.drawable.dp_add );
		ibAddYear.setBackgroundResource( R.drawable.dp_add_bg );
		llYear.addView( ibAddYear );

		// 年输入框
		etYear = new EditText( context );
		etYear.setBackgroundResource( R.drawable.dp_dig_bg );
		etYear.setGravity( Gravity.CENTER );
		etYear.setText( String.valueOf( year ) );
		etYear.setInputType( EditorInfo.TYPE_NULL/* EditorInfo.TYPE_CLASS_NUMBER */);
		etYear.setSingleLine( );
		etYear.setMinEms( 4 );
		etYear.setMaxEms( 4 );
		int dp_5 = CommonFunction.pxToDip(BaseApplication.appContext, 5);
		etYear.setPadding( dp_5 , dp_5 , dp_5 , dp_5 );
		LayoutParams lpEditText = new LayoutParams( LayoutParams.FILL_PARENT ,
				LayoutParams.WRAP_CONTENT );
		lpEditText.weight = 1;
		etYear.setLayoutParams( lpEditText );
		etYear.setOnFocusChangeListener( ofcListener );
		etYear.addTextChangedListener( new TextWatcher( )
		{
			public void onTextChanged( CharSequence s , int start , int before , int count )
			{
				IARDatePicker.this.onTextChanged( etYear , s );
			}

			public void beforeTextChanged( CharSequence s , int start , int count , int after )
			{

			}

			public void afterTextChanged( Editable s )
			{

			}
		} );
		llYear.addView( etYear );

		// 年-号按钮
		ibSubtractYear = new ImageButton( context );
		ibSubtractYear.setScaleType( ScaleType.CENTER_INSIDE );
		ibSubtractYear.setLayoutParams( lpButton );
		ibSubtractYear.setOnClickListener( ocListener );
		ibSubtractYear.setImageResource( R.drawable.dp_sub );
		ibSubtractYear.setBackgroundResource( R.drawable.dp_sub_bg );
		llYear.addView( ibSubtractYear );

		// 月
		LinearLayout llMonth = new LinearLayout( context );
		llMonth.setOrientation( VERTICAL );
		LayoutParams lpMonth = new LayoutParams( LayoutParams.WRAP_CONTENT ,
				LayoutParams.FILL_PARENT );
		lpMonth.weight = 23;
		lpMonth.setMargins( 11 , 0 , 0 , 0 );
		llMonth.setLayoutParams( lpMonth );
		addView( llMonth );
		
		// 月+号按钮
		ibAddMonth = new ImageButton( context );
		ibAddMonth.setScaleType( ScaleType.CENTER_INSIDE );
		ibAddMonth.setLayoutParams( lpButton );
		ibAddMonth.setOnClickListener( ocListener );
		ibAddMonth.setImageResource( R.drawable.dp_add );
		ibAddMonth.setBackgroundResource( R.drawable.dp_add_bg );
		llMonth.addView( ibAddMonth );
		
		// 月输入框
		etMonth = new EditText( context );
		etMonth.setBackgroundResource( R.drawable.dp_dig_bg );
		etMonth.setGravity( Gravity.CENTER );
		etMonth.setInputType( EditorInfo.TYPE_NULL/* EditorInfo.TYPE_CLASS_NUMBER */);
		etMonth.setSingleLine( );
		etMonth.setMinEms( 2 );
		etMonth.setMaxEms( 2 );
		etMonth.setText( String.valueOf( month ) );
		etMonth.setPadding( dp_5 , dp_5 , dp_5 , dp_5 );
		etMonth.setLayoutParams( lpEditText );
		etMonth.addTextChangedListener( new TextWatcher( )
		{
			public void onTextChanged( CharSequence s , int start , int before , int count )
			{
				IARDatePicker.this.onTextChanged( etMonth , s );
			}
			
			public void beforeTextChanged( CharSequence s , int start , int count , int after )
			{
				
			}
			
			public void afterTextChanged( Editable s )
			{
				
			}
		} );
		llMonth.addView( etMonth );
		
		// 月-号按钮
		ibSubtractMonth = new ImageButton( context );
		ibSubtractMonth.setScaleType( ScaleType.CENTER_INSIDE );
		ibSubtractMonth.setLayoutParams( lpButton );
		ibSubtractMonth.setOnClickListener( ocListener );
		ibSubtractMonth.setImageResource( R.drawable.dp_sub );
		ibSubtractMonth.setBackgroundResource( R.drawable.dp_sub_bg );
		llMonth.addView( ibSubtractMonth );
		
		// 日
		LinearLayout llDate = new LinearLayout( context );
		llDate.setOrientation( VERTICAL );
		llDate.setLayoutParams( lpMonth );
		addView( llDate );
		
		// 日+号按钮
		ibAddDate = new ImageButton( context );
		ibAddDate.setScaleType( ScaleType.CENTER_INSIDE );
		ibAddDate.setLayoutParams( lpButton );
		ibAddDate.setOnClickListener( ocListener );
		ibAddDate.setImageResource( R.drawable.dp_add );
		ibAddDate.setBackgroundResource( R.drawable.dp_add_bg );
		llDate.addView( ibAddDate );
		
		// 日输入框
		etDate = new EditText( context );
		etDate.setBackgroundResource( R.drawable.dp_dig_bg );
		etDate.setGravity( Gravity.CENTER );
		etDate.setInputType( EditorInfo.TYPE_NULL/* EditorInfo.TYPE_CLASS_NUMBER */);
		etDate.setSingleLine( );
		etDate.setMinEms( 2 );
		etDate.setMaxEms( 2 );
		etDate.setText( String.valueOf( date ) );
		etDate.setPadding( dp_5 , dp_5 , dp_5 , dp_5 );
		etDate.setLayoutParams( lpEditText );
		etDate.addTextChangedListener( new TextWatcher( )
		{
			public void onTextChanged( CharSequence s , int start , int before , int count )
			{
				IARDatePicker.this.onTextChanged( etDate , s );
			}
			
			public void beforeTextChanged( CharSequence s , int start , int count , int after )
			{
				
			}
			
			public void afterTextChanged( Editable s )
			{
				
			}
		} );
		llDate.addView( etDate );
		
		// 日-号按钮
		ibSubtractDate = new ImageButton( context );
		ibSubtractDate.setScaleType( ScaleType.CENTER_INSIDE );
		ibSubtractDate.setLayoutParams( lpButton );
		ibSubtractDate.setOnClickListener( ocListener );
		ibSubtractDate.setImageResource( R.drawable.dp_sub );
		ibSubtractDate.setBackgroundResource( R.drawable.dp_sub_bg );
		llDate.addView( ibSubtractDate );
		
		addViewLock = true;
	}
	
	public void addView( View child )
	{
		if ( addViewLock )
		{
			return;
		}
		super.addView( child );
	}
	
	public void addView( View child , int index )
	{
		if ( addViewLock )
		{
			return;
		}
		super.addView( child , index );
	}
	
	public void addView( View child , int index , android.view.ViewGroup.LayoutParams params )
	{
		if ( addViewLock )
		{
			return;
		}
		super.addView( child , index , params );
	}
	
	public void addView( View child , int width , int height )
	{
		if ( addViewLock )
		{
			return;
		}
		super.addView( child , width , height );
	}
	
	public void addView( View child , android.view.ViewGroup.LayoutParams params )
	{
		if ( addViewLock )
		{
			return;
		}
		super.addView( child , params );
	}
	
	protected boolean addViewInLayout( View child , int index ,
			android.view.ViewGroup.LayoutParams params )
	{
		if ( addViewLock )
		{
			return false;
		}
		return super.addViewInLayout( child , index , params );
	}
	
	protected boolean addViewInLayout( View child , int index ,
			android.view.ViewGroup.LayoutParams params , boolean preventRequestLayout )
	{
		if ( addViewLock )
		{
			return false;
		}
		return super.addViewInLayout( child , index , params , preventRequestLayout );
	}
	
	private void onClick( View v )
	{
		if ( ibAddYear.equals( v ) )
		{
			year++;
		}
		else if ( ibSubtractYear.equals( v ) )
		{
			year--;
		}
		else if ( ibAddMonth.equals( v ) )
		{
			month++;
		}
		else if ( ibSubtractMonth.equals( v ) )
		{
			month--;
		}
		else if ( ibAddDate.equals( v ) )
		{
			date++;
		}
		else if ( ibSubtractDate.equals( v ) )
		{
			date--;
		}
		
		checkDate( );
		etYear.setText( String.valueOf( year ) );
		etMonth.setText( String.valueOf( month ) );
		etDate.setText( String.valueOf( date ) );
		if ( odcListener != null )
		{
			odcListener.onDateChanged( this , year , month , date );
		}
	}
	
	public void setMaxYear(int maxYear)
	{
		MAX_YEAR = maxYear;
	}
	
	public void setMinYear(int minYear)
	{
		MIN_YEAR = minYear;
	}
	
	
	
	
	/* 检查设置的时间是否正确，不正确会自动调到阀值 */
	private void checkDate( )
	{
		if ( year > MAX_YEAR )
		{
			year = MAX_YEAR;
		}
		if ( year < MIN_YEAR )
		{
			year = MIN_YEAR;
		}
		if ( year % 400 == 0 || ( year % 4 == 0 && year % 100 != 0 ) )
		{
			MAX_DATE[ 1 ] = 29;
		}
		else
		{
			MAX_DATE[ 1 ] = 28;
		}
		if ( month > MAX_MONTH )
		{
			month = MIN_MONTH;
		}
		if ( month < MIN_MONTH )
		{
			month = MAX_MONTH;
		}
		if ( date > MAX_DATE[ month - 1 ] )
		{
			date = MIN_DATE;
		}
		if ( date < MIN_DATE )
		{
			date = MAX_DATE[ month - 1 ];
		}
	}
	
	private void onTextChanged( EditText view , CharSequence s )
	{
		if ( etYear.equals( view ) )
		{
			String sYear = s.toString( );
			if ( sYear == null || sYear.length( ) <= 0 )
			{
				sYear = String.valueOf( curYear );
				view.setText( sYear );
			}
			int setYear = advParseInt( sYear , year );
			if ( !String.valueOf( setYear ).equals( sYear ) )
			{
				view.setText( String.valueOf( setYear ) );
			}
			year = setYear;
			checkDate( );
			if ( year != setYear )
			{
				view.setText( String.valueOf( year ) );
			}
			else
			{
				if ( odcListener != null )
				{
					odcListener.onDateChanged( this , year , month , date );
				}
			}
		}
		else if ( etMonth.equals( view ) )
		{
			String sMonth = s.toString( );
			if ( sMonth == null || sMonth.length( ) <= 0 )
			{
				sMonth = String.valueOf( curMonth );
				view.setText( sMonth );
			}
			int setMonth = advParseInt( sMonth , month );
			if ( !String.valueOf( setMonth ).equals( sMonth ) )
			{
				view.setText( String.valueOf( setMonth ) );
			}
			month = setMonth;
			checkDate( );
			if ( month != setMonth )
			{
				view.setText( String.valueOf( month ) );
			}
			else
			{
				if ( odcListener != null )
				{
					odcListener.onDateChanged( this , year , month , date );
				}
			}
		}
		else if ( etDate.equals( view ) )
		{
			String sDate = s.toString( );
			if ( sDate == null || sDate.length( ) <= 0 )
			{
				sDate = String.valueOf( curDate );
				view.setText( sDate );
			}
			int setDate = advParseInt( sDate , date );
			if ( !String.valueOf( setDate ).equals( sDate ) )
			{
				view.setText( String.valueOf( setDate ) );
			}
			date = setDate;
			checkDate( );
			if ( date != setDate )
			{
				view.setText( String.valueOf( date ) );
			}
			else
			{
				if ( odcListener != null )
				{
					odcListener.onDateChanged( this , year , month , date );
				}
			}
		}
	}
	
	private void onFocusChange( EditText v , boolean hasFocus )
	{
		if ( !hasFocus )
		{
			if ( etYear.equals( v ) )
			{
				String sYear = v.getText( ).toString( );
				if ( sYear == null || sYear.length( ) <= 0 )
				{
					sYear = String.valueOf( curYear );
					v.setText( sYear );
				}
				int setYear = advParseInt( sYear , year );
				if ( !String.valueOf( setYear ).equals( sYear ) )
				{
					v.setText( String.valueOf( setYear ) );
				}
				
				year = setYear;
				if ( year > MAX_YEAR_SELECTED )
				{
					year = MAX_YEAR_SELECTED;
				}
				if ( year < MIN_YEAR_SELECTED )
				{
					year = MIN_YEAR_SELECTED;
				}
				
				if ( year != setYear )
				{
					v.setText( String.valueOf( year ) );
				}
				if ( odcListener != null )
				{
					odcListener.onDateChanged( this , year , month , date );
				}
			}
		}
	}
	
	/** 设置一个日期变更监听器 */
	public void setOnDateChangedListener( OnDateChangedListener l )
	{
		odcListener = l;
	}
	
	/** 手动设置日期 */
	public void setDate( int year , int monthOfYear , int dayOfMonth )
	{
		this.year = year;
		this.month = monthOfYear;
		this.date = dayOfMonth;
		etYear.setText( String.valueOf( year ) );
		etMonth.setText( String.valueOf( monthOfYear ) );
		etDate.setText( String.valueOf( dayOfMonth ) );
	}
	
	/** 手动设置日期 */
	public void resetDate( int _year , int monthOfYear , int dayOfMonth )
	{
		year = _year;
		month = monthOfYear;
		date = dayOfMonth;
		
//		checkDate( );
//		etYear.setText( String.valueOf( year ) );
//		etMonth.setText( String.valueOf( month ) );
//		etDate.setText( String.valueOf( date ) );
//		if ( odcListener != null )
//		{
//			odcListener.onDateChanged( this , year , month , date );
//		}

	}
	
	public int getYear( )
	{
		return year;
	}
	
	public int getMonth( )
	{
		return month-1;
	}
	
	public int getDate( )
	{
		return date;
	}
	
	/**
	 * {@link IARDatePicker}控件的日期变更监听器，当年、月、日发生 变更的时候，会自动调用监听器执行相应的代码
	 * 
	 * @author 余勋杰
	 */
	public interface OnDateChangedListener
	{
		void onDateChanged(IARDatePicker view, int year, int monthOfYear,
                           int dayOfMonth);
	}
	
	private int advParseInt( String intStr , int defaultValue )
	{
		int value = defaultValue;
		try
		{
			value = Integer.parseInt( intStr );
		}
		catch ( Throwable t )
		{
		}
		return value;
	}
	
}
