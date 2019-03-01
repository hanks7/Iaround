package net.iaround.model.skill;

import java.util.List;

/**
 * 作者：zx on 2017/8/17 14:26
 */
public class SkillUsedItemBean {
    /**
     * {
     * "skillBackIcon": "",
     * "skillGiftIcon": "protect_1800_25_first",
     * "skillId": 6,
     * "myUserId": 61001616,
     * "otherUserId": 61001616,
     * "mySkillLevel": 5,
     * "otherSkillLevel": 5,
     * "hitRate": "80",
     * "props": [
     * {
     * "id": 10005,			//道具ID
     * "icon": "",
     * "name": "紧箍咒",
     * "num": 0,			//持有道具数量
     * "needNum": 1		//需要道具数量
     * }
     * ],
     * "goldExp": 45000,
     * "diamondExp": 150,
     * "skillDesc": "主要效果:使对方在当前的聊吧禁言2.3分钟"
     * }
     */
    public String skillBackIcon;
    public String skillGiftIcon; //动态图首帧名
    public String skillId;
    public String myUserId;
    public String otherUserId;
    public int mySkillLevel;
    public int otherSkillLevel;
    public int hitRate;
    public int goldExp;
    public int diamondExp;
    public int starExp;
    public String skillDesc;
    public int status;//1已开启  0未开启技能
    public List<Props> props;

    public static class Props {
        public String id;
        public String icon;
        public String name;
        public int num;
        public int needNum;
    }

}
