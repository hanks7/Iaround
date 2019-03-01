
package net.iaround.ui.chat.view;


import android.content.Context;

import net.iaround.tools.CommonFunction;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-14 上午10:41:46
 * @ClassName ChatRecordViewFactory
 * @Description: 统一处理聊天记录-简单工厂模式
 * @linkClass ChatPersonal GroupChatTopicActivity
 */

public class ChatRecordViewFactory {
    // 类型最大总数数量
    public final static int TYPE_COUNT = 153;

    public final static int TYPE_OFFSET = 50;// 判断对方还是自己的偏移

    public final static int TEXT = 1; // 文本
    public final static int IMAGE = 2; // 图片
    public final static int SOUND = 3; // 语音
    public final static int VIDEO = 4; // 录像
    public final static int LOCATION = 5; // 位置
    public final static int GIFE_REMIND = 6; // 礼物提醒


    @Deprecated
    public final static int THEME_GIFT = 7; // 主题礼物 5.7去掉
    public final static int GAME = 8; // 游戏 - 不用的
    public final static int FACE = 9; // 贴图
    public final static int PICTEXT = 10; // 文本 - 不用的
    public final static int ACCOST_GAME_QUE = 11; // 搭讪问题
    public final static int ACCOST_GAME_ANS = 12; // 搭讪回答
    public final static int SHARE = 13; // 分享类型
    public final static int PICSET = 14; // 图组类型
    public final static int CHATBAR_DELEGATION = 16; //聊吧委托找主持人
    public final static int WORLD_SKILL_MSG = 17;//技能消息


    /**
     * 使用技能
     */
    public final static int USER_SKILL_MSG = 15;
    /***邀请聊天，邀请加入**/
    public final static int INVITE_USER_JOIN_CHAT = 18;//邀请聊天------>跳转至聊吧面板
    public final static int INVITE_USER_JOIN_CHATBAR = 19;//邀请加入----->走加入流程
    public final static int CHCAT_VIDEO = 20;//一对一视频

    /**游戏订单*/
    public final static int GAME_ORDER_TIP =21 ; // 订单提醒 用户端
    public final static int GAME_ORDER_SERVER_TIP =22 ; // 订单开始服务提示

    public final static int CHCAT_AUDIO = 23;//一对一语音聊天

    public final static int RECRURITY_MSG = 32;//招募消息


    public final static int FRIEND_TEXT = 51; // 文本
    public final static int FRIEND_IMAGE = 52; // 图片
    public final static int FRIEND_SOUND = 53; // 语音
    public final static int FRIEND_VIDEO = 54; // 录像
    public final static int FRIEND_LOCATION = 55; // 位置
    public final static int FRIEND_GIFE_REMIND = 56; // 礼物提醒
    @Deprecated
    public final static int FRIEND_THEME_GIFT = 57; // 主题礼物 5.7去掉else
    public final static int FRIEND_GAME = 58; // 游戏
    public final static int FRIEND_FACE = 59; // 贴图
    public final static int FRIEND_PICTEXT = 60; // 文本
    public final static int FRIEND_SHARE = 63; // 分享类型
    public final static int FRIEND_PICSET = 64; // 图组类型
    public final static int FRIEND_CHATBAR_DELEGATION = 66; //聊吧委托找主持人
    public final static int FRIEND_USER_SKILL_MSG = 67;//好友技能消息


    public final static int FRIEND_CHCAT_VIDEO = 70;//一对一视频

    public final static int FRIEND_CHCAT_AUDIO = 23 + TYPE_OFFSET;//一对一语音聊天

    public final static int FRIEND_GAME_ORDER_TIP_ANCHOR = 21 + TYPE_OFFSET ; // 订单建立提醒-此订单的游戏主播


    /**
     * Notice View Flag
     **/
    public final static int FORBID = 40; // 禁言提示
    public final static int ERROR_PROMT = 110; // 限制提示
    public final static int GIFT_LIMIT_PROMT = 111; // 礼物限制提示
    public final static int FOLLOW = 41; // 关注提示
    public final static int ACCOST_NOTICE = 42; // 搭讪游戏提示
    public final static int FRIEND_CREDITS_EXCHANGE = 43; //积分兑换提示
    public final static int TIME_LINE = 0; // 时间提示

    public final static int ACCOST_GAME_FIND_NOTICE = 100; // 搭讪游戏-从哪里搭讪
    public final static int ACCOST_GAME_ANS_TEXT = 101; // 搭讪回答-文本_自己的
    public final static int ACCOST_GAME_ANS_IMAGE = 102; // 搭讪回答-图片_自己的

    public final static int FRIEND_ACCOST_GAME_ANS_TEXT = 151; // 搭讪回答-文本_自己的
    public final static int FRIEND_ACCOST_GAME_ANS_IMAGE = 152; // 搭讪回答-图片_自己的


    public final static int WORLD_MESSAGE_LIST_TEXT_BODY = 30;//邀请加入/招募----好友的
    public final static int WORLD_MESSAGE_LIST_GIFT_BODY = 156;//邀请加入/招募----好友的
    public final static int WORLD_MESSAGE_LIST_RECRUIT_BODY = 157;//邀请加入/招募----好友的
    public final static int WORLD_MESSAGE_LIST_SKILL_BODY = 158;//邀请加入/招募----好友的


