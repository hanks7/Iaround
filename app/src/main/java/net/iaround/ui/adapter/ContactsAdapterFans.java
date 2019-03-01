package net.iaround.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.FriendHttpProtocol;
import net.iaround.model.database.FriendModel;
import net.iaround.model.im.BaseServerBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.contacts.bean.FriendListBean;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Class: 附近中的个人界面
 * Author：gh
 * Date: 2016/12/16 22:49
 * Email：jt_gaohang@163.com
 */
public class ContactsAdapterFans extends BaseAdapter {

    private Context mContext;
    //    private List<Contacts> mList;
    private List<FriendListBean.AttentionsBean> mList;
    private List<FriendListBean.AttentionsBean> mListTemp = new ArrayList<>();
    private User user;
    private HttpCallBack httpCallBack;
    private HashMap<Long, Long> flagToUid = new HashMap<Long, Long>();
    private int PAGE_SIZE = 20;
    private final static int MSG_GET_LIST_DATA = 1000;
    private final static int MSG_STOP_PULLING = 1001;
    private final static int MSG_NOTIFY_CHANGE = 1002;
    private final static int MSG_CLOSE_WAIT_DIALOG = 1003;
    private final static int MSG_SHOW_EMPTY = 1004;
    private boolean focused = false;
    private BaseServerBean bean;
    private ArrayList<User> fans;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {


                case MSG_NOTIFY_CHANGE: {
                    mList = mListTemp;
                    notifyDataSetChanged();
                }
                break;

                case MSG_SHOW_EMPTY: {
//                    setTitleCount(passFansCount);
//                    emptyView.setVisibility(View.VISIBLE);
//                    listView.setVisibility(View.INVISIBLE);
                }
                break;
            }
        }

    };

    public ContactsAdapterFans(Context context, List<FriendListBean.AttentionsBean> mList) {
        this.mContext = context;
        this.mList = mList;
        this.mListTemp = mList;
        httpCallBack = new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                handler.sendEmptyMessage(MSG_STOP_PULLING);
                if (flagToUid.containsKey(flag)) {// 关注好友返回
                    bean = GsonUtil.getInstance().getServerBean(result,
                            BaseServerBean.class);
                    long uid = flagToUid.get(flag);
                    if (bean.isSuccess() || bean.error == 4301) {// 关注成功或已经关注
                        for (int i = 0; i < mListTemp.size(); i++) {
                            if (mListTemp.get(i).getUser().getUserid() == uid) {// 将对应好友项的关系改成好友
                                focused = true;
                                mListTemp.get(i).setRelation(1);
                                NewFansModel.getInstance().updateRelation(mContext, uid, 1, Common.getInstance().loginUser.getUid());//yuchao 取消注释
                                break;
                            }
                        }
                    } else {// 关注失败
                        ErrorCode.toastError(mContext, bean.error);
                    }
                    flagToUid.remove(flag);
                    handler.sendEmptyMessage(MSG_NOTIFY_CHANGE);
                }
            }

            @Override
            public void onGeneralError(int e, long flag) {
                handler.sendEmptyMessage(MSG_STOP_PULLING);
                ErrorCode.toastError(mContext, e);
                if (flagToUid.containsKey(flag)) {
                    flagToUid.remove(flag);
                    handler.sendEmptyMessage(MSG_NOTIFY_CHANGE);
                }
            }
        };

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
        fans = FriendModel.getInstance(mContext).getFansList();
        ContactsHolder viewHolder = new ContactsHolder();
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.my_fans_item, null);
            viewHolder.icon = (HeadPhotoView) convertView.findViewById(R.id.friend_icon);
            viewHolder.nickname = (TextView) convertView.findViewById(R.id.tvNickName);
            viewHolder.sexAge = (TextView) convertView.findViewById(R.id.tvAge);
            viewHolder.selftext = (TextView) convertView.findViewById(R.id.self_text);
            viewHolder.status = (TextView) convertView.findViewById(R.id.time_tag);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.diatance);
            viewHolder.newTag = (ImageView) convertView.findViewById(R.id.new_tag);
            viewHolder.focusBtn = (TextView) convertView.findViewById(R.id.focus);
            viewHolder.rlSexAge = (RelativeLayout) convertView.findViewById(R.id.rlSexAge);
            viewHolder.ivSex = (ImageView) convertView.findViewById(R.id.ivSex);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ContactsHolder) convertView.getTag();
        }
        if (mList != null) {
            final FriendListBean.AttentionsBean itemBean = mList.get(position);
            User user = new User();
            user.setIcon(itemBean.getUser().getIcon());
            user.setSVip(itemBean.getUser().getSvip());
            user.setViplevel(itemBean.getUser().getViplevel());
            user.setUid(itemBean.getUser().getUserid());
            if (itemBean.getUser().getForbid() != 0) {
                user.setForbid(true);
            } else {
                user.setForbid(false);
            }

            viewHolder.icon.execute(user);

            String name = itemBean.getUser().getNickname();
            if (name == null || name.length() <= 0 || name.equals("null")) {
                name = itemBean.getUser().getNickname();
            }
            SpannableString spName = FaceManager.getInstance(mContext)
                    .parseIconForString(mContext, name, 0, null);

            if (itemBean.getUser().getVip() > 0) {
                viewHolder.nickname.setTextColor(Color.parseColor("#ff0000"));
            } else {
                viewHolder.nickname.setTextColor(Color.parseColor("#000000"));
            }
            viewHolder.nickname.setText(spName);

            //性别和年龄
            if (itemBean.getUser().getAge() <= 0) {
                if ("m".equals(itemBean.getUser().getGender())) {
                    viewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
                    viewHolder.ivSex.setImageResource(R.drawable.thread_register_man_select);
                } else {
                    viewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                    viewHolder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
                }
                viewHolder.sexAge.setText(R.string.unknown);
            } else {
                if ("m".equals(itemBean.getUser().getGender())) {
                    viewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_man_bg);
                    viewHolder.ivSex.setImageResource(R.drawable.thread_register_man_select);
                } else {
                    viewHolder.rlSexAge.setBackgroundResource(R.drawable.group_member_age_girl_bg);
                    viewHolder.ivSex.setImageResource(R.drawable.thread_register_woman_select);
                }
                viewHolder.sexAge.setText(String.valueOf(itemBean.getUser().getAge()));
            }


