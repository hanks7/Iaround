package net.iaround.presenter;

import android.content.Context;

import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.LoginHttpProtocol;
import net.iaround.contract.LoginContract;
import net.iaround.tools.CryptoUtil;

/**
 * Class: 业务逻辑
 * Author：gh
 * Date: 2016/11/28 11:49
 * Emial：jt_gaohang@163.com
 */
public class LoginPresenter implements LoginContract.Presenter {
    @Override
    public long doLogin(Context context, String account, String password, int accountType, HttpCallBack callback, String unionid) {
        return LoginHttpProtocol.doLogin(context,account, password ,"",accountType,callback, unionid);
    }

    @Override
    public void chatInit(Context context,int i) {

    }

    @Override
    public long userProfile(Context context , HttpCallBack callback) {
        return LoginHttpProtocol.reProfile(context,callback);
    }
}
