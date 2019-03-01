package net.iaround.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.connector.protocol.UserHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.GroupContactWorker;
import net.iaround.database.NearContactWorker;
import net.iaround.database.PersonalMessageWorker;
import net.iaround.entity.type.GroupMsgReceiveType;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.type.MessageListType;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.adapter.chat.MessagesPrivateAdapter;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.datamodel.GroupAffairModel;
import net.iaround.ui.datamodel.GroupModel;
import net.iaround.ui.datamodel.MeetGameModel;
import net.iaround.ui.datamodel.MessageBean;
import net.iaround.ui.datamodel.MessageModel;
import net.iaround.ui.datamodel.NewFansModel;
import net.iaround.ui.datamodel.StartModel;
import net.iaround.ui.datamodel.VideoChatModel;
import net.iaround.ui.friend.bean.NewFansBean;
import net.iaround.ui.friend.bean.VideoChatBean;
import net.iaround.ui.group.bean.GroupContact;
import net.iaround.ui.group.bean.GroupNoticeBean;
import net.iaround.ui.map.ActivityLocationMap;
import net.iaround.ui.seach.news.SearchMainActivity;
import net.iaround.ui.view.dialog.CustomContextDialog;
import net.iaround.tools.DialogUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantLock;


/**
 * IM消息界面
 */
public class MessageFragmentIm extends Fragment implements HttpCallBack, MainFragmentActivity.PagerSelectMsg {
    private Context mContext;
    private Activity mActivity;
    private ReentrantLock lock = new ReentrantLock();
    private boolean isLoading = false;
    private ArrayList<MessageBean<?>> mDataList = new ArrayList<MessageBean<?>>();
    private ArrayList<Integer> unReadItemPosition = new ArrayList<Integer>();
    private int unReadCount = 0;
    private int networkStatus = 1;
    public static MessageFragmentIm instant;
    //新标题
    private TextView mTitleText;
    private ImageView mIvRight_2;

    //标题右上角弹窗
    private CustomContextDialog customContextDialog;

    private View mMessagesView;
    private PullToRefreshListView mListView;
    private View headerView;
    private MessagesPrivateAdapter adapter;
    private TextView noConnectView;

    public final static int LOGIN_NET_WORK_STATUS_FAIL = 3;
    public final static int LOGIN_NET_WORK_CONNECTING = 2;
    public final static int LOGIN_NET_WORK_STATUS_SUCCESS = 1;

    private final int CONTACT_LIST = 1001;
    public final static int REFRESH_DATA = 1002;
    public final static int REFRESH_TITLE = 1003;
    public final static int REFRESH_TAB_COUNT = 1004;

    public Handler mHandler = new Handler(Looper.getMainLooper()) {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CONTACT_LIST:
                    stopPulling();

                    if (msg.obj != null) {
                        mDataList.clear();
                        mDataList.addAll((ArrayList<MessageBean<?>>) msg.obj);
                    }

                    if (mDataList != null && mDataList.size() > 0) {
                        if (mListView != null) mListView.onRefreshComplete();
                        changeToNetworkMode(StartModel.getInstance().getLoginConnnetStatus());
                        setGroupHelperTop();
                        getUnreadPosition();

                    } else {
                        changeToNetworkMode(StartModel.getInstance().getLoginConnnetStatus());
                    }
                    adapter.notifyDataSetChanged();
                    break;
                case REFRESH_DATA:

                    loadData();
                    break;
                case REFRESH_TITLE:
                    changeToNetworkMode(msg.arg1);
                    break;
                case REFRESH_TAB_COUNT:
                    if (MainFragmentActivity.sInstance != null)
                        MainFragmentActivity.sInstance.freshAllCount();
                    break;
            }
        }
    };

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CommonFunction.log("MessageFragmentIm", "onViewCreated() into, savedInstanceState=" + savedInstanceState);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMessagesView = inflater.inflate(R.layout.friend_near_contact, container, false);

        instant = this;
        mContext = getActivity();
        mActivity = getActivity();
        initView();
        initVariable();
        setListener();
        loadData();

        // 获取登录状态，显示网络是否连接等
        changeToNetworkMode(StartModel.getInstance().getLoginConnnetStatus());
        // 将系统用户id保存起来
        ChatPersonalModel.getInstance().putAccostRelation(mContext, 1001, 1);// 游戏中心消息
        ChatPersonalModel.getInstance().putAccostRelation(mContext, 1003, 1);// 贴吧消息

        return mMessagesView;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        refreshData();
