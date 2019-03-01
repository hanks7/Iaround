package net.iaround.ui.chat;

import android.content.Context;
import android.view.View;
import android.view.View.OnLongClickListener;

import net.iaround.R;
import net.iaround.entity.type.MessageBelongType;
import net.iaround.model.im.ChatRecord;
import net.iaround.ui.chat.ChatMultiHandleDialog.MultiHandleItem;
import net.iaround.ui.chat.view.ChatRecordView;
import net.iaround.ui.chat.view.ChatRecordViewFactory;

import java.util.ArrayList;

/**
 * @author KevinSu kevinsu917@126.com
 * @create 2015年5月5日 下午8:48:05
 * @Description: 聊天消息的长按事件
 */
public class ChatRecordLongClickListener implements OnLongClickListener {

    private SuperChat mContext;

    private ChatMultiHandleDialog dialog;
    private ChatRecordView mRecordView;

    private ChatRecord record;

    // 长按操作flag
    public static final long COPY_ACTION_TAG = 1;// 拷贝
    public static final long DELETE_ACTION_TAG = 2;// 删除
    public static final long REPEATER_ACTION_TAG = 3;// 转发
    public static final long MORE_ACTION_TAG = 4;// 更多
    public static final long SAVE_ACTION_TAG = 5;// 保存
    public static final long SPEAKER_PLAY_ACTION_TAG = 6;// 扬声器播放
    public static final long DETAIL_ACTION_TAG = 7;// 查看详情
    public static final long AT_ACTION_TAG = 8;// @某人

    // MultiHandleItem
    private MultiHandleItem CopyMultiHandleItem;// 拷贝
    private MultiHandleItem DeleteMultiHandleItem;// 删除
    private MultiHandleItem RepeaterMultiHandleItem;// 转发
    private MultiHandleItem MoreMultiHandleItem;// 更多
    private MultiHandleItem SaveMultiHandleItem;// 保存
    private MultiHandleItem SpeakerPlayMultiHandleItem;// 扬声器播放
    private MultiHandleItem DetailMultiHandleItem;// 查看详情
    private MultiHandleItem AtMultiHandleItem;// @某人

    public ChatRecordLongClickListener(Context context,
                                       ChatRecordView recordView) {
        try {
            mContext = (SuperChat) context;
        } catch (Exception e) {
            if (e instanceof ClassCastException) {
                throw new IllegalArgumentException(
                        "context is not instanceof SuperChat");
            }
        }

        mRecordView = recordView;
        initMultiHandleItem();
    }

    @Override
    public boolean onLongClick(View v) {
        record = (ChatRecord) v.getTag(R.id.im_preview_uri);
        int type = Integer.valueOf(record.getType());
        refreshDialog(type);
        dialog.show(mContext.getFragmentManager(), "ChatMultiHandleDialog");
        return true;
    }

    private void refreshDialog(int type) {
        ArrayList<MultiHandleItem> data = getDataList(type);

        if (dialog == null) {
            dialog = new ChatMultiHandleDialog(mRecordView, data, mContext);
        } else {
            dialog.refreshData(mRecordView, data);
        }
    }

    private void initMultiHandleItem() {

        CopyMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.dialog_chat_setting_copy), COPY_ACTION_TAG);
        DeleteMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.dialog_chat_setting_delete), DELETE_ACTION_TAG);
        RepeaterMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.dialog_chat_setting_forward), REPEATER_ACTION_TAG);//转发
//		MoreMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.more), MORE_ACTION_TAG);//更多
        SaveMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.edit_save), SAVE_ACTION_TAG);//保存
        SpeakerPlayMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.chat_record_play), SPEAKER_PLAY_ACTION_TAG);//扬声器播放
        DetailMultiHandleItem = new MultiHandleItem(mContext.getResString(R.string.chat_record_check_detail_title), DETAIL_ACTION_TAG);//查看详情
        AtMultiHandleItem = new MultiHandleItem("@TA", AT_ACTION_TAG);

    }

    private ArrayList<MultiHandleItem> getDataList(int recordType) {
        ArrayList<MultiHandleItem> dataList = new ArrayList<ChatMultiHandleDialog.MultiHandleItem>();

        if (recordType == ChatRecordViewFactory.TEXT) {
            dataList.add(CopyMultiHandleItem);
//			dataList.add(RepeaterMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
//			dataList.add(MoreMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.IMAGE) {
//			dataList.add(SaveMultiHandleItem);
//			dataList.add(RepeaterMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
//			dataList.add(MoreMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.GIFE_REMIND) {
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.LOCATION) {
//			dataList.add(RepeaterMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
//			dataList.add(MoreMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.VIDEO) {
//			dataList.add(RepeaterMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
//			dataList.add(MoreMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.SOUND) {
//			dataList.add(SpeakerPlayMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.FACE) {
            dataList.add(DetailMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.SHARE) {
//			dataList.add(RepeaterMultiHandleItem);
            dataList.add(DeleteMultiHandleItem);
//			dataList.add(MoreMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.CHCAT_VIDEO) {
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.FRIEND_CHCAT_VIDEO) {
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.CHCAT_AUDIO) {
            dataList.add(DeleteMultiHandleItem);
        } else if (recordType == ChatRecordViewFactory.FRIEND_CHCAT_AUDIO) {
            dataList.add(DeleteMultiHandleItem);
        }
        //gh
//		if (record.getSendType() == MessageBelongType.RECEIVE && mContext instanceof GroupChatTopicActivity) {
//			dataList.add(AtMultiHandleItem);
//		}

        return dataList;
    }
}
