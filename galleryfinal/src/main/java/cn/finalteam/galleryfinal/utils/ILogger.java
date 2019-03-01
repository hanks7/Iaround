package cn.finalteam.galleryfinal.utils;

import cn.finalteam.toolsfinal.BuildConfig;
import cn.finalteam.toolsfinal.logger.LoggerFactory;
import cn.finalteam.toolsfinal.logger.LoggerPrinter;
import cn.finalteam.toolsfinal.logger.Printer;
import cn.finalteam.toolsfinal.logger.Settings;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/1/29 0029 17:51
 */
public final class ILogger {

    public static class NoneLogger implements cn.finalteam.toolsfinal.logger.Printer{
        private Settings settings = new Settings();

        public Printer t(String tag, int methodCount){
            return this;
        }

//    Settings init(String tag);

        public Settings getSettings(){
            return settings;
        }

        public void d(String message, Object... args){

        }

        public void e(Throwable throwable){

        }

        public void e(String message, Object... args){

        }

        public void e(Throwable throwable, String message, Object... args){

        }

        public void w(String message, Object... args){

        }

        public void i(String message, Object... args){

        }

        public void v(String message, Object... args){

        }

        public void wtf(String message, Object... args){

        }

        public void json(String json){

        }

        public void xml(String xml){

        }

        public void clear(){

        }
    }
    public static final String DEFAULT_TAG = "GalleryFinal";
    private static final boolean DEBUG = BuildConfig.DEBUG;
    private static final NoneLogger printer = new NoneLogger();//LoggerFactory.getFactory(DEFAULT_TAG, DEBUG);

    //no instance
    private ILogger() {
    }

    public static void clear() {
        printer.clear();
    }

    public static Settings getSettings() {
        return printer.getSettings();
    }

    public static Printer t(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t(null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void e(Throwable throwable) {
        printer.e(throwable);
    }

    public static void e(String message, Object... args) {
        printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        printer.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        printer.xml(xml);
    }


}
