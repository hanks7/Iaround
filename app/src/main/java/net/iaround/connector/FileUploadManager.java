package net.iaround.connector;

import android.content.Context;

import java.util.Map;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-22 下午12:50:57
 * @ClassName FileUploadManager.java
 * @Description: 文件上传管理类
 */

public class FileUploadManager {
    /**
     * 上传文件的类型 JPG、PNG、MP3、GP3V
     */
    public enum FileProfix {
        JPG("file.jpg"),
        PNG("file.png"),
        MP3("file.mp3"),
        GP3V("file.3gpv"),
        MP4("file.mp4");

        private String name;

        FileProfix(String profixName) {
            this.name = profixName;
        }

        public String getName() {
            return name;
        }
    }


    /**
     * 创建一个上传文件的线程 ,需要自己启动线程.start()
     */
    public static Thread createUploadTask(Context context, String fileDir, FileProfix fileProfix, String url,
                                          Map<String, String> map, UploadFileCallback callback, long flag) {
        String filename = fileProfix.getName();
        return createUploadTask(context, fileDir, filename, url, map, callback, flag);
    }

    /**
     * 创建一个上传文件的线程 ,需要自己启动线程.start()
     *
     * @param maxConnectTime 连接超时时间
     * @param maxRequestTime 请求超时时间
     */
    public static Thread createUploadTask(Context context, String fileDir, FileProfix fileProfix, String url,
                                          Map<String, String> map, UploadFileCallback callback, long flag, int maxConnectTime, int maxRequestTime) {
        String filename = fileProfix.getName();
        return createUploadTask(context, fileDir, filename, url, map, callback, flag, maxConnectTime, maxRequestTime);
    }

    /**
     * 创建一个上传文件的线程 ,需要自己启动线程.start()
     */
    public static Thread createUploadTask(Context context, String fileDir, String filename, String url,
                                          Map<String, String> map, UploadFileCallback _callback, long flag) {
        Runnable uploadFileRun = null;
        try {
            uploadFileRun = new UploadFile(context, fileDir, filename, url, map, _callback, flag);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return new Thread(uploadFileRun);
    }

    /**
     * 创建一个上传文件的线程 ,需要自己启动线程.start()
     *
     * @param maxConnectTime 连接超时时间
     * @param maxRequestTime 请求超时时间
     */
    public static Thread createUploadTask(Context context, String fileDir, String filename, String url,
                                          Map<String, String> map, UploadFileCallback _callback, long flag, int maxConnectTime, int maxRequestTime) {
        Runnable uploadFileRun = null;
        try {
            uploadFileRun = new UploadFile(context, fileDir, filename, url, map, _callback, flag);
            ((UploadFile) uploadFileRun).setTimeoutParams(maxConnectTime, maxRequestTime);
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        return new Thread(uploadFileRun);
    }
}
