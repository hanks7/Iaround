package net.iaround.entity;
/**
 * @ClassName UploadFileResponse.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-6-5 下午6:07:14
 * @Description: 用于文件上传得到服务端的回应
 */

public class UploadFileResponse
{
	public int status;//状态
	public String attid;//文件名
	public String url;//文件的url
	
	public boolean isSuccess()
	{
		return status == 200;
	}
}
