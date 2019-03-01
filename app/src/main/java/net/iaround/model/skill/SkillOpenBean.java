package net.iaround.model.skill;

import net.iaround.model.im.BaseServerBean;

/**
 * 作者：zx on 2017/8/22 14:56
 */
public class SkillOpenBean extends BaseServerBean{

    public SkillOpenItem skill;

    public static class SkillOpenItem{
        public String ID;
        public int Level;
        public int Status;
    }
}
