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
 * @author Zhengst
 * @version 创建时间：2015/10/16
 * @ClassName MySelfDelegationRecordView.java
 * @Description: 我的委托页面
 */

public class MySelfDelegationRecordView extends MySelfBaseRecordView implements View.OnClickListener
{

	private ImageView mImageView;// 图片
	private TextView tvTitle;// title
	private TextView tvDetail;//内容
	private TextView tvName;
	private RelativeLayout rlContent;

	private int CONTENT_TEXT_SIZE_DP = 14;// 内容的字体大小
	private final int JUMP_URL_TAG = R.layout.chat_record_share_mine;// 跳转的tag
	private String lastLoadImagePath = "";//上一次加载图片的url

	public MySelfDelegationRecordView( Context context )
	{
		super( context );
		
		mImageView = (ImageView) findViewById( R.id.img );
		tvTitle = (TextView) findViewById( R.id.tvTitle );
		tvDetail = (TextView) findViewById( R.id.tvDetail );
		rlContent = (RelativeLayout) findViewById( R.id.content );
		tvName = (TextView) findViewById( R.id.tvName );
	}
	
	@Override
	protected void inflatView( Context context )
	{
		LayoutInflater.from( context ).inflate( R.layout.chat_record_delegation_mine, this );
	}


	@Override
	public void initRecord(Context context, ChatRecord record )
	{

		super.initRecord( context, record );
		
		// 设置长按事件
		rlContent.setOnClickListener( this );
		rlContent.setOnLongClickListener( mRecordLongClickListener );
	}

	@Override
	public void showRecord(Context context, ChatRecord record )
	{
		ChatBarDelegationNoticeBean noticeBaen = GsonUtil.getInstance( )
			.getServerBean( record.getContent( ), ChatBarDelegationNoticeBean.class );
		if ( noticeBaen == null )
			return;

		//委托标题
		String title = noticeBaen.title;
		if ( CommonFunction.isEmptyOrNullStr( title ) )
		{
			tvTitle.setText( "" );
		}
		else
		{
			tvTitle.setText( title );
		}
		//聊吧名称
		if ( CommonFunction.isEmptyOrNullStr( noticeBaen.chatbarname ) )
		{
			tvName.setText( context.getString( R.string.my_chatbar ) + " : " + "" );
		}
		else
		{
			tvName.setText(
				context.getString( R.string.my_chatbar ) + " : " + noticeBaen.chatbarname );
		}

		//聊吧介绍
		if ( CommonFunction.isEmptyOrNullStr( noticeBaen.chatbarinfo ) )
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvDetail, getContext( ), "", CONTENT_TEXT_SIZE_DP );
			tvDetail.setText( spContent );
		}
		else
		{
			SpannableString spContent = FaceManager.getInstance( getContext( ) )
				.parseIconForString( tvDetail, getContext( ), noticeBaen.chatbarinfo,
					CONTENT_TEXT_SIZE_DP );
			tvDetail.setText( spContent );
		}

		String thumbPicUrl = noticeBaen.pic;
		if ( CommonFunction.isEmptyOrNullStr( lastLoadImagePath ) ||
			!lastLoadImagePath.equals( thumbPicUrl ) )
		{
//			ImageViewUtil.getDefault( )
//				.fadeInRoundLoadImageInConvertView( thumbPicUrl, mImageView, defShare, defShare,
//					null, 36 );
			GlideUtil.loadRoundImage(BaseApplication.appContext,thumbPicUrl,(int)context.getResources().getDimension(R.dimen.x5),mImageView, defShare, defShare);
			lastLoadImagePath = thumbPicUrl;
		}

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		rlContent.setTag(R.id.im_preview_uri, record );
		setTag( record );
		
		checkbox.setChecked( record.isSelect( ) );
		checkbox.setTag( record );
		// 设置头像
		setUserIcon( context, record, mIconView );
		// 设置消息状态
		updateStatus( context, record );
	}

	@Override
	public void reset( )
	{
		tvTitle.setText( "" );
		tvDetail.setText( "" );
		tvName.setText( "" );
		mStatusView.setText( "" );
	}

	@Override
	public void onClick( View v )
	{
		
		//		String url = (String) v.getTag(JUMP_URL_TAG);
		//		// 跳转到页面
		//		String httpPrefix = PathUtil.getHTTPPrefix();
		//		boolean isHttpUrl = url.startsWith(httpPrefix);
		//
		//		if (isHttpUrl && url.contains("gamecenter")) {
		//
		//			GameWebViewActivity.launchGameCenter(getContext(), url);
		//		} else if (isHttpUrl) {
		//			Intent intent = new Intent();
		//			intent.setClass(getContext(), WebViewAvtivity.class);
		//			intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
		//			intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE,
		//					true);
		//			getContext().startActivity(intent);
		//		} else {
		//			InnerJump.Jump(getContext(), url);
		//		}
	}

	@Override
	public void setContentClickEnabled( boolean isEnable )
	{
		rlContent.setEnabled( isEnable );
	}

	
}
