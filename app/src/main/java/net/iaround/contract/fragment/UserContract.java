package net.iaround.contract.fragment;

import net.iaround.presenter.BasePresenter;
import net.iaround.ui.view.BaseView;

/**
 * @author：liush on 2016/12/1 14:45
 */
public interface UserContract {

    interface View extends BaseView<Presenter> {

        void setUserHead(String url);
        void setUserNickname(String nickname);
        void setVipStatus(int status);
        void setPhoneAhthen(int status);
        void setFriendsAction(int num);
        void setVisitor(int num);
        void setContact(int num);
        void showDialog();
        void hideDialog();
        void setUserTitle();
    }

    interface Presenter extends BasePresenter {

    }
}
