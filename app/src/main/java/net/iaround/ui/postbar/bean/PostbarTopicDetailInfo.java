package net.iaround.ui.postbar.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class PostbarTopicDetailInfo implements Serializable
{


	/**
	 *
	 */
	private static final long serialVersionUID = -7459276522867839505L;

	/** 贴吧id */
	public long postbarid;

	/** 贴吧名称 */
	public String postbarname;

	/** 话题id */
	public long topicid;

	/** 用户id */
	public long userid;

	/** 话题内容 */
	public String content;

	/** 话题类型 1-图文 */
	public int type;

	/** 图片 */
	public ArrayList< String > photos;

	/** 时间 */
	public long datetime;

	/** 距离 单位（m） */
	public long distance;

	/** 地址 */
	public String address;

	/** 置顶（0-否，1-是） */
	public int istop;

	/** 精华（0-否，1-是） */
	public int isessence;

	/** 新（0-否，1-是） */
	public int isnew;

	/** 热门（0-否，1-是） */
	public int ishot;

	/** （1-同步到，2-同步来自） */
	public int synctype;

	/** （1-贴吧，2-圈子，3-动态）多个用，号隔开 */
	public String sync;

	public String syncvalue;

	/** 英|简|繁,[标签名称，多语言返回，如果为空，不展示]  */
	public String label;

	/** 标签跳转链接，跳转链接可能会出现内部跳转和外部跳转的情况，请解析  */
	public String labelurl;
}
