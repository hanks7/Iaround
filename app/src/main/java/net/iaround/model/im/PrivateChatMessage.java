package net.iaround.model.im;

/**
 * @ClassName PrivateChatMessage
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-5-10 下午8:43:35
 * @Description: 接收私聊信息
 * @相关协议：81011
 */

public class PrivateChatMessage
{
	/**
	 *  from 字段说明：从附近用户找到你
	 *	1：通过ID搜索找到你
	 *	2：通过昵称搜索找到你
	 *	3：通过附近的人找到你 
	 *	4：通过地图漫游找到你
	 *	5：通过全球焦点找到你
	 *	6：通过附近焦点找到你
	 */
	public int from; 
	public int mtype; //类型（int）（0-正常私聊消息，1-搭讪）
	/**
	 * 消息类型
	 * 	1：文本
	 *	2：图片
	 *	3：声音
	 *	4：视频
	 *	5：位置
	 *	6：礼物
	 *	7.主题礼物
	 *	8.游戏
	 *	9.贴图
	 *	10.小秘书图文消息
	 *	11.搭讪问题
	 *	12.搭讪回答
	 *  13
	 *  14.游戏中心
	 *  15.贴吧广场
	 *  16.聊吧委托找主持人
	 */
	public int type;
	public long msgid;	//消息ID
	public long datetime;	//发送时间
	public int relation;	//关系 @User
	public String data;	//语音内容（Base64编码）
	public String attachment; //附件URL
	public int distance;	//距离（单位：m） 
	public long flag;//消息在客户端的时间戳
	public UserTypeOne user; //发送者详情
	public String groupid;//聊吧id

	public Object content;//不同type，content的内容不同，具体请参照协议文档 @ContentBase

}
