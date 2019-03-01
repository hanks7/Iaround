package net.iaround.model.im;

import java.util.HashMap;

/**
 * @ClassName AccostRelationes.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-13 下午9:49:25
 * @Description: 保存与别人的搭讪关系，用SharedPreferenceUtil
 */

public class AccostRelationes
{
	//key为用户id，Relation为关系
	public HashMap< Long , Relation > relationMap;
	
	public class Relation
	{
		public long userid;
		public int ischat;// 类型（int） 是否交流（0-否，1-有）
		public int type;// 解除类型（0：解除不处理，10：发送钻石礼物，11接收到钻石礼物，
								//20：关注成为好友，21：被关注成为好友，30：收到邂逅同意，31：邂逅同意）
	}
	
	public void put(long userid, int ischat)
	{
		Relation bean = new Relation( );
		bean.userid = userid;
		bean.ischat = ischat;
		
		if(relationMap == null)
		{
			relationMap = new HashMap< Long , Relation >( );
		}
		
		relationMap.put( userid , bean );
	}
	
	public Relation initRelation(long userid, int ischat)
	{
		Relation bean = new Relation( );
		bean.userid = userid;
		bean.ischat = ischat;
		return bean;
	}
}
