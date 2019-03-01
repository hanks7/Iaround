package net.iaround.ui.comon;

import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.MenuBadge;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.NewFansModel;

/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: MenuBadgeHandle
 * @Description: 侧栏标示数据的获取
 * @date 2014-2-14 下午2:22:55
 */


public class MenuBadgeHandle {

    private static MenuBadgeHandle mInstance;
    private Context mContext;

    public MenuBadgeHandle(Context mContext) {
        this.mContext = mContext;
    }

    public static MenuBadgeHandle getInstance(Context context) {
        if (mInstance == null || mInstance.mContext == null) {
            mInstance = new MenuBadgeHandle(context);
        }
        return mInstance;
    }

    public static Object[] countNewMessageNum(Context context) {
        long uid = Common.getInstance().loginUser.getUid();
        int newDynamic = 0;
        //私聊未读和收到搭讪
        int countNoRead = ChatPersonalModel.getInstance()
                .countNoReadAndAccostSender(context, String.valueOf(uid));
        //圈通知未读数
        int groupNotice = GroupAffairModel.getInstance().getUnreadCount(context);
        //新增粉丝数
        int newFans = NewFansModel.getInstance().getUnreadCount(context, uid);
        //新增动态数
        DynamicNewNumberBean dynamicNewNumberBean = DynamicModel.getInstent().getNewNumBean();
        if (dynamicNewNumberBean != null) {
            newDynamic = dynamicNewNumberBean.getCommentNum() + dynamicNewNumberBean.getLikenum();
        }
        //接收且提醒的圈子的未读消息数
        int groupCount = GroupModel.getInstance().getReceiveUnreadCount(context);

//		//聊吧通知未读
//		int chatbarNotice = ChatBarAffairModel.getInstant( ).getNoticeUnread( context );
//		//聊吧邀请函未读
//		int chatbarInvitation = ChatBarAffairModel.getInstant( ).getInvitationUnread( context );
//		//我的聊吧未读数
//		int myChatbarUnread = 0;
//		if ( ChatBarAffairModel.getInstant( ).myChatBar != null &&
//			 ChatBarAffairModel.getInstant( ).myChatBar.atMeCount > 0 )
//			myChatbarUnread = ChatBarAffairModel.getInstant( ).myChatBar.atMeCount;
//		//我关注的聊吧未读数
//		int focusChatbarUnread = 0;
//		boolean focusUnread = false;
//		if ( ChatBarAffairModel.getInstant( ).focusChatBars != null )
//		{
//			for ( ChatBarUnreadMsgBean focusChatBar : ChatBarAffairModel
//					.getInstant( ).focusChatBars )
//			{
//				if ( focusChatBar.atMeCount > 0 )
//					focusChatbarUnread += focusChatBar.atMeCount;
//				else if ( focusChatBar.atMeCount == 0 )
//					focusUnread = true;
//			}
//		}

        //聊吧技能未读数
//		int chatbarSkillUnread = ChatBarAffairModel.getInstant( ).latestSkillItem.atMeCount;
//		chatbarSkillUnread = chatbarSkillUnread < 0 ? 0 : chatbarSkillUnread;


        int totalNum = countNoRead + groupNotice + newFans + newDynamic + groupCount;
        Object[] objects = new Object[2];
        objects[0] = totalNum;
//		objects[ 1 ] = focusUnread;
        return objects;
    }

    /**
     * @param menuBadge
     * @return
     * @Title: getMessagesMenuBadge
     * @Description: 获取消息的标示显示
     */
    public MenuBadge getMessagesMenuBadge(MenuBadge menuBadge) {

        Object[] objects = countNewMessageNum(mContext);
        int all = (Integer) objects[0];
//		boolean focusUnread = ( Boolean )objects[ 1 ];

        //接收且提醒的圈子的未读消息数
        int groupCount = GroupModel.getInstance().getReceiveUnreadCount(mContext);

        if (all > 0) {
            menuBadge.badgeNumber = all;
            menuBadge.badgeType = MainFragmentActivity.BADGE_NUMBER;
        } else if (groupCount > 0 /*| focusUnread */) {
            menuBadge.badgeType = MainFragmentActivity.BADGE_ROUND;
        } else {
            menuBadge.badgeType = MainFragmentActivity.BADGE_GONE;
        }
        return menuBadge;
    }

}
