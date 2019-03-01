
package net.iaround.ui.comon;

/**
 * @ClassName: IPictureViewHandler
 * @Description: 聊天界面处理图片弹出层关闭的接口
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-10-28 下午4:28:42
 * 
 */
public interface IPictureViewHandler
{
	
	/**
	 * @Title: close
	 * @Description: 关闭图片
	 */
    void close();
	
	/**
	 * @Title: isPictureShow
	 * @Description: 图片是否打开
	 * @return
	 */
    boolean isPictureShow();
}
