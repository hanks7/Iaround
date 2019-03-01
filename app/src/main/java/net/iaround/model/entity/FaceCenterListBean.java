
package net.iaround.model.entity;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


/**
 * @author zhengshaota
 * @version 创建时间：2014-12-5
 * @Description: 表情中心做缓存的实体
 */
public class FaceCenterListBean extends BaseServerBean
{
	/** 表情中心广告 */
	public ArrayList< FaceAd > ads;
	/** 表情中心表情列表 */
	public ArrayList< Face > faces;
	
	
}
