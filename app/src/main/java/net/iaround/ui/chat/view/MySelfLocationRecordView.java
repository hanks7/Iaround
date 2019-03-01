package net.iaround.ui.chat.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.map.MapUtils;


/**
 * @ClassName MySelfLocationRecordView.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-24 下午8:42:38
 * @Description: 我的地理位置消息
 */

public class MySelfLocationRecordView extends MySelfBaseRecordView implements
		View.OnClickListener {
	private ImageView mImageView;
	private TextView mContentView;
	private RelativeLayout rlContent;

	public MySelfLocationRecordView(Context context) {
		super(context);

		mImageView = (ImageView) findViewById(R.id.content_image);
		mContentView = (TextView) findViewById(R.id.content_text);
		rlContent = (RelativeLayout) findViewById(R.id.content);
	}

	@Override
	protected void inflatView(Context context) {
		LayoutInflater.from(context).inflate(
				R.layout.chat_record_location_mine, this);
	}

	@Override
	public void initRecord(Context context, ChatRecord record) {
		super.initRecord(context, record);

		rlContent.setOnClickListener(this);
		// 设置长按事件
		rlContent.setOnLongClickListener(mRecordLongClickListener);
	}

	@Override
	public void showRecord(Context context, ChatRecord record) {
		String locationUrl = "";
		if (record.getAttachment() != null
				&& !record.getAttachment().equals("")) {
			String[] latLng = record.getAttachment().split(",");
			double lat = 0;
			double lng = 0;
			try {
				lat = Double.parseDouble(latLng[0]) / 1000000d;
				lng = Double.parseDouble(latLng[1]) / 1000000d;
			} catch (Exception e) {
			}
			if (MapUtils.isLoadNativeMap( context )|| GooglePlayServicesUtil.isGooglePlayServicesAvailable( context ) != ConnectionResult.SUCCESS )
			{
				locationUrl = getMapUrl(lat, lng);
			}
			else {
				locationUrl = getLocationPreview(lat, lng);
			}
		}

//		ImageViewUtil.getDefault().fadeInRoundLoadImageInConvertView(locationUrl, mImageView,
//				defLocation, defLocation, null, 15);//jiqiang
		GlideUtil.loadImage(BaseApplication.appContext,locationUrl,mImageView,defLocation, defLocation);
		mContentView.setText(record.getContent());

		// 替换成未通过的头像
		if(record.getId() == Common.getInstance().loginUser.getUid()){
			String verifyicon = Common.getInstance().loginUser.getVerifyicon();
			if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
				record.setIcon(verifyicon);
			}
		}

		// 设置单击事件
		rlContent.setTag(R.id.im_preview_uri,record);
		setTag(record);

		checkbox.setChecked(record.isSelect());
		checkbox.setTag(record);
		// 设置头像
		setUserIcon(context, record, mIconView);
		// 设置消息状态
		updateStatus(context, record);
	}

	@Override
	public void reset() {
		mContentView.setText("");
		mStatusView.setText("");
	}

	@Override
	public void onClick(View v) {

		ChatRecord record = (ChatRecord) v.getTag(R.id.im_preview_uri);
		String attachment = record.getAttachment();
		// 获取经纬度
		String[] latLng = attachment.split(",");
		int lat = 0;
		int lng = 0;
		try {
			lat = Integer.parseInt(latLng[0]);
			lng = Integer.parseInt(latLng[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		MapUtils.showOnePositionMap(getContext(), MapUtils.LOAD_TYPE_POS_MAP,
				lat, lng, record.getContent(), "");
	}

	@Override
	public void setContentClickEnabled(boolean isEnable) {
		rlContent.setEnabled(isEnable);
	}

}
