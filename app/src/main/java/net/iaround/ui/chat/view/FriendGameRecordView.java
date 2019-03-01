package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.entity.RecordGameBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.glide.GlideUtil;

/**
 * @ClassName FriendGameRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-15 上午10:24:08
 * @Description: 朋友的游戏消息
 */

public class FriendGameRecordView extends FriendBaseRecordView implements
		View.OnClickListener {

	private ImageView mImageView;
	private TextView mContentView;
	private LinearLayout llContent;

	private final int GAME_ID_TAG = R.layout.chat_record_game_other;// 用来获取gameId的tag

	public FriendGameRecordView(Context context) {
		super(context);

		mImageView = (ImageView) findViewById(R.id.content_image);
		mContentView = (TextView) findViewById(R.id.content_text);
		llContent = (LinearLayout) findViewById(R.id.content);
		llContent.setBackgroundResource(contentBackgroundRes);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_game_other,
				this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);
		llContent.setOnClickListener(this);

		if (!bIsSystemUser(record.getFuid())) {
			llContent.setOnLongClickListener(mRecordLongClickListener);
		}
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {

		RecordGameBean bean = GsonUtil.getInstance().getServerBean(
				record.getContent(), RecordGameBean.class);

		String thumPicPath = CommonFunction.thumPicture(record.getAttachment());
//		ImageViewUtil.getDefault().loadImage(thumPicPath, mImageView, defGame,
//				defGame);
		GlideUtil.loadImage(BaseApplication.appContext,thumPicPath,mImageView, defGame,
				defGame);

		mContentView.setText(bean.content);

		checkbox.setChecked(record.isSelect());

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		checkbox.setTag(record);
		llContent.setTag(R.id.im_preview_uri,record);
		llContent.setTag(GAME_ID_TAG, bean.gameid);
		setTag(record);
		setUserNameDis(context, record);
		setUserNotename(context, record);
		// 设置头像点击事件
		setUserIcon(context, record, mIconView);
	}

	@Override
	public void reset() {
		mIconView.getImageView().setImageBitmap(null);
		mImageView.setImageBitmap(null);
	}

	@Override
	public void onClick(View v) {
//		int gameId = (Integer) v.getTag(GAME_ID_TAG);
//		GameDetailActivity.lauch(getContext(), gameId, -1);
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		llContent.setEnabled(isEnable);
	}

}
