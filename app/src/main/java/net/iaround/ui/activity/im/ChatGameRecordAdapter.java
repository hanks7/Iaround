package net.iaround.ui.activity.im;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import net.iaround.conf.Common;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.RecordAccostGameBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.activity.im.accost.AccostAudioView;
import net.iaround.ui.activity.im.accost.AccostRecordFactory;
import net.iaround.ui.activity.im.accost.AccostRecordView;
import net.iaround.ui.chat.SuperChat;
import net.iaround.ui.chat.view.ChatRecordViewFactory;
import net.iaround.ui.datamodel.User;

import java.util.ArrayList;

/**
 * @author
 * @ClassName ChatGameRecordAdapter
 * @Description: 用于收到搭讪界面
 */

public class ChatGameRecordAdapter extends BaseAdapter {
    private ArrayList<?> arrayList;
    private Context mContext;
    private User fUser = null;
    private ListView mChatRecordListView;
    private AccostRecordView[] accostView;
    private Handler mHandler;

    public ChatGameRecordAdapter(Context context,
                                 User friend,
                                 ArrayList<?> arrayList,
                                 ListView chatRecordListView,
                                 Handler handler) {
        // TODO Auto-generated constructor stub
        this.arrayList = arrayList;
        mContext = context;
        fUser = friend;

        mChatRecordListView = chatRecordListView;
        mHandler = handler;
        accostView = new AccostRecordView[arrayList.size()];
    }


    public void changeData(ArrayList<?> arrayList) {
        notifyDataSetInvalidated();

        this.arrayList = arrayList;
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        ChatRecord record = ((ChatRecord) getItem(position));
        int type = -1;
        String recordTypeStr = record.getType();

        if (SuperChat.TIME_LINE_TYPE.equals(recordTypeStr)) {
            type = ChatRecordViewFactory.TIME_LINE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_QUE) {//搭讪问题
            type = ChatRecordViewFactory.ACCOST_GAME_QUE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_NOTICE) {//搭讪游戏提示
            type = ChatRecordViewFactory.ACCOST_NOTICE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.SHARE) {//分享类型
            type = ChatRecordViewFactory.SHARE;
        } else if (Integer.valueOf(recordTypeStr) == ChatRecordViewFactory.ACCOST_GAME_ANS) {//搭讪回答
            String content = record.getContent();
            RecordAccostGameBean bean = GsonUtil.getInstance().getServerBean(content, RecordAccostGameBean.class);
            if (bean.bIsTextAnswer()) {// 搭讪回答-文本_自己的
                type = ChatRecordViewFactory.ACCOST_GAME_ANS_TEXT;
            } else {// 搭讪回答-图片_自己的
                type = ChatRecordViewFactory.ACCOST_GAME_ANS_IMAGE;
            }

            if (!isMyRecord(position)) {// 判断对方还是自己的偏移
                type += ChatRecordViewFactory.TYPE_OFFSET;
            }
        } else {
            type = Integer.valueOf(recordTypeStr);

            if (!isMyRecord(position)) {
                type += ChatRecordViewFactory.TYPE_OFFSET;
            }
        }

        return type;

    }

    @Override
    public int getViewTypeCount() {
        return ChatRecordViewFactory.TYPE_COUNT;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayList != null ? arrayList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrayList != null ? arrayList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    private boolean isMyRecord(int position) {
        ChatRecord record = ((ChatRecord) getItem(position));

        return record.getUid() != fUser.getUid();

    }

    public void stopAllAudioPlay() {
        for (int i = 0, imax = accostView.length; i < imax; i++) {
            AccostRecordView view = accostView[i];
            if (view != null && view instanceof AccostAudioView) {
                ((AccostAudioView) view).accostStopPlayAudio();
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        // 获取聊天记录
        ChatRecord record = (ChatRecord) getItem(position);
        AccostRecordView view = null;
        int ViewType = getItemViewType(position);

        // 获取聊天记录的View
        if (convertView == null) {
            view = AccostRecordFactory.createChatRecordView(mContext, ViewType);
        } else {
            view = (AccostRecordView) convertView;
        }


//		view.reset( );
        if (record.getUid() == fUser.getUid()) {
            // 好友的聊天记录
            // 使用好友最新头像显示
            if (!CommonFunction.isEmptyOrNullStr(fUser.getIcon())
                    && record.getIcon() != fUser.getIcon()) {
                record.setIcon(fUser.getIcon());
            }
        } else {
            // 用户本人的聊天记录
            // 使用本人最新的头像显示
            String micon = Common.getInstance().loginUser.getIcon();
            if (!CommonFunction.isEmptyOrNullStr(micon)
                    && record.getIcon() != micon) {
                record.setIcon(micon);
            }
        }

        view.build(record);
        accostView[position] = view;
        return view;
    }

}
