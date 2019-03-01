package net.iaround.model.im;
/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年3月30日 下午3:33:06
 * @Description: 聊天消息发送的实体
 */
public class ChatMessageBean {

	public static final int PRIVATE_CHAT = 1;
	public static final int GROUP_CHAT = 2;
	public static final int CHATBAR_CHAT = 3;
	
	public int chatType;//聊天的类型(1:私聊,2:圈聊,3:聊吧)
	public ChatRecord chatRecord;//聊天消息的实体
	public int resourceType;//@FileUploadType,如果发送的不是资源类型的消息,resourceType = 0
	public ChatTargetInfo targetInfo;//根据聊天类型对应不同的信息
}
