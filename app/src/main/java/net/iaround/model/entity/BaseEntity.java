
package net.iaround.model.entity;


/**
 * @ClassName: BaseServerBean
 * @Description: 从服务端返回的基础数据bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-5 下午7:59:42
 * 
 */
public class BaseEntity
{
	/**
	 * 请求失败时的错误码
	 */
	public int status = 1;
	
	/** 错误描述 */
	public String msg;

	public boolean isSuccess( ) {
		return status == 200;
	}
}
