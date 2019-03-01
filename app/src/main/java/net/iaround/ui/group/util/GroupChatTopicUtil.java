package net.iaround.ui.group.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.SocketErrorCode;
import net.iaround.connector.protocol.SocketGroupProtocol;
import net.iaround.database.GroupSharePrefrenceUtil;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.GroupChatMessage;
import net.iaround.model.im.GroupOnlineUser;
import net.iaround.model.im.GroupSimpleUser;
import net.iaround.model.im.GroupUser;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.GroupChatTopicOnLineAdapter;
import net.iaround.ui.chat.MessagesSendManager;
import net.iaround.ui.chat.SuperChat.AtSpan;
import net.iaround.ui.chat.SuperChat.HandleMsgCode;
import net.iaround.ui.datamodel.GroupChatListModel;
import net.iaround.ui.datamodel.GroupHistoryMessagesBean;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.group.activity.GroupChatTopicActivity.GroupInfo;
import net.iaround.ui.group.activity.GroupChatTopicActivity.HistoryRecorder;
import net.iaround.ui.group.activity.GroupUseIntrocudeActivity;
import net.iaround.utils.EditTextViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


/**
 * Created by Ray on 2017/7/7.
 */

public class GroupChatTopicUtil {
    private static GroupChatTopicUtil protocol;

    private Handler mHandler;
    private MessagesSendManager backManager;
    private WeakReference<EditTextViewUtil>  editContent;// 输入框
    protected static final int KEYBOARD_HIDE = 1;// 软件键盘隐藏
    protected static final int KEYBOARD_SHOW = 2;// 软件键盘显示
    protected int keyboardState = KEYBOARD_HIDE;// 软件键盘状态：显示、隐藏

    private GroupInfo mGroupInfo;


    /**
     * 是否圈聊（默认true）
     */
    private boolean mIsChat = true;
    /**
     * 加载更多聊天记录的数目
     */
    private static final int LOAD_MORE_NUM = 15;


    public static GroupChatTopicUtil getInstance() {
        if (protocol == null) {
            synchronized (GroupChatTopicUtil.class) {
                if(null==protocol) {
                    protocol = new GroupChatTopicUtil();
                }
            }
        }
        return protocol;
    }

    private GroupChatTopicUtil(){

    }

    public void initView(Context context, Handler handler, MessagesSendManager mBackManager, HistoryRecorder historyRecorder, EditTextViewUtil mEditContent, boolean isChat) {
        this.mHandler = handler;
        this.backManager = mBackManager;
        this.editContent = new WeakReference<EditTextViewUtil>(mEditContent);
        this.mIsChat = isChat;
    }

    public void destroyView(){
        this.mHandler = null;
        this.backManager = null;
        this.editContent = null;
        this.mIsChat = true;
    }

    public void initData(GroupInfo groupInfo) {
        this.mGroupInfo = groupInfo;
    }

