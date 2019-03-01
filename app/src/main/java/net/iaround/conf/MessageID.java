package net.iaround.conf;

/**
 * 消息号
 *
 * @author linyg
 */
public class MessageID
{
	/** session服务 */
	public static final int SESSION = 80000;
	/** 群组服务 **/
	public static final int GROUP = 70000;
	/** 聊吧服务 */
	public static final int CHATBAR = 90000;
	/** 登录session服务器 */
	public static final int LOGINSESSION = 81006;
	/** 登陆session服务成功 */
	public static final int LOGINSESSION_Y = 82006;
	/** 登陆session服务失败 */
	public static final int LOGINSESSION_N = 83006;
	/** 分配聊天室 */
	public static final int ASSIGN = 81007;
	/** 分配聊天室成功 */
	public static final int ASSIGN_Y = 82007;
	/** 分配聊天室失败 */
	public static final int ASSIGN_N = 83007;
	/** 更换房间 **/
	public static final int CHANGE_ROOM = 81004;
	/** 更换房间成功 **/
	public static final int CHANGE_ROOM_Y = 82004;
	/** 更换房间失败 **/
	public static final int CHANGE_ROOM_N = 83004;
	/** session服务器心跳包 */
	public static final int SESSION_HEART = 81013;
	/** session心跳成功 */
	public static final int SESSION_HEART_Y = 82013;
	/** 发送私聊信息 **/
	public static final int SESSION_SEND_PRIVATE_CHAT = 81010;
	/** 发送私聊消息：成功 */
	public static final int SESSION_SEND_PRIVATE_CHAT_SUCC = 82010;
	/** 发送私聊消息：失败 */
	public static final int SESSION_SEND_PRIVATE_CHAT_FAIL = 83010;
	/** 接收私聊信息 **/
	public static final int SESSION_PRIVATE_CHAT = 81011;
	
	/** 私聊语音发送开始 **/
	public static final int SESSION_PRIVATE_AUDIO_BEGIN = 81052;
	/** 私聊语音发送开始_成功 **/
	public static final int SESSION_PRIVATE_AUDIO_BEGIN_SUCCESS = 82052;
	/** 私聊语音发送开始_失败 **/
	public static final int SESSION_PRIVATE_AUDIO_BEGIN_FAIL = 83052;
	/** 私聊语音发送中 **/
	public static final int SESSION_PRIVATE_AUDIO_SEND = 81053;
	/** 私聊语音发送中_成功 **/
	public static final int SESSION_PRIVATE_AUDIO_SEND_SUCCESS = 82053;
	/** 私聊语音发送中_失败 **/
	public static final int SESSION_PRIVATE_AUDIO_SEND_FAIL = 83053;
	/** 私聊语音发送结束 **/
	public static final int SESSION_PRIVATE_AUDIO_END = 81054;
	/** 私聊语音发送结束_成功 **/
	public static final int SESSION_PRIVATE_AUDIO_END_SUCCESS = 82054;
	/** 私聊语音发送结束_失败 **/
	public static final int SESSION_PRIVATE_AUDIO_END_FAIL = 83054;
	/** 私聊语音取消发送 **/
	public static final int SESSION_PRIVATE_AUDIO_CANCEL = 81059;
	/** 私聊语音取消发送_成功 **/
	public static final int SESSION_PRIVATE_AUDIO_CANCEL_SUCCESS = 82059;
	/** 私聊语音取消发送_失败 **/
	public static final int SESSION_PRIVATE_AUDIO_CANCEL_FAIL = 83059;
	// /**好友在线**/
	// public static final int SESSION_FRIEND_ONLINE = 81008;
	// /**好友离线**/
	// public static final int SESSION_FRIEND_OFFLINE = 81009;
	/** 发送通讯录好友 **/
	public static final int SESSION_PUSH_CONTACT = 81014;
	/** 已读私聊消息 **/
	public static final int SESSION_CHAT_PERSION_READED = 81022;
	/** 获取对方已读最大消息ID **/
	public static final int SESSION_GET_CHAT_PERSION_READED = 81023;
	/** 推送对方已读最大消息ID **/
	public static final int SESSION_PUSH_CHAT_PERSION_READED = 82023;
	// /**访问消息**/
	// public static final int SESSION_VISITOR_MESSAGE = 81032;
	/** 最新动态 */
	public static final int SESSION_DYNAMIC_MESSAGE = 81037;
	/** 推送私信 **/
	public static final int SESSION_PRIVATE_MESSAGE = 81001;
	// /**推送评论**/
	// public static final int SESSION_REVIEW_MESSAGE = 81002;
	/** 推送通知 **/
	public static final int SESSION_NOTICE_MESSAGE = 81003;
	
