package net.iaround.model.im;

/**
 * Class: 切换麦位
 * Author：gh
 * Date: 2017/7/18 16:48
 * Email：jt_gaohang@163.com
 */
public class GroupChatUpdateMicMessage {

    /** 聊吧ID */
    public long groupid;
    /** 目标麦位*/
    public int dest;
    /** 当前麦位*/
    public int src;
    /** 麦位用户*/
    public GroupUser micUser;
}
