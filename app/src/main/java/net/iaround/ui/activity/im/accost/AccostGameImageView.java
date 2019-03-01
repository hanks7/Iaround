package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.RecordAccostGameBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.comon.ShowPictrueView;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-大冒险
 */

public class AccostGameImageView extends AccostRecordView
{

	private TextView tvTime;
	private TextView tvTitle;
	
	private TextView tvQuestion;
	private ImageView ivAnswer;
	
	public AccostGameImageView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_game_image );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTitle = (TextView) findViewById( R.id.tvTitle );
		tvTitle.setText( R.string.adventure_answer_title );
		
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
	
		ivAnswer = (ImageView) findViewById( R.id.ivAnswer );

		int defResId = PicIndex.DEFAULT_SMALL;

		final String sourceUrl = bean.answer;
		final String thumPicUrl = CommonFunction.thumPicture( sourceUrl );
		
//		ImageViewUtil.getDefault( ).loadImage(bean.answer, ivAnswer, defResId, defResId);//jiqiang

		ivAnswer.setOnClickListener( new OnClickListener( )
		{

			@Override
			public void onClick( View v )
			{
				new ShowPictrueView( mContext , ivAnswer , sourceUrl ,thumPicUrl,true );			}
		} );
	}
	
}
