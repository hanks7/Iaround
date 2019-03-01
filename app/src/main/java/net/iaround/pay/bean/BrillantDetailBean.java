package net.iaround.pay.bean;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

public class BrillantDetailBean extends BaseServerBean {
	public int pageno;
	public int pagesize;
	public int amount;
	public ArrayList< BrillantDetals > details;// 银行详情列表
	public class BrillantDetals
	{
		public int consumetype; // 类型
		public int diamondnum; // 钻石数
		public long datetime; // 时间
	}
}
