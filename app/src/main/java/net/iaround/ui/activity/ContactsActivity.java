package net.iaround.ui.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import net.iaround.R;
import net.iaround.ui.fragment.AttentionFragment;
import net.iaround.ui.fragment.FansFragment;
import net.iaround.ui.fragment.FriendsFragment;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ClipPagerTitleView;

/**
 * Class: 联系人
 * Author：gh
 * Date: 2016/12/23 16:08
 * Email：jt_gaohang@163.com
 */
public class ContactsActivity extends BaseFragmentActivity implements View.OnClickListener{

    private MagicIndicator mMagicIndicator;
    private ViewPager viewPager;
    private ContactsAdapter adapter;

    private FriendsFragment tab1Fragement;
    private AttentionFragment tab2Fragement;
    private FansFragment tab3Fragement;
    private String[] title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_indector_title);
        initView();
    }

    public void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp_detailed_viewPager);
        mMagicIndicator = (MagicIndicator) findViewById(R.id.tb_detailed_indicator);
        title = getResources().getStringArray(R.array.contacts_top_list);
        adapter = new ContactsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        //后退
        findViewById(R.id.fl_detailed_back).setOnClickListener(this);
        findViewById(R.id.iv_detailed_left).setOnClickListener(this);
        initMagicIndicator();
    }

    private void initMagicIndicator() {
        mMagicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setSkimOver(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return title == null ? 0 : title.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ClipPagerTitleView clipPagerTitleView = new ClipPagerTitleView(context);
                clipPagerTitleView.setPaintBold();
                clipPagerTitleView.setText(title[index]);
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
        ViewPagerHelper.bind(mMagicIndicator, viewPager);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.fl_detailed_back:
            case R.id.iv_detailed_left:
                onBackPressed();
                break;
        }
    }
    private class ContactsAdapter extends FragmentPagerAdapter{

        public ContactsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                if (tab1Fragement == null) {
                    tab1Fragement = new FriendsFragment();
                }
                return tab1Fragement;
            } else if (position == 1) {
                if (tab2Fragement == null) {
                    tab2Fragement = new AttentionFragment();
                }
                return tab2Fragement;
            } else {
                if (tab3Fragement == null) {
                    tab3Fragement = new FansFragment();
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
            return title[position] ;
        }
    }

}