	/** 推送得分得经验 **/
	public static final int SESSION_SCORE = 81016;
	/** 获取敏感关键字更新 **/
	public static final int SEESION_KEYWORD = 81005;
	public static final int SESSION_KEYWORD_Y = 82005;
	/** 退出session */
	public static final int SESSION_LOGOUT = 81012;
	public static final int SESSION_LOGOUT_Y = 82012;
	/** 服务器出错 **/
	public static final int SESSION_EXCEPTION = 81019;
	/** 需重新登录Session服务器 **/
	public static final int SESSION_RELOGIN = 81017;
	/** 重新登陆分配Key **/
	public static final int SESSION_REGET_KEY = 81018;
	/** 用户在其它地方被登陆 **/
	public static final int SESSION_FORCE_OUT = 81015;
	/** 管理员强制用户下线 **/
	public static final int ADMIN_FORCE_OUT = 81025;
	/** 用户禁言消息 **/
	public static final int ADMIN_FORBID_SAY = 81024;
	/** 经纬度偏移 **/
	public static final int SESSION_OFFSET_GEO = 81020;
	public static final int SESSION_OFFSET_GEO_RETURN = 82020;
	/** 其它问题说明，由服务端下发错误 **/
	public static final int SESSION_OTHER_PROBLEM = 81021;
	/** 推送系统广告 **/
	public static final int SESSION_AD = 81026;
	/** 推送新粉丝数量 **/
	public static final int SESSION_ADD_FANS = 81027;
	/** 发送已读粉丝数量 **/
	public static final int SESSION_READED_FANS = 81028;
	/** 更新当前位置 **/
	public static final int SESSION_UPDATE_LOCATION = 81029;
	/** 获得一条搭讪消息所需金币 **/
	public static final int RECEIVE_ACCOST_GOLD = 81033;
	/** 获得一条搭讪消息所需金币：成功 **/
	public static final int RECEIVE_ACCOST_GOLD_SUCC = 82033;
	/** 获得一条搭讪消息所需金币：失败 **/
	public static final int RECEIVE_ACCOST_GOLD_FAIL = 83033;
	/** 发送搭讪消息 **/
	public static final int SEND_ACCOST_MSG = 81031;
	/** 发送搭讪消息：成功 **/
	public static final int SEND_ACCOST_MSG_SUCC = 82031;
	/** 发送搭讪消息：失败 **/
	public static final int SEND_ACCOST_MSG_FAIL = 83031;
	/** 获取搭讪对白 **/
	public static final int OBTAIN_ACCOST_STATEMENT = 81034;
	/** 获取搭讪对白成功 **/
	public static final int OBTAIN_ACCOST_STATEMENT_SUCC = 82034;
	/** 获取搭讪对白失败 **/
	public static final int OBTAIN_ACCOST_STATEMENT_FAIL = 83034;
	/** 标记全部私聊消息为已读（最近联系人） */
	public static final int MY_NEAR_CONTACT_MARK_ALL_READ = 81036;
	/** 发送私聊确认 **/
	public static final int SESSION_PRIVATEVILIFY = 81030;
	/** 相见你的人消息数量 **/
	public static final int SESSION_WANT_MEET = 81038;
	/** 推送未读私聊消息总数 **/
	public static final int SESSION_PRINVATE_NO_READ = 81039;
	/** 推送vip **/
	public static final int SESSION_TURN_VIP = 81040;
	/** 发送场景 **/
	public static final int SESSION_SEND_THEME = 81042;
	/** 发送场景返回 **/
	public static final int SESSION_SEND_THEME_RESPONSE = 82042;
	/** 发送场景失败返回 */
	public static final int SESSION_SEND_THEME_RESPONSE_ERROR = 83042;
	/** 推送场景 */
	// public static final int SESSION_RECEIVE_THEME = 81043;
	/** 推送过期消息 */
	public static final int SESSION_RECEIVE_EXPIRED = 81044;
	/** 推送新游戏数量 **/
	public static final int SESSION_RECEIVE_NEWGAME_COUNT = 81045;
	/** 推送圈子新成员数量和未审核成员数量 - 5.2 */
	public static final int SESSION_PUSH_GROUP_NEW_MEMBER = 81056;
	/** 推送同意加入圈子 - 5.3 */
	public static final int SESSION_PUSH_AGREED_ADD_GROUP = 81060;
	
