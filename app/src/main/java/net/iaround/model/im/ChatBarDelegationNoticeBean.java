package net.iaround.model.im;

/**
 * Created by Zhengst   on 2015/10/16.
 */
public class ChatBarDelegationNoticeBean extends BaseServerBean
{
	public long chatbarid;
	public String title;//标题
	public String chatbarname;
	public String chatbarinfo;//描述
	public String pic;//图片url
	public int type;//1:辅导员，2：主持人


	public int clickSuccess =0;//本地用字段，是否以成功点击，0：否，1：是
}
