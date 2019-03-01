
package net.iaround.ui.group.bean;

/**
 * @ClassName: BasePinnedItem
 * @Description: 置顶ListView的实体类
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-4 下午4:37:43
 * 
 * @param <T>
 */
public class BasePinnedItem< T >
{
	
	/**
	 * 置顶类型（头）
	 */
	public static final int GROUP = 0;
	
	/**
	 * 置顶类型（内容）
	 */
	public static final int CONTENT = 1;
	
	/**
	 * 类型（对应置顶类型）
	 */
	public final int groupType;
	
	public final String title;
	
	/**
	 * 数据实体
	 */
	public T data;
	
	/**
	 * @param type
	 *            类型
	 * @param data
	 *            数据类型
	 */
	public BasePinnedItem(int groupType , String title , T data )
	{
		this.groupType = groupType;
		this.title = title;
		this.data = data;
	}
	
}
