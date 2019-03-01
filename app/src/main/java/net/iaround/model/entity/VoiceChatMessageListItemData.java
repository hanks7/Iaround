package net.iaround.model.entity;

public class VoiceChatMessageListItemData {

    public long userid;//	int	用户id
    public String nickname;//	string	昵称
    public String icon;//	string	头像
    public int state;//	int	通话状态 1:拨打时间  2：未接通
    public long chattime;//	string	拨打时间
    public long seconds;//通话秒数
    public int vip;//会员
    public int svip;//会员
    public long voiceRoomListID;//语音ID
    public int isanchor;//是否是主播 0普通，1主播

}
