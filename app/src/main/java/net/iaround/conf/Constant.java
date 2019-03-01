package net.iaround.conf;

/** 公用静态变量和静态方法 */
public class Constant
{
	/** http协议成功返回的状态 */
	public final static String STATUS_200 = "\"status\":200";
	
	public final static String KEY_SHARE_MARK = "share";
	
	public static boolean isSuccess( String result )
	{
        return result.contains(STATUS_200);
    }
}
