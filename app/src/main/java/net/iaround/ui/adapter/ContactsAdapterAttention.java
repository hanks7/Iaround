package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.database.FriendModel;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.List;


/**
 * Class: 附近中的个人界面
 * Author：gh
 * Date: 2016/12/16 22:49
 * Email：jt_gaohang@163.com
 */
public class ContactsAdapterAttention extends BaseAdapter {

    private Context mContext;
    private List<FriendListBean.AttentionsBean> mList;
    private ArrayList<User> userList;


    public ContactsAdapterAttention(Context context, List<FriendListBean.AttentionsBean> mList) {
        this.mContext = context;
        this.mList = mList;


    }

    public void updataList(List<FriendListBean.AttentionsBean> contactses) {
        this.mList = contactses;
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
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
        userList = FriendModel.getInstance(mContext).getFollowsList();
        ContactsHolder holder = null;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.item_personal_view, null);
            holder = new ContactsHolder();
            holder.icon = (HeadPhotoView) convertView.findViewById(R.id.user_icon);
            holder.location = (TextView) convertView.findViewById(R.id.user_location);

            holder.name = (TextView) convertView.findViewById(R.id.user_name);
            holder.lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
            holder.ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
            holder.tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
            holder.constellation = (TextView) convertView.findViewById(R.id.tv_user_constellation);

            holder.notes = (TextView) convertView.findViewById(R.id.user_notes);

            convertView.setTag(holder);
        } else {
            holder = (ContactsHolder) convertView.getTag();
        }
        if (mList != null) {
            FriendListBean.AttentionsBean itemBean = mList.get(position);

            final User user = new User();
            user.setIcon(itemBean.getUser().getIcon());
            user.setSVip(itemBean.getUser().getSvip());
            user.setViplevel(itemBean.getUser().getViplevel());
            user.setUid(itemBean.getUser().getUserid());
            holder.icon.execute(user);

            String name = itemBean.getUser().getNickname();
            if (name == null || name.length() <= 0 || name.equals("null")) {
                name = itemBean.getUser().getNickname();
            }
            SpannableString spName = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, name, 0, null);

            if (itemBean.getUser().getVip() > 0) {
                holder.name.setTextColor(Color.parseColor("#ff0000"));
            } else {
                holder.name.setTextColor(Color.parseColor("#000000"));
            }
            holder.name.setText(spName);

            if ("m".equals(itemBean.getUser().getGender())) {
                holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
                holder.lyAge.setBackgroundResource(R.drawable.encounter_dynamic_man_circle_bg);
            } else {
                holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
                holder.lyAge.setBackgroundResource(R.drawable.encounter_dynamic_woman_circle_bg);
            }
            try {
                holder.tvAge.setText("" + itemBean.getUser().getAge());
            } catch (Exception e) {
                e.printStackTrace();
            }

//        holder.constellation.setText(TimeFormat.date2Constellation(mContext, itemBean.birthday));//jiqiang星座没有返回

            String content = itemBean.getUser().getSelftext();
            if (content == null || content.length() <= 0 || content.equals("null")) {
                content = itemBean.getUser().getSelftext();
            }
            SpannableString spContent = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, content, 13, null);
            if (spContent != null && content != null && content.length() > 0) {
                holder.notes.setVisibility(View.VISIBLE);
                holder.notes.setText(spContent);
            }else{
                holder.notes.setText("");
            }

            if (itemBean.getUser().getDistance() < 0) { // 不可知
                holder.location.setText(TimeFormat.timeFormat1(mContext, itemBean.getUser().getLastonlinetime()) + " · ");/// 1000
            } else {
                holder.location.setText(TimeFormat.timeFormat1(mContext, itemBean.getUser().getLastonlinetime()) + " · " + CommonFunction.covertSelfDistance(itemBean.getDistance()));
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.isForbid()) {
                        DialogUtil.showOneButtonDialog(mContext,
                                mContext.getString(R.string.dialog_title),
                                mContext.getString(R.string.this_user_is_forbiden),
                                mContext.getString(R.string.ok), null);
                        return;
                    }
                    if(userList.size() > position){
                        User user = userList.get(position);
                        user.setRelationship(3);
                        ChatPersonal.skipToChatPersonal(mContext, user);
                    }

                }
            });

        }

        return convertView;
    }

    class ContactsHolder {

        HeadPhotoView icon;
        TextView location;
        TextView name;
        LinearLayout lyAge;
        TextView tvAge;
        ImageView ivAge;
        TextView constellation;
        TextView notes;

    }

}
