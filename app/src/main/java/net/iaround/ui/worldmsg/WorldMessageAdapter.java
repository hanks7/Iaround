package net.iaround.ui.worldmsg;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.ui.chat.view.WorldMessageBaseView;
import net.iaround.ui.chat.view.WorldMessageOthersRecordView;
import net.iaround.ui.chat.view.WorldMessageSelftRecordView;

import java.util.ArrayList;

/**
 * 作者：zx on 2017/8/24 16:25
 */
public class WorldMessageAdapter extends BaseAdapter {
    private long uid;
    private ArrayList<WorldMessageRecord> mDataList;
    private static final int TYPE_LEFT = 0;
    private static final int TYPE_RIGHT = 1;

    public WorldMessageAdapter() {
        uid = Common.getInstance().loginUser.getUid();
        this.mDataList = new ArrayList<>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取聊天记录
        WorldMessageRecord record = (WorldMessageRecord) getItem(position);
        WorldMessageBaseView view = (WorldMessageBaseView) convertView;
        int type = getItemViewType(position);
//        // 获取聊天记录的View
        if (convertView == null){
            if (type == TYPE_LEFT){
                view = new WorldMessageOthersRecordView(BaseApplication.appContext);
            }else if (type == TYPE_RIGHT){
                view = new WorldMessageSelftRecordView(BaseApplication.appContext);
            }
        }
        view.setBackgroundResource(R.drawable.item_pull_to_refresh);
        view.showRecord(record);
        return view;
    }

    @Override
    public int getCount() {
        if (null != mDataList && mDataList.size() > 0){
            return mDataList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != mDataList && mDataList.size() > 0){
            return  mDataList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        int type = TYPE_LEFT;
        if (null != mDataList && mDataList.size() > 0){
            WorldMessageRecord record = mDataList.get(position);
            if (null != record && null != record.user){
                int userID = record.user.UserID;
                if (uid == userID){
                    type = TYPE_RIGHT;
                }else {
                    type = TYPE_LEFT;
                }
            }

        }
        return type;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public void updateLastMessage(ArrayList<WorldMessageRecord> dataList) {
        if (null != mDataList){
            mDataList.clear();
            mDataList.addAll(dataList);
            notifyDataSetChanged();
        }
    }
}
