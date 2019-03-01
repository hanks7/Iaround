package net.iaround.ui.postbar;

import net.iaround.ui.group.bean.PostbarUserInfo;

import java.util.ArrayList;

public class TopicLikeBasicInfo
{
	/** 点赞总数 */
	public int total;

	/** 当前用户是否点赞（0-否，1-是） */
	public int curruserlike;

	/** 点赞用户列表 */
	public ArrayList<PostbarUserInfo> likeusers;

	public TopicLikeBasicInfo( )
	{
		// TODO Auto-generated constructor stub
		likeusers = new ArrayList< PostbarUserInfo >( );
	}

	// 添加自己
	public void addLikeUser(long userid , String icon , int viplevel )
	{
		PostbarUserInfo userInfo = new PostbarUserInfo( );
		/*userInfo.user = new PostbarUserInfo( );
		userInfo.user.userid = userid;
		userInfo.user.icon = icon;
		*/
		userInfo.userid = userid;
		userInfo.icon = icon;
		userInfo.viplevel = viplevel;
		if ( likeusers == null )
		{
			likeusers = new ArrayList< PostbarUserInfo >( );
		}
//		if (PostbarTopicDetailActivity.isRequestDetailSuccess) {
//			likeusers.add( userInfo );
//		}//jiqiang
		curruserlike = 1;
		total++;
	}

	// 移除自己
	public void removeLikeUser( long userid )
	{
		if (likeusers == null) {
			return;
		}
//		if (PostbarTopicDetailActivity.isRequestDetailSuccess) {
//			for (int i = 0; i < likeusers.size(); i++) {
//				if (likeusers.get(i).userid == userid) {
//					likeusers.remove(i);
//					likeusers.trimToSize();
//					curruserlike = 0;
//					total--;
//					break;
//				}
//			}
//		} else {
//			curruserlike = 0;
//			total--;
//			if (total < 0) {
//				total = 0;
//			}
//		}//jiqiang
	}

	public class PostbarUserExtendInfo
	{
		public PostbarUserInfo user;

		public long datetime;
	}
}
