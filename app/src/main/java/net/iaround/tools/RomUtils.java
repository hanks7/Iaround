package net.iaround.tools;

import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import net.iaround.conf.Common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 系统标识
 * Created by pyt on 2017/3/27.
 */

public class RomUtils {

    private static final String TAG = "RomUtils";

//    private static String getSystemProperty(String propName) {
//        String line;
//        BufferedReader input = null;
//        try {
//            Process p = Runtime.getRuntime().exec("getprop " + propName);
//            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
//            line = input.readLine();
//            input.close();
//        } catch (IOException ex) {
//            Log.e(TAG, "Unable to read sysprop " + propName, ex);
//            return null;
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    Log.e(TAG, "Exception while closing InputStream", e);
//                }
//            }
//        }
//        return line;
//    }


    /**
     * 判断是否为华为UI
     */
    public static boolean isHuaweiRom() {
//        String manufacturer = Build.MANUFACTURER;
//        return !TextUtils.isEmpty(manufacturer) && manufacturer.contains("HUAWEI");
        CommonFunction.log(TAG, "MAN=" + android.os.Build.MANUFACTURER + ", BAND=" + android.os.Build.BRAND);
        if (android.os.Build.MANUFACTURER != null && android.os.Build.MANUFACTURER.toUpperCase().contains("HUAWEI")) {
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("HUAWEI");
    }

    /**
     * 判断是否为小米UI
     */
    public static boolean isMiuiRom() {
        //return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"));
        if (android.os.Build.MANUFACTURER != null && android.os.Build.MANUFACTURER.toUpperCase().contains("XIAOMI")) {
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("XIAOMI");
    }


    /**
     * "ro.build.user" -> "flyme"
     * "persist.sys.use.flyme.icon" -> "true"
     * "ro.flyme.published" -> "true"
     * "ro.build.display.id" -> "Flyme OS 5.1.2.0U"
     * "ro.meizu.setupwizard.flyme" -> "true"
     * <p>
     * 判断是否为魅族UI
     *
     * @return
     */
    public static boolean isFlymeRom() {
        //return "flyme".equalsIgnoreCase(getSystemProperty("ro.build.user"));
        if (android.os.Build.MANUFACTURER != null && android.os.Build.MANUFACTURER.toUpperCase().contains("MEIZU")) {
            return true;
        }
        return Build.BRAND != null && Build.BRAND.toUpperCase().contains("MEIZU");
    }

}
