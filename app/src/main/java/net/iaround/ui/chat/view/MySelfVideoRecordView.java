package net.iaround.ui.chat.view;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.comon.VideoPlayer;


/**
 * @ClassName MySelfVideoRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 我的录像消息
 */
public class MySelfVideoRecordView extends MySelfBaseRecordView implements
        OnClickListener {

	private ImageView mImageView, mPlayView;
	private TextView tvProgress;
	private String lastLoadImagePath = "";// 上一次加载图片的url

	public MySelfVideoRecordView(Context context) {
		super(context);

		mImageView = (ImageView) findViewById(R.id.content);
		mPlayView = (ImageView) findViewById(R.id.play);
		tvProgress = (TextView) findViewById(R.id.tvProgress);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_video_mine,
				this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);

		mImageView.setOnClickListener(this);
		mImageView.setOnLongClickListener(mRecordLongClickListener);
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		String att = record.getAttachment();
		if (!TextUtils.isEmpty(att)) {
			if (att.contains(PathUtil.get3GPPostfix())) {
				String imageUrl = att.substring(0, (att.length() - 4))
						+ PathUtil.getJPGPostfix();
				if (TextUtils.isEmpty(lastLoadImagePath)
						|| !lastLoadImagePath.equals(imageUrl)) {
//					ImageViewUtil.getDefault()
//							.fadeInRoundLoadImageInConvertView(imageUrl,
//									mImageView, defVideo, defVideo, null, 36);//jiqiang
//					GlideUtil.loadImage(getContext(),imageUrl,mImageView, defVideo, defVideo);
					GlideUtil.loadRoundImage(BaseApplication.appContext,imageUrl, (int) context.getResources().getDimension(R.dimen.x5),mImageView,defVideo,defVideo);
					lastLoadImagePath = imageUrl;
				}
			}
		}

		if (record.isUpload()) {
			tvProgress.setVisibility(View.VISIBLE);
			String strPercent = record.getUploadLen() + "%";
			tvProgress.setText(strPercent);
		} else {
			tvProgress.setVisibility(View.INVISIBLE);
		}

		checkbox.setChecked(record.isSelect());

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		mImageView.setTag(R.id.im_preview_uri,record);
		checkbox.setTag(R.id.im_video_recode_tag,record);
		setTag(record);
		// 设置头像点击事件
		setUserIcon(context, record, mIconView);
		// 设置消息状态
		updateStatus(context, record);
	}

	@Override
	public void reset() {
		mStatusView.setText("");
	}

	@Override
	public void onClick(View v) {
		ChatRecord record = (ChatRecord) v.getTag(R.id.im_preview_uri);
		Intent intent = new Intent(getContext(), VideoPlayer.class);
		intent.putExtra("record_datatime", record.getDatetime());
		intent.putExtra("media_url", record.getAttachment());
		getContext().startActivity(intent);
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		mImageView.setEnabled(isEnable);
	}
}
