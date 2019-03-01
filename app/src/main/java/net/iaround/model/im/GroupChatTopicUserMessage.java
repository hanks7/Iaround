package net.iaround.model.im;

/**
 * Class: 上麦消息、下麦消息、上线用户消息、下线用户消息
 * Author：gh
 * Date: 2017/6/15 20:36
 * Email：jt_gaohang@163.com
 */
public class GroupChatTopicUserMessage {
    public long groupid;
    //上线、下线
    public GroupUser user;

    //上麦、下麦
    public GroupUser micUser;
    public GroupUser manager;
    // 上麦位置
    public int slot;

    //版本标示
    public int old;

}
