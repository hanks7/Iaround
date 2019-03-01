
package net.iaround.tools;


import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.connector.ConnectLogin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * 下载最新版本的任务
 *
 * @author chenlb
 */
public class DownloadNewVersionTask extends AsyncTask<String, Long, String> {
    /**
     * 通知ID：下载进度提示
     */
    public static final int DOWNLOAD_NOTIFY_VIEW_ID = 101;
    /**
     * 更新下载进度
     */
    private final int MSG_UPDATE_PROGRESS = 1;
    /** */
    public static final int DOWNLOAD_NEWVERSION_REQUEST = 10086;

    /**
     * 刷新进度的时间间隔
     */
    private final int UPDATE_TIME = 1500;
    private static DownloadNewVersionTask downloadNewVersionTask;
    private Context mContext;
    private NotificationManager mNotificationManager;
    private RemoteViews contentView;
    private PendingIntent contentIntent;
    private Notification notification;
    private long lenghtOfFile;
    private long curTotal;
    private boolean isDownloading;

    // 更新进度条
    private ProgressDialog pd;
    // 是否强制升级,强制升级需要显示进度条
    private boolean isFocusUpdate = false;

    /**
     * 是否正在下载
     *
     * @return true : 正在下载
     */
    public boolean isDownloading() {
        return isDownloading;
    }

    public synchronized static DownloadNewVersionTask getInstance(Context context,
                                                                  boolean isFocusUpdate) {
        if (null == downloadNewVersionTask) {
            downloadNewVersionTask = new DownloadNewVersionTask(context, isFocusUpdate);
        }
        CommonFunction.log("shifengxiong",
                "DownloadNewVersionTask -------------------------");
        return downloadNewVersionTask;
    }

    public void reset() {
        CommonFunction.log("shifengxiong", "downloadNewVersionTask reset");
        downloadNewVersionTask = null;
        if (pd != null) {
            pd.dismiss();
            pd = null;
        }
    }

