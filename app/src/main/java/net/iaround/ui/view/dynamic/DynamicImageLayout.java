package net.iaround.ui.view.dynamic;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * Class: 动态图片布局
 * Author：gh
 * Date: 2016/12/20 15:46
 * Email：jt_gaohang@163.com
 */
public class DynamicImageLayout implements View.OnClickListener {

    private static final int IMG_TAT = R.layout.view_dynamic_pic;
    private Context mContext;
    private View view;
    private ArrayList<String> picList;

    private ImageListenter listenter;

    public DynamicImageLayout(Context context) {
        this.mContext = context;
        view = LayoutInflater.from(mContext).inflate(R.layout.view_dynamic_pic, null);
    }

    /**
     * @param context
     * @return
     */
    public static DynamicImageLayout initDynamicImage(Context context) {
        return new DynamicImageLayout(context);
    }

    public void setImageListenter(ImageListenter imageListenter) {
        listenter = imageListenter;
    }

    public void buildImage(final LinearLayout lyPic, final ArrayList<String> list) {
        if (list.size() == 0) {
            lyPic.removeAllViews();
            return;
        }
        if (picList == null) {
            picList = new ArrayList<String>();
        }
        picList.clear();
        picList.addAll(list);

        if (list != null && list.size() > 0) {
            lyPic.removeAllViews();
            if (list.size() > 0) {
                View jobView = null;
                if (list.size() <= 1) {
                    if (PicSize(list.get(0))) {
                        jobView = createPicOneHeightView(list);
                    } else {
                        jobView = createPicOneView(list);
                    }
                } else if (list.size() <= 2) {
                    if (PicSize(list.get(0))) {
                        jobView = createPicTwoView(list);
                    } else {
                        jobView = createPicTwoHeightView(list);
                    }
                } else if (list.size() <= 3) {
                    if (PicSize(list.get(0))) {
                        if (PicSize(list.get(1)) && PicSize(list.get(2))) {
                            jobView = createPicThreeWidthView(list);
                        } else {
                            jobView = createPicThreeHeightView(list);
                        }
                    } else {
                        jobView = createPicThreeHeight2View(list);
                    }
                } else if (list.size() <= 4) {
                    if (PicSize(list.get(0))) {
                        jobView = createPicFourView(list);
                    } else {
                        jobView = createPicFourTopView(list);
                    }
                } else {
                    if (PicSize(list.get(0))) {
                        jobView = createPicFiveView(list);
                    } else {
                        jobView = createPicFiveTopView(list);
                    }
                }
                lyPic.addView(jobView);
            }

        }

    }

    public void buildSDImage(final LinearLayout lyPic, final ArrayList<String> list) {
        if (list.size() == 0) {
            lyPic.removeAllViews();
        }
        if (picList == null) {
            picList = new ArrayList<String>();
        }
        picList.clear();
        for (String outputPath : list){
            if (outputPath.contains(PathUtil.getFILEPrefix())){
                String url = outputPath.contains(PathUtil.getFILEPrefix()) ? outputPath : PathUtil.getFILEPrefix() + outputPath;
                picList.add(url);
            }
        }

        if (list != null && list.size() > 0) {
            lyPic.removeAllViews();
            if (list.size() > 0) {
                View jobView = null;
                if (list.size() <= 1) {
                    if (PicSDSize(list.get(0))) {
                        jobView = createPicOneHeightView(list);
                    } else {
                        jobView = createPicOneView(list);
                    }
                } else if (list.size() <= 2) {
                    if (PicSDSize(list.get(0))) {
                        jobView = createPicTwoView(list);
                    } else {
                        jobView = createPicTwoHeightView(list);
                    }
                } else if (list.size() <= 3) {
                    if (PicSDSize(list.get(0))) {
                        if (PicSDSize(list.get(1)) && PicSDSize(list.get(2))) {
                            jobView = createPicThreeWidthView(list);
                        } else {
                            jobView = createPicThreeHeightView(list);
                        }
                    } else {
                        jobView = createPicThreeHeight2View(list);
                    }
                } else if (list.size() <= 4) {
                    if (PicSDSize(list.get(0))) {
                        jobView = createPicFourView(list);
                    } else {
                        jobView = createPicFourTopView(list);
                    }

                } else {

                    if (PicSDSize(list.get(0))) {
                        jobView = createPicFiveView(list);
                    } else {
                        jobView = createPicFiveTopView(list);
                    }

                }
                lyPic.addView(jobView);
            }

        } else {
            lyPic.removeAllViewsInLayout();
        }

    }

