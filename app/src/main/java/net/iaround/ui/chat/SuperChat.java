package net.iaround.ui.chat;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.MessageType;
import net.iaround.entity.GroupChatInfo;
import net.iaround.entity.PrivateChatInfo;
import net.iaround.entity.RecordFaceBean;
import net.iaround.entity.type.ChatFromType;
import net.iaround.entity.type.MessageBelongType;
import net.iaround.entity.type.SoundShakeType;
import net.iaround.mic.FlyAudioRoom;
import net.iaround.model.im.AudioBaseBean;
import net.iaround.model.im.ChatMessageBean;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.ChatRecordStatus;
import net.iaround.model.im.ChatTargetInfo;
import net.iaround.model.im.GroupUser;
import net.iaround.model.im.Me;
import net.iaround.model.type.ChatMessageType;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.CryptoUtil;
import net.iaround.tools.DensityUtils;
import net.iaround.tools.DialogUtil;
import net.iaround.tools.FaceManager;
import net.iaround.tools.FaceManager.FaceIcon;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.LocationUtil.MLocationListener;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.adapter.chat.ChatRecordListBaseAdapter;
import net.iaround.ui.chat.ChatMenuTool.MenuToolListener;
import net.iaround.ui.chat.ChatMultiHandleDialog.MultiHandleClickPerform;
import net.iaround.ui.chat.view.ChatRecordView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.chat.view.FriendSoundRecordView;
import net.iaround.ui.chat.view.MySelfSoundRecordView;
import net.iaround.ui.comon.ResizeLayout.OnResizeListener;
import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.face.FaceDetailActivityNew;
import net.iaround.ui.face.SendFaceToFriends;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.bean.Group;
import net.iaround.ui.interfaces.ChatFaceShowCallback;
import net.iaround.ui.interfaces.RecordBtnTouchCallBack;
import net.iaround.ui.view.DragNullPointView;
import net.iaround.ui.view.DragPointView;
import net.iaround.ui.view.FlagImageViewBarrage;
import net.iaround.ui.view.LuxuryGiftView;
import net.iaround.ui.view.dialog.CustomGiftDialogNew;
import net.iaround.utils.EditTextViewUtil;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.GalleryUtils;
import cn.finalteam.galleryfinal.model.PhotoInfo;


/**
 * 聊天记录的抽象类：用于圈子、私聊的聊天界面
 *
 * @author chenlb
 */
public abstract class SuperChat extends SuperActivity implements MenuToolListener, OnClickListener,
        OnResizeListener, MultiHandleClickPerform, RecordBtnTouchCallBack {
    private static final String TAG = "SuperChat";
    public static final int EDITTEXT_PER_MSG_MAX = 1000;// 每条消息最大字符长度，如果超过此长度则用第二条消息发送
    protected LuxuryGiftView luxuryGiftView;
    protected FlyAudioRoom flyAudioRoom;
    protected boolean barrageState;
    protected SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(this);

    /**
     * 用于在线程中创建或移除悬浮窗。
     */
//    private Handler handler = new Handler();

    /**
     * 判断是否是切换表情至软键盘
     */
    private boolean isSwichFaceToKeyboard = false;

    @Override
    public void onClick(View v) {
        if (v == faceMenuBtn) {
            // 显示表情列表
            faceBtnClick();
        } else if (v == giftMenuBtn) {   //YC显示礼物布局
            giftBtnClick();
        } else if (v == toolMenuBtn) {
            // 显示菜单按钮
            toolBtnClick();
        } else if (v == mSoundTextSwitch) {
            soundTextSwitcherClick();
        } else if (v == ivRepeater) {

        } else if (v == ivDelete) {

            moreHandleRecordList.clear();
            for (ChatRecord record : mRecordList) {
                if (record.isSelect()) {
                    moreHandleRecordList.add(record);
                }
            }

            if (moreHandleRecordList.size() <= 0) {
                return;
            }

            chatInputBarLayout.setVisibility(View.VISIBLE);
            llMoreHandle.setVisibility(View.GONE);

            adapter.showCheckBox(false);
            adapter.clearSeletedList();

            for (ChatRecord record : moreHandleRecordList) {
                delOneRecord(record);
            }

            moreHandleRecordList.clear();
        } else if (v == mBtnSend) {
            if (barrageState) {

            } else {
                if (!TextUtils.isEmpty(editContent.getEditableText().toString())) {
                    sendTextFace();
                }
            }

        } else if (v == unseeNum) {//点击未看数字
            unseeCount = 0;
            mHandler.sendEmptyMessage(HandleMsgCode.SHOW_AND_CLEAR_UNSEE);
            mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
        } else if (v == btTask) {
            viewTask.setVisibility(View.GONE);
            SharedPreferenceUtil.getInstance(BaseApplication.appContext).putLong(Common.getInstance().getUid() + SharedPreferenceUtil.GROUO_CHAT_BAR_TASK_READ_STATE, TimeFormat.getYearMonthDayDate());
            String url = CommonFunction.getLangText(getActivity(), Config.sSkillTask);
            Uri uri = Uri.parse(url);
            Intent i = new Intent(getActivity(), WebViewAvtivity.class);
            i.putExtra(WebViewAvtivity.WEBVIEW_URL, uri.toString());
            startActivityForResult(i, ChatRequestCode.CHAT_TASK_REQUEST_CODE); //startActivity(i);

        }
    }

    @Override
    protected void allPermissionsGranted() {
        super.allPermissionsGranted();
        // 语音和文本切换
        soundTextSwitcherClick();
    }

    private void showForideRepeatMsgNotice(final int sendCount, int selectedTotal) {
       /* if (sendCount == selectedTotal) {

            Intent intent = new Intent();
            intent.setClass(mContext, RepeaterActivity.class);
            ((SuperChat) mContext)
                    .startActivityForResult(intent, ChatRequestCode.CHAT_REPEATER_REQUEST_CODE);
        } else */
        {

            String title = getResString(R.string.dialog_title);
            String msg = getResString(R.string.chat_repeater_forbid_notice);
            String cancel = getResString(R.string.cancel);
            String ok = getResString(R.string.ok);

            DialogUtil.showTwoButtonDialog(mContext, title, msg, cancel, ok, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    moreHandleRecordList.clear();
                }
            }, new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (sendCount == 0) {
                        chatInputBarLayout.setVisibility(View.VISIBLE);
                        llMoreHandle.setVisibility(View.GONE);
                        adapter.showCheckBox(false);
                        adapter.clearSeletedList();
                        moreHandleRecordList.clear();
                    } /*else {
                        Intent intent = new Intent();
                        intent.setClass(mContext, RepeaterActivity.class);
                        ((SuperChat) mContext).startActivityForResult(intent,
                                ChatRequestCode.CHAT_REPEATER_REQUEST_CODE);
                    }*/
                }
            });

        }

    }

    private boolean isForbidedSend(ChatRecord record) {

        if (record.getStatus() == ChatRecordStatus.SENDING ||
                record.getStatus() == ChatRecordStatus.FAIL)
            return true;

        String typeStr = record.getType();
        int type = Integer.valueOf(typeStr);
        return type == ChatMessageType.SOUND || type == ChatMessageType.GIFE_REMIND ||
                type == ChatMessageType.MEET_GIFT || type == ChatMessageType.FACE;

    }

    public static class ChatHandler extends Handler {
        private WeakReference<SuperChat> mSuperChat = null;

        public ChatHandler(SuperChat superChat) {
            super();
            mSuperChat = new WeakReference<SuperChat>(superChat);
        }

        @Override
        public void handleMessage(Message msg) {
            SuperChat superChat = mSuperChat.get();
            if (superChat != null) {
                switch (msg.what) {
                    case HandleMsgCode.MSG_GET_ADDRESS_INFO_FAIL: // 获取经纬度、地址信息失败
                        superChat.handleGetAddressInfoFail(msg);
                        break;
                    case HandleMsgCode.MSG_GET_ADDRESS_INFO_SUCC: // 获取经纬度、地址信息成功
                        superChat.handleGetAddressInfoSucc(msg);
                        break;
                    case HandleMsgCode.MSG_UPLOAD_ERROR: // 上传出错
                        superChat.handleUploadFail(msg);
                        break;
                    case HandleMsgCode.MSG_UPLOAD_FINISH: // 上传完成
                        superChat.handleUploadFinish(msg);
                        break;
                    case HandleMsgCode.MSG_UPLOAD_PROGRESS: // 上传进度
                        //显示上传进度
                        superChat.handleUploadProgress(msg);
                        break;
                    case HandleMsgCode.MSG_RESIZE: // 屏幕大小发生改变
                        superChat.handleScreenResize(msg);
                        break;
                    case HandleMsgCode.MSG_SHOW_LIST_BOTTOM: // 让listview显示最底部的数据
                        superChat.showListBottom();
                        break;
                    case HandleMsgCode.AUDIO_DATA_RECORD_START:
                        AudioBaseBean bean1 = (AudioBaseBean) msg.obj;
                        CommonFunction.log(TAG, "ChatHandler.handleMessage record start time == " + bean1.flag);
                        superChat.sendMsg(bean1.flag, ChatMessageType.SOUND, bean1.filePath, "", bean1.audioLength);
                        break;
                    case HandleMsgCode.AUDIO_DATA_RECORD_FINISH:
                        AudioBaseBean bean = (AudioBaseBean) msg.obj;
                        superChat.handleRecordFinish(bean);
                        break;
                    case HandleMsgCode.NOTIFY_DATA_CHANGE:
                        superChat.updateList();
                        break;
                    case HandleMsgCode.PIC_COMPRESS_SEND:
                        String outpath = (String) msg.obj;
                        superChat.sendMsg(superChat.getCurrentFlag(), ChatMessageType.IMAGE, outpath, "", ""); // 发送图片
                        break;
                    case HandleMsgCode.SHOW_AND_CLEAR_UNSEE:
                        superChat.handleUnseeNum();
                        break;
                    default:
                        superChat.handleSelfMessage(msg);
                }
            }

            super.handleMessage(msg);
        }
    }

    protected abstract void handleRecordFinish(AudioBaseBean bean);

    // 屏幕大小发生改变,主要是为了让弹出键盘或者工具栏的时候,消息被顶上去
    @Override
    public void OnResize(int w, int h, int oldw, int oldh) {
        int change = KEYBOARD_HIDE;
        if (h < oldh && ((oldh - h) > 100)) {
            change = KEYBOARD_SHOW;
        }

        Message msg = mHandler.obtainMessage();
        msg.what = HandleMsgCode.MSG_RESIZE;
        msg.arg1 = change;
        mHandler.sendMessage(msg);
    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        // 当输入Layout显示时，点击输入Layout以外的任意地方隐藏输入
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();

            if (isShouldHideInput(view, event)) {
                hideMenu();
                if (hideKeybordWhenTouch)
                    hideKeyboard();
            }

            if (chatVoice.isShown() && isShouldHideInput(chatVoice, event)) {
                hideMenu();
                if (hideKeybordWhenTouch) {
                    hideKeyboard();
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }

    /**
     * 返回聊天记录列表
     *
     * @return
     */
    public ArrayList<ChatRecord> getRecordList() {
        return mRecordList;
    }

    public EditText getEditContent() {
        return editContent;
    }

    /**
     * 更新聊天记录列表
     */
    public void updateList() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public void hideFaceShowKeyboard() {
        // 菜单处于显示状态，则隐藏菜单
        hideMenu();
        editContent.requestFocus();
        showKeyboardDelayed(200);
    }

    /**
     * 发送失败的点击响应
     */
    public class SendFailClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            sendFailDialog(v).show();
        }
    }

    /**
     * 获取聊天对象的ID，私聊：用户ID；圈子：圈子ID
     *
     * @return
     */
    protected abstract long getTargetID();

    // 处理私聊/圈聊的消息
    protected abstract void handleSelfMessage(Message msg);

    /**
     * 删除某一条聊天记录
     *
     * @param record 要删除的聊天记录
     */
    protected abstract void delOneRecord(ChatRecord record);

    // 处理上传失败
    protected void handleUploadFail(Message msg) {
        @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) msg.obj;
        long flag = Long.parseLong(String.valueOf(map.get("flag")));

        int position = findCurSendMsg(mRecordList, flag);

        if (position >= 0) {
            ChatRecord record = mRecordList.get(position);
            record.setStatus(ChatRecordStatus.FAIL);
            record.setUpload(false);
        }
        // 刷新列表
        updateList();
    }

    // 处理上传完成
    protected void handleUploadFinish(Message msg) {
        @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) msg.obj;
        long flag = Long.parseLong(String.valueOf(map.get("flag")));

        int position = findCurSendMsg(mRecordList, flag);

        if (position >= 0) {
            ChatRecord record = mRecordList.get(position);
            record.setUpload(false);
        }

        updateList();
    }

    // 处理上传进度
    protected void handleUploadProgress(Message msg) {
        @SuppressWarnings("unchecked") Map<String, Object> map = (Map<String, Object>) msg.obj;
        long flag = Long.parseLong(String.valueOf(map.get("flag")));
        int len = Integer.parseInt(String.valueOf(map.get("len")));

        CommonFunction.log(TAG, "上传 进度 =======" + len);

        int position = findCurSendMsg(mRecordList, flag);
        if (position >= 0) {
            ChatRecord record = mRecordList.get(position);
            // 更新上传进度
            record.setUploadLen(len);
            record.setUpload(true);

            updateList();
        }
    }

    /**
     * @ChatRecordLongClickListener
     */
    @Override
    public void onItemClick(ChatRecordView recordView, int position, long flag) {

        ChatRecord record = (ChatRecord) recordView.getTag();
        if (flag == ChatRecordLongClickListener.COPY_ACTION_TAG) {
            handleCopy(record);
        } else if (flag == ChatRecordLongClickListener.DELETE_ACTION_TAG) {
            handleDelete(record);
        } /*else if (flag == ChatRecordLongClickListener.REPEATER_ACTION_TAG) {
            handleRepeater(record);
        }*/ else if (flag == ChatRecordLongClickListener.MORE_ACTION_TAG) {
            handleMore(record);
        } else if (flag == ChatRecordLongClickListener.SAVE_ACTION_TAG) {
            handleSave(record);
        } else if (flag == ChatRecordLongClickListener.SPEAKER_PLAY_ACTION_TAG) {
            handleSpeakerPlay(recordView, record);
        } else if (flag == ChatRecordLongClickListener.DETAIL_ACTION_TAG) {
            handleDetail(record);
        } else if (flag == ChatRecordLongClickListener.AT_ACTION_TAG) {
            handleAt(record);
        }
    }

    private void handleCopy(ChatRecord record) {
        String content = record.getContent();
        if (CommonFunction.isEmptyOrNullStr(content))
            return;
        boolean iscopy = CommonFunction.setClipboard(mContext, content);
        if (iscopy) {
            CommonFunction.toastMsg(mContext, R.string.chat_copy_text_succ);
        }
    }

    private void handleDelete(ChatRecord record) {
        delOneRecord(record);
    }

