package net.iaround.utils.eventbus;

import net.iaround.model.skill.SkillOpenBean;

/**
 * Class: 技能开启状态
 * Author：gh
 * Date: 2017/10/20 16:27
 * Email：jt_gaohang@163.com
 */
public class SkillOpenEvent {

    private SkillOpenBean skillBean;

    public SkillOpenEvent(SkillOpenBean skillBean) {
        this.skillBean = skillBean;
    }

    public SkillOpenBean getSkillBean() {
        return skillBean;
    }

    public void setSkillBean(SkillOpenBean skillBean) {
        this.skillBean = skillBean;
    }
}
