package net.iaround.ui.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.ChatbarFriendsBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FaceManager;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:邀请好友，邀请聊天列表展示适配器
 * Author：yuchao
 * Date: 2017/8/15 18:00
 * Email：15369302822@163.com
 */
public class ChatbarFriendsAdapter extends BaseAdapter {

    private Context mContext;
    private List<ChatbarFriendsBean.UsersBean> mList;
    private ArrayList<Boolean> checkboxUsers = new ArrayList<>();

    private  InviteFriendsListCallback inviteFriendsListCallback;
    private InviteRecommendListCallback inviteRecommendListCallback;

    public ChatbarFriendsAdapter(Context context, List<ChatbarFriendsBean.UsersBean> mList,ArrayList<Boolean> checkboxUsers,
                                 InviteFriendsListCallback inviteFriendsListCallback,InviteRecommendListCallback inviteRecommendListCallback) {
        this.mContext = context;
        this.mList = mList;
        this.checkboxUsers = checkboxUsers;
        this.inviteFriendsListCallback = inviteFriendsListCallback;
        this.inviteRecommendListCallback = inviteRecommendListCallback;
    }
    public void updataList(List<ChatbarFriendsBean.UsersBean> contactses, ArrayList<Boolean> checkboxUsers) {
        this.mList = contactses;
        this.checkboxUsers = checkboxUsers;
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return mList != null && mList.size() > 0 ? mList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.chatbar_friend_item,parent,false);
        }
        viewHolder = new ViewHolder(convertView);
        ChatbarFriendsBean.UsersBean usersBean = mList.get(position);
        final ChatbarFriendsBean.UsersBean.UserBean user = usersBean.getUser();
        if (user != null)
        {
            viewHolder.icon.execute(convertToUserFriend(usersBean));
            SpannableString nickname = FaceManager.getInstance(mContext).parseIconForString(mContext,user.getNickname(),13,null);
            viewHolder.tvName.setText(nickname);
            if ("".equals(nickname))
            {
                viewHolder.tvName.setText(user.getUserid() +"");
            }
            viewHolder.selectCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkboxUsers.get(position))
                    {
                        viewHolder.selectCheckBox.setChecked(false);
                        checkboxUsers.set(position,false);
                    }else
                    {
                        viewHolder.selectCheckBox.setChecked(true);
                        checkboxUsers.set(position,true);
                    }
                    notifyDataSetChanged();
                }
            });
            if (position == mList.size() - 1) {
                viewHolder.divider.setVisibility(View.GONE);
            } else {
                viewHolder.divider.setVisibility(View.VISIBLE);
            }
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkboxUsers.get(position))
                {
                    viewHolder.selectCheckBox.setChecked(false);
                    checkboxUsers.set(position,false);
                }else
                {
                    viewHolder.selectCheckBox.setChecked(true);
                    checkboxUsers.set(position,true);
                }

                notifyDataSetChanged();
            }
        });
        viewHolder.selectCheckBox.setChecked(checkboxUsers.get(position));
        if (inviteFriendsListCallback != null)
            inviteFriendsListCallback.getInviteFriendList(checkboxUsers);
        if (inviteRecommendListCallback != null)
            inviteRecommendListCallback.getInviteRecommendList(checkboxUsers);
        return convertView;
    }
    public class ViewHolder
    {
        private HeadPhotoView icon;
        private CheckBox selectCheckBox;
        private TextView tvName;
        private View  divider;

        public ViewHolder(View convertView)
        {
            icon = (HeadPhotoView) convertView.findViewById(R.id.icon);
            selectCheckBox = (CheckBox) convertView.findViewById(R.id.check_box);
            tvName = (TextView) convertView.findViewById(R.id.name);
            divider = convertView.findViewById(R.id.bottom_line);
        }
    }
    private User convertToUserFriend(ChatbarFriendsBean.UsersBean contactUser) {
        if (contactUser != null) {
            User user = new User();
            ChatbarFriendsBean.UsersBean.UserBean baseUserInfo = contactUser.getUser();
            user.setIcon(baseUserInfo.getIcon());
            user.setUid(baseUserInfo.getUserid());
            user.setNoteName(baseUserInfo.getNickname());
            user.setViplevel(baseUserInfo.getViplevel());
            user.setSVip(baseUserInfo.getSvip());
            user.setNickname(baseUserInfo.getNotes());

            int sex = 2;
            if (!CommonFunction.isEmptyOrNullStr(baseUserInfo.getGender())) {
                if (baseUserInfo.getGender().equals("m")) {
                    sex = 1;
                } else if ("f".equals(baseUserInfo.getGender())) {
                    sex = 2;
                }
            }
            user.setSex(sex);
            user.setAge(baseUserInfo.getAge());
            return user;
        }
        return null;
    }
    public List<Boolean> getIsSelected()
    {
        return checkboxUsers;
    }
    public void setIsSelected(ArrayList<Boolean> checkboxUsers)
    {
        this.checkboxUsers = checkboxUsers;
    }
    /**
     * 邀请好友的回调
     */
    public interface InviteFriendsListCallback
    {
        void getInviteFriendList(List<Boolean> inviteUsers);
    }

    /**
     * 邀请推荐用户的回调
     */
    public interface InviteRecommendListCallback
    {
        void getInviteRecommendList(List<Boolean> inviteUsers);
    }
}
