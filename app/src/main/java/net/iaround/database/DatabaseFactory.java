package net.iaround.database;


import android.content.Context;


/**
 * 数据表操作实例生成器
 *
 * @author linyg
 */
public class DatabaseFactory {
    private static MessageWorker mWorker;
    private static PersonalMessageWorker cWorker;
    private static NearContactWorker nWorker;
    private static KeyWordWorker kWorker;
    private static GroupContactWorker gWorker;
    private static GroupMessageWorker gmWorker;
    //	private static DynamicWorker dWorker;
    private static GroupNoticeWorker gnWorker;
    	private static NewFansWorker nfWorker;
//	private static ChatBarNoticeWorker cbnWorker;
    private static DynamicWorker dWorker;
    private static GroupHistoryWorker ghWorker;
    private static VideoChatWorker vcWorker;

    /**
     * 获取消息的数据库操作
     *
     * @param context
     * @return MessageWorker
     */
    public static MessageWorker getMessageWorker(Context context) {
        if (mWorker == null) {
            mWorker = new MessageWorker(context);
        }
        return mWorker;
    }

    /**
     * 私聊信息操作
     *
     * @param context
     * @return PersonalMessageWorker
     */
    public static PersonalMessageWorker getChatMessageWorker(Context context) {
        if (cWorker == null) {
            cWorker = new PersonalMessageWorker(context);
        }
        return cWorker;
    }

    /**
     * 最近联系人
     *
     * @param context
     * @return NearContactWorker
     */
    public static NearContactWorker getNearContactWorker(Context context) {
        if (nWorker == null) {
            nWorker = new NearContactWorker(context);
        }
        return nWorker;
    }

    /**
     * 聊天列表，圈子
     *
     * @param context
     * @return GroupContactWorker
     */
    public static GroupContactWorker getGroupContactWorker(Context context) {
        if (gWorker == null) {
            gWorker = new GroupContactWorker(context);
        }
        return gWorker;
    }

    /**
     * 关键字
     *
     * @param context
     * @return KeyWordWorker
     */
    public static KeyWordWorker getKeyWordWorker(Context context) {
        if (kWorker == null) {
            kWorker = new KeyWordWorker(context);
        }
        return kWorker;
    }

    /**
     * 群组消息列表
     *
     * @param context
     * @return GroupMessageWorker
     */
    public static GroupMessageWorker getGroupMessageWorker(Context context) {
        if (gmWorker == null) {
            gmWorker = new GroupMessageWorker(context);
        }
        return gmWorker;
    }

    /**
     * 未回调提交的订单信息
     *
     * @param context
     * @return
     */
//	public static PayOrderWorker getPayOrderWorker( Context context )
//	{
//		return new PayOrderWorker( context );
//	}

    /**
     * 草稿箱
     *
     * @param context
     * @return
     */
//	public static DraftsWorker getDraftsWorker( Context context )
//	{
//		return new DraftsWorker( context );
//	}

    /**
     * 邂逅游戏
     *
     * @param context
     * @return
     */
	public static MeetGameWorker getMeetGameWorker( Context context )
	{
		return new MeetGameWorker( context );
	}

    /**
     * 聊天场景
     *
     * @param context
     * @return
     */
    public static ChatThemeWorker getChatThemeWorker(Context context) {
        return new ChatThemeWorker(context);
    }

    /**
     * 获取动态表操作
     */
//	public static DynamicWorker getDynamicWorker( Context context )
//	{
//		if ( dWorker == null )
//		{
//			dWorker = new DynamicWorker( context );
//		}
//		return dWorker;
//	}

    /**
     * 获取圈通知消息表
     */
    public static GroupNoticeWorker getGroupNoticeWoker(Context context) {
        if (gnWorker == null)
            gnWorker = new GroupNoticeWorker(context);
        return gnWorker;
    }

    /**
     * 获取新增粉丝表
     */
	public static NewFansWorker getNewFriendWorker( Context context )
	{
		if ( nfWorker == null )
			nfWorker = new NewFansWorker( context );
		return nfWorker;
	}

    /**
     * 获取新增粉丝表
     */
//	public static ChatBarNoticeWorker getChatBarNoticeWorker( Context context )
//	{
//		if ( cbnWorker == null )
//			cbnWorker = new ChatBarNoticeWorker( context );
//		return cbnWorker;
//	}

    /**
     * 未回调提交的订单信息
     *
     * @param context
     * @return
     */
    public static PayOrderWorker getPayOrderWorker(Context context) {
        return new PayOrderWorker(context);
    }

    /**
     * 获取动态表操作
     */
    public static DynamicWorker getDynamicWorker(Context context) {
        if (dWorker == null) {
            dWorker = new DynamicWorker(context);
        }
        return dWorker;
    }

    /**
     * 聊吧浏览记录
     *
     * @param context
     * @return
     */
    public static GroupHistoryWorker getGroupHistoryWorker(Context context) {
        if (ghWorker == null){
            ghWorker = new GroupHistoryWorker(context);
        }
        return ghWorker;
    }

    /**
     * 视频会话记录
     *
     * @param context
     * @return
     */
    public static VideoChatWorker getVideoChatWorker(Context context) {
        if (vcWorker == null){
            vcWorker = new VideoChatWorker(context);
        }
        return vcWorker;
    }



}
