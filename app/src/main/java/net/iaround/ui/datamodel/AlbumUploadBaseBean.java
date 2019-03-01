package net.iaround.ui.datamodel;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2015年1月8日 下午4:31:19
 * @Description: 相册照片上传实体类
 */
public class AlbumUploadBaseBean {

	private String content;//内容
	private ArrayList<String> photos;//照片url
	private String address;//地址
	public long datetime;//时间戳
	private String sync;//同步目标类型（1-贴吧，2-圈子，3-动态）, 5.7版本只能同步到个人动态
	private ArrayList<Integer> thirdPartShareIdList;//用来存放第三方分享的对应方式的Id
	
	
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
}
