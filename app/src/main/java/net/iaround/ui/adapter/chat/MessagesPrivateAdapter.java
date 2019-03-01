package net.iaround.ui.adapter.chat;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.Constants;
import net.iaround.conf.DataTag;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.entity.type.GroupNoticeType;
import net.iaround.im.proto.Iavchat;
import net.iaround.model.entity.GeoData;
import net.iaround.model.entity.MessagesPicsetBean;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.type.MessageListType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.model.type.ChatMessageType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.VoiceChatMessageListActivity;
import net.iaround.ui.activity.im.ChatGameActivity;
import net.iaround.ui.activity.im.SendAccostActivity;
import net.iaround.ui.activity.im.VideoChatListActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.chat.view.swipelayout.SwipeListLayout;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.MeetNoticeBean;
import net.iaround.ui.datamodel.MessageBean;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.datamodel.VideoChatModel;
import net.iaround.ui.dynamic.DynamicGreetersActivity;
import net.iaround.ui.dynamic.DynamicMessagesActivity;
import net.iaround.ui.fragment.MessageFragmentIm;
import net.iaround.ui.friend.bean.NewFansBean;
import net.iaround.ui.friend.bean.VideoChatBean;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupHelperActivity;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.group.activity.GroupNoticeActivity;
import net.iaround.ui.group.activity.NewFansActivity;
import net.iaround.ui.group.bean.GroupContact;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.view.DragPointView;
import net.iaround.ui.view.HeadPhotoView;
import net.iaround.utils.AssetUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-7-15 上午11:42:53
 * @ClassName MessagesPrivateAdapter.java
 * @Description:消息页面的适配器
 */

public class MessagesPrivateAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<MessageBean<?>> mDataList;
    private Handler mHandler;

    private Set<SwipeListLayout> sets = new HashSet();
    private SkillAttackResult skillAttackResult;

    private OnDragListence onDragListence;

    public MessagesPrivateAdapter(Context mContext, ArrayList<MessageBean<?>> mDataList,
                                  Handler mHandler) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        this.mHandler = mHandler;
    }

    public int getCount() {
        if (mDataList != null) {
            return mDataList.size();
        }
        return 0;
    }

    public MessageBean<?> getItem(int position) {
        if (mDataList != null && mDataList.size() > position) {
            return mDataList.get(position);
        }
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if (null == convertView) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.message_contact_item, parent, false);
            holder = new ViewHolder();

            initMyNearItemHolder(convertView, holder);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MessageBean<?> bean = getItem(position);
        if (bean != null) {
            switch (bean.type) {
                case MessageListType.MEET_GAME:
                    // 邂逅通知
                    initMeetGameNoticeView(holder, bean);
                    break;
                case MessageListType.GROUP_CHAT:
                    // 圈聊
                    initGroupContactView(holder, bean);
                    break;
                case MessageListType.PRIVATE_CHAT:
                    // 最近联系人
                    initNearContactView(holder, bean);
                    break;
                case MessageListType.SEND_ACCOST:
                    // 发出搭讪
                    initSendAccostView(holder, bean);
                    break;
                case MessageListType.RECEIVE_ACCOST:
                    // 收到搭讪
                    initReceiveAccostView(holder, bean);
                    break;
                case MessageListType.GROUP_NOTICE:
                    // 圈通知
                    initGroupNoticeView(holder, bean);
                    break;
                case MessageListType.NEW_FANS:
                    // 新增粉丝
                    initNweFansView(holder, bean);
                    break;
                case MessageListType.GROUP_HELPER:
                    // 圈助手
                    initGroupHelperView(holder, bean);
                    break;
                case MessageListType.GROUP_DYNAMIC:
                    // 动态
                    initDynamicView(holder, bean);//应该使用线上的bean类
                    break;
                case MessageListType.VOICE_CHAT:
                    // 语音聊天
                    initVoiceChatView(holder, bean);
                    break;
//				case MessageListType.CHATBAR_NOTICE:
//					// 聊吧通知
//					initChatBarNoticeView( holder, bean );
//					break;
//				case MessageListType.CHATBAR_INVITATION:
//					// 聊吧邀请函
//					initChatBarInvitionView( holder, bean );
//					break;
//				case MessageListType.CHATBAR_SKILL:
//					//聊吧技能
//					initChatBarSkillView( holder, bean );
//					break;
//				case MessageListType.CHATBAR_UNREAD:
//					//聊吧未读
//					initChatBarUnreadView( holder, bean );
//					break;
                case MessageListType.VIDEO_CHAT:
                    // 视频会话
                    initVideoChatView(holder, bean);
                    break;
                default:
                    CommonFunction.log("ChatMessageView", "bean.type error = " + bean.type);
            }

            if (position >= getCount() - 1)
                holder.line.setVisibility(View.INVISIBLE);
            else
                holder.line.setVisibility(View.VISIBLE);

            setItemClickEvent(holder, bean, position);
        }

        return convertView;
    }

    static class ViewHolder {

        public RelativeLayout layoutContent;
        public HeadPhotoView userIconView;
        public DragPointView badge;
        // public ImageView iconBadge;
        public TextView userNameView;
        public TextView onlineTagView;
        public TextView chat_status;
        public TextView tvNotice;
        public View line;
        public TextView tvChatbarBg;
        private TextView tvAnchorBg;
        // public TextView tvAge;
        // public RelativeLayout content_detail_layout;
    }

    private void initMyNearItemHolder(View convertView, ViewHolder holder) {
        holder.layoutContent = (RelativeLayout) convertView.findViewById(R.id.layout_content);
        holder.userIconView = (HeadPhotoView) convertView.findViewById(R.id.friend_icon);
        holder.userNameView = (TextView) convertView.findViewById(R.id.userName);
        holder.onlineTagView = (TextView) convertView.findViewById(R.id.onlineTag);
        holder.chat_status = (TextView) convertView.findViewById(R.id.chat_status);
        holder.badge = (DragPointView) convertView.findViewById(R.id.chat_num_status);
        holder.tvNotice = (TextView) convertView.findViewById(R.id.tv_notice);
        holder.line = convertView.findViewById(R.id.divider_view);
        holder.tvChatbarBg = (TextView) convertView.findViewById(R.id.tv_chatbar_bg);
        holder.tvAnchorBg = (TextView) convertView.findViewById(R.id.tv_anchor_bg);
    }

    private void resetContactView(ViewHolder holder) {
        holder.userNameView.setTextColor(mContext.getResources().getColor(R.color.c_333333));
        holder.userNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        holder.tvNotice.setText("");
    }

    /**
     * 附近联系人
     *
     * @param holder
     * @param bean
     */
    private void initNearContactView(ViewHolder holder, final MessageBean<?> bean) {
        resetContactView(holder);
        NearContact nearContact = (NearContact) bean.messageData;

        // 最近联系人是VIP身份时置顶，终身VIP除外
        if (bean.svip > 0) {
            holder.tvChatbarBg.setVisibility(View.VISIBLE);
            holder.tvChatbarBg.setBackgroundResource(R.drawable.msg_chat_user_vip);
            holder.tvChatbarBg.setText(BaseApplication.appContext.getString(R.string.theme_topic_top_flag));
        } else {
            holder.tvChatbarBg.setVisibility(View.GONE);
        }

        if (Config.getVideoChatOpen() != 0 && bean.userType == 1) {
            holder.tvAnchorBg.setVisibility(View.VISIBLE);
        } else {
            holder.tvAnchorBg.setVisibility(View.GONE);
        }


        User usr = new User();
        usr.setViplevel(bean.vip);
        usr.setSVip(bean.svip);
        usr.setIcon(bean.iconUrl);

        holder.chat_status.setText("");
        holder.chat_status.setVisibility(View.VISIBLE);
//        holder.badge.setBackgroundResource(R.drawable.side_bar_counter_back);
        holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num1);
        if (!nearContact.isFriendMsg()) {
            holder.chat_status.setText("");
            if (nearContact.getChatStatus() == ChatRecordStatus.SENDING) {// 发送中
                holder.chat_status.setBackgroundResource(R.drawable.message_send_ing);
            }
            if (nearContact.getChatStatus() == ChatRecordStatus.ARRIVED) {// 送达
                holder.chat_status.setText(mContext.getResources()
                        .getString(R.string.chat_personal_record_msg_tag_send));
                holder.chat_status.setBackgroundResource(R.drawable.message_send_arri);
                holder.chat_status.setPadding(10, 0, 10, 0);
            } else if (nearContact.getChatStatus() == ChatRecordStatus.READ) {// 已读
                holder.chat_status.setText(mContext.getResources()
                        .getString(R.string.chat_personal_record_msg_tag_read));
                holder.chat_status.setBackgroundResource(R.drawable.message_send_read);
                holder.chat_status.setPadding(10, 0, 10, 0);

            } else if (nearContact.getChatStatus() == ChatRecordStatus.FAIL) {// 失败
                holder.chat_status.setBackgroundResource(R.drawable.message_send_fail);
            } else if (nearContact.getChatStatus() == ChatRecordStatus.NONE) {// 无状态
                // holder.chat_status.setBackgroundResource(
                // R.color.transparent
                // );
                holder.chat_status.setVisibility(View.GONE);
            }
        } else {
            // holder.chat_status.setBackgroundResource( R.color.transparent );
            holder.chat_status.setVisibility(View.GONE);
        }

        // 昵称
        holder.userNameView.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.userNameView, mContext, bean.title, 20));

        int borderSize = CommonFunction.dipToPx(mContext, 1.5f);
        String color = "#00000000";


        // SVIP
        if (bean.svip > 0) {
            color = "#fe9f32";
            holder.userNameView
                    .setTextColor(mContext.getResources().getColor(R.color.c_ee4552));
            holder.userNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (bean.vip > 0) {
            holder.userNameView
                    .setTextColor(mContext.getResources().getColor(R.color.c_333333));
        } else {
            holder.userNameView
                    .setTextColor(mContext.getResources().getColor(R.color.c_333333));
            holder.userNameView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        // 头像

        holder.userIconView.execute(ChatFromType.UNKONW, usr, null);

        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));

        // 聊天内容
        String content = getContentByType(bean);
        NearContact contact = (NearContact) bean.messageData;
        long fuid = contact.getfUid();

        if (fuid > 1000) {
            // GeoData geo = LocationUtil.getCurrentGeo( mContext );
            // int d = LocationUtil.calculateDistance( contact.getUser(
            // ).getLng( ) , contact
            // .getUser( ).getLat( ) , geo.getLng( ) , geo.getLat( ) );
            String dis;
            if (nearContact.getUser().getLat() != 0 && nearContact.getUser().getLng() != 0) {
                if (bean.distance > 0)
                    dis = CommonFunction.covertSelfDistance(bean.distance);
                else
                    dis = "0m";
                content = "[" + dis + "]" + content;
            }
        }

        holder.tvNotice.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.tvNotice, mContext, content, 18));

        // 未读消息数
        if (bean.messageNum == 0) {
            holder.badge.setVisibility(View.INVISIBLE);
            holder.onlineTagView.setVisibility(View.VISIBLE);
        } else {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
            /*if (contact.getQuietSeen() == 1)
                holder.badge.setBackgroundResource(R.drawable.z_msg_quiet_see_count);
            else {*/
            if (bean.messageNum > 0 && bean.messageNum < 10) {
                holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            } else
                holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            /*}*/
//				holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
            holder.onlineTagView.setVisibility(View.VISIBLE);
        }
        // holder.iconBadge.setVisibility( View.GONE );
        // holder.layoutContent.setTag( bean.messageData );
    }

    /**
     * 圈聊
     *
     * @param holder
     * @param bean
     */
    private void initGroupContactView(ViewHolder holder, MessageBean<?> bean) {
        resetContactView(holder);
        GroupContact contact = (GroupContact) bean.messageData;

        //只有圈聊的时候才显示聊吧标识
        holder.tvAnchorBg.setVisibility(View.GONE);
        holder.tvChatbarBg.setVisibility(View.VISIBLE);
        holder.tvChatbarBg.setBackgroundResource(R.drawable.msg_chatbar_bg);
        holder.tvChatbarBg.setText(BaseApplication.appContext.getString(R.string.user_fragment_circle));


        // 状态
        holder.chat_status.setBackgroundResource(R.color.transparent);
        holder.chat_status.setVisibility(View.GONE);

        // 圈图标
        holder.userIconView
                .executeRoundFrame(NetImageView.DEFAULT_AVATAR_ROUND_LIGHT, bean.iconUrl);

        // 圈名
        holder.userNameView.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.userNameView, mContext, bean.title, 20));


        // 正文
        GroupChatMessage lastContact = JSON.parseObject(bean.content, GroupChatMessage.class);
        if (lastContact == null) {
            holder.tvNotice.setText(mContext.getString(R.string.receive_num_new_message));
        } else {
            holder.tvNotice.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(holder.tvNotice, mContext,
                            getGroupContentByType(lastContact, contact.isBeAt), 20));
        }

        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));

        // 未读数
        if (bean.messageNum > 0 && bean.messageNum < 10) {
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setVisibility(View.VISIBLE);
//            holder.badge.setBackgroundResource(R.drawable.message_counter_gray);
            holder.badge.setText(bean.messageNum + "");
        } else if (bean.messageNum >= 10) {
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
//            holder.badge.setBackgroundResource(R.drawable.message_counter_gray);
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(bean.messageNum > 999 ? "999+" : bean.messageNum + "");

        } else {
            holder.badge.setVisibility(View.GONE);
        }
    }

    /**
     * 邂逅
     *
     * @param holder
     * @param bean
     */
    private void initMeetGameNoticeView(ViewHolder holder, final MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        MeetNoticeBean meetBean = (MeetNoticeBean) bean.messageData;
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.onlineTagView.setVisibility(View.VISIBLE);
        holder.chat_status.setVisibility(View.GONE);
        holder.badge.setVisibility(View.VISIBLE);
        holder.badge.setBackgroundResource(R.drawable.side_bar_counter_back);
        holder.userIconView.executeRoundFrame(R.drawable.meet_notice_icon, "");

        // holder.tvAge.setVisibility( View.GONE );
        holder.userNameView.setText(mContext.getResources().getString(R.string.meet_notice));
        // 最后的发送时间
        if (meetBean.time <= 0)
            holder.onlineTagView.setVisibility(View.INVISIBLE);
        else
            holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, meetBean.time));

        // 无未读消息数
        if (bean.messageNum == 0) {
            holder.badge.setVisibility(View.INVISIBLE);
        } else {
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
            if (bean.messageNum > 0 && bean.messageNum < 10) {
                holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            } else
                holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
        }

        // 显示邂逅内容
        String noticeContent = "";
        if (CommonFunction.isEmptyOrNullStr(bean.title))
            bean.title = "TA";
        if (bean.messageType == 1) {
            noticeContent = "[" + bean.title + "]" + mContext.getString(R.string.he_interest_you);
        } else if (bean.messageType == 2) {
            noticeContent = mContext
                    .getString(R.string.you_both_interest, "[" + bean.title + "]");
        }

        holder.tvNotice.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.tvNotice, mContext, noticeContent, 18));
    }

    /**
     * 实例化发出搭讪的View
     */
    private void initSendAccostView(ViewHolder holder, final MessageBean<?> bean) {
        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);

        if (Config.getVideoChatOpen() != 0 && bean.userType == 1) {
            holder.tvAnchorBg.setVisibility(View.VISIBLE);
        } else {
            holder.tvAnchorBg.setVisibility(View.GONE);
        }

        resetContactView(holder);
        holder.onlineTagView.setVisibility(View.VISIBLE);
        // holder.iconBadge.setVisibility( View.GONE );
        if (bean.time <= 0) {
            CommonFunction.log("sherlock", "send accost time error " + (bean.time));
            holder.onlineTagView.setVisibility(View.GONE);
        } else {
            holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        }
        // holder.tvAge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.z_msg_send_accost, null);
        holder.userNameView.setText(R.string.send_accost);
        holder.badge.setVisibility(View.GONE);
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
        holder.chat_status.setVisibility(View.GONE);

        String content = getContentByType(bean);
        content = bean.title + ":" + content;
        GeoData geo = LocationUtil.getCurrentGeo(mContext);
        String distance = "[" + CommonFunction.covertSelfDistance(
                LocationUtil.calculateDistance(bean.lng, bean.lat, geo.getLng(), geo.getLat())) +
                "]";
        try {
            holder.tvNotice.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(holder.tvNotice, mContext, distance + content, 20));
        } catch (Exception e) {
            holder.tvNotice.setText(distance + content);
            e.printStackTrace();
        }

    }

    /**
     * 实例化收到搭讪的View
     */
    private void initReceiveAccostView(ViewHolder holder, final MessageBean<?> bean) {
        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);

        if (Config.getVideoChatOpen() != 0 && bean.userType == 1) {
            holder.tvAnchorBg.setVisibility(View.VISIBLE);
        } else {
            holder.tvAnchorBg.setVisibility(View.GONE);
        }

        resetContactView(holder);
        // holder.tvAge.setVisibility( View.GONE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.z_msg_receive_accost, null);
        holder.userNameView.setText(R.string.receive_accost);
        CommonFunction.log("sherlock", "bean.time == " + bean.time);
        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        holder.onlineTagView.setVisibility(View.VISIBLE);
