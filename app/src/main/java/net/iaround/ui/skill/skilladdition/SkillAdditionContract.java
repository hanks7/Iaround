package net.iaround.ui.skill.skilladdition;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.SkillAdditionBean;

/**
 * 作者：zx on 2017/8/26 11:35
 */
public interface SkillAdditionContract {

    interface View extends BaseView<Presenter>{

        void showAdditionInfo(SkillAdditionBean skillAddition);
    }

    abstract class Presenter extends BasePresenter{

        public abstract void getSkillAddition();
    }
}