	/** 推送圈子转让 - 5.6 */
	public static final int SESSION_PUSH_TRANSFER_GROUP = 81067;
	
	
	/** 推送遇见贴吧今日话题数 */
	public static final int SESSION_PUSH_POSTBAR_GROUP = 81069;
	
	// 5.5
	/** 未读好友推荐 */
	public static final int SESSION_PUSH_UNREAD_RECOMMEND_FRIEND = 81065;
	
	/** 登录group服务器 */
	public static final int GROUP_LOGIN = 71001;
	public static final int GROUP_LOGIN_Y = 72001;
	public static final int GROUP_LOGIN_N = 73001;
	/** 推送群组列表 */
	public static final int GROUP_GET_LIST = 71002;
	
	public static final int GROUP_PUSH_LIST_MEMBER = 72002;
	/** 推送群组未读消息 */
	@Deprecated
	public static final int GROUP_PUSH_NO_READ_MESSAGE = 71003;
	/** 进入群 */
	public static final int GROUP_COME_IN = 71004;//向服务器发送请求麦位信息
	public static final int GROUP_COME_IN_Y = 72004;//客户端接收麦位信息成功
	public static final int GROUP_COME_IN_N = 73004;//客户端接收麦位信息失败
	/** 群组用户列表 */
	public static final int GROUP_LIST_MEMBER = 71005;
	public static final int GROUP_LIST_MEMBER_Y = 72005;
	public static final int GROUP_LIST_MEMBER_N = 73005;
	/** 群历史消息 5.6废弃 */
	@Deprecated
	public static final int GROUP_HISTORY_MESSAGE = 71006;
	/** 推送群组消息 */
	public static final int GROUP_PUSH_MESSAGE = 71007;
	/** 发群组消息 **/
	public static final int GROUP_SEND_MESSAGE = 71008;
	public static final int GROUP_SEND_MESSAGE_Y = 72008;
	public static final int GROUP_SEND_MESSAGE_N = 73008;
	/** 踢人 */
	public static final int GROUP_KICK = 71009;
	public static final int GROUP_KICK_Y = 72009;
	public static final int GROUP_KICK_N = 73009;
	/** 推送踢人消息 */
	public static final int GROUP_PUSH_KICK = 71010;
	/** 离开群组group */
	public static final int GROUP_LEAVE = 71011;
	public static final int GROUP_LEAVE_Y = 72011;
	public static final int GROUP_LEAVE_N = 73011;
	/** 退出群组group */
	public static final int GROUP_LOGOUT = 71012;
	/** 重新登录group **/
	public static final int GROUP_RELOGIN = 71013;
	/** 重新分配group **/
	public static final int GROUP_REASSIGN = 71014;
	/** 服务异常group */
	public static final int GROUP_SERVER_EXCEPTION = 71015;
	/** 心跳group */
	public static final int GROUP_HEART = 71016;
	public static final int GROUP_HEART_Y = 72016;
	/** 推送解散群组 */
	public static final int GROUP_PUSH_DISSOLVE = 71017;
	/** 删除群组消息 */
	public static final int GROUP_PUSH_DELETE_MESSAGE = 71018;
	/** 圈子禁言与解禁 */
	public static final int GROUP_PUSH_FORBID_SAY = 71019;
	/** 特别错误说明 **/
	public static final int GROUP_OTHER_PROBLEM = 71020;
	/** 推送同意邂逅消息 **/
	public static final int SESSION_PUSH_MEET = 81046;
	/** 推送圈子历史消息 */
	public static final int GROUP_PUSH_HISTORY = 71027;
	/** 上报圈子已接收最大id */
	public static final int GROUP_REPORT_RECEIVE_MAX_ID = 71028;
	/** 获取圈子历史消息 */
	public static final int GROUP_GET_HISTORY_MESSAGES = 71029;
	/** 获取圈子历史消息_成功 */
	public static final int GROUP_GET_HISTORY_MESSAGES_SUCCESS = 72029;
	/** 获取圈子历史消息_失败 */
	public static final int GROUP_GET_HISTORY_MESSAGES_FAIL = 73029;
	/** 新贴图通知 */
	public static final int SESSION_NEW_FACE_NOTICE = 81047;
	
