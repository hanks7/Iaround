package net.iaround.contract;


import android.content.Context;

import net.iaround.connector.HttpCallBack;
import net.iaround.presenter.BasePresenter;
import net.iaround.ui.view.BaseView;


/**
 * Class: 契约-注册类
 * Author：gh
 * Date: 2016/11/28 11:34
 * Emial：jt_gaohang@163.com
 */
public interface RegisterContract {

    interface View extends BaseView<Presenter> {

        void initView();

        void setMonitor();
    }

    interface Presenter extends BasePresenter {

        void registerConext(Context mContext);

//        void requestNext(int accounttype , String account,String token, String password,String nickName , int gender , String msgcode , String birthday ,
//                         String icon, HttpStringCallback callback);
//
//        void requestMsgCode( Context context ,String phone, HttpStringCallback callback );

        long userProfile(Context context , HttpCallBack callback);

        void chatInit(Context context,int i);

    }

}
