
package net.iaround.ui.group.bean;


import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.datamodel.BaseUserInfo;


import java.util.ArrayList;


/**
 * @author zhengshaota
 * @version 创建时间：2014-11-24 下午21:43
 * @Description: 圈聚会详情的实体
 */
public class GatherDetailBean extends BaseServerBean
{

	public DetailBean partyinfo;// 圈聚会详情
	
	public class DetailBean
	{
		/**
		 *  0圈主；1管理员；2圈成员；3非圈成员
		 */
		public int grouprole;//圈成员角色
		public DetailUserBean user;// 圈聚会发起者的用户信息
		public DetailPartyBeen party;// 聚会内容
		public GatherListBean.joininfoBeen joininfo;// 已参加用户列表
	}
	
	public class DetailUserBean extends BaseUserInfo
	{
//		public long userid;// 用户id
//		public String nickname;// 用户昵称
//		public String icon;// 用户头像
//		public int viplevel;// Vip等级，0：非Vip
//		public int age;// 年龄
//		public String gender;// 性别（f:女；m:男)
//		public long lastonlinetime;// 最后在线时间
//		public String selftext;// 个人签名
		
		public String getGender( )
		{
			if ( !CommonFunction.isEmptyOrNullStr( this.gender ) )
			{
				return gender;
			}
			return gender = "";
		}

		public int getViplevel( )
		{
			return viplevel;
		}

		public void setViplevel( int viplevel )
		{
			this.viplevel = viplevel;
		}
	}
	
	public class DetailPartyBeen
	{
		public int partyid; // 聚会id
		public long userid; // 用户id
		public long groupid; // 圈子id
		public String content; // 内容
		public ArrayList< String > photos;
		public long jointime; // 参加时间
		public long datetime; // 时间
		public String address; // 地址
		public String phone;// 手机
		public String cost;// 费用
		/** 0-可用，1-取消，2-过期 */
		public int status;
		
		public ArrayList< String > getPhotoList( )
		{
			if ( photos == null )
				photos = new ArrayList< String >( );
			return photos;
		}
	}
	
//	public class DetailjoininfoBeen
//	{
//		public int total;// 参加总数
//		public byte curruserjoin;// 当前用户是否参加
//		public ArrayList< JoinUser > joinusers = new ArrayList< JoinUser >( );// 参加用户列表
//
//		public ArrayList< JoinUser > getjoinUsersList( )
//		{
//			if ( joinusers == null )
//				joinusers = new ArrayList< JoinUser >( );
//			return joinusers;
//		}
//
//		public class JoinUser
//		{
//			public String userid;
//			public String icon;
//			public int viplevel;// Vip等级，0：非Vip
//
//
//		}
//	}
}
