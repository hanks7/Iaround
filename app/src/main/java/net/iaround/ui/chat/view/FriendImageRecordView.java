
package net.iaround.ui.chat.view;


import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.GroupModel;

import java.util.ArrayList;


/**
 * @ClassName FriendImageRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 朋友的图片消息
 */

public class FriendImageRecordView extends FriendBaseRecordView implements
		View.OnClickListener
{
	
	private ImageView mImageView;
	private FrameLayout flContent;
	private String lastLoadImagePath = "";// 上一次加载图片的url
	
	public FriendImageRecordView( Context context )
	{
		super( context );
		mImageView = (ImageView) findViewById( R.id.content );
		flContent = (FrameLayout) findViewById( R.id.flContent );
		flContent.setBackgroundResource( contentBackgroundRes );
	}
	
	@Override
	protected void inflatView( Context context )
	{
		LayoutInflater.from( context ).inflate( R.layout.chat_record_image_other , this );
	}
	
	
	@Override
	public void initRecord(Context context , ChatRecord record )
	{
		
		super.initRecord( context , record );
		mImageView.setOnClickListener( this );
		
		if ( !bIsSystemUser( record.getFuid( ) ) )
		{
			mImageView.setOnLongClickListener( mRecordLongClickListener );
		}
	}
	
	@Override
	public void showRecord(Context context , ChatRecord record )
	{
		String thumbPicUrl = CommonFunction.thumPicture( record.getAttachment( ) );
		if ( TextUtils.isEmpty( lastLoadImagePath ) || !lastLoadImagePath.equals( thumbPicUrl ) )
		{
//			ImageViewUtil.getDefault( ).fadeInRoundLoadImageInConvertView( thumbPicUrl ,
//					mImageView , defShare , defShare , null , 36 );//jiqiang
			GlideUtil.loadRoundImage(BaseApplication.appContext,thumbPicUrl,(int)context.getResources().getDimension(R.dimen.x5),mImageView);
			lastLoadImagePath = thumbPicUrl;
		}

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		mImageView.setTag(R.id.im_preview_uri, record );
		checkbox.setChecked( record.isSelect( ) );
		checkbox.setTag( record );
		setTag( record );
		setUserNotename( context , record );
		setUserNameDis( context , record );
		// 设置头像点击事件
		setUserIcon( context , record , mIconView );
	}
	
	@Override
	public void reset( )
	{
		mIconView.getImageView( ).setImageBitmap( null );
	}


	@Override
	public void onClick( View v )
	{
		ChatRecord record = ( ChatRecord ) v.getTag( R.id.im_preview_uri );
		String picUrl = record.getAttachment( );
		String thumbPicUrl = CommonFunction.thumPicture( picUrl );
		// new ShowPictrueView(getContext(), v, picUrl, thumbPicUrl);
		
		ArrayList< String > list = null;
		if ( ChatPersonalModel.getInstance( ).isInPersonalChat( ) )
			list = ChatPersonalModel.getInstance( ).getPrivateChatThumPicUrlList(
					getContext( ) , record.getUid( ) , record.getFuid( ) );
		else if ( GroupModel.getInstance( ).isInGroupChat( ) )
			list = GroupModel.getInstance( ).getGroupChatThumPicUrlList( getContext( ) ,
					Long.parseLong( GroupModel.getInstance( ).getGroupId( ) ) );
		
		if ( list != null && list.size( ) > 0 )
		{
			int pos = list.indexOf( thumbPicUrl );
			pos = pos < 0 ? list.size( ) - 1 : pos;
			PictureDetailsActivity.launch(getContext(),list,pos);
		}
	}
	
	@Override
	public void setContentClickEnabled( boolean isEnable )
	{
		mImageView.setEnabled( isEnable );
	}
	
}
