package net.iaround.model.ranking;

import java.io.Serializable;

/**
 * Created by Ray on 2017/6/17.
 */

public class RankingEntityType implements Serializable {
    private static final long serialVersionUID = 7635127122654526974L;
    private int type;
    private int threeType;
    private boolean skill;
    private String skillType;
    private int showType;// 1 本周 2 上周 3 本月 4 上月

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public String getSkillType() {
        return skillType;
    }

    public void setSkillType(String skillType) {
        this.skillType = skillType;
    }

    public boolean isSkill() {
        return skill;
    }

    public void setSkill(boolean skill) {
        this.skill = skill;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    private Object object;
    private Object skillObject;

    public Object getSkillObject() {
        return skillObject;
    }

    public void setSkillObject(Object skillObject) {
        this.skillObject = skillObject;
    }
}
