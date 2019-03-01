package net.iaround.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Config;
import net.iaround.statistics.Statistics;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

/**
 * 排行榜数据
 * 作者：gh on 2017/6/10 22:43
 * <p>
 * 邮箱：jt_gaohang@163.com
 */
public class RankingFragment extends LazyLoadBaseFragment implements View.OnClickListener,MainFragmentActivity.PagerSelectRanking {

    private MagicIndicator mMagicIndicator;
    private CharmFragment tab1Fragement;
    private RegalFragment tab2Fragement;
    private SkillRankingFragment tab3Fragement;
    private ImageView ivRightCenter;
    private ImageView ivRightFirst;
    private ViewPager viewPager;
    private DynamicTitleAdapter contactsAdapter1;
    private String[] title;

    @Override
    protected int setContentView() {
        return R.layout.fragment_ranking;
    }

    @Override
    protected boolean lazyLoad() {
        setIsLoadOnce(true);
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initViews(getContentView());
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonFunction.log("RankingFragment", "onDestroy() into");
    }

    private void initViews(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.nears_viewPager);
        mMagicIndicator = (MagicIndicator) view.findViewById(R.id.tb_near_indicator);
        ivRightCenter = (ImageView) view.findViewById(R.id.iv_nears_seach);
        ivRightFirst = (ImageView) view.findViewById(R.id.iv_near_filter);
        ivRightFirst.setBackgroundResource(R.drawable.ranking_order_help);
        ivRightCenter.setVisibility(View.INVISIBLE);
        ivRightFirst.setVisibility(View.VISIBLE);
        ivRightFirst.setOnClickListener(this);
    }

    private void initData(){
        title = getResources().getStringArray(R.array.ranking_title);
        contactsAdapter1 = new DynamicTitleAdapter(getChildFragmentManager());
        viewPager.setAdapter(contactsAdapter1);
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(BaseApplication.appContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return title.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setText(title[index]);
                clipPagerTitleView.setTextSize(UIUtil.dip2px(context, 14));
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
                LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
                linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
                linePagerIndicator.setColors(Color.parseColor("#FF4064"));
                return linePagerIndicator;
            }
        });
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, viewPager);

        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    //统计
                    Statistics.onPageClick(Statistics.PAGE_RANKING_CHARM);
                } else if(position == 1){
                    //统计
                    Statistics.onPageClick(Statistics.PAGE_RANKING_RICH);
                }else if(position == 2){
                    //统计
                    Statistics.onPageClick(Statistics.PAGE_RANKING_SKILL);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_near_filter:
                String url = CommonFunction.getLangText(getActivity(), Config.sRankingHelp);
                Uri uri = Uri.parse(url);
                Intent i = new Intent(getActivity(), WebViewAvtivity.class);
                i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
                startActivity(i);
                break;
        }
    }

    private class DynamicTitleAdapter extends FragmentPagerAdapter {
        private FragmentManager fm;

        public DynamicTitleAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;

        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (tab1Fragement == null) {
                    tab1Fragement = new CharmFragment();
                }
                return tab1Fragement;
            } else if(position == 1) {
                if (tab2Fragement == null) {
                    tab2Fragement = new RegalFragment();
                }
                return tab2Fragement;
            }else {
                if (tab3Fragement == null) {
                    tab3Fragement = new SkillRankingFragment();
                }
                return tab3Fragement;
            }
        }

        @Override
        public int getCount() {
            return title.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }
        public void update(){
            if (null != tab1Fragement){
                tab1Fragement.update();
            }
            if (null != tab2Fragement){
                tab2Fragement.update();
            }
            if(null != tab3Fragement){
                tab3Fragement.update();

            }
        }

    }

    @Override
    public void onRankingSelected() {
        if(null==viewPager) return;
        if( 0 == viewPager.getCurrentItem() ){
            Statistics.onPageClick(Statistics.PAGE_RANKING_CHARM);
        }else if(1 == viewPager.getCurrentItem()){
            Statistics.onPageClick(Statistics.PAGE_RANKING_RICH);
        }else if(2 == viewPager.getCurrentItem()){
            Statistics.onPageClick(Statistics.PAGE_RANKING_SKILL);
        }
    }
}
