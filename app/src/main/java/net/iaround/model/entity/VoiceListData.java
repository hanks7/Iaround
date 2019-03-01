package net.iaround.model.entity;

import java.util.List;

public class VoiceListData extends BaseEntity{

    public int pageno;//	int	当前页码
    public int pagesize;//	int	每页条数
    public int gametype;//	int	游戏类型
    public int count;//     int	总数
    public List<VoiceListItemData> list;
}