//        holder.badge.setBackgroundResource(R.drawable.side_bar_counter_back);
        if (bean.messageNum > 0 && bean.messageNum < 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else if (bean.messageNum > 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else {
            holder.badge.setVisibility(View.GONE);
        }
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.chat_status.setVisibility(View.GONE);
        String content = bean.title + ":" + getContentByType(bean);
        int distance = bean.distance;
        if (distance > 0)
            content = "[" + CommonFunction.covertSelfDistance(distance) + "]" + content;

        holder.tvNotice.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.tvNotice, mContext, content, 20));
    }

    /**
     * 圈通知View
     */
    private void initGroupNoticeView(ViewHolder holder, MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        resetContactView(holder);
        GroupNoticeBean msg = (GroupNoticeBean) bean.messageData;
        // holder.tvAge.setVisibility( View.GONE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.z_msg_group_notice_icon, null);
        holder.userNameView.setText(R.string.group_notice_msg);
        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        holder.onlineTagView.setVisibility(View.VISIBLE);
        if (bean.messageNum > 0 && bean.messageNum < 10) {
//			holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else if (bean.messageNum >= 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else {
            holder.badge.setVisibility(View.GONE);
        }
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.chat_status.setVisibility(View.GONE);

        holder.tvNotice.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.tvNotice, mContext, getGroupNoticeContent(msg), 20));
    }

    /**
     * 新增粉丝View
     */
    private void initNweFansView(ViewHolder holder, MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        resetContactView(holder);
        NewFansBean msg = (NewFansBean) bean.messageData;
        // holder.tvAge.setVisibility( View.GONE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.z_msg_my_new_fans_icon, null);
        holder.userNameView.setText(R.string.my_fans_msg);
        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        holder.onlineTagView.setVisibility(View.VISIBLE);
//        holder.badge.setBackgroundResource(R.drawable.side_bar_counter_back);
        if (bean.messageNum > 0 && bean.messageNum < 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
        } else if (bean.messageNum > 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else {
            holder.badge.setVisibility(View.GONE);
        }
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.chat_status.setVisibility(View.GONE);

        String content =
                "[" + msg.userinfo.nickname + "]" + mContext.getString(R.string.focus_you);
        holder.tvNotice.setText(FaceManager.getInstance(mContext)
                .parseIconForString(holder.tvNotice, mContext, content, 20));
    }

    /**
     * 视频会话View
     */
    private void initVideoChatView(ViewHolder holder, MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        resetContactView(holder);
        VideoChatBean msg = (VideoChatBean) bean.messageData;
        // holder.tvAge.setVisibility( View.GONE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.iaround_new_chat_add_video_chat, null);
        holder.userNameView.setText(R.string.video_details_video_chat);
        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        holder.onlineTagView.setVisibility(View.GONE);

        holder.badge.setVisibility(View.GONE);
        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.chat_status.setVisibility(View.GONE);

        holder.tvNotice.setText(R.string.my_video_chat_title);
    }

    /**
     * 动态View
     */
    private void initDynamicView(ViewHolder holder, MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        resetContactView(holder);
        DynamicNewNumberBean msg = (DynamicNewNumberBean) bean.messageData;
        // holder.tvAge.setVisibility( View.GONE );
        // holder.iconBadge.setVisibility( View.GONE );
        holder.userIconView.executeRoundFrame(R.drawable.z_msg_dynamic_icon, null);
        holder.userNameView.setText(R.string.dynamic_notify_title);

        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        if (bean.time == 0) {
            holder.onlineTagView.setVisibility(View.INVISIBLE);
        } else {
            holder.onlineTagView.setVisibility(View.VISIBLE);
        }

//        holder.badge.setBackgroundResource(R.drawable.side_bar_counter_back);
        if (bean.messageNum > 0 && bean.messageNum < 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
        } else if (bean.messageNum > 10) {
            holder.badge.setVisibility(View.VISIBLE);
            holder.badge.setBackgroundResource(R.drawable.chat_update_bg_of_chat_num);
            holder.badge.setText(getMaxMsgNum(bean.messageNum));
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        // holder.content_detail_layout.setVisibility( View.VISIBLE );
        holder.chat_status.setVisibility(View.GONE);
        String content = "";
        if (msg.getLikenum() > 0) {
            content =
                    "[" + mContext.getString(R.string.somebody_greet_you) + "]" + mContext.getString(R.string.greet_you);
            holder.tvNotice.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(holder.tvNotice, mContext, content, 20));
        }

        if (msg.getCommentNum() > 0) {
            content =
                    "[" + mContext.getString(R.string.somebody_greet_you) + "]" + mContext.getString(R.string.commont_you);
            holder.tvNotice.setText(FaceManager.getInstance(mContext)
                    .parseIconForString(holder.tvNotice, mContext, content, 20));
        }

        if (TextUtils.isEmpty(content)) {
            holder.tvNotice.setText(mContext.getString(R.string.not_message_dynamic_infomation));
        }

    }

    /**
     * 语音聊天入口
     * @param holder
     * @param bean
     */
    private void initVoiceChatView(ViewHolder holder, MessageBean<?> bean) {
        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);
        holder.badge.setVisibility(View.GONE);
        holder.chat_status.setVisibility(View.GONE);
        resetContactView(holder);
        holder.userIconView.executeRoundFrame(R.drawable.pic_voice_chat, null);
        holder.userNameView.setText(R.string.voice_chat);
        holder.tvNotice.setText(mContext.getString(R.string.voice_call_details));
        if (bean.time > 0) {
            holder.onlineTagView.setVisibility(View.VISIBLE);
            holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        } else {
            holder.onlineTagView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 圈助手View
     */
    private void initGroupHelperView(ViewHolder holder, MessageBean<?> bean) {

        //不展示聊吧标签
        holder.tvChatbarBg.setVisibility(View.GONE);
        holder.tvAnchorBg.setVisibility(View.GONE);

        resetContactView(holder);
        GroupContact contact = (GroupContact) bean.messageData;

        holder.userIconView.executeRoundFrame(R.drawable.z_msg_group_helper_icon, null);
        holder.userNameView.setText(R.string.group_helper);
        holder.onlineTagView.setText(TimeFormat.timeFormat4(mContext, bean.time));
        holder.chat_status.setVisibility(View.GONE);
        if (bean.time > 0)
            holder.onlineTagView.setVisibility(View.VISIBLE);
        else
            holder.onlineTagView.setVisibility(View.INVISIBLE);
        holder.badge.setVisibility(View.GONE);

        // 正文
        if (bean.messageNum > 0)
            holder.tvNotice.setText(
                    "[" + bean.messageNum + mContext.getString(R.string.groups_have_new_msg) + "]");
        else {
            GroupChatMessage lastContact = JSON.parseObject(bean.content, GroupChatMessage.class);
            if (lastContact == null) {
                holder.tvNotice.setText("");
            } else {
                String content =
                        contact.groupName + ":" + getGroupContentWithoutName(lastContact);
                holder.tvNotice.setText(FaceManager.getInstance(mContext)
                        .parseIconForString(holder.tvNotice, mContext, content, 20));
            }
        }

    }

    /** 聊吧通知View */
//	private void initChatBarNoticeView( ViewHolder holder, MessageBean< ? > bean )
//	{
//		resetContactView( holder );
//		ChatBarNoticeItemBean contact = ( ChatBarNoticeItemBean ) bean.messageData;
//
//		holder.userIconView.executeRoundFrame( R.drawable.z_msg_chatbar_notice, null );
//		holder.userNameView.setText( R.string.chatbar_notice );
//		holder.onlineTagView.setText( TimeFormat.timeFormat4( mContext, bean.time ) );
//		holder.onlineTagView.setVisibility( View.VISIBLE );
//		if ( bean.messageNum > 0 )
//		{
//			holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
//			holder.badge.setVisibility( View.VISIBLE );
//			holder.badge.setText( getMaxMsgNum( bean.messageNum ) );
//		}
//		else
//		{
//			holder.badge.setVisibility( View.GONE );
//		}
//		// holder.content_detail_layout.setVisibility( View.VISIBLE );
//		holder.chat_status.setVisibility( View.GONE );
//
//		holder.tvNotice.setText( FaceManager.getInstance( mContext )
//			.parseIconForString( holder.tvNotice, mContext, getChatBarNoticeContent( contact ),
//				20 ) );
//	}


    /** 聊吧邀请函View */
//	private void initChatBarInvitionView( ViewHolder holder, MessageBean< ? > bean )
//	{
//		resetContactView( holder );
//		ChatBarNoticeItemBean contact = ( ChatBarNoticeItemBean ) bean.messageData;
//
//		holder.userIconView.executeRoundFrame( R.drawable.z_msg_chatbar_invitation, null );
//		holder.userNameView.setText( R.string.chatbar_invitation );
//		holder.onlineTagView.setText( TimeFormat.timeFormat4( mContext, bean.time ) );
//		holder.onlineTagView.setVisibility( View.VISIBLE );
//		if ( bean.messageNum > 0 )
//		{
//			holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
//			holder.badge.setVisibility( View.VISIBLE );
//			holder.badge.setText( getMaxMsgNum( bean.messageNum ) );
//		}
//		else
//		{
//			holder.badge.setVisibility( View.GONE );
//		}
//		// holder.content_detail_layout.setVisibility( View.VISIBLE );
//		holder.chat_status.setVisibility( View.GONE );
//
//		holder.tvNotice.setText( FaceManager.getInstance( mContext )
//			.parseIconForString( holder.tvNotice, mContext, getChatBarNoticeContent( contact ),
//				20 ) );
//	}

    /** 聊吧技能消息列表 */
//	private void initChatBarSkillView( ViewHolder holder, MessageBean< ? > bean )
//	{
//		resetContactView( holder );
//		ChatBarUnreadMsgBean item = ( ChatBarUnreadMsgBean ) bean.messageData;
//
//		holder.userIconView.executeRoundFrame( R.drawable.z_msg_chatbar_skill, null );
//		holder.userNameView.setText( R.string.chatbar_skill );
//		holder.onlineTagView.setText( TimeFormat.timeFormat4( mContext, bean.time ) );
//		holder.onlineTagView.setVisibility( View.VISIBLE );
//
//		if ( bean.messageNum > 0 )
//		{
//			holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
//			holder.badge.setVisibility( View.VISIBLE );
//			holder.badge.setText( getMaxMsgNum( bean.messageNum ) );
//		}
//		else
//		{
//			holder.badge.setVisibility( View.GONE );
//		}
//
//		holder.chat_status.setVisibility( View.GONE );
//
//		holder.tvNotice.setText( FaceManager.getInstance( mContext )
//			.parseIconForString( holder.tvNotice, mContext, item.useskillmessage, 20 ) );
//	}

    /** 聊吧未读消息 */
//	private void initChatBarUnreadView( ViewHolder holder, MessageBean< ? > bean )
//	{
//		resetContactView( holder );
//		ChatBarUnreadMsgBean item = ( ChatBarUnreadMsgBean ) bean.messageData;
//
//		holder.userIconView.executeRoundFrame( R.drawable.default_chatbar_round, item.chatbaricon );
//		holder.userNameView.setText( FaceManager.getInstance( mContext )
//			.parseIconForString( holder.userNameView, mContext, item.chatbarname, 20 ) );
//		holder.onlineTagView.setText( TimeFormat.timeFormat4( mContext, bean.time ) );
//		holder.onlineTagView.setVisibility( View.VISIBLE );
//		if ( bean.messageNum > 0 )
//		{
//			holder.badge.setBackgroundResource( R.drawable.side_bar_counter_back );
//			holder.badge.setVisibility( View.VISIBLE );
//			holder.badge.setText( getMaxMsgNum( bean.messageNum ) );
//		}
//		else if ( bean.messageNum == 0 )
//		{
//			holder.badge.setBackgroundResource( R.drawable.red_point );
//			holder.badge.setVisibility( View.VISIBLE );
//			holder.badge.setText( "" );
//		}
//		else
//			holder.badge.setVisibility( View.GONE );
//
//		holder.chat_status.setVisibility( View.GONE );
//
//		if ( item.messagetype == 1 )
//			holder.tvNotice.setText( R.string.focus_chatbar_btn_text );
//		else if ( item.messagetype == 0 )
//			holder.tvNotice.setText( R.string.my_chatbar_subtitle );
//	}


    /**
     * 设置消息列表中的每一项的点击事件，事件类型：头像点击、整体点击，整体长按
     *
     * @param holder
     * @param bean
     * @param position
     */
    private void setItemClickEvent(ViewHolder holder, final MessageBean<?> bean,
                                   final int position) {
        final int type = bean.type;
        holder.userIconView.setOnHeadPhotoViewClick(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewOnIconClickEvent(type, bean);
            }
        });
        holder.layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listViewOnItemClickEvent(type, bean);
            }
        });

        holder.layoutContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listViewOnLongClickEvent(type, bean);
                return true;
            }
        });
        holder.badge.setDragListencer(new DragPointView.OnDragListencer() {
            @Override
            public void onDragOut() {
                listViewOnDragPonitEvent(type, bean);
            }
        });
    }

    public void setOnDragListence(OnDragListence onDragListence) {
        this.onDragListence = onDragListence;
    }

    /**
     * 通知私聊列表更新
     */
    public interface OnDragListence {
        void referListView();
    }

    /**
     * @param type
     * @param messageBean
     * @Title: listViewOnItemClickEvent
     * @Description: 列表项点击事件
     */
    private void listViewOnItemClickEvent(int type, MessageBean<?> messageBean) {
        switch (type) {

            case MessageListType.MEET_GAME:
//				StatisticsApi.statisticEventMessageClickType( mContext, 3 );//jiqiang
                // Intent meetIntent = new Intent(mContext,
                // WantMeetActivity.class);
                // mContext.startActivity(meetIntent);
                break;
            case MessageListType.GROUP_CHAT: {//圈聊
                GroupContact bean = (GroupContact) messageBean.messageData;
                GroupModel.getInstance().EraseGroupNoReadNum(mContext, bean.groupID);
                // 用于当屏蔽圈子时进入圈聊再返回要显示最新的聊天内容
                GroupModel.getInstance().getLatestMessageToContact(mContext, bean.groupID);//6.0屏蔽的圈子不下发消息
                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
                intent.putExtra("id", bean.groupID + "");
                intent.putExtra("icon", bean.groupIcon);
                intent.putExtra("name", bean.groupName);
                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
                intent.putExtra("isChat", true);
//                mContext.startActivity(intent);
                GroupChatTopicActivity.ToGroupChatTopicActivity(mContext, intent);
            }
            break;
            case MessageListType.PRIVATE_CHAT://私聊
                long uid = 0;
                NearContact contact = (NearContact) messageBean.messageData;
                Common.getInstance().setNoReadMsgCount(
                        Common.getInstance().getNoReadMsgCount() - contact.getNumber());
                if (contact.getUser().getUid() <= 0) { // 用户不存在
                    CommonFunction
                            .showToast(mContext, mContext.getString(R.string.none_user), 0);
                    return;
                }
//                User user = new User();
//                if (contact.getType() == 15)
//                {
//                    String content = contact.getContent();
//                    if (content != null)
//                    {
//                        try {
//                            JSONObject jsonObject = new JSONObject(content);
//                            String result = jsonObject.getString("content");
//                            if (result != null)
//                            {
//                                skillAttackResult = GsonUtil.getInstance().getServerBean(result,SkillAttackResult.class);
//                                if (Common.getInstance().loginUser.getUid() == skillAttackResult.user.UserID)
//                                {//如果当前登陆者是攻击者
//                                    uid = skillAttackResult.targetUser.UserID;
//                                    user.setUid(skillAttackResult.targetUser.UserID);
//                                    user.setNickname(skillAttackResult.targetUser.NickName);
//                                    user.setIcon(skillAttackResult.targetUser.ICON);
//                                    user.setSex("m".equals(skillAttackResult.targetUser.Gender ) ? 1 : 2);
//                                    user.setAge(skillAttackResult.targetUser.Age);
//                                    user.setViplevel(skillAttackResult.targetUser.VipLevel);
//                                    user.setSVip(skillAttackResult.targetUser.VIP);
//                                }else
//                                {//当前登陆者是被攻击者
//                                    uid = skillAttackResult.user.UserID;
//                                    uid = skillAttackResult.user.UserID;
//                                    user.setUid(skillAttackResult.user.UserID);
//                                    user.setNickname(skillAttackResult.user.NickName);
//                                    user.setIcon(skillAttackResult.user.ICON);
//                                    user.setSex("m".equals(skillAttackResult.user.Gender ) ? 1 : 2);
//                                    user.setAge(skillAttackResult.user.Age);
//                                    user.setViplevel(skillAttackResult.user.VipLevel);
//                                    user.setSVip(skillAttackResult.user.VIP);
//                                }
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }else
//                {
                uid = contact.getUser().getUid();
//                }
                CommonFunction.log("sherlock", "uid == " + uid);
                if (uid == Config.CUSTOM_SERVICE_UID)// 小秘书
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 8 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_secretary );

                }
                if (uid == 999)// 游戏中心
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 4 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_gamecenter );
                }
                if (uid == 998)// 圈管理
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 7 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_group_admin );
                }
                if (uid == 997)// 贴吧
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 5 );
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_kanla );
                }
                if (uid == 996)// 贴吧管理
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 6 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_kanla_admin );
                }
                if (uid == 995)//会员中心
                {
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_vip );
                }
                if (uid == 994)//礼物商店
                {
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_giftshop );
                }
                if (uid == 993)//表情中心
                {
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_facecenter );
                }
                if (uid == 992)//积分商城
                {
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_shop );
                }

