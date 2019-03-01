package net.iaround.ui.group.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-11 上午11:12:43
 * @Description: 动态,圈话题,贴吧发布的基类
 */
public class PublishBaseBean implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6899162837500445125L;
	private String content;//内容
	private ArrayList<String> photos;//照片url
	private String address;//地址
	private String shortaddress;//短地址:广东广州
	private String sync;//同步类型,（1-贴吧，2-圈子，3-动态）同步多个用，隔开
	//sync对应的value 当sysnc为1:时，该值为贴吧id,当sysnc为2:时，该值为圈子id,当sysnc为3:时，该值为用户id,多个并行时，排序与sync排序一致
	private String syncvalue;
	private ArrayList<Integer> thirdPartShareIdList;//用来存放第三方分享的对应方式的Id
	public long datetime;//时间戳
	

	
	/** 设置短地址 */
	public void setShortaddress(String value)
	{
		shortaddress = value;
	}
	
	/** 获取短地址 */
	public String getShortaddress()
	{
		return shortaddress == null ? "" : shortaddress;
	}
	
	/** 设置动态内容*/
	public void setContent(String value)
	{
		content = value;
	}
	
	/** 获取动态内容*/
	public String getContent()
	{
		return content == null ? "" : content;
	}
	
	
	/** 设置图片List */
	public void setPhotoList(ArrayList<String> value)
	{
		if(photos == null)
		{
			photos = new ArrayList<String>();
		}
		photos.clear();
		photos.addAll(value);
	}
	
	/** 获取图片List */
	public ArrayList<String> getPhotoList()
	{
		if(photos == null)
		{
			photos = new ArrayList<String>();
		}
		return photos;
	}
	
	
	/** 设置地址 */
	public void setAddress(String value)
	{
		address = value;
	}
	
	/** 获取地址 */
	public String getAddress()
	{
		return address == null ? "" : address;
	}

	
	/** 设置同步类型 */
	public void setSync(String value)
	{
		sync = value;
	}
	
	/** 获取同步类型 */
	public String getSync()
	{
		return sync == null ? "" : sync;
	}
	
	
	/** 设置同步值 */
	public void setSyncvalue(String value)
	{
		syncvalue = value;
	}
	
	/** 获取同步值 */
	public String getSyncvalue()
	{
		return syncvalue == null ? "" : syncvalue;
	}
	
	/** 获取第三方分享的idList */
	public ArrayList<Integer> getShareList()
	{
		if(thirdPartShareIdList == null)
		{
			thirdPartShareIdList = new ArrayList<Integer>();
		}
		return thirdPartShareIdList;
	}
	
	/** 设置第三方分享的idList */
	public void setShareList(ArrayList<Integer> value)
	{
		if(thirdPartShareIdList == null)
		{
			thirdPartShareIdList = new ArrayList<Integer>();
		}
		thirdPartShareIdList.clear();
		thirdPartShareIdList.addAll(value);
	}
	
}
