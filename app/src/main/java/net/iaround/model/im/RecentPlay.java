package net.iaround.model.im;
/**
 * @ClassName RecentPlay.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-10 上午11:41:31
 * @Description: 用于/game/user/recentplay协议的返回对象
 */

public class RecentPlay extends BaseServerBean
{
	public int flag;//标识为1：解析以下内容，标识为2：不解析以下内容
	public String content;
	public String icon;
	public String appid;
	public long gameid;
	public int plat;//1:android,2:IOS
	
}
