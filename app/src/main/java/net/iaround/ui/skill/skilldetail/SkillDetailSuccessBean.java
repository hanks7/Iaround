package net.iaround.ui.skill.skilldetail;

/**
 *
 * 技能成功率bean
 * Created by gh on 2017/11/9.
 */

public class SkillDetailSuccessBean {


    public int type;// 1-基础，2-道具，3-钻石，4-排行，5-充值，6-星星
    public int success;

    public SkillDetailSuccessBean(int type, int success) {
        this.type = type;
        this.success = success;
    }
}
