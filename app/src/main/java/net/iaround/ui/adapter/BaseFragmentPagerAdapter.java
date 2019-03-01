package net.iaround.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BaseFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;

    private String[] mTitles;

    private List<Fragment> mFragmentList;

    public BaseFragmentPagerAdapter(FragmentManager fm,List<Fragment> fragmentList,String[] titles,Context context) {
        super(fm);
        this.mContext = context;
        this.mTitles = titles;
        this.mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList == null ? 0:mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
