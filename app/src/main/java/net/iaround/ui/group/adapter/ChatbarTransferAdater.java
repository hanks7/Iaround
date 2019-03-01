package net.iaround.ui.group.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.FaceManager;
import net.iaround.ui.datamodel.GroupUser;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.group.bean.GroupSearchUser;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/28 18:06
 * Email：15369302822@163.com
 */
public class ChatbarTransferAdater extends BaseAdapter{

    private Context mContext;
    private List<GroupSearchUser> memberList;
    private LayoutInflater inflater;
//    private List<GroupSearchUser> selectUser = new ArrayList<>();
    private List<Boolean> selectUserStatus = new ArrayList<>();
    private SeletUserToTransgerCallback seletUserToTransgerCallback;
    private GroupSearchUser selectUesr = new GroupSearchUser();

    public ChatbarTransferAdater(Context context, List<GroupSearchUser> memberList,List<Boolean> selectUserStatus,SeletUserToTransgerCallback seletUserToTransgerCallback) {
        this.mContext = context;
        this.memberList = memberList;
        this.selectUserStatus = selectUserStatus;
        this.seletUserToTransgerCallback = seletUserToTransgerCallback;
        inflater = LayoutInflater.from(context);
    }

    public void updateUserData(List<GroupSearchUser> memberList,List<Boolean> selectUserStatus) {
        this.memberList = memberList;
        this.selectUserStatus = selectUserStatus;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (memberList != null && memberList.size() > 0) {
            return memberList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.chatbar_friend_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        GroupSearchUser groupSearchUser = memberList.get(position);
        GroupUser user = groupSearchUser.user;
        //头像
        viewHolder.icon.executeChat(R.drawable.iaround_default_img, user.getIcon(), user.svip, user.viplevel, -1);
        //头像
        SpannableString name;
        if (!"".equals(user.nickname)) {
            name = FaceManager.getInstance(mContext).parseIconForString(mContext, user.nickname, 13, null);
            viewHolder.name.setText(name);
        } else {
            viewHolder.name.setText(user.userid + "");
        }
        if (position == memberList.size() - 1)
        {
            viewHolder.bottomLine.setVisibility(View.GONE);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectUserStatus.size(); i++) {
                    if (position == i)
                        selectUserStatus.set(position,true);
                    else
                        selectUserStatus.set(i,false);
                }
                selectUesr = memberList.get(position);
                seletUserToTransgerCallback.setSelectUser(selectUesr);
                notifyDataSetChanged();
            }
        });
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selectUserStatus.size(); i++) {
                    if (position == i)
                        selectUserStatus.set(position,true);
                    else
                        selectUserStatus.set(i,false);
                }
                selectUesr = memberList.get(position);
                seletUserToTransgerCallback.setSelectUser(selectUesr);
                notifyDataSetChanged();
            }
        });

        viewHolder.checkBox.setChecked(selectUserStatus.get(position));

        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.icon)
        HeadPhotoView icon;
        @BindView(R.id.check_box)
        CheckBox checkBox;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.bottom_line)
        View bottomLine;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public interface SeletUserToTransgerCallback
    {
//        void setSelectUser(List<GroupSearchUser> selectUser);
        void setSelectUser(GroupSearchUser selectUser);
    }
}
