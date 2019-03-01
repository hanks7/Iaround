package net.iaround.ui.activity.im.accost;

import java.io.File;

import pl.droidsonroids.gif.GifImageView;

import net.iaround.BaseApplication;
import net.iaround.entity.RecordFaceBean;
import android.content.Context;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import net.iaround.R;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.glide.GlideUtil;

/**
 * @ClassName AccostFaceView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-7 上午11:19:29
 * @Description: 收到搭讪-动态表情
 */

public class AccostFaceView extends AccostRecordView
{
	private TextView tvTime;
	private GifImageView givImage;
	
	public AccostFaceView( Context context )
	{
		super( context );
		createView( R.layout.accost_record_face );
	}

	@Override
	public void build( ChatRecord record )
	{
		tvTime = ( TextView ) findViewById( R.id.tvTime );
		String timeStr = TimeFormat.timeFormat4( mContext , record.getDatetime( ) );
		tvTime.setText( timeStr );
		
		
		RecordFaceBean bean = GsonUtil.getInstance( ).getServerBean( record.getContent( ) , RecordFaceBean.class );
		String face = bean.pkgid + "_" + bean.mapid;
		File faceFile = FaceManager.getInstance( mContext ).getFaceStreamFromPath( face );
		int defImageRes = PicIndex.DEFAULT_SMALL;
		
		givImage = (GifImageView)findViewById( R.id.givImage );
		
		String faceUrl = "";
		if(faceFile !=null &&faceFile.exists( ))
		{
			faceUrl = faceFile.getAbsolutePath( );
		}else
		{
			faceUrl = record.getAttachment( );
		}
		GlideUtil.loadImageGif(BaseApplication.appContext,faceUrl,givImage,defImageRes,defImageRes);
//		Glide.with(getContext()).load(faceUrl).asGif().placeholder(defImageRes).error(defImageRes).into(givImage);
//		GifViewUtil.getInstance( ).loadGifDrawble( mContext , faceUrl , givImage , defImageRes );
	}	
	
}
