package net.iaround.ui.chat.view;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.entity.RecordFaceBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.ui.face.FaceDetailActivityNew;
import net.iaround.ui.view.pipeline.UserTitleView;
import net.iaround.utils.ImageViewUtil;

import java.io.File;
import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @ClassName MySelfFaceRecordView.java
 * @Description: 我的表情消息
 */

public class MySelfFaceRecordView extends MySelfBaseRecordView implements View.OnClickListener
{

	private ImageView mImageView;
	private LinearLayout llContent;


	public MySelfFaceRecordView( Context context )
	{
		super( context );
		mImageView = (ImageView) findViewById( R.id.content_img );
		llContent = (LinearLayout) findViewById( R.id.content );

	}

	@Override
	protected void inflatView( Context context )
	{
		LayoutInflater.from( context ).inflate( R.layout.chat_record_gifface_mine, this );
	}

	@Override
	public void initRecord(Context context, ChatRecord record )
	{

		super.initRecord( context, record );
		
		llContent.setOnClickListener( this );
		llContent.setOnLongClickListener( mRecordLongClickListener );

	}

	@Override
	public void showRecord(Context context, final ChatRecord record )
	{

		RecordFaceBean bean = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), RecordFaceBean.class );
		String face = bean.pkgid + "_" + bean.mapid;
		File faceFile = FaceManager.getInstance( context ).getFaceStreamFromPath( face );
		if ( faceFile != null && faceFile.exists( ) )
		{

			String facePath = faceFile.getAbsolutePath( );
			boolean isDynamic = facePath.contains( PathUtil.getDynamicFacePostfix( ) );
			boolean isgif = facePath.contains( PathUtil.getGifPostfix( ) );

			if ( isgif || isDynamic )
			{
				GifDrawable gitDrawable;
				try
				{
					gitDrawable = new GifDrawable( faceFile );
					mImageView.setImageDrawable( gitDrawable );
				}
				catch ( IOException e )
				{
					e.printStackTrace( );
				}
			}
			else
			{
				String fileUrl = PathUtil.getFILEPrefix( ) + faceFile.getAbsolutePath( );
				ImageViewUtil.getDefault( ).loadImage( fileUrl, mImageView, defSmall, defSmall );//jiqiang
//				GlideUtil.loadImage(getContext(),fileUrl,mImageView, defSmall, defSmall);
			}
		}
		else
		{
			String tumbPicUrl = CommonFunction.thumPicture( record.getAttachment( ) );
			ImageViewUtil.getDefault( ).loadImage( tumbPicUrl, mImageView, defSmall, defSmall );
//			GlideUtil.loadImage(getContext(),tumbPicUrl,mImageView, defSmall, defSmall);
		}

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		// 设置长按事件
		llContent.setTag(R.id.im_preview_uri, record );
		setTag( record );
		checkbox.setChecked( record.isSelect( ) );
		checkbox.setTag( record );
		// 设置头像点击事件
		setUserIcon( context, record, mIconView );
		// 设置消息状态
		updateStatus( context, record );
		//设置昵称和身份标识及排名
		setUserNameDis(context,record,record.getMgroupRole());
	}

	@Override
	public void reset( )
	{
		mStatusView.setText( "" );
		llContent.setBackgroundResource( R.drawable.transparent );
	}

	@Override
	public void onClick( View v )
	{

		ChatRecord record = ( ChatRecord ) v.getTag( R.id.im_preview_uri);
		RecordFaceBean bean = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), RecordFaceBean.class );
		if ( bean != null )
		{
//			FaceDetailActivity.launch( (Activity) getContext( ), Integer.parseInt( bean.pkgid ) ); 于超  解决图片变形
			FaceDetailActivityNew.launch((Activity) getContext(),Integer.parseInt(bean.pkgid));
		}
		
	}

	@Override
	public void setContentClickEnabled( boolean isEnable )
	{
		llContent.setEnabled( isEnable );
	}

}
