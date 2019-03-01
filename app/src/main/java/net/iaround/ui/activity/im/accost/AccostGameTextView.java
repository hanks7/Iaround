package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.text.SpannableString;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.RecordAccostGameBean;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-真心话
 */

public class AccostGameTextView extends AccostRecordView
{
	private TextView tvTime;
	private TextView tvTitle;
	
	private TextView tvQuestion;
	private TextView tvAnswer;
	
	public AccostGameTextView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_game_text );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTitle = (TextView) findViewById( R.id.tvTitle );
		tvTitle.setText( R.string.true_word_answer_title );
		
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
//		TextView distance = ( TextView ) findViewById( R.id.tvDistance );	
//		int userDistance = record.getDistance( );
//		if ( userDistance < 0 )
//		{ // 不可知
//			distance.setText( R.string.unable_to_get_distance );
//		}
//		else
//		{
//			distance.setText( CommonFunction.covertSelfDistance( userDistance ) );
//		}
		
		RecordAccostGameBean bean = GsonUtil.getInstance( ).getServerBean( record.getContent( ) , RecordAccostGameBean.class );
		tvQuestion = (TextView) findViewById( R.id.tvQuestion );
		
		SpannableString spSign = FaceManager.getInstance( mContext ).parseIconForString( tvQuestion ,
				mContext , bean.question,16 );
		tvQuestion.setText( spSign );
	
		tvAnswer = (TextView) findViewById( R.id.tvAnswer );
		
		 spSign = FaceManager.getInstance( mContext ).parseIconForString( tvAnswer ,
				mContext , bean.answer ,16  );
		 tvAnswer.setText( spSign );
	}
	
}
