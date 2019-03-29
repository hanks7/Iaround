package net.iaround.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.adapter.BaseFragmentPagerAdapter;
import net.iaround.utils.OnClickUtil;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ScaleTransitionPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.ArrayList;
import java.util.List;

public class HomeContainerFragment extends LazyLoadBaseFragment {

    private Context mContext;

    private MagicIndicator mMagicIndicator;

    private ViewPager mViewPager;

    private HomePageFragment mHomePageFragment;

    private Near1Fragment mNear1Fragment;

    private VoiceFragment mVoiceFragment;

    private DynamicCenterFragment mDynamicCenterFragment;

    private List<Fragment> fragmentList;

    private ArrayList<String> titles = new ArrayList<>();

    private LinearLayout mLlRight;

    private int mIndex;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    protected int setContentView() {
        return R.layout.home_container_fragment;
    }

    @Override
    protected boolean lazyLoad() {
        setIsLoadOnce(true);
        initData();
        return false;
    }

    @Override
    protected void init(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = getContentView();
        mViewPager = (ViewPager) contentView.findViewById(R.id.home_container_fragment_viewPager);
        mMagicIndicator = (MagicIndicator) contentView.findViewById(R.id.home_container_fragment_magicindicator);
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        mLlRight = contentView.findViewById(R.id.ll_right);
    }

    private void initData() {
        if (fragmentList == null) {
            fragmentList = new ArrayList<>();
        }
        Resources res = mContext.getResources();
        final int showWhat = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.ACCOMPANY_IS_SHOW);
        int isShowVoice = SharedPreferenceUtil.getInstance(mContext).getInt(SharedPreferenceUtil.VOICE_IS_SHOW);
        if (showWhat == 1) {
            if(isShowVoice == 1){
//                titles = BaseApplication.appContext.getResources().getStringArray(R.array.home_container_title);
                titles.add(res.getString(R.string.play_with_you));
                titles.add(res.getString(R.string.chat_sound));
            }else {
//                titles = new String[]{mContext.getResources().getString(R.string.play_with_you)};
                titles.add(res.getString(R.string.play_with_you));
            }
            if (mHomePageFragment == null) {
                mHomePageFragment = new HomePageFragment();
            }
            fragmentList.add(mHomePageFragment);

        } else {
            if(isShowVoice == 1){//显示语聊
//                titles = new String[]{mContext.getResources().getString(R.string.near_fragment), mContext.getResources().getString(R.string.chat_sound)};
                titles.add(res.getString(R.string.near_fragment));
                titles.add(res.getString(R.string.chat_sound));
            }else {
//                titles = new String[]{mContext.getResources().getString(R.string.near_fragment)};
                titles.add(res.getString(R.string.near_fragment));
            }
            if (mNear1Fragment == null) {
                mNear1Fragment = new Near1Fragment();
            }
            fragmentList.add(mNear1Fragment);

        }
        if(isShowVoice == 1){
            if (mVoiceFragment == null) {
                mVoiceFragment = new VoiceFragment();
            }
            fragmentList.add(mVoiceFragment);
        }

        titles.add(res.getString(R.string.dynamic_tab_text));
        if(mDynamicCenterFragment == null){
            mDynamicCenterFragment = new DynamicCenterFragment();
        }
        fragmentList.add(mDynamicCenterFragment);

        String[] strings = new String[titles.size()];
        titles.toArray(strings);

        BaseFragmentPagerAdapter baseFragmentPagerAdapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), fragmentList, strings, mContext);
        mViewPager.setAdapter(baseFragmentPagerAdapter);

        CommonNavigator commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles == null ? 0 : titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                SimplePagerTitleView simplePagerTitleView = new ScaleTransitionPagerTitleView(context);
                simplePagerTitleView.setText(titles.get(index));
                simplePagerTitleView.setTextSize(24);
                simplePagerTitleView.setNormalColor(Color.parseColor("#333333"));
                simplePagerTitleView.setSelectedColor(Color.parseColor("#FF4064"));
                simplePagerTitleView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int currentItem = mViewPager.getCurrentItem();
                        if (currentItem != index) {
                            mViewPager.setCurrentItem(index);
                        } else {
                            if (index == 1) {
                                if (OnClickUtil.isFastClick()) {
                                    if (mVoiceFragment != null) {
                                        mVoiceFragment.smoothScrollToStart();
                                    }
                                }
                            }
                        }
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                return null;
            }
        });

        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mViewPager);
//        mViewPager.setCurrentItem(Common.getInstance().getDefaultTopShow() - 1);
        //判断首页默认显示哪一页
        int indexTop = Common.getInstance().getDefaultTopShow() - 1;
        //如果首页显示的附近，并且当前在附近的则fragment，显示右上角筛选按钮
        if (indexTop == 0 && showWhat == 0) {
            mLlRight.setVisibility(View.VISIBLE);
        } else {
            mLlRight.setVisibility(View.GONE);
        }
        mViewPager.setCurrentItem(indexTop);

        mLlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIndex == 0) {
                    if (mNear1Fragment == null) {
                        mNear1Fragment = new Near1Fragment();
                    }
                    mNear1Fragment.toFilter();
                }
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mIndex = position;
                //如果首页显示的附近，则显示右上角筛选按钮
                if (position == 0 && showWhat == 0) {
                    mLlRight.setVisibility(View.VISIBLE);
                } else {
                    mLlRight.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
