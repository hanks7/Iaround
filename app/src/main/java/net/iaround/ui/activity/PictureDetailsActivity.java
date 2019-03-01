package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.PathUtil;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.space.SpacePictureActivity;
import net.iaround.ui.view.encounter.IconPagerAdapter;

import java.util.ArrayList;

import cn.finalteam.galleryfinal.widget.zoonview.PhotoView;

/**
 * Class: 照片预览
 * Author：gh
 * Date: 2016/12/12 16:55
 * Email：jt_gaohang@163.com
 */
public class PictureDetailsActivity extends BaseFragmentActivity {

    private ProgressBar mProgressBar;
    private RelativeLayout rlImg;
    protected ViewPager mViewPager;
    private TextView picNumber;
    private ArrayList<String> mSmallPhotoIds; // 图片ID列表

    private int mPhotoIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_detail);

        rlImg = (RelativeLayout) findViewById(R.id.rl_img);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        picNumber = (TextView) findViewById(R.id.right_text_num);

        mPhotoIndex = getIntent().getIntExtra("position", 0);

        if (getIntent().hasExtra("smallPhotos")) {
            mSmallPhotoIds = getIntent().getStringArrayListExtra("smallPhotos");
        }

        String number = mPhotoIndex + 1 + "/" + mSmallPhotoIds.size();
        picNumber.setText(number);

        mViewPager.setAdapter(new PictureDetailsAdapter());
        mViewPager.setCurrentItem(mPhotoIndex);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                String number = position + 1 + "/" + mSmallPhotoIds.size();
                picNumber.setText(number);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    /**
     * 读取网络图片
     *
     * @param context
     * @param iconUrl
     * @param position
     */
    public static void launch(Context context, ArrayList<String> iconUrl, int position) {
//        Intent i = new Intent(context, PictureDetailsActivity.class);
//        i.putExtra("position", position);
//        if (iconUrl != null && iconUrl.size() > 0) {
//            i.putExtra("smallPhotos", iconUrl);
//        }
//        context.startActivity(i);
        SpacePictureActivity.launch(context,iconUrl,position);
    }

    /**
     * 读取本地图片
     *
     * @param context
     * @param iconUrl
     * @param position
     */
    public static void launchStore(Context context, ArrayList<String> iconUrl, int position) {

        ArrayList<String> urlList = new ArrayList<String>();
        for (String url : iconUrl) {
            String uri = url.contains(PathUtil.getFILEPrefix()) ? url : PathUtil.getFILEPrefix() + url;
            urlList.add(uri);
        }
        Intent i = new Intent(context, PictureDetailsActivity.class);
        i.putExtra("position", position);
        if (iconUrl != null && iconUrl.size() > 0) {
            i.putExtra("smallPhotos", urlList);
        }
        context.startActivity(i);
    }

    public class PictureDetailsAdapter extends PagerAdapter implements IconPagerAdapter {

        @Override
        public String getIconResId(int index) {
            return mSmallPhotoIds.get(index % mSmallPhotoIds.size());
        }

        @Override
        public int getCount() {
            return mSmallPhotoIds.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            view.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imgview = LayoutInflater.from(PictureDetailsActivity.this).inflate(
                    R.layout.view_picture_details, null);
            PhotoView img = (PhotoView) imgview
                    .findViewById(R.id.iv_picture_details);
            String url = mSmallPhotoIds.get(position);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setResult(104);
                    finish();
                }
            });


            View viewHolder = imgview;
            setProgressBarVisible(true);
            String picUrl;
            if (url != null)
            {
//                if (PicSize(url)) {
//                    String[] paths = url.split("_");
//                    if (url.contains(".png")) {
//                        picUrl = paths[0] + "_" + paths[1] + ".png";
//                    } else {
//                        picUrl = paths[0] + "_" + paths[1] + ".jpg";
//                    }
//
//                } else {
                    if (url.contains("_s.png")) {
                        picUrl = url.replace("_s.png", ".png");
                    } else {
                        picUrl = url.replace("_s.jpg", ".jpg");
                    }

//                }
                GlideUtil.loadImagePicture(PictureDetailsActivity.this,picUrl,new GlideDrawableImageViewTarget(img) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        //在这里添加一些图片加载完成的操作
                        setProgressBarVisible(false);
                    }
                });
//                Glide.with(PictureDetailsActivity.this).load(picUrl).into(new GlideDrawableImageViewTarget(img) {
//                    @Override
//                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
//                        super.onResourceReady(drawable, anim);
//                        //在这里添加一些图片加载完成的操作
//                        setProgressBarVisible(false);
//                    }
//                });
            }

            view.addView(viewHolder);
            return viewHolder;
        }

    }

    /**
     * 判断图片长宽比
     *
     * @param path
     * @return
     */
    private boolean PicSize(String path) {
        if (path != null) {
            if (path.contains("/storage/")) {
                return false;
            }
            String[] paths = path.split("_");
            String picAddres = paths[paths.length -1];
            //            String picAddres = paths[2];
//            String[] size = picAddres.split("x");
//            if (Integer.valueOf(size[1]) / Integer.valueOf(size[0]) >= 1) {
//                return true;
//            } else {
//                return false;
//            }
            return !picAddres.contains("/");
        } else {
            return false;
        }

    }

    private void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void setProgressBarVisible(boolean isvisible) {
        try {
            if (isvisible && mProgressBar == null) {
                // 滚动条
                mProgressBar = new ProgressBar(this);
                int dp_24 = (int) (getResources().getDimension(R.dimen.x5) * 24);
                RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams(dp_24, dp_24);
                lpPb.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                mProgressBar.setIndeterminate(true);
                mProgressBar.setIndeterminateDrawable(getResources().getDrawable(
                        R.drawable.pull_ref_pb));
                mProgressBar.setLayoutParams(lpPb);
                rlImg.addView(mProgressBar);
            }

            if (mProgressBar != null) {
                mProgressBar.setVisibility(isvisible ? View.VISIBLE : View.GONE);
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

}
