package net.iaround.model.skill;

import java.util.List;

/**
 * 作者：zx on 2017/8/18 11:04
 */
public class SkillDetailBean {
    /**
     * gold : 10000
     * goldExpend : 6000
     * diamond : 199999998
     * diamondExpend : 20
     * diamondSuccessRate : 1000
     * skill_icon :
     * skill_name : 保护费
     * skill_level : 1
     * currentEffect : 主要效果:使对方魅力-0.1%(额外-10)，最高-1500
     * updateEffect : 主要效果:使对方魅力-0.15%(额外-20)，最高-1554
     * currentMastery : 0
     * MAXmastery : 0
     * baseSuccessRate : 9900
     * RankingSuccessRate : 0
     * userLevel : 100
     * updateStatus : 0
     * propsList : [{"propIcon":"","propName":"银星星","propExpend":1,"propNum":1,"updateSuccessRate":1500},{"propIcon":"","propName":"金星星","propExpend":1,"propNum":0,"updateSuccessRate":0}]
     * status : 200
     */
    public int gold;//金子
    public int goldExpend;//升级金币消耗
    public int diamond;//钻石
    public int diamondExpend;//升级钻石消耗
    public int diamondSuccessRate;//钻石增加的成功率
    public int star;//星星
    public int starExpend;//升级星星消耗
    public int starSuccessRate;//消耗星星成功率
    public String skill_icon;//技能icon
    public String skill_gif;//技能icon
    public String skill_name;
    public int skill_level;
    public String currentEffect;//当前效果
    public String updateEffect;//升级效果
    public int currentMastery;//当前的熟练度
    public int MAXmastery;//当前段位总的熟练度
    public int baseSuccessRate;//升级基础成功率
    public int RankingSuccessRate;//排行榜成功率
    public int skillSuccessRate;//技能成功率
    public int userLevel;//用户等级
    public int updateStatus;//0无升级状态  1,升级失败返回状态, 2升级成功返回状态
    public List<PropsListBean> propsList;//道具金月亮和银月亮

    public static class PropsListBean {
        /**
         * "propID": 1,
         * propIcon :
         * propName : 银星星
         * propExpend : 1
         * propNum : 1
         * updateSuccessRate : 1500
         */

        public String propID;
        public String propIcon;
        public String propName;
        public int propExpend;//升级道具消耗
        public int propNum;//拥有道具总数
        public int updateSuccessRate;//道具添加的成功率
    }

}
