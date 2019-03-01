
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;


/**
 * @ClassName: MyGroupBean
 * @Description: 我的圈子的实体bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-16 上午11:29:03
 * 
 */
public class MyGroupBean extends BaseServerBean
{
	
	/** 数量 */
	public int amount;
	
	/** 与我有关的圈子列表（包含我创建的和我加入的） */
	public ArrayList< Group > groups;
	
}