    public void handleAtOperation(ChatRecord record) {
        if(mHandler == null){
            return;
        }
        String noteName = "";
        long userid;
        if (record.getSendType() == MessageBelongType.SEND) {
            //			noteName = "@" + record.getNoteName( true );
            noteName = "@" + record.getNickname();
            userid = record.getUid();
        } else {
            //			noteName = "@" + record.getfNoteName( true );
            noteName = "@" + record.getfNickName();
            userid = record.getFuid();
        }

        final EditText etInput = getEditContent();
        if(null == etInput){
            return;
        }
        AtSpan spanStr = new AtSpan(noteName, userid);
        int end = spanStr.length();
        spanStr.setSpan(spanStr, 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        Editable text = etInput.getText();

        /**
         * 解决重复@某个人的情况
         */
        if (!text.toString().contains(spanStr)) {
            etInput.append(spanStr);
        }
        etInput.append("  ");
        etInput.requestFocus();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                EditText etInput = getEditContent();
                if(etInput!=null) {
                    CommonFunction.showInputMethodForQuery(BaseApplication.appContext, etInput);
                    setKeyboardStateShow();
                }
            }
        }, 100);
    }

    public void setKeyboardStateShow() {
        keyboardState = KEYBOARD_SHOW;
    }

    public void handleAudioSendEndFail(String result) {
        if(mHandler == null){
            return;
        }
        try {
            JSONObject json = new JSONObject(result);
            long flag = json.optLong("flag");
            backManager.RemoveSendThread(flag);

            Message msg = new Message();
            Bundle b = new Bundle();
            b.putLong("flag", flag);
            b.putInt("e", 0);
            msg.what = HandleMsgCode.MSG_SEND_MESSAGE;
            msg.setData(b);
            mHandler.sendMessage(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void handleAudioSendEndSuccess(String result) {
        if(mHandler == null){
            return;
        }
        try {
            JSONObject json = new JSONObject(result);
            long flag = json.optLong("flag");
            backManager.RemoveSendThread(flag);

            Message msg = new Message();
            Bundle b = new Bundle();
            b.putLong("flag", flag);
            b.putInt("e", 1);
            msg.what = HandleMsgCode.MSG_SEND_MESSAGE;
            msg.setData(b);
            mHandler.sendMessage(msg);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 升为管理员成功
     *
     * @param msg
     */
    @SuppressWarnings("unchecked")
    public void handleBecomeManagerSucc(Message msg) {
        try {
            HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
            String userId = String.valueOf(map.get("user_id"));
            String nickname = String.valueOf(map.get("nickname"));

            GroupModel.getInstance().addToManagerIdList(userId);
            String des = String.format(BaseApplication.appContext.getResources().getString(R.string.group_user_list_become_manager_succ_tip), nickname);
            CommonFunction.showToast(BaseApplication.appContext, des, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 免去管理员成功
     *
     * @param msg
     */
    @SuppressWarnings("unchecked")
    public void handleCancleManagerSucc(Message msg) {
        try {
            HashMap<String, Object> map = (HashMap<String, Object>) msg.obj;
            String userId = String.valueOf(map.get("user_id"));
            String nickname = String.valueOf(map.get("nickname"));

            GroupModel.getInstance().delFromManagerIdList(userId);
            CommonFunction.showToast(BaseApplication.appContext, String.format(BaseApplication.appContext.getResources().getString(R.string.group_user_list_cancle_manager_succ_tip), nickname), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 圈成员首次进入圈聊，弹出提示
     */
    public void handleFirstEnter(Context context) {
        if (mIsChat) {
            GroupSharePrefrenceUtil util = new GroupSharePrefrenceUtil();
            String groupId = getCurrentGroupId();
            boolean isFirstEnterGroup = util.getBoolean(context, GroupSharePrefrenceUtil.FIRST_ENTER_GROUP, groupId, false);
            if (isFirstEnterGroup) {
                Intent groupInfoIntent = new Intent(context, GroupUseIntrocudeActivity.class);
                context.startActivity(groupInfoIntent);
                util.putBoolean(context, GroupSharePrefrenceUtil.FIRST_ENTER_GROUP, getCurrentGroupId(), false);
            }
        }
    }

    /**
     * 获取string
     *
     * @param id
     * @return
     */
    public String getResString(int id) {
        return BaseApplication.appContext.getResources().getString(id);
    }


    public String getCurrentGroupId() {
        return mGroupInfo.getGroupId();
    }



    /**
     * 群主踢人失败
     *
     * @param msg
     */
    @SuppressWarnings("unchecked")
    public void handleKickUserFail(Message msg) {
        Map<String, Object> map = (Map<String, Object>) msg.obj;
        int error = (Integer) map.get("error");
        String errorStr = SocketErrorCode.getCoreMessage(BaseApplication.appContext, error);
        CommonFunction.toastMsg(BaseApplication.appContext, errorStr);
    }

    private EditText getEditContent() {
        if(editContent == null){
            return null;
        }
        return editContent.get();
    }

}
