
package net.iaround.ui.comon;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;


/**
 * @ClassName: ChatPictureView
 * @Description: 聊天界面中打开图片的View，用于拦截返回按钮事件
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-10-28 下午5:06:06
 * 
 */
public class ChatPictureView extends RelativeLayout
{
	
	// 处理图片弹出层关闭的接口
	IPictureViewHandler iHandler;
	
	public ChatPictureView(Context context )
	{
		super( context );
	}
	
	public ChatPictureView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
	}
	
	public void setPicrureViewHandler( IPictureViewHandler handler )
	{
		this.iHandler = handler;
	}
	
	@Override
	public boolean onKeyDown( int keyCode , KeyEvent event )
	{
		if ( iHandler != null )
		{
			if ( iHandler.isPictureShow( ) )
			{
				if ( keyCode == KeyEvent.KEYCODE_BACK )
				{
					iHandler.close( );
					return true;
				}
			}
		}
		return super.onKeyDown( keyCode , event );
	}
	
	
	
}
