package net.iaround.ui.activity.im.accost;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import net.iaround.model.im.ChatRecord;
import net.iaround.tools.CommonFunction;


/**
 * @ClassName AccostRecord.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-6 下午10:21:20
 * @Description: 搭讪页面的消息基类
 */

public abstract class AccostRecordView extends RelativeLayout {

	protected Context mContext;

	public AccostRecordView(Context context) {
		super(context);
		mContext = context;
	}

	public void createView(int resID) {
		// 实例化对应的layout
		LayoutInflater.from(mContext).inflate(resID, this);
	}

	public abstract void build(ChatRecord record);

	/**
	 * 获取地理位置的预览图的URL
	 * 
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @return 地理位置的预览图的URL
	 */
	protected String getLocationPreview(double lat, double lng) {
		String locationUrl = null;

		String googleUrl = "http://maps.google.com/maps/api/staticmap?center=";
		locationUrl = String
				.format(googleUrl
								+ "%s"
								+ ","
								+ "%s"
								+ "&zoom=15&size=400*200&maptype=roadmap&markers=color:red|label:A|"
								+ "%s" + "," + "%s" + "&sensor=false", lat, lng, lat,
						lng);
		CommonFunction.log("googleUrl", googleUrl);
		return locationUrl;
	}

	/**
	 * 获取高德地理位置的预览图的URL
	 * @param x
	 * @param y
	 * @return
	 */
	protected String getMapUrl(double x, double y) {
		String url = "http://restapi.amap.com/v3/staticmap?location=" + y + "," + x +
				"&zoom=15&size=400*200"+"&key=" + "7edc278c6ace88576a961c8dceafa206";
		CommonFunction.log("getMapUrl", url);
		return url;
	}
}
