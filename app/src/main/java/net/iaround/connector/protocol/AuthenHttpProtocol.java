package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PhoneInfoUtil;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author：liush on 2016/12/16 15:10
 */
public class AuthenHttpProtocol {

    public static final String authenPicUrl = "/user/auth";
    public static final String authenPhoneUrl = "/user/bind";


    public static long authenPhone(Context context, String verifyCode, String account, String password, HttpCallBack callback){

        LinkedHashMap<String, Object> entity = new LinkedHashMap<>();
        entity.put( "account" , account );
        entity.put( "password" , CryptoUtil.md5( password ) );
        entity.put( "logincode" ,
                CryptoUtil.SHA1( PhoneInfoUtil.getInstance( context ).getDeviceId( ) ) );
        entity.put( "language" , CommonFunction.getLang( context ) );
        return ConnectorManage.getInstance(context).asynPost(authenPhoneUrl,entity,ConnectorManage.HTTP_LOGIN,callback);
    }

}
