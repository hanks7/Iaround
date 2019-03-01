package net.iaround.ui.space.bean;

public class InvisibilitySettingOption
{
	/** y对其隐身，n不隐身 */
	public String invisible;
	
	/** y看到我的动态，n看不到我的动态 */
	public String showmydynamic;
	
	/** y看他（她）的动态，n不看他（她）的动态 */
	public String showdynamic;
	
	/** y已列入黑名单，n未列入黑名单 */
	public String blacklisted;
	
	/** 0:自己 ，1：好友 ，2：陌生人 3、关注 4、粉丝 5推荐 */
	public int relation;
	
	/** 黑名单Id，当blacklisted=y时返回，用于解除黑名单使用 */
	public long devilId;
	
	/** 普通用户上限 */
	public int invisiblenum;
	
	/** vip用户上限 */
	public int vipinvisiblenum;
}
