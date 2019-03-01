package net.iaround.ui.activity.im.accost;

import net.iaround.R;
import net.iaround.model.entity.RecordShareBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.InnerJump;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatMediaLongClickListener;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.comon.PicIndex;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.utils.ImageViewUtil;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AccostShareRecordView extends AccostRecordView {

	private TextView tvTime;
	private ImageView mImageView;// 分享图片
	private TextView mNameView;// 分享title
	private TextView tvDetail;// 分享内容
	private Context mContext;
	private int CONTENT_TEXT_SIZE_DP = 12;// 分享内容的字体大小

	public AccostShareRecordView(Context context) {
		super(context);
		LayoutInflater.from(context)
				.inflate(R.layout.accost_record_share, this);
		mContext = context;

		mImageView = (ImageView) findViewById(R.id.img);
		mNameView = (TextView) findViewById(R.id.tvName);
		tvDetail = (TextView) findViewById(R.id.tvDetail);
	}

	@Override
	public void build(final ChatRecord record) {
		RecordShareBean bean = GsonUtil.getInstance().getServerBean(
				record.getContent(), RecordShareBean.class);

		mNameView.setText(bean.title);

		tvTime = (TextView) findViewById(R.id.tvTime);
		String timeStr = TimeFormat.timeFormat4(mContext, record.getDatetime());
		tvTime.setText(timeStr);

		SpannableString spContent = FaceManager.getInstance(getContext())
				.parseIconForString(tvDetail, getContext(), bean.content,
						CONTENT_TEXT_SIZE_DP);
		tvDetail.setText(spContent);

		ImageViewUtil.getDefault().loadImage(
				CommonFunction.thumPicture(bean.thumb), mImageView,
				PicIndex.DEFAULT_SMALL, PicIndex.DEFAULT_SMALL);

		setTag(record);

		final String url = bean.link;
		this.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 跳转到页面
				InnerJump.Jump(mContext, url);
			}
		});

		// 设置长按事件
		if ((mContext instanceof ChatPersonal)) {
			setOnLongClickListener(new ChatMediaLongClickListener(mContext,
					this));
		} else if (mContext instanceof GroupChatTopicActivity) {
			setOnLongClickListener(new ChatMediaLongClickListener(mContext,
					this));
		}

	}
}
