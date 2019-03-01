package net.iaround.connector.protocol;


import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;

import java.util.LinkedHashMap;

/**
 * @author：liush on 2016/12/21 20:50
 */
public class VerifyPasswordProtocol {

    public static final String checkPassword = "/user/checkPassword";

    public static long verifyPassword(Context mContext, String password, HttpCallBack callback){
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("password", password);
        return ConnectorManage.getInstance(mContext).asynPost(checkPassword, params, ConnectorManage.HTTP_LOGIN, callback);
    }
}
