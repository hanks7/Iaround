
package net.iaround.connector.protocol;


import android.content.Context;
import android.widget.LinearLayout;

import net.iaround.BuildConfig;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PhoneInfoUtil;

import org.apache.http.client.protocol.ClientContextConfigurer;

import java.util.LinkedHashMap;


/**
 * 圈子协议接口
 * 
 * @author linyg
 * @date 2012-8-25 v2.6.0
 * 
 */
public class GroupHttpProtocol extends HttpProtocol
{
	/** 获取创建圈子所需的地标信息 */
	public static String sGetGroupBuildingInfo = "/group/landmark/list";
	/** 获取剩余管理员数量 */
	public static String sGetRemainManagerCount = "/group/manager/leftquota";
	/** 获取圈子类型 */
	public static String sGetGroupType = "/group/categorylist_5_0";
	/** 通过关键字搜索圈子成员 */
	public static String sSearchGroupMember = "/group/members/search";
	/** 获取圈子被禁言用户列表 */
	public static String sGetForbiddenList = "/group/manager/forbidlist";

	/** 获取圈子权限 */
	public static String sGetGroupRole = "/group/authority/detail";
	/** 批量设置管理员 */
	public static String sBatchSetManager = "/group/manager/addbatch";
	/** 批量取消管理员 */
	public static String sBatchDelManager = "/group/manager/cancelbatch";
	/** 批量踢人 */
	public static String sBatchKickUser = "/group/manager/kickbatch";
	/**聊吧背包礼物接口*/
	public static String sGetBagGift = "/v1/gift/bag/list";
	/**聊吧商店礼物接口*/
	public static String sGetStoreGift = "/v1/gift/store/list";

	/**抽奖次数更新接口*/
	public static String sGetLotteryNum = "/v1/task/getlotterynum";

	public static long groupPost(Context context , String url , LinkedHashMap< String , Object > map , HttpCallBack callback )
	{
		return ConnectorManage.getInstance( context ).asynPost( url, map, ConnectorManage.HTTP_GROUP, callback );
	}


