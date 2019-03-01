package net.iaround.model.chat;

import net.iaround.model.im.BaseServerBean;
import net.iaround.model.ranking.WorldMessageContentEntity;

import java.util.List;

/**
 * 作者：zx on 2017/8/28 10:42
 */
public class BaseWorldMessageBean extends BaseServerBean{

    public int time;
    public int recruit;
    public String content;
    public int type;
    public UserBean user;
    public List<WorldMessageContentEntity.RankingBean> rank;

    public static class UserBean {
        /**
         * UserID : 61001627
         * VipLevel : 0
         * Gender : m
         * VIP : 0
         * NickName : 布达拉宫
         * ICON : http://p1.dev.iaround.com/201708/17/FACE/f0977cd7743d1406f188d0cc9a8ac2b1_s.jpg
         * Age : 19
         * Notes :
         */
        public int UserID;
        public int VipLevel;
        public String Gender;
        public int VIP;
        public String NickName;
        public String ICON;
        public int Age;
        public String Notes;
    }

}
