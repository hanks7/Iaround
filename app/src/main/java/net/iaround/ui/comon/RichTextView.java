
package net.iaround.ui.comon;


import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.FaceManager;


/**
 * 富文本显示
 * 
 * @author linyg
 * 
 */
public class RichTextView extends TextView
{
	private Context mContext;
	
	public RichTextView(Context context )
	{
		super( context );
		mContext = context;
	}
	
	public RichTextView(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		mContext = context;
	}
	
	/**
	 * 替换图标
	 * 
	 * @param text
	 */
	public void parseIcon( )
	{
		Resources res = getResources( );
		float faceSize = res.getDimension( R.dimen.face_height );
		parseIcon( faceSize );
	}

	/**
	 * 替换图标
	 *
	 * @param text
	 */
	public void parseIconWorld( )
	{
		Resources res = getResources( );
		float faceSize = res.getDimension( R.dimen.face_world_message_height );
		parseIcon( faceSize );
	}
	
	/**
	 * 替换图标
	 * 
	 * @param text
	 */
	public void parseIcon( float faceSize )
	{
		String text = getText( ).toString( );
		FaceManager faceManager = FaceManager.getInstance( mContext );

		setText( faceManager.parseIconForString( this , mContext , text , (int)(faceSize/2) ) );
	}

	public void parseIconWithColor(float faceSize){

		String text = getText().toString();
		FaceManager faceManager = FaceManager.getInstance(mContext);
		SpannableString spannableString = faceManager.parseIconForString(this, mContext, text, (int) (faceSize / 2));

		setText(Html.fromHtml(spannableString.toString()));
	}
}
