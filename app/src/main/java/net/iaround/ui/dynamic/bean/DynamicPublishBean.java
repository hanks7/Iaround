package net.iaround.ui.dynamic.bean;

import net.iaround.ui.group.bean.PublishBaseBean;

import java.io.Serializable;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-11 下午3:51:35
 * @Description: 动态发布实体类
 */
public class DynamicPublishBean extends PublishBaseBean implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4727346998722996133L;
	public long dynamicid;
	public int type;//类型， 1.图文 2.分享 @DynamicType
	private String title;//标题
	private String url;//分享类型的跳转url
	private String sharesource;//1-贴吧话题，2-圈子话题
	private String sharevalue;//当shareSource为1时，值为贴吧id，当shareSource为2时，值为圈子id
	
	/** 设置Title */
	public void setTitle(String value)
	{
		title = value;
	}
	
	/** 获取Title */
	public String getTitle()
	{
		return title == null ? "" : title;
	}

	
	/** 设置url */
	public void setUrl(String value)
	{
		url = value;
	}
	
	/** 获取url */
	public String getUrl()
	{
		return url == null ? "" : url;
	}
	
	/** 设置分享的源 */
	public void setSharesource(String value)
	{
		sharesource = value;
	}
	
	/** 获取分享的源 */
	public String getSharesource()
	{
		return sharesource == null ? "" : sharesource;
	}
	
	/** 设置分享的值 */
	public void setSharevalue(String value)
	{
		sharevalue = value;
	}
	
	/** 获取分享的值 */
	public String getSharevalue()
	{
		return sharevalue == null ? "" : sharevalue;
	}

}
