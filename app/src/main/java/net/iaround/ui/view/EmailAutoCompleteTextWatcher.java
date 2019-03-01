
package net.iaround.ui.view;


import android.text.Editable;
import android.text.TextWatcher;
import android.widget.AutoCompleteTextView;

import net.iaround.ui.adapter.EmailAutoCompleteAdapter;


/**
 * 邮箱地址自动补全的TextWatcher
 * 
 * @author chenlb
 * 
 */
public class EmailAutoCompleteTextWatcher implements TextWatcher
{
	private AutoCompleteTextView autoTextView;
	private EmailAutoCompleteAdapter emailAdapter;
	private String[ ] emailSuffixArray =
		{ "@163.com" , "@qq.com" , "@gmail.com" , "@126.com" , "@yahoo.com" , "@sina.com" ,
				"@sina.cn" , "@sohu.com" , "@hotmail.com" , "@vip.sina.com" , "@21cn.com" ,
				"@tom.com" , "@139.com" };
	
	public EmailAutoCompleteTextWatcher(AutoCompleteTextView autoCompleteTextView ,
										EmailAutoCompleteAdapter emailAutoCompleteAdapter )
	{
		autoTextView = autoCompleteTextView;
		emailAdapter = emailAutoCompleteAdapter;
	}
	
	public void onTextChanged( CharSequence s , int start , int before , int count )
	{
	}
	
	@Override
	public void beforeTextChanged( CharSequence s , int start , int count , int after )
	{
	}
	
	public void afterTextChanged( Editable s )
	{
		try
		{
			autoTextView.clearListSelection( );
			String input = s.toString( );
			if ( !autoTextView.isPopupShowing( ) )
			{
				autoTextView.showDropDown( );
			}
			createCandidateEmail( input );
		}
		catch ( Exception e )
		{
		}
	}
	
	private void createCandidateEmail( String name )
	{
		emailAdapter.mList.clear( );
		int index = name.indexOf( "@" );
		if ( index > 0 )
		{
			String suffix = name.substring( index );
			for ( String string : emailSuffixArray )
			{
				if ( string.startsWith( suffix ) )
				{
					emailAdapter.mList.add( name.substring( 0 , index ) + string );
				}
			}
			// } else {
			// for (int i = 0; i < emailSuffixArray.length; ++i) {
			// emailAdapter.mList.add(name + emailSuffixArray[i]);
			// }
		}
		emailAdapter.notifyDataSetChanged( );
	}
}
