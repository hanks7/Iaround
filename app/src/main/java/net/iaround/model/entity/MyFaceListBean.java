
package net.iaround.model.entity;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


/**
 * @author zhengshaota
 * @version 
 * @Description: 我的表情页保存数据
 */
public class MyFaceListBean extends BaseServerBean
{
	/** 我拥有的表情 */
	public ArrayList< Face > ownList= new ArrayList< Face >( );
	/** 已删除的表情 */
	public ArrayList< Face > delList = new ArrayList< Face >();
	
	
}
