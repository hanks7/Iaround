package net.iaround.model.entity;

import net.iaround.ui.datamodel.ResourceBanner;

import java.util.ArrayList;

/**
 * Created by yz on 2018/7/30.
 */

public class GameChatInfo extends BaseEntity {
    public ArrayList<ResourceBanner> topbanners;
    public ArrayList<GameType> game_type;
    public ArrayList<GameInfo> game_info;

    //游戏分类
    public class GameType {
        public long gid;
        public String GameIcon;
        public String name;
    }

    public class GameInfo {
        public int mtype;//1-游戏  2-附近的人 3-聊吧
        public long game_id;
        public String game_name;
        public ArrayList<InfoBean> info;//附近的人和聊吧和游戏主播信息
    }

    //附近的人和聊吧和游戏主播信息
    public class InfoBean {

        public long uid;
        public String name;
        public long lastonlinetime;//最后登陆时间


        //游戏主播播信息
        public long gamer_id;
        public String gamer_url;
        public long online;//在线状态
        public RankInfo rank;//游戏等级
        public float price;
        public String unit;//单位
        public int discount;//折扣

        //附近的人
        public String icon;
        public String gender;//性别 f-女 m-男
        public long distance;//距离
        public String nickname;//最后登陆时间

        //聊吧
        public long groupid;
        public String url;
        public int type;
        public int hot;
        public int family;

    }

    public class RankInfo {
        public int level;
        public String name;
    }


}
