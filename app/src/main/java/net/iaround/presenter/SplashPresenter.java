package net.iaround.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.widget.RemoteViews;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.contract.SplashContract;
//import net.iaround.model.database.Lable;
import net.iaround.service.APKDownloadService;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DownLoadAPKThread;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.SplashActivity;

//import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;

/**
 * Class: 业务逻辑
 * Author：gh
 * Date: 2016/11/28 11:49
 * Emial：jt_gaohang@163.com
 */
public class SplashPresenter implements SplashContract.Presenter {


    @Override
    public void initDB(Context mContext) {
        int lableVersion = SharedPreferenceUtil.getInstance(mContext).getInt(Constants.LABLE_VERSION);
        if (lableVersion < Constants.LABLE_VERSION_VALUE) {
            initLableDB(mContext);
        }
    }

    private void initLableDB(Context mContext) {
//        DataSupport.deleteAll(Lable.class);//yuchao
//        ArrayList<Lable> lableList = new ArrayList<>();
//        String[] lableSport = mContext.getResources().getStringArray(R.array.lable_sport);
//        for (int i=0; i<lableSport.length; i++){
//            String id = String.format("%04d", i);
//            lableList.add(new Lable(id, lableSport[i]));
//        }
//
//        String[] lableTravle = mContext.getResources().getStringArray(R.array.lable_travel);
//        for (int i=0; i<lableTravle.length; i++){
//            String id = String.format("%04d", i+200);
//            lableList.add(new Lable(id, lableTravle[i]));
//        }
//
//        String[] lableArt = mContext.getResources().getStringArray(R.array.lable_art);
//        for (int i=0; i<lableArt.length; i++){
//            String id = String.format("%04d", i+400);
//            lableList.add(new Lable(id, lableArt[i]));
//        }
//
//        String[] lableFoot = mContext.getResources().getStringArray(R.array.lable_foot);
//        for (int i=0; i<lableFoot.length; i++){
//            String id = String.format("%04d", i+600);
//            lableList.add(new Lable(id, lableFoot[i]));
//        }
//
//        String[] lableFun = mContext.getResources().getStringArray(R.array.lable_fun);
//        for (int i=0; i<lableFun.length; i++){
//            String id = String.format("%04d", i+800);
//            lableList.add(new Lable(id, lableFun[i]));
//        }
//
//        String[] lableRest = mContext.getResources().getStringArray(R.array.lable_rest);
//        for (int i=0; i<lableRest.length; i++){
//            String id = String.format("%04d", i+1000);
//            lableList.add(new Lable(id, lableRest[i]));
//        }
//
//        String[] lableMy = mContext.getResources().getStringArray(R.array.lable_my);
//        for (int i=0; i<lableMy.length; i++){
//            String id = String.format("%04d", i+1200);
//            lableList.add(new Lable(id, lableMy[i]));
//        }
//        DataSupport.saveAll(lableList);//yuchao
        SharedPreferenceUtil.getInstance(mContext).putInt(Constants.LABLE_VERSION, Constants.LABLE_VERSION_VALUE);
    }


    private AdvertClickNotify instance;

    @Override
    public AdvertClickNotify getAdvertInstance(Context mContext) {
        if (instance == null) {
            instance = new AdvertClickNotify(mContext);
        }
        return instance;
    }

    private DownLoadApk downLoadApk;

    @Override
    public DownLoadApk getDownLoadApk(Context mContext) {
        if (downLoadApk == null) {
            downLoadApk = new DownLoadApk(mContext);
        }
        return downLoadApk;
    }


    public class AdvertClickNotify {

        private Context mContext;

        private NotificationManager mNotificationManager;
        private RemoteViews contentView;
        private PendingIntent contentIntent;
        private Notification notification;

        public AdvertClickNotify(Context mContext) {
            this.mContext = mContext;
            createNotification();
        }

