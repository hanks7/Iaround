
package net.iaround.ui.space.more;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.ui.comon.SuperActivity;


public class BindTelphoneForOne extends SuperActivity implements OnClickListener
{
	private int type = -1;
	private int finish = 0;
	private TextView title_name;
	private ImageView mIvLeft;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.bindtelphone_for_one );
		Common.getInstance( ).addBindActivity( this );
		type = getIntent( ).getExtras( ).getInt( "type" );
		finish = getIntent( ).getExtras( ).getInt( "finish" );
		initView( );
	}
	
	private void initView( )
	{
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById( R.id.iv_left ).setOnClickListener( this );
		findViewById( R.id.bind_telphone ).setOnClickListener( this );
		title_name = (TextView) findViewById( R.id.tv_title );
		mIvLeft = (ImageView) findViewById(R.id.iv_left);
		title_name.setText( R.string.telphone_bind );
		mIvLeft.setImageResource(R.drawable.title_back);
	}
	
	@Override
	public void onClick( View v )
	{
		switch ( v.getId( ) )
		{
			case R.id.fl_left:
			case R.id.iv_left :
			{
				finish( );
			}
				break;
			case R.id.bind_telphone :
			{
				if ( type == 0 )
				{
					// 无密码
					Intent intent = new Intent( mContext , EnterTelActivity.class );
					intent.putExtra( "type" , 0 );// 需设置密码
					mContext.startActivity( intent );
					if ( finish == 1 )
						finish( );
				}
				else if ( type == 1 )
				{
					// 有密码
					Intent intent = new Intent( mContext , EnterPwdActivity.class );
					intent.putExtra( "type" , 1 );// 需输入密码
					mContext.startActivity( intent );
					if ( finish == 1 )
						finish( );
				}
				
			}
				break;
			
			default :
				break;
		}
	}
	
}
