
package net.iaround.connector;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.RoundPicture;
import net.iaround.tools.UrlReplaceUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * 加载图片
 *
 * @author linyg
 */
public class DownLoadBitmap extends BaseHttp {
    private String urlPath;
    private int round;

    public DownLoadBitmap(Context context, String url) throws ConnectionException {
        this(context, url, null, 0);
    }

    public DownLoadBitmap(Context context, String url, String imageType, int round)
            throws ConnectionException {
//		super( context , CommonFunction.replaceUrl( url ) );
        super(context, UrlReplaceUtil.getInstance().replaceImageUrl(url));
        this.urlPath = UrlReplaceUtil.getInstance().replaceImageUrl(url); //url;
        this.connectTimeout = 5000;
        this.readTimeout = 30000;
        this.round = round;
    }

    /**
     * 根据网络图片地址url，获取图片数据，基本本地缓存的功能
     *
     * @param String
     * @return Bitmap
     * @throws ConnectionException
     */
    protected Bitmap getBitmap(String savePath, boolean saveSD) throws Exception {
        if (urlPath == null && urlPath.length() <= 0) {
            return null;
        }
        if (connection == null) {
            return null;
        }
        try {
            connection.connect();
            InputStream is = connection.getInputStream();
            PatchInputStream pis = new PatchInputStream(is);
            Bitmap bitmap = BitmapFactory.decodeStream(pis);
            connection.disconnect();
            if (bitmap == null || bitmap.isRecycled()) {
                return null;
            }

            if (round > 0) {
                bitmap = RoundPicture.getRoundedCornerBitmap(bitmap, round,
                        bitmap.getWidth(), bitmap.getHeight());
            }

            if (saveSD) {
                saveInSD(bitmap, savePath);
            }
            return bitmap;
        } catch (Throwable t) {
            CommonFunction.log(t);
        }

        return null;
    }

    /**
     * 使用线程池保存图片
     **/
    private void saveInSD(Bitmap bitmap, final String savePath) {
        FileOutputStream fos = null;
        try {
            File cacheDir = new File(savePath);
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            } else if (!cacheDir.isDirectory()) {
                cacheDir.delete();
                cacheDir.mkdirs();
            }

            String fileName = (round == 0) ? CryptoUtil.SHA1(urlPath) : CryptoUtil
                    .SHA1(urlPath + round);
            File file = new File(cacheDir, fileName);
            if (file.exists()) {
                file.delete();
            }

            if (file.createNewFile()) {
                fos = new FileOutputStream(file);
                if (round > 0 || urlPath.toLowerCase().endsWith(".png")) { // 保存为png
                    bitmap.compress(Bitmap.CompressFormat.PNG, 60, fos);
                } else {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fos);
                }
                fos.flush();
                fos.close();
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 读取数据
     *
     * @return
     * @throws IOException
     * @time 2011-6-28 下午01:37:33
     * @author:linyg
     */
    public String getFileSave(String path) throws IOException {
        File file = new File(path);
        File folder = file.getParentFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
        connection.connect();
        InputStream is = null;
        FileOutputStream fos = null;

        try {
            is = connection.getInputStream();
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024 * 4];
            int length = -1;
            while ((length = is.read(buffer)) > -1) {
                fos.write(buffer, 0, length);
            }
            fos.flush();
            connection.disconnect();
        } catch (Exception e) {
            CommonFunction.log(e);
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
            }

            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
            }
        }

        return path;
    }

    public class PatchInputStream extends FilterInputStream {
        InputStream in;

        protected PatchInputStream(InputStream in) {
            super(in);
            this.in = in;
        }

        public long skip(long n) throws IOException {
            long m = 0;
            while (m < n) {
                long _m = in.skip(n - m);
                if (_m == 0) {
                    break;
                }
                m += _m;
            }
            return m;
        }
    }
}
