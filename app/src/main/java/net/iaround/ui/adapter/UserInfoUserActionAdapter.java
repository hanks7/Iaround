package net.iaround.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.glide.GlideUtil;

import java.util.List;

/**
 * @author：liush on 2016/12/6 16:29
 */
public class UserInfoUserActionAdapter extends BaseAdapter {

    List<String> datas;

    public UserInfoUserActionAdapter(List<String> datas) {
        this.datas = datas;
    }

    @Override
    public int getCount() {
        if (datas == null) {
            return 0;
        }
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        ImageView ivAction = new ImageView(parent.getContext());
        View view = View.inflate(parent.getContext(), R.layout.userinfo_action_img, null);
        ImageView ivAction = (ImageView) view.findViewById(R.id.iv_pic);
        GlideUtil.loadImage(BaseApplication.appContext, datas.get(position), ivAction, R.drawable.iaroud_normal_dynamic, R.drawable.iaroud_normal_dynamic);//iaround_default_img
//        GlideUtil.loadCircleImage(BaseApplication.appContext, datas.get(position), ivAction, R.drawable.iaround_default_img, R.drawable.iaround_default_img);
//        ImageViewUtil.getDefault().fadeInRoundLoadImage(datas.get(position), ivAction, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle, null);
        return view;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
