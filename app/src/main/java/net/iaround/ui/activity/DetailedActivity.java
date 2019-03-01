package net.iaround.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.ui.adapter.BaseFragmentPagerAdapter;
import net.iaround.ui.fragment.DiamondDetailFragment;
import net.iaround.ui.fragment.StarDetailFragment;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

import java.util.ArrayList;
import java.util.List;


/**
 * 钱包明细
 * 作者：gh on 2018/1/16 14:43
 * <p>
 * 邮箱：jt_gaohang@163.com
 */
public class DetailedActivity extends BaseActivity {

    private ViewPager viewPager;

    private MagicIndicator mMagicIndicator;

    private DiamondDetailFragment tab1Fragement;

    private StarDetailFragment tab2Fragement;

    private List<Fragment> fragmentList;

    private String[] titles;

    public static void jump(Context context) {
        Intent intent = new Intent(context, DetailedActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed_activity);
        findViewById(R.id.fl_detailed_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initViews();
    }

    private void initViews() {
        viewPager = (ViewPager) findViewById(R.id.vp_detailed_viewPager);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.tb_detailed_indicator);
        titles = BaseApplication.appContext.getResources().getStringArray(R.array.detailed_title);
        if (tab1Fragement == null) {
            tab1Fragement = new DiamondDetailFragment();
        }
        if (tab2Fragement == null) {
            tab2Fragement = new StarDetailFragment();
        }
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        fragmentList.add(tab1Fragement);
        fragmentList.add(tab2Fragement);
        BaseFragmentPagerAdapter baseFragmentPagerAdapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), fragmentList, titles, mContext);
        viewPager.setAdapter(baseFragmentPagerAdapter);
        initMagicIndicator();
    }

    private void initMagicIndicator() {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(BaseApplication.appContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titles.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setPaintBold();
                clipPagerTitleView.setText(titles[index]);
                clipPagerTitleView.setTextColor(Color.parseColor("#333333"));
                clipPagerTitleView.setClipColor(Color.parseColor("#FF4064"));
                clipPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return clipPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        //每个tab添加间距
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerDrawable(new ColorDrawable() {
            @Override
            public int getIntrinsicWidth() {
                return UIUtil.dip2px(BaseApplication.appContext, 20);
            }
        });
        ViewPagerHelper.bind(mMagicIndicator, viewPager);
    }
}
