package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.connector.protocol.SignClickHttpProtocol;

import java.util.ArrayList;
import java.util.List;

/**
 * 引导页
 */
public class GuideActivity extends BaseActivity {
    private ViewPager view_pager;
    private Button button;
    private List<ImageView> arrayList;


    private Button newButton;
    private ImageView ivGuidebgIcon;

    private LinearLayout mDotLl;
    private View mRedDot;
    private int[] mImages = new int[]{R.drawable.z_guide_view_one, R.drawable.z_guide_view_two};
    private List<ImageView> mImageViewList = new ArrayList<>();
    private int mDotSpacing; //2个指示点间的距离
    private boolean action;
    private int activationCount;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        view_pager = (ViewPager) findViewById(R.id.vp_guide);
        button = (Button) findViewById(R.id.btn_guide);
//        newButton = (Button) findViewById(R.id.btn_new_guide);

        action = getIntent().getBooleanExtra("class_Actio", false);
        mDotLl = (LinearLayout) findViewById(R.id.ll_dot);
        mRedDot = findViewById(R.id.view_dot);


//        ImageView bgImag = (ImageView)findViewById(R.id.chatbar_guide);

//        if (Config.getVideoChatOpen() == 0){
//            newButton.setText(getResString(R.string.chatbar_guide_btn_text_1));
//            newButton.setTextColor(getResources().getColor(R.color.login_btn));
//            bgImag.setBackgroundResource(R.drawable.chatbar_guide_icon_1);
//        }else{
//            newButton.setText(getResString(R.string.chatbar_guide_btn_text_2));
//            newButton.setTextColor(getResources().getColor(R.color.white));
////            bgImag.setBackgroundResource(R.drawable.chatbar_guide_icon_2);
//        }

//        initData();


        view_pager.setVisibility(View.VISIBLE);
        mDotLl.setVisibility(View.GONE);

        initDotAndImageView();
        initViewPager();
        initListener();
        isFirstStart(GuideActivity.this);
    }

    private void initDotAndImageView() {
        int width = (int) getResources().getDimension(R.dimen.x24);
        int height = (int) getResources().getDimension(R.dimen.y24);
        for (int i = 0; i < mImages.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(mImages[i]);
            mImageViewList.add(imageView);
//            ImageView dot = new ImageView(this);
//            dot.setImageResource(R.drawable.z_guide_dot);
//            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(width, height);
//            if (i > 0) {
//                dotLp.leftMargin = (int) getResources().getDimension(R.dimen.x21);
//            }
//            mDotLl.addView(dot, dotLp);
        }

    }

//    private void initData() {
//        arrayList = new ArrayList<ImageView>();
//
//        ImageView imageView = new ImageView(getApplicationContext());
//        imageView.setBackgroundResource(R.drawable.z_guide_view_one);
//
//        ImageView imageView1 = new ImageView(getApplicationContext());
//        imageView1.setBackgroundResource(R.drawable.z_guide_view_three);
//
////        ImageView imageView2 = new ImageView(getApplicationContext());
////        imageView2.setBackgroundResource(R.drawable.z_guide_view_two);//z_guide_view_two
//
//        arrayList.add(imageView);
//        arrayList.add(imageView1);
////        arrayList.add(imageView2);
//    }

//    	class MyPagerAdapter extends PagerAdapter{
//		@Override
//		public int getCount() {
//			return arrayList.size();
//		}
//
//		@Override
//		public boolean isViewFromObject(View arg0, Object arg1) {
//			return arg0 == arg1;
//		}
//		@Override
//		public Object instantiateItem(ViewGroup container, int position) {
//			container.addView(arrayList.get(position));
//			return arrayList.get(position);
//		}
//
//		@Override
//		public void destroyItem(ViewGroup container, int position, Object object) {
//			container.removeView((View)object);
//		}
//	}
    private void initViewPager() {
        view_pager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViewList.get(position));
                return mImageViewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void initListener() {
        //监听viewTree的布局变化,获取2个指示点间的距离
//        mDotLl.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onGlobalLayout() {
//                mDotLl.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mDotSpacing = mDotLl.getChildAt(1).getLeft() - mDotLl.getChildAt(0).getLeft();
//            }
//        });
        view_pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 滑动的时候回调
             * @param position 当前页面位置
             * @param positionOffset 位置的变化的百分比
             * @param positionOffsetPixels 移动的像素
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                //通过修改mRedDot的leftMargin来改变红点的移动位置随手指的移动而移动
                //当前的marginLeft值 = 2个指示点间的距离*移动的百分比+初始值
//                float currMarginLeft = mDotSpacing * positionOffset + position * mDotSpacing;
//                RelativeLayout.LayoutParams redLp = (RelativeLayout.LayoutParams) mRedDot.getLayoutParams();
//                redLp.leftMargin = (int) currMarginLeft;
//                mRedDot.setLayoutParams(redLp);

            }

            @Override
            public void onPageSelected(int position) {
                if (position == mImageViewList.size() - 1) {
                    //最后一页显示"开始体验"按钮
                    button.setVisibility(View.VISIBLE);
//                    button.setVisibility(View.GONE);
                } else {
                    button.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!action) {
                    Intent intent = new Intent(GuideActivity.this, MainFragmentActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        });

//        newButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!action)
//                {
//                    Intent intent = new Intent(GuideActivity.this, MainFragmentActivity.class);
//                    startActivity(intent);
//                }
//                finish();
//            }
//        });
    }

    public boolean isFirstStart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SHARE_APP_TAG", 0);
        Boolean isFirst = preferences.getBoolean("FIRSTStart", true);
        if (isFirst) {// 第一次
            preferences.edit().putBoolean("FIRSTStart", false).commit();
            activationCount++;
            SignClickHttpProtocol.getInstance().syncGetStatistics(GuideActivity.this, "0", "0", "" + activationCount,"0", null);
            return true;
        } else {

            return false;
        }
    }

}
