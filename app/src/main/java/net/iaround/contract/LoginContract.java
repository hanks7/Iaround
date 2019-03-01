package net.iaround.contract;


import android.content.Context;

import net.iaround.connector.HttpCallBack;
import net.iaround.presenter.BasePresenter;
import net.iaround.ui.view.BaseView;

/**
 * Class: 契约-登录类
 * Author：gh
 * Date: 2016/11/28 11:34
 * Emial：jt_gaohang@163.com
 */
public interface LoginContract {

    interface View extends BaseView<Presenter> {

        void initView();

        void setMonitor();

    }

    interface Presenter extends BasePresenter {

        long doLogin(Context context, String account, String password, int accountType, HttpCallBack callback, String unionid);

        void chatInit(Context context,int i);

        long userProfile(Context context, HttpCallBack callback);
    }

}
