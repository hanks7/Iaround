package net.iaround.ui.view;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.InnerJump;
import net.iaround.ui.datamodel.ResourceBanner;
import net.iaround.ui.group.view.CustomViewPager;

import java.util.List;

public class FlyBanner  extends LinearLayout {

    private static final int WHAT_AUTO_PLAY = 1000;

    private CustomViewPager viewPager;

    private View mRootView;

    private IAViewpagerIndector mViewpagerIndicator;

    private FrameLayout mFlBananer;

    private CardView mCardView;

    private Context mContext;

    private FlyPageAdapter adapter;

    //网络图片资源
    private List<ResourceBanner> resourceBannerList;

    //自动播放时间
    private int mAutoPalyTime = 4000;

    private Handler mAutoPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = viewPager.getCurrentItem();
            viewPager.setCurrentItem(++currentItem);
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }
    };

    public FlyBanner(Context context) {
        this(context,null);
    }

    public FlyBanner(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FlyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.setOrientation(VERTICAL);
        mRootView = LayoutInflater.from(context).inflate( R.layout.ad_view , this );
        viewPager = (CustomViewPager) mRootView.findViewById( R.id.viewpager );
        mViewpagerIndicator = (IAViewpagerIndector) mRootView.findViewById( R.id.viewpager_indicator );
        mFlBananer = (FrameLayout) mRootView.findViewById( R.id.fl_bananer );
        mCardView = (CardView) mRootView.findViewById( R.id.card_view );
        //初始化ViewPager
        viewPager.addOnPageChangeListener(mOnPageChangeListener);
    }

    /**
     * 设置网络图片
     * isShowIndector  安智等应用市场  附近人有bananer  bananer 和其他页面样式不同  此值用于区分
     * @param urls
     */
    public void setImagesUrl(List<ResourceBanner> urls,boolean isShowIndector) {
        if(!isShowIndector){
            mViewpagerIndicator.setVisibility(GONE);
            mFlBananer.setBackgroundColor(Color.TRANSPARENT);
            mFlBananer.setPadding(0,0,0,0);
            mCardView.setRadius(0);
            ViewGroup.LayoutParams layoutParams = mCardView.getLayoutParams();
            layoutParams.height = DensityUtils.dp2px(mContext,80);
            mCardView.setLayoutParams(layoutParams);
        }
        mAutoPlayHandler.removeMessages(WHAT_AUTO_PLAY);
        //加载网络图片
        this.resourceBannerList = urls;
        //设置ViewPager
        if(adapter == null){
            adapter = new FlyPageAdapter();
            viewPager.setAdapter(adapter);
        }else {
            adapter.notifyDataSetChanged();
        }
        mViewpagerIndicator.setCount(resourceBannerList.size());
        if(resourceBannerList.size() > 1){
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }else {
            viewPager.setIsCanFlip(false);
        }
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            position = position % resourceBannerList.size();
            mViewpagerIndicator.setPosition(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private class FlyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            position = position % resourceBannerList.size();
            ImageView imageView = new ImageView(BaseApplication.appContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(BaseApplication.appContext).load(resourceBannerList.get(position).getImageUrl()).into(imageView);
            container.addView(imageView);

            final int finalPosition = position;
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String url = resourceBannerList.get(finalPosition).getLink();
                    if (url != null) {
                        if (url.contains("http://") | url.contains("https://")) {
                            InnerJump.Jump(mContext,url, true);
                        }
                    } else {
                        // 无法跳转
                        CommonFunction.toastMsg(mContext, "无跳转");
                    }
                }
            });
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(resourceBannerList.size() > 1){
            if(viewPager.getCurrentItem() == (resourceBannerList.size() - 1)){
                getParent().requestDisallowInterceptTouchEvent(true);//请求父布局不要拦截子view的touch事件
            }
        }else {
            getParent().requestDisallowInterceptTouchEvent(false);
        }

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mAutoPlayHandler.removeMessages(WHAT_AUTO_PLAY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if(resourceBannerList.size() > 1){
                    mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}