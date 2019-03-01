package net.iaround.model.im;

import net.iaround.model.entity.Item;

/**
 * @ClassName {file_name}
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-12 下午7:26:23
 * @Description: 发送群组消息成功
 * @相关协议:72008
 */

public class GroupMessageSendSuccess
{
	public long flag;
	public long msgid;
	public long groupid;
	public long incmsgid;//自增id

	public int cat;
	public int top;
	public int type;
	public Item item;
}
