package net.iaround.ui.group.bean;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-16 下午4:14:05
 * @Description: 圈子话题发布管理类。所有的话题发布都将通过这个类，用于放在后台发布
 */
public class GroupTopicPublishBean extends PublishBaseBean {

	public long topic_index;//发送数据缓存的时候为时间戳
	public long groupid;//圈子id
	public int type;//附件类型 1：文本 2：图片 3：声音 4：视频 5.位置6.礼物提醒
	public int plat;//手机平台 @Config.PLAT
}
