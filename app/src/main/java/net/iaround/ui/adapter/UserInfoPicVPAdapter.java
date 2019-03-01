package net.iaround.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.PictureDetailsActivity;
import net.iaround.ui.activity.ShowHeadPicActivity;

import java.util.ArrayList;

/**
 * @author：liush on 2016/12/5 14:47
 */
public class UserInfoPicVPAdapter extends PagerAdapter {

    private ArrayList<String> picsThum;
    private ArrayList<String> pics;
    private static final int MAX_SIZE = 18;

    public UserInfoPicVPAdapter() {}

    public UserInfoPicVPAdapter(ArrayList<String> picsThum, ArrayList<String> pics) {
        this.picsThum = picsThum;
        this.pics = pics;
    }

    @Override
    public int getCount() {
//        if (picsThum == null || picsThum.size()==0) {
//            return 1;
//        }
        if (pics == null || pics.size()==0) {
            return 1;
        }
        /*if(picsThum.size()>MAX_SIZE){
            picsThum = (ArrayList<String>) picsThum.subList(0,MAX_SIZE);
        }*/
        int count = (int)Math.ceil(((double) pics.size()) / 6);
        return count>3 ? 3:count;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, int position) {
        View view = View.inflate(container.getContext(), R.layout.activity_userinfo_pic_pager, null);
        if(picsThum == null || picsThum.size()==0){//假如服务器返回的pics为空。一般不会出现这种情况
            ImageView ivHead1 = (ImageView) view.findViewById(R.id.iv_head_1);
            ivHead1.setImageResource(R.drawable.default_avatar_rect_light);
            container.addView(view);
            return view;
        }

        LinearLayout pic1 = (LinearLayout) view.findViewById(R.id.ly_user_info_pic1);
        LinearLayout pic2 = (LinearLayout) view.findViewById(R.id.ly_user_info_pic2);

        if (picsThum.size() <= 3 ){
            pic2.setVisibility(View.GONE);
        }

        int startIndex = 6*position , i=0;
        if(startIndex+i < picsThum.size()){
            final int curPosition = startIndex + i;
            ImageView ivHead1 = (ImageView) view.findViewById(R.id.iv_head_1);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(curPosition), ivHead1, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(curPosition), ivHead1, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
            setOnclickListener(container.getContext(), ivHead1, curPosition);
            i++;
        }
        if(startIndex+i < picsThum.size()){
            ImageView ivHead2 = (ImageView) view.findViewById(R.id.iv_head_2);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(startIndex + i), ivHead2, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(startIndex + i), ivHead2, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
            setOnclickListener(container.getContext(), ivHead2, startIndex + i);
            i++;
        }
        if(startIndex+i < picsThum.size()){
            ImageView ivHead3 = (ImageView) view.findViewById(R.id.iv_head_3);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(startIndex + i), ivHead3, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(startIndex + i), ivHead3, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
            setOnclickListener(container.getContext(), ivHead3, startIndex + i);
            i++;
        }
        if(startIndex+i < picsThum.size()){
            ImageView ivHead4 = (ImageView) view.findViewById(R.id.iv_head_4);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(startIndex + i), ivHead4, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(startIndex + i), ivHead4, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
            setOnclickListener(container.getContext(), ivHead4, startIndex + i);
            i++;
        }
        if(startIndex+i < picsThum.size()){
            ImageView ivHead5 = (ImageView) view.findViewById(R.id.iv_head_5);
            GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(startIndex + i), ivHead5, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//            ImageViewUtil.getDefault().loadImage(picsThum.get(startIndex + i), ivHead5, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
            setOnclickListener(container.getContext(), ivHead5, startIndex + i);
            i++;
        }
        if(startIndex+i < picsThum.size()){
            ImageView ivHead6 = (ImageView) view.findViewById(R.id.iv_head_6);
            if(startIndex+i == MAX_SIZE-1 && picsThum.size()>MAX_SIZE){
                ivHead6.setImageResource(R.drawable.userinfo_more_pic);
                ivHead6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowHeadPicActivity.startAction(container.getContext(), picsThum, pics);
                    }
                });
            } else {
                GlideUtil.loadImage(BaseApplication.appContext,picsThum.get(startIndex + i), ivHead6, R.drawable.default_pitcure_small_angle, R.drawable.default_pitcure_small_angle);
//                ImageViewUtil.getDefault().loadImage(picsThum.get(startIndex + i), ivHead6, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small_angle);
//                setOnclickListener(container.getContext(), ivHead6, startIndex + i);
            }
        }

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setOnclickListener(final Context context, View view, final int position){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDetailsActivity.launch(context, pics, position);
            }
        });
    }
}
