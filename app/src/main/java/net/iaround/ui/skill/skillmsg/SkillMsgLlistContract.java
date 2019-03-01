package net.iaround.ui.skill.skillmsg;


import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.skill.SkillAttackResult;

import java.util.ArrayList;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/25 10:21
 * Email：15369302822@163.com
 */
public interface SkillMsgLlistContract {

    interface View extends BaseView<SkillMsgLlistContract.Presenter> {
        void updateLastSkillMessage(SkillAttackResult result);
        void updateHistoryMessage(ArrayList<SkillAttackResult> recordList, long timeStamp);
        void refreshCompleted();
    }

    abstract class Presenter extends BasePresenter {
        public abstract void setSocketCallback();
        public abstract void clearSocketCallback();
        public abstract void getSkillMessageHistory(String groupId, long timeStamp);
    }

}