//    }

    public void refreshData() {
        if (mListView != null) {
            loadData();
        }
    }

    private ArrayList<MessageBean<?>> initData() throws Exception {
        String uid = String.valueOf(Common.getInstance().loginUser.getUid());

        // 私聊、发出搭讪、收到搭讪、遇见广场，查找tb_near_contact
        Common.getInstance().setNoReadMsgCount(ChatPersonalModel.getInstance().countNoRead(mContext, uid));
        ArrayList<MessageBean<?>> beans = new ArrayList<MessageBean<?>>();
        ArrayList<MessageBean<?>> vipBeans = new ArrayList<MessageBean<?>>();
        ArrayList<MessageBean<?>> barsBeans = new ArrayList<MessageBean<?>>();
        ArrayList<NearContact> contacts = ChatPersonalModel.getInstance().getNearContact(mContext, uid, 0, 100);
        for (NearContact contact : contacts) {
            MessageBean<NearContact> bean = new MessageBean<NearContact>();
            if (contact.getSubGroup() == SubGroupType.NormalChat || contact.getSubGroup() == SubGroupType.SendAccost
                    || contact.getSubGroup() == SubGroupType.ReceiveAccost) {// 属于私聊类型
                bean.type = MessageListType.PRIVATE_CHAT;
                bean.messageData = contact;
                bean.age = contact.getUser().getAge();
                bean.iconUrl = contact.getUser().getIcon();
                bean.time = contact.getUser().getLastSayTime();
                bean.vip = contact.getUser().getViplevel();
                bean.svip = contact.getUser().getSVip();
                bean.lat = contact.getUser().getLat();
                bean.lng = contact.getUser().getLng();
                bean.title = contact.getUser().getNoteName(true);
                bean.sex = contact.getUser().getSexIndex();
                bean.distance = contact.getDistance();
                bean.isFriendMsg = contact.isFriendMsg();
                bean.content = contact.getContent();
                bean.messageType = contact.getType();
                bean.messageNum = contact.getNumber();
                bean.chatStatus = contact.getChatStatus();
                bean.userType = contact.getUser().getUserType();

            }

            /*去掉搭讪分组*/

//            else if (contact.getSubGroup() == SubGroupType.SendAccost) {// 属于发出搭讪类型
//                CommonFunction.log("MessageFragmentIm", "sendaccost fUid="+contact.getfUid()+ ", user=" + contact.getUser().getUid()+ ", type="+contact.getType() + ", subGgroup=" + contact.getSubGroup()+"content=" + contact.getContent());
//                if (Common.getInstance().getBlockStatus() > 0){
//                    if (Common.getInstance().loginUser.getSVip() == 0){
//                        bean.type = MessageListType.SEND_ACCOST;
//                        try {
//                            JSONObject json = new JSONObject(contact.getContent()); ///JSON转换没处理异常，导致显示不了消息
//                            int currencytype = json.getInt("currencytype"); //金币类型
//                            if (currencytype == 2) {
//                                bean.type = MessageListType.PRIVATE_CHAT;
//                            }
//                        }catch (Exception e){
//                            CommonFunction.log("MessageFragmentIm", "转换 NearContact 里的 content异常, content=" + contact.getContent());
//                            e.printStackTrace();
//                        }
//                    }else{
//                        bean.type = MessageListType.PRIVATE_CHAT;
//                    }
//                }else{
//                    bean.type = MessageListType.SEND_ACCOST;
//                }
//
//                bean.messageData = contact;
//                bean.messageNum = contact.getNumber();
//                bean.messageType = contact.getType();
//                bean.vip = contact.getUser().getViplevel();
//                bean.svip = contact.getUser().getSVip();
//                bean.lat = contact.getUser().getLat();
//                bean.lng = contact.getUser().getLng();
//                bean.time = contact.getUser().getLastSayTime();
//                bean.title = contact.getUser().getNickname();
//                bean.content = contact.getContent();
//                bean.userType = contact.getUser().getUserType();
//            } else if (contact.getSubGroup() == SubGroupType.ReceiveAccost) {// 属于收到搭讪类型
//
//                bean.type = MessageListType.RECEIVE_ACCOST;
//                bean.messageData = contact;
//                bean.messageNum = contact.getNumber();
//                bean.messageType = contact.getType();
//                bean.time = contact.getUser().getLastSayTime();
//                bean.title = contact.getUser().getNickname();
//                bean.lat = contact.getUser().getLat();
//                bean.lng = contact.getUser().getLng();
//                bean.distance = contact.getDistance();
//                bean.content = contact.getContent();
//                bean.userType = contact.getUser().getUserType();
//            }
            if (bean.svip > 0) {
                vipBeans.add(0, bean);
                Collections.sort(vipBeans, comparator);
            } else {
                beans.add(bean);
            }
        }

        // 圈消息，圈助手，查找tb_group_contact
        ArrayList<GroupContact> gContact = GroupModel.getInstance().getGroupContactList(mContext);
        if (gContact != null && gContact.size() > 0) {
            // 圈助手bean
            MessageBean<GroupContact> hBean = new MessageBean<GroupContact>();
            hBean.type = MessageListType.GROUP_HELPER;
            hBean.messageData = null;
            hBean.time = 0;
            hBean.messageNum = 0;
            for (int i = 0; i < gContact.size(); i++) {
                if (GroupAffairModel.getInstance().getGroupMsgStatus(gContact.get(i).groupID) == GroupMsgReceiveType.RECEIVE_AND_NOTICE) {
                    GroupContact contact = gContact.get(i);
                    MessageBean<GroupContact> bean = new MessageBean<GroupContact>();
                    bean.type = MessageListType.GROUP_CHAT;// 圈聊消息
                    bean.messageData = contact;
                    bean.iconUrl = contact.groupIcon;
                    bean.title = contact.groupName;
                    bean.content = contact.lastContent;
                    bean.time = contact.time;
                    bean.messageNum = contact.noRead;
                    beans.add(bean);
                } else if (GroupAffairModel.getInstance().getGroupMsgStatus(gContact.get(i).groupID) == GroupMsgReceiveType.RECEIVE_NOT_NOTICE) {
                    GroupContact contact = gContact.get(i);
                    long lastTime = SharedPreferenceUtil.getInstance(mContext).getLong(SharedPreferenceUtil.GROUP_HELPER_ENTER_TIME + uid, 0l);
                    if (contact.time > hBean.time) {
                        hBean.time = contact.time;
                        hBean.messageData = contact;
                        hBean.content = contact.lastContent;
                    }
                    if (contact.noRead > 0 && contact.time > lastTime) {
                        hBean.messageNum++;
                    }
                }
            }
            if (GroupAffairModel.getInstance().getGroupHelperOnOff() == 1 && hBean.time > 0) {// 开启圈助手且圈助手内有消息
                if (GroupAffairModel.getInstance().getGroupHeplerTopAtMsg(getActivity()) == 1) {
                    barsBeans.add(0, hBean);
                } else {
                    beans.add(hBean);
                }
            }
        }

        // 圈通知，查找tb_group_notice
        GroupNoticeBean gnContact = GroupAffairModel.getInstance().getLatestNotice(mContext);
        if (gnContact != null) {
            MessageBean<GroupNoticeBean> bean = new MessageBean<GroupNoticeBean>();
            bean.type = MessageListType.GROUP_NOTICE;// 圈通知
            bean.messageData = gnContact;
            bean.time = gnContact.datetime;
            bean.messageNum = GroupAffairModel.getInstance().getUnreadCount(mContext);
            beans.add(bean);
        }

        // 新增粉丝，查找tb_new_fans
        NewFansBean nfContact = NewFansModel.getInstance().getLatestMessage(mContext, Long.parseLong(uid));
        if (nfContact != null) {
            MessageBean<NewFansBean> bean = new MessageBean<NewFansBean>();
            bean.type = MessageListType.NEW_FANS;// 新增粉丝
            bean.messageData = nfContact;
            bean.time = nfContact.datetime;
            bean.messageNum = NewFansModel.getInstance().getUnreadCount(mContext, Long.parseLong(uid));
            beans.add(bean);
        }

        //视频会话
        VideoChatBean vcContact = VideoChatModel.getInstance().getLatestMessage(mContext, Long.parseLong(uid));
        if (vcContact != null) {
            MessageBean<VideoChatBean> bean = new MessageBean<VideoChatBean>();
            bean.type = MessageListType.VIDEO_CHAT;// 新增视频
            bean.messageData = vcContact;
            bean.time = vcContact.datetime;
            bean.messageNum = 0;
            beans.add(bean);
        }

        //动态通知
        DynamicNewNumberBean dynamicNewNumberBean = DynamicModel.getInstent().getNewNumBean();
        MessageBean<DynamicNewNumberBean> bean;
        if (dynamicNewNumberBean != null) {
            bean = new MessageBean<DynamicNewNumberBean>();
            bean.type = MessageListType.GROUP_DYNAMIC;
            bean.messageData = dynamicNewNumberBean;
            bean.time = dynamicNewNumberBean.time;
            bean.messageNum = dynamicNewNumberBean.getCommentNum() + dynamicNewNumberBean.getLikenum();
            beans.add(bean);
        }

        //语音聊天
        long time = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getLong(uid + "audio_send_date");

        if (time > 0) {
            MessageBean audioBean;

            audioBean = new MessageBean();
            audioBean.type = MessageListType.VOICE_CHAT;
            audioBean.time = time;
            beans.add(audioBean);
        }
        Collections.sort(beans);

        if (GroupAffairModel.getInstance().getGroupHelperOnOff() == 1 && GroupAffairModel.getInstance().getGroupHeplerTopAtMsg(getActivity()) == 1) {
            beans.addAll(0, barsBeans);
            beans.addAll(1, vipBeans);
        } else {
            beans.addAll(0, vipBeans);
        }

        return beans;
    }

    /**
     * 点击两次按刷新数据
     *
     * @param isTrue
     */
    public void isFastPressTwice(boolean isTrue) {
        if (isTrue) {
            if (mListView != null) {
                mListView.setRefreshing(true);
            }
        }
    }

    private void getUnreadPosition() {
        unReadItemPosition.clear();
        unReadCount = 0;
        for (int i = 0; i < mDataList.size(); i++) {
            if (mDataList.get(i).messageNum > 0) {
                unReadItemPosition.add(i);
            }
        }
    }

    private void setGroupHelperTop() {
        if (GroupAffairModel.getInstance().getGroupHelperOnOff() == 1 &&
                GroupAffairModel.getInstance().getGroupHeplerTopAtMsg(mContext) == 1)
            for (int i = 0; i < mDataList.size(); i++) {
                if (mDataList.get(i).type == MessageListType.GROUP_HELPER) {
                    MessageBean<?> bean = mDataList.get(i);
                    mDataList.remove(i);
                    mDataList.add(0, bean);
                    break;
                }
            }
    }

    private void jumpToUnread() {
        if (mListView == null || unReadItemPosition.size() < 1) {
            CommonFunction
                    .log("sherlock", "mListView : unReadItemPosition.size( ) == " + mListView + " : " +
                            unReadItemPosition.size());
            return;
        }
        CommonFunction.log("sherlock", "unReadCount == " + unReadCount);
        mListView.getRefreshableView().setSelection(unReadItemPosition.get(unReadCount) + 1);
        unReadCount = (unReadCount + 1) % unReadItemPosition.size();

    }

    private void initView() {
        mMessagesView.findViewById(R.id.iv_left).setVisibility(View.GONE);
        mTitleText = (TextView) mMessagesView.findViewById(R.id.tv_title);
        mIvRight_2 = (ImageView) mMessagesView.findViewById(R.id.iv_right);

        mIvRight_2.setVisibility(View.VISIBLE);
        mIvRight_2.setImageResource(R.drawable.icon_messages_operating);
        noConnectView = (TextView) mMessagesView.findViewById(R.id.network_fail);

        setTitleText(1);

        mListView = (PullToRefreshListView) mMessagesView.findViewById(R.id.near_contact_list);
        mListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        mListView.getRefreshableView().setFastScrollEnabled(false);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        headerView = LayoutInflater.from(BaseApplication.appContext).inflate(R.layout.bt_header_recommend_search, null);

        TextView searchEdit = (TextView) headerView.findViewById(R.id.edittext_keyword);
        searchEdit.setText(getString(R.string.search));

        mListView.getRefreshableView().addHeaderView(headerView);

        //初始化弹窗
        customContextDialog = new CustomContextDialog(getContext(), 7);
        customContextDialog.setListenner(customContextDialogItemClick);
    }

    private void initVariable() {
        adapter = new MessagesPrivateAdapter(getActivity(), mDataList, mHandler);

        mListView.setAdapter(adapter);
    }

    private void setListener() {

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mHandler.postAtTime(new Runnable() {
                    @Override
                    public void run() {

                        loadData();
                        stopPulling();
                    }
                }, 500);
            }
        });

        mTitleText.setOnClickListener(titleOnclick);
        mIvRight_2.setOnClickListener(mainOnclick);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchMainActivity.launch(getActivity(), ActivityLocationMap.REQUEST_CODE_GET_LATLNG);
            }
        });
    }

    public void stopPulling() {
        if (mListView == null) return;
        mListView.onRefreshComplete();
    }


    private synchronized void loadData() {

        if (isLoading) {
            return;
        }

        isLoading = true;
        new Thread(new Runnable() {

            @Override
            public void run() {
                lock.lock();
                try {
                    ArrayList<MessageBean<?>> recieveBean = initData();
                    Message msg = mHandler.obtainMessage();
                    msg.what = CONTACT_LIST;
                    msg.obj = recieveBean;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isLoading = false;
                    lock.unlock();
                }
            }
        }).start();
    }


    /**
     * 清空消息，全部标记已读对应的点击事件
     */
    private OnClickListener customContextDialogItemClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch ((int) v.getTag()) {
                case 0:
                    // 删除全部消息
                    delAllMsgDialog();
                    break;
                case 1:
                    // 标记全部为已读
                    markAllReadDialog();
                    break;
            }
            customContextDialog.dismiss();
        }
    };

    /**
     * 弹出底部对话框:清空消息，全部标记已读
     */
    private OnClickListener mainOnclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (customContextDialog.isShowing()) {
                customContextDialog.hide();
            } else
                customContextDialog.show();
        }
    };

    private OnClickListener titleOnclick = new OnClickListener() {// 标题跳至未读
        @Override
        public void onClick(View v) {
            jumpToUnread();
        }
    };


    /**
     * 无网络登录调用，1-正常网络，2-网络正常登录中，3-网络连接失败
     */
    public void changeToNetworkMode(int mode) {
        networkStatus = mode;
        CommonFunction.log("MessageFragmentIm", "MessageFragment.changeToNetworkMode mode == " + mode);
        setTitleText(mode);
        switch (mode) {
            case 1: {
                noConnectView.setVisibility(View.GONE);
            }
            break;
            case 2: {
                noConnectView.setVisibility(View.GONE);
            }
            break;
            case 3: {
                noConnectView.setVisibility(View.VISIBLE);
            }
            break;
            case 4: {
                noConnectView.setVisibility(View.GONE);
            }
            break;
        }
    }

    /**
     * 1-正常网络，2-网络正常登录中，3-网络连接失败
     */
    private void setTitleText(int mode) {
        String title = mContext.getResources().getString(R.string.notice);
        int unRead = 0;
        if (mDataList != null && mDataList.size() > 0) {
            for (int i = 0; i < mDataList.size(); i++) {
                switch (mDataList.get(i).type) {
                    case MessageListType.SECRETARY:
                    case MessageListType.PRIVATE_CHAT:
                    case MessageListType.RECEIVE_ACCOST:
                    case MessageListType.NEW_FANS:
                    case MessageListType.GROUP_NOTICE:
                    case MessageListType.CHATBAR_NOTICE:
                    case MessageListType.CHATBAR_INVITATION:
                    case MessageListType.GROUP_DYNAMIC:
                    case MessageListType.CHATBAR_SKILL:
                    case MessageListType.GROUP_CHAT:
                        unRead += mDataList.get(i).messageNum;
                        break;
                    case MessageListType.CHATBAR_UNREAD: {
                        if (mDataList.get(i).messageNum > 0)
                            unRead += mDataList.get(i).messageNum;
                    }
                    case MessageListType.GROUP_HELPER:
                    case MessageListType.SEND_ACCOST:
                        break;
                }
            }
        }


        switch (mode) {
            case 1: {

                if (unRead > 0 && unRead <= 99)
                    title += "(" + unRead + ")";
                else if (unRead > 0)
                    title += "(99+)";
            }
            break;
            case 2: {
                title += "(" + mContext.getResources().getString(R.string.receiveing) + ")";
            }
            break;
            case 3: {
                title += "(" + mContext.getResources().getString(R.string.no_connect) + ")";
            }
            break;
            case 4: {
                title += "(" + mContext.getResources().getString(R.string.connect_fail) + ")";
            }
            break;
        }

        mTitleText.setText(title);
    }

    private void markAllReadDialog() {
        DialogUtil
                .showTwoButtonDialog(mActivity, mContext.getResources().getString(R.string.prompt),
                        mContext.getResources().getString(R.string.my_near_contact_all_read_tip),
                        mContext.getResources().getString(R.string.cancel),
                        mContext.getResources().getString(R.string.ok), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                markAllRead();
                            }
                        });
    }

    private void delAllMsgDialog() {
        DialogUtil
                .showTowButtonDialog(mActivity, mContext.getResources().getString(R.string.prompt),
                        mContext.getResources().getString(R.string.my_near_contact_del_all_tip),
                        mContext.getResources().getString(R.string.cancel),
                        mContext.getResources().getString(R.string.ok), null, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                delAllMsg();
                            }
                        });
    }

    /**
     * 标记全部为已读
     */
    public void markAllRead() {
        sendMarkAllReadMsg();
        long muid = Common.getInstance().loginUser.getUid();

        // 私聊未读消息归零
        NearContactWorker db = DatabaseFactory.getNearContactWorker(mActivity);
        db.onClearAllNoRead(muid);
        PersonalMessageWorker pdb = DatabaseFactory.getChatMessageWorker(mContext);
        pdb.readMyAllMsg(muid);

        GroupContactWorker gdb = DatabaseFactory.getGroupContactWorker(mActivity);
        gdb.updateIgnore(muid);

        // 圈通知清零
        GroupAffairModel.getInstance().setAllRead(mContext);

        // 新增粉丝清零
        NewFansModel.getInstance().setAllRead(mContext, muid);

        // 邂逅消息归零
        MeetGameModel.getInstance().EraseNoReadNum(mActivity);
        Common.getInstance().setMeetGameCount(0);
        ((MainFragmentActivity) getActivity()).freshAllCount();
        refreshData();
        getMessageReadData(0, 0);
        //动态消息归零
        DynamicModel.getInstent().getNewNumBean().setNum(0);
        DynamicModel.getInstent().getNewNumBean().setLikenum(0);

    }

    /**
     * 删除全部消息
     */
    public void delAllMsg() {
        long uid = Common.getInstance().loginUser.getUid();
        MessageModel.getInstance().deleteAllMessage(mActivity, uid);
        GroupAffairModel.getInstance().deleteUserAllNotice(mContext);
        NewFansModel.getInstance().deleteAllMessages(mContext, uid);
        DynamicModel.getInstent().deleteAllRecord(mContext, "" + uid);
        Common.getInstance().setMeetGameCount(0);
        DynamicModel.getInstent().getNewNumBean().setNum(0);
        DynamicModel.getInstent().getNewNumBean().setLikenum(0);
        ((MainFragmentActivity) getActivity()).freshAllCount();

        refreshData();
        getMessageReadData(0, 0);

        VideoChatModel.getInstance().deleteAllMessages(getActivity(), uid);
    }

    /**
     * 发送消息：标记全部为已读
     */
    private void sendMarkAllReadMsg() {
        String uids = getContactUids();
        if (!TextUtils.isEmpty(uids)) {
            // 组合发送到网络的数据
            String body = "{\"readuserid\":[" + uids + "]}";
            SocketSessionProtocol.sessionMarkAllNearContactRead(mActivity, body);
        }
    }

    private void getMessageReadData(int type, long value) {
        UserHttpProtocol.messageListReadStatus(mActivity, type, value, this);
    }

    /**
     * 获取所有联系人的uid
     *
     * @return
     */
    private String getContactUids() {
        String uids = "";
        try {
            if (mDataList != null && !mDataList.isEmpty()) {
                int size = mDataList.size();
                for (int i = 0; i < size; i++) {
                    MessageBean<?> msgBean = mDataList.get(i);
                    if (msgBean.type == MessageListType.PRIVATE_CHAT) {
                        NearContact contact = (NearContact) msgBean.messageData;
                        if (contact.getNumber() == 0) { // 没有未读消息
                            continue;
                        }
                        long uid = contact.getUser().getUid();
                        if (!uids.equals("")) {
                            uids += ",";
                        }
                        uids += String.valueOf(uid);
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            uids = "";
        }

        return uids;
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {

    }

    @Override
    public void onGeneralError(int e, long flag) {

    }

    @Override
    public void onMsgSelected(int mode) {
        CommonFunction.log("MessageFragmentIm", "message fragment selected");
        if (mode == MainFragmentActivity.PAGE_REFRESH) {
            mHandler.sendEmptyMessage(REFRESH_DATA);
        } else if (mode == MainFragmentActivity.PAGE_SELECT) {
            mHandler.sendEmptyMessage(REFRESH_DATA);
        }

    }

    /**
     * 上次 onTouch 发生的时间
     */
    private long lastTouchTime = 0;

    @Subscribe
    public void onEvent(String event) {
        if (event.equals("refersh_message")) {
            if (mHandler == null) return;
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    refreshData();
                }
            });
        } else if (event.equals("main_message")) {
            long now = System.currentTimeMillis();
            if (now - lastTouchTime > 1000 * 1.5) {
                if (mHandler == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        refreshData();
                    }
                });
            }
            lastTouchTime = now;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonFunction.log("MessageFragmentIm", "onDestroy() into");
        EventBus.getDefault().unregister(this);
    }

    //消息时间排序比较
    private Comparator<MessageBean> comparator = new Comparator<MessageBean>() {
        @Override
        public int compare(MessageBean p1, MessageBean p2) {
            if (p1.time < p2.time) {
                return 1;  //正数
            } else if (p1.time > p2.time) {
                return -1;  //负数
            } else {
                return 0;  //相等为0
            }
        }
    };
}