//				if ( uid > 1000 )// 普通私聊
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_privateChat );

                int code = 0;
                // 当有新消息，返回就刷新
                if (contact.getNumber() > 0) {
                    code = MainFragmentActivity.REQ_CODE_MEG_CHAT_PERSONAL;
                }

                ChatPersonal.skipToChatPersonal((Activity) mContext, contact.getUser(), code);
                break;
            case MessageListType.SEND_ACCOST:// 发出搭讪
            {
//				StatisticsApi.statisticEventMessageClickType( mContext, 1 );
                Intent intent = new Intent(mContext, SendAccostActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.RECEIVE_ACCOST:// 收到搭讪
            {
//				StatisticsApi.statisticEventMessageClickType( mContext, 2 );

                Common.getInstance().setNoReadMsgCount(
                        Common.getInstance().getNoReadMsgCount() - messageBean.messageNum);
                messageBean.messageNum = 0;
                MessageModel.getInstance()
                        .clearReceiveAccostNoneRead(Common.getInstance().loginUser.getUid() + "",
                                mContext);

                notifyDataSetChanged();
                mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);

                Intent intent = new Intent(mContext, ChatGameActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.GROUP_NOTICE:// 圈通知
            {
                GroupAffairModel.getInstance().setAllRead(mContext);
                Intent intent = new Intent(mContext, GroupNoticeActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.NEW_FANS:// 新增粉丝
            {
                NewFansModel.getInstance()
                        .setAllRead(mContext, Common.getInstance().loginUser.getUid());
                Intent intent = new Intent(mContext, NewFansActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.VIDEO_CHAT:// 视频会话
            {
                Intent intent = new Intent(mContext, VideoChatListActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.GROUP_HELPER:// 圈助手
            {
                SharedPreferenceUtil.getInstance(mContext).putLong(
                        SharedPreferenceUtil.GROUP_HELPER_ENTER_TIME +
                                Common.getInstance().loginUser.getUid(),
                        System.currentTimeMillis() + Common.getInstance().serverToClientTime);
                Intent intent = new Intent(mContext, GroupHelperActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.CHATBAR_NOTICE://聊吧通知
                break;
            case MessageListType.CHATBAR_INVITATION://聊吧邀请函
                break;
            case MessageListType.CHATBAR_SKILL://聊吧技能
                break;
            case MessageListType.CHATBAR_UNREAD://聊吧未读
                break;
            case MessageListType.GROUP_DYNAMIC://动态列表
                DynamicNewNumberBean bean = DynamicModel.getInstent().getNewNumBean();
                if (bean != null && bean.getCommentNum() <= 0 && bean.getLikenum() > 0) {// 当新消息只有点赞消息的时候，直接跳到点赞列表
                    Intent intent = new Intent();
                    intent.setClass(mContext, DynamicGreetersActivity.class);
                    intent.putExtra(DynamicGreetersActivity.REQUEST_NEW, true);
                    mContext.startActivity(intent);
                } else {
//                    DataStatistics.get( mContext ).addButtonEvent( net.iaround.analytics.ums.DataTag.BTN_dynamic_msgList );
                    Intent intent = new Intent();
                    intent.setClass(mContext, DynamicMessagesActivity.class);
                    mContext.startActivity(intent);
                }

//                DynamicModel.getInstent().getNewNumBean().setLikenum(0);
//                DynamicModel.getInstent().getNewNumBean().setNum(0);
//                DynamicModel.getInstent().setNewNumBean(null);
//                Common.getInstance().setNoReadMsgCount(
//                        Common.getInstance().getNoReadMsgCount() - bean.getLikenum() - bean.getCommentNum());
                break;

            case MessageListType.VOICE_CHAT://语音聊天
                Intent intent = new Intent();
                intent.setClass(mContext, VoiceChatMessageListActivity.class);
                mContext.startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void listViewOnDragPonitEvent(int type, MessageBean<?> messageBean) {
        switch (type) {
            case MessageListType.MEET_GAME:
//				StatisticsApi.statisticEventMessageClickType( mContext, 3 );//jiqiang
                // Intent meetIntent = new Intent(mContext,
                // WantMeetActivity.class);
                // mContext.startActivity(meetIntent);
                break;
            case MessageListType.GROUP_CHAT: {//圈聊
                GroupContact bean = (GroupContact) messageBean.messageData;
                GroupModel.getInstance().EraseGroupNoReadNum(mContext, bean.groupID);
                // 用于当屏蔽圈子时进入圈聊再返回要显示最新的聊天内容
//                GroupModel.getInstance().getLatestMessageToContact(mContext
//                        , bean.groupID);//6.0屏蔽的圈子不下发消息
//                Intent intent = new Intent(mContext, GroupChatTopicActivity.class);
//                intent.putExtra("id", bean.groupID + "");
//                intent.putExtra("icon", bean.groupIcon);
//                intent.putExtra("name", bean.groupName);
//                intent.putExtra("userid", Common.getInstance().loginUser.getUid());
//                intent.putExtra("usericon", Common.getInstance().loginUser.getIcon());
//                intent.putExtra("isChat", true);
//                mContext.startActivity(intent);
            }
            break;
            case MessageListType.PRIVATE_CHAT://私聊
                NearContact contact = (NearContact) messageBean.messageData;
                Common.getInstance().setNoReadMsgCount(
                        Common.getInstance().getNoReadMsgCount() - contact.getNumber());
                if (contact.getUser().getUid() <= 0) { // 用户不存在
                    CommonFunction
                            .showToast(mContext, mContext.getString(R.string.none_user), 0);
                    return;
                }
                long uid = contact.getUser().getUid();
                CommonFunction.log("sherlock", "uid == " + uid);
                if (uid == Config.CUSTOM_SERVICE_UID)// 小秘书
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 8 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_secretary );

                }
                if (uid == 999)// 游戏中心
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 4 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_gamecenter );
                }
                if (uid == 998)// 圈管理
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 7 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_group_admin );
                }
                if (uid == 997)// 贴吧
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 5 );
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_kanla );
                }
                if (uid == 996)// 贴吧管理
                {
//					StatisticsApi.statisticEventMessageClickType( mContext, 6 );
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_kanla_admin );
                }
                if (uid == 995)//会员中心
                {
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_vip );
                }
                if (uid == 994)//礼物商店
                {
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_giftshop );
                }
                if (uid == 993)//表情中心
                {
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_chat_facecenter );
                }
                if (uid == 992)//积分商城
                {
//					DataStatistics.get( mContext ).addButtonEvent( DataTag.BTN_message_chat_shop );
                }