        // 创建
        public void createNotification() {
            mNotificationManager = (NotificationManager) mContext.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            contentView = new RemoteViews(mContext.getPackageName(), R.layout.notify_download_progress);
            contentView.setTextViewText(R.id.title, mContext.getString(R.string.download_new_version_tip));
            contentView.setTextColor(R.id.title, Color.WHITE);

            Intent notificationIntent = new Intent();
            contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);

            String tickerText = mContext.getString(R.string.download_new_version_ticker_txt);
            int icon = android.R.drawable.stat_sys_download;
            //		Builder builder = new Builder(mContext);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            builder.setSmallIcon(icon).setTicker(tickerText).setWhen(System.currentTimeMillis());
            notification = builder.build();
            notification.flags |= Notification.FLAG_ONGOING_EVENT;

        }

        public void showNotification(int id) {

            contentView.setTextViewText(R.id.percentTip, "0%");
            contentView.setProgressBar(R.id.progressBar, 100, 0, false);
            notification.contentView = contentView;
            notification.contentIntent = contentIntent;
            mNotificationManager.notify(id, notification);
        }

        public void updateNotification(int id, long currentLen, long totalLen) {

            double pro1 = ((double) currentLen) / ((double) totalLen);
            int pro = (int) (pro1 * 100);

            contentView.setTextViewText(R.id.percentTip, pro + "%");
            contentView.setProgressBar(R.id.progressBar, 100, pro, false);
            mNotificationManager.notify(id, notification);
        }

        public void hideNotification(int id) {
            mNotificationManager.cancel(id);
        }
    }

    public class DownLoadApk {

        private int count = 1;// 用于log下载进度
        private AdvertClickNotify notify;
        private Context mContext;

        public DownLoadApk(Context mContext) {
            this.mContext = mContext;
            notify = new AdvertClickNotify(mContext);
        }

        /**
         * 启动线程下载应用，只支持单个任务下载
         */
        public void startDownLoad(String url) {

            final int taskFlag = url.hashCode();
            notify.showNotification(taskFlag);
            DownloadFileCallback callback = new DownloadFileCallback() {
                @Override
                public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded,
                                                   int flag) {
                    if (LengthOfDownloaded > 1024 * 1024 * count) {
                        CommonFunction.log("shifengxiong",
                                flag + " downLoad == " + LengthOfDownloaded + "/" + lenghtOfFile);

                        final long currentLen = LengthOfDownloaded;
                        final long totalLen = lenghtOfFile;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                notify.updateNotification(taskFlag, currentLen, totalLen);
                            }
                        }).start();
                        count++;
                    }
                }

                @Override
                public void onDownloadFileFinish(int flag, String fileName, String savePath) {

                    notify.hideNotification(taskFlag);
                    installApp(mContext, fileName);
                }

                @Override
                public void onDownloadFileError(int flag, String fileName, String savePath) {
                    CommonFunction.log("sherlock", "WebViewAvtivity  onDownloadFileError");
                    notify.hideNotification(taskFlag);
                }
            };

            new DownLoadAPKThread(mContext, url, callback).start();

            CommonFunction.toastMsg(mContext, R.string.download_new_version_ticker_txt);
        }

        /**
         * 安装应用
         */
        public void installApp(Context context, String filePath) {
            if (TextUtils.isEmpty(filePath) || context == null) {
                return;
            }

            String dir = PathUtil.getAPKCacheDir();
//            String[] args1 = {"chmod", "705", dir};
//            String result = APKDownloadService.exec(args1);
//            String[] args2 = {"chmod", "604", dir + filePath};
//            result = APKDownloadService.exec(args2);
//            CommonFunction
//                    .log("sherlock", "下载完后，立刻安装-------------------installApp =" + dir + filePath);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setDataAndType(Uri.fromFile(new File(dir + filePath)),
//                    "application/vnd.android.package-archive");
//            context.startActivity(intent);

            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(dir + filePath)),
                    "application/vnd.android.package-archive");
            context.startActivity(intent);
        }

    }


}
