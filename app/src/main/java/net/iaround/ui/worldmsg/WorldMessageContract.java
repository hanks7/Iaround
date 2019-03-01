package net.iaround.ui.worldmsg;

import net.iaround.manager.mvpbase.BasePresenter;
import net.iaround.manager.mvpbase.BaseView;
import net.iaround.model.chat.WorldMessageRecord;

import java.util.ArrayList;

/**
 * 作者：zx on 2017/8/23 18:42
 */
public interface WorldMessageContract {

    interface View extends BaseView<Presenter>{

        void updateLastMessage(WorldMessageRecord record);

        void updateHistoryMessage(ArrayList<WorldMessageRecord> recordList, long timeStamp);

        void refreshCompleted();
    }

    abstract class Presenter extends BasePresenter{

        public abstract void setSocketCallback();

        public abstract void sendWorldMessage(String groupId, String content);

        public abstract void clearSocketCallback();

        public abstract void getWorldMessageHistory(long timeStamp);

    }
}
