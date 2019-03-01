
package net.iaround.ui.group.bean;


import java.util.ArrayList;


/**
 * @ClassName: Landmark
 * @Description: 附近圈子的实体
 * @author zhonglong kylin17@foxmail.com
 * @date 2014-1-4 下午4:52:35
 * 
 */
public class Landmark
{
	
	public String name;
	
	public int lat;
	
	public int lng;
	
	public double rang;
	
	/** 距离 ( 单位m ) */
	public double distance;
	
	public ArrayList< Group > groups;
	
}
