package net.iaround.pay.vip;


import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

/**
 * vip特权
 *
 * @author Administrator
 */
public class VipPrivilegeBean extends BaseServerBean
{
	
	public int svip;// svip标识(0-非svip 1（大于0）-svip)
	public int viplevel;// vip标识(0-非vip 1-vip)
	public long expire;// vip到期时间
	public ArrayList< Privileges > privileges;// vip特权列表

	
	public class Privileges
	{
		public String name; // 名称
		public String icon; // 图标
		public String resume;// 简述
		public String url; // 详情页URL
	}
	
	
}
