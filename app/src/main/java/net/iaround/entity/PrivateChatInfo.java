package net.iaround.entity;

import net.iaround.model.im.ChatTargetInfo;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月30日 下午3:45:17
 * @Description: 私聊消息发送需要的信息
 */
public class PrivateChatInfo extends ChatTargetInfo {

	public int from;//从哪里进入聊天 @ChatFromType
	public int mtype;//聊天类型（0-正常私聊消息，1-搭讪）
	
	public long targetUserId;//聊天对象的用户id
}
