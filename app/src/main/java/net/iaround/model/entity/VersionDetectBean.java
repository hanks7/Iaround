package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * @ClassName VersionDetectBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-6-7 下午10:39:09
 * @Description: 版本检测(新接口)
 * @相关协议: /system/version_5_1
 */

public class VersionDetectBean extends BaseServerBean
{
	public long flag;//是否需要升级，0:不需要，1:需要但不是必要，2:必需升级 
	public String newversion;//新版本号
	public String url; //新版本文件下载地址
	public String contenturl;//内容简介
	
	public boolean isNeedUpdate()
	{
		return flag == 0;
	}
	
	public boolean isNormalUpdate()
	{
		return flag == 1;
	}
	
	public boolean isFocusUpdate()
	{
		return flag == 2;
	}
}
