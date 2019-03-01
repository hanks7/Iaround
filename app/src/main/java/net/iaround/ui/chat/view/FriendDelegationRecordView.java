package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatBarDelegationNoticeBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;

/**
 * @author zhengst
 * @version 创建时间：2015-10-16
 * @ClassName FriendDelegationRecordView.java
 * @Description: 朋友的委托消息
 */

public class FriendDelegationRecordView extends FriendBaseRecordView implements View.OnClickListener
{

	private ImageView mImageView;// 分享图片
	private TextView tvTitle;// 分享title
	private TextView tvDetail;// 分享内容
	private RelativeLayout rlContent;
	private TextView tvName;
	private TextView tvChatbarName;
	private TextView btn;
	private View contentView;

	private int CONTENT_TEXT_SIZE_DP = 14;// 内容的字体大小
	private String lastLoadImagePath = "";//上一次加载图片的url

	public FriendDelegationRecordView( Context context )
	{
		super( context );
		
		mImageView = (ImageView) findViewById( R.id.img );
		tvTitle = (TextView) findViewById( R.id.tvTitle );
		tvDetail = (TextView) findViewById( R.id.tvDetail );
		rlContent = (RelativeLayout) findViewById( R.id.content );
		rlContent.setBackgroundResource( contentBackgroundRes );
		tvName = (TextView) findViewById( R.id.tvName );
		tvChatbarName = (TextView) findViewById( R.id.tvChatbarName );
		btn = (TextView) findViewById( R.id.btn );
		contentView = findViewById( R.id.text_ly );
	}
	
	@Override
	protected void inflatView( Context context )
	{
		LayoutInflater.from( context ).inflate( R.layout.chat_record_delegation_other, this );
	}

	@Override
	public void initRecord(Context context, ChatRecord record )
	{
		super.initRecord( context, record );
		
		rlContent.setOnClickListener( this );

		// 设置长按事件
		if ( !bIsSystemUser( record.getFuid( ) ) )
		{
			rlContent.setOnLongClickListener( mRecordLongClickListener );
		}

	}

	@Override
	public void showRecord(Context context, ChatRecord record )
	{

		final ChatBarDelegationNoticeBean bean = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), ChatBarDelegationNoticeBean.class );
		if ( bean == null )
			return;

		//委托标题
		String title = bean.title;
		if ( CommonFunction.isEmptyOrNullStr( title ) )
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvTitle, getContext( ), "", CONTENT_TEXT_SIZE_DP );
			tvTitle.setText( spContent );
		}
		else
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvTitle, getContext( ), title, CONTENT_TEXT_SIZE_DP );
			tvTitle.setText( spContent );
		}

		//聊吧名称
		if ( CommonFunction.isEmptyOrNullStr( bean.chatbarname ) )
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvChatbarName, getContext( ), "", CONTENT_TEXT_SIZE_DP );
			tvChatbarName.setText( spContent );
		}
		else
		{
			String chatbarname =
				context.getString( R.string.my_chatbar ) + " : " + bean.chatbarname;
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvChatbarName, getContext( ), chatbarname,
					CONTENT_TEXT_SIZE_DP );
			tvChatbarName.setText( spContent );
		}

		//聊吧介绍
		if ( CommonFunction.isEmptyOrNullStr( bean.chatbarinfo ) )
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvDetail, getContext( ), "", CONTENT_TEXT_SIZE_DP );
			tvDetail.setText( spContent );
		}
		else
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvDetail, getContext( ), bean.chatbarinfo,
					CONTENT_TEXT_SIZE_DP );
			tvDetail.setText( spContent );
		}

		String thumbPicUrl = bean.pic;
		if ( CommonFunction.isEmptyOrNullStr( lastLoadImagePath ) ||
			!lastLoadImagePath.equals( thumbPicUrl ) )
		{
//			ImageViewUtil.getDefault( )
//				.fadeInRoundLoadImageInConvertView( bean.pic, mImageView, defShare, defShare, null,
//					36 );//jiqiang
			GlideUtil.loadRoundImage(BaseApplication.appContext,thumbPicUrl,(int)context.getResources().getDimension(R.dimen.x5),mImageView, defShare, defShare);
			lastLoadImagePath = thumbPicUrl;
		}
		contentView.setOnClickListener( new View.OnClickListener( )
		{
			@Override
			public void onClick( View v )
			{
			}
		} );

		if ( bean.clickSuccess == 1 )
		{
			btn.setOnClickListener( null );
			btn.setBackgroundResource( R.drawable.x_chat_delefation_friend_record_bg );
			btn.setTextColor( context.getResources().getColor( R.color.c_999999 ) );
		}
		else
		{
			btn.setOnClickListener( mAcceptDelegationClickListener );
			btn.setBackgroundResource( R.drawable.x_chat_delefation_friend_record_icon_bg );
			btn.setTextColor( context.getResources( ).getColor( R.color.white ) );
		}

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		rlContent.setTag( R.id.im_preview_uri,record );
		checkbox.setChecked( record.isSelect( ) );
		checkbox.setTag( record );
		btn.setTag( record );
		setTag( record );

		setUserNotename( context, record );
		setUserNameDis( context, record );
		// 设置头像点击事件
		setUserIcon( context, record, mIconView );
	}

	
	@Override
	public void reset( )
	{
		mIconView.getImageView( ).setImageBitmap( null );
		tvTitle.setText( "" );
		tvName.setText( "" );
		tvDetail.setText( "" );
	}

	@Override
	public void onClick( View v )
	{

	}

	@Override
	public void setContentClickEnabled( boolean isEnable )
	{
		rlContent.setEnabled( isEnable );
	}
}
