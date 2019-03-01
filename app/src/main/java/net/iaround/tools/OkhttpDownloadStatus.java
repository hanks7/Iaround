package net.iaround.tools;

import android.content.Context;
import android.text.TextUtils;

import net.iaround.model.entity.InitBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：zx on 2017/7/17 17:28
 * 保存与获取礼物下载失败信息
 */
public class OkhttpDownloadStatus {

    public static List<InitBean.GiftPackage> getDownloadFailureGift(Context context) {
        String failureKey = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.DOWNLOAD_FAILURE_GIFT);
        if (TextUtils.isEmpty(failureKey)) {
            return null;
        }
        String[] failureKeys = failureKey.split("&");
        ArrayList<InitBean.GiftPackage> failureGiftPackages = new ArrayList<>();
        for (int i = 0; i < failureKeys.length; i++) {
            String[] keyValues = failureKeys[i].split("#");
            if (keyValues != null && keyValues.length > 1) {
                failureGiftPackages.add(new InitBean.GiftPackage(keyValues[0], keyValues[1]));
            }
        }
        return failureGiftPackages;
    }

    /**
     * 保存下载失败的礼物信息
     * 文件格式为：fileName#url&fileName1#url1&fileName2#url2
     *
     * @param context
     * @param url
     * @param fileName
     */
    public static void putDownloadFailureGift(Context context, String url, String fileName) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName)) {
            return;
        }
        String keyValue = fileName + "#" + url;
        //1.获取当前下载失败的url
        String failureinfo = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.DOWNLOAD_FAILURE_GIFT);
        if (failureinfo.contains(keyValue)) {//如果当前url在失败sp中，则不更新失败sp
            return;
        }
        StringBuffer buffer = new StringBuffer(failureinfo);
        buffer.append("&").append(keyValue);
        //添加url到当前失败sp中
        SharedPreferenceUtil.getInstance(context).putString(SharedPreferenceUtil.DOWNLOAD_FAILURE_GIFT, buffer.toString());
    }

    /**
     * 当失败的礼物下载完成后，清除当条错误信息
     *
     * @param context
     * @param url
     * @param fileName
     */
    public static void clearDownloadFailreGift(Context context, String url, String fileName) {
        if (TextUtils.isEmpty(url) || TextUtils.isEmpty(fileName)) {
            return;
        }
        String keyValue = fileName + "#" + url;
        //1.获取当前下载失败的url
        String failureinfo = SharedPreferenceUtil.getInstance(context).getString(SharedPreferenceUtil.DOWNLOAD_FAILURE_GIFT);
        if (!failureinfo.contains(keyValue)) {//如果当前url不在失败sp中，则不做清除操作
            return;
        }
        //清除已经下载成功的错误信息
        String[] failureinfos = failureinfo.split("&");
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < failureinfos.length; i++) {
            if (!failureinfos[i].equals(keyValue)) {
                buffer.append("&").append(failureinfos[i]);
            }
        }
        //添加url到当前失败sp中
        SharedPreferenceUtil.getInstance(context).putString(SharedPreferenceUtil.DOWNLOAD_FAILURE_GIFT, buffer.toString());
    }
}
