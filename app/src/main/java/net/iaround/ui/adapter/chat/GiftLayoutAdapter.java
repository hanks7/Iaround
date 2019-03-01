package net.iaround.ui.adapter.chat;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Class:礼物面板ViewPager的适配器
 * Author：yuchao
 * Date: 2017/7/22 16:17
 * Email：15369302822@163.com
 */
public class GiftLayoutAdapter extends PagerAdapter
{
    private List<View> mViewPagerGridList;
    public GiftLayoutAdapter(List<View> viewPagerGridList)
    {
        this.mViewPagerGridList = viewPagerGridList;
    }

    @Override
    public int getCount() {
        return mViewPagerGridList.size() > 0 && mViewPagerGridList != null? mViewPagerGridList.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewPagerGridList.get(position));
        return mViewPagerGridList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
