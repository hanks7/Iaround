package net.iaround.ui.chat.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.im.AudioPlayUtil;
import net.iaround.tools.im.AudioPlayUtil.AudioPlayStateCallback;

/**
 * @ClassName MySelfSoundRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 我的语音消息
 */
public class MySelfSoundRecordView extends MySelfBaseRecordView implements
        OnClickListener,AudioPlayStateCallback, DownloadFileCallback {
	private RelativeLayout rlContent;
	private TextView tvContent;
	private ImageView ivPlayIcon;
	private AnimationDrawable playAnim;

	public MySelfSoundRecordView(Context context) {
		super(context);

		rlContent = (RelativeLayout) findViewById(R.id.content);
		tvContent = (TextView) findViewById(R.id.tvContent);
		ivPlayIcon = (ImageView) findViewById(R.id.ivPlayIcon);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.chat_record_sound_mine, this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);

		rlContent.setOnClickListener(this);
		rlContent.setOnLongClickListener(mRecordLongClickListener);
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		if (record.getContent() == null || "".equals(record.getContent()) || "null".equals(record.getContent()))
		{
			tvContent.setText("1”");
		}else {
			tvContent.setText(record.getContent() + "”");
		}
		playAnim = (AnimationDrawable) ivPlayIcon.getDrawable();
		playAnim.stop();

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		rlContent.setTag(R.id.im_preview_uri,record);
		setTag(record);
		checkbox.setChecked(record.isSelect());
		checkbox.setTag(record);
		// 设置头像点击事件
		setUserIcon(context, record, mIconView);
		// 设置消息状态
		if (!record.isLoading()) {
			updateStatus(context, record);
		}
	}

	@Override
	public void reset() {
		mStatusView.setText("");
	}

	@Override
	public void OnPlayStarted() {
		if (playAnim != null) {
			playAnim.start();
		} else {
			ivPlayIcon.setImageResource(R.drawable.audio_play_right);
			playAnim = (AnimationDrawable) ivPlayIcon.getDrawable();
			playAnim.start();
		}
	}

	@Override
	public void OnPlayingProgress() {

	}

	@Override
	@SuppressWarnings("ResourceType")
	public void onPlayCompleted() {
		if (playAnim != null) {
			playAnim.stop();
			ivPlayIcon.setImageResource(R.drawable.audio_play_left_00);
			ivPlayIcon.setImageResource(R.anim.audio_play_left);
			playAnim = (AnimationDrawable) ivPlayIcon.getDrawable();
			playAnim.stop();
		}
	}

	@Override
	public void OnPlayError() {
		CommonFunction.toastMsg(getContext(), R.string.audio_play_fail_notice);
	}

	@Override
	public void onClick(View v) {
		ChatRecord record = (ChatRecord) v.getTag(R.id.im_preview_uri);

		String url = record.getAttachment();
		AudioPlayUtil.getInstance().playAudio(BaseApplication.appContext, url, this, this);
	}

	@Override
	public void onDownloadFileProgress(long lenghtOfFile,
			long LengthOfDownloaded, int flag) {
		this.post(new Runnable() {

			@Override
			public void run() {
				if (mProgress.getVisibility() != View.VISIBLE) {
					mProgress.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	@Override
	public void onDownloadFileFinish(int flag, String fileName, String savePath) {
		this.post(new Runnable() {

			@Override
			public void run() {
				mProgress.setVisibility(View.GONE);
			}
		});

	}

	@Override
	public void onDownloadFileError(int flag, String fileName, String savePath) {
		this.post(new Runnable() {

			@Override
			public void run() {
				mProgress.setVisibility(View.GONE);
				CommonFunction.toastMsg(getContext(),
						R.string.audio_play_fail_notice);
			}
		});

	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		rlContent.setEnabled(isEnable);
	}

}
