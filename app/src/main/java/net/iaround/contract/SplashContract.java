package net.iaround.contract;


import android.content.Context;

import net.iaround.presenter.BasePresenter;
import net.iaround.presenter.SplashPresenter;
import net.iaround.ui.view.BaseView;

/**
 * Class: 契约-启动类
 * Author：gh
 * Date: 2016/11/28 11:34
 * Emial：jt_gaohang@163.com
 */
public interface SplashContract {

    interface View extends BaseView<Presenter> {

        void initView();
    }

    interface Presenter extends BasePresenter {

        void initDB(Context mContext);

        SplashPresenter.AdvertClickNotify getAdvertInstance(Context mContext);

        SplashPresenter.DownLoadApk getDownLoadApk(Context mContext);

    }

}