	/** 需要经过验证码验证 */
	public static final int SESSION_NEED_VERIFY_CODE = 81048;
	/** 推送警告用户的次数 **/
	public static final int SESSION_PUSH_WARN_USER_NUMBER = 81049;
	
	/** 动态消息 **/
	public static final int PUSH_SESSION_DYNAMIC_MESSAGE = 81050;
	
	/** 推送圈通知消息数 5.2废弃 SESSION_PUSH_GROUP_NEW_MEMBER } **/
	@Deprecated
	public static final int PUSH_CIRCLE_MESSAGE_NUMBER = 81051;
	
	/** 圈聊语音发送开始 **/
	public static final int SESSION_GROUP_AUDIO_BEGIN = 71022;
	/** 圈聊语音发送开始_成功 **/
	public static final int SESSION_GROUP_AUDIO_BEGIN_SUCCESS = 72022;
	/** 圈聊语音发送开始_失败 **/
	public static final int SESSION_GROUP_AUDIO_BEGIN_FAIL = 73022;
	/** 圈聊语音发送中 **/
	public static final int SESSION_GROUP_AUDIO_SEND = 71023;
	/** 圈聊语音发送中_成功 **/
	public static final int SESSION_GROUP_AUDIO_SEND_SUCCESS = 72023;
	/** 圈聊语音发送中_失败 **/
	public static final int SESSION_GROUP_AUDIO_SEND_FAIL = 73023;
	/** 圈聊语音发送结束 **/
	public static final int SESSION_GROUP_AUDIO_END = 71024;
	/** 圈聊语音发送结束_成功 **/
	public static final int SESSION_GROUP_AUDIO_END_SUCCESS = 72024;
	/** 圈聊语音发送结束_失败 **/
	public static final int SESSION_GROUP_AUDIO_END_FAIL = 73024;
	/** 圈聊语音发送取消 **/
	public static final int SESSION_GROUP_AUDIO_CANCEL = 71025;
	/** 圈聊语音发送取消_成功 **/
	public static final int SESSION_GROUP_AUDIO_CANCEL_SUCCESS = 72025;
	/** 圈聊语音发送取消_失败 **/
	public static final int SESSION_GROUP_AUDIO_CANCEL_FAIL = 73025;
	
	/** 广告下架推送 5.2 **/
	public static final int SESSION_AD_OUT_OF_STOCK = 81058;
	/** 更新国家地区和语言 5.4 **/
	public static final int SESSION_UPDATE_COUNTRY_AND_LANGUAGE = 81057;
	/** 推送搭讪关系 5.4 **/
	public static final int SESSION_PUSH_ACCOST_RELATION = 81061;
	/** 用户收到搭讪关系推送上报 5.4 **/
	public static final int SESSION_REPORT_RECEIVE_ACCOST_RELATION = 81062;
	/** 获取用户搭讪关系 5.4 **/
	public static final int SESSION_GET_ACCOST_RELATION = 81063;
	/** 获取用户搭讪关系成功 5.4 **/
	public static final int SESSION_GET_ACCOST_RELATION_SUCCESS = 82063;
	/** 获取用户搭讪关系失败 5.4 **/
	public static final int SESSION_GET_ACCOST_RELATION_FAIL = 83063;
	
	
	/** 推送用户未读动态 5.5 **/
	public static final int SESSION_PUSH_USER_UNREAD_DYNAMIC = 81064;
	
