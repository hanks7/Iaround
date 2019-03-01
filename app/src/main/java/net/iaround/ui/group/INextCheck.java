
package net.iaround.ui.group;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.group.bean.GroupNextStep;


/**
 * @ClassName: INextCheck
 * @Description: 创建圈子下一步的检查接口
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-7 下午6:17:36
 * 
 */
public interface INextCheck
{
	
	/**
	 * @Title: initData
	 * @Description: 加载数据
	 * @param mServerBean
	 * @param isBack
	 *            根据是否返回，判断是否需要清除数据
	 */
    void initData(BaseServerBean mServerBean, boolean isBack);
	
	GroupNextStep getGroupNextStep();
	
}
