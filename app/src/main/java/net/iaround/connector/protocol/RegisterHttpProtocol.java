package net.iaround.connector.protocol;

import android.content.Context;

import net.iaround.conf.Config;
import net.iaround.connector.UploadFileManager;
import net.iaround.connector.UploadImageCallback;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.TimeFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Class: 注册接口
 * Author：gh
 * Date: 2016/12/2 16:09
 * Emial：jt_gaohang@163.com
 */
public class RegisterHttpProtocol {

    private static String REGISTER = "/user/signup";


    public static void uploadHeadPic(Map<String, File> picFileMap, UploadImageCallback callback){
        Map<String, String> params = new HashMap<>();
        params.put("type", String.valueOf(FileUploadType.PIC_FACE));
        params.put("key", ""+TimeFormat.getCurrentTimeMillis());
        UploadFileManager.getInstance().requestHttpPost(Config.sPictureHost, params, picFileMap, "application/octet-stream", "file", callback);

    }

}
