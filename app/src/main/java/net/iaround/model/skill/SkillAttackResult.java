package net.iaround.model.skill;

import net.iaround.model.entity.Item;
import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * 作者：zx on 2017/8/23 14:40
 */
public class SkillAttackResult extends BaseServerBean {

    /**
     * user : {"UserID":61001617,"NickName":"八两","ICON":"http://p1.dev.iaround.com/201707/26/FACE/401ea864b7e9d01875cd576f5c4a91f2_s.jpg","VIP":1,"Notes":"","VipLevel":1,"Age":18,"Gender":"m"}
     * targetUser : {"UserID":61001273,"NickName":"时间从来不","ICON":"http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg","VIP":0,"Notes":"","VipLevel":0,"Age":33,"Gender":"m"}
     * userSkillLevel : 4
     * targetSkillLevel : 1
     * isHit : 1
     * skillId : 3
     * skillName : 保护费
     * skillIcon :
     * gif : protect_2000_29_first
     * groupId : 103
     * groupName : 酒色财气
     * charm : 0
     * goldNum :
     * diamondNum : 99998689
     * affectTime : 1
     */

    public UserBean user;
    public TargetUserBean targetUser;
    public int userSkillLevel;
    public int targetSkillLevel;
    public int isHit;
    public int skillId;
    public String skillName;
    public String skillIcon;
    public String gif;
    public int groupId;
    public String groupName;
    public int charm;
    public int goldNum;
    public int affectGoldNum;       // 标注偷取用户金币
    public int affectExp;           // 标注用户经验变化
    public int diamondNum;
    public int starNum;
    public int affectTime;
    public PropsNum propsNum;
    public int isOpen;              //判断被攻击用户是否开启技能  0 未开启， 1 已开启

    public static class PropsNum {
        public String id;
        public String icon;
        public String name;
        public int num;
        public int needNum;
    }


    public static class UserBean {
        /**
         * UserID : 61001617
         * NickName : 八两
         * ICON : http://p1.dev.iaround.com/201707/26/FACE/401ea864b7e9d01875cd576f5c4a91f2_s.jpg
         * VIP : 1
         * Notes :
         * VipLevel : 1
         * Age : 18
         * Gender : m
         */

        public int UserID;
        public String NickName;
        public String ICON;
        public int VIP;
        public String Notes;
        public int VipLevel;
        public int Age;
        public String Gender;
        public Item rankItem;
    }

    public static class TargetUserBean {
        /**
         * UserID : 61001273
         * NickName : 时间从来不
         * ICON : http://p1.dev.iaround.com/201706/15/FACE/4fb344e753eb06bb714d876542a7c15c_s.jpg
         * VIP : 0
         * Notes :
         * VipLevel : 0
         * Age : 33
         * Gender : m
         */

        public int UserID;
        public String NickName;
        public String ICON;
        public int VIP;
        public String Notes;
        public int VipLevel;
        public int Age;
        public String Gender;


    }

}
