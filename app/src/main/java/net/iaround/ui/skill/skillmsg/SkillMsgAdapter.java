package net.iaround.ui.skill.skillmsg;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import net.iaround.BaseApplication;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.ui.chat.view.SkillMsgRecordView;

import java.util.ArrayList;
import java.util.List;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/25 10:18
 * Email：15369302822@163.com
 */
public class SkillMsgAdapter extends BaseAdapter {

    private List<SkillAttackResult> recordList;

    public SkillMsgAdapter() {
        this.recordList = new ArrayList<>();
    }

    public void updateRecordList(List<SkillAttackResult> dataList) {
        if (null != recordList){
            recordList.clear();
            recordList.addAll(dataList);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        if (recordList != null && recordList.size() > 0) {
            return recordList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (recordList != null && recordList.size() > 0) {
            return recordList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SkillAttackResult record = recordList.get(position);
        SkillMsgRecordView view = (SkillMsgRecordView) convertView;
        if (null == convertView){
            view = new SkillMsgRecordView(BaseApplication.appContext);
        }
        view.showRecord(record);
        return view;
    }
}