//				if ( uid > 1000 )// 普通私聊
//					DataStatistics.get( mContext )
//						.addButtonEvent( DataTag.BTN_message_privateChat );

                int code = 0;
                // 当有新消息，返回就刷新
                if (contact.getNumber() > 0) {
                    code = MainFragmentActivity.REQ_CODE_MEG_CHAT_PERSONAL;
                }
//              ChatPersonal.skipToChatPersonal((Activity) mContext, contact.getUser(), code);
                try {

                    ChatPersonalModel.getInstance()
                            .readAllPersonalMsg(mContext, Common.getInstance().loginUser.getUid(), uid);
                    ChatPersonalModel.getInstance()
                            .clearNoneReadCount(mContext, String.valueOf(Common.getInstance().loginUser.getUid()),
                                    String.valueOf(uid));

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (onDragListence != null) {
                    onDragListence.referListView();
                }
                break;
            case MessageListType.SEND_ACCOST:// 发出搭讪
            {
//				StatisticsApi.statisticEventMessageClickType( mContext, 1 );
                Intent intent = new Intent(mContext, SendAccostActivity.class);
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.RECEIVE_ACCOST:// 收到搭讪
            {
//				StatisticsApi.statisticEventMessageClickType( mContext, 2 );

                Common.getInstance().setNoReadMsgCount(
                        Common.getInstance().getNoReadMsgCount() - messageBean.messageNum);
                messageBean.messageNum = 0;
                MessageModel.getInstance()
                        .clearReceiveAccostNoneRead(Common.getInstance().loginUser.getUid() + "",
                                mContext);

                notifyDataSetChanged();
                mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);

//                Intent intent = new Intent(mContext, ChatGameActivity.class);
//                mContext.startActivity(intent);
            }
            break;
            case MessageListType.GROUP_NOTICE:// 圈通知
            {
                GroupAffairModel.getInstance().setAllRead(mContext);
//                Intent intent = new Intent(mContext, GroupNoticeActivity.class);
//                mContext.startActivity(intent);
            }
            break;
            case MessageListType.NEW_FANS:// 新增粉丝
            {
                NewFansModel.getInstance()
                        .setAllRead(mContext, Common.getInstance().loginUser.getUid());
//                Intent intent = new Intent(mContext, NewFansActivity.class);
//                mContext.startActivity(intent);
            }
            break;
            case MessageListType.GROUP_HELPER:// 圈助手
            {
                SharedPreferenceUtil.getInstance(mContext).putLong(
                        SharedPreferenceUtil.GROUP_HELPER_ENTER_TIME +
                                Common.getInstance().loginUser.getUid(),
                        System.currentTimeMillis() + Common.getInstance().serverToClientTime);
//                Intent intent = new Intent(mContext, GroupHelperActivity.class);
//                mContext.startActivity(intent);
            }
            break;
            case MessageListType.CHATBAR_NOTICE://聊吧通知
                break;
            case MessageListType.CHATBAR_INVITATION://聊吧邀请函
                break;
            case MessageListType.CHATBAR_SKILL://聊吧技能
                break;
            case MessageListType.CHATBAR_UNREAD://聊吧未读
                break;
            case MessageListType.GROUP_DYNAMIC://动态列表
                DynamicNewNumberBean bean = DynamicModel.getInstent().getNewNumBean();
                if (bean != null && bean.getCommentNum() <= 0 && bean.getLikenum() > 0) {// 当新消息只有点赞消息的时候，直接跳到点赞列表
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, DynamicGreetersActivity.class);
//                    intent.putExtra(DynamicGreetersActivity.REQUEST_NEW, true);
//                    mContext.startActivity(intent);  DynamicModel.getInstent().getNewNumBean().setNum(0);
                    DynamicModel.getInstent().getNewNumBean().setLikenum(0);
                    DynamicModel.getInstent().setNewNumBean(null);
                } else {
//                    DataStatistics.get( mContext ).addButtonEvent( net.iaround.analytics.ums.DataTag.BTN_dynamic_msgList );
//                    Intent intent = new Intent();
//                    intent.setClass(mContext, DynamicMessagesActivity.class);
//                    mContext.startActivity(intent);
                    DynamicModel.getInstent().getNewNumBean().setNum(0);
                    DynamicModel.getInstent().getNewNumBean().setLikenum(0);
                    DynamicModel.getInstent().setNewNumBean(null);
                }

