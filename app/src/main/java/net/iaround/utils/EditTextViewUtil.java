
package net.iaround.utils;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;


public class EditTextViewUtil extends EditText
{
	private OnEditPast mOnEdeitPast;
	
	public EditTextViewUtil(Context context )
	{
		super( context );
		
	}
	
	public EditTextViewUtil(Context context , AttributeSet attrs )
	{
		super( context , attrs );
		
	}
	
	public EditTextViewUtil(Context context , AttributeSet attrs , int defStyle )
	{
		super( context , attrs , defStyle );
		
	}
	
	public void setOnEditPas( OnEditPast callBackOnEdeitPast )
	{
		this.mOnEdeitPast = callBackOnEdeitPast;
	}

	@Override
	protected boolean getDefaultEditable( )
	{
		return true;
	}
	
	@Override
	public boolean onTextContextMenuItem( int id )
	{
		// TODO Auto-generated method stub
		boolean isMenuItem = super.onTextContextMenuItem( id );
		 
		if( isMenuItem &&id ==android.R.id.paste)
		{
			if(mOnEdeitPast!=null)
			{
				return isMenuItem; //2014-12-27 
			//粘贴的时候不处理表情的分析
//				mOnEdeitPast.onEditPastToView( this );
			}
		}
		return isMenuItem;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (listener != null) {
				listener.back(this);
			}
		}
		return false;

	}
	public interface BackListener {
		void back(TextView textView);
	}

	public BackListener listener;

	public void setBackListener(BackListener listener) {
		this.listener = listener;
	}
}
