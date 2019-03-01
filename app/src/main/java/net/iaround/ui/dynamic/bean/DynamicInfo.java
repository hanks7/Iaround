package net.iaround.ui.dynamic.bean;

import net.iaround.entity.type.SyncType;
import net.iaround.tools.CommonFunction;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-22 下午8:16:53
 * @Description: 动态基本信息
 */
public class DynamicInfo implements Serializable {


	/**
	 *
	 */
	private static final long serialVersionUID = 6092878024576115211L;
	public long dynamicid;// 动态id
	public long userid;// 用户id
	private String title;// 标题
	private String content;// 动态内容
	public int type;// 类型， 1.图文 2.分享 @DynamicType
	private ArrayList<String> photos;// 照片url
	public int distance;// 距离（单位 m）
	private String address;// 地址
	public long datetime;// 时间戳
	private String url;// 分享类型的跳转url
	public int ishot;// 热门(0-否, 1-是)
	public int synctype;// (0-默认情况, 1-同步到, 2-同步来自)@SyncType
	public String sync;// (0-默认情况, 1-同步到, 2-同步来自)@SyncType
	public int sharesource;//分享来源(0-默认情况, 1-分享来源是贴吧, 2-分享来源是圈话题)
	private String shareusername;//分享者的名字
	private int horoscope ;//星座
	private String birthday;//生日


	public int dynamiccategory;//动态分类 1-贴吧 3-动态 @PublishDynamicActivity
	public int dynamicsource;//动态来源 1-附近, 2-好友关注, 3-热门(分热门侃啦,热门动态,通过dynamiccategory字段来区分)
	public long parentid;//父id,当dynamiccategory为1-贴吧的时候,dynamicid表示对应贴吧话题id,parentid是对应贴吧id

	private String label;//英|简|繁,[标签名称，多语言返回，如果为空，不展示]
	private String labelurl;//标签跳转链接，跳转链接可能会出现内部跳转和外部跳转的情况，请解析

	/** 标签名称 */
	public String getLabel() {
		return label;
	}

	/** 设置标签名称 */
	public void setLabel(String label) {
		this.label = label;
	}

	/** 标签跳转链接 */
	public String getLabelurl() {
		return labelurl;
	}

	/** 标签跳转链接 */
	public void setLabelurl(String labelurl) {
		this.labelurl = labelurl;
	}

	/** 获取标题 */
	public String getTitle() {
		return title == null ? "" : title;
	}

	/** 设置标题 */
	public void setTitle(String value) {
		title = value;
	}

	/** 获取动态内容 */
	public String getContent() {
		return content == null ? "" : content;
	}

	/** 设置动态内容 */
	public void setContent(String value) {
		content = value;
	}

	/** 设置照片列表 */
	public void setPhotoList(ArrayList<String> value) {
		if(photos == null)
		{
			photos = new ArrayList<String>();
		}
		photos.clear();
		photos.addAll(value);
	}

	/** 获取照片列表 */
	public ArrayList<String> getPhotoList() {
		return photos == null ? new ArrayList<String>() : photos;
	}

	/** 获取地址 */
	public String getAddress() {
		return address == null ? "" : address;
	}

	/** 设置地址 */
	public void setAddress(String value) {
		address = value;
	}

	/** 获取分享类型的跳转url */
	public String getUrl() {
		return url == null ? "" : url;
	}

	/** 设置分享类型的跳转url */
	public void setUrl(String value) {
		url = value;
	}

	/**
	 * 判断当前动态是否有热门标示
	 *
	 * @return
	 */
	public boolean isHotDynamic() {
		return ishot == 1;
	}

	/**
	 * 是否没有任何同步信息
	 *
	 * @return
	 */
	public boolean isSyncDefault() {
		return synctype == SyncType.DEFAULT;
	}

	/** 获取同步类型 */
	public int getSynctype() {
		return synctype;
	}

	/** 设置同步类型 */
	public void setSynctype(int value) {
		synctype = value;
	}

	/** 获取分享者的名字 */
	public String getshareusername() {
		return shareusername;
	}

	/** 设置分享者的名字 */
	public void setshareusername(String value) {
		shareusername = value;
	}
	/** 获取星座 */
	public int getHoroscope() {
		return horoscope;
	}
	/**设置星座*/
	public void setHoroscope(int horoscope) {
		this.horoscope = horoscope;
	}
	/** 获取生日 */
	public String getBirthday() {
		return birthday;
	}
	/** 设置生日 */
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public int getSync() {
		 if (CommonFunction.isEmptyOrNullStr(sync)){
			return 0;
		}else if (sync.contains(",")){
			 return 1;
		 }else{
			 return Integer.valueOf(sync);
		 }
	}
}
