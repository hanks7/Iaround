package net.iaround.ui.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.ChatbarFansBean;
import net.iaround.tools.FaceManager;
import net.iaround.ui.view.HeadPhotoView;

import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/11 15:58
 * Email：15369302822@163.com
 */
public class ChatbarFansAdpater extends BaseAdapter {

    private Context mContext;
    private List<ChatbarFansBean.FansBean> msgs;
    private ChatbarFansBean.FansBean msgsBean;
    public ChatbarFansAdpater(Context context, List<ChatbarFansBean.FansBean> msgs)
    {
        this.mContext = context;
        this.msgs = msgs;
    }
    public void updateData(List<ChatbarFansBean.FansBean> msgs)
    {
        this.msgs = msgs;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return  msgs != null && msgs.size() > 0 ? msgs.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return msgs == null  ? null : msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FansHolder holder = null;
        if(convertView == null)
        {
            convertView = View.inflate( mContext , R.layout.item_personal_view , null );
            holder = new FansHolder(convertView );
            convertView.setTag( holder );
        }else
        {
            holder = ( FansHolder ) convertView.getTag( );
        }
        msgsBean = msgs.get(position);
        final ChatbarFansBean.FansBean.UserBean userBean = msgsBean.getUser();
        if (userBean != null)
        {
            //头像
            holder.icon.executeChat(R.drawable.iaround_default_img,userBean.getIcon(),userBean.getSvip(),userBean.getViplevel(),-1);
            //昵称
            String nickname = userBean.getNickname();
            SpannableString spannableString = FaceManager.getInstance(mContext).parseIconForString(mContext, nickname, 13, null);
            holder.name.setText(spannableString);
            //年龄
            holder.tvAge.setText(""+userBean.getAge());
            if (userBean.getGender().equals("m"))
            {
                holder.lyAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
                holder.ivAge.setImageResource(R.drawable.thread_register_man_select);
            }else
            {
                holder.lyAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                holder.ivAge.setImageResource(R.drawable.thread_register_woman_select);
            }

            String content = userBean.getSelftext();
            if (content == null || content.length() <= 0 || content.equals("null")) {
                content = userBean.getSelftext();
            }
            SpannableString spContent = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, content, 13, null);
            if (spContent != null && content != null && content.length() > 0) {
                holder.notes.setVisibility(View.VISIBLE);
                holder.notes.setText(spContent);
            }
            //不展示距离和时间
//            if (userBean.getDistance() < 0) { // 不可知
//                holder.location.setText(TimeFormat.timeFormat1(mContext, userBean.getLastonlinetime()) + " · ");/// 1000
//            } else {
//                holder.location.setText(TimeFormat.timeFormat1(mContext, userBean.getLastonlinetime()) + " · " + CommonFunction.covertSelfDistance(userBean.getDistance()));
//            }

//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (userBean.getForbid() == 1) {
//                        DialogUtil.showOneButtonDialog(mContext,
//                                mContext.getString(R.string.dialog_title),
//                                mContext.getString(R.string.this_user_is_forbiden),
//                                mContext.getString(R.string.ok), null);
//                        return;
//                    }
//                    User user = userList.get(position);
//                    user.setRelationship(3);
//                    ChatPersonal.skipToChatPersonal(mContext, user);
//                }
//            });
        }
        return convertView;
    }

    /** 适配器的控件缓存容器 */
    class FansHolder {

        HeadPhotoView icon;
        TextView location;
        TextView name;
        LinearLayout lyAge;
        TextView tvAge;
        ImageView ivAge;
        TextView constellation;
        TextView notes;

        public FansHolder(View convertView)
        {
            icon = (HeadPhotoView) convertView.findViewById(R.id.user_icon);
            location = (TextView) convertView.findViewById(R.id.user_location);

            name = (TextView) convertView.findViewById(R.id.user_name);
            lyAge = (LinearLayout) convertView.findViewById(R.id.ly_user_age_left);
            ivAge = (ImageView) convertView.findViewById(R.id.iv_user_sex_left);
            tvAge = (TextView) convertView.findViewById(R.id.tv_user_age_left);
            constellation = (TextView) convertView.findViewById(R.id.tv_user_constellation);

            notes = (TextView) convertView.findViewById(R.id.user_notes);
        }

    }
}