	/** 5.6 **/
	public static final int GROUP_PUSH_NO_READ_DYNAMIC_MESSAGES = 71026;
	
	/** 推送用户侃啦未读消息 5.7 **/
	public static final int SESSION_PUSH_USER_POSTBAR_UNREAD_MESSAGES = 81070;
	
	/** 5.7 推送我的圈话题被赞或评论数量 */
	public static final int SESSION_PUSH_GROUP_TOPIC_UNREAD = 81071;
	
	
	/***************** 6.0 ********************/
	
	/** 推送圈通知 */
	public static final int SESSION_PUSH_GROUP_NOTICE_MESSAGE = 81072;
	
	/** 上报已读最大圈通知时间 */
	public static final int SESSION_SEND_GROUP_NOTICE_LATEST_TIME = 81073;// 其返回id为82073和83073，因为不反馈给用户所以不作处理
	
	/** 推送新增任务和每日签到状态 */
	public static final int SESSION_PUSH_NEW_TASK_AND_SIGN_STATUS = 81074;
	
	/**** 推送充值结果 ***********/
	public static final int SESSION_PUHS_PAY_RESULT = 81075;
	
	/**** 推送会员到期 ***********/
	public static final int SESSION_PUHS_VIP_EXPIRE = 81076;
	
	/**** 推送新的积分产生 ***********/
	public static final int SESSION_PUHS_NEW_CREDITS = 81077;

	/**** 推送聊吧吧金池增加金币的通知 ***********/
	public static final int SESSION_PUHS_CHATBAR_NEW_GLOD = 81084;

	/***************** 6.4 ********************/

	/**** 登陆chatbar服务器 ***********/
	public static final int CHATBAR_LOGIN = 91001;
	public static final int CHATBAR_LOGIN_Y = 92001;
	public static final int CHATBAR_LOGIN_N = 93001;

	/**** 进入chatbar房间 ***********/
	public static final int CHATBAR_ENTER_ROOM = 91002;
	public static final int CHATBAR_ENTER_ROOM_Y = 92002;
	public static final int CHATBAR_ENTER_ROOM_N = 93002;

	/**** 公聊区发送聊吧消息 ***********/
	public static final int CHATBAR_PUBCHAT_SEND_MSG = 91003;
	public static final int CHATBAR_PUBCHAT_SEND_MSG_Y = 92003;
	public static final int CHATBAR_PUBCHAT_SEND_MSG_N = 93003;

	/**** 语音区语音发送开始 ***********/
	public static final int CHATBAR_AUDIO_SEND_BEGIN = 91004;
	public static final int CHATBAR_AUDIO_SEND_BEGIN_Y = 92004;
	public static final int CHATBAR_AUDIO_SEND_BEGIN_N = 93004;

	/**** 语音区语音发送中 ***********/
	public static final int CHATBAR_AUDIO_SENDING = 91005;
	public static final int CHATBAR_AUDIO_SENDING_Y = 92005;
	public static final int CHATBAR_AUDIO_SENDING_N = 93005;

	/**** 语音区语音发送结束 ***********/
	public static final int CHATBAR_AUDIO_SEND_END = 91006;
	public static final int CHATBAR_AUDIO_SEND_END_Y = 92006;
	public static final int CHATBAR_AUDIO_SEND_END_N = 93006;

	/**** 语音区语音废弃 ***********/
	public static final int CHATBAR_AUDIO_SEND_CANCEL = 91007;
	public static final int CHATBAR_AUDIO_SEND_CANCEL_Y = 92007;
	public static final int CHATBAR_AUDIO_SEND_CANCEL_X = 93007;

	/**** 聊吧消息推送 ***********/
	public static final int CHATBAR_ROOM_PUSH_MSG = 91008;

	/**** 退出chatbar服务器 ***********/
	public static final int CHATBAR_QUIT = 91009;
	public static final int CHATBAR_QUIT_Y = 92009;

