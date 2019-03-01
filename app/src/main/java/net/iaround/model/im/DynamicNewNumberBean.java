package net.iaround.model.im;

import net.iaround.ui.datamodel.BaseUserInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-19 上午11:26:13
 * @Description: 相关协议：81064
 */
public class DynamicNewNumberBean {

	private int num;//未读评论数量
	private int likenum;//未读点赞数量
	public DynamicMsg msg;//最新一条动态消息，用于通知栏推送显示@by tanzy

    public long time;// 接收时间
	
	public int getLikenum() {
		return likenum;
	}

	public void setLikenum(int likenum) {
		this.likenum = likenum;
	}

	public int getCommentNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}
	
	public class DynamicMsg
	{
		public long dynamicid;
		public BaseUserInfo user;
		public int type;
		public long datetime;
		public String content;
	}
}
