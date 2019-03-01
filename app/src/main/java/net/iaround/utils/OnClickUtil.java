package net.iaround.utils;

/**
 * 防止用户快速点击
 */

public class OnClickUtil {

    private static long lastClickTime;

    public synchronized static boolean isFastClick(){

        long time = System.currentTimeMillis();

        if(time - lastClickTime < 2000){

            return true;
        }

        lastClickTime = time;

        return false;

    }

}
