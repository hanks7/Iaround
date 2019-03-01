package net.iaround.tools;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import net.iaround.conf.Common;
import net.iaround.conf.KeyWord;
import net.iaround.connector.ConnectionException;
import net.iaround.connector.DownloadFileCallback;
import net.iaround.connector.FileDownloadManager;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.KeyWordWorker;

import java.io.File;

/**
 * Created by Administrator on 2015/12/26.
 */
public class DownLoadKeywords extends Thread {
    private Context context;
    private String fileUrl;

    public DownLoadKeywords(Context context, String url) {
        this.context = context;
        fileUrl = url;
    }

    @Override
    public void run() {
        String fileName = CryptoUtil.generate(fileUrl) + ".txt";
        try {
            File cacheFile = new File(PathUtil.getAPKCacheDir() + fileName);
            if (cacheFile.exists()) {
                cacheFile.delete();
            }

            FileDownloadManager manager = new FileDownloadManager(context, callback, fileUrl,
                    fileName, PathUtil.getBufferCacheDir(), 0);
            manager.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
        super.run();
    }

    DownloadFileCallback callback = new DownloadFileCallback() {
        @Override
        public void onDownloadFileProgress(long lenghtOfFile, long LengthOfDownloaded,
                                           int flag) {

        }

        @Override
        public void onDownloadFileFinish(int flag, String fileName, String savePath) {
//			String filename =  "20151225.txt";
            String filedata = "";
            try {
                CommonFunction.log("shifengxiong", "fileName>>>>>>>>>>>>>" + fileName);
                CommonFunction.log("shifengxiong", "savePath>>>>>>>>>>>>>" + savePath);
                filedata = FileService.readSDCardFile(savePath + fileName);

                if (!TextUtils.isEmpty(filedata)) {
                    KeyWordWorker keywordWorker = DatabaseFactory.getKeyWordWorker(context);
                    String data[] = filedata.split("\r\n");

                    for (int i = 0; i < data.length; i++) {
                        String[] subData = data[i].split("\t");
                        try {
                            if (subData.length != 4) continue;
                            int key = Integer.parseInt(subData[0]);
                            String value = subData[1];
                            int type = Integer.parseInt(subData[2]);
                            int rank = Integer.parseInt(subData[3]);

                            if (type == 1) { // 添加
                                ContentValues values = new ContentValues();
                                values.put(KeyWordWorker.K_KID, key);
                                values.put(KeyWordWorker.K_KEYWORD, value);
                                values.put(KeyWordWorker.K_KEYWORD_LEVEL, rank);
                                keywordWorker.onInsert(values);
                                KeyWord.getInstance(context).addKeyword(context, value, rank);
                            } else if (type == 2) { // 修改
                                ContentValues values = new ContentValues();
//								values.put( KeyWordWorker.K_KID , key );
                                values.put(KeyWordWorker.K_KEYWORD, value);
                                values.put(KeyWordWorker.K_KEYWORD_LEVEL, rank);
                                String where = KeyWordWorker.K_KID + " = " + key;
                                int success = keywordWorker.onUpdate(values, where);
                                if (success <= 0) {
                                    values.put(KeyWordWorker.K_KID, key);
                                    keywordWorker.onInsert(values);
                                }

                                KeyWord.getInstance(context).modifyKeyword(context, value, rank);
                            } else if (type == 3) { // 删除
                                keywordWorker.delete(key);
                                KeyWord.getInstance(context).removeKeyword(value);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(context);
                    sp.putLong(SharedPreferenceUtil.KEYWORD_VERSION, System.currentTimeMillis()
                            + Common.getInstance().serverToClientTime);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onDownloadFileError(int flag, String fileName, String savePath) {

        }
    };


}