	/**
	 * 圈子禁言用户信息
	 * 
	 * @version v2.6.0
	 * @param context
	 * @param groupid
	 *            圈子id
	 * @param userid
	 *            用户id
	 * @return
	 * @throws ConnectionException
	 */
	public static long forbinUserInfo(Context context , String groupid , long userid ,
                                      HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "userid" , userid );
		return groupPost( context , "/group/manager/forbiduserinfo" , map , callback );
	}

	/**
	 * 加入聊吧
	 *
	 * @vesion v2.5.0
	 * @param context
	 * @param groupid
	 * @param logincode
	 * @return
	 * @throws ConnectionException
	 */
	public static long joinChatbar(Context context , String groupid , String logincode ,
									HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "logincode" , logincode );
		return groupPost( context , "v1/chatbar/group/join" , map , callback );
	}

	/**
	 * 加载地理范围、金币消耗对照列表
	 * 
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupGoldUsedList(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/group/goldusedlist" , map , callback );
	}
	
	/**
	 * 获取群组详情
	 * 
	 * @param groupId
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupDetail(Context context , String groupId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , "/group/info/detail" , map , callback );
	}
	
	/**
	 * 退出群 v2.5
	 * 
	 * @param groupId
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupDelUser(Context context , String groupId , String loginCode ,
                                    HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "logincode" , loginCode );
		return groupPost( context , "/group/deluser_2_5" , map , callback );
	}
	
	/**
	 * 解散群
	 *
	 * @param groupId
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupDel(Context context , String groupId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , "/group/del" , map , callback );
	}
	
	/**
	 * 升级成管理员
	 * 
	 * @param groupid
	 * @param memberId
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupManagerAdd(Context context , String groupid , String memberId ,
                                       HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "memberid" , memberId );
		return groupPost( context , "/group/manager/add" , map , callback );
	}
	
	/**
	 * 取消管理员身份
	 * 
	 * @param groupid
	 * @param memberId
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupManagerCancel(Context context , String groupid , String memberId ,
                                          HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "memberid" , memberId );
		return groupPost( context , "/group/manager/cancel" , map , callback );
	}
	
	/**
	 * 获取群管理员ID列表
	 * 
	 * @param groupid
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupManageList(Context context , String groupid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		return groupPost( context , "/group/manager/list" , map , callback );
	}
	
	/**
	 * 群组成员
	 * 
	 * @param groupid
	 *            群ID
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页的数目
	 * @return 返回-1时，表示请求失败
	 */
	public static long groupMember(Context context , String groupid , int pageno ,
                                   int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return groupPost( context , "/group/members_2_3" , map , callback );
	}
	
	/**
	 * 对圈子用户禁言
	 * @param groupid
	 *            圈子id
	 * @param userid
	 *            被禁言的用户id
	 * @param forbidtime
	 *            禁言时间（单位毫秒）
	 * @return -1为请求失败
	 */
	public static long groupManageForbid(Context context , String groupid , String userid ,
                                         long forbidtime , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "userid" , userid );
		map.put( "forbidtime" , forbidtime );
		return groupPost( context , "/group/manager/forbid" , map , callback );
	}
	
	/**
	 * 解禁用户
	 * 
	 * @param groupid
	 *            圈子id
	 * @param userid
	 *            被禁言的用户id
	 * @return -1为请求失败
	 */
	public static long groupManagerCacelForbid(Context context , String groupid ,
                                               String userid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "userid" , userid );
		return groupPost( context , "/group/manager/cancelforbid" , map , callback );
	}
	
	/**
	 * 检查当前的用户是否被解禁
	 * 
	 * @param context
	 * @param groupid
	 *            圈子id
	 * @param userid
	 *            用户id
	 * @return -1请求失败
	 */
	public static long groupCheckForbid(Context context , String groupid , long userid ,
                                        HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "userid" , userid );
		return groupPost( context , "/group/checkforbid" , map , callback );
	}
	
	/**
	 * 附近的圈子列表
	 * 
	 * @param context
	 * @param lat
	 * @param lng
	 *            排序类型: 1 距离、2 话题数、3 成员数
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页的条数
	 * @return -1请求失败
	 */
	public static long groupNear(Context context , int lat , int lng ,
                                 // int orderby,
                                 String landmarkid , int pageno , int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		// map.put("orderby", orderby);
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		map.put( "landmarkid" , landmarkid );
//		return groupPost( context , "/group/nearlist_5_0" , map , callback );
		return groupPost( context , "/group/nearlist_7_0" , map , callback );
	}
	
	/**
	 * 我的圈子列表
	 * 
	 * @param context
	 * @return -1请求失败
	 */
	public static long groupMylist(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/group/mylist" , map , callback );
	}
	
	/**
	 * @Title: searchGroup
	 * @Description: 5.0版本搜索圈子
	 * @param context
	 *            上下文
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @param keyword
	 *            关键字或id
	 * @param categoryId
	 *            分类id
	 * @param distance
	 *            距离
	 * @param time
	 *            时间
	 * @param orderby
	 *            排序
	 * @param pageno
	 *            页码
	 * @param pagesize
	 *            每页数
	 * @param callback
	 *            回调
	 * @return
	 */
	public static long searchGroup(Context context , int lat , int lng , String keyword ,
                                   String categoryId , long distance , long time , int orderby , int pageno ,
                                   int pagesize , HttpCallBack callback )
	{
		String dis = distance == 0 ? "" : String.valueOf( distance );
		String strTime = time == 0 ? "" : String.valueOf( time );
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "id" , keyword );
		// map.put( "categoryid" , categoryId );
		// map.put( "distance" , dis );
		// map.put( "time" , strTime );
		// map.put( "orderby" , orderby );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return groupPost( context , "/group/search/search" , map , callback );
	}
	
	/**
	 * @Title: getGroupRole
	 * @Description: 获取圈子权限详情
	 * @param context
	 *            上下文
	 * @param groupId
	 *            圈id
	 * @return
	 */
	public static long getGroupRole(Context context , String groupId , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , sGetGroupRole , map , callback );
	}
	
	/**
	 * @Title: postGroupRole
	 * @Description: 修改圈子权限
	 * @param context
	 *            上下文
	 * @param groupId
	 *            圈id
	 * @param talk
	 *            发言权限
	 * @param publicshtopic
	 *            发表话题权限
	 * @param joincheck
	 *            加入圈子是否审核
	 * @return
	 */
	public static long postGroupRole(Context context , String groupId , int talk ,
                                     int publicshtopic , int joincheck , int viewtopic , int issearch ,
                                     HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "talk" , talk );
		map.put( "publishtopic" , publicshtopic );
		map.put( "joincheck" , joincheck );
		map.put( "viewtopic" , viewtopic );
		map.put( "issearch" , issearch );
		return groupPost( context , "/group/authority/modify_5_3" , map , callback );
	}
	
	/**
	 * @Title: submitJoinGroup
	 * @Description: 申请加入圈子
	 * @param context
	 * @param groupId
	 * @param content
	 * @return
	 */
	public static long submitJoinGroup(Context context , String groupId , String content ,
                                       HttpCallBack callback )
	{
		PhoneInfoUtil phone = PhoneInfoUtil.getInstance( context );
		String deviceID = phone.getDeviceId( );
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
//		map.put( "content" , content );
		map.put( "logincode" , CryptoUtil.SHA1( deviceID ) );
//		return groupPost( context , sSubmitJoinGroup , map , callback );
		return groupPost( context , "/v1/chatbar/group/requestjoin" , map , callback );
	}
	
	/**
	 * @Title: batchSetManager
	 * @Description: 批量设置管理员
	 * @param context
	 * @param groupId
	 *            圈子id
	 * @param userIds
	 *            用户id，以英文逗号分隔
	 * @return
	 */
	public static long batchSetManager(Context context , String groupId , String userIds ,
                                       HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userids" , userIds );
		return groupPost( context , sBatchSetManager , map , callback );
	}
	
	/**
	 * @Title: batchDelManager
	 * @Description: 批量取消管理员
	 * @param context
	 * @param groupId
	 *            圈子id
	 * @param userIds
	 *            用户id，以英文逗号分隔
	 * @return
	 */
	public static long batchDelManager(Context context , String groupId , String userIds ,
                                       HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userids" , userIds );
		return groupPost( context , sBatchDelManager , map , callback );
	}
	
	/**
	 * @Title: batchKickUser
	 * @Description: 批量踢出圈子
	 * @param context
	 * @param groupId
	 *            圈子id
	 * @param userIds
	 *            用户id，以英文逗号分隔
	 * @return
	 */
	public static long batchKickUser(Context context , String groupId , String userIds ,
                                     HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userids" , userIds );
		return groupPost( context , sBatchKickUser , map , callback );
	}

	/**
	 * @Title: agreenApplyMessage
	 * @Description: 同意加入圈子
	 * @param context
	 * @param groupId
	 * @param userId
	 * @return
	 */
	public static long agreenApplyMessage(Context context , String groupId , String userId ,
                                          HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
//		return groupPost( context , sAgreenApplyMsg , map , callback );
		return groupPost( context , "v1/chatbar/group/agree", map , callback );
	}
	
	public static long refuseApplyMessage(Context context , String groupId , String userId ,
                                          HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
//		return groupPost( context , sRefuseApplyMsg , map , callback );
		return groupPost( context ,"v1/chatbar/group/refuse" , map , callback );
	}
	
	/**
	 * @Title: getGroupSilenceMember
	 * @Description: 获取被禁言用户列表
	 * @param context
	 * @param groupId
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static long getGroupSilenceMember(Context context , String groupId , int pageNo ,
                                             int pageSize , long time , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "pageno" , pageNo );
		map.put( "pagesize" , pageSize );
		map.put( "time" , time );
		return groupPost( context , sGetForbiddenList , map , callback );
	}
	
	/**
	 * @Title: searchGroupMember
	 * @Description: 搜索圈子成员
	 * @param context
	 * @param groupId
	 * @param userId
	 *            关键字
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public static long searchGroupMember(Context context , String groupId , String userId ,
                                         int pageNo , int pageSize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		map.put( "userid" , userId );
		map.put( "pageno" , pageNo );
		map.put( "pagesize" , pageSize );
		return groupPost( context , sSearchGroupMember , map , callback );
	}

	/**
	 * @Title: getGroupTypeList
	 * @Description: 获取圈子类型列表
	 * @param context
	 * @return
	 */
	public static long getGroupTypeList(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return groupPost( context , sGetGroupType , map , callback );
	}
	
	/**
	 * @Title: getRemainManagerCount
	 * @Description: 获取剩余管理员数量
	 * @param context
	 * @param groupId
	 * @return
	 */
	public static long getRemainManagerCount(Context context , String groupId ,
                                             HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , sGetRemainManagerCount , map , callback );
	}
	
	/**
	 * @Title: getGroupBuildingInfo
	 * @Description: 获取创建圈子所需的地标信息
	 * @param context
	 * @param language
	 *            语言（1：大陆，2：非大陆）
	 * @param groupTypeId
	 *            地标类型id
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @param pageNo
	 *            页码
	 * @param pageSize
	 *            每页个数
	 * @param callback
	 *            回调
	 * @return
	 */
	public static long getGroupBuildingInfo(Context context , int language ,
                                            String groupTypeId , int lat , int lng , int pageNo , int pageSize ,
                                            HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "civil" , language );
		map.put( "type" , groupTypeId );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "pageno" , pageNo );
		map.put( "pagesize" , pageSize );
		return groupPost( context , sGetGroupBuildingInfo , map , callback );
	}
	
	/**
	 * @Title: getGroupBuildingInfoFromGoogle
	 * @Description: 从Google上获取地标信息
	 * @param context
	 * @param url
	 * @param callback
	 * @return
	 */
	public static long getGroupBuildingInfoFromGoogle(Context context , String url ,
                                                      HttpCallBack callback )
	{
		long flag = -1;
		try
		{
			flag = ConnectorManage.getInstance( context ).simplePost( url , "" , callback );
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		return flag;
	}
	
	/**
	 * @Title: getCreateGroupCondition_5_3
	 * @Description: 获取创建圈子所需的条件(5.3)
	 * @param context
	 * @return
	 */
	public static long getCreateGroupCondition_5_3(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/group/create/conditions" , map , callback );
	}
	
	/**
	 * @Title: createGroup_5_3
	 * @Description: 5.3创建圈子
	 * @param context
	 *            上下文
	 * @param type
	 *            圈类型
	 * @param groupName
	 *            圈子名称
	 * @param groupIcon
	 *            圈子图标地址
	 * @param groupCategoryId
	 *            圈子类型id
	 * @param lat
	 *            纬度
	 * @param lng
	 *            经度
	 * @param address
	 *            地址
	 * @param rang
	 *            范围
	 * @param groupDesc
	 *            描述
	 * @param centerId
	 *            圈中心id
	 * @param centerName
	 *            圈中心名称
	 * @param centerLat
	 *            圈中心纬度
	 * @param centerLng
	 *            圈中心经度
	 * @param callback
	 *            接口回调
	 * @return
	 */
	public static long createGroup_5_3(Context context , int type , String groupName ,
                                       String groupIcon , String groupCategoryId , String lat , String lng ,
                                       String address , String rang , String groupDesc , String centerId ,
                                       String centerName , String centerLat , String centerLng , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "type" , type );
		map.put( "name" , groupName );
		map.put( "icon" , groupIcon );
		map.put( "categoryid" , groupCategoryId );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "address" , address );
		map.put( "content" , groupDesc );
		map.put( "landmarkid" , centerId );
		map.put( "landmarkname" , centerName );
		map.put( "landmarklat" , centerLat );
		map.put( "landmarklng" , centerLng );
		return groupPost( context , "/group/create/create" , map , callback );
	}
	
	/**
	 * @Title: getGroupRenewInfo
	 * @Description: 获取圈子年度服务信息
	 * @param context
	 * @param groupId
	 * @return
	 */
	public static long getGroupRenewInfo(Context context , String groupId ,
                                         HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , "/group/authority/expired" , map , callback );
	}
	
	/**
	 * @Title: getGroupSearchKeyword
	 * @Description: 获取圈子热门搜索词
	 * @param context
	 * @return
	 */
	public static long getGroupSearchKeyword(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/group/search/keyword" , map , callback );
	}
	
	/**
	 * @Title: getRecentNewGroups
	 * @Description: 获取七天内新建圈子列表
	 * @param context
	 * @return
	 */
	public static long getRecentNewGroups(Context context , long lat , long lng , int pageno ,
                                          int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return groupPost( context , "/group/nearnewlist" , map , callback );
	}
	
	/**
	 * @Title: getRecommendGroups
	 * @Description: 推荐圈子列表
	 * @param context
	 * @return
	 */
	public static long getRecommendGroups(Context context , long lat , long lng , int pageno ,
                                          int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "lat" , lat );
		map.put( "lng" , lng );
		map.put( "pageno" , pageno );
		map.put( "pagesize" , pagesize );
		return groupPost( context , "/group/recommend/list" , map , callback );
	}
	
	/**
	 * @Title: getUserTopicGreetList
	 * @Description: 获取话题点赞用户列表
	 * @param context
	 * @return
	 */
	public static long getUserTopicGreetList(Context context , long groupId , long topicId ,
                                             int pageNO , int pageSize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		
		map.put( "groupid" , groupId );
		map.put( "topicid" , topicId );
		map.put( "pageno" , pageNO );
		map.put( "pagesize" , pageSize );
		
		return groupPost( context , "/topic/like/list" , map , callback );
	}
	
	/**
	 * @Title: getUserTopicGreetList
	 * @Description: 获取话题点赞用户列表
	 * @param context
	 * @return
	 */
	public static long getUserTopicCommentList(Context context , String groupId ,
                                               String topicId , String userId , String pageNO , String pageSize ,
                                               HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		
		map.put( "groupid" , groupId );
		map.put( "topicid" , topicId );
		map.put( "userid" , userId );
		map.put( "pageno" , pageNO );
		map.put( "pagesize" , pageSize );
		return groupPost( context , "/topic/review/list" , map , callback );
	}
	
	
	/**
	 * @Title:
	 * @Description: 推荐圈子商品列表
	 * @param context
	 * @return
	 */
	public static long getRecommendGoodsList(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		
		
		return groupPost( context , "/group/recommend/goodslist" , map , callback );
	}
	
	/**
	 * @Title:
	 * @Description: 推荐圈子出价
	 * @param context
	 * @return
	 */
	public static long getRecommendGoodsList(Context context , int id , String groupid ,
                                             HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		
		map.put( "id" , String.valueOf( id ) );
		map.put( "groupid" , groupid );
		return groupPost( context , "/group/recommend/buy" , map , callback );
	}

	/**
	 * 发布圈聚会
	 * */
	public static long publishGroupGather(Context context , String groupid , String content ,
                                          String photos , long jointime , String address , String phone , String cost ,
                                          HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "content" , content );
		map.put( "photos" , photos );
		map.put( "jointime" , jointime );
		map.put( "address" , address );
		map.put( "phone" , phone );
		map.put( "cost" , cost );
		return groupPost( context , "/party/add" , map , callback );
	}
	
	/**
	 * 获取圈聚会列表
	 * */
	public static long getGroupGatherList(Context context , String groupid , int pageno ,
                                          int pagesize , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "pageno" , pageno );
		entity.put( "pagesize" , pagesize );
		return groupPost( context , "/party/list" , entity , callback );
	}
	
	/**
	 * 参加圈聚会
	 * */
	public static long joinGroupGather(Context context , String groupid , int partyid ,
                                       HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "partyid" , partyid );
		return groupPost( context , "/party/join" , entity , callback );
	}
	
	/**
	 * 参加圈聚会
	 * */
	public static long DeleteGroupGather(Context context , String groupid , int partyid ,
                                         HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "partyid" , partyid );
		return groupPost( context , "/party/del" , entity , callback );
	}
	
	/**
	 * 取消圈聚会
	 * */
	public static long cancelGroupGather(Context context , String groupid , int partyid ,
                                         HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "partyid" , partyid );
		return groupPost( context , "/party/cancel" , entity , callback );
	}
	
	/**
	 * 获取圈聚会详情
	 * */
	public static long getGroupGatherDetail(Context context , String groupid , int partyid ,
                                            HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "partyid" , partyid );
		return groupPost( context , "/party/detail" , entity , callback );
	}
	
	/**
	 * 修改圈聚会
	 * */
	public static long reviseGroupGather(Context context , String groupid , String partyid ,
                                         String content , String photos , long jointime , String address , String phone ,
                                         String cost , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		map.put( "partyid" , partyid );
		map.put( "content" , content );
		map.put( "photos" , photos );
		map.put( "jointime" , jointime );
		map.put( "address" , address );
		map.put( "phone" , phone );
		map.put( "cost" , cost );
		return groupPost( context , "/party/update" , map , callback );
	}
	
	/**
	 * transferGroup 圈子转让
	 * */
	// /topic/manager/transferopt
	public static long transferGroup(Context context , String groupid , String userid ,
                                     HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		
		entity.put( "groupid" , groupid );
		entity.put( "userid" , userid );
		
		return groupPost( context , "/group/manager/transfer" , entity , callback );
	}
	
	
	/**
	 * transferManagerGroup 接收到圈子转让消息后进行处理 是否接受 y-是 n-否
	 * */
	public static long transferManagerGroup(Context context , String groupid ,
                                            boolean isAccept , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		
		String strAccept = isAccept ? "y" : "n";
		entity.put( "groupid" , groupid );
		entity.put( "value" , strAccept );
		
		return groupPost( context , "/group/manager/transferopt" , entity , callback );
	}
	
	/**
	 * 获取圈话题用户未读消息
	 * */
	public static long getGroupTopicUnread(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/topic/message/mylist" , entity , callback );
	}
	
	/**
	 * 获取圈话题用户历史消息
	 * */
	public static long getGroupTopicHistory(Context context , int pageNo , int pageSize ,
                                            long time , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "pageno" , pageNo );
		entity.put( "pagesize" , pageSize );
		entity.put( "time" , time );
		return groupPost( context , "/topic/message/history" , entity , callback );
	}
	
	/**
	 * 删除圈话题用户历史消息
	 * */
	public static long deleteGroupTopicList(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/topic/message/delhistory" , entity , callback );
	}
	
	/** 获取圈消息接收状态列表 */
	public static long getGroupMsgReceiveStatus(Context context , HttpCallBack callback)
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/group/message/optionlist" , entity , callback );
	}
	
	/** 设置圈消息接收状态 */
	public static long setGroupMsgReceiveStatus(Context context , long groupid , int type ,
                                                HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "type" , type );
		return groupPost( context , "/group/message/updateoption" , entity , callback );
	}
	
	
	/** 设置圈助手开启状态 */
	public static long setGroupHelperStatus(Context context , String value ,
                                            HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "value" , value );
		return groupPost( context , "/group/assistant/update" , entity , callback );
	}
	
	/**
	 * 获取圈话题用户未读点赞消息
	 * */
	public static long getGroupTopicNewLike(Context context , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		return groupPost( context , "/topic/message/mylikelist" , entity , callback );
	}
	
	/**
	 * 获取圈话题用户历史点赞消息
	 * */
	public static long getGroupTopicHistoryLike(Context context , int pageNo , int pageSize ,
                                                long time , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "pageno" , pageNo );
		entity.put( "pagesize" , pageSize );
		entity.put( "time" , time );
		return groupPost( context , "/topic/message/historylike" , entity , callback );
	}

	/**
	 * 进入聊吧拉取资料接口
	 * */
	public static long getGroupTopicSimple(Context context ,String groupid , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put( "packageid" , BuildConfig.packageID );
		//注意 加上 下面两个参数竟然会报错 接口返回数据有问题 未知
//		entity.put( "plat" , Config.PLAT );
//		entity.put( "appversion" , Config.APP_VERSION );
		return groupPost( context , "/v1/chatbar/simple" , entity , callback );
	}
	/**
	 * 聊吧在线用户
	 * */
	public static long getMemberOnLine(Context context ,String groupid ,int pagesize,int pageno,HttpCallBack callback )
	{
		LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
		entity.put( "groupid" , groupid );
		entity.put("page_size",pagesize);
		entity.put("page_no",pageno);
		entity.put("plat",1);
		entity.put("appversion", Config.APP_VERSION);
		entity.put("packageid",CommonFunction.getPackageMetaData(context));
		return groupPost( context , "/v1/chatbar/online" , entity , callback );
	}

	/**
	 * 聊吧内点击用户头像查看资料
	 */
	public static long getUserInfo(Context context,String groupid,String userid,HttpCallBack callback)
	{
		LinkedHashMap<String,Object> entity = new LinkedHashMap<>();
		entity.put("userid",userid);
		entity.put("groupid",groupid);
		return groupPost( context , "/v1/chatbar/user/info" , entity , callback );
	}

	/**
	 * 聊吧背包礼物
	 * @param context
	 * @param callBack
     * @return
     */
	public static long getBagGiftData(Context context,HttpCallBack callBack)
	{
		return groupPost(context,sGetBagGift,null,callBack);
	}
	/**
	 * 聊吧商店礼物
	 * @param context
	 * @param callBack
	 * @return
	 */
	public static long getStoreGiftData(Context context,HttpCallBack callBack)
	{
		return groupPost(context,sGetStoreGift,null,callBack);
	}

	/**
	 * 背包礼物送礼
	 * @param context
	 * @param userid
	 * 				用户id
	 * @param giftid
	 * 				礼物id
	 * @param number
	 * 				赠送礼物数量
	 * @param combo_id
	 * 				是否连击  0 否  1 是
	 * @param groupid
	 * 					连击标识
     * @param callBack
     * @return
     */
	public static long sendBagGift(Context context,long userid,int giftid,long userGiftId,int number,long combo_id,long groupid,HttpCallBack callBack)
	{
		LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
		entity.put("userid", userid);
		entity.put("giftid", giftid);
		entity.put("number",number);
		entity.put("user_gift_id",userGiftId);
		entity.put("combo_id",combo_id);
		entity.put("group_id",groupid);
		return groupPost(context, "v1/gift/bag/send", entity, callBack);
	}

	/**
	 * 聊吧商店礼物  视屏聊天
	 * @param context
	 * @param callBack
	 * @return
	 */
	public static long getLoveGiftData(Context context,HttpCallBack callBack)
	{
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("mType",1);
		return groupPost(context,sGetStoreGift,map,callBack);
	}

	/**
	 *
	 * @param context
	 * @param userid
	 * 				送礼用户id
	 * @param giftid
	 * 				礼物id
	 * @param userGiftId
	 * 				礼物连击标识
	 * @param number
	 * 				送的礼物数量
	 * @param combo_id
	 * 				数量id
	 * @param groupId
	 * 				group id
     * @param callBack
	 *
     * @return
     */
	public static long sendStoreGift(Context context,long userid,int giftid,long userGiftId,int number,long combo_id,long groupId,HttpCallBack callBack)
	{
		LinkedHashMap<String, Object> entity = new LinkedHashMap<String, Object>();
		entity.put("userid", userid);
		entity.put("giftid", giftid);
		entity.put("user_gift_id",userGiftId);
		entity.put("number",number);
		entity.put("combo_id",combo_id);
		entity.put("group_id",groupId);
		return groupPost(context, "v1/gift/store/send", entity, callBack);
	}

	/**
	 * 业务请求
	 *
	 * @param url
	 * @param map
	 * @return 返回-1时，表示请求失败
	 */
	public static long dynamicPost(Context context, String url,
								   LinkedHashMap<String, Object> map, HttpCallBack callback) {
		return ConnectorManage.getInstance(context).asynPost(url, map,
				ConnectorManage.HTTP_DYNAMIC, callback);
	}

	/**
	 * 获取聊吧资料详情
	 * @param context
	 * @param groupId
	 * @param httpCallBack
	 * @return
	 */
	public static long getChatbarInfo(Context context,String groupId,HttpCallBack httpCallBack)
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupId );
		return groupPost( context , "/v1/chatbar/detail" , map , httpCallBack );
	}

	/***
	 * follow 聊吧
	 * @param context
	 * @param groupid
	 * @param httpCallBack
	 * @return
	 */
	public static long followChatbar(Context context,String groupid,HttpCallBack httpCallBack)
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		return groupPost( context , "/v1/chatbar/group/follow" , map , httpCallBack );
	}

	/**
	 * 取消关注聊吧
	 * @param context
	 * @param groupid
	 * @param httpCallBack
	 * @return
	 */
	public static long cancelFollowChatbar(Context context,String groupid,HttpCallBack httpCallBack)
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "groupid" , groupid );
		return groupPost( context , "/v1/chatbar/group/cancelfollow" , map , httpCallBack );
	}

	/**
	 * 聊吧粉丝列表
	 * @param context
	 * @param pageno
	 * @param pagesize
	 * @param groupid
	 * @param httpCallBack
	 * @return
	 */
	public static long getChatbarFansList(Context context,int pageno,int pagesize,long groupid,HttpCallBack httpCallBack)
	{
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("pageno",pageno);
		map.put("pagesize",pagesize);
		map.put("groupid",groupid);
		return groupPost(context,"v1/chatbar/group/fanslist",map,httpCallBack);
	}

	/**
	 * 获取邀请聊天推荐好友列表
	 * @param context
	 * @param httpCallBack
	 * @return
	 */
	public static long getRecommendFriendsList(Context context,HttpCallBack httpCallBack)
	{
		return groupPost(context,"v1/chatbar/group/freshman",null,httpCallBack);
	}
	/**
	 * @Title:
	 * @Description: 邀请好友加入圈子
	 * @param context
	 * @return
	 */
	public static long chatbarInviteUser( Context context , String groupid ,
											  String userids , HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );

		map.put( "groupid" , groupid );
		map.put( "userids" , userids );
		return groupPost( context , "/v1/chatbar/group/invite/join" , map , callback );
	}

	/**
	 * 邀请聊天
	 * @param context
	 * @param groupid
	 * 					聊吧id
	 * @param userids
	 * 					需要邀请的用户id
	 * @param httpCallBack
	 * @return
	 */
	public static long chatbarInviteChat(Context context,String groupid,String userids,HttpCallBack httpCallBack)
	{
		LinkedHashMap<String ,Object> map = new LinkedHashMap<>();
		map.put("groupid",groupid);
		map.put("userids",userids);
		return groupPost(context,"/v1/chatbar/group/invite/chat",map,httpCallBack);
	}

	/**
	 * 聊吧召唤
	 * @param context
	 * @param groupid
	 * @param content
	 * @param httpCallBack
	 * @return
	 */
	public static long chatbarHomecall(Context context,String groupid,String content,HttpCallBack httpCallBack)
	{
		LinkedHashMap<String,Object> map = new LinkedHashMap<>();
		map.put("groupid",groupid);
		map.put("content",content);
		return groupPost(context,"v1/chatbar/group/homecall",map,httpCallBack);
	}

	/**
	 *编辑聊吧资料
	 * @param context
	 * @param groupId
	 * 				聊吧id
	 * @param groupName
	 * 				聊吧昵称
	 * @param groupIcon
	 * 				聊吧icon
	 * @param categoryId
	 * 				类型
	 * @param welcome
	 *				欢迎语内容
	 * @param content
	 *				聊吧介绍
	 * @param landmarkId
	 * @param landmarkName
	 * @param landmarkLat
	 * @param landmarkLng
	 * @param callback
	 * @return
	 */
	public static long editChatbarInfo(Context context , String groupId , String groupName ,
								 String groupIcon , String categoryId , String welcome , String content ,
								 String landmarkId , String landmarkName , String landmarkLat , String landmarkLng ,
								 HttpCallBack callback )
	{
		LinkedHashMap< String , Object > map = new LinkedHashMap< String , Object >( );
		map.put( "id" , groupId );
		map.put( "name" , groupName );
		map.put( "icon" , groupIcon );
		map.put( "categoryid" , categoryId );
		// map.put( "rang" , rang );
		map.put("welcome",welcome);
		map.put( "content" , content );
		map.put( "landmarkid" , landmarkId );
		map.put( "landmarkname" , landmarkName );
		map.put( "landmarklat" , landmarkLat );
		map.put( "landmarklng" , landmarkLng );
		return groupPost( context , "/group/update_5_3" , map , callback );
	}

	/**
	 * 获取抽奖次数
	 *
	 */
	public static long getLotteryNum(Context context , HttpCallBack callback )
	{
		return groupPost( context , sGetLotteryNum , null , callback );
	}

}
