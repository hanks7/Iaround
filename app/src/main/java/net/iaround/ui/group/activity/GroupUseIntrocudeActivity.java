
package net.iaround.ui.group.activity;


import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.ui.comon.SuperActivity;


/**
 * @ClassName: GroupUseIntrocudeActivity
 * @Description: 圈子使用需知界面
 * @author zhonglong kylin17@foxmail.com
 * @date 2014-1-22 上午11:07:18
 * 
 */
public class GroupUseIntrocudeActivity extends SuperActivity
{
	
	private ImageView mTitleBack;
	private TextView mTitleName;
	private Button mBtnStart;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_group_use_introduce );
		initViews( );
	}
	
	/**
	 * @Title: initViews
	 * @Description:
	 */
	private void initViews( )
	{
		mTitleBack = (ImageView) findViewById( R.id.iv_left );
		mTitleName = (TextView) findViewById( R.id.tv_title);
		mBtnStart = (Button) findViewById( R.id.btn_to_group_chat );
		
		mTitleBack.setVisibility( View.GONE );
		mTitleName.setText( R.string.group_user_note );
		mBtnStart.setOnClickListener( new OnClickListener( )
		{
			
			@Override
			public void onClick( View v )
			{
				finish( );
			}
		} );
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_BACK )
		{
			finish( );
			return true;
		}
		return super.onKeyDown( keyCode , event );
	}
	
}
