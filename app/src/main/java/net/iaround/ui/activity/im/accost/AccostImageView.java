package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.ShowPictrueView;


/**
 * @ClassName FindNoticeView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-图片
 */

public class AccostImageView extends AccostRecordView
{

	private TextView tvTime;
	private ImageView ivImage;
	
	public AccostImageView(Context context )
	{
		super( context );
		createView( R.layout.accost_record_image );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTime = (TextView) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
		ivImage = (ImageView) findViewById( R.id.ivImage );
		int defResId = PicIndex.DEFAULT_SMALL;
		final String sourceUrl = record.getAttachment( );
		final String thumPicUrl = CommonFunction.thumPicture( sourceUrl );
		
//		ImageViewUtil.getDefault( ).loadImage(thumPicUrl, ivImage, defResId, defResId);//jiqiang
		GlideUtil.loadImage(BaseApplication.appContext,thumPicUrl,ivImage,defResId,defResId);

		ivImage.setOnClickListener( new OnClickListener( )
		{

			@Override
			public void onClick( View v )
			{
				new ShowPictrueView( mContext , ivImage , sourceUrl ,thumPicUrl,true );
			}
		} );
	}
	
}
