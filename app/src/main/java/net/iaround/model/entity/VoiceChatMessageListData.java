package net.iaround.model.entity;

import java.util.List;

public class VoiceChatMessageListData extends BaseEntity{
    public int pageno;//	int	当前页码
    public int pagesize;//	int	每页条数
    public int count;//     int	总数
    public List<VoiceChatMessageListItemData> chatlist;
}
