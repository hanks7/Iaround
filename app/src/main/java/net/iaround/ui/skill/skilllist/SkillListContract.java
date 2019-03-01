package net.iaround.ui.skill.skilllist;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.SkillBeanBase;
import net.iaround.model.skill.SkillOpenBean;

/**
 * 作者：zx on 2017/8/9 14:59
 */
public interface SkillListContract {

    interface View extends BaseView<Presenter>{
        void updateListView(SkillBeanBase skillBeanBase);

        void updateSkillOpenStatus(SkillOpenBean.SkillOpenItem skill);
    }

    abstract class Presenter extends BasePresenter{
        public abstract void getSkillList();

        public abstract void openSkill(String skillId);
    }


}
