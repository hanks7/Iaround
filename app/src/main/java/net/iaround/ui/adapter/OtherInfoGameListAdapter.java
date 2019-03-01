package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.UserInfoGameInfoBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.glide.GlideUtil;

import java.util.ArrayList;

/**
 * Created by yz on 2018/8/22.
 * 他人资料界面已认证游戏列表
 */

public class OtherInfoGameListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<UserInfoGameInfoBean> mInfoBeans;

    public OtherInfoGameListAdapter(Context mContext, ArrayList<UserInfoGameInfoBean> mInfoBeans) {
        this.mContext = mContext;
        this.mInfoBeans = mInfoBeans;
    }

    @Override
    public int getCount() {
        if (mInfoBeans != null) {
            return mInfoBeans.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mInfoBeans != null && mInfoBeans.size() > 0) {
            return mInfoBeans.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_userinfo_game_item, null);
            holder = new ViewHolder();
            holder.iv_game_head = (ImageView) convertView.findViewById(R.id.iv_game_head);
            holder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
            holder.tv_game_level = (TextView) convertView.findViewById(R.id.tv_game_level);
            holder.tv_game_tip = (TextView) convertView.findViewById(R.id.tv_game_tip);
            holder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
            holder.rb_star = (RatingBar) convertView.findViewById(R.id.rb_star);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserInfoGameInfoBean info = mInfoBeans.get(position);
        GlideUtil.loadRoundImageNew(BaseApplication.appContext, info.image, 10, holder.iv_game_head, R.drawable.default_avatar_rect_light, R.drawable.default_avatar_rect_light);
        holder.tv_game_name.setText(info.game_type_name);
        if (!TextUtils.isEmpty(info.game_type_rank)) {
            holder.tv_game_level.setVisibility(View.VISIBLE);
            GradientDrawable grad = (GradientDrawable) holder.tv_game_level.getBackground();
            grad.setColor(CommonFunction.getRankColor(info.game_level));
            holder.tv_game_level.setText(info.game_type_rank + "");
        } else {
            holder.tv_game_level.setVisibility(View.INVISIBLE);
        }
        if(info.game_id == Constants.AUDIO_CHAT_GAME_ID){
            holder.tv_game_tip.setVisibility(View.GONE);
            holder.rb_star.setVisibility(View.GONE);
        }else {
            holder.rb_star.setVisibility(View.VISIBLE);
            holder.tv_game_tip.setVisibility(View.VISIBLE);
            holder.tv_game_tip.setText(String.format(BaseApplication.appContext.getString(R.string.order_times), info.order_num + "") + " | " + info.tag);
            holder.rb_star.setRating(info.star/2);
        }
        holder.tv_price.setText(info.price + " " + info.Unit);
        return convertView;
    }

    class ViewHolder {
        public ImageView iv_game_head;
        public TextView tv_game_name;
        public TextView tv_game_level;
        public TextView tv_game_tip;
        public TextView tv_price;
        public RatingBar rb_star;

    }
}
