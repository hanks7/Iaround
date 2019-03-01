
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.BaseUserInfo;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * 话题
 * 
 * @author
 * 
 */
public class TopicLikeInfBeen extends BaseServerBean implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8478655291676103405L;
	
	public int total;//点赞总数
	
	public int curruserlike;//类型（byte）当前用户是否点赞（0-否，1-是）
	
	public ArrayList< LikeUser > likeusers;

	public class LikeUser extends BaseUserInfo implements Serializable
	{		/**
		 * 
		 */
		private static final long serialVersionUID = 2175112664581694739L;
//		public String userid ;
//		public String icon;
//		public int viplevel;
	}

	//添加自己
	public void addLikeUser(long userid, String icon , int viplevel)
	{
		LikeUser user = new LikeUser();
		user.userid = userid;
		user.icon = icon;
		user.viplevel = viplevel;
		if(likeusers == null)
		{
			likeusers = new ArrayList<LikeUser>();
		}
		likeusers.add(user);
		curruserlike = 1;
		total++;
	}

	//移除自己
	public void removeLikeUser(long userid)
	{
		if(likeusers == null)
		{
			return;
		}
		for(int i = 0; i < likeusers.size(); i++)
		{
			if(likeusers.get(i).userid == userid   )
			{
				likeusers.remove(i);
				likeusers.trimToSize();
				curruserlike = 0;
				total--;
				break;
			}
		}

	}
	
}

