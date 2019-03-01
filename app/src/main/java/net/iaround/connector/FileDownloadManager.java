
package net.iaround.connector;


import android.content.Context;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.UrlReplaceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-6-12 下午2:04:51
 * @ClassName FileDownloadManager.java
 * @Description: 文件下载管理类
 */

public class FileDownloadManager extends BaseHttp implements Runnable {

    public static final int BUFFER_SIZE = 2 * 1024;
    private DownloadFileCallback callback;
    private int flag;
    private String savePath;
    private String fileName;

    /**
     * 下载图片
     *
     * @param url
     * @param savePath
     * @param flag
     * @throws ConnectionException
     */
    public FileDownloadManager(Context context, DownloadFileCallback callback, String url,
                               String fileName, String savePath, int flag) throws ConnectionException {
        super(context, UrlReplaceUtil.getInstance().replaceImageUrl(url));
        this.url = UrlReplaceUtil.getInstance().replaceImageUrl(url); //CommonFunction.replaceUrl( url );
        this.flag = flag;
        this.callback = callback;
        this.savePath = savePath;
        this.fileName = fileName;
        this.connectTimeout = 5 * 1000;
        this.readTimeout = 30 * 1000;
    }

    @Override
    public void close() {
        super.close();
    }

    @Override
    protected void setConnectTimeOut(int timeout) {
        super.setConnectTimeOut(timeout);
    }

    @Override
    protected void setReadTimeOut(int timeout) {
        super.setReadTimeOut(timeout);
    }

    @Override
    public void run() {
        if (connection != null) {
            InputStream is = null;
            try {
                connection.setConnectTimeout(connectTimeout);
                connection.setReadTimeout(readTimeout);
                connection.connect();
                long length = connection.getContentLength();
                if (length > 0) {
                    is = connection.getInputStream();
                    boolean isDownloaded = writeInputSteamToSD(is, length);

                    if (isDownloaded) {
                        // 下载结束
                        callback.onDownloadFileFinish(this.flag, fileName, savePath);
                    } else {
                        callback.onDownloadFileError(flag, fileName, savePath);
                    }

                } else {
                    callback.onDownloadFileError(flag, fileName, savePath);
                }
            } catch (Exception e) {
                // 下载失败
                callback.onDownloadFileError(flag, fileName, savePath);
                CommonFunction.log("FileDownloadManager", "Exception  下载失败");
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                    }
                }
                close();
            }
        } else {
            callback.onDownloadFileError(flag, fileName, savePath);
        }
    }

    /**
     * 写入缓存
     *
     * @param is
     * @return 是否已经成功写入文件
     */
    private boolean writeInputSteamToSD(InputStream is, long totalLen) {
        File cacheFile = new File(savePath + fileName);
        FileOutputStream fos = null;
        try {
            if (cacheFile.exists()) {
                //如果文件存在,但是大小和下载的大小不一样的话,那么认为是需要重新下载的
                if (cacheFile.length() != totalLen) {
                    cacheFile.delete();
                } else {
                    return true;
                }
            }

            fos = new FileOutputStream(cacheFile);
            byte[] buf = new byte[BUFFER_SIZE];
            int len = 0;
            int downloaded = 0;

            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
                downloaded += len;
                callback.onDownloadFileProgress(totalLen, downloaded, flag);

            }

            //文件下载出错
            if (totalLen != downloaded) {

                CommonFunction.log("FileDownloadManager", "totalLen != downloaded  下载失败");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                    fos = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
