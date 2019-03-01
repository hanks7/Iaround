package net.iaround.connector;

import android.content.Context;

import net.iaround.BaseApplication;
import net.iaround.conf.Config;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.TimeFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 视频或者图片上传
 * Created by Administrator on 2017/12/8.
 */

public class CertificationUpload {

    public static long uploadResourceFile(Context mContext, String path, int type, UploadFileCallback uploadFileCallback) {
        long flag = TimeFormat.getCurrentTimeMillis();

        String urlStr = type == ChatMessageType.IMAGE ? Config.sPictureHost : Config.sVideoHost;
        FileUploadManager.FileProfix fileProfix = type == ChatMessageType.IMAGE ? FileUploadManager.FileProfix.JPG : FileUploadManager.FileProfix.MP4;

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", ConnectorManage.getInstance(BaseApplication.appContext).getKey());
        map.put("type", String.valueOf(type));
        File file = new File(path);

        if (file.exists()) {
            CommonFunction.log("CertificationUpload", "path=" + path + ", file type=" + fileProfix + ", url=" + urlStr);
            FileUploadManager.createUploadTask(mContext, path, fileProfix, urlStr, map, uploadFileCallback, flag, 1000 * 60, 1000 * 60 * 3).start();
        } else {
            uploadFileCallback.onUploadFileError("", flag);// 上传出错
        }

        return flag;
    }


    public static long uploadResourceImgFile(Context mContext, String path, int type, UploadFileCallback uploadFileCallback) {
        long flag = TimeFormat.getCurrentTimeMillis();

        String urlStr = type == ChatMessageType.IMAGE ? Config.sPictureHost
                : Config.sVideoHost;
        FileUploadManager.FileProfix fileProfix = type == ChatMessageType.IMAGE ? FileUploadManager.FileProfix.JPG
                : FileUploadManager.FileProfix.MP4;

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", ConnectorManage.getInstance(BaseApplication.appContext).getKey());
        map.put("type", String.valueOf(type));
        File file = new File(path);

        if (file.exists()) {
            FileUploadManager.createUploadTask(mContext, path, fileProfix,
                    urlStr, map, uploadFileCallback, flag, 1000 * 60, 1000 * 60 * 3).start();
        } else {
            uploadFileCallback.onUploadFileError("", flag);// 上传出错
        }

        return flag;
    }

    public static long uploadResourceVideoImgFile(Context mContext, String path, int type, UploadFileCallback uploadFileCallback) {
        long flag = TimeFormat.getCurrentTimeMillis();

        String urlStr = type == ChatMessageType.IMAGE ? Config.sPictureHost
                : Config.sVideoHost;
        FileUploadManager.FileProfix fileProfix = type == ChatMessageType.IMAGE ? FileUploadManager.FileProfix.JPG
                : FileUploadManager.FileProfix.MP4;

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", ConnectorManage.getInstance(BaseApplication.appContext).getKey());
        map.put("type", String.valueOf(type));
        File file = new File(path);

        if (file.exists()) {
            FileUploadManager.createUploadTask(mContext, path, fileProfix,
                    urlStr, map, uploadFileCallback, flag, 1000 * 60, 1000 * 60 * 3).start();
        } else {
            uploadFileCallback.onUploadFileError("", flag);// 上传出错
        }

        return flag;
    }
}
