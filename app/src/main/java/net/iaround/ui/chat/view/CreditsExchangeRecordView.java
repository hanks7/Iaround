
package net.iaround.ui.chat.view;


import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;


public class CreditsExchangeRecordView extends ChatRecordView
{
	private TextView creditsTipView;
	
	public CreditsExchangeRecordView( Context context )
	{
		super( context );
		LayoutInflater.from( context ).inflate(
				R.layout.chat_room_record_list_credits_exchange_item , this );
		creditsTipView = (TextView) findViewById( R.id.forbidTip );
	}
	
	@Override
	public void initRecord(Context context , ChatRecord record )
	{
	}
	
	@Override
	public void showRecord(Context context , ChatRecord record )
	{
		String url_text = context.getString( R.string.learn_more );
		creditsTipView.setText( record.getContent( ) );
		SpannableString spStr = new SpannableString( url_text );
		spStr.setSpan( new ClickableSpan( )
		{
			@Override
			public void updateDrawState( TextPaint ds )
			{
				super.updateDrawState( ds );
				ds.setColor( Color.parseColor( "#22a4ff" ) ); // 设置文件颜色
				ds.setUnderlineText( true ); // 设置下划线
			}
			
			@Override
			public void onClick( View widget )
			{
				widget.setOnClickListener( mCreditsClickListener );
				
			}
		} , 0 , url_text.length( ) , Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
		
		creditsTipView.setHighlightColor( Color.TRANSPARENT ); // 设置点击后的颜色为透明，否则会一直出现高亮
		creditsTipView.append( spStr );
		creditsTipView.setMovementMethod( LinkMovementMethod.getInstance( ) );// 开始响应点击事件
	}
	
	@Override
	public void reset( )
	{
		
	}
}