    /**
     * @param mContext
     * @param type     详情见 {@link ChatRecordViewFactory}
     * @return {@link ChatRecordView }
     * @Description 根据类型获取相应的ChatRecord
     */
    public static ChatRecordView createChatRecordView(Context mContext, int type) {
        ChatRecordView view = null;
        switch (type) {
            case TIME_LINE: {
                view = new TimeRecordView(mContext);
            }
            break;
            case TEXT: {
                view = new MySelfTextRecordView(mContext);
            }
            break;
            case FRIEND_TEXT: {
                view = new FriendTextRecordView(mContext);
            }
            break;
            case IMAGE: {
                view = new MySelfImageRecordView(mContext);
            }
            break;
            case FRIEND_IMAGE: {
                view = new FriendImageRecordView(mContext);
            }
            break;
            case SOUND: {
                view = new MySelfSoundRecordView(mContext);
            }
            break;
            case FRIEND_SOUND: {
                view = new FriendSoundRecordView(mContext);
            }
            break;
            case VIDEO: {
                view = new MySelfVideoRecordView(mContext);
            }
            break;
            case FRIEND_VIDEO: {
                view = new FriendVideoRecordView(mContext);
            }
            break;
            case LOCATION: {
                view = new MySelfLocationRecordView(mContext);
            }
            break;
            case FRIEND_LOCATION: {
                view = new FriendLocationRecordView(mContext);
            }
            break;
            case GIFE_REMIND: {
//				view = new MySelfGiftRecordView( mContext );
                view = new MySelfGiftRecordViewFromChatBar(mContext);
            }
            break;
            case FRIEND_GIFE_REMIND: {
//				view = new FriendGiftRecordView( mContext );
                view = new FriendGiftRecordViewFromChatBar(mContext);
            }
            break;

            case GAME:
            case FRIEND_GAME: {
                view = new FriendGameRecordView(mContext);
            }
            break;
            case FACE: {
                view = new MySelfFaceRecordView(mContext);
            }
            break;
            case FRIEND_FACE: {
                view = new FriendFaceRecordView(mContext);
            }
            break;
            case PICTEXT:
            case FRIEND_PICTEXT: {
                view = new SecretaryPushRecordView(mContext);
            }
            break;
            case FORBID: {
                view = new ForbidRecordView(mContext);
            }
            break;
            case FOLLOW: {
                view = new FollowRecordView(mContext);
            }
            break;
            case ACCOST_NOTICE: {
                view = new FollowRecordView(mContext);
            }
            break;
//			case ACCOST_GAME_QUE :
//			{
//				view = new FriendGameQuestionRecordView( mContext );
//			}
//				break;
//			case ACCOST_GAME_ANS_TEXT :
//			{
//				view = new MySelfGameTextRecordView( mContext );
//			}
//				break;
//			case ACCOST_GAME_ANS_IMAGE :
//			{
//				view = new MySelfGameImageRecordView( mContext );
//			}
//				break;
//			case FRIEND_ACCOST_GAME_ANS_TEXT :
//			{
//				view = new FriendGameTextRecordView( mContext );
//			}
//				break;
//			case FRIEND_ACCOST_GAME_ANS_IMAGE :
//			{
//				view = new FriendGameImageRecordView( mContext );
//			}
//				break;
            case SHARE: {
                view = new MySelfShareRecordView(mContext);
            }
            break;
            case FRIEND_SHARE: {
                view = new FriendShareRecordView(mContext);
            }
            break;
            case PICSET:
            case FRIEND_PICSET: {
                view = new PicsetPictextView(mContext);
            }
            break;
            case FRIEND_CREDITS_EXCHANGE:
                view = new CreditsExchangeRecordView(mContext);
                break;
            case CHATBAR_DELEGATION:
                view = new MySelfDelegationRecordView(mContext);
                break;
            case FRIEND_CHATBAR_DELEGATION:
                view = new FriendDelegationRecordView(mContext);
                break;
            case ERROR_PROMT:
                view = new ErrorPromptRecordView(mContext);
                break;
            case GIFT_LIMIT_PROMT:
                view = new GiftLimitPromptRecordView(mContext);
                break;
            case INVITE_USER_JOIN_CHATBAR://邀请加入聊吧
                view = new InviteUserJoinChatbar(mContext);
                break;
            case INVITE_USER_JOIN_CHAT://邀请加入聊天
                view = new InviteUserJoinChat(mContext);
                break;
            case WORLD_SKILL_MSG://世界技能消息列表
                view = new SkillTextRecordView(mContext);
                break;
            case USER_SKILL_MSG://使用技能/被攻击技能消息
                view = new MySkillMsgChatPersonalView(mContext);
                break;
            case FRIEND_USER_SKILL_MSG://好友使用技能消息
                view = new FriendSkillMsgChatPersonalView(mContext);
                break;
            case WORLD_MESSAGE_LIST_TEXT_BODY:
//				view = new WorldMessageOthersRecordView(mContext);
                break;

            case CHCAT_VIDEO:
                view = new MySelfVideoChatRecordView(mContext);
                break;

            case FRIEND_CHCAT_VIDEO:
                view = new FriendVideoChatRecordView(mContext);
                break;
            case GAME_ORDER_TIP://订单建立提示 用户端
                view = new MySelfTextRecordView(mContext);
                break;
            case GAME_ORDER_SERVER_TIP://订单服务开始提示
                //TODO
                view = new GameOrderTipRecordView(mContext);
                break;

            case FRIEND_GAME_ORDER_TIP_ANCHOR://订单建立提示 主播端
                view = new FriendGameOrderRecordView(mContext);
                break;
            case CHCAT_AUDIO:
                view = new MySelfAudioChatRecordView(mContext);
                break;

            case FRIEND_CHCAT_AUDIO:
                view = new FriendAudioChatRecordView(mContext);
                break;

            default: {
                view = new TimeRecordView(mContext);
                CommonFunction.log("--->ChatRecordViewFactory", "Type Error");
            }
            break;
        }
        return view;
    }

}
