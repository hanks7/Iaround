package net.iaround.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.GroupUser;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.interfaces.ChatbarSendPersonalSocketListener;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.ui.view.dialog.CustomChatBarDialog;

import java.util.List;

/**
 * Class: 聊吧在线人数
 * Author：gh
 * Date: 2017/6/14 20:04
 * Email：jt_gaohang@163.com
 */
public class GroupChatTopicOnLineAdapter extends
        RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private List<GroupUser> mDatas;
    private Context mContext;
    private CustomChatBarDialog mCustomChatBarDialog;
    private User user;
    private String groupid;
    private GroupUser currentUser;

    private int micNumber;
    public ChatbarSendPersonalSocketListener mSendListener;

    public GroupChatTopicOnLineAdapter(Context context, List<GroupUser> datats, String groupid, ChatbarSendPersonalSocketListener listener) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        this.groupid = groupid;
        this.mSendListener=listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        HeadPhotoView mAvatar;
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    /**
     * 创建ViewHolder
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_group_chat_topic_online,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mAvatar = (HeadPhotoView) view
                .findViewById(R.id.user_icon);
        return viewHolder;
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupUser avatar = mDatas.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        //图片地址
        if (avatar == null)
            return;
        user = new User();
        user.setUid(avatar.getUserid());
        user.setIcon(avatar.getIcon());
        if(avatar.getUserid() == Common.getInstance().loginUser.getUid()){
            String verifyicon = Common.getInstance().loginUser.getVerifyicon();
            if (verifyicon != null && !TextUtils.isEmpty(verifyicon)){
                user.setIcon(verifyicon);
            }
        }
        user.setViplevel(avatar.getViplevel());
        user.setSVip(avatar.getSvip());
        user.setAge(avatar.getAge());
        user.setGroupRole(avatar.getGroup_role());
        user.setNickname(avatar.getNickname());
        user.setNoteName(avatar.getNotes());

        //加载头像
        viewHolder.mAvatar.executeChat(R.drawable.iaround_default_img,user.getIcon(),user.getSVip(),user.getViplevel(),-1);
        setListener(viewHolder.mAvatar, user);

    }

    /***
     * 设置头像的点击事件
     */
    private void setListener(HeadPhotoView userIcon, final User user)
    {
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long creatorUid = Long.valueOf(GroupModel.getInstance().getGroupOwnerId());

                //如果点击的是自己的头像
                if (Common.getInstance().loginUser.getUid() == user.getUid())
                {
                    mCustomChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                    mCustomChatBarDialog.setUser(Common.getInstance().loginUser,user.getGroupRole(),groupid);
                    mCustomChatBarDialog.isClickMySelf(true);
                    mCustomChatBarDialog.setMicNumber(getMicNumber());
                    mCustomChatBarDialog.show();
                } else {
                    if (Common.getInstance().loginUser.getUid() == creatorUid) { // 是群的创建者
                        mCustomChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        mCustomChatBarDialog.setUser(user, 0, groupid);
                        mCustomChatBarDialog.setMicNumber(getMicNumber());
                        mCustomChatBarDialog.show();
                    } else if (currentUser != null && currentUser.getGroup_role() == 1) { // 为管理员
                        mCustomChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        mCustomChatBarDialog.setUser(user, 1, groupid);
                        mCustomChatBarDialog.setMicNumber(getMicNumber());
                        mCustomChatBarDialog.show();
                    } else {
                        /***
                         * 点击角色是圈成员，无论被点击的是圈主 圈管理员，圈成员都
                         * 打开他人资料
                         */
                        mCustomChatBarDialog = new CustomChatBarDialog(mContext,mSendListener);
                        mCustomChatBarDialog.setUser(user, 3, groupid);
                        mCustomChatBarDialog.setMicNumber(getMicNumber());
                        mCustomChatBarDialog.show();
                    }
                }
            }
        });
    }
    public void updateData(List<GroupUser> mDatas) {
        if (mDatas != null) {
            this.mDatas = mDatas;
            this.notifyDataSetChanged();
        }
    }
    public void updateCurrentUser(GroupUser currentUser)
    {
        if (currentUser != null)
        {
            this.currentUser = currentUser;
        }
        this.notifyDataSetChanged();
    }

    public int getMicNumber() {
        return micNumber;
    }

    public void setMicNumber(int micNumber) {
        this.micNumber = micNumber;
    }
}
