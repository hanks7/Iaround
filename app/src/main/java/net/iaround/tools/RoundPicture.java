
package net.iaround.tools;


import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import java.io.InputStream;


public class RoundPicture {

    /**
     * 圆角
     *
     * @param bitmap1
     * @param roundPx
     * @return
     * @time 2011-6-17 下午01:28:14
     * @author:linyg
     */
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap1, float roundPx, int dWidth,
                                                int dHeight) {
        try {
            // 缩放图片
            Bitmap bitmap = scalePicture(bitmap1, dWidth, dHeight);
            Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                    Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            // 处理圆角
            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            final RectF rectF = new RectF(rect);
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);
            return output;
        } catch (Throwable t) {
            t.printStackTrace();
            System.gc();
        }
        return bitmap1;
    }

    /**
     * 制作圆角
     *
     * @param bitmap
     * @return
     */
    public static Bitmap circelBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, 120, 120);
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() / 2;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        try {
            if (bitmap != null && !bitmap.isRecycled())
                bitmap.recycle();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return output;
    }

    /**
     * 计算缩放比例
     *
     * @param srcBitmap srcBitmap 原图
     * @param maxWidth  目标宽度
     * @param maxHeight 目标高度
     */
    public static Bitmap scalePicture(Bitmap srcBitmap, int maxWidth, int maxHeight) {
        try {
            Bitmap bitmap = null;
            // 剪切图片
            if (maxWidth == maxHeight) {
                int width = srcBitmap.getWidth();
                int height = srcBitmap.getHeight();
                int side = width;
                if (width > height) {
                    side = height;
                }
                Bitmap newb = Bitmap.createBitmap(side, side, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
                Canvas cv = new Canvas(newb);
                cv.drawBitmap(srcBitmap, 0, 0, null);// 在 0，0坐标开始画入src
            }

            int srcWidth = srcBitmap.getWidth();
            int srcHeight = srcBitmap.getHeight();
            // 计算缩放率，新尺寸除原始尺寸
            float scaleWidth = ((float) maxWidth) / srcWidth;
            float scaleHeight = ((float) maxHeight) / srcHeight;

            // 创建操作图片用的matrix对象
            Matrix matrix = new Matrix();
            // 缩放图片动作
            matrix.postScale(scaleWidth, scaleHeight);
            // 创建新的图片
            bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcWidth, srcHeight, matrix,
                    true);
            return bitmap;
        } catch (Throwable t) {
            t.printStackTrace();
            System.gc();
        }
        return srcBitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(InputStream src, int reqWidth,
                                                         int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(src, null, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeStream(src, null, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

}
