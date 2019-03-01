package net.iaround.entity;

import net.iaround.model.im.BaseServerBean;

import java.util.ArrayList;

public class CreditsSourcesBackBean extends BaseServerBean {

	public int pageno;
	public int pagesize;
	public int amount;
	public double credits;
	public ArrayList<creditData> creditslist;

	public class creditData {
		public String gifticon;// 礼物图标
		public String giftname;// 礼物名称
		public int senderid;// 赠送者id
		public String sendername;// 赠送者昵称
		public long sendtime;// 赠送时间
		public double credits;// 积分
	}
}
