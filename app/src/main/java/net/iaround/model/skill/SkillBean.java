package net.iaround.model.skill;


/**
 * 作者：zx on 2017/8/9 14:57
 */
public class SkillBean {

    /**
     * ID : 5
     * Name : 紧箍咒
     * Icon : 
     * NeedUserLevel : 0
     * Level : 1
     * Status : 1
     * Desc : 效果：对对方在被惩罚的聊吧禁言，减少对方魅力
     */

    public int ID;
    public String Name;
    public String Icon;
    public String Gif;
    public int NeedUserLevel;
    public int Level;
    public int Status;//0：未开启，1：可升级，2：可开启
    public String Desc;
}
