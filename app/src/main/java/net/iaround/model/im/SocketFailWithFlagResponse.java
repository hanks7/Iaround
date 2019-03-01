package net.iaround.model.im;
/**
 * @ClassName {file_name}
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-10 下午7:33:48
 * @Description: Socket失败响应，带flag
 * @相关协议：83010，
 */

public class SocketFailWithFlagResponse extends SocketFailResponse
{
	public long flag;//标识
	public String prompt;//错误提示

	public String getMessage(){
		return prompt;
	}

}
