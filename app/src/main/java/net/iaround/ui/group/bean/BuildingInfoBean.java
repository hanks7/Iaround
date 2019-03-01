
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * @ClassName: BuildingInfoBean
 * @Description: 服务端下发的地标信息类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-31 上午10:53:34
 * 
 */
public class BuildingInfoBean extends BaseServerBean
{
	
	/** 页码 */
	public int pageno;
	
	/** 每页个数 */
	public int pagesize;
	
	/** 总数 */
	public int amount;
	
	/** 1：使用服务器下发；2：使用url地标 */
	public int flag;
	
	/** flag为2时的url地址 */
	public String url;
	
	/** 地标信息列表 */
	public ArrayList< BuildingInfo > landmarks;
	
}
