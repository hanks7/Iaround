package net.iaround.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.im.NearContact;
import net.iaround.model.im.type.MessageListType;
import net.iaround.model.im.type.SubGroupType;
import net.iaround.ui.adapter.chat.MessagesPrivateAdapter;
import net.iaround.ui.datamodel.ChatPersonalModel;
import net.iaround.ui.datamodel.MessageBean;

import java.util.ArrayList;

/**
 * Class: 消息列表
 * Author：gh
 * Date: 2017/8/22 11:17
 * Email：jt_gaohang@163.com
 */
public class MessageFragment extends Fragment implements View.OnClickListener{

    private final int CONTACT_LIST = 1001;
    private ListView messageLv;

    public Handler mHandler = new Handler(Looper.getMainLooper()) {

        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case CONTACT_LIST:
                    MessagesPrivateAdapter adapter = new MessagesPrivateAdapter(getActivity(), (ArrayList<MessageBean<?>>) msg.obj, mHandler);
                    adapter.setOnDragListence(new MessagesPrivateAdapter.OnDragListence() {
                        @Override
                        public void referListView() {
                            loadData();
                        }
                    });
                    messageLv.setAdapter(adapter);
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView(){
        FrameLayout backFl = (FrameLayout)getView().findViewById(R.id.fl_back);
        TextView titleTv = (TextView)getView().findViewById(R.id.tv_title);
        messageLv = (ListView)getView().findViewById(R.id.ly_message_list);

        titleTv.setText(getString(R.string.message_title));
        backFl.setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private synchronized void loadData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ArrayList<MessageBean<?>> recieveBean = initData();
                    Message msg = mHandler.obtainMessage();
                    msg.what = CONTACT_LIST;
                    msg.obj = recieveBean;
                    mHandler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private ArrayList<MessageBean<?>> initData() throws Exception {
        String uid = String.valueOf(Common.getInstance().loginUser.getUid());

        // 私聊
        Common.getInstance()
                .setNoReadMsgCount(ChatPersonalModel.getInstance().countNoRead(getActivity(), uid));
        ArrayList<MessageBean<?>> beans = new ArrayList<MessageBean<?>>();
        ArrayList<NearContact> contacts = ChatPersonalModel.getInstance()
                .getNearContact(getActivity(), uid, 0, 100);
        for (NearContact contact : contacts) {
            MessageBean<NearContact> bean = new MessageBean<NearContact>();
            if (contact.getSubGroup() == SubGroupType.NormalChat) {// 属于私聊类型

                if (contact.getNumber() > 0){
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
                    beans.add(bean);

                }
            }

        }
        return beans;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_back:
                    getActivity().finish();
                break;
        }
    }
}
