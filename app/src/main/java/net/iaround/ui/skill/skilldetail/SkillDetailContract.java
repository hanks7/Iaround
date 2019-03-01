package net.iaround.ui.skill.skilldetail;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.SkillDetailBean;

/**
 * 作者：zx on 2017/8/10 19:02
 */
public interface SkillDetailContract {

    interface View extends BaseView<Presenter>{
        void updateDetailView(SkillDetailBean skillDetail);

        void updateSkillResult(SkillDetailBean skillDetail);
    }

    abstract class Presenter extends BasePresenter{
        public abstract void getSkillDetail(String skillId);
        public abstract void updateSkill(String skillId, String propId, String currencyType);
    }
}