    /**
     * 判断图片长宽比
     *
     * @param path
     * @return
     */
    private boolean PicSize(String path) {

        String[] paths = path.split("_");
        String picAddres = paths[paths.length - 1];
        synchronized (path) {
            if (!picAddres.contains("/")) {
                String sizeStr = picAddres.substring(0, picAddres.indexOf("."));
                String[] size = sizeStr.split("x");
                if (size.length < 2)return false;
                return div(Double.valueOf(size[1]), Double.valueOf(size[0]), 2) >= 1;
            } else {
                return false;
            }
        }
    }

    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     *
     * @param v1    被除数
     * @param v2    除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    private double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    /**
     * 判断图片长宽比
     *
     * @param path
     * @return
     */
    private boolean PicSDSize(String path) {
        boolean isHeight = false;
        synchronized (path) {
            String file = path.substring(7, path.length());
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file, opts);
            opts.inSampleSize = 1;
            opts.inJustDecodeBounds = false;
            BitmapFactory.decodeFile(file, opts);
            long width = opts.outWidth == 0 ? 1 : opts.outWidth;
            long height = opts.outHeight;
            isHeight = height / width >= 1;
        }
        return isHeight;
    }

    private View createPicOneView(final List<String> list) {
        final ImageView pic = (ImageView) view.findViewById(R.id.iv_dynamic_pic1);
        pic.setVisibility(View.VISIBLE);

        String imgUrl = list.get(0);

        display(imgUrl, pic);
        pic.setTag(IMG_TAT, 0);
        pic.setOnClickListener(this);
        return view;
    }

    private View createPicOneHeightView(final List<String> list) {
        final ImageView pic = (ImageView) view.findViewById(R.id.iv_dynamic_pic_height_1);
        pic.setVisibility(View.VISIBLE);

        String imgUrl = list.get(0);

        display(imgUrl, pic);
        pic.setTag(IMG_TAT, 0);
        pic.setOnClickListener(this);
        return view;
    }

    private View createPicTwoView(final List<String> list) {

        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic2);
        picLy.setVisibility(View.VISIBLE);
        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic2_left);
        ImageView picRight = (ImageView) view.findViewById(R.id.iv_dynamic_pic2_right);

        String imgLeftUrl = list.get(0);
        String imgRightUrl = list.get(1);

        display(imgLeftUrl, picLeft);
        display(imgRightUrl, picRight);
        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picRight.setTag(IMG_TAT, 1);
        picRight.setOnClickListener(this);
        return view;
    }

    private View createPicTwoHeightView(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic_height2);
        picLy.setVisibility(View.VISIBLE);
        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic2_height_top);
        ImageView picRight = (ImageView) view.findViewById(R.id.iv_dynamic_pic2_height_bottom);

        String imgLeftUrl = list.get(0);
        String imgRightUrl = list.get(1);

        display(imgLeftUrl, picLeft);
        display(imgRightUrl, picRight);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picRight.setTag(IMG_TAT, 1);
        picRight.setOnClickListener(this);
        return view;
    }

    private View createPicThreeWidthView(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic3_2);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_left_2);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_top_2);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_bottom_2);
        String imgLeftUrl = list.get(0);
        String imgTopUrl = list.get(1);
        String imgBottomUrl = list.get(2);

        display(imgLeftUrl, picLeft);
        display(imgTopUrl, picTop);
        display(imgBottomUrl, picBottom);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 1);
        picTop.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 2);
        picBottom.setOnClickListener(this);

        return view;
    }

    private View createPicThreeHeightView(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic3_3);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_left_3);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_top_3);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_bottom_2_3);

        String imgLeftUrl = list.get(0);
        String imgTopUrl = list.get(1);
        String imgBottomUrl = list.get(2);

        display(imgLeftUrl, picLeft);
        display(imgTopUrl, picTop);
        display(imgBottomUrl, picBottom);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 1);
        picTop.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 2);
        picBottom.setOnClickListener(this);

        return view;
    }


    private View createPicThreeHeight2View(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic3);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_left);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_top);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic3_bottom);

        String imgLeftUrl = list.get(0);
        String imgTopUrl = list.get(1);
        String imgBottomUrl = list.get(2);

        display(imgLeftUrl, picLeft);
        display(imgTopUrl, picTop);
        display(imgBottomUrl, picBottom);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 1);
        picTop.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 2);
        picBottom.setOnClickListener(this);

        return view;
    }

    private View createPicFourView(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic4);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_left);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_top);
        ImageView picCenter = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_center);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_bottom);


        String imgLeftUrl = list.get(0);
        String imgTopUrl = list.get(1);
        String imgCenterUrl = list.get(2);
        String imgBottomUrl = list.get(3);
        display(imgLeftUrl, picLeft);
        display(imgTopUrl, picTop);
        display(imgCenterUrl, picCenter);
        display(imgBottomUrl, picBottom);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 1);
        picTop.setOnClickListener(this);
        picCenter.setTag(IMG_TAT, 2);
        picCenter.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 3);
        picBottom.setOnClickListener(this);

        return view;
    }

    private View createPicFourTopView(final List<String> list) {
        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic4_2);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeft = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_left_2);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_top_2);
        ImageView picCenter = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_center_2);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic4_bottom_2);

        String imgLeftUrl = list.get(0);
        String imgTopUrl = list.get(1);
        String imgCenterUrl = list.get(2);
        String imgBottomUrl = list.get(3);

        display(imgLeftUrl, picLeft);
        display(imgTopUrl, picTop);
        display(imgCenterUrl, picCenter);
        display(imgBottomUrl, picBottom);

        picLeft.setTag(IMG_TAT, 0);
        picLeft.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 1);
        picTop.setOnClickListener(this);
        picCenter.setTag(IMG_TAT, 2);
        picCenter.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 3);
        picBottom.setOnClickListener(this);

        return view;
    }

    private View createPicFiveView(final List<String> list) {

        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic5);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeftTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_left_top);
        ImageView picLeftBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_left_bottom);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_top);
        ImageView picCenter = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_center);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_bottom);
        TextView tvPicNumber = (TextView) view.findViewById(R.id.tv_dynamic_pic_number);

        String imgLeftUrl = list.get(0);
        String imgLeftBottomUrl = list.get(1);
        String imgTopUrl = list.get(2);
        String imgCentterUrl = list.get(3);
        String imgBottomUrl = list.get(4);
        display(imgLeftUrl, picLeftTop);
        display(imgLeftBottomUrl, picLeftBottom);
        display(imgTopUrl, picTop);
        display(imgCentterUrl, picCenter);
        display(imgBottomUrl, picBottom);

        if (list.size() > 5) {
            tvPicNumber.setVisibility(View.VISIBLE);
        }

        picLeftTop.setTag(IMG_TAT, 0);
        picLeftTop.setOnClickListener(this);
        picLeftBottom.setTag(IMG_TAT, 1);
        picLeftBottom.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 2);
        picTop.setOnClickListener(this);
        picCenter.setTag(IMG_TAT, 3);
        picCenter.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 4);
        picBottom.setOnClickListener(this);

        tvPicNumber.setText("+" + Integer.valueOf(list.size() - 5));
        return view;
    }

    private View createPicFiveTopView(final List<String> list) {

        LinearLayout picLy = (LinearLayout) view.findViewById(R.id.lv_dynamic_pic5_2);
        picLy.setVisibility(View.VISIBLE);

        ImageView picLeftTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_left_top_2);
        ImageView picLeftBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_left_bottom_2);
        ImageView picTop = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_top_2);
        ImageView picCenter = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_center_2);
        ImageView picBottom = (ImageView) view.findViewById(R.id.iv_dynamic_pic5_bottom_2);
        TextView tvPicNumber = (TextView) view.findViewById(R.id.tv_dynamic_pic_number_2);

        String imgLeftUrl = list.get(0);
        String imgLeftBottomUrl = list.get(1);
        String imgTopUrl = list.get(2);
        String imgCentterUrl = list.get(3);
        String imgBottomUrl = list.get(4);

        display(imgLeftUrl, picLeftTop);
        display(imgLeftBottomUrl, picLeftBottom);
        display(imgTopUrl, picTop);
        display(imgCentterUrl, picCenter);
        display(imgBottomUrl, picBottom);

        if (list.size() > 5) {
            tvPicNumber.setVisibility(View.VISIBLE);
        }

        picLeftTop.setTag(IMG_TAT, 0);
        picLeftTop.setOnClickListener(this);
        picLeftBottom.setTag(IMG_TAT, 1);
        picLeftBottom.setOnClickListener(this);
        picTop.setTag(IMG_TAT, 2);
        picTop.setOnClickListener(this);
        picCenter.setTag(IMG_TAT, 3);
        picCenter.setOnClickListener(this);
        picBottom.setTag(IMG_TAT, 4);
        picBottom.setOnClickListener(this);

        tvPicNumber.setText("+" + Integer.valueOf(list.size() - 5));
        return view;
    }

    @Override
    public void onClick(View view) {
        switch ((Integer) view.getTag(IMG_TAT)) {
            case 0:
                listenter.imageList(0);
                break;
            case 1:
                listenter.imageList(1);
                break;
            case 2:
                listenter.imageList(2);
                break;
            case 3:
                listenter.imageList(3);
                break;
            case 4:
                listenter.imageList(4);
                break;
            case 5:
                listenter.imageList(5);
                break;

        }
    }

    public interface ImageListenter {
        void imageList(int position);
    }

    private void display(final String url, final ImageView pic) {
        if (url.contains("/storage/")){
//            Glide.with(mContext).load(url).centerCrop().into(pic);
            GlideUtil.loadImage(BaseApplication.appContext,url,pic);
            return;
        }

        String path = "";
        if (url.contains(".jpg")) {
            path = url.replace(".jpg", "_m.jpg");

        } else {
            path = url.replace(".png", "_m.png");
        }

        GlideUtil.loadImage(BaseApplication.appContext,path,new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                GlideUtil.loadImageDynamic(BaseApplication.appContext,url,pic);
//                Glide.with(mContext).load(url).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().dontAnimate().centerCrop().into(pic);
                return true;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        },pic);

//        Glide.with(mContext).load(path).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().dontAnimate().listener(new RequestListener<String, GlideDrawable>() {
//            @Override
//            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                Glide.with(mContext).load(url).skipMemoryCache(false).diskCacheStrategy(DiskCacheStrategy.NONE).crossFade().dontAnimate().centerCrop().into(pic);
//                return true;
//            }
//
//            @Override
//            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                return false;
//            }
//        }).centerCrop().into(pic);

    }

    private static int getViewFieldValue(Object obj, String fieldName) {
        int value = 0;
        try {
            Field field = ImageView.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            int fieldValue = field.getInt(obj);
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }

}
