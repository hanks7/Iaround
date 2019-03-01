
package net.iaround.tools;


import android.content.Context;

import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;

import java.io.File;


public class DownLoadAPKThread extends Thread {
    private Context context;
    private DownloadFileCallback callback;
    private String fileUrl;

    public DownLoadAPKThread(Context context, String url, DownloadFileCallback callback) {
        this.context = context;
        fileUrl = url;
        this.callback = callback;
    }

    @Override
    public void run() {
        String fileName = CryptoUtil.generate(fileUrl) + ".apk";
        try {
            File cacheFile = new File(PathUtil.getAPKCacheDir() + fileName);
            if (cacheFile.exists()) {
                cacheFile.delete();
            }

            FileDownloadManager manager = new FileDownloadManager(context, callback, fileUrl, fileName, PathUtil.getAPKCacheDir(), 0);
            manager.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        super.run();
    }
}