//                DynamicModel.getInstent().getNewNumBean().setLikenum(0);
//                DynamicModel.getInstent().getNewNumBean().setNum(0);
//                DynamicModel.getInstent().setNewNumBean(null);
//                Common.getInstance().setNoReadMsgCount(
//                        Common.getInstance().getNoReadMsgCount() - bean.getLikenum() - bean.getCommentNum());
                break;
            case MessageListType.VOICE_CHAT://语音聊天
                break;
            default:
                break;
        }
        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
    }


    /**
     * @param type
     * @Title: listViewOnIconClickEvent
     * @Description: 列表头像点击事件
     */
    private void listViewOnIconClickEvent(int type, MessageBean<?> messageBean) {
        switch (type) {
            case MessageListType.MEET_GAME: {
//				Common.getInstance( ).setMeetGameCount( 0 );
                // Intent intent = new Intent(mContext, WantMeetActivity.class);
                // mContext.startActivity(intent);
            }
            break;
            case MessageListType.GROUP_CHAT: {//圈聊
                GroupContact bean = (GroupContact) messageBean.messageData;
                Intent intent = new Intent(mContext, GroupInfoActivity.class);
                intent.putExtra(GroupInfoActivity.GROUPID, bean.groupID + "");
                mContext.startActivity(intent);
            }
            break;
            case MessageListType.PRIVATE_CHAT://私聊
                NearContact nearContact = (NearContact) messageBean.messageData;
                Intent intent = new Intent(mContext, OtherInfoActivity.class);
                intent.putExtra(Constants.UID, nearContact.getfUid());
                intent.putExtra("user", nearContact.getUser());
                mContext.startActivity(intent);
//				SpaceOther.launchUser( mContext, nearContact.getfUid( ), nearContact.getUser( ),
//					ChatFromType.UNKONW );//jiqiang
                break;
            case MessageListType.SEND_ACCOST:// 发出搭讪
                listViewOnItemClickEvent(type, messageBean);
                break;
            case MessageListType.RECEIVE_ACCOST:// 收到搭讪
                listViewOnItemClickEvent(type, messageBean);
                break;

            default:
                break;
        }
    }

    /**
     * @param type
     * @param messageBean
     * @Title: listViewOnLongClickEvent
     * @Description: 列表的长按事件
     */
    private void listViewOnLongClickEvent(int type, MessageBean<?> messageBean) {
        switch (type) {
            case MessageListType.MEET_GAME:// 邂逅游戏
                removeMeetGame();
                break;
            case MessageListType.GROUP_CHAT://圈聊
                removeGroupContact(messageBean);
                break;
            case MessageListType.PRIVATE_CHAT://私聊
                // removeNearContact( messageBean );
                removeQuitetSeePersonalMsg(messageBean);
                break;
            case MessageListType.SEND_ACCOST:// 发出搭讪
            case MessageListType.RECEIVE_ACCOST:// 收到搭讪
                removeAccost(type);
                break;
            case MessageListType.GROUP_NOTICE:// 圈通知
                removeGroupNotice();
                break;
            case MessageListType.NEW_FANS:// 新粉丝
                removeNewFans();
                break;
            case MessageListType.VIDEO_CHAT:// 视频会话
                removeVideoChat();
                break;
            case MessageListType.GROUP_HELPER:// 圈助手
                removeGroupHelper();
                break;
            case MessageListType.CHATBAR_NOTICE:// 聊吧通知
                removeChatBarNotice();
                break;
            case MessageListType.CHATBAR_INVITATION:// 聊吧邀请函
                removeChatBarInvitation();
                break;
            case MessageListType.CHATBAR_SKILL:// 聊吧技能
                removeChatBarSkill();
                break;
            case MessageListType.CHATBAR_UNREAD:// 聊吧未读消息
                removeChatBarUnread(messageBean);
            case MessageListType.GROUP_DYNAMIC:// 动态未读消息
                removeGroupDynamic();
                break;
            case MessageListType.VOICE_CHAT:// 语音聊天未读消息
                break;
            default:
                break;
        }
    }

    // 删除圈聊聊天记录
    private void removeGroupContact(final MessageBean<?> messageBean) {
        DialogUtil.showTwoButtonDialog(mContext,
                mContext.getString(R.string.chat_remove_contact_title),
                mContext.getString(R.string.chat_remove_contact_notice),
                mContext.getString(R.string.cancel),
                mContext.getString(R.string.chat_record_fun_del), null, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        GroupContact contact = (GroupContact) messageBean.messageData;
                        GroupModel.getInstance().removeGroupAndAllMessage(mContext,
                                Common.getInstance().loginUser.getUid() + "", contact.groupID + "");

                        mDataList.remove(messageBean);

                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }

                });
    }

    /**
     * 删除聊天或者悄悄查看
     */
    private void removeQuitetSeePersonalMsg(final MessageBean<?> messageBean) {
        NearContact contact = (NearContact) messageBean.messageData;
        if (contact.getfUid() <= 1000) {//系统用户不能悄悄查看
            removeNearContact(messageBean);
            return;
        }
        // 用于判断悄悄查看按钮是否显示不可点击，真正的点击逻辑在对应的onclick中
        boolean upAble = Common.getInstance().loginUser.isSVip();
        CharSequence charSequenceTitle = mContext.getString(R.string.dialog_title);
        CharSequence charSequenceButton1 = mContext.getString(R.string.quiet_see);
        CharSequence charSequenceButton2 = mContext.getString(R.string.delete_content);
        DialogUtil
                .showTowButtonDialog(mContext, charSequenceTitle, null,
                        charSequenceButton1, charSequenceButton2, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                quietSee(messageBean);
                            }
                        }, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                removeNearContact(messageBean);
                            }
                        });//yuchao
    }

    /**
     * 悄悄查看
     */
    private void quietSee(final MessageBean<?> messageBean) {
        if (Common.getInstance().loginUser.isSVip()) {
            NearContact contact = (NearContact) messageBean.messageData;

            ChatPersonalModel.getInstance()
                    .setNearContactQuietSee(mContext, Common.getInstance().loginUser.getUid(),
                            contact.getUser().getUid(), 1);
            ChatPersonal.skipToChatPersonal(mContext, contact.getUser(), true);
        } else {
            DialogUtil
                    .showTobeVipDialog(mContext, R.string.quiet_see_title, R.string.quiet_see_content,
                            R.string.vip_confirm_text, DataTag.UNKONW);
        }
    }

    // 删除聊天记录
    private void removeNearContact(final MessageBean<?> messageBean) {
        DialogUtil.showTwoButtonDialog(mContext,
                mContext.getString(R.string.chat_remove_contact_title),
                mContext.getString(R.string.chat_remove_contact_notice),
                mContext.getString(R.string.cancel),
                mContext.getString(R.string.chat_record_fun_del), null, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        NearContact con = (NearContact) messageBean.messageData;
                        int id = MessageModel.getInstance().deleteRecordWithPerson(
                                String.valueOf(Common.getInstance().loginUser.getUid()),
                                String.valueOf(con.getfUid()), mContext);
                        if (id > 0) {
                            Common.getInstance().setNoReadMsgCount(
                                    Common.getInstance().getNoReadMsgCount() - con.getNumber());
                            mDataList.remove(messageBean);
                        }
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }

                });
    }

    /**
     * 删除搭讪信息
     *
     * @param type 6：发出搭讪，7：收到搭讪
     */
    private void removeAccost(final int type) {
        String title = "";
        String message = "";
        if (type == 6) {
            title = mContext.getString(R.string.delete_send_accost);
            message = mContext.getString(R.string.all_send_accost_delete);
        } else if (type == 7) {
            title = mContext.getString(R.string.delete_receive_accost);
            message = mContext.getString(R.string.all_reveive_accost_delete);
        }

        DialogUtil.showOKCancelDialog(mContext, title, message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type == 6) {
                    ChatPersonalModel.getInstance().deleteAccostFromTwoTable(mContext, 2);
                } else if (type == 7) {
                    ChatPersonalModel.getInstance().deleteAccostFromTwoTable(mContext, 3);
                }

                notifyDataSetChanged();
                mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
            }
        });
    }

    /**
     * 删除圈通知
     */
    private void removeGroupNotice() {
        DialogUtil.showOKCancelDialog(mContext, R.string.delete_group_notice,
                R.string.all_group_notice_will_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupAffairModel.getInstance().deleteUserAllNotice(mContext);
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }
                });
    }

    /**
     * 删除动态通知
     */
    private void removeGroupDynamic() {
        DialogUtil.showOKCancelDialog(mContext, R.string.delete_group_dynamic_notice,
                R.string.all_group_dynamic_will_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DynamicModel.getInstent().setNewNumBean(null);
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }
                });
    }

    /**
     * 删除新增粉丝
     */
    private void removeNewFans() {
        DialogUtil.showOKCancelDialog(mContext, R.string.delete_new_fans,
                R.string.all_new_fans_will_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NewFansModel.getInstance()
                                .deleteAllMessages(mContext, Common.getInstance().loginUser.getUid());
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }
                });
    }

    /**
     * 删除视频会话
     */
    private void removeVideoChat() {
        DialogUtil.showOKCancelDialog(mContext, R.string.delete_video_chat,
                R.string.all_video_chat_will_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoChatModel.getInstance()
                                .deleteAllMessages(mContext, Common.getInstance().loginUser.getUid());
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }
                });
    }


    /**
     * 删除圈助手
     */
    private void removeGroupHelper() {
        DialogUtil.showOKCancelDialog(mContext, R.string.delete_group_helper,
                R.string.all_group_helper_will_delete, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GroupModel.getInstance().removeGroupMsgByStatus(mContext,
                                Common.getInstance().loginUser.getUid() + "",
                                GroupMsgReceiveType.RECEIVE_NOT_NOTICE);
                        notifyDataSetChanged();
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_DATA);
                        mHandler.sendEmptyMessage(MessageFragmentIm.REFRESH_TAB_COUNT);
                    }
                });
    }

    /**
     * 删除聊吧通知
     */
    private void removeChatBarNotice() {
//		DialogUtil.showOKCancelDialog( mContext, R.string.chatbar_delete_notice,
//			R.string.chatbar_delete_all_notice, new View.OnClickListener( )
//			{
//				@Override
//				public void onClick( View v )
//				{
//					ChatBarAffairModel.getInstant( ).deleteNotice( mContext, 0 );
//					notifyDataSetChanged( );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_DATA );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_TAB_COUNT );
//				}
//			} );//jiqiang
    }

    /**
     * 删除聊吧邀请函
     */
    private void removeChatBarInvitation() {
//		DialogUtil.showOKCancelDialog( mContext, R.string.chatbar_delete_invitation,
//			R.string.chatbar_delete_all_invitation, new View.OnClickListener( )
//			{
//				@Override
//				public void onClick( View v )
//				{
//					ChatBarAffairModel.getInstant( ).deleteInvitation( mContext, 0 );
//					notifyDataSetChanged( );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_DATA );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_TAB_COUNT );
//				}
//			} );
    }

    private void removeChatBarSkill() {
//		DialogUtil.showOKCancelDialog( mContext, R.string.chatbar_delete_skill,
//			R.string.chatbar_delete_all_skill, new View.OnClickListener( )
//			{
//				@Override
//				public void onClick( View v )
//				{
//					ChatBarHttpProtocol.delChatBarSkillMsg( mContext );
//					ChatBarAffairModel.getInstant( ).latestSkillItem.reset( );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_DATA );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_TAB_COUNT );
//				}
//			} );
    }

    private void removeChatBarUnread(MessageBean<?> messageBean) {
//		final ChatBarUnreadMsgBean bean = ( ChatBarUnreadMsgBean ) messageBean.messageData;
//		DialogUtil.showOKCancelDialog( mContext, R.string.chatbar_delete_unread,
//			R.string.chatbar_delete_this_unread, new View.OnClickListener( )
//			{
//				@Override
//				public void onClick( View v )
//				{
//					if ( bean.messagetype == 0 )
//						ChatBarAffairModel.getInstant( ).myChatBar.reset( );
//					else
//					{
//						for ( ChatBarUnreadMsgBean focusChatBar : ChatBarAffairModel
//							.getInstant( ).focusChatBars )
//						{
//							if ( focusChatBar.chatbarid == bean.chatbarid )
//								ChatBarAffairModel.getInstant( ).focusChatBars
//									.remove( focusChatBar );
//						}
//					}
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_DATA );
//					mHandler.sendEmptyMessage( MessageFragment.REFRESH_TAB_COUNT );
//				}
//			} );
    }

    /**
     * 删除邂逅信息
     */
    private void removeMeetGame() {
//		DialogUtil
//			.showOKCancelDialog( mContext, R.string.delete_meet_game, R.string.all_meet_game_delete,
//				new View.OnClickListener( )
//				{
//					@Override
//					public void onClick( View v )
//					{
//						SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( mContext );
//
//						long uid = Common.getInstance( ).loginUser.getUid( );
//						String MeetGameKey = SharedPreferenceUtil.USER_MEET_DATA + uid;
//						if ( sp.has( MeetGameKey ) )
//						{
//							sp.remove( MeetGameKey );
//						}
//						// 上报服务器使其下次登录是不下发邂逅消息
//						RecommandHttpProtocol.cleanMeetList( mContext, null );
//						MeetGameModel.getInstance( ).EraseNoReadNum( mContext );
//						notifyDataSetChanged( );
//						mHandler.sendEmptyMessage( MessageFragment.REFRESH_DATA );
//						mHandler.sendEmptyMessage( MessageFragment.REFRESH_TAB_COUNT );
//					}
//				} );
    }

    private String getContentByType(MessageBean<?> bean) {
        String noticeContent = "";
        switch (bean.messageType) {
            case ChatRecordViewFactory.GAME_ORDER_SERVER_TIP:
            case ChatRecordViewFactory.GAME_ORDER_TIP:
            case ChatRecordViewFactory.CHCAT_AUDIO:
            case ChatRecordViewFactory.FRIEND_CHCAT_AUDIO:
            case ChatMessageType.TEXT:// 普通对话，低版本的打招呼，通过表情来替代
                if (bean.content.equals(mContext.getString(R.string.greeting_content))) {
                    bean.content = "[#a" + (new Random().nextInt(5) + 1) + "#]";
                }
                noticeContent = bean.content;
                break;
            case ChatMessageType.IMAGE:// 图片
                noticeContent = formatPackage(R.string.chat_picture);
                break;
            case ChatMessageType.SOUND:// 语音
                noticeContent = formatPackage(R.string.chat_sound);
                break;
            case ChatMessageType.VIDEO:// 视频
                noticeContent = formatPackage(R.string.chat_video);
                break;
            case ChatMessageType.LOCATION:// 位置
                noticeContent = formatPackage(R.string.chat_location);
                break;
            case ChatMessageType.GIFE_REMIND:// 礼物
                noticeContent = formatPackage(R.string.gift);
                break;
            case ChatMessageType.MEET_GIFT:// 约会道具
                noticeContent = formatPackage(R.string.appointprop);
                break;
            case ChatMessageType.GAME:
                try {
                    JSONObject obj = new JSONObject(bean.content);
                    String content = CommonFunction.jsonOptString(obj, "content");
                    noticeContent = content;
                } catch (JSONException e) {

                    noticeContent = "";
                }
                break;
            case ChatMessageType.FACE:// 贴图
                noticeContent = formatPackage(R.string.mapping);
                break;
            case ChatMessageType.TELETEXT:// 5.1版本 小秘书图文
                try {
                    JSONObject content = new JSONObject(bean.content);
                    String title = CommonFunction.jsonOptString(content, "title");
                    String picname = CommonFunction.jsonOptString(content, "picname");
                    if (!CommonFunction.isEmptyOrNullStr(title.trim())) {
                        noticeContent = title;
                    } else if (!CommonFunction.isEmptyOrNullStr(picname.trim())) {
                        noticeContent = picname;
                    } else {
                        noticeContent = formatPackage(R.string.chat_teletext);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ChatRecordViewFactory.FOLLOW:// 5.3关注
            {
                noticeContent = bean.content;
            }
            break;
            case ChatRecordViewFactory.ACCOST_GAME_ANS: {
                JSONObject obj = null;
                int answerway = 0;
                try {

                    obj = new JSONObject(bean.content);
                    answerway = obj.getInt("answerway");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (answerway == 0) {
                    noticeContent = formatPackage(R.string.reply_true_word);
                } else {
                    noticeContent = formatPackage(R.string.reply_adventure);
                }
            }
            break;
            case ChatRecordViewFactory.SHARE:// 5.5分享
            case ChatRecordViewFactory.INVITE_USER_JOIN_CHAT://邀请聊天
            case ChatRecordViewFactory.INVITE_USER_JOIN_CHATBAR://邀请加入
            {
                if (bean.isFriendMsg) {
                    noticeContent = formatPackage(R.string.share_you_a_message);
                } else {
                    noticeContent = formatPackage(R.string.share_success);
                }
            }
            break;
            case ChatRecordViewFactory.USER_SKILL_MSG://使用技能
            {
                if (bean != null) {
                    String content = bean.content;
                    if (content != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            String result = jsonObject.getString("content");
                            if (result != null) {
                                SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(result, SkillAttackResult.class);
                                String format = AssetUtils.getChatDesc(0, skillAttackResult);
                                if (bean.isFriendMsg) {
//                                    noticeContent = formatPackage(R.string.msg_of_skill_other);
                                    noticeContent = Html.fromHtml(format) + "";
                                } else {
//                                    noticeContent = formatPackage(R.string.msg_of_skill_me);
                                    noticeContent = Html.fromHtml(format) + "";
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            break;
            case ChatRecordViewFactory.PICSET:// 5.6图组
            {
                NearContact contact = (NearContact) bean.messageData;
                String content = contact.getContent();
                MessagesPicsetBean data = GsonUtil.getInstance()
                        .getServerBean(content, MessagesPicsetBean.class);
                noticeContent = data.title;
            }
            break;

            case ChatRecordViewFactory.CHATBAR_DELEGATION:
//				NearContact contact = ( NearContact ) bean.messageData;
//				ChatBarDelegationNoticeBean data = GsonUtil.getInstance( )
//					.getServerBean( contact.getContent( ), ChatBarDelegationNoticeBean.class );
//				if ( contact.getUser( ).getUid( ) == Common.getInstance( ).loginUser.getUid( ) )
//				{
//					noticeContent = formatPackage( R.string.chatbar_delegation_send_success );
//				}
//				else
//				{
//					if ( data.type == 2 )
//					{// 1辅导员，2主持人
//						noticeContent = formatPackage(
//							R.string.message_list_chatbar_delegation_host_msg );
//					}
//					else if ( data.type == 1 )
//					{
//						noticeContent = formatPackage(
//							R.string.message_list_chatbar_delegation_counselor_msg );
//					}
//					else
//					{
//						noticeContent = mContext.getString( R.string.low_version );
//					}
//				}//jiqiang
                break;

            case ChatRecordViewFactory.ERROR_PROMT:
                noticeContent = mContext.getString(R.string.se_200083010);
                break;
            case ChatRecordViewFactory.GIFT_LIMIT_PROMT:
                noticeContent = bean.content;
                break;
            case ChatRecordViewFactory.CHCAT_VIDEO:
                VideoChatBean chatVideo = GsonUtil.getInstance().getServerBean(bean.content, VideoChatBean.class);
                String[] chatVideoStates = mContext.getResources().getStringArray(R.array.video_chat_state);

                switch (chatVideo.getVideoState()) {
                    case Iavchat.STATE_CANCEL:
                        noticeContent = chatVideoStates[0];
                        break;
                    case Iavchat.STATE_REJECT:
                        noticeContent = chatVideoStates[2];
                        break;
                    case Iavchat.STATE_TIMEOUT:
                        noticeContent = chatVideoStates[4];
                        break;
                    case Iavchat.STATE_USER_BUSY:
                        noticeContent = chatVideoStates[5];
                        break;
                    case Iavchat.STATE_CALLER_CLOSE:
                    case Iavchat.STATE_CALLEE_CLOSE:
                        noticeContent = mContext.getString(R.string.private_video_chat_talk_time) + chatVideo.getTalkTime();
                        break;
                }
                break;
            case ChatRecordViewFactory.FRIEND_CHCAT_VIDEO:
                VideoChatBean videoChatFriend = GsonUtil.getInstance().getServerBean(bean.content, VideoChatBean.class);
                String[] states = mContext.getResources().getStringArray(R.array.video_chat_state);

                switch (videoChatFriend.getVideoState()) {
                    case Iavchat.STATE_CANCEL:
                        noticeContent = states[1];
                        break;
                    case Iavchat.STATE_REJECT:
                        noticeContent = states[3];
                        break;
                    case Iavchat.STATE_TIMEOUT:
                        noticeContent = states[2];
                        break;
                    case Iavchat.STATE_CALLER_CLOSE:
                    case Iavchat.STATE_CALLEE_CLOSE:
                        noticeContent = mContext.getString(R.string.private_video_chat_talk_time) + videoChatFriend.getTalkTime();
                        break;
                }
                break;

            default:// 低版本无法显示
                noticeContent = mContext.getString(R.string.low_version);
        }
        return noticeContent;
    }

    /**
     * 根据圈聊内容和类型获取聊天内容
     */
    private String getGroupContentWithoutName(GroupChatMessage contact) {
        String content = "";
        if (contact != null) {
            switch (contact.type) {
                case ChatMessageType.TEXT:// 普通对话，低版本的打招呼，通过表情来替代
                    if (contact.content != null) {
                        if (contact.content.equals(mContext.getString(R.string.greeting_content))) {
                            contact.content = "[#a" + (new Random().nextInt(5) + 1) + "#]";
                        }
                        content = contact.content.toString();
                    }
                    break;
                case ChatMessageType.IMAGE:// 图片
                    content = formatPackage(R.string.chat_picture);
                    break;
                case ChatMessageType.SOUND:// 语音
                    content = formatPackage(R.string.chat_sound);
                    break;
                case ChatMessageType.VIDEO:// 视频
                    content = formatPackage(R.string.chat_video);
                    break;
                case ChatMessageType.LOCATION:// 位置
                    content = formatPackage(R.string.chat_location);
                    break;
                case ChatMessageType.GIFE_REMIND:// 礼物
                    content = formatPackage(R.string.gift);
                    break;
                case ChatMessageType.MEET_GIFT:// 约会道具
                    content = formatPackage(R.string.appointprop);
                    break;
                case ChatMessageType.FACE:// 贴图
                    content = formatPackage(R.string.mapping);
                    break;
                case ChatRecordViewFactory.SHARE:// 5.5分享
                case ChatRecordViewFactory.INVITE_USER_JOIN_CHAT://邀请聊天
                case ChatRecordViewFactory.INVITE_USER_JOIN_CHATBAR://邀请加入聊吧
                    content = formatPackage(R.string.share_you_a_message);
                    break;
                case ChatRecordViewFactory.USER_SKILL_MSG://使用技能
                case ChatRecordViewFactory.FRIEND_USER_SKILL_MSG://使用技能
                    content = formatPackage(R.string.msg_of_skill_other);
                    break;
                default:// 低版本无法显示
                    content = mContext.getString(R.string.low_version);
                    break;
            }
        }
        return content;
    }

    private String getGroupContentByType(GroupChatMessage contact, boolean isBeAt) {
        String content = "";
        content = getGroupContentWithoutName(contact);

        if (Common.getInstance().loginUser == null) {
            CommonFunction.log("sherlock", "login user is null");
        } else if (contact.user == null) {
            CommonFunction.log("sherlock", "contact.user is null");
        } else if (contact.user.userid != Common.getInstance().loginUser.getUid() &&
                contact.user.userid != Config.CUSTOM_SERVICE_UID && contact.user.lng != 0 &&
                contact.user.lat != 0) {
            content = contact.user.getNickname() + ":" + content;
            GeoData geo = LocationUtil.getCurrentGeo(mContext);
            if (contact.user.userid > 1000) {// 5.7圈超级管理员距离问题，改为不显示距离@tanzy

                String prefixContent;
                if (isBeAt) {// 如果是被人At了,优先展示[某人@了你]
                    prefixContent = mContext.getResources().getString(R.string.chat_at_context);
                } else {// 否则显示距离
                    int distance = LocationUtil
                            .calculateDistance(contact.user.lng, contact.user.lat, geo.getLng(),
                                    geo.getLat());
                    prefixContent = CommonFunction.covertSelfDistance(distance);
                }
                content = "[" + prefixContent + "]" + content;
            }
        }

        return content;
    }

    /**
     * 获取圈通知正文
     */
    @SuppressLint("StringFormatMatches")
    private String getGroupNoticeContent(GroupNoticeBean bean) {
        String content = "";
        switch (bean.type) {
            case GroupNoticeType.APPLY_JOIN_GROUP: {// 申请加入圈子
                content =
                        bean.targetuser.nickname + mContext.getString(R.string.apply_join_group) +
                                bean.groupname;
            }
            break;
            case GroupNoticeType.ALLOW_JOIN_GROUP: {// 同意加入
                if (bean.dealuser == null)
                    break;
                content = mContext.getString(R.string.allow_join_group, bean.dealuser.nickname,
                        bean.targetuser.nickname, bean.groupname);
            }
            break;
            case GroupNoticeType.REJECT_JOIN_GROUP: {// 拒绝加入
                if (bean.dealuser == null)
                    break;
                content = mContext.getString(R.string.reject_join_group, bean.dealuser.nickname,
                        bean.targetuser.nickname, bean.groupname);
            }
            break;
            case GroupNoticeType.QUIT_GROUP: {// 退出圈子
                content = mContext
                        .getString(R.string.quit_group, bean.targetuser.nickname, bean.groupname);
            }
            break;
            case GroupNoticeType.KICK_OUT_GROUP: {// 踢出圈子
                if (bean.dealuser == null)
                    break;
                if (bean.targetuser.userid == Common.getInstance().loginUser.getUid())
                    content = mContext
                            .getString(R.string.kick_you_out_group, bean.dealuser.nickname,
                                    bean.groupname);
                else
                    content = mContext.getString(R.string.kick_out_group, bean.targetuser.nickname,
                            bean.dealuser.nickname, bean.groupname);
            }
            break;
            case GroupNoticeType.SET_MANAGER: {// 设置管理员
                if (bean.dealuser == null)
                    break;
                if (bean.targetuser.userid == Common.getInstance().loginUser.getUid())
                    content = mContext.getString(R.string.set_you_manager, bean.dealuser.nickname,
                            bean.groupname);
                else
                    content = mContext.getString(R.string.set_manager, bean.dealuser.nickname,
                            bean.targetuser.nickname, bean.groupname);
            }
            break;
            case GroupNoticeType.REMOVE_MANAGER: {// 取消管理员
                if (bean.dealuser == null)
                    break;
                if (bean.targetuser.userid == Common.getInstance().loginUser.getUid())
                    content = mContext.getString(R.string.remove_you_manager, bean.dealuser.nickname,
                            bean.groupname);
                else
                    content = mContext.getString(R.string.remove_manager, bean.dealuser.nickname,
                            bean.targetuser.nickname, bean.groupname);
            }
            break;
            default: {// 显示低版本
                CommonFunction.log("sherlock", "group notice error type == " + bean.type);
                content = mContext.getString(R.string.low_version);
            }
            break;
        }
        return content;
    }

//	private String getChatBarNoticeContent(ChatBarNoticeItemBean bean )
//	{
//		String content = "";
//		String tname = bean.targetuser.nickname;
//		String dname = "";
//		if ( bean.dealuser != null )
//			dname = bean.dealuser.nickname;
//		String cname = bean.chatbarname;
//
//		if ( bean.type == ChatBarNoticeType.APPLY_JOIN )
//		{
//			content = mContext.getString( R.string.chatbar_apply_join, tname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.ALLOW_JOIN )
//		{
//			content = mContext.getString( R.string.chatbar_allow_join, dname, tname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.REFUSE_JOIN )
//		{
//			content = mContext.getString( R.string.chatbar_refuse_join, dname, tname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.QUIT )
//		{
//			content = mContext.getString( R.string.chatbar_quit, tname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.REMOVE )
//		{
//			if ( bean.targetuser.userid == Common.getInstance( ).loginUser.getUid( ) )
//				content = mContext.getString( R.string.chatbar_remove_you, dname, cname );
//			else
//				content = mContext.getString( R.string.chatbar_remove, tname, dname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.SET_ADMIN )
//		{
//			if ( bean.targetuser.userid == Common.getInstance( ).loginUser.getUid( ) )
//				content = mContext.getString( R.string.chatbar_set_you_admin, dname, cname );
//			else
//				content = mContext.getString( R.string.chatbar_set_admin, dname, tname, cname );
//		}
//		else if ( bean.type == ChatBarNoticeType.CANCEL_ADMIN )
//		{
//			if ( bean.targetuser.userid == Common.getInstance( ).loginUser.getUid( ) )
//				content = mContext.getString( R.string.chatbar_remove_you_admin, dname );
//			else
//				content = mContext.getString( R.string.chatbar_remove_admin, dname, tname );
//		}
//		else if ( bean.type == ChatBarNoticeType.REWARD )
//		{
//			content = mContext.getString( R.string.chatbar_get_reward );
//		}
//		else if ( bean.type == ChatBarNoticeType.TRANSFER_OWNER )
//		{
//			content = mContext.getString( R.string.chatbar_transfer_owner, tname );
//		}
//		else if ( bean.type == ChatBarNoticeType.BECOME_MC )
//		{
//			content = mContext.getString( R.string.chatbar_become_mc );
//		}
//		else if ( bean.type == ChatBarNoticeType.BECOME_COUNSELORS )
//		{
//			content = mContext.getString( R.string.chatbar_become_counselors );
//		}
//		else if ( bean.type == ChatBarNoticeType.INVITATION )
//		{
//			content = mContext.getString( R.string.chatbar_invitation_content, bean.chatbarname );
//		}
//		else
//		{
//			CommonFunction.log( "sherlock", "chatbar notice error type == " + bean.type );
//			content = mContext.getString( R.string.low_version );
//		}
//
//		return content;
//	}


    /**
     * 组装格式
     *
     * @param contentId String资源id
     * @return :[content]
     */
    private String formatPackage(int contentId) {
        String content = mContext.getString(contentId);
        return "[" + content + "]";
    }

    /**
     * 5.6：100条以上的返回"..."
     *
     * @param msgNum 消息量
     * @return
     */
    private String getMaxMsgNum(int msgNum) {
        return msgNum > 99 ? "..." : String.valueOf(msgNum);
    }


    class MyOnSlipStatusListener implements SwipeListLayout.OnSwipeStatusListener {

        private SwipeListLayout slipListLayout;

        public MyOnSlipStatusListener(SwipeListLayout slipListLayout) {
            this.slipListLayout = slipListLayout;
        }

        @Override
        public void onStatusChanged(SwipeListLayout.Status status) {
            if (status == SwipeListLayout.Status.Open) {
                //若有其他的item的状态为Open，则Close，然后移除
                if (sets.size() > 0) {
                    for (SwipeListLayout s : sets) {
                        s.setStatus(SwipeListLayout.Status.Close, true);
                        sets.remove(s);
                    }
                }
                sets.add(slipListLayout);
            } else {
                if (sets.contains(slipListLayout))
                    sets.remove(slipListLayout);
            }
        }

        @Override
        public void onStartCloseAnimation() {

        }

        @Override
        public void onStartOpenAnimation() {

        }

    }
}
