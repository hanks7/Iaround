
package net.iaround.entity.type;


import android.content.Context;

import net.iaround.R;


/**
 * @ClassName ChatFromType.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-22 下午3:04:12
 * @Description: 通过哪里找到对方进行聊天的类型；与5.5新增的用户关系相关联。
 */

public class ChatFromType
{
	public final static int UNKONW = 0;// 未知，或者不需要的时候用这个变量
	public final static int SearchID = 1;// 通过ID搜索找到你
	public final static int SearchNickName = 2;// 通过昵称搜索找到你
	public final static int NearList = 3;// 通过附近的人找到你
	public final static int NearMap = 4;// 通过地图漫游找到你
	public final static int GrobalFocus = 5;// 通过全球焦点找到你
	public final static int NearFocus = 6;// 通过附近焦点找到你
	
	public final static int Group = 7;// 通过圈子找到你
	public final static int ImageChosen = 8;// 通过精选照片找到你
	public final static int Rank = 9;// 通过排行榜找到你
	public final static int GamePlayer = 10;// 通过游戏玩家找到你
	public final static int MeetingGame = 11;// 通过邂逅游戏找到你
	public final static int RecentVisit = 12;// 通过最近来访找到你
	
	
	public final static int AddBookFriend = 31;// 通讯录朋友（address book friend）
	public final static int ABFriendsABFriend = 32;// 通讯录朋友的通讯录朋友
	public final static int ABFriendsFriend = 33;// 通讯录朋友的朋友
	public final static int SomeOneFriendsFriend = 34;// 好友的好友
	public final static int SomeOneGamePlayer = 35;// xx游戏玩家

	public final static int DynamicComment = 36; //动态评论
	public final static int DynamicLike = 37; //动态点赞
	public final static int GroupTopic = 38;// 圈话题
	public final static int GroupTopicComment = 39;// 圈话题评论

	public final static int PostbarTopic = 40;// 侃啦话题
	public final static int PostbarTopicComment = 40;// 侃啦话题评论

	public final static int ContactsList = 41;// 联系人列表

	public final static int OTHERGIVEGIFT = 42;// 通过他人资料送礼
	
	// 返回关系的字符串
	public static String getRelationStr(Context mContext , int relationType )
	{
		switch ( relationType )
		{
			case AddBookFriend :
				return mContext.getString( R.string.AddBookFriend );
			case ABFriendsABFriend :
				return mContext.getString( R.string.ABFriendsABFriend );
			case ABFriendsFriend :
				return mContext.getString( R.string.ABFriendsFriend );
			case SomeOneFriendsFriend :
				return mContext.getString( R.string.SomeOneFriendsFriend );
			case NearList :
				return mContext.getString( R.string.NearList );
			default :
				return mContext.getString( R.string.IaroundUser );
		}
	}
	
}
