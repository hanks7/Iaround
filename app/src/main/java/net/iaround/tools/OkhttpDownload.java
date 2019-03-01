package net.iaround.tools;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 作者：zx on 2017/7/14 12:10
 */
public class OkhttpDownload {

    private static final String TAG = "okdownload";
    private static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 30;
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 30;
    private static final int DEFAULT_WRITE_TIMEOUT_MILLIS = 30;
    private static final String OK_DOWNLOAD_DIR = "/iaround/.ia_download";//下载文件夹目录
    private static final String FILE_SUFFIX = ".webp";//文件后缀
    private static final int MAX_REQUESTS = 1;//当前okhttp的最大并发数
    private File parentFile;
    private OkHttpClient httpClient;
    private Context mContext;
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    public OkhttpDownload(Context context) {
        this.mContext = context.getApplicationContext();
        httpClient = defaultOkHttpClient();
        parentFile = createDefaultDownloadDir();
    }


    private File createDefaultDownloadDir() {
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), OK_DOWNLOAD_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    private OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.SECONDS)
                .build();
        client.dispatcher().setMaxRequests(MAX_REQUESTS);
        return client;
    }

    private File createDownloadFile(String url, String fileName) {
        String name = fileName + FILE_SUFFIX;
        File file = new File(parentFile, name);
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {//此时文件存在，说明以前下载过此礼物包，现在需要更新文件，删除旧的礼物包，重新下载
                file.delete();
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
        }
        return file;
    }

    long startTime = 0;
    long endTime = 0;

    public void downLoadCallback(final String url, final String fileName, final DownloadCallback callback) {
        startTime = System.currentTimeMillis();
        final File file = createDownloadFile(url, fileName);
        final Request request = new Request.Builder().url(url).build();

        final Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
//                Log.e(TAG, e.toString());
                OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
                if (null != callback) {
                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            callback.DownloadFailure(e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
                    if (null != callback) {
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.DownloadFailure(response.message().toString());
                            }
                        });
                    }
                    return;
                }
                if (response.code() != HttpURLConnection.HTTP_OK) {
                    OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
                    if (null != callback) {
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.DownloadFailure(response.message().toString());
                            }
                        });
                    }
                    return;
                }
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    long total = response.body().contentLength();
//                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
//                        Log.e(TAG, "current------>" + current);
                    }
                    fos.flush();
//                    Log.i(TAG, "<------下载成功------>");
                    endTime = System.currentTimeMillis();
//                    long time = endTime - startTime;
//                    Log.i(TAG, "<------下载消耗时间------>" + time + " 毫秒");
                    OkhttpDownloadStatus.clearDownloadFailreGift(mContext, url, fileName);
                    if (null != callback) {
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.DownloadSuccess();
                            }
                        });
                    }
                } catch (final IOException e) {
//                    Log.e(TAG, e.toString());
//                    Log.i(TAG, "<------下载失败------>");
                    OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
                    if (null != callback) {
                        mainThreadHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                callback.DownloadFailure(e.getMessage());
                            }
                        });
                    }
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (final IOException e) {
//                        Log.e(TAG, e.toString());
                        OkhttpDownloadStatus.putDownloadFailureGift(mContext, url, fileName);
                        if (null != callback) {
                            mainThreadHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    callback.DownloadFailure(e.getMessage());
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void downLoad(final String url, final String fileName) {
        downLoadCallback(url, fileName, null);
    }
}
