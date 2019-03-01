
package net.iaround.ui.group.bean;

/**
 * @ClassName: GroupNextStep
 * @Description: 创建圈子下一步返回的数据
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-7 下午6:35:03
 * 
 */
public class GroupNextStep
{
	
	/** 为空则允许进入下一步 */
	public String nextMsg;
	
	/** 下一步的参数 */
	public String[ ] nextParams;
	
	/**
	 * @Title: isCanNext
	 * @Description: 是否能进入下一步
	 * @return boolean
	 */
	public boolean isCanNext( )
	{
        return nextMsg.equals("");
		
	}
	
}
