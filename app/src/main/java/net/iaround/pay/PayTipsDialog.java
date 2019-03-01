
package net.iaround.pay;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import net.iaround.R;


/**
 * @Description: 提示支付情况
 * @author
 * @date
 */
public class PayTipsDialog extends Dialog
{
	private Context context;
	
	private View.OnClickListener waitRefresh;
	private View.OnClickListener netHelp;
	private View.OnClickListener OnClickClose;
	
	public void setCloseListenner( View.OnClickListener click )
	{
		OnClickClose = OnClickClose;
	}
	
	public PayTipsDialog(Context context )
	{
		super( context , R.style.guide_dialog_style );
		this.context = context;
		setContentView( R.layout.x_pay_waiting_dialog );
	}
	
	public PayTipsDialog(Context context , String title , String result )
	{
		super( context , R.style.guide_dialog_style );
		this.context = context;
		
		initViews( title , result );
	}
	
	
	public PayTipsDialog(Context context , String title , View.OnClickListener refreshClick ,
						 View.OnClickListener helpClick )
	{
		super( context , R.style.guide_dialog_style );
		this.context = context;
		
		// dialogView = LayoutInflater.from( mContext ).inflate( layoutRes,
		// null );
		netHelp = helpClick;
		waitRefresh = refreshClick;
		initViews( title , waitRefresh , netHelp );
	}

	private void initViews(String title , View.OnClickListener refreshClick ,
						   View.OnClickListener helpClick )
	{
		setContentView( R.layout.x_pay_wait_dialog );
		
		TextView tv = (TextView) findViewById( R.id.tv_title );
		tv.setText( title );
		
		findViewById( R.id.fresh ).setOnClickListener( refreshClick );
		findViewById( R.id.help ).setOnClickListener( helpClick );
		findViewById( R.id.iv_left ).setOnClickListener(new View.OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
				dismiss( );
			}
		} );
		findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		findViewById( R.id.open ).setOnClickListener(new View.OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				dismiss( );
				
			}
		} );
	}
	
	private void initViews(String title , String result )
	{
		setContentView( R.layout.x_pay_result_dialog );
		
		TextView tv = (TextView) findViewById( R.id.tv_title );
		tv.setText( title );
		
		TextView detail = (TextView) findViewById( R.id.detail );
//		detail.setText( result );
		detail.setText( Html.fromHtml( result ) );
		
		if ( OnClickClose != null )
		{
			findViewById( R.id.iv_left ).setOnClickListener( OnClickClose );
			findViewById( R.id.open ).setOnClickListener( OnClickClose );
		}
		else
		{
			findViewById(R.id.fl_left).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
				}
			});
			findViewById( R.id.iv_left ).setOnClickListener(new View.OnClickListener( )
			{
				@Override
				public void onClick( View v )
				{
					dismiss( );
					
				}
			} );
			
			findViewById( R.id.open ).setOnClickListener(new View.OnClickListener( )
			{
				
				@Override
				public void onClick( View v )
				{
					dismiss( );
				}
			} );
		}
	}
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		
	}
}
