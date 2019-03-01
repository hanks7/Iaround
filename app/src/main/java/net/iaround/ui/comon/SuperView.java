
package net.iaround.ui.comon;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import net.iaround.BaseApplication;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.PermissionUtils;


/**
 * view
 * 
 * @author linyg
 * 
 */
public class SuperView extends FrameLayout implements OnClickListener,
		ActivityToViewListener , HttpCallBack
{
	public static final Object TAG = new Object( );
	
	private SuperActivity vAttachAtivity;
	private LayoutInflater vInflater; // xml 解析器对象
	
	public SuperView(SuperActivity activity , AttributeSet attrs )
	{
		super( activity , attrs );
		setTag( TAG );
		vAttachAtivity = activity;
		vInflater = LayoutInflater.from( activity );
		initialComponent( activity );
	}
	
//	public SuperView(SuperActivity activity , int child )
//	{
//		super( activity );
//		setTag( TAG );
//		vAttachAtivity = activity;
//		vInflater = LayoutInflater.from( activity.getApplicationContext( ) );
//		addView( child , new LayoutParams( LayoutParams.MATCH_PARENT ,
//				LayoutParams.MATCH_PARENT ) );
//		initialComponent( activity );
//	}

	public SuperView(SuperActivity activity, int layoutResID) {
		super( activity );
		setTag( TAG );
		vAttachAtivity = activity;
		vInflater = LayoutInflater.from( activity );
		addView( vInflater.inflate( layoutResID , null ) , new LayoutParams(
				LayoutParams.MATCH_PARENT , LayoutParams.MATCH_PARENT ) );
		initialComponent( activity );
	}

	public void init( )
	{
		
	}
	
	public SuperActivity getAttachActivity( )
	{
		return vAttachAtivity;
	}
	
	public LayoutInflater getLayoutInflater( )
	{
		return vInflater;
	}
	
	public void onActivityStop( )
	{
		
	}
	
	public void onActivityResume( )
	{
		
	}
	
	/**
	 * 当视图进入屏幕时调用
	 */
	protected void onAttachedToWindow( )
	{
		super.onAttachedToWindow( );
		setFocus( vAttachAtivity );
		visibleWindow( vAttachAtivity );
	}
	
	/**
	 * 当视图离开屏幕时调用
	 */
	protected void onDetachedFromWindow( )
	{
		super.onDetachedFromWindow( );
		invisibleWindow( vAttachAtivity );
	}
	
	protected void onLowMemory( )
	{
		
	}
	
	/**
	 * 屏幕显示时的操作
	 */
	protected void visibleWindow( SuperActivity context )
	{
		
	}
	
	/**
	 * 移除屏幕时的操作
	 */
	protected void invisibleWindow( SuperActivity context )
	{
		
	}
	
	/**
	 * 初始化视图组件
	 * 
	 * @param context
	 *            环境对象
	 */
	protected void initialComponent( SuperActivity context )
	{
		
	}
	
	public void onClick( View v )
	{
		
	}
	
	protected void setFocus( SuperActivity context )
	{
		
	}
	
	@Override
	public void onErrorMessageListener( int e )
	{
		
	}
	
	@Override
	public void onSendMessageListener( int e , long flag )
	{
		
	}
	
	@Override
	public void onReceiveMessageListener( TransportMessage message )
	{
		
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		// TODO Auto-generated method stub
		
	}
	
	public void onActivityResult( int requestCode , int resultCode , Intent data )
	{
		
	}
	/**
	 * 获取摄像头权限
	 */
	public void requestCamera(Activity activity) {
		PermissionUtils.requestPermission(activity, PermissionUtils.CODE_CAMERA, mPermissionGrant);
	}
	/**
	 * 获取摄像头权限后执行操作
	 */
	public void doCamera() {}

	private PermissionUtils.PermissionGrant mPermissionGrant = new PermissionUtils.PermissionGrant() {
		@Override
		public void onPermissionGranted(int requestCode) {
			switch (requestCode) {
				case PermissionUtils.CODE_CAMERA://获取摄像头权限
					doCamera();
					break;
			}
		}
	};
}