	/**** 退出chatbar房间 ***********/
	public static final int CHATBAR_EXIT_ROOM = 91010;
	public static final int CHATBAR_EXIT_ROOM_Y = 92010;

	/**** 获取历史数据 ***********/
	public static final int CHATBAR_GET_HISTORY = 91011;
	public static final int CHATBAR_GET_HISTORY_Y = 92011;
	public static final int CHATBAR_GET_HISTORY_N = 93011;

	/**** chatbar服务器心跳 ***********/
	public static final int CHATBAR_HEARTBEAT = 91012;
	public static final int CHATBAR_HEARTBEAT_Y = 92012;

	/**** 上麦或下麦 ***********/
	public static final int CHATBAR_GET_OR_DROP_MIC = 91013;
	public static final int CHATBAR_GET_OR_DROP_MIC_Y = 92013;
	public static final int CHATBAR_GET_OR_DROP_MIC_N = 93013;

	/**** 操作麦位置 ***********/
	public static final int CHATBAR_MIC_POSITION = 91014;
	public static final int CHATBAR_MIC_POSITION_Y = 92014;
	public static final int CHATBAR_MIC_POSITION_N = 93014;

	/**** 推送在线人数 ***********/
	public static final int CHATBAR_PUSH_ONLINE_COUNT = 91015;

	/**** 推送麦克风位置 ***********/
	public static final int CHATBAR_PUSH_MIC_POSITION = 91016;

	/**** 推送无权访问 ***********/
	public static final int CHATBAR_PUSH_NO_PERMISSION = 91017;

	/**** 未登录chatbarsession ***********/
	public static final int CHATBAR_NO_LOGIN = 91018;

	/**** 推送聊吧未读消息 ***********/
	public static final int CHATBAR_PUSH_UNREAD_MSG = 91019;

	/**** 管理员对用户操作 ***********/
	public static final int CHATBAR_ADMIN_OPER = 91020;
	public static final int CHATBAR_ADMIN_OPER_Y = 92020;
	public static final int CHATBAR_ADMIN_OPER_N = 93020;

	/** 推送聊吧管理员对用户的操作 */
	public static final int CHATBAR_PUSH_ADMIN_OPER = 91021;

	/** 获取用户资料和在聊吧中的角色 */
	public static final int CHATBAR_USER_INFO_AND_ROLE = 91022;
	public static final int CHATBAR_USER_INFO_AND_ROLE_Y = 92022;
	public static final int CHATBAR_USER_INFO_AND_ROLE_N = 93022;

	/** 推送聊吧通知消息 */
	public static final int CHATBAR_PUSH_NOTICE = 81078;

	/** 上报已读聊吧通知时间 */
	public static final int CHATBAR_SEND_NOTICE_MAXTIME = 81079;
	public static final int CHATBAR_SEND_NOTICE_MAXTIME_Y = 82079;
	public static final int CHATBAR_SEND_NOTICE_MAXTIME_N = 83079;

	/** 推送聊吧邀请函 */
	public static final int CHATBAR_PUSH_INVITATION = 81081;

	/** 登陆时推送聊吧技能未读消息 */
	public static final int CHATBAR_PUSH_UNREAD_SKILL = 81085;

	/** 推送聊吧解散消息 */
	public static final int CHATBAR_PUSH_CHATBAR_DISMISS = 91023;

	/** 上报已接收邀请函最大时间 */
//	public static final int CHATBAR_SEND_INVITATION_MAXTIME = 81082;
//	public static final int CHATBAR_SEND_INVITATION_MAXTIME_Y = 82082;
//	public static final int CHATBAR_SEND_INVITATION_MAXTIME_N = 83082;

	/** 推送聊吧招募广告 */
	public static final int CHATBAR_SEND_RECRUIT_RADIO = 81080;

	/** 推送终止委托协议 */
	public static final int CHATBAR_PUSH_END_EMPLOY = 81083;

	/** 推送进入聊吧提示 */
	public static final int CHATBAR_PUSH_WELCOME = 91024;

//	验证成功返回 message id = 911111
	public static final int VERIFY_JUMP_WEB_VIEW = 911110;
	public static final int VERIFY_RESULT = 911111;


