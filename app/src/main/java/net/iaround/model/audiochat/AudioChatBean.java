package net.iaround.model.audiochat;

/**
 * 语音聊天消息实体类
 */
public class AudioChatBean {
    public long SendUserID; //发起拨打用户id
    public String SendUserNickName; //发送用户昵称
    public String SendUserICON; //发送用户ICON ，url连接
    public long ReceiveUserID; //接收拨打主播id
    public String ReceiveUserNickName; //接收用户NickName
    public String ReceiveUserICON; //接收用户ICON，url连接
    public String RoomID; //房间id
    public double UserSum; //通话中消费的星星（主播可见,分成后的， 单位 元）
    public int TesidueTime; //(计费推送必有，秒) 用户剩余时长大于180s为0，小于则返回相应时长
    public int Status; //状态列表状态
    public String Info; //状态说明
    public long CurrentSendUserID; //当前消息发送用户id
    public long CurrentReceiveUserID; //当前消息接收用户id
    public long SendTime;//消息时间戳
    public int Seconds;//通话时长
    public int Code; //状态：200成功
}