//    private void handleRepeater(ChatRecord record) {
//        addMoreHandleList(record);
//        int sendCount = isForbidedSend(record) ? 0 : 1;
//        showForideRepeatMsgNotice(sendCount, moreHandleRecordList.size());
//    }

    private void handleMore(ChatRecord record) {
        handleMoreOperation(record);
    }

    private void handleSave(ChatRecord record) {

    }

    private void handleSpeakerPlay(ChatRecordView recordView, ChatRecord record) {
        if (recordView instanceof MySelfSoundRecordView) {
            ((MySelfSoundRecordView) recordView).onClick(recordView);
        } else if (recordView instanceof FriendSoundRecordView) {
            ((FriendSoundRecordView) recordView).onClick(recordView);
        }
    }

    private void handleDetail(ChatRecord record) {
        checkFaceDetail(record);
    }

    private void handleAt(ChatRecord record) {
        handleAtOperation(record);
    }

    protected void handleAtOperation(ChatRecord record) {

    }

    // 处理屏幕大小变化
    protected abstract void handleScreenResize(Message msg);

    protected void backBtnClick() {
        if (llMoreHandle.getVisibility() == View.VISIBLE) {
            chatInputBarLayout.setVisibility(View.VISIBLE);
            llMoreHandle.setVisibility(View.GONE);
            adapter.showCheckBox(false);
            for (ChatRecord record : adapter.getSeletedList()) {
                record.setSelect(false);
            }
            adapter.clearSeletedList();
            moreHandleRecordList.clear();
        } else {
            if (flyAudioRoom != null) {
                flyAudioRoom.clearAudio();
            }
            hideKeyboard();
            finish();
        }
    }

    protected boolean hideChatInputBar() {
        if (llMoreHandle.getVisibility() == View.VISIBLE) {
            chatInputBarLayout.setVisibility(View.VISIBLE);
            llMoreHandle.setVisibility(View.GONE);
            adapter.showCheckBox(false);
            for (ChatRecord record : adapter.getSeletedList()) {
                record.setSelect(false);
            }
            adapter.clearSeletedList();
            moreHandleRecordList.clear();
            return true;
        }
        return false;
    }

    protected abstract void giftBtnClick();

    protected abstract void faceBtnClick();

    protected abstract void toolBtnClick();

    protected abstract void soundTextSwitcherClick();

    // 把聊天记录保存到缓存中
    protected abstract void saveRecordToBuffer(ChatRecord record);

    /**
     * 组装ChatMessageBean
     */
    protected abstract ChatMessageBean composeChatMessageBean(ChatRecord record);

    /**
     * 组装消息,并且发送消息
     *
     * @param flag       消息的时间戳
     * @param type       消息类型 @ChatMessageType
     * @param attachment 消息的附件
     * @param msg        消息的内容
     */
    protected void sendMsg(long flag, int type, String attachment, String field1, String msg) {

        if (isSendTimeLimit(0))
            return;

        if (type != ChatMessageType.SOUND) {
            playSound();
        }

        final ChatRecord record = composeRecordForSend(flag, type, attachment, field1, msg);

        //私聊页面不显示等级
        if (this instanceof ChatPersonal) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }
        saveRecordToBuffer(record);

        ChatMessageBean messageBean = composeChatMessageBean(record);
        sendProtocal(messageBean, false, false);
        showListBottom();
    }

    /**
     * 无状态组装消息,并且发送消息
     *
     * @param flag       消息的时间戳
     * @param type       消息类型 @ChatMessageType
     * @param attachment 消息的附件
     * @param msg        消息的内容
     */
    protected void sendMsgNoStatus(long flag, int type, String attachment, String field1, String msg) {

        if (isSendTimeLimit(0))
            return;

        if (type != ChatMessageType.SOUND) {
            playSound();
        }

        final ChatRecord record = composeRecordForSend(flag, type, attachment, field1, msg);
        record.setStatus(ChatRecordStatus.NONE);

        //私聊页面不显示等级
        if (this instanceof ChatPersonal) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }

        saveRecordToBuffer(record);

        ChatMessageBean messageBean = composeChatMessageBean(record);
        sendProtocal(messageBean, false, false);
        showListBottom();
    }

    /**
     * 应用发消息，非输入框文本发送
     *
     * @param flag       消息的时间戳
     * @param type       消息类型 @ChatMessageType
     * @param attachment 消息的附件
     * @param msg        消息的内容
     */
    protected void sendMsgWithoutText(long flag, int type, String attachment, String field1, String msg, String replys) {
        final ChatRecord record = composeRecordForSend(flag, type, attachment, field1, msg);

        record.setReply(replys);

        //私聊页面不显示等级
        if (this instanceof ChatPersonal) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }

        saveRecordToBuffer(record);
        ChatMessageBean messageBean = composeChatMessageBean(record);
        sendProtocal(messageBean, false, false);
        showListBottom();
    }

    /**
     * 转发给某人,还有一种情况是转发到某圈
     */
    protected void repeaterUserSendMsg(long flag, ChatTargetInfo targetInfo, ChatRecord record,
                                       User fUser) {

        int type = Integer.valueOf(record.getType());
        String attachment = record.getAttachment();
        String msg = record.getContent();
        ChatRecord newRecord = composeRecordForSend(flag, type, attachment, msg, fUser);

        //私聊页面不显示等级
        if (this instanceof ChatPersonal) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }

        if (fUser.getUid() == getTargetID()) {
            saveRecordToBuffer(newRecord);
        }

        ChatMessageBean messageBean = new ChatMessageBean();
        messageBean.chatRecord = newRecord;

        messageBean.chatType = ChatMessageBean.PRIVATE_CHAT;
        messageBean.targetInfo = targetInfo;

        if (record.getType() == String.valueOf(ChatRecordViewFactory.IMAGE)) {
            messageBean.resourceType = FileUploadType.PIC_PRIVATE_CHAT;
        } else if (record.getType() == String.valueOf(ChatRecordViewFactory.VIDEO)) {
            messageBean.resourceType = FileUploadType.VIDEO_PRIVATE_CHAT;
        }

        sendProtocal(messageBean, false, true);
    }

    /**
     * 转发到某圈
     */
    protected void repeaterGroupSendMsg(long flag, GroupChatInfo targetInfo, ChatRecord record) {

        int type = Integer.valueOf(record.getType());
        String attachment = record.getAttachment();
        String msg = record.getContent();
        String field1 = record.getField1();
        ChatRecord newRecord = composeRecordForSend(flag, type, attachment, field1, msg);

        //私聊页面不显示等级
        if (this instanceof ChatPersonal) {
            record.setLevel(-1);
            record.setFLevel(-1);
        }

        if (targetInfo.getGroupId() == getTargetID()) {
            saveRecordToBuffer(newRecord);
        }

        ChatMessageBean messageBean = new ChatMessageBean();
        messageBean.chatRecord = newRecord;
        messageBean.chatType = ChatMessageBean.GROUP_CHAT;
        messageBean.targetInfo = targetInfo;

        if (record.getType() == String.valueOf(ChatRecordViewFactory.IMAGE)) {
            messageBean.resourceType = FileUploadType.PIC_GROUP_CHAT;
        } else if (record.getType() == String.valueOf(ChatRecordViewFactory.VIDEO)) {
            messageBean.resourceType = FileUploadType.VIDEO_GROUP_CHAT;
        }

        sendProtocal(messageBean, false, true);
    }

    /**
     * 消息重发
     */
    protected boolean sendMsgAgain(final ChatRecord record) {

        if (isSendTimeLimit(0))
            return false;

        playSound();

        ChatMessageBean messageBean = composeChatMessageBean(record);
        sendProtocal(messageBean, true, false);
        return true;
    }

    /**
     * 发协议
     */
    private void sendProtocal(final ChatMessageBean messageBean, final boolean isResend,
                              final boolean isRepeater) {

        updateList();

        // 发送消息协议
        dataHandler.post(new Runnable() {

            @Override
            public void run() {
                backManager.sendMessage(messageBean, isResend, isRepeater);
            }
        });
    }

    /**
     * 获取当前时间戳
     */
    protected long getCurrentFlag() {
        return System.currentTimeMillis() + Common.getInstance().serverToClientTime - 2000;
    }

    /**
     * 点击菜单
     *
     * @param type 1点击表情，2点击菜单
     */
    protected void clickMenu(int type) {

        initMenuIfNeeded(type);

        if (type == 0) {
            hideMenu();
            hideKeyboard();
            return;
        } else if (type == 1) {
            if (isKeyboardShow()) {
                isSwichFaceToKeyboard = true;
                hideKeyboard();
                showChatFaceMenuDelayed(200);
            } else {
                if (chatFace.isShown()) {
                    hideFaceShowKeyboard();
//                    faceMenuBtn.setImageResource(R.drawable.iaround_chat_face);
                    faceMenuBtn.setBackgroundResource(R.drawable.iaround_chat_face);
                } else {
                    int sendBtnTag =
                            mSoundTextSwitch.getTag() == null ? 1 : (Integer) mSoundTextSwitch
                                    .getTag();
                    if (sendBtnTag == 0) {
                        updateSwitcherBtnState();
                    }
                    showChatFaceMenuDelayed(200);
                }
            }

        } else if (type == 2) {
            if (isKeyboardShow()) {

                hideKeyboard();
                showChatToolMenuDelayed(200);
            } else {
                if (chatTool.isShown()) {
                    hideMenu();
                    showKeyboardDelayed(200);
                } else {
                    showChatToolMenuDelayed(0);
                }
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                showListBottom();
            }
        }, 300);

        // 让输入框获取焦点
        editContent.requestFocus();
        mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
    }

    /**
     * 更新语音键盘切换按钮
     */
    protected void updateSwitcherBtnState() {
        // 获取输入框的内容，并判断是否已输入内容

        int sendBtnTag = mSoundTextSwitch.getTag() == null ? SOUND_TAG : (Integer) mSoundTextSwitch.getTag();

        if (sendBtnTag == TEXT_TAG) { // 显示语音按钮
            mSoundTextSwitch.setBackgroundResource(R.drawable.iaround_chat_sound);
            mSoundTextSwitch.setTag(SOUND_TAG);

            chatVoice.setVisibility(View.GONE);
            editContent.setVisibility(View.VISIBLE);
            editContent.requestFocus();
            updataSendBtn();

        } else if (sendBtnTag == SOUND_TAG) { // 显示键盘按钮
            //请求语音权限
            CommonFunction.log(TAG, "显示声音按钮");
            requestRecordAudio();
        }
    }

    @Override
    public void doRecordAudio() {
        super.doRecordAudio();
        CommonFunction.log(TAG, "doRecordAudio() into");
        mSoundTextSwitch.setBackgroundResource(R.drawable.iaround_chat_keyborad);
        mSoundTextSwitch.setTag(TEXT_TAG);

        chatVoice.setVisibility(View.VISIBLE);
        editContent.setVisibility(View.GONE);
        mBtnSend.setVisibility(View.GONE);
        if (showMoreMenuBtn() == 0) {
            toolMenuBtn.setVisibility(View.GONE);
        } else {
            toolMenuBtn.setVisibility(View.VISIBLE);
        }

        hideMenu();
    }

    /**
     * 发送地理位置
     */
    protected void sendLocation() {
        // 直接发送地理位置
        if (System.currentTimeMillis() - locationLimitTime > 5000) { // 发送地理位置的限制时间
            loadGeoData(mHandler, mActivity);
            // 限制时间
            locationLimitTime = System.currentTimeMillis();
        } else {
            CommonFunction.showToast(this, getResString(R.string.chat_send_location_limit), 0);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isRestart) {
            return;
        }
        initTargetInfomation();

        mHandler = new ChatHandler(this);
        HandlerThread hHander = new HandlerThread("data_thread");
        hHander.start();
        dataHandler = new Handler(hHander.getLooper());

        backManager = MessagesSendManager.getManager(mContext);
        backManager.setChatHanlder(mHandler);

        initBroadcastReceiver();
        initLimitTimer();

        isRequireCheck = true;
        setMissingPermission(3);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("REPEATER_LIST", moreHandleRecordList);
        outState.putParcelableArrayList("DATA_LIST", mRecordList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);
        moreHandleRecordList = outState.getParcelableArrayList("REPEATER_LIST");
        mRecordList = outState.getParcelableArrayList("DATA_LIST");
    }

    // 初始化聊天对象信息
    public abstract void initTargetInfomation();

    @Override
    protected void onResume() {
        CommonFunction.log(TAG, "onResume() into");
        super.onResume();
        if (isRestart) {
            return;
        }
        try {
            if (setThisCallbackAction) {
                // activity 销毁的时候应该设置为空 不然  ConnectorManage 单例会一直拿着当前的回调
//                CommonFunction.log(TAG, "getConnectorManage setCallBackAction");
//                getConnectorManage().setCallBackAction(this);
            }

            hideMenu();

            if (this instanceof ChatPersonal) {
                if (audioTouchListener != null) {
                    audioTouchListener.releaseAudioRecorder();
                    audioTouchListener = null;
                }
                if (audioTouchListener == null) {
                    audioTouchListener = new ChatSendAudioTouchListener(mContext, chatAudioLayout, chatVoice, backManager, mHandler);
                    chatVoice.setOnTouchListener(audioTouchListener);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isRestart) {
            return;
        }
        // 在onPause析构audioTouchListener，原因：圈聊进入私聊时，会出现对录音器多次初始化
        if (this instanceof ChatPersonal) {
            if (audioTouchListener != null) {
                if (audioTouchListener.mHandler != null) {
                    audioTouchListener.bIsSend = false;
                    audioTouchListener.stopRecord();
                }
                audioTouchListener.releaseAudioRecorder();
                audioTouchListener = null;
            }
        }

        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_POSTION);
        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_POSTION);
        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_ARRAY_POSTION);
        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_ARRAY_POSTION);
        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_BAG_GIFT_ARRAY_VALUE);
        SharedPreferenceUtil.getInstance(this).remove(CustomGiftDialogNew.LAST_STORE_GIFT_ARRAY_VALUE);
    }

    protected void initInputBar() {
        //添加礼物按钮
        giftMenuBtn = (Button) chatInputBarLayout.findViewById(R.id.btn_gift);
        faceMenuBtn = (Button) chatInputBarLayout.findViewById(R.id.face_menu);
        toolMenuBtn = (Button) chatInputBarLayout.findViewById(R.id.tool_menu);
        editContent = (EditTextViewUtil) chatInputBarLayout.findViewById(R.id.editContent);
        chatVoice = (LinearLayout) chatInputBarLayout.findViewById(R.id.chat_voice);
        mSoundTextSwitch = (Button) chatInputBarLayout.findViewById(R.id.btnSoundSwithcher);
        mBtnSend = (TextView) chatInputBarLayout.findViewById(R.id.btnSend);
        faceMenuContainer = (FrameLayout) chatInputBarLayout.findViewById(R.id.faceMenu);

        btNewMessage = (Button) chatInputBarLayout.findViewById(R.id.btn_chat_bar_new_message);
        btWorldMessage = (Button) chatInputBarLayout.findViewById(R.id.btn_chat_bar_world_message);
        btnLuckpan = (Button) chatInputBarLayout.findViewById(R.id.btn_chat_bar_luckpan);
        btTask = (RelativeLayout) chatInputBarLayout.findViewById(R.id.rl_btn_chat_bar_task);
        viewTask = chatInputBarLayout.findViewById(R.id.view_btn_chat_bar_task);
        btShare = (Button) chatInputBarLayout.findViewById(R.id.btn_chat_bar_share);
        rlNewMessage = (RelativeLayout) chatInputBarLayout.findViewById(R.id.rl_chat_bar_bottom_new_message);
        dragPointView = (DragPointView) chatInputBarLayout.findViewById(R.id.drag_point_new_message);
        luckPanCountView = (DragNullPointView) chatInputBarLayout.findViewById(R.id.chat_bar_bottom_luckpan_count);
        luckPanContainer = (RelativeLayout) chatInputBarLayout.findViewById(R.id.rl_chat_bar_bottom_luckpan);
        LyMore = (LinearLayout) chatInputBarLayout.findViewById(R.id.ly_more);

        ivDelete = (ImageView) llMoreHandle.findViewById(R.id.ivDelete);
        ivRepeater = (ImageView) llMoreHandle.findViewById(R.id.ivRepeater);
        ivDelete.setOnClickListener(this);
        ivRepeater.setOnClickListener(this);

        // 设置输入框的内容监听器
        editContent.addTextChangedListener(textWatcher);
        // 设置touch事件监听器的目的是为了在调用键盘时和收起菜单栏的时间错开
        editContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (keyboardState == KEYBOARD_HIDE) {
                    hideFaceShowKeyboard();
                }

                return false;
            }
        });

        flagImageViewBarrage = (FlagImageViewBarrage) chatInputBarLayout.findViewById(R.id.flag_barrage);
        flagImageViewBarrage.setOnClickListener(this);
        flagImageViewBarrage.setState(barrageState);

        isFrom = showMoreMenuBtn();
        if (isFrom == 0) {//聊吧
            toolMenuBtn.setVisibility(View.GONE);
            mSoundTextSwitch.setVisibility(View.GONE);
            flagImageViewBarrage.setVisibility(View.GONE);//世界消息弹幕开关
            // TODO: 2017/7/13 判断软键盘是否弹出
            if (keyboardState == KEYBOARD_SHOW) {
                faceMenuBtn.setVisibility(View.VISIBLE);
                LyMore.setVisibility(View.GONE);
            } else {
                faceMenuBtn.setVisibility(View.GONE);
                LyMore.setVisibility(View.VISIBLE);
            }
        } else {
            toolMenuBtn.setVisibility(View.VISIBLE);
            faceMenuBtn.setVisibility(View.VISIBLE);
            LyMore.setVisibility(View.GONE);
            flagImageViewBarrage.setVisibility(View.GONE);//世界消息弹幕开关
            mSoundTextSwitch.setVisibility(View.VISIBLE);
        }

        faceMenuBtn.setOnClickListener(this);
        giftMenuBtn.setOnClickListener(this);
        toolMenuBtn.setOnClickListener(this);
        mSoundTextSwitch.setOnClickListener(this);
        mBtnSend.setOnClickListener(this);
        btNewMessage.setOnClickListener(this);
        btWorldMessage.setOnClickListener(this);
        btTask.setOnClickListener(this);
        btShare.setOnClickListener(this);
        btnLuckpan.setOnClickListener(this);
    }

    /**
     * 输入框有输入文本的时候
     */
    private void updataSendBtn() {
        String str = getEditContent().getEditableText().toString().trim();
        boolean isShow = !TextUtils.isEmpty(str);

        if (isShow) {
            mBtnSend.setVisibility(View.VISIBLE);
            toolMenuBtn.setVisibility(View.GONE);
        } else {
            mBtnSend.setVisibility(View.GONE);
            if (showMoreMenuBtn() == 0) {//聊吧
                toolMenuBtn.setVisibility(View.GONE);
            } else {//私聊
                toolMenuBtn.setVisibility(View.VISIBLE);
            }
        }
    }

    //--------------------RecordBtnTouchCallBack
    //用于添加当录音按钮被Touch时的操作（例：当Touch Down 的时候停止播放音频
    @Override
    public void onRecordBtnTouchDown() {
    }

    @Override
    public void onRecordBtnTouchMove() {
    }

    @Override
    public void onRecordBtnTouchUp() {
    }

    @Override
    public void onRecordBtnTouchCancel() {
    }
    //--------------------RecordBtnTouchCallBack

    @Override
    protected void onDestroy() {
        CommonFunction.log(TAG, "onDestroy() into");
        super.onDestroy();
        if (isRestart) {
            return;
        }
        if (limiTimer != null) {
            limiTimer.cancel();
            limiTimer = null;
        }

        if (this instanceof ChatPersonal) {
            unregisterReceiver(mScreenOffReceiver);
        }
        unregisterReceiver(mBuyFaceBroadcastReceiver);

//		ImageViewUtil.getDefault( ).clearDefaultLoaderMemoryCache( );//jiqiang
        if (backManager != null) {
            backManager.clearBind();
        }
        adapter = null;

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        if (this instanceof ChatPersonal) {
            audioTouchListener = null;
        }

        GalleryUtils.getInstance().openGalleryMuti(null, 0, 0, null);
    }

    /**
     * 判断当前应用程序处于前台还是后台
     */
    private boolean isRunningForeground() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) &&
                currentPackageName.equals(getPackageName());
    }

    /**
     * 构造和初始化表情视图
     */
    protected ChatFace createChatFace(ViewGroup viewGroup, OnItemClickListener clickListener) {
        ChatFace cf = new ChatFace(this, viewGroup);
        cf.setKeyboardClickListener(new FaceKeyboardClickListener(mActivity));
        if (clickListener == null) {
            clickListener = new FaceIconClickListener(mActivity, editContent);
        }
        cf.setIconClickListener(clickListener);

        if (SendFaceToFriends.isFromSendFriends == true) {
            int index = 0;
            index = FaceDetailActivityNew.getFaceIndex();

            cf.clickTab(index, 1);
            cf.updateNewFace();// 检查是否有新表情

        } else {
            cf.initFace();
        }
        return cf;
    }


    /**
     * 发送文本、表情
     */
    protected void sendTextFace() {
        String editContentStr = editContent.getText().toString().trim();

        if (mContext instanceof ChatPersonal) { // 私聊时，不做屏蔽字处理
            if (CommonFunction.forbidSay(mActivity)) {
                return;
            }
        } else {
            editContentStr = CommonFunction.filterKeyWordAndReplaceEmoji(mActivity, editContentStr);// 敏感字符处理
            if (CommonFunction.forbidSay(mActivity)) { // 是否被禁言
                return;
            }
        }

        ArrayList<String> msgs = CommonFunction.subFaceString(editContent, editContentStr, EDITTEXT_PER_MSG_MAX);
        if (msgs != null)
            for (int i = 0; i < msgs.size(); i++) {
                sendMsg(getCurrentFlag() + i, MessageType.TEXT, "", "", msgs.get(i));
            }
        editContent.getText().clear();
    }

    /**
     * 组合聊天记录，用于发送
     *
     * @param flag
     * @param type       消息类型
     * @param attachment 附件
     * @param msg        需要发送的文本消息
     * @return
     */
    protected ChatRecord composeRecordForSend(long flag, int type, String attachment, String field1, String msg) {
        Me user = Common.getInstance().loginUser;

        ChatRecord record = new ChatRecord();
        record.setId(flag); // 消息id
        record.setUid(user.getUid());
        record.setNickname(user.getNickname());
        record.setIcon(user.getIcon());
        record.setSVip(user.getSVip());
        record.setVip(user.getViplevel());
        record.setLevel(user.getLevel());
        record.setDatetime(flag);
        record.setType(Integer.toString(type));
        record.setStatus(ChatRecordStatus.SENDING); // 默认，发送中
        record.setAttachment(attachment);
        record.setField1(field1);
        record.setContent(msg);
        record.setUpload(false);
        record.setFuid(getTargetID());
        record.setSendType(MessageBelongType.SEND);
        record.setDistance(ChatPersonalModel.UNKNOWN_DISTANCE);
        record.setFlag(flag);

        if (String.valueOf(ChatMessageType.IMAGE).equals(record.getType())) {

            if (!attachment.contains(PathUtil.getHTTPPrefix())) {
                File f = new File(attachment);
                record.setUpload(true);
                record.setFileLen((int) f.length());
                record.setUploadLen(0);
            } else {
                record.setUpload(false);
            }
        }

        return record;
    }

    /**
     * 组合聊天记录，用于发送
     *
     * @param flag
     * @param type       消息类型
     * @param attachment 附件
     * @param msg        需要发送的文本消息
     * @return
     */
    protected ChatRecord composeRecordForSend(long flag, int type, String attachment, String msg,
                                              User fUser) {
        Me me = Common.getInstance().loginUser;

        ChatRecord record = new ChatRecord();
        record.setId(flag); // 消息id
        record.setUid(me.getUid());
        record.setNickname(me.getNickname());
        record.setIcon(me.getIcon());
        record.setVip(me.getViplevel());
        record.setSVip(me.getSVip());
        record.setLevel(me.getLevel());
        record.setDatetime(flag);
        record.setType(Integer.toString(type));
        record.setStatus(ChatRecordStatus.SENDING); // 默认，发送中
        record.setAttachment(attachment);
        record.setContent(msg);
        record.setUpload(false);
        record.setFuid(getTargetID());
        record.setSendType(MessageBelongType.SEND);
        record.setDistance(ChatPersonalModel.UNKNOWN_DISTANCE);

        // 私聊中需要在recrod中添加对方的个人信息
        CommonFunction.log(TAG, "super composeRecordForSend ==========" + fUser.getSVip());
        record.setFuid(fUser.getUid());
        record.setfNickName(fUser.getNickname());
        record.setfNoteName(fUser.getNickname());
        record.setfVipLevel(fUser.getViplevel());
        record.setSVip(fUser.getSVip());
        record.setfIconUrl(fUser.getIcon());
        record.setfSex(fUser.getSexIndex());
        record.setfAge(fUser.getAge());
        record.setfLat(fUser.getLat());
        record.setfLng(fUser.getLng());

        if (record.getType() == String.valueOf(ChatMessageType.IMAGE)) {

            if (!attachment.contains(PathUtil.getHTTPPrefix())) {
                File f = new File(attachment);
                record.setUpload(true);
                record.setFileLen((int) f.length());
                record.setUploadLen(0);
            } else {
                record.setUpload(false);
            }
        }

        return record;
    }

    // 发送是否受限
    protected boolean isSendTimeLimit(int type) {
        // 可以根据type来判断是否需要限制
        if (currentSendCount >= SEND_COUNT_LIMIT) {
            if (sendLimitToast == null) {
                sendLimitToast = Toast
                        .makeText(mContext, mContext.getString(R.string.send_msg_limit),
                                Toast.LENGTH_SHORT);
            }
            sendLimitToast.show();

            if (!bIsEnterLimit) {
                enterColdTime();
            }
            return true;
        } else {
            currentSendCount++;
        }

        return false;
    }

    /**
     * 显示表情菜单
     */
    protected void displayFaceMenu() {
        if (!CommonFunction.forbidSay(mActivity)) { // 是否被禁言
            clickMenu(1);
        }
    }

    /**
     * 显示菜单按钮
     */
    protected void displayToolMenu() {
        clickMenu(2);
    }

    /**
     * 发送地理位置
     */
    protected void sendLocation(int lat, int lng, String address) {
        if (0 == lat && 0 == lng) { // 提示获取地理位置失败
            CommonFunction
                    .showToast(mActivity, getResString(R.string.chat_get_location_fail), 0);
        } else if (CommonFunction.isEmptyOrNullStr(address)) { // 地址无效时
            CommonFunction
                    .showToast(mActivity, getResString(R.string.chat_get_location_fail), 0);
        } else {
            // 发送位置
            sendMsg(getCurrentFlag(), ChatMessageType.LOCATION, lat + "," + lng, "", address);
        }
    }

    @Override
    public void onToolListener(int type) {
        switch (type) {
            case ChatMenuTool.CHAT_TOOL_CAMARA: // 拍照
                break;
            case ChatMenuTool.CHAT_TOOL_PHOTO: // 相片
                requestCamera();
                break;
            case ChatMenuTool.CHAT_TOOL_VIDEO: // 视频
                requestLiveShowPermissions();
                break;
            case ChatMenuTool.CHAT_TOOL_POSITION: // 位置
                sendLocation();
                break;
        }
        hideMenu();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = String.valueOf(TimeFormat.getCurrentTimeMillis());
        File albumF = new File(PathUtil.getPictureDir());
        File imageF = File.createTempFile(imageFileName, PathUtil.getJPGPostfix(), albumF);
        return imageF;
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        path = f.getAbsolutePath();

        return f;
    }

    private final int REQUEST_CODE_GALLERYS = 1002;

    @Override
    public void doCamera() {
        super.doCamera();
        int maxSend = 6;
        GalleryUtils.getInstance().openGalleryMuti(this, REQUEST_CODE_GALLERYS, maxSend, mOnHanlderResultCallback);
    }

    @Override
    public void doLiveShowPerssiomison() {
        super.doLiveShowPerssiomison();
        // TODO: 2017/5/28 获取传递进来的User对象
        Intent intent = new Intent(mActivity, ChatVideoRecorder.class);
        startActivityForResult(intent, ChatRequestCode.CHAT_VIDEO_RECORDER_REQUEST_CODE);
    }

    /**
     * 获取经纬度、地址信息成功
     *
     * @param msg
     */
    protected void handleGetAddressInfoSucc(Message msg) {
        sendLocation(msg.arg1, msg.arg2, String.valueOf(msg.obj));
    }

    /**
     * 获取经纬度、地址信息失败
     *
     * @param msg
     */
    protected void handleGetAddressInfoFail(Message msg) {
        sendLocation(0, 0, "");
    }

    protected void hideKeyboard() {
        CommonFunction.hideInputMethod(mContext, editContent);
        setKeyboardStateHide();
        if (isFrom == 0) {//聊吧
            // TODO: 2017/7/13 YC判断软键盘是否弹出
            if (isSwichFaceToKeyboard) {
                faceMenuBtn.setVisibility(View.VISIBLE);
                LyMore.setVisibility(View.GONE);
                flagImageViewBarrage.setVisibility(View.GONE);//世界消息弹幕开关
                isSwichFaceToKeyboard = false;
            } else {
                if (isKeyboardShow()) {
                    faceMenuBtn.setVisibility(View.VISIBLE);
                    LyMore.setVisibility(View.GONE);
                } else {
                    faceMenuBtn.setVisibility(View.GONE);
                    LyMore.setVisibility(View.VISIBLE);
                }
                flagImageViewBarrage.setVisibility(View.GONE);//世界消息弹幕开关
            }
            if (luxuryGiftView != null) {
                luxuryGiftView.setHeightPosition(33);
                luxuryGiftView.upDateDefaultMargin(33);
            }
        } else {//私聊
            faceMenuBtn.setVisibility(View.VISIBLE);
            LyMore.setVisibility(View.GONE);
        }
    }

    /**
     * 隐藏菜单
     */
    protected void hideMenu() {
        hideChatFace();

        if (chatTool != null) {
            chatTool.setVisibility(View.GONE);
        }

        if (faceMenuBtn != null) {
            faceMenuBtn.setBackgroundResource(R.drawable.iaround_chat_face);
        }

        if (faceMenuContainer != null) {
            faceMenuContainer.invalidate();
        }
    }

    protected void setKeyboardStateHide() {
        keyboardState = KEYBOARD_HIDE;
    }

    protected void setKeyboardStateShow() {
        keyboardState = KEYBOARD_SHOW;
    }

    private void showChatFace() {
        if (chatFace != null && chatFace.getVisibility() == View.GONE) {
            chatFace.setVisibility(View.VISIBLE);
            if (faceShowCallback != null)
                faceShowCallback.onFaceMenuShow();
        }
    }

    private void hideChatFace() {
        if (chatFace != null && chatFace.getVisibility() == View.VISIBLE) {
            chatFace.setVisibility(View.GONE);
            if (faceShowCallback != null)
                faceShowCallback.onFaceMenuHide();
        }
    }

    protected void initBroadcastReceiver() {
        IntentFilter intentFilter = null;
        if (this instanceof ChatPersonal) {
            mScreenOffReceiver = new ScreenOffReceiver(this);
            intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            registerReceiver(mScreenOffReceiver, intentFilter);
        }

        mBuyFaceBroadcastReceiver = new BuyFaceBroadcastReceiver(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(FaceManager.FACE_INIT_ACTION);
        registerReceiver(mBuyFaceBroadcastReceiver, intentFilter);
    }

    protected boolean isMenuOpen() {
        return faceMenuContainer.getHeight() > 0;
    }

    /**
     * @param type 1 表情菜单 2 功能菜单
     */
    protected void initMenuIfNeeded(int type) {
        if (type == 0) {
            return;
        }

        if (chatFace != null && chatTool != null) {
            return;
        }

        chatFace = createChatFace(faceMenuContainer, new MyFaceIconClickListener(this, editContent));
        chatFace.setVisibility(View.GONE);
        faceMenuContainer.addView(chatFace, 0);

        chatTool = new ChatMenuTool(mActivity, faceMenuContainer);
        chatTool.setVisibility(View.GONE);
        int groupType = 1;
        if (mActivity instanceof GroupChatTopicActivity) {
            groupType = 2;
        }//jiqiang
        chatTool.initTool(this, groupType);
        faceMenuContainer.addView(chatTool, 1);
    }


    /**
     * 延迟X毫秒显示键盘
     *
     * @param delayMillis
     */
    protected void showKeyboardDelayed(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                CommonFunction.showInputMethodForQuery(mContext, editContent);
                setKeyboardStateShow();
                // TODO: 2017/7/13 判断软键盘是否弹出
                if (isKeyboardShow()) {
                    if (isFrom == 0) {
                        flagImageViewBarrage.setVisibility(View.VISIBLE);//世界消息弹幕开关
                    }

                    LyMore.setVisibility(View.GONE);
                } else {
                    faceMenuBtn.setVisibility(View.GONE);
                    LyMore.setVisibility(View.VISIBLE);
                }
                if (luxuryGiftView != null) {
                    luxuryGiftView.setHeightPosition(5);
                    luxuryGiftView.upDateDefaultMargin(5);
                }
            }
        }, delayMillis);

    }

    /**
     * 是否显示功能键和发送语音按钮
     */
    protected int showMoreMenuBtn() {
        return isFrom;
    }

    protected void showChatToolMenuDelayed(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideChatFace();

                if (chatTool != null) {
                    chatTool.setVisibility(View.VISIBLE);
                }

                if (faceMenuBtn != null) {
                    faceMenuBtn.setBackgroundResource(R.drawable.iaround_chat_face);
                }

            }
        }, delayMillis);
    }

    /**
     * 展示表情界面
     *
     * @param delayMillis
     */
    protected void showChatFaceMenuDelayed(int delayMillis) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showChatFace();
                if (chatFace != null && chatFace.isShown()) {
                    LyMore.setVisibility(View.GONE);
                }
                if (chatTool != null) {

                    chatTool.setVisibility(View.GONE);
                }
                if (faceMenuBtn != null) {
                    faceMenuBtn.setVisibility(View.VISIBLE);
                    faceMenuBtn.setBackgroundResource(R.drawable.iaround_chat_keyborad);
                }
            }
        }, delayMillis);

    }

    /**
     * 播放音效
     */
    protected void playSound() {
        // 当通知设置为完全静音或仅振动的时候不发出音效
        SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(mContext);
        int svType = sp.getInt(SharedPreferenceUtil.SOUND_VIBRATION_PROMPT);
        if (svType == SoundShakeType.NO_SOUND || svType == SoundShakeType.ONLY_VIBRATION)
            return;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        if (mediaPlayer.isPlaying()) {
                            return;
                        }
                        mediaPlayer.release();
                    }
                    mediaPlayer = MediaPlayer.create(mContext, R.raw.send_msg);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                } catch (Throwable e) {
                    CommonFunction.log(e);
                }
            }
        });
        thread.start();
    }

    protected int getRecordSize() {
        int size = 0;
        if (mRecordList != null) {
            for (ChatRecord chatRecord : mRecordList) {
                if (!TIME_LINE_TYPE.equals(chatRecord.getType())) {
                    size++;
                }
            }
        }

        return size;
    }

    /**
     * 让listview显示最底部的数据
     */
    protected void showListBottom() {
        if (adapter != null) {
            CommonFunction.log(TAG, "have call showListBottom");
            chatRecordListView.setSelection(chatRecordListView.getBottom()); // 让listview显示最底部的数据
        }
    }

    /**
     * 当在底部附近的时候跳到最底部显示最后的数据
     */
    protected void autoShowListBottom() {
        if (isNearBottom()) { // 判断滚动到底部
            showListBottom(); // 让listview显示最底部的数据
        }
    }

    /**
     * 判断当前是否在底部附近
     */
    protected boolean isNearBottom() {//如果最后一项记录显示着，返回true，否则返回false
        return chatRecordListView.getLastVisiblePosition() >=
                (chatRecordListView.getCount() - NEAR_BOTTOM_OFFSET);
    }

    /**
     * 根据msgId删除对应的某条聊天记录，同时判断上一条是否为时间标识
     *
     * @param recordList
     * @param msgId
     */
    protected void delRecordByMsgId(ArrayList<ChatRecord> recordList, long msgId) {
        if (recordList != null) {
            for (int i = 0; i < recordList.size(); i++) {
                ChatRecord record = recordList.get(i);
                if (record.getId() == msgId) {

                    int size = recordList.size();

                    // 判断删除的上一条是否为时间分界,i不可能为0
                    if (i > 0 && recordList.get(i - 1).getType().equals(TIME_LINE_TYPE)) {
                        if (i + 1 < size &&
                                !recordList.get(i + 1).getType().equals(TIME_LINE_TYPE)) {
                            long dataTime = recordList.get(i + 1).getDatetime();
                            int distance = recordList.get(i + 1).getDistance();
                            recordList.remove(i);
                            recordList.get(i - 1).setDatetime(dataTime);
                            recordList.get(i - 1).setDistance(distance);

                        } else {
                            recordList.remove(i);
                            recordList.remove(i - 1);
                        }
                    } else {
                        recordList.remove(i);
                    }

                    recordList.trimToSize();
                    break;
                }
            }
        }
    }

    private void initLimitTimer() {
        limiTimer = new Timer();
        limiTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (currentSendCount > 0 && currentSendCount < SEND_COUNT_LIMIT) {
                    currentSendCount--;
                }
            }
        }, 0, SEND_UNIT_TIME);
    }

    // 消息处理标识码
    public class HandleMsgCode {
        /** -----------------公共部分------------------- **/

        /**
         * 消息：屏幕大小发生改变
         */
        public static final int MSG_RESIZE = 1;
        /**
         * 消息： 让listview显示最底部的数据
         */
        public static final int MSG_SHOW_LIST_BOTTOM = 2;
        /**
         * 消息： 当发送消息，返回的发送情况
         */
        public static final int MSG_SEND_MESSAGE = 3;
        /**
         * 消息：上传出错
         */
        public static final int MSG_UPLOAD_ERROR = 4;
        /**
         * 消息：上传进度
         */
        public static final int MSG_UPLOAD_PROGRESS = 5;
        /**
         * 消息：上传完成
         */
        public static final int MSG_UPLOAD_FINISH = 6;
        /**
         * 加载历史消息
         */
        public static final int MSG_GET_HISTORY = 7;
        /**
         * 消息：语音数据录音开始
         **/
        public static final int AUDIO_DATA_RECORD_START = 10;
        /**
         * 消息：语音数据录音完成
         **/
        public static final int AUDIO_DATA_RECORD_FINISH = 11;
        /**
         * 消息：刷新数据
         **/
        public static final int NOTIFY_DATA_CHANGE = 12;
        /**
         * 消息：图片压缩成功,发送图片
         **/
        public static final int PIC_COMPRESS_SEND = 13;
        /**
         * 消息：显示未看数量如果未看数量小于等于0时隐藏
         */
        public static final int SHOW_AND_CLEAR_UNSEE = 14;


        /** -----------------私聊部分------------------- **/

        /**
         * 消息：成功发送私聊消息
         */
        public static final int MSG_SEND_PRIVATE_CHAT_SUCC = 100;
        /**
         * 消息：发送私聊消息失败
         */
        public static final int MSG_SEND_PRIVATE_CHAT_FAIL = 101;
        /**
         * 消息：接收私聊信息
         */
        public static final int MSG_SESSION_PRIVATE_CHAT = 102;
        /**
         * 消息：接收已读私聊消息(最大的消息id)
         */
        public static final int MSG_GET_MAX_READED_RECORED_ID = 104;
        /**
         * 消息：成功列入黑名单
         */
        public static final int MSG_ADD_TO_BACKLIST_SUCC = 105;
        /** 消息：判断场景是否有效 */
        // public static final int MSG_ADD_TO_SCENEVALID_SUCC = 106;
        /** 接收到聊天场景推送 **/
        // public static final int MSG_CHANGE_THEME = 107;
        /**
         * 最近玩过的游戏
         **/
        public static final int MSG_RECENT_SUCCES = 108;
        /** 消息：更换聊天场景 **/
        // public static final int MSG_THEME_CHAGNE_REQUEST = 109;
        /**
         * 消息：发送礼物
         **/
        public static final int MSG_SEND_GIFT_REQUEST = 110;
        /**
         * 消息：语音发送开始失败
         **/
        public static final int MSG_AUDIO_SEND_BEGIN_FAIL = 112;
        /**
         * 获取进入兑吧的最新URL
         **/
        public final static int MSG_GET_DUIBA_SUCCESS = 113;
        /**
         * 发送委托主持人或辅导员成功
         **/
        public final static int MSG_SEND_DELEGATION_SUCCESS = 114;


        /** -----------------圈聊部分------------------- **/

        /**
         * 消息： 收到广播消息
         */
        public static final int MSG_RECEIVE_ROOM_MESSAGE = 200;
        /**
         * 消息：接收被踢信息(本人被群主踢)
         */
        public static final int MSG_RECEIVE_KICKED_USER = 201;
        /**
         * 消息：群主踢人成功
         */
        public static final int MSG_KICK_USER_SUCC = 202;
        /**
         * 消息：群主踢人失败
         */
        public static final int MSG_KICK_USER_FAIL = 203;
        /**
         * 消息：成功发送聊天消息
         */
        public static final int MSG_SEND_ROOM_MSG_SUCC = 205;
        /**
         * 消息：成功发送聊天消息
         */
        public static final int MSG_SEND_ROOM_MSG_FAIL = 206;
        /**
         * 消息：初始化数据
         */
        public static final int MSG_INIT_DATA = 209;
        /**
         * 消息：群组解散消息
         */
        public static final int MSG_DISMISS_GROUP = 210;
        /**
         * 消息：进入群失败
         */
        public static final int MSG_JOIN_IN_FAIL = 211;
        /**
         * 消息：请求群管理员ID列表
         */
        public static final int MSG_REQ_GROUP_MANAGER_LIST = 212;
        /**
         * 消息：请求群管理员ID列表成功
         */
        public static final int MSG_REQ_GROUP_MANAGER_LIST_SUCC = 213;
        /**
         * 消息：请求群管理员ID列表失败
         */
        public static final int MSG_REQ_GROUP_MANAGER_LIST_FAIL = 214;

        /**
         * 消息：请求聊吧拉取资料接口成功
         */
        public static final int MSG_REQ_GROUP_SIMPLE_SUCC = 218;
        /**
         * 消息：请求聊吧拉取资料接口失败
         */
        public static final int MSG_REQ_GROUP_SIMPLE_FAIL = 219;
        /**
         * 消息：升为管理员成功
         */
        public static final int MSG_BECOME_MANAGER_SUCC = 215;
        /**
         * 消息：免去管理员成功
         */
        public static final int MSG_CANCLE_MANAGER_SUCC = 216;
        /**
         * 消息：达到管理员上限
         */
        public static final int MSG_MANAGER_LIMIT = 217;
        /**
         * 消息：进入群成功
         */
        public static final int MSG_JOIN_IN_SUCCESS = 220;

        /**
         * 获取经纬度、地址信息成功
         */
        public static final int MSG_GET_ADDRESS_INFO_SUCC = 14001;
        /**
         * 获取经纬度、地址信息失败
         */
        public static final int MSG_GET_ADDRESS_INFO_FAIL = 14002;


        /** -----------------聊吧部分------------------- **/

        /**
         * 消息：成功上麦
         */
        public static final int MSG_SEND_ON_MIC_MSG_SUCC = 230;
        /**
         * 消息：上麦失败
         */
        public static final int MSG_ON_MIC_FAIL = 232;
        /**
         * 消息：成功下麦
         */
        public static final int MSG_SEND_OFF_MIC_MSG_SUCC = 231;
        /**
         * 消息：下麦失败
         */
        public static final int MSG_OFF_MIC_FAIL = 233;

        /**
         * 消息：聊吧用户上线
         */
        public static final int MSG_ON_LINE_RESULT = 234;
        /**
         * 消息：聊吧用户下线
         */
        public static final int MSG_OFF_LINE_RESULT = 235;

        /**
         * 消息：聊吧用户上麦
         */
        public static final int MSG_ON_MIC_RESULT = 236;
        /**
         * 消息：聊吧用户下麦
         */
        public static final int MSG_OFF_MIC_RESULT = 237;

        /**
         * 消息：聊吧用户下麦
         */
        public static final int MSG_ADMIN_MESSAGE_RESULT = 238;

        /**
         * 消息：告知用户准备上麦
         */
        public static final int MSG_PREPARE_ON_MIC_MESSAGE_RESULT = 239;


        /**
         * 消息：请求聊吧在线用户接口成功
         */
        public static final int MSG_REQ_GROUP_ONLINE_SUCC = 240;
        /**
         * 消息：请求聊吧在线用户接口失败
         */
        public static final int MSG_REQ_GROUP_ONLINE_FAIL = 241;


        /**
         * 消息：告知用户准备下麦
         */
        public static final int MSG_PREPARE_OFF_MIC_MESSAGE_RESULT = 242;

        /**
         * 消息：赠送背包礼物成功返回
         */
        public static final int MSG_SEND_PACAGE_GIFT_SUCCESS_RESULT = 243;

        public final static int MSG_WHAT_REFRESH_MINE_SOCKET_SEND = 995;// 赠送礼物后发送私聊

        public final static int MSG_UPDATE_MIC_MESSAGE_RESULT = 320;// 麦位更新

        public final static int MSG_STOP_PUSH_RESULT = 321;// 指定用户停止推流

        /**
         * 消息:用户进入聊吧以吧主身份发送一条欢迎语
         */
        public static final int MSG_ON_LINE_WELCOME = 245;

        /**
         * 消息:用户进入聊吧提示xxx来了
         */
        public static final int MSG_ON_LINE_IS_COMEING = 246;
        /**
         * 消息：世界消息socket返回回来
         */
        public static final int MSG_SEND_WORLD_MESSAGE_SUCCESS_RESULT = 247;
        /**
         * 更新麦上用户是否在说话状态
         */
        public static final int MSG_MIC_USER_SPEAKING_STATE_UPDATE = 248;

        /**
         * 更新中奖数目图标
         */
        public static final int MSG_LOTTERY_NUM_UPDATE = 249;

    }

    /**
     * 请求码类
     */
    public static class ChatRequestCode {

        /**
         * 发送视频：录制视频的requestCode
         */
        public static final int CHAT_VIDEO_RECORDER_REQUEST_CODE = 2;

        public static final int CHAT_REPEATER_REQUEST_CODE = 3;

        public static final int CHAT_TASK_REQUEST_CODE = 5;// 跳转到任务页面的请求码

    }

    /**
     * 监听输入框文字的改变
     */
    protected TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            /*
             * s: 改变后的字符串 start: 新字符串开始的位置 before: 被替换的字符串的长度 count: 新字符串的长度
             */
            if (editContent.getLineCount() >= 2) { // 输入框的字超过两行时，显示字数提示(最多输入140个字符)
                // fontNum.setVisibility( View.VISIBLE );
                // fontNum.setText( Html.fromHtml( "<font color='red'>"
                // + FaceManager.calculateLength( s.toString( ) ) + "</font>" +
                // "/140" ) );
                mHandler.sendEmptyMessage(HandleMsgCode.MSG_SHOW_LIST_BOTTOM);
            } else {
                fontNum.setVisibility(View.GONE);
            }


            int sel = editContent.getSelectionEnd();
            strContent = editContent.getText().toString();
            FaceManager.getInstance(editContent.getContext()).parseIconForEditText(editContent.getContext(), editContent, start, before, count);
            int last = editContent.getText().toString().length();
            sel = last >= sel ? sel : last;
            editContent.setSelection(sel);

            if (barrageState && isFrom == 0) {
                int mTextMaxlenght = 0;
                Editable editable = editContent.getText();
                String str = editable.toString().trim();
                //得到最初字段的长度大小，用于光标位置的判断
                int selEndIndex = Selection.getSelectionEnd(editable);

                // 取出每个字符进行判断，如果是字母数字和标点符号则为一个字符加1，

                //如果是汉字则为两个字符

                for (int i = 0; i < str.length(); i++) {

                    char charAt = str.charAt(i);

                    //32-122包含了空格，大小写字母，数字和一些常用的符号，

                    //如果在这个范围内则算一个字符，

                    //如果不在这个范围比如是汉字的话就是两个字符

                    if (charAt >= 32 && charAt <= 122) {

                        mTextMaxlenght++;

                    } else {

                        mTextMaxlenght += 2;

                    }

                    // 当最大字符大于40时，进行字段的截取，并进行提示字段的大小

                    if (mTextMaxlenght > 280) {

                        // 截取最大的字段

                        String newStr = str.substring(0, i);

                        editContent.setText(newStr);

                        // 得到新字段的长度值

                        editable = editContent.getText();

                        int newLen = editable.length();

                        if (selEndIndex > newLen) {

                            selEndIndex = editable.length();

                        }

                        // 设置新光标所在的位置

                        Selection.setSelection(editable, selEndIndex);

                        Toast.makeText(SuperChat.this, getResources().getString(R.string.text_too_long), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

            }

            updataSendBtn();
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            /*
             * s: 改变前的字符串 start: 新字符串开始的位置 count: 被替换的字符串的长度 after: 新字符串的长度
             */
        }

        @Override
        public void afterTextChanged(Editable s) {

            AtSpan[] spanStrs = s.getSpans(0, s.length(), AtSpan.class);

            int count = spanStrs.length;
            for (int i = 0; i < count; i++) {

                AtSpan str = spanStrs[i];
                int start = s.getSpanStart(str);
                int end = s.getSpanEnd(str);

                String subStr = s.toString().substring(start, end);
                if (!subStr.equals(str.getKeyword().trim())) {
                    s.removeSpan(str);
                    s.delete(start, end);
                }
            }

            String str = s.toString();
            if (str.contains("\n")) {
                str = str.replaceAll("\n", "");
                editContent.setText(str);
            }
        }
    };

    /**
     * @author KevinSu
     * @某人的一个Span,记录该用户的用户名和用户id
     */
    public static class AtSpan extends SpannableString {

        private String mKeyword;// 记录@某人
        private long mUserid;// 记录该用户的用户id

        public AtSpan(String keyword, long userid) {
            super(keyword);
            setKeyword(keyword);
            setUserid(userid);
        }

        public String getKeyword() {
            return mKeyword;
        }

        public void setKeyword(String mKeyword) {
            this.mKeyword = mKeyword;
        }

        public long getUserid() {
            return mUserid;
        }

        public void setUserid(long mUserid) {
            this.mUserid = mUserid;
        }
    }

    /**
     * 发送失败后，是否重新发送的对话框
     *
     * @return
     */
    private AlertDialog sendFailDialog(final View view) {
        final CharSequence[] items = {
                getResString(R.string.chat_room_send_fail_dialog_item_send_again)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.chat_room_send_fail_dialog_title);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                ChatRecord record = (ChatRecord) view.getTag();

                // 重新发送
                if (sendMsgAgain(record)) {

                    record.setStatus(ChatRecordStatus.SENDING);
                }
                // 刷新列表
                updateList();


            }

        });
        AlertDialog alert = builder.create();

        return alert;
    }

    private static class MyFaceIconClickListener extends FaceIconClickListener {
        private WeakReference<SuperChat> mSuperChat = null;

        public MyFaceIconClickListener(SuperChat superChat, EditText editContent) {
            super(superChat, editContent);
            mSuperChat = new WeakReference<SuperChat>(superChat);
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            super.onItemClick(parent, view, position, id);
            SuperChat superChat = mSuperChat.get();
            if (superChat == null) return;

            FaceIcon icon = (FaceIcon) view.getTag();
            Pattern p1 = Pattern.compile("\\{\\#(.*)\\#\\}");
            Matcher m1 = p1.matcher(icon.key);
            if (m1.find()) {
                try {
                    JSONObject msg = new JSONObject();
                    String[] body = m1.group(1).split("_");

                    int pkgid = Integer.valueOf(body[0]);
                    int mapid = Integer.valueOf(body[1]);

                    msg.put("pkgid", pkgid);
                    msg.put("mapid", mapid);

                    if (FaceManager.getInstance(superChat).checkPackIsVip(pkgid)) {
                        if (!Common.getInstance().loginUser.isVip() && !Common.getInstance().loginUser.isSVip()) {
                            return;
                        }
                    }
                    superChat.sendMsg(superChat.getCurrentFlag(), ChatMessageType.FACE, "", "", msg.toString());

                    // 将表情保存到最近使用的表情列表
                    FaceManager.FaceLogoIcon face = new FaceManager.FaceLogoIcon();
                    face.pkgId = icon.pkgId;
                    face.key = icon.key;
                    face.iconPath = icon.iconPath;
                    face.description = icon.description;
                    FaceManager.addLastUserFace(superChat, icon.key, face);
                    FaceManager.resetOtherFace();
                } catch (Exception e) {
                }
            }
        }
    }


    private static class BuyFaceBroadcastReceiver extends BroadcastReceiver {
        private WeakReference<SuperChat> mActivity;

        public BuyFaceBroadcastReceiver(SuperChat superChat) {
            mActivity = new WeakReference<SuperChat>(superChat);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SuperChat superChat = mActivity.get();
            if (superChat != null) {
                String action = intent.getAction();
                if (FaceManager.FACE_INIT_ACTION.equals(action)) {
                    if (superChat.chatFace != null && !superChat.chatFace.isShown()) {
                        superChat.chatFace = null;
                    }
                }
            }
        }
    }

    private static class ScreenOffReceiver extends BroadcastReceiver {
        private WeakReference<SuperChat> mActivity;

        public ScreenOffReceiver(SuperChat superChat) {
            mActivity = new WeakReference<SuperChat>(superChat);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            SuperChat superChat = mActivity.get();
            if (superChat != null && superChat.audioTouchListener != null && superChat.audioTouchListener.mHandler != null) {
                superChat.audioTouchListener.stopRecord();
            }
        }
    }

    private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
        @Override
        public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) {

            if (resultList != null) {
                if (reqeustCode == REQUEST_CODE_GALLERYS) {
                    final ArrayList<String> list = new ArrayList<>();

                    for (PhotoInfo photoInfo : resultList) {
                        list.add(photoInfo.getPhotoPath());
                    }
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            for (String path : list) {
                                try {

                                    if (path != null) {
                                        //								String outpath = compressImage( path );
                                        String outpath = CommonFunction.compressImage(path);
                                        Message msg = new Message();
                                        msg.what = HandleMsgCode.PIC_COMPRESS_SEND;
                                        msg.obj = outpath;
                                        mHandler.sendMessage(msg);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }


            }
        }

        @Override
        public void onHanlderFailure(int requestCode, String errorMsg) {
            Toast.makeText(SuperChat.this, errorMsg, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK == resultCode) {

            if (ChatRequestCode.CHAT_VIDEO_RECORDER_REQUEST_CODE == requestCode && data != null) {
                String filePath = data.getStringExtra(ChatVideoRecorder.URI_PATH);
                sendMsg(getCurrentFlag(), ChatMessageType.VIDEO, filePath, "", "");
            } else if (ChatRequestCode.CHAT_REPEATER_REQUEST_CODE == requestCode) {

                if (CommonFunction.forbidSay(mActivity))
                    return;

                final User user = (User) data.getSerializableExtra("user");
                final Group group = (Group) data.getSerializableExtra("group");

                if (user != null) {
                    repeaterToUser(user);
                } else if (group != null) {
                    repeaterToGroup(group);
                }

                moreHandleRecordList.clear();
                chatInputBarLayout.setVisibility(View.VISIBLE);
                llMoreHandle.setVisibility(View.GONE);

                adapter.showCheckBox(false);
                adapter.clearSeletedList();

                for (ChatRecord record : mRecordList) {
                    record.setSelect(false);
                }

                CommonFunction.toastMsg(mContext, R.string.chat_records_sended_notice);
            }
        } else if (Activity.RESULT_CANCELED == resultCode && ChatRequestCode.CHAT_REPEATER_REQUEST_CODE == requestCode) {

            updateList();
        }
    }

    private void repeaterToUser(User user) {
        PrivateChatInfo targetInfo = new PrivateChatInfo();
        targetInfo.targetUserId = user.getUid();
        targetInfo.from = ChatFromType.UNKONW;

        int isChat = ChatPersonalModel.getInstance()
                .getAccostRelation(mContext, UserId, user.getUid());
        if (isChat <= 0) {
            targetInfo.mtype = 1;
        } else {
            targetInfo.mtype = 0;
        }

        long flag = getCurrentFlag();
        for (ChatRecord record : moreHandleRecordList) {
            repeaterUserSendMsg(flag++, targetInfo, record, user);
        }
    }

    private void repeaterToGroup(Group group) {
        GroupChatInfo targetInfo = new GroupChatInfo();
        targetInfo.setGroupIcon(group.icon);
        targetInfo.setGroupId(Long.valueOf(group.id));
        targetInfo.setGroupName(group.name);
        targetInfo.setReply("");

        long flag = getCurrentFlag();
        for (ChatRecord record : moreHandleRecordList) {
            repeaterGroupSendMsg(flag++, targetInfo, record);
        }
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && ((v instanceof EditText) || (v instanceof LinearLayout))) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right =
                    left + v.getWidth();
            int centerPxHorn = CommonFunction.getScreenPixWidth(mContext) / 2;
            if (event.getX() > left && event.getX() < right && event.getY() > top &&
                    event.getY() < bottom) {
                return false;
            } else {
                int x = (int) event.getX();
                int y = (int) event.getY();
                int dp2px = DensityUtils.dp2px(getApplicationContext(), 60);
                if ((event.getY() + DensityUtils.dp2px(getApplicationContext(), 60) < top)) {
                    return true;
                } else if ((event.getX() < centerPxHorn) && event.getY() < top) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isKeyboardShow() {
        return keyboardState == KEYBOARD_SHOW;
    }

    // 进入冷却时间
    private void enterColdTime() {
        bIsEnterLimit = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (currentSendCount >= SEND_COUNT_LIMIT) {
                    currentSendCount = 0;
                }
                bIsEnterLimit = false;
            }
        }, SEND_TIME_LIMIT);
    }

    // 处理图片压缩问题
    protected String compressImage(String sourcePath) {
        File uploadFile = new File(sourcePath);
        String tmpFilePath = sourcePath;

        if (uploadFile.exists()) {
            long len = uploadFile.length();
            if (len > 1024000) // 图片大于 1000K时候进行压缩
            {
                tmpFilePath = PathUtil.getPictureDir() + CryptoUtil.md5(sourcePath);
                File tmpFile = new File(tmpFilePath);
                try {
                    tmpFile.createNewFile();
                    Bitmap bitmap = BitmapFactory.decodeFile(uploadFile.getAbsolutePath());

                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    bitmap.compress(CompressFormat.JPEG, 36, bos);
                    bos.flush();
                    fos.close();
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        } else {
            return tmpFilePath;
        }

        return tmpFilePath;
    }

    /**
     * 获取当前聊天的User对象
     *
     * @return
     */
    protected abstract User getUser();

    /**
     * 找出当前发送的聊天消息
     *
     * @param list 显示的聊天记录列表或缓存列表
     * @param flag 标识
     * @return 返回消息所在的位置；若为-1，则表示没有。
     */
    protected int findCurSendMsg(List<ChatRecord> list, long flag) {
        int size = list.size();

        for (int i = size - 1; i >= 0; i--) {
            ChatRecord record = list.get(i);

            if (record.getDatetime() == flag) {
                return i;
            }
        }

        return -1;
    }

    /**
     * 加载地理信息：经纬度、地址
     */
    public void loadGeoData(final Handler handler, Context context) {
        new LocationUtil(context).startListener(new MLocationListener() {
            @Override
            public void updateLocation(int type, int lat, int lng, String address,
                                       String simpleAddress) {
                if (0 == type) {
                    handler.sendEmptyMessage(HandleMsgCode.MSG_GET_ADDRESS_INFO_FAIL);
                    return;
                }
                Message msg = handler.obtainMessage(HandleMsgCode.MSG_GET_ADDRESS_INFO_SUCC);
                msg.arg1 = lat;
                msg.arg2 = lng;
                msg.obj = CommonFunction.showAddress(address);
                handler.sendMessage(msg);
            }
        }, 1);
    }

    public void checkFaceDetail(ChatRecord record) {
        RecordFaceBean bean = GsonUtil.getInstance()
                .getServerBean(record.getContent(), RecordFaceBean.class);
        if (bean != null) {
            FaceDetailActivityNew.launch(mActivity, Integer.parseInt(bean.pkgid));
        }
    }

    public void handleMoreOperation(ChatRecord record) {
        if (llMoreHandle.getVisibility() == View.GONE) {
            chatInputBarLayout.setVisibility(View.GONE);
            llMoreHandle.setVisibility(View.VISIBLE);
            adapter.showCheckBox(true);
        }
    }

    public void addMoreHandleList(ChatRecord record) {
        moreHandleRecordList.add(record);
    }

    /**
     * 判断是否需要和显示未看数量
     */
    public void checkForUnSee() {
        if (!isNearBottom()) {
            unseeCount++;
            mHandler.sendEmptyMessage(HandleMsgCode.SHOW_AND_CLEAR_UNSEE);
        }
    }

    protected OnScrollListener onScrollListener = new OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_FLING) {//滑动时不加载图片
                Glide.with(getApplicationContext()).pauseRequests();
            } else {//滑动停止时加载图片
                Glide.with(getApplicationContext()).resumeRequests();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                             int totalItemCount) {
            int restCount = totalItemCount - (firstVisibleItem + visibleItemCount);
            if (unseeCount > 0 && restCount <= unseeCount) {
                unseeCount = 0;
                mHandler.sendEmptyMessage(HandleMsgCode.SHOW_AND_CLEAR_UNSEE);
            }
        }
    };

    /**
     * 组合聊天记录，用于发送以吧主的身份发送一条欢迎语
     *
     * @param flag
     * @param type       消息类型
     * @param attachment 附件
     * @param msg        需要发送的文本消息
     * @return
     */
    protected ChatRecord composeRecordForSend(long flag, GroupUser ownerUser, int type, String attachment, String field1, String msg) {

        ChatRecord record = new ChatRecord();
        record.setId(flag); // 消息id
        record.setFuid(ownerUser.getUserid());
        record.setfNickName(ownerUser.getNickname());
        record.setfIconUrl(ownerUser.getIcon());
        record.setfSVip(ownerUser.getSvip());
        record.setfVipLevel(ownerUser.getViplevel());
        record.setFLevel(ownerUser.getLevel());
        record.setDatetime(flag);
        record.setType(Integer.toString(type));
        record.setStatus(ChatRecordStatus.READ); // 默认，已读
        record.setAttachment(attachment);
        record.setField1(field1);
        record.setContent(msg);
        record.setUpload(false);
//        record.setFuid(getTargetID());
        record.setSendType(MessageBelongType.RECEIVE);
        record.setDistance(ChatPersonalModel.UNKNOWN_DISTANCE);
        record.setFlag(flag);
        record.setGroupRole(0);//主要是为了标识以吧主的身份发送这条消息

        if (String.valueOf(ChatMessageType.IMAGE).equals(record.getType())) {

            if (!attachment.contains(PathUtil.getHTTPPrefix())) {
                File f = new File(attachment);
                record.setUpload(true);
                record.setFileLen((int) f.length());
                record.setUploadLen(0);
            } else {
                record.setUpload(false);
            }
        }

        return record;
    }

    /*处理未读消息
     * */
    protected void handleUnseeNum() {
        if (unseeCount > 0) {
            unseeNum.setVisibility(View.VISIBLE);
            unseeNum.setText("" + unseeCount);
        } else {
            unseeNum.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * UI Begin
     **/
    protected String strContent = "";
    protected ListView chatRecordListView;// 聊天记录ListView
    protected EditTextViewUtil editContent;// 输入框
    protected TextView fontNum;// 输入的字数提示
    protected TextView unseeNum;// 未看新消息的字数提示
    protected Button mSoundTextSwitch;// 声音和文字的切换按钮
    //    protected Button mBtnSend;// 发送按钮
    protected TextView mBtnSend;// 发送按钮

    //    protected ImageButton faceMenuBtn;// 表情按钮
//    protected ImageButton toolMenuBtn;// 菜单按钮
    //yuchao
    protected Button giftMenuBtn;//礼物按钮
    protected Button faceMenuBtn;//表情按钮
    protected Button toolMenuBtn;//菜单按钮
    protected LinearLayout chatVoice;// 按下说话
    protected RelativeLayout chatAudioLayout;// 录音弹出窗
    protected FrameLayout faceMenuContainer;// 表情菜单视图的父容器
    protected ChatFace chatFace = null;// 表情按钮对象
    protected ChatMenuTool chatTool = null;// 菜单按钮对象
    protected TextView btnQuickSend1;
    protected TextView btnQuickSend2;
    protected LinearLayout lyQuickSend;


    protected LinearLayout chatInputBarLayout;
    //    protected RelativeLayout chatInputBarLayout;
    protected LinearLayout llMoreHandle;
    protected ImageView ivRepeater;
    protected ImageView ivDelete;

    protected FlagImageViewBarrage flagImageViewBarrage;//聊吧世界消息弹幕开关
    /**
     * UI End
     **/

    protected ScreenOffReceiver mScreenOffReceiver;
    protected BuyFaceBroadcastReceiver mBuyFaceBroadcastReceiver;
    protected ChatSendAudioTouchListener audioTouchListener;

    protected ArrayList<ChatRecord> mRecordList = new ArrayList<ChatRecord>();// 聊天记录
    protected ChatRecordListBaseAdapter adapter;// 聊天记录适配器

    protected Handler mHandler;// 消息处理器
    protected Handler dataHandler;// 数据,网络处理器,用于处理多线程导致的问题
    private MediaPlayer mediaPlayer;// 媒体播放类

    /**
     * 键盘状态 Begin
     **/
    protected static final int KEYBOARD_HIDE = 1;// 软件键盘隐藏
    protected static final int KEYBOARD_SHOW = 2;// 软件键盘显示
    protected int keyboardState = KEYBOARD_HIDE;// 软件键盘状态：显示、隐藏
    protected boolean hideKeybordWhenTouch = true;
    /** 键盘状态 End **/

    /**
     * 消息发送时间次数限制 Begin
     **/
    private Timer limiTimer;
    private final int SEND_COUNT_LIMIT = 12;// 单位时间内的发送限制
    private final int SEND_UNIT_TIME = 5000;// 单位限制时间（单位：毫秒）
    private final int SEND_TIME_LIMIT = 5000;// 限制时长（单位：毫秒）
    private int currentSendCount = 0;// 当前已发送次数
    private long locationLimitTime = 0;// 发送地理位置的限制时间
    private int unseeCount = 0;//当前未读新消息数

    public final int TEXT_TAG = 0;// 当前显示文字输入
    public final int SOUND_TAG = 1;// 当前显示语音输入

    // 发送次数的限制Toast
    private Toast sendLimitToast = null;
    /** 消息发送时间次数限制 End **/

    /**
     * 登陆者的id
     **/
    protected final long UserId = Common.getInstance().loginUser.getUid();
    public static final String TIME_LINE_TYPE = "timeline";

    /**
     * 超时时限
     */
    protected long TIME_OUT_LIMIT = 30 * 1000;

    protected MessagesSendManager backManager;

    // 是否进入限制状态
    private boolean bIsEnterLimit = false;

    protected boolean setThisCallbackAction = true;//是否设置callbackaction

    protected ChatFaceShowCallback faceShowCallback = null;

    private ArrayList<ChatRecord> moreHandleRecordList = new ArrayList<ChatRecord>();


    protected final int MAX_SELECT_COUNT = 15;// 最大的选择记录数

    // 离最底下多少项后才不算底部附近
    // （为3时即离开最底部一个聊天记录时不算底部附近，原因是最底部有个footer空白项，getLastVisiblePosition时从0开始算起，所以要加2）
    protected final int NEAR_BOTTOM_OFFSET = 3;

    private String path = "";// 保存拍照返回的路径

    private int isFrom = 0;

    private User chatPersonalUser;

    public Button btNewMessage;
    public Button btWorldMessage;
    public View viewTask; //任务
    public RelativeLayout btTask; //任务
    public Button btShare; //分享
    public RelativeLayout rlNewMessage;
    public DragPointView dragPointView;
    public LinearLayout LyMore;//输入区域更多
    public Button btnLuckpan; //抽奖盘
    public DragNullPointView luckPanCountView; //免费抽奖次数
    public RelativeLayout luckPanContainer;

}
