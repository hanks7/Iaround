package net.iaround.utils;

import android.os.Build;

import net.iaround.tools.CommonFunction;

/**
 * 判断手机设备厂家类型：华为 魅族 小米
 * Created by liangyuanhuan on 24/10/2017.
 */

public class DeviceUtils {
    public static final String TAG = "DeviceUtils";

    public static boolean isMeizu(){
        //CommonFunction.log(TAG,"MANUFACTURER:" + android.os.Build.MANUFACTURER + ", Build.BRAND="+ Build.BRAND);
        if(android.os.Build.MANUFACTURER!=null && android.os.Build.MANUFACTURER.toUpperCase().contains("MEIZU")){
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("MEIZU");
    }

    public static boolean isXiaomi(){
        //CommonFunction.log(TAG,"MANUFACTURER:" + android.os.Build.MANUFACTURER + ", Build.BRAND="+ Build.BRAND);
        if(android.os.Build.MANUFACTURER!=null && android.os.Build.MANUFACTURER.toUpperCase().contains("XIAOMI")){
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("XIAOMI");
    }

    public static boolean isHuawei(){
        //CommonFunction.log(TAG,"MANUFACTURER:" + android.os.Build.MANUFACTURER + ", Build.BRAND="+ Build.BRAND);
        if(android.os.Build.MANUFACTURER!=null && android.os.Build.MANUFACTURER.toUpperCase().contains("HUAWEI")){
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("HUAWEI");
    }


    public static boolean isOPPO(){
        CommonFunction.log(TAG,"MANUFACTURER:" + android.os.Build.MANUFACTURER + ", Build.BRAND="+ Build.BRAND);
        if(android.os.Build.MANUFACTURER!=null && android.os.Build.MANUFACTURER.toUpperCase().contains("OPPO")){
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("OPPO");
    }

    /*手机厂家
    * */
    public static String getManufacturer(){
        return android.os.Build.MANUFACTURER;
    }

    /*手机型号
    * */
    public static String getModel(){
        return android.os.Build.MODEL;
    }
}
