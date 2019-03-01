package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.tools.glide.GlideUtil;
import net.iaround.ui.activity.PictureDetailsActivity;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * @author：liush on 2017/1/19 23:49
 */
public class OtherInfoVPAdapter extends PagerAdapter {

    private ArrayList<String> picsThum;
    private ArrayList<String> pics;
    private Context mContext;

    public OtherInfoVPAdapter(ArrayList<String> picsThum, ArrayList<String> pics) {
        this.picsThum = picsThum;
        this.pics = pics;
    }

    public OtherInfoVPAdapter(Context context, ArrayList<String> picsThum, ArrayList<String> pics) {
        if (pics.size() == 9) {
            pics.remove(pics.size() - 1);
        }
        this.picsThum = picsThum;
        this.pics = pics;
        this.mContext = context;
    }

    @Override
    public int getCount() {
//        if(picsThum == null){
//            return 0;
//        }
//        return picsThum.size() > 8 ? 8 : picsThum.size();
        if (pics == null) {
            return 0;
        }
        return pics.size() > 8 ? 8 : pics.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView view = (ImageView) View.inflate(container.getContext(), R.layout.userinfo_header_img, null);
//        Bitmap bitmap = readBitMap(mContext, R.drawable.chat_bar_title_default_bg);
//        Drawable drawable = new BitmapDrawable(bitmap);
//        view.setImageDrawable(drawable);
//        GlideUtil.loadImage(container.getContext(), picsThum.get(position), view, R.drawable.default_avatar_round_light, R.drawable.default_pitcure_small);

//        Glide.with(container.getContext()).load(pics.get(position)).thumbnail(Glide.with(container.getContext()).load(R.drawable.loading_gif))
//                .error(R.drawable.encounter_header_default).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(view);

        String picUrl = "";
        if (pics.get(position) != null) {
            if (pics.get(position).contains("_s.")) {
                if (pics.get(position).contains("_s.png")) {
                    picUrl = pics.get(position).replace("_s.png", ".png");
                } else {
                    picUrl = pics.get(position).replace("_s.jpg", ".jpg");
                }
            } else {
                picUrl = pics.get(position);
            }
        }

//        Glide.with(mContext).load(picUrl).centerCrop().placeholder(R.drawable.loading_gif).error(R.drawable.encounter_header_default).into(view);
//        Glide.with(mContext).load(picUrl).placeholder(R.drawable.loading_gif).error(R.drawable.encounter_header_default_other).into(view);
        GlideUtil.loadImageInfo(mContext,picUrl,view,R.drawable.loading_gif,R.drawable.encounter_header_default_other);
        setOnclickListener(container.getContext(), view, position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setOnclickListener(final Context context, View view, final int position) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureDetailsActivity.launch(context, pics, position);
            }
        });
    }

    public static Bitmap readBitMap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
