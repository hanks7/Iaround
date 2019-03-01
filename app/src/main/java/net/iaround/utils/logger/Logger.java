package net.iaround.utils.logger;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mars.xlog.Xlog;

import net.iaround.BaseApplication;
import net.iaround.BuildConfig;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;

import java.io.File;

/**
 * 日志模块
 * 输出到两个出口
 * 1）控制台（调试模式下）
 * 2）文件（调试模式下和发布模式，发布模式下只记录异常日志级别的日志 W E）
 *    文件路径     /SD卡根目录/iaround/logger/
 *    文件名     log_1.txt log_2.txt log_3.txt 每个文件最大 200kB 轮流覆盖每个文件
 */

public class Logger {
    private static boolean sInit = false; //Logger是否初始化

    //手动打开的调试开关（在设置->关于遇见->连续点击遇见图标6次）如果关闭，则只记录 warn 和 error级日志到文件；如果打开，则记录所有级别日志到文件
    private static boolean sDebugEnable = false;

    public static void init(boolean mainProcess){
        if(mainProcess) {
            deleteOldFile(); //先删除过期文件

            initXlog(BaseApplication.appContext, "app"); //初始化
        }else{
            initXlog(BaseApplication.appContext, "service");//初始化
        }

        sDebugEnable = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getBoolean(SharedPreferenceUtil.APP_DEBUG_SWITCH,false);
        sInit = true;
    }

    public static void destroy(){
        destroyXlog();
        sInit = false;
    }

    /* 话痨级别
    * */
    public static void v(String tag, String message) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true) {
            com.tencent.mars.xlog.Log.v(tag, message);
        }
    }

    public static void v(String tag, String message, Object... args) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true) {
            com.tencent.mars.xlog.Log.v(tag, message, args);
        }
    }

    /* 调试级别
    * */
    public static void d(String tag, String message) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true) {
            com.tencent.mars.xlog.Log.d(tag, message);
        }
    }

    public static void d(String tag, String message, Object... args) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true ) {
            com.tencent.mars.xlog.Log.d(tag, message, args);
        }
    }

    /* 信息级别
    * */
    public static void i(String tag, String message) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.i(tag, message);
    }

    public static void i(String tag, String message, Object... args) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.i(tag, message, args);
    }

    /* 警告级别
    * */
    public static void w(String tag, String message) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.w(tag, message);
    }

    public static void w(String tag, String message, Object... args) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.w(tag, message, args);
    }

    /* 错误级别
    * */
    public static void e(String tag, String message) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.e(tag, message);
    }

    public static void e(String tag, String message, Object... args) {
        if(sInit == false) return;
        com.tencent.mars.xlog.Log.e(tag,message,args);
    }

    /**
     * 打印 json 格式
     */
    public static void json(String json) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true) {
            com.tencent.mars.xlog.Log.d("JSON", json);
        }
    }

    /**
     * 打印 xml 格式
     */
    public static void xml(String xml) {
        if(sInit == false) return;
        if(true == BuildConfig.DEBUG || sDebugEnable==true) {
            com.tencent.mars.xlog.Log.d("XML", xml);
        }
    }

    /*日志库初始化
    * */
    private static void initXlog(Context context, String prefix){
        System.loadLibrary("stlport_shared");
        System.loadLibrary("marsxlog");

        final String SDCARD = CommonFunction.getSDPath();
        final String logPath = SDCARD + "log";

        // this is necessary, or may cash for SIGBUS
        final String cachePath = context.getFilesDir() + "/log";

        //init xlog
        if (BuildConfig.DEBUG) {
            Xlog.appenderOpen(Xlog.LEVEL_DEBUG, Xlog.AppednerModeAsync, cachePath, logPath, prefix, "");
            Xlog.setConsoleLogOpen(true);
        } else {
            Xlog.appenderOpen(Xlog.LEVEL_INFO, Xlog.AppednerModeAsync, cachePath, logPath, prefix, "");
            Xlog.setConsoleLogOpen(false);
        }

        com.tencent.mars.xlog.Log.setLogImp(new Xlog());
    }

    /*日志库销毁
    * */
    private static void destroyXlog(){
        com.tencent.mars.xlog.Log.appenderFlush(true);
        com.tencent.mars.xlog.Log.appenderClose();
    }

    //刷新缓存日志到文件中
    public static void flush(boolean sync){
        com.tencent.mars.xlog.Log.appenderFlush(sync);
    }

    /*删除老文件,只保存7天日志文件
    * */
    private static void deleteOldFile(){
        String sdPath = CommonFunction.getSDPath();
        if(TextUtils.isEmpty(sdPath)){
            return;
        }
        String logPath = sdPath + "log";
        try {
            File logDir = new File(logPath);
            if(logDir.isDirectory()){
                File[] files = logDir.listFiles();
                long nt = System.currentTimeMillis();
                //CommonFunction.log("Logger", "current time=" + nt);
                for(int i=0;i<files.length;i++){
                    if(files[i]!=null && files[i].exists() && files[i].isFile()){
                        long ft = files[i].lastModified();
                        //CommonFunction.log("Logger", "file " +files[i].getName()+ " time=" + ft);
                        if(ft>0 && (nt-7*24*60*60*1000) > ft) {
                            //CommonFunction.log("Logger", "file " + files[i].getName() + " should delete");
                            files[i].delete();
                        }
                    }
                }
            }
        }catch (Exception e){

        }
    }
}
