package net.iaround.ui.chat.view;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.analytics.ums.DataTag;
import net.iaround.conf.Common;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.WebViewAvtivity;


/**
 * @ClassName FriendShareRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 朋友的分享消息
 */

public class FriendShareRecordView extends FriendBaseRecordView implements View.OnClickListener{

	private ImageView mImageView;// 分享图片
	private TextView tvTitle;// 分享title
	private TextView tvDetail;// 分享内容
	private RelativeLayout rlContent;
	private ChatRecord record;
	private Context mContext;

	private int CONTENT_TEXT_SIZE_DP = 14;// 分享内容的字体大小
	private final int JUMP_URL_TAG = R.layout.chat_record_share_other;// 跳转的tag
	private String lastLoadImagePath = "";//上一次加载图片的url

	public FriendShareRecordView(Context context) {
		super(context);
		
		mImageView = (ImageView) findViewById(R.id.img);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
		rlContent = (RelativeLayout) findViewById(R.id.content);
		rlContent.setBackgroundResource(contentBackgroundRes);
	}
	
	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_share_other,this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);
		
		rlContent.setOnClickListener(this);

		// 设置长按事件
		if (!bIsSystemUser(record.getFuid())) {
			rlContent.setOnLongClickListener(mRecordLongClickListener);
		}
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {

		RecordShareBean bean = GsonUtil.getInstance().getServerBean(
				record.getContent(), RecordShareBean.class);

		if(bean == null) return;

		this.record = record;

		String title = bean.title;
		if(TextUtils.isEmpty(title)){
//			mNameView.setText("");
			
			SpannableString spContent = FaceManager.getInstance(getContext())
					.parseIconForString(tvTitle, getContext(), "",
							CONTENT_TEXT_SIZE_DP);
			tvTitle.setText(spContent);
		}else{
			SpannableString spContent = FaceManager.getInstance(getContext())
					.parseIconForString(tvTitle, getContext(), title,
							CONTENT_TEXT_SIZE_DP);
			tvTitle.setText(spContent);
		}
		
		if (TextUtils.isEmpty(bean.content)) {
			tvTitle.setMaxLines(4);
			tvDetail.setMaxLines(1);
		} else {
			tvTitle.setMaxLines(2);
			tvDetail.setMaxLines(3);
			tvDetail.setVisibility(VISIBLE);
		}
		
		SpannableString spContent = FaceManager.getInstance(getContext())
				.parseIconForString(tvDetail, getContext(), bean.content,
						CONTENT_TEXT_SIZE_DP);
		tvDetail.setText(spContent);

		String thumbPicUrl = bean.thumb;
		if(TextUtils.isEmpty(lastLoadImagePath) || !lastLoadImagePath.equals(thumbPicUrl)){
//			ImageViewUtil.getDefault().fadeInRoundLoadImageInConvertView(bean.thumb, mImageView, defShare, defShare, null, 36);
			GlideUtil.loadRoundImage(BaseApplication.appContext,thumbPicUrl,(int)context.getResources().getDimension(R.dimen.x5),mImageView, R.drawable.dynamic_no_data_icon, R.drawable.dynamic_no_data_icon);
			lastLoadImagePath = thumbPicUrl;
		}

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		rlContent.setTag(R.id.im_preview_uri,record);
		checkbox.setChecked(record.isSelect());
		checkbox.setTag(record);
		rlContent.setTag(JUMP_URL_TAG, bean.link);
		setTag(record);
		
		setUserNotename(context, record);
		setUserNameDis(context, record);
		// 设置头像点击事件
		setUserIcon(context, record, mIconView);
	}

	
	@Override
	public void reset() {
		mIconView.getImageView().setImageBitmap(null);
		tvTitle.setText("");
		tvDetail.setText("");
	}

	@Override
	public void onClick(View v) {
		
		String url = (String) v.getTag(JUMP_URL_TAG);
		if(record.getFuid() == 998)
		{//圈子超级管理员
//			DataStatistics.get( mContext )
//				.addButtonEvent( DataTag.BTN_message_chat_group_admin_msg );//jiqiang
		}

		// 跳转到页面
		if (url.startsWith(PathUtil.getHTTPPrefix()) && url.contains("gamecenter")) {
//			GameWebViewActivity.launchGameCenter(getContext(), url);//jiqiang
		} else if (url.startsWith(PathUtil.getHTTPPrefix())) {
			Intent intent = new Intent();
			intent.setClass(getContext(), WebViewAvtivity.class);
			intent.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
			intent.putExtra(WebViewAvtivity.WEBVIEW_GET_PAGE_TITLE,
					true);
			getContext().startActivity(intent);
		} else {
			InnerJump.Jump(getContext(), url);
		}
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		rlContent.setEnabled(isEnable);
	}
}
