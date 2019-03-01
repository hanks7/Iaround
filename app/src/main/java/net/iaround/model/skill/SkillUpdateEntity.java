package net.iaround.model.skill;

import net.iaround.model.im.BaseServerBean;

import java.util.List;

/**
 * Class:技能升级
 * Author：gh
 * Date: 2017/10/17 11:07
 * Email：jt_gaohang@163.com
 */
public class SkillUpdateEntity extends BaseServerBean {

    public long coin;   //金币
    public long diamond; //钻石
//    public long noticeTime;//以秒为单位
//    public boolean skillUpgradeSwitch;//技能升级开关

    public List<SkillBean> skillUpgrade;   //推荐升级的技能
}
