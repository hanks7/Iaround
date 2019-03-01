package net.iaround.ui.skill.skilluse;

import android.view.KeyEvent;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.skill.SkillOpenBean;
import net.iaround.model.skill.SkillUsedInfoBean;
import net.iaround.model.skill.SkillUsedItemBean;
import net.iaround.ui.view.dialog.DialogInterface;

/**
 * 作者：zx on 2017/8/15 17:43
 */
public interface SkillUseContract {

    interface View extends BaseView<Presenter>{
        void showSkillUsedInfo(SkillUsedInfoBean usedBean);

        void handleAttackResult(SkillAttackResult attackResult);

        void updateSkillOpenStatus(SkillUsedItemBean itemBean, SkillOpenBean.SkillOpenItem skill);

    }


    abstract class Presenter extends BasePresenter{
        public abstract void getSkillUsedInfo(String targetUserId);
        public abstract void skillAttack(String targetUserId, String skillId, String groupId, String currencyType, String propsId);
        public abstract void openSkill(SkillUsedItemBean itemBean);
    }

}
