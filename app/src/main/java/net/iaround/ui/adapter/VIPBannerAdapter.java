package net.iaround.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import net.iaround.BaseApplication;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.datamodel.ResourceBanner;

import java.util.ArrayList;

/**
 * @author：liush on 2016/12/16 18:11
 */
public class VIPBannerAdapter extends PagerAdapter {

    private int[] imageRes;
    private ArrayList<ResourceBanner> topbanners;
    private Context context;

    public VIPBannerAdapter(int[] imageRes) {
        this.imageRes = imageRes;
    }

    public VIPBannerAdapter(Context context, ArrayList<ResourceBanner> topbanners) {
        this.context = context;
        this.topbanners = topbanners;
    }

    @Override
    public int getCount() {
//        if (imageRes == null) {
//            return 0;
//        }
//        return imageRes.length;
        if (topbanners == null) {
            return 0;
        }
        return topbanners.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(container.getContext());
        GlideUtil.loadImage(BaseApplication.appContext, topbanners.get(position).getImageUrl(), imageView);
//        imageView.setBackgroundResource(imageRes[position]);
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setDataChange(ArrayList<ResourceBanner> topbanners) {
        this.topbanners = topbanners;
        notifyDataSetChanged();

    }
}
