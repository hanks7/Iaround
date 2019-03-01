package net.iaround.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Constants;
import net.iaround.model.entity.GreetListItemBean;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.List;


/**
 * Class: 点赞适配器
 * Author：gh
 * Date: 2016/12/16 22:49
 * Email：jt_gaohang@163.com
 */
public class UserLikeAdapter extends BaseAdapter {

    private Context mContext;
    private List<GreetListItemBean> mList;

    public UserLikeAdapter(Context context, List<GreetListItemBean> list) {
        this.mContext = context;
        this.mList = list;
    }

    public void updataList(List<GreetListItemBean> data) {
        this.mList = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (mList != null) {
            return mList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ContactsHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_personal_view, null);
            holder = new ContactsHolder();
            holder.icon = (HeadPhotoView) convertView.findViewById(R.id.user_icon);
            holder.name = (TextView) convertView.findViewById(R.id.user_name);
            holder.lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
            holder.ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
            holder.time = (TextView) convertView.findViewById(R.id.user_location);
            holder.tvNotes = (TextView) convertView.findViewById(R.id.user_notes);
            holder.tvConstellation = (TextView) convertView.findViewById(R.id.tv_user_constellation);
            convertView.setTag(holder);
        } else {
            holder = (ContactsHolder) convertView.getTag();
        }

        GreetListItemBean itemBean = mList.get(position);

        final User user = new User();
        user.setIcon(itemBean.getUser().icon);
        user.setSVip(itemBean.getUser().svip);
        user.setUid(itemBean.getUser().userid);
        user.setNickname(itemBean.getUser().nickname);
        user.setNoteName(itemBean.getUser().notes);
        user.setViplevel(itemBean.getUser().viplevel);
        user.setSign(itemBean.getUser().selftext);
        user.setJob(itemBean.getUser().occupation >= 0 ? itemBean.getUser().occupation : -1);
        holder.icon.execute(user);

        String name = itemBean.getUser().nickname;
        if (name == null || name.length() <= 0 || name.equals("null")) {
            name = itemBean.getUser().nickname;
        }
        SpannableString spName = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, name, 0, null);

        if (itemBean.getUser().vip > 0) {
            holder.name.setTextColor(Color.parseColor("#ff0000"));
        } else {
            holder.name.setTextColor(Color.parseColor("#000000"));
        }
        holder.name.setText(spName);

        if ("m".equals(itemBean.getUser().gender)) {
            holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
            holder.lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
        } else {
            holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
            holder.lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
        }
        try {
            holder.tvAge.setText(String.valueOf(itemBean.getUser().age));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 年龄
        LinearLayout lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
        ImageView ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
        TextView tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
        if ("m".equals(itemBean.getUser().gender)) {
            ivAge.setImageResource(R.drawable.thread_register_man_select);
            lyAge.setBackgroundResource(R.drawable.encounter_man_circel_bg);
        } else {
            ivAge.setImageResource(R.drawable.thread_register_woman_select);
            lyAge.setBackgroundResource(R.drawable.encounter_woman_circel_bg);
        }
        try {
            tvAge.setText(String.valueOf(itemBean.getUser().age));
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvConstellation.setText(String.valueOf(itemBean.getUser().age));

        String content = itemBean.getUser().notes;
        if (content == null || content.length() <= 0 || content.equals("null")) {
            content = itemBean.getUser().notes;
        }
        SpannableString spContent = FaceManager.getInstance(mContext)
                .parseIconForString(mContext, content, 0, null);

        holder.tvNotes.setText(spContent);

        holder.time.setText(TimeFormat.timeFormat4(mContext, itemBean.datetime));
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(mContext, OtherInfoActivity.class);
                intent.putExtra(Constants.UID, user.getUid());
                intent.putExtra("user", user);
                mContext.startActivity(intent);
            }
        });
        return convertView;
    }

    class ContactsHolder {

        HeadPhotoView icon;
        TextView name;
        LinearLayout lyAge;
        TextView tvAge;
        ImageView ivAge;
        TextView time;
        TextView tvNotes;
        TextView tvConstellation;

    }

}
