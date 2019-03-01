package net.iaround.ui.dynamic.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-10-8 下午4:48:20
 * @Description: 用于把动态记录数据保存在文件的实体
 */
public class DynamicListFileBean implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -874050687857693659L;
	public ArrayList<DynamicItemBean> dynamicList;//动态记录
	
	public DynamicListFileBean() {
		// TODO Auto-generated constructor stub
		dynamicList = new ArrayList<DynamicItemBean>();
	}
}
