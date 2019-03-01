package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.tools.im.AudioPlayUtil.AudioPlayStateCallback;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-语音
 */

public class AccostAudioView extends AccostRecordView implements OnClickListener,AudioPlayStateCallback
{
	private TextView tvTime;
	private TextView tvContent;

	private ImageView ivPlayIcon;
	private LinearLayout llContent;
	private AnimationDrawable playAnim;
	
	public AccostAudioView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_audio );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
		tvContent = (TextView) findViewById( R.id.tvContent );

		tvContent.setText(record.getContent()+"”");
		ivPlayIcon = (ImageView) findViewById( R.id.ivPlayIcon );
		playAnim = (AnimationDrawable) ivPlayIcon.getDrawable( );
		playAnim.stop( );
		
		llContent = (LinearLayout) findViewById( R.id.llContent );
		llContent.setTag( record );
		llContent.setOnClickListener( this );
	}
	
	
	public void accostStopPlayAudio()
	{
		AudioPlayUtil.getInstance( ).releaseRes( );
	}
	
	@Override
	public void onClick( View v )
	{
		ChatRecord record = ( ChatRecord ) v.getTag( );
		if(record != null)
		{
			AudioPlayUtil.getInstance( ).playAudio(BaseApplication.appContext, record.getAttachment( ),  this);
		}
	}

	public void OnPlayStarted( )
	{
		if(playAnim != null)
		{
			playAnim.start( );
		}else
		{
			ivPlayIcon.setImageResource( R.drawable.audio_play_right );
			playAnim = (AnimationDrawable) ivPlayIcon.getDrawable( );
			playAnim.start( );
		}
	}

	@Override
	public void OnPlayingProgress( )
	{
		
	}

	@Override
	public void onPlayCompleted( )
	{
		if(playAnim != null)
		{
			playAnim.stop( );
			ivPlayIcon.setImageResource( R.drawable.audio_play_right_00 );
			ivPlayIcon.setImageResource( R.drawable.audio_play_right );
			playAnim = (AnimationDrawable) ivPlayIcon.getDrawable( );
			playAnim.stop( );
		}
	}

	@Override
	public void OnPlayError( )
	{
		CommonFunction.toastMsg( getContext() , R.string.audio_play_fail_notice );
	}
}