	public static final int VERIFY_GEETEST_VIEW = 911115;

	/**   聊吧   **/
	/** 聊吧上麦发送中 **/
	public static final int SESSION_GROUP_ON_MIC_SEND = 71030;
	/** 聊吧上麦成功 **/
	public static final int SESSION_GROUP_ON_MIC_SEND_SUCCESS = 72030;
	/** 聊吧上麦失败 **/
	public static final int SESSION_GROUP_ON_MIC_SEND_FAIL = 73030;

	/** 聊吧告知用户准备上麦（自己报自己不会收）*/
	public static final int SESSION_GROUP_PREPAREONMI_RESULT = 71034;

	/** 聊吧用户反馈上麦失败*/
	public static final int SESSION_GROUP_ON_MIC_ERROR_SEND = 71035;
	/** 聊吧告知管理员用户上麦失败（自己报自己不会收）*/
	public static final int SESSION_GROUP_ON_MIC_ERROR_RESULT = 71036;
	/** 聊吧用户反馈上麦成功*/
	public static final int SESSION_GROUP_ON_MIC_SUCCESS_SEND = 71037;

	/** 聊吧推送上麦消息*/
	public static final int SESSION_GROUP_ON_MIC_RESULT = 71038;

	/** 聊吧下麦发送中 **/
	public static final int SESSION_GROUP_OFF_MIC_SEND = 71031;
	/** 聊吧下麦成功 **/
	public static final int SESSION_GROUP_OFF_MIC_SEND_SUCCESS = 72031;
	/** 聊吧下麦失败 **/
	public static final int SESSION_GROUP_OFF_MIC_SEND_FAIL = 73031;

	/** 聊吧告知用户准备下麦（自己报自己不会收）*/
	public static final int SESSION_GROUP_PREPARE_OFFMIC_RESULT = 71041;
	/** 聊吧用户反馈下麦成功*/
	public static final int SESSION_GROUP_OFF_MIC_SUCCESS_SEND = 71044;
	/** 聊吧推送下麦消息*/
	public static final int SESSION_GROUP_OFF_MIC_RESULT = 71045;

	/** 聊吧用户上线*/
	public static final int SESSION_GROUP_ON_LINE_RESULT = 71032;
	/** 聊吧用户下线*/
	public static final int SESSION_GROUP_OFF_LINERESULT = 71033;

	/** 用户反馈上麦失败*/
	public static final int SESSION_GROUP_ON_MIC_FEEDBACK_SUCCESS_RESULT = 73035;
	/** 用户反馈上麦成功*/
	public static final int SESSION_GROUP_ON_MIC_FEEDBACK_ERROR_RESULT = 73037;

	/** 切后台后*/
	public static final int SESSION_GROUP_BACK_CHAT_ROOM_SEND = 71046;

	/** 发送背包或者商店礼物成功*/
	public static final int SESSION_GROUP_SEND_PACAGE_GIFT_SUCCESS = 71047;
		/** 调麦消息*/
	public static final int SESSION_GROUP_UPDATE_MIC_RESULT = 71048;
	/** 私聊- 版本升级*/
	public static final int SESSION_PRIVATE_VERSION_UPDATE = 81078;
	/** 圈聊- 版本升级*/
	public static final int SESSION_GROUP_VERSION_UPDATE = 71049;
	/** 圈聊- 指定用户断开推流*/
	public static final int SESSION_GROUP_STOP_PUSH_RESULT = 71050;

	/**聊吧接收世界消息**/
	public static final int SESSION_GROUP_WORLD_MESSAGE = 81079;
	/**世界消息推送到个人*/
	public static final int SESSION_CHAT_PRESONLA_WORLD_MESSAGE = 81080;
	/***/
	public static final int WORLD_MESSAGE_HISTORY = 82080;
	/**聊吧召唤功能**/
	public static final int SESSION_GROUP_CALL_MESSAGE = 71052;

	/** 上传HUAWEI推送token*/
	public static final int UPLOAD_HUAWEI_TOKEN = 81081;

	/**  关系变更 */
	public static final int SESSION_PRIVATE_CHAT_RELATION = 81082;


}
