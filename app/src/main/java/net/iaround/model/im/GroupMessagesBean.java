package net.iaround.model.im;
/**
 * @ClassName GroupMessagesBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-11 下午12:01:11
 * @Description: 保存从数据库中取出来的，{时间、圈id、未读数量}
 */

public class GroupMessagesBean
{
	public int groupId;//圈id
	public long lastTime;//最后一条消息的时间
	public int noreadNum;//未读数量
}
