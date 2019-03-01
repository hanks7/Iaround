
package net.iaround.ui.chat;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.BusinessHttpProtocol;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.EditTextUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.StringUtil;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.LongNoteModel;
import net.iaround.utils.EditTextViewUtil;
import net.iaround.utils.OnEditPast;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * 编辑他人常备注
 * 
 * @author tanzy
 * */
public class EditLongNoteActivity extends SuperActivity implements HttpCallBack, OnClickListener
{
	private TextView title;
	private ImageView back;
	private EditTextViewUtil edit;
	
	long userid;
	String longNote;
	boolean firstIn;
	
	long getNoteFlag;
	String strContent="";
	public static int EDIT_LONG_NOTE = 1024;
	
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_edit_long_note );
		
		// showDialog( );
		title = (TextView) findViewById( R.id.title_name );
		title.setText( R.string.long_note );
		
		back = (ImageView) findViewById( R.id.title_back );
		
		edit = ( EditTextViewUtil ) findViewById( R.id.edit_input );
		EditTextUtil.autoLimitLength( edit , 1000 );
		
		userid = getIntent( ).getLongExtra( "userid" , 0 );
		firstIn = getIntent( ).getBooleanExtra( "firstin" , firstIn );
		
		back.setOnClickListener( this );
		
		String buff = LongNoteModel.getInstent( ).getLongNote( userid + "" );
		edit.setText( buff );
		
		if ( firstIn )
			getNoteFlag = BusinessHttpProtocol.getLongNote( mContext , userid , this );
		
		edit.setOnEditPas( new OnEditPast( )
		{
			
			@Override
			public void onEditPastToView( EditTextViewUtil editText )
			{
				// TODO Auto-generated method stub
				// mHandler.sendEmptyMessage( 1 );
				FaceManager.getInstance( mActivity ).parseIconForEditText( mActivity , edit );
				String mContent = edit.getText( ).toString( );
				edit.setSelection( Math.max( 0 , mContent.length( ) ) );
			}
		} );
		
		
		CommonFunction.MoveCursorToLast( edit );
		
	}
	
	@Override
	public void onGeneralSuccess(String result , long flag )
	{
		super.onGeneralSuccess( result , flag );
		if ( flag == getNoteFlag )
		{
			// hideDialog( );
			try
			{
				JSONObject obj = new JSONObject( result );
				if ( obj.has( "status" ) && obj.optLong( "status" ) == 200 )
				{
					longNote = CommonFunction.jsonOptString( obj , "remarks" );
					// if ( !CommonFunction.isEmptyOrNullStr( longNote ) )
					edit.setText( longNote );
					
					FaceManager.getInstance( mActivity ).parseIconForEditText( mActivity , edit );
					String mContent = edit.getText( ).toString( );
					edit.setSelection( Math.max( 0 , mContent.length( ) ) );
				}
				else
				{
					CommonFunction.log( "sherlock" , "error " + result + " ---- "
							+ this.getClass( ).getName( ) );
				}
			}
			catch ( JSONException e )
			{
				e.printStackTrace( );
			}
		}
	}
	
	@Override
	public void onGeneralError( int e , long flag )
	{
		super.onGeneralError( e , flag );
		// hideDialog( );
		if ( flag == getNoteFlag )
		{
			ErrorCode.toastError( mContext , e );
			CommonFunction.log( "sherlock" , "get note error ==" + e + " ---- "
					+ this.getClass( ).getName( ) );
		}
	}
	
	@Override
	public void onClick( View v )
	{
		if ( v.getId( ) == R.id.title_back )
		{
			// showDialog( );
			CommonFunction.hideInputMethod( mContext , edit );
			String newLongNote = edit.getText( ).toString( );
			
//			if ( CommonFunction.filterKeyWord( mContext , newLongNote.trim( ) ) )
//			{
//				CommonFunction.log( "sherlock" , "filterKeyWord" + " ---- "
//						+ this.getClass( ).getName( ) );
//				return;
//			}
			
			if ( StringUtil.getLengthCN1( newLongNote ) > 1000 )
			{
				Toast.makeText( EditLongNoteActivity.this , R.string.text_too_long ,
						Toast.LENGTH_SHORT ).show( );
				return;
			}
			
			CommonFunction.log( "sherlock" ,
					"newLongNote.equals( longNote )" == newLongNote.equals( longNote ) + " ---- "
							+ this.getClass( ).getName( ) );
			CommonFunction.log( "sherlock" , "newLongNote == " + newLongNote + " ---- "
					+ this.getClass( ).getName( ) );
			CommonFunction.log( "sherlock" , "longNote == " + longNote + " ---- "
					+ this.getClass( ).getName( ) );
			
			if ( newLongNote != null
					&& !newLongNote.equals( LongNoteModel.getInstent( ).getLongNote( userid + "" ) ) )
				LongNoteModel.getInstent( ).saveLongNote( userid + "" , newLongNote );
			
			if ( !newLongNote.equals( longNote ) )
			{
				CommonFunction.log( "sherlock" , "newLongNote == " + newLongNote + " ---- "
						+ this.getClass( ).getName( ) + " - "
						+ Thread.currentThread( ).getStackTrace( )[ 2 ].getMethodName( ) );
				
				Intent data = new Intent( );
				data.putExtra( "newLongNote" , newLongNote );
				data.putExtra( "userid" , userid );
				setResult( Activity.RESULT_OK , data );
				finish( );
			}
			else
			{
				// hideDialog( );
				setResult( Activity.RESULT_CANCELED );
				finish( );
			}
		}
	}
	
	
}
