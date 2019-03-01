package net.iaround.model.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by yz on 2018/8/7.
 */

public class PlayMatesListBean extends BaseEntity implements Serializable {
    public int pageno;
    public int pagesize;
    public int amount;//总数
    public String gametype;
    public ArrayList<UsersBean> users;
    public GamesListBean gameslist;//标题栏数据
    public ConditionBean conditions;//筛选选项

    public class ConditionBean implements Serializable{
        public ArrayList<String> gender;//性别 f-女， m-男， all-全部
        public ArrayList<String> price;
        public ArrayList<LevelBean> level;
    }

    public class GamesListBean implements Serializable{
        public long GameID;
        public String GameName;
        public String GameIcon;
    }

    public class LevelBean implements Serializable{
        public String gamelevel;//游戏等级
        public String levelName;//游戏等级对应的称号
    }

    public class UsersBean implements Serializable{
        public long userid;
        public String nickname;
        public String gender;
        public String icon;
        public int age;
        public int distance;
        public long lastonlinetime;
        public int level;
        public String levelname;
        public float price;
        public String unit;//单位
        public int discount;//折扣
        public int ordertimes;//接单次数
    }
}
