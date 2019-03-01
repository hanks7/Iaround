
package net.iaround.ui.chat;


import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceIcon;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 表情菜单视图：(聊天)点击表情图片的事件响应
 * 
 * @author chenlb
 * 
 */
public class FaceIconClickListener implements OnItemClickListener
{
	private static int FACE_TAG_NUM = 4;
	private Context mContext;
	private EditText editContent;
	protected Pattern p2 = Pattern.compile( "\\{\\#\\w+\\#\\}" );

	protected Pattern p1 = Pattern.compile( FaceManager.regex );
	
	final KeyEvent keyEventDown = new KeyEvent( KeyEvent.ACTION_DOWN , KeyEvent.KEYCODE_DEL );
	
	public FaceIconClickListener(Context context , EditText editContent )
	{
		this.mContext = context;
		this.editContent = editContent;
	}
	
	@Override
	public void onItemClick(AdapterView< ? > parent , View view , int position , long id )
	{
		FaceIcon icon = ( FaceIcon ) view.getTag( );
		Matcher m1 =null;
		if(icon.key.contains( "{" ))
		{
			 m1 = p1.matcher( icon.key );
		}
		else if(icon.key.startsWith(FaceManager.catFlag ))
		{
			m1 = p1.matcher( icon.key );
		}
		else
		{
			String strUnicode = CommonFunction.faceStr2Unicode( icon.key) ;
			 m1 = p1.matcher( strUnicode );
		}
		
		// 设置表情
		if ( "back".equals( icon.key ) )
		{
			editContent.onKeyDown( KeyEvent.KEYCODE_DEL , keyEventDown );
		} else if ( m1.find( ))
		{
			// 设置表情
			CommonFunction.setFace( mContext , editContent , icon.key , icon.iconId , Integer.MAX_VALUE );
		}
	}
	
	
	
}
