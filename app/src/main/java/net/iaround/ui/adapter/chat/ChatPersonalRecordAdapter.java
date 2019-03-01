package net.iaround.ui.adapter.chat;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.RecordAccostGameBean;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.chat.ChatRecordLongClickListener;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-4-15 上午10:24:08
 * @ClassName ChatPersonalRecordAdapter
 * @Description: 用于私聊中的消息界面
 */

public class ChatPersonalRecordAdapter extends ChatRecordListBaseAdapter {

    //私聊中消息的各种点击事件
    private View.OnClickListener mUserIconClickListener;//头像点击的监听器
    private View.OnClickListener mResendClickListener;//发送失败,重发点击监听器
    private View.OnLongClickListener mRecordLongClickListener;//消息体长按的监听器
    private View.OnClickListener mCreditsClickListener;//积分兑换提示框的点击监听器
    private View.OnClickListener mAcceptDelegationClickListener;//接受聊吧委托找主持人/辅导员的点击监听器

    private View.OnClickListener mOrderAgreeClickListener;//订单同意点击事件
    private View.OnClickListener mOrderRefuseClickListener;//订单拒绝点击事件

    public ChatPersonalRecordAdapter(Context context, ArrayList<ChatRecord> dataList) {
        super(context, dataList);
    }

    public void initClickListeners(View.OnClickListener userIconClickListener,
                                   View.OnClickListener resendClickListener, View.OnClickListener creditsClickListener,
                                   View.OnClickListener agreeClickListener, View.OnClickListener refuseClickListener) {
        mUserIconClickListener = userIconClickListener;
        mResendClickListener = resendClickListener;
        mCreditsClickListener = creditsClickListener;
        mOrderAgreeClickListener = agreeClickListener;
        mOrderRefuseClickListener = refuseClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        ChatRecord record = ((ChatRecord) getItem(position));
        int type = -1;
        String recordTypeStr = record.getType();

        if (record.getType().equals("110")) {
            type = ChatRecordViewFactory.ERROR_PROMT;
        } else if (record.getType().equals("111")) {
            type = ChatRecordViewFactory.GIFT_LIMIT_PROMT;
        } else if (SuperChat.TIME_LINE_TYPE.equals(recordTypeStr)) {
            type = ChatRecordViewFactory.TIME_LINE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.GAME_ORDER_SERVER_TIP) {//游戏订单提醒
            type = ChatRecordViewFactory.GAME_ORDER_SERVER_TIP;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_QUE) {
            type = ChatRecordViewFactory.ACCOST_GAME_QUE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_NOTICE) {
            type = ChatRecordViewFactory.ACCOST_NOTICE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.INVITE_USER_JOIN_CHAT) {
            type = ChatRecordViewFactory.INVITE_USER_JOIN_CHAT;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.INVITE_USER_JOIN_CHATBAR) {
            type = ChatRecordViewFactory.INVITE_USER_JOIN_CHATBAR;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.FRIEND_CHCAT_VIDEO) {
            type = ChatRecordViewFactory.FRIEND_CHCAT_VIDEO;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.FRIEND_CHCAT_AUDIO) {
            type = ChatRecordViewFactory.FRIEND_CHCAT_AUDIO;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.USER_SKILL_MSG) {
            SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(record.getContent(), SkillAttackResult.class);
            if (skillAttackResult.user.UserID == Common.getInstance().loginUser.getUid()) {
                type = ChatRecordViewFactory.USER_SKILL_MSG;
            } else {
                type = ChatRecordViewFactory.FRIEND_USER_SKILL_MSG;
            }
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_ANS) {
            String content = record.getContent();
            RecordAccostGameBean bean = GsonUtil.getInstance().getServerBean(content, RecordAccostGameBean.class);
            if (bean.bIsTextAnswer()) {
                type = ChatRecordViewFactory.ACCOST_GAME_ANS_TEXT;
            } else {
                type = ChatRecordViewFactory.ACCOST_GAME_ANS_IMAGE;
            }

            if (!isMyRecord(record)) {
                type += ChatRecordViewFactory.TYPE_OFFSET;
            }
        } else {
            type = Integer.valueOf(recordTypeStr);

            if (!isMyRecord(record)) {
                type += ChatRecordViewFactory.TYPE_OFFSET;
            }
        }

        return type;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 获取聊天记录
        ChatRecord record = (ChatRecord) getItem(position);
        ChatRecordView view = null;

        // 获取聊天记录的View
        if (convertView == null) {
            int ViewType = getItemViewType(position);
            Log.e("tag", "ViewType==" + ViewType);
            view = ChatRecordViewFactory.createChatRecordView(mContext, ViewType);
            mRecordLongClickListener = new ChatRecordLongClickListener(mContext, view);
            view.initClickListener(mUserIconClickListener, null, null,
                    mRecordLongClickListener, mResendClickListener, checkBoxClickListener,
                    mCreditsClickListener, mOrderAgreeClickListener, mOrderRefuseClickListener);
            view.initRecord(mContext, record);
        } else {
            view = (ChatRecordView) convertView;
        }

        view.reset();
        view.showRecord(mContext, record);
        view.showCheckBox(isShowCheckBox);

        return view;
    }

}
