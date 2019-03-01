
package net.iaround.ui.chat;


import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;



/**
 * 表情菜单界面的弹出键盘的按钮点击事件 <br/>
 * <br/>
 * 表情视图：切换到键盘的事件响应
 * 
 * @author chenlb
 * 
 */
public class FaceKeyboardClickListener implements OnClickListener
{
	private Context mContext;
	
	public FaceKeyboardClickListener( Context context )
	{
		this.mContext = context;
	}
	
	@Override
	public void onClick( View v )
	{
		if ( mContext instanceof ChatPersonal)
		{
			( (ChatPersonal) mContext ).hideFaceShowKeyboard( );
		}
//		else if ( mContext instanceof GroupChatTopicActivity )
//		{
//			( ( GroupChatTopicActivity ) mContext ).hideFaceShowKeyboard( );
//		}
	}
	
}
