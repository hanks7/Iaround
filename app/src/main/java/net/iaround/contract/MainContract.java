package net.iaround.contract;

import net.iaround.presenter.BasePresenter;
import net.iaround.ui.view.BaseView;

/**
 * Class: 契约-主界面类
 * Author：gh
 * Date: 2016/11/28 11:34
 * Emial：jt_gaohang@163.com
 */
public interface MainContract {

    interface View extends BaseView<Presenter> {

    }

    interface Presenter extends BasePresenter {


    }


}
