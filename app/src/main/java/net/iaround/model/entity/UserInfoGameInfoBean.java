package net.iaround.model.entity;

import java.io.Serializable;

/**
 * Created by yz on 2018/8/22.
 */

public class UserInfoGameInfoBean extends BaseEntity implements Serializable{
    public long uid;
    public int game_id;
    public String game_type_name;
    public String game_type_rank;//排行等级对应的称号
    public int game_level;//排行等级
    public int order_num;//接单次数
    public String tag;//标签
    public float star;
    public String image;
    public double price;
    public String Unit;//单位
}
