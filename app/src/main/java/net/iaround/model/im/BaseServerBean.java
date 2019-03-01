
package net.iaround.model.im;


/**
 * @ClassName: BaseServerBean
 * @Description: 从服务端返回的基础数据bean
 * @author zhonglong kylin17@foxmail.com
 * @date 2013-12-5 下午7:59:42
 * 
 */
public class BaseServerBean
{
	
	/**
	 * 状态码
	 */
	public int status;
	
	/**
	 * 请求失败时的错误码
	 */
	public int error = 1;
	
	/** 错误描述 */
	public String errordesc;
	/**
	 * @Title: isSuccess
	 * @Description: 数据请求是否成功
	 * @return 参数返回值等信息描述
	 */
	public String vrc; //

	public boolean isSuccess( )
	{
		return status == 200;
	}
}
