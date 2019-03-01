package net.iaround.connector.protocol;

import android.content.Context;
import android.text.TextUtils;

import net.iaround.conf.Config;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.PhoneInfoUtil;
import net.iaround.tools.SharedPreferenceUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Class: 系统初始化接口
 * Author：gh
 * Date: 2016/12/2 16:09
 * Emial：jt_gaohang@163.com
 */
public class InitHttpProtocol {

    private static String INIT = "/v1/system/load";

    public static long post(Context context , String url ,
                            LinkedHashMap< String, Object > map , HttpCallBack callback )
    {
       return ConnectorManage.getInstance( context ).asynPost( url , map ,
                ConnectorManage.HTTP_LOGIN , callback );
    }

    /**
     * 遇见初始化
     *
     * @param context
     * @return
     */
    public static long doInit( Context context, HttpCallBack callback )
    {

        PhoneInfoUtil phone = PhoneInfoUtil.getInstance( context );
        String deviceID = phone.getDeviceId( );
        int screenPixHeight = CommonFunction.getScreenPixHeight( context );
        int screenPixWidth = CommonFunction.getScreenPixWidth( context );
        String dpi = screenPixWidth + "*" + screenPixHeight;
        boolean isChina = PhoneInfoUtil.getInstance(context).isChinaCarrier();
        String area = isChina ? "1" : "2";
        String giftLastUpdate = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.GIFT_LAST_UPDATE);
        if (TextUtils.isEmpty(giftLastUpdate)){
            giftLastUpdate = "0";
        }
        LinkedHashMap< String , Object > entity = new LinkedHashMap< String , Object >( );
        entity.put( "giftLastUpdate" , giftLastUpdate);
        entity.put( "logincode" , CryptoUtil.SHA1( deviceID ) );
        entity.put( "dpi" , dpi );
        entity.put( "plat" , Config.PLAT );
        entity.put( "appversion" , Config.APP_VERSION );
        entity.put( "packageid" , CommonFunction.getPackageMetaData( context ) );
        entity.put( "area" , area );
       return post( context , INIT , entity , callback );

    }


}