//        holder.constellation.setText(TimeFormat.date2Constellation(mContext, itemBean.birthday));//jiqiang星座没有返回

//            String content = itemBean.getUser().getSelftext();
//            if (content == null || content.length() <= 0 || content.equals("null")) {
//                content = itemBean.getUser().getSelftext();
//            }
//            SpannableString spContent = FaceManager.getInstance(mContext)
//                    .parseIconForString(mContext, content, 0, null);
//
//            holder.notes.setText(spContent);
//
//            if (itemBean.getUser().getDistance() < 0) { // 不可知
//                holder.location.setText(TimeFormat.timeFormat5(mContext, itemBean.getUser().getLastonlinetime() / 1000) + " · ");
//            } else {
//                holder.location.setText(TimeFormat.timeFormat5(mContext, itemBean.getUser().getLastonlinetime() / 1000) + " · " + CommonFunction.covertSelfDistance(itemBean.getDistance()));
//            }
//
//            convertView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    User user = userList.get(position);
//                    if (user.isForbid()) {
//                        DialogUtil.showOneButtonDialog(mContext,
//                                mContext.getString(R.string.dialog_title),
//                                mContext.getString(R.string.this_user_is_forbiden),
//                                mContext.getString(R.string.ok), null);
//                        return;
//                    }
//                    ChatPersonal.skipToChatPersonal(mContext, user);
//                }
//            });


            // 签名
            SpannableString selfText = FaceManager.getInstance(mContext)
                    .parseIconForString(viewHolder.selftext, mContext, itemBean.getUser().getSelftext(),
                            13);
            if (selfText != null && !itemBean.getUser().getSelftext().isEmpty()) {
                viewHolder.selftext.setVisibility(View.VISIBLE);
                viewHolder.selftext.setText(selfText.toString());
            }else {
                viewHolder.selftext.setText("");
            }

            // 距离
            int distance = itemBean.getDistance();
            String disText = "";
            if (distance < 0) { // 不可知
                disText = " · " + mContext.getString(R.string.unable_to_get_distance);
            } else {
                disText = " · " + CommonFunction.covertSelfDistance(distance);
            }
            viewHolder.distance.setText(disText);

            // 在线状态
            String time = TimeFormat.timeFormat1(mContext, itemBean.getUser().getLastonlinetime());
            if (time == null || time.length() < 0) {
                time = mContext.getString(R.string.unable_to_get_time);
            }
            viewHolder.status.setText(time);

            // new
            if (itemBean.getNewflag() == 1)
                viewHolder.newTag.setVisibility(View.GONE);
            else
                viewHolder.newTag.setVisibility(View.GONE);

            // 关注按钮

            viewHolder.focusBtn.setVisibility(View.VISIBLE);
            if (itemBean.getRelation() == 0 || itemBean.getRelation() == 1 || itemBean.getRelation() == 3) {
                viewHolder.focusBtn.setText(R.string.postbar_concerned_flag_text);
                viewHolder.focusBtn.setBackgroundResource(R.drawable.z_fans_btn_gray_bg);
                viewHolder.focusBtn.setOnClickListener(null);
            } else {
                viewHolder.focusBtn.setText(R.string.following);
                viewHolder.focusBtn.setBackgroundResource(R.drawable.z_fans_btn_red_new);
                viewHolder.focusBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (itemBean.getUser().getForbid() != 0) {
                            DialogUtil.showOneButtonDialog(mContext,
                                    mContext.getString(R.string.dialog_title),
                                    mContext.getString(R.string.this_user_is_forbiden),
                                    mContext.getString(R.string.ok), null);
                        } else {
                            mListTemp = mList;
                            long flag = FriendHttpProtocol.userFanLove(mContext, itemBean.getUser().getUserid(), 0,
                                    0, httpCallBack);
                            flagToUid.put(flag, (long) itemBean.getUser().getUserid());
                        }
                    }
                });
            }

            convertView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (fans != null) {
                        User user = fans.get(position);
                        user.setRelationship(4);
                        ChatPersonal.skipToChatPersonal(mContext,user );
                    }

                }
            });


        }

        return convertView;
    }

    class ContactsHolder {

//        HeadPhotoView icon;
//        TextView location;
//        TextView name;
//        LinearLayout lyAge;
//        TextView tvAge;
//        ImageView ivAge;
//        TextView constellation;
//        TextView notes;

        public HeadPhotoView icon;
        public TextView nickname;
        public TextView sexAge;
        public TextView selftext;
        public TextView status;
        public TextView distance;
        public ImageView newTag;
        public TextView focusBtn;
        public ImageView svip;
        public ImageView ivSex;
        public RelativeLayout rlSexAge;

    }

}
