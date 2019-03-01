package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.VideoPlayer;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-视频
 */

public class AccostVideoView extends AccostRecordView implements OnClickListener
{

	private TextView tvTime;
	private RelativeLayout llContent;
	private ImageView ivImage;
	
	public AccostVideoView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_video );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
		
		llContent = (RelativeLayout) findViewById( R.id.llContent );
		llContent.setOnClickListener( this );
		llContent.setTag( record );
		
		ivImage = (ImageView) findViewById( R.id.ivImage );
		String url = record.getAttachment( );
		String thumPicUrl = "";
		if(!TextUtils.isEmpty( url ))
		{
			thumPicUrl =  url.substring( 0 , ( url.length( ) - 3 ) ) + "jpg";
			int defResId = R.drawable.video_item_default_left_icon;
//			ImageViewUtil.getDefault( ).loadImage( thumPicUrl, ivImage, defResId, defResId );//jiqiang
			GlideUtil.loadImage(BaseApplication.appContext,thumPicUrl, ivImage, defResId, defResId);
		}
	}

	@Override
	public void onClick( View v )
	{
		ChatRecord record = ( ChatRecord ) v.getTag( );
		Intent intent = new Intent( getContext( ) , VideoPlayer.class );
		
		intent.putExtra( "record_datatime" , record.getDatetime( ) );
		intent.putExtra( "media_url" , record.getAttachment( ) );
		mContext.startActivity( intent );
		
		//限制连续点击事件
		llContent.setOnClickListener( null );
		new Handler( ).postDelayed(new Runnable( )
		{
			
			@Override
			public void run( )
			{
				llContent.setOnClickListener( AccostVideoView.this );
			}
		} , 2000 );
	}
	
}
