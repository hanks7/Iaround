
package net.iaround.ui.group.bean;


import java.util.ArrayList;


/**
 * @ClassName: GoogleBuildingBean
 * @Description: Google地标接口返回的bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-30 下午12:01:01
 * 
 */
public class GoogleBuildingBean
{
	
	/** 地标列表信息 */
	public ArrayList< GoogleBuildingInfo > results;
	
	/** 为“OK”则表示获取成功 */
	public String status;
	
	/** 下一页的token */
	public String next_page_token;
	
	
}
