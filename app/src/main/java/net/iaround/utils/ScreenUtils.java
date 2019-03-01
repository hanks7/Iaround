package net.iaround.utils;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import net.iaround.BaseApplication;

/**
 *  屏幕工具
 * Author：yuchao
 * Date: 2017/3/27 15:30
 * Email：15369302822@163.com
 */
public class ScreenUtils {

    public static Point getScreenSize(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point out = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            display.getSize(out);
        }else{
            int width = display.getWidth();
            int height = display.getHeight();
            out.set(width, height);
        }
        return out;
    }

    public static int dp2px(int value) {
        float density = BaseApplication.appContext.getResources().getDisplayMetrics().density;
        return (int) (value * density + 0.5f);
    }
}
