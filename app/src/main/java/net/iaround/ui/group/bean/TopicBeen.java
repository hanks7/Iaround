
package net.iaround.ui.group.bean;


import java.io.Serializable;
import java.util.ArrayList;


/**
 * 话题
 * 
 * @author
 * 
 */
public class TopicBeen implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8478655291676103405L;
	/**
	 * 
	 */
	public long topicid; // 话题id
	public long userid; // 用户id
	public String imageUrl; // 话题照片
	public int type; // 话题类型：1：文本，2：图片，
	public String content; // 内容
	
	public ArrayList< String > photos;
	
	public long datetime; // 时间
	
	public int distance;  //距离 单位（m）
	
	public String address; // 地址
	public int istop; // 置顶（0-否，1-是）
	public int ishot; //热门（0-否，1-是）
	public int isessence; // 精华（0-否，1-是）
	
	public int isnew; // 新（0-否，1-是）
	
	public int synctype; // （1-同步到，2-同步来自）
	public String sync;// 类型(String)（1-贴吧，2-圈子，3-动态）
	public String syncvalue;
	
	public String getAddress()
	{
		return address;
	}
	
	
}