    private DownloadNewVersionTask(Context context, boolean isFocusUpdate) {
        this.mContext = context;
        isDownloading = false;
        this.isFocusUpdate = isFocusUpdate;

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        contentView = new RemoteViews(context.getPackageName(),
                R.layout.notify_download_progress);
        contentView.setTextViewText(R.id.title,
                mContext.getString(R.string.download_new_version_tip));

        Intent notificationIntent = new Intent();
        contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

        String tickerText = mContext.getString(R.string.download_new_version_ticker_txt);
        int icon = android.R.drawable.stat_sys_download;
        // notification = new Notification( icon , tickerText ,
        // System.currentTimeMillis( ) );
        // notification = new Notification( );
        Builder builder = new Builder(mContext);
        builder.setSmallIcon(icon).setTicker(tickerText)
                .setWhen(System.currentTimeMillis());
        notification = builder.getNotification();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        if (pd == null && isFocusUpdate) {
            pd = new ProgressDialog(mContext);
            pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pd.setProgressNumberFormat(null);
            pd.setCancelable(false);
            pd.show();
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        isDownloading = true;
        contentView.setTextViewText(R.id.percentTip, "0%");
        contentView.setProgressBar(R.id.progressBar, 100, 0, false);
        displayDownloadProgressNotify();
    }

    @Override
    protected String doInBackground(String... params) {
        int count;
        CommonFunction.log("shifengxiong", "doInBackground -----------------iaround_update");
        String filePath = CommonFunction.getSDPath() + "iaround_update.apk"; // http://dl.iaround.com/iAround_2.2.1.apk
        HttpURLConnection connect = null;
        InputStream input = null;
        OutputStream output = null;

        try {
            URL url = new URL(params[0]);

            CommonFunction.log("shifengxiong", " iaround_update  ===============" + url);
            connect = (HttpURLConnection) url.openConnection();

            lenghtOfFile = connect.getContentLength();
            input = new BufferedInputStream(connect.getInputStream());
            output = new FileOutputStream(filePath);

            byte data[] = new byte[1024 * 3];
            curTotal = 0;
            publishProgress(curTotal); // 开始刷新下载进度

            while ((count = input.read(data)) != -1) {
                curTotal += count;
                output.write(data, 0, count);
            }
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
            filePath = null;
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (input != null) {
                    input.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }

    protected void onProgressUpdate(Long... progress) {
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, UPDATE_TIME);
    }

    protected void onPostExecute(String result) {
        mHandler.removeMessages(MSG_UPDATE_PROGRESS);
        mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);

        if (pd != null) {
            pd.setProgress(100);
            pd.dismiss();
            pd = null;
        }

        // 下载完后，立刻安装
        if (!CommonFunction.isEmptyOrNullStr(result)) {
            CommonFunction.log("shifengxiong", "下载完后，立刻安装-------------------onPostExecute" + "  result:" + result);
            Intent i = new Intent();
            i.setAction("android.intent.action.VIEW");
            i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            if (mContext == null)
                mContext = BaseApplication.appContext;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//判读版本是否在7.0以上
                Uri apkUri = FileProvider.getUriForFile(mContext, mContext.getPackageName()
                        + ".fileprovider", new File(result));
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                i.setDataAndType(apkUri, "application/vnd.android.package-archive");

            } else {
                i.setDataAndType(Uri.parse("file://" + result),
                        "application/vnd.android.package-archive");
            }
            // if(isFocusUpdate)
            // {
            // ((SuperActivity)mContext).startActivityForResult( i ,
            // DOWNLOAD_NEWVERSION_REQUEST );
            // }else
            // {
            // ((SuperActivity)mContext).startActivity( i );
            // }

            mContext.startActivity(i);

        } else {
            // 下载失败
            if (isFocusUpdate) {
                // 强升过程中，失败退出登录
                String message = mContext.getResources().getString(
                        R.string.download_new_version_fail);
                String title = mContext.getResources().getString(R.string.dialog_title);
                String btnStr = mContext.getResources().getString(R.string.ok);
                final Dialog d = DialogUtil.showOneButtonDialog(mContext, title, message,
                        btnStr, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // TODO 退出登陆
                                ConnectLogin.getInstance(mContext).logout(mContext, 4);
                            }
                        });
                d.show();
            } else {
                Toast.makeText(mContext, R.string.download_new_version_fail,
                        Toast.LENGTH_LONG).show();
            }

        }

        isDownloading = false;
    }

    /**
     * 更新进度
     *
     * @return true ：下载完成
     */
    private boolean progressUpdate() {
        long count = curTotal;
        double pro1 = ((double) count) / ((double) lenghtOfFile);
        int pro = (int) (pro1 * 100);

        contentView.setTextViewText(R.id.percentTip, pro + "%");
        contentView.setProgressBar(R.id.progressBar, 100, pro, false);
        if (pd != null) {
            pd.setProgress(pro);
        }

        return curTotal >= lenghtOfFile;
    }

    /**
     * 处理：更新下载进度
     */
    private void handleUpdateProgress() {
        boolean isFinish = progressUpdate();
        displayDownloadProgressNotify();

        if (!isFinish) {
            mHandler.sendEmptyMessageDelayed(MSG_UPDATE_PROGRESS, UPDATE_TIME);
        } else {
            if (pd != null) {
                pd.cancel();
                pd.dismiss();
                pd = null;
            }
            mHandler.removeMessages(MSG_UPDATE_PROGRESS);
            mNotificationManager.cancel(DOWNLOAD_NOTIFY_VIEW_ID);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_PROGRESS:
                    handleUpdateProgress();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    private void displayDownloadProgressNotify() {
        notification.contentView = contentView;
        notification.contentIntent = contentIntent;
        mNotificationManager.notify(DownloadNewVersionTask.DOWNLOAD_NOTIFY_VIEW_ID,
                notification);
    }
}
